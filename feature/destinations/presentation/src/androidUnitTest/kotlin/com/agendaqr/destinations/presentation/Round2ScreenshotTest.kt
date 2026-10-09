package com.agendaqr.destinations.presentation

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import com.agendaqr.core.ui.theme.XauxaTheme
import com.github.takahirom.roborazzi.captureRoboImage
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Ronda 2 (Área D, ADR-0007): capturas de referencia de las pantallas
 * críticas en claro y oscuro. Los PNG se generan en
 * `feature/destinations/presentation/build/roborazzi/` y NO se versionan
 * (decisión del 2026-10-09: sin binarios de imagen en git): la regresión
 * se detecta comparando el SHA-256 de cada PNG contra
 * `docs/04-ux/visual-hashes/manifest.json` (verificar con
 * `bash docs/04-ux/visual-hashes/verify.sh`, tarea sensible al entorno —
 * ver ADR).
 */
@RunWith(RobolectricTestRunner::class)
// NATIVE graphics: requisito de Roborazzi para render real de píxeles
// (sin esto las capturas salen en negro/idénticas — hallado en esta ronda).
@org.robolectric.annotation.GraphicsMode(org.robolectric.annotation.GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], qualifiers = "w360dp-h740dp")
class Round2ScreenshotTest {

    @get:Rule
    val compose = createComposeRule()

    private fun shot(name: String, dark: Boolean, content: @androidx.compose.runtime.Composable () -> Unit) {
        compose.setContent { XauxaTheme(darkTheme = dark) { content() } }
        compose.waitForIdle()
        compose.onRoot().captureRoboImage("build/roborazzi/$name.png")
    }

    @Test fun s01_light() = shot("s01-light", dark = false) {
        DestinationsScreen(
            state = DestinationsUiState(
                destinations = listOf(
                    com.agendaqr.destinations.domain.Destination(
                        id = "d-1", name = "Carniceria Don Bife",
                        qr = com.agendaqr.destinations.domain.QrAsset(encoded = "eA=="),
                        createdAt = 1, updatedAt = 1,
                    ),
                ),
                isLoading = false,
            ),
            onAction = {},
        )
    }

    @Test fun s01_dark() = shot("s01-dark", dark = true) {
        DestinationsScreen(
            state = DestinationsUiState(
                destinations = listOf(
                    com.agendaqr.destinations.domain.Destination(
                        id = "d-1", name = "Carniceria Don Bife",
                        qr = com.agendaqr.destinations.domain.QrAsset(encoded = "eA=="),
                        createdAt = 1, updatedAt = 1,
                    ),
                ),
                isLoading = false,
            ),
            onAction = {},
        )
    }

    @Test fun login_light() = shot("login-light", dark = false) {
        AuthScreen(
            state = AuthUiState(email = "", password = "", isSubmitting = false),
            onEmailChanged = {},
            onPasswordChanged = {},
            onSignIn = {},
            onSignUp = {},
        )
    }

    @Test fun login_dark() = shot("login-dark", dark = true) {
        AuthScreen(
            state = AuthUiState(email = "", password = "", isSubmitting = false),
            onEmailChanged = {},
            onPasswordChanged = {},
            onSignIn = {},
            onSignUp = {},
        )
    }

    @Test fun import_batch_light() = shot("import-batch-light", dark = false) {
        ImportBatchScreen(state = ImportBatchUiState.Idle, onAction = {})
    }

    @Test fun import_batch_dark() = shot("import-batch-dark", dark = true) {
        ImportBatchScreen(state = ImportBatchUiState.Idle, onAction = {})
    }

    @Test fun s02_add_light() = shot("s02-add-light", dark = false) {
        AddDestinationScreen(onImport = {}, onBack = {})
    }

    @Test fun s02_add_dark() = shot("s02-add-dark", dark = true) {
        AddDestinationScreen(onImport = {}, onBack = {})
    }
}
