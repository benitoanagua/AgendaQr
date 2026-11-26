package com.agendaqr.core.ui.theme

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.AqSectionTitle
import com.agendaqr.core.ui.components.AqText
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Galería y Component Lab comparten AqComponentRegistry.
 *
 * No se mantienen previews paralelas: los ejemplos de estados/variantes
 * viven en el registro común, usan los componentes Aq* reales y heredan
 * AqTheme. Producción compone esos mismos componentes con el mismo tema.
 */
@Composable
fun AqDesignSystemGallery(darkTheme: Boolean) {
    AqTheme(darkTheme = darkTheme) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(AqSpacing.Lg),
            verticalArrangement = Arrangement.spacedBy(AqSpacing.Lg),
        ) {
            AqText(
                text = "Design System — Componentes compartidos",
                size = AqType.HeadingLg,
                fontWeight = AqWeight.SemiBold,
                color = AqColor.TextPrimary,
            )
            AqText(
                text = "Lab y producción · tema ${if (darkTheme) "oscuro" else "claro"}",
                color = AqColor.TextSecondary,
                size = AqType.BodySm,
            )
            AqComponentCategory.entries.forEach { category ->
                val entries = AqComponentRegistry.filter { it.category == category }
                if (entries.isNotEmpty()) {
                    AqSectionTitle(text = category.label)
                    Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Md)) {
                        entries.forEach { entry ->
                            AqText(
                                text = entry.name,
                                size = AqType.BodyLg,
                                fontWeight = AqWeight.SemiBold,
                                color = AqColor.TextPrimary,
                            )
                            entry.states.forEach { state ->
                                AqText(
                                    text = state.label,
                                    size = AqType.BodySm,
                                    color = AqColor.TextSecondary,
                                )
                                state.content { /* Lab supplies event logging; gallery is read-only. */ }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Preview
@Composable
internal fun AqDesignSystemGalleryPreviewLight() {
    AqDesignSystemGallery(darkTheme = false)
}

@Preview
@Composable
internal fun AqDesignSystemGalleryPreviewDark() {
    AqDesignSystemGallery(darkTheme = true)
}
