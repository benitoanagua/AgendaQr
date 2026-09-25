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
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.semantics.ToggleableState
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.core.ui.theme.XauxaMetrics
import com.agendaqr.core.ui.theme.XauxaSpacing
import com.agendaqr.core.ui.theme.XauxaType

/**
 * Tono semántico de Xauxa para componentes. Un solo acento decorativo de
 * marca (Brand); los demás colores tienen significado semántico (Regla 04,
 * ADR-0002: sin variación de acento por categoría).
 */
enum class XauxaTone {
    Neutral,
    Success,
    Danger,
    Warning,
    Info,
}

@Composable
internal fun XauxaTone.content(): androidx.compose.ui.graphics.Color = when (this) {
    XauxaTone.Neutral -> XauxaColor.TextSecondary
    XauxaTone.Success -> XauxaColor.Success
    XauxaTone.Danger -> XauxaColor.Danger
    XauxaTone.Warning -> XauxaColor.Warning
    XauxaTone.Info -> XauxaColor.Info
}

@Composable
internal fun XauxaTone.container(): androidx.compose.ui.graphics.Color = when (this) {
    XauxaTone.Neutral -> XauxaColor.Surface2
    XauxaTone.Success -> XauxaColor.SuccessBg
    XauxaTone.Danger -> XauxaColor.DangerBg
    XauxaTone.Warning -> XauxaColor.WarningBg
    XauxaTone.Info -> XauxaColor.InfoBg
}

/**
 * Anillo de foco visible dedicado (invariante 10). Todo clickable
 * personalizado lo aplica sobre su propio [MutableInteractionSource].
 */
@Composable
fun Modifier.xauxaFocusRing(source: MutableInteractionSource): Modifier {
    val focused by source.collectIsFocusedAsState()
    return then(
        if (focused) Modifier.border(XauxaMetrics.Focus, XauxaColor.FocusRing, RectangleShape)
        else Modifier,
    )
}

@Composable
fun XauxaDangerButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
) {
    androidx.compose.material3.Button(
        modifier = modifier.defaultMinSize(minHeight = XauxaMetrics.ControlMinSize),
        onClick = onClick,
        enabled = enabled && !isLoading,
        shape = RectangleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = XauxaColor.Danger,
            contentColor = XauxaColor.OnDanger,
            disabledContainerColor = XauxaColor.Surface2,
            disabledContentColor = XauxaColor.TextTertiary,
        ),
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(XauxaSpacing.Lg),
                color = XauxaColor.OnDanger,
                strokeWidth = XauxaMetrics.Border,
            )
        } else {
            Text(
                label.uppercase(),
                fontSize = XauxaType.Label,
                fontWeight = FontWeight.Bold,
                letterSpacing = XauxaType.LetterSpacingWide,
            )
        }
    }
}

/**
 * Tarjeta hero con número grande y footer de estadísticas. Compone
 * [XauxaTile]: no duplica superficie, solo la jerarquía display + footer
 * con borde superior (patrón tile-stats de Xauxa).
 */
@Composable
fun XauxaHeroCard(
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    footer: String? = null,
    onClick: (() -> Unit)? = null,
) {
    XauxaTile(modifier = modifier, onClick = onClick) {
        Column(modifier = Modifier.padding(XauxaSpacing.Lg)) {
            Text(
                value,
                fontSize = XauxaType.Display,
                fontWeight = FontWeight.Bold,
                fontFamily = XauxaType.FamilyMono,
                color = XauxaColor.TextPrimary,
            )
            Text(
                label.uppercase(),
                fontSize = XauxaType.Label,
                letterSpacing = XauxaType.LetterSpacingWide,
                color = XauxaColor.TextSecondary,
            )
            if (footer != null) {
                Box(
                    modifier = Modifier.fillMaxWidth().padding(top = XauxaSpacing.Md)
                        .background(XauxaColor.Border)
                        .height(XauxaMetrics.BorderStrong),
                )
                Text(
                    footer,
                    modifier = Modifier.padding(top = XauxaSpacing.Sm),
                    fontSize = XauxaType.Caption,
                    color = XauxaColor.TextSecondary,
                )
            }
        }
    }
}

/** Etiqueta semántica outline o sólida. Solo indicadores circulares reales pueden ser circulares. */
@Composable
fun XauxaBadge(
    text: String,
    modifier: Modifier = Modifier,
    tone: XauxaTone = XauxaTone.Neutral,
    solid: Boolean = false,
) {
    val background = if (solid) {
        if (tone == XauxaTone.Neutral) XauxaColor.Surface2 else tone.content()
    } else {
        XauxaColor.Surface
    }
    val foreground = if (solid) {
        // Contenido sobre fondo sólido: usa el on-color del rol de fondo.
        // En light todos son blancos (sin cambio visual); en dark son texto
        // oscuro sobre contenedor claro (White fijo daba 1.70:1 en dark).
        when (tone) {
            XauxaTone.Neutral -> XauxaColor.TextPrimary
            XauxaTone.Success -> XauxaColor.OnSecondary
            XauxaTone.Danger -> XauxaColor.OnDanger
            XauxaTone.Warning -> XauxaColor.OnTertiary
            XauxaTone.Info -> XauxaColor.OnBrand
        }
    } else {
        tone.content()
    }
    Box(
        modifier = modifier
            .border(BorderStroke(XauxaMetrics.Border, tone.content()), RectangleShape)
            .background(background)
            .padding(horizontal = XauxaSpacing.Sm, vertical = XauxaSpacing.Xs),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text.uppercase(),
            fontSize = XauxaType.Caption,
            fontWeight = FontWeight.SemiBold,
            letterSpacing = XauxaType.LetterSpacingWide,
            color = foreground,
        )
    }
}

/**
 * Fila de lista: separador por borde inferior y marcador semántico lateral
 * de 2px (el sistema Xauxa permite un marcador estructural de 2px).
 */
@Composable
fun XauxaListRow(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    tone: XauxaTone = XauxaTone.Neutral,
    onClick: (() -> Unit)? = null,
    trailing: (@Composable () -> Unit)? = null,
) {
    val interaction = remember { MutableInteractionSource() }
    val clickableModifier = if (onClick == null) modifier else modifier
        .clickable(interactionSource = interaction, indication = null, role = Role.Button, onClick = onClick)
        .focusable(interactionSource = interaction)
    Row(
        modifier = clickableModifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = XauxaMetrics.ControlMinSize)
            .xauxaFocusRing(interaction)
            .border(BorderStroke(XauxaMetrics.Border, XauxaColor.Border), RectangleShape),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier.width(XauxaMetrics.BorderStrong).fillMaxHeight().background(tone.content()),
        )
        Column(
            modifier = Modifier.weight(XauxaToneWeight).padding(XauxaSpacing.Sm),
        ) {
            Text(title, fontSize = XauxaType.Body, color = XauxaColor.TextPrimary)
            if (subtitle != null) {
                Text(subtitle, fontSize = XauxaType.Caption, color = XauxaColor.TextSecondary)
            }
        }
        trailing?.invoke()
    }
}

private const val XauxaToneWeight = 1f

/** Botón solo-icono con etiqueta accesible y área táctil de 48dp. */
@Composable
fun XauxaIconButton(
    contentDescription: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    content: @Composable () -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .size(XauxaMetrics.ControlMinSize)
            .semantics { this.contentDescription = contentDescription }
            .clickable(
                interactionSource = interaction,
                indication = null,
                role = Role.Button,
                enabled = enabled,
                onClick = onClick,
            )
            .focusable(interactionSource = interaction)
            .xauxaFocusRing(interaction),
        contentAlignment = Alignment.Center,
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
                label.uppercase(),
                fontSize = XauxaType.Label,
                fontWeight = FontWeight.Bold,
                letterSpacing = XauxaType.LetterSpacingWide,
            )
        },
        shape = RectangleShape,
        border = FilterChipDefaults.filterChipBorder(
            enabled = enabled,
            selected = selected,
            borderColor = XauxaColor.Border,
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

/** Chip de categoría estático: clasifica, no filtra. Sin interacción. */
@Composable
fun XauxaCategoryChip(
    label: String,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .border(BorderStroke(XauxaMetrics.Border, XauxaColor.Border), RectangleShape)
            .background(XauxaColor.Surface2)
            .padding(horizontal = XauxaSpacing.Sm, vertical = XauxaSpacing.Xs),
        contentAlignment = Alignment.Center,
    ) {
        Text(label, fontSize = XauxaType.Caption, color = XauxaColor.TextSecondary)
    }
}

/** Entrada de texto etiquetada con estado de error. Textarea con singleLine=false. */
@Composable
fun XauxaTextInput(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isError: Boolean = false,
    errorMessage: String? = null,
    singleLine: Boolean = true,
    minLines: Int = 1,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Xs),
    ) {
        Text(label, fontSize = XauxaType.Label, fontWeight = FontWeight.SemiBold, color = XauxaColor.TextPrimary)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth().defaultMinSize(minHeight = XauxaMetrics.ControlMinSize),
            enabled = enabled,
            isError = isError,
            singleLine = singleLine,
            minLines = minLines,
            supportingText = if (isError && errorMessage != null) {
                { Text(errorMessage, fontSize = XauxaType.Caption, color = XauxaColor.Danger) }
            } else {
                null
            },
            shape = RectangleShape,
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = XauxaType.Body),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = XauxaColor.Surface,
                unfocusedContainerColor = XauxaColor.Surface,
                disabledContainerColor = XauxaColor.Surface2,
                errorContainerColor = XauxaColor.Surface,
                focusedIndicatorColor = XauxaColor.Brand,
                unfocusedIndicatorColor = XauxaColor.Border,
                errorIndicatorColor = XauxaColor.Danger,
                focusedTextColor = XauxaColor.TextPrimary,
                unfocusedTextColor = XauxaColor.TextPrimary,
                errorTextColor = XauxaColor.TextPrimary,
            ),
        )
    }
}

/** Barra de búsqueda: entrada de una línea con limpieza opcional. */
@Composable
fun XauxaSearchBar(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Buscar",
    placeholder: String = "Buscar",
    onClear: (() -> Unit)? = null,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier.fillMaxWidth().defaultMinSize(minHeight = XauxaMetrics.ControlMinSize),
        label = { Text(label, fontSize = XauxaType.Label, color = XauxaColor.TextSecondary) },
        placeholder = { Text(placeholder, fontSize = XauxaType.Body, color = XauxaColor.TextTertiary) },
        singleLine = true,
        shape = RectangleShape,
        textStyle = androidx.compose.ui.text.TextStyle(fontSize = XauxaType.Body),
        trailingIcon = if (onClear != null && value.isNotEmpty()) {
            { XauxaTextAction(label = "Limpiar", onClick = onClear) }
        } else {
            null
        },
        colors = TextFieldDefaults.colors(
            focusedContainerColor = XauxaColor.Surface,
            unfocusedContainerColor = XauxaColor.Surface,
            focusedIndicatorColor = XauxaColor.Brand,
            unfocusedIndicatorColor = XauxaColor.Border,
            focusedTextColor = XauxaColor.TextPrimary,
            unfocusedTextColor = XauxaColor.TextPrimary,
        ),
    )
}

/**
 * Fila de ajuste con toggle cuadrado (Selector + Toggle). checked==null la
 * convierte en fila de navegación. Toda la fila es el objetivo táctil.
 */
@Composable
fun XauxaSettingRow(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    checked: Boolean? = null,
    onCheckedChange: ((Boolean) -> Unit)? = null,
    onClick: (() -> Unit)? = null,
) {
    val interaction = remember { MutableInteractionSource() }
    val action: (() -> Unit)? = when {
        onCheckedChange != null && checked != null -> ({ onCheckedChange(!checked) })
        else -> onClick
    }
    val clickableModifier = if (action == null) modifier else modifier
        .semantics {
            if (checked != null && onCheckedChange != null) {
                toggleableState = if (checked) ToggleableState.On else ToggleableState.Off
            }
        }
        .clickable(
            interactionSource = interaction,
            indication = null,
            role = if (checked != null && onCheckedChange != null) Role.Switch else Role.Button,
            onClick = action,
        )
        .focusable(interactionSource = interaction)
    Row(
        modifier = clickableModifier
            .fillMaxWidth()
            .defaultMinSize(minHeight = XauxaMetrics.ControlMinSize)
            .xauxaFocusRing(interaction)
            .border(BorderStroke(XauxaMetrics.Border, XauxaColor.Border), RectangleShape)
            .padding(XauxaSpacing.Sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm),
    ) {
        Column(modifier = Modifier.weight(XauxaToneWeight)) {
            Text(title, fontSize = XauxaType.Body, color = XauxaColor.TextPrimary)
            Text(description, fontSize = XauxaType.Caption, color = XauxaColor.TextSecondary)
        }
        if (checked != null) {
            Box(
                modifier = Modifier.size(width = XauxaMetrics.ControlMinSize, height = XauxaSpacing.Xxxl)
                    .border(BorderStroke(XauxaMetrics.Border, XauxaColor.Border), RectangleShape)
                    .background(if (checked) XauxaColor.Brand else XauxaColor.Surface2)
                    .padding(XauxaSpacing.Xs),
                contentAlignment = if (checked) Alignment.CenterEnd else Alignment.CenterStart,
            ) {
                // Pastilla del toggle: OnBrand preserva el blanco en light
                // y corrige el contraste en dark (White fijo fallaba el
                // par con Brand en dark). El estado apagado
                // (Surface2 + White en light) queda
                // pendiente de referencia oficial de Xauxa: no se inventa color.
                Box(
                    modifier = Modifier.size(XauxaSpacing.Xl)
                        .background(if (checked) XauxaColor.OnBrand else XauxaColor.White),
                )
            }
        }
    }
}

/**
 * Diálogo de confirmación destructiva con exactamente dos acciones.
 * Sin loops ni sombras: superficie plana con borde.
 */
@Composable
fun XauxaDialog(
    title: String,
    message: String? = null,
    confirmLabel: String,
    onConfirm: () -> Unit,
    dismissLabel: String? = null,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    content: (@Composable () -> Unit)? = null,
) {
    androidx.compose.material3.AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        shape = RectangleShape,
        containerColor = XauxaColor.Surface,
        title = {
            Text(
                title,
                fontSize = XauxaType.Title,
                fontWeight = FontWeight.SemiBold,
                color = XauxaColor.TextPrimary,
            )
        },
        text = {
            if (content != null) {
                content()
            } else {
                Text(
                    message.orEmpty(),
                    fontSize = XauxaType.Body,
                    color = XauxaColor.TextSecondary,
                )
            }
        },
        confirmButton = { XauxaPrimaryButton(label = confirmLabel, onClick = onConfirm) },
        dismissButton = dismissLabel?.let {
            { XauxaTextAction(label = it, onClick = onDismiss) }
        },
    )
}

/** Notificación con acción y descarte. Sin auto-cierre en loop: el llamador decide. */
@Composable
fun XauxaToast(
    message: String,
    modifier: Modifier = Modifier,
    tone: XauxaTone = XauxaTone.Neutral,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    onDismiss: (() -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .border(BorderStroke(XauxaMetrics.Border, XauxaColor.Border), RectangleShape)
            .background(tone.container())
            .padding(XauxaSpacing.Sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm),
    ) {
        Box(modifier = Modifier.width(XauxaMetrics.BorderStrong).fillMaxHeight().background(tone.content()))
        Text(
            message,
            modifier = Modifier.weight(XauxaToneWeight),
            fontSize = XauxaType.Label,
            color = XauxaColor.TextPrimary,
        )
        if (actionLabel != null && onAction != null) {
            XauxaTextAction(label = actionLabel, onClick = onAction)
        }
        if (onDismiss != null) {
            XauxaIconButton(contentDescription = "Descartar notificación", onClick = onDismiss) {
                Text("×", fontSize = XauxaType.Title, color = XauxaColor.TextSecondary)
            }
        }
    }
}

/** Banner de resultado embebido: marcador semántico + contenido + acciones. */
@Composable
fun XauxaInlineResult(
    title: String,
    modifier: Modifier = Modifier,
    meta: String? = null,
    tone: XauxaTone = XauxaTone.Info,
    actions: (@Composable () -> Unit)? = null,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .border(BorderStroke(XauxaMetrics.Border, XauxaColor.Border), RectangleShape)
            .background(tone.container())
            .padding(XauxaSpacing.Sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm),
    ) {
        Box(modifier = Modifier.width(XauxaMetrics.BorderStrong).fillMaxHeight().background(tone.content()))
        Column(modifier = Modifier.weight(XauxaToneWeight)) {
            Text(title, fontSize = XauxaType.Body, color = XauxaColor.TextPrimary)
            if (meta != null) {
                Text(meta, fontSize = XauxaType.Caption, color = XauxaColor.TextSecondary)
            }
        }
        actions?.invoke()
    }
}

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
            color = if (tone == XauxaTone.Neutral) XauxaColor.Brand else tone.content(),
            textAlign = TextAlign.Center,
        )
        Text(
            label.uppercase(),
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
) {
    Box(
        modifier = modifier.fillMaxWidth().padding(XauxaSpacing.Lg),
        contentAlignment = Alignment.Center,
    ) {
        when {
            endReached -> Text("No hay más elementos", fontSize = XauxaType.Caption, color = XauxaColor.TextSecondary)
            isLoading -> Row(
                horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(XauxaSpacing.Lg),
                    color = XauxaColor.Brand,
                    strokeWidth = XauxaMetrics.Border,
                )
                Text("Cargando…", fontSize = XauxaType.Label, color = XauxaColor.TextSecondary)
            }
            onLoadMore != null -> XauxaSecondaryButton(label = "Cargar más", onClick = onLoadMore)
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
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    val interaction = remember { MutableInteractionSource() }
    Box(
        modifier = modifier
            .size(XauxaMetrics.ControlMinSize)
            .semantics {
                this.contentDescription = contentDescription ?: if (favorite) "Quitar de favoritos" else "Marcar como favorito"
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
                .border(BorderStroke(XauxaMetrics.Border, XauxaColor.Border), RectangleShape),
        )
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
            title.uppercase(),
            modifier = Modifier.weight(XauxaToneWeight),
            fontSize = XauxaType.Title,
            fontWeight = FontWeight.Bold,
            letterSpacing = XauxaType.LetterSpacingWide,
            color = XauxaColor.OnBrand,
        )
        if (status != null) {
            Text(
                status.uppercase(),
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
    hint: String = "Encuadre el código QR",
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(BorderStroke(XauxaMetrics.Border, XauxaColor.Border), RectangleShape)
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
                    .border(BorderStroke(XauxaMetrics.BorderStrong, XauxaColor.Brand), RectangleShape),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    if (scanning) "Escaneando…" else hint,
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
) {
    val interaction = remember { MutableInteractionSource() }
    val clickableModifier = if (onSelect == null) modifier else modifier
        .clickable(interactionSource = interaction, indication = null, role = Role.Button, onClick = onSelect)
        .focusable(interactionSource = interaction)
    Column(
        modifier = clickableModifier
            .fillMaxWidth()
            .xauxaFocusRing(interaction)
            .border(BorderStroke(XauxaMetrics.Border, XauxaColor.Border), RectangleShape)
            .background(XauxaColor.Surface)
            .padding(XauxaSpacing.Lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm),
    ) {
        when {
            isLoading -> {
                CircularProgressIndicator(color = XauxaColor.Brand, strokeWidth = XauxaMetrics.Border)
                Text("Cargando archivo…", fontSize = XauxaType.Label, color = XauxaColor.TextSecondary)
            }
            fileName != null -> {
                Text(fileName, fontSize = XauxaType.Body, color = XauxaColor.TextPrimary, textAlign = TextAlign.Center)
                if (onClear != null) XauxaTextAction(label = "Quitar", onClick = onClear)
            }
            else -> {
                Text("Toque para seleccionar un archivo", fontSize = XauxaType.Body, color = XauxaColor.TextPrimary, textAlign = TextAlign.Center)
                Text("PNG, JPG o PDF · máx. 10 MB", fontSize = XauxaType.Caption, color = XauxaColor.TextSecondary, textAlign = TextAlign.Center)
            }
        }
        if (error != null) {
            Text(error, fontSize = XauxaType.Caption, color = XauxaColor.Danger, textAlign = TextAlign.Center)
        }
    }
}
