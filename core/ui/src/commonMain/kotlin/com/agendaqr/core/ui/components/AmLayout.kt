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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.agendaqr.core.ui.theme.AmColor
import com.agendaqr.core.ui.theme.AmMetrics
import com.agendaqr.core.ui.theme.AmSpacing

/** Contenedor semántico equivalente a `s-stack` (bloque): agrupa contenido con `gap` del sistema. */
@Composable
fun AmBlockStack(
    modifier: Modifier = Modifier,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(AmSpacing.Md),
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    padding: PaddingValues = PaddingValues(AmSpacing.None),
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
fun AmInlineStack(
    modifier: Modifier = Modifier,
    horizontalArrangement: Arrangement.Horizontal = Arrangement.spacedBy(AmSpacing.Md),
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
fun AmBox(
    modifier: Modifier = Modifier,
    background: Color = AmColor.Surface,
    contentAlignment: Alignment = Alignment.TopStart,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier.background(background),
        contentAlignment = contentAlignment,
        content = content,
    )
}

/** Separador semántico equivalente a `s-divider`, sobre el hairline canónico. */
@Composable
fun AmDivider(
    modifier: Modifier = Modifier,
    insetStart: Dp = AmSpacing.None,
) {
    AmHairline(modifier = modifier, insetStart = insetStart)
}

/** Separador vertical para barras de acciones/etiquetas. */
@Composable
fun AmVerticalDivider(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .widthIn(min = AmMetrics.DividerHeight)
            .heightIn(min = AmMetrics.ControlMinSize)
            .background(AmColor.Divider),
    )
}

/** Fila de contenido ancho completo con margen de pantalla por defecto. */
@Composable
fun AmPageBlock(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = AmSpacing.ScreenMargin),
        content = content,
    )
}

/** Ancho de contenido acotado: evita `fillMaxWidth` en composiciones internas. */
@Composable
fun AmContentWidth(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(modifier = modifier.widthIn(max = AmMetrics.ContentMaxWidth), content = content)
}
