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
import com.agendaqr.core.ui.theme.AqColor
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
    /** Acción principal fija inferior (p. ej. "Guardar"). */
    bottomAction: (@Composable () -> Unit)? = null,
    /** Estado/banner persistente a ancho completo bajo la barra. */
    statusContent: (@Composable () -> Unit)? = null,
    /** Host de notificaciones efímeras (p. ej. [AqToastHost]). */
    toastHost: (@Composable () -> Unit)? = null,
    scrollable: Boolean = false,
    content: @Composable ColumnScope.() -> Unit,
) {
    Box(
        modifier = modifier.fillMaxSize(),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
        ) {
            // Barra superior solo cuando hay algo que mostrar.
            if (title.isNotBlank() || onBack != null || actions.isNotEmpty() || overflowActions.isNotEmpty()) {
                AqAppBar(
                    title = title,
                    onBack = onBack,
                    backLabel = backLabel,
                    actions = actions,
                    overflowActions = overflowActions,
                    overflowLabel = overflowLabel,
                )
            }
            statusContent?.invoke()
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
                            vertical = AqSpacing.Sm,
                        ),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
                ) {
                    androidx.compose.foundation.layout.Row(
                        modifier = Modifier.fillMaxWidth(),
                    ) {
                        androidx.compose.foundation.layout.Spacer(modifier = Modifier.weight(1f))
                        androidx.compose.material3.HorizontalDivider(
                            modifier = Modifier.weight(0f),
                            color = AqColor.Divider,
                            thickness = AqMetrics.DividerHeight,
                        )
                    }
                    it()
                }
            }
        }
        // Toasts superpuestos sobre el contenido (último hijo del Box).
        toastHost?.invoke()
    }
}
