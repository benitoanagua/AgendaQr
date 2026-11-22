package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import com.agendaqr.core.ui.theme.AqAlpha
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqTextStyles
import com.agendaqr.core.ui.theme.asTextOn
import com.agendaqr.core.ui.theme.AqCorner
import com.agendaqr.core.ui.theme.AqSquircles

/**
 * Piezas de presentación de página y sección. El título
 * de página vive en la barra superior ([AqTopBar]).
 */

/**
 * Cabecera de grupo de celdas con semántica `heading()`. Título tipográfico
 * claro sin fondo rectangular decorativo; el acento de contexto solo tiñe
 * el texto cuando es legible. La separación de sección la aporta el
 * llamador (p. ej. [AqSection] o un divisor), nunca un contenedor de fondo.
 */
@Composable
fun AqSectionHeader(
    text: String,
    modifier: Modifier = Modifier,
    accent: Color? = null,
    /** Sangría alineada a las filas de lista; false en contextos sin
     *  lista (p. ej. [AqFormSection]). */
    inset: Boolean = true,
) {
    val textColor = accent?.asTextOn(AqColor.Surface, AqColor.EmphasisText)
        ?: AqColor.TextSecondary
    Text(
        text = text,
        modifier = modifier
            .fillMaxWidth()
            .then(
                if (inset) {
                    Modifier.padding(horizontal = AqMetrics.ListDividerInset)
                } else {
                    Modifier
                },
            )
            .semantics { heading() },
        style = AqTextStyles.SectionHeader,
        color = textColor,
    )
}

/**
 * Pestaña de [AqTabs]: etiqueta visible SIEMPRE (es la señal de texto) y
 * conteo opcional en badge — el contenido de la pestaña (p. ej. "QR · 0")
 * vive en la pestaña, nunca como texto suelto bajo la barra.
 */
data class AqTab(
    val label: String,
    /** Glifo opcional (p. ej. navegación raíz con icono + etiqueta). */
    val icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    /** Conteo real junto a la etiqueta; null = sin badge. */
    val count: Int? = null,
)

/**
 * Tabs de navegación entre vistas hermanas (S06): misma pista y misma
 * píldora de selección que [AqSegmentedControl] — UN solo lenguaje de
 * selección en el sistema. Tabs se usan para CONMUTAR contenido de una
 * misma superficie (QR · Actividades · Comprobantes); el segmentado se
 * usa para elegir una opción excluyente dentro de un formulario o filtro.
 *
 * La fila es DESPLAZABLE: con fuente grande las etiquetas no se comprimen
 * ni se recortan. Cada pestaña conserva el objetivo táctil de 48 dp y la
 * semántica `selectableGroup` + rol [Role.Tab] + estado `selected`.
 * [accent] tiñe la pestaña activa solo si es legible como texto (>= 4.5:1);
 * si no, cae al acento legible del sistema.
 */
@Composable
fun AqTabs(
    tabs: List<AqTab>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
    accent: Color? = null,
    /** Cuando falso, las pestañas se muestran deshabilitadas y no emiten
     *  onSelect. */
    enabled: Boolean = true,
    content: @Composable (Int) -> Unit,
) {
    require(tabs.isNotEmpty()) { "AqTabs necesita al menos una pestaña" }
    require(selectedIndex in tabs.indices) { "AqTabs: índice fuera de rango" }
    // El acento de contexto solo es texto de pestaña si es legible
    // (>= 4.5:1); si no, cae al acento legible del sistema.
    val selectedTextColor = accent?.asTextOn(AqColor.Background, AqColor.EmphasisText)
        ?: AqColor.EmphasisText
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .background(AqColor.SurfaceSecondary, AqSquircles.InteractiveSm)
            .selectableGroup()
            .padding(AqSpacing.Xs),
        horizontalArrangement = Arrangement.spacedBy(AqSpacing.Xs),
    ) {
        tabs.forEachIndexed { index, tab ->
            val selected = index == selectedIndex
            val textColor = when {
                !enabled -> AqColor.OnDisabled
                selected -> selectedTextColor
                else -> AqColor.TextSecondary
            }
            Box(
                modifier = Modifier
                    .then(
                        if (selected && enabled) {
                            Modifier.background(AqColor.SelectionSurface, AqSquircles.InteractiveSm)
                                .border(AqMetrics.Border, AqColor.Border, AqSquircles.InteractiveSm)
                        } else {
                            Modifier
                        },
                    )
                    .selectable(
                        selected = selected,
                        role = Role.Tab,
                        enabled = enabled,
                        onClick = { if (enabled) onSelect(index) },
                    )
                    .defaultMinSize(minHeight = AqMetrics.ControlMinSize)
                    .padding(horizontal = AqSpacing.Lg),
                contentAlignment = Alignment.Center,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AqSpacing.Xs),
                ) {
                    Text(
                        tab.label,
                        style = AqTextStyles.Label,
                        color = textColor,
                        maxLines = 1,
                    )
                    tab.count?.takeIf { it != 0 }?.let { count ->
                        Box(
                            modifier = Modifier
                                .background(
                                    if (selected && enabled) {
                                        selectedTextColor.copy(alpha = AqAlpha.SelectedOnTone)
                                    } else {
                                        AqColor.SurfaceContainerHighest
                                    },
                                    AqSquircles.InteractiveXs,
                                )
                                .padding(horizontal = AqSpacing.Xs),
                        ) {
                            Text(
                                count.toString(),
                                style = AqTextStyles.Tag,
                                color = textColor,
                            )
                        }
                    }
                }
            }
        }
    }
    content(selectedIndex)
}
