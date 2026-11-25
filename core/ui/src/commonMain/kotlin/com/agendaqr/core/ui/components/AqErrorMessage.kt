package com.agendaqr.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.semantics
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqType

/**
 * Mensaje de error de campo sobre [MoonErrorMessage]
 * (`moon_design` 1.1.0, `widgets/common/error_message_widgets.dart`).
 *
 * Moon pinta una fila con el glifo `generic_info_16` a `x2s` (16 dp), un
 * hueco `x5s` (4 dp) y el texto en `body.text12` (12 regular) con el color
 * de error (`chichi`→[AqColor.Danger]). Se expresa siempre con ícono +
 * texto (nunca solo color) y se anuncia al lector de pantalla mediante
 * liveRegion cortés. Todo el copy llega del llamador (AppStrings);
 * core/ui no hardcodea copy.
 *
 * - [message]: texto del error. Cuando es nulo o vacío, el componente no
 *   pinta nada (se oculta sin dejar hueco de layout).
 * - [icon]: glifo opcional; por defecto el `generic_info_16` de Moon.
 */
@Composable
fun AqErrorMessage(
    message: String?,
    icon: ImageVector? = AqIcons.Info,
    modifier: Modifier = Modifier,
) {
    if (message.isNullOrBlank()) return
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AqSize.X5s),
    ) {
        icon?.let {
            AqIcon(
                imageVector = it,
                contentDescription = null,
                size = AqMetrics.ErrorMessageIconSize,
                tint = AqColor.Danger,
            )
        }
        AqText(
            text = message,
            modifier = Modifier
                .semantics {
                    contentDescription = message
                    liveRegion = LiveRegionMode.Polite
                },
            size = AqType.BodyXs,
            color = AqColor.Danger,
        )
    }
}

/** Mensaje de ayuda (helper) de campo: `body.text12` en `textSecondary`. */
@Composable
fun AqHelperMessage(
    message: String?,
    modifier: Modifier = Modifier,
) {
    if (message.isNullOrBlank()) return
    AqText(
        text = message,
        modifier = modifier.semantics { contentDescription = message },
        size = AqType.BodyXs,
        color = AqColor.TextSecondary,
    )
}
