package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.agendaqr.destinations.data.SyncResource
import com.agendaqr.core.ui.components.AqAlert
import com.agendaqr.core.ui.components.AqButton
import com.agendaqr.core.ui.components.AqButtonSize
import com.agendaqr.core.ui.components.AqButtonVariant
import com.agendaqr.core.ui.components.AqCheckbox
import com.agendaqr.core.ui.components.AqCarousel
import com.agendaqr.core.ui.components.AqCarouselItem
import com.agendaqr.core.ui.components.AqEmptyPlaceholder
import com.agendaqr.core.ui.components.AqFavoriteToggle
import com.agendaqr.core.ui.components.AqHairline
import com.agendaqr.core.ui.components.AqIcon
import com.agendaqr.core.ui.components.AqIcons
import com.agendaqr.core.ui.components.AqLoadMore
import com.agendaqr.core.ui.components.AqMenuItem
import com.agendaqr.core.ui.components.AqPullToRefresh
import com.agendaqr.core.ui.components.AqScreen
import com.agendaqr.core.ui.components.AqSectionTitle
import com.agendaqr.core.ui.components.AqSegmentedControl
import com.agendaqr.core.ui.components.AqSegmentedOption
import com.agendaqr.core.ui.components.AqSelectionToolbar
import com.agendaqr.core.ui.components.AqSkeletonBlock
import com.agendaqr.core.ui.components.AqSwipeAction
import com.agendaqr.core.ui.components.AqSwipeActions
import com.agendaqr.core.ui.components.AqTag
import com.agendaqr.core.ui.components.AqText
import com.agendaqr.core.ui.components.AqToastHost
import com.agendaqr.core.ui.components.AqTone
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.core.ui.theme.AqWeight
import com.agendaqr.destinations.domain.Destination
import com.agendaqr.core.ui.components.AqQuickAction

/**
 * S01 — Inicio: acciones rápidas, control segmentado con conteo real,
 * carrusel de favoritos y lista agrupada con "Ver QR" por fila.
 *
 * Construido exclusivamente con componentes Moon: [AqScreen] (scaffold con
 * nav [AqPillTab]), [AqAppBar] (vía título/atrás/overflow), [AqSelectionToolbar]
 * (modo selección G3), [AqMenuItem] (filas), [AqEmptyPlaceholder] (vacío),
 * [AqToastHost] (toasts), [AqModal] (diálogos), [AqButton] y [AqTag]
 * (acciones/etiquetas). Sin símbolos legacy ni wrappers `Aq*`.
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
    val navDestinations = navChrome?.destinations.orEmpty()
    val navSelectedId = navChrome?.selectedId
    // G3: modo selección por lote en destinos.
    var selectedIds by rememberSaveable { mutableStateOf(setOf<String>()) }
    var confirmarEliminarLote by remember { mutableStateOf(false) }
    val selectionMode = selectedIds.isNotEmpty()
    val visibleCount by remember(state.visibleDestinations.size) { mutableStateOf(50) }
    val paged = state.visibleDestinations.take(visibleCount)
    val favorites = state.favorites
    val toastHost = appHosts?.toastHost

    AqScreen(
        title = AppStrings.Inicio,
        topBar = if (selectionMode) {
            {
                val selectAll = state.destinations.isNotEmpty() && selectedIds.size == state.destinations.size
                AqSelectionToolbar(
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
            }
        } else {
            null
        },
        navDestinations = navDestinations,
        navSelectedId = navSelectedId,
        onNavSelect = { navChrome?.onSelect?.invoke(it) },
        statusContent = appHosts?.status,
        bottomAction = if (selectionMode && navDestinations.isEmpty()) {
            {
                AqButton(
                    label = AppStrings.EliminarDestino + " (${selectedIds.size})",
                    onClick = { confirmarEliminarLote = true },
                    enabled = selectedIds.isNotEmpty(),
                    variant = AqButtonVariant.Danger,
                )
            }
        } else {
            null
        },
        toastHost = toastHost?.let { host -> { AqToastHost(host) } },
    ) {
        // Trigger de búsqueda global (botón que abre el flujo de búsqueda).
        AqButton(
            label = AppStrings.BuscarEnAgendaQr,
            onClick = onOpenSearch,
            variant = AqButtonVariant.Text,
            icon = AqIcons.Search,
            size = AqButtonSize.Small,
        )

        // §10: errores recuperables y contextuales, visibles sin scroll.
        state.error?.let { error ->
            val (bannerAction, bannerDismiss) = error.bannerActions(
                onRetry = { onAction(DestinationAction.RetryFailed) },
                onClear = { onAction(DestinationAction.ClearError) },
            )
            AqAlert(
                tone = AqTone.Danger,
                message = error.dataStatus,
                dismissLabel = bannerDismiss.label,
                onDismiss = bannerDismiss.onClick,
            )
        }

        // Rejilla "King Kong" de acciones rápidas existentes.
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

        // Segmentado Todos/Favoritos (S01): filtros reales mutuamente excluyentes.
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

        // Favoritos: carrusel horizontal.
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
                    AqSectionTitle(text = AppStrings.Favoritos)
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

        // Sección inferior.
        val showsFavoritesCarousel = state.showsFavoritesCarousel
        val showsLowerSection = state.isLoading ||
            state.destinations.isEmpty() ||
            state.favoriteOnly ||
            state.hasOtherDestinations ||
            state.query.isNotBlank()
        if (showsLowerSection) {
            Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
                if (!state.destinations.isEmpty()) {
                    AqSectionTitle(
                        text = if (showsFavoritesCarousel) AppStrings.OtrosDestinos else AppStrings.TodosLosDestinos,
                    )
                }
                when {
                    state.isLoading -> AqSkeletonBlock(lines = 4)
                    state.destinations.isEmpty() -> AqEmptyPlaceholder(
                        icon = AqIcons.QrCode,
                        title = AppStrings.AunNoHayDestinos,
                        subtitle = AppStrings.emptyDestinationsHint,
                    )
                    state.favoriteOnly && state.visibleDestinations.isEmpty() && state.query.isBlank() -> AqEmptyPlaceholder(
                        icon = AqIcons.Favorite,
                        title = AppStrings.AunNoHayFavoritos,
                        subtitle = AppStrings.MarcaUnDestinoConEstrella,
                        actionLabel = AppStrings.VerTodos,
                        onAction = { onAction(DestinationAction.SetFavoritesOnly(false)) },
                    )
                    state.visibleDestinations.isEmpty() -> AqEmptyPlaceholder(
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
                            paged.forEachIndexed { index, destination ->
                                DestinationRow(
                                    destination,
                                    onAction,
                                    syncLookup,
                                    showDivider = index < paged.lastIndex,
                                    onRequestDelete = { destinationToDelete = it },
                                    selected = selectedIds.contains(destination.id),
                                    onToggle = {
                                        selectedIds = if (selectedIds.contains(destination.id)) {
                                            selectedIds - destination.id
                                        } else {
                                            selectedIds + destination.id
                                        }
                                    },
                                    swipeEnabled = !selectionMode,
                                )
                                if (index < paged.lastIndex) AqHairline()
                            }
                        }
                        if (paged.size < state.visibleDestinations.size) {
                            AqLoadMore(
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
        AqModal(
            title = AppStrings.EliminarDestino,
            dismissLabel = AppStrings.Cancelar,
            confirmLabel = AppStrings.Eliminar,
            destructive = true,
            onConfirm = {
                onAction(DestinationAction.Delete(dest.id))
                destinationToDelete = null
            },
            onDismissRequest = { destinationToDelete = null },
        )
    }

    // G3: confirmación de eliminación por lote en destinos.
    if (confirmarEliminarLote) {
        val plural = if (selectedIds.size == 1) AppStrings.OperacionSingular else AppStrings.OperacionesPlural
        AqModal(
            title = AppStrings.EliminarDestino,
            dismissLabel = AppStrings.Cancelar,
            confirmLabel = AppStrings.Eliminar,
            destructive = true,
            onConfirm = {
                selectedIds.toList().forEach { id ->
                    onAction(DestinationAction.Delete(id))
                }
                selectedIds = emptySet()
                confirmarEliminarLote = false
            },
            onDismissRequest = { confirmarEliminarLote = false },
            content = {
                AqText(
                    text = AppStrings.irreversibleActionWarning + "\n" + selectedIds.size + " " + plural,
                    color = AqColor.TextSecondary,
                )
            },
        )
    }
}

/**
 * Mini-tarjeta del carrusel de favoritos: tile de identidad, nombre,
 * metadato y categoría real.
 */
@Composable
private fun FavoriteDestinationCard(
    destination: Destination,
    onClick: () -> Unit,
) {
    // La superficie presionable vive en el DS (feature/ sin clickable
    // crudo): [AqMenuItem] con miniatura QR y nombre.
    AqMenuItem(
        title = destination.name.ifBlank { AppStrings.SinNombre },
        leading = {
            AqIcon(
                imageVector = AqIcons.QrCode,
                contentDescription = null,
                size = com.agendaqr.core.ui.theme.AqMetrics.IconSizeCompact,
            )
        },
        trailing = {
            destination.category?.let {
                AqTag(text = it)
            }
        },
        onClick = onClick,
    )
}

/**
 * Fila del destino: miniatura QR a la izquierda, título con identidad,
 * metadato de sincronización y favorito a la derecha.
 */
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
    // Fila del destino: ícono QR a la izquierda, título con identidad,
    // metadato de sincronización y favorito a la derecha.
    val rowContent = @Composable {
        AqMenuItem(
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
                    AqIcon(
                        imageVector = AqIcons.QrCode,
                        contentDescription = null,
                        size = com.agendaqr.core.ui.theme.AqMetrics.IconSizeCompact,
                    )
                }
            },
            trailing = {
                Row(
                    modifier = Modifier,
                    horizontalArrangement = Arrangement.spacedBy(AqSpacing.Xs),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ElementSyncBadge(
                        syncLookup.status(SyncResource.DESTINATION, destination.id),
                    )
                    AqFavoriteToggle(
                        favorite = destination.favorite,
                        contentDescription = if (destination.favorite) {
                            AppStrings.QuitarFavorito
                        } else {
                            AppStrings.MarcarFavorito
                        },
                        onCheckedChange = { onAction(DestinationAction.ToggleFavorite(destination)) },
                    )
                }
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
 * Subtítulo de la fila del destino.
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
