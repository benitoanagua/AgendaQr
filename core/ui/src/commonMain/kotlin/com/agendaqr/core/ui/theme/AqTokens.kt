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

    /** X-03: el QR siempre negro sobre blanco, con independencia del tema. */
    val qrCanvas = white
    val transparent = Color.Transparent
}

/** Roles semánticos completos; espejo de los 59 roles consumidos por el producto. */
data class AqColorScheme(
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

val LightAmColorScheme = AqColorScheme(
    background = AqPalette.gray50,
    surface = AqPalette.white,
    surfaceSecondary = AqPalette.lightSurfaceSecondary,
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

val DarkAmColorScheme = AqColorScheme(
    background = AqPalette.gray900,
    surface = AqPalette.gray800,
    surfaceSecondary = AqPalette.darkSurfaceSecondary,
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
    dangerContainer = AqPalette.gray800,
    success = AqPalette.darkTextSuccess,
    successFill = AqPalette.successFill,
    onSuccessFill = AqPalette.successOnFill,
    successContainer = AqPalette.gray800,
    warning = AqPalette.darkTextWarning,
    warningFill = AqPalette.warningFill,
    onWarningFill = AqPalette.warningOnFill,
    warningContainer = AqPalette.gray800,
    info = AqPalette.darkTextInfo,
    infoContainer = AqPalette.gray800,
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
val LocalAmColorScheme = compositionLocalOf { DarkAmColorScheme }

/** Familia de la interfaz. Se resuelve dentro de la composición (recursos CMP). */
val LocalAmFontFamily = compositionLocalOf<FontFamily> { FontFamily.Default }

/**
 * Carga los TTF estáticos de Inter (SIL OFL 1.1) distribuidos como recursos
 * multiplataforma. Las cuatro fuentes son las que publica la release 4.0.
 */
@Composable
internal fun rememberAmInterFontFamily(): FontFamily = FontFamily(
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
    val BrandFill: Color @Composable get() = LocalAmColorScheme.current.brandFill
    val BrandFillHover: Color @Composable get() = LocalAmColorScheme.current.brandFillHover
    val BrandFillActive: Color @Composable get() = LocalAmColorScheme.current.brandFillActive
    val BrandFillSelected: Color @Composable get() = LocalAmColorScheme.current.brandFillSelected
    val BrandFillDisabled: Color @Composable get() = LocalAmColorScheme.current.brandFillDisabled
    val OnBrandFill: Color @Composable get() = LocalAmColorScheme.current.onBrandFill
    val OnBrandFillDisabled: Color @Composable get() = LocalAmColorScheme.current.onBrandFillDisabled
    val EmphasisFill: Color @Composable get() = LocalAmColorScheme.current.emphasisFill
    val EmphasisBorder: Color @Composable get() = LocalAmColorScheme.current.emphasisBorder
    val EmphasisText: Color @Composable get() = LocalAmColorScheme.current.emphasisText
    val EmphasisIcon: Color @Composable get() = LocalAmColorScheme.current.emphasisIcon
    val EmphasisSurface: Color @Composable get() = LocalAmColorScheme.current.emphasisSurface
    val EmphasisSurfaceHover: Color @Composable get() = LocalAmColorScheme.current.emphasisSurfaceHover
    val OnEmphasisFill: Color @Composable get() = LocalAmColorScheme.current.emphasisOnFill
    val Link: Color @Composable get() = LocalAmColorScheme.current.link
    val SelectionFill: Color @Composable get() = LocalAmColorScheme.current.selectionFill
    val SelectionSurface: Color @Composable get() = LocalAmColorScheme.current.selectionSurface
    val SelectionBorder: Color @Composable get() = LocalAmColorScheme.current.selectionBorder
    val SelectionIcon: Color @Composable get() = LocalAmColorScheme.current.selectionIcon
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
object AqSpacing {
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

    /**
     * Interlineados proporcionales derivados de los pares publicados
     * `--p-font-line-height-*` (instantánea §tipografía). Se usan solo cuando
     * el rol recibe un tamaño arbitrario; los tamaños canónicos conservan su
     * `lineHeight` publicado.
     */
    const val HeadingLineHeightRatio = 1.25f
    const val BodyLineHeightRatio = 1.5f

    /** Familia efectiva de la interfaz (Inter dentro de [AqTheme]). */
    val FamilyUi: FontFamily @Composable get() = LocalAmFontFamily.current
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

/** Tipografía M3: adaptador de tipos; no aporta valores visuales propios. */
val AqMaterialTypography: Typography = Typography(
    headlineLarge = TextStyle(
        fontSize = AqType.HeadlineLarge,
        lineHeight = AqType.LineHeightHeadlineLarge,
        fontWeight = AqWeight.Bold,
        letterSpacing = AqTracking.Densest,
    ),
    headlineMedium = TextStyle(
        fontSize = AqType.Headline,
        lineHeight = AqType.LineHeightHeadlineMedium,
        fontWeight = AqWeight.Bold,
        letterSpacing = AqTracking.Denser,
    ),
    headlineSmall = TextStyle(
        fontSize = AqType.HeadlineSmall,
        lineHeight = AqType.LineHeightHeadlineSmall,
        fontWeight = AqWeight.Bold,
        letterSpacing = AqTracking.Dense,
    ),
    titleLarge = TextStyle(
        fontSize = AqType.TitleLarge,
        lineHeight = AqType.LineHeightTitleLarge,
        fontWeight = AqWeight.SemiBold,
        letterSpacing = AqTracking.Dense,
    ),
    titleMedium = TextStyle(
        fontSize = AqType.TitleMedium,
        lineHeight = AqType.LineHeightTitleMedium,
        fontWeight = AqWeight.SemiBold,
        letterSpacing = AqTracking.TitleMedium,
    ),
    titleSmall = TextStyle(
        fontSize = AqType.TitleSmall,
        lineHeight = AqType.LineHeightTitleSmall,
        fontWeight = AqWeight.SemiBold,
        letterSpacing = AqTracking.TitleSmall,
    ),
    bodyLarge = TextStyle(
        fontSize = AqType.Label,
        lineHeight = AqType.LineHeightLabel,
        fontWeight = AqWeight.Regular,
        letterSpacing = AqTracking.BodyLarge,
    ),
    bodyMedium = TextStyle(
        fontSize = AqType.Body,
        lineHeight = AqType.LineHeightBody,
        fontWeight = AqWeight.Regular,
        letterSpacing = AqTracking.BodyMedium,
    ),
    bodySmall = TextStyle(
        fontSize = AqType.BodySmall,
        lineHeight = AqType.LineHeightBodySmall,
        fontWeight = AqWeight.Regular,
        letterSpacing = AqTracking.BodySmall,
    ),
    labelLarge = TextStyle(
        fontSize = AqType.Label,
        lineHeight = AqType.LineHeightLabel,
        fontWeight = AqWeight.SemiBold,
        letterSpacing = AqTracking.LabelLarge,
    ),
    labelMedium = TextStyle(
        fontSize = AqType.Body,
        lineHeight = AqType.LineHeightBody,
        fontWeight = AqWeight.SemiBold,
        letterSpacing = AqTracking.LabelMedium,
    ),
    labelSmall = TextStyle(
        fontSize = AqType.BodySmall,
        lineHeight = AqType.LineHeightLabelSmall,
        fontWeight = AqWeight.SemiBold,
        letterSpacing = AqTracking.LabelSmall,
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
object AqTextStyles {
    val ButtonLabel = TextStyle(
        fontSize = AqType.Label,
        fontWeight = AqWeight.SemiBold,
        letterSpacing = AqTracking.ButtonLabel,
        lineHeight = AqType.LineHeightLabel,
    )
    val Body = TextStyle(
        fontSize = AqType.Body,
        fontWeight = AqWeight.Regular,
        lineHeight = AqType.LineHeightBody,
    )
    val Label = TextStyle(
        fontSize = AqType.Label,
        fontWeight = AqWeight.Regular,
        lineHeight = AqType.LineHeightLabel,
    )
    val BodySmall = TextStyle(
        fontSize = AqType.BodySmall,
        fontWeight = AqWeight.Regular,
        lineHeight = AqType.LineHeightBodySmall,
    )
    val RowTitle = TextStyle(
        fontSize = AqType.RowTitle,
        fontWeight = AqWeight.SemiBold,
        lineHeight = AqType.LineHeightRowTitle,
    )
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
    val Caption = TextStyle(
        fontSize = AqType.Caption,
        fontWeight = AqWeight.Regular,
        lineHeight = AqType.LineHeightCaption,
    )
    val Tag = TextStyle(
        fontSize = AqType.CaptionSmall,
        fontWeight = AqWeight.Regular,
        lineHeight = AqType.LineHeightLabelSmall,
    )
    val Support = TextStyle(
        fontSize = AqType.Caption,
        fontWeight = AqWeight.Regular,
        letterSpacing = AqTracking.LabelMedium,
        lineHeight = AqType.LineHeightCaption,
    )
    val SectionHeader = TextStyle(
        fontSize = AqType.SectionHeader,
        fontWeight = AqWeight.Regular,
        lineHeight = AqType.LineHeightSection,
    )
    val Title = TextStyle(
        fontSize = AqType.Title,
        fontWeight = AqWeight.SemiBold,
        letterSpacing = AqTracking.Dense,
        lineHeight = AqType.LineHeightDialog,
    )
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

/** Sombras publicadas: se conserva el desenfoque y la opacidad de cada capa. */
enum class AqElevation(
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
fun Modifier.aqElevation(
    level: AqElevation,
    shape: Shape = RectangleShape,
    clip: Boolean = false,
): Modifier {
    if (level == AqElevation.None) return this
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
