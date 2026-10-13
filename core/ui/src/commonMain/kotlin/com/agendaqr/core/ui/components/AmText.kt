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
import com.agendaqr.core.ui.theme.AmColor
import com.agendaqr.core.ui.theme.AmType

/**
 * Proporción display/titular: interlineado más prieto que el cuerpo,
 * pero siempre > 1.1× del tamaño (un titular no hereda el interlineado
 * del cuerpo).
 */
private const val HeadingLineHeightRatio = 1.25f

/**
 * Proporción de cuerpo/UI: coincide con los estilos canónicos
 * (Body 16/24). Fijarla explícitamente cierra la herencia del ambiente
 * para tamaños mayores.
 */
private const val BodyLineHeightRatio = 1.5f

/**
 * Encabezado: todo título con semántica `heading()` usa la
 * familia de sistema y los tokens tipográficos del sistema.
 *
 * El tamaño sigue en `sp`: el escalado de fuente del sistema se respeta.
 * El interlineado es PROPORCIONAL al tamaño (nunca el ambiente del
 * cuerpo): sin él, un titular a dos líneas se solapa en cuanto supera
 * 24 sp.
 */
@Composable
fun AmHeading(
    text: String,
    modifier: Modifier = Modifier,
    size: TextUnit = AmType.Headline,
    fontWeight: FontWeight = FontWeight.Bold,
    color: Color = AmColor.TextPrimary,
) {
    Text(
        text = text,
        modifier = modifier.semantics { heading() },
        fontSize = size,
        lineHeight = TextUnit(size.value * HeadingLineHeightRatio, TextUnitType.Sp),
        fontFamily = AmType.FamilyUi,
        fontWeight = fontWeight,
        color = color,
    )
}

/**
 * Texto: el cuerpo y la UI usan la familia de texto del
 * sistema con los tokens del design system. La capa feature no compone
 * `androidx.compose.material3.Text` directamente: usa este componente
 * (o [AmHeading] para títulos).
 */
@Composable
fun AmText(
    text: String,
    modifier: Modifier = Modifier,
    size: TextUnit = AmType.Body,
    fontWeight: FontWeight? = null,
    color: Color = AmColor.TextPrimary,
    textAlign: TextAlign? = null,
) {
    Text(
        text = text,
        modifier = modifier,
        fontSize = size,
        lineHeight = TextUnit(size.value * BodyLineHeightRatio, TextUnitType.Sp),
        fontFamily = AmType.FamilyUi,
        fontWeight = fontWeight,
        color = color,
        textAlign = textAlign,
    )
}
