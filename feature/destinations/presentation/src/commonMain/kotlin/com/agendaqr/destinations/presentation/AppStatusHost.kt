package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import com.agendaqr.core.ui.components.AqAlert
import com.agendaqr.core.ui.components.AqTag
import com.agendaqr.core.ui.components.AqText
import com.agendaqr.core.ui.components.AqTone
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqSpacing

class AppHosts(
    val status: (@Composable () -> Unit)? = null,
    val toastHost: com.agendaqr.core.ui.components.AqToastHostState? = null,
)

@Composable
internal fun AppStatusHost(
    importError: String?,
    isOffline: Boolean,
    pendingCount: Int,
    hasFailed: Boolean,
    onDismissImportError: () -> Unit,
    onRetryNow: () -> Unit,
) {
    Column {
        val error = importError?.let { importReadError(it) }
        if (error != null) {
            AqAlert(
                title = error.what,
                message = error.dataStatus,
                tone = com.agendaqr.core.ui.theme.AqTone.Danger,
                action = com.agendaqr.core.ui.components.AqBannerAction(AppStrings.ReintentarAhora, onRetryNow),
                dismiss = com.agendaqr.core.ui.components.AqBannerAction(AppStrings.Cerrar, onDismissImportError),
            )
        } else if (hasFailed && pendingCount > 0) {
            AqAlert(
                message = syncPendingLabel(pendingCount, retrying = true),
                tone = com.agendaqr.core.ui.theme.AqTone.Danger,
                action = com.agendaqr.core.ui.components.AqBannerAction(AppStrings.ReintentarAhora, onRetryNow),
            )
        } else if (isOffline || pendingCount > 0) {
            Row(
                modifier = Modifier.padding(
                    start = AqSpacing.ScreenMargin,
                    end = AqSpacing.ScreenMargin,
                    top = AqSpacing.Sm,
                ),
                horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm, Alignment.Start),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (isOffline) {
                    AqTag(text = AppStrings.SinConexion)
                }
                if (pendingCount > 0) {
                    AqTag(
                        text = syncPendingLabel(pendingCount, retrying = false),
                        tone = com.agendaqr.core.ui.theme.AqTone.Info,
                    )
                }
            }
        }
    }
}
