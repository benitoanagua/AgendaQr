package com.agendaqr.core.ui.lab

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import com.agendaqr.core.ui.lab.model.LabReviewStatus
import com.agendaqr.core.ui.theme.XauxaMetrics
import com.agendaqr.core.ui.theme.XauxaSpacing
import com.agendaqr.core.ui.theme.XauxaType

/**
 * Shared chrome of the component lab. The lab is a development tool, so its
 * own scaffolding uses Material 3 as infrastructure (there are no Xauxa
 * equivalents for search fields or filter chips yet) and consumes
 * MaterialTheme colors, which adapt to the light/dark preview. The components
 * under inspection always render with their real Xauxa tokens, which is what
 * makes theme gaps visible.
 */

/** Bordered flat panel used for every lab region. */
@Composable
internal fun LabPanel(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    trailing: (@Composable () -> Unit)? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RectangleShape,
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = XauxaSpacing.None,
        shadowElevation = XauxaSpacing.None,
        border = BorderStroke(XauxaMetrics.Border, MaterialTheme.colorScheme.outline),
    ) {
        Column(
            modifier = Modifier.padding(XauxaSpacing.Lg),
            verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column {
                    Text(
                        title,
                        fontSize = XauxaType.Body,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    if (subtitle != null) {
                        Text(
                            subtitle,
                            fontSize = XauxaType.Caption,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
                trailing?.invoke()
            }
            HorizontalDivider(color = MaterialTheme.colorScheme.outline)
            content()
        }
    }
}

/** Small metadata badge. Text carries the meaning; color only reinforces it. */
@Composable
internal fun LabBadge(text: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RectangleShape,
        color = MaterialTheme.colorScheme.surfaceVariant,
        tonalElevation = XauxaSpacing.None,
    ) {
        Text(
            text,
            modifier = Modifier.padding(horizontal = XauxaSpacing.Sm, vertical = XauxaSpacing.Xs),
            fontSize = XauxaType.Caption,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

/** Status badge for a review classification. Never color-only: label included. */
@Composable
internal fun LabStatusBadge(status: LabReviewStatus, modifier: Modifier = Modifier) {
    val scheme = MaterialTheme.colorScheme
    val color = when (status) {
        LabReviewStatus.VERIFIED -> scheme.primary
        LabReviewStatus.DOCUMENTED -> scheme.tertiary
        LabReviewStatus.PENDING -> scheme.secondary
        LabReviewStatus.NOT_SUPPORTED -> scheme.outline
    }
    Surface(
        modifier = modifier,
        shape = RectangleShape,
        color = scheme.surfaceVariant,
        tonalElevation = XauxaSpacing.None,
    ) {
        Text(
            status.label,
            modifier = Modifier.padding(horizontal = XauxaSpacing.Sm, vertical = XauxaSpacing.Xs),
            fontSize = XauxaType.Caption,
            color = color,
        )
    }
}

/** Label/value row used across the inspector. */
@Composable
internal fun LabLabelValue(label: String, value: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier.fillMaxWidth().defaultMinSize(minHeight = XauxaSpacing.Xxl),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            label,
            fontSize = XauxaType.Label,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Text(
            value,
            fontSize = XauxaType.Label,
            color = MaterialTheme.colorScheme.onSurface,
        )
    }
}

/**
 * Visible callback log: interactive previews report their events here so a
 * control is never wired to an empty callback.
 */
@Composable
internal fun LabEventLog(events: List<String>, modifier: Modifier = Modifier) {
    LabPanel(title = "Eventos observados", subtitle = "Callbacks reales capturados en esta sesión", modifier = modifier) {
        if (events.isEmpty()) {
            Text(
                "Interactúa con el preview para capturar eventos.",
                fontSize = XauxaType.Label,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        } else {
            events.asReversed().forEachIndexed { index, event ->
                Text(
                    "${index + 1}. $event",
                    fontSize = XauxaType.Label,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}
