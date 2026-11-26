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
 * - [actionLabel] / [onAction]: acción principal opcional del estado vacío.
 * - [secondaryActionLabel] / [onSecondaryAction]: segunda acción (Moon v1
 *   `Type=Multi button`); se pinta como [AqButtonVariant.Outline] debajo
 *   de la principal.
 * - [illustration]: slot de ilustración (Moon v1 `Type=Simple+ Illu`); si se
 *   proporciona sustituye al glifo [icon].
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
    /** Ilustración del estado (Moon v1 `Simple+ Illu`); tiene prioridad
     *  sobre [icon]. */
    illustration: (@Composable () -> Unit)? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    secondaryActionLabel: String? = null,
    onSecondaryAction: (() -> Unit)? = null,
    fillViewport: Boolean = false,
) {
    val body: @Composable () -> Unit = {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            // Moon v1 «Empty state»: gap 8 dp entre icono, título,
            // subtítulo y acción (antes `AqSize.Sm` = 32 dp).
            verticalArrangement = Arrangement.spacedBy(AqSize.X4s),
        ) {
        if (illustration != null) {
            illustration()
        } else {
            icon?.let {
                AqIcon(
                    imageVector = it,
                    contentDescription = null,
                    size = AqSize.Lg,
                )
            }
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
        secondaryActionLabel?.let { label ->
            AqButton(
                label = label,
                onClick = { onSecondaryAction?.invoke() },
                variant = AqButtonVariant.Outline,
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
