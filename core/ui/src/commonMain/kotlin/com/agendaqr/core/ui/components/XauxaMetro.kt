package com.agendaqr.core.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.toggleableState
import com.agendaqr.core.ui.motion.LocalReducedMotion
import com.agendaqr.core.ui.theme.XauxaAccent
import com.agendaqr.core.ui.theme.XauxaAccents
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.core.ui.theme.XauxaMetrics
import com.agendaqr.core.ui.theme.XauxaMotion
import com.agendaqr.core.ui.theme.XauxaSpacing
import com.agendaqr.core.ui.theme.XauxaTextStyles
import com.agendaqr.core.ui.theme.XauxaType
import com.agendaqr.core.ui.theme.asTextOn
import com.agendaqr.core.ui.theme.XauxaShapes

/**
 * Piezas de presentación de página y sección dentro de Xauxa.
 * Invariantes conservados: foco visible, targets ≥
 * [XauxaMetrics.ControlMinSize].
 */

/**
 * Título de página (§12 V1.2): el rol `titleLarge` de M3 con la familia de
 * sistema, tal como lo mostrará la barra superior. Capitalización de
 * oración (la cadena se respeta tal cual; ningún componente Xauxa la
 * transforma).
 */
@Composable
fun XauxaPageTitle(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = XauxaColor.TextPrimary,
) {
    androidx.compose.material3.Text(
        text = text,
        modifier = modifier.semantics { heading() },
        style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
        fontFamily = XauxaType.FamilyUi,
        color = color,
    )
}

/**
 * Encabezado de sección (§12 V1.2): texto del rol `titleSmall` de M3 en
 * color estándar, con semántica `heading()`. El acento de contexto solo
 * tiñe los encabezados de SU superficie (S06) cuando el llamador pasa un
 * [accent] legible como texto.
 */
@Composable
fun XauxaSectionHeader(
    text: String,
    modifier: Modifier = Modifier,
    accent: Color? = null,
) {
    val textColor = accent?.asTextOn(XauxaColor.Background, XauxaColor.BrandText)
        ?: XauxaColor.TextSecondary
    androidx.compose.material3.Text(
        text = text,
        modifier = modifier.semantics { heading() },
        style = XauxaTextStyles.SectionHeader,
        fontFamily = XauxaType.FamilyUi,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
        color = textColor,
    )
}

/** Tamaños de tile sobre la rejilla de 4 columnas en compacto (M3). */
enum class XauxaTileSize { SMALL, MEDIUM, WIDE }

/**
 * Tile Metro (M3/M9/M11): un solo bloque de color plano (el acento), icono
 * centrado y etiqueta de texto SIEMPRE visible, abajo a la izquierda. Sin
 * borde ni sombra en reposo; el borde de 2 dp solo aparece con foco de
 * teclado (M9 funcional) sobre el color del tile (§11/M14).
 *
 * - Tilt al presionar: ≤ [XauxaMotion.DurationShortMs] (150 ms); con
 *   reduced motion no hay tilt (§11).
 * - El tile ancho ([XauxaTileSize.WIDE]) muestra su [content] como texto
 *   (último QR o actividad reciente); el icono se omite y la etiqueta se
 *   mantiene visible (M3).
 * - Geometría: el alto se DERIVA de [unit] (la unidad real calculada por
 *   la rejilla) para que 1×1 siga siendo cuadrado por encima de ~360 dp;
 *   [XauxaMetrics.TileUnit] queda como mínimo. El alto es MÍNIMO, no fijo:
 *   crece con la fuente del usuario.
 *
 * Accesibilidad (M14): rol de botón, etiqueta visible textual, estado y
 * orden de foco lógico por posición. [selected] convierte el tile en un
 * control de estado (p. ej. el filtro Favoritos): expone `selected` y
 * `toggleableState` — el estado NUNCA depende solo del color (§11), así
 * que el llamador lo expresa también con texto/glifo visible.
 */
@Composable
fun XauxaMetroTile(
    label: String,
    accent: XauxaAccent,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    size: XauxaTileSize = XauxaTileSize.SMALL,
    icon: ImageVector? = null,
    /** Estado de conmutación del tile (Favoritos): null = no es un toggle. */
    selected: Boolean? = null,
    /** Unidad real de la rejilla; default el mínimo [XauxaMetrics.TileUnit]. */
    unit: androidx.compose.ui.unit.Dp = XauxaMetrics.TileUnit,
    reducedMotion: Boolean = LocalReducedMotion.current,
    content: (@Composable () -> Unit)? = null,
) {
    val interaction = remember { MutableInteractionSource() }
    val pressed by interaction.collectIsPressedAsState()
    // M11: tilt ≤150 ms; reduced motion = sin tilt (cambio inmediato).
    val scale by animateFloatAsState(
        targetValue = if (pressed && !reducedMotion) TiltScale else FullScale,
        animationSpec = tween(XauxaMotion.DurationShortMs, easing = XauxaMotion.Easings.Standard),
        label = "xauxa_tile_tilt",
    )
    // Geometría derivada de la unidad calculada (1×1 cuadrado; 2×2/4×2 =
    // dos unidades más la separación), nunca del mínimo fijo.
    val height = tileHeight(size, unit)
    // ADR-0011: overlay OPUESTO al texto del acento (negro si onAccent es
    // blanco, blanco si es negro) — el contraste presionado nunca baja
    // de 4.5:1 (XauxaTileContractTest).
    val pressOverlay = accent.pressOverlay
    val fontScale = androidx.compose.ui.platform.LocalDensity.current.fontScale
    Box(
        modifier = modifier
            // El clickable va ANTES del graphicsLayer del tilt: el tilt NO
            // encoje el área táctil (48dp intactos).
            .then(
                if (onClick != null) {
                    Modifier
                        .clickable(
                            interactionSource = interaction,
                            indication = null,
                            role = Role.Button,
                            onClick = onClick,
                        )
                        .focusable(interactionSource = interaction)
                } else {
                    Modifier
                },
            )
            // Altura MÍNIMA, no fija — el tile crece con la fuente grande
            // del usuario (§11 escalado) en vez de recortar su etiqueta.
            .heightIn(min = height)
            .fillMaxWidth()
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .background(accent.background, XauxaShapes.Medium)
            .xauxaPressFeedback(interaction, overlayColor = pressOverlay)
            .xauxaFocusRing(interaction, shape = XauxaShapes.Medium, color = tileFocusRingColor(accent))
            .then(
                if (selected != null) {
                    Modifier.semantics {
                        this.selected = selected
                        toggleableState =
                            if (selected) androidx.compose.ui.state.ToggleableState.On
                            else androidx.compose.ui.state.ToggleableState.Off
                    }
                } else {
                    Modifier
                },
            ),
    ) {
        val horizontalPadding = if (size == XauxaTileSize.SMALL) XauxaSpacing.Xs else XauxaSpacing.Sm
        Column(
            modifier = Modifier.fillMaxWidth().padding(horizontal = horizontalPadding, vertical = XauxaSpacing.Sm),
        ) {
            // Zona flexible para icono/contenido.
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                if (icon != null && size != XauxaTileSize.WIDE) {
                    XauxaIcon(
                        imageVector = icon,
                        contentDescription = null,
                        size = XauxaMetrics.IconSizeTile,
                        tint = accent.onAccent,
                    )
                }
                if (content != null && size == XauxaTileSize.WIDE) {
                    content()
                }
            }
            // Etiqueta debajo, siempre visible; maxLines según escala.
            // D2 (medido en Robolectric NATIVE, Roboto real): el tile 1x1
            // ofrece 68 px de texto (76 menos padding Xs) y "Contextos" a
            // 14 sp ocupa ~64 px limpio pero ~69 px con el tracking ambiente
            // (0.5 sp heredado de Material): partia ("Contexto/s"). La
            // etiqueta del tile PEQUENO va en Caption (12 sp, ~55 px) con
            // tracking neutro explicito: cabe a 1.0x y a 1.15x (63 px) sin
            // partir; mediano/ancho conservan Label con tracking neutro.
            androidx.compose.material3.Text(
                text = label,
                fontSize = if (size == XauxaTileSize.SMALL) XauxaType.Caption else XauxaType.Label,
                letterSpacing = XauxaType.LetterSpacingNone,
                fontFamily = XauxaType.FamilyUi,
                color = accent.onAccent,
                maxLines = tileLabelMaxLines(fontScale),
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
            )
        }
    }
}

/** Un tile de la rejilla Metro: etiqueta, acento, acción y tamaño. */
data class XauxaTileItem(
    val label: String,
    val accent: XauxaAccent = XauxaAccents.System,
    val size: XauxaTileSize = XauxaTileSize.SMALL,
    val icon: ImageVector? = null,
    /** Estado de conmutación (p. ej. el filtro Favoritos); null = no toggle. */
    val selected: Boolean? = null,
    val onClick: (() -> Unit)? = null,
    val content: (@Composable () -> Unit)? = null,
)

/**
 * Geometría de tiles como función PURA: el alto se deriva de la unidad
 * REAL de la rejilla ([unit]) — 1×1 cuadrado; 2×2 y 4×2, dos unidades más
 * una separación — para que el tile siga siendo cuadrado cuando el ancho
 * disponible excede el mínimo ([XauxaMetrics.TileUnit], que antes fijaba
 * el alto a 76 dp mientras el ancho crecía: a 411 dp el 1×1 medía
 * 88,75×76). Testeada en XauxaTileGridTest.
 */
internal fun tileHeight(
    size: XauxaTileSize,
    unit: androidx.compose.ui.unit.Dp,
    gap: androidx.compose.ui.unit.Dp = XauxaSpacing.TileGap,
): androidx.compose.ui.unit.Dp = when (size) {
    XauxaTileSize.SMALL -> unit
    XauxaTileSize.MEDIUM, XauxaTileSize.WIDE -> unit * 2 + gap
}

/**
 * Color del anillo de foco de un tile de acento: el color del CONTENIDO
 * del bloque ([XauxaAccent.onAccent]). El anillo de marca sobre el propio
 * bloque de marca (o sobre acentos parecidos) da 1:1 — invisible; el
 * contenido del tile siempre cumple >= 3:1 contra su fondo (par calculado
 * de la spec §12 Color; XauxaFocusRingContrastTest lo fija para TODOS los
 * acentos).
 */
internal fun tileFocusRingColor(accent: XauxaAccent): Color = accent.onAccent

/**
 * Rejilla de tiles (M3/M11): 4 columnas en compacto; si el ancho disponible
 * no admite la unidad mínima, reduce columnas antes que comprimir. El tile
 * ancho (4×2) ocupa siempre toda la fila.
 *
 * Entrada escalonada: 30–50 ms por tile ([StaggerStepMs]), total acotado a
 * [XauxaMotion.DurationMediumMs] (300 ms). Con reduced motion no hay
 * escalonado ni transición: los tiles aparecen de inmediato (§11); la
 * comprensión nunca depende de la animación.
 */
@Composable
fun XauxaTileGrid(
    items: List<XauxaTileItem>,
    modifier: Modifier = Modifier,
    reducedMotion: Boolean = LocalReducedMotion.current,
) {
    BoxWithConstraints(modifier.fillMaxWidth()) {
        val unitWithGap = XauxaMetrics.TileUnit + XauxaSpacing.TileGap
        val fitUnits = ((maxWidth + XauxaSpacing.TileGap) / unitWithGap)
            .toInt().coerceIn(1, GridColumns)
        val fontScale = androidx.compose.ui.platform.LocalDensity.current.fontScale
        // columnas por escala de fuente (menos columnas con fuente
        // grande para que cada tile conserve ancho legible).
        val maxUnits = tileColumnsFor(fitUnits, fontScale)
        val unit = (maxWidth - XauxaSpacing.TileGap * (maxUnits - 1)) / maxUnits
        val rows = remember(items, maxUnits) { tileRows(items, maxUnits) }
        Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.TileGap)) {
            rows.forEach { rowItems ->
                // IntrinsicSize.Min — un tile que crece con fuente
                // grande alinea la fila completa (sin desalineación).
                Row(
                    horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.TileGap),
                    modifier = Modifier.height(androidx.compose.foundation.layout.IntrinsicSize.Min),
                ) {
                    rowItems.forEach { (index, item, span) ->
                        val tileWidth = unit * span + XauxaSpacing.TileGap * (span - 1)
                        TileAppear(index, reducedMotion) { alpha ->
                            Box(
                                modifier = Modifier
                                    .width(tileWidth)
                                    .graphicsLayer { this.alpha = alpha() },
                            ) {
                                XauxaMetroTile(
                                    label = item.label,
                                    accent = item.accent,
                                    onClick = item.onClick,
                                    modifier = Modifier.fillMaxWidth().fillMaxHeight(),
                                    size = item.size,
                                    icon = item.icon,
                                    selected = item.selected,
                                    unit = unit,
                                    reducedMotion = reducedMotion,
                                    content = item.content,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Entrada escalonada de un tile: alpha 0 → 1 con retardo por índice.
 *
 * CORRECCIÓN: el [Animatable] se envuelve en [remember] — sin esto, cada
 * recomposición crea una NUEVA instancia en 0f y el tile parpadea o queda
 * invisible. El alpha se lee como PROVEEDOR (`() -> Float`) dentro de
 * `graphicsLayer`, evitando recomposición por frame de animación. Con
 * reduced motion arranca en FullScale (sin retardo ni parpadeo).
 */
@Composable
private fun TileAppear(
    index: Int,
    reducedMotion: Boolean,
    content: @Composable (() -> Float) -> Unit,
) {
    val delayMs = tileStaggerDelayMs(index, reducedMotion)
    val appear = remember(delayMs) {
        androidx.compose.animation.core.Animatable(
            initialValue = if (delayMs == 0) FullScale else 0f,
        )
    }
    LaunchedEffect(delayMs) {
        if (delayMs > 0) {
            appear.animateTo(
                targetValue = FullScale,
                animationSpec = tween(
                    durationMillis = XauxaMotion.DurationShortMs,
                    delayMillis = delayMs,
                    easing = XauxaMotion.Easings.Standard,
                ),
            )
        }
    }
    content { appear.value }
}

/** Filas de la rejilla como datos puros (testeable; span por item). */
internal data class XauxaTileRowItem(val index: Int, val item: XauxaTileItem, val span: Int)

internal fun tileRows(items: List<XauxaTileItem>, maxUnits: Int): List<List<XauxaTileRowItem>> {
    fun spanOf(item: XauxaTileItem): Int = when (item.size) {
        XauxaTileSize.WIDE -> maxUnits
        XauxaTileSize.MEDIUM -> 2.coerceAtMost(maxUnits)
        XauxaTileSize.SMALL -> 1
    }
    val rows = mutableListOf<MutableList<XauxaTileRowItem>>()
    var rowUnits = 0
    items.forEachIndexed { index, item ->
        val span = spanOf(item)
        if (rowUnits + span > maxUnits) {
            rows.add(mutableListOf())
            rowUnits = 0
        }
        if (rows.isEmpty()) rows.add(mutableListOf())
        rows.last().add(XauxaTileRowItem(index, item, span))
        rowUnits += span
    }
    return rows.map { row -> row.toList() }
}

/**
 * Secciones con pestañas estándar (§12 V1.2, S06): `PrimaryScrollableTabRow`
 * con las secciones QR · Actividades · Comprobantes. La conmutación es
 * local: NO crea rutas ni navegación nueva (§3: los estados técnicos no
 * crean navegación artificial).
 *
 * Contrato §4.4 (tipografía al 100/150/200 %): las pestañas viven en una
 * fila DESPLAZABLE — con fuente grande las etiquetas no se comprimen ni se
 * recortan: la última pestaña se alcanza desplazando. El grupo declara
 * `selectableGroup()` y cada pestaña rol de pestaña con estado `selected`
 * y etiqueta de texto visible (§11).
 */
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun XauxaPivot(
    sections: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    accent: Color? = null,
    content: @Composable (Int) -> Unit,
) {
    require(sections.isNotEmpty()) { "XauxaPivot necesita al menos una sección" }
    require(selectedIndex in sections.indices) { "XauxaPivot: índice fuera de rango" }
    // El acento de contexto solo es texto de pestaña si es legible
    // (>= 4.5:1); si no, cae al acento de marca legible.
    val selectedTextColor = accent?.asTextOn(XauxaColor.Background, XauxaColor.BrandText)
        ?: XauxaColor.BrandText
    androidx.compose.material3.PrimaryScrollableTabRow(
        selectedTabIndex = selectedIndex,
        modifier = modifier.fillMaxWidth(),
    ) {
        sections.forEachIndexed { index, title ->
            val selected = index == selectedIndex
            androidx.compose.material3.Tab(
                selected = selected,
                onClick = { onSelect(index) },
                text = {
                    androidx.compose.material3.Text(
                        text = title,
                        color = if (selected) selectedTextColor else XauxaColor.TextSecondary,
                        maxLines = 1,
                    )
                },
            )
        }
    }
    content(selectedIndex)
}

/**
 * líneas máximas de la etiqueta del tile según la escala de fuente.
 * Escala normal (≤1.3): 2 líneas. Fuente grande (>1.3): 4 líneas para no
 * truncar nombres largos con elipsis agresiva.
 */
internal fun tileLabelMaxLines(fontScale: Float): Int =
    if (fontScale > 1.3f) 4 else 2

/**
 * columnas de la rejilla según la escala de fuente del usuario. A
 * mayor fuente, menos columnas para que cada tile tenga ancho legible.
 * >1.15 → 3 columnas; ≥1.5 → 2 columnas.
 */
internal fun tileColumnsFor(fitUnits: Int, fontScale: Float): Int = when {
    fontScale >= 1.5f -> fitUnits.coerceAtMost(2)
    fontScale > 1.15f -> fitUnits.coerceAtMost(3)
    else -> fitUnits
}

private const val FullScale = 1f
private const val TiltScale = 0.96f
/** M11: paso por tile de la entrada escalonada — 40 ms (rango 30–50). */
private const val StaggerStepMs = 40
private const val GridColumns = 4

/**
 * Entrada escalonada (M11): retardo por índice — [index] * 30–50 ms por
 * tile, ACOTADO para que retardo + fundido ([XauxaMotion.DurationShortMs])
 * no exceda [XauxaMotion.DurationMediumMs] (300 ms totales de la spec
 * §12 Movimiento; antes el último tile terminaba a 450 ms). Con reduced
 * motion el retardo es 0 (aparición inmediata). Función PURA, cubierta
 * por XauxaTileGridTest.
 */
internal fun tileStaggerDelayMs(index: Int, reducedMotion: Boolean): Int =
    if (reducedMotion) {
        0
    } else {
        (index * StaggerStepMs).coerceAtMost(
            XauxaMotion.DurationMediumMs - XauxaMotion.DurationShortMs,
        )
    }

