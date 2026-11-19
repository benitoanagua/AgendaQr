package com.agendaqr.core.ui.components

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.background
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import com.agendaqr.core.ui.theme.AqWeight
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqMotion
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqType

/** Etiqueta de destino de navegación: 12/20. La familia se aplica desde AqTheme. */
private val AqTabLabelStyle = androidx.compose.ui.text.TextStyle(
    fontSize = AqType.BodySmall,
    lineHeight = AqType.LineHeightBodySmall,
)

/**
 * Chrome de navegación: barra superior jerárquica + destinos raíz,
 * navegación lateral adaptable, botón
 * flotante extendido y la transición estándar entre superficies.
 */

/**
 * Acción de menú "…" (etiqueta + onClick); copy del llamador.
 */
data class AqOverflowAction(
    val label: String,
    val onClick: () -> Unit,
    /** Glifo opcional del menú (decorativo; la etiqueta es la señal). */
    val icon: ImageVector? = null,
)

/**
 * Acción de la barra superior: etiqueta SIEMPRE (es su descripción
 * accesible); [icon] es el glifo aprobado del mapeo o null para pintarla
 * como acción de texto (sin inventar iconos fuera del mapeo).
 */
data class AqTopBarAction(
    val label: String,
    val icon: ImageVector? = null,
    val onClick: () -> Unit,
    val enabled: Boolean = true,
)

/**
 * Barra superior estándar: título centrado de 18 sp (encabezado semántico),
 * flecha atrás cuando hay a dónde
 * volver — la misma en ambas plataformas — y máx. 2 acciones visibles +
 * menú "…". Las acciones conservan objetivos de 48 dp.
 *
 * Edge-to-edge (ADR-0012): el slot topBar de Scaffold NO recibe los
 * insets del sistema — esta barra los aplica por sí misma
 * (`statusBarsPadding`) para que el título nunca quede tras la barra
 * de estado, y el contenido siga fluyendo debajo.
 */
@Composable
fun AqTopBar(
    title: String,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    /** copy accesible de la flecha atrás (copy del llamador). */
    backLabel: String = "",
    actions: List<AqTopBarAction> = emptyList(),
    overflowActions: List<AqOverflowAction> = emptyList(),
    /** copy accesible del botón "…" (requerido con overflowActions). */
    overflowLabel: String = "",
) {
    require(actions.size <= 2) {
        "AqTopBar: máximo 2 acciones visibles; el resto va a overflowActions"
    }
    if (overflowActions.isNotEmpty()) {
        require(overflowLabel.isNotBlank()) {
            "AqTopBar: overflowActions necesita overflowLabel (copy del llamador)"
        }
    }
    val actionSheetController = LocalAqActionSheetController.current
    if (overflowActions.isNotEmpty()) {
        requireNotNull(actionSheetController) {
            "AqTopBar con overflowActions debe estar dentro de AqScreenScaffold"
        }
    }
    Layout(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .heightIn(min = AqMetrics.TopBarHeight)
            .background(AqColor.Surface),
        content = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (onBack != null) {
                    IconButton(onClick = onBack) {
                        AqIcon(
                            imageVector = AqIcons.Back,
                            contentDescription = backLabel,
                            tint = AqColor.TextPrimary,
                        )
                    }
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                actions.forEach { action ->
                    if (action.icon != null) {
                        IconButton(onClick = action.onClick, enabled = action.enabled) {
                            AqIcon(
                                imageVector = action.icon,
                                contentDescription = action.label,
                                tint = if (action.enabled) AqColor.TextPrimary else AqColor.TextTertiary,
                            )
                        }
                    } else {
                        AqTextAction(
                            label = action.label,
                            onClick = action.onClick,
                            enabled = action.enabled,
                        )
                    }
                }
                if (overflowActions.isNotEmpty()) {
                    IconButton(onClick = {
                        actionSheetController?.show(overflowActions, overflowLabel)
                    }) {
                        AqIcon(
                            imageVector = AqIcons.More,
                            contentDescription = overflowLabel,
                            tint = AqColor.TextPrimary,
                        )
                    }
                }
            }
            Text(
                text = title,
                style = androidx.compose.ui.text.TextStyle(
                    fontSize = AqType.TitleLarge,
                    lineHeight = AqType.LineHeightTitleLarge,
                    fontWeight = AqWeight.SemiBold,
                    fontFamily = AqType.FamilyUi,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.semantics { heading() },
            )
        },
        measurePolicy = { measurables, constraints ->
            val minBarHeight = minOf(
                AqMetrics.TopBarHeight.roundToPx(),
                constraints.maxHeight,
            )
            val controlsConstraints = Constraints(
                maxWidth = constraints.maxWidth,
                minHeight = minBarHeight,
                maxHeight = constraints.maxHeight,
            )
            val leading = measurables[0].measure(controlsConstraints)
            val trailing = measurables[1].measure(controlsConstraints)
            val sideWidth = maxOf(leading.width, trailing.width)
            val titleMaxWidth = (constraints.maxWidth - sideWidth * 2).coerceAtLeast(0)
            val title = measurables[2].measure(
                Constraints(
                    maxWidth = titleMaxWidth,
                    maxHeight = constraints.maxHeight,
                ),
            )
            val width = maxOf(
                leading.width + trailing.width,
                title.width + sideWidth * 2,
                constraints.minWidth,
            ).coerceAtMost(constraints.maxWidth)
            val height = maxOf(
                minBarHeight,
                leading.height,
                trailing.height,
                title.height,
            ).coerceIn(constraints.minHeight, constraints.maxHeight)
            layout(width, height) {
                leading.placeRelative(0, (height - leading.height) / 2)
                trailing.placeRelative(width - trailing.width, (height - trailing.height) / 2)
                title.placeRelative((width - title.width) / 2, (height - title.height) / 2)
            }
        },
    )
}

/** Destino raíz de la navegación: etiqueta SIEMPRE visible + icono. */
data class AqNavDestination(
    val id: String,
    val label: String,
    val icon: ImageVector,
)

/**
 * Estado del chrome de navegación raíz que la superficie compone cuando
 * está en su lista raíz: destinos, selección y handler. [selectedId]
 * usa el [AqNavDestination.id].
 */
class AqNavChromeState(
    val destinations: List<AqNavDestination>,
    val selectedId: String?,
    val onSelect: (String) -> Unit,
)

/**
 * Acción flotante extendida: conserva la etiqueta y el glifo, con la
 * geometría y los estados de la acción primaria compartida.
 */
@Composable
fun AqExtendedFab(
    label: String,
    icon: ImageVector?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AqButton(
        label = label,
        onClick = onClick,
        modifier = modifier,
        variant = AqButtonVariant.Primary,
        size = AqButtonSize.Large,
        icon = icon,
    )
}

/**
 * Barra de navegación inferior: destinos etiquetados de 48 dp, activos
 * en marca, con fondo de superficie y divisor superior.
 */
@Composable
fun AqNavBar(
    destinations: List<AqNavDestination>,
    selectedId: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(AqColor.Surface),
    ) {
        HorizontalDivider(
            thickness = AqMetrics.DividerHeight,
            color = AqColor.Divider,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(AqMetrics.NavBarHeight)
                .selectableGroup(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            destinations.forEach { destination ->
                val selected = destination.id == selectedId
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .height(AqMetrics.NavBarHeight)
                        .selectable(
                            selected = selected,
                            role = Role.Tab,
                            onClick = { onSelect(destination.id) },
                        )
                        .padding(vertical = AqSpacing.Xs),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    AqIcon(
                        imageVector = destination.icon,
                        contentDescription = null,
                        size = AqMetrics.ButtonMediumIconSize,
                        tint = if (selected) AqColor.SelectionIcon else AqColor.TextSecondary,
                    )
                    Text(
                        destination.label,
                        style = AqTabLabelStyle.copy(
                            fontWeight = if (selected) {
                                AqWeight.SemiBold
                            } else {
                                AqWeight.Regular
                            },
                        ),
                        fontFamily = AqType.FamilyUi,
                        color = if (selected) AqColor.EmphasisText else AqColor.TextSecondary,
                        maxLines = 1,
                    )
                }
            }
        }
    }
}

/**
 * Riel para anchos amplios: mismos destinos y selección que la barra
 * inferior, con el tratamiento de superficie y tipografía compartido.
 */
@Composable
fun AqNavRail(
    destinations: List<AqNavDestination>,
    selectedId: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .width(AqMetrics.NavRailWidth)
            .fillMaxHeight()
            .background(AqColor.Surface)
            .selectableGroup()
            .padding(top = AqSpacing.Sm),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        destinations.forEach { destination ->
            val selected = destination.id == selectedId
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AqMetrics.NavBarHeight)
                    .selectable(
                        selected = selected,
                        role = Role.Tab,
                        onClick = { onSelect(destination.id) },
                    )
                    .padding(vertical = AqSpacing.Xs),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                AqIcon(
                    imageVector = destination.icon,
                    contentDescription = null,
                    size = AqMetrics.ButtonMediumIconSize,
                    tint = if (selected) AqColor.SelectionIcon else AqColor.TextSecondary,
                )
                Text(
                    destination.label,
                    style = AqTabLabelStyle.copy(
                        fontWeight = if (selected) {
                            AqWeight.SemiBold
                        } else {
                            AqWeight.Regular
                        },
                    ),
                    fontFamily = AqType.FamilyUi,
                    color = if (selected) AqColor.EmphasisText else AqColor.TextSecondary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

fun <S> AnimatedContentTransitionScope<S>.aqSurfaceEnter(reverse: Boolean = false): EnterTransition =
    slideInHorizontally(
        animationSpec = tween(AqMotion.DurationShortMs, easing = AqMotion.Easings.Standard),
        initialOffsetX = { fullWidth -> (if (reverse) -1 else 1) * fullWidth / AqMotion.SurfaceSlideFraction },
    ) + fadeIn(tween(AqMotion.DurationShortMs, easing = AqMotion.Easings.Standard))

fun <S> AnimatedContentTransitionScope<S>.aqSurfaceExit(reverse: Boolean = false): ExitTransition =
    slideOutHorizontally(
        animationSpec = tween(AqMotion.DurationShortMs, easing = AqMotion.Easings.Standard),
        // La superficie que sale se mueve una fracción del ancho, no el
        // 100%: es un desplazamiento pequeño, no un slide completo.
        targetOffsetX = { fullWidth -> (if (reverse) 1 else -1) * fullWidth / AqMotion.SurfaceSlideFraction },
    ) + fadeOut(tween(AqMotion.DurationShortMs, easing = AqMotion.Easings.Standard))

/** Variante reduced-motion de la entrada: cambio inmediato (duración 0). */
fun <S> AnimatedContentTransitionScope<S>.aqReducedSurfaceEnter(): EnterTransition =
    fadeIn(tween(AqMotion.DurationReducedMs))

/** Variante reduced-motion de la salida: cambio inmediato (duración 0). */
fun <S> AnimatedContentTransitionScope<S>.aqReducedSurfaceExit(): ExitTransition =
    fadeOut(tween(AqMotion.DurationReducedMs))
