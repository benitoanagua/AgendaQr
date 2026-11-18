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
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import com.agendaqr.core.ui.components.AqIconButton
import com.agendaqr.core.ui.components.AqPrimaryButton
import com.agendaqr.core.ui.components.AqScreenScaffold
import com.agendaqr.core.ui.components.AqBannerAction
import com.agendaqr.core.ui.components.AqStatusBanner
import com.agendaqr.core.ui.components.AqText
import com.agendaqr.core.ui.components.AqTextAction
import com.agendaqr.core.ui.components.AqTextField
import com.agendaqr.core.ui.components.AqTone
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqType

/**
 * Autenticación remota OPCIONAL. Ya no es la puerta de entrada de la app: la
 * app local se abre sin cuenta. Esta superficie sigue existiendo para
 * conectar una cuenta desde Ajustes (respaldo/sincronización) y se compone
 * con [AuthForm] dentro de [AccountScreen].
 */
@Composable
fun AuthScreen(
    state: AuthUiState,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onSignIn: () -> Unit,
    onSignUp: () -> Unit,
    onClearError: () -> Unit = {},
) {
    // V1.4 (§12): la autenticación no es un destino raíz ni tiene a dónde
    // volver: sin barra; su acción principal es el `Button` del cuerpo.
    AqScreenScaffold(
        scrollable = true,
        verticalArrangement = Arrangement.spacedBy(AqSpacing.Xl),
        horizontalAlignment = Alignment.Start,
    ) {
        AuthForm(
            heading = AppStrings.AgendaQr,
            description = AppStrings.IniciaSesionParaAccederA,
            state = state,
            onEmailChanged = onEmailChanged,
            onPasswordChanged = onPasswordChanged,
            onSignIn = onSignIn,
            onSignUp = onSignUp,
            onClearError = onClearError,
        )
    }
}

/**
 * Cuerpo de credenciales reutilizable (AuthScreen y AccountScreen).
 *
 * Jerarquía visual (patrón de formulario de acceso de apps de consumo, ADR-0021 — S10/Login):
 * 1. Cabecera: título (SectionHeader) + descripción (Body secondary)
 * 2. Formulario: etiqueta + campo + ayuda/validación agrupados
 * 3. Error: banner con tono danger + acción de recuperación
 * 4. Acciones: primaria (full-width) + secundaria (texto centrado)
 */
@Composable
internal fun AuthForm(
    heading: String?,
    description: String?,
    descriptionDetail: String? = null,
    state: AuthUiState,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onSignIn: () -> Unit,
    onSignUp: () -> Unit,
    onClearError: () -> Unit = {},
) {
    var submitted by rememberSaveable { mutableStateOf(false) }
    var passwordVisible by rememberSaveable { mutableStateOf(false) }
    val focusRequester = remember { androidx.compose.ui.focus.FocusRequester() }
    // El banner de error de credenciales también marca los campos, para que
    // el estado del campo y el del banner no se contradigan (p. ej. correo
    // "invalid": el campo se pinta como error al mismo tiempo que el banner).
    val emailError = submitted && !isValidEmail(state.email)
    val passwordError = submitted && state.password.length < 6

    fun submit(target: () -> Unit) {
        submitted = true
        if (isValidEmail(state.email) && state.password.length >= 6) target()
    }

    Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Md)) {
        // Cabecera: título y descripción con jerarquía estrecha (4 dp).
        if (heading != null || description != null || descriptionDetail != null) {
            Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Xs)) {
                heading?.let {
                    AqText(it, size = AqType.Headline, fontWeight = FontWeight.SemiBold)
                }
                description?.let {
                    AqText(it, size = AqType.Body, color = AqColor.TextSecondary)
                }
                descriptionDetail?.let {
                    AqText(it, size = AqType.Body, color = AqColor.TextSecondary)
                }
            }
        }

        // Campos: grupo funcional con etiqueta + campo + ayuda/error.
        Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
            AqTextField(
                label = AppStrings.Correo,
                value = state.email,
                onValueChange = onEmailChanged,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email,
                    imeAction = ImeAction.Next,
                    capitalization = KeyboardCapitalization.None,
                    autoCorrect = false,
                ),
                isError = emailError,
                errorMessage = if (submitted && !isValidEmail(state.email)) AppStrings.IngresaUnCorreoValido else null,
            )

            AqTextField(
                label = AppStrings.Contrasena,
                value = state.password,
                onValueChange = onPasswordChanged,
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
                errorMessage = if (submitted && state.password.length < 6) AppStrings.Minimo6Caracteres else null,
                helperMessage = if (!passwordError) AppStrings.Minimo6Caracteres else null,
                trailing = {
                    AqIconButton(
                        contentDescription = if (passwordVisible) AppStrings.Ocultar else AppStrings.Mostrar,
                        onClick = { passwordVisible = !passwordVisible },
                    ) {
                        AqText(
                            text = if (passwordVisible) AppStrings.Ocultar else AppStrings.Mostrar,
                            size = AqType.Caption,
                            fontWeight = FontWeight.Medium,
                            color = AqColor.Link,
                        )
                    }
                },
            )
        }

        // Error: banner con acción de recuperación.
        state.errorMessage?.let { err ->
            val isAuthError = state.errorFlow == ErrorFlow.SignIn || state.errorFlow == ErrorFlow.SignUp
            val (action, dismiss) = if (isAuthError) {
                AqBannerAction(AppStrings.RevisarDatos) { submitted = true; onClearError() } to null
            } else {
                err.bannerActions(
                    onRetry = onClearError,
                    onClear = onClearError,
                )
            }
            AqStatusBanner(
                title = err.what,
                message = err.dataStatus,
                tone = AqTone.Danger,
                action = action,
                dismiss = dismiss,
            )
        }

        // Confirmación (registro exitoso).
        state.confirmationMessage?.let {
            AqStatusBanner(it, tone = AqTone.Info)
        }

        // Acciones: primaria full-width + secundaria centrada.
        Column(
            verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            AqPrimaryButton(
                AppStrings.IniciarSesion,
                onClick = { submit(onSignIn) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isSubmitting,
                isLoading = state.isSubmitting,
                busyDescription = AppStrings.IniciandoSesion,
            )
            AqTextAction(
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
