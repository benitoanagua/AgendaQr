package com.agendaqr.core.ui.lab

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import com.agendaqr.core.ui.lab.model.LabReviewStatus
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.core.ui.theme.XauxaMetrics
import com.agendaqr.core.ui.theme.XauxaSpacing
import com.agendaqr.core.ui.theme.XauxaType

@Composable
internal fun LabPanel(title: String, modifier: Modifier = Modifier, subtitle: String? = null, trailing: (@Composable () -> Unit)? = null, content: @Composable ColumnScope.() -> Unit) {
    Surface(modifier = modifier.fillMaxWidth(), shape = RectangleShape, color = XauxaColor.Surface, tonalElevation = XauxaSpacing.None, shadowElevation = XauxaSpacing.None, border = BorderStroke(XauxaMetrics.Border, XauxaColor.Border)) {
        Column(modifier = Modifier.padding(XauxaSpacing.Lg), verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(title, fontSize = XauxaType.Body, fontWeight = FontWeight.SemiBold, color = XauxaColor.TextPrimary)
                    subtitle?.let { Text(it, fontSize = XauxaType.Caption, color = XauxaColor.TextSecondary) }
                }
                trailing?.invoke()
            }
            HorizontalDivider(color = XauxaColor.Border)
            content()
        }
    }
}

@Composable
internal fun LabBadge(text: String, modifier: Modifier = Modifier) {
    Surface(modifier = modifier, shape = RectangleShape, color = XauxaColor.Surface2, tonalElevation = XauxaSpacing.None) {
        Text(text, modifier = Modifier.padding(horizontal = XauxaSpacing.Sm, vertical = XauxaSpacing.Xs), fontSize = XauxaType.Caption, color = XauxaColor.TextSecondary)
    }
}

@Composable
internal fun LabStatusBadge(status: LabReviewStatus, modifier: Modifier = Modifier) {
    val color = when (status) {
        LabReviewStatus.VERIFIED -> XauxaColor.Brand
        LabReviewStatus.DOCUMENTED -> XauxaColor.Secondary
        LabReviewStatus.PENDING -> XauxaColor.Tertiary
        LabReviewStatus.NOT_SUPPORTED -> XauxaColor.TextTertiary
    }
    Surface(modifier = modifier, shape = RectangleShape, color = XauxaColor.Surface2, tonalElevation = XauxaSpacing.None) {
        Text(status.label, modifier = Modifier.padding(horizontal = XauxaSpacing.Sm, vertical = XauxaSpacing.Xs), fontSize = XauxaType.Caption, color = color)
    }
}

@Composable
internal fun LabLabelValue(label: String, value: String, modifier: Modifier = Modifier) {
    Row(modifier = modifier.fillMaxWidth().defaultMinSize(minHeight = XauxaSpacing.Xxl), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
        Text(label, fontSize = XauxaType.Label, color = XauxaColor.TextSecondary)
        Text(value, fontSize = XauxaType.Label, color = XauxaColor.TextPrimary)
    }
}

@Composable
internal fun LabEventLog(events: List<String>, modifier: Modifier = Modifier) {
    LabPanel(title = "Eventos observados", subtitle = "Callbacks reales capturados en esta sesión", modifier = modifier) {
        if (events.isEmpty()) {
            Text("Interactúa con el preview para capturar eventos.", fontSize = XauxaType.Label, color = XauxaColor.TextSecondary)
        } else {
            events.asReversed().forEachIndexed { index, event ->
                Text("" + (index + 1) + ". " + event, fontSize = XauxaType.Label, color = XauxaColor.TextPrimary)
            }
        }
    }
}

@Composable
internal fun LabChoiceChip(label: String, selected: Boolean, onClick: () -> Unit, modifier: Modifier = Modifier) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(label, fontSize = XauxaType.Label, fontWeight = FontWeight.Bold, letterSpacing = XauxaType.LetterSpacingWide) },
        modifier = modifier.defaultMinSize(minHeight = XauxaMetrics.ControlMinSize),
        shape = RectangleShape,
        border = FilterChipDefaults.filterChipBorder(enabled = true, selected = selected, borderColor = XauxaColor.Border, selectedBorderColor = XauxaColor.Brand, borderWidth = XauxaMetrics.Border),
        colors = FilterChipDefaults.filterChipColors(containerColor = XauxaColor.Surface2, labelColor = XauxaColor.TextPrimary, selectedContainerColor = XauxaColor.Brand, selectedLabelColor = XauxaColor.OnBrand),
    )
}
