package com.agendaqr.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import com.agendaqr.core.ui.components.AqTabBar
import com.agendaqr.core.ui.components.AqTab
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.aqWindowWidthSizeClass
import androidx.compose.ui.unit.dp

/**
 * Único scaffold de pantalla (Moon). Reemplaza a [AqScreenScaffold]
 * legacy y concentra la geometría común: barra superior, navegación raíz
 * (barra inferior tipo [AqTabBar] en compacto; riel en expandido) y
 * acción fija inferior.
 *
 * Las pantallas ya no usan [AqSnackbarHost]: el feedback transitorio se
 * renderiza con [AqToast] sobre el estado de la pantalla. La acción fija
 * inferior se compone con [AqButton] en el slot [bottomAction].
 *
 * - [topBar]: normalmente [AqTopBar].
 * - [navDestinations]: navegación raíz (Inicio · Buscar · Actividades ·
 *   Contextos). Compacto la pinta como barra inferior [AqTabBar];
 *   expandido como riel lateral ([AqTabBar] en modo riel).
 * - [bottomAction]: acción principal fija abajo (guardar).
 * - [scrollable]: scroll del contenido.
 * - [statusContent]: estado persistente a ancho completo bajo la barra.
 */
@Composable
fun AqScreenScaffold(
    modifier: Modifier = Modifier,
    topBar: (@Composable () -> Unit)? = null,
    navDestinations: List<AqNavDestination> = emptyList(),
    navSelectedId: String? = null,
    onNavSelect: (String) -> Unit = {},
    bottomAction: (@Composable () -> Unit)? = null,
    statusContent: (@Composable () -> Unit)? = null,
    scrollable: Boolean = false,
    contentWindowInsets: WindowInsets = ScaffoldDefaults.contentWindowInsets,
    content: @Composable ColumnScope.() -> Unit,
) {
    val width = aqWindowWidthSizeClass()
    val expanded = width != WindowWidthSizeClass.Compact
    val effectiveInsets = contentWindowInsets
    val selectedIndex = navDestinations.indexOfFirst { it.id == navSelectedId }.coerceAtLeast(0)
    val tabs = navDestinations.map {
        AqTab(
            label = it.label,
        )
    }

    val scaffoldContent: @Composable (androidx.compose.foundation.layout.PaddingValues) -> Unit = { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            statusContent?.invoke()
            Column(
                modifier = Modifier
                    .then(if (expanded) Modifier.fillMaxWidth() else Modifier)
                    .weight(1f)
                    .then(if (scrollable) {
                        Modifier.verticalScroll(rememberScrollState())
                    } else Modifier)
                    .padding(
                        horizontal = AqSpacing.ScreenMargin,
                        vertical = AqSpacing.ScreenMargin,
                    ),
            ) {
                content()
            }
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = { topBar?.invoke() },
            bottomBar = {
                if (navDestinations.isNotEmpty() && !expanded) {
                    AqTabBar(
                        tabs = tabs,
                        selectedIndex = selectedIndex,
                        onSelect = { onNavSelect(navDestinations[it].id) },
                    )
                } else {
                    bottomAction?.invoke()
                }
            },
            contentWindowInsets = effectiveInsets,
        ) { innerPadding ->
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.TopCenter,
            ) {
                scaffoldContent(innerPadding)
            }
        }
        // Riel lateral en expandido.
        if (navDestinations.isNotEmpty() && expanded) {
            Row(modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .width(AqMetrics.NavRailWidth)
                        .fillMaxHeight()
                        .background(AqColor.Surface),
                ) {
                    AqTabBar(
                        tabs = tabs,
                        selectedIndex = selectedIndex,
                        onSelect = { onNavSelect(navDestinations[it].id) },
                        modifier = Modifier.fillMaxHeight().padding(vertical = AqSpacing.Sm),
                    )
                }
                Box(modifier = Modifier.weight(1f).fillMaxSize()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.TopCenter,
                    ) {
                        scaffoldContent(androidx.compose.foundation.layout.PaddingValues(0.dp))
                    }
                }
            }
        }
        // Acción fija inferior cuando no hay barra de navegación raíz.
        bottomAction?.let {
            if (navDestinations.isEmpty() || expanded) {
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .navigationBarsPadding()
                        .imePadding()
                        .padding(horizontal = AqSpacing.ScreenMargin, vertical = AqSpacing.Sm),
                ) { it() }
            }
        }
    }
}

/**
 * Dock inferior de acciones (organismo): superficie propia, separador
 * hairline, inset de barras de navegación e IME y margen de pantalla.
 * Legacy: se conserva temporalmente hasta la Fase D (borrado del
 * legado); las pantallas nuevas no lo usan.
 */
@Composable
fun AqBottomActionDock(
    modifier: Modifier = Modifier,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit,
) {
    androidx.compose.foundation.layout.Column(
        modifier = modifier
            .fillMaxWidth()
            .background(AqColor.Surface)
            .navigationBarsPadding()
            .imePadding()
            .testTag("aq-bottom-action-dock"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
    ) {
        androidx.compose.material3.HorizontalDivider(
            thickness = AqMetrics.DividerHeight,
            color = AqColor.Divider,
        )
        androidx.compose.foundation.layout.Column(
            modifier = Modifier
                .widthIn(max = AqMetrics.ContentMaxWidth)
                .fillMaxWidth()
                .padding(horizontal = AqSpacing.ScreenMargin, vertical = AqSpacing.Sm),
            verticalArrangement = Arrangement.spacedBy(AqSpacing.Md),
        ) {
            content()
        }
    }
}

/** Botón de acción principal FIJO al borde inferior (legacy). */
@Composable
fun AqBottomAction(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    busyDescription: String? = null,
) {
    AqBottomActionDock(modifier = modifier) {
        AqPrimaryButton(
            label = label,
            onClick = onClick,
            modifier = Modifier.fillMaxWidth(),
            enabled = enabled,
            isLoading = isLoading,
            busyDescription = busyDescription,
        )
    }
}
