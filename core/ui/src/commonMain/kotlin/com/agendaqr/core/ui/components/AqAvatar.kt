package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqComposition
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.core.ui.theme.AqWeight
import com.agendaqr.core.ui.theme.AqSquircles

/**
 * Avatar sobre [MoonAvatar] (`moon_design` 1.1.0, `widgets/avatar` +
 * `avatar_sizes.dart`/`avatar_theme.dart`). Muestra un glifo, unas
 * iniciales o queda vacío para que el llamador pinte su imagen. Admite una
 * insignia opcional ([badge]) alineada en la esquina inferior derecha.
 *
 * - Tamaños: xs 24 / sm 32 / md 40 / lg 48 / xl 56 / x2l 64 dp
 *   (`avatarSizeValue = sizes.xs…x2l`).
 * - Radio por tamaño: interactiveXs / Sm / Sm / Sm / Md / Md
 *   (`borders.interactive*`), NO círculo completo.
 * - Texto de iniciales: `heading.text10/12/default/16/16/20` (semibold).
 * - Colores: fondo `goku`→[AqColor.Background], texto `textPrimary`,
 *   icono `iconPrimary`→[AqColor.TextPrimary], insignia `roshi`→
 *   [AqColor.Roshi].
 * - Insignia: diámetro `avatarSize × 12/64` y margen `avatarSize × 4/64`
 *   (`badgeSizeValue`/`badgeMarginValue` de `avatar_sizes.dart`,
 *   [AqComposition]).
 * - Área táctil ≥ 48 dp incluso en los tamaños xs/sm (D-02).
 * - Todo el copy (iniciales) llega del llamador.
 */
@Composable
fun AqAvatar(
    size: AqAvatarSize = AqAvatarSize.Md,
    imageVector: ImageVector? = null,
    initials: String? = null,
    badge: (@Composable () -> Unit)? = null,
    /** Muestra la insignia de punto de Moon (`showBadge`) cuando no hay
     *  slot [badge] personalizado. */
    showBadge: Boolean = false,
    badgeColor: androidx.compose.ui.graphics.Color = AqColor.Roshi,
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .size(size.dpValue)
            .clip(size.shape)
            .background(AqColor.Background, size.shape)
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
            AqText(
                text = it,
                size = size.textSize,
                fontWeight = AqWeight.SemiBold,
                color = AqColor.TextPrimary,
            )
        }
        if (badge != null || showBadge) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomEnd,
            ) {
                // Moon `Align(bottomRight)` + `Container(badgeSize)`: la
                // insignia queda flush en la esquina (`badgeMarginValue`
                // solo alimenta el recorte del squircle, no el margen).
                Box(
                    modifier = Modifier
                        .size(size.dpValue * AqComposition.AvatarBadgeSizeFraction)
                        .background(badgeColor, AqSquircles.Circle),
                    contentAlignment = Alignment.Center,
                ) {
                    badge?.invoke()
                }
            }
        }
    }
}

/** Tamaños de [AqAvatar] (Moon `avatar_sizes.dart`). */
enum class AqAvatarSize(
    val dpValue: Dp,
    val iconSize: Dp,
    val textSize: TextUnit,
    val shape: Shape,
) {
    Xs(AqSize.Xs, AqMetrics.IconSize, AqType.Body10, AqSquircles.InteractiveXs),
    Sm(AqSize.Sm, AqMetrics.IconSize, AqType.BodyXs, AqSquircles.InteractiveSm),
    Md(AqSize.Md, AqMetrics.IconSize, AqType.BodySm, AqSquircles.InteractiveSm),
    Lg(AqSize.Lg, AqMetrics.IconSize, AqType.BodyMd, AqSquircles.InteractiveSm),
    Xl(AqSize.Xl, AqMetrics.IconSize, AqType.BodyMd, AqSquircles.InteractiveMd),
    X2l(AqSize.X2l, AqMetrics.IconSize, AqType.Body20, AqSquircles.InteractiveMd),
}
