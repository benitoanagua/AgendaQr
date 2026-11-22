package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqTextStyles
import com.agendaqr.core.ui.theme.AqWeight
import com.agendaqr.core.ui.theme.AqMotion
import com.agendaqr.core.ui.theme.LocalAqColorScheme
import com.agendaqr.core.ui.theme.AqCorner
import com.agendaqr.core.ui.theme.AqSquircles
import kotlinx.coroutines.delay

/**
 * Toast efímero sobre [MoonToast] (`moon_design` 1.1.0,
 * `widgets/toast`). Mensaje breve que aparece en la parte inferior de la
 * pantalla y se descarta solo tras [durationMs] (por defecto la curva de
 * 200 ms de Moon). No interrumpe el flujo.
 *
 * - [message]: texto del toast.
 * - [actionLabel] / [onAction]: acción opcional (p. ej. "Deshacer").
 * - [icon]: glifo decorativo opcional.
 * - [durationMs]: tiempo de vida en ms; 0 lo mantiene visible hasta que el
 *   llamador cambie [show] a falso.
 * - [onDismissRequest]: se invoca al expirar [durationMs] o al descartar.
 * - Superficie con relleno y radio SurfaceLg, borde funcional.
 * - Objetivo táctil ≥ 48 dp en la acción (D-02).
 * - Todo el copy llega del llamador.
 */
@Composable
fun AqToast(
    show: Boolean = true,
    message: String,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    onDismissRequest: (() -> Unit)? = null,
    icon: ImageVector? = null,
    durationMs: Long = AqMotion.ToastDurationMs.toLong(),
    modifier: Modifier = Modifier,
) {
    if (!show) return
    val onDismissState = rememberUpdatedState(onDismissRequest)
    LaunchedEffect(show, durationMs) {
        if (show && durationMs > 0) {
            delay(durationMs)
            onDismissState.value?.invoke()
        }
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = AqSpacing.Lg, vertical = AqSpacing.Lg),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            modifier = Modifier
                .defaultMinSize(minHeight = AqMetrics.ControlMinSize)
                .background(AqColor.Surface, AqSquircles.SurfaceLg)
                .padding(horizontal = AqSpacing.Lg, vertical = AqSpacing.Md),
            contentAlignment = Alignment.Center,
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AqSize.Sm),
            ) {
                icon?.let {
                    AqIcon(
                        imageVector = it,
                        contentDescription = null,
                        size = AqMetrics.IconSizeCompact,
                        tint = AqColor.TextSecondary,
                    )
                }
                Text(
                    text = message,
                    style = AqTextStyles.Body,
                    color = AqColor.TextPrimary,
                    modifier = Modifier
                        .weight(1f, fill = false)
                        .semantics {
                            contentDescription = message
                            liveRegion = LiveRegionMode.Polite
                        },
                )
                actionLabel?.let { label ->
                    Text(
                        text = label,
                        style = AqTextStyles.Label.copy(fontWeight = AqWeight.SemiBold),
                        color = AqColor.Link,
                        modifier = Modifier
                            .defaultMinSize(minHeight = AqMetrics.ControlMinSize)
                            .clickable(
                                role = Role.Button,
                                onClickLabel = label,
                                onClick = { onAction?.invoke() },
                                enabled = onAction != null,
                            ),
                    )
                }
            }
        }
    }
}
