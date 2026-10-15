package com.agendaqr.destinations.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

private class InMemoryProfileStore : LocalProfileStore {
    var value: String? = null
    override fun read(): String? = value
    override fun write(value: String) { this.value = value }
}

private class InMemoryProtectionStore : LocalProtectionStore {
    var credential: String? = null
    override fun readCredential(): String? = credential
    override fun writeCredential(value: String) { credential = value }
    override fun clearCredential() { credential = null }
}

/** Hasher determinista (NO criptográfico): suficiente para probar la lógica. */
private object DeterministicHasher : PinHasher {
    override fun derive(pin: CharArray, salt: ByteArray, iterations: Int, keyLengthBytes: Int): ByteArray {
        val out = ByteArray(keyLengthBytes)
        for (index in out.indices) {
            val pinByte = pin[index % pin.size].code
            out[index] = (pinByte + salt[index % salt.size] + iterations % 251).toByte()
        }
        return out
    }
}

private fun protection(
    store: InMemoryProtectionStore = InMemoryProtectionStore(),
    now: () -> Long = { 1_000L },
) = store to LocalProtectionRepository(
    store = store,
    hasher = DeterministicHasher,
    randomBytes = { size -> ByteArray(size) { (it + 1).toByte() } },
    now = now,
)

class LocalProfileRepositoryTest {

    @Test
    fun creates_profile_once_and_is_idempotent() {
        val store = InMemoryProfileStore()
        var counter = 0
        val repository = LocalProfileRepository(
            store = store,
            newId = { "profile-${counter++}" },
            now = { 42L },
        )

        val first = repository.getOrCreate()
        val second = repository.getOrCreate()

        assertEquals("profile-0", first.id)
        assertEquals(LocalProfileSchemaVersionForTest, first.schemaVersion)
        assertEquals(42L, first.createdAtEpochMillis)
        assertEquals(first, second)
        assertEquals(1, counter)
    }

    @Test
    fun recovers_profile_after_restart() {
        val store = InMemoryProfileStore()
        val created = LocalProfileRepository(store = store, newId = { "profile-a" }).getOrCreate()
        val reopened = LocalProfileRepository(store = store, newId = { "profile-b" })
        assertEquals(created, reopened.current())
    }

    @Test
    fun corrupt_profile_is_treated_as_absent_and_recreated() {
        val store = InMemoryProfileStore().apply { value = "not-json" }
        val repository = LocalProfileRepository(store = store, newId = { "profile-c" })
        assertEquals(null, repository.current())
        assertEquals("profile-c", repository.getOrCreate().id)
    }
}

private const val LocalProfileSchemaVersionForTest = 1

class LocalProtectionRepositoryTest {

    @Test
    fun configures_and_verifies_pin() {
        val (store, repository) = protection()
        assertFalse(repository.isConfigured())
        assertEquals(PinUpdate.Success, repository.configure("1234".toCharArray(), "1234".toCharArray()))
        assertTrue(repository.isConfigured())
        assertEquals(PinVerification.Success, repository.verify("1234".toCharArray()))
        // el verificador persistido no contiene el PIN en claro
        assertNotNull(store.credential)
        assertFalse(store.credential!!.contains("1234"))
    }

    @Test
    fun rejects_mismatched_confirmation() {
        val (_, repository) = protection()
        assertEquals(
            PinUpdate.Rejected(PinRejectionReason.Mismatch),
            repository.configure("1234".toCharArray(), "4321".toCharArray()),
        )
        assertFalse(repository.isConfigured())
    }

    @Test
    fun rejects_non_digits_and_bad_length() {
        val (_, repository) = protection()
        assertEquals(
            PinUpdate.Rejected(PinRejectionReason.NonDigits),
            repository.configure("12a4".toCharArray(), "12a4".toCharArray()),
        )
        assertEquals(
            PinUpdate.Rejected(PinRejectionReason.TooShort),
            repository.configure("123".toCharArray(), "123".toCharArray()),
        )
        assertEquals(
            PinUpdate.Rejected(PinRejectionReason.TooLong),
            repository.configure("1234567890123".toCharArray(), "1234567890123".toCharArray()),
        )
    }

    @Test
    fun wrong_pin_decrements_remaining_attempts() {
        val (_, repository) = protection()
        repository.configure("1234".toCharArray(), "1234".toCharArray())
        val first = repository.verify("0000".toCharArray())
        assertEquals(PinVerification.Incorrect(PinPolicy.MAX_ATTEMPTS - 1), first)
        val second = repository.verify("0000".toCharArray())
        assertEquals(PinVerification.Incorrect(PinPolicy.MAX_ATTEMPTS - 2), second)
    }

    @Test
    fun locks_out_after_max_attempts_and_recovers_after_backoff() {
        var clock = 1_000L
        val (_, repository) = protection(now = { clock })
        repository.configure("1234".toCharArray(), "1234".toCharArray())
        repeat(PinPolicy.MAX_ATTEMPTS) { repository.verify("0000".toCharArray()) }

        val locked = repository.verify("1234".toCharArray())
        assertTrue(locked is PinVerification.LockedOut, "esperado bloqueo temporal, fue $locked")
        assertTrue(repository.lockoutRemainingMillis() > 0L)

        // avanza el reloj más allá del retardo: el PIN correcto vuelve a entrar
        clock += PinPolicy.MAX_BACKOFF_MILLIS + 1
        assertEquals(PinVerification.Success, repository.verify("1234".toCharArray()))
        assertEquals(PinPolicy.MAX_ATTEMPTS, repository.remainingAttempts())
    }

    @Test
    fun change_requires_current_pin_and_rehashes() {
        val (store, repository) = protection()
        repository.configure("1234".toCharArray(), "1234".toCharArray())
        val before = store.credential

        assertEquals(
            PinUpdate.Rejected(PinRejectionReason.IncorrectCurrent),
            repository.change("0000".toCharArray(), "5678".toCharArray(), "5678".toCharArray()),
        )
        assertEquals(PinUpdate.Success, repository.change("1234".toCharArray(), "5678".toCharArray(), "5678".toCharArray()))
        assertNotEquals(before, store.credential)
        assertEquals(PinVerification.Success, repository.verify("5678".toCharArray()))
        assertEquals(PinVerification.Incorrect(PinPolicy.MAX_ATTEMPTS - 1), repository.verify("1234".toCharArray()))
    }

    @Test
    fun clear_removes_protection() {
        val (_, repository) = protection()
        repository.configure("1234".toCharArray(), "1234".toCharArray())
        repository.clear()
        assertFalse(repository.isConfigured())
        assertEquals(PinVerification.NotConfigured, repository.verify("1234".toCharArray()))
    }
}
