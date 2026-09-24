package com.agendaqr.core.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Xauxa Design System semantic tokens consumed by Agenda QR.
 * Product code must consume semantic tokens, never raw palette values.
 */
object XauxaColor {
    val Background = Color(0xFFF8FAFA)
    val Surface = Color(0xFFFFFFFF)
    val Surface2 = Color(0xFFF0F4F3)
    val Border = Color(0xFFD7DEDC)
    val TextPrimary = Color(0xFF17201E)
    val TextSecondary = Color(0xFF4E5B57)
    val TextTertiary = Color(0xFF6C7773)
    val Brand = Color(0xFF0E7D6E)
    val BrandAccent = Color(0xFF7FD9C9)
    val OnBrand = Color.White
    val White = Color.White
    val Success = Color(0xFF2E7D5B)
    val Danger = Color(0xFFB3261E)
    val Warning = Color(0xFF8A5A00)
    val Info = Color(0xFF245E9B)
    /**
     * Contenedores semánticos de estado (mezcla al 10% sobre superficie clara).
     * Xauxa v13 los define como success-bg/danger-bg/warning-bg/info-bg; se
     * fijan aquí para que el producto nunca derive alfas manualmente.
     */
    val SuccessBg = Color(0xFFEAF2EF)
    val DangerBg = Color(0xFFF7E9E8)
    val WarningBg = Color(0xFFF3EEE6)
    val InfoBg = Color(0xFFE9EFF5)
    /**
     * Anillo de foco visible (invariante 10). Xauxa lo resuelve como
     * focus.ring-color -> brand-accent del producto (AgendaQr: teal30).
     */
    val FocusRing = Color(0xFF7FD9C9)
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
    val Focus = 2.dp
    val ControlMinSize = 48.dp
    val ContentMaxWidth = 720.dp
    val QrPreviewSize = 240.dp
    val FavoriteIndicatorSize = 24.dp

    /**
     * Adaptive breakpoints documented by Xauxa (§07: 480px and 640px are the
     * behavior-change widths of the design system). Product and lab code must
     * consume these tokens instead of raw dp values.
     */
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
}

/**
 * Tokens de movimiento de Xauxa (duraciones y curvas). Independientes del
 * producto, como Motion.kt de la referencia. El movimiento es siempre con
 * propósito: sin loops decorativos; respetar prefers-reduced-motion
 * (en Compose: deshabilitar animaciones cuando el sistema lo indique).
 */
object XauxaMotion {
    const val DurationShortMs = 150
    const val DurationMediumMs = 300
    const val DurationLongMs = 600
    const val EasingStandard = "cubic-bezier(0.4, 0, 0.2, 1)"
    const val EasingEmphasized = "cubic-bezier(0.2, 0, 0, 1)"
    const val EasingDecelerate = "cubic-bezier(0, 0, 0.2, 1)"
}

fun Dp.xauxaBorder() = this
