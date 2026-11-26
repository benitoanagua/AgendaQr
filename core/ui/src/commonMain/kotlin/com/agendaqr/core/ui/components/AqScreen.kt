package com.agendaqr.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Dp
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqLayout
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSpacing

/**
 * Contenedor de pantalla sobre piezas Moon.
 * Sustituye a [AqScreenScaffold], [AqBottomActionDock] y la parte de
 * [AqStatusBanner] que ocupaba el slot `statusContent`.
 *
 * Estructura:
 * - Barra superior opcional ([AqAppBar]) cuando hay título, atrás o acciones.
 * - Contenido scrollable (o no) con el margen de pantalla canónico.
 * - Acción fija inferior (`bottomAction`) en un dock propio que respeta
 *   barras de navegación e IME (sin `ScaffoldDefaults` de Material).
 * - Host de toasts superpuesto (`toastHost`).
 * - Estado/banner persistente (`statusContent`) bajo la barra superior.
 *
 * Todo el copy y la anatomía los aporta el llamador; ningún valor visual
 * se define aquí fuera de los tokens.
 */
@Composable
fun AqScreen(
    title: String = "",
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    /** copy accesible de la flecha atrás (copy del llamador). */
    backLabel: String = "",
    actions: List<AqAppBarAction> = emptyList(),
    overflowActions: List<AqAppBarAction> = emptyList(),
    overflowLabel: String = "",
    /** Barra superior personalizada (p. ej. barra de selección por lote).
     *  Cuando se proporciona, se usa en lugar de [AqAppBar]. */
    topBar: (@Composable () -> Unit)? = null,
    /** Destinos de navegación raíz (pestañas inferiores). Cuando no está
     *  vacía, se renderiza una [AqBottomNavigation] inferior. */
    navDestinations: List<AqNavDestination> = emptyList(),
    navSelectedId: String? = null,
    onNavSelect: ((String) -> Unit)? = null,
    bottomAction: (@Composable () -> Unit)? = null,
    statusContent: (@Composable () -> Unit)? = null,
    toastHost: (@Composable () -> Unit)? = null,
    scrollable: Boolean = false,
    /** Hueco entre organismos consecutivos del contenido (D-12: 16 dp =
     *  [AqLayout.BetweenOrganisms]). Se aplica con
     *  [Arrangement.spacedBy] al Column de contenido. */
    contentSpacing: Dp = AqLayout.BetweenOrganisms,
    content: @Composable ColumnScope.() -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            // Barra superior: la personalizada tiene prioridad sobre la app bar.
            if (topBar != null) {
                topBar()
            } else if (title.isNotBlank() || onBack != null || actions.isNotEmpty() || overflowActions.isNotEmpty()) {
                AqAppBar(
                    title = title,
                    onBack = onBack,
                    backLabel = backLabel,
                    actions = actions,
                    overflowActions = overflowActions,
                    overflowLabel = overflowLabel,
                )
            }
            statusContent?.let { slot ->
                // En Flutter los hijos no-flex de una Column reciben alto no
                // acotado; Compose acota el maxHeight. Medir el slot con alto
                // infinito reproduce ese contrato: los banners (`AqAlert`,
                // `mainAxisSize.max` de Moon) se ajustan a su contenido en
                // lugar de llenar la pantalla.
                Layout(content = slot) { measurables, constraints ->
                    val placeable = measurables.first().measure(
                        constraints.copy(minHeight = 0, maxHeight = Constraints.Infinity),
                    )
                    layout(placeable.width, placeable.height) {
                        placeable.placeRelative(0, 0)
                    }
                }
            }
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .then(
                        if (scrollable) {
                            Modifier.verticalScroll(rememberScrollState())
                        } else {
                            Modifier
                        },
                    )
                    .padding(
                        horizontal = AqSpacing.ScreenMargin,
                        vertical = AqSpacing.ScreenMargin,
                    ),
                verticalArrangement = Arrangement.spacedBy(contentSpacing),
            ) {
                content()
            }
            // Dock inferior con la acción fija: respeta navigationBars + IME.
            bottomAction?.let {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AqColor.Surface)
                        .navigationBarsPadding()
                        .imePadding()
                        .padding(
                            horizontal = AqSpacing.ScreenMargin,
                            vertical = AqLayout.DockVertical,
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(AqLayout.InGroup),
                ) {
                    it()
                }
            }
            // Navegación raíz inferior (chrome dedicado, no selección segmentada).
            if (navDestinations.isNotEmpty()) {
                AqBottomNavigation(
                    destinations = navDestinations,
                    selectedId = navSelectedId,
                    onSelect = { id -> onNavSelect?.invoke(id) },
                )
            }
        }
        // Toasts superpuestos sobre el contenido (último hijo del Box).
        toastHost?.invoke()
    }
}

/** Acción principal fija inferior (dock de acción): usa [AqButton] variante
 *  primaria, ancho completo, respeto de navigationBars e IME. */
@Composable
fun AqDockAction(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    busyDescription: String? = null,
) {
    AqButton(
        label = label,
        onClick = onClick,
        modifier = modifier.fillMaxWidth(),
        enabled = enabled,
        isLoading = isLoading,
        busyDescription = busyDescription,
        variant = AqButtonVariant.Primary,
        size = AqButtonSize.Large,
    )
}
