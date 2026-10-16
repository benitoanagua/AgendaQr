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
 * Tokens del sistema de diseño de Agenda QR (ADR-0014).
 *
 * Referencia visual: Ant Design Mobile (variables de tema claro/oscuro,
 * componentes y patrones oficiales); navegación según convenciones de
 * Android e iOS. Arquitectura de capas (contrato normativo
 * docs/05-design-system/00-contrato-normativo-diseno.md):
 * 1. Tokens privados: [AmPalette] — primitivas por tema, sin exposición.
 * 2. Tokens semánticos: [AmColorScheme], [AmColor], [AmSpacing],
 *    [AmRadius]/[AmShapes], [AmMetrics], [AmType]/[AmTextStyles],
 *    [AmMotion], [AmAccent] — la única capa que el producto consume.
 * 3. Tema: [AmTheme] — puente a Material 3 (implementación, no autoridad).
 *
 * Donde la referencia no alcanza WCAG AA, los roles se recalibran
 * mínimamente (contrato §4.4) y cada par queda fijado por AmSchemeTest;
 * ningún estado depende solo del color.
 */

/** Primitivas por tema: privadas de la capa de tokens. */
internal object AmPalette {
    // Tema claro (referencia: theme-default de Ant Design Mobile; los
    // roles de texto y de estado sobre fondo se recalibran a §4.4).
    val lightBackground = Color(0xFFF5F5F5)
    val lightSurface = Color(0xFFFFFFFF)
    val lightSurfaceSecondary = Color(0xFFF5F5F5)
    val lightSurfaceContainerLow = Color(0xFFFFFFFF)
    val lightSurfaceContainer = Color(0xFFF7F7F7)
    val lightSurfaceContainerHigh = Color(0xFFEFEFEF)
    val lightSurfaceContainerHighest = Color(0xFFE8E8E8)
    val lightBorder = Color(0xFFEEEEEE)
    val lightDivider = Color(0xFFEEEEEE)
    val lightText = Color(0xFF333333)
    val lightTextSecondary = Color(0xFF666666)
    val lightTextTertiary = Color(0xFF6E6E6E)
    val lightAccent = Color(0xFF1668DC)
    val lightOnAccent = Color(0xFFFFFFFF)
    val lightAccentText = Color(0xFF1668DC)
    val lightWathet = Color(0xFFE7F1FF)
    val lightDanger = Color(0xFFD21F2E)
    val lightOnDanger = Color(0xFFFFFFFF)
    val lightDangerContainer = Color(0xFFFDEBEA)
    val lightSuccess = Color(0xFF0A7A44)
    val lightSuccessContainer = Color(0xFFE2F5E8)
    val lightWarning = Color(0xFFA85A00)
    val lightWarningContainer = Color(0xFFFDF3DC)
    val lightDisabled = Color(0xFFF0F0F0)
    val lightOnDisabled = Color(0xFF767676)
    val lightInverseSurface = Color(0xFF1A1A1A)
    val lightInverseOnSurface = Color(0xFFFFFFFF)
    val lightInverseAccent = Color(0xFF3086FF)
    /** Lienzo del QR: fondo claro GARANTIZADO en ambos temas (zona de
     * silencio escaneable); el marco tonal lo aporta AmMediaFrame. */
    val lightQrCanvas = Color(0xFFFFFFFF)

    // Tema oscuro (referencia: theme-dark de Ant Design Mobile; los roles
    // de estado y acento adoptan sus valores oficiales; donde el par
    // oficial no alcanza WCAG AA se recalibra mínimamente — contrato §4.4:
    // wathet, onAccent/accentText/onDanger y el terciario).
    val darkBackground = Color(0xFF1A1A1A)
    val darkSurface = Color(0xFF1F1F1F)
    val darkSurfaceSecondary = Color(0xFF0A0A0A)
    val darkSurfaceContainerLow = Color(0xFF252525)
    val darkSurfaceContainer = Color(0xFF2A2A2A)
    val darkSurfaceContainerHigh = Color(0xFF303030)
    val darkSurfaceContainerHighest = Color(0xFF3A3A3A)
    val darkBorder = Color(0xFF2B2B2B)
    val darkDivider = Color(0xFF2B2B2B)
    val darkText = Color(0xFFE6E6E6)
    val darkTextSecondary = Color(0xFFB3B3B3)
    val darkTextTertiary = Color(0xFF8C8C8C)
    val darkAccent = Color(0xFF3086FF)
    val darkOnAccent = Color(0xFF0A1120)
    val darkAccentText = Color(0xFF4F96FF)
    val darkWathet = Color(0xFF0B2140)
    val darkDanger = Color(0xFFFF4A58)
    val darkOnDanger = Color(0xFF1A1A1A)
    val darkDangerContainer = Color(0xFF3A1512)
    val darkSuccess = Color(0xFF34B368)
    val darkSuccessContainer = Color(0xFF0E2C1C)
    val darkWarning = Color(0xFFFFA930)
    val darkWarningContainer = Color(0xFF3A2B0E)
    val darkDisabled = Color(0xFF3A3A3A)
    val darkOnDisabled = Color(0xFF9E9E9E)
    val darkInverseSurface = Color(0xFFF5F5F5)
    val darkInverseOnSurface = Color(0xFF1A1A1A)
    val darkInverseAccent = Color(0xFF3086FF)
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
    info = AmPalette.lightAccent,
    infoContainer = AmPalette.lightWathet,
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
    info = AmPalette.darkAccent,
    infoContainer = AmPalette.darkWathet,
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
}

/** Radios de la referencia (tag 2, botón 4, búsqueda 6, tarjeta 8,
 * hoja 12): la única fuente de formas. */
object AmRadius {
    val ExtraSmall = 2.dp
    val Small = 4.dp
    val Medium = 6.dp
    val Large = 8.dp
    val ExtraLarge = 12.dp
}

/**
 * Conjunto de formas: [AmTheme] lo entrega a
 * `MaterialTheme.shapes` y los componentes envolventes fijan los radios
 * que M3 no mapea por rol (botones, campos, chips).
 */
object AmShapes {
    val ExtraSmall = RoundedCornerShape(AmRadius.ExtraSmall)
    val Small = RoundedCornerShape(AmRadius.Small)
    val Medium = RoundedCornerShape(AmRadius.Medium)
    val Large = RoundedCornerShape(AmRadius.Large)
    val ExtraLarge = RoundedCornerShape(AmRadius.ExtraLarge)
    /** Chips, badges y pastillas: media píldora. */
    val Pill = RoundedCornerShape(percent = 50)
}

/**
 * Escala tipográfica en sp (respeta el escalado de fuente del sistema).
 * Escala de Ant Design Mobile (ADR-0014, ADR-0017): título 18; cuerpo,
 * botón, título de fila, entrada y pestañas 17 (font-size-7); etiqueta,
 * contenido de modal, cápsula y extra de lista 15 (font-size-5 = main);
 * descripción y metadato 13; apoyo/ayuda/error 12; tag y barra inferior
 * 11. Importe 26 y Display 32 / Headline 24 son extensiones financieras
 * sin equivalente en la referencia. Pesos: normal salvo el título de
 * diálogo (bold) y la etiqueta de botón (Medium, desviación documentada).
 */
object AmType {
    /** Display de bloque (32 sp): número/hero de tarjeta de datos. */
    val Display: TextUnit = 32.sp
    /** Encabezado destacado (24 sp, techo de la escala). */
    val Headline: TextUnit = 24.sp
    /** Título de pantalla en la barra superior (18 sp). */
    val TitleLarge: TextUnit = 18.sp
    /** Título de diálogo/modal centrado (18 sp, NavBar/Modal title). */
    val Title: TextUnit = 18.sp
    /** Cuerpo, botón, título de fila, entrada y pestañas (17 sp). */
    val Body: TextUnit = 17.sp
    /** Descripción y metadato (13 sp, font-size-main de la referencia). */
    val BodySmall: TextUnit = 13.sp
    /** Etiqueta de acción de texto, contenido de modal, cápsula y extra de lista (15 sp). */
    val Label: TextUnit = 15.sp
    /** Título de fila (17 sp, List.Item de la referencia). */
    val RowTitle: TextUnit = 17.sp
    /** Importe (26 sp): hero de detalle y entrada de monto (extensión financiera). */
    val Amount: TextUnit = 26.sp
    /** Apoyo/ayuda/error (12 sp). */
    val Caption: TextUnit = 12.sp
    /** Caption mínima (11 sp, etiquetas de barra y tags). */
    val CaptionSmall: TextUnit = 11.sp
    /** Encabezado de sección (15 sp: List header de la referencia). */
    val SectionHeader: TextUnit = 15.sp
    // Interlineados canónicos en sp (≈ 1.4× su tamaño como la referencia;
    // respetan el escalado, §accesibilidad).
    val LineHeightLabel: TextUnit = 20.sp
    val LineHeightBody: TextUnit = 24.sp
    val LineHeightBodySmall: TextUnit = 18.sp
    val LineHeightCaption: TextUnit = 16.sp
    val LineHeightSection: TextUnit = 20.sp
    val LineAmount: TextUnit = 32.sp
    val LineHeightRowTitle: TextUnit = 24.sp
    val LineHeightHeadlineLarge: TextUnit = 40.sp
    val LineHeightHeadlineSmall: TextUnit = 32.sp
    val LineHeightTitleLarge: TextUnit = 24.sp
    val LineHeightTitleMedium: TextUnit = 24.sp
    val LineHeightTitleSmall: TextUnit = 20.sp
    val LineHeightLabelSmall: TextUnit = 16.sp
    /**
     * Familia de sistema en TODOS los estilos — Roboto en Android, San
     * Francisco en iOS; ninguna fuente se empaqueta (ADR-0012, ADR-0013).
     */
    val FamilyUi: FontFamily = FontFamily.Default
}

/** Tracking neutro: la interfaz no usa tracking amplio como característica
 * general (brief de rediseño); los roles conservan espaciado cero salvo
 * justificación por caso. */
object AmTracking {
    val TitleMedium: TextUnit = 0.sp
    val TitleSmall: TextUnit = 0.sp
    val BodyLarge: TextUnit = 0.sp
    val BodyMedium: TextUnit = 0.sp
    val BodySmall: TextUnit = 0.sp
    val LabelLarge: TextUnit = 0.sp
    val LabelMedium: TextUnit = 0.sp
    val LabelSmall: TextUnit = 0.sp
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
    /** Etiqueta de botón (17 sp Medium: desviación documentada — la
     * referencia usa peso normal; el peso medio conserva el rango de la
     * etiqueta sobre superficies densas). */
    val ButtonLabel = TextStyle(
        fontSize = AmType.Body,
        fontWeight = FontWeight.Medium,
        letterSpacing = AmTracking.LabelLarge,
        lineHeight = AmType.LineHeightBody,
    )

    /** Cuerpo de texto (mensajes, filas): 17/24. */
    val Body = TextStyle(
        fontSize = AmType.Body,
        lineHeight = AmType.LineHeightBody,
    )

    /** Etiqueta de acción de texto, contenido de modal, cápsula y extra
     * de lista (15 sp, font-size-main de la referencia). */
    val Label = TextStyle(
        fontSize = AmType.Label,
        lineHeight = AmType.LineHeightLabel,
    )

    /** Metadato/descripción (13 sp, font-size-main). */
    val BodySmall = TextStyle(
        fontSize = AmType.BodySmall,
        lineHeight = AmType.LineHeightBodySmall,
    )

    /** Título de fila (17 sp normal, List.Item). */
    val RowTitle = TextStyle(
        fontSize = AmType.RowTitle,
        lineHeight = AmType.LineHeightRowTitle,
    )

    /** Importe (26 sp SemiBold — extensión financiera). */
    val Amount = TextStyle(
        fontSize = AmType.Amount,
        fontWeight = FontWeight.SemiBold,
        lineHeight = AmType.LineAmount,
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

    /** Encabezado de sección (15 sp normal: List header de la
     * referencia; el color lo decide el llamador). */
    val SectionHeader = TextStyle(
        fontSize = AmType.SectionHeader,
        lineHeight = AmType.LineHeightSection,
    )
}


/**
 * Movimiento: transiciones cortas y justificadas; con
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
 * Identidad de contexto: acento estable derivado de forma
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
    val System = AmAccent("system", Color(0xFF1668DC), Color(0xFFFFFFFF))

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
