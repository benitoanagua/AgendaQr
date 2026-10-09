package com.agendaqr.supabase

import java.sql.Connection
import java.sql.DriverManager
import kotlin.test.Test
import kotlin.test.assertTrue

/**
 * Acceptance harness for supabase/migrations/001-006 + tests/006_*.
 *
 * Replaces supabase/tests/run-acceptance.sh with pure Kotlin/JVM + JDBC.
 * RLS *behavior* is NOT validated here; full Supabase validation still
 * requires `supabase start` or a hosted project.
 *
 * Usage: DATABASE_URL environment variable (default: localhost:5432).
 */
class AcceptanceTest {

    private fun connection(): Connection {
        val url = System.getenv("DATABASE_URL")
            ?: "jdbc:postgresql://localhost:5432/postgres"
        val props = java.util.Properties()
        props.setProperty("user", "postgres")
        props.setProperty("password", "postgres")
        return DriverManager.getConnection(url, props)
    }

    private fun executeSqlFile(conn: Connection, path: String) {
        val root = java.io.File(System.getProperty("user.dir")).parentFile.parentFile // repo root
        val sql = java.io.File(java.io.File(root, "supabase"), path).readText()
        conn.createStatement().use { stmt ->
            stmt.execute(sql)
        }
    }

    @Test
    fun migrations_001_006_and_006_acceptance_pass() {
        connection().use { conn ->
            conn.autoCommit = true

            // 1. Platform stubs (auth.users, storage.*, pgcrypto).
            executeSqlFile(conn, "tests/support/platform-stubs.sql")

            // 2. Migrations 001-006 in order.
            val migrations = listOf(
                "migrations/001_v1.sql",
                "migrations/002_operation_updated_at.sql",
                "migrations/003_operations_updated_at.sql",
                "migrations/004_contexts.sql",
                "migrations/005_context_relations.sql",
                "migrations/006_context_fk_set_null_columns.sql",
            )
            migrations.forEach { migration ->
                println("Applying $migration")
                executeSqlFile(conn, migration)
            }

            // 3. Acceptance test for 006 (FK SET NULL columns).
            // Any SQLException inside this SQL aborts the test
            // (equivalent to psql -v ON_ERROR_STOP=1).
            println("Running 006 acceptance test")
            executeSqlFile(conn, "tests/006_context_fk_set_null_columns.sql")
            println("ACCEPTANCE PASS")
        }
    }
}
