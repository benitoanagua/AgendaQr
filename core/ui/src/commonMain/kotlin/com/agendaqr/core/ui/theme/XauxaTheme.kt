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

// Fase 4: la red de seguridad de shapes consume el token plano de la
// capa de tokens (XauxaShapeFlat); RoundedCornerShape no aparece aquí.

@Composable
fun XauxaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val scheme = if (darkTheme) DarkXauxaColorScheme else LightXauxaColorScheme
    CompositionLocalProvider(LocalXauxaColorScheme provides scheme) {
        MaterialTheme(
            // Fase 4 (auditoría): red de seguridad — TODO componente M3 que
            // no pase shape explícita hereda radio 0 (invariante Metro). El
            // gate permite RoundedCornerShape SOLO en este archivo si algún
            // día se necesita; hoy ni se usa.
            shapes = Shapes(
                extraSmall = XauxaShapeFlat,
                small = XauxaShapeFlat,
                medium = XauxaShapeFlat,
                large = XauxaShapeFlat,
                extraLarge = XauxaShapeFlat,
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
                modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                color = scheme.background,
                content = content,
            )
        }
    }
}
