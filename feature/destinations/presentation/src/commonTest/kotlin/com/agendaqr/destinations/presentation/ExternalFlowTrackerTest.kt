package com.agendaqr.destinations.presentation

import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * U-02: gracia acotada del bloqueo para flujos externos iniciados por la
 * app (D1). Reloj falso: sin dependencia de Android.
 */
class ExternalFlowTrackerTest {

    @Test
    fun without_mark_the_lock_is_never_skipped() {
        val tracker = ExternalFlowTracker(clock = { 1_000L })
        assertFalse(tracker.hasPendingLaunch())
        assertFalse(tracker.shouldSkipLock(now = 1_000L))
        // Fail-closed: la política bloquea exactamente como hoy.
        assertTrue(shouldLockOnStop(demoShowing = false, tracker = tracker, nowMillis = 1_000L))
        assertFalse(shouldLockOnStop(demoShowing = true, tracker = tracker, nowMillis = 1_000L))
    }

    @Test
    fun recent_mark_skips_the_lock() {
        var now = 1_000L
        val tracker = ExternalFlowTracker(clock = { now })
        tracker.markLaunch()
        assertTrue(tracker.hasPendingLaunch())
        now += 119_000L
        assertTrue(tracker.shouldSkipLock(now = now))
        assertFalse(shouldLockOnStop(demoShowing = false, tracker = tracker, nowMillis = now))
    }

    @Test
    fun expired_mark_does_not_skip_the_lock() {
        var now = 1_000L
        val tracker = ExternalFlowTracker(clock = { now })
        tracker.markLaunch()
        // Gracia de 120 s (D1): en el límite exacto ya venció.
        now += ExternalFlowGraceMillis
        assertFalse(tracker.shouldSkipLock(now = now))
        assertTrue(shouldLockOnStop(demoShowing = false, tracker = tracker, nowMillis = now))
    }

    @Test
    fun clear_consumes_the_mark() {
        val tracker = ExternalFlowTracker(clock = { 1_000L })
        tracker.markLaunch()
        tracker.clear()
        assertFalse(tracker.hasPendingLaunch())
        assertFalse(tracker.shouldSkipLock(now = 2_000L))
    }

    @Test
    fun manual_lock_is_never_skipped() {
        // El rastreador solo afecta a la política automática ON_STOP/
        // ON_START; el bloqueo manual no lo consulta (fail-closed).
        val tracker = ExternalFlowTracker(clock = { 1_000L })
        tracker.markLaunch()
        assertTrue(tracker.shouldSkipLock(now = 2_000L))
    }
}
