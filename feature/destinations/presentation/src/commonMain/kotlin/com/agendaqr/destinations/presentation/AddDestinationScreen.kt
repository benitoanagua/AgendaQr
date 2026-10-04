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
import androidx.compose.material3.Text
import com.agendaqr.core.ui.components.XauxaPrimaryButton
import com.agendaqr.core.ui.components.XauxaSecondaryButton
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
        Text(
            "Añadir",
            modifier = Modifier.semantics { heading() },
            fontSize = XauxaType.Headline,
            fontWeight = FontWeight.Bold,
            color = XauxaColor.TextPrimary,
        )
        Text(
            "Trae a Agenda QR algo que ya tienes.",
            color = XauxaColor.TextSecondary,
        )

        QrImportControls { result ->
            if (result.assets.isNotEmpty()) onImport(result.assets)
        }

        Text(
            "Desde otra app",
            fontSize = XauxaType.Title,
            fontWeight = FontWeight.SemiBold,
            color = XauxaColor.TextPrimary,
        )
        Text(
            "Comparte una imagen o PDF desde otra app con Agenda QR. La importación continuará aquí para revisar lo que se encontró.",
            color = XauxaColor.TextSecondary,
        )

        XauxaSecondaryButton(
            label = "Volver",
            onClick = onBack,
        )
    }
}
