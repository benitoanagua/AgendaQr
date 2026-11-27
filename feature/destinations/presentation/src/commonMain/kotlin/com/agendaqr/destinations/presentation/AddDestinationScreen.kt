package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.AqScreen
import com.agendaqr.core.ui.components.AqText
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.destinations.domain.QrAsset

/**
 * S02 — Añadir destino: título con heading y acciones de importación
 * reales (galería, cámara, PDF). La ayuda de compartir desde otra app
 * queda al pie, tras las acciones (jerarquía declarada en el contrato S02).
 *
 * Los controles son los expect/actual del DS de importación
 * ([QrGalleryControls], [QrCameraEntryControl], [QrPdfPickerControl]):
 * la misma implementación que usa el editor y que resuelve el permiso de
 * cámara con su flujo "Abrir ajustes".
 */
@Composable
fun AddDestinationScreen(
    onImport: (List<QrAsset>) -> Unit,
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
            QrGalleryControls(
                onResult = { result -> if (result.assets.isNotEmpty()) onImport(result.assets) },
            )
            QrCameraEntryControl(
                onResult = { result -> if (result.assets.isNotEmpty()) onImport(result.assets) },
                showDivider = true,
            )
            QrPdfPickerControl(showDivider = false)
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
