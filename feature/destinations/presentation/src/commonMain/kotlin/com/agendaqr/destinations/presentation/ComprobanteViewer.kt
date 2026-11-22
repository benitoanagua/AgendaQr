package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.AqAlert
import com.agendaqr.core.ui.components.AqButton
import com.agendaqr.core.ui.components.AqButtonVariant
import com.agendaqr.core.ui.components.AqCircularLoader
import com.agendaqr.core.ui.components.AqEmptyPlaceholder
import com.agendaqr.core.ui.components.AqIcon
import com.agendaqr.core.ui.components.AqIcons
import com.agendaqr.core.ui.components.AqMenuItem
import com.agendaqr.core.ui.components.AqModal
import com.agendaqr.core.ui.components.AqScreen
import com.agendaqr.core.ui.components.AqText
import com.agendaqr.core.ui.components.AqToastHost
import com.agendaqr.core.ui.components.AqTone
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.destinations.data.SyncResource
import com.agendaqr.destinations.domain.*

@Composable
internal fun ComprobanteViewerDialog(
    state: OperationsUiState,
    receipt: Comprobante,
    viewModel: OperationsViewModel,
    appHosts: AppHosts? = null,
    syncLookup: ElementSyncLookup = ElementSyncLookup.Empty,
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var deleteDoneEvent by remember { mutableStateOf<Any?>(null) }
    val toastHost = appHosts?.toastHost
    androidx.compose.runtime.LaunchedEffect(deleteDoneEvent) {
        if (deleteDoneEvent != null) {
            toastHost?.show(AppStrings.Eliminado)
        }
    }
    if (showDeleteConfirm) {
        AqModal(
            title = AppStrings.EliminarComprobante,
            dismissLabel = AppStrings.Cancelar,
            confirmLabel = AppStrings.Eliminar,
            destructive = true,
            onConfirm = {
                showDeleteConfirm = false
                deleteDoneEvent = Any()
                viewModel.onAction(OperationAction.DeleteComprobante(receipt.id))
            },
            onDismissRequest = { showDeleteConfirm = false },
        )
        return
    }
    val bytes = state.openedComprobanteBytes
    AqModal(
        title = AppStrings.Comprobante,
        dismissLabel = AppStrings.Cerrar,
        confirmLabel = AppStrings.Cerrar,
        onConfirm = { viewModel.onAction(OperationAction.CloseComprobante) },
        onDismissRequest = { viewModel.onAction(OperationAction.CloseComprobante) },
        content = {
            Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
                AqText(formatDate(receipt.createdAt), size = AqType.Label, color = AqColor.TextSecondary)
                AqText(AppStrings.Origen + receiptProvenanceLabel(receipt.provenance), size = AqType.Label, color = AqColor.TextSecondary)
                ElementSyncBadge(syncLookup.status(SyncResource.COMPROBANTE, receipt.id))
                when {
                    state.isLoadingComprobante -> {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
                        ) {
                            CqCircularLoaderPlaceholder()
                        }
                    }
                    bytes != null -> {
                        ComprobantePreview(bytes, receipt.mimeType)
                        AqButton(
                            label = AppStrings.Compartir,
                            onClick = { shareComprobante(bytes, receipt.extension.orEmpty("bin"), receipt.mimeType) },
                            variant = AqButtonVariant.Text,
                        )
                    }
                }
            }
        },
    )
}

@Composable
private fun CqCircularLoaderPlaceholder() {
    com.agendaqr.core.ui.components.AqCircularLoader(
        loaderSize = com.agendaqr.core.ui.theme.AqSize.Md,
        contentDescription = null,
    )
}
