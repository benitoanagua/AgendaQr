package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import com.agendaqr.core.ui.theme.AqWeight
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqShapes
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqTextStyles
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.core.ui.theme.asTextOn

/**
 * Piezas de presentación de página y sección. El título
 * de página vive en la barra superior ([AqTopBar]).
 */

/**
 * Cabecera de grupo de celdas con altura mínima de 45 dp y semántica
 * `heading()`. El acento de contexto solo tiñe el texto cuando es legible.
 */
@Composable
fun AqSectionHeader(
    text: String,
    modifier: Modifier = Modifier,
    accent: Color? = null,
) {
    val textColor = accent?.asTextOn(AqColor.SurfaceSecondary, AqColor.EmphasisText)
        ?: AqColor.TextSecondary
    androidx.compose.foundation.layout.Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = AqMetrics.SectionHeaderHeight)
            .background(AqColor.SurfaceSecondary)
            .padding(horizontal = AqMetrics.ListDividerInset),
        contentAlignment = androidx.compose.ui.Alignment.CenterStart,
    ) {
        Text(
            text = text,
            modifier = Modifier.semantics { heading() },
            style = AqTextStyles.SectionHeader,
            fontFamily = AqType.FamilyUi,
            fontWeight = AqWeight.Regular,
            color = textColor,
        )
    }
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
fun AqTabs(
    sections: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    accent: Color? = null,
    content: @Composable (Int) -> Unit,
) {
    require(sections.isNotEmpty()) { "AqTabs necesita al menos una sección" }
    require(selectedIndex in sections.indices) { "AqTabs: índice fuera de rango" }
    // El acento de contexto solo es texto de pestaña si es legible
    // (>= 4.5:1); si no, cae al acento legible del sistema.
    val selectedTextColor = accent?.asTextOn(AqColor.Background, AqColor.EmphasisText)
        ?: AqColor.EmphasisText
    PrimaryScrollableTabRow(
        selectedTabIndex = selectedIndex,
        modifier = modifier.fillMaxWidth(),
        containerColor = AqColor.Transparent,
        contentColor = AqColor.TextPrimary,
        // Sin sangría propia: la superficie ya aplica su margen de contenido.
        edgePadding = AqSpacing.None,
        // Indicador de acento de la referencia; hairline inferior.
        indicator = {
            androidx.compose.material3.TabRowDefaults.PrimaryIndicator(
                modifier = Modifier.tabIndicatorOffset(selectedIndex),
                width = AqMetrics.TabIndicatorWidth,
                height = AqMetrics.TabIndicatorHeight,
                shape = AqShapes.ExtraSmall,
                color = AqColor.EmphasisBorder,
            )
        },
        divider = {
            androidx.compose.material3.HorizontalDivider(
                thickness = AqMetrics.DividerHeight,
                color = AqColor.Divider,
            )
        },
    ) {
        sections.forEachIndexed { index, title ->
            val selected = index == selectedIndex
            Tab(
                selected = selected,
                onClick = { onSelect(index) },
                text = {
                    Text(
                        text = title,
                        style = AqTextStyles.Body,
                        color = if (selected) selectedTextColor else AqColor.TextPrimary,
                        maxLines = 1,
                    )
                },
            )
        }
    }
    content(selectedIndex)
}
