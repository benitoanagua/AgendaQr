package com.agendaqr.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.theme.XauxaSpacing

/**
 * Contenedor de pantalla Xauxa (contrato §9 insets + M7 chrome): UN solo
 * lugar gestiona los insets del sistema y la barra de aplicación inferior.
 *
 * - Estatus: [androidx.compose.foundation.layout.statusBarsPadding] + IME
 *   en la columna raíz; navegación la consume la [bottomBar] DENTRO de su
 *   superficie (el bloque Surface2 llega hasta el borde, sin franja de
 *   fondo debajo). Sin barra, el propio contenido respeta la barra de
 *   navegación.
 * - Contenido: columna con [XauxaSpacing.ScreenMargin] y el interlineado
 *   canónico de pantalla; [scrollable] añade scroll cuando la pantalla ya
 *   no usa su propia LazyColumn.
 * - [bottomBar]: normalmente [XauxaAppBar] con la acción principal y las
 *   secundarias de la pantalla (M7). Las pantallas raíz sin destino de
 *   retorno y sin acciones (AuthScreen) son la excepción E-07 del
 *   contrato §12: pasan bottomBar = null y su acción principal vive en el
 *   cuerpo (M10).
 *
 * Sustituye al histórico `XauxaScreenColumn` (0 usos): la gestión de
 * insets deja de repetirse pantalla por pantalla.
 */
@Composable
fun XauxaScreenScaffold(
    modifier: Modifier = Modifier,
    /** Scroll vertical del contenido (pantallas sin LazyColumn propia). */
    scrollable: Boolean = false,
    horizontalAlignment: Alignment.Horizontal = Alignment.Start,
    verticalArrangement: Arrangement.Vertical = Arrangement.spacedBy(XauxaSpacing.Lg),
    /** Barra de aplicación inferior (M7); null = pantalla sin chrome inferior. */
    bottomBar: (@Composable () -> Unit)? = null,
    content: @Composable androidx.compose.foundation.layout.ColumnScope.() -> Unit,
) {
    Column(modifier = modifier.fillMaxSize().statusBarsPadding().imePadding()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .then(if (scrollable) Modifier.verticalScroll(rememberScrollState()) else Modifier)
                // Sin barra inferior el contenido respeta la barra de
                // navegación del sistema; con barra, la consume la barra.
                .then(if (bottomBar == null) Modifier.navigationBarsPadding() else Modifier)
                .padding(XauxaSpacing.ScreenMargin),
            horizontalAlignment = horizontalAlignment,
            verticalArrangement = verticalArrangement,
        ) {
            content()
        }
        bottomBar?.invoke()
    }
}
