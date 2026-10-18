package com.agendaqr.destinations.domain

import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

// =====================================================================
// Identidad local
//
// La identidad local es independiente de Supabase: se crea sin conexión y
// persiste entre reinicios. NO depende de `currentUserOrNull()`, del correo,
// del PIN ni de un identificador de hardware. Es la raíz de la que cuelga el
// namespace de almacenamiento (ver `profileScopedKey` en la capa data).
// =====================================================================

/** Versión del esquema de almacenamiento local del perfil. */
const val LOCAL_PROFILE_SCHEMA_VERSION: Int = 1

@Serializable
data class LocalProfile(
    /** UUID aleatorio propio; estable entre reinicios. */
    val id: String,
    /** Fecha de creación (epoch millis), informativa y para auditoría. */
    val createdAtEpochMillis: Long,
    /** Referencia a la versión del esquema local. */
    val schemaVersion: Int = LOCAL_PROFILE_SCHEMA_VERSION,
)

/**
 * Persistencia del registro del perfil. La implementación de plataforma
 * escribe en el almacenamiento propio de la app; el resto del árbol no
 * necesita saber cómo.
 */
interface LocalProfileStore {
    fun read(): String?
    fun write(value: String)
}

/**
 * Repositorio del perfil local. Solo existe un perfil local por dispositivo
 * (v1); los datos de las colecciones sí van namespaced por su id.
 */
class LocalProfileRepository(
    private val store: LocalProfileStore,
    private val newId: () -> String = { newEntityId("profile") },
    private val now: () -> Long = ::nowMillis,
) {
    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

    /** Perfil actual o `null` si aún no se ha creado. */
    fun current(): LocalProfile? =
        store.read()?.let { raw ->
            runCatching { json.decodeFromString(LocalProfile.serializer(), raw) }.getOrNull()
        }

    /**
     * Devuelve el perfil existente o lo crea. Idempotente: llamarlo varias
     * veces no duplica ni cambia el identificador.
     */
    fun getOrCreate(): LocalProfile {
        current()?.let { return it }
        val profile = LocalProfile(id = newId(), createdAtEpochMillis = now())
        store.write(json.encodeToString(LocalProfile.serializer(), profile))
        // Defensivo: si la escritura no persistió, nunca devolvemos un id
        // que no podrá volver a leerse (evita un perfil "fantasma").
        return current() ?: profile
    }
}

class EnsureLocalProfileUseCase(private val repository: LocalProfileRepository) {
    operator fun invoke(): LocalProfile = repository.getOrCreate()
}

// =====================================================================
// Protección local (PIN)
//
// El PIN es una credencial LOCAL propia de AgendaQr: no es el código de
// desbloqueo del dispositivo ni una contraseña remota. Nunca se guarda en
// claro: se persiste únicamente un verificador derivado (PBKDF2-HMAC-SHA256)
// con sal aleatoria, junto al estado de intentos.
// =====================================================================

/** Parámetros del verifier. Ajustables sin cambiar el contrato. */
object PinPolicy {
    /** Longitud mínima y máxima del PIN (solo dígitos). */
    const val MIN_LENGTH: Int = 4
    const val MAX_LENGTH: Int = 12
    /** Salt aleatoria en bytes. */
    const val SALT_BYTES: Int = 16
    /** Iteraciones de PBKDF2: coste asumible en móvil con margen. */
    const val ITERATIONS: Int = 120_000
    /** Longitud de la clave derivada en bytes. */
    const val KEY_BYTES: Int = 32
    /** Intentos fallidos antes de aplicar bloqueo temporal. */
    const val MAX_ATTEMPTS: Int = 5
    /** Retardo base y techo del bloqueo progresivo (ms). */
    const val BASE_BACKOFF_MILLIS: Long = 1_000L
    const val MAX_BACKOFF_MILLIS: Long = 30_000L
}

/** Algoritmo de derivación persistido con el verificador. */
const val PIN_ALGORITHM_ID: String = "PBKDF2WithHmacSHA256"

@Serializable
data class PinCredential(
    val algorithm: String = PIN_ALGORITHM_ID,
    val iterations: Int = PinPolicy.ITERATIONS,
    val saltBase64: String,
    val hashBase64: String,
    /** Intentos fallidos acumulados desde el último acierto. */
    val failedAttempts: Int = 0,
    /** Instante (epoch millis) hasta el que no se admiten intentos. */
    val lockoutUntilEpochMillis: Long = 0L,
)

/**
 * Deriva la clave del PIN. La implementación real usa PBKDF2 en la capa
 * data; el dominio solo declara el contrato para poder probar la lógica con
 * un doble determinista.
 */
interface PinHasher {
    fun derive(pin: CharArray, salt: ByteArray, iterations: Int, keyLengthBytes: Int): ByteArray
}

/** Persistencia del verificador del PIN. */
interface LocalProtectionStore {
    fun readCredential(): String?
    fun writeCredential(value: String)
    fun clearCredential()
}

/** Resultado de un intento de desbloqueo. */
sealed interface PinVerification {
    data object Success : PinVerification
    data class Incorrect(val remainingAttempts: Int) : PinVerification
    data class LockedOut(val retryAfterMillis: Long) : PinVerification
    data object NotConfigured : PinVerification
}

/** Motivo por el que una operación de PIN no se admite. */
enum class PinRejectionReason {
    TooShort,
    TooLong,
    NonDigits,
    Mismatch,
    IncorrectCurrent,
    LockedOut,
    NotConfigured,
}

/** Resultado de configurar/cambiar el PIN. */
sealed interface PinUpdate {
    data object Success : PinUpdate
    data class Rejected(val reason: PinRejectionReason) : PinUpdate
}

/**
 * Servicio de protección local. La lógica (validación, comparación en tiempo
 * constante, límite de intentos y bloqueo progresivo) vive aquí, separada de
 * Compose, y es verificable con un [PinHasher] de prueba.
 */
class LocalProtectionRepository(
    private val store: LocalProtectionStore,
    private val hasher: PinHasher,
    private val randomBytes: (Int) -> ByteArray,
    private val now: () -> Long = ::nowMillis,
) {
    private val json = Json { ignoreUnknownKeys = true; encodeDefaults = true }

    fun isConfigured(): Boolean = readCredential() != null

    /** Milisegundos restantes de bloqueo temporal (0 si no hay). */
    fun lockoutRemainingMillis(): Long {
        val credential = readCredential() ?: return 0L
        return maxOf(0L, credential.lockoutUntilEpochMillis - now())
    }

    /** Intentos restantes antes del bloqueo temporal. */
    fun remainingAttempts(): Int {
        val credential = readCredential() ?: return 0
        return maxOf(0, PinPolicy.MAX_ATTEMPTS - credential.failedAttempts)
    }

    fun verify(pin: CharArray): PinVerification {
        val credential = readCredential() ?: return PinVerification.NotConfigured
        val remaining = credential.lockoutUntilEpochMillis - now()
        if (remaining > 0L) return PinVerification.LockedOut(remaining)

        val candidate = derive(pin, credential)
        val expected = decode(credential.hashBase64)
        return if (constantTimeEquals(candidate, expected)) {
            writeCredential(credential.copy(failedAttempts = 0, lockoutUntilEpochMillis = 0L))
            PinVerification.Success
        } else {
            val attempts = credential.failedAttempts + 1
            val lockoutUntil = if (attempts >= PinPolicy.MAX_ATTEMPTS) {
                now() + backoffMillis(attempts)
            } else {
                0L
            }
            writeCredential(
                credential.copy(failedAttempts = attempts, lockoutUntilEpochMillis = lockoutUntil),
            )
            if (lockoutUntil > 0L) {
                PinVerification.LockedOut(lockoutUntil - now())
            } else {
                PinVerification.Incorrect(maxOf(0, PinPolicy.MAX_ATTEMPTS - attempts))
            }
        }
    }

    /** Configura el PIN por primera vez (no hay ninguno previo). */
    fun configure(pin: CharArray, confirmation: CharArray): PinUpdate {
        validate(pin)?.let { return PinUpdate.Rejected(it) }
        if (!pin.contentEquals(confirmation)) return PinUpdate.Rejected(PinRejectionReason.Mismatch)
        val salt = randomBytes(PinPolicy.SALT_BYTES)
        val hash = hasher.derive(pin, salt, PinPolicy.ITERATIONS, PinPolicy.KEY_BYTES)
        writeCredential(
            PinCredential(
                algorithm = PIN_ALGORITHM_ID,
                iterations = PinPolicy.ITERATIONS,
                saltBase64 = encode(salt),
                hashBase64 = encode(hash),
                failedAttempts = 0,
                lockoutUntilEpochMillis = 0L,
            ),
        )
        return PinUpdate.Success
    }

    /** Cambia el PIN exigiendo el actual (autenticado). */
    fun change(current: CharArray, next: CharArray, confirmation: CharArray): PinUpdate {
        when (val result = verify(current)) {
            PinVerification.Success -> Unit
            PinVerification.NotConfigured -> return PinUpdate.Rejected(PinRejectionReason.NotConfigured)
            is PinVerification.LockedOut -> return PinUpdate.Rejected(PinRejectionReason.LockedOut)
            is PinVerification.Incorrect -> return PinUpdate.Rejected(PinRejectionReason.IncorrectCurrent)
        }
        validate(next)?.let { return PinUpdate.Rejected(it) }
        if (!next.contentEquals(confirmation)) return PinUpdate.Rejected(PinRejectionReason.Mismatch)
        val salt = randomBytes(PinPolicy.SALT_BYTES)
        val hash = hasher.derive(next, salt, PinPolicy.ITERATIONS, PinPolicy.KEY_BYTES)
        writeCredential(
            PinCredential(
                algorithm = PIN_ALGORITHM_ID,
                iterations = PinPolicy.ITERATIONS,
                saltBase64 = encode(salt),
                hashBase64 = encode(hash),
            ),
        )
        return PinUpdate.Success
    }

    /**
     * Elimina la protección. Se documenta que, en este incremento, los datos
     * locales NO están cifrados con el PIN, por lo que retirar la protección
     * no destruye información: solo deja de exigir el desbloqueo.
     */
    fun clear() = store.clearCredential()

    private fun validate(pin: CharArray): PinRejectionReason? = when {
        pin.size < PinPolicy.MIN_LENGTH -> PinRejectionReason.TooShort
        pin.size > PinPolicy.MAX_LENGTH -> PinRejectionReason.TooLong
        pin.any { it !in '0'..'9' } -> PinRejectionReason.NonDigits
        else -> null
    }

    private fun derive(pin: CharArray, credential: PinCredential): ByteArray =
        hasher.derive(pin, decode(credential.saltBase64), credential.iterations, PinPolicy.KEY_BYTES)

    private fun backoffMillis(attempts: Int): Long {
        val over = attempts - PinPolicy.MAX_ATTEMPTS
        var value = PinPolicy.BASE_BACKOFF_MILLIS
        repeat(over.coerceAtMost(16)) { value *= 2 }
        return value.coerceAtMost(PinPolicy.MAX_BACKOFF_MILLIS)
    }

    private fun readCredential(): PinCredential? =
        store.readCredential()?.let { raw ->
            runCatching { json.decodeFromString(PinCredential.serializer(), raw) }.getOrNull()
        }

    private fun writeCredential(credential: PinCredential) =
        store.writeCredential(json.encodeToString(PinCredential.serializer(), credential))

    @OptIn(ExperimentalEncodingApi::class)
    private fun encode(bytes: ByteArray): String = Base64.encode(bytes)

    @OptIn(ExperimentalEncodingApi::class)
    private fun decode(text: String): ByteArray = Base64.decode(text)
}

/** Comparación en tiempo constante (evita filtrar por temporización). */
private fun constantTimeEquals(a: ByteArray, b: ByteArray): Boolean {
    if (a.size != b.size) return false
    var diff = 0
    for (index in a.indices) diff = diff or (a[index].toInt() xor b[index].toInt())
    return diff == 0
}

class ConfigurePinUseCase(private val repository: LocalProtectionRepository) {
    operator fun invoke(pin: CharArray, confirmation: CharArray): PinUpdate =
        repository.configure(pin, confirmation)
}

class VerifyPinUseCase(private val repository: LocalProtectionRepository) {
    operator fun invoke(pin: CharArray): PinVerification = repository.verify(pin)
}

class ChangePinUseCase(private val repository: LocalProtectionRepository) {
    operator fun invoke(current: CharArray, next: CharArray, confirmation: CharArray): PinUpdate =
        repository.change(current, next, confirmation)
}
