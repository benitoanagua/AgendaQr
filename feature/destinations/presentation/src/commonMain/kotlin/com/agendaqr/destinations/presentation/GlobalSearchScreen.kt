package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
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
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(XauxaSpacing.Xxl)
            .imePadding(),
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
        state.error?.let { XauxaStatusBanner(it, tone = XauxaTone.Danger, onDismiss = { onAction(GlobalSearchAction.ClearError) }) }
        when {
            state.query.isBlank() -> Text(
                "Busca destinos, operaciones, contextos o comprobantes.",
                color = XauxaColor.TextSecondary,
            )
            state.isSearching -> XauxaLoading(message = "Buscando en Agenda QR…")
            state.results.isEmpty() -> XauxaEmptyState(
                title = "Sin resultados",
                subtitle = "Prueba con otra palabra o revisa la ortografía.",
                actionLabel = "Limpiar",
                onAction = { onAction(GlobalSearchAction.Clear) },
            )
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize().weight(1f),
                verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm),
            ) {
                items(state.results, key = { it.type.name + ":" + it.id }) { result ->
                    XauxaListRow(
                        title = result.title,
                        subtitle = result.subtitle,
                        tone = XauxaTone.Info,
                        onClick = { onSelect(result) },
                        trailing = { XauxaCategoryChip(searchResultTypeLabel(result.type)) },
                    )
                }
            }
        }
    }
}

private fun searchResultTypeLabel(type: com.agendaqr.destinations.domain.AgendaSearchResultType): String =
    when (type) {
        com.agendaqr.destinations.domain.AgendaSearchResultType.CONTEXT -> "Contexto"
        com.agendaqr.destinations.domain.AgendaSearchResultType.QR -> "QR"
        com.agendaqr.destinations.domain.AgendaSearchResultType.ACTIVITY -> "Actividad"
        com.agendaqr.destinations.domain.AgendaSearchResultType.COMPROBANTE -> "Comprobante"
    }
