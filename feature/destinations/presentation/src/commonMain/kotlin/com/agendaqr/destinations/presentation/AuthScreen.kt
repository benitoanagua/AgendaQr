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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
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
    var submitted by remember { mutableStateOf(false) }
    val emailError = submitted && !isValidEmail(state.email)
    val passwordError = submitted && state.password.length < 6
    val formValid = isValidEmail(state.email) && state.password.length >= 6 && !state.isSubmitting

    XauxaScreen {
        Column(
            modifier = Modifier
                .padding(XauxaSpacing.Xxl)
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
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isRequired = true,
                isError = emailError,
                errorMessage = if (emailError) "Ingresa un correo válido" else null,
            )
            XauxaTextInput(
                label = AppStrings.Contrasena,
                value = state.password,
                onValueChange = onPasswordChanged,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = PasswordVisualTransformation(),
                isRequired = true,
                isError = passwordError,
                errorMessage = if (passwordError) "Mínimo 6 caracteres" else null,
                helperMessage = if (!passwordError) "Mínimo 6 caracteres" else null,
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
                )
            }
            state.confirmationMessage?.let { XauxaStatusBanner(it, tone = XauxaTone.Info) }
            XauxaPrimaryButton(
                AppStrings.IniciarSesion,
                onClick = {
                    submitted = true
                    if (isValidEmail(state.email) && state.password.length >= 6) onSignIn()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = formValid,
                isLoading = state.isSubmitting,
            )
            XauxaSecondaryButton(
                AppStrings.CrearCuenta,
                onClick = {
                    submitted = true
                    if (isValidEmail(state.email) && state.password.length >= 6) onSignUp()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = formValid,
                isLoading = state.isSubmitting,
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
