package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.AqAlert
import com.agendaqr.core.ui.components.AqButton
import com.agendaqr.core.ui.components.AqButtonVariant
import com.agendaqr.core.ui.components.AqEmptyPlaceholder
import com.agendaqr.core.ui.components.AqIcon
import com.agendaqr.core.ui.components.AqIcons
import com.agendaqr.core.ui.components.AqMenuItem
import com.agendaqr.core.ui.components.AqScreen
import com.agendaqr.core.ui.components.AqText
import com.agendaqr.core.ui.components.AqToastHost
import com.agendaqr.core.ui.components.AqTone
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.destinations.domain.QrAsset

/** S09/S12 — revisar lote de QR. Sin errores muestra los assets y la acción
 *  "Guardar"; con error muestra "Reintentar" (guarda de nuevo) y "Cerrar". */
@Composable
fun ImportReviewScreen(
    assets: List<QrAsset>,
    onSaveAll: () -> Unit = {},
    onBack: () -> Unit = {},
    error: UserFacingError? = null,
    onClearError: () -> Unit = {},
    onRetryError: () -> Unit = {},
) {
    val toastHost = null
    AqScreen(
        title = AppStrings.RevisarImportacion,
        onBack = onBack,
        backLabel = AppStrings.Volver,
        statusContent = null,
        toastHost = null,
    ) {
        if (error != null) {
            AqAlert(
                tone = AqTone.Danger,
                message = error.display(),
                confirmLabel = AppStrings.ErrorReintentar,
                onConfirm = onSaveAll,
                dismissLabel = AppStrings.Cerrar,
                onDismiss = onClearError,
            )
        } else if (assets.isEmpty()) {
            AqEmptyPlaceholder(
                icon = AqIcons.Search,
                title = AppStrings.SinElementosParaRevisar,
            )
        } else {
            Column(modifier = Modifier.fillMaxWidth()) {
                AqMenuItem(
                    title = AppStrings.ElementosARevisar + " (" + assets.size + ")",
                )
                assets.forEachIndexed { index, asset ->
                    AqMenuItem(
                        title = asset.content.orEmpty(),
                    )
                }
                AqButton(
                    label = AppStrings.Guardar,
                    onClick = onSaveAll,
                    variant = AqButtonVariant.Primary,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
