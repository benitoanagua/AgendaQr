package com.agendaqr.core.ui.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Xauxa Design System: única fuente editable de tokens.
 *
 * `design-tokens.json` fue retirado: esta capa Kotlin es la fuente canónica.
 * Organización:
 * - [XauxaPrimitive]: valores base sin intención de UI directa. Los
 *   componentes nunca los consumen; solo los esquemas semánticos.
 * - [XauxaColorScheme]: intención semántica (superficies, texto, estados,
 *   acento, foco). Inmutable por tema.
 * - [LightXauxaColorScheme]: producción actual. Preserva la apariencia
 *   vigente; lo nuevo frente a ella se marca como propuesta.
 * - [DarkXauxaColorScheme]: valores de la referencia xauxa v13
 *   (dark-first) y de XauxaXcan (`theme-manager.ts` para el fondo).
 *   Definidos por referencia, **pendientes de validación de producto**.
 * - [XauxaColor]: fachada que resuelve el esquema vigente vía
 *   [LocalXauxaColorScheme]; ningún componente comprueba el tema a mano.
 *
 * Procedencia de cada valor en los comentarios: [refs] = referencia,
 * [prod] = producción vigente preservada, [derivado] = mezcla calculada,
 * [nuevo] = propuesta pendiente de validación.
 */
internal object XauxaPrimitive {
    // Neutros claros [prod].
    val grayBackground = Color(0xFFF8FAFA)
    val graySurface2 = Color(0xFFF0F4F3)
    val graySurface3 = Color(0xFFE6EBEA) // [nuevo] propuesta: un paso más oscuro que Surface2.
    val grayBorder = Color(0xFFD7DEDC)
    val grayText1 = Color(0xFF17201E)
    val grayText2 = Color(0xFF4E5B57)
    val grayText3 = Color(0xFF6C7773)
    // Neutros oscuros [refs] (v13 gray.*).
    val blackBackground = Color(0xFF151218) // XauxaXcan theme-manager.ts, fondo dark.
    val blackSurface = Color(0xFF17171B)
    val blackSurface2 = Color(0xFF1F1F24)
    val blackSurface3 = Color(0xFF26262C)
    val blackBorder = Color(0xFF2A2A30)
    val blackText1 = Color(0xFFF4F4F6)
    val blackText2 = Color(0xFF9C9CA6)
    val blackText3 = Color(0xFF90909A)
    // Marca AgendaQr [prod] (= v13 agendaqr.brand).
    val tealBrand = Color(0xFF0E7D6E)
    val tealAccent = Color(0xFF7FD9C9)
    val white = Color.White
    // Semánticos claros [prod]; fondos [derivado] (10 % sobre superficie clara).
    val lightSuccess = Color(0xFF2E7D5B)
    val lightSuccessBg = Color(0xFFEAF2EF)
    val lightDanger = Color(0xFFB3261E)
    val lightDangerBg = Color(0xFFF7E9E8)
    val lightWarning = Color(0xFF8A5A00)
    val lightWarningBg = Color(0xFFF3EEE6)
    val lightInfo = Color(0xFF245E9B)
    val lightInfoBg = Color(0xFFE9EFF5)
    // Semánticos oscuros [refs] (v13 green/red/amber/blue .60/.10).
    val darkSuccess = Color(0xFF3DA35D)
    val darkSuccessBg = Color(0xFF163521)
    val darkDanger = Color(0xFFD9534F)
    val darkDangerBg = Color(0xFF3A1E1D)
    val darkWarning = Color(0xFFB5790A)
    val darkWarningBg = Color(0xFF3A2B0B)
    val darkInfo = Color(0xFF3A7BD5)
    val darkInfoBg = Color(0xFF17263D)
}

/** Esquema semántico completo e inmutable de un tema Xauxa. */
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
    background = XauxaPrimitive.grayBackground,
    surface = XauxaPrimitive.white,
    surface2 = XauxaPrimitive.graySurface2,
    surface3 = XauxaPrimitive.graySurface3,
    border = XauxaPrimitive.grayBorder,
    textPrimary = XauxaPrimitive.grayText1,
    textSecondary = XauxaPrimitive.grayText2,
    textTertiary = XauxaPrimitive.grayText3,
    brand = XauxaPrimitive.tealBrand,
    brandAccent = XauxaPrimitive.tealAccent,
    onBrand = XauxaPrimitive.white,
    white = XauxaPrimitive.white,
    success = XauxaPrimitive.lightSuccess,
    successBg = XauxaPrimitive.lightSuccessBg,
    danger = XauxaPrimitive.lightDanger,
    dangerBg = XauxaPrimitive.lightDangerBg,
    warning = XauxaPrimitive.lightWarning,
    warningBg = XauxaPrimitive.lightWarningBg,
    info = XauxaPrimitive.lightInfo,
    infoBg = XauxaPrimitive.lightInfoBg,
    focusRing = XauxaPrimitive.tealAccent,
)

val DarkXauxaColorScheme = XauxaColorScheme(
    background = XauxaPrimitive.blackBackground,
    surface = XauxaPrimitive.blackSurface,
    surface2 = XauxaPrimitive.blackSurface2,
    surface3 = XauxaPrimitive.blackSurface3,
    border = XauxaPrimitive.blackBorder,
    textPrimary = XauxaPrimitive.blackText1,
    textSecondary = XauxaPrimitive.blackText2,
    textTertiary = XauxaPrimitive.blackText3,
    brand = XauxaPrimitive.tealBrand,
    brandAccent = XauxaPrimitive.tealAccent,
    onBrand = XauxaPrimitive.white,
    white = XauxaPrimitive.white,
    success = XauxaPrimitive.darkSuccess,
    successBg = XauxaPrimitive.darkSuccessBg,
    danger = XauxaPrimitive.darkDanger,
    dangerBg = XauxaPrimitive.darkDangerBg,
    warning = XauxaPrimitive.darkWarning,
    warningBg = XauxaPrimitive.darkWarningBg,
    info = XauxaPrimitive.darkInfo,
    infoBg = XauxaPrimitive.darkInfoBg,
    focusRing = XauxaPrimitive.tealAccent,
)

val LocalXauxaColorScheme = compositionLocalOf { LightXauxaColorScheme }

/**
 * Fachada semántica consumida por producto y laboratorio. Resuelve el
 * esquema vigente ([LocalXauxaColorScheme]); los componentes nunca eligen
 * tema a mano. Sin CompositionLocal explícito rige el esquema claro, por lo
 * que la producción actual no cambia de apariencia.
 */
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
    /** Borde estructural fuerte de 2px: marcadores semánticos, marcos y separadores de footer (ref: border-t-2, scan-frame). */
    val BorderStrong = 2.dp
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
    /** Tracking amplio de etiquetas en mayúsculas (ref: tracking-wide/wider en botones, pills, badges y encabezados). */
    val LetterSpacingWide: TextUnit = 0.5.sp
    /** Familia UI: sistema por defecto, como XauxaXcan (sin webfont; Archivo/Roboto de la referencia complementaria siguen pendientes). */
    val FamilyUi: FontFamily = FontFamily.Default
    /** Familia monoespaciada para valores numéricos (ref: font-mono en stats y telemetría). */
    val FamilyMono: FontFamily = FontFamily.Monospace
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
