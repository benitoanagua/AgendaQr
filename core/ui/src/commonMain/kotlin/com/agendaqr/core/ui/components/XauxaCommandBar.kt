package com.agendaqr.core.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.core.ui.theme.XauxaSpacing
import com.agendaqr.core.ui.theme.XauxaType

data class XauxaOverflowAction(
    val label: String,
    val onClick: () -> Unit,
)

/**
 * Xauxa checklist KMP §07 — command bar contextual (bottom bar + overflow).
 * Adaptado desde `xauxa-design-system/kotlin-compose/XauxaCommandBar.kt`:
 * mismos invariantes (cb1/cb2/cb4/cb5), pero el botón de overflow usa
 * [XauxaIconButton] con un glifo de texto ("⋮") en vez del ícono Material
 * "más" del ZIP original — este repo prohíbe los íconos extendidos de
 * Compose Material en producción (compuerta `verifyDesignSystemCompliance`;
 * el mismo patrón ya lo usa `XauxaToast` para su botón de descarte "×") — y
 * la barra fija elevación/tono a [XauxaSpacing.None] para cumplir la
 * invariante 03 (separación por borde, nunca sombra).
 *
 * - cb1: DÓNDE aparece es decisión del caller (pantallas de detalle/foco,
 *   nunca la lista principal) — este composable no se auto-inserta en
 *   ningún lado.
 * - cb2: como mucho 3 acciones visibles, forzado con `require` en vez de
 *   dejarlo a criterio de quien arma la pantalla — el resto va a
 *   `overflowActions`.
 * - cb4: NO reemplaza el botón de acción de tile (C12) por su cuenta. Si una
 *   pantalla de detalle tiene una acción primaria como tile y esto para las
 *   secundarias, esa convivencia se decide afuera.
 * - cb5: el botón de overflow lleva contentDescription explícito (no
 *   depende de que el glifo "se entienda"), y cada opción es un
 *   `DropdownMenuItem` real — foco de teclado y TalkBack funcionan gratis con
 *   estos composables. Queda pendiente el mismo chequeo con VoiceOver del
 *   lado iOS (igual que en el ZIP original).
 */
@Composable
fun XauxaCommandBar(
    visibleActions: List<@Composable RowScope.() -> Unit>,
    modifier: Modifier = Modifier,
    overflowActions: List<XauxaOverflowAction> = emptyList(),
) {
    require(visibleActions.size <= 3) {
        "XauxaCommandBar: máximo 3 acciones visibles (checklist cb2) — el resto va a overflowActions"
    }
    var overflowOpen by remember { mutableStateOf(false) }

    BottomAppBar(
        modifier = modifier,
        containerColor = XauxaColor.Surface,
        contentColor = XauxaColor.TextPrimary,
        tonalElevation = XauxaSpacing.None,
        actions = {
            visibleActions.forEach { action -> action() }
        },
        floatingActionButton = if (overflowActions.isEmpty()) {
            null
        } else {
            {
                XauxaIconButton(
                    contentDescription = "Más opciones",
                    onClick = { overflowOpen = true },
                ) {
                    Text("⋮", fontSize = XauxaType.Title, color = XauxaColor.TextPrimary)
                    DropdownMenu(
                        expanded = overflowOpen,
                        onDismissRequest = { overflowOpen = false },
                    ) {
                        overflowActions.forEach { action ->
                            DropdownMenuItem(
                                text = { Text(action.label) },
                                onClick = {
                                    overflowOpen = false
                                    action.onClick()
                                },
                            )
                        }
                    }
                }
            }
        },
    )
}
