package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.AqEmptyState
import com.agendaqr.core.ui.components.AqQrPreview
import com.agendaqr.core.ui.components.AqScreenScaffold
import com.agendaqr.core.ui.components.AqText
import com.agendaqr.core.ui.components.AqTopBar
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.core.ui.theme.AqWeight

@Composable
fun DestinationNotFound(onBack: () -> Unit) {
    AqScreenScaffold(
        topBar = {
            AqTopBar(
                title = AppStrings.DestinoNoEncontrado,
                onBack = onBack,
                backLabel = AppStrings.Volver,
            )
        },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        AqEmptyState(
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
    /** Nombre del destino bajo el QR (copy resuelto por el llamador). */
    destinationName: String = "",
) {
    // Brillo al máximo y pantalla encendida mientras se muestra;
    // se restauran al salir (incluida rotación, Back y bloqueo).
    QrDisplayScreenEffects()
    // V1.4 (§12): "QR" en la barra superior con flecha atrás.
    AqScreenScaffold(
        topBar = {
            AqTopBar(
                title = AppStrings.Qr,
                onBack = onBack,
                backLabel = AppStrings.Volver,
            )
        },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AqSpacing.Lg, Alignment.CenterVertically),
    ) {
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            // Lado = mínimo entre ancho y alto disponibles menos margen:
            // lo más grande posible sin recortar (siempre 1:1).
            val side = (minOf(maxWidth, maxHeight) - AqSpacing.Xxxl).coerceAtLeast(AqMetrics.QrPreviewSize)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AqSpacing.Lg),
            ) {
                AqQrPreview(
                    encodedQr,
                    size = side,
                    unavailableMessage = AppStrings.QrNoSePudoMostrar,
                )
                if (destinationName.isNotBlank()) {
                    AqText(
                        destinationName,
                        size = AqType.Title,
                        fontWeight = AqWeight.SemiBold,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    )
                }
            }
        }
    }
}
