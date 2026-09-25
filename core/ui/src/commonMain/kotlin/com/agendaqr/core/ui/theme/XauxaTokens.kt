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

internal object XauxaPrimitive {
    val lightBackground = Color(0xFFFFF7FF)
    val lightSurface = Color(0xFFFFFFFF)
    val lightSurface2 = Color(0xFFF3EBF4)
    val lightSurface3 = Color(0xFFE8E0E9)
    val lightBorder = Color(0xFFCDC3D2)
    val lightText1 = Color(0xFF1E1A20)
    val lightText2 = Color(0xFF4B4450)
    val lightText3 = Color(0xFF7C7482)

    val darkBackground = Color(0xFF151218)
    val darkSurface = Color(0xFF17171B)
    val darkSurface2 = Color(0xFF221E24)
    val darkSurface3 = Color(0xFF37333A)
    val darkBorder = Color(0xFF968E9C)
    val darkText1 = Color(0xFFE8E0E9)
    val darkText2 = Color(0xFFCDC3D2)
    val darkText3 = Color(0xFF968E9C)

    val teal60 = Color(0xFF4A1F7A)
    val teal30 = Color(0xFF623993)
    val white = Color(0xFFFFFFFF)

    val green50 = Color(0xFF2E7D32)
    val green10 = Color(0xFFE8F5E9)
    val red60 = Color(0xFFBA1A1A)
    val red10 = Color(0xFFFFDAD6)
    val amber60 = Color(0xFF68577C)
    val amber10 = Color(0xFFE8D1FD)
    val blue55 = Color(0xFF671448)
    val blue10 = Color(0xFFFFA4D1)

    val darkGreen10 = Color(0xFF18381B)
    val darkRed10 = Color(0xFF93000A)
    val darkAmber10 = Color(0xFF524266)
    val darkBlue10 = Color(0xFF842D60)
}

data class XauxaColorScheme(
    val background: Color,
    val surface: Color,
    val surface2: Color,
    val surface3: Color,
    val border: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val brand: Color,
    val brandAccent: Color,
    val onBrand: Color,
    val white: Color,
    val success: Color,
    val successBg: Color,
    val danger: Color,
    val dangerBg: Color,
    val warning: Color,
    val warningBg: Color,
    val info: Color,
    val infoBg: Color,
    val focusRing: Color,
)

val LightXauxaColorScheme = XauxaColorScheme(
    background = XauxaPrimitive.lightBackground,
    surface = XauxaPrimitive.lightSurface,
    surface2 = XauxaPrimitive.lightSurface2,
    surface3 = XauxaPrimitive.lightSurface3,
    border = XauxaPrimitive.lightBorder,
    textPrimary = XauxaPrimitive.lightText1,
    textSecondary = XauxaPrimitive.lightText2,
    textTertiary = XauxaPrimitive.lightText3,
    brand = XauxaPrimitive.teal60,
    brandAccent = XauxaPrimitive.teal30,
    onBrand = XauxaPrimitive.white,
    white = XauxaPrimitive.white,
    success = XauxaPrimitive.green50,
    successBg = XauxaPrimitive.green10,
    danger = XauxaPrimitive.red60,
    dangerBg = XauxaPrimitive.red10,
    warning = XauxaPrimitive.amber60,
    warningBg = XauxaPrimitive.amber10,
    info = XauxaPrimitive.blue55,
    infoBg = XauxaPrimitive.blue10,
    focusRing = XauxaPrimitive.teal30,
)

val DarkXauxaColorScheme = XauxaColorScheme(
    background = XauxaPrimitive.darkBackground,
    surface = XauxaPrimitive.darkSurface,
    surface2 = XauxaPrimitive.darkSurface2,
    surface3 = XauxaPrimitive.darkSurface3,
    border = XauxaPrimitive.darkBorder,
    textPrimary = XauxaPrimitive.darkText1,
    textSecondary = XauxaPrimitive.darkText2,
    textTertiary = XauxaPrimitive.darkText3,
    brand = XauxaPrimitive.teal60,
    brandAccent = XauxaPrimitive.teal30,
    onBrand = XauxaPrimitive.white,
    white = XauxaPrimitive.white,
    success = XauxaPrimitive.green50,
    successBg = XauxaPrimitive.darkGreen10,
    danger = XauxaPrimitive.red60,
    dangerBg = XauxaPrimitive.darkRed10,
    warning = XauxaPrimitive.amber60,
    warningBg = XauxaPrimitive.darkAmber10,
    info = XauxaPrimitive.blue55,
    infoBg = XauxaPrimitive.darkBlue10,
    focusRing = XauxaPrimitive.teal30,
)

val LocalXauxaColorScheme = compositionLocalOf { LightXauxaColorScheme }

object XauxaColor {
    val Background: Color @Composable get() = LocalXauxaColorScheme.current.background
    val Surface: Color @Composable get() = LocalXauxaColorScheme.current.surface
    val Surface2: Color @Composable get() = LocalXauxaColorScheme.current.surface2
    val Surface3: Color @Composable get() = LocalXauxaColorScheme.current.surface3
    val Border: Color @Composable get() = LocalXauxaColorScheme.current.border
    val TextPrimary: Color @Composable get() = LocalXauxaColorScheme.current.textPrimary
    val TextSecondary: Color @Composable get() = LocalXauxaColorScheme.current.textSecondary
    val TextTertiary: Color @Composable get() = LocalXauxaColorScheme.current.textTertiary
    val Brand: Color @Composable get() = LocalXauxaColorScheme.current.brand
    val BrandAccent: Color @Composable get() = LocalXauxaColorScheme.current.brandAccent
    val OnBrand: Color @Composable get() = LocalXauxaColorScheme.current.onBrand
    val White: Color @Composable get() = LocalXauxaColorScheme.current.white
    val Success: Color @Composable get() = LocalXauxaColorScheme.current.success
    val SuccessBg: Color @Composable get() = LocalXauxaColorScheme.current.successBg
    val Danger: Color @Composable get() = LocalXauxaColorScheme.current.danger
    val DangerBg: Color @Composable get() = LocalXauxaColorScheme.current.dangerBg
    val Warning: Color @Composable get() = LocalXauxaColorScheme.current.warning
    val WarningBg: Color @Composable get() = LocalXauxaColorScheme.current.warningBg
    val Info: Color @Composable get() = LocalXauxaColorScheme.current.info
    val InfoBg: Color @Composable get() = LocalXauxaColorScheme.current.infoBg
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
