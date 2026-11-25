package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.window.Popup
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqLayout
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqSquircles
import com.agendaqr.core.ui.theme.AqShadow
import com.agendaqr.core.ui.theme.aqShadow

/**
 * Menú desplegable anclado sobre [MoonDropdown] (`moon_design` 1.1.0,
 * `widgets/dropdown` + `dropdown_theme.dart`).
 *
 * Superficie flotante en [Popup] bajo el [anchor]: fondo `goku`→
 * [AqColor.Background], radio `interactiveMd`, padding `x5s` (4 dp),
 * margen `dropdownMargin` = `x4s` (8 dp), sombra `shadows.sm`, sin
 * transición (`transitionDuration: Duration.zero` en Moon). El contenido
 * típico son [AqMenuItem] (anatomía de Moon para opciones de menú).
 *
 * - [open]: controla la visibilidad.
 * - [options]: etiquetas de las opciones.
 * - [selected]: índice seleccionado.
 * - [onOptionClick]: invocada al elegir una opción.
 * - [anchor]: contenido al que se ancla el menú.
 * - [content]: contenido adicional del menú.
 * - Todo el copy llega del llamador.
 */
@Composable
fun AqDropdown(
    open: Boolean,
    options: List<String>,
    selected: Int = -1,
    onOptionClick: ((Int) -> Unit)? = null,
    anchor: @Composable () -> Unit,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(modifier = modifier) {
        anchor()
        if (open) {
            Popup(
                alignment = Alignment.TopStart,
                offset = IntOffset(0, with(androidx.compose.ui.platform.LocalDensity.current) {
                    (AqSize.X4s * 2).roundToPx()
                }),
            ) {
                Column(
                    modifier = Modifier
                        .aqShadow(AqShadow.Sm, AqSquircles.InteractiveMd)
                        .background(AqColor.Background, AqSquircles.InteractiveMd)
                        .padding(AqLayout.DropdownContentPadding),
                ) {
                    options.forEachIndexed { index, label ->
                        AqMenuItem(
                            title = label,
                            selected = selected == index,
                            enabled = onOptionClick != null,
                            onClick = { onOptionClick?.invoke(index) },
                        )
                    }
                    content()
                }
            }
        }
    }
}
