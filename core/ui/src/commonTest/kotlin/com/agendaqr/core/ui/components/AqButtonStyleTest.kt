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
     * usa su contraparte publicada (`#FFFFFF`). Un `#005BDB` aquí delata que
     * algún consumidor volvió a resolver el primario desde `emphasis`.
     */
    @Test
    fun primary_variant_pins_the_brand_fill_published_per_theme() {
        val light = resolveAqButtonColors(
            AqButtonVariant.Primary, LightAqColorScheme, enabled = true, pressed = false,
        )
        assertEquals("FF5C33CF", argb(light.container), "primario claro")
        assertEquals("FFFFFFFF", argb(light.content), "contenido primario claro")

        val dark = resolveAqButtonColors(
            AqButtonVariant.Primary, DarkAqColorScheme, enabled = true, pressed = false,
        )
        assertEquals("FF5C33CF", argb(dark.container), "primario oscuro")
        assertEquals("FFFFFFFF", argb(dark.content), "contenido primario oscuro")
    }

    @Test
    fun sizes_match_moon_visual_heights_and_preserve_touch_target() {
        assertEquals(48.dp, AqButtonSize.Large.visualHeight)
        assertEquals(40.dp, AqButtonSize.Medium.visualHeight)
        assertEquals(32.dp, AqButtonSize.Small.visualHeight)
        assertEquals(24.dp, AqButtonSize.ExtraSmall.visualHeight)
        assertEquals(48.dp, AqMetrics.ControlMinSize)
    }

    @Test
    fun variants_resolve_normal_and_disabled_colors_in_both_themes() {
        listOf(LightAqColorScheme, DarkAqColorScheme).forEach { scheme ->
            // PV-01: el estado pressed no cambia el relleno ni el color del
            // texto; el efecto es el scale de Moon (0,96) sobre el contenido
            // visual, no una paleta pressed/active inventada.
            assertEquals(
                AqButtonColors(scheme.brandFill, scheme.onBrandFill),
                resolveAqButtonColors(AqButtonVariant.Primary, scheme, enabled = true, pressed = false),
            )
            assertEquals(
                AqButtonColors(scheme.brandFill, scheme.onBrandFill),
                resolveAqButtonColors(AqButtonVariant.Primary, scheme, enabled = true, pressed = true),
            )
            assertEquals(
                AqButtonColors(scheme.brandFillDisabled, scheme.onBrandFillDisabled),
                resolveAqButtonColors(AqButtonVariant.Primary, scheme, enabled = false, pressed = false),
            )
            assertEquals(
                AqButtonColors(scheme.surface, scheme.textPrimary, scheme.border),
                resolveAqButtonColors(AqButtonVariant.Default, scheme, enabled = true, pressed = false),
            )
            assertEquals(
                AqButtonColors(scheme.surface, scheme.textPrimary, scheme.border),
                resolveAqButtonColors(AqButtonVariant.Default, scheme, enabled = true, pressed = true),
            )
            assertEquals(
                AqButtonColors(scheme.dangerFill, scheme.onDanger),
                resolveAqButtonColors(AqButtonVariant.Danger, scheme, enabled = true, pressed = false),
            )
            assertEquals(
                AqButtonColors(scheme.dangerFill, scheme.onDanger),
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
                AqButtonColors(scheme.emphasisSurface, scheme.emphasisText),
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
                AqButtonColors(Color.Transparent, scheme.textPrimary, scheme.functionalBorder),
                resolveAqButtonColors(AqButtonVariant.Outline, scheme, enabled = true, pressed = true),
            )
            assertEquals(
                AqButtonColors(Color.Transparent, scheme.link),
                resolveAqButtonColors(AqButtonVariant.Text, scheme, enabled = true, pressed = false),
            )
            assertEquals(
                AqButtonColors(Color.Transparent, scheme.link),
                resolveAqButtonColors(AqButtonVariant.Text, scheme, enabled = true, pressed = true),
            )
            assertEquals(
                AqButtonColors(Color.Transparent, scheme.textTertiary),
                resolveAqButtonColors(AqButtonVariant.Text, scheme, enabled = false, pressed = false),
            )
        }
    }
}
