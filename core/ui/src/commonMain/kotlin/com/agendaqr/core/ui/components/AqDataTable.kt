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
