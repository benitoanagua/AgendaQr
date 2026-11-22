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
import com.agendaqr.core.ui.components.AqEmptyPlaceholder
import com.agendaqr.core.ui.components.AqIcon
import com.agendaqr.core.ui.components.AqIcons
import com.agendaqr.core.ui.components.AqScreen
import com.agendaqr.core.ui.components.AqText
import com.agendaqr.core.ui.components.AqTextInput
import com.agendaqr.core.ui.components.AqToastHost
import com.agendaqr.core.ui.theme.AqSpacing

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
        AqSectionTitlePlaceholder(text = AppStrings.Configuracion)
        AqText(
            text = AppStrings.Sincronizacion,
            size = com.agendaqr.core.ui.theme.AqType.Body,
            color = com.agendaqr.core.ui.theme.AqColor.TextSecondary,
        )
        AqButton(
            label = AppStrings.CerrarSesion,
            onClick = { viewModel.onAction(AccountAction.SignOut) },
            variant = AqButtonVariant.Text,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun AqSectionTitlePlaceholder(text: String) {
    com.agendaqr.core.ui.components.AqText(
        text = text,
        size = com.agendaqr.core.ui.theme.AqType.HeadingSm,
        fontWeight = com.agendaqr.core.ui.theme.AqWeight.SemiBold,
        color = com.agendaqr.core.ui.theme.AqColor.TextPrimary,
    )
}
