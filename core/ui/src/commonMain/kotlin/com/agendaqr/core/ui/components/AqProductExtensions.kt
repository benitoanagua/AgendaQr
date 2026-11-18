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
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqType

/**
 * X-01: insignia de evidencia = icono + texto. El texto NO afirma dinero
 * verificado ni pago; el dominio decide el copy (p. ej. "Comprobante recibido").
 */
@Composable
fun AqEvidenceBadge(
    text: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    tone: AqTone = AqTone.Success,
) {
    AqInlineStack(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
    ) {
        AqIconBadge(icon = icon, tone = tone)
        AqTonalTag(text = text, tone = tone)
    }
}

/** X-02: estados de sincronización del dominio, modelados como sealed interface. */
sealed interface AqSyncState {
    data object Synced : AqSyncState
    data object Pending : AqSyncState
    data object Syncing : AqSyncState
    /** Error sin acción local: el reintento global lo gestiona la capa feature. */
    data object Error : AqSyncState
    data class ErrorRecoverable(val retryLabel: String, val onRetry: () -> Unit) : AqSyncState
    data object Dead : AqSyncState
}

/** Copy de cada estado: lo aporta la capa feature desde sus AppStrings. */
data class AqSyncLabels(
    val synced: String,
    val pending: String,
    val syncing: String,
    val error: String,
    val dead: String,
)

private fun AqSyncState.label(labels: AqSyncLabels): String = when (this) {
    AqSyncState.Synced -> labels.synced
    AqSyncState.Pending -> labels.pending
    AqSyncState.Syncing -> labels.syncing
    AqSyncState.Error -> labels.error
    is AqSyncState.ErrorRecoverable -> labels.error
    AqSyncState.Dead -> labels.dead
}

private fun AqSyncState.tone(): AqTone = when (this) {
    AqSyncState.Synced -> AqTone.Success
    AqSyncState.Pending -> AqTone.Neutral
    AqSyncState.Syncing -> AqTone.Info
    AqSyncState.Error -> AqTone.Danger
    is AqSyncState.ErrorRecoverable -> AqTone.Danger
    AqSyncState.Dead -> AqTone.Neutral
}

/** X-02: indicador de sincronización reutilizable sobre [AqBadge]. */
@Composable
fun AqSyncIndicator(
    state: AqSyncState,
    labels: AqSyncLabels,
    modifier: Modifier = Modifier,
    showWhenSynced: Boolean = false,
) {
    if (state is AqSyncState.Synced && !showWhenSynced) return
    Column(modifier = modifier, horizontalAlignment = Alignment.End) {
        AqBadge(
            text = state.label(labels),
            tone = state.tone(),
            round = true,
        )
        if (state is AqSyncState.ErrorRecoverable) {
            AqTextAction(
                label = state.retryLabel,
                onClick = state.onRetry,
                variant = AqTextActionVariant.Danger,
            )
        }
    }
}

/**
 * X-03: hoja de QR de pantalla completa. El lienzo del QR SIEMPRE usa
 * [AqColor.QrCanvas] (blanco en claro y oscuro) y el efecto de brillo
 * se delega a la plataforma mediante [onBrightnessNeeded]. El acceso se
 * resuelve en la capa feature (≤2 toques); este componente no decide copy.
 */
@Composable
fun AqQrSheet(
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
    AqBottomSheet(
        title = title,
        dismissDescription = closeLabel,
        onDismissRequest = onClose,
        modifier = modifier,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(AqColor.QrCanvas)
                .padding(AqSpacing.Md)
                .semantics {
                    contentDescription = title
                    liveRegion = LiveRegionMode.Polite
                },
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AqQrPreview(
                encodedQr = encodedQr,
                unavailableMessage = unavailableMessage,
            )
            AqText(
                text = title,
                size = AqType.Caption,
                color = com.agendaqr.core.ui.theme.AqColor.TextPrimary,
            )
        }
    }
}
