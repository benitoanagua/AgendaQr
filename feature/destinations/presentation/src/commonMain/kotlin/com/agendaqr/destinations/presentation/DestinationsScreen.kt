package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.agendaqr.destinations.data.SyncResource
import com.agendaqr.core.ui.components.AqBadge
import com.agendaqr.core.ui.components.AqBottomAction
import com.agendaqr.core.ui.components.AqCarousel
import com.agendaqr.core.ui.components.AqCheckbox
import com.agendaqr.core.ui.components.AqCarouselItem
import com.agendaqr.core.ui.components.AqPullToRefresh
import com.agendaqr.core.ui.components.AqSwipeAction
import com.agendaqr.core.ui.components.AqSwipeActions
import com.agendaqr.core.ui.components.AqQuickAction
import com.agendaqr.core.ui.components.AqDialog
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
import com.agendaqr.core.ui.components.AqSelectionBar
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
 * carrusel de favoritos y lista agrupada. Composición alineada 1:1 con
 * la especificación UX/UI y el contrato de interacción (docs/04-ux).
 *
 * Bloques (orden contractual):
 *   SearchTrigger → ErrorBanner → QuickActions → Segmented → FavoritesCarousel → DestinationsSection
 *
 * Sin FAB. Las tres creaciones viven en la rejilla de acciones rápidas.
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
    /** Acción rápida: traer un QR (S02, cámara y galería). */
    onScanQr: () -> Unit = { onAction(DestinationAction.Edit(null)) },
    /** Hub: registrar actividad (S07). */
    onRegisterOperation: () -> Unit = {},
    /** Hub: crear contexto (diálogo existente). */
    onCreateContext: (name: String, note: String?) -> Unit = { _, _ -> },
) {
    var showCreateContext by remember { mutableStateOf(false) }
    var destinationToDelete by remember { mutableStateOf<Destination?>(null) }
    var visibleCount by remember(state.visibleDestinations.size) { mutableStateOf(50) }
    val paged = state.visibleDestinations.take(visibleCount)
    val favorites = state.favorites
    // G3: modo selección por lote en destinos.
    var selectedIds by rememberSaveable { mutableStateOf(setOf<String>()) }
    var confirmarEliminarLote by remember { mutableStateOf(false) }
    val selectionMode = selectedIds.isNotEmpty()

    AqScreenScaffold(
        scrollable = state.destinations.isNotEmpty(),
        topBar = {
            if (selectionMode) {
                val selectAll = state.destinations.isNotEmpty() && selectedIds.size == state.destinations.size
                AqSelectionBar(
                    closeLabel = AppStrings.CerrarSeleccion,
                    selectAllLabel = if (selectAll) AppStrings.SeleccionarNinguno else AppStrings.SeleccionarTodo,
                    selectedCountLabel = "${selectedIds.size} ${AppStrings.Seleccionados}",
                    selectedCount = selectedIds.size,
                    totalCount = state.destinations.size,
                    selectAllChecked = selectAll,
                    onSelectAll = {
                        selectedIds = if (selectAll) emptySet() else state.destinations.map { it.id }.toSet()
                    },
                    onClose = { selectedIds = emptySet() },
                )
            } else {
                AqTopBar(
                    title = AppStrings.Inicio,
                    overflowActions = listOf(
                        AqOverflowAction(AppStrings.CuentaYRespaldo, onClick = onOpenAccount),
                        AqOverflowAction(AppStrings.Bloquear, onClick = onLock),
                    ),
                    overflowLabel = AppStrings.Mas,
                )
            }
        },
        navDestinations = navChrome?.destinations.orEmpty(),
        snackbarHost = appHosts?.feedback?.let { host -> { AqSnackbarHost(host) } },
        statusContent = appHosts?.status,
        navSelectedId = navChrome?.selectedId,
        onNavSelect = { navChrome?.onSelect?.invoke(it) },
        floatingActionButton = null,
        bottomAction = if (selectionMode) {
            {
                AqBottomAction(
                    label = AppStrings.EliminarDestino + " (${selectedIds.size})",
                    onClick = { confirmarEliminarLote = true },
                    enabled = selectedIds.isNotEmpty(),
                )
            }
        } else {
            null
        },
        verticalArrangement = Arrangement.spacedBy(AqSpacing.Lg),
    ) {
        // --- Composición S01 (contrato + spec UX/UI) ---
        AqSearchTrigger(
            label = AppStrings.BuscarEnAgendaQr,
            placeholder = AppStrings.searchPlaceholder,
            onClick = onOpenSearch,
        )

        HomeErrorBanner(state = state, onAction = onAction)

        HomeQuickActions(
            onScanQr = onScanQr,
            onRegisterOperation = onRegisterOperation,
            onCreateContext = { showCreateContext = true },
        )

        HomeSegmentedFilter(
            destinationsCount = state.destinations.size,
            favoritesCount = favorites.size,
            favoriteOnly = state.favoriteOnly,
            onSelect = { index -> onAction(DestinationAction.SetFavoritesOnly(index == 1)) },
        )

        if (state.showsFavoritesCarousel) {
            HomeFavoritesCarousel(
                favorites = favorites,
                onOpen = { id -> onAction(DestinationAction.Open(id)) },
            )
        }

        HomeDestinationsSection(
            state = state,
            paged = paged,
            onLoadMore = {
                visibleCount = (visibleCount + 50).coerceAtMost(state.visibleDestinations.size)
            },
            selectionMode = selectionMode,
            selectedIds = selectedIds,
            onToggleSelection = { id ->
                selectedIds = if (selectedIds.contains(id)) selectedIds - id else selectedIds + id
            },
            onRequestDelete = { destinationToDelete = it },
            onAction = onAction,
            syncLookup = syncLookup,
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

    // G3: confirmación de eliminación por lote en destinos.
    if (confirmarEliminarLote) {
        val plural = if (selectedIds.size == 1) AppStrings.OperacionSingular else AppStrings.OperacionesPlural
        AqDialog(
            title = AppStrings.EliminarDestino,
            destructive = true,
            message = AppStrings.irreversibleActionWarning + "\n" + selectedIds.size + " " + plural,
            confirmLabel = AppStrings.Eliminar,
            onConfirm = {
                selectedIds.toList().forEach { id ->
                    onAction(DestinationAction.Delete(id))
                }
                selectedIds = emptySet()
                confirmarEliminarLote = false
            },
            dismissLabel = AppStrings.Cancelar,
            onDismiss = { confirmarEliminarLote = false },
        )
    }
}

// ---------------------------------------------------------------------------
// Secciones de composición S01 — extraídas para mapear 1:1 el contrato UX
// (docs/04-ux/02-especificacion-ux-ui-v1.md + 03-contrato-interaccion-pantallas.md)
// y facilitar pruebas de layout / a11y por bloque.
// ---------------------------------------------------------------------------

/** §10: errores recuperables y contextuales, visibles sin scroll. */
@Composable
private fun HomeErrorBanner(
    state: DestinationsUiState,
    onAction: (DestinationAction) -> Unit,
) {
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
}

/**
 * Rejilla de las tres creaciones del hub.
 * Contrato: sin FAB, sin duplicar acciones, mismas tres del flujo real.
 */
@Composable
private fun HomeQuickActions(
    onScanQr: () -> Unit,
    onRegisterOperation: () -> Unit,
    onCreateContext: () -> Unit,
) {
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
            onClick = onCreateContext,
            modifier = Modifier.weight(1f),
        )
    }
}

/** Segmented Todos · Favoritos con conteo real (mutuamente excluyente). */
@Composable
private fun HomeSegmentedFilter(
    destinationsCount: Int,
    favoritesCount: Int,
    favoriteOnly: Boolean,
    onSelect: (Int) -> Unit,
) {
    AqSegmentedControl(
        options = listOf(
            AqSegmentedOption(
                label = AppStrings.Todos,
                count = destinationsCount,
            ),
            AqSegmentedOption(
                label = AppStrings.Favoritos,
                icon = AqIcons.Favorite,
                count = favoritesCount,
            ),
        ),
        selectedIndex = if (favoriteOnly) 1 else 0,
        onSelect = onSelect,
    )
}

/** Carrusel horizontal de favoritos (la siguiente tarjeta asoma; sin instrucción de deslizamiento). */
@Composable
private fun HomeFavoritesCarousel(
    favorites: List<Destination>,
    onOpen: (String) -> Unit,
) {
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
                onClick = { onOpen(destination.id) },
            )
        }
    }
}

/**
 * Sección inferior: Otros destinos / Todos los destinos / empties /
 * skeleton / pull-to-refresh. No duplica favoritos ya mostrados en el carrusel.
 */
@Composable
private fun HomeDestinationsSection(
    state: DestinationsUiState,
    paged: List<Destination>,
    onLoadMore: () -> Unit,
    selectionMode: Boolean,
    selectedIds: Set<String>,
    onToggleSelection: (String) -> Unit,
    onRequestDelete: (Destination) -> Unit,
    onAction: (DestinationAction) -> Unit,
    syncLookup: ElementSyncLookup,
) {
    val showsFavoritesCarousel = state.showsFavoritesCarousel
    val showsLowerSection = state.isLoading ||
        state.destinations.isEmpty() ||
        state.favoriteOnly ||
        state.hasOtherDestinations ||
        state.query.isNotBlank()
    if (!showsLowerSection) return

    Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
        if (state.destinations.isNotEmpty()) {
            AqSectionHeader(
                text = if (showsFavoritesCarousel) AppStrings.OtrosDestinos else AppStrings.TodosLosDestinos,
            )
        }
        when {
            state.isLoading -> AqSkeleton(lines = 4)
            state.destinations.isEmpty() -> AqEmptyState(
                icon = AqIcons.QrCode,
                title = AppStrings.AunNoHayDestinos,
                subtitle = AppStrings.emptyDestinationsHint,
            )
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
                                onRequestDelete = onRequestDelete,
                                selected = selectedIds.contains(destination.id),
                                onToggle = if (selectionMode || selectedIds.isNotEmpty()) {
                                    { onToggleSelection(destination.id) }
                                } else {
                                    null
                                },
                                swipeEnabled = !selectionMode,
                            )
                        }
                    }
                }
                if (paged.size < state.visibleDestinations.size) {
                    AqLoadMoreFooter(
                        onLoadMore = onLoadMore,
                        loadMoreLabel = AppStrings.CargarMas,
                        loadingLabel = AppStrings.Cargando,
                        endLabel = AppStrings.NoHayMasElementos,
                    )
                }
            }
        }
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
    AqTappableCard(
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
    /** G3: fila seleccionada (modo selección por lote). */
    selected: Boolean = false,
    /** G3: alterna la selección en vez de navegar. */
    onToggle: (() -> Unit)? = null,
    /** G3: las acciones de deslizamiento se deshabilitan en modo selección. */
    swipeEnabled: Boolean = true,
) {
    val rowContent = @Composable {
        AqListRow(
            title = destination.name.ifBlank { AppStrings.SinNombre },
            subtitle = destinationSubtitle(destination),
            showDivider = showDivider,
            onClick = {
                if (onToggle != null) onToggle() else onAction(DestinationAction.Open(destination.id))
            },
            leading = {
                if (selected) {
                    AqCheckbox(
                        label = "",
                        checked = selected,
                        onCheckedChange = { onToggle?.invoke() },
                    )
                } else {
                    AqIconBadge(icon = AqIcons.QrCode, tone = AqTone.Neutral)
                }
            },
            meta = {
                Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Xs)) {
                    // El contenido decodificado NO aparece en la lista
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
        AqSwipeActions(actions = swipeActions, enabled = swipeEnabled) {
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
