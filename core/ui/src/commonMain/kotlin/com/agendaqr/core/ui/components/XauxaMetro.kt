package com.agendaqr.core.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import com.agendaqr.core.ui.motion.LocalReducedMotion
import com.agendaqr.core.ui.theme.XauxaAccent
import com.agendaqr.core.ui.theme.XauxaAccents
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.core.ui.theme.XauxaMetrics
import com.agendaqr.core.ui.theme.XauxaMotion
import com.agendaqr.core.ui.theme.XauxaSpacing
import com.agendaqr.core.ui.theme.XauxaType
import com.agendaqr.core.ui.theme.asTextOn

/**
 * T3/V1.1 (ADR-0005, spec §12): piezas del lenguaje Metro dentro de Xauxa.
 * Invariantes conservados: radio 0, sin sombras, full-bleed, foco visible,
 * targets ≥ [XauxaMetrics.ControlMinSize]; la separación es por espacio y
 * bloques de color (sin bordes de reposo).
 */

/**
 * Título de página (M6): display ligero — Archivo peso Light (300),
 * [XauxaType.DisplayPage] (≥ 40 sp). Capitalización de oración (la cadena
 * se respeta tal cual; ningún componente Xauxa la transforma).
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
        fontSize = XauxaType.DisplayPage,
        fontFamily = XauxaType.FamilyDisplay,
        fontWeight = XauxaType.WeightDisplayPage,
        color = color,
    )
}

/**
 * Encabezado de sección (M6): pequeño (14–16 sp) en color de acento —
 * acento de sistema por defecto, acento del contexto en S06 (M4).
 *
 * Fase 1 (auditoría a11y): el acento de contexto se usa como TEXTO solo si
 * cumple 4.5:1 ([asTextOn] cae a `brandText` si no); la identidad del
 * contexto la marca el subrayado de 2 dp en el color crudo del acento.
 */
@Composable
fun XauxaSectionHeader(
    text: String,
    modifier: Modifier = Modifier,
    accent: Color = XauxaColor.BrandText,
) {
    val textColor = accent.asTextOn(XauxaColor.Background, XauxaColor.BrandText)
    Column(modifier = modifier) {
        androidx.compose.material3.Text(
            text = text,
            modifier = Modifier.semantics { heading() },
            fontSize = XauxaType.SectionHeader,
            fontFamily = XauxaType.FamilyUi,
            color = textColor,
        )
        // Marcador de 2 dp con el acento crudo: identidad sin sacrificar
        // la lectura del texto.
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(XauxaMetrics.Focus)
                .background(accent),
        )
    }
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
 *
 * Accesibilidad (M14): rol de botón, etiqueta visible textual, estado y
 * orden de foco lógico por posición.
 */
@Composable
fun XauxaMetroTile(
    label: String,
    accent: XauxaAccent,
    onClick: (() -> Unit)?,
    modifier: Modifier = Modifier,
    size: XauxaTileSize = XauxaTileSize.SMALL,
    icon: ImageVector? = null,
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
    val height = when (size) {
        XauxaTileSize.SMALL -> XauxaMetrics.TileUnit
        XauxaTileSize.MEDIUM -> XauxaMetrics.TileWideHeight
        XauxaTileSize.WIDE -> XauxaMetrics.TileWideHeight
    }
    Box(
        modifier = modifier
            .height(height)
            .fillMaxWidth()
            .graphicsLayer { scaleX = scale; scaleY = scale }
            .background(accent.background, RectangleShape)
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
            .xauxaFocusRing(interaction),
    ) {
        // M3: icono centrado (omitido en el tile ancho/que lleva contenido).
        if (icon != null && size != XauxaTileSize.WIDE) {
            XauxaIcon(
                imageVector = icon,
                // La etiqueta visible del tile es el nombre accesible; el
                // glifo es decorativo (§11: excluido para no duplicar).
                contentDescription = null,
                modifier = Modifier.align(Alignment.Center),
                size = XauxaMetrics.IconSizeTile,
                tint = accent.onAccent,
            )
        }
        if (content != null && size == XauxaTileSize.WIDE) {
            Box(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(XauxaSpacing.Md),
            ) { content() }
        }
        androidx.compose.material3.Text(
            text = label,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(XauxaSpacing.Sm),
            fontSize = XauxaType.Label,
            fontFamily = XauxaType.FamilyUi,
            color = accent.onAccent,
        )
    }
}

/** Un tile de la rejilla Metro: etiqueta, acento, acción y tamaño. */
data class XauxaTileItem(
    val label: String,
    val accent: XauxaAccent = XauxaAccents.System,
    val size: XauxaTileSize = XauxaTileSize.SMALL,
    val icon: ImageVector? = null,
    val onClick: (() -> Unit)? = null,
    val content: (@Composable () -> Unit)? = null,
)

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
        val maxUnits = ((maxWidth + XauxaSpacing.TileGap) / unitWithGap)
            .toInt().coerceIn(1, GridColumns)
        val unit = (maxWidth - XauxaSpacing.TileGap * (maxUnits - 1)) / maxUnits
        Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.TileGap)) {
            var row = mutableListOf<Pair<Int, XauxaTileItem>>()
            var rowUnits = 0
            val rows = mutableListOf<List<Pair<Int, XauxaTileItem>>>()
            items.forEachIndexed { index, item ->
                val span = when (item.size) {
                    XauxaTileSize.WIDE -> maxUnits
                    XauxaTileSize.MEDIUM -> 2.coerceAtMost(maxUnits)
                    XauxaTileSize.SMALL -> 1
                }
                if (rowUnits + span > maxUnits) {
                    rows += row.toList()
                    row = mutableListOf()
                    rowUnits = 0
                }
                row += index to item
                rowUnits += span
            }
            if (row.isNotEmpty()) rows += row.toList()
            rows.forEach { rowItems ->
                Row(horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.TileGap)) {
                    rowItems.forEach { (index, item) ->
                        val span = when (item.size) {
                            XauxaTileSize.WIDE -> maxUnits
                            XauxaTileSize.MEDIUM -> 2.coerceAtMost(maxUnits)
                            XauxaTileSize.SMALL -> 1
                        }
                        val tileWidth = unit * span + XauxaSpacing.TileGap * (span - 1)
                        val delayMs = if (reducedMotion) {
                            0
                        } else {
                            (index * StaggerStepMs).coerceAtMost(XauxaMotion.DurationMediumMs)
                        }
                        val alpha by animateFloatAsState(
                            targetValue = FullScale,
                            animationSpec = tween(
                                durationMillis = if (reducedMotion) 0 else XauxaMotion.DurationShortMs,
                                delayMillis = delayMs,
                                easing = XauxaMotion.Easings.Standard,
                            ),
                            label = "xauxa_tile_appear",
                        )
                        Box(
                            modifier = Modifier
                                .width(tileWidth)
                                .graphicsLayer { this.alpha = alpha },
                        ) {
                            XauxaMetroTile(
                                label = item.label,
                                accent = item.accent,
                                onClick = item.onClick,
                                modifier = Modifier.fillMaxWidth(),
                                size = item.size,
                                icon = item.icon,
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

/**
 * Pivot de secciones (M7, S06): encabezados de sección; la sección activa
 * se muestra en el acento (del contexto o de sistema como defecto del
 * caller). La conmutación es local: NO crea rutas ni navegación nueva
 * (§3: los estados técnicos no crean navegación artificial).
 *
 * Cada pestaña declara rol de pestaña con estado `selected` y etiqueta de
 * texto visible (§11/M14).
 */
@Composable
fun XauxaPivot(
    sections: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    accent: Color = XauxaColor.BrandText,
    content: @Composable (Int) -> Unit,
) {
    require(sections.isNotEmpty()) { "XauxaPivot necesita al menos una sección" }
    require(selectedIndex in sections.indices) { "XauxaPivot: índice fuera de rango" }
    Column(modifier.fillMaxWidth()) {
        Row(horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Lg)) {
            // Fase 1: el acento solo es texto si es legible (>= 4.5:1); la
            // sección activa lleva además un marcador de 2 dp con el color
            // crudo del acento (identidad de contexto, M4).
            val selectedTextColor = accent.asTextOn(XauxaColor.Background, XauxaColor.BrandText)
            sections.forEachIndexed { index, title ->
                val selected = index == selectedIndex
                val interaction = remember { MutableInteractionSource() }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    androidx.compose.material3.Text(
                        text = title,
                        modifier = Modifier
                            .defaultMinSize(minHeight = XauxaMetrics.ControlMinSize)
                            .clickable(
                                interactionSource = interaction,
                                indication = null,
                                role = Role.Tab,
                                onClick = { onSelect(index) },
                            )
                            .focusable(interactionSource = interaction)
                            .xauxaFocusRing(interaction)
                            .semantics { this.selected = selected }
                            .padding(vertical = XauxaSpacing.Sm),
                        fontSize = XauxaType.SectionHeader,
                        fontFamily = XauxaType.FamilyUi,
                        color = if (selected) selectedTextColor else XauxaColor.TextSecondary,
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(XauxaMetrics.Focus)
                            .background(if (selected) accent else XauxaColor.Background),
                    )
                }
            }
        }
        content(selectedIndex)
    }
}

private const val FullScale = 1f
private const val TiltScale = 0.96f
private const val StaggerStepMs = 40
private const val GridColumns = 4

