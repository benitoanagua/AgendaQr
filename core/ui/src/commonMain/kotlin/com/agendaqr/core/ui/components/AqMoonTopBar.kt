package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqTextStyles
import com.agendaqr.core.ui.theme.AqWeight
import com.agendaqr.core.ui.theme.AqSquircles
import com.agendaqr.core.ui.theme.LocalAqColorScheme
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.core.ui.theme.AqCorner

/**
 * Barra superior estándar (Moon). Equivalente a [AqTopBar] pero con nombre
 * que no colisiona con símbolos legacy.
 *
 * Barra superior estándar: título centrado de 18 sp (encabezado semántico),
 * flecha atrás cuando hay a dónde volver — la misma en ambas plataformas —
 * y máx. 2 acciones visibles. Las acciones conservan objetivos de 48 dp.
 *
 * Edge-to-edge: el slot topBar de Scaffold NO recibe los insets del sistema
 * — esta barra los aplica por sí misma (`statusBarsPadding`) para que el
 * título nunca quede tras la barra de estado, y el contenido siga fluyendo
 * debajo.
 */
@Composable
fun AqMoonTopBar(
    title: String,
    modifier: Modifier = Modifier,
    onBack: (() -> Unit)? = null,
    /** copy accesible de la flecha atrás (copy del llamador). */
    backLabel: String = "",
    actions: List<AqTopBarAction> = emptyList(),
) {
    require(actions.size <= 2) {
        "AqMoonTopBar: máximo 2 acciones visibles"
    }
    AqTopBar(
        title = title,
        modifier = modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .heightIn(min = AqMetrics.TopBarHeight)
            .background(AqColor.Surface),
        onBack = onBack,
        backLabel = backLabel,
        actions = actions,
    )
}
