package com.agendaqr.core.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.test.assertHeightIsAtLeast
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
        // La altura visual Moon md es 40 dp; el área táctil nunca baja de 48 dp (D-02).
        compose.onNodeWithTag("aq-button-visual", useUnmergedTree = true)
            .assertHeightIsEqualTo(40.dp)
        compose.onNodeWithTag("aq-button-touch-target", useUnmergedTree = true)
            .assertHeightIsAtLeast(48.dp)
    }

    @Test
    fun text_input_md_visual_is_40dp_and_hit_target_is_48dp() {
        compose.setContent {
            AqTheme {
                Box(Modifier.width(280.dp)) {
                    AqTextInput(label = "Importe", value = "", onValueChange = {})
                }
            }
        }
        compose.onNodeWithTag("aq-text-input-visual", useUnmergedTree = true)
            .assertHeightIsEqualTo(40.dp)
        compose.onNodeWithTag("aq-field-touch-target", useUnmergedTree = true)
            .assertHeightIsAtLeast(48.dp)
    }
}
