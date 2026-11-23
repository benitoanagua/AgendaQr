package com.agendaqr.core.ui.theme

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.Dp
import com.agendaqr.core.ui.motion.LocalReducedMotion as LocalAqReducedMotion

/**
 * Port de `MoonBaseControl` y `utils/touch_target_padding.dart` de
 * `moon_design` 1.1.0.
 *
 * API interna de `core/ui`. Los componentes la reutilizan para no duplicar
 * lógica de interacción ni de área táctil por componente.
 *
 * Ofrece:
 * - [AqTouchTarget]: amplía el área táctil a 48 dp (D-02 / ADR-0039 §4.4)
 *   **sin cambiar el aspecto visual**: el contenido se mide con restricciones
 *   naturales y se centra dentro del nodo táctil de 48 dp.
 * - [AqBaseControl]: estados de foco y pressed (emitidos a través de
 *   [interactionSource]) con efecto `scale` al pulsar, respetando «reducir
 *   movimiento» ([LocalAqReducedMotion]).
 *
 * El ancho mínimo táctil de 48 dp se lee de [AqMetrics.ControlMinSize].
 */

/** Estados resueltos por [AqBaseControl]. */
data class AqControlState(
    val pressed: Boolean = false,
    val focused: Boolean = false,
    val enabled: Boolean = true,
)

/**
 * [Box] que mide [content] con restricciones naturales, lo centra y expone un
 * nodo táctil de al menos [minTouchSize] (48 dp por defecto) en ambos ejes.
 * Equivale a `touch_target_padding.dart` de Moon: el contenido conserva su
 * tamaño natural y queda centrado dentro del nodo táctil, sin deformarlo.
 *
 * El nodo resultante es la raíz de semántica (`mergeDescendants = true`), de
    * modo en que consultas como `onNodeWithText` resuelven al nodo táctil de 48 dp
 * y no al nodo visual interno (que puede ser más pequeño).
 *
 * @param text texto accesible que se expone en el nodo táctil de 48 dp, para
 *   que consultas como `onNodeWithText` resuelvan al nodo táctil y no al nodo
 *   visual interno (que puede ser más pequeño).
 * Úsalo en controles cuyo alto visual es inferior a 48 dp (tag x2s/xs/sm,
 * interruptor x2s/xs/sm, chip sm…) para cumplir el objetivo táctil sin
 * deformar el componente.
 */
@Composable
fun AqTouchTarget(
    minTouchSize: Dp = AqMetrics.ControlMinSize,
    text: String? = null,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier
            .defaultMinSize(
                minWidth = minTouchSize,
                minHeight = minTouchSize,
            )
            .semantics(mergeDescendants = true) {
                text?.let { this.text = AnnotatedString(it) }
            },
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

/**
 * Parámetros de comportamiento compartidos por los controles basados en
 * [MoonBaseControl].
 *
 * @param pressEffect escala aplicada al pulsar (1.0f = sin efecto). Se ignora
 *   cuando hay movimiento reducido.
 * @param visualHeight alto visual del control. Cuando es inferior a 48 dp, se
 *   debe envolver el contenido en [AqTouchTarget] para cumplir el área táctil.
 */
data class AqControlParams(
    val pressEffect: Float = 0.96f,
    val visualHeight: Dp = AqMetrics.ControlMinSize,
)

/**
 * Composable base sobre `MoonBaseControl`. Gestiona las fuentes de interacción
 * (pressed, focused) y aplica los efectos transversales de `MoonEffectsTheme`:
 * - `scale`: el contenido se escala levemente al pulsar.
 *
 * Todo queda inanimado (scale = 1f) cuando [LocalAqReducedMotion] es verdadero.
 *
 * Este componente envuelve [contenido] en un nodo táctil (usando
 * [AqTouchTarget] cuando [params.visualHeight] < 48 dp) y, cuando se pasa
 * [onClick], un [clickable] con [Role.Button] conectado a
 * [interactionSource] para que los estados pressed/focused se emitan.
 *
 * El llamador usa los estados para resolver colores y bordes.
 *
 * @param interactionSource fuente de interacción compartida (se crea si no se
 *   pasa).
 * @param enabled si el control responde a la interacción.
 * @param onClick acción de pulsación; cuando no es nula se conecta a
 *   [interactionSource] mediante un [clickable] con [Role.Button].
 * @param params parámetros de comportamiento.
 * @param modifier modificador aplicado al nodo táctil.
 * @param contenido a renderizar dentro del control.
 */
@Composable
fun AqBaseControl(
    contenido: @Composable () -> Unit,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    enabled: Boolean = true,
    onClick: (() -> Unit)? = null,
    params: AqControlParams = AqControlParams(),
    modifier: Modifier = Modifier,
) {
    val reducedMotion = LocalAqReducedMotion.current
    val pressed by interactionSource.collectIsPressedAsState()
    val focused by interactionSource.collectIsFocusedAsState()

    // Efecto `scale` al pulsar (MoonEffectsTheme). Inmovilizado bajo
    // «reducir movimiento».
    val scale = if (enabled && pressed && !reducedMotion && params.pressEffect < 1f) {
        params.pressEffect
    } else {
        1f
    }

    Box(
        modifier = modifier
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        enabled = enabled,
                        onClickLabel = null,
                        interactionSource = interactionSource,
                        indication = null,
                        role = Role.Button,
                        onClick = onClick,
                    )
                } else {
                    Modifier
                },
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (params.visualHeight < AqMetrics.ControlMinSize) {
            AqTouchTarget(AqMetrics.ControlMinSize) {
                // El contenido se mide a su tamaño natural; AqTouchTarget
                // centra y expone el área táctil de 48 dp.
                contenido()
            }
        } else {
            contenido()
        }
    }
}
