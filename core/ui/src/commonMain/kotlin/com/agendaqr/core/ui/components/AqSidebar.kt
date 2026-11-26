package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqLayout
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqStateAlpha
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.core.ui.theme.AqSquircles

/** Ítem de navegación de [AqSidebar] (etiqueta + icono opcional + insignia). */
data class AqSidebarItem(
    val label: String,
    val icon: ImageVector? = null,
    /** Texto de la insignia (p. ej. contador); `null` = sin insignia. */
    val badge: String? = null,
)

/**
 * Barra lateral sobre la especificación Moon v1 (Figma «Sidebar» /
 * `moon-v1-tokens-y-componentes.json`).
 *
 * - Dos anchos: expandida 280 dp y colapsada (rail) 64 dp; radio `md` (12)
 *   sobre superficie [AqColor.Surface].
 * - Padding interno según variante del Figma: 8 dp expandida / 4 dp rail
 *   (variantes «Example=False»); se puede ajustar con [contentPadding].
 * - Hueco vertical de sección 16 dp ([AqSize.X2s]).
 * - Cada ítem es una fila de alto mínimo 40 dp (`sizes.md`): icono de 24 dp
 *   + etiqueta `body` 14/24; en el rail solo se muestra el icono centrado.
 *   El seleccionado usa la superficie de selección del tema
 *   ([AqColor.SelectionSurface]) y `selected` semántico.
 * - Deshabilitado por [enabled] global o por ítem no definido; el llamador
 *   compone [header]/[footer] (logo, usuario, acciones).
 */
@Composable
fun AqSidebar(
    items: List<AqSidebarItem>,
    selectedIndex: Int,
    onItemSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    expanded: Boolean = true,
    enabled: Boolean = true,
    contentPadding: Dp? = null,
    header: (@Composable () -> Unit)? = null,
    footer: (@Composable () -> Unit)? = null,
) {
    val width = if (expanded) AqLayout.SidebarWidthExpanded else AqLayout.SidebarWidthCollapsed
    val padding = contentPadding ?: if (expanded) {
        AqSize.X4s
    } else {
        AqSize.X5s
    }
    Column(
        modifier = modifier
            .width(width)
            .fillMaxHeight()
            .clip(AqSquircles.SurfaceMd)
            .background(AqColor.Surface, AqSquircles.SurfaceMd)
            .padding(padding),
        verticalArrangement = Arrangement.spacedBy(AqSize.X2s),
    ) {
        header?.invoke()
        items.forEachIndexed { index, item ->
            SidebarItemRow(
                item = item,
                selected = index == selectedIndex,
                expanded = expanded,
                enabled = enabled,
                onClick = { onItemSelected(index) },
            )
        }
        Box(modifier = Modifier.weight(1f))
        footer?.invoke()
    }
}

@Composable
private fun SidebarItemRow(
    item: AqSidebarItem,
    selected: Boolean,
    expanded: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val shape = AqSquircles.InteractiveSm
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape)
            .background(
                if (selected) AqColor.SelectionSurface else AqColor.Transparent,
                shape,
            )
            .clickable(
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            )
            .semantics {
                contentDescription = item.label
                this.selected = selected
            }
            .defaultMinSize(minHeight = AqSize.Md)
            .alpha(if (enabled) 1f else AqStateAlpha.Disabled)
            .padding(horizontal = if (expanded) AqSize.X3s else AqLayout.Zero),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (expanded) {
            Arrangement.spacedBy(AqSize.X3s)
        } else {
            Arrangement.Center
        },
    ) {
        item.icon?.let { glyph ->
            AqIcon(
                imageVector = glyph,
                contentDescription = null,
                size = AqMetrics.IconSize,
                tint = if (selected) {
                    AqColor.EmphasisIcon
                } else {
                    AqColor.TextSecondary
                },
            )
        }
        if (expanded) {
            AqText(
                text = item.label,
                size = AqType.BodySm,
                color = when {
                    !enabled -> AqColor.OnDisabled
                    selected -> AqColor.EmphasisText
                    else -> AqColor.TextPrimary
                },
                maxLines = 1,
                modifier = Modifier.weight(1f),
            )
            item.badge?.let { badge ->
                AqText(
                    text = badge,
                    size = AqType.BodyXs,
                    color = AqColor.TextTertiary,
                    maxLines = 1,
                )
            }
        }
    }
    if (!enabled) {
        // La opacidad del estado deshabilitado se aplica en la fila completa.
    }
}
