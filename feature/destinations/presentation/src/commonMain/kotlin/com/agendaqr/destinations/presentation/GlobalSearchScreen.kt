package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import com.agendaqr.destinations.presentation.AppStrings
import com.agendaqr.core.ui.components.XauxaText
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
        XauxaSecondaryButton(label = AppStrings.Volver, onClick = onBack)
        XauxaSearchBar(
            value = state.query,
            onValueChange = { onAction(GlobalSearchAction.QueryChanged(it)) },
            label = AppStrings.BuscarEnAgendaQr,
            placeholder = AppStrings.Buscar,
            onClear = { onAction(GlobalSearchAction.Clear) },
        )
        state.error?.let { err ->
            XauxaStatusBanner(
                err.display(),
                tone = XauxaTone.Danger,
                actionLabel = err.action.label,
                // REINTENTAR: relanza la búsqueda con la misma consulta.
                onAction = { onAction(GlobalSearchAction.QueryChanged(state.query)) },
                onDismiss = { onAction(GlobalSearchAction.ClearError) },
            )
        }
        when {
            state.query.isBlank() -> XauxaText(
                AppStrings.searchIntroHint,
                color = XauxaColor.TextSecondary,
            )
            state.isSearching -> XauxaLoading(message = AppStrings.BuscandoEnAgendaQr)
            state.results.isEmpty() -> XauxaEmptyState(
                title = AppStrings.SinResultados,
                subtitle = AppStrings.PruebaConOtraPalabraO,
                actionLabel = AppStrings.Limpiar,
                onAction = { onAction(GlobalSearchAction.Clear) },
            )
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize().weight(1f),
                verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm),
            ) {
                items(state.results, key = { it.type.name + ":" + it.id }) { result ->
                    XauxaListRow(
                        title = searchResultTitle(result),
                        subtitle = searchResultSubtitle(result),
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

/**
 * El dominio emite `type.name`/`provenance.name` como fallback de texto; aquí
 * se traducen a la semántica visible (nunca enums técnicos en UI).
 */
internal fun searchResultTitle(result: AgendaSearchResult): String = when (result.title) {
    "PAGO" -> "Pago"
    "COBRO" -> "Cobro"
    else -> result.title
}

internal fun searchResultSubtitle(result: AgendaSearchResult): String? {
    val subtitle = result.subtitle ?: return null
    val provenance = runCatching {
        com.agendaqr.destinations.domain.ReceiptProvenance.valueOf(subtitle)
    }.getOrNull()
    return provenance?.let { receiptProvenanceLabel(it) } ?: subtitle
}
