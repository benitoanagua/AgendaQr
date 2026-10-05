package com.agendaqr.destinations.presentation

import androidx.compose.foundation.clickable
import com.agendaqr.destinations.presentation.AppStrings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.font.FontWeight
import com.agendaqr.destinations.data.SyncResource
import com.agendaqr.core.ui.components.XauxaPrimaryButton
import com.agendaqr.core.ui.components.XauxaSecondaryButton
import com.agendaqr.core.ui.components.XauxaStatusBanner
import com.agendaqr.core.ui.components.XauxaTextAction
import com.agendaqr.core.ui.components.XauxaLoading
import com.agendaqr.core.ui.components.XauxaEmptyState
import com.agendaqr.core.ui.components.XauxaSearchBar
import com.agendaqr.core.ui.components.XauxaFilterChip
import com.agendaqr.core.ui.components.XauxaCategoryChip
import com.agendaqr.core.ui.components.XauxaLoadMoreFooter
import com.agendaqr.core.ui.components.XauxaListRow
import com.agendaqr.core.ui.components.XauxaTone
import com.agendaqr.core.ui.components.XauxaHeading
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.core.ui.theme.XauxaSpacing
import com.agendaqr.core.ui.theme.XauxaType
import com.agendaqr.destinations.domain.Destination

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DestinationsScreen(
    state: DestinationsUiState,
    onAction: (DestinationAction) -> Unit,
    onOpenOperations: () -> Unit = {},
    onOpenSearch: () -> Unit = {},
    onSignOut: () -> Unit = {},
    syncLookup: ElementSyncLookup = ElementSyncLookup.Empty,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(XauxaSpacing.Xxl)
            .imePadding(),
        verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Lg),
    ) {
        XauxaHeading(
            text = AppStrings.AgendaQr,
            size = XauxaType.Display,
        )

        // S01 — Inicio: buscar domina visualmente; Añadir y Registrar son
        // acciones secundarias. Contexto pertenece al flujo que lo necesita,
        // no a una taxonomía de navegación principal.
        // La barra es la entrada a S04 (búsqueda global): al tocarla se abre
        // la pantalla de Buscar, que encuentra QR, operaciones, comprobantes
        // y contextos. El listado de destinos de Inicio no se filtra aquí.
        // T10: un ÚNICO nodo semántico para el control — el campo decorativo
        // no expone su editable-node al lector de pantalla; el botón real
        // es el que navega, con rol y etiqueta (cero nodos duplicados).
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
                    // El toque físico vive en clickable; clearAndSetSemantics
                    // reemplaza TODA la semántica (incluida la de clickable)
                    // por el nodo único del control.
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

        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm),
            verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm),
        ) {
            XauxaSecondaryButton(
                label = AppStrings.Anadir,
                onClick = { onAction(DestinationAction.Edit(null)) },
            )
            XauxaSecondaryButton(
                label = AppStrings.Registrar,
                onClick = onOpenOperations,
            )
        }

        Row(horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
            XauxaFilterChip(AppStrings.Favoritos, state.favoriteOnly, { onAction(DestinationAction.ToggleFavorites) })
            state.category?.let { XauxaCategoryChip(it) }
        }
        state.error?.let { error ->
            XauxaStatusBanner(
                error.display(),
                tone = XauxaTone.Danger,
                actionLabel = error.action.label,
                onAction = { onAction(DestinationAction.ClearError) },
                onDismiss = { onAction(DestinationAction.ClearError) },
            )
        }
        when {
            state.isLoading -> XauxaLoading(message = AppStrings.CargandoDestinosQr)
            state.visibleDestinations.isEmpty() -> XauxaEmptyState(
                title = if (state.destinations.isEmpty()) "Aún no hay destinos" else "No se encontraron destinos",
                subtitle = if (state.destinations.isEmpty()) "Agrega tu primer QR para empezar." else "Prueba con otra búsqueda o limpia los filtros.",
                actionLabel = if (state.destinations.isEmpty()) "Añadir" else "Limpiar búsqueda",
                onAction = { if (state.destinations.isEmpty()) onAction(DestinationAction.Edit(null)) else onAction(DestinationAction.Search("")) },
            )
            else -> {
                var visibleCount by remember(state.visibleDestinations.size) { mutableStateOf(50) }
                val paged = state.visibleDestinations.take(visibleCount)
                LazyColumn(
                    modifier = Modifier.fillMaxSize().weight(1f),
                    verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm),
                ) {
                    items(paged, key = { it.id }) { destination ->
                        DestinationRow(destination, onAction, syncLookup)
                    }
                    if (paged.size < state.visibleDestinations.size) {
                        item {
                            XauxaLoadMoreFooter(
                                onLoadMore = { visibleCount = (visibleCount + 50).coerceAtMost(state.visibleDestinations.size) },
                            )
                        }
                    }
                }
            }
        }

        // T11 — S01: "Cerrar sesión" es una acción de cuenta, no del flujo:
        // vive al pie, fuera de la fila de acciones principales (Buscar
        // domina; Añadir y Registrar son las acciones secundarias).
        XauxaTextAction(
            label = AppStrings.CerrarSesion,
            onClick = onSignOut,
            modifier = Modifier.padding(top = XauxaSpacing.Lg),
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
