package com.agendaqr.core.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.agendaqr.core.ui.components.AmTone
import com.agendaqr.core.ui.components.toneColors
import kotlin.math.pow
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/**
 * Contrato del esquema de color de AgendaMobile (ADR-0013): los dos
 * esquemas fijan los valores actuales (ningún cambio visual silencioso),
 * todos los pares que llegan a pantalla cumplen WCAG AA (>= 4.5:1 texto,
 * >= 3:1 bordes/foco) y los roles conmutan con el tema.
 */
class AmSchemeTest {

    private fun AmColorScheme.all(): List<Color> = listOf(
        background, surface, surfaceSecondary,
        border, divider, borderControl,
        textPrimary, textSecondary, textTertiary,
        accent, onAccent, accentContainer, onAccentContainer, accentText,
        danger, onDanger, dangerContainer,
        success, successContainer, warning, warningContainer,
        info, infoContainer, focus,
        disabled, onDisabled,
        inverseSurface, inverseOnSurface, inverseAccent,
        qrCanvas,
        surfaceContainerLow, surfaceContainer, surfaceContainerHigh, surfaceContainerHighest,
    )

    private fun hex(color: Color): String =
        color.toArgb().toUInt().toString(16).uppercase().padStart(8, '0').drop(2)

    private fun luminance(hex: String): Double {
        fun channel(v: Int): Double {
            val c = v / 255.0
            return if (c <= 0.04045) c / 12.92 else ((c + 0.055) / 1.055).pow(2.4)
        }
        val r = channel(hex.substring(0, 2).toInt(16))
        val g = channel(hex.substring(2, 4).toInt(16))
        val b = channel(hex.substring(4, 6).toInt(16))
        return 0.2126 * r + 0.7152 * g + 0.0722 * b
    }

    private fun contrast(a: String, b: String): Double {
        val l1 = luminance(a)
        val l2 = luminance(b)
        val (hi, lo) = if (l1 >= l2) l1 to l2 else l2 to l1
        return (hi + 0.05) / (lo + 0.05)
    }

    @Test
    fun light_scheme_pins_the_current_agendamobile_values() {
        val s = LightAmColorScheme
        assertEquals("F5F5F5", hex(s.background))
        assertEquals("FFFFFF", hex(s.surface))
        assertEquals("F5F5F5", hex(s.surfaceSecondary))
        assertEquals("E5E5E5", hex(s.border))
        assertEquals("F0F0F0", hex(s.divider))
        assertEquals("595959", hex(s.borderControl))
        assertEquals("333333", hex(s.textPrimary))
        assertEquals("595959", hex(s.textSecondary))
        assertEquals("666666", hex(s.textTertiary))
        assertEquals("0958D9", hex(s.accent))
        assertEquals("FFFFFF", hex(s.onAccent))
        assertEquals("0958D9", hex(s.accentContainer))
        assertEquals("FFFFFF", hex(s.onAccentContainer))
        assertEquals("0958D9", hex(s.accentText))
        assertEquals("C41D2A", hex(s.danger))
        assertEquals("FFFFFF", hex(s.onDanger))
        assertEquals("FFF1F0", hex(s.dangerContainer))
        assertEquals("006B45", hex(s.success))
        assertEquals("E8FFF3", hex(s.successContainer))
        assertEquals("8C4A00", hex(s.warning))
        assertEquals("FFF7E6", hex(s.warningContainer))
        assertEquals("0958D9", hex(s.info))
        assertEquals("E6F4FF", hex(s.infoContainer))
        assertEquals("0958D9", hex(s.focus))
        assertEquals("E8E8E8", hex(s.disabled))
        assertEquals("595959", hex(s.onDisabled))
        assertEquals("333333", hex(s.inverseSurface))
        assertEquals("FFFFFF", hex(s.inverseOnSurface))
        assertEquals("69B1FF", hex(s.inverseAccent))
        assertEquals("FFFFFF", hex(s.qrCanvas))
        assertEquals("FFFFFF", hex(s.surfaceContainerLow))
        assertEquals("F5F5F5", hex(s.surfaceContainer))
        assertEquals("F0F0F0", hex(s.surfaceContainerHigh))
        assertEquals("E8E8E8", hex(s.surfaceContainerHighest))
    }

    @Test
    fun dark_scheme_pins_the_current_agendamobile_values() {
        val s = DarkAmColorScheme
        assertEquals("141414", hex(s.background))
        assertEquals("1F1F1F", hex(s.surface))
        assertEquals("2A2A2A", hex(s.surfaceSecondary))
        assertEquals("424242", hex(s.border))
        assertEquals("303030", hex(s.divider))
        assertEquals("BFBFBF", hex(s.borderControl))
        assertEquals("F5F5F5", hex(s.textPrimary))
        assertEquals("D9D9D9", hex(s.textSecondary))
        assertEquals("BFBFBF", hex(s.textTertiary))
        assertEquals("69B1FF", hex(s.accent))
        assertEquals("141414", hex(s.onAccent))
        assertEquals("69B1FF", hex(s.accentContainer))
        assertEquals("141414", hex(s.onAccentContainer))
        assertEquals("69B1FF", hex(s.accentText))
        assertEquals("FF7875", hex(s.danger))
        assertEquals("2A0000", hex(s.onDanger))
        assertEquals("5C0011", hex(s.dangerContainer))
        assertEquals("5CDBA5", hex(s.success))
        assertEquals("003D2B", hex(s.successContainer))
        assertEquals("FFC069", hex(s.warning))
        assertEquals("4A2B00", hex(s.warningContainer))
        assertEquals("69B1FF", hex(s.info))
        assertEquals("102A43", hex(s.infoContainer))
        assertEquals("69B1FF", hex(s.focus))
        assertEquals("303030", hex(s.disabled))
        assertEquals("BFBFBF", hex(s.onDisabled))
        assertEquals("F5F5F5", hex(s.inverseSurface))
        assertEquals("141414", hex(s.inverseOnSurface))
        assertEquals("0958D9", hex(s.inverseAccent))
        // El lienzo del QR es claro en ambos temas (zona de silencio
        // escaneable): no conmuta con el tema por diseño.
        assertEquals("FFFFFF", hex(s.qrCanvas))
        assertEquals("1A1A1A", hex(s.surfaceContainerLow))
        assertEquals("1F1F1F", hex(s.surfaceContainer))
        assertEquals("2A2A2A", hex(s.surfaceContainerHigh))
        assertEquals("383838", hex(s.surfaceContainerHighest))
    }

    @Test
    fun both_schemes_are_complete_and_switchable() {
        listOf(LightAmColorScheme, DarkAmColorScheme).forEach { scheme ->
            assertEquals(34, scheme.all().size)
            assertTrue(scheme.all().none { it == Color.Unspecified }, "scheme has Unspecified colors")
        }
        assertNotEquals(LightAmColorScheme, DarkAmColorScheme)
        // El contenedor de acento es un ALIAS del par accent/onAccent:
        // AgendaMobile tiene un solo acento (sin segunda marca).
        listOf(LightAmColorScheme, DarkAmColorScheme).forEach { scheme ->
            assertEquals(scheme.accent, scheme.accentContainer, "accentContainer must alias accent")
            assertEquals(scheme.onAccent, scheme.onAccentContainer, "onAccentContainer must alias onAccent")
        }
    }

    @Test
    fun light_foreground_background_pairs_meet_wcag_aa() {
        val s = LightAmColorScheme
        assertTrue(contrast(hex(s.accent), hex(s.onAccent)) >= 4.5, "light accent/onAccent")
        assertTrue(contrast(hex(s.danger), hex(s.onDanger)) >= 4.5, "light danger/onDanger")
        assertTrue(contrast(hex(s.background), hex(s.textPrimary)) >= 4.5, "light background/textPrimary")
        assertTrue(contrast(hex(s.surface), hex(s.textPrimary)) >= 4.5, "light surface/textPrimary")
        assertTrue(contrast(hex(s.inverseSurface), hex(s.inverseOnSurface)) >= 4.5, "light inverse pair")
        assertTrue(contrast(hex(s.inverseSurface), hex(s.inverseAccent)) >= 4.5, "light inverse/inverseAccent")
    }

    @Test
    fun dark_foreground_background_pairs_meet_wcag_aa() {
        val s = DarkAmColorScheme
        assertTrue(contrast(hex(s.accent), hex(s.onAccent)) >= 4.5, "dark accent/onAccent")
        assertTrue(contrast(hex(s.danger), hex(s.onDanger)) >= 4.5, "dark danger/onDanger")
        assertTrue(contrast(hex(s.background), hex(s.textPrimary)) >= 4.5, "dark background/textPrimary")
        assertTrue(contrast(hex(s.surface), hex(s.textPrimary)) >= 4.5, "dark surface/textPrimary")
        assertTrue(contrast(hex(s.inverseSurface), hex(s.inverseOnSurface)) >= 4.5, "dark inverse pair")
    }

    @Test
    fun state_tone_pairs_meet_wcag_aa_in_both_schemes() {
        // Cada tono tiene content/container propios con >= 4.5:1 en
        // ambos temas; el texto primario también es legible sobre el
        // contenedor (mensajes largos, acciones heredan el color).
        listOf(LightAmColorScheme, DarkAmColorScheme).forEach { scheme ->
            AmTone.entries.forEach { tone ->
                val colors = scheme.toneColors(tone)
                assertTrue(
                    contrast(hex(colors.content), hex(colors.container)) >= 4.5,
                    "${scheme.javaClass.simpleName} $tone content/container",
                )
                assertTrue(
                    contrast(hex(scheme.textPrimary), hex(colors.container)) >= 4.5,
                    "${scheme.javaClass.simpleName} $tone textPrimary/container",
                )
            }
            assertEquals(scheme.success, scheme.toneColors(AmTone.Success).content)
            assertEquals(scheme.info, scheme.toneColors(AmTone.Info).content)
            assertEquals(scheme.warning, scheme.toneColors(AmTone.Warning).content)
            assertEquals(scheme.danger, scheme.toneColors(AmTone.Danger).content)
        }
    }

    @Test
    fun accent_text_is_readable_on_background_and_surfaces_in_both_schemes() {
        listOf(LightAmColorScheme, DarkAmColorScheme).forEach { scheme ->
            assertTrue(contrast(hex(scheme.accentText), hex(scheme.background)) >= 4.5, "accentText/background")
            assertTrue(contrast(hex(scheme.accentText), hex(scheme.surface)) >= 4.5, "accentText/surface")
            assertTrue(contrast(hex(scheme.accentText), hex(scheme.surfaceSecondary)) >= 4.5, "accentText/surfaceSecondary")
        }
    }

    @Test
    fun ambient_text_pairs_meet_wcag_aa() {
        // Texto secundario/terciario sobre las superficies reales donde
        // viven (hints, captions, filas) y tonos de estado como TEXTO.
        listOf(LightAmColorScheme, DarkAmColorScheme).forEach { scheme ->
            assertTrue(contrast(hex(scheme.textSecondary), hex(scheme.background)) >= 4.5, "textSecondary/background")
            assertTrue(contrast(hex(scheme.textSecondary), hex(scheme.surface)) >= 4.5, "textSecondary/surface")
            assertTrue(contrast(hex(scheme.textSecondary), hex(scheme.surfaceSecondary)) >= 4.5, "textSecondary/surfaceSecondary")
            assertTrue(contrast(hex(scheme.textTertiary), hex(scheme.background)) >= 4.5, "textTertiary/background")
            assertTrue(contrast(hex(scheme.textTertiary), hex(scheme.surfaceSecondary)) >= 4.5, "textTertiary/surfaceSecondary")
            listOf(scheme.danger, scheme.success, scheme.warning, scheme.info).forEach { tone ->
                assertTrue(contrast(hex(tone), hex(scheme.background)) >= 4.5, "tone text/background")
                assertTrue(contrast(hex(tone), hex(scheme.surface)) >= 4.5, "tone text/surface")
            }
        }
    }

    @Test
    fun control_borders_and_focus_meet_3_to_1_in_both_schemes() {
        listOf(LightAmColorScheme, DarkAmColorScheme).forEach { scheme ->
            assertTrue(contrast(hex(scheme.borderControl), hex(scheme.background)) >= 3.0, "borderControl/background")
            assertTrue(contrast(hex(scheme.borderControl), hex(scheme.surface)) >= 3.0, "borderControl/surface")
            assertTrue(contrast(hex(scheme.borderControl), hex(scheme.surfaceSecondary)) >= 3.0, "borderControl/surfaceSecondary")
            assertTrue(contrast(hex(scheme.focus), hex(scheme.background)) >= 3.0, "focus/background")
            assertTrue(contrast(hex(scheme.focus), hex(scheme.surfaceSecondary)) >= 3.0, "focus/surfaceSecondary")
        }
    }

    @Test
    fun disabled_pair_keeps_a_quality_floor() {
        // WCAG exime los controles deshabilitados de AA; el sistema se
        // exige a sí mismo un suelo de 3:1 para que "deshabilitado" sea
        // perceptible sin depender solo del contraste con el fondo.
        listOf(LightAmColorScheme, DarkAmColorScheme).forEach { scheme ->
            assertTrue(
                contrast(hex(scheme.onDisabled), hex(scheme.disabled)) >= 3.0,
                "onDisabled/disabled",
            )
        }
    }

    /**
     * Campos requeridos por esquema: el constructor data-class hace que
     * falte un campo sea error de compilación; este test fija el conjunto
     * para que un renombrado/eliminación silenciosa se detecte en review,
     * y prueba que ningún token es solo-de-oscuro.
     */
    @Test
    fun both_schemes_expose_the_same_required_semantic_fields() {
        fun AmColorScheme.fields(): Map<String, Color> = mapOf(
            "background" to background,
            "surface" to surface,
            "surfaceSecondary" to surfaceSecondary,
            "border" to border,
            "divider" to divider,
            "borderControl" to borderControl,
            "textPrimary" to textPrimary,
            "textSecondary" to textSecondary,
            "textTertiary" to textTertiary,
            "accent" to accent,
            "onAccent" to onAccent,
            "accentContainer" to accentContainer,
            "onAccentContainer" to onAccentContainer,
            "accentText" to accentText,
            "danger" to danger,
            "onDanger" to onDanger,
            "dangerContainer" to dangerContainer,
            "success" to success,
            "successContainer" to successContainer,
            "warning" to warning,
            "warningContainer" to warningContainer,
            "info" to info,
            "infoContainer" to infoContainer,
            "focus" to focus,
            "disabled" to disabled,
            "onDisabled" to onDisabled,
            "inverseSurface" to inverseSurface,
            "inverseOnSurface" to inverseOnSurface,
            "inverseAccent" to inverseAccent,
            "qrCanvas" to qrCanvas,
            "surfaceContainerLow" to surfaceContainerLow,
            "surfaceContainer" to surfaceContainer,
            "surfaceContainerHigh" to surfaceContainerHigh,
            "surfaceContainerHighest" to surfaceContainerHighest,
        )
        val light = LightAmColorScheme.fields()
        val dark = DarkAmColorScheme.fields()
        assertEquals(light.keys, dark.keys)
        assertEquals(34, light.size)
        // Los roles dependientes del tema conmutan.
        listOf(
            "background", "surface", "surfaceSecondary", "border", "divider",
            "borderControl", "textPrimary", "textSecondary", "textTertiary",
            "accent", "onAccent", "accentText", "danger", "onDanger",
            "dangerContainer", "success", "successContainer", "warning",
            "warningContainer", "info", "infoContainer", "focus",
            "disabled", "onDisabled", "inverseSurface", "inverseOnSurface",
            "inverseAccent", "surfaceContainerLow", "surfaceContainer",
            "surfaceContainerHigh", "surfaceContainerHighest",
        ).forEach { key ->
            assertNotEquals(light.getValue(key), dark.getValue(key), "$key must switch with the theme")
        }
        // El lienzo del QR no conmuta: claro garantizado en ambos temas.
        assertEquals(light.getValue("qrCanvas"), dark.getValue("qrCanvas"))
    }
}
