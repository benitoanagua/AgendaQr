package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import com.agendaqr.core.ui.theme.AmColor
import com.agendaqr.core.ui.theme.AmMetrics
import com.agendaqr.core.ui.theme.AmShapes
import com.agendaqr.core.ui.theme.AmSpacing
import com.agendaqr.core.ui.theme.AmTextStyles

/**
 * Celdas y etiquetas con patrones Ant Design Mobile: la celda (`AmListRow`) es la unidad de
 * las listas de producto — patrón de lista móvil con divisores opt-in y
 * metadatos en línea propia.
 */

/**
 * Etiqueta semántica de estado (badge): texto con el par del tono
 * (content/container garantizado por AmSchemeTest), radio compacto. No
 * interactiva; el estado se expresa con TEXTO (nunca solo color).
 */
@Composable
fun AmBadge(
    text: String,
    modifier: Modifier = Modifier,
    tone: AmTone = AmTone.Neutral,
) {
    Box(
        modifier = modifier
            .background(
                if (tone == AmTone.Neutral) AmColor.SurfaceSecondary else tone.container(),
                AmShapes.Pill,
            )
            .padding(horizontal = AmSpacing.Sm, vertical = AmSpacing.Xs),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text,
            style = AmTextStyles.Support,
            color = if (tone == AmTone.Neutral) AmColor.TextPrimary else tone.content(),
        )
    }
}

/**
 * Celda de lista: título, subtítulo, metadatos en línea propia, acción
 * final e identidad de contexto opcional — el patrón de las pantallas de
 * listas de Agenda QR.
 *
 * - La altura crece con el contenido y la escala de fuente; se prohíben
 *   alturas fijas que recorten.
 * - [accent] es la identidad REAL del contexto (derivada del id): un
 *   punto de acento a la izquierda; nunca una barra ornamental.
 * - [selected] expone la selección en la semántica (nunca solo color).
 * - [trailing] son las acciones de fila: botones de icono, no enlaces de
 *   texto.
 * - [showDivider] añade el separador inferior de la lista agrupada; lo
 *   activa la pantalla que agrupa celdas contiguas.
 */
@Composable
fun AmListRow(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    onClick: (() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    /** Identidad de contexto: punto de acento derivado del id. */
    accent: Color? = null,
    /** selección explícita con semántica `selected`. */
    selected: Boolean = false,
    /** Metadato en línea PROPIA, bajo el subtítulo (p. ej. el badge de
     * sincronización): no compite por el ancho con la acción de la fila. */
    meta: (@Composable () -> Unit)? = null,
    /** Separador inferior de la lista agrupada. */
    showDivider: Boolean = false,
) {
    Column(modifier = modifier) {
        ListItem(
            modifier = Modifier
                .then(
                    if (onClick != null) {
                        Modifier.clickable(role = Role.Button, onClick = onClick)
                    } else {
                        Modifier
                    },
                )
                .then(
                    if (selected) Modifier.semantics { this.selected = true } else Modifier,
                ),
            colors = ListItemDefaults.colors(containerColor = AmColor.Transparent),
            headlineContent = {
                Text(title, style = AmTextStyles.Body, color = AmColor.TextPrimary)
            },
            supportingContent = if (subtitle != null || meta != null) {
                {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(AmSpacing.Xs),
                    ) {
                        subtitle?.let {
                            Text(it, style = AmTextStyles.Caption, color = AmColor.TextSecondary)
                        }
                        meta?.invoke()
                    }
                }
            } else {
                null
            },
            leadingContent = accent?.let { color ->
                {
                    Box(
                        modifier = Modifier
                            .size(AmSpacing.Md)
                            .background(color, AmShapes.Pill),
                    )
                }
            },
            trailingContent = trailing,
        )
        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(start = AmSpacing.Lg),
                thickness = AmMetrics.Border,
                color = AmColor.Divider,
            )
        }
    }
}
