package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.AqAlert
import com.agendaqr.core.ui.components.AqBannerAction
import com.agendaqr.core.ui.components.AqButton
import com.agendaqr.core.ui.components.AqButtonVariant
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
    Column(
        modifier = Modifier.padding(
            start = AqSpacing.ScreenMargin,
            end = AqSpacing.ScreenMargin,
            top = AqSpacing.Sm,
        ),
    ) {
        val error = importError?.let { importReadError(it) }
        if (error != null) {
            // §12: el banner de error de importación gana sobre todo lo demás;
            // sus acciones (reintentar / elegir otra imagen / cerrar) viajan
            // listas para mostrar vía [bannerActions], sin duplicar botones.
            val (confirmAction, dismissAction) = error.bannerActions(
                onRetry = onRetryNow,
                onClear = onDismissImportError,
            )
            AqAlert(
                tone = AqTone.Danger,
                message = error.display(),
                confirmLabel = confirmAction?.label,
                onConfirm = confirmAction?.onClick,
                dismissLabel = dismissAction?.label,
                onDismiss = dismissAction?.onClick,
                horizontalPadding = AqSpacing.None,
            )
        } else if (hasFailed && pendingCount > 0) {
            // Mutación fallida con pendientes: banner completo con "Reintentar
            // ahora" (re-muta) y "Cerrar".
            AqAlert(
                tone = AqTone.Danger,
                message = syncPendingLabel(pendingCount, retrying = true),
                confirmLabel = AppStrings.ReintentarAhora,
                onConfirm = onRetryNow,
                dismissLabel = AppStrings.Cerrar,
                onDismiss = { },
                horizontalPadding = AqSpacing.None,
            )
        } else if (isOffline || pendingCount > 0) {
            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm, Alignment.Start),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (isOffline) {
                    AqTag(text = AppStrings.SinConexion)
                }
                if (pendingCount > 0) {
                    AqTag(
                        text = syncPendingLabel(pendingCount, retrying = false),
                        tone = AqTone.Info,
                    )
                }
            }
        }
    }
}
