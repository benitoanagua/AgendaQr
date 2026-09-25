package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import com.agendaqr.core.ui.components.XauxaLoading
import com.agendaqr.core.ui.components.XauxaPrimaryButton
import com.agendaqr.core.ui.components.XauxaScreen
import com.agendaqr.core.ui.components.XauxaSecondaryButton
import com.agendaqr.core.ui.components.XauxaStatusBanner
import com.agendaqr.core.ui.components.XauxaTextInput
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
) {
    XauxaScreen {
        Column(
            modifier = Modifier.padding(XauxaSpacing.Xxl),
            verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Lg),
        ) {
            Text(
                "Agenda QR",
                modifier = Modifier.semantics { heading() },
                fontSize = XauxaType.Display,
                fontWeight = FontWeight.Bold,
                color = XauxaColor.TextPrimary,
            )
            Text(
                "Inicia sesión para acceder a tu agenda.",
                fontSize = XauxaType.Body,
                color = XauxaColor.TextSecondary,
            )
            XauxaTextInput(
                label = "Correo",
                value = state.email,
                onValueChange = onEmailChanged,
            )
            XauxaTextInput(
                label = "Contraseña",
                value = state.password,
                onValueChange = onPasswordChanged,
            )
            state.errorMessage?.let { XauxaStatusBanner(it, danger = true) }
            state.confirmationMessage?.let { XauxaStatusBanner(it) }
            if (state.isSubmitting) {
                XauxaLoading(message = "Iniciando sesión…")
            } else {
                XauxaPrimaryButton("Iniciar sesión", onSignIn, modifier = Modifier.fillMaxWidth())
                XauxaSecondaryButton("Crear cuenta", onSignUp, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}