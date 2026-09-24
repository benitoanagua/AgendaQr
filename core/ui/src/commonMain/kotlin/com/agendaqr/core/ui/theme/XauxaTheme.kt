package com.agendaqr.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider

@Composable
fun AgendaQrTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    val scheme = if (darkTheme) DarkXauxaColorScheme else LightXauxaColorScheme
    CompositionLocalProvider(LocalXauxaColorScheme provides scheme) {
        MaterialTheme(
            colorScheme = if (darkTheme) {
                darkColorScheme(
                    primary = scheme.brandAccent,
                    onPrimary = scheme.textPrimary,
                    secondary = scheme.brand,
                    background = scheme.background,
                    onBackground = scheme.textPrimary,
                    surface = scheme.surface,
                    onSurface = scheme.textPrimary,
                    surfaceVariant = scheme.surface2,
                    onSurfaceVariant = scheme.textSecondary,
                    outline = scheme.border,
                    error = scheme.danger,
                    onError = scheme.white,
                )
            } else {
                lightColorScheme(
                    primary = scheme.brand,
                    onPrimary = scheme.onBrand,
                    secondary = scheme.brandAccent,
                    onSecondary = scheme.textPrimary,
                    background = scheme.background,
                    onBackground = scheme.textPrimary,
                    surface = scheme.surface,
                    onSurface = scheme.textPrimary,
                    surfaceVariant = scheme.surface2,
                    onSurfaceVariant = scheme.textSecondary,
                    outline = scheme.border,
                    error = scheme.danger,
                    onError = scheme.white,
                )
            },
            content = content,
        )
    }
}
