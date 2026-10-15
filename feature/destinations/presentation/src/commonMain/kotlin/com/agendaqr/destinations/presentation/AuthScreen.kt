package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.agendaqr.core.ui.components.AmIcon
import com.agendaqr.core.ui.components.AmIconButton
import com.agendaqr.core.ui.components.AmIcons
import com.agendaqr.core.ui.components.AmPrimaryButton
import com.agendaqr.core.ui.components.AmScreenScaffold
import com.agendaqr.core.ui.components.AmStatusBanner
import com.agendaqr.core.ui.components.AmText
import com.agendaqr.core.ui.components.AmTextAction
import com.agendaqr.core.ui.components.AmTextField
import com.agendaqr.core.ui.components.AmTone
import com.agendaqr.core.ui.theme.AmColor
import com.agendaqr.core.ui.theme.AmSpacing
import com.agendaqr.core.ui.theme.AmType

@Composable
fun AuthScreen(
    state: AuthUiState,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onSignIn: () -> Unit,
    onSignUp: () -> Unit,
    onClearError: () -> Unit = {},
) {
    // sobrevive a recreación.
    var submitted by rememberSaveable { mutableStateOf(false) }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    val emailError = submitted && !isValidEmail(state.email)
    val passwordError = submitted && state.password.length < 6
    // el botón NO se deshabilita por validación —
    // se habilita mientras no se esté enviando y los errores se muestran
    // AL ENVIAR (un botón gris no explica qué falta; el error sí).
    fun submit(target: () -> Unit) {
        submitted = true
        if (isValidEmail(state.email) && state.password.length >= 6) target()
    }

    // V1.4 (§12): la autenticación no es un destino raíz ni tiene a dónde
    // volver: sin barra; su acción principal es el `Button` del cuerpo.
    // El titular usa el rol headlineMedium y los campos sin borde llevan
    // autofill de credenciales. Composición simple sin tarjetas ni sombras:
    // cabecera (título + descripción a 4 dp) · campos (12 dp) · acción
    // principal · enlace secundario.
    AmScreenScaffold(
        scrollable = true,
        verticalArrangement = Arrangement.spacedBy(AmSpacing.Xl),
        horizontalAlignment = Alignment.Start,
    ) {
        // Cabecera: título y descripción con jerarquía estrecha (4 dp).
        Column(verticalArrangement = Arrangement.spacedBy(AmSpacing.Xs)) {
            AmText(
                AppStrings.AgendaQr,
                size = AmType.Title,
                fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
            )
            AmText(
                AppStrings.IniciaSesionParaAccederA,
                size = AmType.Body,
                color = AmColor.TextSecondary,
            )
        }
        // Campos: grupo funcional (12 dp entre campos; la ayuda/error de
        // cada campo se alinea a su texto editable en el DS).
        Column(verticalArrangement = Arrangement.spacedBy(AmSpacing.Md)) {
            AmTextField(
                label = AppStrings.Correo,
                value = state.email,
                onValueChange = onEmailChanged,
                // imeAction Next: el flujo natural es saltar a la contraseña;
                // sin mayúsculas automáticas ni autocorrección (es correo).
                // El autofill llega por el keyboardType Email (iOS lo mapea a
                // textContentType emailAddress; Android a los hints del IME).
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next,
                    capitalization = KeyboardCapitalization.None,
                    autoCorrect = false,
                ),
                isError = emailError,
                errorMessage = if (emailError) AppStrings.IngresaUnCorreoValido else null,
            )
            AmTextField(
                label = AppStrings.Contrasena,
                value = state.password,
                onValueChange = onPasswordChanged,
                // Done envía el formulario (KeyboardActions); el error de
                // validación aparece al intentar, no bloqueando antes. El
                // autofill de contraseñas llega por el keyboardType Password.
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Password,
                    imeAction = ImeAction.Done,
                ),
                keyboardActions = KeyboardActions(onDone = { submit(onSignIn) }),
                visualTransformation = if (passwordVisible) {
                    VisualTransformation.None
                } else {
                    PasswordVisualTransformation()
                },
                isError = passwordError,
                errorMessage = if (passwordError) AppStrings.Minimo6Caracteres else null,
                helperMessage = if (!passwordError) AppStrings.Minimo6Caracteres else null,
                trailing = {
                    // Uso de texto compacto como alternativa a iconos de ojo
                    // (Lucide 1.1.0 no incluye Eye/EyeOff). El botón mantiene
                    // área táctil de 48 dp y estilo de icono.
                    AmIconButton(
                        contentDescription = if (passwordVisible) AppStrings.Ocultar else AppStrings.Mostrar,
                        onClick = { passwordVisible = !passwordVisible },
                    ) {
                        AmText(
                            text = if (passwordVisible) AppStrings.Ocultar else AppStrings.Mostrar,
                            size = AmType.Caption,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                            color = AmColor.AccentText,
                        )
                    }
                },
            )
        }
        state.errorMessage?.let { err ->
            AmStatusBanner(
                err.display(),
                tone = AmTone.Danger,
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
        state.confirmationMessage?.let { AmStatusBanner(it, tone = AmTone.Info) }
        // Acción principal a ancho completo; el enlace de crear cuenta es
        // terciario y no compite por el peso visual.
        Column(
            verticalArrangement = Arrangement.spacedBy(AmSpacing.Sm),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AmPrimaryButton(
                AppStrings.IniciarSesion,
                onClick = { submit(onSignIn) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isSubmitting,
                isLoading = state.isSubmitting,
                busyDescription = AppStrings.IniciandoSesion,
            )
            // el secundario NO muestra spinner (un solo indicador
            // de ocupado por pantalla — el del primario).
            AmTextAction(
                label = AppStrings.CrearCuenta,
                onClick = { submit(onSignUp) },
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
