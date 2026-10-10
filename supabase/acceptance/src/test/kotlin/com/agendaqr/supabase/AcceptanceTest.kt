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
 * RLS *behavior* is NOT validated here; full Supabase validation still
 * requires `supabase start` or a hosted project.
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

    @Test
    fun migrations_apply_and_006_acceptance_passes() {
        connection().use { conn ->
            // Clean slate: drop and recreate public schema (isolated test).
            conn.createStatement().use { stmt ->
                stmt.execute("DROP SCHEMA IF EXISTS public CASCADE; CREATE SCHEMA public;")
                // Las policies de storage viven en el schema storage: limpiarlas.
                stmt.execute("DROP SCHEMA IF EXISTS storage CASCADE;")
            }

            // Platform stubs (idempotent).
            SqlScriptRunner.executeFile(conn, repoFile("tests/support/platform-stubs.sql"))
                .let { result -> assertTrue(result is SqlScriptRunner.Result.Success, "stubs: $result") }

            // Discover migrations by numeric order (001→006).
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

            // 006 acceptance test.
            val acceptanceResult = SqlScriptRunner.executeFile(
                conn, repoFile("tests/006_context_fk_set_null_columns.sql"),
            )
            assertTrue(acceptanceResult is SqlScriptRunner.Result.Success,
                "006 acceptance failed: $acceptanceResult")
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
