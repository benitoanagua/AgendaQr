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
import androidx.compose.ui.unit.TextUnitType
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.core.ui.theme.XauxaType

/**
 * Proporción display/titular: interlineado más prieto que el cuerpo,
 * pero siempre > 1.1× del tamaño (un titular no hereda el interlineado
 * del cuerpo).
 */
private const val HeadingLineHeightRatio = 1.25f

/**
 * Proporcion de cuerpo/UI: coincide con los estilos canonicos
 * (Body 16/24). Fijarla explicita cierra la herencia del ambiente para
 * tamanos mayores (D1: todo tamano > 24 sp solapaba).
 */
private const val BodyLineHeightRatio = 1.5f

/**
 * Encabezado Xauxa: todo título con semántica `heading()` usa la familia
 * de sistema (§12 V1.2) y los tokens tipográficos del sistema.
 *
 * El tamaño sigue en `sp`: el escalado de fuente del sistema se respeta
 * (§11 accesibilidad). El interlineado es PROPORCIONAL al tamaño (nunca
 * el ambiente del cuerpo): sin él, un titular a dos líneas se solapa en
 * cuanto supera 24 sp.
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
        lineHeight = TextUnit(size.value * HeadingLineHeightRatio, TextUnitType.Sp),
        fontFamily = XauxaType.FamilyUi,
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
        lineHeight = TextUnit(size.value * BodyLineHeightRatio, TextUnitType.Sp),
        fontFamily = XauxaType.FamilyUi,
        fontWeight = fontWeight,
        color = color,
        textAlign = textAlign,
    )
}
