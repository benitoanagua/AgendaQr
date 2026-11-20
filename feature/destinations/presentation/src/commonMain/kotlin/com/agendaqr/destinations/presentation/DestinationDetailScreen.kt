package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.AqSnackbarHost
import com.agendaqr.core.ui.components.AqBottomSheet
import com.agendaqr.core.ui.components.AqBottomActionDock
import com.agendaqr.core.ui.components.AqFeedbackEvent
import com.agendaqr.core.ui.components.AqScreenScaffold
import com.agendaqr.core.ui.components.AqTooltip
import com.agendaqr.core.ui.components.AqTopBar
import com.agendaqr.core.ui.components.AqOverflowAction
import com.agendaqr.core.ui.components.AqCompactAction
import com.agendaqr.core.ui.components.AqDialog
import com.agendaqr.core.ui.components.AqEmptyState
import com.agendaqr.core.ui.components.AqHairline
import com.agendaqr.core.ui.components.AqIcon
import com.agendaqr.core.ui.components.AqIcons
import com.agendaqr.core.ui.components.AqCard
import com.agendaqr.core.ui.components.AqListBody
import com.agendaqr.core.ui.components.AqMetaRow
import com.agendaqr.core.ui.components.AqQrPreview
import com.agendaqr.core.ui.components.AqSecondaryButton
import com.agendaqr.core.ui.components.AqTappableCard
import com.agendaqr.core.ui.components.AqStatusPill
import com.agendaqr.core.ui.components.AqText
import com.agendaqr.core.ui.components.AqTextAction
import com.agendaqr.core.ui.components.AqTone
import com.agendaqr.core.ui.components.AqPrimaryButton
import com.agendaqr.core.ui.components.rememberAqClipboard
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.destinations.domain.Destination

/**
 * Detalle y exhibición del QR: tarjeta núcleo con el código real
 * (decodificado del dominio, nunca imagen estática), esquinas de
 * encuadre, guía de uso, botón de pantalla completa (RF-05 real),
 * metadatos en filas compactas con copiar del contenido y dock de
 * acciones reales. El banner "Local" del diseño vive en el host de
 * estado de la app (no por pantalla).
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
    var showDetailsSheet by remember { mutableStateOf(false) }
    var showCopyTooltip by remember { mutableStateOf(false) }
    // anuncio de eliminación confirmada (por evento) y copiado (por evento).
    var deleteDoneEvent by remember { mutableStateOf<Any?>(null) }
    var copiedEvent by remember { mutableStateOf<Any?>(null) }
    val copyToClipboard = com.agendaqr.core.ui.components.rememberAqClipboard()
    AqFeedbackEvent(
        event = deleteDoneEvent,
        message = AppStrings.Eliminado,
        feedbackHost = appHosts?.feedback,
    )
    AqFeedbackEvent(
        event = copiedEvent,
        message = AppStrings.Copiado,
        feedbackHost = appHosts?.feedback,
    )
    // U-04/D3: la tarea central es MOSTRAR el QR (primaria); Compartir
    // es secundaria. Registrar, Editar y Eliminar viven en "…" (la
    // eliminación mantiene su diálogo destructivo). "Registrar actividad"
    // abre un formulario vacío: no promete asociación al destino.
    AqScreenScaffold(
        scrollable = true,
        topBar = {
            AqTopBar(
                title = destination.name.ifBlank { AppStrings.SinNombre },
                onBack = onBack,
                backLabel = AppStrings.Volver,
                overflowActions = listOf(
                    AqOverflowAction(AppStrings.Detalle, { showDetailsSheet = true }, AqIcons.Info),
                    AqOverflowAction(AppStrings.RegistrarOperacion, onRegisterOperation, AqIcons.Register),
                    AqOverflowAction(AppStrings.Editar, onEdit, AqIcons.Edit),
                    AqOverflowAction(AppStrings.Eliminar, { showDeleteConfirm = true }, AqIcons.Trash),
                ),
                overflowLabel = AppStrings.Mas,
            )
        },
        snackbarHost = appHosts?.feedback?.let { host -> { AqSnackbarHost(host) } },
        statusContent = appHosts?.status,
        verticalArrangement = Arrangement.spacedBy(AqSpacing.Lg),
        bottomAction = {
            // Dock (U-04, D3): exactamente una acción primaria — Mostrar
            // QR a pantalla completa — más Compartir secundaria. El dock
            // del DS respeta insets de navegación/IME y el margen de
            // pantalla; la pantalla solo declara las acciones.
            AqBottomActionDock {
                AqPrimaryButton(
                    label = AppStrings.MostrarQr,
                    onClick = onShowQr,
                    icon = AqIcons.Maximize,
                    modifier = Modifier.fillMaxWidth(),
                )
                AqSecondaryButton(
                    label = AppStrings.Compartir,
                    onClick = onShare,
                    icon = AqIcons.Share,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
    ) {
        // Tarjeta núcleo del QR (U-04): código real sobre lienzo claro
        // PLANO (el marco de escáner queda para la cámara) + guía de uso.
        // La tarjeta también es tocable con la misma acción de Mostrar
        // (semántica de botón); el dock conserva la primaria visible.
        if (destination.qr.encoded.isNotBlank()) {
            AqTappableCard(onClick = onShowQr) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(AqSpacing.Md),
                ) {
                    AqQrPreview(
                        encodedQr = destination.qr.encoded,
                        size = AqMetrics.QrPreviewSize,
                        unavailableMessage = AppStrings.QrNoSePudoMostrar,
                    )
                    AqText(
                        AppStrings.MuestraEsteCodigo,
                        color = AqColor.TextSecondary,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    )
                }
            }
        } else {
            AqEmptyState(
                icon = AqIcons.ImageOff,
                title = AppStrings.QrSinContenido,
                subtitle = AppStrings.EditaElDestinoParaImportar,
                actionLabel = AppStrings.Editar,
                onAction = onEdit,
            )
        }

        // Metadatos en filas compactas (patrón de detalle de comprobante, ADR-0021): solo
        // datos reales del dominio; el contenido decodificado se puede
        // COPIAR (portapapeles del sistema, feedback por evento) y la
        // fecha de alta es el createdAt real.
        val hasMetadata = destination.category != null ||
            destination.qr.content?.takeIf { it.isNotBlank() } != null ||
            destination.note?.takeIf { it.isNotBlank() } != null
        if (hasMetadata || destination.qr.encoded.isNotBlank()) {
            AqCard {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
                ) {
                    // Contenido decodificado (U-12, D9): bloque de ancho
                    // completo alineado a la izquierda, máx. 3 líneas con
                    // "Ver más", seleccionable y copiable (no una fila de
                    // valor alineada a la derecha).
                    destination.qr.content?.takeIf { it.isNotBlank() }?.let { content ->
                        var contentExpanded by remember { mutableStateOf(false) }
                        Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Xs)) {
                            AqText(
                                AppStrings.Contenido,
                                color = AqColor.TextSecondary,
                                size = AqType.BodySmall,
                            )
                            androidx.compose.foundation.text.selection.SelectionContainer {
                                AqText(
                                    content,
                                    maxLines = if (contentExpanded) Int.MAX_VALUE else 3,
                                )
                            }
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                AqTooltip(
                                    label = AppStrings.Copiar,
                                    isVisible = showCopyTooltip,
                                    onLongClick = { showCopyTooltip = !showCopyTooltip },
                                ) {
                                    AqCompactAction(
                                        label = AppStrings.Copiar,
                                        onClick = {
                                            copyToClipboard(content)
                                            copiedEvent = Any()
                                        },
                                        icon = AqIcons.Copy,
                                        prominent = true,
                                    )
                                }
                                AqTextAction(
                                    label = if (contentExpanded) AppStrings.VerMenos else AppStrings.VerMas,
                                    onClick = { contentExpanded = !contentExpanded },
                                )
                            }
                        }
                    }
                    destination.category?.let { AqMetaRow(label = AppStrings.Categoria, value = it) }
                    AqMetaRow(
                        label = AppStrings.FechaDeAlta,
                        value = formatDate(destination.createdAt),
                        icon = AqIcons.Calendar,
                    )
                    destination.note?.takeIf { it.isNotBlank() }?.let {
                        AqHairline()
                        AqText(it, color = AqColor.TextSecondary)
                    }
                }
            }
        }

        // Estado de sincronización del elemento (texto, no solo color).
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
        ) {
            ElementSyncBadge(syncStatus, showWhenSynced = true)
            if (syncStatus == ElementSyncStatus.Dead) {
                AqText(
                    text = AppStrings.TuInformacionEstaGuardadaEnElDispositivo,
                    color = AqColor.TextSecondary,
                )
            }
        }
        if (syncStatus == ElementSyncStatus.ErrorRecoverable) {
            // El dato está a salvo localmente: el usuario puede reintentar.
            AqTextAction(label = AppStrings.Reintentar, onClick = onRetrySync)
        }

        // Reaseguro de integridad: cierto en el dominio
        // — editar un destino no toca las operaciones históricas.
        AqStatusPill(
            text = AppStrings.IntegridadHistorica,
            icon = AqIcons.ShieldCheck,
            tone = AqTone.Success,
        )
    }
    if (showDeleteConfirm) {
        AqDialog(
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

    if (showDetailsSheet) {
        AqBottomSheet(
            title = AppStrings.Detalle,
            onDismissRequest = { showDetailsSheet = false },
            dismissDescription = AppStrings.Cerrar,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Md)) {
                destination.category?.let { AqMetaRow(label = AppStrings.Categoria, value = it) }
                AqMetaRow(
                    label = AppStrings.FechaDeAlta,
                    value = formatDate(destination.createdAt),
                    icon = AqIcons.Calendar,
                )
                destination.qr.content?.takeIf { it.isNotBlank() }?.let { content ->
                    Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Xs)) {
                        AqText(
                            AppStrings.Contenido,
                            color = AqColor.TextSecondary,
                            size = AqType.BodySmall,
                        )
                        AqText(content)
                    }
                }
                destination.note?.takeIf { it.isNotBlank() }?.let {
                    AqText(it, color = AqColor.TextSecondary)
                }
            }
        }
    }
}
