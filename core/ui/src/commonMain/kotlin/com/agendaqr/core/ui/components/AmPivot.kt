package com.agendaqr.core.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import com.agendaqr.core.ui.theme.AmColor
import com.agendaqr.core.ui.theme.AmTextStyles
import com.agendaqr.core.ui.theme.AmType
import com.agendaqr.core.ui.theme.asTextOn

/**
 * Piezas de presentación de página y sección de AgendaMobile. El título
 * de página vive en la barra superior ([AmTopBar]).
 */

/**
 * Encabezado de sección (rol titleSmall): texto estándar con semántica
 * `heading()`. El acento de contexto solo tiñe los encabezados de SU
 * superficie (S06) cuando el llamador pasa un [accent] legible como
 * texto.
 */
@Composable
fun AmSectionHeader(
    text: String,
    modifier: Modifier = Modifier,
    accent: Color? = null,
) {
    val textColor = accent?.asTextOn(AmColor.Background, AmColor.AccentText)
        ?: AmColor.TextSecondary
    Text(
        text = text,
        modifier = modifier.semantics { heading() },
        style = AmTextStyles.SectionHeader,
        fontFamily = AmType.FamilyUi,
        fontWeight = FontWeight.Medium,
        color = textColor,
    )
}

/**
 * Secciones con pestañas estándar (S06): `PrimaryScrollableTabRow` con
 * las secciones QR · Actividades · Comprobantes. La conmutación es
 * local: NO crea rutas ni navegación nueva (los estados técnicos no crean
 * navegación artificial).
 *
 * Con tipografía al 100/150/200 % las pestañas viven en una fila
 * DESPLAZABLE — con fuente grande las etiquetas no se comprimen ni se
 * recortan: la última pestaña se alcanza desplazando. El grupo declara
 * `selectableGroup()` (vía TabRow) y cada pestaña rol de pestaña con
 * estado `selected` y etiqueta de texto visible.
 */
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun AmPivot(
    sections: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    accent: Color? = null,
    content: @Composable (Int) -> Unit,
) {
    require(sections.isNotEmpty()) { "AmPivot necesita al menos una sección" }
    require(selectedIndex in sections.indices) { "AmPivot: índice fuera de rango" }
    // El acento de contexto solo es texto de pestaña si es legible
    // (>= 4.5:1); si no, cae al acento legible del sistema.
    val selectedTextColor = accent?.asTextOn(AmColor.Background, AmColor.AccentText)
        ?: AmColor.AccentText
    PrimaryScrollableTabRow(
        selectedTabIndex = selectedIndex,
        modifier = modifier.fillMaxWidth(),
    ) {
        sections.forEachIndexed { index, title ->
            val selected = index == selectedIndex
            Tab(
                selected = selected,
                onClick = { onSelect(index) },
                text = {
                    Text(
                        text = title,
                        color = if (selected) selectedTextColor else AmColor.TextSecondary,
                        maxLines = 1,
                    )
                },
            )
        }
    }
    content(selectedIndex)
}
