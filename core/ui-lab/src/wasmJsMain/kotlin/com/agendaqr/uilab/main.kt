package com.agendaqr.uilab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeViewport
import com.agendaqr.core.ui.components.AqButton
import com.agendaqr.core.ui.components.AqButtonSize
import com.agendaqr.core.ui.components.AqButtonVariant
import com.agendaqr.core.ui.theme.AqDesignSystemGallery
import com.agendaqr.core.ui.theme.AqSpacing
import kotlinx.browser.document

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    ComposeViewport(document.getElementById("app")!!) {
        AqComponentLab()
    }
}

/**
 * v1 del lab: la galería completa del design system con conmutador
 * claro/oscuro. La versión completa (registro por componente, búsqueda,
 * escalas y viewport) se construye sobre este shell.
 */
@Composable
private fun AqComponentLab() {
    var darkTheme by remember { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AqSpacing.Md, vertical = AqSpacing.Sm),
            horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AqButton(
                label = "Claro",
                onClick = { darkTheme = false },
                variant = if (!darkTheme) AqButtonVariant.Primary else AqButtonVariant.Outline,
                size = AqButtonSize.Small,
            )
            AqButton(
                label = "Oscuro",
                onClick = { darkTheme = true },
                variant = if (darkTheme) AqButtonVariant.Primary else AqButtonVariant.Outline,
                size = AqButtonSize.Small,
            )
        }
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
        ) {
            AqDesignSystemGallery(darkTheme = darkTheme)
        }
    }
}
