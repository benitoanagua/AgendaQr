package com.agendaqr.core.ui.theme

import kotlin.test.Test
import kotlin.test.assertEquals

/** `tdesign_flutter@0.2.7` tokens are mapped to every Material 3 color slot. */
class AmThemeColorSchemeTest {

    @Test
    fun light_material_scheme_has_no_default_template_colors() {
        assertMapped(LightAmColorScheme, darkTheme = false)
    }

    @Test
    fun dark_material_scheme_has_no_default_template_colors() {
        assertMapped(DarkAmColorScheme, darkTheme = true)
    }

    private fun assertMapped(tokens: AmColorScheme, darkTheme: Boolean) {
        val material = tokens.asMaterialColorScheme(darkTheme)
        assertEquals(tokens.accent, material.primary)
        assertEquals(tokens.onAccent, material.onPrimary)
        assertEquals(tokens.accentContainer, material.primaryContainer)
        assertEquals(tokens.onAccentContainer, material.onPrimaryContainer)
        assertEquals(tokens.accentText, material.inversePrimary)
        assertEquals(tokens.accent, material.secondary)
        assertEquals(tokens.onAccent, material.onSecondary)
        assertEquals(tokens.surfaceSecondary, material.secondaryContainer)
        assertEquals(tokens.textPrimary, material.onSecondaryContainer)
        assertEquals(tokens.accent, material.tertiary)
        assertEquals(tokens.onAccent, material.onTertiary)
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
        assertEquals(tokens.accent, material.surfaceTint)
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
