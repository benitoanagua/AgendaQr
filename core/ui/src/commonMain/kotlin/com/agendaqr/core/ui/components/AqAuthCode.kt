package com.agendaqr.core.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqMotion
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.core.ui.theme.AqWeight
import com.agendaqr.core.ui.theme.AqSquircles

/**
 * Código de autenticación sobre [MoonAuthCode] (`moon_design` 1.1.0,
 * `widgets/auth_code` + `auth_code_theme.dart`).
 *
 * - Celda: 48 × 56 dp (`sizes.lg` × `sizes.xl`), radio `interactiveSm`,
 *   fondo `goku`→[AqColor.Background] en todos los estados.
 * - Borde: `beerus`→[AqColor.Border] inactivo, `piccolo`→[AqColor.Focus]
 *   en la celda seleccionada ([selectedIndex], por defecto la posición del
 *   cursor = dígitos escritos), `chichi`→[AqColor.Danger] en error.
 * - Texto `body.text24` (24 regular) en `textPrimary`; error
 *   `body.text12` — Moon muestra el mensaje de error bajo las celdas.
 * - Hueco entre celdas `x4s` (8 dp); transición de color de
 *   `defaultTransitionDuration` `easeInOutCubic` (200 ms).
 * - [errorMessage]: texto de error opcional bajo las celdas.
 * - Todo el copy llega del llamador.
 */
@Composable
fun AqAuthCode(
    length: Int = 6,
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean = false,
    modifier: Modifier = Modifier,
    /** Índice de la celda seleccionada; por defecto el cursor
     *  (`value.length`, acotado a la última celda). */
    selectedIndex: Int? = null,
    /** Mensaje de error bajo las celdas (`body.text12`, `chichi`). */
    errorMessage: String? = null,
) {
    require(length > 0) { "AqAuthCode.length debe ser mayor que 0" }
    val shape = AqSquircles.InteractiveSm
    val cursor = selectedIndex ?: value.length.coerceAtMost(length - 1)
    Column(
        modifier = modifier
            .fillMaxWidth()
            .semantics {
                this.contentDescription = "Código de autenticación, $length dígitos"
            },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AqSize.X5s),
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(AqSize.X4s),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            repeat(length) { index ->
                val char = value.getOrNull(index)
                val isSelected = index == cursor
                val borderColor by animateColorAsState(
                    targetValue = when {
                        isError -> AqColor.Danger
                        isSelected -> AqColor.Focus
                        else -> AqColor.Border
                    },
                    animationSpec = tween(
                        durationMillis = AqMotion.DurationMediumMs,
                        easing = AqMotion.Easings.InOutCubic,
                    ),
                    label = "aqAuthCodeBorder$index",
                )
                Box(
                    modifier = Modifier
                        .width(AqSize.Lg)
                        .height(AqSize.Xl)
                        .background(AqColor.Background, shape)
                        .border(AqMetrics.Border, borderColor, shape),
                    contentAlignment = Alignment.Center,
                ) {
                    if (char != null) {
                        AqText(
                            text = char.toString(),
                            size = AqType.HeadingXl,
                            fontWeight = AqWeight.Regular,
                            color = AqColor.TextPrimary,
                        )
                    }
                }
            }
        }
        if (isError && !errorMessage.isNullOrBlank()) {
            AqErrorMessage(message = errorMessage)
        }
    }
}
