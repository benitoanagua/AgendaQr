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
internal fun LocalOnboardingScreen(
    state: LocalOnboardingUiState,
    viewModel: LocalAccessViewModel,
    appHosts: AppHosts? = null,
    onSubmit: (String) -> Unit,
) {
    val pin by remember { mutableStateOf("") }
    val confirm by remember { mutableStateOf("") }
    val toastHost = appHosts?.toastHost
    AqScreen(
        title = AppStrings.ConfigurarPin,
        backLabel = AppStrings.Volver,
        statusContent = appHosts?.status,
        bottomAction = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
            ) {
                AqButton(
                    label = AppStrings.GuardarPin,
                    onClick = { onSubmit(pin) },
                    variant = AqButtonVariant.Primary,
                    enabled = pin.isNotEmpty() && pin == confirm,
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
            label = AppStrings.LabelNuevoPin,
            value = pin,
            onValueChange = { pin = it },
        )
        AqTextInput(
            label = AppStrings.LabelConfirmarPin,
            value = confirm,
            onValueChange = { confirm = it },
        )
        AqButton(
            label = AppStrings.MostrarQR,
            onClick = { },
            variant = AqButtonVariant.Text,
        )
        AqEmptyPlaceholder(
            icon = AqIcons.Lock,
            title = AppStrings.SinPinConfigurado,
        )
    }
}

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

@Composable
internal fun ComprobanteViewer(
    state: ComprobanteUiState,
    viewModel: ComprobanteViewModel,
    appHosts: AppHosts? = null,
    onBack: () -> Unit = {},
) {
    val toastHost = appHosts?.toastHost
    AqScreen(
        title = AppStrings.Comprobante,
        onBack = onBack,
        backLabel = AppStrings.Volver,
        statusContent = appHosts?.status,
        toastHost = toastHost?.let { host -> { AqToastHost(host) } },
    ) {
        com.agendaqr.core.ui.components.AqQrPreview(
            encodedQr = state.comprobante?.qr?.encoded.orEmpty(),
            size = com.agendaqr.core.ui.theme.AqMetrics.QrReviewSize,
        )
    }
}
