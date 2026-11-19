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
import com.agendaqr.core.ui.components.AqLoading
import com.agendaqr.core.ui.components.AqNavChromeState
import com.agendaqr.core.ui.components.AqScreenScaffold
import com.agendaqr.core.ui.components.AqSecondaryButton
import com.agendaqr.core.ui.components.AqSnackbarHost
import com.agendaqr.core.ui.components.AqStatusBanner
import com.agendaqr.core.ui.components.AqText
import com.agendaqr.core.ui.components.AqTone
import com.agendaqr.core.ui.components.AqTopBar
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqType
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
 *
 * [authRepository] permite inyectar el repositorio (tests/preview); por
 * defecto se crea de forma perezosa el de producción.
 */
@Composable
internal fun AccountScreen(
    navChrome: AqNavChromeState?,
    appHosts: AppHosts?,
    onBack: () -> Unit,
    authRepository: com.agendaqr.destinations.domain.AuthRepository? = null,
) {
    // La identidad remota se construye de forma perezosa: si el usuario no
    // entra aquí, no se crea ningún repositorio de Supabase.
    val repository = authRepository ?: remember { createAuthRepository() }
    val authViewModel = remember(repository) {
        AuthViewModel(
            observe = ObserveAuthStateUseCase(repository),
            signIn = SignInUseCase(repository),
            signUp = SignUpUseCase(repository),
            signOutUseCase = SignOutUseCase(repository),
        )
    }
    val authState by authViewModel.state.collectAsStateWithLifecycle()

    AqScreenScaffold(
        scrollable = true,
        topBar = {
            AqTopBar(
                title = AppStrings.CuentaYRespaldo,
                onBack = onBack,
                backLabel = AppStrings.Volver,
            )
        },
        navDestinations = navChrome?.destinations.orEmpty(),
        navSelectedId = navChrome?.selectedId,
        onNavSelect = { navChrome?.onSelect?.invoke(it) },
        snackbarHost = appHosts?.feedback?.let { host -> { AqSnackbarHost(host) } },
        statusContent = appHosts?.status,
        verticalArrangement = Arrangement.spacedBy(AqSpacing.Lg),
    ) {
        when (val session = authState.authState) {
            AuthState.Loading -> AqLoading()
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
                verticalArrangement = Arrangement.spacedBy(AqSpacing.Md),
                horizontalAlignment = Alignment.Start,
            ) {
                AqText(
                    AppStrings.CuentaConectada,
                    size = AqType.SectionHeader,
                    color = AqColor.TextPrimary,
                )
                session.user.email?.let { email ->
                    AqText(email, size = AqType.Body, color = AqColor.TextSecondary)
                }
                AqStatusBanner(
                    message = AppStrings.DatosLocalesSeConservan,
                    tone = AqTone.Info,
                )
                AqSecondaryButton(
                    AppStrings.DesconectarCuenta,
                    onClick = authViewModel::signOut,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }
    }
}
