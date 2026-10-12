package com.agendaqr.core.ui.theme

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Tipografía Xauxa (spec §12 V1.2, ADR-0012): familia de sistema en todos
 * los estilos, roles estándar de M3 desde tokens y sin fuente empaquetada.
 */
class XauxaTypographyTest {

    @Test
    fun sizes_keep_the_display_hierarchy() {
        // La jerarquía de encabezados/cuerpo se lee también por el tamaño;
        // todos en sp: el escalado de fuente del sistema se respeta (§11).
        assertTrue(XauxaType.Display.value > XauxaType.HeadlineMedium.value)
        assertTrue(XauxaType.HeadlineMedium.value > XauxaType.Headline.value)
        assertTrue(XauxaType.Headline.value > XauxaType.TitleLarge.value)
        assertTrue(XauxaType.TitleLarge.value > XauxaType.Body.value)
        assertTrue(XauxaType.Body.value > XauxaType.Caption.value)
        assertTrue(XauxaType.Caption.value > XauxaType.CaptionSmall.value)
    }

    @Test
    fun ui_family_is_the_platform_default_sans() {
        // Roboto en Android, SF en iOS (§12 V1.2): delegar en el sistema
        // evita empaquetar una copia redundante y respeta al usuario.
        assertEquals(FontFamily.Default, XauxaType.FamilyUi)
    }

    @Test
    fun m3_roles_carry_the_canonical_sizes() {
        // El título de página (titleLarge) y el titular de auth
        // (headlineMedium) son los roles M3 estándar, en sp.
        assertEquals(22.sp, XauxaMaterialTypography.titleLarge.fontSize)
        assertEquals(28.sp, XauxaMaterialTypography.headlineMedium.fontSize)
        assertEquals(16.sp, XauxaMaterialTypography.bodyLarge.fontSize)
        assertEquals(14.sp, XauxaMaterialTypography.labelLarge.fontSize)
    }

    @Test
    fun m3_roles_use_the_system_family() {
        // Sin fuente empaquetada: todos los roles resuelven a la sans del
        // sistema (Archivo retirada del binario, ADR-0012).
        val roles = listOf(
            XauxaMaterialTypography.headlineLarge,
            XauxaMaterialTypography.headlineMedium,
            XauxaMaterialTypography.headlineSmall,
            XauxaMaterialTypography.titleLarge,
            XauxaMaterialTypography.titleMedium,
            XauxaMaterialTypography.titleSmall,
            XauxaMaterialTypography.bodyLarge,
            XauxaMaterialTypography.bodyMedium,
            XauxaMaterialTypography.bodySmall,
            XauxaMaterialTypography.labelLarge,
            XauxaMaterialTypography.labelMedium,
            XauxaMaterialTypography.labelSmall,
        )
        assertTrue(
            roles.all { it.fontFamily == FontFamily.Default },
            "todos los roles usan la familia de sistema",
        )
    }

    @Test
    fun line_heights_exceed_their_font_size() {
        // El interlineado de cada rol es mayor que su tamaño: un titular a
        // dos líneas nunca se solapa (§11 escalado).
        val roles = listOf(
            XauxaMaterialTypography.headlineLarge,
            XauxaMaterialTypography.headlineMedium,
            XauxaMaterialTypography.headlineSmall,
            XauxaMaterialTypography.titleLarge,
            XauxaMaterialTypography.titleMedium,
            XauxaMaterialTypography.titleSmall,
            XauxaMaterialTypography.bodyLarge,
            XauxaMaterialTypography.bodyMedium,
            XauxaMaterialTypography.bodySmall,
            XauxaMaterialTypography.labelLarge,
            XauxaMaterialTypography.labelMedium,
            XauxaMaterialTypography.labelSmall,
        )
        roles.forEach { style ->
            assertTrue(
                style.lineHeight.value > style.fontSize.value,
                "lineHeight ${style.lineHeight} <= fontSize ${style.fontSize} en un rol M3",
            )
        }
    }

    @Test
    fun tracking_follows_material_defaults() {
        // §12 V1.2: tracking por defecto de M3; nada de tracking amplio
        // propio en el cuerpo.
        assertEquals(0.5.sp, XauxaTracking.BodyLarge)
        assertEquals(0.1.sp, XauxaTracking.LabelLarge)
        assertEquals(XauxaTracking.BodyLarge, XauxaMaterialTypography.bodyLarge.letterSpacing)
        assertEquals(XauxaTracking.LabelLarge, XauxaMaterialTypography.labelLarge.letterSpacing)
    }

    @Test
    fun section_header_stays_in_the_label_scale() {
        // Encabezados de sección (14–16 sp).
        assertTrue(XauxaType.SectionHeader.value in 14f..16f)
    }
}
