package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import com.agendaqr.core.ui.components.AqIcons
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqComposition
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSquircles

/**
 * Acción de un banner de estado. El DS no hardcodea copy: la etiqueta
 * llega del llamador (habitualmente desde `UserFacingError` o `AppStrings`).
 */
data class AqBannerAction(val label: String, val onClick: () -> Unit)

/** Confirmación visual decorativa que acompaña al texto del resultado. */
@Composable
fun AqSuccessCheck(
    modifier: Modifier = Modifier,
    /** Lado del círculo de confirmación. */
    size: Dp = AqMetrics.ResultGlyphSize,
) {
    Box(
        modifier = modifier.size(size),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(size)
                .background(AqColor.SuccessContainer, AqSquircles.Circle),
            contentAlignment = Alignment.Center,
        ) {
            AqIcon(
                imageVector = AqIcons.CircleCheck,
                contentDescription = null,
                size = size * AqComposition.SuccessCheckIconFraction,
                tint = AqColor.Success,
            )
        }
    }
}
