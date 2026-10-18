package com.agendaqr.supabase

import java.sql.DriverManager
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.test.assertEquals
import kotlin.test.fail
import kotlin.test.assertNotNull

/**
 * Acceptance harness for supabase/migrations/001-006 + tests/006_*.
 * Replaces supabase/tests/run-acceptance.sh with pure Kotlin/JVM + JDBC.
 *
 * Además del SQL de aceptación, valida el COMPORTAMIENTO real de RLS
 * contra postgres:15 stock: los stubs de plataforma definen auth.uid()
 * leyendo el claim de sesión (como Supabase real), así que las pruebas
 * de aislamiento actúan como dos usuarios distintos con un rol NO
 * superusuario (los superusuarios omiten RLS y no servirían como
 * evidencia).
 */
class AcceptanceTest {

    private fun connection(): java.sql.Connection {
        val url = System.getenv("DATABASE_URL")
            ?: fail("DATABASE_URL not set. Example: DATABASE_URL=jdbc:postgresql://localhost:15432/postgres")
        val props = java.util.Properties()
        props.setProperty("user", System.getenv("DATABASE_USER") ?: "postgres")
        props.setProperty("password", System.getenv("DATABASE_PASSWORD") ?: "postgres")
        return DriverManager.getConnection(url, props)
    }

    private fun repoFile(path: String): java.io.File {
        val root = System.getProperty("repo.root")
            ?: fail("repo.root system property not set (configured in build.gradle.kts)")
        return java.io.File(java.io.File(root, "supabase"), path)
    }

    /** Aplica stubs + migraciones 001→006 sobre un schema público limpio. */
    private fun applyMigrations(conn: java.sql.Connection) {
        conn.createStatement().use { stmt ->
            stmt.execute("DROP SCHEMA IF EXISTS public CASCADE; CREATE SCHEMA public;")
            stmt.execute("DROP SCHEMA IF EXISTS storage CASCADE;")
        }
        SqlScriptRunner.executeFile(conn, repoFile("tests/support/platform-stubs.sql"))
            .let { result -> assertTrue(result is SqlScriptRunner.Result.Success, "stubs: $result") }
        val migrationDir = repoFile("migrations")
        val migrations = migrationDir.listFiles()
            ?.filter { it.extension == "sql" && it.name.matches(Regex("00\\d.*\\.sql")) }
            ?.sortedBy { it.name }
            ?: fail("No migrations found in ${migrationDir.path}")
        // Check for gaps in sequence.
        val numbers = migrations.mapNotNull { Regex("^(\\d{3})").find(it.name)?.groupValues?.get(1)?.toIntOrNull() }
        for (i in 1 until numbers.size) {
            if (numbers[i] != numbers[i - 1] + 1) {
                fail("Migration sequence has gaps at position $i: $numbers")
            }
        }
        migrations.forEach { migration ->
            val result = SqlScriptRunner.executeFile(conn, migration)
            assertTrue(result is SqlScriptRunner.Result.Success,
                "Migration ${migration.name} failed: $result")
        }
    }

    /**
     * Sesión como [userId] con un rol NO superusuario (sujeto a RLS).
     * El claim imita el JWT de Supabase; el rol tiene solo los grants
     * mínimos de una app cliente.
     */
    private fun java.sql.Connection.actAs(userId: java.util.UUID) {
        createStatement().use { stmt ->
            stmt.execute("SELECT set_config('request.jwt.claim.sub', '$userId', false)")
            stmt.execute("SET ROLE app_user")
        }
    }

    private fun java.sql.Connection.resetRole() {
        createStatement().use { stmt ->
            stmt.execute("RESET ROLE")
            stmt.execute("SELECT set_config('request.jwt.claim.sub', '', false)")
        }
    }

    @Test
    fun migrations_apply_and_006_acceptance_passes() {
        connection().use { conn ->
            applyMigrations(conn)

            // 006 acceptance test.
            val acceptanceResult = SqlScriptRunner.executeFile(
                conn, repoFile("tests/006_context_fk_set_null_columns.sql"),
            )
            assertTrue(acceptanceResult is SqlScriptRunner.Result.Success,
                "006 acceptance failed: $acceptanceResult")
        }
    }

    // ------------------------------------------------------------------
    // RLS BEHAVIOR: aislamiento real entre dos usuarios (rol NO
    // superusuario, claim de sesión como Supabase real). Un superusuario
    // omite RLS, así que toda la prueba corre como app_user.
    // ------------------------------------------------------------------

    /** Setup común: migraciones + rol de app + datos del usuario A. */
    private fun twoUserFixture(conn: java.sql.Connection): Pair<java.util.UUID, java.util.UUID> {
        applyMigrations(conn)
        val userA = java.util.UUID.randomUUID()
        val userB = java.util.UUID.randomUUID()
        conn.createStatement().use { stmt ->
            // Rol cliente: NO superusuario y sujeto a RLS. Limpio los
            // grants de corridas previas antes de eliminarlo.
            stmt.execute("""
                DO $$ BEGIN
                    IF EXISTS (SELECT 1 FROM pg_roles WHERE rolname = 'app_user') THEN
                        DROP OWNED BY app_user;
                        DROP ROLE app_user;
                    END IF;
                END $$;
            """.trimIndent())
            stmt.execute("CREATE ROLE app_user NOSUPERUSER NOBYPASSRLS LOGIN PASSWORD 'app_user'")
            stmt.execute("GRANT USAGE ON SCHEMA public, storage, auth TO app_user")
            stmt.execute("GRANT SELECT, INSERT, UPDATE, DELETE ON ALL TABLES IN SCHEMA public TO app_user")
            stmt.execute("GRANT SELECT, INSERT, UPDATE, DELETE ON storage.objects TO app_user")
            stmt.execute("GRANT SELECT ON auth.users TO app_user")
            // Semillas del usuario A (como superusuario: solo setup).
            stmt.execute("INSERT INTO auth.users(id) VALUES ('$userA'), ('$userB')")
            stmt.execute(
                "INSERT INTO public.contexts(id, user_id, name, created_at, updated_at) " +
                    "VALUES ('ctx-a', '$userA', 'Mercado', now(), now())",
            )
        }
        return userA to userB
    }

    private fun expectRejected(conn: java.sql.Connection, sql: String, message: String) {
        val error = try {
            conn.createStatement().use { it.execute(sql) }
            null
        } catch (e: java.sql.SQLException) {
            e
        }
        assertNotNull(error, "$message — la escritura cruzó la frontera y NO debía permitirse")
    }

    @Test
    fun rls_isolates_rows_between_two_users() {
        connection().use { conn ->
            val (userA, userB) = twoUserFixture(conn)

            // --- Usuario A inserta SU destino (permitido). ---
            conn.actAs(userA)
            conn.createStatement().use { stmt ->
                stmt.executeUpdate(
                    "INSERT INTO public.destinations(id, user_id, name, qr_raw_content, qr_kind, created_at, updated_at) " +
                        "VALUES ('dest-a', '$userA', 'Cafetería Central', '000201...', 'QR', now(), now())",
                )
            }

            // --- A NO puede escribir una fila PERTENECIENTE a B (WITH CHECK). ---
            expectRejected(
                conn,
                "INSERT INTO public.destinations(id, user_id, name, qr_raw_content, qr_kind, created_at, updated_at) " +
                    "VALUES ('dest-fake', '$userB', 'Suplantación', '000201...', 'QR', now(), now())",
                "RLS destinations: A no puede insertar con user_id de B",
            )

            // --- B NO ve, NI actualiza, NI borra la fila de A (USING). ---
            conn.resetRole()
            conn.actAs(userB)
            conn.createStatement().use { stmt ->
                val rs = stmt.executeQuery("SELECT count(*) FROM public.destinations")
                rs.next()
                assertEquals(0, rs.getInt(1), "RLS destinations: B no debe ver filas de A")
                val updated = stmt.executeUpdate(
                    "UPDATE public.destinations SET name = 'hack' WHERE id = 'dest-a'",
                )
                assertEquals(0, updated, "RLS destinations: B no debe actualizar filas de A")
                val deleted = stmt.executeUpdate(
                    "DELETE FROM public.destinations WHERE id = 'dest-a'",
                )
                assertEquals(0, deleted, "RLS destinations: B no debe borrar filas de A")
            }

            // --- La RELACIÓN context_id no puede cruzar usuarios: el FK
            //     compuesto (context_id, user_id) lo impide a nivel de datos. ---
            conn.resetRole()
            conn.createStatement().use { stmt ->
                val rs = stmt.executeQuery("SELECT id FROM public.contexts WHERE user_id = '$userA' LIMIT 1")
                assertTrue(rs.next(), "seed de contexto de A")
                val ctxA = rs.getString(1)
                conn.actAs(userB)
                expectRejected(
                    conn,
                    "INSERT INTO public.destinations(id, user_id, context_id, name, qr_raw_content, qr_kind, created_at, updated_at) " +
                        "VALUES ('dest-ajeno', '$userB', '$ctxA', 'Ajeno', '000201...', 'QR', now(), now())",
                    "FK compuesto (context_id, user_id): B no puede asociar el contexto de A",
                )
            }

            // --- Storage: B no lee el objeto de A; A sí (píldora por carpeta
            //     de usuario: foldername(name)[1] = auth.uid()). ---
            conn.resetRole()
            conn.actAs(userA)
            conn.createStatement().use { stmt ->
                stmt.executeUpdate(
                    "INSERT INTO storage.objects(id, bucket_id, name) " +
                        "VALUES ('${java.util.UUID.randomUUID()}', 'comprobantes', '$userA/ticket.jpg')",
                )
            }
            conn.resetRole()
            conn.actAs(userB)
            conn.createStatement().use { stmt ->
                val rs = stmt.executeQuery(
                    "SELECT count(*) FROM storage.objects WHERE name LIKE '$userA%'",
                )
                rs.next()
                assertEquals(0, rs.getInt(1), "RLS storage: B no debe ver objetos de A")
            }
            conn.resetRole()
        }
    }

    @Test
    fun runner_handles_dollar_quoting_and_strings() {
        val sql = """
            CREATE FUNCTION test1() RETURNS int AS ${'$'}${'$'} BEGIN RETURN 1; END ${'$'}${'$'} LANGUAGE plpgsql;
            INSERT INTO t VALUES ('hello;world');
            -- comment; with semicolon
            CREATE FUNCTION test2() RETURNS int AS ${'$'}${'$'}tag${'$'} BEGIN RETURN 2; END ${'$'}${'$'}tag${'$'} LANGUAGE plpgsql;
        """.trimIndent()
        val statements = SqlScriptRunner.splitStatements(sql)
        assertEquals(3, statements.size, "Expected 3 statements, got ${statements.size}")
        assertTrue(statements[0].contains("${'$'}${'$'}"), "First statement should contain dollar quotes")
        assertTrue(statements[1].contains("'hello;world'"), "Second statement should contain quoted string with semicolon")
        assertTrue(statements[2].contains("${'$'}${'$'}tag${'$'}"), "Third statement should contain tagged dollar quotes")
    }

    @Test
    fun runner_fails_on_invalid_sql() {
        connection().use { conn ->
            val tmpFile = java.io.File.createTempFile("bad", ".sql").apply {
                writeText("THIS IS NOT VALID SQL;")
                deleteOnExit()
            }
            val result = SqlScriptRunner.executeFile(conn, tmpFile)
            assertTrue(result is SqlScriptRunner.Result.Error,
                "Expected error for invalid SQL, got: $result")
        }
    }
}
