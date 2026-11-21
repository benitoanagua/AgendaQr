package com.agendaqr.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.ui.state.ToggleableState
import com.agendaqr.core.ui.components.AqIcons
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqTextStyles
import com.agendaqr.core.ui.theme.AqType

/**
 * Barra contextual de modo selección (G3 / ADR-0034).
 *
 * Se muestra en lugar de la barra superior normal mientras hay una
 * selección activa. Contiene: botón de cierre, `AqSelectAllCheckbox`
 * para seleccionar todo / limpiar, y el contador «N seleccionados».
 * Las acciones masivas específicas (favorito, asignar contexto, eliminar)
 * las compone la pantalla que lo invoca.
 *
 * - Objetivo táctil ≥ 48 dp en el cierre y el select-all.
 * - Semántica accesible con el conteo anunciado.
 * - El copy llega del llamador (centralizado en AppStrings); core/ui no
 *   hardcodea copy.
 */
@Composable
fun AqSelectionBar(
    closeLabel: String,
    selectAllLabel: String,
    selectedCountLabel: String,
    selectedCount: Int,
    totalCount: Int,
    selectAllChecked: Boolean,
    onSelectAll: () -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = AqSpacing.ScreenMargin, vertical = AqSpacing.Sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
    ) {
        // Cierre del modo selección.
        AqSelectionIconButton(
            buttonContentDescription = closeLabel,
            onClick = onClose,
        ) {
            AqIcon(
                AqIcons.Close,
                null,
                modifier = Modifier.size(AqMetrics.IconSizeCompact),
                tint = AqColor.TextPrimary,
            )
        }

        // Seleccionar todo / limpiar (estado tri-estado derivado por el llamador).
        AqSelectAllCheckbox(
            label = selectAllLabel,
            checkedState = if (selectedCount == 0) {
                ToggleableState.Off
            } else if (selectedCount == totalCount) {
                ToggleableState.On
            } else {
                ToggleableState.Indeterminate
            },
            onClick = onSelectAll,
        )

        // Contador anunciado al lector de pantalla.
        AqText(
            text = selectedCountLabel,
            size = AqType.BodySmall,
            color = AqColor.TextSecondary,
            modifier = Modifier
                .semantics { contentDescription = selectedCountLabel }
                .weight(1f, fill = false),
        )
    }
}

/** Botón icono accesible reutilizado por la barra de selección. */
@Composable
private fun AqSelectionIconButton(
    buttonContentDescription: String,
    onClick: () -> Unit,
    content: @Composable () -> Unit,
) {
    androidx.compose.material3.IconButton(
        onClick = onClick,
        modifier = Modifier
            .defaultMinSize(minHeight = AqMetrics.ControlMinSize)
            .semantics {
                this.contentDescription = buttonContentDescription
            },
    ) {
        content()
    }
}
