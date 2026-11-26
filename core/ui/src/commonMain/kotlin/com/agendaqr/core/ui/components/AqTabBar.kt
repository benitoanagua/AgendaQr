package com.agendaqr.core.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqLayout
import com.agendaqr.core.ui.theme.AqMotion
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqSquircles
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.core.ui.theme.AqWeight

/**
 * Barra de pestañas sobre [MoonTabBar] variante `indicator`
 * (`moon_design` 1.1.0, `widgets/tab_bar` + `tab_bar_theme.dart` /
 * `tab_bar_sizes.dart`).
 *
 * - Tamaños sm/md: alto 32/40, padding horizontal de pestaña 12/16,
 *   hueco icono-etiqueta 4/8, icono `xs` (24), indicador `x6s` (2) en
 *   `piccolo`, radio `interactiveXs`/`interactiveSm`.
 * - Texto `heading.textDefault` (14 semibold): `textPrimary` en reposo y
 *   `piccolo` en la pestaña seleccionada, con transición de color de
 *   200 ms `easeInOutCubic` (`defaultTransitionDuration`).
 * - El indicador sube de ancho con la pestaña activa (misma transición).
 * - Pestañas repartidas en fila con scroll horizontal si desbordan; el
 *   objetivo táctil ≥ 48 dp se conserva sin deformar el alto visual
 *   ([AqMetrics.ControlMinSize], ADR-0039 §4.4).
 */
@Composable
fun AqTabBar(
    tabs: List<AqTab>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    size: AqTabSize = AqTabSize.Md,
    /** `isExpanded` de Moon: las pestañas reparten el ancho a partes
     *  iguales (`Expanded` por pestaña); sin él cada una abraza su
     *  contenido y la fila puede desplazar. */
    isExpanded: Boolean = false,
) {
    require(tabs.isNotEmpty()) { "AqTabBar necesita al menos una pestaña" }
    require(selectedIndex in tabs.indices) { "AqTabBar: índice fuera de rango" }
    if (isExpanded) {
        Row(
            modifier = modifier
                .fillMaxWidth()
                .selectableGroup(),
            horizontalArrangement = Arrangement.spacedBy(AqLayout.TabBarGap),
        ) {
            tabs.forEachIndexed { index, tab ->
                AqTabContent(
                    tab = tab,
                    selected = index == selectedIndex,
                    enabled = enabled && !tab.disabled,
                    onClick = { if (enabled && !tab.disabled) onSelect(index) },
                    size = size,
                    showIndicator = true,
                    modifier = Modifier.weight(1f),
                )
            }
        }
        return
    }
    // `weight` no es válido dentro de `horizontalScroll` (ancho infinito):
    // cada pestaña recibe como mínimo su parte equitativa del ancho
    // disponible y crece según su contenido; si no caben, la fila desplaza.
    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val share = (maxWidth - AqSize.X4s * 2 -
            AqLayout.TabBarGap * (tabs.size - 1)) / tabs.size
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .selectableGroup(),
            horizontalArrangement = Arrangement.spacedBy(AqLayout.TabBarGap),
        ) {
            tabs.forEachIndexed { index, tab ->
                AqTabContent(
                    tab = tab,
                    selected = index == selectedIndex,
                    enabled = enabled && !tab.disabled,
                    onClick = { if (enabled && !tab.disabled) onSelect(index) },
                    size = size,
                    showIndicator = true,
                    modifier = Modifier.widthIn(min = share),
                )
            }
        }
    }
}

/** Barra de pestañas tipo píldora ([MoonTabBar.pill] de Moon Design). La
 * pestaña activa se muestra sobre una píldora `goku`→[AqColor.Background]
 * que se funde durante la transición; el texto permanece `textPrimary`
 * en ambos estados (`selectedPillTextColor` = `textColor` en Moon). */
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
    // Pista (`gohan`→Surface) tras las píldoras: sin ella la píldora
    // seleccionada (Background) es idéntica al fondo de la pantalla y el
    // estado activo era invisible.
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(AqColor.Surface, AqSquircles.InteractiveMd)
            .padding(AqLayout.SegmentedControlPadding)
            .selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(AqLayout.TabBarGap),
    ) {
        tabs.forEachIndexed { index, tab ->
            AqTabContent(
                tab = tab,
                selected = index == selectedIndex,
                enabled = enabled && !tab.disabled,
                onClick = { if (enabled && !tab.disabled) onSelect(index) },
                size = size,
                pill = true,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

/** Contenido de una pestaña: ícono opcional (`xs` = 24 dp) + etiqueta
 *  `heading.textDefault` + conteo opcional, centrados en una fila.
 *  El alto visual es el del tamaño Moon; el objetivo táctil se amplía a
 *  48 dp sin deformarlo. */
@Composable
private fun AqTabContent(
    tab: AqTab,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    size: AqTabSize,
    modifier: Modifier = Modifier,
    showIndicator: Boolean = false,
    pill: Boolean = false,
) {
    // Moon `tab_bar`: indicador → textPrimary→piccolo; píldora → fondo
    // transparente→goku y texto siempre textPrimary.
    val textColor by animateColorAsState(
        targetValue = when {
            // Señal redundante: la pestaña activa es TextPrimary y las
            // demás TextSecondary (no depende solo del fondo de la píldora).
            pill && selected && enabled -> AqColor.TextPrimary
            pill -> AqColor.TextSecondary
            selected && enabled -> AqColor.Focus
            else -> AqColor.TextPrimary
        },
        animationSpec = tween(
            durationMillis = AqMotion.DurationMediumMs,
            easing = AqMotion.Easings.InOutCubic,
        ),
        label = "aqTabText${tab.label}",
    )
    val pillColor by animateColorAsState(
        targetValue = if (pill && selected && enabled) AqColor.Background else AqColor.Transparent,
        animationSpec = tween(
            durationMillis = AqMotion.DurationMediumMs,
            easing = AqMotion.Easings.InOutCubic,
        ),
        label = "aqPillTabBg${tab.label}",
    )
    val shape = if (size == AqTabSize.Sm) AqSquircles.InteractiveXs else AqSquircles.InteractiveSm
    Box(
        modifier = modifier
            // Moon `tab_bar.dart`: el alto de la barra es el del tamaño
            // (32/40) y la pestaña entera es el objetivo táctil.
            .height(size.height)
            .clip(shape)
            .semantics(mergeDescendants = true) {
                this.selected = selected
                contentDescription = tab.label
            }
            .clickable(
                enabled = enabled,
                role = Role.Tab,
                onClick = onClick,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(size.height)
                .background(pillColor, shape),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    // Píldoras repartidas a partes iguales: padding compacto
                    // para que "Actividades"/"Comprobantes" no se trunquen.
                    .padding(horizontal = if (pill) AqSize.X4s else size.padding),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(
                    size.gap,
                    Alignment.CenterHorizontally,
                ),
            ) {
                tab.icon?.let {
                    AqIcon(
                        imageVector = it,
                        contentDescription = null,
                        size = AqSize.Xs,
                        tint = textColor,
                    )
                }
                AqText(
                    text = tab.label,
                    size = AqType.HeadingSm,
                    fontWeight = AqWeight.SemiBold,
                    color = textColor,
                    maxLines = 1,
                )
                tab.count?.let {
                    Box(
                        modifier = Modifier
                            .defaultMinSize(minWidth = AqSize.X2s, minHeight = AqSize.X2s)
                            .background(
                                // Par emphasisFill/emphasisOnFill: piccolo con
                                // texto blanco en ambos temas. `Focus` aclara
                                // a darkPiccoloText en oscuro y dejaba el
                                // contador blanco sobre lavanda (bajo contraste).
                                if (selected && enabled) AqColor.EmphasisFill else AqColor.SurfaceContainerHighest,
                                AqSquircles.SurfaceSm,
                            )
                            .padding(horizontal = AqSize.X5s),
                        contentAlignment = Alignment.Center,
                    ) {
                        AqText(
                            text = it.toString(),
                            size = AqType.CaptionSmall,
                            color = if (selected && enabled) AqColor.OnEmphasisFill else AqColor.TextSecondary,
                            maxLines = 1,
                        )
                    }
                }
            }
            if (showIndicator) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .height(AqSize.X6s)
                        .background(if (selected && enabled) AqColor.Focus else AqColor.Transparent),
                )
            }
        }
    }
}

/** Tamaños de [AqTabBar] y [AqPillTab] (`tab_bar_sizes.dart` de Moon). */
enum class AqTabSize(
    /** Alto de la pestaña (`sizes.sm`/`sizes.md`). */
    val height: Dp,
    /** Padding horizontal de la pestaña (`tabPadding`). */
    val padding: Dp,
    /** Hueco icono-etiqueta (`tabGap`). */
    val gap: Dp,
) {
    Sm(AqSize.Sm, AqSize.X3s, AqSize.X5s),
    Md(AqSize.Md, AqSize.X2s, AqSize.X4s),
}

/**
 * Navegación primaria inferior de la aplicación. No reutiliza la píldora de
 * selección de Moon: es un patrón de chrome de producto, construido con
 * roles semánticos, targets accesibles y separación estructural.
 */
@Composable
fun AqBottomNavigation(
    destinations: List<AqNavDestination>,
    selectedId: String?,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    require(destinations.isNotEmpty()) { "AqBottomNavigation necesita destinos" }
    require(destinations.map { it.id }.distinct().size == destinations.size) {
        "AqBottomNavigation requiere ids únicos"
    }
    val selectedIndex = destinations.indexOfFirst { it.id == selectedId }
        .takeIf { it >= 0 } ?: 0
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(AqColor.Surface),
    ) {
        AqHairline()
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .selectableGroup()
                .then(
                    if (destinations.size > 5) Modifier.horizontalScroll(rememberScrollState())
                    else Modifier,
                )
                .padding(horizontal = AqSize.X4s, vertical = AqSize.X4s),
            horizontalArrangement = Arrangement.spacedBy(AqSize.X5s),
        ) {
            destinations.forEachIndexed { index, destination ->
                val selected = index == selectedIndex
                Column(
                    modifier = Modifier
                        .then(if (destinations.size <= 5) Modifier.weight(1f) else Modifier.width(AqMetrics.NavItemWidth))
                        .heightIn(min = AqMetrics.ControlMinSize)
                        .clip(AqSquircles.InteractiveSm)
                        .semantics(mergeDescendants = true) {
                            this.selected = selected
                            contentDescription = destination.label
                        }
                        .clickable(role = Role.Tab) { onSelect(destination.id) }
                        .padding(horizontal = AqSize.X5s, vertical = AqSize.X4s),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(AqSize.X5s),
                ) {
                    // Cápsula de selección DETRÁS del icono (patrón
                    // convencional Android/iOS): sin elemento suelto sobre
                    // el icono y sin alto reservado cuando no está activo.
                    Box(
                        modifier = Modifier
                            .width(AqMetrics.NavPillWidth)
                            .height(AqMetrics.NavPillHeight)
                            .clip(AqSquircles.InteractiveMd)
                            .background(if (selected) AqColor.Jiren else AqColor.Transparent),
                        contentAlignment = Alignment.Center,
                    ) {
                        AqIcon(
                            imageVector = destination.icon,
                            contentDescription = null,
                            size = AqMetrics.IconSize,
                            tint = if (selected) AqColor.EmphasisIcon else AqColor.TextSecondary,
                        )
                    }
                    AqText(
                        text = destination.label,
                        size = AqType.BodyXs,
                        fontWeight = if (selected) AqWeight.SemiBold else AqWeight.Medium,
                        color = if (selected) AqColor.EmphasisText else AqColor.TextSecondary,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}
