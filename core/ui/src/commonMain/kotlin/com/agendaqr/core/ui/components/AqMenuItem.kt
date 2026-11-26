package com.agendaqr.core.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqLayout
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqMotion
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqStateAlpha
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.core.ui.theme.AqSquircles

/**
 * Celda de lista sobre [MoonMenuItem] (`moon_design` 1.1.0,
 * `widgets/menu_item` + `menu_item_theme.dart`).
 *
 * - Contenedor: squircle `interactiveSm`, fondo transparente que funde
 *   `heles` (velo translúcido) en hover/focus durante 200 ms
 *   `easeInOutCubic`; padding `x3s` (12 dp) en los cuatro lados; alto
 *   mínimo `sizes.md` (40 dp) y hueco horizontal `x3s` entre
 *   leading/label/trailing (Moon usa el padding como gap).
 * - Label: `body.textDefault` (14 regular) en `textPrimary`; content:
 *   `body.text12` (12 regular) en `textSecondary`, a `x5s` (4 dp) bajo el
 *   label.
 * - [leading]/[trailing]: slots de Moon (avatar, glifo, chevron…).
 * - [selected] (contrato de producto): superficie de selección del tema.
 * - [showDivider]: divisor `beerus` de 1 dp con inset de lista.
 * - Deshabilitado = opacidad 0.6. Moon no impone target mínimo en la
 *   celda; el alto mínimo es `sizes.md` (40 dp) más el padding `x3s`.
 * - Todo el copy llega del llamador.
 */
@Composable
fun AqMenuItem(
    title: String,
    subtitle: String? = null,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    /** Pulsación larga opcional (p. ej. entrar al modo selección). */
    onLongClick: (() -> Unit)? = null,
    /** Etiqueta accesible de la acción de pulsación larga. */
    onLongClickLabel: String? = null,
    selected: Boolean = false,
    enabled: Boolean = true,
    showDivider: Boolean = false,
    /** Líneas máximas del título; Moon deja envolver el `label` (`Text` con
     *  `softWrap`), así que por defecto no hay límite. */
    titleMaxLines: Int = Int.MAX_VALUE,
    /** Etiqueta/metadato secundario opcional (p. ej. estado de una fila). */
    meta: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    val hoverAlpha by animateFloatAsState(
        targetValue = if (hovered && enabled && !selected) 1f else 0f,
        animationSpec = tween(
            durationMillis = AqMotion.DurationMediumMs,
            easing = AqMotion.Easings.InOutCubic,
        ),
        label = "aqMenuItemHover",
    )
    val shape = AqSquircles.InteractiveSm
    // Moon `menu_item.dart` no impone target mínimo: la fila mide
    // `max(minHeight md=40, contenido + padding x3s)`. La superficie
    // interactiva es la propia fila.
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(shape)
                .then(
                    if (selected) {
                        Modifier.background(AqColor.SelectionSurface, shape)
                    } else {
                        Modifier
                    },
                )
                .then(
                    if (onLongClick != null) {
                        @OptIn(ExperimentalFoundationApi::class)
                        Modifier.combinedClickable(
                            role = Role.Button,
                            onClickLabel = title,
                            interactionSource = interactionSource,
                            indication = null,
                            enabled = enabled && onClick != null,
                            onLongClickLabel = onLongClickLabel,
                            onLongClick = onLongClick,
                            onClick = { onClick?.invoke() },
                        )
                    } else {
                        Modifier.clickable(
                            role = Role.Button,
                            onClickLabel = title,
                            interactionSource = interactionSource,
                            indication = null,
                            enabled = enabled && onClick != null,
                            onClick = { onClick?.invoke() },
                        )
                    },
                )
                .menuItemSemantics(
                    desc = title + (subtitle?.takeIf { it.isNotBlank() }?.let { " - $it" } ?: ""),
                    isSelected = selected,
                )
                .alpha(if (enabled) 1f else AqStateAlpha.Disabled),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    // Moon `menu_item.dart`: `BoxConstraints(minHeight: md)`
                    // aplica al Container completo (padding incluido).
                    .defaultMinSize(minHeight = AqSize.Md),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(AqLayout.CellPadding),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    leading?.let {
                        Box(modifier = Modifier.padding(end = AqLayout.MenuItemGap)) { it() }
                    }
                    Column(
                        modifier = Modifier
                            .weight(1f, fill = true)
                            .padding(
                                end = if (trailing != null) AqLayout.MenuItemGap else AqLayout.Zero,
                            ),
                        verticalArrangement = Arrangement.spacedBy(AqSize.X5s),
                    ) {
                        AqText(
                            text = title,
                            size = AqType.BodySm,
                            color = AqColor.TextPrimary,
                            maxLines = titleMaxLines,
                        )
                        subtitle?.takeIf { it.isNotBlank() }?.let {
                            AqText(
                                text = it,
                                size = AqType.BodyXs,
                                color = AqColor.TextSecondary,
                                maxLines = 1,
                            )
                        }
                        meta?.let { slot -> slot() }
                    }
                    trailing?.let {
                        Box { it() }
                    }
                }
                // Velo de hover de Moon (`primaryHoverColor` = heles).
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .alpha(hoverAlpha)
                        .background(AqColor.Disabled, shape),
                )
            }
            if (showDivider) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Spacer(modifier = Modifier.width(AqMetrics.ListDividerInset))
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(AqMetrics.DividerHeight)
                            .background(AqColor.Border),
                    )
                }
            }
        }
}

/** Semántica de una celda de menú: nombre accesible + estado seleccionado. */
private fun Modifier.menuItemSemantics(desc: String, isSelected: Boolean): Modifier =
    semantics {
        contentDescription = desc
        selected = isSelected
    }
