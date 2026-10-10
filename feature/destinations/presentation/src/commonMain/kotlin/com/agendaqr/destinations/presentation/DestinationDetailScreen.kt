package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import com.agendaqr.destinations.presentation.AppStrings
import com.agendaqr.core.ui.components.XauxaText
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import com.agendaqr.core.ui.components.XauxaAppBar
import com.agendaqr.core.ui.components.XauxaAppBarAction
import com.agendaqr.core.ui.components.XauxaIcons
import com.agendaqr.core.ui.components.XauxaHeading
import com.agendaqr.core.ui.components.XauxaFeedbackEvent
import com.agendaqr.core.ui.components.XauxaPageTitle
import com.agendaqr.core.ui.components.XauxaDialog
import com.agendaqr.core.ui.components.XauxaEmptyState
import com.agendaqr.core.ui.components.XauxaOverflowAction
import com.agendaqr.core.ui.components.XauxaPrimaryButton
import com.agendaqr.core.ui.components.XauxaQrPreview
import com.agendaqr.core.ui.components.XauxaSecondaryButton
import com.agendaqr.core.ui.components.XauxaTextAction
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.core.ui.theme.XauxaSpacing
import com.agendaqr.core.ui.theme.XauxaType
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
    // Ronda 2 (Área F): anuncio de eliminación confirmada (por evento).
    var deleteDoneEvent by remember { mutableStateOf<Any?>(null) }
    XauxaFeedbackEvent(event = deleteDoneEvent, message = AppStrings.Eliminado)
    Column(modifier = Modifier.fillMaxSize().statusBarsPadding().imePadding()) {
        Column(
            modifier = Modifier.weight(1f).padding(XauxaSpacing.ScreenMargin).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Lg),
        ) {
            XauxaPageTitle(text = destination.name.ifBlank { "Sin nombre" })
            destination.category?.let { XauxaText(it, color = XauxaColor.TextSecondary) }
            destination.note?.takeIf { it.isNotBlank() }?.let { XauxaText(it, color = XauxaColor.TextSecondary) }
            // T6: estado de sincronización del elemento (texto, no solo
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
        // M7 (V1.1): acciones de detalle en la barra de aplicación
        // inferior — principal "Mostrar QR" en bloque de acento; Editar y
        // Compartir como acciones etiquetadas; Eliminar en el menú "…"
        // (destructiva, no a un toque), mismo criterio que la command bar
        // V1 (cb1/cb2) ahora expresado en el chrome Metro.
        XauxaAppBar(
            modifier = Modifier.navigationBarsPadding(),
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
                XauxaOverflowAction("Eliminar", { showDeleteConfirm = true }),
            ),
            onBack = onBack,
            backLabel = AppStrings.Volver,
        )
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
