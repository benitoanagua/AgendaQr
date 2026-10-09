package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import com.agendaqr.destinations.presentation.AppStrings
import com.agendaqr.core.ui.components.XauxaText
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import com.agendaqr.core.ui.components.XauxaPrimaryButton
import com.agendaqr.core.ui.components.XauxaScreen
import com.agendaqr.core.ui.components.XauxaSecondaryButton
import com.agendaqr.core.ui.components.XauxaStatusBanner
import com.agendaqr.core.ui.components.XauxaTextInput
import com.agendaqr.core.ui.components.XauxaTone
import com.agendaqr.core.ui.components.XauxaHeading
import com.agendaqr.core.ui.components.XauxaPageTitle
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.core.ui.theme.XauxaSpacing
import com.agendaqr.core.ui.theme.XauxaType

@Composable
fun AuthScreen(
    state: AuthUiState,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onSignIn: () -> Unit,
    onSignUp: () -> Unit,
    onClearError: () -> Unit = {},
) {
    // Ronda 2 (Área C): sobrevive a recreación.
    var submitted by rememberSaveable { mutableStateOf(false) }
    val emailError = submitted && !isValidEmail(state.email)
    val passwordError = submitted && state.password.length < 6
    // Fase 2 (auditoría a11y): el botón NO se deshabilita por validación —
    // se habilita mientras no se esté enviando y los errores se muestran
    // AL ENVIAR (un botón gris no explica qué falta; el error sí).
    fun submit(target: () -> Unit) {
        submitted = true
        if (isValidEmail(state.email) && state.password.length >= 6) target()
    }

    XauxaScreen {
        Column(
            modifier = Modifier
                .padding(XauxaSpacing.ScreenMargin)
                .statusBarsPadding()
                .navigationBarsPadding()
                .imePadding()
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Lg),
        ) {
            XauxaPageTitle(text = AppStrings.AgendaQr)
            XauxaText(
                AppStrings.IniciaSesionParaAccederA,
                size = XauxaType.Body,
                color = XauxaColor.TextSecondary,
            )
            XauxaTextInput(
                label = AppStrings.Correo,
                value = state.email,
                onValueChange = onEmailChanged,
                // imeAction Next: el flujo natural es saltar a la contraseña;
                // sin mayúsculas automáticas ni autocorrección (es correo).
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next,
                    capitalization = KeyboardCapitalization.None,
                    autoCorrect = false,
                ),
                isRequired = true,
                isError = emailError,
                errorMessage = if (emailError) AppStrings.IngresaUnCorreoValido else null,
            )
            XauxaTextInput(
                label = AppStrings.Contrasena,
                value = state.password,
                onValueChange = onPasswordChanged,
                // Done envía el formulario (KeyboardActions); el error de
                // validación aparece al intentar, no bloqueando antes.
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(onDone = { submit(onSignIn) }),
                visualTransformation = PasswordVisualTransformation(),
                isRequired = true,
                isError = passwordError,
                errorMessage = if (passwordError) AppStrings.Minimo6Caracteres else null,
                helperMessage = if (!passwordError) AppStrings.Minimo6Caracteres else null,
            )
            state.errorMessage?.let { err ->
                XauxaStatusBanner(
                    err.display(),
                    tone = XauxaTone.Danger,
                    actionLabel = err.action.label,
                    // REINTENTAR repite el mismo paso que falló.
                    onAction = when (state.errorFlow) {
                        ErrorFlow.SignIn -> onSignIn
                        ErrorFlow.SignUp -> onSignUp
                        else -> onClearError
                    },
                    onDismiss = onClearError,
                    dismissLabel = AppStrings.Descartar,
                )
            }
            state.confirmationMessage?.let { XauxaStatusBanner(it, tone = XauxaTone.Info) }
            XauxaPrimaryButton(
                AppStrings.IniciarSesion,
                onClick = { submit(onSignIn) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isSubmitting,
                isLoading = state.isSubmitting,
                busyDescription = AppStrings.IniciandoSesion,
            )
            // Fase 2: el secundario NO muestra spinner (un solo indicador
            // de ocupado por pantalla — el del primario).
            XauxaSecondaryButton(
                AppStrings.CrearCuenta,
                onClick = { submit(onSignUp) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isSubmitting,
            )
        }
    }
}

private fun isValidEmail(value: String): Boolean {
    val trimmed = value.trim()
    if (trimmed.isEmpty() || trimmed.contains(" ")) return false
    val parts = trimmed.split("@")
    return parts.size == 2 && parts[0].isNotEmpty() && parts[1].contains(".") && parts[1].length >= 3
}
