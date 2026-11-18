package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqShapes
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqType

/**
 * Indicador enmascarado de puntos del PIN (S00): indicador enmascarado de la
 * entrada. Decorativos por diseño — cada tecla anuncia su dígito y los
 * errores y los intentos restantes viven como texto en la pantalla, así
 * que el punto no duplica señal accesible.
 *
 * @param filled dígitos ya ingresados.
 * @param total capacidad visible (crece con la entrada hasta el máximo).
 */
@Composable
fun AqPinDots(
    filled: Int,
    total: Int,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(AqSpacing.Md),
    ) {
        repeat(total) { index ->
            Box(
                modifier = Modifier
                    .size(AqMetrics.PinDotSize)
                    .background(
                        if (index < filled) AqColor.EmphasisFill else AqColor.SurfaceContainerHighest,
                        AqShapes.Pill,
                    ),
            )
        }
    }
}

/**
 * Teclado numérico propio del acceso local (S00):
 * retícula 3×4 con 1–9, hueco inferior-izquierdo, 0 y borrado. Cada tecla
 * es un único botón con el objetivo táctil completo (≥48 dp) y la
 * pulsación fiel de TDesign: color activo del token, sin ripple (D-11).
 *
 * El PIN es de longitud variable (4–12): no hay envío automático; la
 * pantalla mantiene su acción primaria explícita de desbloqueo.
 *
 * @param backspaceDescription nombre accesible de la tecla de borrado;
 *   copy del llamador (la pantalla nunca escribe literales de UI).
 */
@Composable
fun AqKeypad(
    onDigit: (Char) -> Unit,
    onBackspace: () -> Unit,
    backspaceDescription: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AqSpacing.Xs),
    ) {
        listOf(
            listOf('1', '2', '3'),
            listOf('4', '5', '6'),
            listOf('7', '8', '9'),
        ).forEach { row ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
            ) {
                row.forEach { digit ->
                    KeypadKey(
                        onClick = { onDigit(digit) },
                        modifier = Modifier.weight(1f),
                    ) {
                        AqText(
                            digit.toString(),
                            size = AqType.HeadlineSmall,
                        )
                    }
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
        ) {
            // Hueco clásico del teclado telefónico: conserva la retícula
            // sin inventar una función que el producto no tiene.
            Box(modifier = Modifier.weight(1f))
            KeypadKey(
                onClick = { onDigit('0') },
                modifier = Modifier.weight(1f),
            ) {
                AqText(
                    "0",
                    size = AqType.HeadlineSmall,
                )
            }
            KeypadKey(
                onClick = onBackspace,
                modifier = Modifier.weight(1f),
                contentDescription = backspaceDescription,
            ) {
                AqIcon(
                    imageVector = AqIcons.Erase,
                    contentDescription = null,
                    tint = AqColor.TextSecondary,
                )
            }
        }
    }
}

/**
 * Tecla del teclado: superficie con color activo al presionar (D-11) y
 * objetivo táctil de 48 dp aunque la retícula sea compacta.
 */
@Composable
private fun KeypadKey(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
    content: @Composable () -> Unit,
) {
    // TDCell responde en `onTapDown` con color activo: token activo, sin
    // ripple Material.
    val interactionSource = remember { MutableInteractionSource() }
    val pressed = interactionSource.collectIsPressedAsState().value
    Box(
        modifier = modifier
            .defaultMinSize(minHeight = AqMetrics.ControlMinSize)
            .clip(AqShapes.Medium)
            .background(
                if (pressed) AqColor.SurfaceActive else AqColor.SurfaceContainerLow,
                AqShapes.Medium,
            )
            .clickable(
                role = Role.Button,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick,
            )
            .semantics { contentDescription?.let { this.contentDescription = it } },
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}
