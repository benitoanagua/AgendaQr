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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.agendaqr.core.ui.components.XauxaAppBar
import com.agendaqr.core.ui.components.XauxaEmptyState
import com.agendaqr.core.ui.components.XauxaText
import com.agendaqr.core.ui.components.XauxaPageTitle
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
        title = AppStrings.SinDestinos,
        subtitle = AppStrings.emptyDestinationsHint,
        actionLabel = AppStrings.Anadir,
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
            .padding(XauxaSpacing.ScreenMargin)
            .imePadding(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        XauxaEmptyState(
            title = AppStrings.DestinoNoEncontrado,
            subtitle = AppStrings.PudoHaberSidoEliminadoEn,
            actionLabel = AppStrings.VolverALaLista,
            onAction = onBack,
        )
    }
}

@Composable
fun QrFullscreenPattern(
    encodedQr: String,
    onBack: () -> Unit,
) {
    // M6/M7: título de página en display ligero; Volver vive en la barra
    // de aplicación inferior, no suelto en el cuerpo.
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
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Lg, Alignment.CenterVertically),
        ) {
            XauxaPageTitle(text = AppStrings.Qr)
            XauxaQrPreview(encodedQr, modifier = Modifier.fillMaxWidth())
        }
        XauxaAppBar(
            modifier = Modifier.navigationBarsPadding(),
            actions = emptyList(),
            onBack = onBack,
            backLabel = AppStrings.Volver,
        )
    }
}
