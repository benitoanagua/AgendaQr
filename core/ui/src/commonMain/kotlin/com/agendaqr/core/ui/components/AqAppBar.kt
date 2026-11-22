package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.semantics
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.core.ui.theme.AqWeight

/**
 * Acción de la barra superior de la app.
 *
 * - [label]: etiqueta SIEMPRE visible (es la descripción accesible).
 * - [icon]: glifo opcional del mapeo de Polaris; si se omite, se pinta como
 *   acción de texto. Cuando hay icon y el label está en blanco, se renderiza
 *   solo-icono (área táctil 48 dp).
 */
data class AqAppBarAction(
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector? = null,
    val onClick: () -> Unit,
    val enabled: Boolean = true,
)

/**
 * Barra superior de la app sobre piezas Moon ([AqButton] + [AqText]).
 * Sustituye a [AqTopBar] y [AqAppBar].
 *
 * - [title]: título centrado (18 sp, semibold, semántica `heading` vía el
 *   rol de [AqText]).
 * - [onBack] / [backLabel]: flecha atrás; cuando [backLabel] está en blanco
 *   se renderiza solo-icono.
 * - [actions]: máx. 2 acciones visibles (icono + etiqueta o solo texto).
 * - [overflowActions] / [overflowLabel]: máx. 4 acciones adicionales. Se
 *   muestran en un menú desplegable ([AqDropdown]) anclado al botón "…".
 * - Edge-to-edge: aplica `statusBarsPadding` por sí misma para que el título
 *   nunca quede tras la barra de estado.
 * - Todas las acciones conservan el objetivo táctil de 48 dp (D-02).
 * - Todo el copy llega del llamador.
 */
@Composable
fun AqAppBar(
    title: String,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    /** copy accesible de la flecha atrás (copy del llamador). */
    backLabel: String = "",
    actions: List<AqAppBarAction> = emptyList(),
    /** Acciones adicionales del menú "…" (copy del llamador). */
    overflowActions: List<AqAppBarAction> = emptyList(),
    /** copy accesible del botón "…" (requerido con overflowActions). */
    overflowLabel: String = "",
) {
    require(actions.size <= 2) {
        "AqAppBar: máximo 2 acciones visibles; el resto va a overflowActions"
    }
    require(overflowActions.size <= 4) {
        "AqAppBar: máximo 4 acciones de overflow"
    }
    if (overflowActions.isNotEmpty()) {
        require(overflowLabel.isNotBlank()) {
            "AqAppBar: overflowActions necesita overflowLabel (copy del llamador)"
        }
    }
    val overflowOpen = remember { mutableStateOf(false) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .heightIn(min = AqMetrics.TopBarHeight)
            .background(AqColor.Surface),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AqSize.Xs),
    ) {
        if (onBack != null) {
            AqButton(
                label = backLabel,
                onClick = onBack,
                variant = AqButtonVariant.Text,
                size = AqButtonSize.Small,
                icon = AqIcons.Back,
                iconOnly = backLabel.isBlank(),
                modifier = Modifier.defaultMinSize(minHeight = AqMetrics.ControlMinSize),
            )
        }
        AqText(
            text = title,
            size = AqType.HeadingLg,
            fontWeight = AqWeight.SemiBold,
            color = AqColor.TextPrimary,
            modifier = Modifier.weight(1f, fill = false),
            maxLines = 1,
            semanticHeading = true,
        )
        actions.forEach { action ->
            AqButton(
                label = action.label,
                onClick = action.onClick,
                variant = AqButtonVariant.Text,
                size = AqButtonSize.Small,
                icon = action.icon,
                iconOnly = action.icon != null && action.label.isBlank(),
                enabled = action.enabled,
                modifier = Modifier.defaultMinSize(minHeight = AqMetrics.ControlMinSize),
            )
        }
        if (overflowActions.isNotEmpty()) {
            AqButton(
                label = overflowLabel,
                onClick = { overflowOpen.value = true },
                variant = AqButtonVariant.Text,
                size = AqButtonSize.Small,
                icon = AqIcons.More,
                iconOnly = overflowLabel.isBlank(),
                modifier = Modifier.defaultMinSize(minHeight = AqMetrics.ControlMinSize),
            )
        }
    }

    // Menú de overflow (desplegable bajo el botón "…").
    if (overflowOpen.value) {
        AqDropdown(
            open = true,
            options = overflowActions.map { it.label },
            onOptionClick = { index ->
                overflowActions[index].onClick()
                overflowOpen.value = false
            },
            anchor = {
                // El ancla ya se renderizó como el botón "…" arriba; aquí
                // solo se precisa el slot vacío para que AqDropdown pinte la
                // lista bajo la barra.
            },
            content = {},
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
