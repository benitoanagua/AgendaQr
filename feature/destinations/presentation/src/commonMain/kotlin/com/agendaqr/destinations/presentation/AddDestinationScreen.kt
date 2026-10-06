package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import com.agendaqr.destinations.presentation.AppStrings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import com.agendaqr.core.ui.components.XauxaAppBar
import com.agendaqr.core.ui.components.XauxaPageTitle
import com.agendaqr.core.ui.components.XauxaPrimaryButton
import com.agendaqr.core.ui.components.XauxaText
import com.agendaqr.core.ui.components.XauxaSecondaryButton
import com.agendaqr.core.ui.components.XauxaHeading
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.core.ui.theme.XauxaSpacing
import com.agendaqr.core.ui.theme.XauxaType

/**
 * S02 — Añadir.
 *
 * Esta pantalla solo decide cómo traer un recurso existente. No pide nombre,
 * categoría ni contexto antes de que Agenda QR haya entendido el recurso.
 * La revisión ocurre después de la clasificación (S09).
 */
@Composable
fun AddDestinationScreen(
    onImport: (List<com.agendaqr.destinations.domain.QrAsset>) -> Unit,
    onBack: () -> Unit,
) {
    // V1.1 (M7): Volver vive en la barra de aplicación inferior; el orden
    // congelado S02 (Galería → Desde otra app → Cámara) no cambia.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(XauxaSpacing.ScreenMargin),
            verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Lg),
        ) {
        XauxaPageTitle(text = AppStrings.Anadir)
        XauxaText(
            AppStrings.TraeAAgendaQrAlgo,
            color = XauxaColor.TextSecondary,
        )

        // Orden congelado S02: Galería → Desde otra app → Cámara.
        QrGalleryControls(
            onResult = { result -> if (result.assets.isNotEmpty()) onImport(result.assets) },
            galleryAsPrimary = true,
        )

        XauxaText(
            AppStrings.DesdeOtraApp,
            size = XauxaType.Title,
            fontWeight = FontWeight.SemiBold,
            color = XauxaColor.TextPrimary,
        )
        XauxaText(
            AppStrings.shareFromAnotherAppHint,
            color = XauxaColor.TextSecondary,
        )

        QrCameraEntryControl(
            onResult = { result -> if (result.assets.isNotEmpty()) onImport(result.assets) },
        )

        }
        XauxaAppBar(
            modifier = Modifier.navigationBarsPadding(),
            actions = emptyList(),
            onBack = onBack,
            backLabel = AppStrings.Volver,
        )
    }
}
