package com.agendaqr.destinations.presentation

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.agendaqr.core.ui.theme.XauxaTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Escalado de fuente del sistema al 200 % (§11): las acciones primarias de
 * las pantallas de entrada no se truncan ni quedan fuera del viewport.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], qualifiers = "w360dp-h760dp")
class AuthFontScaleTest {

    @get:Rule
    val compose = createComposeRule()

    private val state = AuthUiState(
        email = "correo@ejemplo.com",
        password = "secreto",
        authState = com.agendaqr.destinations.domain.AuthState.SignedOut,
    )

    private fun atScales(check: (Float) -> Unit) {
        var scale by androidx.compose.runtime.mutableStateOf(1f)
        compose.setContent {
            XauxaTheme {
                CompositionLocalProvider(
                    LocalDensity provides Density(density = 1f, fontScale = scale),
                ) {
                    AuthScreen(
                        state = state,
                        onEmailChanged = {},
                        onPasswordChanged = {},
                        onSignIn = {},
                        onSignUp = {},
                    )
                }
            }
        }
        listOf(1f, 1.5f, 2f).forEach { value ->
            compose.runOnIdle { scale = value }
            compose.waitForIdle()
            check(value)
        }
    }

    private fun assertInsideScreenWidth(text: String, scale: Float) {
        val screenRight = compose.onRoot().getBoundsInRoot().right
        val nodes = compose.onAllNodesWithText(text, substring = true).fetchSemanticsNodes()
        assertTrue("sin nodos para: $text (escala $scale)", nodes.isNotEmpty())
        nodes.forEach { node ->
            assertTrue(
                "texto '$text' desborda el ancho (right=${node.boundsInRoot.right} > $screenRight) con fuente grande (escala $scale)",
                node.boundsInRoot.right <= screenRight.value + 1f,
            )
        }
    }

    @Test
    fun at_every_scale_the_primary_action_stays_reachable_and_uncut() {
        atScales { scale ->
            assertInsideScreenWidth(AppStrings.IniciarSesion, scale)
            assertInsideScreenWidth(AppStrings.CrearCuenta, scale)
            compose.onNodeWithText(AppStrings.IniciarSesion).assertHeightIsAtLeast(48.dp)
            compose.onNodeWithText(AppStrings.CrearCuenta).assertExists()
        }
    }

    @Test
    fun the_password_helper_and_show_button_coexist_at_100_percent() {
        compose.setContent {
            XauxaTheme {
                AuthScreen(
                    state = state,
                    onEmailChanged = {},
                    onPasswordChanged = {},
                    onSignIn = {},
                    onSignUp = {},
                )
            }
        }
        compose.waitForIdle()
        // El helper "Mínimo 6 caracteres" acompaña al campo de contraseña.
        compose.onNodeWithText(AppStrings.Minimo6Caracteres).assertExists()
        // Mostrar/ocultar: botón con descripción accesible (el glifo es texto).
        compose.onNodeWithText(AppStrings.Mostrar).assertExists()
    }
}
