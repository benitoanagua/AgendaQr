package com.agendaqr.core.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agendaqr.core.ui.theme.LightXauxaColorScheme
import com.agendaqr.core.ui.theme.XauxaAccents
import com.agendaqr.core.ui.theme.XauxaTheme
import com.agendaqr.core.ui.theme.XauxaType
import com.agendaqr.core.ui.theme.contrastRatio
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue
import kotlin.test.fail
import kotlin.math.abs

/**
 * Defectos de pulido UI V1.1 (D1/D2/D6 a nivel de componente).
 *
 * D1: el display de pagina (40 sp) necesita interlineado propio (>= 1.1x);
 * heredar el del cuerpo (24 sp del estilo ambiente) solapa dos lineas.
 * D2: las etiquetas de los tiles 1x1 nunca parten palabras (4 columnas en
 * compacto intactas).
 * D6: el placeholder del buscador vive en una sola linea.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], qualifiers = "w360dp-h800dp")
// NATIVE graphics: la medicion de texto usa fuentes reales (en LEGACY los
// avances son degenerados y ningun wrap ocurre).
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class XauxaUiPolishDefectsTest {

    @get:Rule
    val compose = createComposeRule()

    private fun setScaledContent(fontScale: Float, content: @androidx.compose.runtime.Composable () -> Unit) {
        compose.setContent {
            CompositionLocalProvider(LocalDensity provides Density(1f, fontScale)) {
                XauxaTheme(darkTheme = false) {
                    // Column intermedia: Surface propaga minConstraints a su
                    // hijo directo (lo fuerza a 360x800); sin ella, una caja
                    // de ancho fijo mediria a pantalla completa.
                    Column { content() }
                }
            }
        }
        compose.waitForIdle()
    }

    private fun layoutOf(text: String): TextLayoutResult {
        // Las consultas de layout pueden devolver un layout transitorio
        // (p. ej. altura 0) si una recomposicion tardia sigue pendiente tras
        // waitForIdle: se reintenta hasta obtener dos lecturas estables
        // seguidas del NODO SIN FUSIONAR (el fusionado expone el layout del
        // campo vacio en placeholders, no el del texto visible).
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
        // Sin estabilizar: la ultima lectura valida, para que el mensaje de
        // la asercion describa el estado real en vez de un transitorio.
        return last ?: fail("sin TextLayoutResult para '$text'")
    }

    private fun assertNoBrokenWords(layout: TextLayoutResult, label: String) {
        val full = layout.layoutInput.text.text
        for (i in 0 until layout.lineCount) {
            val start = layout.getLineStart(i)
            val end = layout.getLineEnd(i)
            if (end <= start) continue
            if (end < full.length) {
                val lastChar = full[end - 1]
                val nextChar = full[end]
                assertTrue(
                    !lastChar.isLetterOrDigit() || !nextChar.isLetterOrDigit(),
                    "la etiqueta '$label' parte una palabra en la linea $i " +
                        "('${full.substring(start, end)}'|'${full.substring(end)}')",
                )
            }
        }
    }

    private fun assertLinesDoNotOverlap(title: String, layout: TextLayoutResult) {
        val style = layout.layoutInput.style
        val ratio = style.lineHeight.value / style.fontSize.value
        assertTrue(
            ratio >= 1.1f,
            "'$title': lineHeight ${style.lineHeight} < 1.1x de ${style.fontSize} (ratio=$ratio)",
        )
        if (layout.lineCount >= 2) {
            val first = layout.getLineBottom(0) - layout.getLineTop(0)
            val total = layout.getLineBottom(layout.lineCount - 1) - layout.getLineTop(0)
            assertTrue(
                total >= 2 * first * 0.95f,
                "'$title': las lineas se solapan (total=$total, primera=$first)",
            )
        } else {
            fail("'$title': se esperaban dos lineas a 328 dp y no hubo wrap")
        }
    }

    // --- D1: interlineado del display de pagina ---

    private fun pageTitleNoOverlapAt(fontScale: Float) {
        val title = "Resultado de importación"
        setScaledContent(fontScale) {
            // S12 a 360 dp: contenedor de 328 dp (margen 16 por lado).
            Box(modifier = Modifier.width(328.dp)) { XauxaPageTitle(title) }
        }
        assertLinesDoNotOverlap(title, layoutOf(title))
    }

    @Test
    fun d1_pageTitle_twoLines_doNotOverlap_at100() = pageTitleNoOverlapAt(1.0f)

    @Test
    fun d1_pageTitle_twoLines_doNotOverlap_at130() = pageTitleNoOverlapAt(1.3f)

    @Test
    fun d1_pageTitle_twoLines_doNotOverlap_at200() = pageTitleNoOverlapAt(2.0f)

    @Test
    fun d1_pageTitle_singleLine_keeps_display_metrics() {
        setScaledContent(1.0f) { XauxaPageTitle("Agenda QR") }
        val style = layoutOf("Agenda QR").layoutInput.style
        // Display ligero M6: 40 sp / Archivo Light; interlineado propio 46 sp.
        assertEquals(40.sp, style.fontSize)
        assertEquals(46.sp, style.lineHeight)
        assertEquals(FontWeight.Light, style.fontWeight)
        assertTrue(
            style.fontFamily != null && style.fontFamily != androidx.compose.ui.text.font.FontFamily.Default,
            "el titulo de pagina usa la familia display empaquetada, no la del sistema",
        )
    }

    @Test
    fun d1_heading_does_not_inherit_body_lineheight() {
        setScaledContent(1.0f) {
            Column {
                XauxaHeading(
                    text = "Titular de comprobante pendiente de revision larga",
                    size = XauxaType.Headline,
                )
            }
        }
        val layout = layoutOf("Titular de comprobante pendiente de revision larga")
        val style = layout.layoutInput.style
        // Headline 24 sp con el interlineado ambiente (24 sp) queda a 1.0x:
        // pegado. El encabezado fija interlineado propio (> 1.1x).
        val ratio = style.lineHeight.value / style.fontSize.value
        assertTrue(ratio >= 1.1f, "heading hereda interlineado ambiente (ratio=$ratio)")
        assertLinesDoNotOverlap("heading", layout)
    }

    @Test
    fun d1_body_text_keeps_canonical_lineheight() {
        // Caracterizacion: el cuerpo por defecto ya era 16/24 (sin cambio
        // visual); el arreglo solo cierra la herencia para tamanos mayores.
        setScaledContent(1.0f) {
            XauxaText("Cuerpo de dos lineas para probar interlineado expandido")
        }
        val style = layoutOf("Cuerpo de dos lineas para probar interlineado expandido").layoutInput.style
        assertEquals(16.sp, style.fontSize)
        assertEquals(24.sp, style.lineHeight)
    }

    @Test
    fun d1_large_body_text_does_not_overlap() {
        // Sonda del mecanismo: con el estilo ambiente, todo tamano > 24 sp
        // solapaba. El texto fija interlineado proporcional a su tamano.
        setScaledContent(1.0f) {
            Box(modifier = Modifier.width(328.dp)) {
                XauxaText(
                    text = "Texto grande de comprobante pendiente de revision",
                    size = 28.sp,
                )
            }
        }
        val layout = layoutOf("Texto grande de comprobante pendiente de revision")
        val style = layout.layoutInput.style
        val ratio = style.lineHeight.value / style.fontSize.value
        assertTrue(ratio >= 1.1f, "texto grande hereda interlineado ambiente (ratio=$ratio)")
        assertLinesDoNotOverlap("texto grande", layout)
    }

    // --- D2: etiquetas de tiles sin partir palabras ---

    private val s01TileLabels = listOf("Añadir", "Registrar", "Favoritos", "Contextos")
    private val activatedLabel = "Favoritos: activado"
    private val wideTileLabel = "Último QR o actividad reciente"

    /**
     * Rejilla real de S01 a 360 dp (contenido de 328 dp con el margen de
     * pantalla): 4 columnas a fontScale <= 1.15, 3 a 1.3 y 2 a 2.0. La
     * rejilla reduce columnas; la etiqueta nunca parte palabras.
     */
    private fun tileGridLabelsDoNotSplitAt(fontScale: Float) {
        val labels = s01TileLabels + activatedLabel + wideTileLabel
        setScaledContent(fontScale) {
            // Altura acotada: la zona flexible del tile (weight 1f) comeria
            // todo el alto libre de una Column sin limite y empujaria los
            // tiles fuera de la pantalla (layouts de altura 0).
            Box(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp)) {
                XauxaTileGrid(
                    items = buildList {
                        (s01TileLabels + activatedLabel).forEach { label ->
                            add(
                                XauxaTileItem(
                                    label = label,
                                    accent = XauxaAccents.System,
                                    icon = XauxaIcons.Favorite,
                                    onClick = {},
                                ),
                            )
                        }
                        add(
                            XauxaTileItem(
                                label = wideTileLabel,
                                accent = XauxaAccents.System,
                                size = XauxaTileSize.WIDE,
                                onClick = {},
                            ),
                        )
                    },
                )
            }
        }
        compose.waitForIdle()
        labels.forEach { label -> assertNoBrokenWords(layoutOf(label), label) }
    }

    @Test
    fun d2_tile_labels_do_not_split_words_at100() = tileGridLabelsDoNotSplitAt(1.0f)

    @Test
    fun d2_tile_labels_do_not_split_words_at115() = tileGridLabelsDoNotSplitAt(1.15f)

    @Test
    fun d2_tile_labels_do_not_split_words_at130() = tileGridLabelsDoNotSplitAt(1.3f)

    @Test
    fun d2_tile_labels_do_not_split_words_at200() = tileGridLabelsDoNotSplitAt(2.0f)

    // --- D3: icono del tile centrado (M3) ---

    private fun unmergedNodes(): List<SemanticsNode> {
        val all = mutableListOf<SemanticsNode>()
        fun walk(node: SemanticsNode) {
            all.add(node)
            node.children.forEach(::walk)
        }
        walk(compose.onRoot(useUnmergedTree = true).fetchSemanticsNode())
        return all
    }

    @Test
    fun d3_tile_icon_is_centered_over_flex_zone() {
        setScaledContent(1.0f) {
            // Caja acotada en ambas dimensiones (ver D2): el alto libre
            // desboca la zona flexible del tile.
            Box(modifier = Modifier.width(200.dp).height(76.dp)) {
                XauxaMetroTile(
                    label = "Probe",
                    accent = XauxaAccents.System,
                    icon = XauxaIcons.Favorite,
                    onClick = {},
                )
            }
        }
        val all = unmergedNodes()
        val tile = all.singleOrNull { it.config.getOrNull(SemanticsProperties.Role) == Role.Button }
        assertNotNull(tile, "tile con rol Button no encontrado en el arbol sin fusionar")
        val pxPerDp = tile.size.width / 200f
        // El icono es el unico bloque de 32 dp (IconSizeTile) del tile.
        val icon = all.singleOrNull {
            it != tile &&
                kotlin.math.abs(it.size.width - 32f * pxPerDp) <= 1f &&
                kotlin.math.abs(it.size.height - 32f * pxPerDp) <= 1f
        }
        assertNotNull(icon, "icono de 32 dp no encontrado en el tile")
        // Decorativo: sin rol, sin texto y sin descripcion (la etiqueta del
        // tile ya es el nombre accesible, §11/M12).
        assertNull(icon.config.getOrNull(SemanticsProperties.Role))
        assertNull(icon.config.getOrNull(SemanticsProperties.Text))
        assertNull(icon.config.getOrNull(SemanticsProperties.ContentDescription))
        val tileCenterX = tile.positionInRoot.x + tile.size.width / 2f
        val iconCenterX = icon.positionInRoot.x + icon.size.width / 2f
        assertTrue(
            abs(iconCenterX - tileCenterX) <= pxPerDp,
            "icono descentrado: centro del icono=$iconCenterX, centro del tile=$tileCenterX (tolerancia 1 dp)",
        )
        // La etiqueta sigue abajo a la izquierda (M3).
        val label = all.singleOrNull {
            it.config.getOrNull(SemanticsProperties.Text)?.any { t -> t.text == "Probe" } == true
        }
        assertNotNull(label, "etiqueta 'Probe' no encontrada en el tile")
        assertTrue(
            label.positionInRoot.x - tile.positionInRoot.x <= 8f * pxPerDp,
            "la etiqueta ya no esta abajo a la izquierda",
        )
        assertTrue(
            label.positionInRoot.y > icon.positionInRoot.y,
            "la etiqueta ya no esta debajo del icono",
        )
    }

    // --- D6 (componente): placeholder en una linea ---

    @Test
    fun d6_search_placeholder_stays_on_single_line() {
        // Garantia del COMPONENTE con un copy deliberadamente largo (el
        // viejo placeholder de 51 caracteres): maxLines=1 lo contiene en
        // una linea (con ellipsis). Que el copy REAL quepa sin truncarse
        // se fija en feature (S02UiPolishTest) con AppStrings.
        val placeholder = "Buscar QR, actividades, comprobantes o contextos"
        setScaledContent(1.0f) {
            Box(modifier = Modifier.width(328.dp)) {
                XauxaSearchBar(
                    value = "",
                    onValueChange = {},
                    label = "Buscar en Agenda QR",
                    placeholder = placeholder,
                )
            }
        }
        val layout = layoutOf(placeholder)
        assertEquals(1, layout.lineCount, "el placeholder ocupa ${layout.lineCount} lineas a 360 dp")
    }

    // --- D4 (contraste del encabezado de seccion, puro) ---

    @Test
    fun d4_section_header_accent_meets_text_contrast_in_both_themes() {
        val light = contrastRatio(LightXauxaColorScheme.brandText, LightXauxaColorScheme.background)
        assertTrue(light >= 4.5, "acento de seccion en claro: $light < 4.5")
        val dark = contrastRatio(
            com.agendaqr.core.ui.theme.DarkXauxaColorScheme.brandText,
            com.agendaqr.core.ui.theme.DarkXauxaColorScheme.background,
        )
        assertTrue(dark >= 4.5, "acento de seccion en oscuro: $dark < 4.5")
    }
}
