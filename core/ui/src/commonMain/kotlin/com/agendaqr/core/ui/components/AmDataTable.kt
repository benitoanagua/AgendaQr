package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.agendaqr.core.ui.theme.AmColor
import com.agendaqr.core.ui.theme.AmMetrics
import com.agendaqr.core.ui.theme.AmSpacing
import com.agendaqr.core.ui.theme.AmType
import com.agendaqr.core.ui.theme.AmWeight

/** Modelo inmutable de una columna de la tabla de índices. */
data class AmTableColumn(
    val id: String,
    val label: String,
    val width: androidx.compose.ui.unit.Dp? = null,
    val weight: Float = 1f,
    val align: TextAlign = TextAlign.Start,
)

/** Modelo inmutable de una fila de la tabla de índices. */
data class AmTableRow(
    val id: String,
    val cells: Map<String, String>,
)

/**
 * Tabla de índices equivalente a `s-index-table`: cabecera con semántica
 * de encabezado, filas con hairlines y columnas flexibles que crecen en
 * altura al aumentar la fuente (layout estable al 200 %). El caller
 * aporta todo el texto.
 */
@Composable
fun AmIndexTable(
    columns: List<AmTableColumn>,
    rows: List<AmTableRow>,
    modifier: Modifier = Modifier,
    emptyMessage: String? = null,
) {
    require(columns.isNotEmpty()) { "AmIndexTable requiere al menos una columna" }
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(AmColor.SurfaceContainerLow),
        ) {
            columns.forEach { column ->
                Box(
                    modifier = Modifier
                        .then(column.width?.let { Modifier.width(it) } ?: Modifier.weight(column.weight))
                        .defaultMinSize(minHeight = AmMetrics.ControlMinSize)
                        .padding(horizontal = AmSpacing.Md, vertical = AmSpacing.Sm),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    AmText(
                        text = column.label,
                        size = AmType.TitleSmall,
                        fontWeight = AmWeight.SemiBold,
                        textAlign = column.align,
                    )
                }
            }
        }
        if (rows.isEmpty()) {
            emptyMessage?.let {
                Box(modifier = Modifier.padding(AmSpacing.Lg).fillMaxWidth()) {
                    AmText(text = it, size = AmType.Body, color = AmColor.TextSecondary)
                }
            }
        } else {
            rows.forEach { row ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    columns.forEach { column ->
                        Box(
                            modifier = Modifier
                                .then(column.width?.let { Modifier.width(it) } ?: Modifier.weight(column.weight))
                                .defaultMinSize(minHeight = AmMetrics.ControlMinSize)
                                .padding(horizontal = AmSpacing.Md, vertical = AmSpacing.Sm),
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            AmText(
                                text = row.cells[column.id].orEmpty(),
                                size = AmType.Body,
                                textAlign = column.align,
                            )
                        }
                    }
                }
                AmHairline()
            }
        }
    }
}
