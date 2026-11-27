package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.AqAlert
import com.agendaqr.core.ui.components.AqButton
import com.agendaqr.core.ui.components.AqButtonVariant
import com.agendaqr.core.ui.components.AqScreen
import com.agendaqr.core.ui.components.AqTextInput
import com.agendaqr.core.ui.components.AqTone
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqType

/**
 * Pantalla de autenticación remota (opcional). No es una puerta de entrada:
 * la app arranca en el acceso local con PIN. Se expone para coherencia de
 * rutas pero no está cableada en el flujo principal local-first.
 *
 * Firma canónica usada por las pruebas de contrato:
 * [AuthScreen](state, onEmailChanged, onPasswordChanged, onSignIn, onSignUp).
 *
 * Reglas de validación al enviar (spec §10 / §2):
 * - Los botones nunca nacen deshabilitados por el formulario: el primario
 *   solo se deshabilita mientras hay un envío en curso.
 * - Al pulsar "Iniciar sesión" con datos inválidos, se muestran los errores
 *   de validación (correo / contraseña corta) y NO se invoca [onSignIn].
 * - El indicador de ocupado (StateDescription) vive solo en el botón
 *   primario; el secundario nunca lo anuncia.
 */
@Composable
internal fun AuthScreen(
    state: AuthUiState,
    onEmailChanged: (String) -> Unit,
    onPasswordChanged: (String) -> Unit,
    onSignIn: () -> Unit,
    onSignUp: () -> Unit,
    /** Título de la barra superior cuando el formulario ES la superficie
     *  (AppRoute.Account); vacío en usos embebidos. */
    title: String = "",
    onBack: () -> Unit = {},
) {
    val toastHost: com.agendaqr.core.ui.components.AqToastHostState? = null
    // Errores de validación de campo, derivados del state para que
    // sobrevivan a la recreación. Se muestran en cada campo mediante
    // AqTextInput(isError, errorMessage); las AqAlert apiladas se
    // eliminan (D5).
    // Errores de validación de campo: solo tras el primer intento de envío
    // o tras editar un campo ya marcado (nunca en el estado inicial — el
    // formulario abre limpio; el estado --error de la galería es el que
    // los muestra).
    var submitAttempted by rememberSaveable { mutableStateOf(false) }
    var emailTouched by rememberSaveable { mutableStateOf(false) }
    var passwordTouched by rememberSaveable { mutableStateOf(false) }
    val emailInvalid = state.email.isBlank()
    val passwordInvalid = state.password.length < 6
    val emailError = emailInvalid && (submitAttempted || emailTouched)
    val passwordError = passwordInvalid && (submitAttempted || passwordTouched)
    AqScreen(
        title = title,
        onBack = onBack,
        backLabel = AppStrings.Volver,
        statusContent = null,
        toastHost = null,
    ) {
        // Error remoto de autenticación (no apilado: un solo banner).
        state.errorMessage?.let {
            AqAlert(
                tone = AqTone.Danger,
                message = it.display(),
                dismissLabel = AppStrings.Cerrar,
                onDismiss = { },
            )
        }
        AqTextInput(
            label = AppStrings.Correo,
            value = state.email,
            onValueChange = {
                emailTouched = true
                onEmailChanged(it)
            },
            isError = emailError,
            errorMessage = if (emailError) AppStrings.IngresaUnCorreoValido else null,
        )
        AqTextInput(
            label = AppStrings.Contrasena,
            value = state.password,
            onValueChange = {
                passwordTouched = true
                onPasswordChanged(it)
            },
            isError = passwordError,
            errorMessage = if (passwordError) AppStrings.Minimo6Caracteres else null,
        )
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
        ) {
            // Primario: nunca deshabilitado por el formulario; ocupado solo
            // mientras se envía (StateDescription = busyDescription).
            AqButton(
                label = AppStrings.IniciarSesion,
                onClick = {
                    if (!state.isSubmitting) {
                        if (emailInvalid || passwordInvalid) {
                            submitAttempted = true
                        } else {
                            onSignIn()
                        }
                    }
                },
                variant = AqButtonVariant.Primary,
                enabled = !state.isSubmitting,
                isLoading = state.isSubmitting,
                busyDescription = AppStrings.IniciandoSesion,
                modifier = Modifier.fillMaxWidth(),
            )
            // Secundario: ancho intrínseco centrado bajo el primario (sin
            // fillMaxWidth). No anuncia ocupado.
            AqButton(
                label = AppStrings.CrearCuenta,
                onClick = { onSignUp() },
                variant = AqButtonVariant.Text,
            )
        }
    }
}
