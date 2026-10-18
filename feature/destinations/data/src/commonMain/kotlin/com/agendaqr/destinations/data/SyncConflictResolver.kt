package com.agendaqr.destinations.data

/**
 * Estrategia de resolución de conflictos para la sincronización Local-First
 * contra Supabase.
 *
 * ## Modelo
 * Cada registro versionado expone un `updatedAt` en milisegundos (reloj del
 * dispositivo que lo escribió). Cuando una mutación offline se drena, el
 * procesador compara tres marcas:
 * - `baseUpdatedAt`: versión que el cliente conocía al encolar (puede ser
 *   nula en mutaciones antiguas).
 * - `localUpdatedAt`: versión local actual.
 * - `remoteUpdatedAt`: versión del servidor (nula si no existe remoto).
 *
 * ## Regla: Last-Write-Wins con tolerancia de skew
 * Gana el lado con `updatedAt` mayor. Diferencias dentro de
 * [clockSkewToleranceMillis] se consideran empate y gana el servidor para que
 * todos los dispositivos converjan al mismo valor de forma determinista.
 *
 * ## Divergencia
 * Si `base != null` y tanto el servidor como el cliente avanzaron respecto a
 * la base, hubo edición concurrente real (`diverged = true`). Igual se
 * resuelve por LWW, pero el [SyncConflict] resultante se reporta al
 * observador para telemetría o revisión manual en UI.
 *
 * ## Borrados
 * Un DELETE local compite con el `updatedAt` remoto usando como marca la
 * propia `localUpdatedAt` (o el instante de encolado si se desconoce):
 * solo borra en el servidor si el borrado es estrictamente más nuevo que la
 * última escritura remota; en caso contrario el registro remoto "resucita"
 * localmente (el borrado pierde).
 */
enum class SyncConflictDecision {
    /** El valor local es más nuevo: subir al servidor. */
    PUSH_LOCAL,

    /** El servidor es más nuevo o empató: bajar y sobrescribir local. */
    PULL_REMOTE,
}

data class SyncConflict(
    val resource: SyncResource,
    val entityId: String,
    val localUpdatedAt: Long?,
    val remoteUpdatedAt: Long?,
    val baseUpdatedAt: Long?,
    val diverged: Boolean,
    val decision: SyncConflictDecision,
)

class SyncConflictResolver(
    /** Margen para absorber skew entre relojes de cliente y servidor. */
    val clockSkewToleranceMillis: Long = 1_000L,
) {
    /**
     * Resuelve un UPSERT ([localUpdatedAt] no nulo) o un DELETE
     * ([localUpdatedAt] nulo + [deleteAt] no nulo).
     */
    fun resolve(
        resource: SyncResource,
        entityId: String,
        localUpdatedAt: Long?,
        remoteUpdatedAt: Long?,
        baseUpdatedAt: Long? = null,
        deleteAt: Long? = null,
    ): SyncConflict {
        val effectiveLocal = localUpdatedAt ?: deleteAt
        val diverged = baseUpdatedAt != null &&
            remoteUpdatedAt != null &&
            remoteUpdatedAt != baseUpdatedAt &&
            effectiveLocal != null &&
            effectiveLocal != baseUpdatedAt

        val decision = when {
            effectiveLocal == null -> SyncConflictDecision.PULL_REMOTE
            remoteUpdatedAt == null -> SyncConflictDecision.PUSH_LOCAL
            effectiveLocal - remoteUpdatedAt > clockSkewToleranceMillis ->
                SyncConflictDecision.PUSH_LOCAL
            else -> SyncConflictDecision.PULL_REMOTE
        }
        return SyncConflict(
            resource = resource,
            entityId = entityId,
            localUpdatedAt = effectiveLocal,
            remoteUpdatedAt = remoteUpdatedAt,
            baseUpdatedAt = baseUpdatedAt,
            diverged = diverged,
            decision = decision,
        )
    }

    /** Variante para `syncFromRemote`: ¿debe el snapshot remoto pisar el local? */
    fun shouldApplyRemote(
        localUpdatedAt: Long?,
        remoteUpdatedAt: Long,
        baseUpdatedAt: Long? = null,
    ): Boolean {
        if (localUpdatedAt == null) return true
        // Si el remoto no avanzó respecto a la base, no hay nada nuevo.
        if (baseUpdatedAt != null && remoteUpdatedAt == baseUpdatedAt) return false
        // Empate (dentro de tolerancia) → gana el servidor, igual que en drain.
        return !(localUpdatedAt - remoteUpdatedAt > clockSkewToleranceMillis)
    }
}
