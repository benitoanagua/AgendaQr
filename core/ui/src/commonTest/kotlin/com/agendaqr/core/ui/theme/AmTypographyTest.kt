package com.agendaqr.core.ui.theme

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.sp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Tipografía AgendaMobile (ADR-0013): familia de sistema en todos los
 * estilos, roles estándar de M3 desde tokens y sin fuente empaquetada.
 */
class AmTypographyTest {

    @Test
    fun sizes_keep_the_display_hierarchy() {
        // La jerarquía de encabezados/cuerpo se lee también por el tamaño;
        // todos en sp: el escalado de fuente del sistema se respeta.
        assertTrue(AmType.Display.value > AmType.HeadlineMedium.value)
        assertTrue(AmType.HeadlineMedium.value > AmType.Headline.value)
        assertTrue(AmType.Headline.value > AmType.TitleLarge.value)
        assertTrue(AmType.TitleLarge.value > AmType.Body.value)
        assertTrue(AmType.Body.value > AmType.Caption.value)
        assertTrue(AmType.Caption.value > AmType.CaptionSmall.value)
    }

    @Test
    fun ui_family_is_the_platform_default_sans() {
        // Roboto en Android, SF en iOS: delegar en el sistema evita
        // empaquetar una copia redundante y respeta al usuario.
        assertEquals(FontFamily.Default, AmType.FamilyUi)
    }

    @Test
    fun m3_roles_carry_the_canonical_sizes() {
        // El título de página (titleLarge) y el titular de auth
        // (headlineMedium) son los roles M3 estándar, en sp.
        assertEquals(22.sp, AmMaterialTypography.titleLarge.fontSize)
        assertEquals(28.sp, AmMaterialTypography.headlineMedium.fontSize)
        assertEquals(17.sp, AmMaterialTypography.bodyLarge.fontSize)
        assertEquals(14.sp, AmMaterialTypography.labelLarge.fontSize)
    }

    @Test
    fun m3_roles_use_the_system_family() {
        // Sin fuente empaquetada: todos los roles resuelven a la sans del
        // sistema.
        val roles = listOf(
            AmMaterialTypography.headlineLarge,
            AmMaterialTypography.headlineMedium,
            AmMaterialTypography.headlineSmall,
            AmMaterialTypography.titleLarge,
            AmMaterialTypography.titleMedium,
            AmMaterialTypography.titleSmall,
            AmMaterialTypography.bodyLarge,
            AmMaterialTypography.bodyMedium,
            AmMaterialTypography.bodySmall,
            AmMaterialTypography.labelLarge,
            AmMaterialTypography.labelMedium,
            AmMaterialTypography.labelSmall,
        )
        assertTrue(
            roles.all { it.fontFamily == FontFamily.Default },
            "todos los roles usan la familia de sistema",
        )
    }

    @Test
    fun line_heights_exceed_their_font_size() {
        // El interlineado de cada rol es mayor que su tamaño: un titular
        // a dos líneas nunca se solapa (escalado de fuente).
        val roles = listOf(
            AmMaterialTypography.headlineLarge,
            AmMaterialTypography.headlineMedium,
            AmMaterialTypography.headlineSmall,
            AmMaterialTypography.titleLarge,
            AmMaterialTypography.titleMedium,
            AmMaterialTypography.titleSmall,
            AmMaterialTypography.bodyLarge,
            AmMaterialTypography.bodyMedium,
            AmMaterialTypography.bodySmall,
            AmMaterialTypography.labelLarge,
            AmMaterialTypography.labelMedium,
            AmMaterialTypography.labelSmall,
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
        // Tracking por defecto de M3; nada de tracking amplio propio en
        // el cuerpo.
        assertEquals(0.5.sp, AmTracking.BodyLarge)
        assertEquals(0.1.sp, AmTracking.LabelLarge)
        assertEquals(AmTracking.BodyLarge, AmMaterialTypography.bodyLarge.letterSpacing)
        assertEquals(AmTracking.LabelLarge, AmMaterialTypography.labelLarge.letterSpacing)
    }

    @Test
    fun section_header_stays_in_the_label_scale() {
        // Encabezados de sección (título de sección, 14 sp Medium).
        assertTrue(AmType.SectionHeader.value in 14f..16f)
    }
}
