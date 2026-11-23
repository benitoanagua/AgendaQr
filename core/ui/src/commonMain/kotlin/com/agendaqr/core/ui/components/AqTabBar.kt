package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqCorner
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqSquircles
import com.agendaqr.core.ui.theme.AqTextStyles
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.core.ui.theme.AqWeight

/**
 * Barra de pestañas sobre [MoonTabBar] (variante indicator). La pestaña
 * activa muestra un indicador inferior. Cada pestaña conserva el objetivo
 * táctil de 48 dp (D-02). Usa [AqTabs.AqTab] como modelo de pestaña.
 *
 * Geometría Moon (§ TabBar):
 * - Alto: sm 32 / md 40 dp; padding horizontal 12 / 16 dp; hueco
 *   icono-etiqueta 4 / 8 dp (X5s / X4s).
 * - Cada pestaña reparte el ancho del padre con weight(1f) cuando el
 *   llamador las coloca en una fila compartida; el Row usa
 *   horizontalScroll para que ninguna etiqueta quede oculta al desbordar
 *   (S2). Nunca se comprime el texto con un ancho fijo.
 */
@Composable
fun AqTabBar(
    tabs: List<AqTab>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: AqTabSize = AqTabSize.Md,
) {
    require(tabs.isNotEmpty()) { "AqTabBar necesita al menos una pestaña" }
    require(selectedIndex in tabs.indices) { "AqTabBar: índice fuera de rango" }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .selectableGroup()
            .padding(horizontal = size.padding),
        horizontalArrangement = Arrangement.spacedBy(size.gap),
    ) {
        tabs.forEachIndexed { index, tab ->
            AqTabContent(
                tab = tab,
                selected = index == selectedIndex,
                enabled = enabled,
                onClick = { if (enabled) onSelect(index) },
                size = size,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

/** Barra de pestañas tipo píldora ([MoonTabBar.pill] de Moon Design). La
 * pestaña activa se muestra sobre una píldora rellena. Cada pestaña conserva
 * el objetivo táctil de 48 dp (D-02). */
@Composable
fun AqPillTab(
    tabs: List<AqTab>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: AqTabSize = AqTabSize.Md,
) {
    require(tabs.isNotEmpty()) { "AqPillTab necesita al menos una pestaña" }
    require(selectedIndex in tabs.indices) { "AqPillTab: índice fuera de rango" }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(AqColor.SurfaceSecondary, AqSquircles.InteractiveSm)
            .padding(horizontal = size.padding),
    ) {
        Row(
            modifier = Modifier.selectableGroup(),
            horizontalArrangement = Arrangement.spacedBy(size.gap),
        ) {
            tabs.forEachIndexed { index, tab ->
                AqTabContent(
                    tab = tab,
                    selected = index == selectedIndex,
                    enabled = enabled,
                    onClick = { if (enabled) onSelect(index) },
                    size = size,
                    modifier = Modifier.weight(1f)
                        .then(
                            if (selectedIndex == index && enabled) {
                                Modifier.background(AqColor.SelectionSurface, AqSquircles.InteractiveSm)
                            } else {
                                Modifier
                            },
                        ),
                )
            }
        }
    }
}

/** Contenido de una pestaña: ícono opcional (20-24 dp) + etiqueta (una
 *  línea, sin ellipsis a 0). Área táctil ≥ 48 dp. */
@Composable
private fun AqTabContent(
    tab: AqTab,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    size: AqTabSize,
    modifier: Modifier = Modifier,
) {
    val selectedAndEnabled = selected && enabled
    Box(
        modifier = modifier
            .selectable(
                selected = selected,
                role = Role.Tab,
                enabled = enabled,
                onClick = onClick,
            )
            .defaultMinSize(minHeight = AqMetrics.ControlMinSize),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            modifier = Modifier.height(size.height),
            verticalArrangement = Arrangement.spacedBy(size.gap),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            tab.icon?.let {
                AqIcon(
                    imageVector = it,
                    contentDescription = null,
                    size = AqMetrics.IconSizeCompact,
                    tint = if (selectedAndEnabled) AqColor.EmphasisFill else AqColor.TextSecondary,
                )
            }
            AqText(
                text = tab.label,
                size = AqType.Label,
                fontWeight = AqWeight.SemiBold,
                color = if (selectedAndEnabled) AqColor.EmphasisFill else AqColor.TextSecondary,
                maxLines = 1,
            )
            tab.count?.let {
                Box(
                    modifier = Modifier
                        .background(
                            if (selectedAndEnabled) AqColor.EmphasisFill else AqColor.SurfaceContainerHighest,
                            AqSquircles.SurfaceSm,
                        )
                        .padding(horizontal = AqSize.Xs),
                ) {
                    AqText(
                        text = it.toString(),
                        size = AqType.CaptionSmall,
                        color = if (selectedAndEnabled) AqColor.OnEmphasisFill else AqColor.TextSecondary,
                    )
                }
            }
        }
    }
}

/** Tamaños de [AqTabBar] y [AqPillTab] (tabla Moon § TabBar). */
enum class AqTabSize(
    val height: Dp,
    val padding: Dp,
    val gap: Dp,
) {
    Sm(AqSize.Sm, AqSize.X3s, AqSize.X5s),
    Md(AqSize.Md, AqSize.X2s, AqSize.X4s),
}
