package com.agendaqr.destinations.presentation

import com.agendaqr.destinations.presentation.AppStrings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.XauxaAppBar
import com.agendaqr.core.ui.components.XauxaAppBarAction
import com.agendaqr.core.ui.components.XauxaFeedbackEvent
import com.agendaqr.core.ui.components.XauxaPageTitle
import com.agendaqr.core.ui.components.XauxaScreenScaffold
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
    XauxaScreenScaffold(
        scrollable = true,
        bottomBar = {
            // M7 (V1.1): acciones de detalle en la barra de aplicación
            // inferior — principal "Mostrar QR" en bloque de acento; Editar y
            // Compartir como acciones etiquetadas; Eliminar en el menú "…"
            // (destructiva, no a un toque), mismo criterio que la command bar
            // V1 (cb1/cb2) ahora expresado en el chrome Metro.
            XauxaAppBar(
                actions = listOf(
                    // Sin glifo asignado aún en el mapeo mínimo (spec §12): la
                    // acción va etiquetada en texto (la etiqueta siempre se ve,
                    // §11) hasta una decisión de iconografía nueva.
                    XauxaAppBarAction(
                        label = AppStrings.MostrarQr,
                        icon = null,
                        primary = true,
                        enabled = destination.qr.encoded.isNotBlank(),
                        onClick = onShowQr,
                    ),
                    XauxaAppBarAction(AppStrings.Editar, icon = null, onClick = onEdit),
                    XauxaAppBarAction(AppStrings.Compartir, icon = null, onClick = onShare),
                ),
                overflowActions = listOf(
                    XauxaOverflowAction(AppStrings.Eliminar, { showDeleteConfirm = true }),
                ),
                overflowLabel = AppStrings.Mas,
                onBack = onBack,
                backLabel = AppStrings.Volver,
            )
        },
    ) {
            XauxaPageTitle(text = destination.name.ifBlank { AppStrings.SinNombre })
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
