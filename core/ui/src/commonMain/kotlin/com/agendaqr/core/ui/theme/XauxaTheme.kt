package com.agendaqr.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp

private val LightColors = lightColorScheme(
    primary = XauxaColor.Brand,
    onPrimary = XauxaColor.OnBrand,
    secondary = XauxaColor.BrandAccent,
    onSecondary = XauxaColor.TextPrimary,
    background = XauxaColor.Background,
    onBackground = XauxaColor.TextPrimary,
    surface = XauxaColor.Surface,
    onSurface = XauxaColor.TextPrimary,
    surfaceVariant = XauxaColor.Surface2,
    onSurfaceVariant = XauxaColor.TextSecondary,
    outline = XauxaColor.Border,
    error = XauxaColor.Danger,
)

private val DarkColors = darkColorScheme(
    primary = XauxaColor.BrandAccent,
    onPrimary = XauxaColor.TextPrimary,
    background = XauxaColor.TextPrimary,
    onBackground = XauxaColor.Surface,
    surface = XauxaColor.TextPrimary,
    onSurface = XauxaColor.Surface,
    outline = XauxaColor.TextTertiary,
    error = XauxaColor.Danger,
)

@Composable
fun AgendaQrTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content,
    )
}
