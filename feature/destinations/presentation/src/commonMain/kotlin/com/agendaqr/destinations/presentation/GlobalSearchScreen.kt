package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.AmSnackbarHost
import com.agendaqr.core.ui.components.*
import com.agendaqr.core.ui.components.AmNavChromeState
import com.agendaqr.core.ui.theme.AmColor
import com.agendaqr.core.ui.theme.AmSpacing
import com.agendaqr.destinations.domain.AgendaSearchResult

@Composable
fun GlobalSearchScreen(
    state: GlobalSearchUiState,
    navChrome: AmNavChromeState? = null,
    appHosts: AppHosts? = null,
    onAction: (GlobalSearchAction) -> Unit,
    onSelect: (AgendaSearchResult) -> Unit,
) {
    // V1.4 (§12): Buscar es destino raíz — barra superior con título y
    // navegación inferior; el campo de búsqueda es el cuerpo de la
    // superficie. Los resultados siguen agrupados por tipo con
    // encabezados de sección estándar.
    AmScreenScaffold(
        topBar = {
            AmTopBar(title = AppStrings.Buscar)
        },
        navDestinations = navChrome?.destinations.orEmpty(),
        navSelectedId = navChrome?.selectedId,
        onNavSelect = { navChrome?.onSelect?.invoke(it) },
        snackbarHost = appHosts?.feedback?.let { host -> { AmSnackbarHost(host) } },
        statusContent = appHosts?.status,
    ) {
        AmSearchField(
            value = state.query,
            onValueChange = { onAction(GlobalSearchAction.QueryChanged(it)) },
            label = AppStrings.BuscarEnAgendaQr,
            placeholder = AppStrings.Buscar,
            onClear = { onAction(GlobalSearchAction.Clear) },
            clearLabel = AppStrings.Limpiar,
            // Entrar a Buscar es intención explícita de búsqueda: foco
            // inmediato con teclado de texto (sin autocompletado agresivo).
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Text,
                imeAction = androidx.compose.ui.text.input.ImeAction.Search,
            ),
            autoFocus = true,
        )
        state.error?.let { err ->
            AmStatusBanner(
                err.display(),
                tone = AmTone.Danger,
                actionLabel = err.action.label,
                // REINTENTAR: relanza la búsqueda con la misma consulta.
                onAction = { onAction(GlobalSearchAction.QueryChanged(state.query)) },
                onDismiss = { onAction(GlobalSearchAction.ClearError) },
                dismissLabel = AppStrings.Descartar,
            )
        }
        when {
            state.query.isBlank() -> AmText(
                AppStrings.searchIntroHint,
                color = AmColor.TextSecondary,
            )
            state.isSearching -> AmLoading(message = AppStrings.BuscandoEnAgendaQr)
            state.results.isEmpty() -> AmEmptyState(
                icon = AmIcons.SearchX,
                title = AppStrings.SinResultados,
                subtitle = AppStrings.PruebaConOtraPalabraO,
                actionLabel = AppStrings.Limpiar,
                onAction = { onAction(GlobalSearchAction.Clear) },
            )
            else -> LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(AmSpacing.Md),
            ) {
                // S04/S05 (M7): resultados heterogéneos agrupados por tipo,
                // cada grupo con su encabezado de sección (§4: el tipo se
                // expresa con texto/semántica) y su cuerpo de lista.
                val groups = state.results.groupBy { it.type }
                com.agendaqr.destinations.domain.AgendaSearchResultType.entries.forEach { type ->
                    val group = groups[type].orEmpty()
                    if (group.isNotEmpty()) {
                        item(key = "header:" + type.name) {
                            AmSectionHeader(text = searchResultTypeLabel(type))
                        }
                        item(key = "body:" + type.name) {
                            AmListBody {
                                group.forEachIndexed { index, result ->
                                    AmListRow(
                                        title = searchResultTitle(result),
                                        subtitle = searchResultSubtitle(result),
                                        // El marcador lateral solo aparece
                                        // con tono de estado o acento de
                                        // contexto REALES; un resultado de
                                        // búsqueda no es ninguno.
                                        onClick = { onSelect(result) },
                                        showDivider = index < group.lastIndex,
                                    )
                                }
                            }
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
