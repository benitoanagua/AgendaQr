package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.AqButton
import com.agendaqr.core.ui.components.AqButtonVariant
import com.agendaqr.core.ui.components.AqIcon
import com.agendaqr.core.ui.components.AqIcons
import com.agendaqr.core.ui.components.AqScreen
import com.agendaqr.core.ui.components.AqText
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqType

/** S02 — Añadir destino: título con heading y acciones de importación
 *  (galería, cámara, PDF). La ayuda de compartir desde otra app queda al
 *  pie, tras las acciones (jerarquía declarada en el contrato S02).
 */
@Composable
fun AddDestinationScreen(
    onImport: () -> Unit,
    onBack: () -> Unit,
) {
    AqScreen(
        title = AppStrings.AnadirDestino,
        onBack = onBack,
        backLabel = AppStrings.Volver,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
        ) {
            AqButton(
                label = AppStrings.ElegirDeLaGaleria,
                onClick = onImport,
                variant = AqButtonVariant.Primary,
                icon = AqIcons.Gallery,
                modifier = Modifier.fillMaxWidth(),
            )
            // D1: la segunda acción primaria del documento pasa a Outlined
            // para no competir con la primera (Elegir de la galería).
            AqButton(
                label = AppStrings.Camara,
                onClick = onImport,
                variant = AqButtonVariant.Outline,
                icon = AqIcons.Camera,
                modifier = Modifier.fillMaxWidth(),
            )
            AqText(
                text = AppStrings.PermisoCamaraContexto,
                size = AqType.BodyXs,
                color = AqColor.TextSecondary,
            )
            AqButton(
                label = AppStrings.ElegirArchivoPdf,
                onClick = onImport,
                variant = AqButtonVariant.Text,
                modifier = Modifier.fillMaxWidth(),
            )
            if (sharedImportsSupported()) {
                AqText(
                    text = AppStrings.shareFromAnotherAppHint,
                    size = AqType.BodySm,
                    color = AqColor.TextSecondary,
                )
            }
        }
    }
}
