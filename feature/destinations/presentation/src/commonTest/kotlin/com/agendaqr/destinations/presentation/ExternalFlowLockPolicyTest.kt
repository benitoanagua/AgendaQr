package com.agendaqr.destinations.presentation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Política de bloqueo para flujos externos (D1) con reloj falso: fija las
 * secuencias STOP/START/RESUME, incluida la limpieza en ON_RESUME que evita
 * que una marca sobreviva a un regreso a primer plano sin STOP (hoja de
 * compartir o diálogo de permiso translúcidos).
 */
class ExternalFlowLockPolicyTest {

    private var now = 1_000L
    private val tracker = ExternalFlowTracker(clock = { now })
    private val policy = ExternalFlowLockPolicy(tracker, now = { now })
    private var locks = 0
    private val lock: () -> Unit = { locks++ }

    @Test
    fun a_stop_and_start_within_grace_do_not_lock_and_resume_clears() {
        tracker.markLaunch()
        policy.onStop(demoShowing = false, lock = lock)
        assertEquals(0, locks, "dentro de la gracia no se bloquea en ON_STOP")
        policy.onStart(demoShowing = false, lock = lock)
        assertEquals(0, locks, "dentro de la gracia no se bloquea en ON_START")
        assertFalse(tracker.hasPendingLaunch(), "ON_START consume la marca")
        policy.onResume()
        assertEquals(0, locks)
        assertFalse(tracker.hasPendingLaunch())
    }

    @Test
    fun b_resume_without_stop_clears_the_mark_and_a_later_stop_locks() {
        tracker.markLaunch()
        // Flujo translúcido: regreso a primer plano (PAUSE → RESUME) sin STOP.
        policy.onResume()
        assertFalse(tracker.hasPendingLaunch(), "ON_RESUME limpia la marca pendiente")
        // Un paso a segundo plano posterior ya NO tiene gracia: bloquea.
        policy.onStop(demoShowing = false, lock = lock)
        assertEquals(1, locks)
    }

    @Test
    fun c_start_after_the_grace_expires_locks() {
        tracker.markLaunch()
        now += ExternalFlowGraceMillis
        policy.onStart(demoShowing = false, lock = lock)
        assertEquals(1, locks, "gracia vencida: bloquea al volver")
        assertFalse(tracker.hasPendingLaunch())
    }

    @Test
    fun d_stop_without_any_mark_locks() {
        policy.onStop(demoShowing = false, lock = lock)
        assertEquals(1, locks, "fail-closed sin marca")
    }

    @Test
    fun demo_never_locks_in_any_event() {
        tracker.markLaunch()
        now += ExternalFlowGraceMillis
        policy.onStop(demoShowing = true, lock = lock)
        policy.onStart(demoShowing = true, lock = lock)
        assertEquals(0, locks)
        // ON_RESUME limpia igualmente (no hay nada que proteger en demo).
        policy.onResume()
        assertFalse(tracker.hasPendingLaunch())
    }

    @Test
    fun resume_is_a_no_op_without_a_mark() {
        policy.onResume()
        assertFalse(tracker.hasPendingLaunch())
        assertTrue(locks == 0)
    }
}
