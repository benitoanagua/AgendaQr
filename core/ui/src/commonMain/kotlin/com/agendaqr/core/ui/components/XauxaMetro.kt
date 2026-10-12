package com.agendaqr.core.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.core.ui.theme.XauxaTextStyles
import com.agendaqr.core.ui.theme.XauxaType
import com.agendaqr.core.ui.theme.asTextOn

/**
 * Piezas de presentación de página y sección dentro de Xauxa (§12 V1.2).
 * El título de página vive en la barra superior ([XauxaTopBar]).
 */

/**
 * Encabezado de sección (§12 V1.2): texto del rol `titleSmall` de M3 en
 * color estándar, con semántica `heading()`. El acento de contexto solo
 * tiñe los encabezados de SU superficie (S06) cuando el llamador pasa un
 * [accent] legible como texto.
 */
@Composable
fun XauxaSectionHeader(
    text: String,
    modifier: Modifier = Modifier,
    accent: Color? = null,
) {
    val textColor = accent?.asTextOn(XauxaColor.Background, XauxaColor.BrandText)
        ?: XauxaColor.TextSecondary
    androidx.compose.material3.Text(
        text = text,
        modifier = modifier.semantics { heading() },
        style = XauxaTextStyles.SectionHeader,
        fontFamily = XauxaType.FamilyUi,
        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
        color = textColor,
    )
}

/**
 * Secciones con pestañas estándar (§12 V1.2, S06): `PrimaryScrollableTabRow`
 * con las secciones QR · Actividades · Comprobantes. La conmutación es
 * local: NO crea rutas ni navegación nueva (§3: los estados técnicos no
 * crean navegación artificial).
 *
 * Contrato §4.4 (tipografía al 100/150/200 %): las pestañas viven en una
 * fila DESPLAZABLE — con fuente grande las etiquetas no se comprimen ni se
 * recortan: la última pestaña se alcanza desplazando. El grupo declara
 * `selectableGroup()` y cada pestaña rol de pestaña con estado `selected`
 * y etiqueta de texto visible (§11).
 */
@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun XauxaPivot(
    sections: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    accent: Color? = null,
    content: @Composable (Int) -> Unit,
) {
    require(sections.isNotEmpty()) { "XauxaPivot necesita al menos una sección" }
    require(selectedIndex in sections.indices) { "XauxaPivot: índice fuera de rango" }
    // El acento de contexto solo es texto de pestaña si es legible
    // (>= 4.5:1); si no, cae al acento de marca legible.
    val selectedTextColor = accent?.asTextOn(XauxaColor.Background, XauxaColor.BrandText)
        ?: XauxaColor.BrandText
    androidx.compose.material3.PrimaryScrollableTabRow(
        selectedTabIndex = selectedIndex,
        modifier = modifier.fillMaxWidth(),
    ) {
        sections.forEachIndexed { index, title ->
            val selected = index == selectedIndex
            androidx.compose.material3.Tab(
                selected = selected,
                onClick = { onSelect(index) },
                text = {
                    androidx.compose.material3.Text(
                        text = title,
                        color = if (selected) selectedTextColor else XauxaColor.TextSecondary,
                        maxLines = 1,
                    )
                },
            )
        }
    }
    content(selectedIndex)
}
