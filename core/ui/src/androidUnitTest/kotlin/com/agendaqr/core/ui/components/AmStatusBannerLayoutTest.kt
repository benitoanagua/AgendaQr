package com.agendaqr.core.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.Density
import com.agendaqr.core.ui.theme.AmTheme
import kotlin.test.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], qualifiers = "w360dp-h760dp")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class AmStatusBannerLayoutTest {

    @get:Rule
    val compose = createComposeRule()

    private val longMessage = "Este es un mensaje largo de error para verificar que el banner envuelve correctamente y no desborda la pantalla en anchos pequeños."

    private fun runScenario(dark: Boolean, scale: Float) {
        compose.setContent {
            CompositionLocalProvider(LocalDensity provides Density(1f, scale)) {
                AmTheme(darkTheme = dark) {
                    Column {
                        AmStatusBanner(
                            message = longMessage,
                            tone = AmTone.Danger,
                            action = AmBannerAction("Reintentar", {}),
                            dismiss = AmBannerAction("DESCARTAR", {}),
                        )
                    }
                }
            }
        }
        compose.waitForIdle()
        val screenRight = compose.onRoot().getBoundsInRoot().right
        val banner = compose.onNodeWithTag("am-status-banner").getBoundsInRoot()
        val bannerWidth = banner.right.value - banner.left.value
        assertTrue(
            bannerWidth >= screenRight.value - 1f,
            "el banner no ocupa todo el ancho disponible (dark=$dark scale=$scale): $bannerWidth / ${screenRight.value}",
        )
        val messageNode = compose.onNodeWithText(longMessage, substring = true).getBoundsInRoot()
        assertTrue(
            messageNode.left.value >= banner.left.value,
            "el mensaje empieza fuera del banner (dark=$dark scale=$scale)",
        )
        val node = compose.onNodeWithText("Reintentar").getBoundsInRoot()
        assertTrue(
            node.right.value <= screenRight.value + 1f,
            "accion desborda (right=${node.right}) dark=$dark scale=$scale",
        )
    }

    @Test
    fun at_360_light_scale_1_0() { runScenario(false, 1.0f) }

    @Test
    fun at_360_dark_scale_1_3() { runScenario(true, 1.3f) }

    @Test
    fun at_360_light_scale_2_0() { runScenario(false, 2.0f) }

    @Test
    @Config(sdk = [34], qualifiers = "w320dp-h640dp")
    fun at_320_dark_scale_2_0() { runScenario(true, 2.0f) }
}