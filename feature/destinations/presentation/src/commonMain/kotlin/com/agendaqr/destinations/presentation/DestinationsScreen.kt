package com.agendaqr.destinations.presentation

import com.agendaqr.destinations.presentation.AppStrings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import com.agendaqr.destinations.data.SyncResource
import com.agendaqr.core.ui.components.XauxaSnackbarHost
import com.agendaqr.core.ui.components.XauxaFavoriteToggle
import com.agendaqr.core.ui.components.XauxaFilterChip
import com.agendaqr.core.ui.components.XauxaIcons
import com.agendaqr.core.ui.components.XauxaExtendedFab
import com.agendaqr.core.ui.components.XauxaNavChromeState
import com.agendaqr.core.ui.components.XauxaOverflowAction
import com.agendaqr.core.ui.components.XauxaScreenScaffold
import com.agendaqr.core.ui.components.XauxaSearchTrigger
import com.agendaqr.core.ui.components.XauxaSectionHeader
import com.agendaqr.core.ui.components.XauxaSkeleton
import com.agendaqr.core.ui.components.XauxaText
import com.agendaqr.core.ui.components.XauxaStatusBanner
import com.agendaqr.core.ui.components.XauxaLoading
import com.agendaqr.core.ui.components.XauxaEmptyState
import com.agendaqr.core.ui.components.XauxaCategoryChip
import com.agendaqr.core.ui.components.XauxaLoadMoreFooter
import com.agendaqr.core.ui.components.XauxaListRow
import com.agendaqr.core.ui.components.XauxaTopBar
import com.agendaqr.core.ui.components.XauxaTone
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.core.ui.theme.XauxaSpacing
import com.agendaqr.destinations.domain.Destination

@Composable
fun DestinationsScreen(
    state: DestinationsUiState,
    onAction: (DestinationAction) -> Unit,
    navChrome: XauxaNavChromeState? = null,
    appHosts: AppHosts? = null,
    onOpenOperations: () -> Unit = {},
    onOpenSearch: () -> Unit = {},
    onOpenContexts: () -> Unit = {},
    onSignOut: () -> Unit = {},
    /** S01 (tile vivo): abre la actividad reciente del tile en su detalle. */
    onOpenOperation: (String) -> Unit = {},
    syncLookup: ElementSyncLookup = ElementSyncLookup.Empty,
) {
    var visibleCount by remember(state.visibleDestinations.size) { mutableStateOf(50) }
    val paged = state.visibleDestinations.take(visibleCount)
    // S01 V1.2 (ADR-0012): barra superior con el título, navegación raíz
    // (Inicio · Buscar · Actividades · Contextos), búsqueda como entrada y
    // FAB extendido "Añadir". El cuerpo conserva su contenido hasta la
    // pasada de pantallas.
    XauxaScreenScaffold(
        scrollable = true,
        topBar = {
            XauxaTopBar(
                title = AppStrings.AgendaQr,
                overflowActions = listOf(
                    XauxaOverflowAction(AppStrings.CerrarSesion, onClick = onSignOut),
                ),
                overflowLabel = AppStrings.Mas,
            )
        },
        navDestinations = navChrome?.destinations.orEmpty(),        snackbarHost = appHosts?.feedback?.let { host -> { XauxaSnackbarHost(host) } },
        statusContent = appHosts?.status,

        navSelectedId = navChrome?.selectedId,
        onNavSelect = { navChrome?.onSelect?.invoke(it) },
        floatingActionButton = {
            XauxaExtendedFab(
                label = AppStrings.Anadir,
                icon = XauxaIcons.Add,
                onClick = { onAction(DestinationAction.Edit(null)) },
            )
        },
    ) {
            // S01 — buscar domina visualmente (§4). La barra es la entrada
            // a S04; el listado de Inicio no se filtra aquí. El
            // disparador vive en el DS (XauxaSearchTrigger — nodo único con
            // rol Button); el feature ya no compone clickable crudo.
            XauxaSearchTrigger(
                label = AppStrings.BuscarEnAgendaQr,
                placeholder = AppStrings.searchPlaceholder,
                onClick = onOpenSearch,
            )

            // §10: los errores son recuperables y contextuales — el banner
            // queda junto a la acción que lo provocó, visible sin scroll.
            state.error?.let { error ->
                XauxaStatusBanner(
                    error.display(),
                    tone = XauxaTone.Danger,
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
            // S01 V1.2 (ADR-0012): Inicio es una LISTA convencional. Sin
            // tiles ni tile vivo; las entradas viven en la barra superior
            // (Cerrar sesión), el FAB (Añadir), la navegación raíz y las
            // filas. Favoritos es un filtro de la propia lista.
            Row(
                horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            ) {
                state.category?.let { XauxaCategoryChip(it) }
                XauxaFilterChip(
                    label = if (state.favoriteOnly) AppStrings.FavoritosActivado else AppStrings.Favoritos,
                    selected = state.favoriteOnly,
                    onClick = { onAction(DestinationAction.ToggleFavorites) },
                )
            }
            XauxaSectionHeader(text = AppStrings.Recientes)
            when {
                // la carga inicial de la lista se anuncia con el
                // skeleton del DS (XauxaSkeleton, sin bucles) — el spinner
                // con mensaje se reserva para estados en curso de una
                // acción (guardar/sincronizar). Cambio de presentación; el
                // flujo de datos no se toca.
                state.isLoading -> XauxaSkeleton(lines = 4)
                state.visibleDestinations.isEmpty() -> XauxaEmptyState(
                    title = if (state.destinations.isEmpty()) AppStrings.AunNoHayDestinos else AppStrings.NoSeEncontraronDestinos,
                    subtitle = if (state.destinations.isEmpty()) AppStrings.emptyDestinationsHint else AppStrings.PruebaConOtraBusquedaOLimpia,
                    actionLabel = if (state.destinations.isEmpty()) AppStrings.Anadir else AppStrings.LimpiarBusqueda,
                    onAction = { if (state.destinations.isEmpty()) onAction(DestinationAction.Edit(null)) else onAction(DestinationAction.Search("")) },
                )
                else -> {
                    Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
                        paged.forEach { destination ->
                            DestinationRow(destination, onAction, syncLookup)
                        }
                        if (paged.size < state.visibleDestinations.size) {
                            XauxaLoadMoreFooter(
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

@Composable
fun DestinationRow(
    destination: Destination,
    onAction: (DestinationAction) -> Unit,
    syncLookup: ElementSyncLookup = ElementSyncLookup.Empty,
) {
    // §12 V1.2: fila ListItem con la acción de favorito como
    // IconToggleButton de estrella (estado en semántica; la descripción
    // accesible cambia con el estado, §11).
    XauxaListRow(
        title = destination.name.ifBlank { AppStrings.SinNombre },
        subtitle = destinationSubtitle(destination),
        onClick = { onAction(DestinationAction.Open(destination.id)) },
        // Badge bajo el texto (línea propia vía `meta`): no compite por el
        // ancho con la acción en pantallas estrechas.
        meta = {
            // Estado de sincronización por elemento (con texto).
            ElementSyncBadge(
                syncLookup.status(SyncResource.DESTINATION, destination.id),
            )
        },
        trailing = {
            XauxaFavoriteToggle(
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
 * (defecto abierto registrado; decisión de esta pasada, no de spec).
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
