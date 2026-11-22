package com.agendaqr.core.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.semantics
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqTextStyles
import com.agendaqr.core.ui.theme.AqType

/**
 * Título de sección en una lista o formulario.
 * Sustituye a [AqSection] y [AqSectionHeader] cuando se usa como encabezado
 * de sección de contenido (no de formulario).
 *
 * - [text]: título de la sección (copy del llamador).
 * - [trailing]: acción opcional a la derecha (p. ej. "Ver todo").
 * - Todo el copy llega del llamador.
 */
@Composable
fun AqSectionTitle(
    text: String,
    modifier: Modifier = Modifier,
    trailing: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        AqText(
            text = text,
            size = AqType.HeadingSm,
            fontWeight = com.agendaqr.core.ui.theme.AqWeight.SemiBold,
            color = com.agendaqr.core.ui.theme.AqColor.TextSecondary,
            semanticHeading = true,
        )
        trailing?.invoke()
    }
}

/**
 * Sección de lista: agrupación visual con título opcional y espaciado
 * canónico. Construida con [AqText] + layout ([AqSpacing]), sin [AqSection].
 */
@Composable
fun AqListSection(
    modifier: Modifier = Modifier,
    title: String? = null,
    content: @Composable () -> Unit,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AqSpacing.Md),
    ) {
        if (title != null) {
            AqSectionTitle(text = title)
        }
        content()
    }
}
