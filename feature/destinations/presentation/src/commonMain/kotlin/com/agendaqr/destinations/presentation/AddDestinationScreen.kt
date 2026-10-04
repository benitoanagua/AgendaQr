package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(XauxaSpacing.Xxl)
            .imePadding(),
        verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Lg),
    ) {
        XauxaHeading(
            text = "Añadir",
            size = XauxaType.Headline,
        )
        XauxaText(
            "Trae a Agenda QR algo que ya tienes.",
            color = XauxaColor.TextSecondary,
        )

        QrImportControls(onResult = { result -> if (result.assets.isNotEmpty()) onImport(result.assets) }, galleryAsPrimary = true)

        XauxaText(
            "Desde otra app",
            size = XauxaType.Title,
            fontWeight = FontWeight.SemiBold,
            color = XauxaColor.TextPrimary,
        )
        XauxaText(
            "Comparte una imagen o PDF desde otra app con Agenda QR. La importación continuará aquí para revisar lo que se encontró.",
            color = XauxaColor.TextSecondary,
        )

        XauxaSecondaryButton(
            label = "Volver",
            onClick = onBack,
        )
    }
}
