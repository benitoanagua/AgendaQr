package com.agendaqr.core.ui.theme

import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * Tipografía (contrato §5, ADR-0014): familia de sistema en todos los
 * estilos, roles estándar de M3 desde tokens y sin fuente empaquetada.
 */
class AmTypographyTest {

    @Test
    fun sizes_keep_the_display_hierarchy() {
        // La jerarquía de encabezados/cuerpo se lee también por el tamaño;
        // todos en sp: el escalado de fuente del sistema se respeta.
        assertTrue(AmType.Display.value > AmType.Headline.value)
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
        // Título de pantalla/H1 (titleLarge, 20), titular destacado
        // (headlineMedium/Headline, 24), cuerpo (bodyLarge, 14) y etiqueta
        // (labelLarge, 14): escala del Anexo A (ADR-0022), en sp.
        assertEquals(20.sp, AmMaterialTypography.titleLarge.fontSize)
        assertEquals(24.sp, AmMaterialTypography.headlineMedium.fontSize)
        assertEquals(14.sp, AmMaterialTypography.bodyLarge.fontSize)
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
    fun tracking_is_neutral_by_default() {
        // Sin tracking amplio como característica general (brief de
        // rediseño): los roles usan espaciado neutro y los estilos del
        // sistema lo consumen desde los tokens.
        listOf(
            AmTracking.TitleMedium, AmTracking.TitleSmall,
            AmTracking.BodyLarge, AmTracking.BodyMedium, AmTracking.BodySmall,
            AmTracking.LabelLarge, AmTracking.LabelMedium, AmTracking.LabelSmall,
        ).forEach { assertEquals(0.sp, it) }
        assertEquals(AmTracking.BodyLarge, AmMaterialTypography.bodyLarge.letterSpacing)
        assertEquals(AmTracking.LabelLarge, AmMaterialTypography.labelLarge.letterSpacing)
    }

    @Test
    fun section_header_stays_in_the_label_scale() {
        // Encabezado de sección y título de fila (H2 16/600), etiqueta/botón
        // 14, metadato/caption 12, importe hero 30 y tags 11 (Anexo A).
        assertEquals(16f, AmType.SectionHeader.value)
        assertEquals(16f, AmTextStyles.SectionHeader.fontSize.value)
        assertEquals(16f, AmTextStyles.RowTitle.fontSize.value)
        assertEquals(14f, AmTextStyles.ButtonLabel.fontSize.value)
        assertEquals(12f, AmTextStyles.BodySmall.fontSize.value)
        assertEquals(30f, AmTextStyles.Amount.fontSize.value)
        assertEquals(14f, AmTextStyles.AmountSymbol.fontSize.value)
        assertEquals(11f, AmTextStyles.Tag.fontSize.value)
    }

    @Test
    fun weights_follow_the_reference_with_documented_exceptions() {
        // Los títulos H2 (fila/sección) y la etiqueta de acción usan
        // semi-bold (600) del Anexo A; el importe es extra-bold (800) con
        // símbolo bold (700). Tag y texto de apoyo se quedan en el peso
        // regular del sistema (null = regular).
        assertNull(AmTextStyles.Tag.fontWeight)
        assertNull(AmTextStyles.Support.fontWeight)
        assertEquals(FontWeight.SemiBold, AmTextStyles.RowTitle.fontWeight)
        assertEquals(FontWeight.SemiBold, AmTextStyles.SectionHeader.fontWeight)
        assertEquals(FontWeight.SemiBold, AmTextStyles.ButtonLabel.fontWeight)
        assertEquals(FontWeight.ExtraBold, AmTextStyles.Amount.fontWeight)
        assertEquals(FontWeight.Bold, AmTextStyles.AmountSymbol.fontWeight)
    }
}
