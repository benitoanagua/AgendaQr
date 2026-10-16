package com.agendaqr.destinations.presentation

import android.content.Context
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.core.app.ApplicationProvider
import com.agendaqr.destinations.data.AgendaQrAndroidStorage
import com.agendaqr.destinations.data.createLocalProtectionRepository
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Arranque local-first sobre el componente REAL ([AgendaQrApp]).
 *
 * Evidencia de aceptación: una instalación limpia abre la app SIN cuenta, SIN
 * red y SIN Supabase configurado, y lleva a la configuración del PIN local (no
 * a un login remoto).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AgendaQrLocalStartupTest {

    @get:Rule
    val compose = createComposeRule()

    private val context: Context get() = ApplicationProvider.getApplicationContext()

    @Before
    fun reset() {
        AgendaQrAndroidStorage.initialize(context)
        // "Instalación limpia": sin perfil local, sin PIN, sin cola.
        context.getSharedPreferences("agendaqr", Context.MODE_PRIVATE)
            .edit().clear().commit()
    }

    @Test
    fun clean_install_opens_local_onboarding_not_a_remote_login() {
        compose.setContent { AgendaQrApp() }
        compose.waitForIdle()

        // Pantalla de acceso LOCAL (PIN), no credenciales remotas.
        compose.onNodeWithText(AppStrings.ConfiguraTuPin, useUnmergedTree = true).assertExists()
        compose.onNodeWithText(AppStrings.GuardarPin, useUnmergedTree = true).assertExists()
        compose.onNodeWithText(AppStrings.IniciarSesion, useUnmergedTree = true).assertDoesNotExist()
        compose.onNodeWithText(AppStrings.Correo, useUnmergedTree = true).assertDoesNotExist()
        compose.onNodeWithText(AppStrings.Contrasena, useUnmergedTree = true).assertDoesNotExist()
    }

    @Test
    fun onboarding_creates_local_profile_without_any_account() {
        compose.setContent { AgendaQrApp() }
        compose.waitForIdle()

        // El perfil local se crea en el almacenamiento propio de la app.
        val rawProfile = context.getSharedPreferences("agendaqr", Context.MODE_PRIVATE)
            .getString("agendaqr.local.profile.v1", null)
        assertNotNull(rawProfile)

        compose.onAllNodes(hasSetTextAction())[0].performTextInput("1234")
        compose.onAllNodes(hasSetTextAction())[1].performTextInput("1234")
        compose.onNodeWithText(AppStrings.GuardarPin, useUnmergedTree = true).performClick()
        compose.waitForIdle()

        // Entró a la app local: la configuración desaparece y aparece Inicio.
        compose.onNodeWithText(AppStrings.ConfiguraTuPin, useUnmergedTree = true).assertDoesNotExist()
        compose.onNodeWithText(AppStrings.Anadir, useUnmergedTree = true).assertExists()
    }

    @Test
    fun restart_with_configured_pin_shows_local_unlock() {
        // Simula un reinicio: PIN ya configurado en el almacenamiento.
        createLocalProtectionRepository().configure("9876".toCharArray(), "9876".toCharArray())

        compose.setContent { AgendaQrApp() }
        compose.waitForIdle()

        compose.onNodeWithText(AppStrings.DesbloqueaAgendaQr, useUnmergedTree = true).assertExists()
        compose.onNodeWithText(AppStrings.Desbloquear, useUnmergedTree = true).assertExists()
        compose.onNodeWithText(AppStrings.IniciarSesion, useUnmergedTree = true).assertDoesNotExist()
    }
}
