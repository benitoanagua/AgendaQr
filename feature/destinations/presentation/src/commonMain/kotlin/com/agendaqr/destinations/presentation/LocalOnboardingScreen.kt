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
import com.agendaqr.core.ui.components.AmBottomAction
import com.agendaqr.core.ui.components.AmScreenScaffold
import com.agendaqr.core.ui.components.AmSecondaryButton
import com.agendaqr.core.ui.components.AmText
import com.agendaqr.core.ui.components.AmTextField
import com.agendaqr.core.ui.theme.AmColor
import com.agendaqr.core.ui.theme.AmSpacing
import com.agendaqr.core.ui.theme.AmType

/**
 * Primer arranque: el perfil local ya existe (se crea sin conexión) y aquí el
 * usuario configura su PIN. No es un login: no hay correo, contraseña ni red.
 *
 * Desde aquí también se puede entrar al modo de demostración (datos
 * ficticios aislados) sin configurar PIN ni cuenta.
 */
@Composable
internal fun LocalOnboardingScreen(
    state: LocalAccessUiState,
    onPinChanged: (String) -> Unit,
    onConfirmationChanged: (String) -> Unit,
    onSubmit: () -> Unit,
    onExploreDemo: (() -> Unit)? = null,
) {
    AmScreenScaffold(
        scrollable = true,
        verticalArrangement = Arrangement.spacedBy(AmSpacing.Xl),
        horizontalAlignment = Alignment.Start,
        // Guardar PIN fijo al pie: la acción aplica navigationBars + ime, así
        // que no queda bajo el teclado numérico ni bajo la barra del sistema.
        bottomAction = {
            AmBottomAction(label = AppStrings.GuardarPin, onClick = onSubmit)
        },
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(AmSpacing.Xs)) {
            AmText(
                AppStrings.ConfiguraTuPin,
                size = AmType.Title,
                fontWeight = FontWeight.SemiBold,
            )
            AmText(
                AppStrings.ConfiguraTuPinDescripcion,
                size = AmType.Body,
                color = AmColor.TextSecondary,
            )
        }
        Column(verticalArrangement = Arrangement.spacedBy(AmSpacing.Md)) {
            AmTextField(
                label = AppStrings.Pin,
                value = state.pin,
                onValueChange = onPinChanged,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.NumberPassword,
                    imeAction = ImeAction.Next,
                ),
                visualTransformation = PasswordVisualTransformation(),
                isError = state.error.isPinFieldError(),
                errorMessage = state.error.pinFieldMessage(),
            )
            AmTextField(
                label = AppStrings.ConfirmaElPin,
                value = state.confirmation,
                onValueChange = onConfirmationChanged,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.NumberPassword,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(onDone = { onSubmit() }),
                visualTransformation = PasswordVisualTransformation(),
                isError = state.error == LocalAccessError.Mismatch,
                errorMessage = if (state.error == LocalAccessError.Mismatch) {
                    AppStrings.PinErrorNoCoinciden
                } else {
                    null
                },
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
