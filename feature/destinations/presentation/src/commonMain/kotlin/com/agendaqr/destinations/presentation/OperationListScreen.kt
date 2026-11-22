package com.agendaqr.destinations.presentation

import androidx.compose.runtime.Composable
import com.agendaqr.core.ui.components.AqToastHost

@Composable
internal fun OperationListScreen(
    state: OperationsUiState,
    viewModel: OperationsViewModel,
    appHosts: AppHosts? = null,
) {
    OperationListGrouped(
        state = state,
        viewModel = viewModel,
        appHosts = appHosts,
    )
    val toastHost = appHosts?.toastHost
    // El host de toasts se monta desde la pantalla padre (OperationListContent
    // / AqScreen); aquí no se duplica.
}
