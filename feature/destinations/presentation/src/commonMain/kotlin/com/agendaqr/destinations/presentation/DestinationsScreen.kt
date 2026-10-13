package com.agendaqr.destinations.presentation

import com.agendaqr.destinations.presentation.AppStrings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.agendaqr.destinations.data.SyncResource
import com.agendaqr.core.ui.components.AmSnackbarHost
import com.agendaqr.core.ui.components.AmFavoriteToggle
import com.agendaqr.core.ui.components.AmFilterChip
import com.agendaqr.core.ui.components.AmIcons
import com.agendaqr.core.ui.components.AmExtendedFab
import com.agendaqr.core.ui.components.AmNavChromeState
import com.agendaqr.core.ui.components.AmOverflowAction
import com.agendaqr.core.ui.components.AmScreenScaffold
import com.agendaqr.core.ui.components.AmSearchTrigger
import com.agendaqr.core.ui.components.AmSectionHeader
import com.agendaqr.core.ui.components.AmSkeleton
import com.agendaqr.core.ui.components.AmText
import com.agendaqr.core.ui.components.AmStatusBanner
import com.agendaqr.core.ui.components.AmEmptyState
import com.agendaqr.core.ui.components.AmLoadMoreFooter
import com.agendaqr.core.ui.components.AmListBody
import com.agendaqr.core.ui.components.AmListRow
import com.agendaqr.core.ui.components.AmTopBar
import com.agendaqr.core.ui.components.AmTone
import com.agendaqr.core.ui.theme.AmColor
import com.agendaqr.core.ui.theme.AmSpacing
import com.agendaqr.destinations.domain.Destination

@Composable
fun DestinationsScreen(
    state: DestinationsUiState,
    onAction: (DestinationAction) -> Unit,
    navChrome: AmNavChromeState? = null,
    appHosts: AppHosts? = null,
    onOpenSearch: () -> Unit = {},
    onOpenContexts: () -> Unit = {},
    onSignOut: () -> Unit = {},
    syncLookup: ElementSyncLookup = ElementSyncLookup.Empty,
) {
    var visibleCount by remember(state.visibleDestinations.size) { mutableStateOf(50) }
    val paged = state.visibleDestinations.take(visibleCount)
    // S01 V1.4 (ADR-0014): barra superior con el título, navegación raíz
    // (Inicio · Buscar · Actividades · Contextos), búsqueda como entrada y
    // FAB extendido "Añadir". El cuerpo conserva su contenido hasta la
    // pasada de pantallas.
    AmScreenScaffold(
        scrollable = true,
        topBar = {
            AmTopBar(
                title = AppStrings.AgendaQr,
                overflowActions = listOf(
                    AmOverflowAction(AppStrings.CerrarSesion, onClick = onSignOut),
                ),
                overflowLabel = AppStrings.Mas,
            )
        },
        navDestinations = navChrome?.destinations.orEmpty(),        snackbarHost = appHosts?.feedback?.let { host -> { AmSnackbarHost(host) } },
        statusContent = appHosts?.status,

        navSelectedId = navChrome?.selectedId,
        onNavSelect = { navChrome?.onSelect?.invoke(it) },
        floatingActionButton = {
            AmExtendedFab(
                label = AppStrings.Anadir,
                icon = AmIcons.Add,
                onClick = { onAction(DestinationAction.Edit(null)) },
            )
        },
    ) {
            // S01 — buscar domina visualmente (§4). La barra es la entrada
            // a S04; el listado de Inicio no se filtra aquí. El
            // disparador vive en el DS (AmSearchTrigger — nodo único con
            // rol Button); el feature ya no compone clickable crudo.
            AmSearchTrigger(
                label = AppStrings.BuscarEnAgendaQr,
                placeholder = AppStrings.searchPlaceholder,
                onClick = onOpenSearch,
            )

            // §10: los errores son recuperables y contextuales — el banner
            // queda junto a la acción que lo provocó, visible sin scroll.
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
            // S01 V1.4 (ADR-0014): Inicio es una LISTA convencional. Sin
            // tiles ni tile vivo; las entradas viven en la barra superior
            // (Cerrar sesión), el FAB (Añadir), la navegación raíz y las
            // filas. Favoritos es un filtro de la propia lista.
            Row(
                horizontalArrangement = Arrangement.spacedBy(AmSpacing.Sm),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            ) {
                AmFilterChip(
                    label = if (state.favoriteOnly) AppStrings.FavoritosActivado else AppStrings.Favoritos,
                    selected = state.favoriteOnly,
                    onClick = { onAction(DestinationAction.ToggleFavorites) },
                )
            }
            AmSectionHeader(text = AppStrings.Recientes)
            when {
                // la carga inicial de la lista se anuncia con el
                // skeleton del DS (AmSkeleton, sin bucles) — el spinner
                // con mensaje se reserva para estados en curso de una
                // acción (guardar/sincronizar). Cambio de presentación; el
                // flujo de datos no se toca.
                state.isLoading -> AmSkeleton(lines = 4)
                state.visibleDestinations.isEmpty() -> AmEmptyState(
                    icon = AmIcons.QrCode,
                    title = if (state.destinations.isEmpty()) AppStrings.AunNoHayDestinos else AppStrings.NoSeEncontraronDestinos,
                    subtitle = if (state.destinations.isEmpty()) AppStrings.emptyDestinationsHint else AppStrings.PruebaConOtraBusquedaOLimpia,
                    actionLabel = if (state.destinations.isEmpty()) AppStrings.Anadir else AppStrings.LimpiarBusqueda,
                    onAction = { if (state.destinations.isEmpty()) onAction(DestinationAction.Edit(null)) else onAction(DestinationAction.Search("")) },
                )
                else -> {
                    // Cuerpo de lista de la referencia: superficie con
                    // hairlines entre celdas contiguas; la paginación vive
                    // fuera del cuerpo.
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

@Composable
fun DestinationRow(
    destination: Destination,
    onAction: (DestinationAction) -> Unit,
    syncLookup: ElementSyncLookup = ElementSyncLookup.Empty,
    showDivider: Boolean = false,
) {
    // §12 V1.4: fila ListItem con la acción de favorito como
    // IconToggleButton de estrella (estado en semántica; la descripción
    // accesible cambia con el estado, §11).
    AmListRow(
        title = destination.name.ifBlank { AppStrings.SinNombre },
        subtitle = destinationSubtitle(destination),
        showDivider = showDivider,
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
