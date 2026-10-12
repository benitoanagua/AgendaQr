package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ListItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.core.ui.theme.XauxaShapes
import com.agendaqr.core.ui.theme.XauxaSpacing
import com.agendaqr.core.ui.theme.XauxaTextStyles


/**
 * Listas y etiquetas Xauxa (spec §12 V1.2): filas con `ListItem` de M3 y
 * badge semántico.
 */

/**
 * Etiqueta semántica de estado: texto con el color del tono (par
 * content/container garantizado por XauxaSchemeTest). No interactiva; el
 * estado se expresa con TEXTO (§11: nunca solo color).
 */
@Composable
fun XauxaBadge(
    text: String,
    modifier: Modifier = Modifier,
    tone: XauxaTone = XauxaTone.Neutral,
) {
    Box(
        modifier = modifier
            .background(
                if (tone == XauxaTone.Neutral) XauxaColor.Surface2 else tone.container(),
                XauxaShapes.ExtraSmall,
            )
            .padding(horizontal = XauxaSpacing.Sm, vertical = XauxaSpacing.Xs),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text,
            style = XauxaTextStyles.Support,
            color = if (tone == XauxaTone.Neutral) XauxaColor.TextPrimary else tone.content(),
        )
    }
}

/**
 * Fila de lista (§12 V1.2): `ListItem` de M3 con título, subtítulo, meta
 * en línea propia, acción final e identidad de contexto opcional.
 *
 * - La altura crece con el contenido y la escala de fuente; se prohíben
 *   alturas fijas que recorten (§8).
 * - [accent] es la identidad REAL del contexto (derivada del id, E-05):
 *   un punto de acento a la izquierda; nunca una barra ornamental.
 * - [selected] expone la selección en la semántica (§11).
 * - [trailing] son las acciones de fila: botones de icono, no enlaces de
 *   texto.
 */
@Composable
fun XauxaListRow(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    onClick: (() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    /** Identidad de contexto (E-05): punto de acento derivado del id. */
    accent: Color? = null,
    /** selección explícita con semántica `selected` (§11). */
    selected: Boolean = false,
    /** Metadato en línea PROPIA, bajo el subtítulo (p. ej. el badge de
     * sincronización): no compite por el ancho con la acción de la fila. */
    meta: (@Composable () -> Unit)? = null,
) {
    ListItem(
        modifier = modifier
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
        headlineContent = {
            Text(title, style = XauxaTextStyles.Body, color = XauxaColor.TextPrimary)
        },
        supportingContent = if (subtitle != null || meta != null) {
            {
                Column(
                    verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Xs),
                ) {
                    subtitle?.let {
                        Text(it, style = XauxaTextStyles.Caption, color = XauxaColor.TextSecondary)
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
                        .size(XauxaSpacing.Md)
                        .background(color, XauxaShapes.ExtraSmall),
                )
            }
        },
        trailingContent = trailing,
    )
}
