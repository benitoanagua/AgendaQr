package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.AmEmptyState
import com.agendaqr.core.ui.components.AmQrPreview
import com.agendaqr.core.ui.components.AmScreenScaffold
import com.agendaqr.core.ui.components.AmText
import com.agendaqr.core.ui.components.AmTopBar
import com.agendaqr.core.ui.theme.AmMetrics
import com.agendaqr.core.ui.theme.AmSpacing
import com.agendaqr.core.ui.theme.AmType

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
    /** Nombre del destino bajo el QR (copy resuelto por el llamador). */
    destinationName: String = "",
) {
    // U-05/D4: brillo al máximo y pantalla encendida mientras se muestra;
    // se restauran al salir (incluida rotación, Back y bloqueo).
    QrDisplayScreenEffects()
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
        BoxWithConstraints(modifier = Modifier.fillMaxWidth()) {
            // Lado = mínimo entre ancho y alto disponibles menos margen:
            // lo más grande posible sin recortar (siempre 1:1).
            val side = (minOf(maxWidth, maxHeight) - AmSpacing.Xxxl).coerceAtLeast(AmMetrics.QrPreviewSize)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AmSpacing.Lg),
            ) {
                AmQrPreview(
                    encodedQr,
                    size = side,
                    unavailableMessage = AppStrings.QrNoSePudoMostrar,
                )
                if (destinationName.isNotBlank()) {
                    AmText(
                        destinationName,
                        size = AmType.Title,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    )
                }
            }
        }
    }
}
