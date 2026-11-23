package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqCorner
import com.agendaqr.core.ui.theme.AqLayout
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.core.ui.theme.AqWeight
import com.agendaqr.core.ui.theme.LocalAqColorScheme
import com.agendaqr.core.ui.theme.AqSquircles

/**
 * Alerta persistente sobre [MoonAlert] (`moon_design` 1.1.0,
 * `widgets/alert`). Superficie de ancho completo que comunica un estado
 * (éxito, peligro, advertencia, información) y permanece visible hasta que
 * el usuario la descarta o el flujo la retira.
 *
 * - [tone]: semántica del mensaje (success / danger / warning / info /
 *   neutral), resuelta desde los tonos de Moon.
 * - [dismissLabel] y [onDismiss]: acción de descarte; cuando es nula, la
 *   alerta no es descartable por el usuario. El descarte usa [AqIcons.Close].
 * - [icon]: glifo decorativo del tono (aportado por el llamador).
 * - Borde de 1 dp y relleno tonal del contenedor; radio SurfaceSm.
 * - Geometría Moon (§ Alert): padding 16 en los cuatro lados, hueco
 *   horizontal 12, hueco vertical 8, alto mínimo 56; con más de una acción,
 *   los botones van en una [Row] debajo del mensaje alineados al final; con
 *   una sola, puede ir a la derecha.
 * - Objetivo táctil ≥ 48 dp en el descarte (D-02).
 * - Todo el copy llega del llamador.
 */
@Composable
fun AqAlert(
    tone: AqTone,
    message: String,
    /** Etiqueta destacada sobre [message] (anatomía MoonAlert: etiqueta +
     *  contenido). */
    title: String? = null,
    /** Acción de descarte (secundaria). Se representa con [AqIcons.Close]. */
    dismissLabel: String? = null,
    onDismiss: (() -> Unit)? = null,
    /** Acción primaria opcional (p. ej. "Reintentar ahora"). */
    confirmLabel: String? = null,
    onConfirm: (() -> Unit)? = null,
    /** Descripción accesible del banner (para anuncios de estado como "Guardado"). */
    contentDescription: String? = null,
    icon: ImageVector? = null,
    /** Relleno horizontal del banner (16 dp por defecto; None cuando el
     *  host ya aplica el margen de pantalla, p. ej. [AppStatusHost]). */
    horizontalPadding: Dp = AqSize.X2s,
    modifier: Modifier = Modifier,
) {
    val scheme = LocalAqColorScheme.current
    val colors = scheme.toneColors(tone)
    val interactionSource = remember { MutableInteractionSource() }
    val pressed = interactionSource.collectIsPressedAsState().value
    val hasMultipleActions = (confirmLabel != null && onConfirm != null) &&
        (dismissLabel != null && onDismiss != null)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = AqSize.Xl)
            .background(colors.container, AqSquircles.SurfaceSm)
            .padding(horizontal = horizontalPadding, vertical = AqSize.X2s)
            .semantics {
                liveRegion = LiveRegionMode.Polite
                contentDescription?.let { this.contentDescription = it }
            },
        verticalArrangement = Arrangement.spacedBy(AqSize.X4s),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AqSize.X3s),
        ) {
            icon?.let {
                AqIcon(
                    imageVector = it,
                    contentDescription = null,
                    size = AqMetrics.IconSizeCompact,
                    tint = colors.content,
                )
            }
            Column(
                modifier = Modifier.weight(1f, fill = false),
                verticalArrangement = Arrangement.spacedBy(AqSize.X4s),
            ) {
                title?.let {
                    AqText(
                        text = it,
                        size = AqType.Label,
                        fontWeight = AqWeight.SemiBold,
                        color = colors.content,
                    )
                }
                AqText(
                    text = message,
                    size = AqType.Body,
                    color = colors.content,
                )
            }
            if (!hasMultipleActions) {
                // Una sola acción: va a la derecha del mensaje.
                confirmLabel?.let { label ->
                    val confirmEnabled = onConfirm != null
                    AqButton(
                        label = label,
                        onClick = { onConfirm?.invoke() },
                        variant = AqButtonVariant.Primary,
                        size = AqButtonSize.Small,
                        enabled = confirmEnabled,
                    )
                }
                dismissLabel?.let { label ->
                    val dismissEnabled = onDismiss != null
                    AqButton(
                        label = label,
                        onClick = { onDismiss?.invoke() },
                        variant = AqButtonVariant.Text,
                        size = AqButtonSize.Small,
                        enabled = dismissEnabled,
                        icon = AqIcons.Close,
                        iconOnly = true,
                    )
                }
            }
        }
        if (hasMultipleActions) {
            // Varias acciones: van en una fila debajo del mensaje,
            // alineadas al final.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
            ) {
                confirmLabel?.let { label ->
                    val confirmEnabled = onConfirm != null
                    AqButton(
                        label = label,
                        onClick = { onConfirm?.invoke() },
                        variant = AqButtonVariant.Primary,
                        size = AqButtonSize.Small,
                        enabled = confirmEnabled,
                    )
                }
                dismissLabel?.let { label ->
                    val dismissEnabled = onDismiss != null
                    AqButton(
                        label = label,
                        onClick = { onDismiss?.invoke() },
                        variant = AqButtonVariant.Text,
                        size = AqButtonSize.Small,
                        enabled = dismissEnabled,
                        icon = AqIcons.Close,
                        iconOnly = true,
                    )
                }
            }
        }
    }
}
