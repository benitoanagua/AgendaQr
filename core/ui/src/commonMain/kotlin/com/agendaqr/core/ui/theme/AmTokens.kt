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
 * Tokens TDesign Mobile 1.16.2. Las primitivas permanecen internas; los
 * consumidores usan roles semánticos y [AmTheme] solo los adapta a M3.
 */
internal object AmPalette {
    val lightBrand = listOf(
        0xFFF2F3FF, 0xFFD9E1FF, 0xFFB5C7FF, 0xFF8EABFF, 0xFF618DFF,
        0xFF366EF4, 0xFF0052D9, 0xFF003CAB, 0xFF002A7C, 0xFF001A57,
    ).map(::Color)
    val darkBrand = listOf(
        0xFF1B2F51, 0xFF173463, 0xFF143975, 0xFF103D88, 0xFF0D429A,
        0xFF054BBE, 0xFF2667D4, 0xFF4582E6, 0xFF699EF5, 0xFF96BBF8,
    ).map(::Color)
    val lightWarningScale = listOf(
        0xFFFFF1E9, 0xFFFFD9C2, 0xFFFFB98C, 0xFFFA9550, 0xFFE37318,
        0xFFBE5A00, 0xFF954500, 0xFF713300, 0xFF532300, 0xFF3B1700,
    ).map(::Color)
    val darkWarningScale = listOf(
        0xFF4F2A1D, 0xFF582F21, 0xFF733C23, 0xFFA75D2B, 0xFFCF6E2D,
        0xFFDC7633, 0xFFE8935C, 0xFFECBF91, 0xFFEED7BF, 0xFFF3E9DC,
    ).map(::Color)
    val lightErrorScale = listOf(
        0xFFFFF0ED, 0xFFFFD8D2, 0xFFFFB9B0, 0xFFFF9285, 0xFFF6685D,
        0xFFD54941, 0xFFAD352F, 0xFF881F1C, 0xFF68070A, 0xFF490002,
    ).map(::Color)
    val darkErrorScale = listOf(
        0xFF472324, 0xFF5E2A2D, 0xFF703439, 0xFF83383E, 0xFFA03F46,
        0xFFC64751, 0xFFDE6670, 0xFFEC888E, 0xFFEDB1B6, 0xFFEECED0,
    ).map(::Color)
    val lightSuccessScale = listOf(
        0xFFE3F9E9, 0xFFC6F3D7, 0xFF92DAB2, 0xFF56C08D, 0xFF2BA471,
        0xFF008858, 0xFF006C45, 0xFF005334, 0xFF003B23, 0xFF002515,
    ).map(::Color)
    val darkSuccessScale = listOf(
        0xFF193A2A, 0xFF1A4230, 0xFF17533D, 0xFF0D7A55, 0xFF059465,
        0xFF43AF8A, 0xFF46BF96, 0xFF80D2B6, 0xFFB4E1D3, 0xFFDEEDE8,
    ).map(::Color)
    val lightGray = listOf(
        0xFFF3F3F3, 0xFFEEEEEE, 0xFFE7E7E7, 0xFFDCDCDC, 0xFFC5C5C5,
        0xFFA6A6A6, 0xFF8B8B8B, 0xFF777777, 0xFF5E5E5E, 0xFF4B4B4B,
        0xFF383838, 0xFF2C2C2C, 0xFF242424, 0xFF181818,
    ).map(::Color)
    val darkGray = listOf(
        0xFFF3F3F3, 0xFFEEEEEE, 0xFFE8E8E8, 0xFFDDDDDD, 0xFFC6C6C6,
        0xFFA6A6A6, 0xFF8B8B8B, 0xFF777777, 0xFF5E5E5E, 0xFF4B4B4B,
        0xFF383838, 0xFF2C2C2C, 0xFF242424, 0xFF181818,
    ).map(::Color)

    val lightBackground = lightGray[0]
    val darkBackground = darkGray[13]
    val lightSurface = Color.White
    val darkSurface = darkGray[12]
    val lightSurfaceSecondary = lightGray[0]
    val darkSurfaceSecondary = darkGray[11]
    val lightSurfaceContainerLow = lightSurface
    val darkSurfaceContainerLow = darkSurface
    val lightSurfaceContainer = lightBackground
    val darkSurfaceContainer = darkBackground
    val lightSurfaceContainerHigh = lightGray[2]
    val darkSurfaceContainerHigh = darkGray[10]
    val lightSurfaceContainerHighest = lightGray[3]
    val darkSurfaceContainerHighest = darkGray[9]
    val lightSurfaceActive = lightGray[2]
    val darkSurfaceActive = darkGray[11]
    val lightComponentActive = lightGray[5]
    val darkComponentActive = darkGray[9]
    val lightBorder = lightGray[3]
    val darkBorder = darkGray[8]
    val lightFunctionalBorder = lightGray[6]
    val darkFunctionalBorder = darkGray[7]
    val lightDivider = lightGray[2]
    val darkDivider = darkGray[10]
    val lightText = Color(0xE6000000)
    val darkText = Color.White
    val lightTextSecondary = Color(0x99000000)
    val darkTextSecondary = Color(0x8CFFFFFF)
    val lightTextTertiary = lightGray[8]
    val darkTextTertiary = darkGray[5]
    val lightAccent = lightBrand[6]
    val darkAccent = darkBrand[6]
    val lightAccentActive = lightBrand[7]
    val darkAccentActive = darkBrand[8]
    val lightAccentActiveContent = Color.White
    val darkAccentActiveContent = darkBrand[0]
    val lightAccentDisabled = lightBrand[2]
    val darkAccentDisabled = darkBrand[2]
    val lightAccentFocus = lightBrand[0]
    val darkAccentFocus = darkBrand[0]
    val lightAccentLightActive = lightBrand[1]
    val darkAccentLightActive = darkBrand[1]
    val lightOnAccent = Color.White
    val darkOnAccent = Color.White
    val lightAccentText = lightBrand[6]
    val darkAccentText = darkBrand[8]
    val lightAccentContainer = lightBrand[0]
    val darkAccentContainer = darkBrand[0]
    val lightDanger = lightErrorScale[6]
    val darkDanger = darkErrorScale[7]
    val lightDangerFill = lightErrorScale[6]
    val darkDangerFill = darkErrorScale[5]
    val lightDangerActive = lightErrorScale[7]
    val darkDangerActive = darkErrorScale[5]
    val lightDangerDisabled = lightErrorScale[2]
    val darkDangerDisabled = darkErrorScale[2]
    val lightDangerContainer = lightErrorScale[0]
    val darkDangerContainer = darkErrorScale[0]
    val lightSuccess = lightSuccessScale[6]
    val darkSuccessText = darkSuccessScale[5]
    val lightSuccessFill = lightSuccessScale[5]
    val darkSuccessFill = darkSuccessScale[4]
    val lightSuccessOnFill = Color.White
    val darkSuccessOnFill = darkGray[13]
    val lightSuccessContainer = lightSuccessScale[0]
    val darkSuccessContainer = darkSuccessScale[0]
    val lightWarning = lightWarningScale[6]
    val darkWarningText = darkWarningScale[6]
    val lightWarningFill = lightWarningScale[5]
    val darkWarningFill = darkWarningScale[4]
    val lightWarningOnFill = Color.White
    val darkWarningOnFill = darkGray[13]
    val lightWarningContainer = lightWarningScale[0]
    val darkWarningContainer = darkWarningScale[0]
    val lightDisabled = lightGray[1]
    val darkDisabled = darkGray[11]
    val lightOnDisabled = Color(0x42000000)
    val darkOnDisabled = Color(0x38FFFFFF)
    val lightInverseSurface = lightGray[13]
    val darkInverseSurface = darkGray[1]
    val lightInverseOnSurface = Color.White
    val darkInverseOnSurface = Color(0xE6000000)
    val lightFocus = lightBrand[6]
    val darkFocus = darkBrand[8]
    val lightInfoContainer = lightBrand[0]
    val darkInfoContainer = darkBrand[0]
    val lightScrim = Color(0x99000000)
    val darkScrim = Color(0x66000000)
    /** Lienzo del QR: blanco en ambos temas para preservar su escaneabilidad. */
    val qrCanvas = Color.White
    val transparent = Color.Transparent
}

data class AmColorScheme(
    val background: Color,
    val surface: Color,
    val surfaceSecondary: Color,
    val border: Color,
    val functionalBorder: Color,
    val divider: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val accent: Color,
    val accentActive: Color,
    val accentDisabled: Color,
    val accentFocus: Color,
    val accentLightActive: Color,
    val onAccent: Color,
    val accentContainer: Color,
    val onAccentContainer: Color,
    val accentText: Color,
    val danger: Color,
    val dangerFill: Color,
    val onDanger: Color,
    val dangerContainer: Color,
    val success: Color,
    val successFill: Color,
    val onSuccessFill: Color,
    val successContainer: Color,
    val warning: Color,
    val warningFill: Color,
    val onWarningFill: Color,
    val warningContainer: Color,
    val info: Color,
    val infoContainer: Color,
    val focus: Color,
    val disabled: Color,
    val onDisabled: Color,
    val inverseSurface: Color,
    val inverseOnSurface: Color,
    val scrim: Color,
    val qrCanvas: Color,
    val surfaceContainerLow: Color,
    val surfaceContainer: Color,
    val surfaceContainerHigh: Color,
    val surfaceContainerHighest: Color,
    val surfaceActive: Color,
    val componentActive: Color,
    val accentActiveContent: Color,
    val dangerActive: Color,
    val dangerDisabled: Color,
    val transparent: Color,
)

val LightAmColorScheme = AmColorScheme(
    background = AmPalette.lightBackground,
    surface = AmPalette.lightSurface,
    surfaceSecondary = AmPalette.lightSurfaceSecondary,
    border = AmPalette.lightBorder,
    functionalBorder = AmPalette.lightFunctionalBorder,
    divider = AmPalette.lightDivider,
    textPrimary = AmPalette.lightText,
    textSecondary = AmPalette.lightTextSecondary,
    textTertiary = AmPalette.lightTextTertiary,
    accent = AmPalette.lightAccent,
    accentActive = AmPalette.lightAccentActive,
    accentDisabled = AmPalette.lightAccentDisabled,
    accentFocus = AmPalette.lightAccentFocus,
    accentLightActive = AmPalette.lightAccentLightActive,
    onAccent = AmPalette.lightOnAccent,
    accentContainer = AmPalette.lightAccentContainer,
    onAccentContainer = AmPalette.lightAccentText,
    accentText = AmPalette.lightAccentText,
    danger = AmPalette.lightDanger,
    dangerFill = AmPalette.lightDangerFill,
    onDanger = Color.White,
    dangerContainer = AmPalette.lightDangerContainer,
    success = AmPalette.lightSuccess,
    successFill = AmPalette.lightSuccessFill,
    onSuccessFill = AmPalette.lightSuccessOnFill,
    successContainer = AmPalette.lightSuccessContainer,
    warning = AmPalette.lightWarning,
    warningFill = AmPalette.lightWarningFill,
    onWarningFill = AmPalette.lightWarningOnFill,
    warningContainer = AmPalette.lightWarningContainer,
    info = AmPalette.lightAccentText,
    infoContainer = AmPalette.lightInfoContainer,
    focus = AmPalette.lightFocus,
    disabled = AmPalette.lightDisabled,
    onDisabled = AmPalette.lightOnDisabled,
    inverseSurface = AmPalette.lightInverseSurface,
    inverseOnSurface = AmPalette.lightInverseOnSurface,
    scrim = AmPalette.lightScrim,
    qrCanvas = AmPalette.qrCanvas,
    surfaceContainerLow = AmPalette.lightSurfaceContainerLow,
    surfaceContainer = AmPalette.lightSurfaceContainer,
    surfaceContainerHigh = AmPalette.lightSurfaceContainerHigh,
    surfaceContainerHighest = AmPalette.lightSurfaceContainerHighest,
    surfaceActive = AmPalette.lightSurfaceActive,
    componentActive = AmPalette.lightComponentActive,
    accentActiveContent = AmPalette.lightAccentActiveContent,
    dangerActive = AmPalette.lightDangerActive,
    dangerDisabled = AmPalette.lightDangerDisabled,
    transparent = AmPalette.transparent,
)

val DarkAmColorScheme = AmColorScheme(
    background = AmPalette.darkBackground,
    surface = AmPalette.darkSurface,
    surfaceSecondary = AmPalette.darkSurfaceSecondary,
    border = AmPalette.darkBorder,
    functionalBorder = AmPalette.darkFunctionalBorder,
    divider = AmPalette.darkDivider,
    textPrimary = AmPalette.darkText,
    textSecondary = AmPalette.darkTextSecondary,
    textTertiary = AmPalette.darkTextTertiary,
    accent = AmPalette.darkAccent,
    accentActive = AmPalette.darkAccentActive,
    accentDisabled = AmPalette.darkAccentDisabled,
    accentFocus = AmPalette.darkAccentFocus,
    accentLightActive = AmPalette.darkAccentLightActive,
    onAccent = AmPalette.darkOnAccent,
    accentContainer = AmPalette.darkAccentContainer,
    onAccentContainer = AmPalette.darkAccentText,
    accentText = AmPalette.darkAccentText,
    danger = AmPalette.darkDanger,
    dangerFill = AmPalette.darkDangerFill,
    onDanger = Color.White,
    dangerContainer = AmPalette.darkDangerContainer,
    success = AmPalette.darkSuccessText,
    successFill = AmPalette.darkSuccessFill,
    onSuccessFill = AmPalette.darkSuccessOnFill,
    successContainer = AmPalette.darkSuccessContainer,
    warning = AmPalette.darkWarningText,
    warningFill = AmPalette.darkWarningFill,
    onWarningFill = AmPalette.darkWarningOnFill,
    warningContainer = AmPalette.darkWarningContainer,
    info = AmPalette.darkAccentText,
    infoContainer = AmPalette.darkInfoContainer,
    focus = AmPalette.darkFocus,
    disabled = AmPalette.darkDisabled,
    onDisabled = AmPalette.darkOnDisabled,
    inverseSurface = AmPalette.darkInverseSurface,
    inverseOnSurface = AmPalette.darkInverseOnSurface,
    scrim = AmPalette.darkScrim,
    qrCanvas = AmPalette.qrCanvas,
    surfaceContainerLow = AmPalette.darkSurfaceContainerLow,
    surfaceContainer = AmPalette.darkSurfaceContainer,
    surfaceContainerHigh = AmPalette.darkSurfaceContainerHigh,
    surfaceContainerHighest = AmPalette.darkSurfaceContainerHighest,
    surfaceActive = AmPalette.darkSurfaceActive,
    componentActive = AmPalette.darkComponentActive,
    accentActiveContent = AmPalette.darkAccentActiveContent,
    dangerActive = AmPalette.darkDangerActive,
    dangerDisabled = AmPalette.darkDangerDisabled,
    transparent = AmPalette.transparent,
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
    val Border: Color @Composable get() = LocalAmColorScheme.current.border
    val FunctionalBorder: Color @Composable get() = LocalAmColorScheme.current.functionalBorder
    val Divider: Color @Composable get() = LocalAmColorScheme.current.divider
    val TextPrimary: Color @Composable get() = LocalAmColorScheme.current.textPrimary
    val TextSecondary: Color @Composable get() = LocalAmColorScheme.current.textSecondary
    val TextTertiary: Color @Composable get() = LocalAmColorScheme.current.textTertiary
    val Accent: Color @Composable get() = LocalAmColorScheme.current.accent
    val AccentActive: Color @Composable get() = LocalAmColorScheme.current.accentActive
    val AccentDisabled: Color @Composable get() = LocalAmColorScheme.current.accentDisabled
    val AccentFocus: Color @Composable get() = LocalAmColorScheme.current.accentFocus
    val AccentLightActive: Color @Composable get() = LocalAmColorScheme.current.accentLightActive
    val OnAccent: Color @Composable get() = LocalAmColorScheme.current.onAccent
    val AccentText: Color @Composable get() = LocalAmColorScheme.current.accentText
    val AccentContainer: Color @Composable get() = LocalAmColorScheme.current.accentContainer
    val OnAccentContainer: Color @Composable get() = LocalAmColorScheme.current.onAccentContainer
    val Danger: Color @Composable get() = LocalAmColorScheme.current.danger
    val DangerFill: Color @Composable get() = LocalAmColorScheme.current.dangerFill
    val DangerActive: Color @Composable get() = LocalAmColorScheme.current.dangerActive
    val DangerDisabled: Color @Composable get() = LocalAmColorScheme.current.dangerDisabled
    val OnDanger: Color @Composable get() = LocalAmColorScheme.current.onDanger
    val DangerContainer: Color @Composable get() = LocalAmColorScheme.current.dangerContainer
    val Success: Color @Composable get() = LocalAmColorScheme.current.success
    val SuccessFill: Color @Composable get() = LocalAmColorScheme.current.successFill
    val OnSuccessFill: Color @Composable get() = LocalAmColorScheme.current.onSuccessFill
    val SuccessContainer: Color @Composable get() = LocalAmColorScheme.current.successContainer
    val Warning: Color @Composable get() = LocalAmColorScheme.current.warning
    val WarningFill: Color @Composable get() = LocalAmColorScheme.current.warningFill
    val OnWarningFill: Color @Composable get() = LocalAmColorScheme.current.onWarningFill
    val WarningContainer: Color @Composable get() = LocalAmColorScheme.current.warningContainer
    val Info: Color @Composable get() = LocalAmColorScheme.current.info
    val InfoContainer: Color @Composable get() = LocalAmColorScheme.current.infoContainer
    val SurfaceContainerHighest: Color @Composable get() = LocalAmColorScheme.current.surfaceContainerHighest
    val SurfaceContainerHigh: Color @Composable get() = LocalAmColorScheme.current.surfaceContainerHigh
    val SurfaceActive: Color @Composable get() = LocalAmColorScheme.current.surfaceActive
    val ComponentActive: Color @Composable get() = LocalAmColorScheme.current.componentActive
    val AccentActiveContent: Color @Composable get() = LocalAmColorScheme.current.accentActiveContent
    val SurfaceContainerLow: Color @Composable get() = LocalAmColorScheme.current.surfaceContainerLow
    val InverseSurface: Color @Composable get() = LocalAmColorScheme.current.inverseSurface
    val InverseOnSurface: Color @Composable get() = LocalAmColorScheme.current.inverseOnSurface
    val Focus: Color @Composable get() = LocalAmColorScheme.current.focus
    val Disabled: Color @Composable get() = LocalAmColorScheme.current.disabled
    val OnDisabled: Color @Composable get() = LocalAmColorScheme.current.onDisabled
    val QrCanvas: Color @Composable get() = LocalAmColorScheme.current.qrCanvas
    val Transparent: Color = Color.Transparent
}

object AmSpacing {
    val None = 0.dp
    val Xs = 4.dp
    val Sm = 8.dp
    val Md = 12.dp
    val Lg = 16.dp
    val Xl = 24.dp
    val Xxl = 32.dp
    val Xxxl = 48.dp
    val Huge = 80.dp
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
    val ButtonLargeHeight = 48.dp
    val ButtonCompactHeight = 40.dp
    val ButtonSmallHeight = 32.dp
    val ButtonExtraSmallHeight = 28.dp
    val ButtonLargeHorizontalPadding = 20.dp
    val ButtonMediumIconSize = 20.dp
    /** Altura visual de píldoras/chips compactos. */
    val ChipCompactHeight = 32.dp
    /** Altura visual compacta de entradas. */
    val FieldCompactHeight = 40.dp
    /** Altura visual de la barra superior. */
    val TopBarHeight = 48.dp
    /** Altura visual y objetivo de cada destino de la barra inferior. */
    val NavBarHeight = 48.dp
    /** Ancho del riel de navegación para ventanas amplias. */
    val NavRailWidth = 80.dp
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
    /** Inset del divisor de una celda respecto al borde del grupo. */
    val ListDividerInset = 16.dp
    /** Alto mínimo de la cabecera de un grupo de celdas. */
    val SectionHeaderHeight = 45.dp
    /** Tamaño del indicador de pestaña. */
    val TabIndicatorWidth = 16.dp
    val TabIndicatorHeight = 3.dp
    /** Altura máxima de una lista dentro de un diálogo/hoja: alcanzable
     * con scroll cuando es larga; por debajo mide su contenido. */
    val DialogListMaxHeight = 320.dp
    /** Ancho mínimo del popup de un diálogo (Dialog de la referencia:
     * 280 px → 280 dp). */
    val DialogMinWidth = 311.dp
    /** Fracción del ancho disponible para diálogos en ventanas estrechas. */
    const val DialogMaxWidthFraction = 0.9f
    /** Círculo tonal del glifo del estado vacío. */
    val EmptyStateGlyphSize = 96.dp
    /** Glifo del resultado de un flujo completado. */
    val ResultGlyphSize = 80.dp
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

object AmRadius {
    val ExtraSmall = 3.dp
    val Band = 4.dp
    val Small = 6.dp
    val Medium = 9.dp
    val Large = 12.dp
    val ExtraLarge = 12.dp
    val GridIcon = 6.dp
    val Round = 999.dp
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
    val Pill = RoundedCornerShape(AmRadius.Round)
}

object AmType {
    val Display: TextUnit = 64.sp
    val DisplayMedium: TextUnit = 48.sp
    val HeadlineLarge: TextUnit = 36.sp
    val Headline: TextUnit = 28.sp
    val HeadlineSmall: TextUnit = 24.sp
    val TitleLarge: TextUnit = 20.sp
    val Title: TextUnit = 18.sp
    val TitleMedium: TextUnit = 16.sp
    val TitleSmall: TextUnit = 14.sp
    val Amount: TextUnit = 28.sp
    val AmountSymbol: TextUnit = 14.sp
    val RowTitle: TextUnit = 16.sp
    val SectionHeader: TextUnit = 14.sp
    val Body: TextUnit = 14.sp
    val Label: TextUnit = 16.sp
    val BodySmall: TextUnit = 12.sp
    val Caption: TextUnit = 12.sp
    val CaptionSmall: TextUnit = 12.sp
    val LineHeightLabel: TextUnit = 24.sp
    val LineHeightDialog: TextUnit = 26.sp
    val LineHeightBody: TextUnit = 22.sp
    val LineHeightBodySmall: TextUnit = 20.sp
    val LineHeightCaption: TextUnit = 20.sp
    val LineHeightSection: TextUnit = 24.sp
    val LineAmount: TextUnit = 36.sp
    val LineAmountSymbol: TextUnit = 20.sp
    val LineHeightRowTitle: TextUnit = 24.sp
    val LineHeightHeadlineLarge: TextUnit = 44.sp
    val LineHeightHeadlineMedium: TextUnit = 36.sp
    val LineHeightHeadlineSmall: TextUnit = 32.sp
    val LineHeightTitleLarge: TextUnit = 28.sp
    val LineHeightTitleMedium: TextUnit = 24.sp
    val LineHeightTitleSmall: TextUnit = 22.sp
    val LineHeightLabelSmall: TextUnit = 20.sp
    val FamilyUi: FontFamily = FontFamily.Default
}

object AmTracking {
    val TitleMedium: TextUnit = 0.sp
    val TitleSmall: TextUnit = 0.sp
    val BodyLarge: TextUnit = 0.sp
    val BodyMedium: TextUnit = 0.sp
    val BodySmall: TextUnit = 0.sp
    val LabelLarge: TextUnit = 0.sp
    val LabelMedium: TextUnit = 0.sp
    val LabelSmall: TextUnit = 0.sp
    val ButtonLabel: TextUnit = 0.sp
}

/**
 * Tipografía Material 3: [AmTheme] la entrega a
 * `MaterialTheme(typography = …)` y cada componente M3 hereda la misma
 * familia y tamaños.
 */
val AmMaterialTypography: Typography = Typography(
    headlineLarge = TextStyle(
        fontSize = AmType.HeadlineLarge,
        lineHeight = AmType.LineHeightHeadlineLarge,
        fontWeight = FontWeight.SemiBold,
        fontFamily = AmType.FamilyUi,
    ),
    headlineMedium = TextStyle(
        fontSize = AmType.Headline,
        lineHeight = AmType.LineHeightHeadlineMedium,
        fontWeight = FontWeight.SemiBold,
        fontFamily = AmType.FamilyUi,
    ),
    headlineSmall = TextStyle(
        fontSize = AmType.HeadlineSmall,
        lineHeight = AmType.LineHeightHeadlineSmall,
        fontWeight = FontWeight.SemiBold,
        fontFamily = AmType.FamilyUi,
    ),
    titleLarge = TextStyle(
        fontSize = AmType.TitleLarge,
        lineHeight = AmType.LineHeightTitleLarge,
        fontWeight = FontWeight.SemiBold,
        fontFamily = AmType.FamilyUi,
    ),
    titleMedium = TextStyle(
        fontSize = AmType.TitleMedium,
        lineHeight = AmType.LineHeightTitleMedium,
        letterSpacing = AmTracking.TitleMedium,
        fontWeight = FontWeight.SemiBold,
        fontFamily = AmType.FamilyUi,
    ),
    titleSmall = TextStyle(
        fontSize = AmType.TitleSmall,
        lineHeight = AmType.LineHeightTitleSmall,
        letterSpacing = AmTracking.TitleSmall,
        fontWeight = FontWeight.SemiBold,
        fontFamily = AmType.FamilyUi,
    ),
    bodyLarge = TextStyle(
        fontSize = AmType.Label,
        lineHeight = AmType.LineHeightLabel,
        letterSpacing = AmTracking.BodyLarge,
        fontFamily = AmType.FamilyUi,
    ),
    bodyMedium = TextStyle(
        fontSize = AmType.Body,
        lineHeight = AmType.LineHeightBody,
        letterSpacing = AmTracking.BodyMedium,
        fontFamily = AmType.FamilyUi,
    ),
    bodySmall = TextStyle(
        fontSize = AmType.BodySmall,
        lineHeight = AmType.LineHeightBodySmall,
        letterSpacing = AmTracking.BodySmall,
        fontFamily = AmType.FamilyUi,
    ),
    labelLarge = TextStyle(
        fontSize = AmType.Label,
        lineHeight = AmType.LineHeightLabel,
        letterSpacing = AmTracking.LabelLarge,
        fontWeight = FontWeight.SemiBold,
        fontFamily = AmType.FamilyUi,
    ),
    labelMedium = TextStyle(
        fontSize = AmType.Body,
        lineHeight = AmType.LineHeightBody,
        letterSpacing = AmTracking.LabelMedium,
        fontWeight = FontWeight.SemiBold,
        fontFamily = AmType.FamilyUi,
    ),
    labelSmall = TextStyle(
        fontSize = AmType.BodySmall,
        lineHeight = AmType.LineHeightLabelSmall,
        letterSpacing = AmTracking.LabelSmall,
        fontWeight = FontWeight.SemiBold,
        fontFamily = AmType.FamilyUi,
    ),
)

/**
 * Estilos tipográficos canónicos del sistema: reúnen fontSize, peso,
 * tracking e interlineado. Los componentes consumen estos estilos en
 * lugar de repetir tríos de tokens.
 */
object AmTextStyles {
    val ButtonLabel = TextStyle(
        fontSize = AmType.Label,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = AmTracking.ButtonLabel,
        lineHeight = AmType.LineHeightLabel,
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

    val Amount = TextStyle(
        fontSize = AmType.Amount,
        fontWeight = FontWeight.SemiBold,
        lineHeight = AmType.LineAmount,
        fontFeatureSettings = "tnum",
    )

    val AmountSymbol = TextStyle(
        fontSize = AmType.AmountSymbol,
        fontWeight = FontWeight.Bold,
        lineHeight = AmType.LineAmountSymbol,
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

    /** Cabecera de grupo de celdas: 14/22 regular sobre superficie secundaria. */
    val SectionHeader = TextStyle(
        fontSize = AmType.SectionHeader,
        fontWeight = FontWeight.Normal,
        lineHeight = AmType.LineHeightBody,
    )

    val Title = TextStyle(
        fontSize = AmType.Title,
        fontWeight = FontWeight.SemiBold,
        lineHeight = AmType.LineHeightDialog,
    )
}


object AmMotion {
    const val DurationShortMs = 200
    const val DurationMediumMs = 240
    const val DurationLongMs = 280
    const val DurationReducedMs = 0
    const val EasingControlX1 = 0.38f
    const val EasingControlY1 = 0f
    const val EasingControlX2 = 0.24f
    const val EasingControlY2 = 1f

    object Easings {
        val Standard: Easing = CubicBezierEasing(
            EasingControlX1,
            EasingControlY1,
            EasingControlX2,
            EasingControlY2,
        )
    }
}

/**
 * Source shadows have multiple CSS layers. Compose keeps the largest-blur
 * layer; its blur radius is halved for elevation and its opacity is retained.
 */
enum class AmElevation(
    val elevation: Dp,
    val lightAlpha: Float,
    val darkAlpha: Float,
) {
    None(0.dp, 0f, 0f),
    Shadow1(5.dp, 0.05f, 0.08f),
    Shadow2(7.dp, 0.05f, 0.10f),
    Shadow3(15.dp, 0.05f, 0.12f),
    Shadow4(4.dp, 0.06f, 0.06f),
}

@Composable
fun Modifier.amElevation(
    level: AmElevation,
    shape: Shape = RectangleShape,
    clip: Boolean = false,
): Modifier {
    if (level == AmElevation.None) return this
    val alpha = if (LocalAmColorScheme.current == DarkAmColorScheme) level.darkAlpha else level.lightAlpha
    val shadowColor = Color.Black.copy(alpha = alpha)
    return shadow(
        elevation = level.elevation,
        shape = shape,
        clip = clip,
        ambientColor = shadowColor,
        spotColor = shadowColor,
    )
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
