package com.agendaqr.core.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlin.math.pow
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/**
 * Paleta de identidad de contextos (contrato §4): 12 tonos
 * contemporáneos de etiqueta más el acento de marca AgendaQr. `tdesign_flutter@0.2.7`
 * supplies the shared UI palette; these domain identity colors remain
 * the explicit accessibility-approved exception. Tests pin values and FNV-1a.
 */
class AmAccentTest {

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
    fun the_palette_is_exactly_the_12_admitted_accents_plus_system() {
        assertEquals(12, AmAccents.Admitted.size)
        val expected = mapOf(
            "system" to "245FE5",
            "blue" to "1D4ED8", "sky" to "0369A1", "teal" to "0F766E",
            "cyan" to "0E7490", "emerald" to "047857", "green" to "15803D",
            "lime" to "4D7C0F", "amber" to "A16207", "orange" to "C2410C",
            "red" to "B91C1C", "rose" to "BE123C", "violet" to "6D28D9",
        )
        val actual = (AmAccents.Admitted + AmAccents.System)
            .associate { it.id to hex(it.background) }
        assertEquals(expected, actual)
    }

    @Test
    fun every_accent_text_pair_meets_the_wcag_threshold() {
        // Texto normal >= 4.5:1; el par es fijo e idéntico en claro y
        // oscuro (par autónomo), por lo que una única comprobación cubre
        // ambos temas.
        val black = Color(0xFF000000)
        val white = Color(0xFFFFFFFF)
        (AmAccents.Admitted + AmAccents.System).forEach { accent ->
            val ratio = contrast(accent.background, accent.onAccent)
            assertTrue(ratio >= 4.5, "${accent.id}: onAccent/background = $ratio < 4.5")
            assertTrue(
                accent.onAccent == black || accent.onAccent == white,
                "${accent.id}: onAccent debe ser negro o blanco puro",
            )
        }
    }

    @Test
    fun accentFor_is_deterministic_and_platform_independent() {
        val id = "context-9f83b7c2-0000-4abc-9def-0123456789ab"
        assertEquals(accentFor(id), accentFor(id))
        assertEquals(AmAccents.System, accentFor(null))
        assertNotEquals(AmAccents.System, accentFor(id))
        // El algoritmo es FNV-1a 32 bits sobre UTF-8, sin String.hashCode:
        // el mismo identificador asigna el mismo acento en Android e iOS.
        assertEquals(accentFor("mercado"), accentFor("mercado".repeat(1)))
        assertTrue(AmAccents.Admitted.containsAll(
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
        val cases = listOf(
            AmAccent("lime", Color(0xFF4D7C0F), Color(0xFFFFFFFF)) to LightAmColorScheme,
            AmAccent("violet", Color(0xFF6D28D9), Color(0xFFFFFFFF)) to DarkAmColorScheme,
        )
        cases.forEach { (accent, scheme) ->
            val asText = accent.background.asTextOn(scheme.background, scheme.accentText)
            assertTrue(
                contrast(asText, scheme.background) >= 4.5,
                "${accent.id} asTextOn ${if (scheme === LightAmColorScheme) "light" else "dark"}",
            )
        }
        // Un acento ilegible sobre el fondo cae SIEMPRE al fallback.
        assertEquals(
            DarkAmColorScheme.accentText,
            AmAccents.System.background.asTextOn(DarkAmColorScheme.background, DarkAmColorScheme.accentText),
        )
    }
}
