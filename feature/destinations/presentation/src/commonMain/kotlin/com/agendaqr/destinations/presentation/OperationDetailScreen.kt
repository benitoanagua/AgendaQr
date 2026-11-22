package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.AqAlert
import com.agendaqr.core.ui.components.AqAppBarAction
import com.agendaqr.core.ui.components.AqButton
import com.agendaqr.core.ui.components.AqButtonSize
import com.agendaqr.core.ui.components.AqButtonVariant
import com.agendaqr.core.ui.components.AqEmptyPlaceholder
import com.agendaqr.core.ui.components.AqIcon
import com.agendaqr.core.ui.components.AqIcons
import com.agendaqr.core.ui.components.AqMenuItem
import com.agendaqr.core.ui.components.AqModal
import com.agendaqr.core.ui.components.AqScreen
import com.agendaqr.core.ui.components.AqSectionTitle
import com.agendaqr.core.ui.components.AqTag
import com.agendaqr.core.ui.components.AqText
import com.agendaqr.core.ui.components.AqToastHost
import com.agendaqr.core.ui.components.AqTone
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqShadow
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqSquircles
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.core.ui.theme.AqWeight
import com.agendaqr.destinations.data.SyncResource
import com.agendaqr.destinations.domain.*

/**
 * Detalle de la operación: datos, comprobantes, acciones y confirmación
 * de borrado. Estado de sincronización por elemento.
 *
 * Construido exclusivamente con componentes Moon: [AqScreen], [AqModal],
 * [AqToastHost], [AqButton], [AqTag], [AqText], etc.
 * Sin símbolos legacy ni wrappers `Aq*`.
 */
@Composable
internal fun OperationDetailScreen(
    state: OperationsUiState,
    viewModel: OperationsViewModel,
    appHosts: AppHosts? = null,
    syncLookup: ElementSyncLookup = ElementSyncLookup.Empty,
    onRetrySync: () -> Unit = {},
    destinationsViewModel: DestinationsViewModel? = null,
) {
    val operation = viewModel.selectedOperation()
    if (operation == null) {
        AqScreen(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AqEmptyPlaceholder(
                title = AppStrings.OperacionNoEncontrada,
                subtitle = AppStrings.PudoHaberSidoEliminada,
                actionLabel = AppStrings.Volver,
                onAction = { viewModel.onAction(OperationAction.Back) },
            )
        }
        return
    }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var deleteDoneEvent by remember { mutableStateOf<Any?>(null) }
    val toastHost = appHosts?.toastHost
    LaunchedEffect(deleteDoneEvent) {
        if (deleteDoneEvent != null) {
            toastHost?.show(AppStrings.Eliminado)
        }
    }

    AqScreen(
        title = AppStrings.Detalle,
        onBack = { viewModel.onAction(OperationAction.Back) },
        backLabel = AppStrings.Volver,
        overflowActions = listOf(
            AqAppBarAction(AppStrings.Compartir, AqIcons.Share, { shareOperation(operation) }),
            AqAppBarAction(AppStrings.Editar, AqIcons.Edit, { viewModel.onAction(OperationAction.Edit(operation.id)) }),
        ),
        overflowLabel = AppStrings.Mas,
        statusContent = appHosts?.status,
        toastHost = toastHost?.let { host -> { AqToastHost(host) } },
    ) {
        val amountText = operationAmountText(operation)
        val contextName = state.contexts.firstOrNull { it.id == operation.contextId }?.name
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(AqColor.Surface, AqSquircles.SurfaceLg)
                .aqShadow(AqShadow.Sm, AqSquircles.SurfaceLg)
                .padding(AqSpacing.Lg),
            verticalArrangement = Arrangement.spacedBy(AqSpacing.Md),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AqTag(
                    text = operationTypeLabel(operation.type),
                    tone = operationTypeTone(operation.type),
                )
                contextName?.let {
                    AqTag(text = it)
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
                    .background(AqColor.SurfaceContainerLow, AqSquircles.SurfaceLg)
                    .padding(AqSpacing.Md),
                verticalArrangement = Arrangement.spacedBy(AqSpacing.Xs),
            ) {
                AqMetaLine(label = AppStrings.FechaDeOperacion, value = formatDate(operation.occurredAt), leadingIcon = AqIcons.Calendar)
                AqMetaLine(label = AppStrings.RegistradoEnDispositivo, value = formatDate(operation.createdAt), leadingIcon = AqIcons.Clock)
            }
        }

        val hasMetadata = operation.personOrEntity != null ||
            operation.currency != null ||
            operation.concept != null ||
            operation.note != null
        if (hasMetadata) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AqColor.Surface, AqSquircles.SurfaceLg)
                    .aqShadow(AqShadow.Sm, AqSquircles.SurfaceLg)
                    .padding(AqSpacing.Lg),
                verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
            ) {
                operation.personOrEntity?.let { AqMetaLine(label = AppStrings.LabelPersonaOEntidad, value = it) }
                operation.currency?.let { AqMetaLine(label = AppStrings.LabelMoneda, value = it) }
                operation.concept?.let { AqMetaLine(label = AppStrings.LabelConcepto, value = it) }
                operation.note?.let {
                    AqText(it, color = AqColor.TextSecondary)
                }
            }
        }

        val operationSyncStatus = syncLookup.status(SyncResource.OPERATION, operation.id)
        ElementSyncBadge(operationSyncStatus)
        if (operationSyncStatus == ElementSyncStatus.ErrorRecoverable) {
            AqButton(
                label = AppStrings.Reintentar,
                onClick = onRetrySync,
                variant = AqButtonVariant.Text,
            )
        }
        if (operationSyncStatus == ElementSyncStatus.Dead) {
            AqText(
                text = AppStrings.TuInformacionEstaGuardadaEnElDispositivo,
                color = AqColor.TextSecondary,
            )
        }

        operation.destinationId?.let { destinationId ->
            val destinationName = destinationsViewModel?.destination(destinationId)?.name
            AqMetaLine(
                label = AppStrings.DestinoVinculado,
                value = destinationName ?: destinationId,
            )
        }

        AqSectionTitle(text = AppStrings.SeccionComprobantes)
        if (state.operationComprobantes.isEmpty()) {
            AqText(AppStrings.SinComprobanteAdjunto, color = AqColor.TextSecondary)
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
                com.agendaqr.core.ui.components.AqTag(
                    text = AppStrings.receiptReceivedTitle,
                    tone = AqTone.Success,
                )
                for (receipt in state.operationComprobantes) {
                    AqMenuItem(
                        title = AppStrings.Comprobante,
                        subtitle = formatDate(receipt.createdAt),
                        onClick = { viewModel.onAction(OperationAction.OpenComprobante(receipt.id)) },
                        leading = {
                            AqIcon(
                                imageVector = AqIcons.Attachment,
                                contentDescription = null,
                            )
                        },
                        trailing = {
                            AqTag(
                                text = receiptProvenanceLabel(receipt.provenance),
                                tone = provenanceTone(receipt.provenance),
                            )
                        },
                        trailing2 = {
                            AqButton(
                                label = AppStrings.Desasociar,
                                onClick = { viewModel.onAction(OperationAction.Disassociate(receipt.id)) },
                                variant = AqButtonVariant.Danger,
                                size = AqButtonSize.Small,
                            )
                        },
                    )
                }
            }
        }
        AqButton(
            label = AppStrings.EliminarOperacion,
            onClick = { showDeleteConfirm = true },
            variant = AqButtonVariant.Danger,
            size = AqButtonSize.Medium,
        )
    }

    if (showDeleteConfirm) {
        AqModal(
            title = AppStrings.EliminarOperacion,
            destructive = true,
            dismissLabel = AppStrings.Cancelar,
            confirmLabel = AppStrings.Eliminar,
            onConfirm = {
                showDeleteConfirm = false
                deleteDoneEvent = Any()
                viewModel.onAction(OperationAction.Delete(operation.id))
            },
            onDismissRequest = { showDeleteConfirm = false },
        )
    }
}

/** Fila de metadato (etiqueta + valor) construida con piezas Moon. */
@Composable
private fun AqMetaLine(
    label: String,
    value: String,
    leadingIcon: androidx.compose.ui.graphics.vector.ImageVector? = null,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
    ) {
        leadingIcon?.let {
            AqIcon(imageVector = it, contentDescription = null, size = com.agendaqr.core.ui.theme.AqMetrics.IconSizeCompact)
        }
        AqText(
            text = label,
            size = AqType.Body,
            color = com.agendaqr.core.ui.theme.AqColor.TextSecondary,
            modifier = Modifier.weight(0f, fill = false),
        )
        AqText(
            text = value,
            size = AqType.Body,
            color = com.agendaqr.core.ui.theme.AqColor.TextPrimary,
            modifier = Modifier.weight(1f, fill = false),
        )
    }
}
