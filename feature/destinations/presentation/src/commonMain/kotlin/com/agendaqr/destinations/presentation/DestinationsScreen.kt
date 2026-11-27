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
import com.agendaqr.core.ui.components.AqCarousel
import com.agendaqr.core.ui.components.AqCarouselItem
import com.agendaqr.core.ui.components.AqCheckbox
import com.agendaqr.core.ui.components.AqEmptyPlaceholder
import com.agendaqr.core.ui.components.AqFavoriteToggle
import com.agendaqr.core.ui.components.AqHairline
import com.agendaqr.core.ui.components.AqIcon
import com.agendaqr.core.ui.components.AqIcons
import com.agendaqr.core.ui.components.AqLoadMore
import com.agendaqr.core.ui.components.AqMenuItem
import com.agendaqr.core.ui.components.AqModal
import com.agendaqr.core.ui.components.AqNavChromeState
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
import com.agendaqr.core.ui.theme.AqLayout
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
    var visibleCount by remember(state.visibleDestinations.size) { mutableStateOf(50) }
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
        onNavSelect = { name -> navChrome?.onSelect?.invoke(name); Unit },
        // Spec §S01: el menú "…" de Inicio ofrece "Cuenta y respaldo"
        // (nube opcional, ADR-0016) y "Bloquear".
        overflowActions = listOf(
            com.agendaqr.core.ui.components.AqAppBarAction(
                AppStrings.CuentaYRespaldo,
                onClick = onOpenAccount,
            ),
            com.agendaqr.core.ui.components.AqAppBarAction(
                AppStrings.Bloquear,
                com.agendaqr.core.ui.components.AqIcons.Lock,
                onLock,
            ),
        ),
        overflowLabel = AppStrings.Mas,
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
        // D-02: el contenido puede exceder el viewport (acciones rápidas +
        // segmentado + secciones); sin scroll los hijos que desbordan miden
        // 0×0 y quedan inalcanzables.
        scrollable = true,
        toastHost = toastHost?.let { host -> { AqToastHost(host) } },
    ) {
        // Trigger de búsqueda global: ancho completo, variante Outlined para no
        // competir con las acciones rápidas primarias. Mismo copy que el
        // disparador previo (AppStrings.BuscarEnAgendaQr).
        AqButton(
            label = AppStrings.BuscarEnAgendaQr,
            icon = com.agendaqr.core.ui.components.AqIcons.Search,
            onClick = onOpenSearch,
            variant = AqButtonVariant.Outline,
            size = AqButtonSize.Large,
            modifier = Modifier.fillMaxWidth(),
        )

        // §10: errores recuperables y contextuales, visibles sin scroll.
        state.error?.let { error ->
            val (bannerAction, bannerDismiss) = error.bannerActions(
                onRetry = { onAction(DestinationAction.RetryFailed) },
                onClear = { onAction(DestinationAction.ClearError) },
            )
            AqAlert(
                tone = AqTone.Danger,
                message = error.display(),
                confirmLabel = bannerAction?.label,
                onConfirm = bannerAction?.onClick,
                dismissLabel = bannerDismiss?.label,
                onDismiss = bannerDismiss?.onClick ?: { },
            )
        }

        // Rejilla "King Kong" de acciones rápidas existentes.
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AqLayout.InGroup),
        ) {
            AqQuickAction(
                label = AppStrings.TraerQr,
                icon = com.agendaqr.core.ui.components.AqIcons.QrCode,
                onClick = onScanQr,
                modifier = Modifier.weight(1f),
            )
            AqQuickAction(
                label = AppStrings.RegistrarOperacion,
                icon = com.agendaqr.core.ui.components.AqIcons.Register,
                onClick = onRegisterOperation,
                modifier = Modifier.weight(1f),
            )
            AqQuickAction(
                label = AppStrings.CrearContexto,
                icon = com.agendaqr.core.ui.components.AqIcons.Context,
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
                    icon = com.agendaqr.core.ui.components.AqIcons.Favorite,
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
                        imageVector = com.agendaqr.core.ui.components.AqIcons.Favorite,
                        contentDescription = null,
                        size = com.agendaqr.core.ui.theme.AqMetrics.IconSizeCompact,
                        tint = AqColor.SelectionIcon,
                    )
                    AqSectionTitle(text = AppStrings.Favoritos)
                }
                AqCarousel(
                    items = favorites.map {
                        AqCarouselItem(it.id, it.name.ifBlank { AppStrings.SinNombre })
                    },
                    itemExtent = com.agendaqr.core.ui.theme.AqMetrics.FavCardWidth,
                    gap = AqSpacing.Md,
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
                        icon = com.agendaqr.core.ui.components.AqIcons.QrCode,
                        title = AppStrings.AunNoHayDestinos,
                        subtitle = AppStrings.emptyDestinationsHint,
                        actionLabel = AppStrings.TraerQr,
                        onAction = onScanQr,
                    )
                    state.favoriteOnly && state.visibleDestinations.isEmpty() && state.query.isBlank() -> AqEmptyPlaceholder(
                        icon = com.agendaqr.core.ui.components.AqIcons.Favorite,
                        title = AppStrings.AunNoHayFavoritos,
                        subtitle = AppStrings.MarcaUnDestinoConEstrella,
                        actionLabel = AppStrings.VerTodos,
                        onAction = { onAction(DestinationAction.SetFavoritesOnly(false)) },
                    )
                    state.visibleDestinations.isEmpty() -> AqEmptyPlaceholder(
                        icon = com.agendaqr.core.ui.components.AqIcons.QrCode,
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
            content = { },
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
