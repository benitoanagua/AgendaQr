package com.agendaqr.destinations.data

import com.agendaqr.destinations.domain.PinHasher
import javax.crypto.SecretKeyFactory
import javax.crypto.spec.PBEKeySpec

/**
 * Implementación Android: PBKDF2-HMAC-SHA256 vía JCA (disponible desde API
 * 26, el minSdk del proyecto) y [java.security.SecureRandom] para la sal.
 */
private object AndroidPinHasher : PinHasher {
    override fun derive(pin: CharArray, salt: ByteArray, iterations: Int, keyLengthBytes: Int): ByteArray {
        val spec = PBEKeySpec(pin, salt, iterations, keyLengthBytes * 8)
        return try {
            SecretKeyFactory.getInstance(PIN_PBKDF2_ALGORITHM).generateSecret(spec).encoded
        } finally {
            spec.clearPassword()
        }
    }
}

private const val PIN_PBKDF2_ALGORITHM = "PBKDF2WithHmacSHA256"

private val secureRandom = java.security.SecureRandom()

actual fun secureRandomBytes(size: Int): ByteArray =
    ByteArray(size).also { secureRandom.nextBytes(it) }

actual fun platformPinHasher(): PinHasher = AndroidPinHasher
