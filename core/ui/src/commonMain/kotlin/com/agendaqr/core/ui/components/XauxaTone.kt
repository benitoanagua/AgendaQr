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
 * Tono semántico Xauxa y anillo de foco: la base compartida de los componentes.
 * (T12: dividido de XauxaExtendedComponents.kt por responsabilidad, sin cambio de comportamiento.)
 */

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
