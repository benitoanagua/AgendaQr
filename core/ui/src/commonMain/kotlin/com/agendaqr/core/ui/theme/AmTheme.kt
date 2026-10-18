package com.agendaqr.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier

internal fun AmColorScheme.asMaterialColorScheme(darkTheme: Boolean): ColorScheme {
    return if (darkTheme) {
        darkColorScheme(
            primary = accent,
            onPrimary = onAccent,
            primaryContainer = accentContainer,
            onPrimaryContainer = onAccentContainer,
            inversePrimary = accentText,
            secondary = accent,
            onSecondary = onAccent,
            secondaryContainer = surfaceSecondary,
            onSecondaryContainer = textPrimary,
            tertiary = accent,
            onTertiary = onAccent,
            tertiaryContainer = infoContainer,
            onTertiaryContainer = info,
            error = dangerFill,
            onError = onDanger,
            errorContainer = dangerContainer,
            onErrorContainer = danger,
            background = background,
            onBackground = textPrimary,
            surface = surface,
            onSurface = textPrimary,
            surfaceVariant = surfaceSecondary,
            onSurfaceVariant = textSecondary,
            surfaceTint = accent,
            inverseSurface = inverseSurface,
            inverseOnSurface = inverseOnSurface,
            outline = functionalBorder,
            outlineVariant = border,
            scrim = scrim,
            surfaceDim = background,
            surfaceBright = surface,
            surfaceContainerLowest = background,
            surfaceContainerLow = surfaceContainerLow,
            surfaceContainer = surfaceContainer,
            surfaceContainerHigh = surfaceContainerHigh,
            surfaceContainerHighest = surfaceContainerHighest,
        )
    } else {
        lightColorScheme(
            primary = accent,
            onPrimary = onAccent,
            primaryContainer = accentContainer,
            onPrimaryContainer = onAccentContainer,
            inversePrimary = accentText,
            secondary = accent,
            onSecondary = onAccent,
            secondaryContainer = surfaceSecondary,
            onSecondaryContainer = textPrimary,
            tertiary = accent,
            onTertiary = onAccent,
            tertiaryContainer = infoContainer,
            onTertiaryContainer = info,
            error = dangerFill,
            onError = onDanger,
            errorContainer = dangerContainer,
            onErrorContainer = danger,
            background = background,
            onBackground = textPrimary,
            surface = surface,
            onSurface = textPrimary,
            surfaceVariant = surfaceSecondary,
            onSurfaceVariant = textSecondary,
            surfaceTint = accent,
            inverseSurface = inverseSurface,
            inverseOnSurface = inverseOnSurface,
            outline = functionalBorder,
            outlineVariant = border,
            scrim = scrim,
            surfaceDim = background,
            surfaceBright = surface,
            surfaceContainerLowest = background,
            surfaceContainerLow = surfaceContainerLow,
            surfaceContainer = surfaceContainer,
            surfaceContainerHigh = surfaceContainerHigh,
            surfaceContainerHighest = surfaceContainerHighest,
        )
    }
}

/** Material 3 is an adapter; the semantic TDesign tokens remain authoritative. */
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
            colorScheme = scheme.asMaterialColorScheme(darkTheme),
        ) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = scheme.background,
                content = content,
            )
        }
    }
}
