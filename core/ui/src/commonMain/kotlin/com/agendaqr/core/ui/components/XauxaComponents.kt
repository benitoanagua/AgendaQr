package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.font.FontWeight
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.core.ui.theme.XauxaMetrics
import com.agendaqr.core.ui.theme.XauxaSpacing
import com.agendaqr.core.ui.theme.XauxaTextStyles
import com.agendaqr.core.ui.theme.XauxaType

@Composable
fun XauxaScreen(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing),
        color = XauxaColor.Background,
        shape = RectangleShape,
        tonalElevation = XauxaSpacing.None,
        shadowElevation = XauxaSpacing.None,
    ) { content() }
}

@Composable
fun XauxaSection(
    title: String,
    modifier: Modifier = Modifier,
    trailing: @Composable (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    Column(modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            // V1.1 (M6): encabezado de sección pequeño en color de acento.
            XauxaSectionHeader(text = title)
            trailing?.invoke()
        }
        Spacer(Modifier.height(XauxaSpacing.Md))
        content()
    }
}

@Composable
fun XauxaTile(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit,
) {
    val interaction = remember { MutableInteractionSource() }
    val clickableModifier = if (onClick == null) modifier else modifier
        .clickable(interactionSource = interaction, indication = null, role = Role.Button, onClick = onClick)
        .focusable(interactionSource = interaction)
    // V1.1 (M9): sin borde de reposo — la separación es por bloque de
    // color (Surface2 sobre Background); el borde queda reservado al foco
    // (xauxaFocusRing) y a la selección (M9 funcional).
    Surface(
        modifier = clickableModifier
            .fillMaxWidth()
            .xauxaFocusRing(interaction),
        shape = RectangleShape,
        color = XauxaColor.Surface2,
        tonalElevation = XauxaSpacing.None,
        shadowElevation = XauxaSpacing.None,
    ) { content() }
}

@Composable
fun XauxaPrimaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    /** Fase 2: stateDescription de "ocupado" (texto del llamador, §5). */
    busyDescription: String? = null,
) {
    XauxaLoadingButton(
        label = label,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        isLoading = isLoading,
        busyDescription = busyDescription,
        containerColor = XauxaColor.Brand,
        contentColor = XauxaColor.OnBrand,
        disabledContainerColor = XauxaColor.Surface3,
        disabledContentColor = XauxaColor.TextTertiary,
    )
}

/**
 * Cuerpo compartido de los botones sólidos (Fase 2): con carga, la
 * etiqueta PERMANECE en el layout con alpha 0 y el spinner se superpone
 * centrado — sin salto de ancho; el estado "ocupado" se anuncia con
 * [busyDescription] (stateDescription, texto del llamador desde
 * AppStrings; nunca hardcodeado).
 */
@Composable
internal fun XauxaLoadingButton(
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
) {
    androidx.compose.material3.Button(
        modifier = modifier
            .defaultMinSize(minHeight = XauxaMetrics.ControlMinSize)
            .then(
                if (isLoading && busyDescription != null) {
                    Modifier.semantics { stateDescription = busyDescription }
                } else {
                    Modifier
                },
            ),
        onClick = onClick,
        enabled = enabled && !isLoading,
        shape = RectangleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = containerColor,
            contentColor = contentColor,
            disabledContainerColor = disabledContainerColor,
            disabledContentColor = disabledContentColor,
        ),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                label,
                modifier = if (isLoading) Modifier.alpha(0f) else Modifier,
                style = XauxaTextStyles.ButtonLabel,
            )
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(XauxaSpacing.Lg),
                    color = contentColor,
                    strokeWidth = XauxaMetrics.Border,
                )
            }
        }
    }
}

@Composable
fun XauxaSecondaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    icon: ImageVector? = null,
) {
    // V1.1 (M10): las secundarias son texto con icono, SIN caja (nada de
    // outline Material en reposo). La acción principal es bloque sólido de
    // acento o acción de la app bar.
    TextButton(
        modifier = modifier.defaultMinSize(minHeight = XauxaMetrics.ControlMinSize),
        onClick = onClick,
        enabled = enabled && !isLoading,
        shape = RectangleShape,
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(XauxaSpacing.Lg),
                color = XauxaColor.Brand,
                strokeWidth = XauxaMetrics.Border,
            )
        } else {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Xs),
            ) {
                if (icon != null) {
                    XauxaIcon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (enabled) XauxaColor.BrandText else XauxaColor.TextTertiary,
                    )
                }
                Text(
                    label,
                    style = XauxaTextStyles.ButtonLabel,
                    color = if (enabled) XauxaColor.BrandText else XauxaColor.TextTertiary,
                )
            }
        }
    }
}

@Composable
fun XauxaTextAction(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    /**
     * Ronda 2 (Área A): color del texto/icono. Por defecto brandText; el
     * llamador lo anula cuando la acción vive sobre un fondo donde brand
     * no cumple 4.5:1 (p. ej. las acciones DENTRO de un banner de tono,
     * que heredan el color de contenido del tono — par garantizado por
     * XauxaSchemeTest).
     */
    color: Color? = null,
) {
    val actionColor = color ?: XauxaColor.BrandText
    TextButton(
        modifier = modifier.defaultMinSize(minHeight = XauxaMetrics.ControlMinSize),
        onClick = onClick,
        shape = RectangleShape,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Xs),
        ) {
            if (icon != null) {
                XauxaIcon(imageVector = icon, contentDescription = null, tint = actionColor)
            }
            Text(
                label,
                style = XauxaTextStyles.ButtonLabel,
                color = actionColor,
            )
        }
    }
}

/**
 * Banner de resultado embebido: marcador semántico + contenido + acciones.
 *
 * El tono se expresa SIEMPRE con [tone] (T12: el parámetro booleano
 * `danger` histórico se retiró; unifica Neutral/Danger/Success/…).
 */
@Composable
fun XauxaStatusBanner(
    message: String,
    modifier: Modifier = Modifier,
    tone: XauxaTone? = null,
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
    onDismiss: (() -> Unit)? = null,
    /** Fase 4: copy del descarte desde el llamador (core/ui no hardcodea). */
    dismissLabel: String? = null,
) {
    if (onDismiss != null) {
        requireNotNull(dismissLabel) {
            "XauxaStatusBanner: onDismiss necesita dismissLabel (copy del llamador)"
        }
    }
    val resolvedTone = tone ?: XauxaTone.Neutral
    val background = resolvedTone.container()
    val foreground = resolvedTone.content()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .border(XauxaMetrics.Border, XauxaColor.Border, RectangleShape)
            .background(background)
            .semantics { liveRegion = LiveRegionMode.Polite }
            .padding(XauxaSpacing.Lg),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm),
    ) {
        // §11: el estado nunca se comunica solo por color — icono por tono.
        resolvedTone.iconVector?.let { glyph ->
            XauxaIcon(
                imageVector = glyph,
                contentDescription = null,
                tint = foreground,
            )
        }
        Text(
            message,
            modifier = Modifier.weight(1f),
            color = foreground,
            fontSize = XauxaType.Label,
        )
        // Acción del error recuperable (spec §10: el error ofrece qué
        // hacer) antes que el descarte opcional. Ronda 2 (Área A): heredan
        // el color de CONTENIDO del tono — brandText sobre el contenedor
        // Danger fallaba 4.5:1 en ambos temas (4.47 claro / 3.45 oscuro).
        if (actionLabel != null && onAction != null) {
            XauxaTextAction(label = actionLabel, onClick = onAction, color = foreground)
        }
        if (onDismiss != null && dismissLabel != null) {
            XauxaTextAction(label = dismissLabel, onClick = onDismiss, color = foreground)
        }
    }
}

@Composable
fun XauxaLoading(modifier: Modifier = Modifier, message: String? = null) {
    Column(
        modifier = modifier.fillMaxWidth().padding(XauxaSpacing.Xxxl)
            .semantics { liveRegion = LiveRegionMode.Polite },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Md),
    ) {
        CircularProgressIndicator(color = XauxaColor.Brand)
        message?.let { Text(it, color = XauxaColor.TextSecondary, fontSize = XauxaType.Label) }
    }
}

@Composable
fun XauxaEmptyState(
    title: String,
    actionLabel: String,
    onAction: () -> Unit,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(XauxaSpacing.Huge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Lg),
    ) {
        XauxaHeading(text = title, size = XauxaType.Title, fontWeight = FontWeight.SemiBold)
        subtitle?.let { Text(it, color = XauxaColor.TextSecondary, fontSize = XauxaType.Label) }
        XauxaPrimaryButton(actionLabel, onAction)
    }
}

@Composable
expect fun XauxaQrPreview(
    encodedQr: String,
    modifier: Modifier = Modifier,
)

/**
 * Indicador de favorito (Fase 2): cuadrado (radio 0, Metro) con glifo y
 * descripción accesible — el estado NO depende solo del color (§11).
 * El glifo Favorito aparece solo cuando está marcado; sin marca el
 * cuadro queda vacío y apagado.
 */
@Composable
fun XauxaFavoriteIndicator(
    favorite: Boolean,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
) {
    Box(
        modifier = modifier
            .size(XauxaMetrics.FavoriteIndicatorSize)
            .background(if (favorite) XauxaColor.Brand else XauxaColor.Surface2)
            .then(
                if (contentDescription != null) {
                    Modifier.semantics { this.contentDescription = contentDescription }
                } else {
                    Modifier
                },
            ),
        contentAlignment = Alignment.Center,
    ) {
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
