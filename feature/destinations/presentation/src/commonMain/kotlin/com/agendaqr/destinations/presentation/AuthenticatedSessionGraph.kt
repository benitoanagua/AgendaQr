package com.agendaqr.destinations.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import com.agendaqr.destinations.data.LocalComprobanteRepository
import com.agendaqr.destinations.data.LocalContextRepository
import com.agendaqr.destinations.data.LocalDestinationRepository
import com.agendaqr.destinations.data.LocalOperationRepository
import com.agendaqr.destinations.data.LocalSyncQueue
import com.agendaqr.destinations.data.SyncMutationEnqueuer
import com.agendaqr.destinations.data.SyncMutationProcessor
import com.agendaqr.destinations.data.SyncQueueObserver
import com.agendaqr.destinations.data.SyncRecoveryCoordinator
import com.agendaqr.destinations.data.createComprobanteFileStore
import com.agendaqr.destinations.data.createDeletedOperationHistoryRepository
import com.agendaqr.destinations.data.createImportPayloadStore
import com.agendaqr.destinations.data.createRemoteComprobanteRepository
import com.agendaqr.destinations.data.createRemoteContextRepository
import com.agendaqr.destinations.data.createRemoteDestinationRepository
import com.agendaqr.destinations.data.createRemoteOperationRepository
import com.agendaqr.destinations.data.createSyncedComprobanteRepository
import com.agendaqr.destinations.data.createSyncedContextRepository
import com.agendaqr.destinations.data.createSyncedDestinationRepository
import com.agendaqr.destinations.data.createSyncedOperationRepository
import com.agendaqr.destinations.data.platformNetworkMonitor
import com.agendaqr.destinations.data.userScopedKey
import com.agendaqr.destinations.domain.AssociateComprobanteToOperationUseCase
import com.agendaqr.destinations.domain.DeleteComprobanteUseCase
import com.agendaqr.destinations.domain.DeleteDestinationUseCase
import com.agendaqr.destinations.domain.DeleteOperationWithHistoryUseCase
import com.agendaqr.destinations.domain.FindDuplicateComprobantesUseCase
import com.agendaqr.destinations.domain.GetComprobanteUseCase
import com.agendaqr.destinations.domain.GetContextUseCase
import com.agendaqr.destinations.domain.GetDestinationUseCase
import com.agendaqr.destinations.domain.GetOperationUseCase
import com.agendaqr.destinations.domain.ImportPayloadStore
import com.agendaqr.destinations.domain.ObserveContextContentsUseCase
import com.agendaqr.destinations.domain.ObserveContextsUseCase
import com.agendaqr.destinations.domain.ObserveDestinationsUseCase
import com.agendaqr.destinations.domain.ObserveOperationComprobantesUseCase
import com.agendaqr.destinations.domain.ObserveOperationsUseCase
import com.agendaqr.destinations.domain.ObserveUnassociatedComprobantesUseCase
import com.agendaqr.destinations.domain.SaveComprobanteUseCase
import com.agendaqr.destinations.domain.SaveDestinationUseCase
import com.agendaqr.destinations.domain.SaveImportBatchUseCase
import com.agendaqr.destinations.domain.SaveOperationUseCase
import com.agendaqr.destinations.domain.SearchAgendaQrUseCase
import com.agendaqr.destinations.domain.SuggestReceiptAssociationUseCase
import com.agendaqr.destinations.domain.ToggleFavoriteUseCase
import com.agendaqr.destinations.domain.UnassociateComprobanteUseCase
import com.agendaqr.destinations.domain.UpdateDestinationUseCase
import com.agendaqr.destinations.domain.UpdateOperationUseCase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel

/**
 * Grafo de dependencias de una sesión autenticada (T2).
 *
 * Construye y retiene TODO lo que depende del usuario: scope de sync, cola
 * durable, repositorios locales crudos compartidos, wrappers de escritura
 * que encolan, procesador, recovery y los ViewModels de los cuatro flujos.
 *
 * Reglas preservadas (no renegociables):
 * - aislamiento por usuario: cada clave de almacenamiento es user-scoped
 *   y el grafo se reconstruye al cambiar `userId`;
 * - modelo local-first: la UI escribe vía los wrappers Sync (encolan) y el
 *   procesador opera sobre los locales crudos compartidos;
 * - el scope se cancela solo al salir de composición esta sesión; el
 *   coordinator se detiene por separado con `recovery.stop()` para que
 *   "Reintentar ahora" y los `syncScope.launch` sigan funcionando.
 */
@Suppress("LongParameterList", "TooManyFunctions")
class AuthenticatedSessionGraph(
    val userId: String,
    val syncScope: CoroutineScope,
    val syncQueue: LocalSyncQueue,
    val syncProcessor: SyncMutationProcessor,
    val recovery: SyncRecoveryCoordinator,
    val queueObserver: SyncQueueObserver,
    val destinationRepository: com.agendaqr.destinations.domain.DestinationRepository,
    val operationRepository: com.agendaqr.destinations.domain.OperationRepository,
    val comprobanteRepository: com.agendaqr.destinations.domain.ComprobanteRepository,
    val comprobanteFiles: com.agendaqr.destinations.domain.ComprobanteFileStore,
    val contextRepository: com.agendaqr.destinations.domain.ContextRepository,
    val importPayloadStore: ImportPayloadStore,
    val saveImportBatch: SaveImportBatchUseCase,
    val destinationsViewModel: DestinationsViewModel,
    val operationsViewModel: OperationsViewModel,
    val contextsViewModel: ContextsViewModel,
    val globalSearchViewModel: GlobalSearchViewModel,
)

/**
 * Construye el grafo ligado a [userId] con el mismo ciclo de vida que la
 * sesión: sobrevive a recomposiciones, se reconstruye al cambiar usuario
 * y libera sus recursos al salir de composición.
 */
@Composable
fun rememberAuthenticatedSessionGraph(userId: String): AuthenticatedSessionGraph {
    val syncScope = remember(userId) { CoroutineScope(SupervisorJob() + Dispatchers.Default) }
    DisposableEffect(syncScope) {
        onDispose { syncScope.coroutineContext.cancel() }
    }
    val syncQueue = remember(userId) { LocalSyncQueue() }
    val syncEnqueuer = remember(syncQueue) { SyncMutationEnqueuer(syncQueue) }
    // Estado local crudo compartido: los wrappers Sync son la vía de escritura
    // de UI (encolan), y el procesador opera sobre estos mismos locales sin
    // re-encolar al aplicar snapshots ganadores del servidor.
    val contextLocal = remember(userId) {
        LocalContextRepository(storageKey = userScopedKey("agendaqr.contexts.v1"))
    }
    val destinationLocal = remember(userId) {
        LocalDestinationRepository(storageKey = userScopedKey("agendaqr.destinations.v1"))
    }
    val operationLocal = remember(userId) {
        LocalOperationRepository(storageKey = userScopedKey("agendaqr.operations.v1"))
    }
    val comprobanteLocal = remember(userId) {
        LocalComprobanteRepository(storageKey = userScopedKey("agendaqr.comprobantes.v1"))
    }
    val contextRepository = remember(syncQueue) { createSyncedContextRepository(syncQueue, contextLocal) }
    val destinationRepository = remember(syncEnqueuer) { createSyncedDestinationRepository(syncEnqueuer, destinationLocal) }
    val operationRepository = remember(syncEnqueuer) { createSyncedOperationRepository(syncEnqueuer, operationLocal) }
    val fileStore = remember(userId) { createComprobanteFileStore() }
    val importPayloadStore = remember(userId) { createImportPayloadStore() }
    val comprobanteRepository = remember(syncEnqueuer, fileStore) {
        createSyncedComprobanteRepository(fileStore, syncEnqueuer, comprobanteLocal)
    }
    val syncProcessor = remember(syncQueue, contextLocal, destinationLocal, operationLocal, comprobanteLocal, fileStore) {
        SyncMutationProcessor(
            queue = syncQueue,
            contexts = contextLocal,
            destinations = destinationLocal,
            operations = operationLocal,
            comprobantes = comprobanteLocal,
            remoteContexts = createRemoteContextRepository(),
            remoteDestinations = createRemoteDestinationRepository(),
            remoteOperations = createRemoteOperationRepository(),
            remoteComprobantes = createRemoteComprobanteRepository(),
            fileStore = fileStore,
        )
    }
    val networkMonitor = remember { platformNetworkMonitor() }
    val recovery = remember(syncProcessor, syncScope, networkMonitor) {
        SyncRecoveryCoordinator(syncProcessor, syncScope, networkMonitor = networkMonitor)
    }
    DisposableEffect(recovery) {
        recovery.start()
        onDispose { recovery.stop() }
    }
    val queueObserver = remember(syncQueue) { SyncQueueObserver(syncQueue) }
    val saveImportBatch = remember(destinationRepository, comprobanteRepository, fileStore, importPayloadStore) {
        SaveImportBatchUseCase(destinationRepository, comprobanteRepository, fileStore, importPayloadStore)
    }
    val historyRepository = remember(userId) { createDeletedOperationHistoryRepository() }
    val destinationsViewModel = remember(destinationRepository) {
        DestinationsViewModel(
            observe = ObserveDestinationsUseCase(destinationRepository),
            get = GetDestinationUseCase(destinationRepository),
            save = SaveDestinationUseCase(destinationRepository),
            update = UpdateDestinationUseCase(destinationRepository),
            delete = DeleteDestinationUseCase(destinationRepository),
            toggleFavorite = ToggleFavoriteUseCase(destinationRepository),
        )
    }
    val operationsViewModel = remember(operationRepository, comprobanteRepository, fileStore, historyRepository) {
        OperationsViewModel(
            observeOperations = ObserveOperationsUseCase(operationRepository),
            observeContexts = ObserveContextsUseCase(contextRepository),
            observeUnassociated = ObserveUnassociatedComprobantesUseCase(comprobanteRepository),
            observeOperationComprobantes = ObserveOperationComprobantesUseCase(comprobanteRepository),
            getOperation = GetOperationUseCase(operationRepository),
            saveOperation = SaveOperationUseCase(operationRepository),
            updateOperation = UpdateOperationUseCase(operationRepository),
            deleteOperation = DeleteOperationWithHistoryUseCase(operationRepository, comprobanteRepository, fileStore, historyRepository),
            associate = AssociateComprobanteToOperationUseCase(operationRepository, comprobanteRepository),
            unassociate = UnassociateComprobanteUseCase(comprobanteRepository),
            saveComprobante = SaveComprobanteUseCase(comprobanteRepository, fileStore),
            findDuplicates = FindDuplicateComprobantesUseCase(comprobanteRepository, fileStore),
            getComprobante = GetComprobanteUseCase(comprobanteRepository),
            suggestReceiptAssociation = SuggestReceiptAssociationUseCase(comprobanteRepository, operationRepository),
            deleteComprobante = DeleteComprobanteUseCase(comprobanteRepository, fileStore),
            comprobanteFiles = fileStore,
        )
    }
    val contextsViewModel = remember(contextRepository, destinationRepository, operationRepository, comprobanteRepository) {
        ContextsViewModel(
            observe = ObserveContextsUseCase(contextRepository),
            observeContents = ObserveContextContentsUseCase(
                contextRepository,
                destinationRepository,
                operationRepository,
                comprobanteRepository,
            ),
            get = GetContextUseCase(contextRepository),
        )
    }
    val globalSearchViewModel = remember(contextRepository, destinationRepository, operationRepository, comprobanteRepository) {
        GlobalSearchViewModel(
            search = SearchAgendaQrUseCase(
                contextRepository,
                destinationRepository,
                operationRepository,
                comprobanteRepository,
            ),
        )
    }
    return remember(
        userId, syncScope, syncQueue, syncProcessor, recovery, queueObserver,
        destinationRepository, operationRepository, comprobanteRepository, fileStore,
        contextRepository,
        importPayloadStore, saveImportBatch,
        destinationsViewModel, operationsViewModel, contextsViewModel, globalSearchViewModel,
    ) {
        AuthenticatedSessionGraph(
            userId = userId,
            syncScope = syncScope,
            syncQueue = syncQueue,
            syncProcessor = syncProcessor,
            recovery = recovery,
            queueObserver = queueObserver,
            destinationRepository = destinationRepository,
            operationRepository = operationRepository,
            comprobanteRepository = comprobanteRepository,
            comprobanteFiles = fileStore,
            contextRepository = contextRepository,
            importPayloadStore = importPayloadStore,
            saveImportBatch = saveImportBatch,
            destinationsViewModel = destinationsViewModel,
            operationsViewModel = operationsViewModel,
            contextsViewModel = contextsViewModel,
            globalSearchViewModel = globalSearchViewModel,
        )
    }
}
