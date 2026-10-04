package com.agendaqr.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.core.ui.theme.XauxaMetrics
import com.agendaqr.core.ui.theme.XauxaSpacing
import com.agendaqr.core.ui.theme.XauxaType


/**
 * Botones y chips Xauxa (adaptaciones de Material con tokens).
 * (T12: dividido de XauxaExtendedComponents.kt por responsabilidad, sin cambio de comportamiento.)
 */

@Composable
fun XauxaDangerButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
) {
    androidx.compose.material3.Button(
        modifier = modifier.defaultMinSize(minHeight = XauxaMetrics.ControlMinSize),
        onClick = onClick,
        enabled = enabled && !isLoading,
        shape = RectangleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = XauxaColor.Danger,
            contentColor = XauxaColor.OnDanger,
            disabledContainerColor = XauxaColor.Surface2,
            disabledContentColor = XauxaColor.TextTertiary,
        ),
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(XauxaSpacing.Lg),
                color = XauxaColor.OnDanger,
                strokeWidth = XauxaMetrics.Border,
            )
        } else {
            Text(
                label,
                fontSize = XauxaType.Label,
                fontWeight = FontWeight.Bold,
                letterSpacing = XauxaType.LetterSpacingWide,
            )
        }
    }
}

/** Botón solo-icono con etiqueta accesible y área táctil de 48dp. */
@Composable
fun XauxaIconButton(
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .size(XauxaMetrics.ControlMinSize)
            .semantics { this.contentDescription = contentDescription }
            .clickable(
                interactionSource = interaction,
                indication = null,
                role = Role.Button,
                enabled = enabled,
                onClick = onClick,
            )
            .focusable(interactionSource = interaction)
            .xauxaFocusRing(interaction),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
}

/** Chip de filtro interactivo, distinto de [XauxaCategoryChip]. */
@Composable
fun XauxaFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    FilterChip(
        modifier = modifier.defaultMinSize(minHeight = XauxaMetrics.ControlMinSize),
        selected = selected,
        onClick = onClick,
        enabled = enabled,
        label = {
            Text(
                label,
                fontSize = XauxaType.Label,
                fontWeight = FontWeight.Bold,
                letterSpacing = XauxaType.LetterSpacingWide,
            )
        },
        shape = RectangleShape,
        border = FilterChipDefaults.filterChipBorder(
            enabled = enabled,
            selected = selected,
            borderColor = XauxaColor.Border,
            selectedBorderColor = XauxaColor.Brand,
            borderWidth = XauxaMetrics.Border,
        ),
        colors = FilterChipDefaults.filterChipColors(
            containerColor = XauxaColor.Surface,
            labelColor = XauxaColor.TextPrimary,
            selectedContainerColor = XauxaColor.Brand,
            selectedLabelColor = XauxaColor.OnBrand,
        ),
    )
}

/** Chip de categoría estático: clasifica, no filtra. Sin interacción. */
@Composable
fun XauxaCategoryChip(
    label: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .border(BorderStroke(XauxaMetrics.Border, XauxaColor.Border), RectangleShape)
            .background(XauxaColor.Surface2)
            .padding(horizontal = XauxaSpacing.Sm, vertical = XauxaSpacing.Xs),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, fontSize = XauxaType.Caption, color = XauxaColor.TextSecondary)
    }
}
