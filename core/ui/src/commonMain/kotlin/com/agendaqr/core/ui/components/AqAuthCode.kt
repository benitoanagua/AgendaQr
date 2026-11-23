package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqSquircles
import com.agendaqr.core.ui.theme.AqStroke
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.core.ui.theme.LocalAqColorScheme

/**
 * Código de autenticación sobre [MoonAuthCode] (`moon_design` 1.1.0,
 * `widgets/auth_code`). Campos individuales por dígito, con animación de
 * error opcional. Todo el copy llega del llamador.
 *
 * - [length]: número de dígitos.
 * - [value]: valor actual (un carácter por campo).
 * - [onValueChange]: invocada con el valor completo al cambiar.
 * - [isError]: activa el estado de error (borde de peligro).
 * - Cada campo tiene área táctil ≥ 48 dp (D-02).
 */
@Composable
fun AqAuthCode(
    length: Int = 6,
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean = false,
    modifier: Modifier = Modifier,
) {
    require(length > 0) { "AqAuthCode.length debe ser mayor que 0" }
    val scheme = LocalAqColorScheme.current
    Row(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = AqMetrics.ControlMinSize)
            .semantics {
                this.contentDescription = "Código de autenticación, $length dígitos"
            },
        horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(AqSize.X2s),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        repeat(length) { index ->
            val char = value.take(index + 1).lastOrNull()
            val borderColor = when {
                isError -> AqColor.Danger
                else -> scheme.border
            }
            Box(
                modifier = Modifier
                    .size(AqSize.Sm)
                    .heightIn(min = AqSize.Sm)
                    .background(
                        if (isError) AqColor.DangerContainer else AqColor.InputSurface,
                        AqSquircles.InteractiveXs,
                    )
                    .border(AqStroke.Default, borderColor, AqSquircles.InteractiveXs)
                    .padding(horizontal = AqSize.X2s),
                contentAlignment = Alignment.Center,
            ) {
                if (char != null) {
                    AqText(
                        text = char.toString(),
                        size = AqType.Body,
                        color = AqColor.TextPrimary,
                    )
                }
            }
        }
    }
}
