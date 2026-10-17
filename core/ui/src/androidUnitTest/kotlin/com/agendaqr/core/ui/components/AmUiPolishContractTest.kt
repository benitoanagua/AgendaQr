package com.agendaqr.core.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agendaqr.core.ui.theme.DarkAmColorScheme
import com.agendaqr.core.ui.theme.LightAmColorScheme
import com.agendaqr.core.ui.theme.AmTheme
import com.agendaqr.core.ui.theme.AmType
import com.agendaqr.core.ui.theme.contrastRatio
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import kotlin.test.assertEquals
import kotlin.test.assertTrue
import kotlin.test.fail

/**
 * Defectos de pulido UI a nivel de componente (contrato §5).
 *
 * - Interlineado: el encabezado no hereda el del cuerpo; el cuerpo fija
 *   el canónico 14/21; todo tamaño > 24 sp mantiene su proporción.
 * - El acento de sección cumple AA como texto en ambos temas.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], qualifiers = "w360dp-h800dp")
// NATIVE graphics: la medición de texto usa fuentes reales (en LEGACY los
// avances son degenerados y ningún wrap ocurre).
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class AmUiPolishContractTest {

    @get:Rule
    val compose = createComposeRule()

    private fun setScaledContent(fontScale: Float, content: @androidx.compose.runtime.Composable () -> Unit) {
        compose.setContent {
            CompositionLocalProvider(LocalDensity provides Density(1f, fontScale)) {
                AmTheme(darkTheme = false) {
                    // Column intermedia: Surface propaga minConstraints a su
                    // hijo directo (lo fuerza a 360x800); sin ella, una caja
                    // de ancho fijo mediría a pantalla completa.
                    Column { content() }
                }
            }
        }
        compose.waitForIdle()
    }

    private fun layoutOf(text: String): TextLayoutResult {
        // Las consultas de layout pueden devolver un layout transitorio
        // (p. ej. altura 0) si una recomposición tardía sigue pendiente
        // tras waitForIdle: se reintenta hasta obtener dos lecturas
        // estables seguidas del NODO SIN FUSIONAR.
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
        // Sin estabilizar: la última lectura válida, para que el mensaje de
        // la aserción describa el estado real en vez de un transitorio.
        return last ?: fail("sin TextLayoutResult para '$text'")
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
                "'$title': las líneas se solapan (total=$total, primera=$first)",
            )
        } else {
            fail("'$title': se esperaban dos líneas a 328 dp y no hubo wrap")
        }
    }

    @Test
    fun heading_does_not_inherit_body_lineheight() {
        setScaledContent(1.0f) {
            Column {
                AmHeading(
                    text = "Titular de comprobante pendiente de revision larga",
                    size = AmType.Headline,
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
    fun body_text_keeps_canonical_lineheight() {
        // Caracterización: el cuerpo por defecto es 17/25.5 (cuerpo de la
        // referencia con proporción 1.5); AmText aplica la proporción
        // 1.5 → 25.5 sp.
        setScaledContent(1.0f) {
            AmText("Cuerpo de dos lineas para probar interlineado expandido")
        }
        val style = layoutOf("Cuerpo de dos lineas para probar interlineado expandido").layoutInput.style
        assertEquals(17.sp, style.fontSize)
        assertEquals(25.5.sp, style.lineHeight)
    }

    @Test
    fun large_body_text_does_not_overlap() {
        // Sonda del mecanismo: con el estilo ambiente, todo tamaño > 24 sp
        // solapaba. El texto fija interlineado proporcional a su tamaño.
        setScaledContent(1.0f) {
            Box(modifier = Modifier.width(328.dp)) {
                AmText(
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

    @Test
    fun section_header_accent_meets_text_contrast_in_both_themes() {
        val light = contrastRatio(LightAmColorScheme.accentText, LightAmColorScheme.background)
        assertTrue(light >= 4.5, "acento de sección en claro: $light < 4.5")
        val dark = contrastRatio(
            DarkAmColorScheme.accentText,
            DarkAmColorScheme.background,
        )
        assertTrue(dark >= 4.5, "acento de sección en oscuro: $dark < 4.5")
        // El encabezado por defecto usa color estándar (TextSecondary); el
        // acento (S06) solo es texto si cumple >= 4.5:1 (asTextOn).
        val standardLight = contrastRatio(
            LightAmColorScheme.textSecondary,
            LightAmColorScheme.background,
        )
        assertTrue(standardLight >= 4.5, "color estándar en claro: $standardLight < 4.5")
        val standardDark = contrastRatio(
            DarkAmColorScheme.textSecondary,
            DarkAmColorScheme.background,
        )
        assertTrue(standardDark >= 4.5, "color estándar en oscuro: $standardDark < 4.5")
    }

    @Test
    fun empty_state_glyph_is_decorative_and_never_announced() {
        compose.setContent {
            AmTheme {
                AmEmptyState(
                    title = "Aún no hay destinos",
                    subtitle = "Importa tu primer código QR",
                    icon = AmIcons.QrCode,
                )
            }
        }
        compose.waitForIdle()
        // El glifo es decorativo: NO existe un nodo con su descripción.
        compose.onNodeWithText("Aún no hay destinos").assertExists()
        compose.onNodeWithText("Importa tu primer código QR").assertExists()
        // Ningún nodo del árbol expone la imagen como contenido accesible
        // (clearAndSetSemantics): solo el título y el subtítulo.
        val all = mutableListOf<SemanticsNode>()
        fun walk(node: SemanticsNode) {
            all.add(node)
            node.children.forEach(::walk)
        }
        walk(compose.onRoot(useUnmergedTree = true).fetchSemanticsNode())
        assertTrue(
            all.none { it.config.getOrNull(androidx.compose.ui.semantics.SemanticsProperties.ContentDescription)?.isNotEmpty() == true },
            "el glifo del estado vacío no debe anunciarse al lector de pantalla",
        )
    }

    // --- Tarea A: metadato final (endMeta) de AmListRow ---

    @Test
    fun end_meta_keeps_the_title_line_and_right_alignment_when_there_is_room() {
        setScaledContent(1f) {
            AmListRow(
                title = "Mercado",
                subtitle = "Contexto de compras",
                endMeta = "14/11/2023",
                onClick = {},
            )
        }
        val title = compose.onNodeWithText("Mercado", useUnmergedTree = true)
            .getBoundsInRoot()
        val meta = compose.onNodeWithText("14/11/2023", useUnmergedTree = true)
            .getBoundsInRoot()
        val titleTop = title.top.value
        val titleBottom = title.bottom.value
        val titleRight = title.right.value
        val metaTop = meta.top.value
        val metaBottom = meta.bottom.value
        val metaRight = meta.right.value
        // Misma línea del título: los rangos verticales se solapan.
        assertTrue(
            metaTop < titleBottom - 1f && metaBottom > titleTop + 1f,
            "el metadato no comparte la línea del título: title=$title meta=$meta",
        )
        // Alineado a la derecha del contenedor (fila de 360 dp).
        val rowRight = compose.onRoot().getBoundsInRoot().right.value
        assertTrue(
            metaRight > titleRight && metaRight >= rowRight - 24f,
            "el metadato no está alineado a la derecha: right=$metaRight, fila=$rowRight",
        )
    }

    @Test
    @Config(sdk = [34], qualifiers = "w320dp-h640dp")
    fun end_meta_moves_to_its_own_line_on_a_narrow_row() {
        setScaledContent(1f) {
            AmListRow(
                title = "Mercado",
                subtitle = "Contexto de compras",
                endMeta = "14/11/2023",
                onClick = {},
            )
        }
        assertEndMetaBelowTitle()
    }

    @Test
    fun end_meta_moves_to_its_own_line_at_200_percent_font_scale() {
        setScaledContent(2f) {
            AmListRow(
                title = "Mercado",
                subtitle = "Contexto de compras",
                endMeta = "14/11/2023",
                onClick = {},
            )
        }
        assertEndMetaBelowTitle()
    }

    private fun assertEndMetaBelowTitle() {
        val title = compose.onNodeWithText("Mercado", useUnmergedTree = true)
            .getBoundsInRoot()
        val meta = compose.onNodeWithText("14/11/2023", useUnmergedTree = true)
            .getBoundsInRoot()
        assertTrue(
            meta.top.value >= title.bottom.value - 1f,
            "el metadato no bajó a su propia línea: title=$title meta=$meta",
        )
        val rowRight = compose.onRoot().getBoundsInRoot().right.value
        assertTrue(
            meta.right.value <= rowRight + 1f,
            "el metadato desborda el ancho: right=${meta.right.value}, fila=$rowRight",
        )
    }

    @Test
    fun end_meta_reads_as_one_accessible_element_without_duplicate_announcements() {
        compose.setContent {
            AmTheme {
                AmListRow(
                    title = "Mercado Central",
                    subtitle = "Compras semanales",
                    endMeta = "14/11/2023",
                    onClick = {},
                )
            }
        }
        compose.waitForIdle()
        // Un solo nodo por el texto del metadato: la fila fusiona sus
        // descendientes (sin anuncios repetidos).
        compose.onAllNodesWithText("14/11/2023").assertCountEquals(1)
        val texts = compose.onNodeWithText("Mercado Central")
            .fetchSemanticsNode()
            .config[SemanticsProperties.Text]
            .map { it.text }
            assertEquals(
                setOf("Mercado Central", "Compras semanales", "14/11/2023"),
                texts.toSet(),
            )
        assertEquals(3, texts.size)
    }
}
