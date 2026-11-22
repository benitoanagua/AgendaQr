package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqCorner
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqSquircles
import com.agendaqr.core.ui.theme.AqTextStyles

/**
 * Barra de pestañas sobre [MoonTabBar] (variante indicator). La pestaña
 * activa muestra un indicador inferior. Cada pestaña conserva el objetivo
 * táctil de 48 dp (D-02). Usa [AqTabs.AqTab] como modelo de pestaña.
 */
@Composable
fun AqTabBar(
    tabs: List<AqTab>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    require(tabs.isNotEmpty()) { "AqTabBar necesita al menos una pestaña" }
    require(selectedIndex in tabs.indices) { "AqTabBar: índice fuera de rango" }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .selectableGroup()
            .padding(AqSize.X5s),
        horizontalArrangement = Arrangement.spacedBy(AqSize.X5s),
    ) {
        tabs.forEachIndexed { index, tab ->
            val selected = index == selectedIndex
            Box(
                modifier = Modifier
                    .weight(1f)
                    .selectable(
                        selected = selected,
                        role = Role.Tab,
                        enabled = enabled,
                        onClick = { if (enabled) onSelect(index) },
                    )
                    .defaultMinSize(minHeight = AqMetrics.ControlMinSize),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    modifier = Modifier.size(AqMetrics.IconSize),
                    verticalArrangement = Arrangement.spacedBy(AqSize.X5s),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    tab.icon?.let {
                        val selectedAndEnabled = selected && enabled
                        AqIcon(
                            imageVector = it,
                            contentDescription = null,
                            size = AqMetrics.IconSizeCompact,
                            tint = if (selectedAndEnabled) AqColor.EmphasisFill else AqColor.TextSecondary,
                        )
                    }
                    Text(
                        text = tab.label,
                        style = AqTextStyles.Label,
                        color = if (selected && enabled) AqColor.EmphasisFill else AqColor.TextSecondary,
                        maxLines = 1,
                    )
                    tab.count?.let {
                        Box(
                            modifier = Modifier
                                .background(
                                    if (selected && enabled) AqColor.EmphasisFill else AqColor.SurfaceContainerHighest,
                                    AqSquircles.SurfaceSm,
                                )
                                .padding(horizontal = AqSize.Xs),
                        ) {
                            Text(
                                text = it.toString(),
                                style = AqTextStyles.Tag,
                                color = if (selected && enabled) AqColor.OnEmphasisFill else AqColor.TextSecondary,
                            )
                        }
                    }
                }
            }
        }
    }
}

/** Barra de pestañas tipo píldora ([MoonTabBar.pill] de Moon Design). La
 * pestaña activa se muestra sobre una píldora rellena. Cada pestaña conserva
 * el objetivo táctil de 48 dp (D-02).
 */
@Composable
fun AqPillTab(
    tabs: List<AqTab>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    require(tabs.isNotEmpty()) { "AqPillTab necesita al menos una pestaña" }
    require(selectedIndex in tabs.indices) { "AqPillTab: índice fuera de rango" }
    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(AqColor.SurfaceSecondary, AqSquircles.InteractiveSm)
            .padding(AqSize.X5s),
    ) {
        Row(
            modifier = Modifier.selectableGroup(),
            horizontalArrangement = Arrangement.spacedBy(AqSize.X5s),
        ) {
            tabs.forEachIndexed { index, tab ->
                val selected = index == selectedIndex
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .then(
                            if (selected && enabled) {
                                Modifier.background(AqColor.SelectionSurface, AqSquircles.InteractiveSm)
                            } else {
                                Modifier
                            },
                        )
                        .selectable(
                            selected = selected,
                            role = Role.Tab,
                            enabled = enabled,
                            onClick = { if (enabled) onSelect(index) },
                        )
                        .defaultMinSize(minHeight = AqMetrics.ControlMinSize),
                    contentAlignment = Alignment.Center,
                ) {
                    Column(
                        modifier = Modifier.size(AqMetrics.IconSize),
                        verticalArrangement = Arrangement.spacedBy(AqSize.X5s),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        tab.icon?.let {
                            val selectedAndEnabled = selected && enabled
                            AqIcon(
                                imageVector = it,
                                contentDescription = null,
                                size = AqMetrics.IconSizeCompact,
                                tint = if (selectedAndEnabled) AqColor.EmphasisFill else AqColor.TextSecondary,
                            )
                        }
                        Text(
                            text = tab.label,
                            style = AqTextStyles.Label,
                            color = if (selected && enabled) AqColor.EmphasisFill else AqColor.TextSecondary,
                            maxLines = 1,
                        )
                        tab.count?.let {
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (selected && enabled) AqColor.EmphasisFill else AqColor.SurfaceContainerHighest,
                                        AqSquircles.SurfaceSm,
                                    )
                                    .padding(horizontal = AqSize.Xs),
                            ) {
                                Text(
                                    text = it.toString(),
                                    style = AqTextStyles.Tag,
                                    color = if (selected && enabled) AqColor.OnEmphasisFill else AqColor.TextSecondary,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
