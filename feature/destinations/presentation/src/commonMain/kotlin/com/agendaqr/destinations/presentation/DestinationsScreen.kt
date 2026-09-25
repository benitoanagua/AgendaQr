package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.agendaqr.core.ui.components.XauxaPrimaryButton
import com.agendaqr.core.ui.components.XauxaSecondaryButton
import com.agendaqr.core.ui.components.XauxaStatusBanner
import com.agendaqr.core.ui.components.XauxaTextAction
import com.agendaqr.core.ui.components.XauxaTile
import com.agendaqr.core.ui.components.XauxaLoading
import com.agendaqr.core.ui.components.XauxaEmptyState
import com.agendaqr.core.ui.components.XauxaSearchBar
import com.agendaqr.core.ui.components.XauxaFilterChip
import com.agendaqr.core.ui.components.XauxaCategoryChip
import com.agendaqr.core.ui.components.XauxaLoadMoreFooter
import com.agendaqr.core.ui.components.XauxaListRow
import com.agendaqr.core.ui.components.XauxaTone
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
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(XauxaSpacing.Xxl),
        verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Lg),
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Agenda QR", fontSize = XauxaType.Display, fontWeight = FontWeight.Bold, color = XauxaColor.TextPrimary)
            Row(horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
                XauxaTextAction(label = "Cerrar sesión", onClick = onSignOut)
                XauxaSecondaryButton(label = "Contextos", onClick = onOpenContexts)
                XauxaSecondaryButton(label = "Operaciones", onClick = onOpenOperations)
                XauxaPrimaryButton(label = "Buscar", onClick = onOpenSearch)
                XauxaPrimaryButton(label = "Add QR", onClick = { onAction(DestinationAction.Edit(null)) })
            }
        }
        XauxaSearchBar(value = state.query, onValueChange = { onAction(DestinationAction.Search(it)) }, label = "Buscar", placeholder = "Buscar destinos", onClear = { onAction(DestinationAction.Search("")) })
        Row(horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
            XauxaFilterChip("Favoritos", state.favoriteOnly, { onAction(DestinationAction.ToggleFavorites) })
            XauxaFilterChip("Recientes", state.recentOnly, { onAction(DestinationAction.ToggleRecent) })
            state.category?.let { XauxaCategoryChip(it) }
        }
        state.error?.let { XauxaStatusBanner(it, danger = true) }
        when {
            state.isLoading -> XauxaLoading()
            state.visibleDestinations.isEmpty() -> XauxaEmptyState(
                title = if (state.destinations.isEmpty()) "No destinations" else "No matching destinations",
                actionLabel = if (state.destinations.isEmpty()) "Add QR" else "Clear search",
                onAction = { if (state.destinations.isEmpty()) onAction(DestinationAction.Edit(null)) else onAction(DestinationAction.Search("")) },
            )
            else -> {
                var visibleCount by remember(state.visibleDestinations.size) { mutableStateOf(50) }
                val paged = state.visibleDestinations.take(visibleCount)
                LazyColumn(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
                    items(paged, key = { it.id }) { destination -> DestinationRow(destination, onAction) }
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
    }
}

@Composable
fun DestinationRow(destination: Destination, onAction: (DestinationAction) -> Unit) {
    XauxaListRow(
        title = destination.name.ifBlank { "Sin nombre" },
        subtitle = destination.note ?: destination.category,
        tone = if (destination.favorite) XauxaTone.Success else XauxaTone.Neutral,
        onClick = { onAction(DestinationAction.Open(destination.id)) },
        trailing = {
            XauxaTextAction(
                label = if (destination.favorite) "Favorito" else "Marcar favorito",
                onClick = { onAction(DestinationAction.ToggleFavorite(destination)) },
            )
        },
    )
}
