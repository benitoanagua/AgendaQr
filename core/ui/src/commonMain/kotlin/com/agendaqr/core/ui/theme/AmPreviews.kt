package com.agendaqr.core.ui.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import com.agendaqr.core.ui.components.AmHeading
import com.agendaqr.core.ui.components.AmPrimaryButton
import com.agendaqr.core.ui.components.AmSecondaryButton
import com.agendaqr.core.ui.components.AmStatusBanner
import com.agendaqr.core.ui.components.AmText
import com.agendaqr.core.ui.components.AmTone
import org.jetbrains.compose.ui.tooling.preview.Preview

/** Galería mínima para revisar los roles semánticos en los tres estados. */
@Composable
private fun AmTokenGallery(darkTheme: Boolean) {
    AmTheme(darkTheme = darkTheme) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(AmSpacing.Lg),
            verticalArrangement = Arrangement.spacedBy(AmSpacing.Md),
        ) {
            AmHeading("Sistema visual")
            AmText(
                text = "Comprobante recibido",
                color = AmColor.TextSecondary,
            )
            AmStatusBanner(
                message = "Comprobante recibido",
                tone = AmTone.Info,
            )
            AmPrimaryButton(label = "Continuar", onClick = {})
            AmSecondaryButton(label = "Cancelar", onClick = {})
        }
    }
}

@Preview
@Composable
internal fun AmTokenPreviewLight() {
    AmTokenGallery(darkTheme = false)
}

@Preview
@Composable
internal fun AmTokenPreviewDark() {
    AmTokenGallery(darkTheme = true)
}

@Preview
@Composable
internal fun AmTokenPreviewFont200() {
    CompositionLocalProvider(LocalDensity provides Density(density = 1f, fontScale = 2f)) {
        AmTokenGallery(darkTheme = false)
    }
}
