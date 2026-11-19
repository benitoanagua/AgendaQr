package com.agendaqr.core.ui.components

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.unit.Density
import com.agendaqr.core.ui.theme.AqTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Contrato de tipografía escalable: con fuente al 100/150/200 % el pivot
 * de secciones (S06) mantiene sus pestañas ALCANZABLES — el Row desplaza
 * en horizontal (selectableGroup) y ninguna etiqueta se recorta.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], qualifiers = "w360dp-h800dp")
class AqTabsLayoutTest {

    @get:Rule
    val compose = createComposeRule()

    private fun composePivotAt(initialFontScale: Float): (Float) -> Unit {
        var selected by mutableStateOf(0)
        var fontScale by mutableStateOf(initialFontScale)
        compose.setContent {
            AqTheme {
                CompositionLocalProvider(
                    LocalDensity provides Density(density = 1f, fontScale = fontScale),
                ) {
                    AqTabs(
                        tabs = listOf(AqTab("QR"), AqTab("Actividades"), AqTab("Comprobantes")),
                        selectedIndex = selected,
                        onSelect = { selected = it },
                    ) { }
                }
            }
        }
        compose.waitForIdle()
        return { scale ->
            compose.runOnIdle {
                fontScale = scale
                selected = 0
            }
            compose.waitForIdle()
        }
    }

    private fun lastTabIsReachableAndSelectable(setFontScale: (Float) -> Unit, scale: Float) {
        setFontScale(scale)
        // La última pestaña se alcanza aunque desborde el ancho: el pivot
        // desplaza en horizontal (performScrollTo falla sin scrollable).
        compose.onNodeWithText("Comprobantes")
            .performScrollTo()
            .assertIsDisplayed()
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Tab))
            .performClick()
        compose.onNodeWithText("Comprobantes")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Selected, true))
    }

    @Test
    @Config(sdk = [34], qualifiers = "w360dp-h800dp")
    fun at_360dp_tabs_stay_visible_at_font_scale_150() {
        composePivotAt(1.5f)
        compose.onNodeWithText("QR")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Tab))
            .assertIsDisplayed()
    }

    @Test
    fun last_tab_remains_reachable_and_selectable_at_font_scales_up_to_200() {
        val setFontScale = composePivotAt(1f)
        listOf(1f, 1.5f, 2f).forEach { scale ->
            lastTabIsReachableAndSelectable(setFontScale, scale)
        }
    }
}
