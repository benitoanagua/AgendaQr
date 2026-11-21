/**
 * Detalle de la operación: datos, comprobantes, acciones y confirmación
 * de borrado. Muestra el estado de sincronización por elemento .
 */
package com.agendaqr.destinations.presentation

/**
 * Detalle de la operación: datos, comprobantes, acciones y confirmación
 * de borrado. Estado de sincronización por elemento .
 */
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.AqSnackbarHost
import com.agendaqr.core.ui.components.*
import com.agendaqr.core.ui.theme.*
import com.agendaqr.destinations.data.SyncResource
import com.agendaqr.destinations.domain.*

@Composable
internal fun OperationDetailScreen(
    state: OperationsUiState,
    viewModel: OperationsViewModel,
    appHosts: AppHosts? = null,
    syncLookup: ElementSyncLookup = ElementSyncLookup.Empty,
    onRetrySync: () -> Unit = {},
    /** D8: destino QR vinculado a la actividad (opcional). */
    destinationsViewModel: DestinationsViewModel? = null,
) {
    val operation = viewModel.selectedOperation()
    if (operation == null) {
        AqScreenScaffold(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            AqEmptyState(
                title = AppStrings.OperacionNoEncontrada,
                subtitle = AppStrings.PudoHaberSidoEliminada,
                actionLabel = AppStrings.Volver,
                onAction = { viewModel.onAction(OperationAction.Back) },
            )
        }
        return
    }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    // anuncio de eliminación confirmada (por evento).
    var deleteDoneEvent by remember { mutableStateOf<Any?>(null) }
    AqFeedbackEvent(
        event = deleteDoneEvent,
        message = AppStrings.Eliminado,
        feedbackHost = appHosts?.feedback,
    )
    // V1.4 (§12): detalle con título y flecha atrás en la barra superior;
    // Compartir y Editar como acciones visibles (Editar es la principal),
    // Eliminar en el menú "…" (destructiva, no a un toque).
    AqScreenScaffold(
        scrollable = true,
        topBar = {
            AqTopBar(
                title = AppStrings.Detalle,
                onBack = { viewModel.onAction(OperationAction.Back) },
                backLabel = AppStrings.Volver,
                actions = listOf(
                    AqTopBarAction(label = AppStrings.Compartir, icon = AqIcons.Share, onClick = { shareOperation(operation) }),
                    AqTopBarAction(
                        label = AppStrings.Editar,
                        icon = AqIcons.Edit,
                        onClick = { viewModel.onAction(OperationAction.Edit(operation.id)) },
                    ),
                ),
                overflowActions = listOf(
                    AqOverflowAction(
                        label = AppStrings.EliminarOperacion,
                        icon = AqIcons.Trash,
                        onClick = { showDeleteConfirm = true },
                    ),
                ),
                overflowLabel = AppStrings.Mas,
            )
        },
        snackbarHost = appHosts?.feedback?.let { host -> { AqSnackbarHost(host) } },
        statusContent = appHosts?.status,
    ) {
        // Mapa 1:1 de los bloques contractuales (docs/04-ux/02-especificacion-ux-ui-v1.md,
        // 03-contrato-interaccion-pantallas.md):
        //   Hero → Metadata → SyncStatus → LinkedDestination → ComprobantesSection → DeleteButton
        OperationDetailHero(
            operation = operation,
            contexts = state.contexts,
        )
        OperationDetailMetadata(operation = operation)
        OperationDetailSyncStatus(
            syncLookup = syncLookup,
            operationId = operation.id,
            onRetrySync = onRetrySync,
        )
        OperationDetailLinkedDestination(
            operation = operation,
            destinationsViewModel = destinationsViewModel,
        )
        OperationDetailComprobantesSection(
            comprobantes = state.operationComprobantes,
            onOpenComprobante = { viewModel.onAction(OperationAction.OpenComprobante(it)) },
            onDisassociate = { viewModel.onAction(OperationAction.Disassociate(it)) },
            openUnassociated = { viewModel.openUnassociatedForOperation(operation.id) },
        )
        OperationDetailDeleteButton(
            onDelete = { showDeleteConfirm = true },
        )
    }
    if (showDeleteConfirm) {
        AqDialog(
            title = AppStrings.EliminarOperacion,
            destructive = true,
            message = AppStrings.irreversibleActionWarning,
            confirmLabel = AppStrings.Eliminar,
            onConfirm = {
                showDeleteConfirm = false
                deleteDoneEvent = Any()
                viewModel.onAction(OperationAction.Delete(operation.id))
            },
            dismissLabel = AppStrings.Cancelar,
            onDismiss = { showDeleteConfirm = false },
        )
    }
}

// ---------------------------------------------------------------------------
// Secciones de composición — extraídas para mapear 1:1 el contrato UX
// ---------------------------------------------------------------------------

/** Hero del detalle: tipo, importe, contexto y caja temporal. */
@Composable
private fun OperationDetailHero(
    operation: Operation,
    contexts: List<Context>,
) {
    val amountText = operationAmountText(operation)
    val contextName = contexts.firstOrNull { it.id == operation.contextId }?.name
    AqCard {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(AqSpacing.Md),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AqTonalTag(
                    text = operationTypeLabel(operation.type),
                    tone = operationTypeTone(operation.type),
                    uppercase = true,
                )
                contextName?.let {
                    AqTonalTag(text = it, tone = AqTone.Neutral)
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Xs)) {
                amountText?.let {
                    AqText(
                        it,
                        size = AqType.Amount,
                        fontWeight = AqWeight.SemiBold,
                    )
                }
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AqSpacing.Xs),
                ) {
                    AqIcon(
                        imageVector = AqIcons.Store,
                        contentDescription = null,
                        tint = AqColor.TextSecondary,
                    )
                    AqText(
                        operationTitle(operation),
                        size = AqType.SectionHeader,
                        fontWeight = AqWeight.SemiBold,
                    )
                }
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AqColor.SurfaceContainerLow, AqShapes.Large)
                    .padding(AqSpacing.Md),
                verticalArrangement = Arrangement.spacedBy(AqSpacing.Xs),
            ) {
                AqMetaRow(
                    label = AppStrings.FechaDeOperacion,
                    value = formatDate(operation.occurredAt),
                    icon = AqIcons.Calendar,
                )
                AqMetaRow(
                    label = AppStrings.RegistradoEnDispositivo,
                    value = formatDate(operation.createdAt),
                    icon = AqIcons.Clock,
                )
            }
        }
    }
}

/** Metadatos del detalle: persona/entidad, moneda, concepto, nota. */
@Composable
private fun OperationDetailMetadata(operation: Operation) {
    val hasMetadata = operation.personOrEntity != null ||
        operation.currency != null ||
        operation.concept != null ||
        operation.note != null
    if (hasMetadata) {
        AqCard {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
            ) {
                operation.personOrEntity?.let { AqMetaRow(label = AppStrings.LabelPersonaOEntidad, value = it) }
                operation.currency?.let { AqMetaRow(label = AppStrings.LabelMoneda, value = it) }
                operation.concept?.let { AqMetaRow(label = AppStrings.LabelConcepto, value = it) }
                operation.note?.let {
                    AqText(it, color = AqColor.TextSecondary)
                }
            }
        }
    }
}

/** Estado de sincronización del detalle (texto, no solo color). */
@Composable
private fun OperationDetailSyncStatus(
    syncLookup: ElementSyncLookup,
    operationId: String,
    onRetrySync: () -> Unit,
) {
    val operationSyncStatus = syncLookup.status(SyncResource.OPERATION, operationId)
    ElementSyncBadge(operationSyncStatus)
    if (operationSyncStatus == ElementSyncStatus.ErrorRecoverable) {
        AqTextAction(label = AppStrings.Reintentar, onClick = onRetrySync)
    }
    if (operationSyncStatus == ElementSyncStatus.Dead) {
        AqText(
            text = AppStrings.TuInformacionEstaGuardadaEnElDispositivo,
            color = AqColor.TextSecondary,
        )
    }
}

/** Destino QR vinculado a la actividad, cuando existe. */
@Composable
private fun OperationDetailLinkedDestination(
    operation: Operation,
    destinationsViewModel: DestinationsViewModel?,
) {
    operation.destinationId?.let { destinationId ->
        val destinationName = destinationsViewModel?.destination(destinationId)?.name
        AqMetaRow(
            label = AppStrings.DestinoVinculado,
            value = destinationName ?: destinationId,
        )
    }
}

/** Sección de comprobantes del detalle de operación. */
@Composable
private fun OperationDetailComprobantesSection(
    comprobantes: List<Comprobante>,
    onOpenComprobante: (String) -> Unit,
    onDisassociate: (String) -> Unit,
    openUnassociated: () -> Unit,
) {
    AqSection(AppStrings.SeccionComprobantes) {
        if (comprobantes.isEmpty()) {
            AqText(AppStrings.SinComprobanteAdjunto, color = AqColor.TextSecondary)
            AqSecondaryButton(
                label = AppStrings.attachReceiptNow,
                icon = AqIcons.Attachment,
                onClick = openUnassociated,
            )
        } else {
            AqEvidenceBadge(
                text = AppStrings.receiptReceivedTitle,
                icon = AqIcons.Attachment,
                tone = AqTone.Success,
            )
            AqListBody {
                for (receipt in comprobantes) {
                    AqListRow(
                        title = AppStrings.Comprobante,
                        subtitle = formatDate(receipt.createdAt),
                        onClick = { onOpenComprobante(receipt.id) },
                        leading = {
                            AqIconBadge(icon = AqIcons.Attachment, tone = AqTone.Neutral)
                        },
                        meta = {
                            AqTonalTag(
                                text = receiptProvenanceLabel(receipt.provenance),
                                tone = provenanceTone(receipt.provenance),
                            )
                        },
                        trailing = {
                            AqTextAction(
                                label = AppStrings.Desasociar,
                                onClick = { onDisassociate(receipt.id) },
                                variant = AqTextActionVariant.Danger,
                            )
                        },
                    )
                }
            }
        }
    }
}

/** Botón destructivo de eliminación, separado al final. */
@Composable
private fun OperationDetailDeleteButton(
    onDelete: () -> Unit,
) {
    AqDangerButton(
        label = AppStrings.EliminarOperacion,
        onClick = onDelete,
        size = AqButtonSize.Medium,
    )
}

