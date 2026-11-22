package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.AqAlert
import com.agendaqr.core.ui.components.AqAppBarAction
import com.agendaqr.core.ui.components.AqButton
import com.agendaqr.core.ui.components.AqButtonVariant
import com.agendaqr.core.ui.components.AqEmptyPlaceholder
import com.agendaqr.core.ui.components.AqIcon
import com.agendaqr.core.ui.components.AqIcons
import com.agendaqr.core.ui.components.AqMenuItem
import com.agendaqr.core.ui.components.AqScreen
import com.agendaqr.core.ui.components.AqSectionTitle
import com.agendaqr.core.ui.components.AqText
import com.agendaqr.core.ui.components.AqTextInput
import com.agendaqr.core.ui.components.AqToastHost
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.destinations.domain.AgendaSearchResult

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
            leadingIcon = AqIcons.Search,
        )
        val results = state.results
        if (results.isEmpty() && state.query.isNotBlank()) {
            AqEmptyPlaceholder(
                icon = AqIcons.Search,
                title = AppStrings.NoSeEncontraronResultados,
            )
        } else {
            LazyColumn(modifier = Modifier.fillMaxWidth()) {
                results.forEachIndexed { index, result ->
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
                tone = com.agendaqr.core.ui.theme.AqTone.Danger,
                message = state.error.dataStatus,
                dismissLabel = AppStrings.Cerrar,
                onDismiss = { },
            )
        }
    }
}
