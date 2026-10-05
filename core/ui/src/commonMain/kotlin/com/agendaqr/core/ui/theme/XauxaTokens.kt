package com.agendaqr.core.ui.theme

import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.Easing
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
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
    val lightSecondary = Color(0xFF68577C)
    val lightOnSecondary = Color(0xFFFFFFFF)
    val lightSecondaryContainer = Color(0xFFE8D1FD)
    val lightOnSecondaryContainer = Color(0xFF69587D)
    val lightTertiary = Color(0xFF671448)
    val lightOnTertiary = Color(0xFFFFFFFF)
    val lightTertiaryContainer = Color(0xFF842D60)
    val lightOnTertiaryContainer = Color(0xFFFFA4D1)
    val lightError = Color(0xFFBA1A1A)
    val lightOnError = Color(0xFFFFFFFF)
    val lightErrorContainer = Color(0xFFFFDAD6)
    val lightOnErrorContainer = Color(0xFF93000A)
    val lightInverseSurface = Color(0xFF332F35)
    val lightInverseOnSurface = Color(0xFFF6EEF7)
    val lightInversePrimary = Color(0xFFDAB9FF)

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
    val darkSecondary = Color(0xFFD4BEE9)
    val darkOnSecondary = Color(0xFF39294B)
    val darkSecondaryContainer = Color(0xFF524266)
    val darkOnSecondaryContainer = Color(0xFFC5B0DA)
    val darkTertiary = Color(0xFFFFAFD5)
    val darkOnTertiary = Color(0xFF5E0A41)
    val darkTertiaryContainer = Color(0xFF842D60)
    val darkOnTertiaryContainer = Color(0xFFFFA4D1)
    val darkError = Color(0xFFFFB4AB)
    val darkOnError = Color(0xFF690005)
    val darkErrorContainer = Color(0xFF93000A)
    val darkOnErrorContainer = Color(0xFFFFDAD6)
    val darkInverseSurface = Color(0xFFE8E0E9)
    val darkInverseOnSurface = Color(0xFF332F35)
    val darkInversePrimary = Color(0xFF734AA5)
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
)

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
    val Success: Color @Composable get() = LocalXauxaColorScheme.current.secondary
    val SuccessBg: Color @Composable get() = LocalXauxaColorScheme.current.secondaryContainer
    val Warning: Color @Composable get() = LocalXauxaColorScheme.current.tertiary
    val WarningBg: Color @Composable get() = LocalXauxaColorScheme.current.tertiaryContainer
    val Info: Color @Composable get() = LocalXauxaColorScheme.current.brand
    val InfoBg: Color @Composable get() = LocalXauxaColorScheme.current.brandContainer
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
    /** Unidad 1×1 de la rejilla de tiles (compacto, margen 16 + gaps 8). */
    val TileUnit = 76.dp
    /** Alto del tile ancho (4×2): dos unidades más una separación. */
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
data class XauxaAccent(val id: String, val background: Color, val onAccent: Color)

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

fun Dp.xauxaBorder() = this
