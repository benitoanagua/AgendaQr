package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import com.agendaqr.destinations.presentation.AppStrings
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.XauxaAppBar
import com.agendaqr.core.ui.components.XauxaEmptyState
import com.agendaqr.core.ui.components.XauxaPageTitle
import com.agendaqr.core.ui.components.XauxaQrPreview
import com.agendaqr.core.ui.components.XauxaScreenScaffold
import com.agendaqr.core.ui.components.XauxaStatusBanner
import com.agendaqr.core.ui.components.XauxaTone
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
    XauxaStatusBanner(message = message, tone = XauxaTone.Danger)
}

@Composable
fun DestinationNotFound(onBack: () -> Unit) {
    XauxaScreenScaffold(
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
    // M6/M7: título de página en display ligero; Volver vive en la barra
    // de aplicación inferior, no suelto en el cuerpo.
    XauxaScreenScaffold(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Lg, Alignment.CenterVertically),
        bottomBar = {
            XauxaAppBar(
                actions = emptyList(),
                onBack = onBack,
                backLabel = AppStrings.Volver,
            )
        },
    ) {
        XauxaPageTitle(text = AppStrings.Qr)
        XauxaQrPreview(encodedQr, modifier = Modifier.fillMaxWidth())
    }
}
