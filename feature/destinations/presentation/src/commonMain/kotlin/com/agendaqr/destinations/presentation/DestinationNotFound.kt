package com.agendaqr.destinations.presentation

import androidx.compose.runtime.Composable
import com.agendaqr.core.ui.components.AqEmptyPlaceholder
import com.agendaqr.core.ui.components.AqIcons
import com.agendaqr.core.ui.components.AqScreen
import com.agendaqr.core.ui.components.AqText

/**
 * Superficie de destino no encontrado (S05). Muestra un estado vacío
 * informativo y una acción de regreso.
 */
@Composable
internal fun DestinationNotFound(
    onBack: () -> Unit,
) {
    AqScreen(
        title = AppStrings.DestinoNoEncontrado,
        onBack = onBack,
        backLabel = AppStrings.Volver,
    ) {
        AqEmptyPlaceholder(
            icon = AqIcons.QrCode,
            title = AppStrings.QrNoSePudoMostrar,
            subtitle = AppStrings.PudoHaberSidoEliminadoEn,
        )
    }
}
