package com.agendaqr.core.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.core.ui.theme.XauxaType

/**
 * Encabezado Xauxa (T9): todo título con semántica `heading()` usa la
 * familia display (Archivo, §12 del contrato visual) y los tokens
 * tipográficos del sistema.
 *
 * El tamaño sigue en `sp`: el escalado de fuente del sistema se respeta
 * (§11 accesibilidad).
 */
@Composable
fun XauxaHeading(
    text: String,
    modifier: Modifier = Modifier,
    size: TextUnit = XauxaType.Headline,
    fontWeight: FontWeight = FontWeight.Bold,
    color: Color = XauxaColor.TextPrimary,
) {
    Text(
        text = text,
        modifier = modifier.semantics { heading() },
        fontSize = size,
        fontFamily = XauxaType.FamilyDisplay,
        fontWeight = fontWeight,
        color = color,
    )
}
