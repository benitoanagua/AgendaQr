package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
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
) {
    val toastHost: com.agendaqr.core.ui.components.AqToastHostState? = null
    // Errores de validación de campo, derivados del state para que
    // sobrevivan a la recreación. Se muestran en cada campo mediante
    // AqTextInput(isError, errorMessage); las AqAlert apiladas se
    // eliminan (D5).
    val emailInvalid = state.email.isBlank()
    val passwordInvalid = state.password.length < 6
    AqScreen(
        title = "",
        onBack = { },
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
            onValueChange = onEmailChanged,
            isError = emailInvalid,
            errorMessage = if (emailInvalid) AppStrings.IngresaUnCorreoValido else null,
        )
        AqTextInput(
            label = AppStrings.Contrasena,
            value = state.password,
            onValueChange = onPasswordChanged,
            isError = passwordInvalid,
            errorMessage = if (passwordInvalid) AppStrings.Minimo6Caracteres else null,
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
                    if (!state.isSubmitting && !emailInvalid && !passwordInvalid) {
                        onSignIn()
                    }
                },
                variant = AqButtonVariant.Primary,
                enabled = !state.isSubmitting,
                isLoading = state.isSubmitting,
                busyDescription = AppStrings.IniciandoSesion,
                modifier = Modifier.fillMaxWidth(),
            )
            // Secundario: sin StateDescription (no anuncia ocupado).
            AqButton(
                label = AppStrings.CrearCuenta,
                onClick = { onSignUp() },
                variant = AqButtonVariant.Text,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}
