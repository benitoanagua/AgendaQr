package com.agendaqr.core.ui.theme

import kotlin.test.Test
import kotlin.test.assertEquals

/** Semantic Polaris tokens are mapped to every Material 3 color slot. */
class AqThemeColorSchemeTest {

    @Test
    fun light_material_scheme_has_no_default_template_colors() {
        assertMapped(LightAmColorScheme, darkTheme = false)
    }

    @Test
    fun dark_material_scheme_has_no_default_template_colors() {
        assertMapped(DarkAmColorScheme, darkTheme = true)
    }

    private fun assertMapped(tokens: AqColorScheme, darkTheme: Boolean) {
        val material = tokens.asMaterialColorScheme(darkTheme)
        assertEquals(tokens.brandFill, material.primary)
        assertEquals(tokens.onBrandFill, material.onPrimary)
        assertEquals(tokens.emphasisSurface, material.primaryContainer)
        assertEquals(tokens.emphasisText, material.onPrimaryContainer)
        assertEquals(tokens.link, material.inversePrimary)
        assertEquals(tokens.emphasisFill, material.secondary)
        assertEquals(tokens.emphasisOnFill, material.onSecondary)
        assertEquals(tokens.surfaceSecondary, material.secondaryContainer)
        assertEquals(tokens.textPrimary, material.onSecondaryContainer)
        assertEquals(tokens.emphasisFill, material.tertiary)
        assertEquals(tokens.emphasisOnFill, material.onTertiary)
        assertEquals(tokens.infoContainer, material.tertiaryContainer)
        assertEquals(tokens.info, material.onTertiaryContainer)
        assertEquals(tokens.dangerFill, material.error)
        assertEquals(tokens.onDanger, material.onError)
        assertEquals(tokens.dangerContainer, material.errorContainer)
        assertEquals(tokens.danger, material.onErrorContainer)
        assertEquals(tokens.background, material.background)
        assertEquals(tokens.textPrimary, material.onBackground)
        assertEquals(tokens.surface, material.surface)
        assertEquals(tokens.textPrimary, material.onSurface)
        assertEquals(tokens.surfaceSecondary, material.surfaceVariant)
        assertEquals(tokens.textSecondary, material.onSurfaceVariant)
        assertEquals(tokens.brandFill, material.surfaceTint)
        assertEquals(tokens.inverseSurface, material.inverseSurface)
        assertEquals(tokens.inverseOnSurface, material.inverseOnSurface)
        assertEquals(tokens.functionalBorder, material.outline)
        assertEquals(tokens.border, material.outlineVariant)
        assertEquals(tokens.scrim, material.scrim)
        assertEquals(tokens.background, material.surfaceDim)
        assertEquals(tokens.surface, material.surfaceBright)
        assertEquals(tokens.background, material.surfaceContainerLowest)
        assertEquals(tokens.surfaceContainerLow, material.surfaceContainerLow)
        assertEquals(tokens.surfaceContainer, material.surfaceContainer)
        assertEquals(tokens.surfaceContainerHigh, material.surfaceContainerHigh)
        assertEquals(tokens.surfaceContainerHighest, material.surfaceContainerHighest)
    }
}
