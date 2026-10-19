package com.agendaqr.destinations.presentation

import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import com.agendaqr.core.ui.components.AmScreenScaffold
import com.agendaqr.core.ui.components.AmText
import com.agendaqr.core.ui.theme.AmSpacing
import com.agendaqr.core.ui.theme.AmTheme
import kotlin.math.abs
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * AppStatusHost vive en el slot `statusContent` de [AmScreenScaffold]: a
 * ancho completo bajo la barra superior, FUERA del margen del contenido
 * (edge-to-edge). Sus banners piden `screenMargin = ScreenMargin` para
 * quedar al ancho útil con un solo margen — la misma geometría que los
 * banners dentro del contenido de las pantallas (contrato revisado en
 * [AmStatusBannerLayoutTest] y [ImportBatchBannerLayoutTest]).
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], qualifiers = "w360dp-h740dp")
class AppStatusHostBannerLayoutTest {

    @get:Rule
    val compose = createComposeRule()

    /** El banner es el único nodo con liveRegion en el host. */
    private val bannerMatcher = SemanticsMatcher("liveRegion") {
        it.config.contains(SemanticsProperties.LiveRegion)
    }

    /** Rama 1 del host: error recuperable de importación (título+acción). */
    private fun composeImportErrorHost(dark: Boolean = false) {
        compose.setContent {
            AmTheme(darkTheme = dark) {
                AmScreenScaffold(
                    statusContent = {
                        AppStatusHost(
                            importError = "No se pudo leer el archivo seleccionado",
                            isOffline = false,
                            pendingCount = 0,
                            hasFailed = false,
                            onDismissImportError = {},
                            onRetryNow = {},
                        )
                    },
                ) {
                    AmText("Contenido de la pantalla")
                }
            }
        }
        compose.waitForIdle()
    }

    /** Rama 2 del host: mutaciones FAILED con "Reintentar ahora". */
    private fun composeFailedSyncHost(dark: Boolean = false) {
        compose.setContent {
            AmTheme(darkTheme = dark) {
                AmScreenScaffold(
                    statusContent = {
                        AppStatusHost(
                            importError = null,
                            isOffline = false,
                            pendingCount = 3,
                            hasFailed = true,
                            onDismissImportError = {},
                            onRetryNow = {},
                        )
                    },
                ) {
                    AmText("Contenido de la pantalla")
                }
            }
        }
        compose.waitForIdle()
    }

    /** Un solo margen de pantalla a cada lado (tolerancia 1 dp). */
    private fun assertSingleScreenMargin(label: String) {
        val root = compose.onRoot().getBoundsInRoot()
        val banner = compose.onNode(bannerMatcher).getBoundsInRoot()
        val margin = AmSpacing.ScreenMargin
        val width = banner.right - banner.left
        val useful = root.right - root.left - margin * 2
        println(
            "$label: root=${root.right - root.left} banner=$width " +
                "esperado=$useful left=${banner.left} right=${root.right - banner.right}",
        )
        assertTrue(
            "$label: margen izquierdo ${banner.left}, esperado $margin",
            abs(banner.left.value - margin.value) <= 1f,
        )
        assertTrue(
            "$label: margen derecho ${root.right - banner.right}, esperado $margin",
            abs((root.right - banner.right).value - margin.value) <= 1f,
        )
        assertTrue(
            "$label: ancho $width != ancho útil $useful (¿margen ausente o duplicado?)",
            abs(width.value - useful.value) <= 1f,
        )
    }

    /** Los textos dados quedan dentro de los límites del bloque. */
    private fun assertTextsInsideBanner(label: String, vararg texts: String) {
        val banner = compose.onNode(bannerMatcher).getBoundsInRoot()
        texts.forEach { text ->
            val node = compose.onNodeWithText(text, substring = true).getBoundsInRoot()
            assertTrue(
                "$label: '$text' sale del bloque ($node vs $banner)",
                node.left.value >= banner.left.value - 1f &&
                    node.right.value <= banner.right.value + 1f &&
                    node.top.value >= banner.top.value - 1f &&
                    node.bottom.value <= banner.bottom.value + 1f,
            )
        }
    }

    @Test
    fun import_error_banner_fills_useful_width_360_light() {
        composeImportErrorHost(dark = false)
        assertSingleScreenMargin("import-error-360-light")
        val what = importReadError("No se pudo leer el archivo seleccionado").what
        assertTextsInsideBanner(
            "import-error-360-light",
            what,
            AppStrings.ErrorElegirOtraImagen,
        )
        // El contenido de la pantalla no solapa el estado.
        val banner = compose.onNode(bannerMatcher).getBoundsInRoot()
        val content = compose.onNodeWithText("Contenido de la pantalla").getBoundsInRoot()
        assertTrue(
            "el estado solapa el contenido (banner=$banner content=$content)",
            banner.bottom <= content.top,
        )
    }

    @Test
    fun import_error_banner_fills_useful_width_360_dark() {
        composeImportErrorHost(dark = true)
        assertSingleScreenMargin("import-error-360-dark")
    }

    @Test
    @Config(sdk = [34], qualifiers = "w320dp-h640dp")
    fun failed_sync_banner_fills_useful_width_320_light() {
        composeFailedSyncHost(dark = false)
        assertSingleScreenMargin("failed-sync-320-light")
        assertTextsInsideBanner(
            "failed-sync-320-light",
            syncPendingLabel(3, retrying = true),
            AppStrings.ReintentarAhora,
        )
    }
}
