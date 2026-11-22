package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.core.ui.theme.AqSquircles

/**
 * Celda de lista sobre [MoonMenuItem] (`moon_design` 1.1.0,
 * `widgets/menu_item`). Patrón de composición del catálogo §4:
 * `AqMenuItem` + `AqAvatar` / `AqTag`.
 *
 * - [leading]: avatar o glifo a la izquierda.
 * - [title]: título (una línea).
 * - [subtitle]: segunda línea opcional (texto secundario).
 * - [trailing]: glifo, chevron o slot personalizado a la derecha.
 * - [selected]: estado de selección (fondo de selección).
 * - [enabled]: controla la interacción; cuando es falso no emite onClick.
 * - [showDivider]: divisor de 1 dp al final.
 * - Área táctil ≥ 48 dp (D-02); semántica de botón con rol.
 * - Todo el copy llega del llamador.
 */
@Composable
fun AqMenuItem(
    title: String,
    subtitle: String? = null,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    selected: Boolean = false,
    enabled: Boolean = true,
    showDivider: Boolean = false,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed = interactionSource.collectIsPressedAsState().value
    val container = when {
        !enabled -> AqColor.Disabled
        selected -> AqColor.SelectionSurface
        pressed && enabled -> AqColor.SurfaceActive
        else -> AqColor.Surface
    }
    val contentColor = when {
        !enabled -> AqColor.OnDisabled
        selected -> AqColor.SelectionIcon
        else -> AqColor.TextPrimary
    }
    val subtitleColor = when {
        !enabled -> AqColor.OnDisabled
        else -> AqColor.TextSecondary
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = AqMetrics.ControlMinSize)
            .background(container, AqSquircles.InteractiveSm)
            .clickable(
                role = Role.Button,
                onClickLabel = title,
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled && onClick != null,
                onClick = { onClick?.invoke() },
            )
            .semantics {
                contentDescription = title + (subtitle?.let { " - $it" } ?: "")
            },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AqSpacing.Lg, vertical = AqSize.X2s),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AqSize.Sm),
        ) {
            leading?.invoke()
            Column(
                modifier = Modifier.weight(1f, fill = false),
                verticalArrangement = Arrangement.spacedBy(AqSize.X5s),
            ) {
                Text(
                    text = title,
                    style = AqTextStyles.RowTitle,
                    color = contentColor,
                    modifier = Modifier.weight(1f, fill = false),
                    maxLines = 1,
                )
                subtitle?.let {
                    Text(
                        text = it,
                        style = AqTextStyles.BodySmall,
                        color = subtitleColor,
                        maxLines = 1,
                    )
                }
            }
            Spacer(Modifier.weight(1f, fill = false))
            trailing?.invoke()
        }
        if (showDivider) {
            androidx.compose.foundation.layout.Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Spacer(modifier = Modifier.width(AqMetrics.ListDividerInset))
                androidx.compose.material3.HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    color = AqColor.Divider,
                    thickness = AqMetrics.DividerHeight,
                )
            }
        }
    }
}
