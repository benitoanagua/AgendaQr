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
import androidx.compose.ui.semantics.toggleableState
import androidx.compose.ui.state.ToggleableState
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.core.ui.theme.XauxaMetrics
import com.agendaqr.core.ui.theme.XauxaSpacing
import com.agendaqr.core.ui.theme.XauxaType


/**
 * Listas y tarjetas Xauxa: hero, badge y fila con marcador semántico.
 * (T12: dividido de XauxaExtendedComponents.kt por responsabilidad, sin cambio de comportamiento.)
 */

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
                label,
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
            text,
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
            // La altura de la fila la fija su CONTENIDO (48dp mínimo): sin
            // esto, el marcador `fillMaxHeight` dentro de un contenedor con
            // alto no acotado (slot scrolleable de un diálogo) estira la
            // fila a TODO el alto disponible y empuja a las demás fuera
            // del pliegue (hallado en runtime: el selector S08 solo
            // mostraba la primera fila).
            .height(androidx.compose.foundation.layout.IntrinsicSize.Min)
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

internal const val XauxaToneWeight = 1f
