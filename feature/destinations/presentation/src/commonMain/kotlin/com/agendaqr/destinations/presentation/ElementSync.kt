package com.agendaqr.destinations.presentation

import com.agendaqr.core.ui.components.XauxaBadge
import com.agendaqr.core.ui.components.XauxaTone
import com.agendaqr.destinations.data.PendingSyncMutation
import com.agendaqr.destinations.data.SyncMutationState
import com.agendaqr.destinations.data.SyncResource
import androidx.compose.runtime.Composable

/**
 * Estado de sincronización por elemento (T6) — contrato local-first §5:
 *
 *     GUARDADO → PENDIENTE → SINCRONIZANDO → SINCRONIZADO (+ ERROR RECUPERABLE)
 *
 * En esta implementación el guardado local y el encolado son atómicos (los
 * wrappers Sync escriben y encolan), así que "Guardado" y "Pendiente"
 * coinciden para el usuario: el elemento está a salvo en el dispositivo y
 * esperando sincronizar. Cuando la mutación sale de la cola, el elemento
 * está sincronizado con Supabase.
 *
 * Se muestra con TEXTO (no solo color), con significado independiente del
 * color (spec §11).
 */
enum class ElementSyncStatus(val label: String) {
    /** Sin mutaciones en cola: el elemento está sincronizado con Supabase. */
    Synced("Sincronizado"),

    /** Guardado localmente y esperando sincronizar (contrato §5). */
    Pending("Pendiente"),

    /** Mutación en vuelo. */
    Syncing("Sincronizando"),

    /** Falló la sincronización; el dato está a salvo y se puede reintentar. */
    ErrorRecoverable("Error recuperable"),
}

fun ElementSyncStatus.tone(): XauxaTone = when (this) {
    ElementSyncStatus.Synced -> XauxaTone.Success
    ElementSyncStatus.Pending -> XauxaTone.Neutral
    ElementSyncStatus.Syncing -> XauxaTone.Info
    ElementSyncStatus.ErrorRecoverable -> XauxaTone.Danger
}

/** Insignia con texto del estado de sincronización del elemento. */
@Composable
fun ElementSyncBadge(status: ElementSyncStatus) {
    XauxaBadge(text = status.label, tone = status.tone())
}

/**
 * Mapeo cola → estado por (resource, entityId). Puro y testeable.
 *
 * - Sin mutación en cola → [ElementSyncStatus.Synced].
 * - PENDING → [ElementSyncStatus.Pending] (guardado localmente, esperando).
 * - PROCESSING → [ElementSyncStatus.Syncing].
 * - FAILED (y DEAD_LETTER, cuarentena permanente pendiente de A3) →
 *   [ElementSyncStatus.ErrorRecoverable]; el dato sigue a salvo localmente
 *   y el banner global ofrece REINTENTAR (drain).
 */
fun elementSyncStatus(
    queue: List<PendingSyncMutation>,
    resource: SyncResource,
    entityId: String,
): ElementSyncStatus {
    // La cola deduplica por (resource, entityId); ante una snapshot anómala
    // la última posición es la más reciente.
    val mutation = queue.lastOrNull { it.resource == resource && it.entityId == entityId }
        ?: return ElementSyncStatus.Synced
    return when (mutation.state) {
        SyncMutationState.PENDING -> ElementSyncStatus.Pending
        SyncMutationState.PROCESSING -> ElementSyncStatus.Syncing
        SyncMutationState.FAILED, SyncMutationState.DEAD_LETTER ->
            ElementSyncStatus.ErrorRecoverable
    }
}

/**
 * Consulta barata por elemento para la UI: indexa la cola una vez por
 * emisión del observador en lugar de recorrerla por fila.
 */
class ElementSyncLookup(private val queue: List<PendingSyncMutation>) {
    private val byEntity: Map<Pair<SyncResource, String>, PendingSyncMutation> =
        queue.associateBy { it.resource to it.entityId }

    fun status(resource: SyncResource, entityId: String): ElementSyncStatus {
        val mutation = byEntity[resource to entityId]
            ?: return ElementSyncStatus.Synced
        return when (mutation.state) {
            SyncMutationState.PENDING -> ElementSyncStatus.Pending
            SyncMutationState.PROCESSING -> ElementSyncStatus.Syncing
            SyncMutationState.FAILED, SyncMutationState.DEAD_LETTER ->
                ElementSyncStatus.ErrorRecoverable
        }
    }

    companion object {
        val Empty = ElementSyncLookup(emptyList())
    }
}
