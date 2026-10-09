package com.agendaqr.core.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.core.ui.theme.XauxaMetrics
import com.agendaqr.core.ui.theme.XauxaSpacing
import com.agendaqr.core.ui.theme.XauxaType
import com.agendaqr.core.ui.theme.XauxaShape


/**
 * Feedback y estados Xauxa: stats, skeleton, error, paginación, favorito, cabecera de tile, escáner y subida.
 * (T12: dividido de XauxaExtendedComponents.kt por responsabilidad, sin cambio de comportamiento.)
 */

/** Bloque de estadística centrado (patrón stats de Xauxa). */
@Composable
fun XauxaStatBlock(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    tone: XauxaTone = XauxaTone.Neutral,
) {
    Column(
        modifier = modifier.padding(XauxaSpacing.Sm),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Xs),
    ) {
        Text(
            value,
            fontSize = XauxaType.Headline,
            fontWeight = FontWeight.Bold,
            fontFamily = XauxaType.FamilyMono,
            color = if (tone == XauxaTone.Neutral) XauxaColor.BrandText else tone.content(),
            textAlign = TextAlign.Center,
        )
        Text(
            label,
            fontSize = XauxaType.Caption,
            letterSpacing = XauxaType.LetterSpacingWide,
            color = XauxaColor.TextSecondary,
            textAlign = TextAlign.Center,
        )
    }
}

/** Skeleton estático sin movimiento decorativo continuo. */
@Composable
fun XauxaSkeleton(
    modifier: Modifier = Modifier,
    lines: Int = 3,
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(XauxaSpacing.Lg),
        verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm),
    ) {
        Box(modifier = Modifier.fillMaxWidth(fraction = XauxaSkeletonTitleFraction).heightIn(min = XauxaSpacing.Xl).background(XauxaColor.Surface2))
        repeat(lines.coerceAtLeast(1)) {
            Box(modifier = Modifier.fillMaxWidth().heightIn(min = XauxaSpacing.Sm).background(XauxaColor.Surface2))
        }
    }
}

private const val XauxaSkeletonTitleFraction = 0.6f

/** Página de error a pantalla completa, centrada (estado permitido). */
@Composable
fun XauxaErrorPage(
    title: String,
    message: String,
    actionLabel: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
    secondaryLabel: String? = null,
    onSecondary: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(XauxaSpacing.Huge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Lg, Alignment.CenterVertically),
    ) {
        Text(title, fontSize = XauxaType.Headline, fontWeight = FontWeight.Bold, color = XauxaColor.Danger, textAlign = TextAlign.Center)
        Text(message, fontSize = XauxaType.Body, color = XauxaColor.TextSecondary, textAlign = TextAlign.Center)
        XauxaPrimaryButton(label = actionLabel, onClick = onAction)
        if (secondaryLabel != null && onSecondary != null) {
            XauxaTextAction(label = secondaryLabel, onClick = onSecondary)
        }
    }
}

/**
 * Footer de "Cargar más". La paginación se compone de este footer
 * (inv. 23): no existe un componente de paginación duplicado.
 */
@Composable
fun XauxaLoadMoreFooter(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
    endReached: Boolean = false,
    onLoadMore: (() -> Unit)? = null,
    /** Fase 4: copy desde el llamador (core/ui no hardcodea copy). */
    loadMoreLabel: String = "",
    loadingLabel: String = "",
    endLabel: String = "",
) {
    if (onLoadMore != null) require(loadMoreLabel.isNotBlank()) { "loadMoreLabel requerido" }
    Box(
        modifier = modifier.fillMaxWidth().padding(XauxaSpacing.Lg),
        contentAlignment = Alignment.Center,
    ) {
        when {
            endReached -> Text(endLabel, fontSize = XauxaType.Caption, color = XauxaColor.TextSecondary)
            isLoading -> Row(
                horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                XauxaDotProgress(color = XauxaColor.BrandText)
                Text(loadingLabel, fontSize = XauxaType.Label, color = XauxaColor.TextSecondary)
            }
            onLoadMore != null -> XauxaSecondaryButton(label = loadMoreLabel, onClick = onLoadMore)
        }
    }
}

/**
 * Toggle de favorito interactivo. Implementación real del control; la
 * integración con producto queda pendiente (ver contrato del catálogo).
 */
@Composable
fun XauxaFavoriteToggle(
    favorite: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    /** Fase 4: nombre accesible del control, copy del llamador (§11). */
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    val interaction = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .size(XauxaMetrics.ControlMinSize)
            .semantics {
                this.contentDescription = contentDescription
                toggleableState = if (favorite) ToggleableState.On else ToggleableState.Off
            }
            .clickable(
                interactionSource = interaction,
                indication = null,
                role = Role.Checkbox,
                onClick = { onCheckedChange(!favorite) },
            )
            .focusable(interactionSource = interaction)
            .xauxaFocusRing(interaction),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier.size(XauxaMetrics.FavoriteIndicatorSize)
                .background(if (favorite) XauxaColor.Brand else XauxaColor.Surface2)
                .border(BorderStroke(XauxaMetrics.Border, XauxaColor.BorderControl), XauxaShape),
            contentAlignment = Alignment.Center,
        ) {
            // Fase 2 (§11): el estado no depende solo del color — el
            // glifo aparece solo cuando está marcado.
            if (favorite) {
                XauxaIcon(
                    imageVector = XauxaIcons.Favorite,
                    contentDescription = null,
                    size = XauxaMetrics.IconSize,
                    tint = XauxaColor.OnBrand,
                )
            }
        }
    }
}

/**
 * Cabecera de tile con banda de marca (patrón tile-header de Xauxa:
 * fondo Brand, título uppercase y slot de estado/acciones). Opt-in: las
 * pantallas existentes que usan XauxaSection no cambian.
 */
@Composable
fun XauxaTileHeader(
    title: String,
    modifier: Modifier = Modifier,
    status: String? = null,
    actions: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(XauxaColor.Brand)
            .padding(XauxaSpacing.Lg),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm),
    ) {
        Text(
            title,
            modifier = Modifier.weight(XauxaToneWeight),
            fontSize = XauxaType.Title,
            fontWeight = FontWeight.Bold,
            letterSpacing = XauxaType.LetterSpacingWide,
            color = XauxaColor.OnBrand,
        )
        if (status != null) {
            Text(
                status,
                fontSize = XauxaType.Caption,
                fontWeight = FontWeight.Bold,
                letterSpacing = XauxaType.LetterSpacingWide,
                color = XauxaColor.OnBrand,
            )
        }
        actions?.invoke()
    }
}

/**
 * Viewport de cámara/escáner. El encuadre es preview real; el escaneo real
 * requiere cámara y queda pendiente por plataforma (ver contrato).
 */
@Composable
fun XauxaScannerViewport(
    modifier: Modifier = Modifier,
    scanning: Boolean = false,
    /** Ronda 2 (Área H/ADR-0008): copy del llamador; el DS no hardcodea. */
    hint: String = "",
    /** Texto del estado "escaneando". */
    scanningLabel: String = "",
) {
    if (scanning) require(scanningLabel.isNotBlank()) { "scanningLabel requerido" }
    Box(
        modifier = modifier
            .fillMaxWidth()
            // Sin borde de reposo: el bloque tonal ya define el límite.
            .background(XauxaColor.Surface2)
            .padding(XauxaSpacing.Xxxl),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm),
        ) {
            Box(
                modifier = Modifier.size(XauxaMetrics.QrPreviewSize)
                    .border(BorderStroke(XauxaMetrics.BorderStrong, XauxaColor.Brand), XauxaShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    if (scanning) scanningLabel else hint,
                    fontSize = XauxaType.Caption,
                    color = XauxaColor.TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(XauxaSpacing.Sm),
                )
            }
        }
    }
}

/**
 * Zona de carga de archivos con estados. Presentacional: la selección real
 * depende de la plataforma y queda pendiente (ver contrato).
 */
@Composable
fun XauxaFileUpload(
    modifier: Modifier = Modifier,
    fileName: String? = null,
    isLoading: Boolean = false,
    error: String? = null,
    onSelect: (() -> Unit)? = null,
    onClear: (() -> Unit)? = null,
    /** Ronda 2 (Área H/ADR-0008): copy del llamador; el DS no hardcodea. */
    loadingLabel: String = "",
    clearLabel: String = "",
    placeholderTitle: String = "",
    placeholderMeta: String = "",
) {
    if (isLoading) require(placeholderTitle.isNotBlank() || loadingLabel.isNotBlank()) {
        "XauxaFileUpload: copy del llamador requerido"
    }
    if (onClear != null) require(clearLabel.isNotBlank()) { "clearLabel requerido" }
    val interaction = remember { MutableInteractionSource() }
    val clickableModifier = if (onSelect == null) modifier else modifier
        .clickable(interactionSource = interaction, indication = null, role = Role.Button, onClick = onSelect)
        .focusable(interactionSource = interaction)
    Column(
        modifier = clickableModifier
            .fillMaxWidth()
            .xauxaFocusRing(interaction)
            // Sin borde de reposo: el bloque tonal define el límite.
            .background(XauxaColor.Surface)
            .padding(XauxaSpacing.Lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm),
    ) {
        when {
            isLoading -> {
                XauxaDotProgress(color = XauxaColor.Brand)
                Text(loadingLabel, fontSize = XauxaType.Label, color = XauxaColor.TextSecondary)
            }
            fileName != null -> {
                Text(fileName, fontSize = XauxaType.Body, color = XauxaColor.TextPrimary, textAlign = TextAlign.Center)
                if (onClear != null) XauxaTextAction(label = clearLabel, onClick = onClear)
            }
            else -> {
                Text(placeholderTitle, fontSize = XauxaType.Body, color = XauxaColor.TextPrimary, textAlign = TextAlign.Center)
                Text(placeholderMeta, fontSize = XauxaType.Caption, color = XauxaColor.TextSecondary, textAlign = TextAlign.Center)
            }
        }
        if (error != null) {
            Text(error, fontSize = XauxaType.Caption, color = XauxaColor.Danger, textAlign = TextAlign.Center)
        }
    }
}
