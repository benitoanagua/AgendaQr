package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import com.agendaqr.core.ui.theme.AmColor
import com.agendaqr.core.ui.theme.AmMetrics
import com.agendaqr.core.ui.theme.AmShapes
import com.agendaqr.core.ui.theme.AmSpacing
import com.agendaqr.core.ui.theme.AmTextStyles
import com.agendaqr.core.ui.theme.AmTracking
import com.agendaqr.core.ui.theme.AmType

/**
 * Botones y chips (referencia: Button/Selector de Ant Design Mobile):
 * fills solid/outline/none, radio 4, deshabilitado con opacidad .4,
 * área táctil mínima de 48 dp y estado de carga que no desplaza el layout.
 */

/**
 * Cuerpo compartido de los botones sólidos con carga: la etiqueta
 * PERMANECE en el layout con alpha 0 y el spinner se superpone centrado —
 * sin salto de ancho; el estado "ocupado" se anuncia con
 * [busyDescription] (stateDescription, copy del llamador).
 */
@Composable
internal fun AmLoadingButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier,
    enabled: Boolean,
    isLoading: Boolean,
    busyDescription: String?,
    containerColor: Color,
    contentColor: Color,
    disabledContainerColor: Color,
    disabledContentColor: Color,
    icon: ImageVector? = null,
) {
    Button(
        modifier = modifier
            .defaultMinSize(minHeight = AmMetrics.ControlMinSize)
            // Deshabilitado con opacidad .4 (referencia); el estado
            // habilitado/deshabilitado también vive en la semántica.
            .then(if (!enabled || isLoading) Modifier.alpha(0.4f) else Modifier)
            .then(
                if (isLoading && busyDescription != null) {
                    Modifier.semantics { stateDescription = busyDescription }
                } else {
                    Modifier
                },
            ),
        onClick = onClick,
        enabled = enabled && !isLoading,
        // Radio 4 de la referencia, no el rol de la plantilla M3.
        shape = AmShapes.Small,
        // Padding vertical propio a cero: la altura la fija el contenido
        // (48 dp), no el relleno interno de Material. El horizontal
        // conserva el de Material (24 dp = Xxl).
        contentPadding = PaddingValues(
            horizontal = AmSpacing.Xxl,
            vertical = AmSpacing.None,
        ),
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = disabledContainerColor,
            disabledContentColor = disabledContentColor,
        ),
    ) {
        Box(contentAlignment = Alignment.Center) {
            // Altura DETERMINÍSTICA 48: el contenido manda sobre el nodo
            // semántico del M3 (el floor externo no lo alcanza en todos
            // los árboles) y con métricas degeneradas el contenido mide 44.
            Row(
                modifier = Modifier.defaultMinSize(minHeight = AmMetrics.ControlMinSize)
                    .then(if (isLoading) Modifier.alpha(0f) else Modifier),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AmSpacing.Xs),
            ) {
                if (!isLoading && icon != null) {
                    AmIcon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = contentColor,
                    )
                }
                Text(
                    label,
                    style = AmTextStyles.ButtonLabel,
                )
            }
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(AmSpacing.Xxl),
                    color = contentColor,
                )
            }
        }
    }
}

/** Botón principal: la acción primaria de la pantalla. */
@Composable
fun AmPrimaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    /** stateDescription de "ocupado" (texto del llamador). */
    busyDescription: String? = null,
    /** Glifo decorativo: la etiqueta visible sigue siendo el nombre
     * accesible (contrato de accesibilidad). */
    icon: ImageVector? = null,
) {
    AmLoadingButton(
        label = label,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        isLoading = isLoading,
        busyDescription = busyDescription,
        icon = icon,
        containerColor = AmColor.Accent,
        contentColor = AmColor.OnAccent,
        disabledContainerColor = AmColor.Disabled,
        disabledContentColor = AmColor.OnDisabled,
    )
}

/** Acción destructiva (eliminar QR/actividad/comprobante). */
@Composable
fun AmDangerButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    /** stateDescription de "ocupado" (texto del llamador). */
    busyDescription: String? = null,
) {
    AmLoadingButton(
        label = label,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        isLoading = isLoading,
        busyDescription = busyDescription,
        containerColor = AmColor.Danger,
        contentColor = AmColor.OnDanger,
        disabledContainerColor = AmColor.Disabled,
        disabledContentColor = AmColor.OnDisabled,
    )
}

/**
 * Botón secundario (fill outline de la referencia): borde y texto de
 * acento sobre fondo transparente — jerarquía visual bajo el botón
 * principal sin superficies tonales.
 */
@Composable
fun AmSecondaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    icon: ImageVector? = null,
) {
    androidx.compose.material3.OutlinedButton(
        modifier = modifier
            .defaultMinSize(minHeight = AmMetrics.ControlMinSize)
            .then(if (!enabled || isLoading) Modifier.alpha(0.4f) else Modifier),
        onClick = onClick,
        enabled = enabled && !isLoading,
        shape = AmShapes.Small,
        contentPadding = PaddingValues(
            horizontal = AmSpacing.Lg,
            vertical = AmSpacing.None,
        ),
        border = androidx.compose.foundation.BorderStroke(
            AmMetrics.Border,
            AmColor.Accent,
        ),
        colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
            containerColor = AmColor.Transparent,
            contentColor = AmColor.AccentText,
            disabledContainerColor = AmColor.Transparent,
            disabledContentColor = AmColor.AccentText,
        ),
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(AmSpacing.Xxl),
                color = AmColor.AccentText,
            )
        } else {
            Row(
                modifier = Modifier.defaultMinSize(minHeight = AmMetrics.ControlMinSize),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AmSpacing.Xs),
            ) {
                if (icon != null) {
                    AmIcon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = AmColor.AccentText,
                    )
                }
                Text(
                    label,
                    style = AmTextStyles.ButtonLabel,
                    color = AmColor.AccentText,
                )
            }
        }
    }
}

/**
 * Acción de texto (terciaria): `TextButton` con área táctil de 48 dp.
 * [color] lo anula el llamador cuando la acción vive sobre un fondo
 * donde el acento no cumple 4.5:1 (p. ej. dentro de un banner de tono,
 * que hereda el color de contenido del tono — par garantizado).
 */
@Composable
fun AmTextAction(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    color: Color? = null,
) {
    val actionColor = color ?: AmColor.AccentText
    TextButton(
        modifier = modifier.defaultMinSize(minHeight = AmMetrics.ControlMinSize),
        onClick = onClick,
        enabled = enabled,
        shape = AmShapes.Small,
        // 48 dp determinísticos como en los botones.
        contentPadding = PaddingValues(
            horizontal = AmSpacing.Md,
            vertical = AmSpacing.None,
        ),
    ) {
        Row(
            modifier = Modifier.defaultMinSize(minHeight = AmMetrics.ControlMinSize),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AmSpacing.Xs),
        ) {
            if (icon != null) {
                AmIcon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (enabled) actionColor else AmColor.OnDisabled,
                )
            }
            Text(
                label,
                style = AmTextStyles.ButtonLabel,
                color = if (enabled) actionColor else AmColor.OnDisabled,
            )
        }
    }
}

/**
 * Botón solo-icono: `IconButton` M3 con descripción accesible y área
 * táctil de 48 dp. El glifo lo aporta el llamador vía [content].
 */
@Composable
fun AmIconButton(
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    IconButton(
        onClick = onClick,
        modifier = modifier
            .size(AmMetrics.ControlMinSize)
            .semantics { this.contentDescription = contentDescription },
        enabled = enabled,
    ) {
        content()
    }
}

/**
 * Chip de filtro interactivo (estilo Selector de la referencia):
 * borde hairline en reposo; seleccionado con borde de acento, texto de
 * acento y fondo wathet. La selección también vive en la semántica.
 */
@Composable
fun AmFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    FilterChip(
        modifier = modifier.defaultMinSize(minHeight = AmMetrics.ControlMinSize),
        selected = selected,
        onClick = onClick,
        enabled = enabled,
        shape = AmShapes.Pill,
        border = FilterChipDefaults.filterChipBorder(
            enabled = enabled,
            selected = selected,
            borderColor = AmColor.Divider,
            selectedBorderColor = AmColor.Accent,
            borderWidth = AmMetrics.Border,
        ),
        colors = FilterChipDefaults.filterChipColors(
            containerColor = AmColor.Surface,
            labelColor = AmColor.TextPrimary,
            disabledContainerColor = AmColor.Disabled,
            disabledLabelColor = AmColor.OnDisabled,
            selectedContainerColor = AmColor.Wathet,
            selectedLabelColor = AmColor.AccentText,
        ),
        label = {
            Text(
                label,
                fontSize = AmType.Label,
                fontWeight = FontWeight.Medium,
                letterSpacing = AmTracking.LabelLarge,
            )
        },
    )
}

/**
 * Conmutador de favorito: `IconToggleButton` M3 con la estrella de
 * Lucide. El estado seleccionado se expone en la semántica del control y
 * la descripción accesible la aporta el llamador (debe cambiar con el
 * estado). El color acompaña; nunca es la única señal.
 */
@Composable
fun AmFavoriteToggle(
    favorite: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    /** nombre accesible del control, copy del llamador. */
    contentDescription: String,
    modifier: Modifier = Modifier,
) {
    IconToggleButton(
        checked = favorite,
        onCheckedChange = onCheckedChange,
        modifier = modifier
            .size(AmMetrics.ControlMinSize)
            .semantics { this.contentDescription = contentDescription },
    ) {
        AmIcon(
            imageVector = AmIcons.Favorite,
            contentDescription = null,
            tint = if (favorite) AmColor.Accent else AmColor.TextTertiary,
        )
    }
}
