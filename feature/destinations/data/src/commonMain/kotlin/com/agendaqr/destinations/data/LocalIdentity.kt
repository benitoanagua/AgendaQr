package com.agendaqr.destinations.data

import com.agendaqr.destinations.domain.LocalProfileRepository
import com.agendaqr.destinations.domain.LocalProfileStore
import com.agendaqr.destinations.domain.LocalProtectionRepository
import com.agendaqr.destinations.domain.LocalProtectionStore
import com.agendaqr.destinations.domain.PinHasher
import kotlin.concurrent.Volatile

/**
 * Identidad local e infraestructura de protección.
 *
 * El perfil local es la raíz del namespace de almacenamiento: sustituye al
 * `user_id` de Supabase para TODAS las claves locales. Sin cuenta remota la
 * app funciona igual; con cuenta, los datos siguen viviendo bajo el perfil
 * local (la vinculación remota es una capa aparte).
 */

/** Clave (fija, no namespaced) del registro del perfil local. */
private const val LOCAL_PROFILE_KEY = "agendaqr.local.profile.v1"

/** Clave (fija) del verificador del PIN. */
private const val LOCAL_PROTECTION_KEY = "agendaqr.local.protection.v1"

/**
 * Perfil local activo del proceso. Se activa una sola vez al arrancar, antes
 * de construir repositorios, y no cambia mientras la app vive.
 */
object LocalProfileScope {
    @Volatile
    private var activeId: String? = null

    fun activate(id: String) {
        activeId = id
    }

    fun activeIdOrNull(): String? = activeId

    fun requireId(): String =
        activeId ?: error("Local profile not initialized: call LocalProfileScope.activate first")
}

/**
 * Clave de almacenamiento namespaced por perfil local. El segmento `.local.`
 * distingue estas claves de las antiguas `.<supabase_user_id>` (migración) y
 * evita que un id remoto arbitrario se confunda con la identidad local.
 */
fun profileScopedKey(prefix: String): String = "$prefix.local.${LocalProfileScope.requireId()}"

fun platformLocalProfileStore(): LocalProfileStore {
    val delegate = platformDestinationStore()
    return object : LocalProfileStore {
        override fun read(): String? = delegate.read(LOCAL_PROFILE_KEY)
        override fun write(value: String) = delegate.write(LOCAL_PROFILE_KEY, value)
    }
}

fun platformLocalProtectionStore(): LocalProtectionStore {
    val delegate = platformDestinationStore()
    return object : LocalProtectionStore {
        override fun readCredential(): String? = delegate.read(LOCAL_PROTECTION_KEY)
        override fun writeCredential(value: String) = delegate.write(LOCAL_PROTECTION_KEY, value)
        override fun clearCredential() = delegate.remove(LOCAL_PROTECTION_KEY)
    }
}

fun createLocalProfileRepository(): LocalProfileRepository =
    LocalProfileRepository(platformLocalProfileStore())

fun createLocalProtectionRepository(): LocalProtectionRepository =
    LocalProtectionRepository(
        store = platformLocalProtectionStore(),
        hasher = platformPinHasher(),
        randomBytes = ::secureRandomBytes,
    )

/** Generador de bytes aleatorios criptográficos (plataforma). */
expect fun secureRandomBytes(size: Int): ByteArray

/** Derivación PBKDF2-HMAC-SHA256 (plataforma). */
expect fun platformPinHasher(): PinHasher

/** Colecciones locales que antes iban namespaced por `user_id` remoto. */
internal val LEGACY_COLLECTION_PREFIXES = listOf(
    "agendaqr.contexts.v1",
    "agendaqr.destinations.v1",
    "agendaqr.operations.v1",
    "agendaqr.comprobantes.v1",
    "agendaqr.deleted_operations.v1",
    "agendaqr.sync.queue.v1",
)

/**
 * Identificador con el que una instalación ANTERIOR namespació sus datos
 * (`<prefijo>.<userId>`), detectado escaneando las claves del almacén.
 *
 * Ventajas frente a leer la sesión remota persistida: no crea el cliente
 * Supabase, no toca la red y sigue funcionando aunque la sesión antigua ya no
 * exista. Es la señal que usa el arranque para migrar datos.
 */
fun legacyLocalProfileIdOrNull(store: DestinationStore = platformDestinationStore()): String? {
    val keys = store.keys()
    for (prefix in LEGACY_COLLECTION_PREFIXES) {
        val head = "$prefix."
        for (key in keys) {
            if (!key.startsWith(head)) continue
            val suffix = key.removePrefix(head)
            // El namespace nuevo contiene ".local."; una clave heredada es un
            // id simple (UUID, sin puntos) y nunca "legacy".
            if (suffix.isBlank() || '.' in suffix || suffix == "legacy") continue
            return suffix
        }
    }
    return null
}

/**
 * ¿Hay configuración remota utilizable en esta compilación?
 *
 * La AUSENCIA de configuración es un estado normal y soportado (modo local),
 * no un error: sirve para no arrancar bucles de sincronización contra un
 * backend inexistente.
 */
fun isRemoteConfigured(): Boolean =
    supabaseConfig().let { it.url.isNotBlank() && it.publishableKey.isNotBlank() }

/**
 * Resultado de la migración de instalaciones existentes.
 *
 * [migratedCollections] = colecciones copiadas al namespace del perfil local.
 * [hadLegacyData] = había datos bajo el namespace remoto antiguo.
 */
data class LegacyMigrationReport(
    val migratedCollections: Int,
    val hadLegacyData: Boolean,
)

/**
 * Migra los datos de una instalación con sesión remota previa al namespace
 * del perfil local.
 *
 * Reglas de seguridad:
 * - NO borra las claves antiguas (el origen se conserva hasta validar).
 * - Idempotente: solo escribe cuando el destino no existe todavía.
 * - No mezcla datos de otras cuentas: opera sobre el `legacyUserId` concreto.
 */
class LegacyLocalDataMigration(
    private val store: DestinationStore = platformDestinationStore(),
) {
    fun migrate(legacyUserId: String, profileId: String): LegacyMigrationReport {
        var migrated = 0
        var hadLegacy = false
        LEGACY_COLLECTION_PREFIXES.forEach { prefix ->
            val legacyKey = "$prefix.$legacyUserId"
            val targetKey = "$prefix.local.$profileId"
            val legacyValue = store.read(legacyKey) ?: return@forEach
            hadLegacy = true
            if (store.read(targetKey) == null) {
                store.write(targetKey, legacyValue)
                migrated++
            }
        }
        return LegacyMigrationReport(migratedCollections = migrated, hadLegacyData = hadLegacy)
    }
}
