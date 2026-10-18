package com.agendaqr.destinations.presentation

import com.agendaqr.destinations.domain.ImportBatch
import com.agendaqr.destinations.domain.ImportCandidate
import com.agendaqr.destinations.domain.ImportKind
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * U1 — contadores en español: singular honesto para N=1, copy congelado
 * intacto para N≠1, cero concatenación manual en las pantallas.
 */
class CountersTest {

    private fun pendingBatch(n: Int) = ImportBatch(
        (0 until n).map { ImportCandidate("u-$it", ImportKind.DESCONOCIDO, "fp-$it") },
    )

    @Test
    fun review_button_uses_singular_for_one() {
        assertEquals("Revisar 1 pendiente", reviewPendingLabel(pendingBatch(1)))
        assertEquals("Revisar 0 pendientes", reviewPendingLabel(pendingBatch(0)))
        assertEquals("Revisar 2 pendientes", reviewPendingLabel(pendingBatch(2)))
    }

    @Test
    fun unassociated_tray_label_agrees_with_count() {
        assertEquals("Comprobante sin asociar: 1", unassociatedReceiptsLabel(1))
        assertEquals("Comprobantes sin asociar: 0", unassignedSafe(0))
        assertEquals("Comprobantes sin asociar: 2", unassociatedReceiptsLabel(2))
    }

    private fun unassignedSafe(count: Int) = unassociatedReceiptsLabel(count)

    @Test
    fun sync_banner_keeps_the_frozen_copy_for_any_count() {
        assertEquals("Sincronización pendiente: 1", syncPendingLabel(1, retrying = false))
        assertEquals("Sincronización pendiente: 2 · reintentando…", syncPendingLabel(2, retrying = true))
    }

    @Test
    fun context_sections_agree_with_their_count() {
        assertEquals("QR · 1", contextSectionLabel("QR", "QR", 1))
        assertEquals("QR · 3", contextSectionLabel("QR", "QR", 3))
        assertEquals("Comprobante · 1", contextSectionLabel("Comprobante", "Comprobantes", 1))
        assertEquals("Comprobantes · 4", contextSectionLabel("Comprobante", "Comprobantes", 4))
        assertEquals("Actividad reciente · 1", contextSectionLabel("Actividad reciente", "Actividades recientes", 1))
        assertEquals("Actividades recientes · 5", contextSectionLabel("Actividad reciente", "Actividades recientes", 5))
    }

    @Test
    fun spanish_plural_rules() {
        assertEquals("pendiente", spanishPlural("pendiente", 1))
        assertEquals("pendientes", spanishPlural("pendiente", 2))
        assertEquals("comprobantes", spanishPlural("comprobante", 0))
        assertEquals("lápices", spanishPlural("lápiz", 2))
        // Siglas invariables: QR no cambia (se declara singular=plural).
        assertEquals("QR", counterLabel("QR", "QR", 2))
    }
}
