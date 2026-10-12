package com.agendaqr.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier

/**
 * Tema AgendaMobile (ADR-0013): la autoridad visual es [AmColorScheme];
 * Material 3 es la implementación. El esquema de M3 se deriva COMPLETO del
 * esquema semántico — incluidos los contenedores — para que ningún
 * componente herede la plantilla base de Material. Los componentes
 * envolventes heredan `typography` y `shapes`.
 */
@Composable
fun AmTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val scheme = if (darkTheme) DarkAmColorScheme else LightAmColorScheme
    CompositionLocalProvider(
        LocalAmColorScheme provides scheme,
    ) {
        MaterialTheme(
            typography = AmMaterialTypography,
            shapes = Shapes(
                extraSmall = AmShapes.ExtraSmall,
                small = AmShapes.Small,
                medium = AmShapes.Medium,
                large = AmShapes.Large,
                extraLarge = AmShapes.ExtraLarge,
            ),
            colorScheme = if (darkTheme) {
                darkColorScheme(
                    primary = scheme.accent,
                    onPrimary = scheme.onAccent,
                    primaryContainer = scheme.accentContainer,
                    onPrimaryContainer = scheme.onAccentContainer,
                    // AgendaMobile tiene UN acento; los slots M3 restantes
                    // se cubren con roles propios (info), no con una
                    // segunda marca.
                    secondary = scheme.accent,
                    onSecondary = scheme.onAccent,
                    secondaryContainer = scheme.surfaceSecondary,
                    onSecondaryContainer = scheme.textPrimary,
                    tertiary = scheme.info,
                    onTertiary = scheme.onAccent,
                    tertiaryContainer = scheme.infoContainer,
                    onTertiaryContainer = scheme.info,
                    error = scheme.danger,
                    onError = scheme.onDanger,
                    errorContainer = scheme.dangerContainer,
                    onErrorContainer = scheme.danger,
                    background = scheme.background,
                    onBackground = scheme.textPrimary,
                    surface = scheme.surface,
                    onSurface = scheme.textPrimary,
                    surfaceVariant = scheme.surfaceSecondary,
                    onSurfaceVariant = scheme.textSecondary,
                    surfaceContainerLowest = scheme.background,
                    surfaceContainerLow = scheme.surfaceContainerLow,
                    surfaceContainer = scheme.surfaceContainer,
                    surfaceContainerHigh = scheme.surfaceContainerHigh,
                    surfaceContainerHighest = scheme.surfaceContainerHighest,
                    outline = scheme.borderControl,
                    outlineVariant = scheme.border,
                    inverseSurface = scheme.inverseSurface,
                    inverseOnSurface = scheme.inverseOnSurface,
                    inversePrimary = scheme.inverseAccent,
                )
            } else {
                lightColorScheme(
                    primary = scheme.accent,
                    onPrimary = scheme.onAccent,
                    primaryContainer = scheme.accentContainer,
                    onPrimaryContainer = scheme.onAccentContainer,
                    secondary = scheme.accent,
                    onSecondary = scheme.onAccent,
                    secondaryContainer = scheme.surfaceSecondary,
                    onSecondaryContainer = scheme.textPrimary,
                    tertiary = scheme.info,
                    onTertiary = scheme.onAccent,
                    tertiaryContainer = scheme.infoContainer,
                    onTertiaryContainer = scheme.info,
                    error = scheme.danger,
                    onError = scheme.onDanger,
                    errorContainer = scheme.dangerContainer,
                    onErrorContainer = scheme.danger,
                    background = scheme.background,
                    onBackground = scheme.textPrimary,
                    surface = scheme.surface,
                    onSurface = scheme.textPrimary,
                    surfaceVariant = scheme.surfaceSecondary,
                    onSurfaceVariant = scheme.textSecondary,
                    surfaceContainerLowest = scheme.background,
                    surfaceContainerLow = scheme.surfaceContainerLow,
                    surfaceContainer = scheme.surfaceContainer,
                    surfaceContainerHigh = scheme.surfaceContainerHigh,
                    surfaceContainerHighest = scheme.surfaceContainerHighest,
                    outline = scheme.borderControl,
                    outlineVariant = scheme.border,
                    inverseSurface = scheme.inverseSurface,
                    inverseOnSurface = scheme.inverseOnSurface,
                    inversePrimary = scheme.inverseAccent,
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
