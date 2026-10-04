package com.agendaqr.destinations.presentation

import com.agendaqr.destinations.data.PendingSyncMutation
import com.agendaqr.destinations.data.SyncMutationState
import com.agendaqr.destinations.data.SyncMutationType
import com.agendaqr.destinations.data.SyncResource
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * T6 — Estados Guardado / Pendiente / Sincronizado visibles por elemento.
 *
 * Contrato local-first §5: GUARDADO → PENDIENTE → SINCRONIZANDO →
 * SINCRONIZADO (+ ERROR RECUPERABLE con REINTENTAR). El estado se muestra
 * con TEXTO (no solo color).
 */
class ElementSyncStatusTest {

    private fun mutation(
        resource: SyncResource,
        entityId: String,
        state: SyncMutationState,
    ) = PendingSyncMutation(
        id = "m-${resource.name}-$entityId",
        resource = resource,
        mutation = SyncMutationType.UPSERT,
        entityId = entityId,
        enqueuedAt = 1,
        state = state,
    )

    @Test
    fun absent_mutation_means_synced() {
        val status = elementSyncStatus(emptyList(), SyncResource.OPERATION, "op-1")
        assertEquals(ElementSyncStatus.Synced, status)
        assertEquals("Sincronizado", status.label)
    }

    @Test
    fun pending_mutation_shows_pendiente() {
        val queue = listOf(mutation(SyncResource.OPERATION, "op-1", SyncMutationState.PENDING))
        val status = elementSyncStatus(queue, SyncResource.OPERATION, "op-1")
        assertEquals(ElementSyncStatus.Pending, status)
        // "Guardado" localmente + esperando: el dato está a salvo.
        assertEquals("Pendiente", status.label)
    }

    @Test
    fun processing_mutation_shows_sincronizando() {
        val queue = listOf(mutation(SyncResource.DESTINATION, "d-1", SyncMutationState.PROCESSING))
        assertEquals(
            ElementSyncStatus.Syncing,
            elementSyncStatus(queue, SyncResource.DESTINATION, "d-1"),
        )
        assertEquals("Sincronizando", ElementSyncStatus.Syncing.label)
    }

    @Test
    fun failed_mutation_shows_error_recuperable() {
        val queue = listOf(mutation(SyncResource.COMPROBANTE, "r-1", SyncMutationState.FAILED))
        assertEquals(
            ElementSyncStatus.ErrorRecoverable,
            elementSyncStatus(queue, SyncResource.COMPROBANTE, "r-1"),
        )
        assertEquals("Error recuperable", ElementSyncStatus.ErrorRecoverable.label)
    }

    @Test
    fun dead_letter_quarantine_is_visible_as_an_error_to_the_user() {
        val queue = listOf(mutation(SyncResource.OPERATION, "op-1", SyncMutationState.DEAD_LETTER))
        assertEquals(
            ElementSyncStatus.ErrorRecoverable,
            elementSyncStatus(queue, SyncResource.OPERATION, "op-1"),
        )
    }

    @Test
    fun lookup_is_scoped_to_the_exact_resource_and_entity() {
        // La misma entidad en dos recursos no colisiona; otro id no se ve
        // afectado por la mutación ajena.
        val queue = listOf(
            mutation(SyncResource.OPERATION, "op-1", SyncMutationState.PENDING),
            mutation(SyncResource.COMPROBANTE, "r-1", SyncMutationState.FAILED),
        )
        val lookup = ElementSyncLookup(queue)
        assertEquals(ElementSyncStatus.Pending, lookup.status(SyncResource.OPERATION, "op-1"))
        assertEquals(ElementSyncStatus.Synced, lookup.status(SyncResource.OPERATION, "op-2"))
        assertEquals(ElementSyncStatus.ErrorRecoverable, lookup.status(SyncResource.COMPROBANTE, "r-1"))
        // Un id con el mismo valor en otro recurso está sincronizado.
        assertEquals(ElementSyncStatus.Synced, lookup.status(SyncResource.DESTINATION, "op-1"))
    }

    @Test
    fun last_mutation_for_an_entity_wins_in_the_index() {
        // El observer puede emitir la misma entidad con estado distinto;
        // la versión más reciente (última en la lista) es la vigente.
        val queue = listOf(
            mutation(SyncResource.OPERATION, "op-1", SyncMutationState.PENDING),
            mutation(SyncResource.OPERATION, "op-1", SyncMutationState.FAILED),
        )
        assertEquals(
            ElementSyncStatus.ErrorRecoverable,
            elementSyncStatus(queue, SyncResource.OPERATION, "op-1"),
        )
    }

    @Test
    fun labels_are_text_not_only_color() {
        // Spec §11: significado independiente del color; los cuatro estados
        // del contrato §5 tienen texto distinguible.
        val labels = ElementSyncStatus.values().map { it.label }
        assertEquals(4, labels.toSet().size)
        for (status in ElementSyncStatus.values()) {
            // kotlin-test común: el mensaje va primero.
            assertTrue("El estado $status necesita texto") { status.label.isNotBlank() }
        }
    }
}
