package com.agendaqr.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun XauxaTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    val scheme = if (darkTheme) DarkXauxaColorScheme else LightXauxaColorScheme
    CompositionLocalProvider(LocalXauxaColorScheme provides scheme) {
        MaterialTheme(
            colorScheme = if (darkTheme) {
                darkColorScheme(
                    primary = scheme.brand,
                    onPrimary = scheme.onBrand,
                    primaryContainer = scheme.brandContainer,
                    onPrimaryContainer = scheme.onBrandContainer,
                    secondary = scheme.secondary,
                    onSecondary = scheme.onSecondary,
                    secondaryContainer = scheme.secondaryContainer,
                    onSecondaryContainer = scheme.onSecondaryContainer,
                    tertiary = scheme.tertiary,
                    onTertiary = scheme.onTertiary,
                    tertiaryContainer = scheme.tertiaryContainer,
                    onTertiaryContainer = scheme.onTertiaryContainer,
                    error = scheme.danger,
                    onError = scheme.onDanger,
                    errorContainer = scheme.dangerBg,
                    onErrorContainer = scheme.onDangerBg,
                    background = scheme.background,
                    onBackground = scheme.textPrimary,
                    surface = scheme.surface,
                    onSurface = scheme.textPrimary,
                    surfaceVariant = scheme.surfaceVariant,
                    onSurfaceVariant = scheme.textSecondary,
                    outline = scheme.borderVariant,
                    outlineVariant = scheme.border,
                    inverseSurface = scheme.inverseSurface,
                    inverseOnSurface = scheme.inverseOnSurface,
                    inversePrimary = scheme.inverseBrand,
                )
            } else {
                lightColorScheme(
                    primary = scheme.brand,
                    onPrimary = scheme.onBrand,
                    primaryContainer = scheme.brandContainer,
                    onPrimaryContainer = scheme.onBrandContainer,
                    secondary = scheme.secondary,
                    onSecondary = scheme.onSecondary,
                    secondaryContainer = scheme.secondaryContainer,
                    onSecondaryContainer = scheme.onSecondaryContainer,
                    tertiary = scheme.tertiary,
                    onTertiary = scheme.onTertiary,
                    tertiaryContainer = scheme.tertiaryContainer,
                    onTertiaryContainer = scheme.onTertiaryContainer,
                    error = scheme.danger,
                    onError = scheme.onDanger,
                    errorContainer = scheme.dangerBg,
                    onErrorContainer = scheme.onDangerBg,
                    background = scheme.background,
                    onBackground = scheme.textPrimary,
                    surface = scheme.surface,
                    onSurface = scheme.textPrimary,
                    surfaceVariant = scheme.surfaceVariant,
                    onSurfaceVariant = scheme.textSecondary,
                    outline = scheme.borderVariant,
                    outlineVariant = scheme.border,
                    inverseSurface = scheme.inverseSurface,
                    inverseOnSurface = scheme.inverseOnSurface,
                    inversePrimary = scheme.inverseBrand,
                )
            },
            content = content,
        )
    }
}
