package com.agendaqr.core.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.text
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.style.TextAlign
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqSquircles
import com.agendaqr.core.ui.theme.AqTextStyles
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.core.ui.theme.AqWeight

/**
 * Acción rápida tipo tarjeta: glifo + etiqueta centrados, área táctil ≥ 48 dp.
 * Usada en la rejilla de acciones de Inicio (S01).
 *
 * La celda acepta el ancho del padre: el llamador pasa [Modifier.weight(1f)]
 * cuando las acciones comparten una fila (p. ej. 3 en Inicio). Se elimina el
 * ancho fijo de 48 dp que forzaba a la etiqueta a envolverse una letra por
 * línea (síntoma S1).
 *
 * - [label]: etiqueta visible y descripción accesible (máx. 2 líneas, centrada).
 * - [icon]: glifo de [AqIcons] aportado por el llamador (24 dp = [AqSize.Xs]).
 * - Se compone con [Modifier.clickable] + forma [AqSquircles] sin Material:
 *   [AqButton] no soporta etiqueta multilínea.
 * - Todo el copy llega del llamador ([AppStrings]).
 */
@Composable
fun AqQuickAction(
    label: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Column(
        modifier = modifier
            // El clicable es la celda entera: fusiona la etiqueta y declara
            // el nombre accesible (mismo patrón que AqPillTab/AqMenuItem;
            // el glifo va sin descripción).
            .semantics(mergeDescendants = true) {
                // En Android el nombre accesible del objetivo clicable viaja
                // en `text`; `contentDescription` cae en un nodo hijo no
                // interactivo (verificado con árbol de accesibilidad real).
                text = AnnotatedString(label)
                contentDescription = label
            }
            .defaultMinSize(minHeight = AqMetrics.ControlMinSize)
            .padding(vertical = AqSize.X3s, horizontal = AqSize.X4s)
            .then(
                if (enabled) {
                    Modifier.clickable(
                        role = Role.Button,
                        enabled = true,
                        onClick = onClick,
                    )
                } else {
                    Modifier
                },
            ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(AqSize.X5s),
    ) {
        AqIcon(
            imageVector = icon,
            contentDescription = null,
            size = AqSize.Xs,
            tint = if (enabled) AqColor.TextPrimary else AqColor.OnDisabled,
        )
        AqText(
            text = label,
            size = AqType.BodySm,
            fontWeight = AqWeight.SemiBold,
            color = if (enabled) AqColor.TextPrimary else AqColor.OnDisabled,
            textAlign = TextAlign.Center,
            maxLines = 2,
        )
    }
}
