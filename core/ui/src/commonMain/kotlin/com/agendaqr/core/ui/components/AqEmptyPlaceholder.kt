package com.agendaqr.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.core.ui.theme.AqWeight

/**
 * Estado vacío sobre piezas Moon ([AqAvatar] / glifo Polaris + [AqText] +
 * [AqButton]). Sustituye a [AqEmptyState].
 *
 * - [title]: título del estado vacío (copy del llamador).
 * - [subtitle]: subtítulo opcional.
 * - [icon]: glifo decorativo opcional (p. ej. [AqIcons.QrCode], [AqIcons.Attachment]).
 * - [actionLabel] / [onAction]: acción opcional del estado vacío.
 * - Todo el copy llega del llamador.
 */
@Composable
fun AqEmptyPlaceholder(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    icon: ImageVector? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AqSize.Sm),
    ) {
        icon?.let {
            AqIcon(
                imageVector = it,
                contentDescription = null,
                size = AqSize.Lg,
            )
        }
        AqText(
            text = title,
            size = AqType.Title,
            fontWeight = AqWeight.SemiBold,
            color = AqColor.TextPrimary,
        )
        subtitle?.let {
            AqText(
                text = it,
                size = AqType.Body,
                color = AqColor.TextSecondary,
            )
        }
        actionLabel?.let { label ->
            AqButton(
                label = label,
                onClick = { onAction?.invoke() },
                modifier = Modifier
                    .width(AqMetrics.ButtonCompactHeight + AqSpacing.Lg + AqSpacing.Lg)
                    .defaultMinSize(minWidth = AqMetrics.ControlMinSize),
            )
        }
    }
}
