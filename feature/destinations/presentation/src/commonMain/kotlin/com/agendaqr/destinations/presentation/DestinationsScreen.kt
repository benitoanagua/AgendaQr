package com.agendaqr.destinations.presentation

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.agendaqr.destinations.data.SyncResource
import com.agendaqr.core.ui.components.AmBadge
import com.agendaqr.core.ui.components.AmCompactAction
import com.agendaqr.core.ui.components.AmFavoriteToggle
import com.agendaqr.core.ui.components.AmFilterChip
import com.agendaqr.core.ui.components.AmIcon
import com.agendaqr.core.ui.components.AmIconBadge
import com.agendaqr.core.ui.components.AmIcons
import com.agendaqr.core.ui.components.AmExtendedFab
import com.agendaqr.core.ui.components.AmListRow
import com.agendaqr.core.ui.components.AmListBody
import com.agendaqr.core.ui.components.AmNavChromeState
import com.agendaqr.core.ui.components.AmOverflowAction
import com.agendaqr.core.ui.components.AmScreenScaffold
import com.agendaqr.core.ui.components.AmSearchTrigger
import com.agendaqr.core.ui.components.AmSecondaryButton
import com.agendaqr.core.ui.components.AmSectionHeader
import com.agendaqr.core.ui.components.AmSkeleton
import com.agendaqr.core.ui.components.AmSnackbarHost
import com.agendaqr.core.ui.components.AmStatusPill
import com.agendaqr.core.ui.components.AmText
import com.agendaqr.core.ui.components.AmTone
import com.agendaqr.core.ui.components.AmStatusBanner
import com.agendaqr.core.ui.components.AmEmptyState
import com.agendaqr.core.ui.components.AmLoadMoreFooter
import com.agendaqr.core.ui.components.AmTopBar
import com.agendaqr.core.ui.theme.AmColor
import com.agendaqr.core.ui.theme.AmMetrics
import com.agendaqr.core.ui.theme.AmSpacing
import com.agendaqr.core.ui.theme.AmType
import com.agendaqr.destinations.domain.Destination

/**
 * S01 — Inicio (V1.4; composición del diseño "Destinos QR - Agenda" del
 * proyecto Stitch "Ant Design Mobile App", ADR-0015): tarjeta de
 * garantía local con acciones rápidas, pestañas cápsula con conteo
 * real, carrusel de destinos frecuentes y lista agrupada con "Ver QR"
 * por fila. Todas las acciones son reales: S02 (traer QR), pantalla
 * completa del código (RF-05) y favoritos del dominio.
 */
@Composable
fun DestinationsScreen(
    state: DestinationsUiState,
    onAction: (DestinationAction) -> Unit,
    navChrome: AmNavChromeState? = null,
    appHosts: AppHosts? = null,
    onOpenSearch: () -> Unit = {},
    onOpenContexts: () -> Unit = {},
    onLock: () -> Unit = {},
    onOpenAccount: () -> Unit = {},
    syncLookup: ElementSyncLookup = ElementSyncLookup.Empty,
    /** Acción rápida / FAB: traer un QR (S02, cámara y galería). */
    onScanQr: () -> Unit = { onAction(DestinationAction.Edit(null)) },
) {
    var visibleCount by remember(state.visibleDestinations.size) { mutableStateOf(50) }
    val paged = state.visibleDestinations.take(visibleCount)
    val favorites = remember(state.destinations) {
        state.destinations.filter { it.favorite }
    }

    AmScreenScaffold(
        scrollable = true,
        topBar = {
            AmTopBar(
                title = AppStrings.AgendaQr,
                overflowActions = listOf(
                    AmOverflowAction(AppStrings.CuentaYRespaldo, onClick = onOpenAccount),
                    AmOverflowAction(AppStrings.Bloquear, onClick = onLock),
                ),
                overflowLabel = AppStrings.Mas,
            )
        },
        navDestinations = navChrome?.destinations.orEmpty(),
        snackbarHost = appHosts?.feedback?.let { host -> { AmSnackbarHost(host) } },
        statusContent = appHosts?.status,

        navSelectedId = navChrome?.selectedId,
        onNavSelect = { navChrome?.onSelect?.invoke(it) },
        floatingActionButton = {
            AmExtendedFab(
                label = AppStrings.AnadirDestino,
                icon = AmIcons.Add,
                onClick = onScanQr,
            )
        },
        verticalArrangement = Arrangement.spacedBy(AmSpacing.Lg),
    ) {
        // §10: errores recuperables y contextuales, visibles sin scroll.
        state.error?.let { error ->
            AmStatusBanner(
                error.display(),
                tone = AmTone.Danger,
                actionLabel = error.action.label,
                // REINTENTAR re-ejecuta la operación fallida en el VM.
                onAction = {
                    onAction(
                        if (error.action == ErrorAction.Retry) DestinationAction.RetryFailed
                        else DestinationAction.ClearError,
                    )
                },
                onDismiss = { onAction(DestinationAction.ClearError) },
                dismissLabel = AppStrings.Descartar,
            )
        }

        // Búsqueda dominante (entrada a S04).
        AmSearchTrigger(
            label = AppStrings.BuscarEnAgendaQr,
            placeholder = AppStrings.searchPlaceholder,
            onClick = onOpenSearch,
        )

        // Tarjeta de garantía local (referencia MCP): reaseguro SIEMPRE
        // cierto (la agenda vive en el dispositivo) + acciones rápidas
        // reales hacia S02.
        AmListBody {
            Column(
                modifier = Modifier.fillMaxWidth().padding(AmSpacing.Md),
                verticalArrangement = Arrangement.spacedBy(AmSpacing.Sm),
            ) {
            // Jerarquía: escanear es la acción insignia del producto
            // (variante sólida); importar es su alternativa tonal.
            // Apiladas a ancho completo: las etiquetas completas caben sin
            // elipsis (la fila comprimía «Importar imagen» en 360 dp).
            AmSecondaryButton(
                label = AppStrings.EscanearQr,
                onClick = onScanQr,
                icon = AmIcons.ScanLine,
                solid = true,
                modifier = Modifier.fillMaxWidth(),
            )
            AmSecondaryButton(
                label = AppStrings.ImportarImagen,
                onClick = onScanQr,
                icon = AmIcons.Gallery,
                modifier = Modifier.fillMaxWidth(),
            )
            }
        }

        // Pestañas cápsula (referencia MCP): filtros reales con conteo.
        Row(horizontalArrangement = Arrangement.spacedBy(AmSpacing.Sm)) {
            AmFilterChip(
                label = AppStrings.Todos,
                selected = !state.favoriteOnly,
                onClick = { onAction(DestinationAction.ToggleFavorites) },
                count = state.destinations.size,
            )
            AmFilterChip(
                label = if (state.favoriteOnly) AppStrings.FavoritosActivado else AppStrings.Favoritos,
                selected = state.favoriteOnly,
                onClick = { onAction(DestinationAction.ToggleFavorites) },
                count = favorites.size,
                icon = AmIcons.Favorite,
            )
        }

        // "Destinos frecuentes" (referencia MCP): carrusel horizontal de
        // los favoritos reales del dominio.
        if (favorites.isNotEmpty() && !state.favoriteOnly) {
            Column(verticalArrangement = Arrangement.spacedBy(AmSpacing.Sm)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(AmSpacing.Xs),
                    ) {
                        AmIcon(
                            imageVector = AmIcons.Favorite,
                            contentDescription = null,
                            size = AmMetrics.IconSizeCompact,
                            tint = AmColor.AccentText,
                        )
                        AmSectionHeader(text = AppStrings.DestinosFrecuentes)
                    }
                    AmText(
                        AppStrings.DeslizaParaVerMas,
                        color = AmColor.TextTertiary,
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(AmSpacing.Md),
                ) {
                    favorites.forEach { destination ->
                        FavoriteDestinationCard(
                            destination = destination,
                            onClick = { onAction(DestinationAction.Open(destination.id)) },
                        )
                    }
                }
            }
        }

        // "Todos los destinos": lista agrupada con hairlines y "Ver QR".
        Column(verticalArrangement = Arrangement.spacedBy(AmSpacing.Sm)) {
            AmSectionHeader(text = AppStrings.TodosLosDestinos)
            when {
                // la carga inicial se anuncia con el skeleton del DS.
                state.isLoading -> AmSkeleton(lines = 4)
                state.visibleDestinations.isEmpty() -> AmEmptyState(
                    icon = AmIcons.QrCode,
                    title = if (state.destinations.isEmpty()) AppStrings.AunNoHayDestinos else AppStrings.NoSeEncontraronDestinos,
                    subtitle = if (state.destinations.isEmpty()) AppStrings.emptyDestinationsHint else AppStrings.PruebaConOtraBusquedaOLimpia,
                    actionLabel = if (state.destinations.isEmpty()) AppStrings.Anadir else AppStrings.LimpiarBusqueda,
                    onAction = { if (state.destinations.isEmpty()) onAction(DestinationAction.Edit(null)) else onAction(DestinationAction.Search("")) },
                )
                else -> {
                    AmListBody {
                        paged.forEachIndexed { index, destination ->
                            DestinationRow(
                                destination,
                                onAction,
                                syncLookup,
                                showDivider = index < paged.lastIndex,
                            )
                        }
                    }
                    if (paged.size < state.visibleDestinations.size) {
                        AmLoadMoreFooter(
                            onLoadMore = { visibleCount = (visibleCount + 50).coerceAtMost(state.visibleDestinations.size) },
                            loadMoreLabel = AppStrings.CargarMas,
                            loadingLabel = AppStrings.Cargando,
                            endLabel = AppStrings.NoHayMasElementos,
                        )
                    }
                }
            }
        }
    }
}

/**
 * Mini-tarjeta del carrusel de frecuentes (referencia MCP w-48): tile de
 * identidad, nombre, metadato y categoría real. Toda la interacción
 * vive en la fila; el toque abre el detalle.
 */
@Composable
private fun FavoriteDestinationCard(
    destination: Destination,
    onClick: () -> Unit,
) {
    // La superficie presionable vive en el DS (feature/ sin clickable
    // crudo): tarjeta compacta de ancho de token.
    com.agendaqr.core.ui.components.AmTappableCard(
        onClick = onClick,
        modifier = Modifier.width(AmMetrics.FavCardWidth),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(AmSpacing.Sm)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                AmIconBadge(icon = AmIcons.QrCode, tone = AmTone.Info)
                destination.category?.let {
                    AmBadge(text = it, tone = AmTone.Neutral)
                }
            }
            Column {
                AmText(
                    destination.name.ifBlank { AppStrings.SinNombre },
                    size = AmType.RowTitle,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                )
                AmText(
                    destinationSubtitle(destination) ?: "",
                    color = AmColor.TextSecondary,
                )
            }
        }
    }
}

@Composable
fun DestinationRow(
    destination: Destination,
    onAction: (DestinationAction) -> Unit,
    syncLookup: ElementSyncLookup = ElementSyncLookup.Empty,
    showDivider: Boolean = false,
) {
    // Fila del diseño MCP: miniatura QR a la izquierda, título con
    // identidad, contenido decodificado como evidencia y "Ver QR" +
    // favorito a la derecha.
    AmListRow(
        title = destination.name.ifBlank { AppStrings.SinNombre },
        subtitle = destinationSubtitle(destination),
        showDivider = showDivider,
        onClick = { onAction(DestinationAction.Open(destination.id)) },
        leading = {
            AmIconBadge(icon = AmIcons.QrCode, tone = AmTone.Neutral)
        },
        meta = {
            Column(verticalArrangement = Arrangement.spacedBy(AmSpacing.Xs)) {
                destination.qr.content?.takeIf { it.isNotBlank() }?.let {
                    AmText(it, color = AmColor.AccentText)
                }
                ElementSyncBadge(
                    syncLookup.status(SyncResource.DESTINATION, destination.id),
                )
            }
        },
        trailing = {
            AmFavoriteToggle(
                favorite = destination.favorite,
                contentDescription = if (destination.favorite) {
                    AppStrings.QuitarFavorito
                } else {
                    AppStrings.MarcarFavorito
                },
                onCheckedChange = { onAction(DestinationAction.ToggleFavorite(destination)) },
            )
        },
    )
}

/**
 * Subtítulo de la fila del destino: nota/categoría si existen; si el
 * nombre es genérico ("Sin nombre" o "QR importado"), ORIGEN + FECHA —
 * dos destinos con nombre genérico quedaban indistinguibles en la lista
 * (decisión de implementación, no de spec).
 */
internal fun destinationSubtitle(destination: Destination): String? {
    val generic = destination.name.isBlank() ||
        destination.name.startsWith(AppStrings.QrImportado)
    return when {
        generic && destination.name.isBlank() ->
            AppStrings.AnadidoEl + formatDate(destination.createdAt)
        generic -> AppStrings.ImportadoEl + formatDate(destination.createdAt)
        else -> destination.note ?: destination.category
    }
}
