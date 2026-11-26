package com.agendaqr.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
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
 * Estado vacío sobre piezas Moon ([AqAvatar] / glifo Moon + [AqText] +
 * [AqButton]). Sustituye a [AqEmptyState].
 *
 * - [title]: título del estado vacío (copy del llamador).
 * - [subtitle]: subtítulo opcional.
 * - [icon]: glifo decorativo opcional (p. ej. [AqIcons.QrCode], [AqIcons.Attachment]).
 * - [actionLabel] / [onAction]: acción opcional del estado vacío.
 * - [fillViewport]: el placeholder ocupa todo el alto disponible (p. ej.
 *   `Modifier.weight(1f)` dentro del contenido de [AqScreen]) y centra su
 *   contenido verticalmente — patrón de estado vacío de pantalla.
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
    fillViewport: Boolean = false,
) {
    val body: @Composable () -> Unit = {
        Column(
            modifier = Modifier.fillMaxWidth(),
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
                    .fillMaxWidth()
                    .widthIn(max = AqMetrics.ButtonLargeHeight + AqSpacing.Xxl),
            )
        }
        }
    }
    if (fillViewport) {
        Box(
            modifier = modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) {
            body()
        }
    } else {
        Column(modifier = modifier.fillMaxWidth()) { body() }
    }
}
