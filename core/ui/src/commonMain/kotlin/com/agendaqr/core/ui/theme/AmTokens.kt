package com.agendaqr.core.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
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
import agendaqr.core.ui.generated.resources.Res
import agendaqr.core.ui.generated.resources.inter_bold
import agendaqr.core.ui.generated.resources.inter_medium
import agendaqr.core.ui.generated.resources.inter_regular
import agendaqr.core.ui.generated.resources.inter_semi_bold
import org.jetbrains.compose.resources.Font
import kotlin.math.pow

/**
 * Capa de tokens del sistema visual. Los valores primitivos son una
 * transcripción de `docs/05-design-system/09-instantanea-tokens-polaris-9.4.2.md`
 * (tarball `@shopify/polaris-tokens@9.4.2`, capturado el 2026-11-17).
 *
 * Reglas de esta capa:
 * - Es el ÚNICO archivo de producción autorizado a contener literales
 *   visuales (hex, dp, sp, pesos, radios, duraciones, alfas).
 * - Las primitivas son internas; los consumidores usan [AmColorScheme],
 *   [AmColor], [AmSpacing], [AmMetrics], [AmType], [AmMotion] y [AmElevation].
 * - Las derivaciones necesarias para móvil,tema oscuro y accesibilidad están
 *   gobernadas por D-01, D-02, D-07, X-05 y X-06 del contrato normativo.
 * - No se usa ningún nombre, logotipo ni marca del sistema de origen en el
 *   producto; las referencias son solo de ingeniería.
 */
internal object AmPalette {
    // Escalas publicadas por el paquete de tokens (valores rgba de la instantánea).
    val white = Color(0xFFFFFFFF)
    val black = Color(0xFF000000)
    val gray50 = Color(0xFFF1F1F1)
    val gray100 = Color(0xFFEBEBEB)
    val gray200 = Color(0xFFE3E3E3)
    val gray300 = Color(0xFFD4D4D4)
    val gray400 = Color(0xFFB5B5B5)
    val gray500 = Color(0xFF8A8A8A)
    val gray600 = Color(0xFF616161)
    val gray700 = Color(0xFF4A4A4A)
    val gray800 = Color(0xFF303030)
    val gray900 = Color(0xFF1A1A1A)
    /** `--p-color-text` claro y `--p-color-text` de `dark-experimental`. */
    val lightText = gray800
    val darkText = Color(0xFFE3E3E3)

    val emphasis = Color(0xFF005BDB)
    val emphasisHover = Color(0xFF004299)
    val emphasisActive = Color(0xFF002E6A)
    val emphasisSurface = Color(0xFFF0F2FF)
    val emphasisSurfaceHover = Color(0xFFEAEDFF)
    val emphasisOnFill = Color(0xFFFCFDFF)

    val info = Color(0xFF003A5A)
    val infoFill = Color(0xFF91D0FF)
    val infoSurface = Color(0xFFEAF4FF)

    val success = Color(0xFF014B40)
    val successFill = Color(0xFF047B5D)
    val successHover = Color(0xFF035E4C)
    val successSurface = Color(0xFFCDFED4)
    val successOnFill = Color(0xFFFAFFFB)

    val warning = Color(0xFF5E4200)
    val warningFill = Color(0xFFFFB800)
    val warningSurface = Color(0xFFFFF1E3)
    val warningOnFill = Color(0xFF251A00)

    val critical = Color(0xFF8E0B21)
    val criticalFill = Color(0xFFC70A24)
    val criticalHover = Color(0xFFA30A24)
    val criticalSurface = Color(0xFFFEE8EB)
    val criticalBorder = Color(0xFFFEC1C7)
    val criticalOnFill = Color(0xFFFFFAFB)

    /** `--p-color-bg-surface-secondary` / `--p-color-bg-surface-tertiary`. */
    val lightSurfaceSecondary = Color(0xFFF7F7F7)
    val lightSurfaceTertiary = Color(0xFFF3F3F3)
    val lightSurfaceActive = Color(0xFFF3F3F3)
    val lightInputBorder = Color(0xFF8A8A8A)
    val lightDisabledFill = Color(0x0D000000)
    val lightScrim = Color(0xB5000000)

    /** Derivaciones X-05 del tema oscuro: valores del propio tema publicado. */
    val darkSurfaceSecondary = gray800
    val darkSurfaceHover = Color(0xFF4A4A4A)
    val darkSurfaceSelected = Color(0xFF616161)
    val darkInputBorder = gray500
    val darkDisabledFill = Color(0x1CFFFFFF)
    val darkDisabledText = gray500
    val darkTextOnBrandFill = gray800
    val darkTextCritical = Color(0xFFFED1D7)
    val darkTextSuccess = successSurface
    val darkTextWarning = warningFill
    val darkTextInfo = infoFill

    /** X-03: el QR siempre negro sobre blanco, con independencia del tema. */
    val qrCanvas = white
    val transparent = Color.Transparent
}

/** Roles semánticos completos; espejo de los 50 roles consumidos por el producto. */
data class AmColorScheme(
    val background: Color,
    val surface: Color,
    val surfaceSecondary: Color,
    val border: Color,
    val functionalBorder: Color,
    val divider: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textPlaceholder: Color,
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
    background = AmPalette.gray50,
    surface = AmPalette.white,
    surfaceSecondary = AmPalette.lightSurfaceSecondary,
    border = AmPalette.gray200,
    functionalBorder = AmPalette.lightInputBorder,
    divider = AmPalette.gray200,
    textPrimary = AmPalette.gray800,
    textSecondary = AmPalette.gray600,
    // D-02: el placeholder publicado no llega a AA; se usa el rol secundario.
    textPlaceholder = AmPalette.gray600,
    textTertiary = AmPalette.gray600,
    accent = AmPalette.emphasis,
    accentActive = AmPalette.emphasisHover,
    accentDisabled = AmPalette.gray400,
    accentFocus = AmPalette.emphasis,
    accentLightActive = AmPalette.emphasisSurfaceHover,
    onAccent = AmPalette.emphasisOnFill,
    accentContainer = AmPalette.emphasisSurface,
    onAccentContainer = AmPalette.emphasis,
    accentText = AmPalette.emphasis,
    danger = AmPalette.critical,
    dangerFill = AmPalette.criticalFill,
    onDanger = AmPalette.criticalOnFill,
    dangerContainer = AmPalette.criticalSurface,
    success = AmPalette.success,
    successFill = AmPalette.successFill,
    onSuccessFill = AmPalette.successOnFill,
    successContainer = AmPalette.successSurface,
    warning = AmPalette.warning,
    warningFill = AmPalette.warningFill,
    onWarningFill = AmPalette.warningOnFill,
    warningContainer = AmPalette.warningSurface,
    info = AmPalette.info,
    infoContainer = AmPalette.infoSurface,
    focus = AmPalette.emphasis,
    disabled = AmPalette.lightDisabledFill,
    onDisabled = AmPalette.gray400,
    inverseSurface = AmPalette.gray800,
    inverseOnSurface = AmPalette.darkText,
    scrim = AmPalette.lightScrim,
    qrCanvas = AmPalette.qrCanvas,
    surfaceContainerLow = AmPalette.white,
    surfaceContainer = AmPalette.lightSurfaceSecondary,
    surfaceContainerHigh = AmPalette.gray50,
    surfaceContainerHighest = AmPalette.gray100,
    surfaceActive = AmPalette.lightSurfaceActive,
    componentActive = AmPalette.lightSurfaceSecondary,
    accentActiveContent = AmPalette.emphasisOnFill,
    dangerActive = AmPalette.criticalHover,
    dangerDisabled = AmPalette.criticalBorder,
    transparent = AmPalette.transparent,
)

val DarkAmColorScheme = AmColorScheme(
    background = AmPalette.gray900,
    surface = AmPalette.gray800,
    surfaceSecondary = AmPalette.darkSurfaceSecondary,
    border = AmPalette.darkSurfaceHover,
    functionalBorder = AmPalette.darkInputBorder,
    divider = AmPalette.darkSurfaceHover,
    textPrimary = AmPalette.darkText,
    textSecondary = AmPalette.gray400,
    textPlaceholder = AmPalette.gray400,
    textTertiary = AmPalette.gray400,
    accent = AmPalette.white,
    accentActive = AmPalette.gray100,
    accentDisabled = AmPalette.gray600,
    accentFocus = AmPalette.darkTextInfo,
    accentLightActive = AmPalette.darkSurfaceHover,
    onAccent = AmPalette.darkTextOnBrandFill,
    accentContainer = AmPalette.gray800,
    onAccentContainer = AmPalette.darkTextInfo,
    accentText = AmPalette.darkTextInfo,
    danger = AmPalette.darkTextCritical,
    dangerFill = AmPalette.criticalFill,
    onDanger = AmPalette.criticalOnFill,
    dangerContainer = AmPalette.gray800,
    success = AmPalette.darkTextSuccess,
    successFill = AmPalette.successFill,
    onSuccessFill = AmPalette.successOnFill,
    successContainer = AmPalette.gray800,
    warning = AmPalette.darkTextWarning,
    warningFill = AmPalette.warningFill,
    onWarningFill = AmPalette.warningOnFill,
    warningContainer = AmPalette.gray800,
    info = AmPalette.darkTextInfo,
    infoContainer = AmPalette.gray800,
    focus = AmPalette.darkTextInfo,
    disabled = AmPalette.darkDisabledFill,
    onDisabled = AmPalette.darkDisabledText,
    inverseSurface = AmPalette.gray50,
    inverseOnSurface = AmPalette.gray900,
    scrim = AmPalette.lightScrim,
    qrCanvas = AmPalette.qrCanvas,
    surfaceContainerLow = AmPalette.gray900,
    surfaceContainer = AmPalette.gray800,
    surfaceContainerHigh = AmPalette.darkSurfaceHover,
    surfaceContainerHighest = AmPalette.darkSurfaceSelected,
    // D-02: #616161 deja `accentText` por debajo de AA; se usa el hover publicado.
    surfaceActive = AmPalette.darkSurfaceHover,
    componentActive = AmPalette.gray800,
    accentActiveContent = AmPalette.darkTextOnBrandFill,
    dangerActive = AmPalette.criticalHover,
    dangerDisabled = AmPalette.critical,
    transparent = AmPalette.transparent,
)

// El default del CompositionLocal es el esquema OSCURO: AmTheme siempre
// proporciona el esquema real; este valor solo afecta a tooling sin AmTheme.
val LocalAmColorScheme = compositionLocalOf { DarkAmColorScheme }

/** Familia de la interfaz. Se resuelve dentro de la composición (recursos CMP). */
val LocalAmFontFamily = compositionLocalOf<FontFamily> { FontFamily.Default }

/**
 * Carga los TTF estáticos de Inter (SIL OFL 1.1) distribuidos como recursos
 * multiplataforma. Las cuatro fuentes son las que publica la release 4.0.
 */
@Composable
internal fun rememberAmInterFontFamily(): FontFamily = FontFamily(
    Font(resource = Res.font.inter_regular, weight = AmWeight.Regular),
    Font(resource = Res.font.inter_medium, weight = AmWeight.Medium),
    Font(resource = Res.font.inter_semi_bold, weight = AmWeight.SemiBold),
    Font(resource = Res.font.inter_bold, weight = AmWeight.Bold),
)

/** Única puerta de color del producto; ningún componente introduce literales. */
object AmColor {
    val Background: Color @Composable get() = LocalAmColorScheme.current.background
    val Surface: Color @Composable get() = LocalAmColorScheme.current.surface
    val SurfaceSecondary: Color @Composable get() = LocalAmColorScheme.current.surfaceSecondary
    val Border: Color @Composable get() = LocalAmColorScheme.current.border
    val FunctionalBorder: Color @Composable get() = LocalAmColorScheme.current.functionalBorder
    val Divider: Color @Composable get() = LocalAmColorScheme.current.divider
    val TextPrimary: Color @Composable get() = LocalAmColorScheme.current.textPrimary
    val TextSecondary: Color @Composable get() = LocalAmColorScheme.current.textSecondary
    val TextPlaceholder: Color @Composable get() = LocalAmColorScheme.current.textPlaceholder
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
    val SurfaceContainer: Color @Composable get() = LocalAmColorScheme.current.surfaceContainer
    val InverseSurface: Color @Composable get() = LocalAmColorScheme.current.inverseSurface
    val InverseOnSurface: Color @Composable get() = LocalAmColorScheme.current.inverseOnSurface
    val Focus: Color @Composable get() = LocalAmColorScheme.current.focus
    val Disabled: Color @Composable get() = LocalAmColorScheme.current.disabled
    val OnDisabled: Color @Composable get() = LocalAmColorScheme.current.onDisabled
    val QrCanvas: Color @Composable get() = LocalAmColorScheme.current.qrCanvas
    val Scrim: Color @Composable get() = LocalAmColorScheme.current.scrim
    val Transparent: Color = Color.Transparent
}

/** Escala `space` publicada, interpretada como dp (D-01: 1rem = 16dp). */
object AmSpacing {
    val None = 0.dp
    val Xs = 2.dp
    val Sm = 4.dp
    val Md = 8.dp
    val Lg = 16.dp
    val Xl = 24.dp
    val Xxl = 32.dp
    val Xxxl = 48.dp
    val Huge = 96.dp
    val ScreenMargin = 16.dp
}

/** Métricas de interacción y composición (D-02 para la escala táctil). */
object AmMetrics {
    val Border = 1.dp
    val DividerHeight = 1.dp
    val BorderStrong = 4.dp
    val ControlMinSize = 48.dp
    val ButtonLargeHeight = 48.dp
    val ButtonCompactHeight = 40.dp
    val ButtonSmallHeight = 32.dp
    val ButtonExtraSmallHeight = 24.dp
    val ButtonLargeHorizontalPadding = 20.dp
    val ButtonMediumIconSize = 20.dp
    val ChipCompactHeight = 32.dp
    val FieldCompactHeight = 40.dp
    val TopBarHeight = 48.dp
    val NavBarHeight = 64.dp
    val NavRailWidth = 80.dp
    val ContentMaxWidth = 720.dp
    val QrPreviewSize = 240.dp
    val QrReviewSize = 320.dp
    val IconSize = 24.dp
    val IconSizeCompact = 20.dp
    val FavCardWidth = 184.dp
    val PinDotSize = 12.dp
    val ListDividerInset = 16.dp
    val SectionHeaderHeight = 48.dp
    val TabIndicatorWidth = 16.dp
    val TabIndicatorHeight = 4.dp
    val DialogListMaxHeight = 320.dp
    val DialogMinWidth = 311.dp
    const val DialogMaxWidthFraction = 0.9f
    val EmptyStateGlyphSize = 96.dp
    val ResultGlyphSize = 64.dp
    val GridIconContainerSize = 48.dp
    val ScannerCornerLength = 24.dp
    val ContextBadgeSize = 40.dp
    val FabClearance = ControlMinSize + AmSpacing.Lg
    val ListRowEndMetaMinWidth = 320.dp
    const val ListRowEndMetaStackFontScale = 2f
}

/** Escala `border-radius` publicada. */
object AmRadius {
    val ExtraSmall = 2.dp
    val Small = 4.dp
    val Medium = 8.dp
    val Large = 12.dp
    val ExtraLarge = 16.dp
    val GridIcon = 8.dp
    val Round = 9999.dp
}

object AmShapes {
    val ExtraSmall = RoundedCornerShape(AmRadius.ExtraSmall)
    val Small = RoundedCornerShape(AmRadius.Small)
    val Medium = RoundedCornerShape(AmRadius.Medium)
    val Large = RoundedCornerShape(AmRadius.Large)
    val ExtraLarge = RoundedCornerShape(AmRadius.ExtraLarge)
    val GridIcon = RoundedCornerShape(AmRadius.GridIcon)
    val Pill = RoundedCornerShape(AmRadius.Round)
    /** D-04: hoja inferior modular con el radio publicado ExtraLarge (16 dp). */
    val BottomSheet = RoundedCornerShape(AmRadius.ExtraLarge)
}

/** Pesos `font-weight` publicados: la familia Inter estática cubre estos cortes. */
object AmWeight {
    val Regular = FontWeight(450)
    val Medium = FontWeight(550)
    val SemiBold = FontWeight(650)
    val Bold = FontWeight(700)
}

/** Tamaños e interlineados de los roles `text-*` de la instantánea. */
object AmType {
    val Display: TextUnit = 36.sp
    val DisplayMedium: TextUnit = 40.sp
    val HeadlineLarge: TextUnit = 36.sp
    val Headline: TextUnit = 32.sp
    val HeadlineSmall: TextUnit = 24.sp
    val TitleLarge: TextUnit = 20.sp
    val Title: TextUnit = 18.sp
    val TitleMedium: TextUnit = 16.sp
    val TitleSmall: TextUnit = 14.sp
    val Amount: TextUnit = 30.sp
    val AmountSymbol: TextUnit = 14.sp
    val RowTitle: TextUnit = 16.sp
    val SectionHeader: TextUnit = 14.sp
    val Body: TextUnit = 14.sp
    val Label: TextUnit = 16.sp
    val BodySmall: TextUnit = 12.sp
    val Caption: TextUnit = 12.sp
    val CaptionSmall: TextUnit = 11.sp
    val LineHeightLabel: TextUnit = 24.sp
    val LineHeightDialog: TextUnit = 28.sp
    val LineHeightBody: TextUnit = 20.sp
    val LineHeightBodySmall: TextUnit = 16.sp
    val LineHeightCaption: TextUnit = 16.sp
    val LineHeightSection: TextUnit = 20.sp
    val LineAmount: TextUnit = 40.sp
    val LineAmountSymbol: TextUnit = 20.sp
    val LineHeightRowTitle: TextUnit = 24.sp
    val LineHeightHeadlineLarge: TextUnit = 48.sp
    val LineHeightHeadlineMedium: TextUnit = 40.sp
    val LineHeightHeadlineSmall: TextUnit = 32.sp
    val LineHeightTitleLarge: TextUnit = 24.sp
    val LineHeightTitleMedium: TextUnit = 20.sp
    val LineHeightTitleSmall: TextUnit = 20.sp
    val LineHeightLabelSmall: TextUnit = 16.sp

    /** Familia efectiva de la interfaz (Inter dentro de [AmTheme]). */
    val FamilyUi: FontFamily @Composable get() = LocalAmFontFamily.current
}

/** `font-letter-spacing` publicado, en sp. */
object AmTracking {
    val Densest: TextUnit = (-0.54).sp
    val Denser: TextUnit = (-0.3).sp
    val Dense: TextUnit = (-0.2).sp
    val Normal: TextUnit = 0.sp
    val TitleMedium: TextUnit = Normal
    val TitleSmall: TextUnit = Normal
    val BodyLarge: TextUnit = Normal
    val BodyMedium: TextUnit = Normal
    val BodySmall: TextUnit = Normal
    val LabelLarge: TextUnit = Normal
    val LabelMedium: TextUnit = Normal
    val LabelSmall: TextUnit = Normal
    val ButtonLabel: TextUnit = Normal
}

/** Tipografía M3: adaptador de tipos; no aporta valores visuales propios. */
val AmMaterialTypography: Typography = Typography(
    headlineLarge = TextStyle(
        fontSize = AmType.HeadlineLarge,
        lineHeight = AmType.LineHeightHeadlineLarge,
        fontWeight = AmWeight.Bold,
        letterSpacing = AmTracking.Densest,
    ),
    headlineMedium = TextStyle(
        fontSize = AmType.Headline,
        lineHeight = AmType.LineHeightHeadlineMedium,
        fontWeight = AmWeight.Bold,
        letterSpacing = AmTracking.Denser,
    ),
    headlineSmall = TextStyle(
        fontSize = AmType.HeadlineSmall,
        lineHeight = AmType.LineHeightHeadlineSmall,
        fontWeight = AmWeight.Bold,
        letterSpacing = AmTracking.Dense,
    ),
    titleLarge = TextStyle(
        fontSize = AmType.TitleLarge,
        lineHeight = AmType.LineHeightTitleLarge,
        fontWeight = AmWeight.SemiBold,
        letterSpacing = AmTracking.Dense,
    ),
    titleMedium = TextStyle(
        fontSize = AmType.TitleMedium,
        lineHeight = AmType.LineHeightTitleMedium,
        fontWeight = AmWeight.SemiBold,
        letterSpacing = AmTracking.TitleMedium,
    ),
    titleSmall = TextStyle(
        fontSize = AmType.TitleSmall,
        lineHeight = AmType.LineHeightTitleSmall,
        fontWeight = AmWeight.SemiBold,
        letterSpacing = AmTracking.TitleSmall,
    ),
    bodyLarge = TextStyle(
        fontSize = AmType.Label,
        lineHeight = AmType.LineHeightLabel,
        fontWeight = AmWeight.Regular,
        letterSpacing = AmTracking.BodyLarge,
    ),
    bodyMedium = TextStyle(
        fontSize = AmType.Body,
        lineHeight = AmType.LineHeightBody,
        fontWeight = AmWeight.Regular,
        letterSpacing = AmTracking.BodyMedium,
    ),
    bodySmall = TextStyle(
        fontSize = AmType.BodySmall,
        lineHeight = AmType.LineHeightBodySmall,
        fontWeight = AmWeight.Regular,
        letterSpacing = AmTracking.BodySmall,
    ),
    labelLarge = TextStyle(
        fontSize = AmType.Label,
        lineHeight = AmType.LineHeightLabel,
        fontWeight = AmWeight.SemiBold,
        letterSpacing = AmTracking.LabelLarge,
    ),
    labelMedium = TextStyle(
        fontSize = AmType.Body,
        lineHeight = AmType.LineHeightBody,
        fontWeight = AmWeight.SemiBold,
        letterSpacing = AmTracking.LabelMedium,
    ),
    labelSmall = TextStyle(
        fontSize = AmType.BodySmall,
        lineHeight = AmType.LineHeightLabelSmall,
        fontWeight = AmWeight.SemiBold,
        letterSpacing = AmTracking.LabelSmall,
    ),
)

/** Sustituye la familia de todos los roles sin alterar su geometría. */
internal fun Typography.withAmFontFamily(family: FontFamily): Typography = copy(
    headlineLarge = headlineLarge.copy(fontFamily = family),
    headlineMedium = headlineMedium.copy(fontFamily = family),
    headlineSmall = headlineSmall.copy(fontFamily = family),
    titleLarge = titleLarge.copy(fontFamily = family),
    titleMedium = titleMedium.copy(fontFamily = family),
    titleSmall = titleSmall.copy(fontFamily = family),
    bodyLarge = bodyLarge.copy(fontFamily = family),
    bodyMedium = bodyMedium.copy(fontFamily = family),
    bodySmall = bodySmall.copy(fontFamily = family),
    labelLarge = labelLarge.copy(fontFamily = family),
    labelMedium = labelMedium.copy(fontFamily = family),
    labelSmall = labelSmall.copy(fontFamily = family),
)

/** Estilos canónicos: reúnen tamaño, peso, tracking e interlineado. */
object AmTextStyles {
    val ButtonLabel = TextStyle(
        fontSize = AmType.Label,
        fontWeight = AmWeight.SemiBold,
        letterSpacing = AmTracking.ButtonLabel,
        lineHeight = AmType.LineHeightLabel,
    )
    val Body = TextStyle(
        fontSize = AmType.Body,
        fontWeight = AmWeight.Regular,
        lineHeight = AmType.LineHeightBody,
    )
    val Label = TextStyle(
        fontSize = AmType.Label,
        fontWeight = AmWeight.Regular,
        lineHeight = AmType.LineHeightLabel,
    )
    val BodySmall = TextStyle(
        fontSize = AmType.BodySmall,
        fontWeight = AmWeight.Regular,
        lineHeight = AmType.LineHeightBodySmall,
    )
    val RowTitle = TextStyle(
        fontSize = AmType.RowTitle,
        fontWeight = AmWeight.SemiBold,
        lineHeight = AmType.LineHeightRowTitle,
    )
    val Amount = TextStyle(
        fontSize = AmType.Amount,
        fontWeight = AmWeight.SemiBold,
        lineHeight = AmType.LineAmount,
        fontFeatureSettings = "tnum",
    )
    val AmountSymbol = TextStyle(
        fontSize = AmType.AmountSymbol,
        fontWeight = AmWeight.Bold,
        lineHeight = AmType.LineAmountSymbol,
    )
    val Caption = TextStyle(
        fontSize = AmType.Caption,
        fontWeight = AmWeight.Regular,
        lineHeight = AmType.LineHeightCaption,
    )
    val Tag = TextStyle(
        fontSize = AmType.CaptionSmall,
        fontWeight = AmWeight.Regular,
        lineHeight = AmType.LineHeightLabelSmall,
    )
    val Support = TextStyle(
        fontSize = AmType.Caption,
        fontWeight = AmWeight.Regular,
        letterSpacing = AmTracking.LabelMedium,
        lineHeight = AmType.LineHeightCaption,
    )
    val SectionHeader = TextStyle(
        fontSize = AmType.SectionHeader,
        fontWeight = AmWeight.Regular,
        lineHeight = AmType.LineHeightSection,
    )
    val Title = TextStyle(
        fontSize = AmType.Title,
        fontWeight = AmWeight.SemiBold,
        letterSpacing = AmTracking.Dense,
        lineHeight = AmType.LineHeightDialog,
    )
}

/** Duraciones y curvas publicadas; los roles de producto son X-04. */
object AmMotion {
    const val DurationReducedMs = 0
    const val DurationShortMs = 150
    const val DurationMediumMs = 200
    const val DurationLongMs = 250
    const val EasingControlX1 = 0.25f
    const val EasingControlY1 = 0.1f
    const val EasingControlX2 = 0.25f
    const val EasingControlY2 = 1f

    object Easings {
        val Standard: Easing = CubicBezierEasing(
            EasingControlX1,
            EasingControlY1,
            EasingControlX2,
            EasingControlY2,
        )
        val In: Easing = CubicBezierEasing(0.42f, 0f, 1f, 1f)
        val Out: Easing = CubicBezierEasing(0.19f, 0.91f, 0.38f, 1f)
        val InOut: Easing = CubicBezierEasing(0.42f, 0f, 0.58f, 1f)
        val Linear: Easing = CubicBezierEasing(0f, 0f, 1f, 1f)
    }
}

/** Sombras publicadas: se conserva el desenfoque y la opacidad de cada capa. */
enum class AmElevation(
    val elevation: Dp,
    val lightAlpha: Float,
    val darkAlpha: Float,
) {
    None(0.dp, 0f, 0f),
    Shadow1(1.dp, 0.07f, 0.07f),
    Shadow2(3.dp, 0.07f, 0.07f),
    Shadow3(6.dp, 0.20f, 0.20f),
    Shadow4(16.dp, 0.22f, 0.22f),
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

/** Identidad de contexto X-06: pares fijos de la instantánea, validados a AA. */
data class AmAccent(val id: String, val background: Color, val onAccent: Color)

object AmAccents {
    val System = AmAccent("system", AmPalette.emphasis, AmPalette.white)

    private val Blue = AmAccent("blue", AmPalette.emphasisHover, AmPalette.white)
    private val Ink = AmAccent("ink", AmPalette.info, AmPalette.white)
    private val Emerald = AmAccent("emerald", AmPalette.successFill, AmPalette.white)
    private val Forest = AmAccent("forest", AmPalette.successHover, AmPalette.white)
    private val Red = AmAccent("red", AmPalette.criticalFill, AmPalette.white)
    private val Wine = AmAccent("wine", AmPalette.critical, AmPalette.white)
    private val Violet = AmAccent("violet", Color(0xFF5700D1), AmPalette.white)
    private val Indigo = AmAccent("indigo", Color(0xFF7126FF), AmPalette.white)
    private val Magenta = AmAccent("magenta", Color(0xFFC530C5), AmPalette.white)
    private val Sky = AmAccent("sky", AmPalette.infoFill, AmPalette.gray900)
    private val Amber = AmAccent("amber", AmPalette.warningFill, AmPalette.gray900)
    private val Lemon = AmAccent("lemon", Color(0xFFFFE600), AmPalette.gray900)

    val Admitted: List<AmAccent> = listOf(
        Blue, Ink, Emerald, Forest, Red, Wine,
        Violet, Indigo, Magenta, Sky, Amber, Lemon,
    )
}

/** Hash FNV-1a sobre UTF-8: asignación idéntica en Android e iOS. */
fun accentFor(contextId: String?): AmAccent {
    if (contextId == null) return AmAccents.System
    var hash: UInt = 0x811C9DC5u
    for (byte in contextId.encodeToByteArray()) {
        hash = hash xor byte.toUInt()
        hash *= 0x01000193u
    }
    return AmAccents.Admitted[(hash % AmAccents.Admitted.size.toUInt()).toInt()]
}

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

/** Texto legible sobre [background]; si falla AA, cae al [fallback]. */
fun Color.asTextOn(background: Color, fallback: Color): Color =
    if (contrastRatio(this, background) >= 4.5) this else fallback
