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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.agendaqr.core.ui.components.XauxaEmptyState
import com.agendaqr.core.ui.components.XauxaText
import com.agendaqr.core.ui.components.XauxaQrPreview
import com.agendaqr.core.ui.components.XauxaSecondaryButton
import com.agendaqr.core.ui.components.XauxaStatusBanner
import com.agendaqr.core.ui.components.XauxaTone
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.core.ui.theme.XauxaSpacing
import com.agendaqr.core.ui.theme.XauxaType

@Composable
fun FirstUsePattern(onAdd: () -> Unit) {
    XauxaEmptyState(
        title = "Sin destinos",
        subtitle = "Agrega tu primer QR para empezar.",
        actionLabel = "Agregar QR",
        onAction = onAdd,
    )
}

@Composable
fun OperationFailurePattern(message: String) {
    XauxaStatusBanner(message = message, tone = XauxaTone.Danger)
}

@Composable
fun DestinationNotFound(onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(XauxaSpacing.Xxl)
            .imePadding(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        XauxaEmptyState(
            title = "Destino no encontrado",
            subtitle = "Pudo haber sido eliminado en otro dispositivo.",
            actionLabel = "Volver a la lista",
            onAction = onBack,
        )
    }
}

@Composable
fun QrFullscreenPattern(
    encodedQr: String,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(XauxaSpacing.Xxl)
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Lg, Alignment.CenterVertically),
    ) {
        XauxaText("QR", size = XauxaType.Headline, fontWeight = FontWeight.SemiBold, color = XauxaColor.TextPrimary)
        XauxaQrPreview(encodedQr, modifier = Modifier.fillMaxWidth())
        XauxaSecondaryButton("Volver", onBack)
    }
}
