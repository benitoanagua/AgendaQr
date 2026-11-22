package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.AqButton
import com.agendaqr.core.ui.components.AqButtonVariant
import com.agendaqr.core.ui.components.AqScreen
import com.agendaqr.core.ui.components.AqText
import com.agendaqr.core.ui.components.AqTextInput
import com.agendaqr.core.ui.components.AqToastHost
import com.agendaqr.core.ui.theme.AqSpacing

@Composable
internal fun LocalLockScreen(
    state: LocalLockUiState,
    viewModel: LocalAccessViewModel,
    appHosts: AppHosts? = null,
    onSubmit: (String) -> Unit,
    onCancel: () -> Unit,
) {
    val pin by remember { mutableStateOf("") }
    val toastHost = appHosts?.toastHost
    AqScreen(
        title = AppStrings.Desbloquear,
        backLabel = AppStrings.Cancelar,
        statusContent = appHosts?.status,
        bottomAction = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
            ) {
                AqButton(
                    label = AppStrings.Desbloquear,
                    onClick = { onSubmit(pin) },
                    variant = com.agendaqr.core.ui.components.AqButtonVariant.Primary,
                    modifier = Modifier.fillMaxWidth(),
                )
                AqButton(
                    label = AppStrings.Cancelar,
                    onClick = onCancel,
                    variant = com.agendaqr.core.ui.components.AqButtonVariant.Text,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        toastHost = toastHost?.let { host -> { AqToastHost(host) } },
    ) {
        state.error?.let { err ->
            com.agendaqr.core.ui.components.AqAlert(
                tone = com.agendaqr.core.ui.theme.AqTone.Danger,
                message = err.dataStatus,
                dismissLabel = AppStrings.Cerrar,
                onDismiss = { },
            )
        }
        AqTextInput(
            label = AppStrings.LabelPin,
            value = pin,
            onValueChange = { pin = it },
        )
    }
}
