package com.agendaqr.core.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.agendaqr.core.ui.theme.DarkAqColorScheme
import com.agendaqr.core.ui.theme.LightAqColorScheme
import com.agendaqr.core.ui.theme.AqMetrics
import kotlin.test.Test
import kotlin.test.assertEquals

class AqButtonStyleTest {

    @Test
    fun sizes_match_polaris_visual_heights_and_preserve_touch_target() {
        assertEquals(48.dp, AqButtonSize.Large.visualHeight)
        assertEquals(40.dp, AqButtonSize.Medium.visualHeight)
        assertEquals(32.dp, AqButtonSize.Small.visualHeight)
        assertEquals(24.dp, AqButtonSize.ExtraSmall.visualHeight)
        assertEquals(48.dp, AqMetrics.ControlMinSize)
    }

    @Test
    fun variants_resolve_normal_pressed_and_disabled_colors_in_both_themes() {
        listOf(LightAqColorScheme, DarkAqColorScheme).forEach { scheme ->
            assertEquals(
                AqButtonColors(scheme.brandFill, scheme.onBrandFill),
                resolveAqButtonColors(AqButtonVariant.Primary, scheme, enabled = true, pressed = false),
            )
            assertEquals(
                AqButtonColors(scheme.brandFillActive, scheme.onBrandFill),
                resolveAqButtonColors(AqButtonVariant.Primary, scheme, enabled = true, pressed = true),
            )
            assertEquals(
                AqButtonColors(scheme.brandFillDisabled, scheme.onBrandFillDisabled),
                resolveAqButtonColors(AqButtonVariant.Primary, scheme, enabled = false, pressed = false),
            )
            assertEquals(
                AqButtonColors(scheme.surfaceContainerHigh, scheme.textPrimary),
                resolveAqButtonColors(AqButtonVariant.Default, scheme, enabled = true, pressed = false),
            )
            assertEquals(
                AqButtonColors(scheme.componentActive, scheme.textPrimary),
                resolveAqButtonColors(AqButtonVariant.Default, scheme, enabled = true, pressed = true),
            )
            assertEquals(
                AqButtonColors(scheme.dangerFill, scheme.onDanger),
                resolveAqButtonColors(AqButtonVariant.Danger, scheme, enabled = true, pressed = false),
            )
            assertEquals(
                AqButtonColors(scheme.dangerActive, scheme.onDanger),
                resolveAqButtonColors(AqButtonVariant.Danger, scheme, enabled = true, pressed = true),
            )
            assertEquals(
                AqButtonColors(scheme.dangerDisabled, scheme.textPrimary),
                resolveAqButtonColors(AqButtonVariant.Danger, scheme, enabled = false, pressed = false),
            )
            assertEquals(
                AqButtonColors(scheme.emphasisSurface, scheme.emphasisText),
                resolveAqButtonColors(AqButtonVariant.Light, scheme, enabled = true, pressed = false),
            )
            assertEquals(
                AqButtonColors(scheme.emphasisSurfaceHover, scheme.emphasisText),
                resolveAqButtonColors(AqButtonVariant.Light, scheme, enabled = true, pressed = true),
            )
            assertEquals(
                AqButtonColors(scheme.disabled, scheme.textTertiary),
                resolveAqButtonColors(AqButtonVariant.Light, scheme, enabled = false, pressed = false),
            )
            assertEquals(
                AqButtonColors(Color.Transparent, scheme.textPrimary, scheme.functionalBorder),
                resolveAqButtonColors(AqButtonVariant.Outline, scheme, enabled = true, pressed = false),
            )
            assertEquals(
                AqButtonColors(scheme.surfaceActive, scheme.textPrimary, scheme.functionalBorder),
                resolveAqButtonColors(AqButtonVariant.Outline, scheme, enabled = true, pressed = true),
            )
            assertEquals(
                AqButtonColors(Color.Transparent, scheme.link),
                resolveAqButtonColors(AqButtonVariant.Text, scheme, enabled = true, pressed = false),
            )
            assertEquals(
                AqButtonColors(scheme.surfaceActive, scheme.link),
                resolveAqButtonColors(AqButtonVariant.Text, scheme, enabled = true, pressed = true),
            )
            assertEquals(
                AqButtonColors(Color.Transparent, scheme.textTertiary),
                resolveAqButtonColors(AqButtonVariant.Text, scheme, enabled = false, pressed = false),
            )
        }
    }
}
