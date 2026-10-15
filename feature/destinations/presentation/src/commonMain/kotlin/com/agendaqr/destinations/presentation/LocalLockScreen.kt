package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.agendaqr.core.ui.components.AmPrimaryButton
import com.agendaqr.core.ui.components.AmScreenScaffold
import com.agendaqr.core.ui.components.AmSecondaryButton
import com.agendaqr.core.ui.components.AmText
import com.agendaqr.core.ui.components.AmTextField
import com.agendaqr.core.ui.theme.AmColor
import com.agendaqr.core.ui.theme.AmSpacing
import com.agendaqr.core.ui.theme.AmType
import com.agendaqr.destinations.domain.BiometricAvailability

/**
 * Desbloqueo local. Sustituye al login remoto como puerta de entrada: se
 * muestra cuando ya existe un PIN configurado y la app está bloqueada.
 *
 * La biometría es un atajo opcional (solo cuando está disponible e inscrita);
 * el PIN es siempre la vía principal y el fallback ante cualquier fallo.
 */
@Composable
internal fun LocalLockScreen(
    state: LocalAccessUiState,
    onPinChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    onBiometric: (() -> Unit)? = null,
    onExploreDemo: (() -> Unit)? = null,
) {
    AmScreenScaffold(
        scrollable = true,
        verticalArrangement = Arrangement.spacedBy(AmSpacing.Xl),
        horizontalAlignment = Alignment.Start,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(AmSpacing.Xs)) {
            AmText(
                AppStrings.DesbloqueaAgendaQr,
                size = AmType.Title,
                fontWeight = FontWeight.SemiBold,
            )
            AmText(
                AppStrings.DesbloqueaAgendaQrDescripcion,
                size = AmType.Body,
                color = AmColor.TextSecondary,
            )
        }
        AmTextField(
            label = AppStrings.Pin,
            value = state.pin,
            onValueChange = onPinChanged,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.NumberPassword,
                imeAction = ImeAction.Done,
            ),
            keyboardActions = KeyboardActions(onDone = { onSubmit() }),
            visualTransformation = PasswordVisualTransformation(),
            isError = state.error.isPinFieldError(),
            errorMessage = state.error.pinFieldMessage(),
            helperMessage = if (state.remainingAttempts > 0) {
                AppStrings.IntentosRestantes + state.remainingAttempts
            } else {
                null
            },
        )
        AmPrimaryButton(
            AppStrings.Desbloquear,
            onClick = onSubmit,
            modifier = Modifier.fillMaxWidth(),
        )
        if (state.biometricAvailability == BiometricAvailability.Available && onBiometric != null) {
            AmSecondaryButton(
                AppStrings.UsarBiometria,
                onClick = onBiometric,
                modifier = Modifier.fillMaxWidth(),
            )
        }
        if (state.biometricFallbackVisible) {
            AmText(
                AppStrings.BiometriaNoDisponible,
                size = AmType.Body,
                color = AmColor.TextSecondary,
            )
        }
        if (onExploreDemo != null) {
            AmText(
                AppStrings.ExplorarDemostracionDescripcion,
                size = AmType.Body,
                color = AmColor.TextSecondary,
            )
            AmSecondaryButton(
                AppStrings.ExplorarDemostracion,
                onClick = onExploreDemo,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
