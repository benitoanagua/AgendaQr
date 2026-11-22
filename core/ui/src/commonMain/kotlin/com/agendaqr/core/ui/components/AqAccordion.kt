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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqSquircles
import com.agendaqr.core.ui.theme.AqTextStyles
import com.agendaqr.core.ui.theme.LocalAqColorScheme
import androidx.compose.ui.graphics.Shape

/**
 * Acordeón sobre [MoonAccordion] (`moon_design` 1.1.0,
 * `widgets/accordion`). Panel expandible/colapsable con cabecera y
 * contenido revelado al pulsar. Mutuamente excluyente dentro de un
 * grupo (el llamador gestiona el estado de cada panel).
 *
 * - [expanded]: estado de expansión.
 * - [onToggle]: invocada al pulsar la cabecera.
 * - [title]: título de la cabecera (copy del llamador).
 * - Cabecera con radio InteractiveSm y área táctil ≥ 48 dp (D-02).
 * - Contenido con relleno SurfaceSm.
 * - Todo el copy llega del llamador.
 */
@Composable
fun AqAccordion(
    title: String,
    expanded: Boolean,
    onToggle: () -> Unit,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    val scheme = LocalAqColorScheme.current
    val interactionSource = remember { MutableInteractionSource() }
    val pressed = interactionSource.collectIsPressedAsState().value
    val container = when {
        pressed && expanded -> scheme.surfaceActive
        expanded -> scheme.selectionSurface
        else -> scheme.surface
    }
    val shape: Shape = AqSquircles.InteractiveSm
    Column(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = AqMetrics.ControlMinSize)
            .background(container, shape)
            .clickable(
                role = Role.Button,
                onClickLabel = title,
                interactionSource = interactionSource,
                indication = null,
                onClick = onToggle,
            )
            .semantics {
                this.contentDescription = title
            },
        verticalArrangement = Arrangement.spacedBy(AqSize.X5s),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AqSpacing.Lg, vertical = AqSize.Xs),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AqSize.Sm),
        ) {
            Text(
                text = title,
                style = AqTextStyles.Label,
                color = scheme.textPrimary,
                modifier = Modifier.weight(1f, fill = false),
            )
        }
        if (expanded) {
            Spacer(modifier = Modifier.height(AqSize.X2s))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AqSpacing.Lg, vertical = AqSize.Xs),
            ) {
                content()
            }
        }
    }
}
