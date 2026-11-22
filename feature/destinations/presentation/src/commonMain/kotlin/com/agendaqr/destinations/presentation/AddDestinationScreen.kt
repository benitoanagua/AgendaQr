package com.agendaqr.destinations.presentation

import androidx.compose.runtime.Composable

@Composable
internal fun AddDestinationScreen(
    state: DestinationsUiState,
    viewModel: DestinationsViewModel,
    appHosts: AppHosts? = null,
    onBack: () -> Unit = {},
) {
    DestinationEditorScreen(
        state = state,
        viewModel = viewModel,
        appHosts = appHosts,
        destination = null,
        onBack = onBack,
    )
}
