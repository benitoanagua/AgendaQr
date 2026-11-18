package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSpacing

/** Contenedor semántico equivalente a `s-stack` (bloque): agrupa contenido con `gap` del sistema. */
@Composable
fun AqBlockStack(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(AqSpacing.Md),
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    padding: PaddingValues = PaddingValues(AqSpacing.None),
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier.padding(padding),
        verticalArrangement = verticalArrangement,
        horizontalAlignment = horizontalAlignment,
        content = content,
    )
}

/** Contenedor semántico equivalente a `s-inline-stack`. */
@Composable
fun AqInlineStack(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(AqSpacing.Md),
    verticalAlignment: Alignment.Vertical = Alignment.CenterVertically,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = verticalAlignment,
        content = content,
    )
}

/** Contenedor de superficie con fondo semántico del sistema. */
@Composable
fun AqBox(
    modifier: Modifier = Modifier,
    background: Color = AqColor.Surface,
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier.background(background),
        contentAlignment = contentAlignment,
        content = content,
    )
}


