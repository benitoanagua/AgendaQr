package com.agendaqr.destinations.presentation

import com.agendaqr.core.ui.components.XauxaBadge
import com.agendaqr.core.ui.components.XauxaTone
import com.agendaqr.destinations.data.PendingSyncMutation
import com.agendaqr.destinations.data.SyncMutationState
import com.agendaqr.destinations.data.SyncResource
import androidx.compose.runtime.Composable

/**
 * Estado de sincronización por elemento  — contrato local-first §5:
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

    /** Error TRANSITORIO (FAILED): el drain lo reintentará; REINTENTAR ayuda. */
    ErrorRecoverable("Error recuperable"),

    /**
     * U4 — DEAD_LETTER: error permanente (validación/esquema/permisos) o
     * reintentos agotados. El drain NO lo retoma: REINTENTAR no puede
     * recuperarlo (verificaría como falso). El dato está a salvo en el
     * dispositivo; la acción de recuperación (re-encolar) no existe en el
     * contrato V1 y se propone por ADR.
     */
    Dead("No se pudo sincronizar"),
}

fun ElementSyncStatus.tone(): XauxaTone = when (this) {
    ElementSyncStatus.Synced -> XauxaTone.Success
    ElementSyncStatus.Pending -> XauxaTone.Neutral
    ElementSyncStatus.Syncing -> XauxaTone.Info
    ElementSyncStatus.ErrorRecoverable, ElementSyncStatus.Dead -> XauxaTone.Danger
}

/**
 * Insignia del estado de sincronización del elemento.
 *
 * Defecto F-1 (badge que compite por ancho): el estado SINCRONIZADO es el
 * estado normal — mostrar "Sincronizado" en cada fila es ruido que consume
 * espacio en pantallas estrechas. La insignia solo aparece cuando hay algo
 * que el usuario necesita saber (Pendiente, Sincronizando o Error). El
 * estado sigue siendo consultable en el detalle.
 */
@Composable
fun ElementSyncBadge(status: ElementSyncStatus, showWhenSynced: Boolean = false) {
    if (showWhenSynced || status != ElementSyncStatus.Synced) {
        XauxaBadge(text = status.label, tone = status.tone())
    }
}

/**
 * Mapeo cola → estado por (resource, entityId). Puro y testeable.
 *
 * - Sin mutación en cola → [ElementSyncStatus.Synced].
 * - PENDING → [ElementSyncStatus.Pending] (guardado localmente, esperando).
 * - PROCESSING → [ElementSyncStatus.Syncing].
 * - FAILED → [ElementSyncStatus.ErrorRecoverable]: error transitorio; el
 *   drain (y su REINTENTAR) lo reintenta.
 * - DEAD_LETTER → [ElementSyncStatus.Dead] (U4): error permanente o
 *   reintentos agotados; el drain lo omite, así que REINTENTAR no se
 *   ofrece para estos elementos — la recuperación requeriría re-encolar
 *   (acción nueva: ADR-0004). El dato está a salvo localmente.
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
        SyncMutationState.FAILED -> ElementSyncStatus.ErrorRecoverable
        SyncMutationState.DEAD_LETTER -> ElementSyncStatus.Dead
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
            SyncMutationState.FAILED -> ElementSyncStatus.ErrorRecoverable
            SyncMutationState.DEAD_LETTER -> ElementSyncStatus.Dead
        }
    }

    companion object {
        val Empty = ElementSyncLookup(emptyList())
    }
}
