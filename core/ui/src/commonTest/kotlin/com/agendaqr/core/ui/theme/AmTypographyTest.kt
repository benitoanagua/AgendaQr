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
        // Título de pantalla (titleLarge, 18), titular destacado
        // (headlineSmall/Headline, 24), cuerpo (bodyLarge, 17) y etiqueta
        // (labelLarge, 15): escala de la referencia, en sp.
        assertEquals(18.sp, AmMaterialTypography.titleLarge.fontSize)
        assertEquals(24.sp, AmMaterialTypography.headlineMedium.fontSize)
        assertEquals(17.sp, AmMaterialTypography.bodyLarge.fontSize)
        assertEquals(15.sp, AmMaterialTypography.labelLarge.fontSize)
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
        // Encabezados de sección (List header de la referencia: 15 sp
        // normal), título y etiqueta de botón en el cuerpo 17, metadato
        // 13, importe 26 y tags 11.
        assertEquals(15f, AmType.SectionHeader.value)
        assertEquals(15f, AmTextStyles.SectionHeader.fontSize.value)
        assertEquals(17f, AmTextStyles.RowTitle.fontSize.value)
        assertEquals(17f, AmTextStyles.ButtonLabel.fontSize.value)
        assertEquals(13f, AmTextStyles.BodySmall.fontSize.value)
        assertEquals(26f, AmTextStyles.Amount.fontSize.value)
        assertEquals(11f, AmTextStyles.Tag.fontSize.value)
    }

    @Test
    fun weights_follow_the_reference_with_documented_exceptions() {
        // Pesos normal en toda la escala salvo las dos desviaciones
        // documentadas: etiqueta de botón Medium y título de diálogo bold
        // (este último lo fija AmDialog con FontWeight.Bold). Los roles
        // "normales" no fijan peso (null = regular del sistema).
        assertNull(AmTextStyles.RowTitle.fontWeight)
        assertNull(AmTextStyles.Tag.fontWeight)
        assertNull(AmTextStyles.SectionHeader.fontWeight)
        assertNull(AmTextStyles.Support.fontWeight)
        assertEquals(FontWeight.Medium, AmTextStyles.ButtonLabel.fontWeight)
        assertEquals(FontWeight.SemiBold, AmTextStyles.Amount.fontWeight)
    }
}
