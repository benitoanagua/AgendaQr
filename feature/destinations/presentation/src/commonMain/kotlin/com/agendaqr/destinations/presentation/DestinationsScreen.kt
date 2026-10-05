package com.agendaqr.destinations.presentation

import androidx.compose.foundation.clickable
import com.agendaqr.destinations.presentation.AppStrings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.Alignment
import com.agendaqr.destinations.data.SyncResource
import com.agendaqr.core.ui.components.XauxaAppBar
import com.agendaqr.core.ui.components.XauxaAppBarAction
import com.agendaqr.core.ui.components.XauxaIcons
import com.agendaqr.core.ui.components.XauxaLiveTile
import com.agendaqr.core.ui.components.XauxaOverflowAction
import com.agendaqr.core.ui.components.XauxaPageTitle
import com.agendaqr.core.ui.components.XauxaSectionHeader
import com.agendaqr.core.ui.components.XauxaText
import com.agendaqr.core.ui.components.XauxaTileGrid
import com.agendaqr.core.ui.components.XauxaTileItem
import com.agendaqr.core.ui.components.XauxaTileSize
import com.agendaqr.core.ui.components.XauxaStatusBanner
import com.agendaqr.core.ui.components.XauxaTextAction
import com.agendaqr.core.ui.components.XauxaLoading
import com.agendaqr.core.ui.components.XauxaEmptyState
import com.agendaqr.core.ui.components.XauxaSearchBar
import com.agendaqr.core.ui.components.XauxaCategoryChip
import com.agendaqr.core.ui.components.XauxaLoadMoreFooter
import com.agendaqr.core.ui.components.XauxaListRow
import com.agendaqr.core.ui.components.XauxaTone
import com.agendaqr.core.ui.theme.XauxaAccents
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.core.ui.theme.XauxaSpacing
import com.agendaqr.core.ui.theme.XauxaType
import com.agendaqr.destinations.domain.Destination

@Composable
fun DestinationsScreen(
    state: DestinationsUiState,
    onAction: (DestinationAction) -> Unit,
    onOpenOperations: () -> Unit = {},
    onOpenSearch: () -> Unit = {},
    onOpenContexts: () -> Unit = {},
    onSignOut: () -> Unit = {},
    syncLookup: ElementSyncLookup = ElementSyncLookup.Empty,
) {
    var visibleCount by remember(state.visibleDestinations.size) { mutableStateOf(50) }
    val paged = state.visibleDestinations.take(visibleCount)
    // S01 V1.1 (ADR-0005): título ligero, búsqueda como primer bloque,
    // rejilla de tiles, sección Recientes con filas abiertas sin borde y
    // las acciones de sesión en la barra de aplicación inferior. Ninguna
    // capacidad existente desaparece: favorito, filtro de favoritos,
    // estado de sincronización y cerrar sesión siguen accesibles.
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .imePadding(),
    ) {
        // Un único contenedor scrolleable (M1: contenido antes que cromo):
        // la pantalla entera desliza y la app bar queda anclada abajo.
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = XauxaSpacing.ScreenMargin),
            verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Lg),
        ) {
            XauxaPageTitle(text = AppStrings.AgendaQr)

            // S01 — buscar domina visualmente (M2). La barra es la entrada
            // a S04; el listado de Inicio no se filtra aquí. T10: un ÚNICO
            // nodo semántico para el control (cero nodos duplicados).
            run {
                Box {
                    XauxaSearchBar(
                        value = "",
                        onValueChange = {},
                        label = AppStrings.BuscarEnAgendaQr,
                        placeholder = AppStrings.searchPlaceholder,
                    )
                    Box(
                        Modifier
                            .matchParentSize()
                            .clickable(onClickLabel = AppStrings.BuscarEnAgendaQr) { onOpenSearch() }
                            .clearAndSetSemantics {
                                contentDescription = AppStrings.BuscarEnAgendaQr
                                role = Role.Button
                                onClick(label = AppStrings.BuscarEnAgendaQr) {
                                    onOpenSearch()
                                    true
                                }
                            },
                    )
                }
            }

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
                )
            }
            // M2: rejilla de tiles — Añadir, Registrar, Favoritos, Contextos y
            // el tile ancho vivo (último QR o actividad reciente). La capacidad
            // de filtrar por favoritos la conserva el tile Favoritos (misma
            // acción ToggleFavorites que antes hacía el chip).
            val latest = state.visibleDestinations.firstOrNull()
            XauxaTileGrid(
                items = listOf(
                        XauxaTileItem(
                            label = AppStrings.Anadir,
                            icon = XauxaIcons.Add,
                            onClick = { onAction(DestinationAction.Edit(null)) },
                        ),
                        XauxaTileItem(
                            label = AppStrings.Registrar,
                            icon = XauxaIcons.Register,
                            onClick = onOpenOperations,
                        ),
                        XauxaTileItem(
                            label = AppStrings.Favoritos,
                            icon = XauxaIcons.Favorite,
                            accent = if (state.favoriteOnly) XauxaAccents.Admitted.first() else XauxaAccents.System,
                            onClick = { onAction(DestinationAction.ToggleFavorites) },
                        ),
                        XauxaTileItem(
                            label = AppStrings.Contextos,
                            icon = XauxaIcons.Context,
                            onClick = onOpenContexts,
                        ),
                        // M2/M11: el tile vivo cambia de contenido SOLO cuando
                        // cambia el dato (una transición por cambio, sin bucles).
                        XauxaTileItem(
                            label = AppStrings.UltimoQrOActividad,
                            size = XauxaTileSize.WIDE,
                            onClick = latest?.let { d -> { onAction(DestinationAction.Open(d.id)) } },
                            content = {
                                    XauxaLiveTile(data = latest?.id) {
                                        XauxaText(
                                                text = latest?.name?.ifBlank { AppStrings.SinNombre }
                                                    ?: AppStrings.AunNoHayDestinos,
                                                color = XauxaColor.OnBrand,
                                        )
                                    }
                            },
                        ),
                ),
            )

            Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
                state.category?.let { XauxaCategoryChip(it) }
                XauxaSectionHeader(text = AppStrings.Recientes)
            }
            when {
                state.isLoading -> XauxaLoading(message = AppStrings.CargandoDestinosQr)
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
                            )
                        }
                    }
                }
            }
        }

        // M7: las acciones de sesión viven en la barra de aplicación
        // inferior (menú "…"), no sueltas en el cuerpo. "Buscar" domina
        // como primer bloque y además es la acción principal de la barra.
        XauxaAppBar(
            modifier = Modifier.navigationBarsPadding(),
            actions = listOf(
                XauxaAppBarAction(
                    label = AppStrings.BuscarEnAgendaQr,
                    icon = XauxaIcons.Search,
                    primary = true,
                    onClick = onOpenSearch,
                ),
            ),
            overflowActions = listOf(
                XauxaOverflowAction(AppStrings.CerrarSesion, onClick = onSignOut),
            ),
        )
    }
}

@Composable
fun DestinationRow(
    destination: Destination,
    onAction: (DestinationAction) -> Unit,
    syncLookup: ElementSyncLookup = ElementSyncLookup.Empty,
) {
    XauxaListRow(
        title = destination.name.ifBlank { AppStrings.SinNombre },
        subtitle = destination.note ?: destination.category,
        tone = if (destination.favorite) XauxaTone.Info else XauxaTone.Neutral,
        onClick = { onAction(DestinationAction.Open(destination.id)) },
        trailing = {
            Row(horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm), verticalAlignment = Alignment.CenterVertically) {
                // T6: estado de sincronización por elemento (con texto).
                ElementSyncBadge(
                    syncLookup.status(SyncResource.DESTINATION, destination.id),
                )
                XauxaTextAction(
                    label = if (destination.favorite) AppStrings.Favorito else AppStrings.MarcarFavorito,
                    onClick = { onAction(DestinationAction.ToggleFavorite(destination)) },
                )
            }
        },
    )
}
