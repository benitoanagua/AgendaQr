package com.agendaqr.destinations.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.agendaqr.core.ui.components.XauxaLoading
import com.agendaqr.core.ui.theme.XauxaTheme
import com.agendaqr.destinations.data.createAuthRepository
import com.agendaqr.destinations.domain.AuthState
import com.agendaqr.destinations.domain.ObserveAuthStateUseCase
import com.agendaqr.destinations.domain.SignInUseCase
import com.agendaqr.destinations.domain.SignOutUseCase
import com.agendaqr.destinations.domain.SignUpUseCase

/**
 * Raíz de la app: solo decide entre sesión cargando, autenticación y la
 * app autenticada. La construcción de dependencias de la sesión vive en
 * [rememberAuthenticatedSessionGraph]; la navegación de superficies vive en
 * [AuthenticatedAppRoot] con su back stack ([AppBackStack]).
 */
@Composable
fun AgendaQrApp() {
    val authRepository = remember { createAuthRepository() }
    val authViewModel = remember(authRepository) {
        AuthViewModel(
            observe = ObserveAuthStateUseCase(authRepository),
            signIn = SignInUseCase(authRepository),
            signUp = SignUpUseCase(authRepository),
            signOutUseCase = SignOutUseCase(authRepository),
        )
    }
    val authState by authViewModel.state.collectAsState()

    XauxaTheme {
        val session = authState.authState
        when (session) {
            AuthState.Loading -> XauxaLoading()
            AuthState.SignedOut -> AuthScreen(
                state = authState,
                onEmailChanged = authViewModel::setEmail,
                onPasswordChanged = authViewModel::setPassword,
                onSignIn = authViewModel::submitSignIn,
                onSignUp = authViewModel::submitSignUp,
            )
            is AuthState.SignedIn -> AgendaQrAuthenticatedApp(
                userId = session.user.id,
                onSignOut = authViewModel::signOut,
            )
        }
    }
}

/**
 * Sesión autenticada. El grafo de dependencias se construye ligado al
 * usuario y la navegación se entrega a [AuthenticatedAppRoot].
 */
@Composable
private fun AgendaQrAuthenticatedApp(userId: String, onSignOut: () -> Unit) {
    val graph = rememberAuthenticatedSessionGraph(userId)
    AuthenticatedAppRoot(graph = graph, onSignOut = onSignOut)
}
