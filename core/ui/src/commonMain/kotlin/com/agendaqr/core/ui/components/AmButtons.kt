package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.agendaqr.core.ui.theme.AmColor
import com.agendaqr.core.ui.theme.AmMetrics
import com.agendaqr.core.ui.theme.AmShapes
import com.agendaqr.core.ui.theme.AmSpacing
import com.agendaqr.core.ui.theme.AmTextStyles
import com.agendaqr.core.ui.theme.AmTracking
import com.agendaqr.core.ui.theme.AmType

/**
 * Botones y chips (referencia: Button/CapsuleTabs de Ant Design Mobile):
 * fills solid/default/none — el secundario equivale al botón `default`
 * (fondo claro + hairline + texto primario, no outline de acento) —,
 * radio 4, deshabilitado con opacidad .4, área táctil mínima de 48 dp y
 * estado de carga que no desplaza el layout.
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
        // Radio 4 del botón (Button de la referencia), no el rol de la
        // plantilla M3.
        shape = AmShapes.Small,
        // Padding vertical propio a cero: la altura la fija el contenido
        // (48 dp de objetivo táctil), no el relleno interno de Material.
        // Horizontal compacto de 16 dp.
        contentPadding = PaddingValues(
            horizontal = AmSpacing.Lg,
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

/** Nivel 1 — Acción primaria.
 *  Acción principal claramente identificable por sección o contexto.
 *  Utilizada para completar el objetivo más importante del usuario en ese estado.
 *  Tratamiento visual destacado y etiqueta específica (uso obligatorio de AmColor.Accent).
 *  Siempre debe haber exactamente una acción primaria por sección/contexto cuando el objetivo del producto lo requiera.
 */
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

/**
 * Nivel 4 — Acción destructiva o excepcional.
 * Descartar, eliminar o realizar una operación con consecuencias especiales.
 * NO debe competir visualmente con la acción primaria: su color propio
 * (AmColor.Danger) y su texto específico la diferencian semánticamente
 * (el color nunca es la única señal).
 * Requiere confirmación cuando la acción pueda provocar una pérdida
 * irreversible de información (AmDialog destructive = true).
 */
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
 * Nivel 2 — Acción secundaria.
 * Alternativa importante, pero no la acción principal.
 * Tratamiento menos dominante: variante tonal con background SurfaceContainerLow
 * y border Divider (hairline) para diferenciarla visualmente de la primaria.
 * Área táctil mínima de 48 dp aunque la superficie visible sea 40 dp (mandato de compactación):
 * la zona interactiva completa es 48 dp, la superficie mostrada puede ser menor.
 * Nunca compite visualmente con la acción primaria.
 * Ideal para: acciones alternativas, navegación auxiliar, compartir, editar.
 * Debe haber como máximo una acción secundaria destacada por contexto.
 */
@Composable
fun AmSecondaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    icon: ImageVector? = null,
    /** Variante sólida de acento (misma métrica compacta). */
    solid: Boolean = false,
) {
    // Zona interactiva: 48 dp aunque la superficie visible sea 40. La
    // superficie (fondo + borde) se dibuja en una capa que ocupa todo el
    // ancho disponible cuando el llamador pasa fillMaxWidth(), y se ajusta
    // al contenido en caso contrario (matchParentSize sobre el Box exterior).
    val container = if (solid) AmColor.Accent else AmColor.SurfaceContainerLow
    val content = if (solid) {
        AmColor.OnAccent
    } else {
        AmColor.TextPrimary
    }
    val borderModifier = if (solid) {
        Modifier
    } else {
        Modifier.border(AmMetrics.Border, AmColor.Divider, AmShapes.Small)
    }
    Box(
        modifier = modifier
            .defaultMinSize(minHeight = AmMetrics.ControlMinSize)
            .clickable(
                enabled = enabled && !isLoading,
                role = Role.Button,
                onClickLabel = label,
                onClick = onClick,
            )
            .then(if (!enabled || isLoading) Modifier.alpha(0.4f) else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .heightIn(min = AmMetrics.ButtonCompactHeight)
                .background(container, AmShapes.Small)
                .then(borderModifier),
        )
        Row(
            modifier = Modifier.padding(horizontal = AmSpacing.Lg, vertical = AmSpacing.Xs),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(AmSpacing.Xxl),
                    color = content,
                )
            } else {
                if (icon != null) {
                    AmIcon(
                        imageVector = icon,
                        contentDescription = null,
                        size = AmMetrics.IconSizeCompact,
                        tint = content,
                    )
                }
                Text(
                    label,
                    style = AmTextStyles.ButtonLabel,
                    color = content,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

/**
 * Acción compacta de fila/tarjeta (referencia historial: "Compartir",
 * "Ver QR"): píldora tonal de ~36 dp visuales con glifo y etiqueta 13,
 * sobre zona interactiva de 48 dp. [prominent] tiñe el texto de acento.
 */
@Composable
fun AmCompactAction(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    /** Texto de acento (acción destacada de la tarjeta). */
    prominent: Boolean = false,
    /** Color explícito del texto (p. ej. heredado de un banner de tono). */
    color: Color? = null,
    /** Peso del texto (p. ej. SemiBold para la acción principal del banner). */
    fontWeight: FontWeight? = null,
) {
    Box(
        modifier = modifier
            .defaultMinSize(minHeight = AmMetrics.ControlMinSize)
            .clickable(role = Role.Button, onClickLabel = label, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .heightIn(min = AmMetrics.ChipCompactHeight + AmSpacing.Xs)
                .background(AmColor.SurfaceContainerLow, AmShapes.Small),
        )
        Row(
            modifier = Modifier.padding(horizontal = AmSpacing.Sm, vertical = AmSpacing.Xs),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AmSpacing.Xs),
        ) {
            icon?.let {
                AmIcon(
                    imageVector = it,
                    contentDescription = null,
                    size = AmMetrics.IconSizeCompact,
                    tint = color ?: if (prominent) AmColor.AccentText else AmColor.TextSecondary,
                )
            }
            Text(
                label,
                style = AmTextStyles.BodySmall,
                fontWeight = fontWeight ?: FontWeight.Medium,
                color = color ?: if (prominent) AmColor.AccentText else AmColor.TextSecondary,
            )
        }
    }
}

/**
 * Nivel 3 — Acción terciaria.
 * Alternativas contextuales, navegación auxiliar y acciones de menor prioridad.
 * Acción de texto: `TextButton` con área táctil de 48 dp — la etiqueta
 * completa mantiene su target accesible sin peso visual de botón.
 * [color] lo anula el llamador cuando la acción vive sobre un fondo
 * donde el acento no cumple 4.5:1 (p. ej. dentro de un banner de tono,
 * que hereda el color de contenido del tono — par garantizado).
 */
/** Variante visual de [AmTextAction]: acento (por defecto), neutra o destructiva. */
enum class AmTextActionVariant { Default, Neutral, Danger }

@Composable
fun AmTextAction(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    /** Color explícito (p. ej. heredado de un banner de tono). Tiene
     *  prioridad sobre [variant]. */
    color: Color? = null,
    /** Variante: Default (acento) o Danger (destructiva). Prohíbe el uso de
     *  grises (TextSecondary/TextTertiary) como color de acción. */
    variant: AmTextActionVariant = AmTextActionVariant.Default,
    /** Padding interno del TextButton. Por defecto el canónico (16 dp
     *  horizontal). Pásale `PaddingValues(AmSpacing.None)` para alinear el
     *  texto al borde de la superficie contenedora. */
    contentPadding: PaddingValues = PaddingValues(
        horizontal = AmSpacing.Md,
        vertical = AmSpacing.None,
    ),
) {
    val actionColor = color ?: when (variant) {
        AmTextActionVariant.Default -> AmColor.AccentText
        AmTextActionVariant.Neutral -> AmColor.TextPrimary
        AmTextActionVariant.Danger -> AmColor.Danger
    }
    val fontWeight = when (variant) {
        AmTextActionVariant.Neutral -> FontWeight.SemiBold
        else -> null
    }
    TextButton(
        modifier = modifier.defaultMinSize(minHeight = AmMetrics.ControlMinSize),
        onClick = onClick,
        enabled = enabled,
        shape = AmShapes.Small,
        contentPadding = contentPadding,
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
                fontWeight = fontWeight,
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
 * Píldora de filtro (cápsula tipo CapsuleTabs de la referencia):
 * inactiva con relleno box (`SurfaceSecondary`) SIN borde; activa sólida
 * de acento (par garantizado). Etiqueta 15; conteo opcional en badge de
 * Tag. Zona interactiva de 48 dp — la superficie visible es menor que el
 * target (mandato de compactación). La selección también vive en la
 * semántica.
 */
@Composable
fun AmFilterChip(
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    /** Conteo real junto a la etiqueta (p. ej. "Todas · 18"). */
    count: Int? = null,
    /** Glifo decorativo de la píldora (p. ej. favorito). */
    icon: ImageVector? = null,
    /** Descripción de estado accesible (p. ej. "Seleccionado"); copy del
     * llamador. La selección también vive en la semántica `selected`. */
    stateDescription: String? = null,
) {
    // Zona interactiva: 48 dp aunque la píldora visible sea compacta.
    Row(
        modifier = modifier
            .defaultMinSize(minHeight = AmMetrics.ControlMinSize)
            .clickable(
                enabled = enabled,
                role = Role.Button,
                onClickLabel = label,
                onClick = onClick,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier
                .heightIn(min = AmMetrics.ChipCompactHeight)
                .semantics {
                    this.selected = selected
                    if (selected && stateDescription != null) {
                        this.stateDescription = stateDescription
                    }
                }
                .then(
                    if (selected) {
                        Modifier.background(AmColor.Accent, AmShapes.Pill)
                    } else {
                        Modifier.background(AmColor.SurfaceSecondary, AmShapes.Pill)
                    },
                )
                .padding(horizontal = AmSpacing.Md, vertical = AmSpacing.Xs),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(AmSpacing.Xs),
        ) {
            icon?.let {
                AmIcon(
                    imageVector = it,
                    contentDescription = null,
                    size = AmMetrics.IconSizeCompact,
                    tint = if (selected) AmColor.OnAccent else AmColor.TextSecondary,
                )
            }
            Text(
                label,
                style = AmTextStyles.Label,
                color = if (selected) AmColor.OnAccent else AmColor.TextPrimary,
            )
            count?.takeIf { it != 0 }?.let {
                Box(
                    modifier = Modifier
                        .background(
                            if (selected) AmColor.OnAccent.copy(alpha = 0.25f) else AmColor.SurfaceContainerHighest,
                            AmShapes.ExtraSmall,
                        )
                        .padding(horizontal = AmSpacing.Xs),
                ) {
                    Text(
                        it.toString(),
                        style = AmTextStyles.Tag,
                        color = if (selected) AmColor.OnAccent else AmColor.TextSecondary,
                    )
                }
            }
        }
    }
}

/**
 * Opción del control segmentado: etiqueta SIEMPRE (es la señal de texto),
 * glifo decorativo opcional, badge de conteo opcional y tono del estado
 * activo opcional (p. ej. cobro en success) — el tono activo comunica el
 * significado del segmento.
 */
data class AmSegmentedOption(
    val label: String,
    val icon: ImageVector? = null,
    /** Badge de conteo pequeño (p. ej. "5"). */
    val count: Int? = null,
    /** Color de TEXTO de la opción activa; null = acento del sistema. */
    val activeTone: AmTone? = null,
)

/**
 * Control segmentado (Segmented de la referencia): pista `SurfaceSecondary`
 * (fill-content) con la opción activa sobre píldora `Surface`, radios 2.
 * Mutuamente excluyente por semántica (`selectableGroup` + rol radio).
 * Cada opción conserva el objetivo táctil de 48 dp.
 */
@Composable
fun AmSegmentedControl(
    options: List<AmSegmentedOption>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    require(options.isNotEmpty()) { "AmSegmentedControl necesita al menos una opción" }
    require(selectedIndex in options.indices) { "AmSegmentedControl: índice fuera de rango" }
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(AmColor.SurfaceSecondary, AmShapes.ExtraSmall)
            .selectableGroup()
            .padding(AmSpacing.Xs),
        horizontalArrangement = Arrangement.spacedBy(AmSpacing.Xs),
    ) {
        options.forEachIndexed { index, option ->
            val selected = index == selectedIndex
            val activeColor = option.activeTone?.content() ?: AmColor.AccentText
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(AmShapes.ExtraSmall)
                    .then(
                        if (selected) {
                            Modifier.background(AmColor.Surface, AmShapes.ExtraSmall)
                        } else {
                            Modifier
                        },
                    )
                    .selectable(
                        selected = selected,
                        role = Role.RadioButton,
                        onClick = { onSelect(index) },
                    )
                    .defaultMinSize(minHeight = AmMetrics.ControlMinSize),
                contentAlignment = Alignment.Center,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(AmSpacing.Xs),
                ) {
                    option.icon?.let {
                        AmIcon(
                            imageVector = it,
                            contentDescription = null,
                            size = AmMetrics.IconSizeCompact,
                            tint = if (selected) activeColor else AmColor.TextSecondary,
                        )
                    }
                    Text(
                        option.label,
                        style = AmTextStyles.Label,
                        color = if (selected) activeColor else AmColor.TextSecondary,
                        maxLines = 1,
                    )
                    option.count?.let {
                        Box(
                            modifier = Modifier
                                .background(
                                    if (selected) activeColor.copy(alpha = 0.2f) else AmColor.SurfaceContainerHighest,
                                    AmShapes.ExtraSmall,
                                )
                                .padding(horizontal = AmSpacing.Xs),
                        ) {
                            Text(
                                it.toString(),
                                style = AmTextStyles.Tag,
                                color = if (selected) activeColor else AmColor.TextSecondary,
                            )
                        }
                    }
                }
            }
        }
    }
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


