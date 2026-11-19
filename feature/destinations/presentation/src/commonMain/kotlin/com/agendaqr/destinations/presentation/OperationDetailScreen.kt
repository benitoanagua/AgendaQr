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
            )
        },
        snackbarHost = appHosts?.feedback?.let { host -> { AqSnackbarHost(host) } },
        statusContent = appHosts?.status,
    ) {
        // Hero de la actividad (patrón de detalle de factura, ADR-0021): píldora de
        // TIPO con texto (pago = danger, cobro = success) + contexto,
        // cifra grande NEUTRA con signo (la referencia no colorea el
        // importe; el tipo lo comunica la píldora) y caja temporal.
        val amountText = operationAmountText(operation)
        val contextName = state.contexts.firstOrNull { it.id == operation.contextId }?.name
        // superficie y padding de sección los aporta la tarjeta (organismo);
        // la página solo compone contenido.
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
                // Caja temporal compacta (referencia): ocurrido vs
                // registrado — datos reales del dominio.
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
        // Metadatos en filas compactas (patrón de detalle de factura, ADR-0021): etiqueta a la
        // izquierda, valor a la derecha; la nota larga va como texto.
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
        // estado de sincronización del elemento (texto, no solo color).
        val operationSyncStatus = syncLookup.status(SyncResource.OPERATION, operation.id)
        ElementSyncBadge(operationSyncStatus)
        if (operationSyncStatus == ElementSyncStatus.ErrorRecoverable) {
            // El dato está a salvo localmente: el usuario puede reintentar.
            AqTextAction(label = AppStrings.Reintentar, onClick = onRetrySync)
        }
        if (operationSyncStatus == ElementSyncStatus.Dead) {
            // U4: el drain omite los errores permanentes; reintentar no
            // puede recuperarlos. El dato está a salvo localmente.
            AqText(
                text = AppStrings.TuInformacionEstaGuardadaEnElDispositivo,
                color = AqColor.TextSecondary,
            )
        }

        AqSection(AppStrings.SeccionComprobantes) {
            if (state.operationComprobantes.isEmpty()) {
                AqText(AppStrings.SinComprobanteAdjunto, color = AqColor.TextSecondary)
                // Secundaria/tonal con icono, dentro de su sección: nunca
                // un enlace del mismo peso que la acción destructiva.
                AqSecondaryButton(
                    label = AppStrings.attachReceiptNow,
                    icon = AqIcons.Attachment,
                    onClick = { viewModel.openUnassociatedForOperation(operation.id) },
                )
            } else {
                // X-01: insignia de evidencia reutilizable (icono + texto).
                // El texto afirma solo la recepción del comprobante; nunca
                // dinero verificado ni pago.
                AqEvidenceBadge(
                    text = AppStrings.receiptReceivedTitle,
                    icon = AqIcons.Attachment,
                    tone = AqTone.Success,
                )
                AqListBody {
                    for (receipt in state.operationComprobantes) {
                        // §12 V1.4: comprobante como fila con identidad
                        // (sujeta-papeles) y procedencia como etiqueta
                        // tonal; "Desasociar" como acción de fila.
                        AqListRow(
                            title = AppStrings.Comprobante,
                            subtitle = formatDate(receipt.createdAt),
                            onClick = { viewModel.onAction(OperationAction.OpenComprobante(receipt.id)) },
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
                                    onClick = { viewModel.onAction(OperationAction.Disassociate(receipt.id)) },
                                    variant = AqTextActionVariant.Danger,
                                )
                            },
                        )
                    }
                }
            }
        }
        // Destructiva al final, separada del contenido y con el diálogo
        // estándar: una sola vía de eliminación (sin duplicar en el menú).
        AqDangerButton(
            label = AppStrings.EliminarOperacion,
            onClick = { showDeleteConfirm = true },
            size = AqButtonSize.Medium,
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

