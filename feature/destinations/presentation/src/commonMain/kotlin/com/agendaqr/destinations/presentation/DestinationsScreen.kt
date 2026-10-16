package com.agendaqr.destinations.presentation

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
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
import com.agendaqr.core.ui.components.AmDialog
import com.agendaqr.core.ui.components.AmDialogList
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
import com.agendaqr.core.ui.components.AmSectionHeader
import com.agendaqr.core.ui.components.AmSkeleton
import com.agendaqr.core.ui.components.AmSnackbarHost
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
 * S01 — Inicio (V1.4): tarjeta de garantía local con acciones rápidas,
 * pestañas cápsula con conteo real, carrusel de favoritos y lista
 * agrupada con "Ver QR" por fila. Todas las acciones son reales: S02
 * (traer QR), pantalla completa del código (RF-05) y favoritos del
 * dominio.
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
    /** Hub "¿Qué quieres añadir?": registrar actividad (S07). */
    onRegisterOperation: () -> Unit = {},
    /** Hub "¿Qué quieres añadir?": crear contexto (diálogo existente). */
    onCreateContext: (name: String, note: String?) -> Unit = { _, _ -> },
) {
    var showAddHub by remember { mutableStateOf(false) }
    var showCreateContext by remember { mutableStateOf(false) }
    var visibleCount by remember(state.visibleDestinations.size) { mutableStateOf(50) }
    val paged = state.visibleDestinations.take(visibleCount)
    val favorites = state.favorites

    AmScreenScaffold(
        scrollable = state.destinations.isNotEmpty(),
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
        floatingActionButton = if (state.destinations.isEmpty() && state.query.isBlank()) {
            null
        } else {
            {
                // U-10/D8: el FAB abre el hub "¿Qué quieres añadir?" con las 3
                // creaciones (la especificación S01); sin acciones duplicadas.
                AmExtendedFab(
                    label = AppStrings.Anadir,
                    icon = AmIcons.Add,
                    onClick = { showAddHub = true },
                )
            }
        },
        verticalArrangement = Arrangement.spacedBy(AmSpacing.Lg),
    ) {
        // §10: errores recuperables y contextuales, visibles sin scroll.
        state.error?.let { error ->
            val (bannerAction, bannerDismiss) = error.bannerActions(
                onRetry = { onAction(DestinationAction.RetryFailed) },
                onClear = { onAction(DestinationAction.ClearError) },
            )
            AmStatusBanner(
                title = error.what,
                message = error.dataStatus,
                tone = AmTone.Danger,
                action = bannerAction,
                dismiss = bannerDismiss,
            )
        }

        // Búsqueda dominante (entrada a S04).
        AmSearchTrigger(
            label = AppStrings.BuscarEnAgendaQr,
            placeholder = AppStrings.searchPlaceholder,
            onClick = onOpenSearch,
        )

        // Pestañas cápsula: filtros reales con conteo.
        Row(horizontalArrangement = Arrangement.spacedBy(AmSpacing.Sm)) {
            AmFilterChip(
                label = AppStrings.Todos,
                selected = !state.favoriteOnly,
                onClick = { onAction(DestinationAction.SetFavoritesOnly(false)) },
                count = state.destinations.size,
                stateDescription = AppStrings.Seleccionado,
            )
            AmFilterChip(
                label = AppStrings.Favoritos,
                selected = state.favoriteOnly,
                onClick = { onAction(DestinationAction.SetFavoritesOnly(true)) },
                count = favorites.size,
                icon = AmIcons.Favorite,
                stateDescription = AppStrings.Seleccionado,
            )
        }

        // Favoritos (U-10: es lo que son, no "frecuentes"): carrusel
        // horizontal; la siguiente tarjeta asoma como indicio (sin
        // instrucción de deslizamiento).
        if (state.showsFavoritesCarousel) {
            Column(verticalArrangement = Arrangement.spacedBy(AmSpacing.Sm)) {
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
                    AmSectionHeader(text = AppStrings.Favoritos)
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

        // Sección inferior: los favoritos ya viven en el carrusel, así que el
        // encabezado es "Otros destinos" solo en ese caso; "Todos los destinos"
        // cuando no hay carrusel. Cuando el carrusel muestra TODO el contenido
        // (todos son favoritos) la sección no se renderiza: un estado vacío
        // aquí sería un mensaje falso.
        val showsFavoritesCarousel = state.showsFavoritesCarousel
        val showsLowerSection = state.isLoading ||
            state.destinations.isEmpty() ||
            state.favoriteOnly ||
            state.hasOtherDestinations ||
            state.query.isNotBlank()
        if (showsLowerSection) {
            Column(verticalArrangement = Arrangement.spacedBy(AmSpacing.Sm)) {
                if (!state.destinations.isEmpty()) {
                    AmSectionHeader(
                        text = if (showsFavoritesCarousel) AppStrings.OtrosDestinos else AppStrings.TodosLosDestinos,
                    )
                }
                when {
                    // la carga inicial se anuncia con el skeleton del DS.
                    state.isLoading -> AmSkeleton(lines = 4)
                    state.destinations.isEmpty() -> AmEmptyState(
                        icon = AmIcons.QrCode,
                        title = AppStrings.AunNoHayDestinos,
                        subtitle = AppStrings.emptyDestinationsHint,
                        actionLabel = AppStrings.TraerQr,
                        onAction = { onAction(DestinationAction.Edit(null)) },
                    )
                    // Filtro Favoritos sin favoritos: mensaje propio y salida
                    // real al filtro Todos (antes "Limpiar búsqueda" no
                    // cambiaba el filtro).
                    state.favoriteOnly && state.visibleDestinations.isEmpty() && state.query.isBlank() -> AmEmptyState(
                        icon = AmIcons.Favorite,
                        title = AppStrings.AunNoHayFavoritos,
                        subtitle = AppStrings.MarcaUnDestinoConEstrella,
                        actionLabel = AppStrings.VerTodos,
                        onAction = { onAction(DestinationAction.SetFavoritesOnly(false)) },
                    )
                    state.visibleDestinations.isEmpty() -> AmEmptyState(
                        icon = AmIcons.QrCode,
                        title = AppStrings.NoSeEncontraronDestinos,
                        subtitle = AppStrings.PruebaConOtraBusquedaOLimpia,
                        actionLabel = AppStrings.LimpiarBusqueda,
                        onAction = { onAction(DestinationAction.Search("")) },
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
    // Hub "¿Qué quieres añadir?" (U-10, D8, S01): las 3 creaciones reales
    // (diálogo por la limitación de ModalBottomSheet bajo Robolectric).
    if (showAddHub) {
        AmDialog(
            title = AppStrings.QueQuieresAnadir,
            confirmLabel = AppStrings.Cancelar,
            confirmAsText = true,
            onConfirm = { showAddHub = false },
            onDismiss = { showAddHub = false },
            content = {
                AmDialogList {
                    AmListRow(
                        title = AppStrings.TraerQr,
                        onClick = {
                            showAddHub = false
                            onScanQr()
                        },
                        leading = {
                            AmIconBadge(icon = AmIcons.QrCode, tone = AmTone.Info)
                        },
                        showChevron = true,
                    )
                    AmListRow(
                        title = AppStrings.RegistrarOperacion,
                        onClick = {
                            showAddHub = false
                            onRegisterOperation()
                        },
                        leading = {
                            AmIconBadge(icon = AmIcons.Register, tone = AmTone.Success)
                        },
                        showChevron = true,
                    )
                    AmListRow(
                        title = AppStrings.CrearContexto,
                        onClick = {
                            showAddHub = false
                            showCreateContext = true
                        },
                        leading = {
                            AmIconBadge(icon = AmIcons.Context, tone = AmTone.Warning)
                        },
                        showChevron = true,
                    )
                }
            },
        )
    }
    if (showCreateContext) {
        ContextCreationDialog(
            onConfirmCreate = { name, note ->
                showCreateContext = false
                onCreateContext(name, note)
            },
            onDismiss = { showCreateContext = false },
        )
    }
}

/**
 * Mini-tarjeta del carrusel de favoritos: tile de identidad, nombre,
 * metadato y categoría real. Toda la interacción vive en la tarjeta; el
 * toque abre el detalle.
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
    // Fila del destino: miniatura QR a la izquierda, título con
    // identidad, metadato de sincronización y favorito a la derecha.
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
                // U-12/D9: el contenido decodificado NO aparece en la lista
                // (privacidad + no parece un enlace); vive en el Detalle.
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
