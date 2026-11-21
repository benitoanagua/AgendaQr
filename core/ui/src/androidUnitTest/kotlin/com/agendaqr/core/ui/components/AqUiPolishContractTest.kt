package com.agendaqr.core.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
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
import com.agendaqr.core.ui.theme.DarkAqColorScheme
import com.agendaqr.core.ui.theme.LightAqColorScheme
import com.agendaqr.core.ui.theme.AqTheme
import com.agendaqr.core.ui.theme.AqType
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
class AqUiPolishContractTest {

    @get:Rule
    val compose = createComposeRule()

    private fun setScaledContent(fontScale: Float, content: @androidx.compose.runtime.Composable () -> Unit) {
        compose.setContent {
            CompositionLocalProvider(LocalDensity provides Density(1f, fontScale)) {
                AqTheme(darkTheme = false) {
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
                AqHeading(
                    text = "Titular de comprobante pendiente de revision larga",
                    size = AqType.Headline,
                )
            }
        }
        val layout = layoutOf("Titular de comprobante pendiente de revision larga")
        val style = layout.layoutInput.style
        // El rol heading-2xl publica 32/40 sp. El encabezado fija ese
        // interlineado canónico (> 1.1x) en lugar del ambiente.
        val ratio = style.lineHeight.value / style.fontSize.value
        assertTrue(ratio >= 1.1f, "heading hereda interlineado ambiente (ratio=$ratio)")
        assertLinesDoNotOverlap("heading", layout)
    }

    @Test
    fun body_text_keeps_canonical_lineheight() {
        // body-sm publica 14/20 sp en light-mobile (instantánea :522-523).
        setScaledContent(1.0f) {
            AqText("Cuerpo de dos lineas para probar interlineado expandido")
        }
        val style = layoutOf("Cuerpo de dos lineas para probar interlineado expandido").layoutInput.style
        assertEquals(14.sp, style.fontSize)
        assertEquals(20.sp, style.lineHeight)
    }

    @Test
    fun large_body_text_does_not_overlap() {
        // Sonda del mecanismo: con el estilo ambiente, todo tamaño > 24 sp
        // solapaba. El texto fija interlineado proporcional a su tamaño.
        setScaledContent(1.0f) {
            Box(modifier = Modifier.width(328.dp)) {
                AqText(
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
        val light = contrastRatio(LightAqColorScheme.emphasisText, LightAqColorScheme.background)
        assertTrue(light >= 4.5, "acento de sección en claro: $light < 4.5")
        val dark = contrastRatio(
            DarkAqColorScheme.emphasisText,
            DarkAqColorScheme.background,
        )
        assertTrue(dark >= 4.5, "acento de sección en oscuro: $dark < 4.5")
        // El encabezado por defecto usa color estándar (TextSecondary); el
        // acento (S06) solo es texto si cumple >= 4.5:1 (asTextOn).
        val standardLight = contrastRatio(
            LightAqColorScheme.textSecondary,
            LightAqColorScheme.background,
        )
        assertTrue(standardLight >= 4.5, "color estándar en claro: $standardLight < 4.5")
        val standardDark = contrastRatio(
            DarkAqColorScheme.textSecondary,
            DarkAqColorScheme.background,
        )
        assertTrue(standardDark >= 4.5, "color estándar en oscuro: $standardDark < 4.5")
    }

    @Test
    fun empty_state_glyph_is_decorative_and_never_announced() {
        compose.setContent {
            AqTheme {
                AqEmptyState(
                    title = "Aún no hay destinos",
                    subtitle = "Importa tu primer código QR",
                    icon = AqIcons.QrCode,
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

    // --- Tarea A: metadato final (endMeta) de AqListRow ---

    @Test
    fun list_row_description_uses_the_body_type_scale() {
        setScaledContent(1f) {
            AqListRow(
                title = "Mercado",
                subtitle = "Contexto de compras",
            )
        }
        val style = layoutOf("Contexto de compras").layoutInput.style
        assertEquals(14.sp, style.fontSize)
        assertEquals(20.sp, style.lineHeight)
    }

    @Test
    fun end_meta_keeps_the_title_line_and_right_alignment_when_there_is_room() {
        setScaledContent(1f) {
            AqListRow(
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
    fun end_meta_reads_as_one_accessible_element_without_duplicate_announcements() {
        compose.setContent {
            AqTheme {
                AqListRow(
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

    // --- Contrato de densidad: campo, banner, tarjeta y fila ---

    @Test
    fun text_field_label_aligns_with_the_box_left_edge_and_keeps_a_4dp_gap() {
        setScaledContent(1f) {
            AqTextField(label = "Comercio", value = "", onValueChange = {})
        }
        val label = compose.onNodeWithText("Comercio", useUnmergedTree = true).getBoundsInRoot()
        val field = compose.onNodeWithContentDescription("Comercio", useUnmergedTree = true)
            .getBoundsInRoot()
        val root = compose.onRoot().getBoundsInRoot()
        assertEquals(
            0f,
            label.left.value - root.left.value,
            0.5f,
            "la etiqueta no arranca en el borde izquierdo del contenedor",
        )
        assertEquals(
            12f,
            field.left.value - label.left.value,
            0.5f,
            "el texto no tiene 12 dp de relleno horizontal interno",
        )
        assertEquals(
            12f,
            root.right.value - field.right.value,
            0.5f,
            "el campo no alcanza el borde derecho del contenedor",
        )
        assertEquals(
            4f,
            field.top.value - label.bottom.value,
            0.5f,
            "la separación etiqueta/caja no es 4 dp",
        )
        assertEquals(
            48f,
            field.bottom.value - field.top.value,
            0.5f,
            "se perdió el objetivo táctil de 48 dp",
        )
    }

    @Test
    fun text_field_error_block_aligns_with_the_label() {
        setScaledContent(1f) {
            AqTextField(
                label = "Comercio",
                value = "",
                onValueChange = {},
                isError = true,
                errorMessage = "Falta el comercio",
            )
        }
        val label = compose.onNodeWithText("Comercio", useUnmergedTree = true).getBoundsInRoot()
        val support = compose.onNodeWithTag("aq-field-support", useUnmergedTree = true)
            .getBoundsInRoot()
        val field = compose.onNodeWithContentDescription("Comercio", useUnmergedTree = true)
            .getBoundsInRoot()
        assertEquals(label.left.value, support.left.value, 0.5f, "el error no alinea con la etiqueta")
        assertEquals(
            4f,
            support.top.value - field.bottom.value,
            0.5f,
            "la separación caja/error no es 4 dp",
        )
    }

    @Test
    fun text_field_helper_text_aligns_with_the_label() {
        setScaledContent(1f) {
            AqTextField(
                label = "Nota",
                value = "",
                onValueChange = {},
                helperMessage = "Opcional",
            )
        }
        val label = compose.onNodeWithText("Nota", useUnmergedTree = true).getBoundsInRoot()
        val helper = compose.onNodeWithText("Opcional", useUnmergedTree = true).getBoundsInRoot()
        assertEquals(label.left.value, helper.left.value, 0.5f, "la ayuda no alinea con la etiqueta")
    }

    @Test
    fun status_banner_uses_16dp_padding_and_a_12dp_action_gap() {
        compose.setContent {
            AqTheme(darkTheme = false) {
                AqStatusBanner(
                    message = "No pudimos guardar.",
                    action = AqBannerAction("Reintentar", {}),
                )
            }
        }
        compose.waitForIdle()
        val banner = compose.onNodeWithTag("am-status-banner").getBoundsInRoot()
        val message = compose.onNodeWithText("No pudimos guardar.", useUnmergedTree = true)
            .getBoundsInRoot()
        val actions = compose.onNodeWithTag("aq-banner-actions", useUnmergedTree = true)
            .getBoundsInRoot()
        assertEquals(
            16f,
            message.left.value - banner.left.value,
            1f,
            "el contenido no tiene 16 dp de relleno lateral",
        )
        assertEquals(
            16f,
            message.top.value - banner.top.value,
            1f,
            "el contenido no arranca a 16 dp del borde superior",
        )
        assertEquals(
            12f,
            actions.top.value - message.bottom.value,
            1f,
            "la acción no está a 12 dp del contenido",
        )
        assertEquals(
            16f,
            banner.right.value - actions.right.value,
            1f,
            "la acción no tiene 16 dp de respiro hasta el borde",
        )
    }

    @Test
    fun card_applies_16dp_internal_padding() {
        compose.setContent {
            AqTheme {
                AqCard { AqText("Contenido de tarjeta") }
            }
        }
        compose.waitForIdle()
        val card = compose.onNodeWithTag("aq-card").getBoundsInRoot()
        val content = compose.onNodeWithText("Contenido de tarjeta", useUnmergedTree = true)
            .getBoundsInRoot()
        assertEquals(16f, content.left.value - card.left.value, 0.5f, "padding izquierdo")
        assertEquals(16f, content.top.value - card.top.value, 0.5f, "padding superior")
    }

    /**
     * Botón secundario de la referencia: relleno `bg-fill` + borde inset de
     * 1 px (`shadow-button` de `light-mobile`). Sin el borde, el botón
     * desaparece sobre el fondo de página.
     */
    @Test
    fun default_button_uses_surface_fill_and_a_hairline_border() {
        val idle = resolveAqButtonColors(
            LightAqColorScheme,
            variant = AqButtonVariant.Default,
            enabled = true,
            pressed = false,
            focused = false,
        )
        assertEquals(Color.Transparent, idle.container, "relleno del secundario")
        assertEquals(LightAqColorScheme.border, idle.border, "borde fino del secundario")

        val pressed = resolveAqButtonColors(
            LightAqColorScheme,
            variant = AqButtonVariant.Default,
            enabled = true,
            pressed = true,
            focused = false,
        )
        assertEquals(
            LightAqColorScheme.surfaceActive,
            pressed.container,
            "relleno activo del secundario",
        )
        assertEquals(LightAqColorScheme.border, pressed.border, "el borde se conserva al pulsar")
    }

    @Test
    fun choice_options_do_not_add_external_vertical_padding() {
        // El grupo es dueño de la separación (`spacedBy`); cada opción
        // conserva el objetivo táctil de 48 dp sin sumar 4+4 dp de margen
        // externo (que daría 56 dp por fila).
        compose.setContent {
            AqTheme {
                AqChoiceGroup(
                    label = "Moneda",
                    options = listOf(
                        AqChoiceOption("bob", "Boliviano"),
                        AqChoiceOption("usd", "Dólar"),
                    ),
                    selectedId = "bob",
                    onSelected = {},
                )
            }
        }
        compose.waitForIdle()
        listOf("Boliviano", "Dólar").forEach { label ->
            val row = compose.onNodeWithText(label).getBoundsInRoot()
            assertEquals(
                48f,
                row.bottom.value - row.top.value,
                1f,
                "'$label' añade margen vertical externo sobre el objetivo de 48 dp",
            )
        }
    }

    @Test
    fun list_row_uses_12dp_vertical_density_and_16dp_leading_inset() {
        setScaledContent(1f) {
            AqListRow(
                title = "Mercado",
                subtitle = "Compras semanales",
                onClick = {},
            )
        }
        val row = compose.onNodeWithText("Mercado").getBoundsInRoot()
        val title = compose.onNodeWithText("Mercado", useUnmergedTree = true).getBoundsInRoot()
        assertEquals(12f, title.top.value - row.top.value, 1f, "densidad vertical")
        assertEquals(16f, title.left.value - row.left.value, 1f, "inset izquierdo")
    }
}
