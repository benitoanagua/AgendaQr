package com.agendaqr.core.ui.components

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.getBoundsInRoot
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import com.agendaqr.core.ui.theme.AqTheme
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Contrato de [AqExpansionPanel]: panel expandible para "Más detalles"
 * (S07 registro de operación).
 *
 * - El contenido está oculto cuando expanded = false y visible cuando true.
 * - El título siempre está presente.
 * - La fila de título tiene el objetivo táctil de 48 dp.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], qualifiers = "w360dp-h760dp")
class AqExpansionPanelTest {

    @get:Rule
    val compose = createComposeRule()

    private fun composeContent(expanded: Boolean) {
        compose.setContent {
            AqTheme(darkTheme = false) {
                AqExpansionPanel(
                    title = "Más detalles",
                    expanded = expanded,
                    onToggle = {},
                    leading = {},
                    content = {
                        androidx.compose.foundation.text.BasicTextField(
                            value = "contenido expandido",
                            onValueChange = {},
                            modifier = Modifier.testTag("expansion-content"),
                        )
                    },
                )
            }
        }
        compose.waitForIdle()
    }

    @Test
    fun content_visible_when_expanded() {
        composeContent(expanded = true)
        compose.onNodeWithTag("expansion-content").getBoundsInRoot()
    }

    @Test
    fun content_hidden_when_collapsed() {
        composeContent(expanded = false)
        assertFailsWith<Throwable> {
            compose.onNodeWithTag("expansion-content").getBoundsInRoot()
        }
    }

    @Test
    fun title_always_present() {
        composeContent(expanded = false)
        compose.onNodeWithText("Más detalles")
    }

    @Test
    fun title_row_has_min_touch_target() {
        composeContent(expanded = false)
        val bounds = compose.onNodeWithText("Más detalles").getBoundsInRoot()
        val height = bounds.bottom - bounds.top
        assertTrue(
            height.value >= 48f,
            "fila de título menor a 48 dp: $height",
        )
    }

    @Test
    fun header_tag_present_and_content_tag_only_when_expanded() {
        composeContent(expanded = true)
        compose.onNodeWithTag("aq-expansion-header").getBoundsInRoot()
        compose.onNodeWithTag("aq-expansion-content").getBoundsInRoot()
        composeContent(expanded = false)
        compose.onNodeWithTag("aq-expansion-header").getBoundsInRoot()
        assertFailsWith<Throwable> {
            compose.onNodeWithTag("aq-expansion-content").getBoundsInRoot()
        }
    }
}
