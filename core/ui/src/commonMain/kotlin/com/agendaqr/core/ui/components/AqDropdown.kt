package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqSquircles
import com.agendaqr.core.ui.theme.AqTextStyles
import com.agendaqr.core.ui.theme.LocalAqColorScheme

/**
 * Menú desplegable anclado sobre [MoonDropdown] (`moon_design` 1.1.0,
 * `widgets/dropdown`). Lista de opciones que se despliega desde un
 * anclaje.
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
    if (!open) {
        anchor()
        return
    }
    val scheme = LocalAqColorScheme.current
    Column(
        modifier = modifier.fillMaxWidth(),
    ) {
        anchor()
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(AqColor.Surface, AqSquircles.SurfaceMd)
                .padding(vertical = AqSize.Xs),
            contentAlignment = Alignment.TopStart,
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AqSpacing.Sm),
            ) {
                options.forEachIndexed { index, label ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = AqSize.Xs, vertical = AqSize.X2s)
                            .background(
                                if (selected == index) scheme.selectionSurface else AqColor.Transparent,
                                AqSquircles.InteractiveXs,
                            ),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = label,
                            style = AqTextStyles.Body,
                            color = if (selected == index) AqColor.EmphasisFill else AqColor.TextPrimary,
                            modifier = Modifier
                                .weight(1f, fill = false)
                                .defaultMinSize(minHeight = AqMetrics.ControlMinSize)
                                .clickable(
                                    enabled = onOptionClick != null,
                                    role = Role.Button,
                                    onClick = { onOptionClick?.invoke(index) },
                                ),
                        )
                    }
                    if (index < options.size - 1) {
                        Spacer(modifier = Modifier.height(AqSize.X5s))
                    }
                }
                content()
            }
        }
    }
}
