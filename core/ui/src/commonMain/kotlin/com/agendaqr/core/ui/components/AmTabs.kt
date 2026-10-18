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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import com.agendaqr.core.ui.theme.AmColor
import com.agendaqr.core.ui.theme.AmMetrics
import com.agendaqr.core.ui.theme.AmShapes
import com.agendaqr.core.ui.theme.AmTextStyles
import com.agendaqr.core.ui.theme.AmType
import com.agendaqr.core.ui.theme.asTextOn

/**
 * Piezas de presentación de página y sección. El título
 * de página vive en la barra superior ([AmTopBar]).
 */

/**
 * Cabecera de grupo de celdas con altura mínima de 45 dp y semántica
 * `heading()`. El acento de contexto solo tiñe el texto cuando es legible.
 */
@Composable
fun AmSectionHeader(
    text: String,
    modifier: Modifier = Modifier,
    accent: Color? = null,
) {
    val textColor = accent?.asTextOn(AmColor.SurfaceSecondary, AmColor.AccentText)
        ?: AmColor.TextSecondary
    androidx.compose.foundation.layout.Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(min = AmMetrics.SectionHeaderHeight)
            .background(AmColor.SurfaceSecondary)
            .padding(horizontal = AmMetrics.ListDividerInset),
        contentAlignment = androidx.compose.ui.Alignment.CenterStart,
    ) {
        Text(
            text = text,
            modifier = Modifier.semantics { heading() },
            style = AmTextStyles.SectionHeader,
            fontFamily = AmType.FamilyUi,
            fontWeight = FontWeight.Normal,
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
fun AmTabs(
    sections: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    accent: Color? = null,
    content: @Composable (Int) -> Unit,
) {
    require(sections.isNotEmpty()) { "AmTabs necesita al menos una sección" }
    require(selectedIndex in sections.indices) { "AmTabs: índice fuera de rango" }
    // El acento de contexto solo es texto de pestaña si es legible
    // (>= 4.5:1); si no, cae al acento legible del sistema.
    val selectedTextColor = accent?.asTextOn(AmColor.Background, AmColor.AccentText)
        ?: AmColor.AccentText
    PrimaryScrollableTabRow(
        selectedTabIndex = selectedIndex,
        modifier = modifier.fillMaxWidth(),
        containerColor = AmColor.Transparent,
        contentColor = AmColor.TextPrimary,
        // Indicador de acento de la referencia; hairline inferior.
        indicator = {
            androidx.compose.material3.TabRowDefaults.PrimaryIndicator(
                modifier = Modifier.tabIndicatorOffset(selectedIndex),
                width = AmMetrics.TabIndicatorWidth,
                height = AmMetrics.TabIndicatorHeight,
                shape = AmShapes.ExtraSmall,
                color = AmColor.Accent,
            )
        },
        divider = {
            androidx.compose.material3.HorizontalDivider(
                thickness = AmMetrics.DividerHeight,
                color = AmColor.Divider,
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
                        style = AmTextStyles.Body,
                        color = if (selected) selectedTextColor else AmColor.TextPrimary,
                        maxLines = 1,
                    )
                },
            )
        }
    }
    content(selectedIndex)
}
