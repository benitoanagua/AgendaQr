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
import androidx.compose.ui.graphics.compositeOver
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
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
 * - Las primitivas son internas; los consumidores usan [AqColorScheme],
 *   [AqColor], [AqSpacing], [AqMetrics], [AqType], [AqMotion] y [AqElevation].
 * - Las derivaciones necesarias para móvil,tema oscuro y accesibilidad están
 *   gobernadas por D-01, D-02, D-07, X-05 y X-06 del contrato normativo.
 * - No se usa ningún nombre, logotipo ni marca del sistema de origen en el
 *   producto; las referencias son solo de ingeniería.
 */
internal object AqPalette {
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

    /** `--p-color-bg-fill-emphasis`, `text-emphasis`, `border-emphasis`,
     * `icon-emphasis` y `text-link` (`:142,162,196,213,221,245`). */
    val emphasis = Color(0xFF005BD3)
    val emphasisHover = Color(0xFF004299)
    val emphasisActive = Color(0xFF002E6A)
    val emphasisSurface = Color(0xFFF0F2FF)
    val emphasisSurfaceHover = Color(0xFFEAEDFF)
    val emphasisSurfaceActive = Color(0xFFE2E7FF)
    val emphasisOnFill = Color(0xFFFCFDFF)
    val emphasisOnFillHover = Color(0xFFE2E7FF)
    val emphasisOnFillActive = Color(0xFFD5DCFF)

    /** `--p-color-text-magic*` y su texto sobre relleno (`:202-204`). */
    val textMagic = Color(0xFF5700D1)
    val textMagicSecondary = Color(0xFF7126FF)
    val magicOnFill = Color(0xFFFDFDFF)

    /** `--p-color-bg-fill-caution` (`:133`). */
    val cautionFill = Color(0xFFFFE600)

    /** `--p-color-avatar-one-*` (`:255-256`). El texto publicado no llega a AA: PV-07. */
    val avatarOneFill = Color(0xFFC530C5)
    val avatarOneOnFill = Color(0xFFFDEFFD)

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
    /** `--p-color-input-bg-surface` (`:270`); hover/active no se usan aún. */
    val lightInputSurface = Color(0xFFFDFDFD)
    val lightInputBorder = Color(0xFF8A8A8A)
    val lightDisabledFill = Color(0x0D000000)
    val lightScrim = Color(0xB5000000)

    /** `--p-color-bg-fill-brand*` del tema claro (`:116-120`). */
    val brandFill = gray800
    val brandFillHover = gray900
    val brandFillActive = gray900
    val brandFillSelected = gray800
    val brandFillDisabled = Color(0x2B000000)

    /** `--p-color-bg-fill-selected` y `bg-surface-selected` (`:68,107`). */
    val selectionFill = Color(0xFFCCCCCC)
    val selectionSurface = gray50

    /** Derivaciones X-05 del tema oscuro: valores del propio tema publicado. */
    val darkSurfaceSecondary = gray800
    val darkSurfaceHover = Color(0xFF4A4A4A)
    val darkSurfaceSelected = Color(0xFF616161)
    val darkInputBorder = gray500
    val darkDisabledFill = Color(0x1CFFFFFF)
    val darkDisabledText = gray500
    val darkTextOnBrandFill = gray800
    val darkTextOnBrandFillDisabled = gray500
    /** `--p-color-bg-fill-brand*` de `dark-experimental` (`:550,555,559,563,573`). */
    val darkBrandFill = white
    val darkBrandFillHover = Color(0xFFF3F3F3)
    val darkBrandFillActive = Color(0xFFF7F7F7)
    val darkBrandFillSelected = Color(0xFFD4D4D4)
    val darkBrandFillDisabled = Color(0x38FFFFFF)
    val darkTextCritical = Color(0xFFFED1D7)
    val darkTextSuccess = successSurface
    val darkTextWarning = warningFill
    val darkTextInfo = infoFill

    /**
     * D-11: `dark-experimental` no publica contenedores tonales. Se derivan
     * mezclando el relleno semántico al 16 % sobre `gray800`, la superficie
     * oscura publicada, de modo que el contenedor se distinga de `surface`
     * y conserve el par de contenido en AA.
     */
    private const val darkToneContainerAlpha = 0.16f
    val darkDangerContainer = criticalFill.copy(alpha = darkToneContainerAlpha).compositeOver(gray800)
    val darkSuccessContainer = successFill.copy(alpha = darkToneContainerAlpha).compositeOver(gray800)
    val darkWarningContainer = warningFill.copy(alpha = darkToneContainerAlpha).compositeOver(gray800)
    val darkInfoContainer = infoFill.copy(alpha = darkToneContainerAlpha).compositeOver(gray800)

    /** X-03: el QR siempre negro sobre blanco, con independencia del tema. */
    val qrCanvas = white
    val transparent = Color.Transparent
}

/** Roles semánticos completos; espejo de los 60 roles consumidos por el producto. */
data class AqColorScheme(
    val background: Color,
    val surface: Color,
    val surfaceSecondary: Color,
    val inputSurface: Color,
    val border: Color,
    val functionalBorder: Color,
    val divider: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textPlaceholder: Color,
    val textTertiary: Color,
    val brandFill: Color,
    val brandFillHover: Color,
    val brandFillActive: Color,
    val brandFillSelected: Color,
    val brandFillDisabled: Color,
    val onBrandFill: Color,
    val onBrandFillDisabled: Color,
    val emphasisFill: Color,
    val emphasisBorder: Color,
    val emphasisText: Color,
    val emphasisIcon: Color,
    val emphasisSurface: Color,
    val emphasisSurfaceHover: Color,
    val emphasisOnFill: Color,
    val link: Color,
    val selectionFill: Color,
    val selectionSurface: Color,
    val selectionBorder: Color,
    val selectionIcon: Color,
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
    val dangerActive: Color,
    val dangerDisabled: Color,
    val transparent: Color,
)

val LightAqColorScheme = AqColorScheme(
    background = AqPalette.gray50,
    surface = AqPalette.white,
    surfaceSecondary = AqPalette.lightSurfaceSecondary,
    inputSurface = AqPalette.lightInputSurface,
    border = AqPalette.gray200,
    functionalBorder = AqPalette.lightInputBorder,
    divider = AqPalette.gray200,
    textPrimary = AqPalette.gray800,
    textSecondary = AqPalette.gray600,
    // D-02: el placeholder publicado no llega a AA; se usa el rol secundario.
    textPlaceholder = AqPalette.gray600,
    textTertiary = AqPalette.gray600,
    brandFill = AqPalette.brandFill,
    brandFillHover = AqPalette.brandFillHover,
    brandFillActive = AqPalette.brandFillActive,
    brandFillSelected = AqPalette.brandFillSelected,
    brandFillDisabled = AqPalette.brandFillDisabled,
    onBrandFill = AqPalette.white,
    onBrandFillDisabled = AqPalette.white,
    emphasisFill = AqPalette.emphasis,
    emphasisBorder = AqPalette.emphasis,
    emphasisText = AqPalette.emphasis,
    emphasisIcon = AqPalette.emphasis,
    emphasisSurface = AqPalette.emphasisSurface,
    emphasisSurfaceHover = AqPalette.emphasisSurfaceHover,
    emphasisOnFill = AqPalette.emphasisOnFill,
    link = AqPalette.emphasis,
    selectionFill = AqPalette.selectionFill,
    selectionSurface = AqPalette.selectionSurface,
    selectionBorder = AqPalette.emphasis,
    selectionIcon = AqPalette.emphasis,
    danger = AqPalette.critical,
    dangerFill = AqPalette.criticalFill,
    onDanger = AqPalette.criticalOnFill,
    dangerContainer = AqPalette.criticalSurface,
    success = AqPalette.success,
    successFill = AqPalette.successFill,
    onSuccessFill = AqPalette.successOnFill,
    successContainer = AqPalette.successSurface,
    warning = AqPalette.warning,
    warningFill = AqPalette.warningFill,
    onWarningFill = AqPalette.warningOnFill,
    warningContainer = AqPalette.warningSurface,
    info = AqPalette.info,
    infoContainer = AqPalette.infoSurface,
    focus = AqPalette.emphasis,
    disabled = AqPalette.lightDisabledFill,
    onDisabled = AqPalette.gray400,
    inverseSurface = AqPalette.gray800,
    inverseOnSurface = AqPalette.darkText,
    scrim = AqPalette.lightScrim,
    qrCanvas = AqPalette.qrCanvas,
    surfaceContainerLow = AqPalette.white,
    surfaceContainer = AqPalette.lightSurfaceSecondary,
    surfaceContainerHigh = AqPalette.gray50,
    surfaceContainerHighest = AqPalette.gray100,
    surfaceActive = AqPalette.lightSurfaceActive,
    componentActive = AqPalette.lightSurfaceSecondary,
    dangerActive = AqPalette.criticalHover,
    dangerDisabled = AqPalette.criticalBorder,
    transparent = AqPalette.transparent,
)

val DarkAqColorScheme = AqColorScheme(
    background = AqPalette.gray900,
    surface = AqPalette.gray800,
    surfaceSecondary = AqPalette.darkSurfaceSecondary,
    inputSurface = AqPalette.gray800,
    border = AqPalette.darkSurfaceHover,
    functionalBorder = AqPalette.darkInputBorder,
    divider = AqPalette.darkSurfaceHover,
    textPrimary = AqPalette.darkText,
    textSecondary = AqPalette.gray400,
    textPlaceholder = AqPalette.gray400,
    textTertiary = AqPalette.gray400,
    brandFill = AqPalette.darkBrandFill,
    brandFillHover = AqPalette.darkBrandFillHover,
    brandFillActive = AqPalette.darkBrandFillActive,
    brandFillSelected = AqPalette.darkBrandFillSelected,
    brandFillDisabled = AqPalette.darkBrandFillDisabled,
    onBrandFill = AqPalette.darkTextOnBrandFill,
    onBrandFillDisabled = AqPalette.darkTextOnBrandFillDisabled,
    // X-05/PV-06: el tema oscuro no publica emphasis/link; se usan los
    // valores publicados más cercanos que cumplen AA sobre las superficies.
    emphasisFill = AqPalette.infoFill,
    emphasisBorder = AqPalette.infoFill,
    emphasisText = AqPalette.infoFill,
    emphasisIcon = AqPalette.infoFill,
    emphasisSurface = AqPalette.gray800,
    emphasisSurfaceHover = AqPalette.darkSurfaceHover,
    emphasisOnFill = AqPalette.gray900,
    link = AqPalette.infoFill,
    selectionFill = AqPalette.darkSurfaceSelected,
    selectionSurface = AqPalette.darkSurfaceSelected,
    selectionBorder = AqPalette.infoFill,
    selectionIcon = AqPalette.infoFill,
    danger = AqPalette.darkTextCritical,
    dangerFill = AqPalette.criticalFill,
    onDanger = AqPalette.criticalOnFill,
    dangerContainer = AqPalette.darkDangerContainer,
    success = AqPalette.darkTextSuccess,
    successFill = AqPalette.successFill,
    onSuccessFill = AqPalette.successOnFill,
    successContainer = AqPalette.darkSuccessContainer,
    warning = AqPalette.darkTextWarning,
    warningFill = AqPalette.warningFill,
    onWarningFill = AqPalette.warningOnFill,
    warningContainer = AqPalette.darkWarningContainer,
    info = AqPalette.darkTextInfo,
    infoContainer = AqPalette.darkInfoContainer,
    focus = AqPalette.darkTextInfo,
    disabled = AqPalette.darkDisabledFill,
    onDisabled = AqPalette.darkDisabledText,
    inverseSurface = AqPalette.gray50,
    inverseOnSurface = AqPalette.gray900,
    scrim = AqPalette.lightScrim,
    qrCanvas = AqPalette.qrCanvas,
    surfaceContainerLow = AqPalette.gray900,
    surfaceContainer = AqPalette.gray800,
    surfaceContainerHigh = AqPalette.darkSurfaceHover,
    surfaceContainerHighest = AqPalette.darkSurfaceSelected,
    // D-02: #616161 deja el énfasis por debajo de AA; se usa el hover publicado.
    surfaceActive = AqPalette.darkSurfaceHover,
    componentActive = AqPalette.gray800,
    dangerActive = AqPalette.criticalHover,
    dangerDisabled = AqPalette.critical,
    transparent = AqPalette.transparent,
)

// El default del CompositionLocal es el esquema OSCURO: AqTheme siempre
// proporciona el esquema real; este valor solo afecta a tooling sin AqTheme.
val LocalAqColorScheme = compositionLocalOf { LightAqColorScheme }

/** Familia de la interfaz. Se resuelve dentro de la composición (recursos CMP). */
val LocalAqFontFamily = compositionLocalOf<FontFamily> { FontFamily.Default }

/**
 * Carga los TTF estáticos de Inter (SIL OFL 1.1) distribuidos como recursos
 * multiplataforma. Las cuatro fuentes son las que publica la release 4.0.
 */
@Composable
internal fun rememberAqInterFontFamily(): FontFamily = FontFamily(
    Font(resource = Res.font.inter_regular, weight = AqWeight.Regular),
    Font(resource = Res.font.inter_medium, weight = AqWeight.Medium),
    Font(resource = Res.font.inter_semi_bold, weight = AqWeight.SemiBold),
    Font(resource = Res.font.inter_bold, weight = AqWeight.Bold),
)

/**
 * Alfas semánticos. Los valores de selección son los publicados en la
 * instantánea para los estados pressed/selected (`rgba(…, 0.25)` y
 * `rgba(…, 0.20)`); [Hidden] significa «sin pintura», no un color nuevo.
 */
object AqAlpha {
    const val Hidden = 0f
    const val SelectedOnFill = 0.25f
    const val SelectedOnTone = 0.2f
}

/** Única puerta de color del producto; ningún componente introduce literales. */
object AqColor {
    val Background: Color @Composable get() = LocalAqColorScheme.current.background
    val Surface: Color @Composable get() = LocalAqColorScheme.current.surface
    val SurfaceSecondary: Color @Composable get() = LocalAqColorScheme.current.surfaceSecondary
    val InputSurface: Color @Composable get() = LocalAqColorScheme.current.inputSurface
    val Border: Color @Composable get() = LocalAqColorScheme.current.border
    val FunctionalBorder: Color @Composable get() = LocalAqColorScheme.current.functionalBorder
    val Divider: Color @Composable get() = LocalAqColorScheme.current.divider
    val TextPrimary: Color @Composable get() = LocalAqColorScheme.current.textPrimary
    val TextSecondary: Color @Composable get() = LocalAqColorScheme.current.textSecondary
    val TextPlaceholder: Color @Composable get() = LocalAqColorScheme.current.textPlaceholder
    val TextTertiary: Color @Composable get() = LocalAqColorScheme.current.textTertiary
    val BrandFill: Color @Composable get() = LocalAqColorScheme.current.brandFill
    val BrandFillHover: Color @Composable get() = LocalAqColorScheme.current.brandFillHover
    val BrandFillActive: Color @Composable get() = LocalAqColorScheme.current.brandFillActive
    val BrandFillSelected: Color @Composable get() = LocalAqColorScheme.current.brandFillSelected
    val BrandFillDisabled: Color @Composable get() = LocalAqColorScheme.current.brandFillDisabled
    val OnBrandFill: Color @Composable get() = LocalAqColorScheme.current.onBrandFill
    val OnBrandFillDisabled: Color @Composable get() = LocalAqColorScheme.current.onBrandFillDisabled
    val EmphasisFill: Color @Composable get() = LocalAqColorScheme.current.emphasisFill
    val EmphasisBorder: Color @Composable get() = LocalAqColorScheme.current.emphasisBorder
    val EmphasisText: Color @Composable get() = LocalAqColorScheme.current.emphasisText
    val EmphasisIcon: Color @Composable get() = LocalAqColorScheme.current.emphasisIcon
    val EmphasisSurface: Color @Composable get() = LocalAqColorScheme.current.emphasisSurface
    val EmphasisSurfaceHover: Color @Composable get() = LocalAqColorScheme.current.emphasisSurfaceHover
    val OnEmphasisFill: Color @Composable get() = LocalAqColorScheme.current.emphasisOnFill
    val Link: Color @Composable get() = LocalAqColorScheme.current.link
    val SelectionFill: Color @Composable get() = LocalAqColorScheme.current.selectionFill
    val SelectionSurface: Color @Composable get() = LocalAqColorScheme.current.selectionSurface
    val SelectionBorder: Color @Composable get() = LocalAqColorScheme.current.selectionBorder
    val SelectionIcon: Color @Composable get() = LocalAqColorScheme.current.selectionIcon
    val Danger: Color @Composable get() = LocalAqColorScheme.current.danger
    val DangerFill: Color @Composable get() = LocalAqColorScheme.current.dangerFill
    val DangerActive: Color @Composable get() = LocalAqColorScheme.current.dangerActive
    val DangerDisabled: Color @Composable get() = LocalAqColorScheme.current.dangerDisabled
    val OnDanger: Color @Composable get() = LocalAqColorScheme.current.onDanger
    val DangerContainer: Color @Composable get() = LocalAqColorScheme.current.dangerContainer
    val Success: Color @Composable get() = LocalAqColorScheme.current.success
    val SuccessFill: Color @Composable get() = LocalAqColorScheme.current.successFill
    val OnSuccessFill: Color @Composable get() = LocalAqColorScheme.current.onSuccessFill
    val SuccessContainer: Color @Composable get() = LocalAqColorScheme.current.successContainer
    val Warning: Color @Composable get() = LocalAqColorScheme.current.warning
    val WarningFill: Color @Composable get() = LocalAqColorScheme.current.warningFill
    val OnWarningFill: Color @Composable get() = LocalAqColorScheme.current.onWarningFill
    val WarningContainer: Color @Composable get() = LocalAqColorScheme.current.warningContainer
    val Info: Color @Composable get() = LocalAqColorScheme.current.info
    val InfoContainer: Color @Composable get() = LocalAqColorScheme.current.infoContainer
    val SurfaceContainerHighest: Color @Composable get() = LocalAqColorScheme.current.surfaceContainerHighest
    val SurfaceContainerHigh: Color @Composable get() = LocalAqColorScheme.current.surfaceContainerHigh
    val SurfaceActive: Color @Composable get() = LocalAqColorScheme.current.surfaceActive
    val ComponentActive: Color @Composable get() = LocalAqColorScheme.current.componentActive
    val SurfaceContainerLow: Color @Composable get() = LocalAqColorScheme.current.surfaceContainerLow
    val SurfaceContainer: Color @Composable get() = LocalAqColorScheme.current.surfaceContainer
    val InverseSurface: Color @Composable get() = LocalAqColorScheme.current.inverseSurface
    val InverseOnSurface: Color @Composable get() = LocalAqColorScheme.current.inverseOnSurface
    val Focus: Color @Composable get() = LocalAqColorScheme.current.focus
    val Disabled: Color @Composable get() = LocalAqColorScheme.current.disabled
    val OnDisabled: Color @Composable get() = LocalAqColorScheme.current.onDisabled
    val QrCanvas: Color @Composable get() = LocalAqColorScheme.current.qrCanvas
    val Scrim: Color @Composable get() = LocalAqColorScheme.current.scrim
    val Transparent: Color = Color.Transparent
}

/** Escala `space` publicada, interpretada como dp (D-01: 1rem = 16dp). */
object AqSpacing {
    val None = 0.dp
    val Xs = 2.dp
    val Sm = 4.dp
    val Md = 8.dp
    /** `space-300` (12 dp). */
    val MdPlus = 12.dp
    val Lg = 16.dp
    /** `space-500` (20 dp). */
    val LgPlus = 20.dp
    val Xl = 24.dp
    val Xxl = 32.dp
    val Xxxl = 48.dp
    val Huge = 96.dp
    val ScreenMargin = 16.dp
}

/** Métricas de interacción y composición (D-02 para la escala táctil). */
object AqMetrics {
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
    val FabClearance = ControlMinSize + AqSpacing.Lg
    val ListRowEndMetaMinWidth = 320.dp
    const val ListRowEndMetaStackFontScale = 2f
}

/**
 * Proporciones internas de composición (D-08). No son escalas publicadas:
 * se conservan de la implementación auditada y se concentran aquí para que
 * ningún componente defina geometría propia. No alteran los valores de la
 * instantánea ni los tamaños táctiles.
 */
object AqComposition {
    /** Ancho de la línea de título del skeleton respecto al ancho disponible. */
    const val SkeletonTitleWidthFraction = 0.6f

    /** Tamaño del glifo dentro del círculo de confirmación de resultado. */
    const val SuccessCheckIconFraction = 0.55f
}

/** Escala `border-radius` publicada. */
object AqRadius {
    val ExtraSmall = 2.dp
    val Small = 4.dp
    val Medium = 8.dp
    val Large = 12.dp
    val ExtraLarge = 16.dp
    val GridIcon = 8.dp
    val Round = 9999.dp
}

object AqShapes {
    val ExtraSmall = RoundedCornerShape(AqRadius.ExtraSmall)
    val Small = RoundedCornerShape(AqRadius.Small)
    val Medium = RoundedCornerShape(AqRadius.Medium)
    val Large = RoundedCornerShape(AqRadius.Large)
    val ExtraLarge = RoundedCornerShape(AqRadius.ExtraLarge)
    val GridIcon = RoundedCornerShape(AqRadius.GridIcon)
    val Pill = RoundedCornerShape(AqRadius.Round)
    /** D-04: hoja inferior modular con el radio publicado ExtraLarge (16 dp). */
    val BottomSheet = RoundedCornerShape(AqRadius.ExtraLarge)
}

/** Pesos `font-weight` publicados: la familia Inter estática cubre estos cortes. */
object AqWeight {
    val Regular = FontWeight(450)
    val Medium = FontWeight(550)
    val SemiBold = FontWeight(650)
    val Bold = FontWeight(700)
}

/** Tamaños e interlineados de los roles `text-*` de la instantánea. */
object AqType {
    /** Roles `heading-*` de `light-mobile` (`:412-441,512-517`). */
    val Heading3Xl: TextUnit = 36.sp
    val LineHeightHeading3Xl: TextUnit = 48.sp
    val Heading2Xl: TextUnit = 32.sp
    val LineHeightHeading2Xl: TextUnit = 40.sp
    val HeadingXl: TextUnit = 22.sp
    val LineHeightHeadingXl: TextUnit = 28.sp
    val HeadingLg: TextUnit = 18.sp
    val LineHeightHeadingLg: TextUnit = 24.sp
    val HeadingMd: TextUnit = 16.sp
    val LineHeightHeadingMd: TextUnit = 20.sp
    val HeadingSm: TextUnit = 14.sp
    val LineHeightHeadingSm: TextUnit = 20.sp
    val HeadingXs: TextUnit = 12.sp
    val LineHeightHeadingXs: TextUnit = 16.sp

    /** Roles `body-*` de `light-mobile` (`:442-461,518-525`). */
    val BodyLg: TextUnit = 18.sp
    val LineHeightBodyLg: TextUnit = 28.sp
    val BodyMd: TextUnit = 16.sp
    val LineHeightBodyMd: TextUnit = 24.sp
    val BodySm: TextUnit = 14.sp
    val LineHeightBodySm: TextUnit = 20.sp
    val BodyXs: TextUnit = 12.sp
    val LineHeightBodyXs: TextUnit = 16.sp

    /** Alias conservados para el adaptador Material y los estilos de producto. */
    val Display = Heading3Xl
    val DisplayMedium: TextUnit = 40.sp
    val HeadlineLarge = Heading3Xl
    val Headline = Heading2Xl
    /** 24/32 no tiene equivalente móvil publicado: se mantiene como extensión. */
    val HeadlineSmall: TextUnit = 24.sp
    val TitleLarge: TextUnit = 20.sp
    val Title = HeadingLg
    val TitleMedium = HeadingMd
    val TitleSmall = HeadingSm
    val Amount: TextUnit = 30.sp
    val AmountSymbol = HeadingSm
    val RowTitle = HeadingMd
    val SectionHeader = HeadingSm
    val Body = BodySm
    val Label = BodyMd
    val BodySmall = BodyXs
    val Caption = BodyXs
    val CaptionSmall: TextUnit = 11.sp
    val LineHeightLabel = LineHeightBodyMd
    val LineHeightDialog = LineHeightBodyLg
    val LineHeightBody = LineHeightBodySm
    val LineHeightBodySmall = LineHeightBodyXs
    val LineHeightCaption = LineHeightBodyXs
    val LineHeightSection = LineHeightHeadingSm
    val LineAmount: TextUnit = 40.sp
    val LineAmountSymbol = LineHeightHeadingMd
    val LineHeightRowTitle = LineHeightBodyMd
    val LineHeightHeadlineLarge = LineHeightHeading3Xl
    val LineHeightHeadlineMedium = LineHeightHeading2Xl
    val LineHeightHeadlineSmall: TextUnit = 32.sp
    val LineHeightTitleLarge: TextUnit = 24.sp
    val LineHeightTitleMedium = LineHeightHeadingMd
    val LineHeightTitleSmall = LineHeightHeadingSm
    val LineHeightLabelSmall = LineHeightBodyXs

    /**
     * Ratios derivados de los pares publicados `heading-2xl` (40/32) y
     * `body-md` (24/16). Se aplican solo a tamaños arbitrarios (PV-08);
     * los roles canónicos conservan su `lineHeight` publicado.
     */
    const val HeadingLineHeightRatio = 1.25f
    const val BodyLineHeightRatio = 1.5f

    /** Familia efectiva de la interfaz (Inter dentro de [AqTheme]). */
    val FamilyUi: FontFamily @Composable get() = LocalAqFontFamily.current
}

/** `font-letter-spacing` publicado, en sp. */
object AqTracking {
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

/** Geometría completa de un rol tipográfico publicado. */
internal data class AqTextRole(
    val size: TextUnit,
    val lineHeight: TextUnit,
    val weight: FontWeight,
    val tracking: TextUnit,
)

/** Resolución de tamaños canónicos y de tamaños arbitrarios (PV-08). */
internal object AqTextRoles {
    val Heading3Xl = AqTextRole(
        AqType.Heading3Xl, AqType.LineHeightHeading3Xl, AqWeight.Bold, AqTracking.Densest,
    )
    val Heading2Xl = AqTextRole(
        AqType.Heading2Xl, AqType.LineHeightHeading2Xl, AqWeight.Bold, AqTracking.Denser,
    )
    val HeadingXl = AqTextRole(
        AqType.HeadingXl, AqType.LineHeightHeadingXl, AqWeight.Bold, AqTracking.Dense,
    )
    val HeadingLg = AqTextRole(
        AqType.HeadingLg, AqType.LineHeightHeadingLg, AqWeight.SemiBold, AqTracking.Dense,
    )
    val HeadingMd = AqTextRole(
        AqType.HeadingMd, AqType.LineHeightHeadingMd, AqWeight.SemiBold, AqTracking.Normal,
    )
    val HeadingSm = AqTextRole(
        AqType.HeadingSm, AqType.LineHeightHeadingSm, AqWeight.SemiBold, AqTracking.Normal,
    )
    val HeadingXs = AqTextRole(
        AqType.HeadingXs, AqType.LineHeightHeadingXs, AqWeight.SemiBold, AqTracking.Normal,
    )
    val BodyLg = AqTextRole(
        AqType.BodyLg, AqType.LineHeightBodyLg, AqWeight.Regular, AqTracking.Normal,
    )
    val BodyMd = AqTextRole(
        AqType.BodyMd, AqType.LineHeightBodyMd, AqWeight.Regular, AqTracking.Normal,
    )
    val BodySm = AqTextRole(
        AqType.BodySm, AqType.LineHeightBodySm, AqWeight.Regular, AqTracking.Normal,
    )
    val BodyXs = AqTextRole(
        AqType.BodyXs, AqType.LineHeightBodyXs, AqWeight.Regular, AqTracking.Normal,
    )

    fun of(size: TextUnit, heading: Boolean): AqTextRole = when (size.value) {
        AqType.Heading3Xl.value -> Heading3Xl
        AqType.Heading2Xl.value -> Heading2Xl
        AqType.HeadlineSmall.value -> HeadingXl.copy(size = size)
        AqType.HeadingXl.value -> HeadingXl
        AqType.HeadingLg.value -> if (heading) HeadingLg else BodyLg
        AqType.HeadingMd.value -> if (heading) HeadingMd else BodyMd
        AqType.HeadingSm.value -> if (heading) HeadingSm else BodySm
        AqType.HeadingXs.value -> if (heading) HeadingXs else BodyXs
        else -> fallback(size, heading)
    }

    private fun fallback(size: TextUnit, heading: Boolean) = AqTextRole(
        size = size,
        lineHeight = TextUnit(
            size.value * if (heading) AqType.HeadingLineHeightRatio else AqType.BodyLineHeightRatio,
            TextUnitType.Sp,
        ),
        weight = if (heading) AqWeight.Bold else AqWeight.Regular,
        tracking = if (heading) AqTracking.Dense else AqTracking.Normal,
    )
}

/** Tipografía M3: adaptador de tipos; no aporta valores visuales propios. */
private fun AqTextRole.asTextStyle() = TextStyle(
    fontSize = size,
    lineHeight = lineHeight,
    fontWeight = weight,
    letterSpacing = tracking,
)

val AqMaterialTypography: Typography = Typography(
    headlineLarge = AqTextRoles.Heading3Xl.asTextStyle(),
    headlineMedium = AqTextRoles.Heading2Xl.asTextStyle(),
    headlineSmall = AqTextRoles.HeadingXl.asTextStyle(),
    titleLarge = AqTextRoles.HeadingLg.asTextStyle(),
    titleMedium = AqTextRoles.HeadingMd.asTextStyle(),
    titleSmall = AqTextRoles.HeadingSm.asTextStyle(),
    bodyLarge = AqTextRoles.BodyLg.asTextStyle(),
    bodyMedium = AqTextRoles.BodyMd.asTextStyle(),
    bodySmall = AqTextRoles.BodySm.asTextStyle(),
    labelLarge = AqTextRoles.BodyMd.copy(weight = AqWeight.SemiBold).asTextStyle(),
    labelMedium = AqTextRoles.BodySm.copy(weight = AqWeight.SemiBold).asTextStyle(),
    labelSmall = AqTextRoles.BodyXs.copy(weight = AqWeight.SemiBold).asTextStyle(),
)

/** Sustituye la familia de todos los roles sin alterar su geometría. */
internal fun Typography.withAqFontFamily(family: FontFamily): Typography = copy(
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
object AqTextStyles {
    val Heading3Xl = AqTextRoles.Heading3Xl.asTextStyle()
    val Heading2Xl = AqTextRoles.Heading2Xl.asTextStyle()
    val HeadingXl = AqTextRoles.HeadingXl.asTextStyle()
    val HeadingLg = AqTextRoles.HeadingLg.asTextStyle()
    val HeadingMd = AqTextRoles.HeadingMd.asTextStyle()
    val HeadingSm = AqTextRoles.HeadingSm.asTextStyle()
    val HeadingXs = AqTextRoles.HeadingXs.asTextStyle()
    val BodyLg = AqTextRoles.BodyLg.asTextStyle()
    val BodyMd = AqTextRoles.BodyMd.asTextStyle()
    val BodySm = AqTextRoles.BodySm.asTextStyle()
    val BodyXs = AqTextRoles.BodyXs.asTextStyle()

    val ButtonLabel = TextStyle(
        fontSize = AqType.Label,
        fontWeight = AqWeight.SemiBold,
        letterSpacing = AqTracking.ButtonLabel,
        lineHeight = AqType.LineHeightLabel,
    )
    val Body = BodySm
    val Label = BodyMd
    val BodySmall = BodyXs
    val RowTitle = BodyMd.copy(fontWeight = AqWeight.SemiBold)
    val Amount = TextStyle(
        fontSize = AqType.Amount,
        fontWeight = AqWeight.SemiBold,
        lineHeight = AqType.LineAmount,
        fontFeatureSettings = "tnum",
    )
    val AmountSymbol = TextStyle(
        fontSize = AqType.AmountSymbol,
        fontWeight = AqWeight.Bold,
        lineHeight = AqType.LineAmountSymbol,
    )
    val Caption = BodyXs
    val Tag = TextStyle(
        fontSize = AqType.CaptionSmall,
        fontWeight = AqWeight.Regular,
        lineHeight = AqType.LineHeightLabelSmall,
    )
    val Support = BodyXs.copy(letterSpacing = AqTracking.LabelMedium)
    val SectionHeader = BodySm
    /** Rol de producto: `heading-lg`, antes mezclado con `body-lg`. */
    val Title = HeadingLg
}

/** Duraciones y curvas publicadas; los roles de producto son X-04. */
object AqMotion {
    const val DurationReducedMs = 0
    const val DurationShortMs = 150
    const val DurationMediumMs = 200
    const val DurationLongMs = 250
    const val EasingControlX1 = 0.25f
    const val EasingControlY1 = 0.1f
    const val EasingControlX2 = 0.25f
    const val EasingControlY2 = 1f

    /** Desplazamiento lateral de la superficie como fracción de su ancho (D-08). */
    const val SurfaceSlideFraction = 8

    /** Geometría del predictive back nativo (D-03), como fracción 0–1. */
    const val PredictiveBackShrink = 0.08f
    const val PredictiveBackFade = 0.5f

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

/**
 * Sombras publicadas (`:363-368`). `elevation` es el radio de desenfoque;
 * `offsetY` y `spread` se conservan como metadato porque el `Modifier.shadow`
 * de Compose no expone spread (D-10). `shadow-100` se anula en
 * `light-mobile` (`:497`).
 */
enum class AqElevation(
    val elevation: Dp,
    val offsetY: Dp,
    val spread: Dp,
    val lightAlpha: Float,
    val darkAlpha: Float,
    val suppressedInLightMobile: Boolean = false,
) {
    None(0.dp, 0.dp, 0.dp, 0f, 0f),
    Shadow1(1.dp, 1.dp, 0.dp, 0.07f, 0.07f, true),
    Shadow2(1.dp, 3.dp, (-1).dp, 0.07f, 0.07f),
    Shadow3(6.dp, 4.dp, (-2).dp, 0.20f, 0.20f),
    Shadow4(16.dp, 8.dp, (-4).dp, 0.22f, 0.22f),
    Shadow5(20.dp, 12.dp, (-8).dp, 0.24f, 0.24f),
    Shadow6(20.dp, 20.dp, (-8).dp, 0.28f, 0.28f),
}

@Composable
fun Modifier.aqElevation(
    level: AqElevation,
    shape: Shape = RectangleShape,
    clip: Boolean = false,
): Modifier {
    val dark = LocalAqColorScheme.current == DarkAqColorScheme
    if (level == AqElevation.None || (!dark && level.suppressedInLightMobile)) return this
    val alpha = if (dark) level.darkAlpha else level.lightAlpha
    val shadowColor = AqPalette.gray900.copy(alpha = alpha)
    return shadow(
        elevation = level.elevation,
        shape = shape,
        clip = clip,
        ambientColor = shadowColor,
        spotColor = shadowColor,
    )
}

/** Identidad de contexto X-06: pares fijos de la instantánea, validados a AA. */
data class AqAccent(val id: String, val background: Color, val onAccent: Color)

object AqAccents {
    val System = AqAccent("system", AqPalette.emphasis, AqPalette.white)

    private val Blue = AqAccent("blue", AqPalette.emphasisHover, AqPalette.white)
    private val Ink = AqAccent("ink", AqPalette.info, AqPalette.white)
    private val Emerald = AqAccent("emerald", AqPalette.successFill, AqPalette.white)
    private val Forest = AqAccent("forest", AqPalette.successHover, AqPalette.white)
    private val Red = AqAccent("red", AqPalette.criticalFill, AqPalette.white)
    private val Wine = AqAccent("wine", AqPalette.critical, AqPalette.white)
    private val Violet = AqAccent("violet", AqPalette.textMagic, AqPalette.magicOnFill)
    private val Indigo = AqAccent("indigo", AqPalette.textMagicSecondary, AqPalette.magicOnFill)
    // PV-07: el texto publicado `avatarOneOnFill` no alcanza 4.5:1 sobre este relleno.
    private val Magenta = AqAccent("magenta", AqPalette.avatarOneFill, AqPalette.white)
    private val Sky = AqAccent("sky", AqPalette.infoFill, AqPalette.gray900)
    private val Amber = AqAccent("amber", AqPalette.warningFill, AqPalette.gray900)
    private val Lemon = AqAccent("lemon", AqPalette.cautionFill, AqPalette.gray900)

    val Admitted: List<AqAccent> = listOf(
        Blue, Ink, Emerald, Forest, Red, Wine,
        Violet, Indigo, Magenta, Sky, Amber, Lemon,
    )
}

/** Hash FNV-1a sobre UTF-8: asignación idéntica en Android e iOS. */
fun accentFor(contextId: String?): AqAccent {
    if (contextId == null) return AqAccents.System
    var hash: UInt = 0x811C9DC5u
    for (byte in contextId.encodeToByteArray()) {
        hash = hash xor byte.toUInt()
        hash *= 0x01000193u
    }
    return AqAccents.Admitted[(hash % AqAccents.Admitted.size.toUInt()).toInt()]
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
