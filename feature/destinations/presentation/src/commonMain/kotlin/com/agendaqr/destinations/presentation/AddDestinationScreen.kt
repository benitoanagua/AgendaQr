package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import com.agendaqr.destinations.presentation.AppStrings
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.XauxaListRow
import com.agendaqr.core.ui.components.XauxaPrimaryButton
import com.agendaqr.core.ui.components.XauxaScreenScaffold
import com.agendaqr.core.ui.components.XauxaSnackbarHost
import com.agendaqr.core.ui.components.XauxaSecondaryButton
import com.agendaqr.core.ui.components.XauxaText
import com.agendaqr.core.ui.components.XauxaTopBar
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.core.ui.theme.XauxaSpacing

/**
 * S02 — Añadir (V1.2, ADR-0012).
 *
 * Esta pantalla solo decide cómo traer un recurso existente. No pide nombre,
 * categoría ni contexto antes de que Agenda QR haya entendido el recurso.
 * La revisión ocurre después de la clasificación (S09).
 *
 * Lista de acciones con icono y etiqueta: Elegir de la galería (una o
 * varias imágenes) → Escanear con la cámara (con el texto de privacidad
 * visible junto a la acción). El bloque "Desde otra app" es una ayuda breve
 * y solo aparece donde la plataforma soporta compartir hacia Agenda QR.
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

        // Orden congelado S02: Galería → (Desde otra app) → Cámara.
        // Una única acción de galería con selección múltiple: 1 elemento →
        // revisión única (S09); 2 o más → lote (S12).
        QrGalleryControls(
            onResult = { result -> if (result.assets.isNotEmpty()) onImport(result.assets) },
        )

        // "Desde otra app": ayuda breve, solo donde la plataforma lo
        // soporta (Android: share intents; iOS: pendiente de Share
        // Extension, no se muestra).
        if (sharedImportsSupported()) {
            XauxaText(
                AppStrings.shareFromAnotherAppHint,
                color = XauxaColor.TextSecondary,
            )
        }

        QrCameraEntryControl(
            onResult = { result -> if (result.assets.isNotEmpty()) onImport(result.assets) },
        )
    }
}
