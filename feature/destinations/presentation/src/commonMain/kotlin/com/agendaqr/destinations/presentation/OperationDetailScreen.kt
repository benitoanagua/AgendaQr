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
import com.agendaqr.core.ui.components.AmSnackbarHost
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
) {
    val operation = viewModel.selectedOperation()
    if (operation == null) {
        AmScreenScaffold(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            AmEmptyState(
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
    AmFeedbackEvent(
        event = deleteDoneEvent,
        message = AppStrings.Eliminado,
        feedbackHost = appHosts?.feedback,
    )
    // V1.4 (§12): detalle con título y flecha atrás en la barra superior;
    // Compartir y Editar como acciones visibles (Editar es la principal),
    // Eliminar en el menú "…" (destructiva, no a un toque).
    AmScreenScaffold(
        scrollable = true,
        topBar = {
            AmTopBar(
                title = AppStrings.Detalle,
                onBack = { viewModel.onAction(OperationAction.Back) },
                backLabel = AppStrings.Volver,
                actions = listOf(
                    AmTopBarAction(label = AppStrings.Compartir, icon = AmIcons.Share, onClick = { shareOperation(operation) }),
                    AmTopBarAction(
                        label = AppStrings.Editar,
                        icon = AmIcons.Edit,
                        onClick = { viewModel.onAction(OperationAction.Edit(operation.id)) },
                    ),
                ),
                overflowActions = listOf(
                    AmOverflowAction(AppStrings.EliminarOperacion, { showDeleteConfirm = true }),
                ),
                overflowLabel = AppStrings.Mas,
            )
        },
        snackbarHost = appHosts?.feedback?.let { host -> { AmSnackbarHost(host) } },
        statusContent = appHosts?.status,
    ) {
        // Hero de la actividad (patrón de detalle de factura, ADR-0021): píldora de
        // TIPO con texto (pago = danger, cobro = success) + contexto,
        // cifra grande NEUTRA con signo (la referencia no colorea el
        // importe; el tipo lo comunica la píldora) y caja temporal.
        val amountText = operationAmountText(operation)
        val contextName = state.contexts.firstOrNull { it.id == operation.contextId }?.name
        AmListBody {
            Column(
                modifier = Modifier.fillMaxWidth().padding(AmSpacing.Lg),
                verticalArrangement = Arrangement.spacedBy(AmSpacing.Md),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    AmTonalTag(
                        text = operationTypeLabel(operation.type),
                        tone = operationTypeTone(operation.type),
                        uppercase = true,
                    )
                    contextName?.let {
                        AmTonalTag(text = it, tone = AmTone.Neutral)
                    }
                }
                Column(verticalArrangement = Arrangement.spacedBy(AmSpacing.Xs)) {
                    amountText?.let {
                        AmText(
                            it,
                            size = AmType.Amount,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AmSpacing.Xs),
                    ) {
                        AmIcon(
                            imageVector = AmIcons.Store,
                            contentDescription = null,
                            tint = AmColor.TextSecondary,
                        )
                        AmText(
                            operationTitle(operation),
                            size = AmType.SectionHeader,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                        )
                    }
                }
                // Caja temporal compacta (referencia): ocurrido vs
                // registrado — datos reales del dominio.
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AmColor.SurfaceContainerLow, AmShapes.Large)
                        .padding(AmSpacing.Md),
                    verticalArrangement = Arrangement.spacedBy(AmSpacing.Xs),
                ) {
                    AmMetaRow(
                        label = AppStrings.FechaDeOperacion,
                        value = formatDate(operation.occurredAt),
                        icon = AmIcons.Calendar,
                    )
                    AmMetaRow(
                        label = AppStrings.RegistradoEnDispositivo,
                        value = formatDate(operation.createdAt),
                        icon = AmIcons.Clock,
                    )
                }
            }
        }
        // Metadatos en filas compactas (patrón de detalle de factura, ADR-0021): etiqueta a la
        // izquierda, valor a la derecha; la nota larga va como texto.
        val hasMetadata = operation.personOrEntity != null ||
            operation.currency != null ||
            operation.concept != null ||
            operation.note != null
        if (hasMetadata) {
            AmListBody {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = AmSpacing.Lg, vertical = AmSpacing.Md),
                    verticalArrangement = Arrangement.spacedBy(AmSpacing.Sm),
                ) {
                    operation.personOrEntity?.let { AmMetaRow(label = AppStrings.LabelPersonaOEntidad, value = it) }
                    operation.currency?.let { AmMetaRow(label = AppStrings.LabelMoneda, value = it) }
                    operation.concept?.let { AmMetaRow(label = AppStrings.LabelConcepto, value = it) }
                    operation.note?.let {
                        AmText(it, color = AmColor.TextSecondary)
                    }
                }
            }
        }
        // estado de sincronización del elemento (texto, no solo color).
        val operationSyncStatus = syncLookup.status(SyncResource.OPERATION, operation.id)
        ElementSyncBadge(operationSyncStatus)
        if (operationSyncStatus == ElementSyncStatus.ErrorRecoverable) {
            // El dato está a salvo localmente: el usuario puede reintentar.
            AmTextAction(label = AppStrings.Reintentar, onClick = onRetrySync)
        }
        if (operationSyncStatus == ElementSyncStatus.Dead) {
            // U4: el drain omite los errores permanentes; reintentar no
            // puede recuperarlos. El dato está a salvo localmente.
            AmText(
                text = AppStrings.TuInformacionEstaGuardadaEnElDispositivo,
                color = AmColor.TextSecondary,
            )
        }

        AmSection(AppStrings.SeccionComprobantes) {
            if (state.operationComprobantes.isEmpty()) {
                AmText(AppStrings.SinComprobanteAdjunto, color = AmColor.TextSecondary)
                AmTextAction(label = AppStrings.attachReceiptNow, onClick = { viewModel.openUnassociatedForOperation(operation.id) })
            } else {
                AmListBody {
                    for (receipt in state.operationComprobantes) {
                        // §12 V1.4: comprobante como fila con identidad
                        // (sujeta-papeles) y procedencia como etiqueta
                        // tonal; "Desasociar" como acción de fila.
                        AmListRow(
                            title = AppStrings.Comprobante,
                            subtitle = formatDate(receipt.createdAt),
                            onClick = { viewModel.onAction(OperationAction.OpenComprobante(receipt.id)) },
                            leading = {
                                AmIconBadge(icon = AmIcons.Attachment, tone = AmTone.Neutral)
                            },
                            meta = {
                                AmTonalTag(
                                    text = receiptProvenanceLabel(receipt.provenance),
                                    tone = provenanceTone(receipt.provenance),
                                )
                            },
                            trailing = {
                                AmTextAction(
                                    label = AppStrings.Desasociar,
                                    onClick = { viewModel.onAction(OperationAction.Disassociate(receipt.id)) },
                                    variant = AmTextActionVariant.Danger,
                                )
                            },
                        )
                    }
                }
            }
        }
        AmTextAction(label = AppStrings.EliminarOperacion, onClick = { showDeleteConfirm = true })
    }
    if (showDeleteConfirm) {
        AmDialog(
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

