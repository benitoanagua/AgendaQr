package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import com.agendaqr.core.ui.components.XauxaEmptyState
import com.agendaqr.core.ui.components.XauxaSecondaryButton
import com.agendaqr.core.ui.components.XauxaStatusBanner
import com.agendaqr.core.ui.components.XauxaTile
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.core.ui.theme.XauxaSpacing
import com.agendaqr.core.ui.theme.XauxaType
import com.agendaqr.destinations.domain.AgendaSearchResult

@Composable
fun GlobalSearchScreen(
    state: GlobalSearchUiState,
    onAction: (GlobalSearchAction) -> Unit,
    onSelect: (AgendaSearchResult) -> Unit,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(XauxaSpacing.Xxl),
        verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Lg),
    ) {
        XauxaSecondaryButton(label = "Volver", onClick = onBack)
        OutlinedTextField(
            value = state.query,
            onValueChange = { onAction(GlobalSearchAction.QueryChanged(it)) },
            modifier = Modifier.fillMaxWidth(),
            label = { Text("Buscar en Agenda QR") },
            shape = RectangleShape,
            singleLine = true,
        )
        state.error?.let { XauxaStatusBanner(it, danger = true) }
        when {
            state.query.isBlank() -> XauxaEmptyState(
                title = "Escribe para buscar",
                actionLabel = "Limpiar",
                onAction = { onAction(GlobalSearchAction.Clear) },
            )
            state.isSearching -> Text("Buscando…", color = XauxaColor.TextSecondary)
            state.results.isEmpty() -> Text("Sin resultados", color = XauxaColor.TextSecondary)
            else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
                items(state.results, key = { it.type.name + ":" + it.id }) { result ->
                    XauxaTile(onClick = { onSelect(result) }) {
                        Column(
                            modifier = Modifier.fillMaxWidth().padding(XauxaSpacing.Lg),
                            verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm),
                        ) {
                            Text(result.type.name, fontSize = XauxaType.Label, color = XauxaColor.TextSecondary)
                            Text(result.title, fontSize = XauxaType.Title, color = XauxaColor.TextPrimary)
                            result.subtitle?.let { Text(it, fontSize = XauxaType.Label, color = XauxaColor.TextSecondary) }
                        }
                    }
                }
            }
        }
    }
}
