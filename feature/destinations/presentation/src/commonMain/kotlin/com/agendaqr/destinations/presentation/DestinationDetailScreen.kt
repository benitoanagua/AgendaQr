package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.AqButton
import com.agendaqr.core.ui.components.AqButtonVariant
import com.agendaqr.core.ui.components.AqIcons
import com.agendaqr.core.ui.components.AqMenuItem
import com.agendaqr.core.ui.components.AqScreen
import com.agendaqr.core.ui.components.AqText
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.destinations.domain.Destination

/** Pantalla de detalle de un destino (S05). Única acción primaria: Mostrar
 *  QR. Compartir como secundaria visible; sin Registro/Editar en el dock. */
@Composable
fun DestinationDetailScreen(
    destination: Destination,
    onShowQr: () -> Unit = {},
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {},
    onShare: () -> Unit = {},
    onBack: () -> Unit,
) {
    val toastHost = null
    AqScreen(
        title = destination.name.ifBlank { AppStrings.SinNombre },
        onBack = onBack,
        backLabel = AppStrings.Volver,
        statusContent = null,
        toastHost = null,
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            AqMenuItem(
                title = AppStrings.MuestraEsteCodigo,
                subtitle = destination.qr.encoded.takeIf { it.isNotEmpty() },
                onClick = onShowQr,
                leading = { com.agendaqr.core.ui.components.AqIcon(imageVector = com.agendaqr.core.ui.components.AqIcons.QrCode, contentDescription = null) },
            )
            AqButton(
                label = AppStrings.MostrarQr,
                onClick = onShowQr,
                variant = AqButtonVariant.Primary,
                modifier = Modifier.fillMaxWidth(),
            )
            AqButton(
                label = AppStrings.Compartir,
                onClick = onShare,
                variant = AqButtonVariant.Text,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
