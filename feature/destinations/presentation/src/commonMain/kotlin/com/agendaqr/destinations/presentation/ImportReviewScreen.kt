package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.AqAlert
import com.agendaqr.core.ui.components.AqButton
import com.agendaqr.core.ui.components.AqButtonVariant
import com.agendaqr.core.ui.components.AqEmptyPlaceholder
import com.agendaqr.core.ui.components.AqIcon
import com.agendaqr.core.ui.components.AqIcons
import com.agendaqr.core.ui.components.AqMenuItem
import com.agendaqr.core.ui.components.AqScreen
import com.agendaqr.core.ui.components.AqText
import com.agendaqr.core.ui.components.AqToastHost
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.destinations.domain.*

@Composable
internal fun ImportReviewScreen(
    state: ImportBatchUiState,
    viewModel: OperationsViewModel,
    appHosts: AppHosts? = null,
    onBack: () -> Unit = {},
) {
    val toastHost = appHosts?.toastHost
    AqScreen(
        title = AppStrings.RevisarImportacion,
        onBack = onBack,
        backLabel = AppStrings.Volver,
        statusContent = appHosts?.status,
        toastHost = toastHost?.let { host -> { AqToastHost(host) } },
    ) {
        state.error?.let { err ->
            AqAlert(
                tone = com.agendaqr.core.ui.theme.AqTone.Danger,
                message = err.dataStatus,
                dismissLabel = AppStrings.Cerrar,
                onDismiss = { },
            )
        }
        val batch = when (state) {
            is ImportBatchUiState.Review -> state.batch
            else -> null
        }
        if (batch == null) {
            AqEmptyPlaceholder(
                icon = AqIcons.Search,
                title = AppStrings.SinElementosParaRevisar,
            )
        } else {
            Column(modifier = Modifier.fillMaxWidth()) {
                AqSectionTitle(text = AppStrings.ElementosARevisar + " (" + batch.pendingItems().size + ")")
                androidx.compose.foundation.lazy.LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    batch.pendingItems().forEachIndexed { index, candidate ->
                        AqMenuItem(
                            title = candidate.displayName ?: AppStrings.ElementoImportado,
                            subtitle = candidate.extension.orEmpty(),
                            onClick = { viewModel.onAction(ImportBatchAction.ViewExisting(candidate.id)) },
                        )
                    }
                }
            }
        }
    }
}
