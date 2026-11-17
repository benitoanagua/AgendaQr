package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import com.agendaqr.core.ui.theme.AmColor
import com.agendaqr.core.ui.theme.AmSpacing
import com.agendaqr.core.ui.theme.AmType

/**
 * X-01: insignia de evidencia = icono + texto. El texto NO afirma dinero
 * verificado ni pago; el dominio decide el copy (p. ej. "Comprobante recibido").
 */
@Composable
fun AmEvidenceBadge(
    text: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    tone: AmTone = AmTone.Success,
) {
    AmInlineStack(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(AmSpacing.Sm),
    ) {
        AmIconBadge(icon = icon, tone = tone)
        AmTonalTag(text = text, tone = tone)
    }
}

/** X-02: estados de sincronización del dominio, modelados como sealed interface. */
sealed interface AmSyncState {
    data object Synced : AmSyncState
    data object Pending : AmSyncState
    data object Syncing : AmSyncState
    data class ErrorRecoverable(val retryLabel: String, val onRetry: () -> Unit) : AmSyncState
    data object Dead : AmSyncState
}

/** Copy de cada estado: lo aporta la capa feature desde sus AppStrings. */
data class AmSyncLabels(
    val synced: String,
    val pending: String,
    val syncing: String,
    val error: String,
    val dead: String,
)

private fun AmSyncState.label(labels: AmSyncLabels): String = when (this) {
    AmSyncState.Synced -> labels.synced
    AmSyncState.Pending -> labels.pending
    AmSyncState.Syncing -> labels.syncing
    is AmSyncState.ErrorRecoverable -> labels.error
    AmSyncState.Dead -> labels.dead
}

private fun AmSyncState.tone(): AmTone = when (this) {
    AmSyncState.Synced -> AmTone.Success
    AmSyncState.Pending -> AmTone.Warning
    AmSyncState.Syncing -> AmTone.Info
    is AmSyncState.ErrorRecoverable -> AmTone.Danger
    AmSyncState.Dead -> AmTone.Neutral
}

/** X-02: indicador de sincronización reutilizable sobre [AmBadge]. */
@Composable
fun AmSyncIndicator(
    state: AmSyncState,
    labels: AmSyncLabels,
    modifier: Modifier = Modifier,
    showWhenSynced: Boolean = false,
) {
    if (state is AmSyncState.Synced && !showWhenSynced) return
    Column(modifier = modifier, horizontalAlignment = Alignment.End) {
        AmBadge(
            text = state.label(labels),
            tone = state.tone(),
            round = true,
        )
        if (state is AmSyncState.ErrorRecoverable) {
            AmTextAction(
                label = state.retryLabel,
                onClick = state.onRetry,
                variant = AmTextActionVariant.Danger,
            )
        }
    }
}

/**
 * X-03: hoja de QR de pantalla completa. El lienzo del QR SIEMPRE usa
 * [AmColor.QrCanvas] (blanco en claro y oscuro) y el efecto de brillo
 * se delega a la plataforma mediante [onBrightnessNeeded]. El acceso se
 * resuelve en la capa feature (≤2 toques); este componente no decide copy.
 */
@Composable
fun AmQrSheet(
    encodedQr: String,
    title: String,
    closeLabel: String,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
    unavailableMessage: String? = null,
    onBrightnessNeeded: (() -> Unit)? = null,
) {
    androidx.compose.runtime.LaunchedEffect(Unit) {
        onBrightnessNeeded?.invoke()
    }
    AmBottomSheet(
        title = title,
        dismissDescription = closeLabel,
        onDismissRequest = onClose,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(AmColor.QrCanvas)
                .padding(AmSpacing.Md)
                .semantics {
                    contentDescription = title
                    liveRegion = LiveRegionMode.Polite
                },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AmQrPreview(
                encodedQr = encodedQr,
                unavailableMessage = unavailableMessage,
            )
            AmText(
                text = title,
                size = AmType.Caption,
                color = com.agendaqr.core.ui.theme.AmColor.TextPrimary,
            )
        }
    }
}
