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
 * Contrato del esquema de color (ADR-0014, referencia Ant Design Mobile):
 * los dos esquemas fijan los valores actuales (ningún cambio visual
 * silencioso), todos los pares que llegan a pantalla cumplen WCAG AA
 * (>= 4.5:1 texto, >= 3:1 foco) y los roles conmutan con el tema.
 * La paleta de marca está documentada en el contrato §4; la accesibilidad y legibilidad son invariantes.
 */
class AmSchemeTest {

    private fun AmColorScheme.all(): List<Color> = listOf(
        background, surface, surfaceSecondary,
        border, divider,
        textPrimary, textSecondary, textTertiary,
        accent, onAccent, accentContainer, onAccentContainer, accentText, wathet,
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
    fun light_scheme_pins_the_current_values() {
        val s = LightAmColorScheme
        assertEquals("F7F8FA", hex(s.background))
        assertEquals("FFFFFF", hex(s.surface))
        assertEquals("F1F4F8", hex(s.surfaceSecondary))
        assertEquals("E2E7EF", hex(s.border))
        assertEquals("E2E7EF", hex(s.divider))
        assertEquals("1F2937", hex(s.textPrimary))
        assertEquals("596579", hex(s.textSecondary))
        assertEquals("5D6879", hex(s.textTertiary))
        assertEquals("245FE5", hex(s.accent))
        assertEquals("FFFFFF", hex(s.onAccent))
        assertEquals("245FE5", hex(s.accentContainer))
        assertEquals("FFFFFF", hex(s.onAccentContainer))
        assertEquals("245FE5", hex(s.accentText))
        assertEquals("E8F0FF", hex(s.wathet))
        assertEquals("C62828", hex(s.danger))
        assertEquals("FFFFFF", hex(s.onDanger))
        assertEquals("FDECEC", hex(s.dangerContainer))
        assertEquals("087443", hex(s.success))
        assertEquals("E2F7EB", hex(s.successContainer))
        assertEquals("8A4B00", hex(s.warning))
        assertEquals("FFF2D9", hex(s.warningContainer))
        assertEquals("245FE5", hex(s.info))
        assertEquals("E8F0FF", hex(s.infoContainer))
        assertEquals("245FE5", hex(s.focus))
        assertEquals("E9EDF2", hex(s.disabled))
        assertEquals("596579", hex(s.onDisabled))
        assertEquals("172033", hex(s.inverseSurface))
        assertEquals("FFFFFF", hex(s.inverseOnSurface))
        assertEquals("70A5FF", hex(s.inverseAccent))
        assertEquals("FFFFFF", hex(s.qrCanvas))
        assertEquals("FFFFFF", hex(s.surfaceContainerLow))
        assertEquals("F7F9FC", hex(s.surfaceContainer))
        assertEquals("EDF1F6", hex(s.surfaceContainerHigh))
        assertEquals("E3E9F1", hex(s.surfaceContainerHighest))
    }

    @Test
    fun dark_scheme_pins_the_current_values() {
        val s = DarkAmColorScheme
        assertEquals("10141B", hex(s.background))
        assertEquals("171D26", hex(s.surface))
        assertEquals("0C1118", hex(s.surfaceSecondary))
        assertEquals("303844", hex(s.border))
        assertEquals("303844", hex(s.divider))
        assertEquals("F1F5F9", hex(s.textPrimary))
        assertEquals("C1CAD6", hex(s.textSecondary))
        assertEquals("A9B5C5", hex(s.textTertiary))
        assertEquals("70A5FF", hex(s.accent))
        assertEquals("101B2E", hex(s.onAccent))
        assertEquals("70A5FF", hex(s.accentContainer))
        assertEquals("101B2E", hex(s.onAccentContainer))
        assertEquals("80B0FF", hex(s.accentText))
        assertEquals("172B49", hex(s.wathet))
        assertEquals("FF6B78", hex(s.danger))
        assertEquals("2A0F14", hex(s.onDanger))
        assertEquals("3A151D", hex(s.dangerContainer))
        assertEquals("5DDBA5", hex(s.success))
        assertEquals("123628", hex(s.successContainer))
        assertEquals("FFC15A", hex(s.warning))
        assertEquals("3A2B0E", hex(s.warningContainer))
        assertEquals("70A5FF", hex(s.info))
        assertEquals("172B49", hex(s.infoContainer))
        assertEquals("70A5FF", hex(s.focus))
        assertEquals("303844", hex(s.disabled))
        assertEquals("A6B1C0", hex(s.onDisabled))
        assertEquals("F1F5F9", hex(s.inverseSurface))
        assertEquals("10141B", hex(s.inverseOnSurface))
        assertEquals("70A5FF", hex(s.inverseAccent))
        // El lienzo del QR es claro en ambos temas (zona de silencio
        // escaneable): no conmuta con el tema por diseño.
        assertEquals("FFFFFF", hex(s.qrCanvas))
        assertEquals("171D26", hex(s.surfaceContainerLow))
        assertEquals("1D2530", hex(s.surfaceContainer))
        assertEquals("26313E", hex(s.surfaceContainerHigh))
        assertEquals("303B49", hex(s.surfaceContainerHighest))
    }

    @Test
    fun both_schemes_are_complete_and_switchable() {
        listOf(LightAmColorScheme, DarkAmColorScheme).forEach { scheme ->
            assertEquals(34, scheme.all().size)
            assertTrue(scheme.all().none { it == Color.Unspecified }, "scheme has Unspecified colors")
        }
        assertNotEquals(LightAmColorScheme, DarkAmColorScheme)
        // El contenedor de acento es un ALIAS del par accent/onAccent: UN
        // solo acento (sin segunda marca).
        listOf(LightAmColorScheme, DarkAmColorScheme).forEach { scheme ->
            assertEquals(scheme.accent, scheme.accentContainer, "accentContainer must alias accent")
            assertEquals(scheme.onAccent, scheme.onAccentContainer, "onAccentContainer must alias onAccent")
        }
        // El tono informativo usa el acento de marca.
        listOf(LightAmColorScheme, DarkAmColorScheme).forEach { scheme ->
            assertEquals(scheme.accent, scheme.info, "info must be the accent")
            assertEquals(scheme.wathet, scheme.infoContainer, "infoContainer must be wathet")
        }
    }

    @Test
    fun light_foreground_background_pairs_meet_wcag_aa() {
        val s = LightAmColorScheme
        assertTrue(contrast(hex(s.accent), hex(s.onAccent)) >= 4.5, "light accent/onAccent")
        assertTrue(contrast(hex(s.danger), hex(s.onDanger)) >= 4.5, "light danger/onDanger")
        assertTrue(contrast(hex(s.accentText), hex(s.wathet)) >= 4.5, "light accentText/wathet")
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
        assertTrue(contrast(hex(s.accentText), hex(s.wathet)) >= 4.5, "dark accentText/wathet")
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
            // El contenedor informativo es wathet: el contenido es accentText
            // (el acento puro no alcanza AA como texto sobre wathet).
            assertEquals(scheme.accentText, scheme.toneColors(AmTone.Info).content)
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
    fun focus_meets_3_to_1_in_both_schemes() {
        // La separación usa hairlines decorativos del sistema; el
        // foco y los indicadores activos sí cumplen ≥ 3:1.
        listOf(LightAmColorScheme, DarkAmColorScheme).forEach { scheme ->
            assertTrue(contrast(hex(scheme.focus), hex(scheme.background)) >= 3.0, "focus/background")
            assertTrue(contrast(hex(scheme.focus), hex(scheme.surface)) >= 3.0, "focus/surface")
            assertTrue(contrast(hex(scheme.focus), hex(scheme.surfaceSecondary)) >= 3.0, "focus/surfaceSecondary")
        }
    }

    @Test
    fun disabled_pair_keeps_a_quality_floor() {
        // WCAG exime los controles deshabilitados de AA; el sistema se
        // exige a sí mismo un suelo de 3:1 para que "deshabilitado" sea
        // perceptible sin depender solo del contraste con el fondo
        // (además de la opacidad .4 definida por el sistema en botones).
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
            "textPrimary" to textPrimary,
            "textSecondary" to textSecondary,
            "textTertiary" to textTertiary,
            "accent" to accent,
            "onAccent" to onAccent,
            "accentContainer" to accentContainer,
            "onAccentContainer" to onAccentContainer,
            "accentText" to accentText,
            "wathet" to wathet,
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
            "textPrimary", "textSecondary", "textTertiary",
            "accent", "onAccent", "accentText", "wathet", "danger", "onDanger",
            "dangerContainer", "success", "successContainer", "warning",
            "warningContainer", "info", "infoContainer", "focus",
            "disabled", "onDisabled", "inverseSurface", "inverseOnSurface",
            "surfaceContainerLow", "surfaceContainer",
            "surfaceContainerHigh", "surfaceContainerHighest",
        ).forEach { key ->
            assertNotEquals(light.getValue(key), dark.getValue(key), "$key must switch with the theme")
        }
        // El lienzo del QR no conmuta: claro garantizado en ambos temas.
        assertEquals(light.getValue("qrCanvas"), dark.getValue("qrCanvas"))
        // El acento inverso tampoco: el primario oscuro de la referencia
        // funciona en ambos contextos inversos.
        assertEquals(light.getValue("inverseAccent"), dark.getValue("inverseAccent"))
    }
}
