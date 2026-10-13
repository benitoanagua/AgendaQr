package com.agendaqr.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.union
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.theme.AmMetrics
import com.agendaqr.core.ui.theme.AmSpacing
import com.agendaqr.core.ui.theme.amWindowWidthSizeClass

/**
 * Contenedor de pantalla: `Scaffold` de Material 3 con
 * `contentWindowInsets` (nada manual de statusBars/navigationBars), IME
 * consumido para pantallas con campos, chrome estándar y adaptación de
 * ancho por `WindowSizeClass` (medium/expanded centra el contenido con
 * `ContentMaxWidth` y usa `AmNavRail`).
 *
 * - [topBar]: normalmente [AmTopBar] (título + flecha atrás + acciones).
 * - [navDestinations]: la navegación raíz (Inicio · Buscar · Actividades
 *   · Contextos). Solo las superficies raíz la declaran, y su pantalla
 *   decide cuándo está en su lista raíz (no en detalle/edición/import).
 *   Compacto la pinta como [AmNavBar] abajo; expandido como
 *   [AmNavRail] lateral.
 * - [bottomAction]: botón principal FIJO abajo solo cuando existe una
 *   acción real de guardado. Vive en el slot bottomBar, fuera de los
 *   insets de contenido: la acción aplica `navigationBarsPadding()` +
 *   `imePadding()` por su cuenta para no quedar bajo las barras ni bajo
 *   el teclado.
 * - [scrollable]: scroll del contenido para pantallas sin LazyColumn
 *   propia.
 * - [statusContent]: host de estados persistentes, a ancho completo BAJO
 *   la barra superior, dentro del Scaffold.
 */
@Composable
fun AmScreenScaffold(
    modifier: Modifier = Modifier,
    topBar: (@Composable () -> Unit)? = null,
    navDestinations: List<AmNavDestination> = emptyList(),
    navSelectedId: String? = null,
    onNavSelect: (String) -> Unit = {},
    snackbarHost: (@Composable () -> Unit)? = null,
    floatingActionButton: (@Composable () -> Unit)? = null,
    /** Botón principal fijo abajo (acción real de guardado). */
    bottomAction: (@Composable () -> Unit)? = null,
    /** Host de estados persistentes: a ancho completo, bajo la barra. */
    statusContent: (@Composable () -> Unit)? = null,
    scrollable: Boolean = false,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(AmSpacing.Lg),
    contentWindowInsets: WindowInsets = ScaffoldDefaults.contentWindowInsets,
    content: @Composable ColumnScope.() -> Unit,
) {
    val width = amWindowWidthSizeClass()
    val expanded = width != WindowWidthSizeClass.Compact
    // IME consumido siempre: con el teclado cerrado el inset es 0 y no
    // afecta; abierto, el contenido respeta los campos.
    val effectiveInsets = contentWindowInsets.union(WindowInsets.ime)

    val scaffoldContent: @Composable (PaddingValues) -> Unit = { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Estados persistentes del host: a ancho completo, bajo la
            // barra superior (sin el margen del contenido).
            statusContent?.invoke()
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (expanded) {
                            Modifier.widthIn(max = AmMetrics.ContentMaxWidth)
                        } else {
                            Modifier
                        },
                    )
                    .weight(1f)
                    .then(if (scrollable) Modifier.verticalScroll(rememberScrollState()) else Modifier)
                    .padding(AmSpacing.ScreenMargin),
                horizontalAlignment = horizontalAlignment,
                verticalArrangement = verticalArrangement,
            ) {
                content()
            }
        }
    }

    if (navDestinations.isNotEmpty() && expanded) {
        // Expandido: riel lateral + contenido centrado.
        Row(modifier = modifier.fillMaxSize()) {
            AmNavRail(
                destinations = navDestinations,
                selectedId = navSelectedId.orEmpty(),
                onSelect = onNavSelect,
            )
            Box(modifier = Modifier.weight(1f).fillMaxSize()) {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    topBar = { topBar?.invoke() },
                    snackbarHost = { snackbarHost?.invoke() },
                    floatingActionButton = { floatingActionButton?.invoke() },
                    bottomBar = { bottomAction?.invoke() },
                    contentWindowInsets = effectiveInsets,
                ) { innerPadding ->
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopCenter) {
                        scaffoldContent(innerPadding)
                    }
                }
            }
        }
    } else {
        Scaffold(
            modifier = modifier.fillMaxSize(),
            topBar = { topBar?.invoke() },
            bottomBar = {
                if (navDestinations.isNotEmpty()) {
                    AmNavBar(
                        destinations = navDestinations,
                        selectedId = navSelectedId.orEmpty(),
                        onSelect = onNavSelect,
                    )
                } else {
                    bottomAction?.invoke()
                }
            },
            snackbarHost = { snackbarHost?.invoke() },
            floatingActionButton = { floatingActionButton?.invoke() },
            contentWindowInsets = effectiveInsets,
        ) { innerPadding ->
            scaffoldContent(innerPadding)
        }
    }
}
