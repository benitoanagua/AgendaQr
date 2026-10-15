package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.agendaqr.core.ui.components.AmLoading
import com.agendaqr.core.ui.components.AmNavChromeState
import com.agendaqr.core.ui.components.AmScreenScaffold
import com.agendaqr.core.ui.components.AmSecondaryButton
import com.agendaqr.core.ui.components.AmSnackbarHost
import com.agendaqr.core.ui.components.AmStatusBanner
import com.agendaqr.core.ui.components.AmText
import com.agendaqr.core.ui.components.AmTone
import com.agendaqr.core.ui.components.AmTopBar
import com.agendaqr.core.ui.theme.AmColor
import com.agendaqr.core.ui.theme.AmSpacing
import com.agendaqr.core.ui.theme.AmType
import com.agendaqr.destinations.data.createAuthRepository
import com.agendaqr.destinations.domain.AuthState
import com.agendaqr.destinations.domain.ObserveAuthStateUseCase
import com.agendaqr.destinations.domain.SignInUseCase
import com.agendaqr.destinations.domain.SignOutUseCase
import com.agendaqr.destinations.domain.SignUpUseCase

/**
 * Ajustes → Cuenta y respaldo. Superficie OPCIONAL: la app funciona sin
 * cuenta. Aquí se conecta o desconecta la cuenta remota; desconectar nunca
 * destruye los datos locales (que viven bajo el perfil local).
 */
@Composable
internal fun AccountScreen(
    navChrome: AmNavChromeState?,
    appHosts: AppHosts?,
    onBack: () -> Unit,
) {
    // La identidad remota se construye de forma perezosa: si el usuario no
    // entra aquí, no se crea ningún repositorio de Supabase.
    val authRepository = remember { createAuthRepository() }
    val authViewModel = remember(authRepository) {
        AuthViewModel(
            observe = ObserveAuthStateUseCase(authRepository),
            signIn = SignInUseCase(authRepository),
            signUp = SignUpUseCase(authRepository),
            signOutUseCase = SignOutUseCase(authRepository),
        )
    }
    val authState by authViewModel.state.collectAsStateWithLifecycle()

    AmScreenScaffold(
        scrollable = true,
        topBar = {
            AmTopBar(
                title = AppStrings.CuentaYRespaldo,
                onBack = onBack,
                backLabel = AppStrings.Volver,
            )
        },
        navDestinations = navChrome?.destinations.orEmpty(),
        navSelectedId = navChrome?.selectedId,
        onNavSelect = { navChrome?.onSelect?.invoke(it) },
        snackbarHost = appHosts?.feedback?.let { host -> { AmSnackbarHost(host) } },
        statusContent = appHosts?.status,
        verticalArrangement = Arrangement.spacedBy(AmSpacing.Lg),
    ) {
        when (val session = authState.authState) {
            AuthState.Loading -> AmLoading()
            AuthState.SignedOut -> AuthForm(
                heading = null,
                description = AppStrings.ConectarCuentaDescripcion,
                state = authState,
                onEmailChanged = authViewModel::setEmail,
                onPasswordChanged = authViewModel::setPassword,
                onSignIn = authViewModel::submitSignIn,
                onSignUp = authViewModel::submitSignUp,
                onClearError = authViewModel::clearError,
            )
            is AuthState.SignedIn -> Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(AmSpacing.Md),
                horizontalAlignment = Alignment.Start,
            ) {
                AmText(
                    AppStrings.CuentaConectada,
                    size = AmType.SectionHeader,
                    color = AmColor.TextPrimary,
                )
                session.user.email?.let { email ->
                    AmText(email, size = AmType.Body, color = AmColor.TextSecondary)
                }
                AmStatusBanner(
                    AppStrings.DatosLocalesSeConservan,
                    tone = AmTone.Info,
                )
                AmSecondaryButton(
                    AppStrings.DesconectarCuenta,
                    onClick = authViewModel::signOut,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
