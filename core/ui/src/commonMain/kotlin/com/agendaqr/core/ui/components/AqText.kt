package com.agendaqr.core.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.core.ui.theme.AqTextRoles

/**
 * Texto: el cuerpo y la UI usan la familia de texto del
 * sistema con los tokens del design system. La capa feature no compone
 * `androidx.compose.material3.Text` directamente: usa este componente
 * (o [AqHeading] para títulos).
 *
 * - [semanticHeading]: declara el nodo como encabezado semántico
 *   (T-03: los encabezados de pantalla y de sección lo declaran
 *   explícitamente; el cuerpo nunca lo declara).
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
    /** Declara el nodo como encabezado semántico (solo encabezados reales). */
    semanticHeading: Boolean = false,
) {
    val role = AqTextRoles.of(size, heading = semanticHeading)
    val baseModifier = if (semanticHeading) Modifier.semantics { this[SemanticsProperties.Heading] = Unit } else Modifier
    Text(
        text = text,
        modifier = baseModifier.then(modifier),
        fontSize = size,
        lineHeight = role.lineHeight,
        fontFamily = AqType.FamilyUi,
        fontWeight = fontWeight ?: role.weight,
        letterSpacing = role.tracking,
        color = color,
        textAlign = textAlign,
        maxLines = maxLines,
        overflow = TextOverflow.Ellipsis,
    )
}
