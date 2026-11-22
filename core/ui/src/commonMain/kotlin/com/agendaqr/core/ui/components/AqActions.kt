package com.agendaqr.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.agendaqr.core.ui.theme.AqSpacing

/**
 * Acción de navegación textual equivalente a `s-link`: usa [AqButton] con
 * variante Text sin inventar un nuevo estilo visual.
 */
@Composable
fun AqNavLink(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
) {
    AqButton(
        label = label,
        onClick = onClick,
        modifier = modifier,
        variant = com.agendaqr.core.ui.components.AqButtonVariant.Text,
        size = com.agendaqr.core.ui.components.AqButtonSize.Medium,
        icon = icon,
        enabled = enabled,
    )
}

/** Modelo inmutable de una acción dentro de [AqButtonGroup]. */
data class AqGroupedAction(
    val id: String,
    val label: String,
    val onClick: () -> Unit,
    val icon: ImageVector? = null,
    val enabled: Boolean = true,
    val emphasized: Boolean = false,
)

/**
 * Grupo de acciones relacionadas equivalente a `s-button-group`: compone
 * botones existentes con `gap` despacing del sistema y decide el énfasis
 * (un único primario) desde el modelo, nunca desde colores locales.
 */
@Composable
fun AqActionGroup(
    actions: List<AqGroupedAction>,
    modifier: Modifier = Modifier,
    fillMaxWidth: Boolean = false,
) {
    require(actions.count { it.emphasized } <= 1) {
        "AqActionGroup admite como máximo una acción enfatizada"
    }
    Row(
        modifier = if (fillMaxWidth) modifier.fillMaxWidth() else modifier,
        horizontalArrangement = Arrangement.spacedBy(AqSpacing.Md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        actions.forEach { action ->
            val actionModifier = if (fillMaxWidth) Modifier.weight(1f) else Modifier
            if (action.emphasized) {
                AqButton(
                    label = action.label,
                    onClick = action.onClick,
                    enabled = action.enabled,
                    icon = action.icon,
                    modifier = actionModifier,
                    variant = com.agendaqr.core.ui.components.AqButtonVariant.Primary,
                    size = com.agendaqr.core.ui.components.AqButtonSize.Large,
                )
            } else {
                AqButton(
                    label = action.label,
                    onClick = action.onClick,
                    enabled = action.enabled,
                    icon = action.icon,
                    modifier = actionModifier,
                    variant = com.agendaqr.core.ui.components.AqButtonVariant.Default,
                    size = com.agendaqr.core.ui.components.AqButtonSize.Medium,
                )
            }
        }
    }
}
