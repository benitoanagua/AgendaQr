package com.agendaqr.core.ui.components

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import com.agendaqr.core.ui.theme.AmColor
import com.agendaqr.core.ui.theme.AmMotion
import com.agendaqr.core.ui.theme.AmSpacing
import com.agendaqr.core.ui.theme.AmTextStyles
import com.agendaqr.core.ui.theme.AmType

/** Etiqueta de pestaña (referencia: etiqueta 10 px; 11 sp por
 * altura de barra M3). */
private val AmTabLabelStyle = androidx.compose.ui.text.TextStyle(
    fontSize = AmType.Caption,
    lineHeight = AmType.LineHeightLabelSmall,
    fontFamily = AmType.FamilyUi,
    letterSpacing = androidx.compose.ui.unit.TextUnit.Unspecified,
)

/**
 * Chrome de navegación (barra superior jerárquica + destinos raíz según
 * convención Android; componentes de la referencia): barra superior
 * `TopAppBar`, navegación raíz `NavigationBar`/`NavigationRail`, botón
 * flotante extendido y la transición estándar entre superficies.
 */

/**
 * Acción de menú "…" (etiqueta + onClick); copy del llamador.
 */
data class AmOverflowAction(
    val label: String,
    val onClick: () -> Unit,
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
 * Barra superior estándar: `TopAppBar` con el título a la izquierda
 * (encabezado semántico), flecha atrás cuando hay a dónde volver — la
 * misma en ambas plataformas — y máx. 2 acciones visibles + menú "…".
 */
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
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
    TopAppBar(
        modifier = modifier,
        title = {
            // El título de la pantalla es un encabezado semántico (la
            // navegación por encabezados de TalkBack/VoiceOver lo usa).
            Text(
                title,
                style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.semantics { heading() },
            )
        },
        navigationIcon = if (onBack != null) {
            {
                IconButton(onClick = onBack) {
                    AmIcon(
                        imageVector = AmIcons.Back,
                        contentDescription = backLabel,
                        tint = AmColor.TextPrimary,
                    )
                }
            }
        } else {
            {}
        },
        actions = {
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
                    // Menú contextual M3 estándar: contenedor y elevación
                    // del tema (superficies explícitas en AmTheme), sin
                    // bordes manuales.
                    DropdownMenu(
                        expanded = overflowOpen,
                        onDismissRequest = { overflowOpen = false },
                    ) {
                        overflowActions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option.label) },
                                onClick = {
                                    overflowOpen = false
                                    option.onClick()
                                },
                            )
                        }
                    }
                }
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
 * Botón flotante extendido de Inicio/Actividades: texto visible y glifo
 * decorativo sobre el bloque de acento.
 */
@Composable
fun AmExtendedFab(
    label: String,
    icon: ImageVector?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ExtendedFloatingActionButton(
        modifier = modifier,
        containerColor = AmColor.Accent,
        contentColor = AmColor.OnAccent,
        text = { Text(label, style = AmTextStyles.ButtonLabel) },
        icon = icon?.let { glyph ->
            {
                AmIcon(
                    imageVector = glyph,
                    contentDescription = null,
                    tint = AmColor.OnAccent,
                )
            }
        } ?: {},
        onClick = onClick,
    )
}

/**
 * Barra de navegación inferior de las superficies raíz: etiqueta + icono
 * en cada destino (nunca solo icono).
 */
@Composable
fun AmNavBar(
    destinations: List<AmNavDestination>,
    selectedId: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationBar(
        modifier = modifier,
        containerColor = AmColor.Surface,
        contentColor = AmColor.TextPrimary,
        tonalElevation = AmSpacing.None,
    ) {
        destinations.forEach { destination ->
            val selected = destination.id == selectedId
            NavigationBarItem(
                selected = selected,
                onClick = { onSelect(destination.id) },
                icon = {
                    AmIcon(
                        imageVector = destination.icon,
                        contentDescription = null,
                        tint = if (selected) AmColor.Accent else AmColor.TextSecondary,
                    )
                },
                label = { Text(destination.label, style = AmTabLabelStyle) },
                alwaysShowLabel = true,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AmColor.Accent,
                    selectedTextColor = AmColor.AccentText,
                    unselectedIconColor = AmColor.TextSecondary,
                    unselectedTextColor = AmColor.TextSecondary,
                    indicatorColor = AmColor.Transparent,
                ),
            )
        }
    }
}

/**
 * Riel de navegación para ancho medium/expanded: mismos destinos y
 * etiquetas que [AmNavBar], en columna lateral.
 */
@Composable
fun AmNavRail(
    destinations: List<AmNavDestination>,
    selectedId: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationRail(modifier = modifier) {
        destinations.forEach { destination ->
            val selected = destination.id == selectedId
            NavigationRailItem(
                selected = selected,
                onClick = { onSelect(destination.id) },
                icon = {
                    AmIcon(
                        imageVector = destination.icon,
                        contentDescription = null,
                        tint = if (selected) AmColor.Accent else AmColor.TextSecondary,
                    )
                },
                label = { Text(destination.label, style = AmTabLabelStyle) },
                alwaysShowLabel = true,
            )
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
