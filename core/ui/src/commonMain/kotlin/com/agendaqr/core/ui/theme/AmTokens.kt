package com.agendaqr.core.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.pow

/**
 * Tokens del sistema visual propio de AgendaQr (ADR-0022).
 *
 * Identidad visual: AgendaQr (ADR-0022), Super-App de alta densidad con accesibilidad; navegación según convenciones de Android e iOS. Arquitectura de capas (contrato normativo
 * docs/05-design-system/00-contrato-normativo-diseno.md):
 * 1. Tokens privados: [AmPalette] — primitivas por tema, sin exposición.
 * 2. Tokens semánticos: [AmColorScheme], [AmColor], [AmSpacing],
 *    [AmRadius]/[AmShapes], [AmMetrics], [AmType]/[AmTextStyles],
 *    [AmMotion], [AmElevation], [AmAccent] — la única capa que el producto consume.
 * 3. Tema: [AmTheme] — puente a Material 3 (implementación, no autoridad).
 *
 * La paleta de marca se define para AgendaQr y se verifica contra WCAG AA en ambos temas; ningún estado depende solo del color.
 */

/** Primitivas por tema: privadas de la capa de tokens. */
internal object AmPalette {
    // Tema claro: paleta Super-App de alta densidad (ADR-0022), con
    // ajustes mínimos de contraste para cumplir WCAG AA.
    val lightBackground = Color(0xFFF5F6F8)
    val lightSurface = Color(0xFFFFFFFF)
    val lightSurfaceSecondary = Color(0xFFF1F2F5)
    val lightSurfaceContainerLow = Color(0xFFFFFFFF)
    val lightSurfaceContainer = Color(0xFFF5F6F8)
    val lightSurfaceContainerHigh = Color(0xFFEDEFF3)
    val lightSurfaceContainerHighest = Color(0xFFE3E6EC)
    val lightBorder = Color(0xFFE2E4E9)
    val lightDivider = Color(0xFFEEEEEE)
    val lightText = Color(0xFF1A1A1A)
    val lightTextSecondary = Color(0xFF666666)
    val lightTextTertiary = Color(0xFF6B6B6B)
    val lightAccent = Color(0xFFE4001F)
    val lightOnAccent = Color(0xFFFFFFFF)
    val lightAccentText = Color(0xFFD0001C)
    val lightWathet = Color(0xFFFDE7EA)
    val lightDanger = Color(0xFFC62828)
    val lightOnDanger = Color(0xFFFFFFFF)
    val lightDangerContainer = Color(0xFFFDECEC)
    val lightSuccess = Color(0xFF008156)
    val lightSuccessContainer = Color(0xFFECFBF4)
    val lightWarning = Color(0xFF8A4B00)
    val lightWarningContainer = Color(0xFFFFF2D9)
    val lightDisabled = Color(0xFFE9EDF2)
    val lightOnDisabled = Color(0xFF666666)
    val lightInverseSurface = Color(0xFF1A1A1A)
    val lightInverseOnSurface = Color(0xFFFFFFFF)
    val lightInverseAccent = Color(0xFF3086FF)
    /** Lienzo del QR: fondo claro GARANTIZADO en ambos temas (zona de
     * silencio escaneable); el marco tonal lo aporta AmMediaFrame. */
    val lightQrCanvas = Color(0xFFFFFFFF)
    // Roles de la dirección Super-App (ADR-0022).
    val lightActionGradientStart = Color(0xFFE4001F)
    val lightActionGradientMid = Color(0xFFD12810)
    val lightActionGradientEnd = Color(0xFFBE4F00)
    val lightOnActionGradient = Color(0xFFFFFFFF)
    val lightPromo = Color(0xFFBE4F00)
    val lightOnPromo = Color(0xFFFFFFFF)
    val lightPromoContainer = Color(0xFFFFE9D6)
    val lightInfoContainer = Color(0xFFF2F8FF)
    val lightFintech = Color(0xFF1677FF)
    val lightOnFintech = Color(0xFFFFFFFF)
    val lightFintechText = Color(0xFF0067F7)
    val lightSurfaceBorder = Color(0xFFE2E4E9)
    val lightDividerSubtle = Color(0xFFEEEEEE)

    // Tema oscuro: paleta independiente inspirada en el Anexo A (fondo
    // #0A0A0A, superficie #1A1A1A con borde sutil), no inversión mecánica
    // del claro; los pares de contraste los fija AmSchemeTest (contrato §4.4).
    val darkBackground = Color(0xFF0A0A0A)
    val darkSurface = Color(0xFF1A1A1A)
    val darkSurfaceSecondary = Color(0xFF141414)
    val darkSurfaceContainerLow = Color(0xFF1A1A1A)
    val darkSurfaceContainer = Color(0xFF202020)
    val darkSurfaceContainerHigh = Color(0xFF2A2A2A)
    val darkSurfaceContainerHighest = Color(0xFF333333)
    val darkBorder = Color(0xFF2A2A2A)
    val darkDivider = Color(0xFF2A2A2A)
    val darkText = Color(0xFFE6E6E6)
    val darkTextSecondary = Color(0xFF9A9A9A)
    val darkTextTertiary = Color(0xFF8A8A8A)
    val darkAccent = Color(0xFFFF5A70)
    val darkOnAccent = Color(0xFF2A0A10)
    val darkAccentText = Color(0xFFFF5A70)
    val darkWathet = Color(0xFF33141A)
    val darkDanger = Color(0xFFFF6B78)
    val darkOnDanger = Color(0xFF2A0F14)
    val darkDangerContainer = Color(0xFF3A151D)
    val darkSuccess = Color(0xFF3CCB8E)
    val darkSuccessContainer = Color(0xFF0E2A1E)
    val darkWarning = Color(0xFFFFC15A)
    val darkWarningContainer = Color(0xFF3A2B0E)
    val darkDisabled = Color(0xFF303030)
    val darkOnDisabled = Color(0xFF9A9A9A)
    val darkInverseSurface = Color(0xFFE6E6E6)
    val darkInverseOnSurface = Color(0xFF0A0A0A)
    val darkInverseAccent = Color(0xFF3086FF)
    // El gradiente de acción es marca: se conserva idéntico en ambos temas
    // (texto blanco ≥ 4.5:1 sobre cada extremo).
    val darkActionGradientStart = Color(0xFFE4001F)
    val darkActionGradientMid = Color(0xFFD12810)
    val darkActionGradientEnd = Color(0xFFBE4F00)
    val darkOnActionGradient = Color(0xFFFFFFFF)
    val darkPromo = Color(0xFFFF8A3D)
    val darkOnPromo = Color(0xFF2A1200)
    val darkPromoContainer = Color(0xFF3A2410)
    val darkInfoContainer = Color(0xFF10233F)
    val darkFintech = Color(0xFF1677FF)
    val darkOnFintech = Color(0xFFFFFFFF)
    val darkFintechText = Color(0xFF4D9BFF)
    val darkSurfaceBorder = Color(0xFF333333)
    val darkDividerSubtle = Color(0xFF262626)
}

/**
 * Esquema semántico del sistema. Los roles son el contrato del producto:
 * background/surface/surfaceSecondary, texto en tres niveles, acento con
 * su par, hairlines, estados (success/warning/danger/info), foco y
 * deshabilitado. Pares verificados ≥ 4.5:1 (contenido) y ≥ 3:1
 * (foco) por AmSchemeTest.
 */
data class AmColorScheme(
    val background: Color,
    val surface: Color,
    val surfaceSecondary: Color,
    val border: Color,
    val divider: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val accent: Color,
    val onAccent: Color,
    /** Alias de accent/onAccent por diseño: UN solo acento; el contenedor
     * tonal de M3 no introduce una segunda marca. */
    val accentContainer: Color,
    val onAccentContainer: Color,
    /** Acento legible como TEXTO sobre background/surface (≥ 4.5:1). */
    val accentText: Color,
    /** Tinte de la referencia para fondos de selección. */
    val wathet: Color,
    val danger: Color,
    val onDanger: Color,
    val dangerContainer: Color,
    val success: Color,
    val successContainer: Color,
    val warning: Color,
    val warningContainer: Color,
    /** La referencia no define tono info: lo informativo usa el acento. */
    val info: Color,
    val infoContainer: Color,
    val focus: Color,
    val disabled: Color,
    val onDisabled: Color,
    val inverseSurface: Color,
    val inverseOnSurface: Color,
    val inverseAccent: Color,
    /** Lienzo del QR (fondo claro garantizado en ambos temas). */
    val qrCanvas: Color,
    /** Gradiente de acción (marca): inicio, medio y fin; [onActionGradient]
     * es el único texto admitido sobre él (≥ 4.5:1 sobre cada extremo). */
    val actionGradientStart: Color,
    val actionGradientMid: Color,
    val actionGradientEnd: Color,
    val onActionGradient: Color,
    /** Naranja promo (badges de novedad/urgencia y acentos comerciales). */
    val promo: Color,
    val onPromo: Color,
    val promoContainer: Color,
    /** Azul fintech: relleno de confianza y texto/enlace legible. */
    val fintech: Color,
    val onFintech: Color,
    val fintechText: Color,
    /** Borde sutil de superficie (en oscuro separa la tarjeta del fondo). */
    val surfaceBorder: Color,
    /** Divisor decorativo fino entre filas (1 px; no es límite funcional). */
    val dividerSubtle: Color,
    /** Contenedores M3 explícitos: ninguna superficie hereda el esquema
     * base de Material (contaminación morada de la plantilla). */
    val surfaceContainerLow: Color,
    val surfaceContainer: Color,
    val surfaceContainerHigh: Color,
    val surfaceContainerHighest: Color,
)

val LightAmColorScheme = AmColorScheme(
    background = AmPalette.lightBackground,
    surface = AmPalette.lightSurface,
    surfaceSecondary = AmPalette.lightSurfaceSecondary,
    border = AmPalette.lightBorder,
    divider = AmPalette.lightDivider,
    textPrimary = AmPalette.lightText,
    textSecondary = AmPalette.lightTextSecondary,
    textTertiary = AmPalette.lightTextTertiary,
    accent = AmPalette.lightAccent,
    onAccent = AmPalette.lightOnAccent,
    accentContainer = AmPalette.lightAccent,
    onAccentContainer = AmPalette.lightOnAccent,
    accentText = AmPalette.lightAccentText,
    wathet = AmPalette.lightWathet,
    danger = AmPalette.lightDanger,
    onDanger = AmPalette.lightOnDanger,
    dangerContainer = AmPalette.lightDangerContainer,
    success = AmPalette.lightSuccess,
    successContainer = AmPalette.lightSuccessContainer,
    warning = AmPalette.lightWarning,
    warningContainer = AmPalette.lightWarningContainer,
    info = AmPalette.lightFintechText,
    infoContainer = AmPalette.lightInfoContainer,
    focus = AmPalette.lightFintechText,
    disabled = AmPalette.lightDisabled,
    onDisabled = AmPalette.lightOnDisabled,
    inverseSurface = AmPalette.lightInverseSurface,
    inverseOnSurface = AmPalette.lightInverseOnSurface,
    inverseAccent = AmPalette.lightInverseAccent,
    qrCanvas = AmPalette.lightQrCanvas,
    actionGradientStart = AmPalette.lightActionGradientStart,
    actionGradientMid = AmPalette.lightActionGradientMid,
    actionGradientEnd = AmPalette.lightActionGradientEnd,
    onActionGradient = AmPalette.lightOnActionGradient,
    promo = AmPalette.lightPromo,
    onPromo = AmPalette.lightOnPromo,
    promoContainer = AmPalette.lightPromoContainer,
    fintech = AmPalette.lightFintech,
    onFintech = AmPalette.lightOnFintech,
    fintechText = AmPalette.lightFintechText,
    surfaceBorder = AmPalette.lightSurfaceBorder,
    dividerSubtle = AmPalette.lightDividerSubtle,
    surfaceContainerLow = AmPalette.lightSurfaceContainerLow,
    surfaceContainer = AmPalette.lightSurfaceContainer,
    surfaceContainerHigh = AmPalette.lightSurfaceContainerHigh,
    surfaceContainerHighest = AmPalette.lightSurfaceContainerHighest,
)

val DarkAmColorScheme = AmColorScheme(
    background = AmPalette.darkBackground,
    surface = AmPalette.darkSurface,
    surfaceSecondary = AmPalette.darkSurfaceSecondary,
    border = AmPalette.darkBorder,
    divider = AmPalette.darkDivider,
    textPrimary = AmPalette.darkText,
    textSecondary = AmPalette.darkTextSecondary,
    textTertiary = AmPalette.darkTextTertiary,
    accent = AmPalette.darkAccent,
    onAccent = AmPalette.darkOnAccent,
    accentContainer = AmPalette.darkAccent,
    onAccentContainer = AmPalette.darkOnAccent,
    accentText = AmPalette.darkAccentText,
    wathet = AmPalette.darkWathet,
    danger = AmPalette.darkDanger,
    onDanger = AmPalette.darkOnDanger,
    dangerContainer = AmPalette.darkDangerContainer,
    success = AmPalette.darkSuccess,
    successContainer = AmPalette.darkSuccessContainer,
    warning = AmPalette.darkWarning,
    warningContainer = AmPalette.darkWarningContainer,
    info = AmPalette.darkFintechText,
    infoContainer = AmPalette.darkInfoContainer,
    focus = AmPalette.darkFintechText,
    disabled = AmPalette.darkDisabled,
    onDisabled = AmPalette.darkOnDisabled,
    inverseSurface = AmPalette.darkInverseSurface,
    inverseOnSurface = AmPalette.darkInverseOnSurface,
    inverseAccent = AmPalette.darkInverseAccent,
    qrCanvas = AmPalette.lightQrCanvas,
    actionGradientStart = AmPalette.darkActionGradientStart,
    actionGradientMid = AmPalette.darkActionGradientMid,
    actionGradientEnd = AmPalette.darkActionGradientEnd,
    onActionGradient = AmPalette.darkOnActionGradient,
    promo = AmPalette.darkPromo,
    onPromo = AmPalette.darkOnPromo,
    promoContainer = AmPalette.darkPromoContainer,
    fintech = AmPalette.darkFintech,
    onFintech = AmPalette.darkOnFintech,
    fintechText = AmPalette.darkFintechText,
    surfaceBorder = AmPalette.darkSurfaceBorder,
    dividerSubtle = AmPalette.darkDividerSubtle,
    surfaceContainerLow = AmPalette.darkSurfaceContainerLow,
    surfaceContainer = AmPalette.darkSurfaceContainer,
    surfaceContainerHigh = AmPalette.darkSurfaceContainerHigh,
    surfaceContainerHighest = AmPalette.darkSurfaceContainerHighest,
)

// NOTA: el default del CompositionLocal es el esquema OSCURO. AmTheme
// SIEMPRE proporciona el esquema real (claro u oscuro según el sistema);
// este default solo afecta a composiciones sin AmTheme (tooling/previews).
val LocalAmColorScheme = compositionLocalOf { DarkAmColorScheme }

/**
 * Roles semánticos de color: la ÚNICA puerta de color del producto.
 * Ninguna pantalla introduce literales; los estados nunca dependen solo
 * del color (icono/texto por tono, contrato de accesibilidad).
 */
object AmColor {
    val Background: Color @Composable get() = LocalAmColorScheme.current.background
    val Surface: Color @Composable get() = LocalAmColorScheme.current.surface
    val SurfaceSecondary: Color @Composable get() = LocalAmColorScheme.current.surfaceSecondary
    val Divider: Color @Composable get() = LocalAmColorScheme.current.divider
    val TextPrimary: Color @Composable get() = LocalAmColorScheme.current.textPrimary
    val TextSecondary: Color @Composable get() = LocalAmColorScheme.current.textSecondary
    val TextTertiary: Color @Composable get() = LocalAmColorScheme.current.textTertiary
    val Accent: Color @Composable get() = LocalAmColorScheme.current.accent
    val OnAccent: Color @Composable get() = LocalAmColorScheme.current.onAccent
    /** Acento legible como texto (≥ 4.5:1 sobre background y surface). */
    val AccentText: Color @Composable get() = LocalAmColorScheme.current.accentText
    /** Tinte para fondos de selección (chips, pestañas). */
    val Wathet: Color @Composable get() = LocalAmColorScheme.current.wathet
    val Danger: Color @Composable get() = LocalAmColorScheme.current.danger
    val OnDanger: Color @Composable get() = LocalAmColorScheme.current.onDanger
    /** Bloque tonal de peligro (acción destructiva tonal: contorno o tono). */
    val DangerContainer: Color @Composable get() = LocalAmColorScheme.current.dangerContainer
    /** Texto/ icono de éxito sobre superficie o contenedor (≥ 4.5:1). */
    val Success: Color @Composable get() = LocalAmColorScheme.current.success
    /** Bloque tonal de éxito (fondos de identidad de cobro, etiquetas). */
    val SuccessContainer: Color @Composable get() = LocalAmColorScheme.current.successContainer
    /** Texto/ icono de advertencia (≥ 4.5:1 sobre fondos claros). */
    val Warning: Color @Composable get() = LocalAmColorScheme.current.warning
    /** Bloque tonal de advertencia (fondos de advertencia, etiquetas). */
    val WarningContainer: Color @Composable get() = LocalAmColorScheme.current.warningContainer
    /** Texto/ icono informativo (≥ 4.5:1 sobre fondos claros). */
    val Info: Color @Composable get() = LocalAmColorScheme.current.info
    /** Bloque tonal informativo (fondos de información, etiquetas). */
    val InfoContainer: Color @Composable get() = LocalAmColorScheme.current.infoContainer
    /** Bloque tonal neutro profundo (etiquetas sobre fondo, ambos temas). */
    val SurfaceContainerHighest: Color @Composable get() = LocalAmColorScheme.current.surfaceContainerHighest
    /** Pista del control segmentado y chips de reposo (relleno distinguible
     * del fondo y de la superficie activa en ambos temas). */
    val SurfaceContainerHigh: Color @Composable get() = LocalAmColorScheme.current.surfaceContainerHigh
    /** Caja tonal de metadatos y filas de solo lectura (relleno sutil que
     * agrupa sin competir con el contenido). */
    val SurfaceContainerLow: Color @Composable get() = LocalAmColorScheme.current.surfaceContainerLow
    /** Píldora "Modo local": superficie inversa con contenido inverso. */
    val InverseSurface: Color @Composable get() = LocalAmColorScheme.current.inverseSurface
    val InverseOnSurface: Color @Composable get() = LocalAmColorScheme.current.inverseOnSurface
    val Focus: Color @Composable get() = LocalAmColorScheme.current.focus
    val Disabled: Color @Composable get() = LocalAmColorScheme.current.disabled
    val OnDisabled: Color @Composable get() = LocalAmColorScheme.current.onDisabled
    /** Lienzo del QR: fondo claro garantizado en claro y oscuro. */
    val QrCanvas: Color @Composable get() = LocalAmColorScheme.current.qrCanvas
    /** Gradiente de acción (marca): extremos y medio; [OnActionGradient] es
     * el texto admitido sobre él. */
    val ActionGradientStart: Color @Composable get() = LocalAmColorScheme.current.actionGradientStart
    val ActionGradientMid: Color @Composable get() = LocalAmColorScheme.current.actionGradientMid
    val ActionGradientEnd: Color @Composable get() = LocalAmColorScheme.current.actionGradientEnd
    val OnActionGradient: Color @Composable get() = LocalAmColorScheme.current.onActionGradient
    /** Naranja promo y su par. */
    val Promo: Color @Composable get() = LocalAmColorScheme.current.promo
    val OnPromo: Color @Composable get() = LocalAmColorScheme.current.onPromo
    val PromoContainer: Color @Composable get() = LocalAmColorScheme.current.promoContainer
    /** Azul fintech: relleno de confianza (con glifo) y texto/enlace legible. */
    val Fintech: Color @Composable get() = LocalAmColorScheme.current.fintech
    val OnFintech: Color @Composable get() = LocalAmColorScheme.current.onFintech
    val FintechText: Color @Composable get() = LocalAmColorScheme.current.fintechText
    /** Borde sutil de superficie (en oscuro separa la tarjeta del fondo). */
    val SurfaceBorder: Color @Composable get() = LocalAmColorScheme.current.surfaceBorder
    /** Divisor decorativo fino entre filas. */
    val DividerSubtle: Color @Composable get() = LocalAmColorScheme.current.dividerSubtle
    /** Transparente funcional (indicadores deshabilitados de M3); único
     * literal de color crudo permitido en la capa de tokens. */
    val Transparent: Color = Color.Transparent
}

/**
 * Escala de espaciado en múltiplos de 4 dp (contrato §espaciado):
 * 4 · 8 · 12 · 16 · 20 · 24 · 32, con 40 para aire excepcional.
 */
object AmSpacing {
    val None = 0.dp
    val Xs = 4.dp
    val Sm = 8.dp
    val Md = 12.dp
    val Lg = 16.dp
    val Xl = 20.dp
    val Xxl = 24.dp
    val Xxxl = 32.dp
    val Huge = 40.dp
    /** Margen lateral de pantalla (múltiplo de 4; 12 dp del Anexo A). */
    val ScreenMargin = 12.dp
}

/**
 * Métricas semánticas de interacción y composición (contrato §metría).
 */
object AmMetrics {
    val Border = 1.dp
    /** Borde fuerte FUNCIONAL (marco del encuadre del scanner). */
    val BorderStrong = 3.dp
    /** Área táctil mínima (accesibilidad, invariante del gate). */
    val ControlMinSize = 48.dp
    /** Altura VISUAL compacta de botones secundarios: la zona interactiva
     * sigue siendo 48 dp (mandato de compactación: la superficie visible
     * puede ser menor que el target). */
    val ButtonCompactHeight = 40.dp
    /** Altura visual de píldoras/chips compactos. */
    val ChipCompactHeight = 32.dp
    /** Altura visual compacta de entradas. */
    val FieldCompactHeight = 44.dp
    /** Altura de la barra superior compacta (NavBar de la referencia mide
     * 45 px; 56 dp aloja objetivos táctiles de 48). */
    val TopBarHeight = 56.dp
    /** Altura de la barra inferior compacta (TabBar de la referencia mide
     * 48 px; 56 dp con etiqueta 11 sp aloja objetivos de 48 dp por ítem). */
    val NavBarHeight = 56.dp
    /** Ancho máximo del contenido en medium/expanded. */
    val ContentMaxWidth = 720.dp
    val QrPreviewSize = 240.dp
    /** Lado del marco de revisión (S09): aprovecha el ancho disponible sin
     * deformar el código (siempre 1:1). */
    val QrReviewSize = 320.dp
    /** Tamaño de icono estándar de acciones y filas. */
    val IconSize = 24.dp
    /** Icono compacto: segmentados, metadatos y ayudas (glifo acompaña,
     * no domina, la etiqueta de texto). */
    val IconSizeCompact = 18.dp
    /** Ancho de las mini-tarjetas del carrusel de favoritos. */
    val FavCardWidth = 184.dp
    /** Insets del hairline de una fila con marca a la izquierda: alinea el
     * separador con el texto, no con el borde de la tarjeta. */
    val ListDividerInset = 56.dp
    /** Altura máxima de una lista dentro de un diálogo/hoja: alcanzable
     * con scroll cuando es larga; por debajo mide su contenido. */
    val DialogListMaxHeight = 320.dp
    /** Ancho mínimo del popup de un diálogo (Dialog de la referencia:
     * 280 px → 280 dp). */
    val DialogMinWidth = 280.dp
    /** Ancho máximo del popup de un diálogo como fracción del ancho de
     * ventana (Dialog de la referencia: 75vw). */
    const val DialogMaxWidthFraction = 0.75f
    /** Círculo tonal del glifo del estado vacío. */
    val EmptyStateGlyphSize = 64.dp
    /** Contenedor del icono de una celda de acción rápida en Inicio. */
    val GridIconContainerSize = 48.dp
    /** Largo del trazo de las esquinas del encuadre del scanner. */
    val ScannerCornerLength = 24.dp
    /** Insignia de contexto (M4): círculo de 40 dp, mismo tile que la
     * marca tipada de las filas financieras. */
    val ContextBadgeSize = 40.dp
    /** Espacio que el FAB necesita por encima de la barra inferior para no
     * tapar el último elemento del contenido desplazable: alto real del FAB
     * ([ControlMinSize]) + el margen del Scaffold ([AmSpacing.Lg]). Es un
     * valor DERIVADO de componentes reales, no una constante a mano. */
    val FabClearance = ControlMinSize + AmSpacing.Lg
    /** Ancho de fila a partir del cual el metadato final de la celda
     * (`AmListRow` `endMeta`) comparte la línea del título; por debajo pasa
     * a su propia línea para no comprimir el título en un teléfono angosto. */
    val ListRowEndMetaMinWidth = 320.dp
    /** Escala de fuente del sistema a partir de la cual el metadato final
     * pasa a su propia línea (el texto grande nunca recorta el título). */
    const val ListRowEndMetaStackFontScale = 2f
}

/** Radios del Anexo A (ADR-0022): tag/cinta 4–8, control y tarjeta
 * pequeña 8, tarjeta 12, panel 16, píldora 20+, rejilla 14. */
object AmRadius {
    val ExtraSmall = 4.dp
    val Band = 6.dp
    val Small = 8.dp
    val Medium = 12.dp
    val Large = 16.dp
    val ExtraLarge = 20.dp
    val GridIcon = 14.dp
}

/**
 * Conjunto de formas: [AmTheme] lo entrega a
 * `MaterialTheme.shapes` y los componentes envolventes fijan los radios
 * que M3 no mapea por rol (botones, campos, chips).
 */
object AmShapes {
    val ExtraSmall = RoundedCornerShape(AmRadius.ExtraSmall)
    val Band = RoundedCornerShape(AmRadius.Band)
    val Small = RoundedCornerShape(AmRadius.Small)
    val Medium = RoundedCornerShape(AmRadius.Medium)
    val Large = RoundedCornerShape(AmRadius.Large)
    val ExtraLarge = RoundedCornerShape(AmRadius.ExtraLarge)
    val GridIcon = RoundedCornerShape(AmRadius.GridIcon)
    /** Chips, botones primarios, badges y pastillas: píldora completa. */
    val Pill = RoundedCornerShape(percent = 50)
}

/**
 * Escala tipográfica en sp (respeta el escalado de fuente del sistema) del
 * Anexo A (ADR-0022): Importe hero 30/800 con símbolo 14/700; H1 20/600;
 * H2 16/600 (`RowTitle`, `SectionHeader`); Body 14/400; Caption 12/400;
 * badges y etiquetas de barra 11–12. **Mínimo absoluto 11 sp.** Display 32
 * y Headline 24 se conservan del sistema.
 */
object AmType {
    /** Display de bloque (32 sp): número/hero de tarjeta de datos. */
    val Display: TextUnit = 32.sp
    /** Encabezado destacado (24 sp, techo de la escala). */
    val Headline: TextUnit = 24.sp
    /** Título de pantalla y H1 (20 sp, Anexo 18–20/600). */
    val TitleLarge: TextUnit = 20.sp
    /** Título de diálogo/modal centrado (18 sp). */
    val Title: TextUnit = 18.sp
    /** Importe hero (30 sp, Anexo 28–32/800): tabular, símbolo más pequeño. */
    val Amount: TextUnit = 30.sp
    /** Símbolo de moneda del importe (14 sp/700, siempre menor que el número). */
    val AmountSymbol: TextUnit = 14.sp
    /** H2: título de fila y de sección (16 sp/600). */
    val RowTitle: TextUnit = 16.sp
    /** Encabezado de sección (16 sp/600, Anexo H2). */
    val SectionHeader: TextUnit = 16.sp
    /** Cuerpo y etiqueta de acción (14 sp/400, Anexo Body). */
    val Body: TextUnit = 14.sp
    /** Etiqueta de acción de texto, contenido de modal y cápsula (14 sp). */
    val Label: TextUnit = 14.sp
    /** Descripción y metadato (12 sp, Anexo Caption). */
    val BodySmall: TextUnit = 12.sp
    /** Apoyo/ayuda/error (12 sp). */
    val Caption: TextUnit = 12.sp
    /** Caption mínima: badges y etiquetas de barra (11 sp, mínimo absoluto). */
    val CaptionSmall: TextUnit = 11.sp
    // Interlineados canónicos en sp (> 1.1× su tamaño, §accesibilidad).
    val LineHeightLabel: TextUnit = 20.sp
    val LineHeightBody: TextUnit = 20.sp
    val LineHeightBodySmall: TextUnit = 18.sp
    val LineHeightCaption: TextUnit = 16.sp
    val LineHeightSection: TextUnit = 22.sp
    val LineAmount: TextUnit = 36.sp
    val LineHeightRowTitle: TextUnit = 22.sp
    val LineHeightHeadlineLarge: TextUnit = 40.sp
    val LineHeightHeadlineSmall: TextUnit = 32.sp
    val LineHeightTitleLarge: TextUnit = 26.sp
    val LineHeightTitleMedium: TextUnit = 22.sp
    val LineHeightTitleSmall: TextUnit = 20.sp
    val LineHeightLabelSmall: TextUnit = 16.sp
    /**
     * Familia de sistema en TODOS los estilos — Roboto en Android, San
     * Francisco en iOS; ninguna fuente se empaqueta (ADR-0020/ADR-0022).
     */
    val FamilyUi: FontFamily = FontFamily.Default
}

/** Tracking de la interfaz: neutro salvo la etiqueta de la acción primaria
 * (Anexo A: tracking ligeramente positivo). */
object AmTracking {
    val TitleMedium: TextUnit = 0.sp
    val TitleSmall: TextUnit = 0.sp
    val BodyLarge: TextUnit = 0.sp
    val BodyMedium: TextUnit = 0.sp
    val BodySmall: TextUnit = 0.sp
    val LabelLarge: TextUnit = 0.sp
    val LabelMedium: TextUnit = 0.sp
    val LabelSmall: TextUnit = 0.sp
    /** Etiqueta de la acción primaria (Anexo A). */
    val ButtonLabel: TextUnit = 0.2.sp
}

/**
 * Tipografía Material 3: [AmTheme] la entrega a
 * `MaterialTheme(typography = …)` y cada componente M3 hereda la misma
 * familia y tamaños.
 */
val AmMaterialTypography: Typography = Typography(
    headlineLarge = TextStyle(
        fontSize = AmType.Display,
        lineHeight = AmType.LineHeightHeadlineLarge,
        fontFamily = AmType.FamilyUi,
    ),
    headlineMedium = TextStyle(
        fontSize = AmType.Headline,
        lineHeight = AmType.LineHeightHeadlineSmall,
        fontFamily = AmType.FamilyUi,
    ),
    headlineSmall = TextStyle(
        fontSize = AmType.Headline,
        lineHeight = AmType.LineHeightHeadlineSmall,
        fontFamily = AmType.FamilyUi,
    ),
    titleLarge = TextStyle(
        fontSize = AmType.TitleLarge,
        lineHeight = AmType.LineHeightTitleLarge,
        fontWeight = FontWeight.SemiBold,
        fontFamily = AmType.FamilyUi,
    ),
    titleMedium = TextStyle(
        fontSize = AmType.RowTitle,
        lineHeight = AmType.LineHeightTitleMedium,
        letterSpacing = AmTracking.TitleMedium,
        fontWeight = FontWeight.SemiBold,
        fontFamily = AmType.FamilyUi,
    ),
    titleSmall = TextStyle(
        fontSize = AmType.Label,
        lineHeight = AmType.LineHeightTitleSmall,
        letterSpacing = AmTracking.TitleSmall,
        fontWeight = FontWeight.Medium,
        fontFamily = AmType.FamilyUi,
    ),
    bodyLarge = TextStyle(
        fontSize = AmType.Body,
        lineHeight = AmType.LineHeightBody,
        letterSpacing = AmTracking.BodyLarge,
        fontFamily = AmType.FamilyUi,
    ),
    bodyMedium = TextStyle(
        fontSize = AmType.Label,
        lineHeight = AmType.LineHeightTitleSmall,
        letterSpacing = AmTracking.BodyMedium,
        fontFamily = AmType.FamilyUi,
    ),
    bodySmall = TextStyle(
        fontSize = AmType.Caption,
        lineHeight = AmType.LineHeightCaption,
        letterSpacing = AmTracking.BodySmall,
        fontFamily = AmType.FamilyUi,
    ),
    labelLarge = TextStyle(
        fontSize = AmType.Label,
        lineHeight = AmType.LineHeightLabel,
        letterSpacing = AmTracking.LabelLarge,
        fontWeight = FontWeight.Medium,
        fontFamily = AmType.FamilyUi,
    ),
    labelMedium = TextStyle(
        fontSize = AmType.Caption,
        lineHeight = AmType.LineHeightCaption,
        letterSpacing = AmTracking.LabelMedium,
        fontWeight = FontWeight.Medium,
        fontFamily = AmType.FamilyUi,
    ),
    labelSmall = TextStyle(
        fontSize = AmType.CaptionSmall,
        lineHeight = AmType.LineHeightLabelSmall,
        letterSpacing = AmTracking.LabelSmall,
        fontWeight = FontWeight.Medium,
        fontFamily = AmType.FamilyUi,
    ),
)

/**
 * Estilos tipográficos canónicos del sistema: reúnen fontSize, peso,
 * tracking e interlineado. Los componentes consumen estos estilos en
 * lugar de repetir tríos de tokens.
 */
object AmTextStyles {
    /** Etiqueta de la acción primaria (Anexo A: 14/600 con tracking
     * positivo; el texto blanco sobre el gradiente gana con semi-bold). */
    val ButtonLabel = TextStyle(
        fontSize = AmType.Body,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = AmTracking.ButtonLabel,
        lineHeight = AmType.LineHeightBody,
    )

    /** Cuerpo de texto (mensajes, filas): 14/20 (Anexo Body 14/400). */
    val Body = TextStyle(
        fontSize = AmType.Body,
        lineHeight = AmType.LineHeightBody,
    )

    /** Etiqueta de acción de texto, contenido de modal y cápsula (14 sp). */
    val Label = TextStyle(
        fontSize = AmType.Label,
        lineHeight = AmType.LineHeightLabel,
    )

    /** Metadato/descripción (12 sp, Anexo Caption 12/400). */
    val BodySmall = TextStyle(
        fontSize = AmType.BodySmall,
        lineHeight = AmType.LineHeightBodySmall,
    )

    /** Título de fila y H2 (16 sp/600, Anexo H2). */
    val RowTitle = TextStyle(
        fontSize = AmType.RowTitle,
        fontWeight = FontWeight.SemiBold,
        lineHeight = AmType.LineHeightRowTitle,
    )

    /** Importe hero (30 sp/800, Anexo 28–32/800) con cifras tabulares
     * para alinear la columna financiera. */
    val Amount = TextStyle(
        fontSize = AmType.Amount,
        fontWeight = FontWeight.ExtraBold,
        lineHeight = AmType.LineAmount,
        fontFeatureSettings = "tnum",
    )

    /** Símbolo de moneda del importe (14 sp/700): siempre menor que el
     * número y tabular para no desalinear la cifra. */
    val AmountSymbol = TextStyle(
        fontSize = AmType.AmountSymbol,
        fontWeight = FontWeight.Bold,
        lineHeight = AmType.LineHeightLabel,
        fontFeatureSettings = "tnum",
    )

    /** Subtítulo/metadato de fila (texto secundario, 12 sp). */
    val Caption = TextStyle(
        fontSize = AmType.Caption,
        lineHeight = AmType.LineHeightCaption,
    )

    /** Etiqueta/insignia mínima (11 sp normal, Tag de la referencia). */
    val Tag = TextStyle(
        fontSize = AmType.CaptionSmall,
        lineHeight = AmType.LineHeightLabelSmall,
    )

    /** Texto de apoyo (errores, ayuda, badges) — rol labelMedium de M3. */
    val Support = TextStyle(
        fontSize = AmType.Caption,
        letterSpacing = AmTracking.LabelMedium,
        lineHeight = AmType.LineHeightCaption,
    )

    /** Encabezado de sección y H2 (16 sp/600, Anexo H2; el color lo
     * decide el llamador). */
    val SectionHeader = TextStyle(
        fontSize = AmType.SectionHeader,
        fontWeight = FontWeight.SemiBold,
        lineHeight = AmType.LineHeightSection,
    )

    /** Título de diálogo (18 sp; AmDialog fija el bold del popup). */
    val Title = TextStyle(
        fontSize = AmType.Title,
        lineHeight = AmType.LineHeightTitleLarge,
    )
}


/**
 * Movimiento: transiciones cortas y justificadas; con
 * movimiento reducido la duración es 0 (cambio en el sitio, §accesibilidad
 * — reducir no es "animar más rápido"). Una sola easing.
 */
object AmMotion {
    /** Duración corta: pulsaciones y confirmaciones (Anexo 200–300 ms). */
    const val DurationShortMs = 200
    /** Duración media: transiciones de superficie de la app. */
    const val DurationMediumMs = 260
    /** Duración larga: transición de pantalla completa. */
    const val DurationLongMs = 300
    /** Con movimiento reducido la transición no dura nada: cambio en el sitio. */
    const val DurationReducedMs = 0
    /** Escala de pulsación (Anexo A: `scale(0.95)`); 1 con movimiento reducido. */
    const val PressScale = 0.95f
    /** Techo del brillo de entrada del Anexo A: ≤ 5 s y luego estático. */
    const val ShimmerDurationMs = 5000

    object Easings {
        /** Transición estándar de superficie (fade + desplazamiento corto). */
        val Standard: Easing = CubicBezierEasing(0.1f, 0.9f, 0.2f, 1f)

        /** Rebote suave del Anexo A: `cubic-bezier(0.34, 1.56, 0.64, 1)`. */
        val Spring: Easing = CubicBezierEasing(0.34f, 1.56f, 0.64f, 1f)
    }
}

/**
 * Niveles de elevación del Anexo A (ADR-0022, contrato §9).
 *
 * La sombra de Compose aproxima las sombras CSS del Anexo (no hay control
 * fino de desplazamiento/desenfoque): cada nivel fija el `Dp` que consume
 * [amElevation] y el color de su proyección. E-0 no dibuja sombra.
 */
enum class AmElevation(val elevation: Dp) {
    /** E-0: sin sombra (contenido inline, listas). */
    None(0.dp),

    /** E-1 (`0 2 8 rgba(0,0,0,.04)`): tarjetas estándar sobre fondo gris. */
    E1(2.dp),

    /** E-2 (`0 4 16 rgba(0,0,0,.08)`): tarjetas destacadas y carruseles. */
    E2(4.dp),

    /** E-3 (`0 8 24` del color de acción al 20 %): acción primaria flotante. */
    E3(8.dp),
}

private const val ShadowAlphaLow = 0.04f
private const val ShadowAlphaHigh = 0.08f
private const val ActionShadowAlpha = 0.20f

/**
 * Sombra de [level] sobre [shape]. Es el ÚNICO punto del código que llama a
 * `Modifier.shadow` (capa de tokens); las excepciones E-05/E-06 habilitan su
 * uso por archivo en los componentes. [AmElevation.None] no añade nada y no
 * provoca recomposición. E-3 proyecta el color de acción al 20 %; E-1/E-2
 * usan negro de baja opacidad (Anexo A).
 */
@Composable
fun Modifier.amElevation(
    level: AmElevation,
    shape: Shape = RectangleShape,
    clip: Boolean = false,
): Modifier {
    if (level == AmElevation.None) return this
    val scheme = LocalAmColorScheme.current
    val shadowColor = when (level) {
        AmElevation.E3 -> scheme.accent.copy(alpha = ActionShadowAlpha)
        AmElevation.E1 -> Color.Black.copy(alpha = ShadowAlphaLow)
        AmElevation.E2 -> Color.Black.copy(alpha = ShadowAlphaHigh)
        AmElevation.None -> Color.Transparent
    }
    return shadow(
        elevation = level.elevation,
        shape = shape,
        clip = clip,
        ambientColor = shadowColor,
        spotColor = shadowColor,
    )
}

/**
 * Gradiente de acción de marca: `ActionGradientStart→Mid→End`
 * (Anexo A, obligatorio en la acción primaria y los banners). El único
 * contenido admitido sobre él es [AmColor.OnActionGradient].
 */
@Composable
fun amActionGradient(): Brush = Brush.linearGradient(
    listOf(
        AmColor.ActionGradientStart,
        AmColor.ActionGradientMid,
        AmColor.ActionGradientEnd,
    ),
)

/**
 * Identidad de contexto: acento estable derivado de forma
 * determinista del identificador del contexto. [background] es el bloque
 * de identidad (marcador de fila, tinte de pestañas de S06) y
 * [onAccent] el único texto admitido sobre él. El par es fijo y
 * verificado (≥ 4.5:1) en ambos temas.
 */
data class AmAccent(val id: String, val background: Color, val onAccent: Color)

/**
 * Paleta de identidad de contextos: 12 tonos contemporáneos de etiqueta
 * (no la paleta de acentos histórica) más el acento de marca AgendaQr. Cada par
 * está verificado por AmAccentTest; onAccent es siempre negro o blanco
 * puro para mantener los pares robustos en ambas plataformas.
 */
object AmAccents {
    val System = AmAccent("system", Color(0xFF245FE5), Color(0xFFFFFFFF))

    private val Blue = AmAccent("blue", Color(0xFF1D4ED8), Color(0xFFFFFFFF))
    private val Sky = AmAccent("sky", Color(0xFF0369A1), Color(0xFFFFFFFF))
    private val Teal = AmAccent("teal", Color(0xFF0F766E), Color(0xFFFFFFFF))
    private val Cyan = AmAccent("cyan", Color(0xFF0E7490), Color(0xFFFFFFFF))
    private val Emerald = AmAccent("emerald", Color(0xFF047857), Color(0xFFFFFFFF))
    private val Green = AmAccent("green", Color(0xFF15803D), Color(0xFFFFFFFF))
    private val Lime = AmAccent("lime", Color(0xFF4D7C0F), Color(0xFFFFFFFF))
    private val Amber = AmAccent("amber", Color(0xFFA16207), Color(0xFFFFFFFF))
    private val Orange = AmAccent("orange", Color(0xFFC2410C), Color(0xFFFFFFFF))
    private val Red = AmAccent("red", Color(0xFFB91C1C), Color(0xFFFFFFFF))
    private val Rose = AmAccent("rose", Color(0xFFBE123C), Color(0xFFFFFFFF))
    private val Violet = AmAccent("violet", Color(0xFF6D28D9), Color(0xFFFFFFFF))

    val Admitted: List<AmAccent> = listOf(
        Blue, Sky, Teal, Cyan, Emerald, Green,
        Lime, Amber, Orange, Red, Rose, Violet,
    )
}

/**
 * Acento por contexto, determinista e independiente de la plataforma.
 *
 * Hash propio FNV-1a (32 bits, sobre los bytes UTF-8 del identificador):
 * explícitamente NO `String.hashCode`, para que la asignación sea idéntica
 * en Android e iOS y no cambie entre ejecuciones. [contextId] nulo
 * devuelve el acento de sistema.
 */
fun accentFor(contextId: String?): AmAccent {
    if (contextId == null) return AmAccents.System
    var hash: UInt = 0x811C9DC5u
    for (byte in contextId.encodeToByteArray()) {
        hash = hash xor byte.toUInt()
        hash *= 0x01000193u
    }
    return AmAccents.Admitted[(hash % AmAccents.Admitted.size.toUInt()).toInt()]
}

// ---------------------------------------------------------------------------------
// Matemática de contraste en la capa de tokens (accesibilidad): funciones
// PURAS (no composable) que usan los componentes para decidir colores
// legibles y los tests para fijar los umbrales.
// ---------------------------------------------------------------------------------

private fun linearChannel(channel: Float): Double {
    val c = channel.toDouble()
    return if (c <= 0.04045) c / 12.92 else ((c + 0.055) / 1.055).pow(2.4)
}

private fun luminance(color: Color): Double =
    0.2126 * linearChannel(color.red) +
        0.7152 * linearChannel(color.green) +
        0.0722 * linearChannel(color.blue)

/** Razón de contraste WCAG 2.x entre dos colores (1.0 = idénticos). */
fun contrastRatio(a: Color, b: Color): Double {
    val l1 = luminance(a)
    val l2 = luminance(b)
    val (hi, lo) = if (l1 >= l2) l1 to l2 else l2 to l1
    return (hi + 0.05) / (lo + 0.05)
}

/**
 * Este color usado como TEXTO sobre [background]: se devuelve tal cual si
 * cumple >= 4.5:1 (umbral de texto normal) y si no cae al [fallback]
 * legible. Los acentos de contexto marcan identidad con el bloque de
 * color, pero NUNCA como texto ilegible.
 */
fun Color.asTextOn(background: Color, fallback: Color): Color =
    if (contrastRatio(this, background) >= 4.5) this else fallback
