package com.agendaqr.core.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqTextStyles
import com.agendaqr.core.ui.theme.AqType

/**
 * Mensaje de error de campo sobre [MoonErrorMessage]
 * (`moon_design` 1.1.0, `widgets/common/error_message_widgets.dart`).
 *
 * Se expresa siempre con ícono + texto (nunca solo color) y se anuncia
 * al lector de pantalla mediante liveRegion cortés. Todo el copy llega
 * del llamador (AppStrings); core/ui no hardcodea copy.
 *
 * - [message]: texto del error. Cuando es nulo o vacío, el componente no
 *   pinta nada (se oculta sin dejar hueco de layout).
 * - [icon]: glifo decorativo del error (por defecto el glifo de cancelar
 *   de Moon, aportado por el llamador cuando lo desea).
 * - Objetivo táctil ≥ 48 dp (defaultMinSize).
 */
@Composable
fun AqErrorMessage(
    message: String?,
    icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    modifier: Modifier = Modifier,
) {
    if (message.isNullOrBlank()) return
    Row(
        modifier = modifier
            .defaultMinSize(minHeight = AqMetrics.ControlMinSize),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(AqSize.X2s),
    ) {
        icon?.let {
            AqIcon(
                imageVector = it,
                contentDescription = null,
                size = AqMetrics.IconSizeCompact,
                tint = AqColor.Danger,
            )
            Spacer(modifier = Modifier.width(AqSize.X2s))
        }
        androidx.compose.material3.Text(
            text = message,
            modifier = Modifier
                .semantics {
                    contentDescription = message
                    liveRegion = androidx.compose.ui.semantics.LiveRegionMode.Polite
                },
            style = AqTextStyles.Support,
            color = AqColor.Danger,
        )
    }
}

/** Mensaje de ayuda (helper) de campo, estilo secundario. */
@Composable
fun AqHelperMessage(
    message: String?,
    modifier: Modifier = Modifier,
) {
    if (message.isNullOrBlank()) return
    androidx.compose.material3.Text(
        text = message,
        modifier = modifier.semantics { contentDescription = message },
        style = AqTextStyles.Support,
        color = AqColor.TextSecondary,
    )
}
