package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqCorner
import com.agendaqr.core.ui.theme.AqLayout
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqTextStyles
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.core.ui.theme.AqSquircles

/**
 * Celda de lista sobre [MoonMenuItem] (`moon_design` 1.1.0,
 * `widgets/menu_item`). Patrón de composición del catálogo §4:
 * `AqMenuItem` + `AqAvatar` / `AqTag`.
 *
 * - [leading]: avatar o glifo a la izquierda.
 * - [title]: título (una línea).
 * - [subtitle]: segunda línea opcional (texto secundario).
 * - [trailing]: glifo, chevron o slot personalizado a la derecha.
 * - [selected]: estado de selección (fondo de selección).
 * - [enabled]: controla la interacción; cuando es falso no emite onClick.
 * - [showDivider]: divisor de 1 dp al final.
 * - Área táctil ≥ 48 dp (D-02); semántica de botón con rol.
 * - Todo el copy llega del llamador.
 *
 * Geometría Moon (§ MenuItem):
 * - Relleno: 12 dp en los 4 lados ([AqLayout.CellPadding]).
 * - Hueco entre bloques (leading / texto / trailing): 8 dp ([AqLayout.InGroup]).
 * - Hueco título-subtítulo: 4 dp ([AqSize.X5s]).
 * - Alto mínimo visual 40 dp; objetivo táctil 48 dp ([AqMetrics.ControlMinSize]).
 * - El bloque de texto se expande (weight(1f), fill = true); el trailing
 *   queda con wrapContentWidth (no Spacer con weight).
 */
@Composable
fun AqMenuItem(
    title: String,
    subtitle: String? = null,
    leading: (@Composable () -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
    onClick: (() -> Unit)? = null,
    /** Pulsación larga opcional (p. ej. entrar al modo selección). */
    onLongClick: (() -> Unit)? = null,
    /** Etiqueta accesible de la acción de pulsación larga. */
    onLongClickLabel: String? = null,
    selected: Boolean = false,
    enabled: Boolean = true,
    showDivider: Boolean = false,
    /** Líneas máximas del título (por defecto 1). */
    titleMaxLines: Int = 1,
    /** Etiqueta/metadato secundario opcional (p. ej. estado de una fila). */
    meta: (@Composable () -> Unit)? = null,
    modifier: Modifier = Modifier,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val pressed = interactionSource.collectIsPressedAsState().value
    val isSelected = selected
    val container = when {
        !enabled -> AqColor.Disabled
        selected -> AqColor.SelectionSurface
        pressed && enabled -> AqColor.SurfaceActive
        else -> AqColor.Surface
    }
    val contentColor = when {
        !enabled -> AqColor.OnDisabled
        selected -> AqColor.SelectionIcon
        else -> AqColor.TextPrimary
    }
    val subtitleColor = when {
        !enabled -> AqColor.OnDisabled
        else -> AqColor.TextSecondary
    }
    Column(
        modifier = modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = AqMetrics.ControlMinSize)
            .background(container, AqSquircles.InteractiveSm)
            .then(
                if (onLongClick != null) {
                    @OptIn(ExperimentalFoundationApi::class)
                    Modifier.combinedClickable(
                        role = Role.Button,
                        onClickLabel = title,
                        interactionSource = interactionSource,
                        indication = null,
                        enabled = enabled && onClick != null,
                        onLongClickLabel = onLongClickLabel,
                        onLongClick = onLongClick,
                        onClick = { onClick?.invoke() },
                    )
                } else {
                    Modifier.clickable(
                        role = Role.Button,
                        onClickLabel = title,
                        interactionSource = interactionSource,
                        indication = null,
                        enabled = enabled && onClick != null,
                        onClick = { onClick?.invoke() },
                    )
                },
            )
            .menuItemSemantics(
                desc = title + (subtitle?.let { " - $it" } ?: ""),
                isSelected = isSelected,
            ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AqLayout.CellPadding),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AqLayout.InGroup),
        ) {
            leading?.invoke()
            Column(
                modifier = Modifier.weight(1f, fill = true),
                verticalArrangement = Arrangement.spacedBy(AqSize.X5s),
            ) {
                Text(
                    text = title,
                    style = AqTextStyles.RowTitle,
                    color = contentColor,
                    maxLines = titleMaxLines,
                )
                subtitle?.let {
                    Text(
                        text = it,
                        style = AqTextStyles.BodySmall,
                        color = subtitleColor,
                        maxLines = 1,
                    )
                }
                meta?.let { slot ->
                    slot()
                }
            }
            trailing?.invoke()
        }
        if (showDivider) {
            androidx.compose.foundation.layout.Row(
                modifier = Modifier.fillMaxWidth(),
            ) {
                androidx.compose.foundation.layout.Spacer(modifier = Modifier.width(AqMetrics.ListDividerInset))
                HorizontalDivider(
                    modifier = Modifier.fillMaxWidth(),
                    color = AqColor.Divider,
                    thickness = AqMetrics.DividerHeight,
                )
            }
        }
    }
}

/** Semántica de una celda de menú: nombre accesible + estado seleccionado. */
private fun Modifier.menuItemSemantics(desc: String, isSelected: Boolean): Modifier =
    semantics {
        contentDescription = desc
        selected = isSelected
    }
