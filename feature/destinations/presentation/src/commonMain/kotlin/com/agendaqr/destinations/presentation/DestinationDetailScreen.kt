package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.agendaqr.destinations.data.SyncResource
import com.agendaqr.core.ui.components.AqAlert
import com.agendaqr.core.ui.components.AqBottomSheet
import com.agendaqr.core.ui.components.AqButton
import com.agendaqr.core.ui.components.AqButtonSize
import com.agendaqr.core.ui.components.AqButtonVariant
import com.agendaqr.core.ui.components.AqEmptyPlaceholder
import com.agendaqr.core.ui.components.AqIcon
import com.agendaqr.core.ui.components.AqIcons
import com.agendaqr.core.ui.components.AqMenuItem
import com.agendaqr.core.ui.components.AqModal
import com.agendaqr.core.ui.components.AqQrPreview
import com.agendaqr.core.ui.components.AqScreen
import com.agendaqr.core.ui.components.AqSectionTitle
import com.agendaqr.core.ui.components.AqSkeletonBlock
import com.agendaqr.core.ui.components.AqTag
import com.agendaqr.core.ui.components.AqText
import com.agendaqr.core.ui.components.AqTextInput
import com.agendaqr.core.ui.components.AqToastHost
import com.agendaqr.core.ui.components.AqTone
import com.agendaqr.core.ui.components.rememberAqClipboard
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqShadow
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqSquircles
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.core.ui.theme.AqWeight
import com.agendaqr.destinations.domain.Destination

/**
 * Detalle y exhibición del QR: tarjeta núcleo con el código real
 * (decodificado del dominio, nunca imagen estática), esquinas de
 * encuadre, guía de uso, botón de pantalla completa (RF-05 real),
 * metadatos en filas compactas con copiar del contenido y dock de
 * acciones reales.
 *
 * Construido exclusivamente con componentes Moon: [AqScreen], [AqModal],
 * [AqBottomSheet], [AqToastHost], [AqButton], [AqTag], [AqText], etc.
 * Sin símbolos legacy ni wrappers `Aq*`.
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
    var contentExpanded by remember { mutableStateOf(false) }
    // anuncio de eliminación confirmada y copiado (por evento) -> toasts.
    var deleteDoneEvent by remember { mutableStateOf<Any?>(null) }
    var copiedEvent by remember { mutableStateOf<Any?>(null) }
    val toastHost = appHosts?.toastHost
    val copyToClipboard = rememberAqClipboard()

    LaunchedEffect(deleteDoneEvent) {
        if (deleteDoneEvent != null) {
            toastHost?.show(AppStrings.Eliminado)
        }
    }
    LaunchedEffect(copiedEvent) {
        if (copiedEvent != null) {
            toastHost?.show(AppStrings.Copiado)
        }
    }

    AqScreen(
        title = destination.name.ifBlank { AppStrings.SinNombre },
        onBack = onBack,
        backLabel = AppStrings.Volver,
        overflowActions = listOf(
            com.agendaqr.core.ui.components.AqAppBarAction(
                AppStrings.Detalle, AqIcons.Info, { showDetailsSheet = true }),
            com.agendaqr.core.ui.components.AqAppBarAction(
                AppStrings.RegistrarOperacion, AqIcons.Register, onRegisterOperation),
            com.agendaqr.core.ui.components.AqAppBarAction(
                AppStrings.Editar, AqIcons.Edit, onEdit),
            com.agendaqr.core.ui.components.AqAppBarAction(
                AppStrings.Eliminar, AqIcons.Trash, { showDeleteConfirm = true }),
        ),
        overflowLabel = AppStrings.Mas,
        statusContent = appHosts?.status,
        bottomAction = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AqButton(
                    label = AppStrings.MostrarQr,
                    onClick = onShowQr,
                    variant = AqButtonVariant.Primary,
                    icon = AqIcons.Maximize,
                    modifier = Modifier.fillMaxWidth(),
                )
                AqButton(
                    label = AppStrings.Compartir,
                    onClick = onShare,
                    variant = AqButtonVariant.Text,
                    icon = AqIcons.Share,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        toastHost = toastHost?.let { host -> { AqToastHost(host) } },
    ) {
        // Tarjeta núcleo del QR: código real sobre lienzo claro PLANO.
        if (destination.qr.encoded.isNotBlank()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AqColor.Surface, AqSquircles.SurfaceLg)
                    .aqShadow(AqShadow.Sm, AqSquircles.SurfaceLg)
                    .padding(AqSpacing.Lg),
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
                    textAlign = TextAlign.Center,
                )
            }
        } else {
            AqEmptyPlaceholder(
                icon = AqIcons.QrCode,
                title = AppStrings.QrSinContenido,
                subtitle = AppStrings.EditaElDestinoParaImportar,
                actionLabel = AppStrings.Editar,
                onAction = onEdit,
            )
        }

        // Metadatos en filas compactas: solo datos reales del dominio.
        val hasMetadata = destination.category != null ||
            destination.qr.content?.takeIf { it.isNotBlank() } != null ||
            destination.note?.takeIf { it.isNotBlank() } != null
        if (hasMetadata || destination.qr.encoded.isNotBlank()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(AqColor.Surface, AqSquircles.SurfaceLg)
                    .aqShadow(AqShadow.Sm, AqSquircles.SurfaceLg)
                    .padding(AqSpacing.Lg),
                verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
            ) {
                // Contenido decodificado: seleccionable y copiable.
                destination.qr.content?.takeIf { it.isNotBlank() }?.let { content ->
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
                            AqButton(
                                label = AppStrings.Copiar,
                                onClick = {
                                    copyToClipboard(content)
                                    copiedEvent = Any()
                                },
                                variant = AqButtonVariant.Text,
                                icon = AqIcons.Copy,
                                size = AqButtonSize.Small,
                            )
                            AqButton(
                                label = if (contentExpanded) AppStrings.VerMenos else AppStrings.VerMas,
                                onClick = { contentExpanded = !contentExpanded },
                                variant = AqButtonVariant.Text,
                                size = AqButtonSize.Small,
                            )
                        }
                    }
                }
                destination.category?.let {
                    AqMetaLine(label = AppStrings.Categoria, value = it)
                }
                AqMetaLine(
                    label = AppStrings.FechaDeAlta,
                    value = formatDate(destination.createdAt),
                    leadingIcon = AqIcons.Calendar,
                )
                destination.note?.takeIf { it.isNotBlank() }?.let {
                    AqHairline()
                    AqText(it, color = AqColor.TextSecondary)
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
            AqButton(
                label = AppStrings.Reintentar,
                onClick = onRetrySync,
                variant = AqButtonVariant.Text,
            )
        }

        // Reaseguro de integridad: cierto en el dominio.
        AqTag(
            text = AppStrings.IntegridadHistorica,
            tone = AqTone.Success,
        )
    }

    if (showDeleteConfirm) {
        AqModal(
            title = AppStrings.EliminarDestino,
            dismissLabel = AppStrings.Cancelar,
            confirmLabel = AppStrings.Eliminar,
            destructive = true,
            onConfirm = {
                showDeleteConfirm = false
                deleteDoneEvent = Any()
                onDelete()
            },
            onDismissRequest = { showDeleteConfirm = false },
        )
    }

    if (showDetailsSheet) {
        AqBottomSheet(
            title = AppStrings.Detalle,
            onDismissRequest = { showDetailsSheet = false },
            dismissDescription = AppStrings.Cerrar,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Md)) {
                destination.category?.let {
                    AqMetaLine(label = AppStrings.Categoria, value = it)
                }
                AqMetaLine(
                    label = AppStrings.FechaDeAlta,
                    value = formatDate(destination.createdAt),
                    leadingIcon = AqIcons.Calendar,
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

/** Fila de metadato (etiqueta + valor) construida con piezas Moon. */
@Composable
private fun AqMetaLine(
    label: String,
    value: String,
    leadingIcon: com.agendaqr.core.ui.components.AqIcons.() -> Unit? = null,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
    ) {
        leadingIcon?.invoke()
        AqText(
            text = label,
            size = AqType.Body,
            color = com.agendaqr.core.ui.theme.AqColor.TextSecondary,
            modifier = Modifier.weight(0f, fill = false),
        )
        AqText(
            text = value,
            size = AqType.Body,
            color = com.agendaqr.core.ui.theme.AqColor.TextPrimary,
            modifier = Modifier.weight(1f, fill = false),
        )
    }
}
