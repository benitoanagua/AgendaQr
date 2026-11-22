package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.AqAlert
import com.agendaqr.core.ui.components.AqButton
import com.agendaqr.core.ui.components.AqButtonVariant
import com.agendaqr.core.ui.components.AqCheckbox
import com.agendaqr.core.ui.components.AqEmptyPlaceholder
import com.agendaqr.core.ui.components.AqIcon
import com.agendaqr.core.ui.components.AqIcons
import com.agendaqr.core.ui.components.AqMenuItem
import com.agendaqr.core.ui.components.AqModal
import com.agendaqr.core.ui.components.AqScreen
import com.agendaqr.core.ui.components.AqSectionTitle
import com.agendaqr.core.ui.components.AqSwitch
import com.agendaqr.core.ui.components.AqTag
import com.agendaqr.core.ui.components.AqText
import com.agendaqr.core.ui.components.AqTextInput
import com.agendaqr.core.ui.components.AqToastHost
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.destinations.domain.*

@Composable
internal fun DestinationEditorScreen(
    state: DestinationsUiState,
    viewModel: DestinationsViewModel,
    appHosts: AppHosts? = null,
    destination: Destination? = null,
    onBack: () -> Unit = {},
) {
    val isNew = destination == null
    val name by rememberSaveable { mutableStateOf(destination?.name.orEmpty()) }
    val note by rememberSaveable { mutableStateOf(destination?.note.orEmpty()) }
    val contextId by rememberSaveable { mutableStateOf(destination?.contextId) }
    val favorite by rememberSaveable { mutableStateOf(destination?.favorite ?: false) }
    val toastHost = appHosts?.toastHost
    AqScreen(
        title = if (isNew) AppStrings.CrearDestino else AppStrings.EditarDestino,
        onBack = onBack,
        backLabel = AppStrings.Volver,
        statusContent = appHosts?.status,
        bottomAction = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                AqButton(
                    label = AppStrings.Guardar,
                    onClick = {
                        viewModel.onAction(DestinationAction.Save(
                            id = destination?.id,
                            name = name,
                            note = note,
                            contextId = contextId,
                            favorite = favorite,
                        ))
                    },
                    variant = com.agendaqr.core.ui.components.AqButtonVariant.Primary,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
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
        AqSectionTitle(text = AppStrings.LabelNombre)
        AqTextInput(
            label = AppStrings.LabelNombre,
            value = name,
            onValueChange = { name = it },
        )
        AqSectionTitle(text = AppStrings.LabelNota)
        AqTextInput(
            label = AppStrings.LabelNota,
            value = note,
            onValueChange = { note = it },
        )
        AqSectionTitle(text = AppStrings.Contexto)
        AqMenuItem(
            title = contextId?.let { id ->
                state.contexts.firstOrNull { it.id == id }?.name ?: AppStrings.SinContexto
            } ?: AppStrings.SeleccionarContexto,
            subtitle = AppStrings.Opcional,
            onClick = { },
            leading = {
                AqTag(text = contextId?.take(1).uppercase() ?: "?")
            },
        )
        AqSectionTitle(text = AppStrings.Favorito)
        AqSwitch(
            label = AppStrings.MarcarComoFavorito,
            checked = favorite,
            onCheckedChange = { favorite = it },
        )
        if (isNew) {
            AqButton(
                label = AppStrings.CrearContexto,
                onClick = { },
                variant = AqButtonVariant.Text,
            )
        }
    }
}

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
