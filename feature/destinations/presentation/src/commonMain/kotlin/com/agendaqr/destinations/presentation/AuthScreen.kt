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
import com.agendaqr.core.ui.components.AqScreen
import com.agendaqr.core.ui.components.AqText
import com.agendaqr.core.ui.components.AqTextInput
import com.agendaqr.core.ui.components.AqToastHost
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqType

@Composable
internal fun AuthScreen(
    state: AuthUiState,
    viewModel: AuthViewModel,
    appHosts: AppHosts? = null,
    onBack: () -> Unit = {},
) {
    val email by remember { mutableStateOf("") }
    val password by remember { mutableStateOf("") }
    val toastHost = appHosts?.toastHost
    AqScreen(
        title = AppStrings.AccesoLocal,
        onBack = onBack,
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
        AqTextInput(
            label = AppStrings.LabelCorreo,
            value = email,
            onValueChange = { email = it },
            leadingIcon = AqIcons.Mail,
        )
        AqTextInput(
            label = AppStrings.LabelContrasena,
            value = password,
            onValueChange = { password = it },
            leadingIcon = AqIcons.Lock,
        )
        AqButton(
            label = AppStrings.Desbloquear,
            onClick = { viewModel.onAction(AuthAction.Unlock(email, password)) },
            variant = com.agendaqr.core.ui.components.AqButtonVariant.Primary,
            modifier = Modifier.fillMaxWidth(),
            enabled = state !is AuthUiState.Unlocking,
        )
    }
}

@Composable
internal fun AccountScreen(
    state: AccountUiState,
    viewModel: AccountViewModel,
    appHosts: AppHosts? = null,
    onBack: () -> Unit = {},
) {
    val toastHost = appHosts?.toastHost
    AqScreen(
        title = AppStrings.Cuenta,
        onBack = onBack,
        backLabel = AppStrings.Volver,
        statusContent = appHosts?.status,
        toastHost = toastHost?.let { host -> { AqToastHost(host) } },
    ) {
        AqSectionTitle(text = AppStrings.Configuracion)
        AqText(
            text = AppStrings.Sincronizacion,
            size = AqType.Body,
            color = AqColor.TextSecondary,
        )
        AqButton(
            label = AppStrings.CerrarSesion,
            onClick = { viewModel.onAction(AccountAction.SignOut) },
            variant = AqButtonVariant.Text,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
