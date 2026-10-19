package com.agendaqr.destinations.presentation

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.getUnclippedBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agendaqr.core.ui.theme.AmSpacing
import com.agendaqr.core.ui.theme.AmTheme
import com.agendaqr.destinations.domain.ImportBatch
import com.agendaqr.destinations.domain.ImportCandidate
import com.agendaqr.destinations.domain.ImportKind
import com.agendaqr.destinations.domain.QrAsset
import kotlin.math.abs
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Bloque de resultado de S12 (Resultado de importación): el contenedor del
 * mensaje (error Y confirmación de guardado) debe ocupar TODO el ancho útil
 * del contenido de la pantalla (un solo margen de pantalla, sin duplicar el
 * del scaffold), con título y descripción completos, jerarquía tipográfica
 * clara, crecimiento vertical libre y las acciones/etapas del resumen
 * intactas (ADR-0007: verificación por aserciones, sin capturas
 * versionadas).
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], qualifiers = "w360dp-h740dp")
class ImportBatchBannerLayoutTest {

    @get:Rule
    val compose = createComposeRule()

    /** El banner es el único nodo con liveRegion en el estado de error. */
    private val bannerMatcher = SemanticsMatcher("liveRegion") {
        it.config.contains(SemanticsProperties.LiveRegion)
    }

    private var fontScale by mutableFloatStateOf(1f)

    /** Compone S12 en estado de error con el lote de referencia. */
    private fun composeErrorScreen(
        dark: Boolean,
        scale: Float = 1f,
        error: UserFacingError? = null,
    ) {
        fontScale = scale
        compose.setContent {
            val density = LocalDensity.current.density
            AmTheme(darkTheme = dark) {
                CompositionLocalProvider(LocalDensity provides Density(density, fontScale)) {
                    ImportBatchScreen(
                        state = ImportBatchUiState.Error(
                            error = error ?: userFacingError(
                                IllegalStateException("batch save failed"),
                                ErrorFlow.ImportBatchSave,
                            ),
                            batch = importBatchFixture(),
                        ),
                        onAction = {},
                    )
                }
            }
        }
        compose.waitForIdle()
    }

    /**
     * Lote: 2 reconocidos únicos, 1 duplicado y 1 sin clasificar →
     * resumen "2 / 1 / 1", "Revisar 2 pendientes" y acción fija
     * "Reintentar guardado".
     */
    private fun importBatchFixture() = ImportBatch(
        candidates = listOf(
            ImportCandidate(
                id = "imp-1", kind = ImportKind.QR, fingerprint = "fp-1",
                mimeType = "image/png", extension = "png",
                qrAsset = QrAsset(encoded = QrTestFixture.base64Png),
            ),
            ImportCandidate(
                id = "imp-2", kind = ImportKind.QR, fingerprint = "fp-2",
                mimeType = "image/png", extension = "png",
                qrAsset = QrAsset(encoded = QrTestFixture.base64Png),
            ),
            ImportCandidate(
                id = "imp-3", kind = ImportKind.QR, fingerprint = "fp-1",
                mimeType = "image/png", extension = "png",
                qrAsset = QrAsset(encoded = QrTestFixture.base64Png),
            ),
            ImportCandidate(
                id = "imp-4", kind = ImportKind.DESCONOCIDO, fingerprint = "fp-4",
                mimeType = "image/png", extension = "png", error = "No se pudo clasificar",
            ),
        ),
    )

    /**
     * El bloque llena el ancho útil (sin `width`/`widthIn` que lo estrechen)
     * Y su contenedor arranca y termina en el margen ÚNICO de pantalla:
     * el margen propio del banner no se suma al del scaffold.
     */
    private fun assertBannerFillsContentWidth(label: String) {
        val root = compose.onRoot().getUnclippedBoundsInRoot()
        val banner = compose.onNode(bannerMatcher).getUnclippedBoundsInRoot()
        val margin = AmSpacing.ScreenMargin
        val bannerWidth = banner.right - banner.left
        val expectedWidth = root.right - root.left - margin * 2
        println(
            "$label: root=${root.right - root.left} banner=$bannerWidth " +
                "esperado=$expectedWidth bannerLeft=${banner.left} bannerRight=${banner.right}",
        )
        assertTrue(
            "$label: el bloque empieza antes que el margen único de pantalla " +
                "(left=${banner.left}, esperado=$margin)",
            abs(banner.left.value - margin.value) <= 1f,
        )
        assertTrue(
            "$label: el bloque no llega al borde derecho del contenido " +
                "(right=${banner.right}, root.right=${root.right}, margen=$margin)",
            abs((root.right - banner.right).value - margin.value) <= 1f,
        )
        assertTrue(
            "$label: el bloque no ocupa el ancho útil " +
                "(banner=$bannerWidth, esperado=$expectedWidth)",
            abs(bannerWidth.value - expectedWidth.value) <= 1f,
        )
    }

    /** Textos dados completos, visibles y sin recorte lateral. */
    private fun assertTextsComplete(
        label: String,
        texts: List<String> = listOf(
            AppStrings.ErrorImportBatchSaveWhat,
            AppStrings.ErrorImportBatchSaveData,
        ),
    ) {
        val root = compose.onRoot().getUnclippedBoundsInRoot()
        texts.forEach { text ->
            val bounds = compose.onNodeWithText(text).getUnclippedBoundsInRoot()
            println("$label: '$text' -> $bounds")
            compose.onNodeWithText(text).assertIsDisplayed()
            assertTrue(
                "$label: texto recortado o fuera de pantalla: '$text' $bounds (root=$root)",
                bounds.left >= root.left - 1.dp &&
                    bounds.right <= root.right + 1.dp &&
                    bounds.right - bounds.left > 0.dp,
            )
        }
    }

    /** El bloque no solapa la tarjeta de resumen ni las acciones. */
    private fun assertNoOverlap(label: String) {
        val banner = compose.onNode(bannerMatcher).getUnclippedBoundsInRoot()
        val card = compose.onNodeWithText(AppStrings.Reconocidos).getUnclippedBoundsInRoot()
        println("$label: bannerBottom=${banner.bottom} cardTop=${card.top}")
        assertTrue(
            "$label: el bloque solapa la tarjeta de resumen (banner=$banner card=$card)",
            banner.bottom <= card.top,
        )
    }

    /**
     * TextLayoutResult del nodo de texto (dos lecturas estables sin
     * fusionar, igual que en S02UiPolishTest/AmUiPolishContractTest).
     */
    private fun layoutOf(text: String): TextLayoutResult {
        fun query(): TextLayoutResult? {
            var current: TextLayoutResult? = null
            runCatching {
                compose.onNodeWithText(text, useUnmergedTree = true).assert(
                    SemanticsMatcher("GetTextLayoutResult($text)") { node ->
                        val out = mutableListOf<TextLayoutResult>()
                        val action = node.config.getOrNull(SemanticsActions.GetTextLayoutResult)
                        if (action?.action?.invoke(out) == true && out.isNotEmpty()) {
                            current = out.first()
                            true
                        } else {
                            false
                        }
                    },
                )
            }
            return current
        }
        var lastKey: String? = null
        var last: TextLayoutResult? = null
        repeat(10) {
            compose.waitForIdle()
            val current = query()
            val key = current?.let {
                "${it.layoutInput.text.text}|${it.lineCount}|${it.size}"
            }
            val valid = current != null && current.layoutInput.text.text == text &&
                current.size.height > 0
            if (valid && key == lastKey) return current!!
            lastKey = key
            if (valid) last = current
        }
        return last ?: error("sin TextLayoutResult para '$text'")
    }

    @Test
    fun error_block_fills_content_width_light() {
        composeErrorScreen(dark = false)
        assertBannerFillsContentWidth("360-light-font100")
        assertTextsComplete("360-light-font100")
        assertNoOverlap("360-light-font100")
    }

    @Test
    fun error_block_fills_content_width_dark() {
        composeErrorScreen(dark = true)
        assertBannerFillsContentWidth("360-dark-font100")
        assertTextsComplete("360-dark-font100")
        assertNoOverlap("360-dark-font100")
    }

    @Test
    @Config(sdk = [34], qualifiers = "w320dp-h640dp")
    fun error_block_fills_content_width_compact_320dp() {
        composeErrorScreen(dark = false)
        assertBannerFillsContentWidth("320-light-font100")
        assertTextsComplete("320-light-font100")
        assertNoOverlap("320-light-font100")
    }

    @Test
    fun error_block_grows_with_large_font_scale() {
        composeErrorScreen(dark = false)
        val at100 = compose.onNode(bannerMatcher).getUnclippedBoundsInRoot()
        val at100Height = at100.bottom - at100.top
        assertBannerFillsContentWidth("360-light-font100")
        compose.runOnIdle { fontScale = 2f }
        compose.waitForIdle()
        val at200 = compose.onNode(bannerMatcher).getUnclippedBoundsInRoot()
        val at200Height = at200.bottom - at200.top
        println("font200: banner=$at200 (font100=$at100)")
        assertTrue(
            "el bloque no crece con la escala de fuente (100%=$at100Height, 200%=$at200Height)",
            at200Height > at100Height,
        )
        assertBannerFillsContentWidth("360-light-font200")
        assertTextsComplete("360-light-font200")
        assertNoOverlap("360-light-font200")
        compose.onNodeWithText(reviewPendingLabel(importBatchFixture())).assertIsDisplayed()
        compose.onNodeWithText(AppStrings.reintentarGuardado).assertIsDisplayed()
    }

    @Test
    fun error_state_keeps_summary_and_actions() {
        composeErrorScreen(dark = false)
        listOf(
            AppStrings.Reconocidos,
            AppStrings.PosiblesDuplicados,
            AppStrings.PendientesDeRevision,
        ).forEach { compose.onNodeWithText(it).assertIsDisplayed() }
        compose.onNodeWithText(reviewPendingLabel(importBatchFixture())).assertIsDisplayed()
        compose.onNodeWithText(AppStrings.reintentarGuardado).assertIsDisplayed()
        assertNoOverlap("360-light-font100")
    }

    /**
     * Jerarquía del bloque: título semibold dominante con semántica de
     * encabezado, descripción de cuerpo normal (sin heading) y separación
     * vertical clara; el icono es discreto (compacto) y arranca a la
     * altura del inicio del título.
     */
    @Test
    fun error_block_shows_title_and_description_hierarchy() {
        composeErrorScreen(dark = false)
        val title = AppStrings.ErrorImportBatchSaveWhat
        val description = AppStrings.ErrorImportBatchSaveData

        // Título: 16 sp semibold (antes 18 sp bold) + heading().
        val titleStyle = layoutOf(title).layoutInput.style
        assertTrue(
            "título fontSize=${titleStyle.fontSize}, esperado 16.sp",
            titleStyle.fontSize == 16.sp,
        )
        assertTrue(
            "título fontWeight=${titleStyle.fontWeight}, esperado SemiBold",
            titleStyle.fontWeight == FontWeight.SemiBold,
        )
        compose.onNodeWithText(title)
            .assert(SemanticsMatcher.keyIsDefined(SemanticsProperties.Heading))

        // Descripción: 14 sp de cuerpo, peso normal, sin heading().
        val descStyle = layoutOf(description).layoutInput.style
        assertTrue(
            "descripción fontSize=${descStyle.fontSize}, esperado 14.sp",
            descStyle.fontSize == 14.sp,
        )
        val descWeight = descStyle.fontWeight
        assertTrue(
            "descripción con peso dominante: $descWeight",
            descWeight == null || descWeight < FontWeight.SemiBold,
        )
        compose.onNodeWithText(description)
            .assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.Heading))

        // Icono compacto: margen Md (12) + icono 18 + hueco Sm (8) = 38
        // desde el borde del bloque hasta el inicio del título.
        val banner = compose.onNode(bannerMatcher).getUnclippedBoundsInRoot()
        val titleBounds = compose.onNodeWithText(title).getUnclippedBoundsInRoot()
        val iconOffset = titleBounds.left - banner.left
        assertTrue(
            "el título no arranca tras el icono compacto (offset=$iconOffset, esperado 38.dp)",
            abs(iconOffset.value - 38f) <= 1f,
        )

        // Separación vertical clara entre título y descripción (Xs=4).
        val descBounds = compose.onNodeWithText(description).getUnclippedBoundsInRoot()
        val gap = descBounds.top - titleBounds.bottom
        println("jerarquía: title=$titleBounds desc=$descBounds gap=$gap")
        assertTrue(
            "sin separación título/descripción (gap=$gap)",
            gap.value >= 1f,
        )
    }

    /**
     * Copy largo: título y descripción envuelven a varias líneas sin
     * recorte, sin solapes y con el bloque sigue al ancho útil completo.
     */
    @Test
    fun error_block_wraps_long_copy_without_clipping() {
        val longTitle = "No pudimos guardar la importación completa del lote, " +
            "incluidos todos los elementos reconocidos y sus posibles duplicados."
        val longDescription = "Los elementos reconocidos siguen pendientes de " +
            "guardar en este dispositivo hasta que vuelvas a intentarlo con " +
            "conexión disponible."
        composeErrorScreen(
            dark = false,
            error = UserFacingError(
                what = longTitle,
                dataStatus = longDescription,
                action = ErrorAction.Retry,
            ),
        )
        val titleLayout = layoutOf(longTitle)
        assertTrue(
            "el título largo debe envolver (lineCount=${titleLayout.lineCount})",
            titleLayout.lineCount >= 2,
        )
        val descLayout = layoutOf(longDescription)
        assertTrue(
            "la descripción larga debe envolver (lineCount=${descLayout.lineCount})",
            descLayout.lineCount >= 2,
        )
        assertBannerFillsContentWidth("360-light-long-copy")
        assertTextsComplete(
            "360-light-long-copy",
            listOf(longTitle, longDescription),
        )
        assertNoOverlap("360-light-long-copy")
        compose.onNodeWithText(reviewPendingLabel(importBatchFixture())).assertIsDisplayed()
        compose.onNodeWithText(AppStrings.reintentarGuardado).assertIsDisplayed()
    }

    // ---------------------------------------------------------------------
    // Éxito: la confirmación de guardado comparte geometría con el error.
    // ---------------------------------------------------------------------

    /** Compone S12 en estado Saved (confirmación de guardado). */
    private fun composeSavedScreen(dark: Boolean = false) {
        compose.setContent {
            AmTheme(darkTheme = dark) {
                ImportBatchScreen(
                    state = ImportBatchUiState.Saved(batch = importBatchFixture()),
                    onAction = {},
                )
            }
        }
        compose.waitForIdle()
    }

    /**
     * §10: los estados de error y éxito mantienen la MISMA geometría —
     * un solo margen de pantalla y ancho útil completo — con sus textos
     * completos y sin solapes con el resto del estado.
     */
    @Test
    fun saved_block_matches_error_geometry() {
        composeSavedScreen(dark = false)
        assertBannerFillsContentWidth("360-light-saved")
        assertTextsComplete(
            "360-light-saved",
            listOf(AppStrings.GuardadoOk, AppStrings.batchSavedKeepReviewNotice),
        )
        // El glyph de éxito y el bloque no solapan la acción de revisión.
        val banner = compose.onNode(bannerMatcher).getUnclippedBoundsInRoot()
        val reviewButton = compose
            .onNodeWithText(reviewPendingLabel(importBatchFixture()))
            .getUnclippedBoundsInRoot()
        println("saved: banner=$banner reviewButton=$reviewButton")
        assertTrue(
            "el bloque solapa la acción de revisar (banner=$banner button=$reviewButton)",
            banner.bottom <= reviewButton.top,
        )
        compose.onNodeWithText(reviewPendingLabel(importBatchFixture())).assertIsDisplayed()
    }

    @Test
    @Config(sdk = [34], qualifiers = "w320dp-h640dp")
    fun saved_block_matches_error_geometry_compact_320dp() {
        composeSavedScreen(dark = true)
        assertBannerFillsContentWidth("320-dark-saved")
        assertTextsComplete(
            "320-dark-saved",
            listOf(AppStrings.GuardadoOk, AppStrings.batchSavedKeepReviewNotice),
        )
    }
}
