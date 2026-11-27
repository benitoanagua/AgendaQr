package com.agendaqr.destinations.presentation

import android.content.Context
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.SemanticsMatcher
import androidx.test.core.app.ApplicationProvider
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.destinations.data.AgendaQrAndroidStorage
import com.agendaqr.destinations.data.createLocalProtectionRepository
import kotlin.math.abs
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
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
        compose.onNodeWithText(AppStrings.Continuar, useUnmergedTree = true).assertExists()
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

        // Flujo del onboarding en dos pasos con teclado propio (V1.16):
        // crear el código, confirmarlo y guardar.
        "1234".forEach { digit ->
            compose.onNodeWithText(digit.toString()).performClick()
        }
        compose.onNodeWithText(AppStrings.Continuar, useUnmergedTree = true).performClick()
        "1234".forEach { digit ->
            compose.onNodeWithText(digit.toString()).performClick()
        }
        compose.onNodeWithText(AppStrings.GuardarPin, useUnmergedTree = true).performClick()
        compose.waitForIdle()

        // Entró a la app local: la configuración desaparece y aparece Inicio
        // (estado vacío de destinos; «Traer un QR» aparece dos veces: acción
        // rápida y CTA del placeholder).
        compose.onNodeWithText(AppStrings.ConfiguraTuPin, useUnmergedTree = true).assertDoesNotExist()
        // La lista vacía llega cuando el ViewModel termina de observar el
        // repositorio (dispatcher de fondo): esperar el nodo, no el frame.
        compose.waitUntil(timeoutMillis = 10_000) {
            compose.onAllNodes(
                androidx.compose.ui.test.hasText(AppStrings.AunNoHayDestinos),
            ).fetchSemanticsNodes().isNotEmpty()
        }
        compose.onNodeWithText(AppStrings.AunNoHayDestinos).assertExists()
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

    @Test
    @Config(sdk = [34], qualifiers = "w360dp-h740dp")
    fun demo_mode_banner_fills_useful_width_with_single_margin() {
        compose.setContent { AgendaQrApp() }
        compose.waitForIdle()
        compose.onNodeWithText(AppStrings.ExplorarDemostracion, useUnmergedTree = true).performClick()
        compose.waitUntil(timeoutMillis = 15_000) {
            compose.onAllNodesWithText(AppStrings.ModoDemostracion, useUnmergedTree = true)
                .fetchSemanticsNodes().isNotEmpty()
        }
        // Virtualización (LazyColumn) puede dejar varios nodos raíz; se toma
        // el de mayor superficie (la pantalla) y se lee su bounds con el
        // mismo método que el banner (getBoundsInRoot) para comparar dp.
        val rootNode = compose.onAllNodes(SemanticsMatcher("[any]") { true }, useUnmergedTree = true).fetchSemanticsNodes()
            .filter { it.isRoot }
            .maxByOrNull { b -> val r = b.boundsInRoot; (r.right - r.left) * (r.bottom - r.top) }
            ?: error("no root node")
        val rootBounds = rootNode.boundsInRoot
        val root = compose.onNode(SemanticsMatcher("root screen") {
            it.isRoot && it.boundsInRoot == rootBounds
        }).getBoundsInRoot()
        val banner = compose.onNodeWithTag("am-status-banner").getBoundsInRoot()
        val margin = AqSpacing.ScreenMargin
        println("demo: root=$root banner=$banner")
        assertTrue(
            "demo: margen izquierdo ${banner.left}, esperado $margin",
            abs(banner.left.value - margin.value) <= 1f,
        )
        assertTrue(
            "demo: margen derecho ${root.right - banner.right}, esperado $margin",
            abs((root.right - banner.right).value - margin.value) <= 1f,
        )
        assertTrue(
            "demo: ancho ${banner.right - banner.left} != útil ${root.right - root.left - margin * 2}",
            abs(
                (banner.right - banner.left).value -
                    (root.right - root.left - margin * 2).value,
            ) <= 1f,
        )
        compose.onNodeWithText(AppStrings.SalirDeDemostracion, useUnmergedTree = true).assertExists()
    }
}
