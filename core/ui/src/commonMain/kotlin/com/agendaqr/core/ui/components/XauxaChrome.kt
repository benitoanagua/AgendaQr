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
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
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
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.core.ui.theme.XauxaMetrics
import com.agendaqr.core.ui.theme.XauxaMotion
import com.agendaqr.core.ui.theme.XauxaSpacing
import com.agendaqr.core.ui.theme.XauxaTextStyles

/**
 * Chrome convencional de Xauxa (spec §12 V1.2, ADR-0012): barra superior
 * `TopAppBar`, navegación raíz `NavigationBar`/`NavigationRail` y la
 * transición estándar entre superficies.
 */

/**
 * Acción de menú "…" (etiqueta + onClick); copy del llamador.
 */
data class XauxaOverflowAction(
    val label: String,
    val onClick: () -> Unit,
)

/**
 * Acción de la barra superior: etiqueta SIEMPRE (es su descripción
 * accesible); [icon] es el glifo aprobado del mapeo o null para pintarla
 * como acción de texto (sin inventar iconos fuera del mapeo).
 */
data class XauxaTopBarAction(
    val label: String,
    val icon: ImageVector? = null,
    val onClick: () -> Unit,
    val enabled: Boolean = true,
)

/**
 * Barra superior estándar (§12 V1.2): `TopAppBar` pequeña con el título a
 * la izquierda, flecha atrás cuando hay a dónde volver — la misma en ambas
 * plataformas — y máx. 2 acciones visibles + menú "…".
 */
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun XauxaTopBar(
    title: String,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    /** copy accesible de la flecha atrás (copy del llamador). */
    backLabel: String = "",
    actions: List<XauxaTopBarAction> = emptyList(),
    overflowActions: List<XauxaOverflowAction> = emptyList(),
    /** copy accesible del botón "…" (requerido con overflowActions). */
    overflowLabel: String = "",
) {
    require(actions.size <= 2) {
        "XauxaTopBar: máximo 2 acciones visibles (§12); el resto va a overflowActions"
    }
    if (overflowActions.isNotEmpty()) {
        require(overflowLabel.isNotBlank()) {
            "XauxaTopBar: overflowActions necesita overflowLabel (copy del llamador)"
        }
    }
    var overflowOpen by remember { mutableStateOf(false) }
    TopAppBar(
        modifier = modifier,
        title = {
            // §11: el título de la pantalla es un encabezado semántico (la
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
                    XauxaIcon(
                        imageVector = XauxaIcons.Back,
                        contentDescription = backLabel,
                        tint = XauxaColor.TextPrimary,
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
                        XauxaIcon(
                            imageVector = action.icon,
                            contentDescription = action.label,
                            tint = if (action.enabled) XauxaColor.TextPrimary else XauxaColor.TextTertiary,
                        )
                    }
                } else {
                    XauxaTextAction(
                        label = action.label,
                        onClick = action.onClick,
                        enabled = action.enabled,
                    )
                }
            }
            if (overflowActions.isNotEmpty()) {
                Box {
                    IconButton(onClick = { overflowOpen = true }) {
                        XauxaIcon(
                            imageVector = XauxaIcons.More,
                            contentDescription = overflowLabel,
                            tint = XauxaColor.TextPrimary,
                        )
                    }
                    DropdownMenu(
                        expanded = overflowOpen,
                        onDismissRequest = { overflowOpen = false },
                        // Límite del overlay del menú (excepción E-01 del
                        // contrato §12); la forma hereda MaterialTheme.shapes.
                        containerColor = XauxaColor.Surface,
                        tonalElevation = XauxaSpacing.None,
                        shadowElevation = XauxaSpacing.None,
                        border = androidx.compose.foundation.BorderStroke(
                            width = XauxaMetrics.Border,
                            color = XauxaColor.BorderControl,
                        ),
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
data class XauxaNavDestination(
    val id: String,
    val label: String,
    val icon: ImageVector,
)

/**
 * Estado del chrome de navegación raíz que la superficie compone cuando
 * está en su lista raíz: destinos, selección y handler. [selectedId] usa
 * el [XauxaNavDestination.id].
 */
class XauxaNavChromeState(
    val destinations: List<XauxaNavDestination>,
    val selectedId: String?,
    val onSelect: (String) -> Unit,
)

/**
 * Botón flotante extendido de Inicio/Actividades (§12 V1.2):
 * `ExtendedFloatingActionButton` con texto visible y glifo decorativo.
 */
@Composable
fun XauxaExtendedFab(
    label: String,
    icon: ImageVector?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    androidx.compose.material3.ExtendedFloatingActionButton(
        modifier = modifier,
        text = { Text(label, style = XauxaTextStyles.ButtonLabel) },
        icon = icon?.let { glyph ->
            {
                XauxaIcon(
                    imageVector = glyph,
                    contentDescription = null,
                    tint = XauxaColor.OnBrand,
                )
            }
        } ?: {},
        onClick = onClick,
    )
}

/**
 * Barra de navegación inferior de las superficies raíz (§12 V1.2):
 * `NavigationBar` con etiqueta + icono en cada destino (nunca solo icono).
 */
@Composable
fun XauxaNavBar(
    destinations: List<XauxaNavDestination>,
    selectedId: String,
    onSelect: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    NavigationBar(modifier = modifier, tonalElevation = XauxaSpacing.None) {
        destinations.forEach { destination ->
            val selected = destination.id == selectedId
            NavigationBarItem(
                selected = selected,
                onClick = { onSelect(destination.id) },
                icon = {
                    XauxaIcon(
                        imageVector = destination.icon,
                        contentDescription = null,
                        tint = if (selected) XauxaColor.OnBrand else XauxaColor.TextSecondary,
                    )
                },
                label = { Text(destination.label, style = XauxaTextStyles.Caption) },
                alwaysShowLabel = true,
            )
        }
    }
}

/**
 * Riel de navegación para ancho medium/expanded (§12 V1.2): mismos
 * destinos y etiquetas que [XauxaNavBar], en columna lateral.
 */
@Composable
fun XauxaNavRail(
    destinations: List<XauxaNavDestination>,
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
                    XauxaIcon(
                        imageVector = destination.icon,
                        contentDescription = null,
                        tint = if (selected) XauxaColor.OnBrand else XauxaColor.TextSecondary,
                    )
                },
                label = { Text(destination.label, style = XauxaTextStyles.Caption) },
                alwaysShowLabel = true,
            )
        }
    }
}

/**
 * Transición estándar entre superficies (§12 V1.2): fundido con un
 * desplazamiento horizontal pequeño, corto y con tokens de movimiento;
 * con reduced motion el cambio es inmediato (duración 0, §11).
 */
enum class XauxaSurfaceMotion { Standard, Reduced }

/** Decisión pura y testeable del movimiento de la transición. */
fun xauxaSurfaceMotion(reducedMotion: Boolean): XauxaSurfaceMotion =
    if (reducedMotion) XauxaSurfaceMotion.Reduced else XauxaSurfaceMotion.Standard

/** Fracción del ancho que se desplaza la superficie saliente (1/8). */
internal const val XauxaSurfaceSlideFraction = 8

fun <S> AnimatedContentTransitionScope<S>.xauxaSurfaceEnter(reverse: Boolean = false): EnterTransition =
    slideInHorizontally(
        animationSpec = tween(XauxaMotion.DurationShortMs, easing = XauxaMotion.Easings.Standard),
        initialOffsetX = { fullWidth -> (if (reverse) -1 else 1) * fullWidth / XauxaSurfaceSlideFraction },
    ) + fadeIn(tween(XauxaMotion.DurationShortMs, easing = XauxaMotion.Easings.Standard))

fun <S> AnimatedContentTransitionScope<S>.xauxaSurfaceExit(reverse: Boolean = false): ExitTransition =
    slideOutHorizontally(
        animationSpec = tween(XauxaMotion.DurationShortMs, easing = XauxaMotion.Easings.Standard),
        // La superficie que sale se mueve una fracción del ancho, no el
        // 100%: es un desplazamiento pequeño, no un slide completo.
        targetOffsetX = { fullWidth -> (if (reverse) 1 else -1) * fullWidth / XauxaSurfaceSlideFraction },
    ) + fadeOut(tween(XauxaMotion.DurationShortMs, easing = XauxaMotion.Easings.Standard))

/** Variante reduced-motion de la entrada: cambio inmediato (duración 0). */
fun <S> AnimatedContentTransitionScope<S>.xauxaReducedSurfaceEnter(): EnterTransition =
    fadeIn(tween(XauxaMotion.DurationReducedMs))

/** Variante reduced-motion de la salida: cambio inmediato (duración 0). */
fun <S> AnimatedContentTransitionScope<S>.xauxaReducedSurfaceExit(): ExitTransition =
    fadeOut(tween(XauxaMotion.DurationReducedMs))
