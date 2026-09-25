package com.agendaqr.core.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Xauxa semantic tokens. The feature layer must consume only this semantic
 * facade; raw primitives stay private to the theme layer.
 */
internal object XauxaPrimitive {
    // Xauxa light scheme
    val lightBackground = Color(0xFFFFF7FF)
    val lightSurface = Color(0xFFFFF7FF)
    val lightSurfaceContainerLowest = Color(0xFFFFFFFF)
    val lightSurfaceContainerLow = Color(0xFFF9F1FA)
    val lightSurfaceContainer = Color(0xFFF3EBF4)
    val lightSurfaceContainerHigh = Color(0xFFEEE6EE)
    val lightSurfaceContainerHighest = Color(0xFFE8E0E9)
    val lightSurfaceVariant = Color(0xFFE9DFEE)
    val lightOutline = Color(0xFF7C7482)
    val lightOutlineVariant = Color(0xFFCDC3D2)
    val lightText = Color(0xFF1E1A20)
    val lightTextVariant = Color(0xFF4B4450)
    val lightPrimary = Color(0xFF4A1F7A)
    val lightOnPrimary = Color(0xFFFFFFFF)
    val lightPrimaryContainer = Color(0xFF623993)
    val lightOnPrimaryContainer = Color(0xFFD5B0FF)
    val lightSecondary = Color(0xFF68577C)
    val lightOnSecondary = Color(0xFFFFFFFF)
    val lightSecondaryContainer = Color(0xFFE8D1FD)
    val lightOnSecondaryContainer = Color(0xFF69587D)
    val lightTertiary = Color(0xFF671448)
    val lightOnTertiary = Color(0xFFFFFFFF)
    val lightTertiaryContainer = Color(0xFF842D60)
    val lightOnTertiaryContainer = Color(0xFFFFA4D1)
    val lightError = Color(0xFFBA1A1A)
    val lightOnError = Color(0xFFFFFFFF)
    val lightErrorContainer = Color(0xFFFFDAD6)
    val lightOnErrorContainer = Color(0xFF93000A)
    val lightInverseSurface = Color(0xFF332F35)
    val lightInverseOnSurface = Color(0xFFF6EEF7)
    val lightInversePrimary = Color(0xFFDAB9FF)

    // Xauxa dark scheme
    val darkBackground = Color(0xFF151218)
    val darkSurface = Color(0xFF151218)
    val darkSurfaceContainerLowest = Color(0xFF100D13)
    val darkSurfaceContainerLow = Color(0xFF1E1A20)
    val darkSurfaceContainer = Color(0xFF221E24)
    val darkSurfaceContainerHigh = Color(0xFF2C292F)
    val darkSurfaceContainerHighest = Color(0xFF37333A)
    val darkSurfaceVariant = Color(0xFF4B4450)
    val darkOutline = Color(0xFF968E9C)
    val darkOutlineVariant = Color(0xFF4B4450)
    val darkText = Color(0xFFE8E0E9)
    val darkTextVariant = Color(0xFFCDC3D2)
    val darkPrimary = Color(0xFFDAB9FF)
    val darkOnPrimary = Color(0xFF421673)
    val darkPrimaryContainer = Color(0xFF623993)
    val darkOnPrimaryContainer = Color(0xFFD5B0FF)
    val darkSecondary = Color(0xFFD4BEE9)
    val darkOnSecondary = Color(0xFF39294B)
    val darkSecondaryContainer = Color(0xFF524266)
    val darkOnSecondaryContainer = Color(0xFFC5B0DA)
    val darkTertiary = Color(0xFFFFAFD5)
    val darkOnTertiary = Color(0xFF5E0A41)
    val darkTertiaryContainer = Color(0xFF842D60)
    val darkOnTertiaryContainer = Color(0xFFFFA4D1)
    val darkError = Color(0xFFFFB4AB)
    val darkOnError = Color(0xFF690005)
    val darkErrorContainer = Color(0xFF93000A)
    val darkOnErrorContainer = Color(0xFFFFDAD6)
    val darkInverseSurface = Color(0xFFE8E0E9)
    val darkInverseOnSurface = Color(0xFF332F35)
    val darkInversePrimary = Color(0xFF734AA5)
}

data class XauxaColorScheme(
    val background: Color,
    val surface: Color,
    val surface2: Color,
    val surface3: Color,
    val surfaceVariant: Color,
    val border: Color,
    val borderVariant: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val brand: Color,
    val onBrand: Color,
    val brandContainer: Color,
    val onBrandContainer: Color,
    val secondary: Color,
    val onSecondary: Color,
    val secondaryContainer: Color,
    val onSecondaryContainer: Color,
    val tertiary: Color,
    val onTertiary: Color,
    val tertiaryContainer: Color,
    val onTertiaryContainer: Color,
    val danger: Color,
    val onDanger: Color,
    val dangerBg: Color,
    val onDangerBg: Color,
    val inverseSurface: Color,
    val inverseOnSurface: Color,
    val inverseBrand: Color,
    val focusRing: Color,
)

val LightXauxaColorScheme = XauxaColorScheme(
    background = XauxaPrimitive.lightBackground,
    surface = XauxaPrimitive.lightSurface,
    surface2 = XauxaPrimitive.lightSurfaceContainer,
    surface3 = XauxaPrimitive.lightSurfaceContainerHighest,
    surfaceVariant = XauxaPrimitive.lightSurfaceVariant,
    border = XauxaPrimitive.lightOutlineVariant,
    borderVariant = XauxaPrimitive.lightOutline,
    textPrimary = XauxaPrimitive.lightText,
    textSecondary = XauxaPrimitive.lightTextVariant,
    textTertiary = XauxaPrimitive.lightOutline,
    brand = XauxaPrimitive.lightPrimary,
    onBrand = XauxaPrimitive.lightOnPrimary,
    brandContainer = XauxaPrimitive.lightPrimaryContainer,
    onBrandContainer = XauxaPrimitive.lightOnPrimaryContainer,
    secondary = XauxaPrimitive.lightSecondary,
    onSecondary = XauxaPrimitive.lightOnSecondary,
    secondaryContainer = XauxaPrimitive.lightSecondaryContainer,
    onSecondaryContainer = XauxaPrimitive.lightOnSecondaryContainer,
    tertiary = XauxaPrimitive.lightTertiary,
    onTertiary = XauxaPrimitive.lightOnTertiary,
    tertiaryContainer = XauxaPrimitive.lightTertiaryContainer,
    onTertiaryContainer = XauxaPrimitive.lightOnTertiaryContainer,
    danger = XauxaPrimitive.lightError,
    onDanger = XauxaPrimitive.lightOnError,
    dangerBg = XauxaPrimitive.lightErrorContainer,
    onDangerBg = XauxaPrimitive.lightOnErrorContainer,
    inverseSurface = XauxaPrimitive.lightInverseSurface,
    inverseOnSurface = XauxaPrimitive.lightInverseOnSurface,
    inverseBrand = XauxaPrimitive.lightInversePrimary,
    focusRing = XauxaPrimitive.lightPrimary,
)

val DarkXauxaColorScheme = XauxaColorScheme(
    background = XauxaPrimitive.darkBackground,
    surface = XauxaPrimitive.darkSurface,
    surface2 = XauxaPrimitive.darkSurfaceContainer,
    surface3 = XauxaPrimitive.darkSurfaceContainerHighest,
    surfaceVariant = XauxaPrimitive.darkSurfaceVariant,
    border = XauxaPrimitive.darkOutlineVariant,
    borderVariant = XauxaPrimitive.darkOutline,
    textPrimary = XauxaPrimitive.darkText,
    textSecondary = XauxaPrimitive.darkTextVariant,
    textTertiary = XauxaPrimitive.darkOutline,
    brand = XauxaPrimitive.darkPrimary,
    onBrand = XauxaPrimitive.darkOnPrimary,
    brandContainer = XauxaPrimitive.darkPrimaryContainer,
    onBrandContainer = XauxaPrimitive.darkOnPrimaryContainer,
    secondary = XauxaPrimitive.darkSecondary,
    onSecondary = XauxaPrimitive.darkOnSecondary,
    secondaryContainer = XauxaPrimitive.darkSecondaryContainer,
    onSecondaryContainer = XauxaPrimitive.darkOnSecondaryContainer,
    tertiary = XauxaPrimitive.darkTertiary,
    onTertiary = XauxaPrimitive.darkOnTertiary,
    tertiaryContainer = XauxaPrimitive.darkTertiaryContainer,
    onTertiaryContainer = XauxaPrimitive.darkOnTertiaryContainer,
    danger = XauxaPrimitive.darkError,
    onDanger = XauxaPrimitive.darkOnError,
    dangerBg = XauxaPrimitive.darkErrorContainer,
    onDangerBg = XauxaPrimitive.darkOnErrorContainer,
    inverseSurface = XauxaPrimitive.darkInverseSurface,
    inverseOnSurface = XauxaPrimitive.darkInverseOnSurface,
    inverseBrand = XauxaPrimitive.darkInversePrimary,
    focusRing = XauxaPrimitive.darkPrimary,
)

val LocalXauxaColorScheme = compositionLocalOf { LightXauxaColorScheme }

object XauxaColor {
    val Background: Color @Composable get() = LocalXauxaColorScheme.current.background
    val Surface: Color @Composable get() = LocalXauxaColorScheme.current.surface
    val Surface2: Color @Composable get() = LocalXauxaColorScheme.current.surface2
    val Surface3: Color @Composable get() = LocalXauxaColorScheme.current.surface3
    val SurfaceVariant: Color @Composable get() = LocalXauxaColorScheme.current.surfaceVariant
    val Border: Color @Composable get() = LocalXauxaColorScheme.current.border
    val BorderVariant: Color @Composable get() = LocalXauxaColorScheme.current.borderVariant
    val TextPrimary: Color @Composable get() = LocalXauxaColorScheme.current.textPrimary
    val TextSecondary: Color @Composable get() = LocalXauxaColorScheme.current.textSecondary
    val TextTertiary: Color @Composable get() = LocalXauxaColorScheme.current.textTertiary
    val Brand: Color @Composable get() = LocalXauxaColorScheme.current.brand
    val OnBrand: Color @Composable get() = LocalXauxaColorScheme.current.onBrand
    val BrandContainer: Color @Composable get() = LocalXauxaColorScheme.current.brandContainer
    val OnBrandContainer: Color @Composable get() = LocalXauxaColorScheme.current.onBrandContainer
    val Secondary: Color @Composable get() = LocalXauxaColorScheme.current.secondary
    val OnSecondary: Color @Composable get() = LocalXauxaColorScheme.current.onSecondary
    val SecondaryContainer: Color @Composable get() = LocalXauxaColorScheme.current.secondaryContainer
    val OnSecondaryContainer: Color @Composable get() = LocalXauxaColorScheme.current.onSecondaryContainer
    val Tertiary: Color @Composable get() = LocalXauxaColorScheme.current.tertiary
    val OnTertiary: Color @Composable get() = LocalXauxaColorScheme.current.onTertiary
    val TertiaryContainer: Color @Composable get() = LocalXauxaColorScheme.current.tertiaryContainer
    val OnTertiaryContainer: Color @Composable get() = LocalXauxaColorScheme.current.onTertiaryContainer
    val Danger: Color @Composable get() = LocalXauxaColorScheme.current.danger
    val OnDanger: Color @Composable get() = LocalXauxaColorScheme.current.onDanger
    val DangerBg: Color @Composable get() = LocalXauxaColorScheme.current.dangerBg
    val OnDangerBg: Color @Composable get() = LocalXauxaColorScheme.current.onDangerBg
    val InverseSurface: Color @Composable get() = LocalXauxaColorScheme.current.inverseSurface
    val InverseOnSurface: Color @Composable get() = LocalXauxaColorScheme.current.inverseOnSurface
    val InverseBrand: Color @Composable get() = LocalXauxaColorScheme.current.inverseBrand
    val FocusRing: Color @Composable get() = LocalXauxaColorScheme.current.focusRing
}

object XauxaSpacing {
    val None = 0.dp
    val Xs = 4.dp
    val Sm = 8.dp
    val Md = 12.dp
    val Lg = 16.dp
    val Xl = 20.dp
    val Xxl = 24.dp
    val Xxxl = 32.dp
    val Huge = 40.dp
}

object XauxaMetrics {
    val Border = 1.dp
    val BorderStrong = 2.dp
    val Focus = 2.dp
    val ControlMinSize = 48.dp
    val ContentMaxWidth = 720.dp
    val QrPreviewSize = 240.dp
    val FavoriteIndicatorSize = 24.dp
    val BreakpointCompact = 480.dp
    val BreakpointMedium = 640.dp
}

object XauxaType {
    val Display: TextUnit = 32.sp
    val Headline: TextUnit = 24.sp
    val Title: TextUnit = 20.sp
    val Body: TextUnit = 16.sp
    val Label: TextUnit = 14.sp
    val Caption: TextUnit = 12.sp
    val LetterSpacingWide: TextUnit = 0.5.sp
    val FamilyUi: FontFamily = FontFamily.Default
    val FamilyMono: FontFamily = FontFamily.Monospace
}

object XauxaMotion {
    const val DurationShortMs = 150
    const val DurationMediumMs = 300
    const val DurationLongMs = 600
    const val EasingStandard = "cubic-bezier(0.4, 0, 0.2, 1)"
    const val EasingEmphasized = "cubic-bezier(0.2, 0, 0, 1)"
    const val EasingDecelerate = "cubic-bezier(0, 0, 0.2, 1)"
    object Easings {
        val Standard: Easing = CubicBezierEasing(0.4f, 0f, 0.2f, 1f)
        val Emphasized: Easing = CubicBezierEasing(0.2f, 0f, 0f, 1f)
        val Decelerate: Easing = CubicBezierEasing(0f, 0f, 0.2f, 1f)
    }
}

fun Dp.xauxaBorder() = this
