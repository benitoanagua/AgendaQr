package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.ui.draw.clip
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqTextStyles
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.core.ui.theme.AqWeight
import com.agendaqr.core.ui.theme.AqCorner
import com.agendaqr.core.ui.theme.AqRadius
import com.agendaqr.core.ui.theme.AqSquircles
import androidx.compose.material3.Text

/**
 * Avatar sobre [MoonAvatar] (`moon_design` 1.1.0, `widgets/avatar`).
 *
 * Círculo o cuadrado suavizado que muestra un glifo de Moon, unas
 * iniciales o queda vacío para que el llamador pinte su imagen. Admite una
 * insignia opcional ([badge]) alineada en la esquina inferior derecha para
 * estados como el contexto activo.
 *
 * - Tamaños: xs 24 / sm 32 / md 40 / lg 48 / xl 56 / x2l 64 dp ([AqSize]).
 * - Radio: SurfaceSm (cuadrado) o círculo completo para xs/sm.
 * - Área táctil ≥ 48 dp incluso en los tamaños xs/sm (D-02).
 * - Todo el copy (iniciales) llega del llamador.
 */
@Composable
fun AqAvatar(
    size: AqAvatarSize = AqAvatarSize.Md,
    imageVector: ImageVector? = null,
    initials: String? = null,
    badge: (@Composable () -> Unit)? = null,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    val circle = size == AqAvatarSize.Xs || size == AqAvatarSize.Sm
    val shape: Shape = if (circle) AqSquircles.Circle else AqSquircles.SurfaceSm
    Box(
        modifier = modifier
            .size(size.dpValue)
            .defaultMinSize(minWidth = AqMetrics.ControlMinSize, minHeight = AqMetrics.ControlMinSize)
            .clip(shape)
            .background(AqColor.SurfaceContainerHigh, shape)
            .semantics { this.contentDescription = contentDescription }
            .testTag("aq-avatar"),
        contentAlignment = Alignment.Center,
    ) {
        imageVector?.let {
            AqIcon(
                imageVector = it,
                contentDescription = null,
                size = size.iconSize,
                tint = AqColor.TextPrimary,
            )
        }
        initials?.let {
            Text(
                text = it,
                style = AqTextStyles.Label.copy(
                    fontWeight = AqWeight.SemiBold,
                    fontSize = size.textSize,
                ),
                color = AqColor.TextPrimary,
            )
        }
        badge?.let {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomEnd,
            ) {
                Box(modifier = Modifier.padding(AqSize.X2s)) { it() }
            }
        }
    }
}

/** Tamaños de [AqAvatar] (tabla §3 del catálogo Moon). */
enum class AqAvatarSize(
    val dpValue: Dp,
    val iconSize: Dp,
    val textSize: androidx.compose.ui.unit.TextUnit,
) {
    Xs(AqSize.Xs, AqMetrics.IconSizeCompact, com.agendaqr.core.ui.theme.AqType.BodyXs),
    Sm(AqSize.Sm, AqMetrics.IconSizeCompact, com.agendaqr.core.ui.theme.AqType.BodyXs),
    Md(AqSize.Md, AqMetrics.IconSize, com.agendaqr.core.ui.theme.AqType.BodySm),
    Lg(AqSize.Lg, AqMetrics.IconSize, com.agendaqr.core.ui.theme.AqType.BodyMd),
    Xl(AqSize.Xl, AqMetrics.IconSize, com.agendaqr.core.ui.theme.AqType.BodyMd),
    X2l(AqSize.X2l, AqMetrics.IconSize, com.agendaqr.core.ui.theme.AqType.BodyLg),
}
