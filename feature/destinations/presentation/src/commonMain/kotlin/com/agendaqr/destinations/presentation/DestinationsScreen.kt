package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import com.agendaqr.core.ui.components.XauxaPrimaryButton
import com.agendaqr.core.ui.components.XauxaSecondaryButton
import com.agendaqr.core.ui.components.XauxaStatusBanner
import com.agendaqr.core.ui.components.XauxaTextAction
import com.agendaqr.core.ui.components.XauxaTile
import com.agendaqr.core.ui.components.XauxaLoading
import com.agendaqr.core.ui.components.XauxaEmptyState
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.core.ui.theme.XauxaSpacing
import com.agendaqr.core.ui.theme.XauxaType
import com.agendaqr.destinations.domain.Destination

@Composable
fun DestinationsScreen(
    state: DestinationsUiState,
    onAction: (DestinationAction) -> Unit,
    onOpenOperations: () -> Unit = {},
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
                XauxaSecondaryButton(label = "Operaciones", onClick = onOpenOperations)
                XauxaPrimaryButton(label = "Add QR", onClick = { onAction(DestinationAction.Edit(null)) })
            }
        }
        OutlinedTextField(value = state.query, onValueChange = { onAction(DestinationAction.Search(it)) }, modifier = Modifier.fillMaxWidth(), label = { Text("Search") }, shape = RectangleShape, singleLine = true)
        Row(horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
            if (state.favoriteOnly) XauxaPrimaryButton(label = "Favorites", onClick = { onAction(DestinationAction.ToggleFavorites) }) else XauxaSecondaryButton(label = "Favorites", onClick = { onAction(DestinationAction.ToggleFavorites) })
            if (state.recentOnly) XauxaPrimaryButton(label = "Recent", onClick = { onAction(DestinationAction.ToggleRecent) }) else XauxaSecondaryButton(label = "Recent", onClick = { onAction(DestinationAction.ToggleRecent) })
            if (state.category != null) XauxaTextAction(label = state.category, onClick = { onAction(DestinationAction.SelectCategory(null)) })
        }
        state.error?.let { XauxaStatusBanner(it, danger = true) }
        when {
            state.isLoading -> XauxaLoading()
            state.visibleDestinations.isEmpty() -> XauxaEmptyState(
                title = if (state.destinations.isEmpty()) "No destinations" else "No matching destinations",
                actionLabel = if (state.destinations.isEmpty()) "Add QR" else "Clear search",
                onAction = { if (state.destinations.isEmpty()) onAction(DestinationAction.Edit(null)) else onAction(DestinationAction.Search("")) },
            )
            else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
                items(state.visibleDestinations, key = { it.id }) { destination -> DestinationRow(destination, onAction) }
            }
        }
    }
}

@Composable
fun DestinationRow(destination: Destination, onAction: (DestinationAction) -> Unit) {
    XauxaTile(onClick = { onAction(DestinationAction.Open(destination.id)) }) {
        Column(modifier = Modifier.fillMaxWidth().padding(XauxaSpacing.Lg), verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(destination.name.ifBlank { "Unnamed destination" }, fontSize = XauxaType.Title, fontWeight = FontWeight.SemiBold, color = XauxaColor.TextPrimary)
                XauxaTextAction(label = if (destination.favorite) "Favorite" else "Mark favorite", onClick = { onAction(DestinationAction.ToggleFavorite(destination)) })
            }
            destination.category?.let { Text(it, color = XauxaColor.TextSecondary, fontSize = XauxaType.Label) }
            destination.note?.takeIf { it.isNotBlank() }?.let { Text(it, color = XauxaColor.TextSecondary, fontSize = XauxaType.Label) }
            Row(horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
                XauxaPrimaryButton(label = "Show QR", onClick = { onAction(DestinationAction.ShowQr(destination.id)) })
                XauxaSecondaryButton(label = "Edit", onClick = { onAction(DestinationAction.Edit(destination.id)) })
            }
        }
    }
}
