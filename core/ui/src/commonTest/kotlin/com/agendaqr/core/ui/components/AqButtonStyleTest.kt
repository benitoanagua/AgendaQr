package com.agendaqr.core.ui.components

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import com.agendaqr.core.ui.theme.DarkAqColorScheme
import com.agendaqr.core.ui.theme.LightAqColorScheme
import com.agendaqr.core.ui.theme.AqMetrics
import kotlin.test.Test
import kotlin.test.assertEquals

class AqButtonStyleTest {

    private fun argb(color: Color): String =
        color.toArgb().toUInt().toString(16).uppercase().padStart(8, '0')

    /**
     * F1 pin: el primario claro usa `bg-fill-brand` (`#303030`) y el oscuro
     * usa su contraparte publicada (`#FFFFFF`).
     */
    @Test
    fun primary_variant_pins_the_brand_fill_published_per_theme() {
        val light = resolveAqButtonColors(
            LightAqColorScheme, AqButtonVariant.Primary, enabled = true, pressed = false, focused = false,
        )
        assertEquals("FF303030", argb(light.container), "primario claro")
        assertEquals("FFFFFFFF", argb(light.content), "contenido primario claro")

        val dark = resolveAqButtonColors(
            DarkAqColorScheme, AqButtonVariant.Primary, enabled = true, pressed = false, focused = false,
        )
        assertEquals("FFFFFFFF", argb(dark.container), "primario oscuro")
        assertEquals("FF303030", argb(dark.content), "contenido primario oscuro")
    }

    @Test
    fun sizes_match_moon_visual_heights_and_preserve_touch_target() {
        assertEquals(48.dp, AqButtonSize.Lg.visualHeight)
        assertEquals(40.dp, AqButtonSize.Md.visualHeight)
        assertEquals(32.dp, AqButtonSize.Sm.visualHeight)
        assertEquals(24.dp, AqButtonSize.Xs.visualHeight)
        assertEquals(48.dp, AqMetrics.ControlMinSize)
    }

    @Test
    fun variants_resolve_colors_per_state_in_both_themes() {
        listOf(LightAqColorScheme, DarkAqColorScheme).forEach { scheme ->
            // Filled (Primary / Danger): relleno de marca.
            assertEquals(
                AqButtonColors(scheme.brandFill, scheme.onBrandFill),
                resolveAqButtonColors(scheme, AqButtonVariant.Primary, enabled = true, pressed = false, focused = false),
            )
            assertEquals(
                AqButtonColors(scheme.brandFillActive, scheme.onBrandFill),
                resolveAqButtonColors(scheme, AqButtonVariant.Primary, enabled = true, pressed = true, focused = false),
            )
            assertEquals(
                AqButtonColors(scheme.brandFillDisabled, scheme.onBrandFillDisabled),
                resolveAqButtonColors(scheme, AqButtonVariant.Primary, enabled = false, pressed = false, focused = false),
            )

            // Outlined: contorno sobre superficie transparente.
            assertEquals(
                AqButtonColors(Color.Transparent, scheme.textPrimary, scheme.border),
                resolveAqButtonColors(scheme, AqButtonVariant.Outline, enabled = true, pressed = false, focused = false),
            )
            assertEquals(
                AqButtonColors(scheme.surfaceActive, scheme.textPrimary, scheme.border),
                resolveAqButtonColors(scheme, AqButtonVariant.Outline, enabled = true, pressed = true, focused = false),
            )
            assertEquals(
                AqButtonColors(Color.Transparent, scheme.textPrimary, scheme.focus),
                resolveAqButtonColors(scheme, AqButtonVariant.Outline, enabled = true, pressed = false, focused = true),
            )
            assertEquals(
                AqButtonColors(Color.Transparent, scheme.onDisabled, scheme.border),
                resolveAqButtonColors(scheme, AqButtonVariant.Outline, enabled = false, pressed = false, focused = false),
            )

            // Text (Default / Light / Text): solo texto.
            assertEquals(
                AqButtonColors(Color.Transparent, scheme.link),
                resolveAqButtonColors(scheme, AqButtonVariant.Text, enabled = true, pressed = false, focused = false),
            )
            assertEquals(
                AqButtonColors(scheme.surfaceActive, scheme.emphasisFill),
                resolveAqButtonColors(scheme, AqButtonVariant.Text, enabled = true, pressed = true, focused = false),
            )
            assertEquals(
                AqButtonColors(Color.Transparent, scheme.onDisabled),
                resolveAqButtonColors(scheme, AqButtonVariant.Text, enabled = false, pressed = false, focused = false),
            )
        }
    }
}
