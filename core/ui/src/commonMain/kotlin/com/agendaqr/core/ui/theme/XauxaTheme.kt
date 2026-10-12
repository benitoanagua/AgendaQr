package com.agendaqr.core.ui.theme

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.ui.Modifier

/**
 * Tema Xauxa (spec §12 V1.2, ADR-0012): Material 3 con el esquema de color
 * derivado de [XauxaColorScheme], la tipografía y las formas definidas como
 * tokens. La indicación de pulsación es la estándar de M3 (ripple del tema);
 * los componentes heredan `typography` y `shapes` de `MaterialTheme`.
 */
@Composable
fun XauxaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val scheme = if (darkTheme) DarkXauxaColorScheme else LightXauxaColorScheme
    CompositionLocalProvider(
        LocalXauxaColorScheme provides scheme,
    ) {
        MaterialTheme(
            typography = XauxaMaterialTypography,
            shapes = Shapes(
                extraSmall = XauxaShapes.ExtraSmall,
                small = XauxaShapes.Small,
                medium = XauxaShapes.Medium,
                large = XauxaShapes.Large,
                extraLarge = XauxaShapes.ExtraLarge,
            ),
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
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = scheme.background,
                content = content,
            )
        }
    }
}
