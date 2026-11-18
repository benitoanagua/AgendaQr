package com.agendaqr.core.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import kotlin.math.pow
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

/**
 * Identidad de contextos (X-06): pares deterministas tomados de la
 * instantánea de `@shopify/polaris-tokens@9.4.2`, todos validados a AA.
 * El acento de sistema usa `emphasis` + blanco; Magenta aplica PV-07.
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
    fun the_palette_is_exactly_the_12_admitted_accents_plus_system() {
        assertEquals(12, AqAccents.Admitted.size)
        val expected = mapOf(
            "system" to "005BD3",
            "blue" to "004299",
            "ink" to "003A5A",
            "emerald" to "047B5D",
            "forest" to "035E4C",
            "red" to "C70A24",
            "wine" to "8E0B21",
            "violet" to "5700D1",
            "indigo" to "7126FF",
            "magenta" to "C530C5",
            "sky" to "91D0FF",
            "amber" to "FFB800",
            "lemon" to "FFE600",
        )
        val actual = (AqAccents.Admitted + AqAccents.System)
            .associate { it.id to hex(it.background) }
        assertEquals(expected, actual)

        // F2: los literales anónimos pasan a constantes nombradas y se fija el
        // par completo. Magenta conserva blanco aunque el par publicado sea
        // `avatarOneOnFill` (PV-07).
        val expectedOn = mapOf(
            "system" to AqPalette.white,
            "blue" to AqPalette.white,
            "ink" to AqPalette.white,
            "emerald" to AqPalette.white,
            "forest" to AqPalette.white,
            "red" to AqPalette.white,
            "wine" to AqPalette.white,
            "violet" to AqPalette.magicOnFill,
            "indigo" to AqPalette.magicOnFill,
            "magenta" to AqPalette.white,
            "sky" to AqPalette.gray900,
            "amber" to AqPalette.gray900,
            "lemon" to AqPalette.gray900,
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
                accent.onAccent == AqPalette.white ||
                    accent.onAccent == AqPalette.magicOnFill ||
                    accent.onAccent == AqPalette.gray900,
                "${accent.id}: onAccent debe ser un par publicado verificado",
            )
        }
    }

    @Test
    fun magenta_uses_verified_white_instead_of_the_published_avatar_pair() {
        val magenta = AqAccents.Admitted.first { it.id == "magenta" }
        val published = contrast(magenta.background, AqPalette.avatarOneOnFill)
        val chosen = contrast(magenta.background, magenta.onAccent)
        assertTrue(published < 4.5, "PV-07: par publicado $published no debería llegar a AA")
        assertTrue(chosen >= 4.5, "PV-07: par aplicado $chosen debe cumplir AA")
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
        val sky = AqAccents.Admitted.first { it.id == "sky" }
        val cases = listOf(
            blue to LightAmColorScheme,
            sky to DarkAmColorScheme,
        )
        cases.forEach { (accent, scheme) ->
            val asText = accent.background.asTextOn(scheme.background, scheme.emphasisText)
            assertTrue(
                contrast(asText, scheme.background) >= 4.5,
                "${accent.id} asTextOn ${if (scheme === LightAmColorScheme) "light" else "dark"}",
            )
        }
        // El azul de sistema no es legible sobre el fondo oscuro: cae SIEMPRE
        // al texto de acento del tema.
        assertEquals(
            DarkAmColorScheme.emphasisText,
            AqAccents.System.background.asTextOn(DarkAmColorScheme.background, DarkAmColorScheme.emphasisText),
        )
    }
}
