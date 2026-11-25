package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqStateAlpha
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.core.ui.theme.AqSquircles

/**
 * Grupo de campos sobre [MoonTextInputGroup] (`moon_design` 1.1.0,
 * `widgets/text_input_group` + `text_input_group_theme.dart`).
 *
 * Moon agrupa varios `MoonFormTextInput` sin bordes propios dentro de un
 * único contenedor con borde:
 * - Contenedor: fondo `goku`→[AqColor.Background], borde `beerus`→
 *   [AqColor.Border] (o `chichi`→[AqColor.Danger] cuando todos los campos
 *   tienen error), radio `interactiveSm`, recorte de hijos.
 * - Divisores de 1 dp entre campos (`beerus`), ocultos en hover del grupo.
 * - Orientación `vertical` (Column) u `horizontal` (Row de alto `xl`).
 * - Debajo: helper `body.text12` en `trunks`→[AqColor.TextSecondary] con
 *   padding superior `x4s` y laterales `x3s`; los errores se listan con
 *   [AqErrorMessage] (icono + texto, `body.text12`, color de error).
 * - Los campos hijos deben componerse con `borderless = true` ([AqTextInput]
 *   / [AqTextArea]) para no duplicar fondo ni borde.
 * - Deshabilitado = opacidad 0.6. Todo el copy llega del llamador.
 */
@Composable
fun AqTextInputGroup(
    children: List<@Composable () -> Unit>,
    orientation: AqTextInputGroupOrientation = AqTextInputGroupOrientation.Vertical,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    helper: String? = null,
    /** Errores del grupo; cuando cubre todos los campos el borde del
     *  contenedor pasa a color de error (regla de Moon). */
    errors: List<String> = emptyList(),
) {
    val interactionSource = remember { MutableInteractionSource() }
    val hovered by interactionSource.collectIsHoveredAsState()
    val shape = AqSquircles.InteractiveSm
    val allErrors = errors.isNotEmpty() && errors.size >= children.size
    val borderColor = if (allErrors) AqColor.Danger else AqColor.Border
    Column(
        modifier = modifier
            .fillMaxWidth()
            .alpha(if (enabled) 1f else AqStateAlpha.Disabled),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .hoverable(interactionSource)
                .clip(shape)
                .background(AqColor.Background, shape)
                .border(AqMetrics.Border, borderColor, shape),
        ) {
            when (orientation) {
                AqTextInputGroupOrientation.Vertical -> Column(
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    children.forEachIndexed { index, child ->
                        if (index > 0 && !hovered) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(AqMetrics.DividerHeight)
                                    .background(AqColor.Border),
                            )
                        }
                        Box(modifier = Modifier.fillMaxWidth()) { child() }
                    }
                }
                AqTextInputGroupOrientation.Horizontal -> Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(AqSize.Xl),
                ) {
                    children.forEachIndexed { index, child ->
                        if (index > 0 && !hovered) {
                            Box(
                                modifier = Modifier
                                    .width(AqMetrics.DividerHeight)
                                    .height(AqSize.Xl)
                                    .background(AqColor.Border),
                            )
                        }
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(AqSize.Xl),
                        ) { child() }
                    }
                }
            }
        }
        if (errors.isNotEmpty()) {
            Column(
                modifier = Modifier.padding(
                    start = AqSize.X3s,
                    end = AqSize.X3s,
                    top = AqSize.X4s,
                ),
                verticalArrangement = Arrangement.spacedBy(AqSize.X5s),
            ) {
                errors.forEach { AqErrorMessage(message = it) }
            }
        } else if (helper != null) {
            com.agendaqr.core.ui.components.AqText(
                text = helper,
                modifier = Modifier
                    .padding(
                        start = AqSize.X3s,
                        end = AqSize.X3s,
                        top = AqSize.X4s,
                    )
                    .semantics { contentDescription = helper },
                size = AqType.BodyXs,
                color = AqColor.TextSecondary,
            )
        }
    }
}

/** Orientación de [AqTextInputGroup] (Moon `MoonTextInputGroupOrientation`). */
enum class AqTextInputGroupOrientation { Vertical, Horizontal }
