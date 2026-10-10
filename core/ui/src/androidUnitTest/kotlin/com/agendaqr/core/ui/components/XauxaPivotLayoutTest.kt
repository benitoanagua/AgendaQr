package com.agendaqr.core.ui.components

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assert
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.unit.Density
import com.agendaqr.core.ui.theme.XauxaTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Contrato §4.4 (tipografía escalable): con fuente al 100/150/200 % el
 * pivot de secciones (S06) mantiene sus pestañas ALCANZABLES — el Row
 * desplaza en horizontal (selectableGroup) y ninguna etiqueta se recorta.
 * Antes el Row fijo dejaba la última pestaña fuera de la pantalla con
 * fuente grande (límite conocido retirado).
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34], qualifiers = "w320dp-h640dp")
class XauxaPivotLayoutTest {

    @get:Rule
    val compose = createComposeRule()

    private fun composePivotAt(fontScale: Float) {
        var selected by mutableStateOf(0)
        compose.setContent {
            XauxaTheme {
                CompositionLocalProvider(
                    LocalDensity provides Density(density = 1f, fontScale = fontScale),
                ) {
                    XauxaPivot(
                        sections = listOf("QR", "Actividades", "Comprobantes"),
                        selectedIndex = selected,
                        onSelect = { selected = it },
                    ) { }
                }
            }
        }
        compose.waitForIdle()
    }

    private fun lastTabIsReachableAndSelectable(scale: Float) {
        composePivotAt(scale)
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
    fun at_320dp_last_tab_is_reachable_at_font_scale_100() =
        lastTabIsReachableAndSelectable(1f)

    @Test
    fun at_320dp_last_tab_is_reachable_at_font_scale_150() =
        lastTabIsReachableAndSelectable(1.5f)

    @Test
    fun at_320dp_last_tab_is_reachable_at_font_scale_200() =
        lastTabIsReachableAndSelectable(2f)

    @Test
    @Config(sdk = [34], qualifiers = "w360dp-h800dp")
    fun at_360dp_last_tab_is_reachable_at_font_scale_200() {
        lastTabIsReachableAndSelectable(2f)
    }

    @Test
    @Config(sdk = [34], qualifiers = "w360dp-h800dp")
    fun at_360dp_tabs_stay_visible_at_font_scale_150() {
        composePivotAt(1.5f)
        compose.onNodeWithText("QR")
            .assert(SemanticsMatcher.expectValue(SemanticsProperties.Role, Role.Tab))
            .assertIsDisplayed()
    }
}
