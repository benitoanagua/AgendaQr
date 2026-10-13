package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.AmSnackbarHost
import com.agendaqr.core.ui.components.AmFeedbackEvent
import com.agendaqr.core.ui.components.AmScreenScaffold
import com.agendaqr.core.ui.components.AmTopBar
import com.agendaqr.core.ui.components.AmTopBarAction
import com.agendaqr.core.ui.components.AmDialog
import com.agendaqr.core.ui.components.AmEmptyState
import com.agendaqr.core.ui.components.AmIcons
import com.agendaqr.core.ui.components.AmListBody
import com.agendaqr.core.ui.components.AmMetaRow
import com.agendaqr.core.ui.components.AmOverflowAction
import com.agendaqr.core.ui.components.AmQrPreview
import com.agendaqr.core.ui.components.AmText
import com.agendaqr.core.ui.components.AmTextAction
import com.agendaqr.core.ui.theme.AmColor
import com.agendaqr.core.ui.theme.AmMetrics
import com.agendaqr.core.ui.theme.AmSpacing
import com.agendaqr.core.ui.theme.accentFor
import com.agendaqr.destinations.domain.Destination

@Composable
fun DestinationDetailScreen(
    destination: Destination,
    appHosts: AppHosts? = null,
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
    AmFeedbackEvent(
        event = deleteDoneEvent,
        message = AppStrings.Eliminado,
        feedbackHost = appHosts?.feedback,
    )
    // V1.4 (§12): título del QR en la barra superior con flecha atrás;
    // Mostrar QR y Editar como acciones visibles, Compartir y Eliminar en
    // el menú "…" (destructiva, no a un toque).
    AmScreenScaffold(
        scrollable = true,
        topBar = {
            AmTopBar(
                title = destination.name.ifBlank { AppStrings.SinNombre },
                onBack = onBack,
                backLabel = AppStrings.Volver,
                actions = listOf(
                    AmTopBarAction(
                        label = AppStrings.MostrarQr,
                        onClick = onShowQr,
                        enabled = destination.qr.encoded.isNotBlank(),
                    ),
                    AmTopBarAction(label = AppStrings.Editar, onClick = onEdit),
                ),
                overflowActions = listOf(
                    AmOverflowAction(AppStrings.Compartir, onClick = onShare),
                    AmOverflowAction(AppStrings.Eliminar, { showDeleteConfirm = true }),
                ),
                overflowLabel = AppStrings.Mas,
            )
        },
        snackbarHost = appHosts?.feedback?.let { host -> { AmSnackbarHost(host) } },
        statusContent = appHosts?.status,
    ) {
            // Metadatos del destino como grupo (categoría + nota a 4 dp) en
        // tarjeta compacta — solo si hay contenido que agrupar.
        val hasMetadata = destination.category != null ||
            destination.note?.takeIf { it.isNotBlank() } != null
        if (hasMetadata) {
            AmListBody {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = AmSpacing.Lg, vertical = AmSpacing.Md),
                    verticalArrangement = Arrangement.spacedBy(AmSpacing.Sm),
                ) {
                    destination.category?.let { AmMetaRow(label = AppStrings.Categoria, value = it) }
                    destination.note?.takeIf { it.isNotBlank() }?.let {
                        AmText(it, color = AmColor.TextSecondary)
                    }
                }
            }
        }
            // estado de sincronización del elemento (texto, no solo
            // color) con REINTENTAR cuando es un error recuperable.
            ElementSyncBadge(syncStatus, showWhenSynced = true)
            if (syncStatus == ElementSyncStatus.ErrorRecoverable) {
                AmTextAction(label = AppStrings.Reintentar, onClick = onRetrySync)
            }
            if (syncStatus == ElementSyncStatus.Dead) {
                AmText(
                    text = AppStrings.TuInformacionEstaGuardadaEnElDispositivo,
                    color = AmColor.TextSecondary,
                )
            }
            if (destination.qr.encoded.isNotBlank()) {
                // Hero de exhibición (referencia Stitch): QR centrado a
                // 320 dp sobre marco tonal, con aire a los metadatos.
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    AmQrPreview(destination.qr.encoded, size = AmMetrics.QrReviewSize)
                }
            } else {
                AmEmptyState(
                    icon = AmIcons.ImageOff,
                    title = AppStrings.QrSinContenido,
                    subtitle = AppStrings.EditaElDestinoParaImportar,
                    actionLabel = AppStrings.Editar,
                    onAction = onEdit,
                )
            }
    }
    if (showDeleteConfirm) {
        AmDialog(
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
