package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.state.ToggleableState
import com.agendaqr.core.ui.theme.AqAlpha
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqCorner
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqSquircles
import com.agendaqr.core.ui.theme.AqStateAlpha
import com.agendaqr.core.ui.theme.AqStroke
import com.agendaqr.core.ui.theme.AqTextStyles
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.core.ui.theme.AqWeight
import com.agendaqr.core.ui.theme.LocalAqColorScheme
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Barra contextual de modo selección (G3 / ADR-0034).
 *
 * Se muestra en lugar de la barra superior normal mientras hay una
 * selección activa. Contiene: botón de cierre, `AqSelectAllCheckbox`
 * para seleccionar todo / limpiar, y el contador «N seleccionados».
 * Las acciones masivas específicas las compone la pantalla.
 *
 * - Objetivo táctil ≥ 48 dp en el cierre y el select-all.
 * - Semántica accesible con el conteo anunciado.
 * - El copy llega del llamador (centralizado en AppStrings).
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
            .padding(horizontal = AqSpacing.ScreenMargin, vertical = AqSize.X5s),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AqSize.X5s),
    ) {
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

/**
 * Casilla de verificación sobre [MoonCheckbox]
 * (`moon_design` 1.1.0, `lib/src/widgets/checkbox/checkbox.dart`).
 *
 * Caja de 16 dp con radio squircle interactivo (4 dp), marca de verificación
 * cuando está seleccionada, área táctil de 48 dp (D-02) y soporte tri-estado
 * (seleccionada / no seleccionada / indeterminada). El color de relleno
 * activo y de la marca vienen de Polaris; la forma y el tamaño, de Moon.
 */
@Composable
fun AqCheckbox(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)? = null,
    /** name accesible del control, copy del llamador. */
    contentDescription: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    state: AqFieldState = AqFieldState(),
    /** Tono del relleno cuando está marcada; por defecto énfasis del sistema. */
    activeTone: com.agendaqr.core.ui.components.AqTone = com.agendaqr.core.ui.components.AqTone.Info,
) {
    val canToggle = enabled && onCheckedChange != null && state.errorMessage == null
    val isEnabled = canToggle
    val selected = checked
    val borderColor = when {
        state.isError -> AqColor.Danger
        !enabled -> AqColor.OnDisabled.copy(alpha = 0.5f)
        selected -> activeTone.content()
        else -> AqColor.FunctionalBorder
    }
    val containerColor = when {
        !enabled -> AqColor.OnDisabled.copy(alpha = 0.1f)
        selected -> activeTone.container()
        else -> AqColor.Transparent
    }

    Row(
        modifier = modifier
            .defaultMinSize(minHeight = AqMetrics.ControlMinSize, minWidth = AqMetrics.ControlMinSize),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(AqSize.X2s)
                .clip(AqSquircles.InteractiveXs)
                .background(containerColor, AqSquircles.InteractiveXs)
                .border(AqStroke.Default, borderColor, AqSquircles.InteractiveXs)
                .selectable(
                    selected = selected,
                    role = Role.Checkbox,
                    enabled = isEnabled,
                    onClick = { if (isEnabled) onCheckedChange?.invoke(!selected) },
                )
                .defaultMinSize(minHeight = AqMetrics.ControlMinSize, minWidth = AqMetrics.ControlMinSize),
            contentAlignment = Alignment.Center,
        ) {
            if (selected) {
                AqIcon(
                    imageVector = com.agendaqr.core.ui.components.AqIcons.Check,
                    contentDescription = null,
                    size = AqSize.X3s,
                    tint = activeTone.content(),
                )
            }
        }
        if (contentDescription.isNotBlank()) {
            Text(
                text = contentDescription,
                style = AqTextStyles.Body,
                color = when {
                    !enabled -> AqColor.OnDisabled
                    state.isError -> AqColor.Danger
                    else -> AqColor.TextPrimary
                },
                modifier = Modifier
                    .weight(1f, fill = false)
                    .padding(start = AqSize.Xs),
            )
        }
    }
}

/**
 * Botón de radio sobre [MoonRadio]
 * (`moon_design` 1.1.0, `lib/src/widgets/radio`). Círculo exterior con punto
 * interior cuando está seleccionado, mutua exclusividad por semántica
 * (`selectableGroup` + rol RadioButton) y área táctil de 48 dp.
 */
@Composable
fun AqRadio(
    selected: Boolean,
    onSelectedChange: ((Boolean) -> Unit)? = null,
    /** name accesible del control, copy del llamador. */
    contentDescription: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    activeTone: com.agendaqr.core.ui.components.AqTone = com.agendaqr.core.ui.components.AqTone.Info,
) {
    val canToggle = enabled && onSelectedChange != null
    val isEnabled = canToggle
    val toneColor = activeTone.content()
    val toneContainer = activeTone.container()
    val borderColor = when {
        !enabled -> AqColor.OnDisabled.copy(alpha = 0.5f)
        selected -> toneColor
        else -> AqColor.FunctionalBorder
    }
    Row(
        modifier = modifier
            .defaultMinSize(minHeight = AqMetrics.ControlMinSize, minWidth = AqMetrics.ControlMinSize),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(AqMetrics.IconSize)
                .clip(AqSquircles.InteractiveSm)
                .background(if (selected) toneContainer else AqColor.Transparent, AqSquircles.InteractiveSm)
                .border(AqStroke.Default, borderColor, AqSquircles.InteractiveSm)
                .selectable(
                    selected = selected,
                    role = Role.RadioButton,
                    enabled = isEnabled,
                    onClick = { if (isEnabled) onSelectedChange?.invoke(!selected) },
                )
                .defaultMinSize(minHeight = AqMetrics.ControlMinSize, minWidth = AqMetrics.ControlMinSize),
            contentAlignment = Alignment.Center,
        ) {
            if (selected) {
                Box(
                    modifier = Modifier.size(AqSize.X4s).clip(AqSquircles.InteractiveSm)
                        .background(toneContainer, AqSquircles.InteractiveSm),
                )
            }
        }
        if (contentDescription.isNotBlank()) {
            Text(
                text = contentDescription,
                style = AqTextStyles.Body,
                color = when {
                    !enabled -> AqColor.OnDisabled
                    else -> AqColor.TextPrimary
                },
                modifier = Modifier.weight(1f, fill = false).padding(start = AqSize.Xs),
            )
        }
    }
}

/**
 * Grupo de opciones mutuamente excluyentes sobre [MoonRadio]
 * (semántica de radio). Cada opción conserva el objetivo táctil de 48 dp.
 */
@Composable
fun AqRadioGroup(
    selectedId: String?,
    options: List<AqChoiceOption>,
    onSelected: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    require(options.isNotEmpty()) { "AqRadioGroup necesita al menos una opción" }
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AqSize.X5s),
    ) {
        options.forEachIndexed { index, option ->
            val selected = option.id == selectedId
            AqRadio(
                selected = selected,
                onSelectedChange = { if (it) onSelected(option.id) },
                contentDescription = option.label,
                enabled = enabled,
            )
        }
    }
}

/**
 * Interruptor sobre [MoonSwitch]
 * (`moon_design` 1.1.0, `lib/src/widgets/switch/switch.dart`).
 *
 * Pista con pulgar deslizable, tamaños x2s (16 dp), xs (24 dp) y sm (32 dp),
 * relleno activo e inactivo de Polaris, área táctil de 48 dp.
 */
@Composable
fun AqSwitch(
    checked: Boolean,
    onCheckedChange: ((Boolean) -> Unit)? = null,
    /** name accesible del control, copy del llamador. */
    contentDescription: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    state: AqFieldState = AqFieldState(),
    size: AqSwitchSize = AqSwitchSize.Sm,
) {
    val canToggle = enabled && onCheckedChange != null && state.errorMessage == null
    val isEnabled = canToggle
    val selected = checked
    val trackColor = when {
        state.isError -> AqColor.Danger
        !enabled -> AqColor.OnDisabled.copy(alpha = 0.5f)
        selected -> AqColor.EmphasisFill
        else -> AqColor.SurfaceContainerHighest
    }
    val thumbColor = when {
        state.isError -> AqColor.OnDanger
        !enabled -> AqColor.OnDisabled
        selected -> AqColor.OnEmphasisFill
        else -> AqColor.Surface
    }
    Row(
        modifier = modifier
            .defaultMinSize(minHeight = AqMetrics.ControlMinSize, minWidth = size.touchWidth),
    ) {
        Box(
            modifier = Modifier
                .size(size.trackWidth, size.trackHeight)
                .clip(size.trackShape)
                .background(trackColor, size.trackShape)
                .selectable(
                    selected = selected,
                    role = Role.Switch,
                    enabled = isEnabled,
                    onClick = { if (isEnabled) onCheckedChange?.invoke(!selected) },
                ),
            contentAlignment = if (selected) Alignment.End as Alignment else Alignment.Start as Alignment,
        ) {
            Box(
                modifier = Modifier.size(size.thumbSize).clip(size.thumbShape)
                    .padding(size.thumbOverlap)
                    .background(thumbColor, size.thumbShape),
            )
        }
        if (contentDescription.isNotBlank()) {
            Text(
                text = contentDescription,
                style = AqTextStyles.Body,
                color = when {
                    !enabled -> AqColor.OnDisabled
                    state.isError -> AqColor.Danger
                    else -> AqColor.TextPrimary
                },
                modifier = Modifier.weight(1f, fill = false).padding(start = AqSize.Sm),
            )
        }
    }
}

/** Tamaños del interruptor de Moon (`MoonSwitchSize`: x2s, xs, sm). */
enum class AqSwitchSize(
    val trackHeight: Dp,
    val trackWidth: Dp,
    val thumbSize: Dp,
    val trackShape: androidx.compose.ui.graphics.Shape,
    val thumbShape: androidx.compose.ui.graphics.Shape,
    val thumbOverlap: Dp,
    val touchWidth: Dp,
) {
    X2s(
        trackHeight = AqSize.X2s,
        trackWidth = AqSize.Sm,
        thumbSize = AqSize.X3s,
        trackShape = AqSquircles.InteractiveXs,
        thumbShape = AqSquircles.InteractiveXs,
        thumbOverlap = AqSize.X6s,
        touchWidth = AqMetrics.ControlMinSize,
    ),
    Xs(
        trackHeight = AqSize.Xs,
        trackWidth = AqSize.Md + AqSize.X6s,
        thumbSize = AqSize.X2s,
        trackShape = AqSquircles.InteractiveSm,
        thumbShape = AqSquircles.InteractiveSm,
        thumbOverlap = AqSize.X6s,
        touchWidth = AqMetrics.ControlMinSize,
    ),
    Sm(
        trackHeight = AqSize.Xs,
        trackWidth = AqSize.Xl + AqSize.X6s,
        thumbSize = AqSize.Xs,
        trackShape = AqSquircles.InteractiveSm,
        thumbShape = AqSquircles.InteractiveSm,
        thumbOverlap = AqSize.X5s,
        touchWidth = AqMetrics.ControlMinSize,
    );
}
