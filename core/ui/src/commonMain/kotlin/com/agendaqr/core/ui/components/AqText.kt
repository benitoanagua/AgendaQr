package com.agendaqr.core.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import com.agendaqr.core.ui.theme.AqWeight
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqType

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
fun AqHeading(
    text: String,
    modifier: Modifier = Modifier,
    size: TextUnit = AqType.Headline,
    fontWeight: FontWeight = AqWeight.Bold,
    color: Color = AqColor.TextPrimary,
) {
    Text(
        text = text,
        modifier = modifier.semantics { heading() },
        fontSize = size,
        lineHeight = TextUnit(size.value * AqType.HeadingLineHeightRatio, TextUnitType.Sp),
        fontFamily = AqType.FamilyUi,
        fontWeight = fontWeight,
        color = color,
    )
}

/**
 * Texto: el cuerpo y la UI usan la familia de texto del
 * sistema con los tokens del design system. La capa feature no compone
 * `androidx.compose.material3.Text` directamente: usa este componente
 * (o [AqHeading] para títulos).
 */
@Composable
fun AqText(
    text: String,
    modifier: Modifier = Modifier,
    size: TextUnit = AqType.Body,
    fontWeight: FontWeight? = null,
    color: Color = AqColor.TextPrimary,
    textAlign: TextAlign? = null,
    /** Límite de líneas con elipsis (p. ej. contenido largo plegado). */
    maxLines: Int = Int.MAX_VALUE,
) {
    Text(
        text = text,
        modifier = modifier,
        fontSize = size,
        lineHeight = TextUnit(size.value * AqType.BodyLineHeightRatio, TextUnitType.Sp),
        fontFamily = AqType.FamilyUi,
        fontWeight = fontWeight,
        color = color,
        textAlign = textAlign,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
    )
}
