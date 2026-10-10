package com.agendaqr.core.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.TextUnit
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.core.ui.theme.XauxaType

/**
 * Encabezado Xauxa : todo título con semántica `heading()` usa la
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

/**
 * Texto Xauxa : el cuerpo y la UI usan la familia de texto del
 * sistema (Roboto/SF, §12) con los tokens del design system. La capa
 * feature no importa androidx.compose.material3.Text: compone este (o
 * [XauxaHeading] para títulos).
 */
@Composable
fun XauxaText(
    text: String,
    modifier: Modifier = Modifier,
    size: TextUnit = XauxaType.Body,
    fontWeight: FontWeight? = null,
    color: Color = XauxaColor.TextPrimary,
    textAlign: TextAlign? = null,
) {
    Text(
        text = text,
        modifier = modifier,
        fontSize = size,
        fontFamily = XauxaType.FamilyUi,
        fontWeight = fontWeight,
        color = color,
        textAlign = textAlign,
    )
}
