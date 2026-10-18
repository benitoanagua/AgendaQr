package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsNode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.sp
import com.agendaqr.core.ui.theme.DarkAmColorScheme
import com.agendaqr.core.ui.theme.LightAmColorScheme
import com.agendaqr.core.ui.theme.AmTheme
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
 * Pulido UI V1.1 en pantallas.
 *
 * "Desde otra app" (S02) es encabezado de seccion (M6, 14 sp en acento),
 * con el mismo estilo que "Recientes" (S01).
 * El placeholder del buscador de S01 vive en una sola linea a 360 dp.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], qualifiers = "w360dp-h800dp")
@GraphicsMode(GraphicsMode.Mode.NATIVE)
class S02UiPolishTest {

    @get:Rule
    val compose = createComposeRule()

    private fun headingMatcher() =
        SemanticsMatcher.keyIsDefined(SemanticsProperties.Heading)

    private fun layoutOf(text: String): TextLayoutResult {
        // Igual que en core:ui — dos lecturas estables del nodo sin fusionar
        // (el fusionado expone el layout del campo vacio en placeholders).
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
        return last ?: fail("sin TextLayoutResult para '$text'")
    }

    private fun s01State() = DestinationsUiState(
        destinations = listOf(
            com.agendaqr.destinations.domain.Destination(
                id = "d-1",
                name = "Carniceria Don Bife",
                qr = com.agendaqr.destinations.domain.QrAsset(encoded = "eA=="),
                createdAt = 1,
                updatedAt = 1,
            ),
        ),
        isLoading = false,
    )

    /** Mezcla: un favorito en el carrusel y un destino en la lista inferior. */
    private fun s01StateWithFavorite() = DestinationsUiState(
        destinations = listOf(
            com.agendaqr.destinations.domain.Destination(
                id = "d-fav",
                name = "Mercado",
                qr = com.agendaqr.destinations.domain.QrAsset(encoded = "eA=="),
                favorite = true,
                createdAt = 1,
                updatedAt = 1,
            ),
        ) + s01State().destinations,
        isLoading = false,
    )

    @Test
    fun s01_recientes_is_a_section_header_in_standard_color() {
        compose.setContent {
            AmTheme(darkTheme = false) {
                DestinationsScreen(state = s01State(), onAction = {})
            }
        }
        compose.waitForIdle()
        // Encabezado de sección (H2 del Anexo A): 16 sp en
        // texto secundario — semántica heading.
        compose.onNodeWithText(AppStrings.TodosLosDestinos).assert(headingMatcher())
        val style = layoutOf(AppStrings.TodosLosDestinos).layoutInput.style
        assertEquals(16.sp, style.fontSize)
        assertEquals(LightAmColorScheme.textSecondary, style.color)
    }

    @Test
    fun s01_otros_destinos_is_the_section_header_when_the_carousel_shows_favorites() {
        compose.setContent {
            AmTheme(darkTheme = false) {
                DestinationsScreen(state = s01StateWithFavorite(), onAction = {})
            }
        }
        compose.waitForIdle()
        // Con carrusel de favoritos, la lista inferior encabeza "Otros
        // destinos" con el MISMO estilo de H2 (16 sp, secundario).
        compose.onNodeWithText(AppStrings.OtrosDestinos).assert(headingMatcher())
        compose.onNodeWithText(AppStrings.TodosLosDestinos).assertDoesNotExist()
        val style = layoutOf(AppStrings.OtrosDestinos).layoutInput.style
        assertEquals(16.sp, style.fontSize)
        assertEquals(LightAmColorScheme.textSecondary, style.color)
    }

    @Test
    fun s02_share_hint_is_plain_secondary_text_not_a_heading() {
        compose.setContent {
            AmTheme(darkTheme = false) {
                AddDestinationScreen(onImport = {}, onBack = {})
            }
        }
        compose.waitForIdle()
        // "Desde otra app" es una ayuda breve de texto secundario,
        // no una sección permanente destacada.
        compose.onNodeWithText(AppStrings.shareFromAnotherAppHint)
            .assert(SemanticsMatcher.keyNotDefined(SemanticsProperties.Heading))
        val style = layoutOf(AppStrings.shareFromAnotherAppHint).layoutInput.style
        assertEquals(LightAmColorScheme.textSecondary, style.color)
    }

    @Test
    fun s01_section_header_uses_readable_standard_color_in_dark_theme() {
        compose.setContent {
            AmTheme(darkTheme = true) {
                DestinationsScreen(state = s01State(), onAction = {})
            }
        }
        compose.waitForIdle()
        val style = layoutOf(AppStrings.TodosLosDestinos).layoutInput.style
        // H2 del Anexo A: 16 sp en texto secundario
        // (legible en ambos temas).
        assertEquals(16.sp, style.fontSize)
        assertEquals(DarkAmColorScheme.textSecondary, style.color)
    }

    @Test
    fun s01_otros_destinos_header_uses_readable_standard_color_in_dark_theme() {
        compose.setContent {
            AmTheme(darkTheme = true) {
                DestinationsScreen(state = s01StateWithFavorite(), onAction = {})
            }
        }
        compose.waitForIdle()
        val style = layoutOf(AppStrings.OtrosDestinos).layoutInput.style
        assertEquals(16.sp, style.fontSize)
        assertEquals(DarkAmColorScheme.textSecondary, style.color)
    }

    // --- D5: los 3 controles de S02 muestran icono + etiqueta (M10/M12) ---

    private fun unmergedNodes(): List<SemanticsNode> {
        val all = mutableListOf<SemanticsNode>()
        fun walk(node: SemanticsNode) {
            all.add(node)
            node.children.forEach(::walk)
        }
        walk(compose.onRoot(useUnmergedTree = true).fetchSemanticsNode())
        return all
    }

    /**
     * Icono decorativo junto a la etiqueta: bloque de 24 dp (IconSize) a la
     * izquierda del texto, en su misma fila, sin rol/texto/descripcion
     * propios (la etiqueta visible ya es el nombre accesible, §11/M12).
     */
    private fun iconForLabel(all: List<SemanticsNode>, label: String, pxPerDp: Float): SemanticsNode? {
        val text = all.singleOrNull {
            it.config.getOrNull(SemanticsProperties.Text)?.any { t -> t.text == label } == true
        } ?: return null
        val textLeft = text.positionInRoot.x
        val textCenterY = text.positionInRoot.y + text.size.height / 2f
        // Compactación (referencia MCP): los controles compactos usan el
        // glifo de 18 dp (IconSizeCompact); los primarios conservan 24.
        return all.firstOrNull { node ->
            node != text &&
                listOf(18f, 20f, 24f).any { expected ->
                    abs(node.size.width - expected * pxPerDp) <= 1f &&
                        abs(node.size.height - expected * pxPerDp) <= 1f
                } &&
                node.size.width > 0 &&
                (textLeft - (node.positionInRoot.x + node.size.width)) in 0f..(8f * pxPerDp) &&
                abs(node.positionInRoot.y + node.size.height / 2f - textCenterY) <= 8f * pxPerDp &&
                node.config.getOrNull(SemanticsProperties.Role) == null &&
                node.config.getOrNull(SemanticsProperties.Text) == null &&
                node.config.getOrNull(SemanticsProperties.ContentDescription) == null
        }
    }

    @Test
    fun d5_s02_controls_show_icon_with_label() {
        compose.setContent {
            AmTheme(darkTheme = false) {
                AddDestinationScreen(onImport = {}, onBack = {})
            }
        }
        compose.waitForIdle()
        val all = unmergedNodes()
        val root = compose.onRoot(useUnmergedTree = true).fetchSemanticsNode()
        val pxPerDp = root.size.width / 360f
        // V1.2: UNA acción de galería con selección múltiple (sin "Galería
        // (varios)") y la cámara, cada una con glifo decorativo + etiqueta.
        listOf(AppStrings.ElegirDeLaGaleria, AppStrings.Camara).forEach { label ->
            val icon = iconForLabel(all, label, pxPerDp)
            assertNotNull(icon, "S02: el control '$label' no muestra icono junto a la etiqueta")
            assertNull(icon.config.getOrNull(SemanticsProperties.ContentDescription))
        }
        // La pareja histórica Galería/Galería (varios) ya no existe.
        assertTrue(
            compose.onAllNodesWithText("Galería (varios)").fetchSemanticsNodes().isEmpty(),
            "S02 ya no muestra 'Galería (varios)'",
        )
    }
}
