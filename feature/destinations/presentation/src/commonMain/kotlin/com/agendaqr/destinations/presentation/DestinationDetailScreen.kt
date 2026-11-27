package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import com.agendaqr.core.ui.components.AqAppBarAction
import com.agendaqr.core.ui.components.AqButton
import com.agendaqr.core.ui.components.AqButtonVariant
import com.agendaqr.core.ui.components.AqIcons
import com.agendaqr.core.ui.components.AqMenuItem
import com.agendaqr.core.ui.components.AqModal
import com.agendaqr.core.ui.components.AqQrPreview
import com.agendaqr.core.ui.components.AqScreen
import com.agendaqr.core.ui.components.AqToastHost
import com.agendaqr.core.ui.components.AqToastHostState
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.destinations.domain.Destination

/**
 * S05 — Detalle de un destino.
 *
 * La tarea central es MOSTRAR el QR (primaria); Compartir es secundaria.
 * Registrar actividad, Editar y Eliminar viven en el overflow "Más"; la
 * eliminación mantiene su diálogo destructivo con Confirmar/Cancelar y el
 * anuncio "Eliminado" vía el [AqToastHostState] compartido de la app.
 * El valor codificado del QR nunca se muestra como texto: la fila de
 * acceso al QR lleva la vista previa como miniatura y metadatos humanos
 * (categoría/nota), no datos técnicos.
 */
@Composable
fun DestinationDetailScreen(
    destination: Destination,
    onShowQr: () -> Unit = {},
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {},
    onShare: () -> Unit = {},
    onBack: () -> Unit,
    /** Dock/overflow: registrar una actividad (S07, sin preasociación). */
    onRegisterOperation: () -> Unit = {},
    appHosts: AppHosts? = null,
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }
    val haptics = LocalHapticFeedback.current
    val humanMeta = listOfNotNull(
        destination.category?.takeIf { it.isNotBlank() },
        destination.note?.takeIf { it.isNotBlank() },
    ).joinToString(" · ").takeIf { it.isNotBlank() }
    AqScreen(
        title = destination.name.ifBlank { AppStrings.SinNombre },
        onBack = onBack,
        backLabel = AppStrings.Volver,
        overflowActions = listOf(
            AqAppBarAction(AppStrings.RegistrarOperacion, AqIcons.Register, onRegisterOperation),
            AqAppBarAction(AppStrings.Editar, AqIcons.Edit, onEdit),
            AqAppBarAction(AppStrings.Eliminar, AqIcons.Trash, { showDeleteConfirm = true }),
        ),
        overflowLabel = AppStrings.Mas,
        statusContent = appHosts?.status,
        toastHost = appHosts?.toastHost?.let { host -> { AqToastHost(host) } },
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

    if (showDeleteConfirm) {
        AqModal(
            title = AppStrings.EliminarDestino,
            dismissLabel = AppStrings.Cancelar,
            confirmLabel = AppStrings.Eliminar,
            destructive = true,
            onConfirm = {
                showDeleteConfirm = false
                // El guardado local y el encolado son atómicos (§5): el
                // anuncio es inmediato y el host sobrevive a la navegación.
                haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                appHosts?.toastHost?.show(AppStrings.Eliminado)
                onDelete()
            },
            onDismissRequest = { showDeleteConfirm = false },
            content = {
                // Aviso destructivo: la fila del modal queda en el copy del
                // título/confirmación; el cuerpo repite la consecuencia.
            },
        )
    }
}
