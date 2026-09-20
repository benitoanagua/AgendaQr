package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.XauxaLoading
import com.agendaqr.core.ui.components.XauxaPrimaryButton
import com.agendaqr.core.ui.components.XauxaScreen
import com.agendaqr.core.ui.components.XauxaSecondaryButton
import com.agendaqr.core.ui.components.XauxaStatusBanner
import com.agendaqr.core.ui.theme.XauxaSpacing

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
            modifier = Modifier.padding(XauxaSpacing.Xl),
            verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Md),
        ) {
            Text("Agenda QR")
            Text("Inicia sesión para acceder a tu agenda.")
            OutlinedTextField(value = state.email, onValueChange = onEmailChanged, label = { Text("Correo") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            OutlinedTextField(value = state.password, onValueChange = onPasswordChanged, label = { Text("Contraseña") }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            state.errorMessage?.let { XauxaStatusBanner(it, danger = true) }
            state.confirmationMessage?.let { XauxaStatusBanner(it) }
            if (state.isSubmitting) {
                XauxaLoading()
            } else {
                XauxaPrimaryButton("Iniciar sesión", onSignIn, modifier = Modifier.fillMaxWidth())
                XauxaSecondaryButton("Crear cuenta", onSignUp, modifier = Modifier.fillMaxWidth())
            }
        }
    }
}
