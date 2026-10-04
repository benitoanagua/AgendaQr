package com.agendaqr.core.ui.theme

import androidx.compose.ui.text.font.FontFamily
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * T9 — Tipografía Xauxa (§12): Archivo para display/encabezados;
 * Roboto/San Francisco (la sans del sistema) para UI/cuerpo.
 *
 * La familia display (Archivo empaquetada, OFL 1.1 —
 * docs/05-design-system/05-xauxa-tipografia.md) es un acceso componible
 * (mismo patrón que XauxaColor), por lo que su instancia solo se evalúa en
 * composición: aquí se fija el resto del contrato tipográfico.
 */
class XauxaTypographyTest {

    @Test
    fun sizes_keep_the_display_hierarchy() {
        // La jerarquía de encabezados/cuerpo se lee también por el tamaño;
        // todos en sp: el escalado de fuente del sistema se respeta (§11).
        assertTrue(XauxaType.Display.value > XauxaType.Headline.value)
        assertTrue(XauxaType.Headline.value > XauxaType.Title.value)
        assertTrue(XauxaType.Title.value > XauxaType.Body.value)
        assertTrue(XauxaType.Body.value > XauxaType.Label.value)
        assertTrue(XauxaType.Label.value > XauxaType.Caption.value)
    }

    @Test
    fun ui_family_is_the_platform_default_sans() {
        // Roboto en Android, SF en iOS (§12): delegar en el sistema evita
        // empaquetar una copia redundante y respeta al usuario.
        assertEquals(FontFamily.Default, XauxaType.FamilyUi)
    }
}
