package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.AqAlert
import com.agendaqr.core.ui.components.AqChipOption
import com.agendaqr.core.ui.components.AqChipRow
import com.agendaqr.core.ui.components.AqEmptyPlaceholder
import com.agendaqr.core.ui.components.AqIcon
import com.agendaqr.core.ui.components.AqIcons
import com.agendaqr.core.ui.components.AqMenuItem
import com.agendaqr.core.ui.components.AqNavChromeState
import com.agendaqr.core.ui.components.AqNavDestination
import com.agendaqr.core.ui.components.AqScreen
import com.agendaqr.core.ui.components.AqSectionTitle
import com.agendaqr.core.ui.components.AqText
import com.agendaqr.core.ui.components.AqTextInput
import com.agendaqr.core.ui.components.AqToastHost
import com.agendaqr.core.ui.components.AqTone
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.destinations.domain.AgendaSearchResult
import com.agendaqr.destinations.domain.AgendaSearchResultType

@Composable
fun GlobalSearchScreen(
    state: GlobalSearchUiState,
    navChrome: AqNavChromeState? = null,
    appHosts: AppHosts? = null,
    onAction: (GlobalSearchAction) -> Unit,
    onSelect: (AgendaSearchResult) -> Unit,
    autoFocus: Boolean = true,
) {
    val toastHost = appHosts?.toastHost
    val focusRequester = remember { FocusRequester() }
    LaunchedEffect(autoFocus) {
        if (autoFocus) {
            focusRequester.requestFocus()
        }
    }
    AqScreen(
        title = AppStrings.Buscar,
        navDestinations = navChrome?.destinations.orEmpty(),
        navSelectedId = navChrome?.selectedId,
        onNavSelect = { navChrome?.onSelect?.invoke(it) },
        statusContent = appHosts?.status,
        toastHost = toastHost?.let { host -> { AqToastHost(host) } },
    ) {
        AqTextInput(
            label = AppStrings.BuscarEnAgendaQr,
            value = state.query,
            onValueChange = { onAction(GlobalSearchAction.QueryChanged(it)) },
            placeholder = AppStrings.Buscar,
            focusRequester = focusRequester,
        )
        val results = state.results
        if (state.query.isBlank()) {
            AqText(
                text = AppStrings.searchIntroHint,
                size = com.agendaqr.core.ui.theme.AqType.Body,
                color = com.agendaqr.core.ui.theme.AqColor.TextSecondary,
                modifier = Modifier.fillMaxWidth(),
            )
        } else if (results.isEmpty()) {
            AqEmptyPlaceholder(
                icon = AqIcons.Search,
                title = AppStrings.NoSeEncontraronResultados,
            )
        } else {
            val types = results.map { it.type }.distinct()
            // Filtro por tipo (S04): antes eran botones sin acción y sin
            // estado seleccionado; ahora es un grupo de chips funcional.
            var selectedFilter by remember { mutableStateOf<AgendaSearchResultType?>(null) }
            val activeFilter = selectedFilter?.takeIf { it in types }
            val visibleResults = if (activeFilter == null) results else results.filter { it.type == activeFilter }
            if (types.size > 1) {
                val filterTypes: List<AgendaSearchResultType?> = listOf<AgendaSearchResultType?>(null) + types
                AqChipRow(
                    options = filterTypes.map { type ->
                        AqChipOption(label = if (type == null) AppStrings.Todos else typeDisplayName(type))
                    },
                    selectedIndex = filterTypes.indexOf(activeFilter),
                    onSelect = { index -> selectedFilter = filterTypes.getOrNull(index) },
                    horizontalScroll = true,
                )
            } else if (types.size == 1) {
                // Encabezado del grupo único con conteo (S04).
                AqSectionTitle(
                    text = typeDisplayName(types.first()) + AppStrings.SectionCountOpen + results.size + AppStrings.SectionCountClose
                )
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
            ) {
                visibleResults.forEachIndexed { index, result ->
                    AqMenuItem(
                        title = result.title,
                        subtitle = result.subtitle.orEmpty(),
                        onClick = { onSelect(result) },
                        leading = {
                            AqIcon(
                                imageVector = AqIcons.Search,
                                contentDescription = null,
                            )
                        },
                    )
                }
            }
        }
        if (state.error != null) {
            AqAlert(
                tone = AqTone.Danger,
                message = state.error.dataStatus,
                dismissLabel = AppStrings.Cerrar,
                onDismiss = { },
            )
        }
    }
}

/** Nombre visible de un tipo de resultado de búsqueda para los chips de filtro (S04). */
private fun typeDisplayName(type: AgendaSearchResultType): String = when (type) {
    AgendaSearchResultType.QR -> AppStrings.TipoQr
    AgendaSearchResultType.ACTIVITY -> AppStrings.TipoActividad
    AgendaSearchResultType.COMPROBANTE -> AppStrings.TipoComprobante
    AgendaSearchResultType.CONTEXT -> AppStrings.TipoContexto
}
