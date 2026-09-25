package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.*
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.core.ui.theme.XauxaSpacing
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
        XauxaSearchBar(
            value = state.query,
            onValueChange = { onAction(GlobalSearchAction.QueryChanged(it)) },
            label = "Buscar en Agenda QR",
            placeholder = "Buscar",
            onClear = { onAction(GlobalSearchAction.Clear) },
        )
        state.error?.let { XauxaStatusBanner(it, danger = true) }
        when {
            state.query.isBlank() -> XauxaEmptyState(
                title = "Escribe para buscar",
                actionLabel = "Limpiar",
                onAction = { onAction(GlobalSearchAction.Clear) },
            )
            state.isSearching -> XauxaLoading()
            state.results.isEmpty() -> XauxaEmptyState(
                title = "Sin resultados",
                actionLabel = "Limpiar",
                onAction = { onAction(GlobalSearchAction.Clear) },
            )
            else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
                items(state.results, key = { it.type.name + ":" + it.id }) { result ->
                    XauxaListRow(
                        title = result.title,
                        subtitle = result.subtitle,
                        tone = XauxaTone.Info,
                        onClick = { onSelect(result) },
                        trailing = { XauxaCategoryChip(result.type.name) },
                    )
                }
            }
        }
    }
}