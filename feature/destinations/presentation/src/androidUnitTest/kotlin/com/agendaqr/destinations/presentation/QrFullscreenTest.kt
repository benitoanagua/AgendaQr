package com.agendaqr.destinations.presentation

import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.agendaqr.core.ui.theme.AmTheme
import com.agendaqr.destinations.domain.Destination
import com.agendaqr.destinations.domain.QrAsset
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * U-04/U-05: mostrar el QR es la tarea central del Detalle (única
 * acción primaria), la tarjeta también lo abre, y la pantalla completa
 * sube el brillo / mantiene la pantalla encendida restaurando todo al
 * salir (D4).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class QrFullscreenTest {

    @get:Rule
    val compose = createComposeRule()

    private fun destination() = Destination(
        id = "d-1",
        name = "Carniceria Don Bife",
        qr = QrAsset(encoded = "eA=="),
        createdAt = 1,
        updatedAt = 1,
    )

    private fun renderDetail(
        onShowQr: () -> Unit = {},
        onShare: () -> Unit = {},
    ) {
        compose.setContent {
            AmTheme {
                DestinationDetailScreen(
                    destination = destination(),
                    onShowQr = onShowQr,
                    onEdit = {},
                    onDelete = {},
                    onShare = onShare,
                    onBack = {},
                )
            }
        }
        compose.waitForIdle()
    }

    @Test
    fun detail_dock_has_show_qr_as_the_only_primary_action() {
        renderDetail()
        // Primaria única: Mostrar QR con rol de botón.
        compose.onNodeWithText(AppStrings.MostrarQr)
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Button))
        // Compartir sigue como secundaria visible.
        compose.onNodeWithText(AppStrings.Compartir).assertExists()
        // Registrar/Editar salieron del dock (viven en "…").
        compose.onNodeWithText(AppStrings.RegistrarOperacion).assertDoesNotExist()
        compose.onNodeWithText(AppStrings.EditarMetadatosReemplazarQr).assertDoesNotExist()
    }

    @Test
    fun qr_card_opens_fullscreen_with_the_same_action() {
        var shown = 0
        renderDetail(onShowQr = { shown++ })
        // La tarjeta del QR es tocable con la misma acción de Mostrar.
        compose.onNodeWithText(AppStrings.MuestraEsteCodigo).performClick()
        assertEquals(1, shown)
    }

    @Test
    fun fullscreen_shows_the_destination_name_and_keeps_back() {
        var backed = false
        compose.setContent {
            AmTheme {
                QrFullscreenPattern(
                    encodedQr = "eA==",
                    destinationName = "Carniceria Don Bife",
                    onBack = { backed = true },
                )
            }
        }
        compose.waitForIdle()
        compose.onNodeWithText("Carniceria Don Bife").assertExists()
        compose.onNodeWithText(AppStrings.Qr).assertExists()
        compose.onNodeWithContentDescription(AppStrings.Volver).performClick()
        assertTrue(backed)
    }

    @Test
    fun fullscreen_applies_and_restores_brightness_and_keep_screen_on() {
        // Ventana real de Robolectric como contexto de composición: el
        // efecto lee LocalContext como Activity.
        val activityController = Robolectric.buildActivity(ComponentActivity::class.java).setup()
        val activity = activityController.get()
        val window = activity.window
        var showEffects by mutableStateOf(true)
        val previousBrightness = window.attributes.screenBrightness
        val hadKeepScreenOn = window.attributes.flags and
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON != 0
        compose.setContent {
            CompositionLocalProvider(LocalContext provides activity) {
                AmTheme {
                    if (showEffects) QrDisplayScreenEffects()
                }
            }
        }
        compose.waitForIdle()
        assertEquals(
            WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_FULL,
            window.attributes.screenBrightness,
        )
        assertTrue(
            window.attributes.flags and WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON != 0,
            "la pantalla no debe apagarse mientras se muestra el QR",
        )
        // Al salir se restaura todo (incluida rotación, que recompone).
        compose.runOnIdle { showEffects = false }
        compose.waitForIdle()
        assertEquals(previousBrightness, window.attributes.screenBrightness)
        assertFalse(
            !hadKeepScreenOn &&
                window.attributes.flags and WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON != 0,
            "el flag debe retirarse al salir si no estaba antes",
        )
    }
}
