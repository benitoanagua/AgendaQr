package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqLayout
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqSquircles
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.core.ui.theme.AqWeight

/**
 * Paginación sobre la especificación Moon v1 (Figma «Pagination» /
 * `moon-v1-tokens-y-componentes.json`: fila de 32 dp con hueco de 4 dp).
 *
 * - Fila de alto 32 dp, `gap` 4 dp: botón «anterior», páginas numeradas y
 *   botón «siguiente».
 * - La página actual se pinta con el relleno de énfasis (`piccolo`) y texto
 *   `onEmphasisFill`; las demás son transparentes con texto primario y
 *   velo `heles` en hover (delegado en el estado pressed/hover estándar).
 * - Deshabilitada por [enabled] (opacidad 0.6) o por límites del rango.
 * - El copy de los botones de navegación llega del llamador vía
 *   [previousContentDescription]/[nextContentDescription].
 */
@Composable
fun AqPagination(
    pageCount: Int,
    currentPage: Int,
    onPageSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    previousContentDescription: String,
    nextContentDescription: String,
    pageContentDescription: (Int) -> String,
) {
    if (pageCount <= 0) return
    val safeCurrent = currentPage.coerceIn(0, pageCount - 1)
    Row(
        modifier = modifier.height(AqSize.Sm),
        horizontalArrangement = Arrangement.spacedBy(AqSize.X5s),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PaginationNavButton(
            label = "‹",
            contentDescription = previousContentDescription,
            enabled = enabled && safeCurrent > 0,
            onClick = { onPageSelected(safeCurrent - 1) },
        )
        for (page in 0 until pageCount) {
            PaginationPageButton(
                page = page,
                selected = page == safeCurrent,
                enabled = enabled,
                onClick = { onPageSelected(page) },
                contentDescription = pageContentDescription(page + 1),
            )
        }
        PaginationNavButton(
            label = "›",
            contentDescription = nextContentDescription,
            enabled = enabled && safeCurrent < pageCount - 1,
            onClick = { onPageSelected(safeCurrent + 1) },
        )
    }
}

@Composable
private fun PaginationPageButton(
    page: Int,
    selected: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    contentDescription: String,
) {
    val shape = AqSquircles.InteractiveXs
    Box(
        modifier = Modifier
            .size(AqLayout.PaginationItemSize)
            .clip(shape)
            .background(
                if (selected) AqColor.EmphasisFill else AqColor.Transparent,
                shape,
            )
            .clickable(
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            )
            .semantics {
                this.contentDescription = contentDescription
                this.selected = selected
            },
        contentAlignment = Alignment.Center,
    ) {
        AqText(
            text = "${page + 1}",
            size = AqType.BodySm,
            fontWeight = if (selected) AqWeight.SemiBold else null,
            color = when {
                !enabled -> AqColor.OnDisabled
                selected -> AqColor.OnEmphasisFill
                else -> AqColor.TextPrimary
            },
            textAlign = TextAlign.Center,
            maxLines = 1,
        )
    }
}

@Composable
private fun PaginationNavButton(
    label: String,
    contentDescription: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val shape = AqSquircles.InteractiveXs
    Box(
        modifier = Modifier
            .size(AqLayout.PaginationItemSize)
            .clip(shape)
            .clickable(
                enabled = enabled,
                role = Role.Button,
                onClick = onClick,
            )
            .semantics {
                this.contentDescription = contentDescription
            },
        contentAlignment = Alignment.Center,
    ) {
        AqText(
            text = label,
            size = AqType.BodySm,
            color = if (enabled) AqColor.TextPrimary else AqColor.OnDisabled,
            maxLines = 1,
        )
    }
}
