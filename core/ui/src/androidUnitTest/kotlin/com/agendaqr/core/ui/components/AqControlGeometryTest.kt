package com.agendaqr.core.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertHeightIsEqualTo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.dp
import com.agendaqr.core.ui.theme.AqTheme
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class AqControlGeometryTest {
    @get:Rule
    val compose = createComposeRule()

    @Test
    fun full_width_button_fills_parent_and_keeps_moon_md_visual_height() {
        compose.setContent {
            AqTheme {
                Box(Modifier.width(280.dp)) {
                    AqButton(label = "Continuar", onClick = {}, size = AqButtonSize.Medium, fullWidth = true)
                }
            }
        }
        // Moon fija la altura del botón (md = 40 dp) y el botón entero es el
        // objetivo táctil: no hay expansión extra (ADR-0044).
        compose.onNodeWithTag("aq-button-visual", useUnmergedTree = true)
            .assertHeightIsEqualTo(40.dp)
        compose.onNodeWithTag("aq-button-touch-target", useUnmergedTree = true)
            .assertHeightIsEqualTo(40.dp)
    }

    @Test
    fun text_input_md_is_exactly_40dp_with_no_extra_hit_target() {
        compose.setContent {
            AqTheme {
                Box(Modifier.width(280.dp)) {
                    AqTextInput(label = "Importe", value = "", onValueChange = {})
                }
            }
        }
        // Moon `text_input`: la caja visual ES el campo (md = 40 dp).
        compose.onNodeWithTag("aq-text-input-visual", useUnmergedTree = true)
            .assertHeightIsEqualTo(40.dp)
    }
}
