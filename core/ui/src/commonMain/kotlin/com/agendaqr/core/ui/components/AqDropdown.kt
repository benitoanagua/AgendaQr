package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.Popup
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqLayout
import com.agendaqr.core.ui.theme.AqShadow
import com.agendaqr.core.ui.theme.AqSquircles
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
 * Posición y ancho replican Moon (`MoonDropdownAnchorPosition.bottom` por
 * defecto): centrada bajo el ancla con separación `x4s` (8 dp) y
 * `maxWidth = overlayWidth - |overlayWidth/2 - anchorCenterX| * 2 -
 * dropdownMargin.horizontal`; el contenido se ajusta a su ancho intrínseco
 * hasta ese tope y envuelve el texto al alcanzarlo.
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
    val (anchorPosition, anchorModifier) = rememberAqAnchor()
    val maxWidth = aqAnchorMaxWidthDp(anchorPosition.centerXPx)
    val density = LocalDensity.current
    val gapPx = with(density) { AqAnchorGap.toPx().toInt() }
    Box(modifier = modifier) {
        Box(modifier = anchorModifier) { anchor() }
        if (open) {
            Popup(
                popupPositionProvider = AqCenteredAnchorPositionProvider(
                    gapPx = gapPx,
                    below = true,
                ),
            ) {
                AqDropdownSurface(
                    maxWidth = maxWidth,
                    options = options,
                    selected = selected,
                    onOptionClick = onOptionClick,
                    content = content,
                )
            }
        }
    }
}

/**
 * Tarjeta del menú (fondo, radio, sombra, padding y opciones). Separada del
 * [Popup] para poder renderizarla inline en capturas de prueba: las ventanas
 * `Popup` no rasterizan bajo Robolectric.
 */
@Composable
internal fun AqDropdownSurface(
    maxWidth: Dp,
    options: List<String>,
    selected: Int,
    onOptionClick: ((Int) -> Unit)?,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = Modifier
            .widthIn(max = maxWidth)
            .aqWrapIntrinsicWidth()
            // `testTag` antes de `padding`: las bounds semánticas
            // del tag deben abarcar la tarjeta completa (como el
            // `Container` de Moon), no solo el contenido.
            .testTag("aq-dropdown-popup")
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
