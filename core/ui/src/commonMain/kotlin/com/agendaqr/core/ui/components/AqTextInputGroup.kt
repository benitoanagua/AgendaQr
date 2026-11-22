package com.agendaqr.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSpacing

/**
 * Grupo de campos de texto sobre [MoonTextInputGroup]
 * (`moon_design` 1.1.0, `widgets/text_input_group`). Orientación
 * horizontal o vertical con hueco consistente entre campos.
 *
 * - [orientation]: `Horizontal` o `Vertical`.
 * - [fields]: composables de campo (p. ej. [AqTextInput]).
 * - Todo el copy llega del llamador a través de cada campo.
 */
@Composable
fun AqTextInputGroup(
    orientation: AqTextInputGroupOrientation = AqTextInputGroupOrientation.Vertical,
    fields: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    when (orientation) {
        AqTextInputGroupOrientation.Vertical -> Column(
            modifier = modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = AqMetrics.ControlMinSize),
            verticalArrangement = Arrangement.spacedBy(AqSpacing.Md),
        ) {
            fields()
        }
        AqTextInputGroupOrientation.Horizontal -> Row(
            modifier = modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = AqMetrics.ControlMinSize),
            horizontalArrangement = Arrangement.spacedBy(AqSpacing.Md),
        ) {
            fields()
        }
    }
}

/** Orientación de [AqTextInputGroup]. */
enum class AqTextInputGroupOrientation { Vertical, Horizontal }
