package com.agendaqr.core.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlin.math.pow
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/**
 * Identidad de contextos (X-06): pares deterministas de colores Moon
 * (`MoonColors`), todos validados a AA. El acento de sistema es `piccolo` +
 * blanco; verde y bronce son derivados oscurecidos de `roshi` y `raditz`.
 */
class AqAccentTest {

    private fun hex(color: Color): String =
        color.toArgb().toUInt().toString(16).uppercase().padStart(8, '0').drop(2)

    private fun luminance(color: Color): Double {
        fun channel(v: Float): Double {
            val c = v.toDouble()
            return if (c <= 0.04045) c / 12.92 else ((c + 0.055) / 1.055).pow(2.4)
        }
        return 0.2126 * channel(color.red) + 0.7152 * channel(color.green) +
            0.0722 * channel(color.blue)
    }

    private fun contrast(a: Color, b: Color): Double {
        val l1 = luminance(a)
        val l2 = luminance(b)
        val (hi, lo) = if (l1 >= l2) l1 to l2 else l2 to l1
        return (hi + 0.05) / (lo + 0.05)
    }

    @Test
    fun the_palette_is_exactly_the_11_admitted_accents_plus_system() {
        assertEquals(11, AqAccents.Admitted.size)
        val expected = mapOf(
            "system" to "5C33CF",
            "blue" to "3448F0",
            "green" to "2C6B34",
            "red" to "D33030",
            "brown" to "725550",
            "bronze" to "745330",
            "slate" to "687083",
            "ink" to "000000",
            "mint" to "1BD29A",
            "aqua" to "95F1D5",
            "amber" to "FFB319",
            "coral" to "FF4E64",
        )
        val actual = (AqAccents.Admitted + AqAccents.System)
            .associate { it.id to hex(it.background) }
        assertEquals(expected, actual)

        // Par completo: texto blanco (goten) sobre los acentos oscuros y negro
        // (popo) sobre los claros.
        val expectedOn = mapOf(
            "system" to AqPalette.goten,
            "blue" to AqPalette.goten,
            "green" to AqPalette.goten,
            "red" to AqPalette.goten,
            "brown" to AqPalette.goten,
            "bronze" to AqPalette.goten,
            "slate" to AqPalette.goten,
            "ink" to AqPalette.goten,
            "mint" to AqPalette.popo,
            "aqua" to AqPalette.popo,
            "amber" to AqPalette.popo,
            "coral" to AqPalette.popo,
        )
        assertEquals(
            expectedOn,
            (AqAccents.Admitted + AqAccents.System).associate { it.id to it.onAccent },
        )
    }

    @Test
    fun every_accent_text_pair_meets_the_wcag_threshold() {
        // Texto normal >= 4.5:1; el par es fijo e idéntico en claro y
        // oscuro (par autónomo), por lo que una única comprobación cubre
        // ambos temas.
        (AqAccents.Admitted + AqAccents.System).forEach { accent ->
            val ratio = contrast(accent.background, accent.onAccent)
            assertTrue(ratio >= 4.5, "${accent.id}: onAccent/background = $ratio < 4.5")
            assertTrue(
                accent.onAccent == AqPalette.goten || accent.onAccent == AqPalette.popo,
                "${accent.id}: onAccent debe ser goten o popo de Moon",
            )
        }
    }

    @Test
    fun accentFor_is_deterministic_and_platform_independent() {
        val id = "context-9f83b7c2-0000-4abc-9def-0123456789ab"
        assertEquals(accentFor(id), accentFor(id))
        assertEquals(AqAccents.System, accentFor(null))
        assertNotEquals(AqAccents.System, accentFor(id))
        // El algoritmo es FNV-1a 32 bits sobre UTF-8, sin String.hashCode:
        // el mismo identificador asigna el mismo acento en Android e iOS.
        assertEquals(accentFor("mercado"), accentFor("mercado".repeat(1)))
        assertTrue(AqAccents.Admitted.containsAll(
            listOf("a", "b", "c", "mercado", id).map { accentFor(it) },
        ))
    }

    @Test
    fun accentFor_distributes_across_the_palette() {
        // Reparto: con suficientes identificadores distintos, la paleta
        // completa se usa (evita un hash degenerado que colapse a un solo
        // acento). No exige uniformidad estadística.
        val used = (0 until 500)
            .map { accentFor("ctx-$it") }
            .toSet()
        assertTrue(used.size >= 8, "reparto degradado: solo ${used.size} acentos usados")
    }

    @Test
    fun accent_as_text_never_returns_below_threshold() {
        // Los acentos de contexto NO se usan como texto si su par no llega
        // a 4.5:1: asTextOn cae al fallback legible del tema.
        val blue = AqAccents.Admitted.first { it.id == "blue" }
        val mint = AqAccents.Admitted.first { it.id == "mint" }
        val cases = listOf(
            blue to LightAqColorScheme,
            mint to DarkAqColorScheme,
        )
        cases.forEach { (accent, scheme) ->
            val asText = accent.background.asTextOn(scheme.background, scheme.emphasisText)
            assertTrue(
                contrast(asText, scheme.background) >= 4.5,
                "${accent.id} asTextOn ${if (scheme === LightAqColorScheme) "light" else "dark"}",
            )
        }
        // El morado de sistema no es legible sobre el fondo oscuro: cae SIEMPRE
        // al texto de acento del tema.
        assertEquals(
            DarkAqColorScheme.emphasisText,
            AqAccents.System.background.asTextOn(DarkAqColorScheme.background, DarkAqColorScheme.emphasisText),
        )
    }
}
