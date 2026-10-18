package com.agendaqr.destinations.data

/**
 * Puerto de sistema de archivos para la migración de bytes heredados.
 *
 * La LÓGICA (idempotencia, no-borrado, salto de existentes, aislamiento por
 * propietario) vive en [LegacyComprobanteBytesMigration] y es verificable con
 * un doble en commonTest; las plataformas solo aportan las rutas y las
 * operaciones nativas de archivos.
 */
interface LegacyBytesFs {
    /** Directorio de comprobantes de un propietario (perfil o usuario heredado). */
    fun comprobantesDir(ownerId: String): String

    fun dirExists(dir: String): Boolean

    fun listFiles(dir: String): List<String>

    fun fileExists(dir: String, name: String): Boolean

    /** Copia `name` de `sourceDir` a `destDir`; `false` si falla SIN lanzar. */
    fun copyFile(sourceDir: String, name: String, destDir: String): Boolean
}

/** Resultado de la migración de bytes. */
data class LegacyComprobanteBytesReport(
    /** Bytes copiados en ESTA ejecución (los preexistentes no se cuentan). */
    val copied: Int,
    /** Había directorio heredado con archivos que copiar. */
    val hadLegacyBytes: Boolean,
)

/**
 * Migra los BYTES de comprobantes de la instalación heredada
 * (`<base>/comprobantes/<user_id>/`) al directorio del perfil local
 * (`<base>/comprobantes/<profile_id>/`).
 *
 * La migración de colecciones serializadas es [LegacyLocalDataMigration];
 * esta cubre los archivos físicos que aquella no copiaba (limitación
 * documentada en ADR-0016).
 *
 * Reglas de seguridad (similares a las de las colecciones):
 * - NO borra el origen: la ruta heredada se conserva intacta;
 * - idempotente: un archivo que ya existe en el destino no se copia ni
 *   sobreescribe (reentrar no duplica ni pisa);
 * - un fallo de un archivo NO aborta los demás (procesamiento parcial);
 * - nunca escribe fuera de los dos directorios implicados.
 */
class LegacyComprobanteBytesMigration(
    private val fs: LegacyBytesFs = platformLegacyBytesFs(),
) {
    fun migrate(legacyUserId: String, profileId: String): LegacyComprobanteBytesReport {
        require(legacyUserId.isNotBlank()) { "legacyUserId is required" }
        require(profileId.isNotBlank()) { "profileId is required" }
        // Un perfil nunca migra sobre sí mismo (sin operación de bulto).
        if (legacyUserId == profileId) {
            return LegacyComprobanteBytesReport(copied = 0, hadLegacyBytes = false)
        }
        val source = fs.comprobantesDir(legacyUserId)
        val target = fs.comprobantesDir(profileId)
        if (!fs.dirExists(source)) {
            return LegacyComprobanteBytesReport(copied = 0, hadLegacyBytes = false)
        }
        val names = fs.listFiles(source)
        if (names.isEmpty()) {
            return LegacyComprobanteBytesReport(copied = 0, hadLegacyBytes = false)
        }
        var copied = 0
        for (name in names) {
            // Idempotente: lo ya migrado (o creado por el usuario bajo el
            // perfil local) se conserva tal cual.
            if (fs.fileExists(target, name)) continue
            if (fs.copyFile(source, name, target)) copied++
        }
        return LegacyComprobanteBytesReport(copied = copied, hadLegacyBytes = true)
    }
}

/** Implementación de plataforma del puerto de archivos. */
expect fun platformLegacyBytesFs(): LegacyBytesFs
