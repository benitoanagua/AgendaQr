package com.agendaqr.core.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.pow

/**
 * Ant Design Mobile — tokens de referencia replicados en Compose (ADR-0014).
 *
 * Arquitectura de capas (contrato normativo docs/05-design-system/00):
 * 1. Tokens privados: [AmPalette] — primitivas por tema, sin exposición.
 * 2. Tokens semánticos: [AmColorScheme], [AmColor], [AmSpacing],
 *    [AmRadius]/[AmShapes], [AmMetrics], [AmType]/[AmTextStyles],
 *    [AmMotion], [AmAccent] — la única capa que el producto consume.
 * 3. Tema: [AmTheme] — puente a Material 3 (implementación, no autoridad).
 *
 * La identidad visual replica la paleta azul y los neutros compactos de Ant Design Mobile. El rol de acción sólida usa una variante accesible del azul; los pares están verificados por AmSchemeTest y ningún estado depende solo del color.
 */

/** Primitivas por tema: privadas de la capa de tokens. */
internal object AmPalette {
    // Tema claro: lienzo frío, superficie blanca, tinta azulada.
    val lightBackground = Color(0xFFF5F5F5)
    val lightSurface = Color(0xFFFFFFFF)
    val lightSurfaceSecondary = Color(0xFFF5F5F5)
    val lightSurfaceContainerLow = Color(0xFFFFFFFF)
    val lightSurfaceContainer = Color(0xFFF5F5F5)
    val lightSurfaceContainerHigh = Color(0xFFF0F0F0)
    val lightSurfaceContainerHighest = Color(0xFFE8E8E8)
    val lightBorder = Color(0xFFE5E5E5)
    val lightDivider = Color(0xFFF0F0F0)
    val lightBorderControl = Color(0xFF595959)
    val lightText = Color(0xFF333333)
    val lightTextSecondary = Color(0xFF595959)
    val lightTextTertiary = Color(0xFF666666)
    val lightAccent = Color(0xFF0958D9)
    val lightOnAccent = Color(0xFFFFFFFF)
    val lightAccentText = Color(0xFF0958D9)
    val lightDanger = Color(0xFFC41D2A)
    val lightOnDanger = Color(0xFFFFFFFF)
    val lightDangerContainer = Color(0xFFFFF1F0)
    val lightSuccess = Color(0xFF006B45)
    val lightSuccessContainer = Color(0xFFE8FFF3)
    val lightWarning = Color(0xFF8C4A00)
    val lightWarningContainer = Color(0xFFFFF7E6)
    val lightInfo = Color(0xFF0958D9)
    val lightInfoContainer = Color(0xFFE6F4FF)
    val lightDisabled = Color(0xFFE8E8E8)
    val lightOnDisabled = Color(0xFF595959)
    val lightInverseSurface = Color(0xFF333333)
    val lightInverseOnSurface = Color(0xFFFFFFFF)
    val lightInverseAccent = Color(0xFF69B1FF)
    /** Lienzo del QR: fondo claro GARANTIZADO en ambos temas (zona de
     * silencio escaneable); el marco tonal lo aporta AmMediaFrame. */
    val lightQrCanvas = Color(0xFFFFFFFF)

    // Tema oscuro: tinta profunda (no negro puro), superficies elevadas,
    // acento claro con texto de tinta (par AA verificado).
    val darkBackground = Color(0xFF141414)
    val darkSurface = Color(0xFF1F1F1F)
    val darkSurfaceSecondary = Color(0xFF2A2A2A)
    val darkSurfaceContainerLow = Color(0xFF1A1A1A)
    val darkSurfaceContainer = Color(0xFF1F1F1F)
    val darkSurfaceContainerHigh = Color(0xFF2A2A2A)
    val darkSurfaceContainerHighest = Color(0xFF383838)
    val darkBorder = Color(0xFF424242)
    val darkDivider = Color(0xFF303030)
    val darkBorderControl = Color(0xFFBFBFBF)
    val darkText = Color(0xFFF5F5F5)
    val darkTextSecondary = Color(0xFFD9D9D9)
    val darkTextTertiary = Color(0xFFBFBFBF)
    val darkAccent = Color(0xFF69B1FF)
    val darkOnAccent = Color(0xFF141414)
    val darkAccentText = Color(0xFF69B1FF)
    val darkDanger = Color(0xFFFF7875)
    val darkOnDanger = Color(0xFF2A0000)
    val darkDangerContainer = Color(0xFF5C0011)
    val darkSuccess = Color(0xFF5CDBA5)
    val darkSuccessContainer = Color(0xFF003D2B)
    val darkWarning = Color(0xFFFFC069)
    val darkWarningContainer = Color(0xFF4A2B00)
    val darkInfo = Color(0xFF69B1FF)
    val darkInfoContainer = Color(0xFF102A43)
    val darkDisabled = Color(0xFF303030)
    val darkOnDisabled = Color(0xFFBFBFBF)
    val darkInverseSurface = Color(0xFFF5F5F5)
    val darkInverseOnSurface = Color(0xFF141414)
    val darkInverseAccent = Color(0xFF0958D9)
}

/**
 * Esquema semántico de AgendaMobile. Los roles son el contrato del
 * producto: background/surface/surfaceSecondary, texto en tres niveles,
 * acento con su par, bordes/divisores funcionales, estados (success/
 * warning/danger/info), foco y deshabilitado. Pares verificados ≥ 4.5:1
 * (contenido) y ≥ 3:1 (bordes/foco) por AmSchemeTest.
 */
data class AmColorScheme(
    val background: Color,
    val surface: Color,
    val surfaceSecondary: Color,
    val border: Color,
    val divider: Color,
    val borderControl: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val accent: Color,
    val onAccent: Color,
    /** Alias de accent/onAccent por diseño: AgendaMobile tiene UN solo
     * acento; el contenedor tonal de M3 no introduce una segunda marca. */
    val accentContainer: Color,
    val onAccentContainer: Color,
    /** Acento legible como TEXTO sobre background/surface (≥ 4.5:1). */
    val accentText: Color,
    val danger: Color,
    val onDanger: Color,
    val dangerContainer: Color,
    val success: Color,
    val successContainer: Color,
    val warning: Color,
    val warningContainer: Color,
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
    borderControl = AmPalette.lightBorderControl,
    textPrimary = AmPalette.lightText,
    textSecondary = AmPalette.lightTextSecondary,
    textTertiary = AmPalette.lightTextTertiary,
    accent = AmPalette.lightAccent,
    onAccent = AmPalette.lightOnAccent,
    accentContainer = AmPalette.lightAccent,
    onAccentContainer = AmPalette.lightOnAccent,
    accentText = AmPalette.lightAccentText,
    danger = AmPalette.lightDanger,
    onDanger = AmPalette.lightOnDanger,
    dangerContainer = AmPalette.lightDangerContainer,
    success = AmPalette.lightSuccess,
    successContainer = AmPalette.lightSuccessContainer,
    warning = AmPalette.lightWarning,
    warningContainer = AmPalette.lightWarningContainer,
    info = AmPalette.lightInfo,
    infoContainer = AmPalette.lightInfoContainer,
    focus = AmPalette.lightAccent,
    disabled = AmPalette.lightDisabled,
    onDisabled = AmPalette.lightOnDisabled,
    inverseSurface = AmPalette.lightInverseSurface,
    inverseOnSurface = AmPalette.lightInverseOnSurface,
    inverseAccent = AmPalette.lightInverseAccent,
    qrCanvas = AmPalette.lightQrCanvas,
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
    borderControl = AmPalette.darkBorderControl,
    textPrimary = AmPalette.darkText,
    textSecondary = AmPalette.darkTextSecondary,
    textTertiary = AmPalette.darkTextTertiary,
    accent = AmPalette.darkAccent,
    onAccent = AmPalette.darkOnAccent,
    accentContainer = AmPalette.darkAccent,
    onAccentContainer = AmPalette.darkOnAccent,
    accentText = AmPalette.darkAccentText,
    danger = AmPalette.darkDanger,
    onDanger = AmPalette.darkOnDanger,
    dangerContainer = AmPalette.darkDangerContainer,
    success = AmPalette.darkSuccess,
    successContainer = AmPalette.darkSuccessContainer,
    warning = AmPalette.darkWarning,
    warningContainer = AmPalette.darkWarningContainer,
    info = AmPalette.darkInfo,
    infoContainer = AmPalette.darkInfoContainer,
    focus = AmPalette.darkAccent,
    disabled = AmPalette.darkDisabled,
    onDisabled = AmPalette.darkOnDisabled,
    inverseSurface = AmPalette.darkInverseSurface,
    inverseOnSurface = AmPalette.darkInverseOnSurface,
    inverseAccent = AmPalette.darkInverseAccent,
    qrCanvas = AmPalette.lightQrCanvas,
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
    val SurfaceSecondary: Color @Composable get() = LocalAmColorScheme.current.surfaceSecondary
    val Divider: Color @Composable get() = LocalAmColorScheme.current.divider
    val TextPrimary: Color @Composable get() = LocalAmColorScheme.current.textPrimary
    val TextSecondary: Color @Composable get() = LocalAmColorScheme.current.textSecondary
    val TextTertiary: Color @Composable get() = LocalAmColorScheme.current.textTertiary
    val Accent: Color @Composable get() = LocalAmColorScheme.current.accent
    val OnAccent: Color @Composable get() = LocalAmColorScheme.current.onAccent
    /** Acento legible como texto (≥ 4.5:1 sobre background y surface). */
    val AccentText: Color @Composable get() = LocalAmColorScheme.current.accentText
    val Danger: Color @Composable get() = LocalAmColorScheme.current.danger
    val OnDanger: Color @Composable get() = LocalAmColorScheme.current.onDanger
    val Focus: Color @Composable get() = LocalAmColorScheme.current.focus
    val Disabled: Color @Composable get() = LocalAmColorScheme.current.disabled
    val OnDisabled: Color @Composable get() = LocalAmColorScheme.current.onDisabled
    /** Lienzo del QR: fondo claro garantizado en claro y oscuro. */
    val QrCanvas: Color @Composable get() = LocalAmColorScheme.current.qrCanvas
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
    /** Margen lateral de pantalla (múltiplo de 4). */
    val ScreenMargin = 16.dp
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
    /** Ancho máximo del contenido en medium/expanded. */
    val ContentMaxWidth = 720.dp
    val QrPreviewSize = 240.dp
    /** Tamaño de icono estándar de acciones y filas. */
    val IconSize = 24.dp
    /** Altura máxima de una lista dentro de un diálogo/hoja: alcanzable
     * con scroll cuando es larga; por debajo mide su contenido. */
    val DialogListMaxHeight = 320.dp
    /** Círculo tonal del glifo del estado vacío. */
    val EmptyStateGlyphSize = 64.dp
    /** Largo del trazo de las esquinas del encuadre del scanner. */
    val ScannerCornerLength = 24.dp
}

/** Radios moderados y consistentes (dp): la única fuente de formas. */
object AmRadius {
    val ExtraSmall = 2.dp
    val Small = 4.dp
    val Medium = 4.dp
    val Large = 8.dp
    val ExtraLarge = 8.dp
}

/**
 * Conjunto de formas compactas de Ant Design Mobile: [AmTheme] lo entrega a
 * `MaterialTheme.shapes` y los componentes envolventes fijan los radios
 * que M3 no mapea por rol (botones, campos, chips).
 */
object AmShapes {
    val ExtraSmall = RoundedCornerShape(AmRadius.ExtraSmall)
    val Small = RoundedCornerShape(AmRadius.Small)
    val Medium = RoundedCornerShape(AmRadius.Medium)
    val Large = RoundedCornerShape(AmRadius.Large)
    val ExtraLarge = RoundedCornerShape(AmRadius.ExtraLarge)
    /** Chips, badges y controles compactos: radio corto, no píldora. */
    val Pill = RoundedCornerShape(AmRadius.Small)
}

/**
 * Escala tipográfica en sp (respeta el escalado de fuente del sistema).
 * Roles semánticos del producto:
 * - Título de página: [TitleLarge] (18) en la barra superior.
 * - Título de sección: [SectionHeader] (14, Medium).
 * - Título de fila: Body (14) en AmListRow.
 * - Cuerpo y etiqueta de control: [Body]/[Label] (14).
 * - Texto secundario/ayuda/error: [Caption] (12) vía [AmTextStyles].
 */
object AmType {
    /** Display de bloque (32 sp): número/hero de tarjeta de datos. */
    val Display: TextUnit = 28.sp
    /** Titular de autenticación (28 sp). */
    val HeadlineMedium: TextUnit = 24.sp
    /** Encabezado de sección grande (24 sp). */
    val Headline: TextUnit = 22.sp
    /** Título de página en la barra superior (22 sp). */
    val TitleLarge: TextUnit = 18.sp
    /** Título de diálogo/tarjeta (20 sp). */
    val Title: TextUnit = 16.sp
    /** Cuerpo (16 sp). */
    val Body: TextUnit = 14.sp
    /** Etiqueta de botón/fila (14 sp). */
    val Label: TextUnit = 14.sp
    /** Badge/apoyo (12 sp). */
    val Caption: TextUnit = 12.sp
    /** Caption mínima (11 sp). */
    val CaptionSmall: TextUnit = 11.sp
    /** Encabezados de sección (14 sp). */
    val SectionHeader: TextUnit = 14.sp
    // Interlineados canónicos en sp (respetan el escalado, §accesibilidad).
    val LineHeightLabel: TextUnit = 20.sp
    val LineHeightBody: TextUnit = 22.sp
    val LineHeightCaption: TextUnit = 16.sp
    val LineHeightSection: TextUnit = 20.sp
    val LineHeightHeadlineLarge: TextUnit = 40.sp
    val LineHeightHeadlineMedium: TextUnit = 36.sp
    val LineHeightHeadlineSmall: TextUnit = 32.sp
    val LineHeightTitleLarge: TextUnit = 28.sp
    val LineHeightTitleMedium: TextUnit = 24.sp
    val LineHeightTitleSmall: TextUnit = 20.sp
    val LineHeightLabelSmall: TextUnit = 16.sp
    /**
     * Familia de sistema en TODOS los estilos — Roboto en Android, San
     * Francisco en iOS; ninguna fuente se empaqueta (ADR-0012, ADR-0013).
     */
    val FamilyUi: FontFamily = FontFamily.Default
}

/** Tracking de los roles tipográficos de Material 3 (valores por defecto). */
object AmTracking {
    val TitleMedium: TextUnit = 0.15.sp
    val TitleSmall: TextUnit = 0.1.sp
    val BodyLarge: TextUnit = 0.5.sp
    val BodyMedium: TextUnit = 0.25.sp
    val BodySmall: TextUnit = 0.4.sp
    val LabelLarge: TextUnit = 0.1.sp
    val LabelMedium: TextUnit = 0.5.sp
    val LabelSmall: TextUnit = 0.5.sp
}

/**
 * Tipografía Compose alineada con Ant Design Mobile: [AmTheme] la entrega a
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
        fontSize = AmType.HeadlineMedium,
        lineHeight = AmType.LineHeightHeadlineMedium,
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
        fontFamily = AmType.FamilyUi,
    ),
    titleMedium = TextStyle(
        fontSize = AmType.Body,
        lineHeight = AmType.LineHeightTitleMedium,
        letterSpacing = AmTracking.TitleMedium,
        fontWeight = FontWeight.Medium,
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
    /** Etiqueta de botón (rol labelLarge de M3). */
    val ButtonLabel = TextStyle(
        fontSize = AmType.Label,
        fontWeight = FontWeight.Medium,
        letterSpacing = AmTracking.LabelLarge,
        lineHeight = AmType.LineHeightLabel,
    )

    /** Cuerpo de texto (mensajes, filas). */
    val Body = TextStyle(
        fontSize = AmType.Body,
        lineHeight = AmType.LineHeightBody,
    )

    /** Subtítulo/metadato de fila (texto secundario). */
    val Caption = TextStyle(
        fontSize = AmType.Caption,
        lineHeight = AmType.LineHeightCaption,
    )

    /** Texto de apoyo (errores, ayuda, badges) — rol labelMedium de M3. */
    val Support = TextStyle(
        fontSize = AmType.Caption,
        fontWeight = FontWeight.Medium,
        letterSpacing = AmTracking.LabelMedium,
        lineHeight = AmType.LineHeightCaption,
    )

    /** Encabezado de sección (título de sección, 14 sp Medium). */
    val SectionHeader = TextStyle(
        fontSize = AmType.SectionHeader,
        lineHeight = AmType.LineHeightSection,
    )
}


/**
 * Movimiento de AgendaMobile: transiciones cortas y justificadas; con
 * movimiento reducido la duración es 0 (cambio en el sitio, §accesibilidad
 * — reducir no es "animar más rápido"). Una sola easing.
 */
object AmMotion {
    const val DurationShortMs = 150
    const val DurationReducedMs = 0

    object Easings {
        val Standard: Easing = CubicBezierEasing(0.1f, 0.9f, 0.2f, 1f)
    }
}

/**
 * Identidad de contexto (ADR-0013): acento estable derivado de forma
 * determinista del identificador del contexto. [background] es el bloque
 * de identidad (marcador de fila, tinte de pestañas de S06) y
 * [onAccent] el único texto admitido sobre él. El par es fijo y
 * verificado (≥ 4.5:1) en ambos temas.
 */
data class AmAccent(val id: String, val background: Color, val onAccent: Color)

/**
 * Paleta de identidad de contextos: 12 tonos contemporáneos de etiqueta
 * (no la paleta de acentos histórica) más el acento de sistema. Cada par
 * está verificado por AmAccentTest; onAccent es siempre negro o blanco
 * puro para mantener los pares robustos en ambas plataformas.
 */
object AmAccents {
    val System = AmAccent("system", Color(0xFF0958D9), Color(0xFFFFFFFF))

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
