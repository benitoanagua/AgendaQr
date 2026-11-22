package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqCorner
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqTextStyles
import com.agendaqr.core.ui.theme.AqWeight
import com.agendaqr.core.ui.theme.LocalAqColorScheme
import com.agendaqr.core.ui.theme.aqShadow
import com.agendaqr.core.ui.theme.AqShadow
import com.agendaqr.core.ui.theme.AqSquircles

/**
 * Alerta persistente sobre [MoonAlert] (`moon_design` 1.1.0,
 * `widgets/alert`). Superficie de ancho completo que comunica un estado
 * (éxito, peligro, advertencia, información) y permanece visible hasta que
 * el usuario la descarta o el flujo la retira.
 *
 * - [tone]: semántica del mensaje (success / danger / warning / info /
 *   neutral), resuelta desde los tonos de Polaris.
 * - [dismissLabel] y [onDismiss]: acción de descarte; cuando es nula, la
 *   alerta no es descartable por el usuario.
 * - [icon]: glifo decorativo del tono (aportado por el llamador).
 * - Borde de 1 dp y relleno tonal del contenedor; radio SurfaceSm.
 * - Objetivo táctil ≥ 48 dp en el descarte (D-02).
 * - Todo el copy llega del llamador.
 */
@Composable
fun AqAlert(
    tone: AqTone,
    message: String,
    dismissLabel: String? = null,
    onDismiss: (() -> Unit)? = null,
    icon: ImageVector? = null,
    modifier: Modifier = Modifier,
) {
    val scheme = LocalAqColorScheme.current
    val colors = scheme.toneColors(tone)
    val interactionSource = remember { MutableInteractionSource() }
    val pressed = interactionSource.collectIsPressedAsState().value
    Row(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = AqMetrics.ControlMinSize)
            .background(colors.container, AqSquircles.SurfaceSm)
            .aqShadow(AqShadow.Sm, AqSquircles.SurfaceSm)
            .padding(horizontal = AqSpacing.Lg, vertical = AqSpacing.Md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AqSize.Sm),
    ) {
        icon?.let {
            AqIcon(
                imageVector = it,
                contentDescription = null,
                size = AqMetrics.IconSizeCompact,
                tint = colors.content,
            )
        }
        Text(
            text = message,
            style = AqTextStyles.Body,
            color = colors.content,
            modifier = Modifier.weight(1f, fill = false),
        )
        dismissLabel?.let { label ->
            val dismissEnabled = onDismiss != null
            Box(
                modifier = Modifier
                    .defaultMinSize(minHeight = AqMetrics.ControlMinSize)
                    .clickable(
                        role = Role.Button,
                        onClickLabel = label,
                        interactionSource = interactionSource,
                        indication = null,
                        enabled = dismissEnabled,
                        onClick = { onDismiss?.invoke() },
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = label,
                    style = AqTextStyles.Label.copy(fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold),
                    color = if (pressed && dismissEnabled) colors.content.copy(alpha = 0.8f) else colors.content,
                )
            }
        }
    }
}
