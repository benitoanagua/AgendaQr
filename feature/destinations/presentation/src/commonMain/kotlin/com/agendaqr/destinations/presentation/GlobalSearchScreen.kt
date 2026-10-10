package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import com.agendaqr.destinations.presentation.AppStrings
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
    // V1.1 (M7): Volver en la app bar inferior; los resultados siguen
    // agrupados por tipo (ahora con encabezados de sección en acento).
    XauxaScreenScaffold(
        bottomBar = {
            XauxaAppBar(
                actions = emptyList(),
                onBack = onBack,
                backLabel = AppStrings.Volver,
            )
        },
    ) {
        XauxaSearchBar(
            value = state.query,
            onValueChange = { onAction(GlobalSearchAction.QueryChanged(it)) },
            label = AppStrings.BuscarEnAgendaQr,
            placeholder = AppStrings.Buscar,
            onClear = { onAction(GlobalSearchAction.Clear) },
            clearLabel = AppStrings.Limpiar,
        )
        state.error?.let { err ->
            XauxaStatusBanner(
                err.display(),
                tone = XauxaTone.Danger,
                actionLabel = err.action.label,
                // REINTENTAR: relanza la búsqueda con la misma consulta.
                onAction = { onAction(GlobalSearchAction.QueryChanged(state.query)) },
                onDismiss = { onAction(GlobalSearchAction.ClearError) },
                dismissLabel = AppStrings.Descartar,
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
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm),
            ) {
                // S04/S05 (M7): resultados heterogéneos agrupados por tipo,
                // cada grupo con su encabezado de sección (§4: el tipo se
                // expresa con texto/semántica).
                val groups = state.results.groupBy { it.type }
                com.agendaqr.destinations.domain.AgendaSearchResultType.entries.forEach { type ->
                    val group = groups[type].orEmpty()
                    if (group.isNotEmpty()) {
                        item(key = "header:" + type.name) {
                            XauxaSectionHeader(text = searchResultTypeLabel(type))
                        }
                        items(group, key = { it.type.name + ":" + it.id }) { result ->
                            XauxaListRow(
                                title = searchResultTitle(result),
                                subtitle = searchResultSubtitle(result),
                                // E-05: el marcador lateral solo aparece con
                                // tono de estado o acento de contexto REALES;
                                // un resultado de búsqueda no es ninguno.
                                onClick = { onSelect(result) },
                            )
                        }
                    }
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
