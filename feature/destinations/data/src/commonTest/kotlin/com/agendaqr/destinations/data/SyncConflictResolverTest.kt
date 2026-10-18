package com.agendaqr.destinations.data

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class SyncConflictResolverTest {

    private val resolver = SyncConflictResolver(clockSkewToleranceMillis = 1_000L)

    @Test
    fun local_newer_pushes() {
        val conflict = resolver.resolve(SyncResource.OPERATION, "op-1", 5_000L, 2_000L, 1_000L)
        assertEquals(SyncConflictDecision.PUSH_LOCAL, conflict.decision)
        assertTrue(conflict.diverged)
    }

    @Test
    fun remote_newer_pulls() {
        val conflict = resolver.resolve(SyncResource.OPERATION, "op-1", 1_000L, 5_000L, 1_000L)
        assertEquals(SyncConflictDecision.PULL_REMOTE, conflict.decision)
        assertFalse(conflict.diverged)
    }

    @Test
    fun tie_goes_to_server_for_convergence() {
        val conflict = resolver.resolve(SyncResource.DESTINATION, "d-1", 2_000L, 2_000L, 1_000L)
        assertEquals(SyncConflictDecision.PULL_REMOTE, conflict.decision)
    }

    @Test
    fun skew_within_tolerance_goes_to_server() {
        // Relojes con 500ms de skew no deben flapear: gana el servidor.
        val conflict = resolver.resolve(SyncResource.DESTINATION, "d-1", 2_500L, 2_000L, 1_000L)
        assertEquals(SyncConflictDecision.PULL_REMOTE, conflict.decision)
    }

    @Test
    fun skew_beyond_tolerance_pushes() {
        val conflict = resolver.resolve(SyncResource.DESTINATION, "d-1", 3_500L, 2_000L, 1_000L)
        assertEquals(SyncConflictDecision.PUSH_LOCAL, conflict.decision)
    }

    @Test
    fun missing_remote_always_pushes() {
        val conflict = resolver.resolve(SyncResource.CONTEXT, "c-1", 7_000L, null, 6_000L)
        assertEquals(SyncConflictDecision.PUSH_LOCAL, conflict.decision)
        assertFalse(conflict.diverged)
    }

    @Test
    fun delete_newer_than_remote_pushes_delete() {
        val conflict = resolver.resolve(
            resource = SyncResource.OPERATION,
            entityId = "op-1",
            localUpdatedAt = null,
            remoteUpdatedAt = 1_000L,
            baseUpdatedAt = 1_000L,
            deleteAt = 5_000L,
        )
        assertEquals(SyncConflictDecision.PUSH_LOCAL, conflict.decision)
    }

    @Test
    fun delete_older_than_remote_resurrects() {
        val conflict = resolver.resolve(
            resource = SyncResource.OPERATION,
            entityId = "op-1",
            localUpdatedAt = null,
            remoteUpdatedAt = 5_000L,
            baseUpdatedAt = 1_000L,
            deleteAt = 2_000L,
        )
        assertEquals(SyncConflictDecision.PULL_REMOTE, conflict.decision)
    }

    @Test
    fun shouldApplyRemote_server_wins_ties() {
        assertTrue(resolver.shouldApplyRemote(2_000L, 2_000L))
        assertTrue(resolver.shouldApplyRemote(1_000L, 5_000L))
        assertFalse(resolver.shouldApplyRemote(5_000L, 1_000L))
        assertTrue(resolver.shouldApplyRemote(null, 1_000L))
    }

    @Test
    fun error_classifier_marks_auth_and_schema_as_permanent() {
        assertTrue(SyncErrorClassifier.isTransient(IllegalStateException("connection reset")))
        assertTrue(SyncErrorClassifier.isTransient(IllegalStateException("remote unavailable")))
        assertFalse(SyncErrorClassifier.isTransient(IllegalStateException("permission denied by RLS")))
        assertFalse(SyncErrorClassifier.isTransient(IllegalStateException("JWT expired")))
        assertFalse(SyncErrorClassifier.isTransient(IllegalStateException("duplicate key value")))
    }

    @Test
    fun retry_policy_is_exponential_and_bounded() {
        val policy = SyncRetryPolicy(baseDelayMillis = 2_000L, maxDelayMillis = 60_000L)
        assertEquals(2_000L, policy.delayFor(1))
        assertEquals(4_000L, policy.delayFor(2))
        assertEquals(8_000L, policy.delayFor(3))
        assertEquals(60_000L, policy.delayFor(30))
    }
}
