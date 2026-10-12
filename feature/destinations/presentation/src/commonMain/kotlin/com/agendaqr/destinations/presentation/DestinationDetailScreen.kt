package com.agendaqr.destinations.presentation

import com.agendaqr.destinations.presentation.AppStrings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import com.agendaqr.core.ui.components.XauxaFeedbackEvent
import com.agendaqr.core.ui.components.XauxaScreenScaffold
import com.agendaqr.core.ui.components.XauxaTopBar
import com.agendaqr.core.ui.components.XauxaTopBarAction
import com.agendaqr.core.ui.components.XauxaDialog
import com.agendaqr.core.ui.components.XauxaEmptyState
import com.agendaqr.core.ui.components.XauxaOverflowAction
import com.agendaqr.core.ui.components.XauxaQrPreview
import com.agendaqr.core.ui.components.XauxaText
import com.agendaqr.core.ui.components.XauxaTextAction
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.destinations.domain.Destination

@Composable
fun DestinationDetailScreen(
    destination: Destination,
    onShowQr: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onShare: () -> Unit,
    onBack: () -> Unit,
    syncStatus: ElementSyncStatus = ElementSyncStatus.Synced,
    onRetrySync: () -> Unit = {},
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }
    // anuncio de eliminación confirmada (por evento).
    var deleteDoneEvent by remember { mutableStateOf<Any?>(null) }
    XauxaFeedbackEvent(event = deleteDoneEvent, message = AppStrings.Eliminado)
    // V1.2 (§12): título del QR en la barra superior con flecha atrás;
    // Mostrar QR y Editar como acciones visibles, Compartir y Eliminar en
    // el menú "…" (destructiva, no a un toque).
    XauxaScreenScaffold(
        scrollable = true,
        topBar = {
            XauxaTopBar(
                title = destination.name.ifBlank { AppStrings.SinNombre },
                onBack = onBack,
                backLabel = AppStrings.Volver,
                actions = listOf(
                    XauxaTopBarAction(
                        label = AppStrings.MostrarQr,
                        onClick = onShowQr,
                        enabled = destination.qr.encoded.isNotBlank(),
                    ),
                    XauxaTopBarAction(label = AppStrings.Editar, onClick = onEdit),
                ),
                overflowActions = listOf(
                    XauxaOverflowAction(AppStrings.Compartir, onClick = onShare),
                    XauxaOverflowAction(AppStrings.Eliminar, { showDeleteConfirm = true }),
                ),
                overflowLabel = AppStrings.Mas,
            )
        },
    ) {
            destination.category?.let { XauxaText(it, color = XauxaColor.TextSecondary) }
            destination.note?.takeIf { it.isNotBlank() }?.let { XauxaText(it, color = XauxaColor.TextSecondary) }
            // estado de sincronización del elemento (texto, no solo
            // color) con REINTENTAR cuando es un error recuperable.
            ElementSyncBadge(syncStatus, showWhenSynced = true)
            if (syncStatus == ElementSyncStatus.ErrorRecoverable) {
                XauxaTextAction(label = AppStrings.Reintentar, onClick = onRetrySync)
            }
            if (syncStatus == ElementSyncStatus.Dead) {
                XauxaText(
                    text = AppStrings.TuInformacionEstaGuardadaEnElDispositivo,
                    color = XauxaColor.TextSecondary,
                )
            }
            if (destination.qr.encoded.isNotBlank()) {
                XauxaQrPreview(destination.qr.encoded)
            } else {
                XauxaEmptyState(
                    title = AppStrings.QrSinContenido,
                    subtitle = AppStrings.EditaElDestinoParaImportar,
                    actionLabel = AppStrings.Editar,
                    onAction = onEdit,
                )
            }
    }
    if (showDeleteConfirm) {
        XauxaDialog(
            title = AppStrings.EliminarDestino,
            message = AppStrings.irreversibleActionWarning,
            confirmLabel = AppStrings.Eliminar,
            destructive = true,
            onConfirm = {
                showDeleteConfirm = false
                deleteDoneEvent = Any()
                onDelete()
            },
            dismissLabel = AppStrings.Cancelar,
            onDismiss = { showDeleteConfirm = false },
        )
    }
}
