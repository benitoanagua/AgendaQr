package com.agendaqr.core.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import kotlin.math.pow
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agendaqr.core.ui.Res
import com.agendaqr.core.ui.archivo_bold
import com.agendaqr.core.ui.archivo_light
import com.agendaqr.core.ui.archivo_medium
import com.agendaqr.core.ui.archivo_regular
import com.agendaqr.core.ui.archivo_semibold
import org.jetbrains.compose.resources.Font

/**
 * Xauxa semantic tokens. The feature layer must consume only this semantic
 * facade; raw primitives stay private to the theme layer.
 */
internal object XauxaPrimitive {
    // Xauxa light scheme
    val lightBackground = Color(0xFFFFFFFF)
    val lightSurface = Color(0xFFFFFFFF)
    val lightSurfaceContainerLowest = Color(0xFFFFFFFF)
    val lightSurfaceContainerLow = Color(0xFFF7F7F7)
    val lightSurfaceContainer = Color(0xFFF2F2F2)
    val lightSurfaceContainerHigh = Color(0xFFEAEAEA)
    val lightSurfaceContainerHighest = Color(0xFFE0E0E0)
    val lightSurfaceVariant = Color(0xFFF2F2F2)
    val lightOutline = Color(0xFF666666)
    val lightOutlineVariant = Color(0xFFBDBDBD)
    val lightText = Color(0xFF000000)
    val lightTextVariant = Color(0xFF333333)
    val lightPrimary = Color(0xFF0067B8)
    val lightOnPrimary = Color(0xFFFFFFFF)
    val lightPrimaryContainer = Color(0xFF0067B8)
    val lightOnPrimaryContainer = Color(0xFFFFFFFF)
    // M3 requires secondary/tertiary slots; Xauxa has one brand accent.
    // These slots alias the brand instead of introducing a second palette.
    val lightSecondary = Color(0xFF0067B8)
    val lightOnSecondary = Color(0xFFFFFFFF)
    val lightSecondaryContainer = Color(0xFFF2F2F2)
    val lightOnSecondaryContainer = Color(0xFF000000)
    val lightTertiary = Color(0xFF0067B8)
    val lightOnTertiary = Color(0xFFFFFFFF)
    val lightTertiaryContainer = Color(0xFFF2F2F2)
    val lightOnTertiaryContainer = Color(0xFF000000)
    val lightError = Color(0xFFBA1A1A)
    val lightOnError = Color(0xFFFFFFFF)
    val lightErrorContainer = Color(0xFFFFDAD6)
    val lightOnErrorContainer = Color(0xFF93000A)
    val lightInverseSurface = Color(0xFF1A1A1A)
    val lightInverseOnSurface = Color(0xFFFFFFFF)
    val lightInversePrimary = Color(0xFF4DA3EA)

    // Fase 1 de la auditoría de accesibilidad: tonos de estado con par
    // propio (>= 4.5:1 content/container), brand legible como TEXTO y
    // borde de control >= 3:1. Valores verificados por XauxaSchemeTest.
    val lightSuccess = Color(0xFF0B6B2E)
    val lightSuccessContainer = Color(0xFFE3F4E8)
    val lightWarning = Color(0xFF7A4A00)
    val lightWarningContainer = Color(0xFFFFF1CC)
    val lightInfo = Color(0xFF004A87)
    val lightInfoContainer = Color(0xFFDCEBFA)
    val lightBrandText = Color(0xFF0067B8)
    val lightBorderControl = Color(0xFF858585)

    // Xauxa dark scheme
    val darkBackground = Color(0xFF000000)
    val darkSurface = Color(0xFF000000)
    val darkSurfaceContainerLowest = Color(0xFF000000)
    val darkSurfaceContainerLow = Color(0xFF111111)
    val darkSurfaceContainer = Color(0xFF1A1A1A)
    val darkSurfaceContainerHigh = Color(0xFF222222)
    val darkSurfaceContainerHighest = Color(0xFF2A2A2A)
    val darkSurfaceVariant = Color(0xFF1A1A1A)
    val darkOutline = Color(0xFF999999)
    val darkOutlineVariant = Color(0xFF555555)
    val darkText = Color(0xFFFFFFFF)
    val darkTextVariant = Color(0xFFCCCCCC)
    val darkPrimary = Color(0xFF0067B8)
    val darkOnPrimary = Color(0xFFFFFFFF)
    val darkPrimaryContainer = Color(0xFF0067B8)
    val darkOnPrimaryContainer = Color(0xFFFFFFFF)
    val darkSecondary = Color(0xFF0067B8)
    val darkOnSecondary = Color(0xFFFFFFFF)
    val darkSecondaryContainer = Color(0xFF2A2A2A)
    val darkOnSecondaryContainer = Color(0xFFFFFFFF)
    val darkTertiary = Color(0xFF0067B8)
    val darkOnTertiary = Color(0xFFFFFFFF)
    val darkTertiaryContainer = Color(0xFF2A2A2A)
    val darkOnTertiaryContainer = Color(0xFFFFFFFF)
    val darkError = Color(0xFFFFB4AB)
    val darkOnError = Color(0xFF690005)
    val darkErrorContainer = Color(0xFF93000A)
    val darkOnErrorContainer = Color(0xFFFFDAD6)
    val darkInverseSurface = Color(0xFFF2F2F2)
    val darkInverseOnSurface = Color(0xFF111111)

    val darkSuccess = Color(0xFF7FD99A)
    val darkSuccessContainer = Color(0xFF0F2E1A)
    val darkWarning = Color(0xFFFFD27A)
    val darkWarningContainer = Color(0xFF3A2A00)
    val darkInfo = Color(0xFF8CC4F5)
    val darkInfoContainer = Color(0xFF0B2A47)
    val darkBrandText = Color(0xFF4DA3EA)
    val darkBorderControl = Color(0xFF6E6E6E)
    val darkInversePrimary = Color(0xFF0067B8)
}

data class XauxaColorScheme(
    val background: Color,
    val surface: Color,
    val surface2: Color,
    val surface3: Color,
    val surfaceVariant: Color,
    val border: Color,
    val borderVariant: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textTertiary: Color,
    val brand: Color,
    val onBrand: Color,
    // NOTA (Fase 4): brandContainer/onBrandContainer son ALIAS de brand/
    // onBrand por diseño V1.1 ("la familia de marca es compartida entre
    // temas", XauxaSchemeTest la fija). Existen porque el esquema Material
    // pide un contenedor primario; no es un segundo acento.
    val brandContainer: Color,
    val onBrandContainer: Color,
    val secondary: Color,
    val onSecondary: Color,
    val secondaryContainer: Color,
    val onSecondaryContainer: Color,
    val tertiary: Color,
    val onTertiary: Color,
    val tertiaryContainer: Color,
    val onTertiaryContainer: Color,
    val danger: Color,
    val onDanger: Color,
    val dangerBg: Color,
    val onDangerBg: Color,
    val inverseSurface: Color,
    val inverseOnSurface: Color,
    val inverseBrand: Color,
    val focusRing: Color,
    // Fase 1 (auditoría a11y): estado con par propio, brand como texto y
    // borde de control — ver primitivas y XauxaSchemeTest.
    val success: Color,
    val successContainer: Color,
    val warning: Color,
    val warningContainer: Color,
    val info: Color,
    val infoContainer: Color,
    val brandText: Color,
    val borderControl: Color,
)

val LightXauxaColorScheme = XauxaColorScheme(
    background = XauxaPrimitive.lightBackground,
    surface = XauxaPrimitive.lightSurface,
    surface2 = XauxaPrimitive.lightSurfaceContainer,
    surface3 = XauxaPrimitive.lightSurfaceContainerHighest,
    surfaceVariant = XauxaPrimitive.lightSurfaceVariant,
    border = XauxaPrimitive.lightOutlineVariant,
    borderVariant = XauxaPrimitive.lightOutline,
    textPrimary = XauxaPrimitive.lightText,
    textSecondary = XauxaPrimitive.lightTextVariant,
    textTertiary = XauxaPrimitive.lightOutline,
    brand = XauxaPrimitive.lightPrimary,
    onBrand = XauxaPrimitive.lightOnPrimary,
    brandContainer = XauxaPrimitive.lightPrimaryContainer,
    onBrandContainer = XauxaPrimitive.lightOnPrimaryContainer,
    secondary = XauxaPrimitive.lightSecondary,
    onSecondary = XauxaPrimitive.lightOnSecondary,
    secondaryContainer = XauxaPrimitive.lightSecondaryContainer,
    onSecondaryContainer = XauxaPrimitive.lightOnSecondaryContainer,
    tertiary = XauxaPrimitive.lightTertiary,
    onTertiary = XauxaPrimitive.lightOnTertiary,
    tertiaryContainer = XauxaPrimitive.lightTertiaryContainer,
    onTertiaryContainer = XauxaPrimitive.lightOnTertiaryContainer,
    danger = XauxaPrimitive.lightError,
    onDanger = XauxaPrimitive.lightOnError,
    dangerBg = XauxaPrimitive.lightErrorContainer,
    onDangerBg = XauxaPrimitive.lightOnErrorContainer,
    inverseSurface = XauxaPrimitive.lightInverseSurface,
    inverseOnSurface = XauxaPrimitive.lightInverseOnSurface,
    inverseBrand = XauxaPrimitive.lightInversePrimary,
    focusRing = XauxaPrimitive.lightPrimary,
    success = XauxaPrimitive.lightSuccess,
    successContainer = XauxaPrimitive.lightSuccessContainer,
    warning = XauxaPrimitive.lightWarning,
    warningContainer = XauxaPrimitive.lightWarningContainer,
    info = XauxaPrimitive.lightInfo,
    infoContainer = XauxaPrimitive.lightInfoContainer,
    brandText = XauxaPrimitive.lightBrandText,
    borderControl = XauxaPrimitive.lightBorderControl,
)

val DarkXauxaColorScheme = XauxaColorScheme(
    background = XauxaPrimitive.darkBackground,
    surface = XauxaPrimitive.darkSurface,
    surface2 = XauxaPrimitive.darkSurfaceContainer,
    surface3 = XauxaPrimitive.darkSurfaceContainerHighest,
    surfaceVariant = XauxaPrimitive.darkSurfaceVariant,
    border = XauxaPrimitive.darkOutlineVariant,
    borderVariant = XauxaPrimitive.darkOutline,
    textPrimary = XauxaPrimitive.darkText,
    textSecondary = XauxaPrimitive.darkTextVariant,
    textTertiary = XauxaPrimitive.darkOutline,
    brand = XauxaPrimitive.darkPrimary,
    onBrand = XauxaPrimitive.darkOnPrimary,
    brandContainer = XauxaPrimitive.darkPrimaryContainer,
    onBrandContainer = XauxaPrimitive.darkOnPrimaryContainer,
    secondary = XauxaPrimitive.darkSecondary,
    onSecondary = XauxaPrimitive.darkOnSecondary,
    secondaryContainer = XauxaPrimitive.darkSecondaryContainer,
    onSecondaryContainer = XauxaPrimitive.darkOnSecondaryContainer,
    tertiary = XauxaPrimitive.darkTertiary,
    onTertiary = XauxaPrimitive.darkOnTertiary,
    tertiaryContainer = XauxaPrimitive.darkTertiaryContainer,
    onTertiaryContainer = XauxaPrimitive.darkOnTertiaryContainer,
    danger = XauxaPrimitive.darkError,
    onDanger = XauxaPrimitive.darkOnError,
    dangerBg = XauxaPrimitive.darkErrorContainer,
    onDangerBg = XauxaPrimitive.darkOnErrorContainer,
    inverseSurface = XauxaPrimitive.darkInverseSurface,
    inverseOnSurface = XauxaPrimitive.darkInverseOnSurface,
    inverseBrand = XauxaPrimitive.darkInversePrimary,
    focusRing = XauxaPrimitive.darkPrimary,
    success = XauxaPrimitive.darkSuccess,
    successContainer = XauxaPrimitive.darkSuccessContainer,
    warning = XauxaPrimitive.darkWarning,
    warningContainer = XauxaPrimitive.darkWarningContainer,
    info = XauxaPrimitive.darkInfo,
    infoContainer = XauxaPrimitive.darkInfoContainer,
    brandText = XauxaPrimitive.darkBrandText,
    borderControl = XauxaPrimitive.darkBorderControl,
)

// NOTA (Fase 4): el default del CompositionLocal es el esquema OSCURO.
// XauxaTheme SIEMPRE proporciona el esquema real (claro u oscuro según el
// sistema); este default solo afecta a composiciones sin XauxaTheme
// (tooling/previews). Se conserva oscuro por conservadurismo: cambiar el
// default alteraría previews existentes; no afecta a producción.
val LocalXauxaColorScheme = compositionLocalOf { DarkXauxaColorScheme }

object XauxaColor {
    val Background: Color @Composable get() = LocalXauxaColorScheme.current.background
    val Surface: Color @Composable get() = LocalXauxaColorScheme.current.surface
    val Surface2: Color @Composable get() = LocalXauxaColorScheme.current.surface2
    val Surface3: Color @Composable get() = LocalXauxaColorScheme.current.surface3
    val SurfaceVariant: Color @Composable get() = LocalXauxaColorScheme.current.surfaceVariant
    val Border: Color @Composable get() = LocalXauxaColorScheme.current.border
    val BorderVariant: Color @Composable get() = LocalXauxaColorScheme.current.borderVariant
    val TextPrimary: Color @Composable get() = LocalXauxaColorScheme.current.textPrimary
    val TextSecondary: Color @Composable get() = LocalXauxaColorScheme.current.textSecondary
    val TextTertiary: Color @Composable get() = LocalXauxaColorScheme.current.textTertiary
    val Brand: Color @Composable get() = LocalXauxaColorScheme.current.brand
    val OnBrand: Color @Composable get() = LocalXauxaColorScheme.current.onBrand
    val BrandContainer: Color @Composable get() = LocalXauxaColorScheme.current.brandContainer
    val OnBrandContainer: Color @Composable get() = LocalXauxaColorScheme.current.onBrandContainer
    val Secondary: Color @Composable get() = LocalXauxaColorScheme.current.secondary
    val OnSecondary: Color @Composable get() = LocalXauxaColorScheme.current.onSecondary
    val SecondaryContainer: Color @Composable get() = LocalXauxaColorScheme.current.secondaryContainer
    val OnSecondaryContainer: Color @Composable get() = LocalXauxaColorScheme.current.onSecondaryContainer
    val Tertiary: Color @Composable get() = LocalXauxaColorScheme.current.tertiary
    val OnTertiary: Color @Composable get() = LocalXauxaColorScheme.current.onTertiary
    val TertiaryContainer: Color @Composable get() = LocalXauxaColorScheme.current.tertiaryContainer
    val OnTertiaryContainer: Color @Composable get() = LocalXauxaColorScheme.current.onTertiaryContainer
    val Danger: Color @Composable get() = LocalXauxaColorScheme.current.danger
    val OnDanger: Color @Composable get() = LocalXauxaColorScheme.current.onDanger
    val DangerBg: Color @Composable get() = LocalXauxaColorScheme.current.dangerBg
    // Fase 1: el estado tiene par propio; el acento de marca ya no
    // hace doble trabajo de "info" (el banner de confirmación era
    // invisible: Brand sobre BrandContainer daba 1:1).
    val Success: Color @Composable get() = LocalXauxaColorScheme.current.success
    val SuccessBg: Color @Composable get() = LocalXauxaColorScheme.current.successContainer
    val Warning: Color @Composable get() = LocalXauxaColorScheme.current.warning
    val WarningBg: Color @Composable get() = LocalXauxaColorScheme.current.warningContainer
    val Info: Color @Composable get() = LocalXauxaColorScheme.current.info
    val InfoBg: Color @Composable get() = LocalXauxaColorScheme.current.infoContainer
    /** Brand legible como TEXTO (fondo Brand como texto fallaba en oscuro). */
    val BrandText: Color @Composable get() = LocalXauxaColorScheme.current.brandText
    /** Borde de control (>= 3:1 contra background/surface2; M9 funcional). */
    val BorderControl: Color @Composable get() = LocalXauxaColorScheme.current.borderControl
    val White: Color = Color.White
    val OnDangerBg: Color @Composable get() = LocalXauxaColorScheme.current.onDangerBg
    val InverseSurface: Color @Composable get() = LocalXauxaColorScheme.current.inverseSurface
    val InverseOnSurface: Color @Composable get() = LocalXauxaColorScheme.current.inverseOnSurface
    val InverseBrand: Color @Composable get() = LocalXauxaColorScheme.current.inverseBrand
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
    /** V1.1 (ADR-0005, spec §12 Rejilla): margen de pantalla — múltiplo de 4. */
    val ScreenMargin = 16.dp
    /** V1.1: separación entre tiles de la rejilla — múltiplo de 4. */
    val TileGap = 8.dp
}

object XauxaMetrics {
    val Border = 1.dp
    val BorderStrong = 2.dp
    /** §12 V1.1 (M8/M9): ancho de foco y de borde funcional — 2 dp, solo en
     * foco, campo en foco/error o tile seleccionado; sin bordes de reposo. */
    val Focus = 2.dp
    val ControlMinSize = 48.dp
    val ContentMaxWidth = 720.dp
    val QrPreviewSize = 240.dp
    val FavoriteIndicatorSize = 24.dp
    val BreakpointCompact = 480.dp
    val BreakpointMedium = 640.dp

    // V1.1 (ADR-0005, spec §12 Rejilla y tiles) — rejilla Metro de 4
    // columnas en compacto. Unidad base de tile y métricas derivadas en
    // múltiplos de 4 dp (Xauxa base 4px); la rejilla reduce columnas antes
    // de comprimir por debajo de la unidad mínima.
    /** Unidad 1×1 MÍNIMA de la rejilla de tiles (compacto, margen 16 + gaps 8);
     * el ancho/alto real se calcula desde el ancho disponible (XauxaTileGrid)
     * y el alto se deriva en `tileHeight` — la unidad nunca baja de esto. */
    val TileUnit = 76.dp
    /** Alto MÍNIMO del tile 2×2/4×2 con la unidad mínima (2×76+8); el alto
     * real se deriva de la unidad calculada (`tileHeight`), no de aquí. */
    val TileWideHeight = 160.dp
    /** Tamaño de icono estándar en tiles y acciones (un solo color, línea). */
    val IconSize = 24.dp
    /** Icono grande de tile (pequeño/mediano: glifo centrado dominante). */
    val IconSizeTile = 32.dp
    /** Altura de la barra de aplicación inferior (M7): 2–4 acciones con
     * icono + etiqueta visible; admite dos líneas de contenido y respeta
     * el objetivo táctil mínimo. */
    val AppBarHeight = 64.dp
    /**
     * Ancho mínimo de tile del catálogo del laboratorio. Existe para que
     * la cuadrícula adaptativa (`GridCells.Adaptive`) consuma un token
     * semántico en lugar de un literal `dp` fuera de la capa de tokens.
     * El valor (208dp) da a cada tile Metro una superficie de lectura
     * cómoda: nombre a 16sp en dos líneas y descripción en tres sin
     * compresión. La cuadrícula reduce columnas antes que comprimir el
     * contenido por debajo de este mínimo.
     */
    val CatalogCardMinWidth = 208.dp
    /**
     * Altura mínima de tile del catálogo. Garantiza un objetivo táctil
     * superior al mínimo de control y una superficie de lectura
     * consistente; las tiles con más texto crecen por encima de este
     * mínimo en lugar de recortar.
     */
    val CatalogTileMinHeight = 116.dp
    /**
     * Altura máxima de una lista dentro de un diálogo: las filas de
     * selección (S08, ¿a cuál corresponde?) deben ser alcanzables con
     * scroll cuando la lista es larga (§11);
     * por debajo de este tope la lista mide su contenido.
     */
    val DialogListMaxHeight = 320.dp
}

object XauxaType {
    val Display: TextUnit = 32.sp
    val Headline: TextUnit = 24.sp
    val Title: TextUnit = 20.sp
    val Body: TextUnit = 16.sp
    val Label: TextUnit = 14.sp
    val Caption: TextUnit = 12.sp
    val LetterSpacingWide: TextUnit = 0.5.sp
    // Fase 4: interlineados canónicos (sp — respetan el escalado, §11).
    val LineHeightLabel: TextUnit = 20.sp
    val LineHeightBody: TextUnit = 24.sp
    val LineHeightCaption: TextUnit = 16.sp
    val LineHeightSection: TextUnit = 20.sp
    /** V1.1 (ADR-0005, spec §12 Tipografía): título de página en display
     * ligero (Archivo 300) y ≥ 40 sp. */
    val DisplayPage: TextUnit = 40.sp
    /** V1.1: encabezados de sección pequeños (14–16 sp) en color de
     * acento. */
    val SectionHeader: TextUnit = 14.sp
    /**
     * T9 — §12: "Archivo para display/encabezados" (V1.1: el display de
     * página usa el peso Light 300). Empaquetada en
     * `composeResources/font` (5 pesos estáticos, subset latin, ~40 KB
     * cada uno); licencia OFL 1.1 documentada en
     * `docs/05-design-system/05-xauxa-tipografia.md` y
     * `06-licencias-terceros.md`. Solo se consume a través de
     * [com.agendaqr.core.ui.components.XauxaHeading] y
     * [com.agendaqr.core.ui.components.XauxaPageTitle].
     *
     * Acceso componible (mismo patrón que [XauxaColor]): el cargador de
     * fuentes de Compose retiene los typefaces.
     */
    val FamilyDisplay: FontFamily
        @Composable get() = FontFamily(
            Font(Res.font.archivo_light, FontWeight.Light),
            Font(Res.font.archivo_regular, FontWeight.Normal),
            Font(Res.font.archivo_medium, FontWeight.Medium),
            Font(Res.font.archivo_semibold, FontWeight.SemiBold),
            Font(Res.font.archivo_bold, FontWeight.Bold),
        )
    /** V1.1: peso del título de página (display ligero). */
    val WeightDisplayPage: FontWeight = FontWeight.Light
    /**
     * §12: "Roboto/San Francisco para UI/cuerpo": la sans del sistema
     * (Roboto en Android, SF en iOS) es la fuente de UI correcta sin
     * empaquetar una copia redundante; resperta el escalado del usuario.
     */
    val FamilyUi: FontFamily = FontFamily.Default
    val FamilyMono: FontFamily = FontFamily.Monospace
}

/**
 * Fase 4 (auditoría): estilos tipográficos canónicos. Reúnen fontSize,
 * peso, tracking y LINE HEIGHT (la pieza que las repeticiones manuales de
 * los componentes nunca fijaban). Los componentes consumen estos estilos
 * en lugar de repetir tríos de tokens; los tamaños siguen siendo los de
 * XauxaType (en sp: escalado del sistema, §11).
 */
object XauxaTextStyles {
    /** Etiqueta de botón (Body de acción): Label/Bold/tracking wide. */
    val ButtonLabel = androidx.compose.ui.text.TextStyle(
        fontSize = XauxaType.Label,
        fontWeight = FontWeight.Bold,
        letterSpacing = XauxaType.LetterSpacingWide,
        lineHeight = XauxaType.LineHeightLabel,
    )

    /** Cuerpo de texto (mensajes, filas). */
    val Body = androidx.compose.ui.text.TextStyle(
        fontSize = XauxaType.Body,
        lineHeight = XauxaType.LineHeightBody,
    )

    /** Subtítulo/metadato de fila. */
    val Caption = androidx.compose.ui.text.TextStyle(
        fontSize = XauxaType.Caption,
        lineHeight = XauxaType.LineHeightCaption,
    )

    /** Texto de apoyo (errores, helpers, badges). */
    val Support = androidx.compose.ui.text.TextStyle(
        fontSize = XauxaType.Caption,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = XauxaType.LetterSpacingWide,
        lineHeight = XauxaType.LineHeightCaption,
    )

    /** Etiqueta de acción de la app bar (bajo el icono). */
    val AppBarItem = androidx.compose.ui.text.TextStyle(
        fontSize = XauxaType.Caption,
        lineHeight = XauxaType.LineHeightCaption,
    )

    /** Encabezado de sección (14 sp en acento, M6). */
    val SectionHeader = androidx.compose.ui.text.TextStyle(
        fontSize = XauxaType.SectionHeader,
        lineHeight = XauxaType.LineHeightSection,
    )
}

/** Radio base único de Xauxa. Cambiarlo requiere revisión visual global. */
object XauxaRadius {
    val Base = 0.dp
}

/** Forma canónica para superficies y controles rectangulares del sistema. */
val XauxaShape = androidx.compose.foundation.shape.RoundedCornerShape(size = XauxaRadius.Base)

/**
 * Fase 3 (auditoría): opacidades funcionales del lenguaje. [Pressed] es la
 * capa de realimentación táctil (overlay sobre el contenido, nunca un
 * cambio de color local).
 */
object XauxaOpacity {
    const val Pressed = 0.08f
}

object XauxaMotion {
    const val DurationShortMs = 150
    const val DurationMediumMs = 300
    const val DurationLongMs = 450
    /**
     * Duración de las transiciones con movimiento reducido activo: 0.
     * Reduced motion no significa "animación corta" sino "sin
     * desplazamiento" (§11 accesibilidad); el cambio ocurre en el sitio.
     */
    const val DurationReducedMs = 0
    // Curvas en STRING: las consume el inspector del laboratorio
    // (LabFoundationPreview las muestra como contrato documentado); las
    // Easings reales (compose) están abajo. No son código muerto.
    const val EasingStandard = "cubic-bezier(0.1, 0.9, 0.2, 1)"
    const val EasingEmphasized = "cubic-bezier(0.1, 0.9, 0.2, 1)"
    const val EasingDecelerate = "cubic-bezier(0, 0, 0.2, 1)"
    object Easings {
        val Standard: Easing = CubicBezierEasing(0.1f, 0.9f, 0.2f, 1f)
        val Emphasized: Easing = CubicBezierEasing(0.1f, 0.9f, 0.2f, 1f)
        val Decelerate: Easing = CubicBezierEasing(0f, 0f, 0.2f, 1f)
    }
}

/**
 * V1.1 (ADR-0005, spec §12 Color): acento Metro. [background] es el bloque
 * plano del tile y [onAccent] el único color de texto admitido sobre él;
 * el par es fijo, calculado (tabla de contraste de la spec) e idéntico en
 * tema claro y oscuro (el fondo oscuro es negro puro y la regla de
 * contraste de cada par es válida contra él — ver spec §12 Modo oscuro).
 */
data class XauxaAccent(val id: String, val background: Color, val onAccent: Color) {
    /**
     * ADR-0011 (calidad de tiles): overlay de pulsación del bloque de
     * acento — el color OPUESTO al texto. El overlay por defecto del tema
     * ([XauxaColor.TextPrimary]) no vale aquí: sobre el propio bloque el
     * color del tema puede repetir el fondo y dejar la pulsación invisible.
     * El par onAccent/pressOverlay garantiza que el estado presionado
     * nunca baje de 4.5:1 (XauxaTileContractTest).
     */
    val pressOverlay: Color
        get() = if (onAccent == Color.White) Color.Black else Color.White
}

/**
 * Paleta de acentos V1.1: los 12 admitidos por la tabla de contraste
 * calculada de la spec §12 (ni uno más ni uno menos) más el acento de
 * sistema `0067B8` por defecto. Los colores de estado (danger/warning/
 * success) siguen siendo semánticos y separados.
 */
object XauxaAccents {
    val System = XauxaAccent("system", Color(0xFF0067B8), Color(0xFFFFFFFF))

    private val Lime = XauxaAccent("lime", Color(0xFFA4C400), Color(0xFF000000))
    private val Emerald = XauxaAccent("emerald", Color(0xFF008A00), Color(0xFF000000))
    private val Teal = XauxaAccent("teal", Color(0xFF00ABA9), Color(0xFF000000))
    private val Cyan = XauxaAccent("cyan", Color(0xFF1BA1E2), Color(0xFF000000))
    private val Cobalt = XauxaAccent("cobalt", Color(0xFF0050EF), Color(0xFFFFFFFF))
    private val Indigo = XauxaAccent("indigo", Color(0xFF6A00FF), Color(0xFFFFFFFF))
    private val Violet = XauxaAccent("violet", Color(0xFFAA00FF), Color(0xFFFFFFFF))
    private val Magenta = XauxaAccent("magenta", Color(0xFFD80073), Color(0xFFFFFFFF))
    private val Crimson = XauxaAccent("crimson", Color(0xFFA20025), Color(0xFFFFFFFF))
    private val Red = XauxaAccent("red", Color(0xFFE51400), Color(0xFFFFFFFF))
    private val Orange = XauxaAccent("orange", Color(0xFFFA6800), Color(0xFF000000))
    private val Amber = XauxaAccent("amber", Color(0xFFF0A30A), Color(0xFF000000))

    val Admitted: List<XauxaAccent> = listOf(
        Lime, Emerald, Teal, Cyan, Cobalt, Indigo,
        Violet, Magenta, Crimson, Red, Orange, Amber,
    )
}

/**
 * V1.1 (spec §12 Color): acento estable por contexto, derivado de forma
 * determinista del identificador del contexto. Sin persistencia ni cambio
 * de dominio; [contextId] nulo devuelve el acento de sistema.
 *
 * Hash propio FNV-1a (32 bits, sobre los bytes UTF-8 del identificador):
 * explícitamente NO `String.hashCode`, para que la asignación sea idéntica
 * en Android, iOS y wasmJs y no cambie entre ejecuciones ni plataformas.
 */
fun accentFor(contextId: String?): XauxaAccent {
    if (contextId == null) return XauxaAccents.System
    var hash: UInt = 0x811C9DC5u
    for (byte in contextId.encodeToByteArray()) {
        hash = hash xor byte.toUInt()
        hash *= 0x01000193u
    }
    return XauxaAccents.Admitted[(hash % XauxaAccents.Admitted.size.toUInt()).toInt()]
}

// ---------------------------------------------------------------------------------
// Fase 1 (auditoría de accesibilidad): matemática de contraste en la capa
// de tema. Funciones PURAS (no composable): las usan los componentes para
// decidir colores legibles y los tests para fijar los umbrales (§11).
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
 * cumple >= 4.5:1 (umbral de texto normal, spec §11/§12) y si no cae al
 * [fallback] legible. Fase 1: los acentos de contexto marcan identidad
 * con el bloque/marcador de color, pero NUNCA como texto ilegible.
 */
fun Color.asTextOn(background: Color, fallback: Color): Color =
    if (contrastRatio(this, background) >= 4.5) this else fallback

