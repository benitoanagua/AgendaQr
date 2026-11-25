package com.agendaqr.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqLayout
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqMotion
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.core.ui.theme.AqWeight
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween

/**
 * Barra de migas sobre [MoonBreadcrumb] (`moon_design` 1.1.0,
 * `widgets/breadcrumb` + `breadcrumb_theme.dart`). Navegación jerárquica
 * con desplazamiento horizontal, separador `arrows_right_24` y la página
 * actual como último ítem.
 *
 * - Separador: chevron derecho de 24 dp ([AqIcons.ChevronRight]) en
 *   `iconSecondary`→[AqColor.TextSecondary]; hueco `x4s` a cada lado
 *   (`properties.gap`).
 * - Ítems: `body.textDefault` (14 sp regular); color `textSecondary`, el
 *   actual `textPrimary`; hover/pressed anima al color del hover
 *   (`hoverEffectColor = textPrimary`, 200 ms `easeInOutCubic`).
 * - [visibleItemCount] (defecto 3, como Moon): con más ítems muestra el
 *   primero, los últimos y un «…» que despliega la ruta completa al pulsar
 *   (`showMoreWidget` de Moon; el copy «…» es de Moon).
 * - [onItemClick]: opcional; se invoca al pulsar un migas (excepto la
 *   actual y el «…»).
 * - Área táctil ≥ 48 dp por ítem sin cambiar el aspecto (D-02).
 * - Todo el copy llega del llamador.
 */
@Composable
fun AqBreadcrumb(
    items: List<String>,
    onItemClick: ((Int) -> Unit)? = null,
    modifier: Modifier = Modifier,
    visibleItemCount: Int = 3,
) {
    require(items.isNotEmpty()) { "AqBreadcrumb necesita al menos un ítem" }
    require(visibleItemCount > 0) { "AqBreadcrumb.visibleItemCount debe ser > 0" }
    var showFullPath by remember { mutableStateOf(false) }
    val resolvedCount = if (showFullPath) items.size else visibleItemCount
    // `_getVisibleItems` de Moon: primero + los últimos (count-1).
    val collapsed = items.size > resolvedCount && resolvedCount > 1
    // `_getVisibleItems` de Moon: todos, o primero + últimos (count-1);
    // con count == 1 solo el primero.
    val visible: List<Pair<Int, String>> = when {
        !collapsed && resolvedCount >= items.size -> items.mapIndexed { i, s -> i to s }
        resolvedCount == 1 -> listOf(0 to items[0])
        else -> listOf(0 to items[0]) +
            (items.size - (resolvedCount - 1) until items.size).map { it to items[it] }
    }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = AqMetrics.ControlMinSize)
            .horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        visible.forEachIndexed { position, (index, label) ->
            val isCurrent = index == items.lastIndex
            // Separador antes de cada ítem que no es el primero visible.
            if (position > 0 || (collapsed && position == 1)) {
                AqIcon(
                    imageVector = AqIcons.ChevronRight,
                    contentDescription = null,
                    size = AqMetrics.IconSize,
                    tint = AqColor.TextSecondary,
                    modifier = Modifier.padding(horizontal = AqLayout.BreadcrumbGap),
                )
            }
            // «…» de Moon: primer hueco cuando la ruta está colapsada.
            if (collapsed && position == 1) {
                BreadcrumbItem(
                    label = "…",
                    isCurrent = false,
                    onClick = { showFullPath = true },
                )
                return@forEachIndexed
            }
            BreadcrumbItem(
                label = label,
                isCurrent = isCurrent,
                onClick = if (isCurrent) null else onItemClick?.let { cb -> { cb(index) } },
            )
        }
    }
}

@Composable
private fun BreadcrumbItem(
    label: String,
    isCurrent: Boolean,
    onClick: (() -> Unit)?,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed by interactionSource.collectIsPressedAsState()
    val baseColor = if (isCurrent) AqColor.TextPrimary else AqColor.TextSecondary
    // `hoverEffectColor` de Moon: textPrimary en todos los estados.
    val color by animateColorAsState(
        targetValue = if (pressed && onClick != null) AqColor.TextPrimary else baseColor,
        animationSpec = tween(
            durationMillis = AqMotion.DurationMediumMs,
            easing = AqMotion.Easings.InOutCubic,
        ),
        label = "aqBreadcrumbItem",
    )
    Box(
        modifier = Modifier
            .defaultMinSize(
                minWidth = AqMetrics.ControlMinSize,
                minHeight = AqMetrics.ControlMinSize,
            )
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        role = Role.Button,
                        onClickLabel = label,
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick,
                    )
                } else {
                    Modifier
                },
            )
            .semantics {
                this.contentDescription = label
                if (onClick != null) this.role = Role.Button
            },
        contentAlignment = Alignment.Center,
    ) {
        AqText(
            text = label,
            size = AqType.BodySm,
            color = color,
            fontWeight = AqWeight.Regular,
        )
    }
}
