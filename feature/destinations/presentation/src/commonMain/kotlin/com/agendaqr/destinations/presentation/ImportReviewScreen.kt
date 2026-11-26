package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.AqAlert
import com.agendaqr.core.ui.components.AqButton
import com.agendaqr.core.ui.components.AqButtonVariant
import com.agendaqr.core.ui.components.AqEmptyPlaceholder
import com.agendaqr.core.ui.components.AqIcons
import com.agendaqr.core.ui.components.AqMenuItem
import com.agendaqr.core.ui.components.AqQrPreview
import com.agendaqr.core.ui.components.AqScreen
import com.agendaqr.core.ui.components.AqSectionTitle
import com.agendaqr.core.ui.components.AqTone
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.destinations.domain.QrAsset

/** S09/S12 — revisar lote de QR. Sin errores muestra los assets y la acción
 *  "Guardar"; con error muestra "Reintentar" (guarda de nuevo) y "Cerrar".
 *  Cada fila usa la anatomía Moon de lista: miniatura QR ([AqQrPreview]),
 *  título = contenido decodificado (evidencia de lo entendido, truncado a
 *  una línea) y divisor entre filas. El payload largo nunca se expone
 *  completo: el texto decodificado se elide con «…». */
@Composable
fun ImportReviewScreen(
    assets: List<QrAsset>,
    onSaveAll: () -> Unit = {},
    onBack: () -> Unit = {},
    error: UserFacingError? = null,
    onClearError: () -> Unit = {},
    onRetryError: () -> Unit = {},
) {
    AqScreen(
        title = AppStrings.RevisarImportacion,
        onBack = onBack,
        backLabel = AppStrings.Volver,
        statusContent = null,
        bottomAction = if (error == null && assets.isNotEmpty()) {
            {
                AqButton(
                    label = AppStrings.Guardar,
                    onClick = onSaveAll,
                    variant = AqButtonVariant.Primary,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        } else {
            null
        },
        scrollable = true,
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
                subtitle = AppStrings.emptyDestinationsHint,
            )
        } else {
            AqSectionTitle(
                text = AppStrings.ElementosARevisar + " (" + assets.size + ")",
            )
            Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(AqSpacing.Sm)) {
                assets.forEachIndexed { index, asset ->
                    AqMenuItem(
                        title = asset.content?.takeIf { it.isNotBlank() }
                            ?: AppStrings.QrSinNombre,
                        subtitle = null,
                        leading = {
                            AqQrPreview(
                                encodedQr = asset.encoded,
                                size = AqSize.Md,
                            )
                        },
                        titleMaxLines = 1,
                        showDivider = index < assets.lastIndex,
                    )
                }
            }
        }
    }
}
