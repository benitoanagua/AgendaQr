package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqSquircles
import com.agendaqr.core.ui.theme.AqTextStyles
import com.agendaqr.core.ui.theme.LocalAqColorScheme

/**
 * Barra de migas sobre [MoonBreadcrumb] (`moon_design` 1.1.0,
 * `widgets/breadcrumb`). Navegación jerárquica con separadores y la
 * página actual como último ítem (no interactiva).
 *
 * - [items]: etiquetas del camino, la última es la página actual.
 * - [onItemClick]: opcional; se invoca al pulsar un migas (excepto la
 *   actual).
 * - Todo el copy llega del llamador.
 */
@Composable
fun AqBreadcrumb(
    items: List<String>,
    onItemClick: ((Int) -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    require(items.isNotEmpty()) { "AqBreadcrumb necesita al menos un ítem" }
    val scheme = LocalAqColorScheme.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = AqMetrics.ControlMinSize),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AqSize.X2s),
    ) {
        items.forEachIndexed { index, label ->
            val last = index == items.lastIndex
            if (index > 0) {
                Spacer(modifier = Modifier.width(AqSize.X2s))
                Text(
                    text = "/",
                    style = AqTextStyles.Body,
                    color = scheme.textTertiary,
                )
                Spacer(modifier = Modifier.width(AqSize.X2s))
            }
            if (last) {
                Text(
                    text = label,
                    style = AqTextStyles.Body,
                    color = scheme.textPrimary,
                    modifier = Modifier.weight(1f, fill = false),
                )
            } else {
                val interactionSource = remember { MutableInteractionSource() }
                val pressed = interactionSource.collectIsPressedAsState().value
                Row(
                    modifier = Modifier
                        .clickable(
                            role = Role.Button,
                            onClickLabel = label,
                            interactionSource = interactionSource,
                            indication = null,
                            enabled = onItemClick != null,
                            onClick = { onItemClick?.invoke(index) },
                        )
                        .background(
                            if (pressed && onItemClick != null) scheme.surfaceActive else AqColor.Transparent,
                            AqSquircles.InteractiveXs,
                        )
                        .padding(horizontal = AqSize.X2s, vertical = AqSize.X5s)
                        .semantics {
                            this.contentDescription = label
                            if (onItemClick != null) {
                                this.role = Role.Button
                            }
                        },
                ) {
                    Text(
                        text = label,
                        style = AqTextStyles.Body.copy(
                            color = scheme.emphasisFill,
                            fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                        ),
                    )
                }
            }
        }
    }
}
