package com.agendaqr.core.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
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
 * transcripción de Moon Design: `moon_tokens` 0.0.6 (`MoonColors.light` y
 * `MoonColors.dark`) para color y `moon_design` 1.1.0 para geometría y
 * movimiento (`docs/05-design-system/13-catalogo-moon.md`).
 *
 * Reglas de esta capa:
 * - Es el ÚNICO archivo de producción autorizado a contener literales
 *   visuales (hex, dp, sp, pesos, radios, duraciones, alfas).
 * - Las primitivas son internas; los consumidores usan [AqColorScheme],
 *   [AqColor], [AqSpacing], [AqMetrics], [AqType], [AqMotion] y [AqShadow].
 * - Los valores marcados «derivado» no existen en Moon: se calculan
 *   mezclando colores Moon para cumplir AA (D-02) o para estados que Moon
 *   resuelve con efectos de escala/pulso en lugar de color.
 */
internal object AqPalette {
    // MoonColors (nombres de Moon). Valores idénticos en claro y oscuro.
    val piccolo = Color(0xFF5C33CF)
    val hit = Color(0xFF1BD29A)
    val goten = Color(0xFFFFFFFF)
    val popo = Color(0xFF000000)
    val krillin = Color(0xFFFFB319)
    val chichi = Color(0xFFFF4E64)
    val roshi = Color(0xFF49B356)
    val dodoria = Color(0xFFD33030)
    val cell = Color(0xFF95F1D5)
    val raditz = Color(0xFFB3804A)
    val whis = Color(0xFF3448F0)
    val nappa = Color(0xFF725550)
    /** `jiren` de MoonColors: piccolo al 12 %; idéntico en ambos temas. */
    val jiren = Color(0x1F5C33CF)
    /** Derivados para acentos: roshi y raditz oscurecidos hasta AA con texto blanco. */
    val accentGreen = Color(0xFF2C6B34)
    val accentBronze = Color(0xFF745330)
    val zeno = Color(0x8F000000)

    // MoonColors.light
    val lightBeerus = Color(0xFFDCDEE3)
    val lightGoku = Color(0xFFFFFFFF)
    val lightGohan = Color(0xFFF6F7F9)
    val lightBulma = Color(0xFF000000)
    val lightTrunks = Color(0xFF687083)
    val lightHeles = Color(0x0A000000)

    // MoonColors.dark
    val darkBeerus = Color(0xFF292929)
    val darkGoku = Color(0xFF000000)
    val darkGohan = Color(0xFF1F1F1F)
    val darkBulma = Color(0xFFFFFFFF)
    val darkTrunks = Color(0xFF94989E)
    val darkHeles = Color(0x14FFFFFF)

    /** Moon resuelve disabled con opacidad 0,6 (`MoonOpacities`). */
    val piccoloDisabled = Color(0x995C33CF)
    val lightTrunksDisabled = Color(0x99687083)
    val darkTrunksDisabled = Color(0x9994989E)

    // Derivados claro (piccolo/dodoria hacia negro; rellenos tonales 8-20 %).
    val lightBrandHover = Color(0xFF512DB6)
    val lightBrandActive = Color(0xFF46279D)
    val lightEmphasisSurface = Color(0xFFEBE7F9)
    val lightEmphasisSurfaceHover = Color(0xFFDED6F5)
    val lightSelectionFill = Color(0xFFDED6F5)
    val lightSurfaceHigh = Color(0xFFECEDEF)
    val lightDanger = Color(0xFFBA2A2A)
    val lightDangerActive = Color(0xFFA02424)
    val lightDangerContainer = Color(0xFFFBEEEE)
    val lightDangerDisabled = Color(0xFFEDACAC)
    val lightSuccess = Color(0xFF2C6B34)
    val lightSuccessFill = Color(0xFF337D3C)
    val lightSuccessContainer = Color(0xFFEDF7EE)
    val lightWarning = Color(0xFF805A0C)
    val lightWarningContainer = Color(0xFFFFF6E3)
    val lightInfoContainer = Color(0xFFEFF0FE)

    // Derivados oscuro (piccolo/whis/chichi hacia blanco; tonos sobre gohan).
    val darkBrandHover = Color(0xFF6943D3)
    val darkBrandActive = Color(0xFF7654D7)
    val darkPiccoloText = Color(0xFFA58FE5)
    val darkEmphasisSurface = Color(0xFF2B2342)
    val darkEmphasisSurfaceHover = Color(0xFF332557)
    val darkSelectionFill = Color(0xFF372765)
    val darkSelectionSurface = Color(0xFF302550)
    val darkSurfaceHighest = Color(0xFF3A3A3A)
    val darkDanger = Color(0xFFFF7A8B)
    val darkDangerActive = Color(0xFFBA2A2A)
    val darkDangerContainer = Color(0xFF43272A)
    val darkDangerDisabled = Color(0xFF6A1818)
    val darkSuccessContainer = Color(0xFF263728)
    val darkWarningContainer = Color(0xFF43371E)
    val darkInfo = Color(0xFF8F9AF7)
    val darkInfoContainer = Color(0xFF232749)

    /** X-03: el QR siempre negro sobre blanco, con independencia del tema. */
    val qrCanvas = Color(0xFFFFFFFF)
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
    background = AqPalette.lightGoku,
    surface = AqPalette.lightGohan,
    surfaceSecondary = AqPalette.lightGohan,
    inputSurface = AqPalette.lightGohan,
    border = AqPalette.lightBeerus,
    // D-02: beerus (1,3:1) no sirve como límite funcional; se usa trunks (>= 3:1).
    functionalBorder = AqPalette.lightTrunks,
    divider = AqPalette.lightBeerus,
    textPrimary = AqPalette.lightBulma,
    textSecondary = AqPalette.lightTrunks,
    textPlaceholder = AqPalette.lightTrunks,
    textTertiary = AqPalette.lightTrunks,
    brandFill = AqPalette.piccolo,
    brandFillHover = AqPalette.lightBrandHover,
    brandFillActive = AqPalette.lightBrandActive,
    brandFillSelected = AqPalette.piccolo,
    brandFillDisabled = AqPalette.piccoloDisabled,
    onBrandFill = AqPalette.goten,
    onBrandFillDisabled = AqPalette.goten,
    emphasisFill = AqPalette.piccolo,
    emphasisBorder = AqPalette.piccolo,
    emphasisText = AqPalette.piccolo,
    emphasisIcon = AqPalette.piccolo,
    emphasisSurface = AqPalette.lightEmphasisSurface,
    emphasisSurfaceHover = AqPalette.lightEmphasisSurfaceHover,
    emphasisOnFill = AqPalette.goten,
    link = AqPalette.piccolo,
    selectionFill = AqPalette.lightSelectionFill,
    selectionSurface = AqPalette.lightEmphasisSurface,
    selectionBorder = AqPalette.piccolo,
    selectionIcon = AqPalette.piccolo,
    danger = AqPalette.lightDanger,
    dangerFill = AqPalette.dodoria,
    onDanger = AqPalette.goten,
    dangerContainer = AqPalette.lightDangerContainer,
    success = AqPalette.lightSuccess,
    successFill = AqPalette.lightSuccessFill,
    onSuccessFill = AqPalette.goten,
    successContainer = AqPalette.lightSuccessContainer,
    warning = AqPalette.lightWarning,
    warningFill = AqPalette.krillin,
    onWarningFill = AqPalette.popo,
    warningContainer = AqPalette.lightWarningContainer,
    info = AqPalette.whis,
    infoContainer = AqPalette.lightInfoContainer,
    focus = AqPalette.piccolo,
    disabled = AqPalette.lightHeles,
    onDisabled = AqPalette.lightTrunksDisabled,
    inverseSurface = AqPalette.lightBulma,
    inverseOnSurface = AqPalette.lightGoku,
    scrim = AqPalette.zeno,
    qrCanvas = AqPalette.qrCanvas,
    surfaceContainerLow = AqPalette.lightGoku,
    surfaceContainer = AqPalette.lightGohan,
    surfaceContainerHigh = AqPalette.lightSurfaceHigh,
    surfaceContainerHighest = AqPalette.lightBeerus,
    surfaceActive = AqPalette.lightSurfaceHigh,
    componentActive = AqPalette.lightGohan,
    dangerActive = AqPalette.lightDangerActive,
    dangerDisabled = AqPalette.lightDangerDisabled,
    transparent = AqPalette.transparent,
)

val DarkAqColorScheme = AqColorScheme(
    background = AqPalette.darkGoku,
    surface = AqPalette.darkGohan,
    surfaceSecondary = AqPalette.darkGohan,
    inputSurface = AqPalette.darkGohan,
    border = AqPalette.darkBeerus,
    functionalBorder = AqPalette.darkTrunks,
    divider = AqPalette.darkBeerus,
    textPrimary = AqPalette.darkBulma,
    textSecondary = AqPalette.darkTrunks,
    textPlaceholder = AqPalette.darkTrunks,
    textTertiary = AqPalette.darkTrunks,
    brandFill = AqPalette.piccolo,
    brandFillHover = AqPalette.darkBrandHover,
    brandFillActive = AqPalette.darkBrandActive,
    brandFillSelected = AqPalette.piccolo,
    brandFillDisabled = AqPalette.piccoloDisabled,
    onBrandFill = AqPalette.goten,
    onBrandFillDisabled = AqPalette.goten,
    // D-02: piccolo sobre negro da 2,8:1; texto, icono, borde y foco usan su
    // versión aclarada (derivado) para llegar a AA.
    emphasisFill = AqPalette.piccolo,
    emphasisBorder = AqPalette.darkPiccoloText,
    emphasisText = AqPalette.darkPiccoloText,
    emphasisIcon = AqPalette.darkPiccoloText,
    emphasisSurface = AqPalette.darkEmphasisSurface,
    emphasisSurfaceHover = AqPalette.darkEmphasisSurfaceHover,
    emphasisOnFill = AqPalette.goten,
    link = AqPalette.darkPiccoloText,
    selectionFill = AqPalette.darkSelectionFill,
    selectionSurface = AqPalette.darkSelectionSurface,
    selectionBorder = AqPalette.darkPiccoloText,
    selectionIcon = AqPalette.darkPiccoloText,
    danger = AqPalette.darkDanger,
    dangerFill = AqPalette.dodoria,
    onDanger = AqPalette.goten,
    dangerContainer = AqPalette.darkDangerContainer,
    success = AqPalette.roshi,
    successFill = AqPalette.lightSuccessFill,
    onSuccessFill = AqPalette.goten,
    successContainer = AqPalette.darkSuccessContainer,
    warning = AqPalette.krillin,
    warningFill = AqPalette.krillin,
    onWarningFill = AqPalette.popo,
    warningContainer = AqPalette.darkWarningContainer,
    info = AqPalette.darkInfo,
    infoContainer = AqPalette.darkInfoContainer,
    focus = AqPalette.darkPiccoloText,
    disabled = AqPalette.darkHeles,
    onDisabled = AqPalette.darkTrunksDisabled,
    inverseSurface = AqPalette.darkBulma,
    inverseOnSurface = AqPalette.darkGoku,
    scrim = AqPalette.zeno,
    qrCanvas = AqPalette.qrCanvas,
    surfaceContainerLow = AqPalette.darkGoku,
    surfaceContainer = AqPalette.darkGohan,
    surfaceContainerHigh = AqPalette.darkBeerus,
    surfaceContainerHighest = AqPalette.darkSurfaceHighest,
    surfaceActive = AqPalette.darkBeerus,
    componentActive = AqPalette.darkGohan,
    dangerActive = AqPalette.darkDangerActive,
    dangerDisabled = AqPalette.darkDangerDisabled,
    transparent = AqPalette.transparent,
)

// El default del CompositionLocal es el esquema OSCURO: AqTheme siempre
// proporciona el esquema real; este valor solo afecta a tooling sin AqTheme.
val LocalAqColorScheme = compositionLocalOf { LightAqColorScheme }

/** Familia de la interfaz. Se resuelve dentro de la composición (recursos CMP). */
val LocalAqFontFamily = compositionLocalOf<FontFamily> { FontFamily.Default }

/**
 * Carga los TTF estáticos de Inter (SIL OFL 1.1) distribuidos como recursos
 * multiplataforma. Moon no empaqueta fuente propia (Averta Std es comercial),
 * así que el producto fija Inter como familia de la interfaz.
 */
@Composable
internal fun rememberAqInterFontFamily(): FontFamily = FontFamily(
    Font(resource = Res.font.inter_regular, weight = AqWeight.Regular),
    Font(resource = Res.font.inter_medium, weight = AqWeight.Medium),
    Font(resource = Res.font.inter_semi_bold, weight = AqWeight.SemiBold),
    Font(resource = Res.font.inter_bold, weight = AqWeight.Bold),
)

/**
 * Alfas semánticos para los estados pressed/selected (0,25 y 0,20);
 * [Hidden] significa «sin pintura», no un color nuevo.
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
    /** `jiren` de MoonColors: piccolo al 12 %; idéntico en ambos temas
     *  (fondo activo de chips/controles de hover secundarios de Moon). */
    val Jiren: Color get() = AqPalette.jiren
    /** `popo` de MoonColors (negro puro en ambos temas): contenido sobre
     *  superficies claras invertidas como el pulgar del switch. */
    val Popo: Color get() = AqPalette.popo
    /** `roshi` de MoonColors (verde de estado, idéntico en ambos temas):
     *  color de la insignia de [AqAvatar] (`avatar_theme.dart`). */
    val Roshi: Color get() = AqPalette.roshi
    val Transparent: Color = Color.Transparent
}

/** Escala de espaciado del producto, en dp. */
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

/**
 * Escala de espaciado única para la composición de pantallas y componentes
 * (D-12: decisión de producto vigente; origen: tabla de espaciado Moon y
 * derivada de los tokens de tamaño existentes).
 *
 * Cada valor se expresa con el token numérico equivalente ya definido en
 * [AqSpacing] o [AqSize]; ningún valor se duplica como literal. Un hueco
 * entre hermanos usa siempre el valor numérico Moon de la tabla expresado
 * con su token (p. ej. hueco de input 8 dp = [AqSize.X4s]), nunca [AqSize]
 * como "escala genérica de huecos".
 */
object AqLayout {
    /** Margen lateral de pantalla (16 dp; [AqSpacing.ScreenMargin]). */
    val ScreenMargin: Dp = AqSpacing.ScreenMargin
    /** Hueco entre organismos/contenedores consecutivos de una pantalla (16 dp = [AqSpacing.Lg]). */
    val BetweenOrganisms: Dp = AqSpacing.Lg
    /** Hueco entre elementos dentro de un mismo grupo/celda (8 dp = [AqSize.X4s]). */
    val InGroup: Dp = AqSize.X4s
    /** Relleno interno de celda en los 4 lados (12 dp = [AqSize.X3s]). */
    val CellPadding: Dp = AqSize.X3s
    /** Hueco horizontal leading↔label↔trailing de [AqMenuItem] (Moon
     *  `menu_item`: el gap equivale al padding `x3s` del contenedor). */
    val MenuItemGap: Dp = AqSize.X3s
    /** Hueco entre pestañas de [AqTabBar]/[AqPillTab] (`tab_bar`
     *  `properties.gap` = `sizes.x5s`). */
    val TabBarGap: Dp = AqSize.X5s
    /** Relleno interno de tarjeta en los 4 lados (16 dp = [AqSize.X2s]). */
    val CardPadding: Dp = AqSize.X2s
    /** Hueco entre etiqueta persistente y su caja de entrada (8 dp = [AqSize.X4s]). */
    val LabelToField: Dp = AqSize.X4s
    /** Relleno vertical del dock inferior (12 dp = [AqSize.X3s]). */
    val DockVertical: Dp = AqSize.X3s

    /** Cero dp: permite inicializar operaciones de [Dp] sin literales
     *  `[0].dp`, que el gate prohíbe fuera de la capa de tokens. */
    val Zero: Dp = 0.dp

    /**
     * Relleno vertical interno de [AqTextInput] / [AqTextArea] por tamaño
     * (Moon §3 TextInput: sm 2, md 4, lg 6, xl 9). Los valores 6 y 9 no
     * pertenecen a la escala [AqSize] y se declaran aquí como tokens del
     * sistema, nunca como literales en los componentes.
     */
    val InputPaddingVerticalSm: Dp = 2.dp
    val InputPaddingVerticalMd: Dp = 4.dp
    val InputPaddingVerticalLg: Dp = 6.dp
    val InputPaddingVerticalXl: Dp = 9.dp

    /**
     * Relleno horizontal interno de [AqTextInput] / [AqTextArea] por tamaño
     * (Moon §3 TextInput: sm 8, md 12, lg 12, xl 16).
     */
    val InputPaddingHorizontalSm: Dp = 8.dp
    val InputPaddingHorizontalMd: Dp = 12.dp
    val InputPaddingHorizontalLg: Dp = 12.dp
    val InputPaddingHorizontalXl: Dp = 16.dp

    // -------------------------------------------------------------------------
    // Geometría de componentes transcrita de moon_design 1.1.0 (§ tabla de
    // medidas). Estos valores son la autoridad numérica; ningún componente
    // define medidas propias.
    // -------------------------------------------------------------------------

    /** Altura visual de [AqButton] por tamaño (Moon § Botón: xs 24 / sm 32 /
     *  md 40 / lg 48 / xl 56 dp). Ya expresada con [AqSize]; se declara aquí
     *  para que [AqButtonSize] no duplique medidas. */
    val ButtonHeightXs: Dp = AqSize.Xs
    val ButtonHeightSm: Dp = AqSize.Sm
    val ButtonHeightMd: Dp = AqSize.Md
    val ButtonHeightLg: Dp = AqSize.Lg
    val ButtonHeightXl: Dp = AqSize.Xl

    /** Relleno horizontal de [AqButton] por tamaño (Moon § Botón: xs 8 / sm
     *  12 / md 16 / lg 16 / xl 24 dp). */
    val ButtonPaddingHorizontalXs: Dp = AqSize.X4s
    val ButtonPaddingHorizontalSm: Dp = AqSize.X3s
    val ButtonPaddingHorizontalMd: Dp = AqSize.X2s
    val ButtonPaddingHorizontalLg: Dp = AqSize.X2s
    val ButtonPaddingHorizontalXl: Dp = AqSize.Xs

    /** Hueco entre ícono y etiqueta de [AqButton] por tamaño (Moon § Botón:
     *  xs 4 / sm 4 / md 8 / lg 12 / xl 16 dp). md-xl estaban *ver fuente* en
     *  el catálogo §3 y se transcriben aquí desde moon_design 1.1.0. */
    val ButtonGapXs: Dp = AqSize.X5s
    val ButtonGapSm: Dp = AqSize.X5s
    val ButtonGapMd: Dp = AqSize.X4s
    val ButtonGapLg: Dp = AqSize.X3s
    val ButtonGapXl: Dp = AqSize.X2s

    /** Altura visual de [AqTag] por tamaño (Moon § Tag: x2s 16 / xs 24 / sm
     *  32 dp). */
    val TagHeightX2s: Dp = AqSize.X2s
    val TagHeightXs: Dp = AqSize.Xs
    val TagHeightSm: Dp = AqSize.Sm

    /** Relleno horizontal de [AqTag] por tamaño (Moon `tag_sizes.dart`:
     *  x2s/xs `sizes.x4s`, sm `sizes.x2s`). */
    val TagPaddingHorizontalX2s: Dp = AqSize.X4s
    val TagPaddingHorizontalXs: Dp = AqSize.X4s
    val TagPaddingHorizontalSm: Dp = AqSize.X2s

    /** Hueco interno de [AqTag] por tamaño (Moon `tag_sizes.dart`: x2s/xs
     *  `sizes.x6s`, sm `sizes.x4s`). */
    val TagGapX2s: Dp = AqSize.X6s
    val TagGapXs: Dp = AqSize.X6s
    val TagGapSm: Dp = AqSize.X4s

    /** Tamaño del glifo de [AqTag] por tamaño (Moon `tag_sizes.dart`: x2s
     *  `sizes.x3s`, xs/sm `sizes.x2s`). */
    val TagIconSizeX2s: Dp = AqSize.X3s
    val TagIconSizeXs: Dp = AqSize.X2s
    val TagIconSizeSm: Dp = AqSize.X2s

    /** Altura visual de [AqSwitch] por tamaño (Moon § Switch: x2s 16 / xs 24
     *  / sm 32 dp). */
    val SwitchHeightX2s: Dp = AqSize.X2s
    val SwitchHeightXs: Dp = AqSize.Xs
    val SwitchHeightSm: Dp = AqSize.Sm

    /** Ancho de la pista de [AqSwitch] por tamaño (Moon § Switch: x2s 32 /
     *  xs 44 / sm 60 dp). Valores fuera de la escala [AqSize]: se declaran
     *  aquí como tokens del sistema, nunca como literales en el componente. */
    val SwitchTrackWidthX2s: Dp = 32.dp
    val SwitchTrackWidthXs: Dp = 44.dp
    val SwitchTrackWidthSm: Dp = 60.dp

    /** Diámetro del pulgar de [AqSwitch] por tamaño (Moon
     *  `switch_sizes.dart`: x2s 12 / xs 16 / sm 24 dp — el pulgar sm usa
     *  `sizes.xs`, no un valor intermedio). */
    val SwitchThumbX2s: Dp = 12.dp
    val SwitchThumbXs: Dp = 16.dp
    val SwitchThumbSm: Dp = AqSize.Xs

    /** Relleno interior de la pista de [AqSwitch] por tamaño (Moon
     *  `switch_sizes.dart`: x2s `sizes.x6s`, xs/sm `sizes.x5s`). */
    val SwitchTrackPaddingX2s: Dp = AqSize.X6s
    val SwitchTrackPaddingXs: Dp = AqSize.X5s
    val SwitchTrackPaddingSm: Dp = AqSize.X5s

    /** Lado del glifo de [AqCheckbox] y [AqRadio] (Moon
     *  `checkbox.dart`/`radio.dart`: `Size(16, 16)` = `sizes.x2s`). */
    val CheckboxGlyphSize: Dp = AqSize.X2s
    /** Ancho del trazo del check/dash y del borde de la casilla (Moon
     *  `checkbox_painter.dart` `_kStrokeWidth` = 1, `BorderSide` por defecto). */
    val CheckboxStrokeWidth: Dp = AqStroke.Default
    /** Radio exterior del glifo de radio (Moon `radio_painter.dart`
     *  `_kOuterRadius` = 8 sobre caja de 16 → círculo completo). */
    val RadioOuterRadius: Dp = 8.dp
    /** Radio del punto interior del radio (Moon `_kInnerRadius` = 4). */
    val RadioInnerRadius: Dp = 4.dp

    /** Hueco entre opciones de [AqSegmentedControl] (Moon
     *  `segmented_control_sizes.dart`: sm `sizes.x5s`, md `sizes.x4s`). */
    val SegmentedControlGapSm: Dp = AqSize.X5s
    val SegmentedControlGapMd: Dp = AqSize.X4s

    /** Relleno horizontal de cada segmento de [AqSegmentedControl] (Moon:
     *  sm `sizes.x3s`, md `sizes.x2s`). */
    val SegmentedControlPaddingHorizontalSm: Dp = AqSize.X3s
    val SegmentedControlPaddingHorizontalMd: Dp = AqSize.X2s

    /** Relleno exterior de [AqSegmentedControl] (Moon
     *  `segmented_control_theme.dart`: `EdgeInsets.all(sizes.x5s)`). */
    val SegmentedControlPadding: Dp = AqSize.X5s
    /** Altura total del control segmentado por tamaño (Moon
     *  `segmented_control_sizes.dart`: sm `sizes.md`, md `sizes.lg`). */
    val SegmentedControlHeightSm: Dp = AqSize.Md
    val SegmentedControlHeightMd: Dp = AqSize.Lg
    /** Tamaño del icono de segmento (Moon `iconSizeValue = sizes.xs`). */
    val SegmentedControlIconSize: Dp = AqSize.Xs
    /** Hueco exterior entre segmentos (Moon
     *  `segmented_control_theme.dart`: `gap = sizes.x5s`). */
    val SegmentedControlOuterGap: Dp = AqSize.X5s

    /** Hueco interno de [AqChip] entre leading/label/trailing (Moon
     *  `chip_sizes.dart`: `gap = sizes.x4s` en sm y md). */
    val ChipGap: Dp = AqSize.X4s
    /** Tamaño del icono de [AqChip] (Moon `iconSizeValue = sizes.xs`). */
    val ChipIconSize: Dp = AqSize.Xs

    /** Hueco alrededor del separador de [AqBreadcrumb] (Moon
     *  `breadcrumb_theme.dart`: `gap = sizes.x4s`). */
    val BreadcrumbGap: Dp = AqSize.X4s
    /** Hueco interno de un ítem de [AqBreadcrumb] entre leading/label/
     *  trailing (Moon `itemGap = sizes.x6s`). */
    val BreadcrumbItemGap: Dp = AqSize.X6s

    /** Ancho de [AqDrawer] (Moon `drawer_theme.dart`: 448 dp). */
    val DrawerWidth: Dp = 448.dp

    /** Tamaño del punto de [AqDotIndicator] (Moon
     *  `dot_indicator_theme.dart`: `sizes.x4s`). */
    val DotIndicatorSize: Dp = AqSize.X4s
    /** Hueco entre puntos de [AqDotIndicator] (Moon: `sizes.x4s`). */
    val DotIndicatorGap: Dp = AqSize.X4s

    /** Geometría de [AqPagination] (Moon v1 Figma «Pagination»: fila de
     *  32 dp con `gap` 4 dp; ítems compactos de 24 dp con radio `xs`). */
    val PaginationItemSize: Dp = AqSize.Xs

    /** Anchos de [AqSidebar] (Moon v1 Figma «Sidebar»: 280 dp expandida,
     *  64 dp rail colapsada). */
    val SidebarWidthExpanded: Dp = 280.dp
    val SidebarWidthCollapsed: Dp = 64.dp

    /** Insignia de puesto de [AqLeaderboard] (24 dp, `sizes.xs`). */
    val LeaderboardRankSize: Dp = AqSize.Xs

    /** Geometría del clavo de [AqProgressPin] (Moon
     *  `progress_pin_theme.dart`: pinWidth 36, arrowHeight 6,
     *  arrowWidth `sizes.x4s`, pinDistance `sizes.x5s`,
     *  pinBorderWidth `sizes.x6s`, shadowElevation 6). */
    val ProgressPinWidth: Dp = 36.dp
    val ProgressPinArrowHeight: Dp = 6.dp
    val ProgressPinArrowWidth: Dp = AqSize.X4s
    val ProgressPinDistance: Dp = AqSize.X5s
    val ProgressPinBorderWidth: Dp = AqSize.X6s
    /** `drawShadow(path, popo, 6)` de Flutter: el engine convierte la
     *  elevación en una silueta difuminada desplazada hacia abajo; estos
     *  valores reproducen esa capa única en `aqShadowPlatform`. */
    val ProgressPinShadowElevation: Dp = 6.dp
    val ProgressPinShadowBlur: Dp = 14.dp
    val ProgressPinShadowOffset: Dp = 5.dp
    val ProgressPinShadowAlpha: Float = 0.4f

    /** Hueco vertical entre las etiquetas y la barra de
     *  [AqLinearProgress] (`linear_progress_sizes.dart` `verticalGap`:
     *  x6s/x5s `sizes.x4s`, x2s `sizes.x5s`; x4s/x3s usan el literal 6 de
     *  Moon, token propio porque no pertenece a la escala [AqSize]). */
    val ProgressLabelGap: Dp = AqSize.X4s
    val ProgressLabelGapSmall: Dp = 6.dp

    /** Handle de arrastre de [AqBottomSheet] (Moon no define handle; se
     *  declara aquí como token del sistema, nunca literal en el
     *  componente). */
    val BottomSheetHandleWidth: Dp = 36.dp
    val BottomSheetHandleHeight: Dp = AqSize.X5s

    /** Geometría de overlays anclados (Moon `popover`/`dropdown`/`tooltip`):
     *  distancia al objetivo `sizes.x4s`; padding de contenido popover
     *  `sizes.x3s`, dropdown `sizes.x5s`; margen del dropdown `sizes.x4s`;
     *  flecha del tooltip base `sizes.x2s`, largo y distancia
     *  `sizes.x4s`; padding del tooltip `sizes.x3s`. */
    val OverlayDistanceToTarget: Dp = AqSize.X4s
    val PopoverContentPadding: Dp = AqSize.X3s
    val DropdownContentPadding: Dp = AqSize.X5s
    val DropdownMargin: Dp = AqSize.X4s
    val TooltipArrowBaseWidth: Dp = AqSize.X2s
    val TooltipArrowLength: Dp = AqSize.X4s
    val TooltipArrowTipDistance: Dp = AqSize.X4s
    val TooltipContentPadding: Dp = AqSize.X3s

    /** Hueco entre tarjetas de [AqCarousel] (Moon
     *  `carousel_theme.dart`: `sizes.x2s`). */
    val CarouselGap: Dp = AqSize.X2s

    /** Geometría de [AqTable] por tamaño de fila (Moon
     *  `table_sizes.dart`): alturas xs..x2l, hueco `sizes.x5s`, icono de
     *  orden `sizes.x2s`, hueco icono-orden `sizes.x5s`, relleno
     *  horizontal xs `sizes.x4s` y sm..x2l `sizes.x3s`. */
    val TableRowGap: Dp = AqSize.X5s
    val TableSortIconSize: Dp = AqSize.X2s
    val TableSortIconGap: Dp = AqSize.X5s
    val TableCellPaddingHorizontalXs: Dp = AqSize.X4s
    val TableCellPaddingHorizontal: Dp = AqSize.X3s
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
    val NavItemWidth = 88.dp
    val NavIndicatorWidth = 24.dp
    /** Cápsula de selección tras el icono de la navegación inferior. */
    val NavPillWidth = 56.dp
    val NavPillHeight = 32.dp
    val NavRailWidth = 80.dp
    val ContentMaxWidth = 720.dp
    val QrPreviewSize = 240.dp
    val QrReviewSize = 320.dp
    val IconSize = 24.dp
    val IconSizeCompact = 20.dp
    /** Icono del mensaje de error (`generic_info_16` de Moon, `x2s`). */
    val ErrorMessageIconSize: Dp = AqSize.X2s
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
    /** Miniatura de comprobante en filas de lista (cuadrada). */
    val ReceiptThumbnailSize = 56.dp
    val GridIconContainerSize = 48.dp
    val ScannerCornerLength = 24.dp
    val ContextBadgeSize = 40.dp
    val FabClearance = ControlMinSize + AqSpacing.Lg
    val ListRowEndMetaMinWidth = 320.dp
    const val ListRowEndMetaStackFontScale = 2f
}

/**
 * Proporciones internas de composición (D-08). No son escalas de Moon:
 * se conservan de la implementación auditada y se concentran aquí para que
 * ningún componente defina geometría propia. No alteran los tamaños táctiles.
 */
object AqComposition {
    /** Ancho de la línea de título del skeleton respecto al ancho disponible. */
    const val SkeletonTitleWidthFraction = 0.6f

    /** Tamaño del glifo dentro del círculo de confirmación de resultado. */
    const val SuccessCheckIconFraction = 0.55f

    /** Fracción del tamaño del avatar para la insignia (Moon
     *  `avatar_sizes.dart`: `badgeSizeValue = avatarSize * sizes.x3s /
     *  sizes.x2l` = tamaño × 12/64). */
    const val AvatarBadgeSizeFraction = 0.1875f
    /** Fracción del tamaño del avatar para el margen de la insignia (Moon:
     *  `badgeMarginValue = avatarSize * sizes.x5s / sizes.x2l` = × 4/64). */
    const val AvatarBadgeMarginFraction = 0.0625f
}

/** Formas de esquina (`CornerBasedShape`) para la capa de tema (MaterialTheme).
 *  Definidas aquí (capa de tokens) para que `AqTheme.kt` solo las référence.
 */
object AqThemeShapes {
    val ExtraSmall = RoundedCornerShape(AqRadius.ExtraSmall)
    val Small = RoundedCornerShape(AqRadius.Small)
    val Medium = RoundedCornerShape(AqRadius.Medium)
    val Large = RoundedCornerShape(AqRadius.Large)
    val ExtraLarge = RoundedCornerShape(AqRadius.ExtraLarge)
}

/** Escala de radios de producto (controles y contenedores usan [AqCorner]). */
object AqRadius {
    val ExtraSmall = 2.dp
    val Small = 4.dp
    val Medium = 8.dp
    val Large = 12.dp
    val ExtraLarge = 16.dp
    val GridIcon = 8.dp
    val Round = 9999.dp
}

/**
 * Escala de tamaños de la capa UX (ADR-0039). Transcripción de `MoonSizes.sizes`
 * (`moon_design` 1.1.0, `lib/src/theme/tokens/sizes.dart`). Los componentes
 * derivan alturas, huecos, iconos y rellenos de esta escala; el área táctil
 * mínima de 48 dp (D-02) se garantiza aparte con [AqMetrics.ControlMinSize].
 */
object AqSize {
    val X6s = 2.dp
    val X5s = 4.dp
    val X4s = 8.dp
    val X3s = 12.dp
    val X2s = 16.dp
    val Xs = 24.dp
    val Sm = 32.dp
    val Md = 40.dp
    val Lg = 48.dp
    val Xl = 56.dp
    val X2l = 64.dp
}

/** Grosores de borde (`MoonBorders.defaultBorderWidth` / `activeBorderWidth`). */
object AqStroke {
    val Default = 1.dp
    val Active = 1.5.dp
}

/**
 * Radios de esquina (`MoonBorders.borders`, `tokens/borders.dart`). `Interactive*`
 * es para controles (botón, input, chip, tab); `Surface*` para contenedores
 * (tarjeta, hoja, diálogo). [Smoothing] es el suavizado squircle por defecto
 * (0,9: el valor 1,0 produce NaN en la implementación de origen).
 */
object AqCorner {
    val InteractiveXs = 4.dp
    val InteractiveSm = 8.dp
    val InteractiveMd = 12.dp
    val SurfaceXs = 4.dp
    val SurfaceSm = 8.dp
    val SurfaceMd = 12.dp
    val SurfaceLg = 16.dp
    const val Smoothing = 0.9f
}

/** Opacidades de estado (`MoonOpacities.opacities`). */
object AqStateAlpha {
    const val Disabled = 0.6f
}

/** Pesos de Moon (`MoonTextStyles`: cuerpo regular 400, encabezado semibold 600); Inter estática cubre estos cortes. */
object AqWeight {
    val Regular = FontWeight(400)
    val Medium = FontWeight(500)
    val SemiBold = FontWeight(600)
    val Bold = FontWeight(700)
}

/** Tamaños de la escala de texto de Moon (`MoonTextStyles`: 12, 14, 16, 18, 20, 24, 32, 40) con interlineados del producto. */
object AqType {
    /** Roles de encabezado (peso semibold de Moon). */
    val Heading3Xl: TextUnit = 40.sp
    val LineHeightHeading3Xl: TextUnit = 48.sp
    val Heading2Xl: TextUnit = 32.sp
    val LineHeightHeading2Xl: TextUnit = 40.sp
    val HeadingXl: TextUnit = 24.sp
    val LineHeightHeadingXl: TextUnit = 32.sp
    val HeadingLg: TextUnit = 18.sp
    val LineHeightHeadingLg: TextUnit = 24.sp
    val HeadingMd: TextUnit = 16.sp
    val LineHeightHeadingMd: TextUnit = 20.sp
    val HeadingSm: TextUnit = 14.sp
    val LineHeightHeadingSm: TextUnit = 20.sp
    val HeadingXs: TextUnit = 12.sp
    val LineHeightHeadingXs: TextUnit = 16.sp

    /** Roles de cuerpo (peso regular de Moon). */
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
    val HeadlineSmall: TextUnit = 24.sp
    val TitleLarge: TextUnit = 20.sp
    val Title = HeadingLg
    val TitleMedium = HeadingMd
    val TitleSmall = HeadingSm
    /** Héroe de importe: rol `heading-2xl`. */
    val Amount: TextUnit = 32.sp
    val AmountSymbol = HeadingSm
    val RowTitle = HeadingMd
    val SectionHeader = HeadingSm
    val Body = BodySm
    val Label = BodyMd
    val BodySmall = BodyXs
    val Caption = BodyXs
    /** Rol `body-xs`; piso tipográfico del producto. */
    val CaptionSmall: TextUnit = 12.sp
    /** `body.text9` de Moon (`text_styles.dart`): microtexto de tags x2s. */
    val Body9: TextUnit = 9.sp
    /** `body.text10`/`heading.text10` de Moon: etiquetas de progreso y
     *  avatares xs. */
    val Body10: TextUnit = 10.sp
    /** `body.text8` de Moon: texto de pista del switch tamaño xs. */
    val Body8: TextUnit = 8.sp
    /** `body.text6` de Moon: texto de pista del switch tamaño x2s. */
    val Body6: TextUnit = 6.sp
    /** `body.text20`/`heading.text20` de Moon: iniciales del avatar x2l. */
    val Body20: TextUnit = 20.sp
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
     * Ratios derivados de los pares `heading-2xl` (40/32) y `body-md` (24/16).
     * Se aplican solo a tamaños arbitrarios (PV-08); los roles canónicos
     * conservan su `lineHeight`.
     */
    const val HeadingLineHeightRatio = 1.25f
    const val BodyLineHeightRatio = 1.5f

    /** Familia efectiva de la interfaz (Inter dentro de [AqTheme]). */
    val FamilyUi: FontFamily @Composable get() = LocalAqFontFamily.current
}

/** Espaciado de letras: Moon no define tracking, todo es 0 sp. */
object AqTracking {
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

/** Geometría completa de un rol tipográfico. */
internal data class AqTextRole(
    val size: TextUnit,
    val lineHeight: TextUnit,
    val weight: FontWeight,
    val tracking: TextUnit,
)

/** Resolución de tamaños canónicos y de tamaños arbitrarios (PV-08). */
internal object AqTextRoles {
    val Heading3Xl = AqTextRole(
        AqType.Heading3Xl, AqType.LineHeightHeading3Xl, AqWeight.SemiBold, AqTracking.Normal,
    )
    val Heading2Xl = AqTextRole(
        AqType.Heading2Xl, AqType.LineHeightHeading2Xl, AqWeight.SemiBold, AqTracking.Normal,
    )
    val HeadingXl = AqTextRole(
        AqType.HeadingXl, AqType.LineHeightHeadingXl, AqWeight.SemiBold, AqTracking.Normal,
    )
    val HeadingLg = AqTextRole(
        AqType.HeadingLg, AqType.LineHeightHeadingLg, AqWeight.SemiBold, AqTracking.Normal,
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
        weight = if (heading) AqWeight.SemiBold else AqWeight.Regular,
        tracking = AqTracking.Normal,
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

/** Duraciones y curvas de Moon; los roles de producto son X-04. */
object AqMotion {
    const val DurationReducedMs = 0
    const val DurationShortMs = 150
    const val DurationMediumMs = 200
    const val DurationLongMs = 250
    /**
     * Duración por defecto de [AqToast].
     *
     * Moon (`moon_design` 1.1.0, `widgets/toast` + `toast_theme.dart`):
     * `displayDuration` = 3 s, `transitionDuration` =
     * `defaultTransitionDuration` (200 ms) con `defaultTransitionCurve`.
     */
    const val ToastDurationMs = 3000L
    /**
     * `autoPlayDelay` por defecto de [AqCarousel] (Moon
     * `carousel_theme.dart`: `Duration(seconds: 3)`).
     */
    const val CarouselAutoPlayDelayMs = 3000L
    /**
     * `transitionDuration` del autoplay de [AqCarousel] (Moon
     * `carousel_theme.dart`: 800 ms `fastOutSlowIn`).
     */
    const val CarouselTransitionDurationMs = 800
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
        /** Curva por defecto de transición de la capa UX (`Curves.easeInOutCubic`). */
        val InOutCubic: Easing = CubicBezierEasing(0.645f, 0.045f, 0.355f, 1f)
        /** `Curves.fastOutSlowIn` de Flutter: transición del autoplay de [AqCarousel]. */
        val FastOutSlowIn: Easing = CubicBezierEasing(0.4f, 0f, 0.2f, 1f)
    }
}

/** Capa de una sombra: desenfoque, desplazamiento vertical, dispersión y alfa. */
data class AqShadowLayer(
    val blur: Dp,
    val offsetY: Dp,
    val spread: Dp,
    val alpha: Float,
)

/**
 * Sombras de la capa UX (`MoonShadows.light` / `MoonShadows.dark`,
 * `tokens/shadows.dart`). La geometría (blur, offset, spread, alfa) es de la
 * capa UX; el color de la sombra sale de la paleta (negro). `Modifier.shadow`
 * de Compose no expone offset ni spread (D-10): [aqShadow] apila una sombra
 * por capa usando el blur como elevación y conserva el resto como metadato.
 */
enum class AqShadow(val light: List<AqShadowLayer>, val dark: List<AqShadowLayer>) {
    Sm(
        light = listOf(
            AqShadowLayer(1.dp, 0.dp, 0.dp, 0.4f),
            AqShadowLayer(6.dp, 6.dp, (-6).dp, 0.157f),
        ),
        dark = listOf(
            AqShadowLayer(1.dp, 0.dp, 0.dp, 0.557f),
            AqShadowLayer(6.dp, 6.dp, (-6).dp, 0.639f),
        ),
    ),
    Md(
        light = listOf(
            AqShadowLayer(1.dp, 0.dp, 0.dp, 0.4f),
            AqShadowLayer(12.dp, 12.dp, 0.dp, 0.157f),
        ),
        dark = listOf(
            AqShadowLayer(1.dp, 0.dp, 0.dp, 0.557f),
            AqShadowLayer(12.dp, 12.dp, 0.dp, 0.639f),
        ),
    ),
    Lg(
        light = listOf(
            AqShadowLayer(1.dp, 0.dp, 0.dp, 0.4f),
            AqShadowLayer(24.dp, 8.dp, 0.dp, 0.157f),
        ),
        dark = listOf(
            AqShadowLayer(1.dp, 0.dp, 0.dp, 0.557f),
            AqShadowLayer(24.dp, 24.dp, 0.dp, 0.639f),
        ),
    ),
    Xl(
        light = listOf(
            AqShadowLayer(1.dp, 0.dp, 0.dp, 0.2f),
            AqShadowLayer(32.dp, 0.dp, 0.dp, 0.118f),
            AqShadowLayer(32.dp, 32.dp, 0.dp, 0.078f),
        ),
        dark = listOf(
            AqShadowLayer(1.dp, 0.dp, 0.dp, 0.718f),
            AqShadowLayer(48.dp, 48.dp, 0.dp, 0.878f),
        ),
    ),
}

/**
 * Aplica las capas de [level] según el tema activo (claro/oscuro). El pintado
 * es por plataforma: Android reproduce los `BoxShadow` de Moon con
 * `BlurMaskFilter` (blur/offset/spread reales); el resto usa la aproximación
 * por elevación de `Modifier.shadow`.
 */
@Composable
fun Modifier.aqShadow(level: AqShadow, shape: Shape = RectangleShape): Modifier {
    val dark = LocalAqColorScheme.current == DarkAqColorScheme
    val layers = if (dark) level.dark else level.light
    return aqShadowPlatform(layers, shape)
}

/** Identidad de contexto X-06: pares fijos de colores Moon, validados a AA. */
data class AqAccent(val id: String, val background: Color, val onAccent: Color)

object AqAccents {
    val System = AqAccent("system", AqPalette.piccolo, AqPalette.goten)

    private val Blue = AqAccent("blue", AqPalette.whis, AqPalette.goten)
    private val Green = AqAccent("green", AqPalette.accentGreen, AqPalette.goten)
    private val Red = AqAccent("red", AqPalette.dodoria, AqPalette.goten)
    private val Brown = AqAccent("brown", AqPalette.nappa, AqPalette.goten)
    private val Bronze = AqAccent("bronze", AqPalette.accentBronze, AqPalette.goten)
    private val Slate = AqAccent("slate", AqPalette.lightTrunks, AqPalette.goten)
    private val Ink = AqAccent("ink", AqPalette.lightBulma, AqPalette.goten)
    private val Mint = AqAccent("mint", AqPalette.hit, AqPalette.popo)
    private val Aqua = AqAccent("aqua", AqPalette.cell, AqPalette.popo)
    private val Amber = AqAccent("amber", AqPalette.krillin, AqPalette.popo)
    private val Coral = AqAccent("coral", AqPalette.chichi, AqPalette.popo)

    val Admitted: List<AqAccent> = listOf(
        Blue, Green, Red, Brown, Bronze, Slate, Ink, Mint, Aqua, Amber, Coral,
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
