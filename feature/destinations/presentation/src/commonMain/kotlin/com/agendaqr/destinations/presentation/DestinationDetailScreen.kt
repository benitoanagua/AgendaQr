package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.AmSnackbarHost
import com.agendaqr.core.ui.components.AmFeedbackEvent
import com.agendaqr.core.ui.components.AmScreenScaffold
import com.agendaqr.core.ui.components.AmTopBar
import com.agendaqr.core.ui.components.AmOverflowAction
import com.agendaqr.core.ui.components.AmCompactAction
import com.agendaqr.core.ui.components.AmDialog
import com.agendaqr.core.ui.components.AmEmptyState
import com.agendaqr.core.ui.components.AmHairline
import com.agendaqr.core.ui.components.AmIcon
import com.agendaqr.core.ui.components.AmIcons
import com.agendaqr.core.ui.components.AmListBody
import com.agendaqr.core.ui.components.AmMetaRow
import com.agendaqr.core.ui.components.AmQrPreview
import com.agendaqr.core.ui.components.AmScannerFrame
import com.agendaqr.core.ui.components.AmSecondaryButton
import com.agendaqr.core.ui.components.AmStatusPill
import com.agendaqr.core.ui.components.AmText
import com.agendaqr.core.ui.components.AmTextAction
import com.agendaqr.core.ui.components.AmTone
import com.agendaqr.core.ui.components.AmPrimaryButton
import com.agendaqr.core.ui.components.rememberAmClipboard
import com.agendaqr.core.ui.theme.AmColor
import com.agendaqr.core.ui.theme.AmMetrics
import com.agendaqr.core.ui.theme.AmSpacing
import com.agendaqr.core.ui.theme.AmType
import com.agendaqr.destinations.domain.Destination

/**
 * Detalle y exhibición del QR (diseño MCP "Detalle y Exhibición QR"):
 * tarjeta núcleo con el código real (decodificado del dominio, nunca
 * imagen estática), esquinas de encuadre, guía de uso, botón de
 * pantalla completa (RF-05 real), metadatos en filas compactas con
 * copiar del contenido y dock de acciones reales. El banner "Local" del
 * diseño vive en el host de estado de la app (no por pantalla).
 */
@Composable
fun DestinationDetailScreen(
    destination: Destination,
    appHosts: AppHosts? = null,
    onShowQr: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onShare: () -> Unit,
    onBack: () -> Unit,
    /** Dock: registrar una actividad (S07, sin preasociación). */
    onRegisterOperation: () -> Unit = {},
    syncStatus: ElementSyncStatus = ElementSyncStatus.Synced,
    onRetrySync: () -> Unit = {},
) {
    var showDeleteConfirm by remember { mutableStateOf(false) }
    // anuncio de eliminación confirmada (por evento) y copiado (por evento).
    var deleteDoneEvent by remember { mutableStateOf<Any?>(null) }
    var copiedEvent by remember { mutableStateOf<Any?>(null) }
    val copyToClipboard = com.agendaqr.core.ui.components.rememberAmClipboard()
    AmFeedbackEvent(
        event = deleteDoneEvent,
        message = AppStrings.Eliminado,
        feedbackHost = appHosts?.feedback,
    )
    AmFeedbackEvent(
        event = copiedEvent,
        message = AppStrings.Copiado,
        feedbackHost = appHosts?.feedback,
    )
    // V1.4 + MCP: título del destino en la barra; Compartir/Editar viven
    // en el dock de acciones (bottomAction slot); Eliminar en "…" (destructiva, no a un
    // toque). Mostrar QR = pantalla completa (RF-05), acción propia del
    // núcleo.
    AmScreenScaffold(
        scrollable = true,
        topBar = {
            AmTopBar(
                title = destination.name.ifBlank { AppStrings.SinNombre },
                onBack = onBack,
                backLabel = AppStrings.Volver,
                overflowActions = listOf(
                    AmOverflowAction(AppStrings.Eliminar, { showDeleteConfirm = true }),
                ),
                overflowLabel = AppStrings.Mas,
            )
        },
        snackbarHost = appHosts?.feedback?.let { host -> { AmSnackbarHost(host) } },
        statusContent = appHosts?.status,
        verticalArrangement = Arrangement.spacedBy(AmSpacing.Lg),
        bottomAction = {
            // Dock de acciones persistente (referencia MCP): registrar, compartir y editar
            // — todas reales. "Registrar operación" es la acción primaria (sólida),
            // Compartir y Editar son secundarias. El dock respeta insets de navegación y IME.
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .imePadding()
                    .padding(horizontal = AmSpacing.ScreenMargin, vertical = AmSpacing.Sm),
                verticalArrangement = Arrangement.spacedBy(AmSpacing.Sm),
            ) {
                AmPrimaryButton(
                    label = AppStrings.RegistrarOperacion,
                    onClick = onRegisterOperation,
                    modifier = Modifier.fillMaxWidth(),
                )
                AmSecondaryButton(
                    label = AppStrings.Compartir,
                    onClick = onShare,
                    icon = AmIcons.Share,
                    modifier = Modifier.fillMaxWidth(),
                )
                AmTextAction(
                    label = AppStrings.EditarMetadatosReemplazarQr,
                    onClick = onEdit,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
    ) {
        // Tarjeta núcleo del QR (referencia MCP): código real sobre lienzo
        // claro, encuadre de esquinas y guía de uso honesta.
        if (destination.qr.encoded.isNotBlank()) {
            AmListBody {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(AmSpacing.Lg),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(AmSpacing.Md),
                ) {
                    AmScannerFrame(size = AmMetrics.QrPreviewSize) {
                        AmQrPreview(
                            encodedQr = destination.qr.encoded,
                            size = AmMetrics.QrPreviewSize,
                        )
                    }
                    AmText(
                        AppStrings.MuestraEsteCodigo,
                        color = AmColor.TextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    )
                    AmSecondaryButton(
                        label = AppStrings.PantallaCompleta,
                        onClick = onShowQr,
                        icon = AmIcons.ScanLine,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        } else {
            AmEmptyState(
                icon = AmIcons.ImageOff,
                title = AppStrings.QrSinContenido,
                subtitle = AppStrings.EditaElDestinoParaImportar,
                actionLabel = AppStrings.Editar,
                onAction = onEdit,
            )
        }

        // Metadatos en filas compactas (referencia MCP "Ant List"): solo
        // datos reales del dominio; el contenido decodificado se puede
        // COPIAR (portapapeles del sistema, feedback por evento) y la
        // fecha de alta es el createdAt real.
        val hasMetadata = destination.category != null ||
            destination.qr.content?.takeIf { it.isNotBlank() } != null ||
            destination.note?.takeIf { it.isNotBlank() } != null
        if (hasMetadata || destination.qr.encoded.isNotBlank()) {
            AmListBody {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(horizontal = AmSpacing.Lg, vertical = AmSpacing.Md),
                    verticalArrangement = Arrangement.spacedBy(AmSpacing.Sm),
                ) {
                    destination.qr.content?.takeIf { it.isNotBlank() }?.let {
                        AmMetaRow(
                            label = AppStrings.Contenido,
                            value = it,
                            trailing = {
                                AmCompactAction(
                                    label = AppStrings.Copiar,
                                    onClick = {
                                        copyToClipboard(it)
                                        copiedEvent = Any()
                                    },
                                    icon = AmIcons.Attachment,
                                    prominent = true,
                                )
                            },
                        )
                    }
                    destination.category?.let { AmMetaRow(label = AppStrings.Categoria, value = it) }
                    AmMetaRow(
                        label = AppStrings.FechaDeAlta,
                        value = formatDate(destination.createdAt),
                        icon = AmIcons.Calendar,
                    )
                    destination.note?.takeIf { it.isNotBlank() }?.let {
                        AmHairline()
                        AmText(it, color = AmColor.TextSecondary)
                    }
                }
            }
        }

        // Estado de sincronización del elemento (texto, no solo color).
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AmSpacing.Sm),
        ) {
            ElementSyncBadge(syncStatus, showWhenSynced = true)
            if (syncStatus == ElementSyncStatus.Dead) {
                AmText(
                    text = AppStrings.TuInformacionEstaGuardadaEnElDispositivo,
                    color = AmColor.TextSecondary,
                )
            }
        }
        if (syncStatus == ElementSyncStatus.ErrorRecoverable) {
            // El dato está a salvo localmente: el usuario puede reintentar.
            AmTextAction(label = AppStrings.Reintentar, onClick = onRetrySync)
        }

        // Reaseguro de integridad (referencia MCP): cierto en el dominio
        // — editar un destino no toca las operaciones históricas.
        AmStatusPill(
            text = AppStrings.IntegridadHistorica,
            icon = AmIcons.ShieldCheck,
            tone = AmTone.Success,
        )
    }
    if (showDeleteConfirm) {
        AmDialog(
            title = AppStrings.EliminarDestino,
            message = AppStrings.irreversibleActionWarning,
            confirmLabel = AppStrings.Eliminar,
            destructive = true,
            onConfirm = {
                showDeleteConfirm = false
                deleteDoneEvent = Any()
                onDelete()
            },
            dismissLabel = AppStrings.Cancelar,
            onDismiss = { showDeleteConfirm = false },
        )
    }
}
