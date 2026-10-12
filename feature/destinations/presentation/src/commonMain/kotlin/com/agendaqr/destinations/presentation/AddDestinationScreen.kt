package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import com.agendaqr.destinations.presentation.AppStrings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.XauxaSnackbarHost
import com.agendaqr.core.ui.components.XauxaScreenScaffold
import com.agendaqr.core.ui.components.XauxaSectionHeader
import com.agendaqr.core.ui.components.XauxaText
import com.agendaqr.core.ui.components.XauxaTopBar
import com.agendaqr.core.ui.theme.XauxaColor

/**
 * S02 — Añadir.
 *
 * Esta pantalla solo decide cómo traer un recurso existente. No pide nombre,
 * categoría ni contexto antes de que Agenda QR haya entendido el recurso.
 * La revisión ocurre después de la clasificación (S09).
 */
@Composable
fun AddDestinationScreen(
    appHosts: AppHosts? = null,
    onImport: (List<com.agendaqr.destinations.domain.QrAsset>) -> Unit,
    onBack: () -> Unit,
) {
    // V1.2 (§12): la flecha atrás vive en la barra superior (la misma en
    // ambas plataformas); el orden congelado S02 (Galería → Desde otra app
    // → Cámara) no cambia.
    XauxaScreenScaffold(
        topBar = {
            XauxaTopBar(
                title = AppStrings.Anadir,
                onBack = onBack,
                backLabel = AppStrings.Volver,
            )
        },
        snackbarHost = appHosts?.feedback?.let { host -> { XauxaSnackbarHost(host) } },
        statusContent = appHosts?.status,
    ) {
        XauxaText(
            AppStrings.TraeAAgendaQrAlgo,
            color = XauxaColor.TextSecondary,
        )

        // Orden congelado S02: Galería → Desde otra app → Cámara.
        QrGalleryControls(
            onResult = { result -> if (result.assets.isNotEmpty()) onImport(result.assets) },
            galleryAsPrimary = true,
        )

        XauxaSectionHeader(text = AppStrings.DesdeOtraApp)
        XauxaText(
            AppStrings.shareFromAnotherAppHint,
            color = XauxaColor.TextSecondary,
        )

        QrCameraEntryControl(
            onResult = { result -> if (result.assets.isNotEmpty()) onImport(result.assets) },
        )
    }
}
