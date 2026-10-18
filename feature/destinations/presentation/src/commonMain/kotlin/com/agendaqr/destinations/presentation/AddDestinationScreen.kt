package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import com.agendaqr.core.ui.components.AmListBody
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
        scrollable = true,
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

        // Galería y archivo son celdas del grupo; una única acción de
        // galería conserva la selección múltiple y el enrutado por cantidad.
        Column(verticalArrangement = Arrangement.spacedBy(AmSpacing.Sm)) {
            AmSectionHeader(text = AppStrings.DesdeArchivos)
            AmListBody {
                QrGalleryControls(
                    onResult = { result -> if (result.assets.isNotEmpty()) onImport(result.assets) },
                )
                QrPdfPickerControl()
            }
        }

        Column(verticalArrangement = Arrangement.spacedBy(AmSpacing.Sm)) {
            AmSectionHeader(text = AppStrings.DesdeLaCamara)
            AmListBody {
                QrCameraEntryControl(
                    onResult = { result -> if (result.assets.isNotEmpty()) onImport(result.assets) },
                )
            }
        }

        if (sharedImportsSupported()) {
            AmText(
                AppStrings.shareFromAnotherAppHint,
                size = AmType.Caption,
                color = AmColor.TextSecondary,
            )
        }
    }
}
