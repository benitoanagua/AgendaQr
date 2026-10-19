package com.agendaqr.core.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.unit.Density
import com.agendaqr.core.ui.theme.AmSpacing
import com.agendaqr.core.ui.theme.AmTheme
import kotlin.math.abs
import kotlin.test.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Contrato de ancho/margen de [AmStatusBanner]: el bloque tonal ocupa el
 * ancho útil de SU contenedor y el margen de pantalla se aplica exactamente
 * una vez, según el tipo de contenedor:
 * - host edge-to-edge (p. ej. slot `statusContent`, app demo, overlay):
 *   el banner pide `screenMargin = AmSpacing.ScreenMargin`.
 * - contenido ya acotado por AmScreenScaffold: parámetro por defecto
 *   (cero) y el scaffold pone el único margen.
 * Cubre 320/360 dp, claro/oscuro, escala 100/200 %, con/sin título,
 * acciones y descarte, y copy largo en español (ADR-0007: aserciones de
 * límites reales, sin capturas versionadas).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], qualifiers = "w360dp-h760dp")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class AmStatusBannerLayoutTest {

    @get:Rule
    val compose = createComposeRule()

    private val longMessage = "Este es un mensaje largo de error para verificar que el banner envuelve correctamente y no desborda la pantalla en anchos pequeños."

    private val shortMessage = "No pudimos completar la operación."

    private val screenMargin = AmSpacing.ScreenMargin

    private var fontScale by mutableFloatStateOf(1f)

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

    // ---------------------------------------------------------------------
    // Contrato de margen: exactamente UN margen de pantalla por tipo de
    // contenedor (tolerancia 1 dp).
    // ---------------------------------------------------------------------

    /** Host edge-to-edge real: el banner pide su margen de pantalla. */
    private fun composeEdgeToEdgeHost(
        dark: Boolean = false,
        title: String? = null,
        action: Boolean = true,
        dismiss: Boolean = true,
        message: String = shortMessage,
    ) {
        compose.setContent {
            CompositionLocalProvider(LocalDensity provides Density(1f, fontScale)) {
                AmTheme(darkTheme = dark) {
                    // Igual que los hosts reales (AppStatusHost/demo/cámara):
                    // el banner cuelga de un Column, no directo del Surface.
                    Column {
                        AmStatusBanner(
                            message = message,
                            title = title,
                            tone = AmTone.Danger,
                            action = if (action) AmBannerAction("Reintentar", {}) else null,
                            dismiss = if (dismiss) AmBannerAction("DESCARTAR", {}) else null,
                            screenMargin = screenMargin,
                        )
                    }
                }
            }
        }
        compose.waitForIdle()
    }

    /** Réplica del contenido acotado de AmScreenScaffold (su margen). */
    private fun composeScaffoldPaddedContent(dark: Boolean = false) {
        compose.setContent {
            CompositionLocalProvider(LocalDensity provides Density(1f, fontScale)) {
                AmTheme(darkTheme = dark) {
                    Column(
                        modifier = Modifier.padding(horizontal = screenMargin),
                    ) {
                        AmStatusBanner(
                            message = shortMessage,
                            tone = AmTone.Danger,
                            action = AmBannerAction("Reintentar", {}),
                            dismiss = AmBannerAction("DESCARTAR", {}),
                        )
                    }
                }
            }
        }
        compose.waitForIdle()
    }

    /** Un solo margen de pantalla a izquierda y derecha, ancho útil exacto. */
    private fun assertSingleScreenMargin(label: String) {
        val root = compose.onRoot().getBoundsInRoot()
        val banner = compose.onNodeWithTag("am-status-banner").getBoundsInRoot()
        val usefulWidth = root.right - root.left - screenMargin * 2
        val bannerWidth = banner.right - banner.left
        assertTrue(
            abs(banner.left.value - screenMargin.value) <= 1f,
            "$label: margen izquierdo ${banner.left}, esperado $screenMargin",
        )
        assertTrue(
            abs((root.right - banner.right).value - screenMargin.value) <= 1f,
            "$label: margen derecho ${root.right - banner.right}, esperado $screenMargin",
        )
        assertTrue(
            abs(bannerWidth.value - usefulWidth.value) <= 1f,
            "$label: ancho $bannerWidth != ancho útil $usefulWidth " +
                "(¿margen duplicado o ausente?)",
        )
    }

    /** Todo el contenido dado queda dentro del bloque tonal. */
    private fun assertContentInsideBanner(label: String, vararg texts: String) {
        val banner = compose.onNodeWithTag("am-status-banner").getBoundsInRoot()
        texts.forEach { text ->
            val node = compose.onNodeWithText(text, substring = true).getBoundsInRoot()
            assertTrue(
                node.left.value >= banner.left.value - 1f &&
                    node.right.value <= banner.right.value + 1f &&
                    node.top.value >= banner.top.value - 1f &&
                    node.bottom.value <= banner.bottom.value + 1f,
                "$label: '$text' sale del bloque ($node vs $banner)",
            )
        }
    }

    /** El nodo de la interacción (p. ej. descarte) queda dentro del bloque. */
    private fun SemanticsNodeInteraction.assertIsInsideBanner(label: String) {
        val banner = compose.onNodeWithTag("am-status-banner").getBoundsInRoot()
        val bounds = fetchSemanticsNode().boundsInRoot
        assertTrue(
            bounds.left >= banner.left.value - 1f &&
                bounds.right <= banner.right.value + 1f &&
                bounds.top >= banner.top.value - 1f &&
                bounds.bottom <= banner.bottom.value + 1f,
            "$label: nodo fuera del bloque ($bounds vs $banner)",
        )
    }

    @Test
    fun edge_to_edge_host_applies_single_margin_360_light() {
        composeEdgeToEdgeHost(dark = false)
        assertSingleScreenMargin("host-360-light")
        assertContentInsideBanner("host-360-light", shortMessage, "Reintentar")
        compose.onNodeWithContentDescription("DESCARTAR").assertIsInsideBanner("host-360-light")
    }

    @Test
    fun edge_to_edge_host_applies_single_margin_360_dark() {
        composeEdgeToEdgeHost(dark = true)
        assertSingleScreenMargin("host-360-dark")
    }

    @Test
    @Config(sdk = [34], qualifiers = "w320dp-h640dp")
    fun edge_to_edge_host_applies_single_margin_320_light() {
        composeEdgeToEdgeHost(dark = false)
        assertSingleScreenMargin("host-320-light")
        assertContentInsideBanner("host-320-light", shortMessage, "Reintentar")
    }

    @Test
    fun scaffold_padded_content_applies_margin_once_360_light() {
        composeScaffoldPaddedContent(dark = false)
        assertSingleScreenMargin("scaffold-360-light")
        assertContentInsideBanner("scaffold-360-light", shortMessage, "Reintentar")
    }

    @Test
    @Config(sdk = [34], qualifiers = "w320dp-h640dp")
    fun scaffold_padded_content_applies_margin_once_320_dark() {
        composeScaffoldPaddedContent(dark = true)
        assertSingleScreenMargin("scaffold-320-dark")
    }

    // ---------------------------------------------------------------------
    // Configuraciones del banner: título/acciones/descarte y copy largo.
    // ---------------------------------------------------------------------

    @Test
    fun message_only_without_title_keeps_geometry() {
        composeEdgeToEdgeHost(title = null, action = false, dismiss = false)
        assertSingleScreenMargin("sin-titulo")
        assertContentInsideBanner("sin-titulo", shortMessage)
        // Sin título no hay nodo de encabezado: el mensaje comunica solo.
        compose.onNodeWithText(shortMessage)
            .assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.Heading))
    }

    @Test
    fun with_title_keeps_hierarchy_and_geometry() {
        composeEdgeToEdgeHost(title = "No pudimos guardar.", action = true, dismiss = true)
        assertSingleScreenMargin("con-titulo")
        assertContentInsideBanner(
            "con-titulo",
            "No pudimos guardar.",
            shortMessage,
            "Reintentar",
        )
        // Título con semántica de encabezado; mensaje sin ella.
        compose.onNodeWithText("No pudimos guardar.")
            .assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.Heading))
        compose.onNodeWithText(shortMessage)
            .assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.Heading))
        // Sin solape vertical entre título y mensaje.
        val title = compose.onNodeWithText("No pudimos guardar.").getBoundsInRoot()
        val message = compose.onNodeWithText(shortMessage).getBoundsInRoot()
        assertTrue(
            message.top.value >= title.bottom.value - 1f,
            "título y mensaje solapados (title=$title message=$message)",
        )
    }

    @Test
    fun action_only_stays_inside_banner() {
        composeEdgeToEdgeHost(action = true, dismiss = false)
        assertSingleScreenMargin("solo-accion")
        assertContentInsideBanner("solo-accion", "Reintentar")
    }

    @Test
    fun dismiss_only_stays_inside_banner() {
        composeEdgeToEdgeHost(action = false, dismiss = true)
        assertSingleScreenMargin("solo-descarte")
        compose.onNodeWithContentDescription("DESCARTAR").assertIsInsideBanner("solo-descarte")
    }

    /** Copy largo + acciones simultáneas: multi-línea, sin recorte ni desborde. */
    @Test
    fun long_message_with_actions_stays_contained_360() {
        composeEdgeToEdgeHost(
            title = "No pudimos guardar la importación completa del lote.",
            message = longMessage,
            action = true,
            dismiss = true,
        )
        assertSingleScreenMargin("copy-largo-360")
        assertContentInsideBanner("copy-largo-360", longMessage, "Reintentar")
        compose.onNodeWithContentDescription("DESCARTAR").assertIsInsideBanner("copy-largo-360")
    }

    @Test
    @Config(sdk = [34], qualifiers = "w320dp-h640dp")
    fun long_message_with_actions_stays_contained_320dp() {
        composeEdgeToEdgeHost(
            message = longMessage,
            action = true,
            dismiss = true,
        )
        assertSingleScreenMargin("copy-largo-320dp")
        assertContentInsideBanner("copy-largo-320dp", longMessage, "Reintentar")
    }

    // ---------------------------------------------------------------------
    // Escala tipográfica 200 %: crecimiento vertical libre + geometría.
    // ---------------------------------------------------------------------

    @Test
    fun font_scale_200_grows_vertically_and_keeps_single_margin() {
        composeEdgeToEdgeHost(dark = false, action = true, dismiss = true)
        val at100 = compose.onNodeWithTag("am-status-banner").getBoundsInRoot()
        compose.runOnIdle { fontScale = 2f }
        compose.waitForIdle()
        val at200 = compose.onNodeWithTag("am-status-banner").getBoundsInRoot()
        println("font200: banner=$at200 (font100=$at100)")
        assertTrue(
            (at200.bottom.value - at200.top.value) > (at100.bottom.value - at100.top.value),
            "el banner no crece con la escala 200 % (100%=$at100, 200%=$at200)",
        )
        assertSingleScreenMargin("font200")
        assertContentInsideBanner("font200", shortMessage, "Reintentar")
        compose.onNodeWithContentDescription("DESCARTAR").assertIsInsideBanner("font200")
    }
}
