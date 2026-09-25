package com.agendaqr.core.ui.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.core.ui.theme.XauxaMetrics
import com.agendaqr.core.ui.theme.XauxaSpacing
import com.agendaqr.core.ui.theme.XauxaType

@Composable
fun XauxaScreen(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
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
            Text(
                title,
                fontSize = XauxaType.Title,
                fontWeight = FontWeight.Bold,
                letterSpacing = XauxaType.LetterSpacingWide,
                color = XauxaColor.TextPrimary,
            )
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
    Surface(
        modifier = clickableModifier
            .fillMaxWidth()
            .xauxaFocusRing(interaction)
            .border(BorderStroke(XauxaMetrics.Border, XauxaColor.Border), RectangleShape),
        shape = RectangleShape,
        color = XauxaColor.Surface,
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
) {
    androidx.compose.material3.Button(
        modifier = modifier.defaultMinSize(minHeight = XauxaMetrics.ControlMinSize),
        onClick = onClick,
        enabled = enabled && !isLoading,
        shape = RectangleShape,
        colors = ButtonDefaults.buttonColors(
            containerColor = XauxaColor.Brand,
            contentColor = XauxaColor.OnBrand,
            disabledContainerColor = XauxaColor.Surface2,
            disabledContentColor = XauxaColor.TextTertiary,
        ),
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(XauxaSpacing.Lg),
                color = XauxaColor.OnBrand,
                strokeWidth = XauxaMetrics.Border,
            )
        } else {
            Text(
                label,
                fontSize = XauxaType.Label,
                fontWeight = FontWeight.Bold,
                letterSpacing = XauxaType.LetterSpacingWide,
            )
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
) {
    OutlinedButton(
        modifier = modifier.defaultMinSize(minHeight = XauxaMetrics.ControlMinSize),
        onClick = onClick,
        enabled = enabled && !isLoading,
        shape = RectangleShape,
        border = BorderStroke(XauxaMetrics.Border, XauxaColor.Border),
    ) {
        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier.size(XauxaSpacing.Lg),
                color = XauxaColor.Brand,
                strokeWidth = XauxaMetrics.Border,
            )
        } else {
            Text(
                label,
                fontSize = XauxaType.Label,
                fontWeight = FontWeight.Bold,
                letterSpacing = XauxaType.LetterSpacingWide,
                color = XauxaColor.TextPrimary,
            )
        }
    }
}

@Composable
fun XauxaTextAction(label: String, onClick: () -> Unit, modifier: Modifier = Modifier) {
    TextButton(
        modifier = modifier.defaultMinSize(minHeight = XauxaMetrics.ControlMinSize),
        onClick = onClick,
        shape = RectangleShape,
    ) {
        Text(
            label,
            fontSize = XauxaType.Label,
            fontWeight = FontWeight.Bold,
            letterSpacing = XauxaType.LetterSpacingWide,
            color = XauxaColor.Brand,
        )
    }
}

@Composable
fun XauxaStatusBanner(
    message: String,
    modifier: Modifier = Modifier,
    danger: Boolean = false,
) {
    val background = if (danger) XauxaColor.DangerBg else XauxaColor.Surface2
    val foreground = if (danger) XauxaColor.Danger else XauxaColor.TextSecondary
    Box(
        modifier = modifier
            .fillMaxWidth()
            .border(XauxaMetrics.Border, XauxaColor.Border, RectangleShape)
            .background(background)
            .semantics { liveRegion = LiveRegionMode.Polite }
            .padding(XauxaSpacing.Lg),
    ) { Text(message, color = foreground, fontSize = XauxaType.Label) }
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
) {
    Column(
        modifier = modifier.fillMaxWidth().padding(XauxaSpacing.Huge),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Lg),
    ) {
        Text(title, fontSize = XauxaType.Title, fontWeight = FontWeight.SemiBold, color = XauxaColor.TextPrimary)
        XauxaPrimaryButton(actionLabel, onAction)
    }
}

@Composable
expect fun XauxaQrPreview(
    encodedQr: String,
    modifier: Modifier = Modifier,
)

@Composable
fun XauxaFavoriteIndicator(favorite: Boolean) {
    Box(
        modifier = Modifier.size(XauxaMetrics.FavoriteIndicatorSize).clip(CircleShape).background(if (favorite) XauxaColor.Brand else XauxaColor.Surface2),
    )
}
