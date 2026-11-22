package com.agendaqr.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.core.ui.theme.AqWeight

/**
 * Barra de selección por lote sobre piezas Moon.
 * Sustituye a [AqSelectionBar] y [AqMoonSelectionBar] (el nombre natural
 * colisiona con el símbolo legacy prohibido, por tanto se usa este nombre).
 *
 * - [closeLabel] / [onClose]: cierra el modo selección.
 * - [selectAllLabel] / [onSelectAll]: selecciona/deseleciona todo.
 * - [selectedCountLabel]: copia del conteo (p. ej. "3 seleccionados").
 * - [selectedCount] / [totalCount]: contadores.
 * - [selectAllChecked]: estado del "seleccionar todo".
 * - Las acciones son [AqButton] variante texto (área táctil 48 dp).
 * - Todo el copy llega del llamador.
 */
@Composable
fun AqSelectionToolbar(
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
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AqSize.Xs),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AqText(
                text = selectedCountLabel,
                size = AqType.Body,
                color = AqColor.TextPrimary,
                fontWeight = AqWeight.Medium,
            )
            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.spacedBy(AqSize.Xs),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AqButton(
                    label = closeLabel,
                    onClick = onClose,
                    variant = AqButtonVariant.Text,
                    size = AqButtonSize.Small,
                    modifier = Modifier.defaultMinSize(minHeight = AqMetrics.ControlMinSize),
                )
                AqButton(
                    label = selectAllLabel,
                    onClick = onSelectAll,
                    variant = AqButtonVariant.Text,
                    size = AqButtonSize.Small,
                    modifier = Modifier.defaultMinSize(minHeight = AqMetrics.ControlMinSize),
                )
            }
        }
    }
}
