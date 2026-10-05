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
import com.agendaqr.core.ui.components.XauxaCommandBar
import com.agendaqr.core.ui.components.XauxaHeading
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
    Column(modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().imePadding()) {
        Column(
            modifier = Modifier.weight(1f).padding(XauxaSpacing.Xxl).verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Lg),
        ) {
            XauxaHeading(text = destination.name.ifBlank { "Sin nombre" }, size = XauxaType.Headline, fontWeight = FontWeight.Bold)
            destination.category?.let { XauxaText(it, color = XauxaColor.TextSecondary) }
            destination.note?.takeIf { it.isNotBlank() }?.let { XauxaText(it, color = XauxaColor.TextSecondary) }
            // T6: estado de sincronización del elemento (texto, no solo
            // color) con REINTENTAR cuando es un error recuperable.
            ElementSyncBadge(syncStatus)
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
            Row(horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
                XauxaPrimaryButton(AppStrings.MostrarQr, onShowQr, enabled = destination.qr.encoded.isNotBlank())
                XauxaSecondaryButton(AppStrings.Volver, onBack)
            }
        }
        // cb1: command bar contextual solo en pantallas de detalle/foco como
        // esta (nunca en la lista/Home) — Xauxa checklist KMP §07. Delete va
        // a overflow a propósito, no a las hasta 3 acciones visibles (cb2):
        // es destructiva y no debería quedar a un toque de Edit/Share.
        XauxaCommandBar(
            visibleActions = listOf(
                { XauxaTextAction(AppStrings.Editar, onEdit) },
                { XauxaTextAction(AppStrings.Compartir, onShare) },
            ),
            overflowActions = listOf(
                XauxaOverflowAction("Eliminar", { showDeleteConfirm = true }),
            ),
        )
    }
    if (showDeleteConfirm) {
        XauxaDialog(
            title = AppStrings.EliminarDestino,
            message = AppStrings.irreversibleActionWarning,
            confirmLabel = AppStrings.Eliminar,
            onConfirm = {
                showDeleteConfirm = false
                onDelete()
            },
            dismissLabel = AppStrings.Cancelar,
            onDismiss = { showDeleteConfirm = false },
        )
    }
}
