package com.agendaqr.destinations.data

import com.agendaqr.destinations.domain.AuthState
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

/**
 * A build without Supabase configuration must start in the signed-out
 * state and surface an explicit configuration error on authentication
 * attempts, instead of crashing while the Supabase client is created.
 */
class UnconfiguredAuthRepositoryTest {

    @Test
    fun unconfigured_build_starts_signed_out_instead_of_crashing() = runTest {
        val state = UnconfiguredAuthRepository().state.first()

        assertEquals(AuthState.SignedOut, state)
    }

    @Test
    fun sign_in_reports_an_explicit_configuration_error() = runTest {
        val error = assertFailsWith<IllegalStateException> {
            UnconfiguredAuthRepository().signIn("user@example.com", "password")
        }

        assertTrue("Supabase" in (error.message ?: ""))
    }

    @Test
    fun sign_up_reports_an_explicit_configuration_error() = runTest {
        val error = assertFailsWith<IllegalStateException> {
            UnconfiguredAuthRepository().signUp("user@example.com", "password")
        }

        assertTrue("Supabase" in (error.message ?: ""))
    }

    @Test
    fun sign_out_is_a_no_op() = runTest {
        UnconfiguredAuthRepository().signOut()
    }
}
