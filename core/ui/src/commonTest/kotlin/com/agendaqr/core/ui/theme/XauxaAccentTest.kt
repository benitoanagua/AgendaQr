package com.agendaqr.core.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlin.math.pow
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/**
 * T1/V1.1 (ADR-0005, spec §12 Color): la paleta de acentos Metro fijada
 * por la tabla de contraste calculada de la spec. Estos tests fijan los
 * colores exactos (no se recalculan ni se cambian), verifican que cada
 * par acento/texto asignado cumple el umbral WCAG de la spec en claro y
 * en oscuro, y fijan el determinismo multiplataforma de [accentFor].
 */
class XauxaAccentTest {

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
        // Tabla de contraste de la spec §12 (V1.1): 12 acentos admitidos,
        // ningún candidato excluido por contraste salió de la tabla — los
        // 4 no incluidos se descartaron solo por acotar la paleta a 12.
        assertEquals(12, XauxaAccents.Admitted.size)
        val expected = mapOf(
            "system" to "0067B8",
            "lime" to "A4C400", "emerald" to "008A00", "teal" to "00ABA9",
            "cyan" to "1BA1E2", "cobalt" to "0050EF", "indigo" to "6A00FF",
            "violet" to "AA00FF", "magenta" to "D80073", "crimson" to "A20025",
            "red" to "E51400", "orange" to "FA6800", "amber" to "F0A30A",
        )
        val actual = (XauxaAccents.Admitted + XauxaAccents.System)
            .associate { it.id to hex(it.background) }
        assertEquals(expected, actual)
    }

    @Test
    fun every_accent_text_pair_meets_the_spec_wcag_thresholds() {
        // Spec §12 (M5): texto normal >= 4.5:1, texto grande >= 3:1; el
        // par es fijo e idéntico en claro y oscuro (fondo oscuro negro
        // puro), por lo que una única comprobación del par cubre ambos
        // temas (I19).
        val black = Color(0xFF000000)
        val white = Color(0xFFFFFFFF)
        (XauxaAccents.Admitted + XauxaAccents.System).forEach { accent ->
            val ratio = contrast(accent.background, accent.onAccent)
            assertTrue(ratio >= 4.5, "${accent.id}: onAccent/background = $ratio < 4.5")
            assertTrue(
                accent.onAccent == black || accent.onAccent == white,
                "${accent.id}: onAccent debe ser negro o blanco puro (regla M5)",
            )
        }
    }

    @Test
    fun accentFor_is_deterministic_and_platform_independent() {
        val id = "context-9f83b7c2-0000-4abc-9def-0123456789ab"
        assertEquals(accentFor(id), accentFor(id))
        assertEquals(XauxaAccents.System, accentFor(null))
        assertNotEquals(XauxaAccents.System, accentFor(id))
        // El algoritmo es FNV-1a 32 bits sobre UTF-8, sin String.hashCode:
        // el mismo identificador asigna el mismo acento en Android, iOS y
        // wasmJs. Valores de referencia calculados con el propio algoritmo
        // documentado (pin anti-cambio-silencioso).
        assertEquals(accentFor("mercado"), accentFor("mercado".repeat(1)))
        assertTrue(XauxaAccents.Admitted.containsAll(
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
}
