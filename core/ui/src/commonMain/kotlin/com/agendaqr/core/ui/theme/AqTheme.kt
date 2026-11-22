package com.agendaqr.core.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Surface
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier

internal fun AqColorScheme.asMaterialColorScheme(darkTheme: Boolean): ColorScheme {
    return if (darkTheme) {
        darkColorScheme(
            primary = brandFill,
            onPrimary = onBrandFill,
            primaryContainer = emphasisSurface,
            onPrimaryContainer = emphasisText,
            inversePrimary = link,
            secondary = emphasisFill,
            onSecondary = emphasisOnFill,
            secondaryContainer = surfaceSecondary,
            onSecondaryContainer = textPrimary,
            tertiary = emphasisFill,
            onTertiary = emphasisOnFill,
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
            surfaceTint = brandFill,
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
            primary = brandFill,
            onPrimary = onBrandFill,
            primaryContainer = emphasisSurface,
            onPrimaryContainer = emphasisText,
            inversePrimary = link,
            secondary = emphasisFill,
            onSecondary = emphasisOnFill,
            secondaryContainer = surfaceSecondary,
            onSecondaryContainer = textPrimary,
            tertiary = emphasisFill,
            onTertiary = emphasisOnFill,
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
            surfaceTint = brandFill,
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

/** Material 3 adapts types only; the semantic Polaris tokens remain authoritative. */
@Composable
fun AqTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val scheme = if (darkTheme) DarkAqColorScheme else LightAqColorScheme
    CompositionLocalProvider(
        LocalAqColorScheme provides scheme,
        LocalAqFontFamily provides rememberAqInterFontFamily(),
    ) {
        MaterialTheme(
            typography = AqMaterialTypography.withAqFontFamily(AqType.FamilyUi),
            shapes = Shapes(
                extraSmall = RoundedCornerShape(AqRadius.ExtraSmall),
                small = RoundedCornerShape(AqRadius.Small),
                medium = RoundedCornerShape(AqRadius.Medium),
                large = RoundedCornerShape(AqRadius.Large),
                extraLarge = RoundedCornerShape(AqRadius.ExtraLarge),
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
