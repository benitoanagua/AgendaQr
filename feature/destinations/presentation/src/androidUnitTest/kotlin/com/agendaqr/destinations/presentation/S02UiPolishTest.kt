package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertHeightIsAtLeast
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertCountEquals
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.hasClickAction
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.getOrNull
import androidx.compose.ui.unit.dp
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
        // Cabecera de grupo: 14 sp en texto secundario — semántica heading.
        compose.onNodeWithText(AppStrings.TodosLosDestinos).assert(headingMatcher())
        val style = layoutOf(AppStrings.TodosLosDestinos).layoutInput.style
        assertEquals(14.sp, style.fontSize)
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
        // destinos" con el mismo estilo (14 sp, secundario).
        compose.onNodeWithText(AppStrings.OtrosDestinos).assert(headingMatcher())
        compose.onNodeWithText(AppStrings.TodosLosDestinos).assertDoesNotExist()
        val style = layoutOf(AppStrings.OtrosDestinos).layoutInput.style
        assertEquals(14.sp, style.fontSize)
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
        // La cabecera conserva 14 sp en ambos temas.
        assertEquals(14.sp, style.fontSize)
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
        assertEquals(14.sp, style.fontSize)
        assertEquals(DarkAmColorScheme.textSecondary, style.color)
    }

    // --- D5: los 3 controles de S02 muestran icono + etiqueta (M10/M12) ---

    @Test
    fun s02_import_sources_are_labelled_interactive_cells() {
        compose.setContent {
            AmTheme(darkTheme = false) {
                AddDestinationScreen(onImport = {}, onBack = {})
            }
        }
        compose.waitForIdle()
        listOf(
            AppStrings.ElegirDeLaGaleria,
            AppStrings.ElegirArchivoPdf,
            AppStrings.Camara,
        ).forEach { label ->
            compose.onNodeWithText(label)
                .assertIsDisplayed()
                .assertHeightIsAtLeast(48.dp)
                .assert(hasClickAction())
        }
        compose.onAllNodesWithText("Galería (varios)", useUnmergedTree = true).assertCountEquals(0)
    }
}
