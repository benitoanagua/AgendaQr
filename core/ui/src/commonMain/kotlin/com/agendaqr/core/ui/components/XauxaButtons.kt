package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.core.ui.theme.XauxaMetrics
import com.agendaqr.core.ui.theme.XauxaSpacing
import com.agendaqr.core.ui.theme.XauxaTracking
import com.agendaqr.core.ui.theme.XauxaType
import com.agendaqr.core.ui.theme.asTextOn


/**
 * Botones y chips Xauxa (spec §12 V1.2): envoltorios finos de los
 * componentes M3 con color/forma/tipografía del tema.
 */

@Composable
fun XauxaDangerButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    /** stateDescription de "ocupado" (texto del llamador, §5). */
    busyDescription: String? = null,
) {
    XauxaLoadingButton(
        label = label,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        isLoading = isLoading,
        busyDescription = busyDescription,
        containerColor = XauxaColor.Danger,
        contentColor = XauxaColor.OnDanger,
        disabledContainerColor = XauxaColor.Surface3,
        disabledContentColor = XauxaColor.TextTertiary,
    )
}

/**
 * Botón solo-icono: `IconButton` M3 con descripción accesible y área
 * táctil de 48 dp. El glifo lo aporta el llamador vía [content].
 */
@Composable
fun XauxaIconButton(
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(XauxaMetrics.ControlMinSize)
            .semantics { this.contentDescription = contentDescription },
        enabled = enabled,
    ) {
        content()
    }
}

/** Chip de filtro interactivo, distinto de [XauxaCategoryChip]. */
@Composable
fun XauxaFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    FilterChip(
        modifier = modifier.defaultMinSize(minHeight = XauxaMetrics.ControlMinSize),
        selected = selected,
        onClick = onClick,
        enabled = enabled,
        label = {
            Text(
                label,
                fontSize = XauxaType.Label,
                fontWeight = FontWeight.Medium,
                letterSpacing = XauxaTracking.LabelLarge,
            )
        },
        border = FilterChipDefaults.filterChipBorder(
            enabled = enabled,
            selected = selected,
            // borde de control >= 3:1 (Border fallaba 3:1).
            borderColor = XauxaColor.BorderControl,
            selectedBorderColor = XauxaColor.Brand,
            borderWidth = XauxaMetrics.Border,
        ),
        colors = FilterChipDefaults.filterChipColors(
            containerColor = XauxaColor.Surface,
            labelColor = XauxaColor.TextPrimary,
            selectedContainerColor = XauxaColor.Brand,
            selectedLabelColor = XauxaColor.OnBrand,
        ),
    )
}

/**
 * Conmutador de favorito: `IconToggleButton` M3 con la estrella de Lucide.
 * El estado seleccionado se expone en la semántica del control y la
 * descripción accesible la aporta el llamador (debe cambiar con el
 * estado, §11). El color acompaña; nunca es la única señal.
 */
@Composable
fun XauxaFavoriteToggle(
    favorite: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    /** nombre accesible del control, copy del llamador (§11). */
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    IconToggleButton(
        checked = favorite,
        onCheckedChange = onCheckedChange,
        modifier = modifier
            .size(XauxaMetrics.ControlMinSize)
            .semantics { this.contentDescription = contentDescription },
    ) {
        XauxaIcon(
            imageVector = XauxaIcons.Favorite,
            contentDescription = null,
            tint = if (favorite) XauxaColor.Brand else XauxaColor.TextTertiary,
        )
    }
}

/** Chip de categoría estático: clasifica, no filtra. Sin interacción. */
@Composable
fun XauxaCategoryChip(
    label: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            // Sin borde de reposo: el bloque tonal Surface2 ya define el límite.
            .background(XauxaColor.Surface2, com.agendaqr.core.ui.theme.XauxaShapes.ExtraSmall)
            .padding(horizontal = XauxaSpacing.Sm, vertical = XauxaSpacing.Xs),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, fontSize = XauxaType.Caption, color = XauxaColor.TextSecondary)
    }
}
