package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.AmEmptyState
import com.agendaqr.core.ui.components.AmQrPreview
import com.agendaqr.core.ui.components.AmScreenScaffold
import com.agendaqr.core.ui.components.AmTopBar
import com.agendaqr.core.ui.theme.AmSpacing

@Composable
fun DestinationNotFound(onBack: () -> Unit) {
    AmScreenScaffold(
        topBar = {
            AmTopBar(
                title = AppStrings.DestinoNoEncontrado,
                onBack = onBack,
                backLabel = AppStrings.Volver,
            )
        },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        AmEmptyState(
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
    // V1.4 (§12): "QR" en la barra superior con flecha atrás.
    AmScreenScaffold(
        topBar = {
            AmTopBar(
                title = AppStrings.Qr,
                onBack = onBack,
                backLabel = AppStrings.Volver,
            )
        },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AmSpacing.Lg, Alignment.CenterVertically),
    ) {
        AmQrPreview(encodedQr, modifier = Modifier.fillMaxWidth())
    }
}
