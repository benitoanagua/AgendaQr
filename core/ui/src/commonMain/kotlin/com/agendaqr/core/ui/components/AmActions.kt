package com.agendaqr.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.agendaqr.core.ui.theme.AmSpacing

/**
 * Acción de navegación textual equivalente a `s-link`: usa la semántica
 * y el área táctil de [AmTextAction] sin inventar un nuevo estilo visual.
 */
@Composable
fun AmLink(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
) {
    AmTextAction(
        label = label,
        onClick = onClick,
        modifier = modifier,
        icon = icon,
        enabled = enabled,
    )
}

/** Modelo inmutable de una acción dentro de [AmButtonGroup]. */
data class AmGroupedAction(
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
fun AmButtonGroup(
    actions: List<AmGroupedAction>,
    modifier: Modifier = Modifier,
    fillMaxWidth: Boolean = false,
) {
    require(actions.count { it.emphasized } <= 1) {
        "AmButtonGroup admite como máximo una acción enfatizada"
    }
    Row(
        modifier = if (fillMaxWidth) modifier.fillMaxWidth() else modifier,
        horizontalArrangement = Arrangement.spacedBy(AmSpacing.Md),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        actions.forEach { action ->
            val actionModifier = if (fillMaxWidth) Modifier.weight(1f) else Modifier
            if (action.emphasized) {
                AmPrimaryButton(
                    label = action.label,
                    onClick = action.onClick,
                    enabled = action.enabled,
                    icon = action.icon,
                    modifier = actionModifier,
                )
            } else {
                AmSecondaryButton(
                    label = action.label,
                    onClick = action.onClick,
                    enabled = action.enabled,
                    icon = action.icon,
                    modifier = actionModifier,
                )
            }
        }
    }
}
