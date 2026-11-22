package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqType

/**
 * X-01: insignia de evidencia = icono + texto. El texto NO afirma dinero
 * verificado ni pago; el dominio decide el copy (p. ej. "Comprobante recibido").
 */
@Composable
fun AqEvidenceTag(
    text: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    tone: AqTone = AqTone.Success,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AqIcon(
            imageVector = icon,
            contentDescription = null,
            tint = tone.content(),
        )
        AqTag(
            text = text,
            tone = tone,
            size = AqTagSize.Xs,
        )
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

/** X-02: indicador de sincronización reutilizable sobre [AqTag]. */
@Composable
fun AqSyncIndicator(
    state: AqSyncState,
    labels: AqSyncLabels,
    modifier: Modifier = Modifier,
    showWhenSynced: Boolean = false,
) {
    if (state is AqSyncState.Synced && !showWhenSynced) return
    Column(modifier = modifier, horizontalAlignment = Alignment.End) {
        AqTag(
            text = state.label(labels),
            tone = state.tone(),
            size = com.agendaqr.core.ui.components.AqTagSize.Xs,
        )
        if (state is AqSyncState.ErrorRecoverable) {
            AqButton(
                label = state.retryLabel,
                onClick = state.onRetry,
                variant = com.agendaqr.core.ui.components.AqButtonVariant.Text,
                size = com.agendaqr.core.ui.components.AqButtonSize.Small,
            )
        }
    }
}


