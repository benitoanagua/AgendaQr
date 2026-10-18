package com.agendaqr.destinations.data

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Lógica de la migración de bytes heredados (ADR-0016): idempotencia,
 * no-borrado del origen, salto de existentes, fallo parcial sin aborto y
 * aislamiento por propietario — todo con un FS falso determinista.
 */
private class FakeBytesFs : LegacyBytesFs {
    /** dir -> (nombre -> contenido) */
    val files = mutableMapOf<String, MutableMap<String, String>>()
    /** Copia fallará para estos nombres (simula disco/IO). */
    val failing = mutableSetOf<String>()
    var copyCalls = 0

    override fun comprobantesDir(ownerId: String): String = "/base/comprobantes/$ownerId"

    override fun dirExists(dir: String): Boolean = files.containsKey(dir)

    override fun listFiles(dir: String): List<String> = files[dir]?.keys?.toList() ?: emptyList()

    override fun fileExists(dir: String, name: String): Boolean = files[dir]?.containsKey(name) == true

    override fun copyFile(sourceDir: String, name: String, destDir: String): Boolean {
        copyCalls++
        if (name in failing) return false
        val content = files[sourceDir]?.get(name) ?: return false
        files.getOrPut(destDir) { mutableMapOf() }[name] = content
        return true
    }
}

class LegacyComprobanteBytesMigrationTest {

    private val legacy = "legacy-user-1"
    private val profile = "profile-1"

    private fun seededFs(): FakeBytesFs = FakeBytesFs().apply {
        files["/base/comprobantes/$legacy"] = mutableMapOf(
            "rc-1.png" to "bytes-1",
            "rc-2.pdf" to "bytes-2",
        )
    }

    @Test
    fun copies_legacy_bytes_to_profile_dir_and_keeps_origin() {
        val fs = seededFs()
        val report = LegacyComprobanteBytesMigration(fs).migrate(legacy, profile)

        assertTrue(report.hadLegacyBytes)
        assertEquals(2, report.copied)
        val target = "/base/comprobantes/$profile"
        assertEquals("bytes-1", fs.files[target]?.get("rc-1.png"))
        assertEquals("bytes-2", fs.files[target]?.get("rc-2.pdf"))
        // No-borrado: el origen queda intacto.
        assertEquals(2, fs.files["/base/comprobantes/$legacy"]?.size)
    }

    @Test
    fun second_run_is_idempotent_and_overwrites_nothing() {
        val fs = seededFs()
        val migration = LegacyComprobanteBytesMigration(fs)
        migration.migrate(legacy, profile)
        // El usuario modifica un comprobante migrado bajo su perfil.
        fs.files["/base/comprobantes/$profile"]?.set("rc-1.png", "editado-por-usuario")

        val second = migration.migrate(legacy, profile)

        assertEquals(0, second.copied, "nada se copia dos veces")
        // La versión del usuario NO se pisa con la heredada.
        assertEquals("editado-por-usuario", fs.files["/base/comprobantes/$profile"]?.get("rc-1.png"))
    }

    @Test
    fun partial_failure_does_not_abort_remaining_files() {
        val fs = seededFs().apply { failing.add("rc-1.png") }
        val report = LegacyComprobanteBytesMigration(fs).migrate(legacy, profile)

        assertTrue(report.hadLegacyBytes)
        assertEquals(1, report.copied, "rc-2 se copia aunque rc-1 falle")
        assertEquals("bytes-2", fs.files["/base/comprobantes/$profile"]?.get("rc-2.pdf"))
        // El fallo parcial no lanza: el proceso continúa (procesamiento parcial).
    }

    @Test
    fun retry_after_partial_failure_recovers_the_missing_file() {
        val fs = seededFs().apply { failing.add("rc-1.png") }
        val migration = LegacyComprobanteBytesMigration(fs)
        migration.migrate(legacy, profile)
        fs.failing.clear()

        val retry = migration.migrate(legacy, profile)
        assertEquals(1, retry.copied, "el archivo fallido se recupera al reintentar")
        assertEquals(2, fs.files["/base/comprobantes/$profile"]?.size)
    }

    @Test
    fun missing_or_empty_legacy_dir_reports_no_legacy_bytes() {
        val empty = FakeBytesFs()
        val missing = LegacyComprobanteBytesMigration(empty).migrate(legacy, profile)
        assertEquals(0, missing.copied)
        assertTrue(!missing.hadLegacyBytes)

        val withEmptyDir = FakeBytesFs().apply {
            files["/base/comprobantes/$legacy"] = mutableMapOf()
        }
        val emptyDir = LegacyComprobanteBytesMigration(withEmptyDir).migrate(legacy, profile)
        assertEquals(0, emptyDir.copied)
        assertTrue(!emptyDir.hadLegacyBytes)
    }

    @Test
    fun same_owner_never_migrates_onto_itself() {
        val fs = seededFs()
        val report = LegacyComprobanteBytesMigration(fs).migrate(legacy, legacy)
        assertEquals(0, report.copied)
        assertEquals(0, fs.copyCalls)
    }

    @Test
    fun never_writes_outside_source_or_target_dirs() {
        val fs = seededFs()
        LegacyComprobanteBytesMigration(fs).migrate(legacy, profile)
        val dirs = fs.files.keys
        assertTrue(dirs.all { it == "/base/comprobantes/$legacy" || it == "/base/comprobantes/$profile" })
    }
}
