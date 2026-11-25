package com.agendaqr.core.ui.components

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqMotion
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.core.ui.theme.AqWeight

/**
 * Destino raíz de la navegación: etiqueta SIEMPRE visible + icono.
 */
data class AqNavDestination(
    val id: String,
    val label: String,
    val icon: ImageVector,
)

/**
 * Estado del chrome de navegación raíz (barra inferior tipo píldora / rail).
 * Lo mantiene la superficie que lo usa; [AqScreen] lo consume vía
 * [navDestinations]/[navSelectedId]/[onNavSelect].
 *
 * - [destinations]: destinos raíz (Inicio, Buscar, Operaciones, Contextos…).
 * - [selectedId]: id del destino actualmente seleccionado.
 * - [onSelect]: invocada al tocar un destino.
 */
data class AqNavChromeState(
    val destinations: List<AqNavDestination> = emptyList(),
    val selectedId: String? = null,
    val onSelect: (String) -> Unit = {},
)

fun <S> androidx.compose.animation.AnimatedContentTransitionScope<S>.aqSurfaceEnter(reverse: Boolean = false): androidx.compose.animation.EnterTransition =
    slideInHorizontally(
        animationSpec = tween(AqMotion.DurationShortMs, easing = AqMotion.Easings.Standard),
        initialOffsetX = { fullWidth -> (if (reverse) -1 else 1) * fullWidth / AqMotion.SurfaceSlideFraction },
    ) + fadeIn(tween(AqMotion.DurationShortMs, easing = AqMotion.Easings.Standard))

fun <S> androidx.compose.animation.AnimatedContentTransitionScope<S>.aqSurfaceExit(reverse: Boolean = false): androidx.compose.animation.ExitTransition =
    slideOutHorizontally(
        animationSpec = tween(AqMotion.DurationShortMs, easing = AqMotion.Easings.Standard),
        targetOffsetX = { fullWidth -> (if (reverse) 1 else -1) * fullWidth / AqMotion.SurfaceSlideFraction },
    ) + fadeOut(tween(AqMotion.DurationShortMs, easing = AqMotion.Easings.Standard))

/** Variante reduced-motion de la entrada: cambio inmediato (duración 0). */
fun <S> androidx.compose.animation.AnimatedContentTransitionScope<S>.aqReducedSurfaceEnter(): androidx.compose.animation.EnterTransition =
    fadeIn(tween(AqMotion.DurationReducedMs))

/** Variante reduced-motion de la salida: cambio inmediato (duración 0). */
fun <S> androidx.compose.animation.AnimatedContentTransitionScope<S>.aqReducedSurfaceExit(): androidx.compose.animation.ExitTransition =
    fadeOut(tween(AqMotion.DurationReducedMs))
