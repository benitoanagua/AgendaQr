package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.AqAlert
import com.agendaqr.core.ui.components.AqButton
import com.agendaqr.core.ui.components.AqButtonVariant
import com.agendaqr.core.ui.components.AqKeypad
import com.agendaqr.core.ui.components.AqPinDots
import com.agendaqr.core.ui.components.AqScreen
import com.agendaqr.core.ui.components.AqText
import com.agendaqr.core.ui.components.AqTextInput
import com.agendaqr.core.ui.components.AqToastHost
import com.agendaqr.core.ui.components.AqTone
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.destinations.domain.BiometricAvailability
import com.agendaqr.destinations.domain.PinPolicy

/**
 * S00 — Pantallas de acceso local sin cuenta (onboarding de PIN y desbloqueo).
 *
 * Son pantallas *controladas*: el ViewModel es el dueño del PIN y la
 * confirmación ([LocalAccessUiState.pin] / [.confirmation]); la pantalla solo
 * refleja el estado y notifica cambios ([onPinChanged],
 * [onConfirmationChanged]) y la acción de submit ([onSubmit]).
 *
 * La entrada usa el teclado propio ([AqKeypad]) y el indicador de puntos
 * ([AqPinDots]), nunca un campo de texto libre. Todo el copy vive en
 * [AppStrings]; ningún literal visual aquí.
 */
@Composable
internal fun LocalOnboardingScreen(
    state: LocalAccessUiState,
    viewModel: LocalAccessViewModel? = null,
    appHosts: AppHosts? = null,
    onPinChanged: (String) -> Unit = {},
    onConfirmationChanged: (String) -> Unit = {},
    onSubmit: (String) -> Unit = {},
    onExploreDemo: () -> Unit = {},
) {
    val creating = remember { mutableStateOf(true) }
    val pin = state.pin
    val confirmation = state.confirmation
    // Paso 1: crear el código. Paso 2: confirmarlo. El ViewModel no se
    // invoca hasta que ambos pasos están listos (submitOnboarding).
    val readyToContinue = pin.length >= PinPolicy.MIN_LENGTH
    val readyToSave = confirmation.isNotEmpty() && confirmation == pin
    val activeValue = if (creating.value) pin else confirmation
    // El teclado propio emite dígitos sueltos pero el estado guarda el PIN
    // completo: hay que componer el valor nuevo antes de notificarlo.
    // Con ViewModel se delega en él; sin él la pantalla es controlada y
    // notifica por los callbacks.
    val updatePin: (String) -> Unit = { value ->
        if (viewModel != null) viewModel.onPinChanged(value) else onPinChanged(value)
    }
    val updateConfirmation: (String) -> Unit = { value ->
        if (viewModel != null) viewModel.onConfirmationChanged(value) else onConfirmationChanged(value)
    }
    val onDigit = { digit: Char ->
        if (creating.value) updatePin(pin + digit) else updateConfirmation(confirmation + digit)
    }
    val onBackspace = {
        if (creating.value) updatePin(pin.dropLast(1)) else updateConfirmation(confirmation.dropLast(1))
    }
    // Al confirmar el primer código se pasa al paso de confirmación sin
    // invocar aún al ViewModel (solo guarda cuando ambos pasos coinciden).
    val onFinishCreating = { creating.value = false }
    val toastHost = appHosts?.toastHost
    AqScreen(
        title = if (creating.value) AppStrings.ConfiguraTuPin else AppStrings.ConfirmaElPin,
        backLabel = AppStrings.Volver,
        statusContent = appHosts?.status,
        bottomAction = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
            ) {
                if (creating.value) {
                    AqButton(
                        label = AppStrings.Continuar,
                        onClick = onFinishCreating,
                        enabled = readyToContinue,
                        modifier = Modifier.fillMaxWidth(),
                    )
                } else {
                    AqButton(
                        label = AppStrings.GuardarPin,
                        onClick = {
                            if (viewModel != null) viewModel.submitOnboarding() else onSubmit(pin)
                        },
                        variant = AqButtonVariant.Primary,
                        enabled = readyToSave,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        },
        toastHost = toastHost?.let { host -> { AqToastHost(host) } },
        scrollable = true,
    ) {
        state.error?.let { err ->
            com.agendaqr.core.ui.components.AqAlert(
                tone = AqTone.Danger,
                message = err.toMessage(),
                dismissLabel = AppStrings.Cerrar,
                onDismiss = { },
            )
        }
        if (!creating.value) {
            AqText(
                text = AppStrings.ConfirmaElPin,
                size = com.agendaqr.core.ui.theme.AqType.Body,
                color = com.agendaqr.core.ui.theme.AqColor.TextSecondary,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        AqPinDots(
            filled = activeValue.length,
            total = PinPolicy.MAX_LENGTH,
            error = state.error != null,
        )
        AqKeypad(
            onDigit = onDigit,
            onBackspace = onBackspace,
            backspaceDescription = AppStrings.BorrarDigito,
        )
        // Acciones secundarias dentro del área scrollable: el dock inferior
        // reserva solo la acción principal del paso (S00, V1.16).
        AqButton(
            label = AppStrings.ExplorarDemostracion,
            onClick = onExploreDemo,
            variant = AqButtonVariant.Text,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

/** Mensaje de copy para cada error de acceso local. */
private fun LocalAccessError.toMessage(): String = when (this) {
    LocalAccessError.TooShort -> AppStrings.PinErrorCorto
    LocalAccessError.TooLong -> AppStrings.PinErrorLargo
    LocalAccessError.NonDigits -> AppStrings.PinErrorSoloDigitos
    LocalAccessError.Mismatch -> AppStrings.PinErrorNoCoinciden
    LocalAccessError.Incorrect -> AppStrings.PinErrorIncorrecto
    LocalAccessError.LockedOut -> AppStrings.PinErrorBloqueado
    LocalAccessError.NotConfigured -> AppStrings.PinErrorIncorrecto
}

@Composable
internal fun LocalLockScreen(
    state: LocalAccessUiState,
    viewModel: LocalAccessViewModel? = null,
    appHosts: AppHosts? = null,
    onPinChanged: (String) -> Unit = {},
    onSubmit: (String) -> Unit = {},
    onCancel: () -> Unit = {},
    onBiometric: () -> Unit = {},
    onExploreDemo: () -> Unit = {},
) {
    val pin = state.pin
    val readyToUnlock = pin.isNotEmpty()
    val showBiometric = state.biometricAvailability == BiometricAvailability.Available
    val toastHost = appHosts?.toastHost
    // Misma regla que el onboarding: el teclado compone el valor completo
    // y la pantalla delega en el ViewModel o notifica por callbacks.
    val updatePin: (String) -> Unit = { value ->
        if (viewModel != null) viewModel.onPinChanged(value) else onPinChanged(value)
    }

    // La biometría se solicita una sola vez por bloqueo (cambio de
    // lockSequence). Con el mismo bloqueo no se relanza al recomponerse.
    LaunchedEffect(state.lockSequence, state.biometricAvailability) {
        if (showBiometric) {
            onBiometric()
        }
    }

    AqScreen(
        title = AppStrings.DesbloqueaAgendaQr,
        backLabel = AppStrings.Cancelar,
        statusContent = appHosts?.status,
        bottomAction = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
            ) {
                AqButton(
                    label = AppStrings.Desbloquear,
                    onClick = {
                        if (viewModel != null) viewModel.submitUnlock() else onSubmit(pin)
                    },
                    variant = com.agendaqr.core.ui.components.AqButtonVariant.Primary,
                    enabled = readyToUnlock,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        toastHost = toastHost?.let { host -> { AqToastHost(host) } },
        scrollable = true,
    ) {
        state.error?.let { err ->
            com.agendaqr.core.ui.components.AqAlert(
                tone = AqTone.Danger,
                message = err.toMessage(),
                dismissLabel = AppStrings.Cerrar,
                onDismiss = { },
            )
        }
        AqTextInput(
            label = AppStrings.LabelPin,
            value = pin,
            onValueChange = updatePin,
        )
        AqPinDots(
            filled = pin.length,
            total = PinPolicy.MAX_LENGTH,
            error = state.error != null,
        )
        AqKeypad(
            onDigit = { digit -> updatePin(pin + digit) },
            onBackspace = { updatePin(pin.dropLast(1)) },
            backspaceDescription = AppStrings.BorrarDigito,
        )
        // Acciones secundarias dentro del área scrollable: el dock inferior
        // reserva solo la acción principal (Desbloquear, S00 V1.16).
        if (showBiometric) {
            AqButton(
                label = AppStrings.UsarBiometria,
                onClick = onBiometric,
                variant = com.agendaqr.core.ui.components.AqButtonVariant.Text,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        if (!showBiometric) {
            AqButton(
                label = AppStrings.ExplorarDemostracion,
                onClick = onExploreDemo,
                variant = com.agendaqr.core.ui.components.AqButtonVariant.Text,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        AqButton(
            label = AppStrings.Cancelar,
            onClick = {
                if (viewModel != null) viewModel.lock() else onCancel()
            },
            variant = com.agendaqr.core.ui.components.AqButtonVariant.Text,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
