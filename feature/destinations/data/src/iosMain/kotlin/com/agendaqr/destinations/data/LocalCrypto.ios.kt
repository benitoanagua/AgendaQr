package com.agendaqr.destinations.data

import com.agendaqr.destinations.domain.PinHasher
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.addressOf
import kotlinx.cinterop.convert
import kotlinx.cinterop.usePinned
import platform.CoreCrypto.CCKeyDerivationPBKDF
import platform.CoreCrypto.kCCPBKDF2
import platform.CoreCrypto.kCCPRFHmacAlgSHA256
import platform.Security.SecRandomCopyBytes
import platform.Security.kSecRandomDefault

/**
 * Implementación iOS: PBKDF2-HMAC-SHA256 vía CommonCrypto (`CoreCrypto`) y
 * `SecRandomCopyBytes` para la sal. No usa Secure Enclave ni biometría: es
 * derivación pura y no depende de hardware.
 */
@OptIn(ExperimentalForeignApi::class)
private object IosPinHasher : PinHasher {
    override fun derive(pin: CharArray, salt: ByteArray, iterations: Int, keyLengthBytes: Int): ByteArray {
        // El PIN es solo dígitos: su longitud en UTF-8 coincide con el nº de
        // caracteres, así que `passwordLen` = pin.size.
        val password = pin.concatToString()
        val saltBytes = salt.toUByteArray()
        val derived = UByteArray(keyLengthBytes)
        val status = saltBytes.usePinned { saltPinned ->
            derived.usePinned { outPinned ->
                CCKeyDerivationPBKDF(
                    kCCPBKDF2,
                    password,
                    pin.size.convert(),
                    saltPinned.addressOf(0),
                    salt.size.convert(),
                    kCCPRFHmacAlgSHA256,
                    iterations.convert(),
                    outPinned.addressOf(0),
                    keyLengthBytes.convert(),
                )
            }
        }
        check(status == 0) { "PBKDF2 derivation failed with status $status" }
        return derived.toByteArray()
    }
}

@OptIn(ExperimentalForeignApi::class)
actual fun secureRandomBytes(size: Int): ByteArray {
    val bytes = ByteArray(size)
    if (size == 0) return bytes
    bytes.usePinned { pinned ->
        SecRandomCopyBytes(kSecRandomDefault, size.convert(), pinned.addressOf(0))
    }
    return bytes
}

actual fun platformPinHasher(): PinHasher = IosPinHasher
