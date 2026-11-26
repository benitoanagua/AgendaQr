package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.AqButton
import com.agendaqr.core.ui.components.AqButtonVariant
import com.agendaqr.core.ui.components.AqMenuItem
import com.agendaqr.core.ui.components.AqQrPreview
import com.agendaqr.core.ui.components.AqScreen
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.destinations.domain.Destination

/** Pantalla de detalle de un destino (S05). Única acción primaria: Mostrar
 *  QR. Compartir como secundaria visible; sin Registro/Editar en el dock.
 *  El valor codificado del QR nunca se muestra como texto: la fila de
 *  acceso al QR lleva la vista previa como miniatura y metadatos humanos
 *  (categoría/nota), no datos técnicos. */
@Composable
fun DestinationDetailScreen(
    destination: Destination,
    onShowQr: () -> Unit = {},
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {},
    onShare: () -> Unit = {},
    onBack: () -> Unit,
) {
    val humanMeta = listOfNotNull(
        destination.category?.takeIf { it.isNotBlank() },
        destination.note?.takeIf { it.isNotBlank() },
    ).joinToString(" · ").takeIf { it.isNotBlank() }
    AqScreen(
        title = destination.name.ifBlank { AppStrings.SinNombre },
        onBack = onBack,
        backLabel = AppStrings.Volver,
        statusContent = null,
        toastHost = null,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(AqSize.Sm),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AqMenuItem(
                title = AppStrings.MuestraEsteCodigo,
                subtitle = humanMeta,
                onClick = onShowQr,
                leading = {
                    AqQrPreview(
                        encodedQr = destination.qr.encoded,
                        size = AqSize.Md,
                    )
                },
                modifier = Modifier.fillMaxWidth(),
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
