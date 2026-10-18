package com.agendaqr.core.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.agendaqr.core.ui.theme.DarkAmColorScheme
import com.agendaqr.core.ui.theme.LightAmColorScheme
import com.agendaqr.core.ui.theme.AmMetrics
import kotlin.test.Test
import kotlin.test.assertEquals

class AmButtonStyleTest {

    @Test
    fun sizes_match_tdesign_visual_heights_and_preserve_touch_target() {
        assertEquals(48.dp, AmButtonSize.Large.visualHeight)
        assertEquals(40.dp, AmButtonSize.Medium.visualHeight)
        assertEquals(32.dp, AmButtonSize.Small.visualHeight)
        assertEquals(28.dp, AmButtonSize.ExtraSmall.visualHeight)
        assertEquals(48.dp, AmMetrics.ControlMinSize)
    }

    @Test
    fun variants_resolve_normal_pressed_and_disabled_colors_in_both_themes() {
        listOf(LightAmColorScheme, DarkAmColorScheme).forEach { scheme ->
            assertEquals(
                AmButtonColors(scheme.accent, scheme.onAccent),
                resolveAmButtonColors(AmButtonVariant.Primary, scheme, enabled = true, pressed = false),
            )
            assertEquals(
                AmButtonColors(scheme.accentActive, scheme.accentActiveContent),
                resolveAmButtonColors(AmButtonVariant.Primary, scheme, enabled = true, pressed = true),
            )
            assertEquals(
                AmButtonColors(scheme.accentDisabled, scheme.textPrimary),
                resolveAmButtonColors(AmButtonVariant.Primary, scheme, enabled = false, pressed = false),
            )
            assertEquals(
                AmButtonColors(scheme.surfaceContainerHigh, scheme.textPrimary),
                resolveAmButtonColors(AmButtonVariant.Default, scheme, enabled = true, pressed = false),
            )
            assertEquals(
                AmButtonColors(scheme.componentActive, scheme.textPrimary),
                resolveAmButtonColors(AmButtonVariant.Default, scheme, enabled = true, pressed = true),
            )
            assertEquals(
                AmButtonColors(scheme.dangerFill, scheme.onDanger),
                resolveAmButtonColors(AmButtonVariant.Danger, scheme, enabled = true, pressed = false),
            )
            assertEquals(
                AmButtonColors(scheme.dangerActive, scheme.onDanger),
                resolveAmButtonColors(AmButtonVariant.Danger, scheme, enabled = true, pressed = true),
            )
            assertEquals(
                AmButtonColors(scheme.dangerDisabled, scheme.textPrimary),
                resolveAmButtonColors(AmButtonVariant.Danger, scheme, enabled = false, pressed = false),
            )
            assertEquals(
                AmButtonColors(scheme.accentContainer, scheme.accentText),
                resolveAmButtonColors(AmButtonVariant.Light, scheme, enabled = true, pressed = false),
            )
            assertEquals(
                AmButtonColors(scheme.accentLightActive, scheme.accentText),
                resolveAmButtonColors(AmButtonVariant.Light, scheme, enabled = true, pressed = true),
            )
            assertEquals(
                AmButtonColors(scheme.disabled, scheme.textTertiary),
                resolveAmButtonColors(AmButtonVariant.Light, scheme, enabled = false, pressed = false),
            )
            assertEquals(
                AmButtonColors(Color.Transparent, scheme.textPrimary, scheme.functionalBorder),
                resolveAmButtonColors(AmButtonVariant.Outline, scheme, enabled = true, pressed = false),
            )
            assertEquals(
                AmButtonColors(scheme.surfaceActive, scheme.textPrimary, scheme.functionalBorder),
                resolveAmButtonColors(AmButtonVariant.Outline, scheme, enabled = true, pressed = true),
            )
            assertEquals(
                AmButtonColors(Color.Transparent, scheme.accentText),
                resolveAmButtonColors(AmButtonVariant.Text, scheme, enabled = true, pressed = false),
            )
            assertEquals(
                AmButtonColors(scheme.surfaceActive, scheme.accentText),
                resolveAmButtonColors(AmButtonVariant.Text, scheme, enabled = true, pressed = true),
            )
            assertEquals(
                AmButtonColors(Color.Transparent, scheme.textTertiary),
                resolveAmButtonColors(AmButtonVariant.Text, scheme, enabled = false, pressed = false),
            )
        }
    }
}
