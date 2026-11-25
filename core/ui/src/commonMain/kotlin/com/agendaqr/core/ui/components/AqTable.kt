package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqSquircles
import com.agendaqr.core.ui.theme.AqLayout
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqType

/** Tamaño de fila de [AqTable] (Moon `MoonTableRowSize`). */
enum class AqTableRowSize { Xs, Sm, Md, Lg, Xl, X2l }

/**
 * Tabla sobre [MoonTable] (`moon_design` 1.1.0, `widgets/table` +
 * `theme/table/table_sizes.dart`/`table_colors.dart`).
 *
 * - [rowSize]: xs/sm/md/lg/xl/x2l (por defecto md, como Moon). Alturas de
 *   fila `sizes.xs..x2l` (24/32/40/48/56/64 dp), hueco entre filas
 *   [AqLayout.TableRowGap] (`sizes.x5s`), radios `interactiveXs/Sm/Md`
 *   de [AqCorner].
 * - Relleno horizontal de celda `sizes.x4s` en xs y `sizes.x3s` en el
 *   resto ([AqLayout.TableCellPaddingHorizontal*]).
 * - Textos: columnas `heading.text12`/`heading.textDefault` y celdas
 *   `body.text12`/`body.textDefault` según tamaño (14/12 dp, semibold en
 *   cabecera → [FontWeight.SemiBold]).
 * - Colores `textPrimary`→[AqColor.TextPrimary] e iconos
 *   `iconPrimary`→[AqColor.TextPrimary]; fondo de fila `goku`→
 *   [AqColor.SurfaceContainerLow].
 * - La cabecera usa las mismas columnas y métricas que las filas
 *   (`MoonTableHeader` comparte `rowHeight`).
 *
 * - [columns]: definición de columnas ([AqTableColumn] de [AqDataTable]).
 * - [rows]: filas de datos ([AqTableRow] de [AqDataTable]).
 * - Todo el copy llega del llamador.
 */
@Composable
fun AqTable(
    columns: List<AqTableColumn>,
    rows: List<AqTableRow>,
    modifier: Modifier = Modifier,
    rowSize: AqTableRowSize = AqTableRowSize.Md,
) {
    val rowHeight = when (rowSize) {
        AqTableRowSize.Xs -> AqSize.Xs
        AqTableRowSize.Sm -> AqSize.Sm
        AqTableRowSize.Md -> AqSize.Md
        AqTableRowSize.Lg -> AqSize.Lg
        AqTableRowSize.Xl -> AqSize.Xl
        AqTableRowSize.X2l -> AqSize.X2l
    }
    val rowShape = when (rowSize) {
        AqTableRowSize.Xs, AqTableRowSize.Sm -> AqSquircles.InteractiveXs
        AqTableRowSize.Md, AqTableRowSize.Lg -> AqSquircles.InteractiveSm
        else -> AqSquircles.InteractiveMd
    }
    val cellPadding = if (rowSize == AqTableRowSize.Xs) {
        AqLayout.TableCellPaddingHorizontalXs
    } else {
        AqLayout.TableCellPaddingHorizontal
    }
    val columnSize = if (rowSize == AqTableRowSize.Xs) AqType.BodyXs else AqType.BodySm
    val rowTextSize = columnSize

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AqLayout.TableRowGap),
    ) {
        // Cabecera (`MoonTableHeader`): mismo alto y métricas que la fila.
        AqTableRowContent(
            height = rowHeight,
            rowShape = rowShape,
            cellPadding = cellPadding,
            columns = columns,
            cells = columns.associate { it.id to it.label },
            textSize = columnSize,
            fontWeight = FontWeight.SemiBold,
            align = { it.align },
        )
        rows.forEach { row ->
            AqTableRowContent(
                height = rowHeight,
                rowShape = rowShape,
                cellPadding = cellPadding,
                columns = columns,
                cells = row.cells,
                textSize = rowTextSize,
                fontWeight = FontWeight.Normal,
                align = { it.align },
            )
        }
    }
}

@Composable
private fun AqTableRowContent(
    height: Dp,
    rowShape: androidx.compose.ui.graphics.Shape,
    cellPadding: Dp,
    columns: List<AqTableColumn>,
    cells: Map<String, String>,
    textSize: androidx.compose.ui.unit.TextUnit,
    fontWeight: FontWeight,
    align: (AqTableColumn) -> TextAlign,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(height)
            .background(AqColor.SurfaceContainerLow, rowShape)
            .padding(horizontal = cellPadding),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        columns.forEach { column ->
            val cellModifier = column.width?.let { Modifier.width(it) }
                ?: Modifier.weight(column.weight.coerceAtLeast(0f), fill = true)
            AqText(
                text = cells[column.id] ?: "",
                size = textSize,
                fontWeight = fontWeight,
                color = AqColor.TextPrimary,
                textAlign = align(column),
                maxLines = 1,
                modifier = cellModifier,
            )
        }
    }
}
