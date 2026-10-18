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
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import com.agendaqr.core.ui.theme.AmColor
import com.agendaqr.core.ui.theme.AmMetrics
import com.agendaqr.core.ui.theme.AmMotion
import com.agendaqr.core.ui.theme.AmSpacing
import com.agendaqr.core.ui.theme.AmType

/** Etiqueta de destino de navegación: 12/20. */
private val AmTabLabelStyle = androidx.compose.ui.text.TextStyle(
    fontSize = AmType.BodySmall,
    lineHeight = AmType.LineHeightBodySmall,
    fontFamily = AmType.FamilyUi,
)

/**
 * Chrome de navegación: barra superior jerárquica + destinos raíz,
 * navegación lateral adaptable, botón
 * flotante extendido y la transición estándar entre superficies.
 */

/**
 * Acción de menú "…" (etiqueta + onClick); copy del llamador.
 */
data class AmOverflowAction(
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
data class AmTopBarAction(
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
fun AmTopBar(
    title: String,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    /** copy accesible de la flecha atrás (copy del llamador). */
    backLabel: String = "",
    actions: List<AmTopBarAction> = emptyList(),
    overflowActions: List<AmOverflowAction> = emptyList(),
    /** copy accesible del botón "…" (requerido con overflowActions). */
    overflowLabel: String = "",
) {
    require(actions.size <= 2) {
        "AmTopBar: máximo 2 acciones visibles; el resto va a overflowActions"
    }
    if (overflowActions.isNotEmpty()) {
        require(overflowLabel.isNotBlank()) {
            "AmTopBar: overflowActions necesita overflowLabel (copy del llamador)"
        }
    }
    var overflowOpen by remember { mutableStateOf(false) }
    Layout(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .heightIn(min = AmMetrics.TopBarHeight)
            .background(AmColor.Surface),
        content = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (onBack != null) {
                    IconButton(onClick = onBack) {
                        AmIcon(
                            imageVector = AmIcons.Back,
                            contentDescription = backLabel,
                            tint = AmColor.TextPrimary,
                        )
                    }
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                actions.forEach { action ->
                    if (action.icon != null) {
                        IconButton(onClick = action.onClick, enabled = action.enabled) {
                            AmIcon(
                                imageVector = action.icon,
                                contentDescription = action.label,
                                tint = if (action.enabled) AmColor.TextPrimary else AmColor.TextTertiary,
                            )
                        }
                    } else {
                        AmTextAction(
                            label = action.label,
                            onClick = action.onClick,
                            enabled = action.enabled,
                        )
                    }
                }
                if (overflowActions.isNotEmpty()) {
                    Box {
                        IconButton(onClick = { overflowOpen = true }) {
                            AmIcon(
                                imageVector = AmIcons.More,
                                contentDescription = overflowLabel,
                                tint = AmColor.TextPrimary,
                            )
                        }
                        DropdownMenu(
                            expanded = overflowOpen,
                            onDismissRequest = { overflowOpen = false },
                        ) {
                            overflowActions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option.label) },
                                    leadingIcon = option.icon?.let { glyph ->
                                        { AmIcon(imageVector = glyph, contentDescription = null, tint = AmColor.TextPrimary) }
                                    },
                                    onClick = {
                                        overflowOpen = false
                                        option.onClick()
                                    },
                                )
                            }
                        }
                    }
                }
            }
            Text(
                text = title,
                style = androidx.compose.ui.text.TextStyle(
                    fontSize = AmType.TitleLarge,
                    lineHeight = AmType.LineHeightTitleLarge,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                    fontFamily = AmType.FamilyUi,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.semantics { heading() },
            )
        },
        measurePolicy = { measurables, constraints ->
            val minBarHeight = minOf(
                AmMetrics.TopBarHeight.roundToPx(),
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
data class AmNavDestination(
    val id: String,
    val label: String,
    val icon: ImageVector,
)

/**
 * Estado del chrome de navegación raíz que la superficie compone cuando
 * está en su lista raíz: destinos, selección y handler. [selectedId]
 * usa el [AmNavDestination.id].
 */
class AmNavChromeState(
    val destinations: List<AmNavDestination>,
    val selectedId: String?,
    val onSelect: (String) -> Unit,
)

/**
 * Acción flotante extendida: conserva la etiqueta y el glifo, con la
 * geometría y los estados de la acción primaria compartida.
 */
@Composable
fun AmExtendedFab(
    label: String,
    icon: ImageVector?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AmButton(
        label = label,
        onClick = onClick,
        modifier = modifier,
        variant = AmButtonVariant.Primary,
        size = AmButtonSize.Large,
        icon = icon,
    )
}

/**
 * Barra de navegación inferior: destinos etiquetados de 48 dp, activos
 * en marca, con fondo de superficie y divisor superior.
 */
@Composable
fun AmNavBar(
    destinations: List<AmNavDestination>,
    selectedId: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(AmColor.Surface),
    ) {
        HorizontalDivider(
            thickness = AmMetrics.Border,
            color = AmColor.Divider,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .height(AmMetrics.NavBarHeight)
                .selectableGroup(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            destinations.forEach { destination ->
                val selected = destination.id == selectedId
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .height(AmMetrics.NavBarHeight)
                        .selectable(
                            selected = selected,
                            role = Role.Tab,
                            onClick = { onSelect(destination.id) },
                        )
                        .padding(vertical = AmSpacing.Xs),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    AmIcon(
                        imageVector = destination.icon,
                        contentDescription = null,
                        size = AmMetrics.ButtonMediumIconSize,
                        tint = if (selected) AmColor.Accent else AmColor.TextSecondary,
                    )
                    Text(
                        destination.label,
                        style = AmTabLabelStyle.copy(
                            fontWeight = if (selected) {
                                androidx.compose.ui.text.font.FontWeight.SemiBold
                            } else {
                                androidx.compose.ui.text.font.FontWeight.Normal
                            },
                        ),
                        color = if (selected) AmColor.AccentText else AmColor.TextSecondary,
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
fun AmNavRail(
    destinations: List<AmNavDestination>,
    selectedId: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .width(AmMetrics.NavRailWidth)
            .fillMaxHeight()
            .background(AmColor.Surface)
            .selectableGroup()
            .padding(top = AmSpacing.Sm),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        destinations.forEach { destination ->
            val selected = destination.id == selectedId
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AmMetrics.NavBarHeight)
                    .selectable(
                        selected = selected,
                        role = Role.Tab,
                        onClick = { onSelect(destination.id) },
                    )
                    .padding(vertical = AmSpacing.Xs),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                AmIcon(
                    imageVector = destination.icon,
                    contentDescription = null,
                    size = AmMetrics.ButtonMediumIconSize,
                    tint = if (selected) AmColor.Accent else AmColor.TextSecondary,
                )
                Text(
                    destination.label,
                    style = AmTabLabelStyle.copy(
                        fontWeight = if (selected) {
                            androidx.compose.ui.text.font.FontWeight.SemiBold
                        } else {
                            androidx.compose.ui.text.font.FontWeight.Normal
                        },
                    ),
                    color = if (selected) AmColor.AccentText else AmColor.TextSecondary,
                    maxLines = 1,
                )
            }
        }
    }
}

/** Fracción del ancho que se desplaza la superficie saliente (1/8). */
internal const val AmSurfaceSlideFraction = 8

fun <S> AnimatedContentTransitionScope<S>.amSurfaceEnter(reverse: Boolean = false): EnterTransition =
    slideInHorizontally(
        animationSpec = tween(AmMotion.DurationShortMs, easing = AmMotion.Easings.Standard),
        initialOffsetX = { fullWidth -> (if (reverse) -1 else 1) * fullWidth / AmSurfaceSlideFraction },
    ) + fadeIn(tween(AmMotion.DurationShortMs, easing = AmMotion.Easings.Standard))

fun <S> AnimatedContentTransitionScope<S>.amSurfaceExit(reverse: Boolean = false): ExitTransition =
    slideOutHorizontally(
        animationSpec = tween(AmMotion.DurationShortMs, easing = AmMotion.Easings.Standard),
        // La superficie que sale se mueve una fracción del ancho, no el
        // 100%: es un desplazamiento pequeño, no un slide completo.
        targetOffsetX = { fullWidth -> (if (reverse) 1 else -1) * fullWidth / AmSurfaceSlideFraction },
    ) + fadeOut(tween(AmMotion.DurationShortMs, easing = AmMotion.Easings.Standard))

/** Variante reduced-motion de la entrada: cambio inmediato (duración 0). */
fun <S> AnimatedContentTransitionScope<S>.amReducedSurfaceEnter(): EnterTransition =
    fadeIn(tween(AmMotion.DurationReducedMs))

/** Variante reduced-motion de la salida: cambio inmediato (duración 0). */
fun <S> AnimatedContentTransitionScope<S>.amReducedSurfaceExit(): ExitTransition =
    fadeOut(tween(AmMotion.DurationReducedMs))

/**
 * Cabecera de Inicio: fondo sólido de marca, texto con su par accesible y
 * campo de búsqueda integrado. El fondo permanece estático para
 * conservar el contraste del texto durante toda la interacción.
 *
 * El título es un encabezado semántico (heading) para TalkBack/VoiceOver.
 * Las acciones de overflow (cuenta, bloquear) viven en un menú "…" con
 * etiquetas siempre visibles.
 */
@Composable
fun AmHeroHeader(
    title: String,
    searchLabel: String,
    searchPlaceholder: String,
    onSearchClick: () -> Unit,
    overflowActions: List<AmOverflowAction>,
    overflowLabel: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .background(AmColor.Accent)
            .padding(horizontal = AmSpacing.ScreenMargin)
            .padding(top = AmSpacing.Sm, bottom = AmSpacing.Md),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            // El texto usa el par accesible del color primario.
            Text(
                title,
                style = androidx.compose.ui.text.TextStyle(
                    fontSize = AmType.TitleLarge,
                    lineHeight = AmType.LineHeightTitleLarge,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                    fontFamily = AmType.FamilyUi,
                    color = AmColor.OnAccent,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier
                    .weight(1f)
                    .semantics { heading() },
            )
            if (overflowActions.isNotEmpty()) {
                Box {
                    var overflowOpen by remember { mutableStateOf(false) }
                    IconButton(onClick = { overflowOpen = true }) {
                        AmIcon(
                            imageVector = AmIcons.More,
                            contentDescription = overflowLabel,
                            tint = AmColor.OnAccent,
                        )
                    }
                    DropdownMenu(
                        expanded = overflowOpen,
                        onDismissRequest = { overflowOpen = false },
                    ) {
                        overflowActions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option.label) },
                                leadingIcon = option.icon?.let { glyph ->
                                    {
                                        AmIcon(
                                            imageVector = glyph,
                                            contentDescription = null,
                                            tint = AmColor.TextPrimary,
                                        )
                                    }
                                },
                                onClick = {
                                    overflowOpen = false
                                    option.onClick()
                                },
                            )
                        }
                    }
                }
            }
        }
        // Campo de búsqueda en píldora superpuesto a la banda (Anexo A).
        AmSearchTrigger(
            label = searchLabel,
            placeholder = searchPlaceholder,
            onClick = onSearchClick,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
