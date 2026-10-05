package com.agendaqr.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.Surface
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.core.ui.theme.XauxaMetrics
import com.agendaqr.core.ui.theme.XauxaSpacing
import com.agendaqr.core.ui.theme.XauxaType

/** Acción visible de la app bar inferior: icono + etiqueta SIEMPRE visible
 * (§11/M12: decisión de accesibilidad y desviación consciente de Metro —
 * el significado no depende solo de un símbolo). */
data class XauxaAppBarAction(
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit,
    /** true en la acción principal de la pantalla (M7/M10): se diferencia
     * como bloque sólido de acento. */
    val primary: Boolean = false,
)

/**
 * Barra de aplicación inferior (spec V1.1 §12, Navegación y chrome):
 * 2–4 acciones con icono + etiqueta visible, menú "…" para el resto
 * (reutiliza [XauxaOverflowAction] de [XauxaCommandBar]) y flecha atrás
 * opcional (requerida en iOS; en Android el Back del sistema se mantiene y
 * la flecha es redundante — decisión del caller, no del componente).
 *
 * Aloja la acción PRINCIPAL de la pantalla (bloque sólido de acento, M10):
 * al vivir en la barra nunca queda recortada al final del scroll y respeta
 * los insets de navegación/teclado (la barra se ancla al borde seguro).
 *
 * Sin sombra ni borde de reposo: la separación con el contenido es el
 * bloque de color de superficie (M9). Área táctil ≥ [XauxaMetrics.ControlMinSize].
 */
@Composable
fun XauxaAppBar(
    actions: List<XauxaAppBarAction>,
    modifier: Modifier = Modifier,
    overflowActions: List<XauxaOverflowAction> = emptyList(),
    onBack: (() -> Unit)? = null,
    backLabel: String = "Atrás",
) {
    require(actions.size <= 4) {
        "XauxaAppBar: 2–4 acciones visibles (spec §12); el resto va a overflowActions"
    }
    var overflowOpen by remember { mutableStateOf(false) }
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = XauxaColor.Surface2,
        shape = RectangleShape,
        tonalElevation = XauxaSpacing.None,
        shadowElevation = XauxaSpacing.None,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .defaultMinSize(minHeight = XauxaMetrics.AppBarHeight)
                .padding(horizontal = XauxaSpacing.Xs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (onBack != null) {
                // Flecha atrás (M7). La etiqueta accesible vive en el
                // nombre del rol; el glifo es parte del control etiquetado.
                val interaction = remember { MutableInteractionSource() }
                Row(
                    modifier = Modifier
                        .defaultMinSize(
                            minWidth = XauxaMetrics.ControlMinSize,
                            minHeight = XauxaMetrics.ControlMinSize,
                        )
                        .clickable(
                            interactionSource = interaction,
                            indication = null,
                            role = Role.Button,
                            onClick = onBack,
                        )
                        .focusable(interactionSource = interaction)
                        .xauxaFocusRing(interaction)
                        .padding(XauxaSpacing.Xs),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Xs),
                ) {
                    XauxaIcon(imageVector = XauxaIcons.Back, contentDescription = null)
                    Text(backLabel, fontSize = XauxaType.Caption, color = XauxaColor.TextPrimary, maxLines = 1)
                }
            }
            Row(
                modifier = Modifier.weight(1f),
                horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Xs, Alignment.End),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                actions.forEach { action -> AppBarItem(action) }
                if (overflowActions.isNotEmpty()) {
                    Box {
                        AppBarItem(
                            XauxaAppBarAction(
                                label = "Más",
                                icon = XauxaIcons.More,
                                onClick = { overflowOpen = true },
                            ),
                        )
                        DropdownMenu(
                            expanded = overflowOpen,
                            onDismissRequest = { overflowOpen = false },
                        ) {
                            overflowActions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option.label) },
                                    onClick = {
                                        overflowOpen = false
                                        option.onClick()
                                    },
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AppBarItem(action: XauxaAppBarAction) {
    val interaction = remember { MutableInteractionSource() }
    Column(
        modifier = Modifier
            .defaultMinSize(
                minWidth = XauxaMetrics.ControlMinSize,
                minHeight = XauxaMetrics.ControlMinSize,
            )
            .then(
                // Acción principal: bloque sólido de acento (M10); el resto
                // texto + icono sin caja (M10).
                if (action.primary) {
                    Modifier.background(XauxaColor.Brand, RectangleShape)
                } else {
                    Modifier
                },
            )
            .clickable(
                interactionSource = interaction,
                indication = null,
                role = Role.Button,
                onClick = action.onClick,
            )
            .focusable(interactionSource = interaction)
            .xauxaFocusRing(interaction)
            .padding(horizontal = XauxaSpacing.Sm, vertical = XauxaSpacing.Xs),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        val tint = if (action.primary) XauxaColor.OnBrand else XauxaColor.TextPrimary
        XauxaIcon(
            imageVector = action.icon,
            // La etiqueta visible (debajo) es el nombre del control; el
            // glifo no duplica anuncio (§11/M12).
            contentDescription = null,
            tint = tint,
        )
        Text(
            action.label,
            fontSize = XauxaType.Caption,
            color = tint,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            textAlign = TextAlign.Center,
        )
    }
}
