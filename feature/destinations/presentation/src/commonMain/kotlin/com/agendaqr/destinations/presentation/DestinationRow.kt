package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.agendaqr.destinations.data.SyncResource
import com.agendaqr.core.ui.components.AqAlert
import com.agendaqr.core.ui.components.AqButton
import com.agendaqr.core.ui.components.AqButtonSize
import com.agendaqr.core.ui.components.AqButtonVariant
import com.agendaqr.core.ui.components.AqCheckbox
import com.agendaqr.core.ui.components.AqEmptyPlaceholder
import com.agendaqr.core.ui.components.AqFavoriteToggle
import com.agendaqr.core.ui.components.AqHairline
import com.agendaqr.core.ui.components.AqIcon
import com.agendaqr.core.ui.components.AqIcons
import com.agendaqr.core.ui.components.AqLoadMore
import com.agendaqr.core.ui.components.AqMenuItem
import com.agendaqr.core.ui.components.AqModal
import com.agendaqr.core.ui.components.AqNavChromeState
import com.agendaqr.core.ui.components.AqPullToRefresh
import com.agendaqr.core.ui.components.AqScreen
import com.agendaqr.core.ui.components.AqSectionTitle
import com.agendaqr.core.ui.components.AqSegmentedControl
import com.agendaqr.core.ui.components.AqSegmentedOption
import com.agendaqr.core.ui.components.AqSelectionToolbar
import com.agendaqr.core.ui.components.AqSkeletonBlock
import com.agendaqr.core.ui.components.AqSwipeAction
import com.agendaqr.core.ui.components.AqSwipeActions
import com.agendaqr.core.ui.components.AqTag
import com.agendaqr.core.ui.components.AqText
import com.agendaqr.core.ui.components.AqToastHost
import com.agendaqr.core.ui.components.AqTone
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqLayout
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.core.ui.theme.AqWeight
import com.agendaqr.destinations.domain.Destination
import com.agendaqr.core.ui.components.AqQuickAction

/**
 * Mini-tarjeta del carrusel de favoritos: tile de identidad, nombre,
 * metadato y categoría real.
 */
@Composable
internal fun FavoriteDestinationCard(
    destination: Destination,
    onClick: () -> Unit,
) {
    // La superficie presionable vive en el DS (feature/ sin clickable
    // crudo): [AqMenuItem] con miniatura QR y nombre.
    AqMenuItem(
        title = destination.name.ifBlank { AppStrings.SinNombre },
        leading = {
            AqIcon(
                imageVector = com.agendaqr.core.ui.components.AqIcons.QrCode,
                contentDescription = null,
                size = com.agendaqr.core.ui.theme.AqMetrics.IconSizeCompact,
            )
        },
        trailing = {
            destination.category?.let {
                AqTag(text = it)
            }
        },
        onClick = onClick,
    )
}

/**
 * Fila del destino: miniatura QR a la izquierda, título con identidad,
 * metadato de sincronización y favorito a la derecha.
 */
@Composable
fun DestinationRow(
    destination: Destination,
    onAction: (DestinationAction) -> Unit,
    syncLookup: ElementSyncLookup = ElementSyncLookup.Empty,
    showDivider: Boolean = false,
    onRequestDelete: ((Destination) -> Unit)? = null,
    /** G3: fila seleccionada (modo selección por lote). */
    selected: Boolean = false,
    /** G3: alterna la selección en vez de navegar. */
    onToggle: (() -> Unit)? = null,
    /** G3: las acciones de deslizamiento se deshabilitan en modo selección. */
    swipeEnabled: Boolean = true,
) {
    // Fila del destino: ícono QR a la izquierda, título con identidad,
    // metadato de sincronización y favorito a la derecha.
    val rowContent = @Composable {
        AqMenuItem(
            title = destination.name.ifBlank { AppStrings.SinNombre },
            subtitle = destinationSubtitle(destination),
            showDivider = showDivider,
            onClick = {
                if (onToggle != null) onToggle() else onAction(DestinationAction.Open(destination.id))
            },
            leading = {
                if (selected) {
                    AqCheckbox(
                        label = "",
                        checked = selected,
                        onCheckedChange = { onToggle?.invoke() },
                    )
                } else {
                    AqIcon(
                        imageVector = com.agendaqr.core.ui.components.AqIcons.QrCode,
                        contentDescription = null,
                        size = com.agendaqr.core.ui.theme.AqMetrics.IconSizeCompact,
                    )
                }
            },
            trailing = {
                Row(
                    modifier = Modifier,
                    horizontalArrangement = Arrangement.spacedBy(AqSpacing.Xs),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    ElementSyncBadge(
                        syncLookup.status(SyncResource.DESTINATION, destination.id),
                    )
                    AqFavoriteToggle(
                        favorite = destination.favorite,
                        // Nombre único por fila: el contrato del árbol de
                        // accesibilidad prohíbe destinos repetidos.
                        contentDescription = (
                            if (destination.favorite) {
                                AppStrings.QuitarFavorito
                            } else {
                                AppStrings.MarcarFavorito
                            }
                            ) + ": " + destination.name.ifBlank { AppStrings.SinNombre },
                        onCheckedChange = { onAction(DestinationAction.ToggleFavorite(destination)) },
                    )
                }
            },
        )
    }

    if (onRequestDelete != null) {
        val swipeActions = listOf(
            AqSwipeAction(
                label = if (destination.favorite) AppStrings.QuitarFavorito else AppStrings.MarcarFavorito,
                icon = com.agendaqr.core.ui.components.AqIcons.Favorite,
                backgroundColor = AqColor.EmphasisFill,
                contentColor = AqColor.InverseOnSurface,
                onAction = { onAction(DestinationAction.ToggleFavorite(destination)) },
            ),
            AqSwipeAction(
                label = AppStrings.Eliminar,
                icon = com.agendaqr.core.ui.components.AqIcons.Trash,
                backgroundColor = AqColor.DangerFill,
                contentColor = AqColor.InverseOnSurface,
                onAction = { onRequestDelete(destination) },
            ),
        )
        AqSwipeActions(actions = swipeActions, enabled = swipeEnabled) {
            rowContent()
        }
    } else {
        rowContent()
    }
}

/**
 * Subtítulo de la fila del destino.
 */
internal fun destinationSubtitle(destination: Destination): String? {
    val generic = destination.name.isBlank() ||
        destination.name.startsWith(AppStrings.QrImportado)
    return when {
        generic && destination.name.isBlank() ->
            AppStrings.AnadidoEl + formatDate(destination.createdAt)
        generic -> AppStrings.ImportadoEl + formatDate(destination.createdAt)
        else -> destination.note ?: destination.category
    }
}
