package com.agendaqr.destinations.presentation

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import com.agendaqr.core.ui.components.AqBadge
import com.agendaqr.core.ui.components.AqCarousel
import com.agendaqr.core.ui.components.AqCarouselItem
import com.agendaqr.core.ui.components.AqCompactAction
import com.agendaqr.core.ui.components.AqPullToRefresh
import com.agendaqr.core.ui.components.AqSwipeAction
import com.agendaqr.core.ui.components.AqSwipeActions
import com.agendaqr.core.ui.components.AqQuickAction
import com.agendaqr.core.ui.components.AqDialog
import com.agendaqr.core.ui.components.AqDialogList
import com.agendaqr.core.ui.components.AqFavoriteToggle
import com.agendaqr.core.ui.components.AqSegmentedControl
import com.agendaqr.core.ui.components.AqSegmentedOption
import com.agendaqr.core.ui.components.AqIcon
import com.agendaqr.core.ui.components.AqIconBadge
import com.agendaqr.core.ui.components.AqIcons
import com.agendaqr.core.ui.components.AqListRow
import com.agendaqr.core.ui.components.AqListBody
import com.agendaqr.core.ui.components.AqNavChromeState
import com.agendaqr.core.ui.components.AqOverflowAction
import com.agendaqr.core.ui.components.AqScreenScaffold
import com.agendaqr.core.ui.components.AqSectionHeader
import com.agendaqr.core.ui.components.AqTappableCard
import com.agendaqr.core.ui.components.AqSkeleton
import com.agendaqr.core.ui.components.AqSnackbarHost
import com.agendaqr.core.ui.components.AqSearchTrigger
import com.agendaqr.core.ui.components.AqText
import com.agendaqr.core.ui.components.AqTopBar
import com.agendaqr.core.ui.components.AqTone
import com.agendaqr.core.ui.components.AqStatusBanner
import com.agendaqr.core.ui.components.AqEmptyState
import com.agendaqr.core.ui.components.AqLoadMoreFooter
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.core.ui.theme.AqWeight
import com.agendaqr.destinations.domain.Destination

/**
 * S01 — Inicio: acciones rápidas, control segmentado con conteo real,
 * carrusel de favoritos y lista agrupada con "Ver QR" por fila. Composición
 * alineada con la especificación UX/UI vigente: navbar con el nombre de la superficie y filtros
 * Todos/Favoritos como segmentado mutuamente excluyente. Todas las
 * acciones son reales: S02 (traer QR), pantalla completa del código
 * (RF-05) y favoritos del dominio.
 */
@Composable
fun DestinationsScreen(
    state: DestinationsUiState,
    onAction: (DestinationAction) -> Unit,
    navChrome: AqNavChromeState? = null,
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
    var showCreateContext by remember { mutableStateOf(false) }
    var destinationToDelete by remember { mutableStateOf<Destination?>(null) }
    var visibleCount by remember(state.visibleDestinations.size) { mutableStateOf(50) }
    val paged = state.visibleDestinations.take(visibleCount)
    val favorites = state.favorites

    AqScreenScaffold(
        scrollable = state.destinations.isNotEmpty(),
        topBar = {
            AqTopBar(
                title = AppStrings.Inicio,
                overflowActions = listOf(
                    AqOverflowAction(AppStrings.CuentaYRespaldo, onClick = onOpenAccount),
                    AqOverflowAction(AppStrings.Bloquear, onClick = onLock),
                ),
                overflowLabel = AppStrings.Mas,
            )
        },
        navDestinations = navChrome?.destinations.orEmpty(),
        snackbarHost = appHosts?.feedback?.let { host -> { AqSnackbarHost(host) } },
        statusContent = appHosts?.status,

        navSelectedId = navChrome?.selectedId,
        onNavSelect = { navChrome?.onSelect?.invoke(it) },
        floatingActionButton = null,
        verticalArrangement = Arrangement.spacedBy(AqSpacing.Lg),
    ) {
        AqSearchTrigger(
            label = AppStrings.BuscarEnAgendaQr,
            placeholder = AppStrings.searchPlaceholder,
            onClick = onOpenSearch,
        )

        // §10: errores recuperables y contextuales, visibles sin scroll.
        state.error?.let { error ->
            val (bannerAction, bannerDismiss) = error.bannerActions(
                onRetry = { onAction(DestinationAction.RetryFailed) },
                onClear = { onAction(DestinationAction.ClearError) },
            )
            AqStatusBanner(
                title = error.what,
                message = error.dataStatus,
                tone = AqTone.Danger,
                action = bannerAction,
                dismiss = bannerDismiss,
            )
        }

        // Rejilla "King Kong" de acciones rápidas existentes — mismas tres acciones del hub anterior, sin duplicar.
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
        ) {
            AqQuickAction(
                label = AppStrings.TraerQr,
                icon = AqIcons.QrCode,
                onClick = onScanQr,
                modifier = Modifier.weight(1f),
            )
            AqQuickAction(
                label = AppStrings.RegistrarOperacion,
                icon = AqIcons.Register,
                onClick = onRegisterOperation,
                modifier = Modifier.weight(1f),
            )
            AqQuickAction(
                label = AppStrings.CrearContexto,
                icon = AqIcons.Context,
                onClick = { showCreateContext = true },
                modifier = Modifier.weight(1f),
            )
        }

        // Segmentado Todos/Favoritos (S01; la spec
        // admite "Tabs o Segmented"): filtros reales mutuamente
        // excluyentes con conteo. La selección vive en la semántica
        // (`selectableGroup` + rol radio), nunca solo en el color.
        AqSegmentedControl(
            options = listOf(
                AqSegmentedOption(
                    label = AppStrings.Todos,
                    count = state.destinations.size,
                ),
                AqSegmentedOption(
                    label = AppStrings.Favoritos,
                    icon = AqIcons.Favorite,
                    count = favorites.size,
                ),
            ),
            selectedIndex = if (state.favoriteOnly) 1 else 0,
            onSelect = { index -> onAction(DestinationAction.SetFavoritesOnly(index == 1)) },
        )

        // Favoritos (U-10: es lo que son, no "frecuentes"): carrusel
        // horizontal; la siguiente tarjeta asoma como indicio (sin
        // instrucción de deslizamiento).
        if (state.showsFavoritesCarousel) {
            Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AqSpacing.Xs),
                ) {
                    AqIcon(
                        imageVector = AqIcons.Favorite,
                        contentDescription = null,
                        size = AqMetrics.IconSizeCompact,
                        tint = AqColor.SelectionIcon,
                    )
                    AqSectionHeader(text = AppStrings.Favoritos)
                }
                AqCarousel(
                    items = favorites.map {
                        AqCarouselItem(it.id, it.name.ifBlank { AppStrings.SinNombre })
                    },
                    itemWidth = AqMetrics.FavCardWidth,
                    itemSpacing = AqSpacing.Md,
                ) { item ->
                    val destination = favorites.find { it.id == item.id }!!
                    FavoriteDestinationCard(
                        destination = destination,
                        onClick = { onAction(DestinationAction.Open(destination.id)) },
                    )
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
            Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
                if (!state.destinations.isEmpty()) {
                    AqSectionHeader(
                        text = if (showsFavoritesCarousel) AppStrings.OtrosDestinos else AppStrings.TodosLosDestinos,
                    )
                }
                when {
                    // la carga inicial se anuncia con el skeleton del DS.
                    state.isLoading -> AqSkeleton(lines = 4)
                    state.destinations.isEmpty() -> AqEmptyState(
                        icon = AqIcons.QrCode,
                        title = AppStrings.AunNoHayDestinos,
                        subtitle = AppStrings.emptyDestinationsHint,
                    )
                    // Filtro Favoritos sin favoritos: mensaje propio y salida
                    // real al filtro Todos (antes "Limpiar búsqueda" no
                    // cambiaba el filtro).
                    state.favoriteOnly && state.visibleDestinations.isEmpty() && state.query.isBlank() -> AqEmptyState(
                        icon = AqIcons.Favorite,
                        title = AppStrings.AunNoHayFavoritos,
                        subtitle = AppStrings.MarcaUnDestinoConEstrella,
                        actionLabel = AppStrings.VerTodos,
                        onAction = { onAction(DestinationAction.SetFavoritesOnly(false)) },
                    )
                    state.visibleDestinations.isEmpty() -> AqEmptyState(
                        icon = AqIcons.QrCode,
                        title = AppStrings.NoSeEncontraronDestinos,
                        subtitle = AppStrings.PruebaConOtraBusquedaOLimpia,
                        actionLabel = AppStrings.LimpiarBusqueda,
                        onAction = { onAction(DestinationAction.Search("")) },
                    )
                    else -> {
                        AqPullToRefresh(
                            isRefreshing = state.isLoading,
                            onRefresh = { onAction(DestinationAction.Search(state.query)) },
                            refreshingLabel = AppStrings.Cargando,
                        ) {
                            AqListBody {
                                paged.forEachIndexed { index, destination ->
                                    DestinationRow(
                                        destination,
                                        onAction,
                                        syncLookup,
                                        showDivider = index < paged.lastIndex,
                                        onRequestDelete = { destinationToDelete = it },
                                    )
                                }
                            }
                        }
                        if (paged.size < state.visibleDestinations.size) {
                            AqLoadMoreFooter(
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

    if (showCreateContext) {
        ContextCreationDialog(
            onConfirmCreate = { name, note ->
                showCreateContext = false
                onCreateContext(name, note)
            },
            onDismiss = { showCreateContext = false },
        )
    }

    destinationToDelete?.let { dest ->
        AqDialog(
            title = AppStrings.EliminarDestino,
            message = AppStrings.irreversibleActionWarning,
            confirmLabel = AppStrings.Eliminar,
            destructive = true,
            onConfirm = {
                onAction(DestinationAction.Delete(dest.id))
                destinationToDelete = null
            },
            dismissLabel = AppStrings.Cancelar,
            onDismiss = { destinationToDelete = null },
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
    com.agendaqr.core.ui.components.AqTappableCard(
        onClick = onClick,
        modifier = Modifier.width(AqMetrics.FavCardWidth),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                AqIconBadge(icon = AqIcons.QrCode, tone = AqTone.Info)
                destination.category?.let {
                    AqBadge(text = it, tone = AqTone.Neutral)
                }
            }
            Column {
                AqText(
                    destination.name.ifBlank { AppStrings.SinNombre },
                    size = AqType.RowTitle,
                    fontWeight = AqWeight.SemiBold,
                )
                AqText(
                    destinationSubtitle(destination) ?: "",
                    color = AqColor.TextSecondary,
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
    onRequestDelete: ((Destination) -> Unit)? = null,
) {
    // Fila del destino: miniatura QR a la izquierda, título con
    // identidad, metadato de sincronización y favorito a la derecha.
    val rowContent = @Composable {
        AqListRow(
            title = destination.name.ifBlank { AppStrings.SinNombre },
            subtitle = destinationSubtitle(destination),
            showDivider = showDivider,
            onClick = { onAction(DestinationAction.Open(destination.id)) },
            leading = {
                AqIconBadge(icon = AqIcons.QrCode, tone = AqTone.Neutral)
            },
            meta = {
                Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Xs)) {
                    // U-12/D9: el contenido decodificado NO aparece en la lista
                    // (privacidad + no parece un enlace); vive en el Detalle.
                    ElementSyncBadge(
                        syncLookup.status(SyncResource.DESTINATION, destination.id),
                    )
                }
            },
            trailing = {
                AqFavoriteToggle(
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

    if (onRequestDelete != null) {
        val swipeActions = listOf(
            AqSwipeAction(
                label = if (destination.favorite) AppStrings.QuitarFavorito else AppStrings.MarcarFavorito,
                icon = AqIcons.Favorite,
                backgroundColor = AqColor.EmphasisFill,
                contentColor = AqColor.InverseOnSurface,
                onAction = { onAction(DestinationAction.ToggleFavorite(destination)) },
            ),
            AqSwipeAction(
                label = AppStrings.Eliminar,
                icon = AqIcons.Trash,
                backgroundColor = AqColor.DangerFill,
                contentColor = AqColor.InverseOnSurface,
                onAction = { onRequestDelete(destination) },
            ),
        )
        AqSwipeActions(actions = swipeActions) {
            rowContent()
        }
    } else {
        rowContent()
    }
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
