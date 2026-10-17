package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.agendaqr.core.ui.components.AmScreenScaffold
import com.agendaqr.core.ui.components.AmSectionHeader
import com.agendaqr.core.ui.components.AmSnackbarHost
import com.agendaqr.core.ui.components.AmText
import com.agendaqr.core.ui.components.AmTopBar
import com.agendaqr.core.ui.theme.AmColor
import com.agendaqr.core.ui.theme.AmSpacing
import com.agendaqr.core.ui.theme.AmType

/**
 * S02 — Añadir (V1.4, ADR-0014).
 *
 * Esta pantalla solo decide cómo traer un recurso existente. No pide nombre,
 * categoría ni contexto antes de que Agenda QR haya entendido el recurso.
 * La revisión ocurre después de la clasificación (S09).
 *
 * Grupos visuales (separados a 20 dp): explicación · fuentes de
 * galería/archivo (alternativas, 8 dp entre sí) · cámara con su contexto
 * de privacidad. La galería es la acción primaria; el resto son
 * alternativas y no compiten por el peso visual. El bloque "Desde otra
 * app" es una ayuda breve y solo aparece donde la plataforma soporta
 * compartir hacia Agenda QR.
 */
@Composable
fun AddDestinationScreen(
    appHosts: AppHosts? = null,
    onImport: (List<com.agendaqr.destinations.domain.QrAsset>) -> Unit,
    onBack: () -> Unit,
) {
    // V1.4 (§12): la flecha atrás vive en la barra superior (la misma en
    // ambas plataformas); el orden congelado S02 (Galería → Desde otra app
    // → Cámara) no cambia.
    AmScreenScaffold(
        topBar = {
            AmTopBar(
                title = AppStrings.Anadir,
                onBack = onBack,
                backLabel = AppStrings.Volver,
            )
        },
        snackbarHost = appHosts?.feedback?.let { host -> { AmSnackbarHost(host) } },
        statusContent = appHosts?.status,
        verticalArrangement = Arrangement.spacedBy(AmSpacing.Xl),
    ) {
        AmText(
            AppStrings.TraeAAgendaQrAlgo,
            color = AmColor.TextSecondary,
        )

        // Orden congelado S02: Galería → (Desde otra app) → Cámara.
        // Fuentes de archivo como grupo con rótulo de sección; una única
        // acción de galería con selección múltiple: 1 elemento → revisión
        // única (S09); 2 o más → lote (S12).
        Column(verticalArrangement = Arrangement.spacedBy(AmSpacing.Sm)) {
            AmSectionHeader(text = AppStrings.DesdeArchivos)
            QrGalleryControls(
                onResult = { result -> if (result.assets.isNotEmpty()) onImport(result.assets) },
            )

            // PDF (opcional): solo donde la plataforma lo soporta (F7).
            QrPdfPickerControl()
        }

        // Fuente de cámara con su rótulo de sección. "Desde otra app": ayuda
        // breve, solo donde la plataforma lo soporta (Android: share
        // intents; iOS: pendiente de Share Extension, no se muestra). Vive
        // pegada al grupo de cámara al que contextualiza (4 dp), no como
        // bloque aislado.
        Column(verticalArrangement = Arrangement.spacedBy(AmSpacing.Sm)) {
            AmSectionHeader(text = AppStrings.DesdeLaCamara)
            Column(verticalArrangement = Arrangement.spacedBy(AmSpacing.Xs)) {
                if (sharedImportsSupported()) {
                    AmText(
                        AppStrings.shareFromAnotherAppHint,
                        size = AmType.Caption,
                        color = AmColor.TextSecondary,
                    )
                }

                QrCameraEntryControl(
                    onResult = { result -> if (result.assets.isNotEmpty()) onImport(result.assets) },
                )
            }
        }
    }
}
