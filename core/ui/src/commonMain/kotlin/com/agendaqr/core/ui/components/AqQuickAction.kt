package com.agendaqr.core.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSpacing

/**
 * Acción rápida tipo tarjeta: glifo + etiqueta centrados, área táctil ≥ 48 dp.
 * Usada en la rejilla de acciones de Inicio (S01).
 *
 * - [label]: etiqueta visible y descripción accesible.
 * - [icon]: glifo de [AqIcons] aportado por el llamador.
 * - Todo el copy llega del llamador ([AppStrings]).
 */
@Composable
fun AqQuickAction(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Column(
        modifier = modifier
            .width(AqMetrics.GridIconContainerSize)
            .defaultMinSize(minHeight = AqMetrics.ControlMinSize)
            .padding(AqSpacing.Xs),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AqButton(
            label = label,
            onClick = onClick,
            enabled = enabled,
            icon = icon,
            iconOnly = false,
            variant = AqButtonVariant.Text,
            size = AqButtonSize.Small,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
