package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import com.agendaqr.destinations.presentation.AppStrings
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.XauxaEmptyState
import com.agendaqr.core.ui.components.XauxaQrPreview
import com.agendaqr.core.ui.components.XauxaScreenScaffold
import com.agendaqr.core.ui.components.XauxaTopBar
import com.agendaqr.core.ui.theme.XauxaSpacing

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
    com.agendaqr.core.ui.components.XauxaStatusBanner(message = message, tone = com.agendaqr.core.ui.components.XauxaTone.Danger)
}

@Composable
fun DestinationNotFound(onBack: () -> Unit) {
    XauxaScreenScaffold(
        topBar = {
            XauxaTopBar(
                title = AppStrings.DestinoNoEncontrado,
                onBack = onBack,
                backLabel = AppStrings.Volver,
            )
        },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
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
    // V1.2 (§12): "QR" en la barra superior con flecha atrás.
    XauxaScreenScaffold(
        topBar = {
            XauxaTopBar(
                title = AppStrings.Qr,
                onBack = onBack,
                backLabel = AppStrings.Volver,
            )
        },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Lg, Alignment.CenterVertically),
    ) {
        XauxaQrPreview(encodedQr, modifier = Modifier.fillMaxWidth())
    }
}
