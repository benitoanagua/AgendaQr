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
import com.agendaqr.core.ui.theme.LocalAqColorScheme
import com.agendaqr.core.ui.theme.AqSquircles

/** Relleno interno del banner en los cuatro lados (Moon § Alert: 16 dp). */
private val AqAlertPadding: Dp = AqSize.X2s

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
 *   horizontal 12, hueco vertical 8, alto mínimo 56; siempre abraza su
 *   contenido (nunca llena el alto del padre); con más de una acción,
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
    /** Color de fondo del banner (Moon `MoonAlert.backgroundColor`, p. ej.
     *  la variante `filled`). Por defecto el `container` del tono. */
    containerColor: Color? = null,
    modifier: Modifier = Modifier,
) {
    val scheme = LocalAqColorScheme.current
    val colors = scheme.toneColors(tone)
    val interactionSource = remember { MutableInteractionSource() }
    val pressed = interactionSource.collectIsPressedAsState().value
    val hasMultipleActions = (confirmLabel != null && onConfirm != null) &&
        (dismissLabel != null && onDismiss != null)
    // La alerta SIEMPRE abraza su contenido. Moon (Flutter) usa
    // `mainAxisSize.max`, pero en Compose un padre con alto acotado
    // (Column con weight, Box de tamaño fijo, marco de galería) haría que el
    // banner llenara toda la pantalla.
    Box(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = AqSize.Xl)
            .background(containerColor ?: colors.container, AqSquircles.InteractiveSm)
            // La semántica va antes del padding para que el nodo liveRegion
            // (y sus bounds) cubra el banner completo, no solo el contenido.
            .semantics {
                liveRegion = LiveRegionMode.Polite
                contentDescription?.let { this.contentDescription = it }
            }
            .padding(horizontal = AqAlertPadding, vertical = AqAlertPadding),
        contentAlignment = Alignment.CenterStart,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
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
                modifier = Modifier.weight(1f, fill = true),
                verticalArrangement = Arrangement.spacedBy(AqSize.X4s),
            ) {
                title?.let {
                    AqText(
                        text = it,
                        size = AqType.HeadingSm,
                        color = colors.content,
                    )
                }
                AqText(
                    text = message,
                    size = AqType.BodySm,
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
                horizontalArrangement = Arrangement.spacedBy(AqSize.X4s, Alignment.End),
            ) {
                confirmLabel?.let { label ->
                    AqButton(
                        label = label,
                        onClick = { onConfirm?.invoke() },
                        variant = AqButtonVariant.Primary,
                        size = AqButtonSize.Small,
                    )
                }
                dismissLabel?.let { label ->
                    AqButton(
                        label = label,
                        onClick = { onDismiss?.invoke() },
                        variant = AqButtonVariant.Text,
                        size = AqButtonSize.Small,
                        icon = AqIcons.Close,
                        iconOnly = true,
                    )
                }
            }
        }
        }
    }
}
