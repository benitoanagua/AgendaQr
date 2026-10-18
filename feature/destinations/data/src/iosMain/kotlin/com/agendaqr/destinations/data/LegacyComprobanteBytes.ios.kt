package com.agendaqr.destinations.data

import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSApplicationSupportDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSSearchPathForDirectoriesInDomains
import platform.Foundation.NSUserDomainMask

/**
 * iOS: `<Application Support>/AgendaQr/comprobantes/<ownerId>/`, la misma
 * convención que `IosComprobanteFileStore` (plataforma heredada incluida).
 */
@OptIn(ExperimentalForeignApi::class)
internal class IosLegacyBytesFs : LegacyBytesFs {

    private val fileManager = NSFileManager.defaultManager

    private fun base(): String {
        val appSupport = NSSearchPathForDirectoriesInDomains(
            NSApplicationSupportDirectory,
            NSUserDomainMask,
            true,
        ).first() as String
        return appSupport + "/AgendaQr/comprobantes"
    }

    override fun comprobantesDir(ownerId: String): String = base() + "/" + ownerId

    override fun dirExists(dir: String): Boolean = fileManager.fileExistsAtPath(dir)

    override fun listFiles(dir: String): List<String> =
        (fileManager.contentsOfDirectoryAtPath(dir, error = null) as? List<*>)
            ?.filterIsInstance<String>()
            ?.filter { name -> fileManager.fileExistsAtPath(dir + "/" + name) }
            ?: emptyList()

    override fun fileExists(dir: String, name: String): Boolean =
        fileManager.fileExistsAtPath(dir + "/" + name)

    override fun copyFile(sourceDir: String, name: String, destDir: String): Boolean =
        runCatching {
            // El directorio de destino se crea si es la primera copia.
            fileManager.createDirectoryAtPath(destDir, true, null, null)
            // copyItemAtPath deja medio archivo fuera si falla: NSFileManager
            // copia a un temporal y renombra para no dejar residuos.
            val source = sourceDir + "/" + name
            val temp = destDir + "/" + name + ".migrating"
            // Limpia un temporal de una corrida interrumpida anterior.
            fileManager.removeItemAtPath(temp, null)
            if (!fileManager.copyItemAtPath(source, toPath = temp, error = null)) return false
            if (!fileManager.moveItemAtPath(temp, toPath = destDir + "/" + name, error = null)) {
                fileManager.removeItemAtPath(temp, null)
                return false
            }
            true
        }.getOrDefault(false)
}

actual fun platformLegacyBytesFs(): LegacyBytesFs = IosLegacyBytesFs()
