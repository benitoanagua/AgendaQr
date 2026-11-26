package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqLayout
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqSquircleShape
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.core.ui.theme.AqWeight

/** Entrada de [AqLeaderboard]: posición, textos y valor final opcional. */
data class AqLeaderboardEntry(
    val title: String,
    /** Posición en el ranking; `null` = sin insignia de puesto. */
    val rank: Int? = null,
    val subtitle: String? = null,
    /** Valor alineado a la derecha (p. ej. puntos); `null` = sin valor. */
    val value: String? = null,
)

/**
 * Clasificación sobre la especificación Moon v1 (Figma «Leaderboard» /
 * `moon-v1-tokens-y-componentes.json`: columna de 640 dp con `gap` 12 dp y
 * texto `body` 14/24).
 *
 * - Columna con hueco de 12 dp ([AqSize.X3s]) entre filas.
 * - Cada fila: insignia circular de puesto (24 dp), título `body` 14/24 en
 *   `textPrimary`, subtítulo `bodyXs` 12/16 en `textSecondary` y valor
 *   final opcional alineado a la derecha.
 * - El podio (puestos 1–3) se destaca con el texto de énfasis; el resto
 *   usa el peso regular. Todo el copy llega del llamador.
 */
@Composable
fun AqLeaderboard(
    entries: List<AqLeaderboardEntry>,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(AqSize.X3s),
    ) {
        entries.forEach { entry ->
            LeaderboardRow(entry = entry)
        }
    }
}

@Composable
private fun LeaderboardRow(entry: AqLeaderboardEntry) {
    val podium = (entry.rank ?: Int.MAX_VALUE) <= 3
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = AqSize.Md)
            .semantics {
                contentDescription = buildString {
                    entry.rank?.let { append("Puesto $it. ") }
                    append(entry.title)
                    entry.subtitle?.let { append(". $it") }
                    entry.value?.let { append(". $it") }
                }
            },
        verticalAlignment = Alignment.CenterVertically,
    ) {
        entry.rank?.let { rank ->
            // Insignia de puesto: squircle de radio = mitad del tamaño
            // (equivalente circular dentro de la regla de formas `Aq*`).
            val badgeShape = AqSquircleShape(AqLayout.LeaderboardRankSize / 2)
            Box(
                modifier = Modifier
                    .size(AqLayout.LeaderboardRankSize)
                    .clip(badgeShape)
                    .background(
                        if (podium) AqColor.EmphasisSurface else AqColor.SurfaceContainerHigh,
                        badgeShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                AqText(
                    text = "$rank",
                    size = AqType.BodySm,
                    fontWeight = AqWeight.SemiBold,
                    color = if (podium) AqColor.EmphasisText else AqColor.TextSecondary,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                )
            }
        }
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(horizontal = AqSize.X3s),
        ) {
            AqText(
                text = entry.title,
                size = AqType.BodySm,
                fontWeight = if (podium) AqWeight.SemiBold else null,
                color = AqColor.TextPrimary,
                maxLines = 1,
            )
            entry.subtitle?.takeIf { it.isNotBlank() }?.let {
                AqText(
                    text = it,
                    size = AqType.BodyXs,
                    color = AqColor.TextSecondary,
                    maxLines = 1,
                )
            }
        }
        entry.value?.let {
            AqText(
                text = it,
                size = AqType.BodySm,
                fontWeight = if (podium) AqWeight.SemiBold else null,
                color = if (podium) AqColor.EmphasisText else AqColor.TextPrimary,
                maxLines = 1,
            )
        }
    }
}
