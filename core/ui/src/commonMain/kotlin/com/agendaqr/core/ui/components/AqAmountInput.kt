package com.agendaqr.core.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.theme.AqSpacing

/**
 * Entrada de importe sobre [AqTextInput] (ADR-0036: sin teclado propio).
 * Sustituye a [AqAmountField].
 *
 * - [label]: etiqueta del campo (copy del llamador).
 * - [value] / [onValueChange]: estado del importe (controlado por el llamador).
 * - [currencySymbol]: moneda mostrada en el slot trailing (p. ej. "USD", "EUR").
 * - [isError] / [errorMessage]: estado de error con borde de peligro.
 * - Todo el copy llega del llamador.
 */
@Composable
fun AqAmountInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    currencySymbol: String? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    enabled: Boolean = true,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        AqTextInput(
            label = label,
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            size = AqTextInputSize.Md,
            isError = isError,
            errorMessage = errorMessage,
            visualTestTag = "aq-amount-input-visual",
            trailing = currencySymbol?.let {
                {
                    AqText(
                        text = it,
                        size = com.agendaqr.core.ui.theme.AqType.Body,
                        color = com.agendaqr.core.ui.theme.AqColor.TextSecondary,
                        modifier = Modifier.padding(end = com.agendaqr.core.ui.theme.AqSpacing.Sm),
                    )
                }
            },
        )
    }
}
