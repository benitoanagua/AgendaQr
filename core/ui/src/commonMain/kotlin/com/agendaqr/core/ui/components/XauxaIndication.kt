package com.agendaqr.core.ui.components

import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import com.agendaqr.core.ui.theme.XauxaOpacity

/**
 * Indicación XAUXA global (contrato §7): overlay plano con el token de
 * opacidad de pulsación; sin ripple ni state layer de Material.
 *
 * NOTA: en CMP 1.8.x la interfaz Indication/IndicationInstance de
 * foundation.interaction no está disponible en commonMain (verificado
 * por compilación). Esta clase ofrece un Modifier que los componentes
 * aplican en lugar de un Indication global; XauxaTheme también
 * desactiva el ripple de M3 vía LocalIndication.
 */
object XauxaIndication {
    /**
     * Modifier que dibuja el overlay de pulsación plano. El llamador lo
     * aplica sobre el clickable; sustituye a indication = null donde
     * el componente no dibuje su propio feedback.
     */
    @Composable
    fun Modifier.pressOverlay(source: InteractionSource): Modifier {
        val pressed by source.collectIsPressedAsState()
        return drawWithContent {
            drawContent()
            if (pressed) {
                drawRect(
                    color = Color.Black.copy(alpha = XauxaOpacity.Pressed),
                    size = size,
                )
            }
        }
    }
}
