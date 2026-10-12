package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import com.agendaqr.core.ui.theme.XauxaShapes
import com.agendaqr.core.ui.theme.XauxaSpacing
import com.agendaqr.core.ui.theme.XauxaTextStyles
import com.agendaqr.core.ui.theme.XauxaType

/**
 * Marco de medios (QR, comprobante): contenedor tonal [XauxaColor.Surface2]
 * con la forma media de tokens. El límite del contenido es el bloque
 * tonal; el fondo interno del contenido (p. ej. el lienzo claro del QR,
 * [XauxaColor.QrCanvas]) lo aporta quien lo dibuja.
 */
@Composable
fun XauxaMediaFrame(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = modifier.background(XauxaColor.Surface2, XauxaShapes.Medium),
        contentAlignment = Alignment.Center,
    ) {
        content()
    }
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
            // Encabezado de sección estándar (§12 V1.2).
            XauxaSectionHeader(text = title)
            trailing?.invoke()
        }
        Spacer(Modifier.height(XauxaSpacing.Md))
        content()
    }
}

@Composable
fun XauxaPrimaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    /** stateDescription de "ocupado" (texto del llamador, §5). */
    busyDescription: String? = null,
    /** Glifo decorativo del mapeo minimo (M12, p. ej. Galeria en S02):
     * la etiqueta visible sigue siendo el nombre accesible (§11). */
    icon: ImageVector? = null,
) {
    XauxaLoadingButton(
        label = label,
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        isLoading = isLoading,
        busyDescription = busyDescription,
        icon = icon,
        containerColor = XauxaColor.Brand,
        contentColor = XauxaColor.OnBrand,
        disabledContainerColor = XauxaColor.Surface3,
        disabledContentColor = XauxaColor.TextTertiary,
    )
}

/**
 * Cuerpo compartido de los botones sólidos con carga, la
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
    icon: ImageVector? = null,
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
        // Padding vertical propio a cero: la altura la fija el contenido
        // (48 dp, ver abajo), no el relleno interno de Material (8+8). El
        // horizontal conserva el de Material (24 dp = Xxl).
        contentPadding = PaddingValues(
            horizontal = XauxaSpacing.Xxl,
            vertical = XauxaSpacing.None,
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
            // semántico del M3 (el floor externo no lo alcanza en todos los
            // árboles) y con métricas degeneradas el contenido mide 44.
            Row(
                modifier = Modifier.defaultMinSize(minHeight = XauxaMetrics.ControlMinSize)
                    .then(if (isLoading) Modifier.alpha(0f) else Modifier),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Xs),
            ) {
                if (!isLoading && icon != null) {
                    XauxaIcon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = contentColor,
                    )
                }
                Text(
                    label,
                    style = XauxaTextStyles.ButtonLabel,
                )
            }
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(XauxaSpacing.Xxl),
                    color = contentColor,
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
    // §12 V1.2: la acción secundaria es `OutlinedButton` estándar; el
    // borde de reposo es el del tema (outline = borderVariant, >= 3:1 en
    // claro y oscuro, verificado por XauxaSchemeTest).
    OutlinedButton(
        modifier = modifier.defaultMinSize(minHeight = XauxaMetrics.ControlMinSize),
        onClick = onClick,
        enabled = enabled && !isLoading,
        // Igual que el primario: padding vertical cero (el horizontal
        // conserva el de Material, 12 dp = Md) y 48 dp en el contenido.
        contentPadding = PaddingValues(
            horizontal = XauxaSpacing.Md,
            vertical = XauxaSpacing.None,
        ),
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(XauxaSpacing.Xxl),
                color = XauxaColor.Brand,
            )
        } else {
            Row(
                modifier = Modifier.defaultMinSize(minHeight = XauxaMetrics.ControlMinSize),
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
                    color = if (enabled) XauxaColor.TextPrimary else XauxaColor.TextTertiary,
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
    enabled: Boolean = true,
    /**
     * color del texto/icono. Por defecto brandText; el
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
        enabled = enabled,
        // 48 dp determinísticos como en los botones (ver XauxaLoadingButton).
        contentPadding = PaddingValues(
            horizontal = XauxaSpacing.Md,
            vertical = XauxaSpacing.None,
        ),
    ) {
        Row(
            modifier = Modifier.defaultMinSize(minHeight = XauxaMetrics.ControlMinSize),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Xs),
        ) {
            if (icon != null) {
                XauxaIcon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (enabled) actionColor else XauxaColor.TextTertiary,
                )
            }
            Text(
                label,
                style = XauxaTextStyles.ButtonLabel,
                color = if (enabled) actionColor else XauxaColor.TextTertiary,
            )
        }
    }
}

/**
 * Banner de resultado embebido: marcador semántico + contenido + acciones.
 *
 * El tono se expresa SIEMPRE con [tone] (el parámetro booleano
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
    /** copy del descarte desde el llamador (core/ui no hardcodea). */
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
            // Sin borde de reposo: el bloque tonal ya define el límite.
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
        // hacer) antes que el descarte opcional. heredan
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
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    /**
     * Acción primaria del estado vacío. Opcional: cuando la acción viable
     * vive en el chrome (flecha atrás, FAB) el cuerpo no la duplica; el
     * empty state solo explica qué ocurrió (§10/§6.4).
     */
    actionLabel: String? = null,
    onAction: (() -> Unit)? = null,
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(XauxaSpacing.Huge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Lg),
    ) {
        XauxaHeading(text = title, size = XauxaType.Title, fontWeight = FontWeight.SemiBold)
        subtitle?.let { Text(it, color = XauxaColor.TextSecondary, fontSize = XauxaType.Label) }
        if (actionLabel != null && onAction != null) {
            XauxaPrimaryButton(actionLabel, onAction)
        }
    }
}

@Composable
expect fun XauxaQrPreview(
    encodedQr: String,
    modifier: Modifier = Modifier,
)
