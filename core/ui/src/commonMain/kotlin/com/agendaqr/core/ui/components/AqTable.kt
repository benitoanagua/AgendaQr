package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqSquircles
import com.agendaqr.core.ui.theme.AqTextStyles
import com.agendaqr.core.ui.theme.LocalAqColorScheme
import androidx.compose.ui.graphics.Color

/**
 * Tabla sobre [MoonTable] (`moon_design` 1.1.0, `widgets/table`).
 * Cabecera y filas con alineación de columnas y divisor inferior.
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
) {
    val scheme = LocalAqColorScheme.current
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        // Cabecera.
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(AqSize.Md)
                .background(AqColor.SurfaceContainerHigh, AqSquircles.SurfaceXs)
                .padding(horizontal = AqSpacing.Md),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            columns.forEach { column ->
                Text(
                    text = column.label,
                    style = AqTextStyles.Label.copy(
                        fontWeight = androidx.compose.ui.text.font.FontWeight.SemiBold,
                    ),
                    color = scheme.textSecondary,
                    modifier = Modifier.weight(1f, fill = false),
                )
            }
        }
        Spacer(modifier = Modifier.height(AqSize.X5s))
        // Filas.
        rows.forEachIndexed { index, row ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(AqSize.Lg)
                    .background(
                        if (index % 2 == 1) AqColor.SurfaceContainerHigh else AqColor.Transparent,
                        AqSquircles.SurfaceXs,
                    )
                    .padding(horizontal = AqSpacing.Md),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                columns.forEach { column ->
                    Text(
                        text = row.cells.getOrDefault(column.id, ""),
                        style = AqTextStyles.Body,
                        color = scheme.textPrimary,
                        modifier = Modifier.weight(1f, fill = false),
                    )
                }
            }
            if (index < rows.size - 1) {
                Spacer(modifier = Modifier.height(AqSize.X5s))
            }
        }
    }
}
