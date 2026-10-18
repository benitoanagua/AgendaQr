package com.agendaqr.destinations.data

import java.io.File

/**
 * Android: `<filesDir>/comprobantes/<ownerId>/`, la misma convención que
 * [AndroidComprobanteFileStore] (plataforma heredada incluida).
 */
internal class AndroidLegacyBytesFs : LegacyBytesFs {

    private fun dir(ownerId: String): File =
        File(File(AgendaQrAndroidStorage.requireFilesDir(), "comprobantes"), ownerId)

    override fun comprobantesDir(ownerId: String): String = dir(ownerId).absolutePath

    override fun dirExists(dir: String): Boolean = File(dir).isDirectory

    override fun listFiles(dir: String): List<String> =
        File(dir).listFiles()?.map { it.name } ?: emptyList()

    override fun fileExists(dir: String, name: String): Boolean = File(dir, name).isFile

    override fun copyFile(sourceDir: String, name: String, destDir: String): Boolean =
        runCatching {
            val destination = File(File(destDir).apply { mkdirs() }, name)
            // Copia atómica-	en-el-destino: primero temporal, luego rename,
            // para no dejar medios archivos si el proceso muere a mitad.
            val temp = File(destination.parentFile, name + ".migrating")
            File(sourceDir, name).copyTo(temp, overwrite = true)
            if (!temp.renameTo(destination)) {
                temp.delete()
                return false
            }
            true
        }.getOrDefault(false)
}

actual fun platformLegacyBytesFs(): LegacyBytesFs = AndroidLegacyBytesFs()
