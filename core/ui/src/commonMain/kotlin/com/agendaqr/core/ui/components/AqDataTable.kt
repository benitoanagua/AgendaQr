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
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.core.ui.theme.AqWeight

/** Modelo inmutable de una columna de la tabla de índices. */
data class AqTableColumn(
    val id: String,
    val label: String,
    val width: androidx.compose.ui.unit.Dp? = null,
    val weight: Float = 1f,
    val align: TextAlign = TextAlign.Start,
)

/** Modelo inmutable de una fila de la tabla de índices. */
data class AqTableRow(
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
fun AqIndexTable(
    columns: List<AqTableColumn>,
    rows: List<AqTableRow>,
    modifier: Modifier = Modifier,
    emptyMessage: String? = null,
) {
    require(columns.isNotEmpty()) { "AqIndexTable requiere al menos una columna" }
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(AqColor.SurfaceContainerLow),
        ) {
            columns.forEach { column ->
                Box(
                    modifier = Modifier
                        .then(column.width?.let { Modifier.width(it) } ?: Modifier.weight(column.weight))
                        .defaultMinSize(minHeight = AqMetrics.ControlMinSize)
                        .padding(horizontal = AqSpacing.Md, vertical = AqSpacing.Sm),
                    contentAlignment = Alignment.CenterStart,
                ) {
                    AqText(
                        text = column.label,
                        size = AqType.TitleSmall,
                        fontWeight = AqWeight.SemiBold,
                        textAlign = column.align,
                    )
                }
            }
        }
        if (rows.isEmpty()) {
            emptyMessage?.let {
                Box(modifier = Modifier.padding(AqSpacing.Lg).fillMaxWidth()) {
                    AqText(text = it, size = AqType.Body, color = AqColor.TextSecondary)
                }
            }
        } else {
            rows.forEach { row ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    columns.forEach { column ->
                        Box(
                            modifier = Modifier
                                .then(column.width?.let { Modifier.width(it) } ?: Modifier.weight(column.weight))
                                .defaultMinSize(minHeight = AqMetrics.ControlMinSize)
                                .padding(horizontal = AqSpacing.Md, vertical = AqSpacing.Sm),
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            AqText(
                                text = row.cells[column.id].orEmpty(),
                                size = AqType.Body,
                                textAlign = column.align,
                            )
                        }
                    }
                }
                AqHairline()
            }
        }
    }
}
