package com.agendaqr.core.ui.lab

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.core.ui.theme.XauxaMetrics
import com.agendaqr.core.ui.theme.XauxaSpacing
import com.agendaqr.core.ui.theme.XauxaType

/**
 * Token specimens for the Xauxa foundations. Each specimen renders the real
 * token values so the lab can be used to review the visual language before
 * building or touching production screens. This render layer is the single
 * mapping point between a foundation id and its real token objects.
 */
@Composable
internal fun LabFoundationPreview(foundationId: String, modifier: Modifier = Modifier) {
    LabPanel(
        title = "Specimen de tokens",
        subtitle = "Valores reales de la capa de tokens consumida por producción",
        modifier = modifier,
    ) {
        when (foundationId) {
            "xauxa-color" -> ColorSpecimen()
            "xauxa-spacing" -> SpacingSpecimen()
            "xauxa-metrics" -> MetricsSpecimen()
            "xauxa-type" -> TypeSpecimen()
            "xauxa-motion" -> MotionSpecimen()
            "xauxa-focus" -> FocusSpecimen()
        }
    }
}

private data class LabColorEntry(val name: String, val color: Color, val role: String)

@Composable
private fun labColorEntries(): List<LabColorEntry> = listOf(
    LabColorEntry("Background", XauxaColor.Background, "Fondo de pantalla"),
    LabColorEntry("Surface", XauxaColor.Surface, "Fondo de superficies"),
    LabColorEntry("Surface2", XauxaColor.Surface2, "Superficie secundaria y fondo de banner"),
    LabColorEntry("Surface3", XauxaColor.Surface3, "Tercera superficie (propuesta)"),
    LabColorEntry("Border", XauxaColor.Border, "Bordes estructurales de 1px"),
    LabColorEntry("TextPrimary", XauxaColor.TextPrimary, "Texto principal"),
    LabColorEntry("TextSecondary", XauxaColor.TextSecondary, "Texto secundario"),
    LabColorEntry("TextTertiary", XauxaColor.TextTertiary, "Texto terciario y deshabilitado"),
    LabColorEntry("Brand", XauxaColor.Brand, "Acento de AgendaQr"),
    LabColorEntry("BrandAccent", XauxaColor.BrandAccent, "Acento secundario de marca"),
    LabColorEntry("OnBrand", XauxaColor.OnBrand, "Contenido sobre marca"),
    LabColorEntry("White", XauxaColor.White, "Contenido sobre fondo de QR"),
    LabColorEntry("Success", XauxaColor.Success, "Semántica de éxito"),
    LabColorEntry("SuccessBg", XauxaColor.SuccessBg, "Contenedor de éxito"),
    LabColorEntry("Danger", XauxaColor.Danger, "Semántica de error/peligro"),
    LabColorEntry("DangerBg", XauxaColor.DangerBg, "Contenedor de peligro"),
    LabColorEntry("Warning", XauxaColor.Warning, "Semántica de advertencia"),
    LabColorEntry("WarningBg", XauxaColor.WarningBg, "Contenedor de advertencia"),
    LabColorEntry("Info", XauxaColor.Info, "Semántica informativa"),
    LabColorEntry("InfoBg", XauxaColor.InfoBg, "Contenedor informativo"),
    LabColorEntry("FocusRing", XauxaColor.FocusRing, "Anillo de foco visible"),
)

@Composable
private fun ColorSpecimen() {
    Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
        labColorEntries().forEach { entry ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Md),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .size(XauxaMetrics.ControlMinSize)
                        .background(entry.color)
                        .border(XauxaMetrics.Border, MaterialTheme.colorScheme.outline, RectangleShape),
                )
                Column {
                    Text(
                        "XauxaColor.${entry.name}",
                        fontSize = XauxaType.Label,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        entry.role,
                        fontSize = XauxaType.Caption,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        }
        Text(
            "No existen variantes dark de estos tokens: el tema Material oscuro no altera los componentes Xauxa.",
            fontSize = XauxaType.Caption,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

private data class LabSpacingEntry(val name: String, val value: androidx.compose.ui.unit.Dp)

private val labSpacingEntries: List<LabSpacingEntry> = listOf(
    LabSpacingEntry("None", XauxaSpacing.None),
    LabSpacingEntry("Xs", XauxaSpacing.Xs),
    LabSpacingEntry("Sm", XauxaSpacing.Sm),
    LabSpacingEntry("Md", XauxaSpacing.Md),
    LabSpacingEntry("Lg", XauxaSpacing.Lg),
    LabSpacingEntry("Xl", XauxaSpacing.Xl),
    LabSpacingEntry("Xxl", XauxaSpacing.Xxl),
    LabSpacingEntry("Xxxl", XauxaSpacing.Xxxl),
    LabSpacingEntry("Huge", XauxaSpacing.Huge),
)

@Composable
private fun SpacingSpecimen() {
    Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
        labSpacingEntries.forEach { entry ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Md),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier = Modifier
                        .width(entry.value)
                        .height(XauxaSpacing.Md)
                        .background(MaterialTheme.colorScheme.primary),
                )
                Text(
                    "XauxaSpacing.${entry.name}",
                    fontSize = XauxaType.Label,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@Composable
private fun MetricsSpecimen() {
    Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Xs)) {
        LabLabelValue("XauxaMetrics.Border", "Grosor de borde estructural (1px)")
        LabLabelValue("XauxaMetrics.BorderStrong", "Borde fuerte: marcadores, marcos y footers (2px)")
        LabLabelValue("XauxaMetrics.Focus", "Ancho del anillo de foco")
        LabLabelValue("XauxaMetrics.ControlMinSize", "Target táctil mínimo (invariante 09)")
        LabLabelValue("XauxaMetrics.ContentMaxWidth", "Ancho máximo de contenido")
        LabLabelValue("XauxaMetrics.QrPreviewSize", "Tamaño del preview de QR")
        LabLabelValue("XauxaMetrics.FavoriteIndicatorSize", "Diámetro del indicador de favorito")
        LabLabelValue("XauxaMetrics.BreakpointCompact", "Breakpoint 480 documentado por Xauxa (§07)")
        LabLabelValue("XauxaMetrics.BreakpointMedium", "Breakpoint 640 documentado por Xauxa (§07)")
    }
}

private data class LabTypeEntry(val name: String, val value: androidx.compose.ui.unit.TextUnit, val sample: String)

private val labTypeEntries: List<LabTypeEntry> = listOf(
    LabTypeEntry("Display", XauxaType.Display, "Agenda QR"),
    LabTypeEntry("Headline", XauxaType.Headline, "Destinos guardados"),
    LabTypeEntry("Title", XauxaType.Title, "Obligaciones"),
    LabTypeEntry("Body", XauxaType.Body, "La mensualidad escolar vence el día 5."),
    LabTypeEntry("Label", XauxaType.Label, "Comprobante pendiente"),
    LabTypeEntry("Caption", XauxaType.Caption, "Sincronizado hace 4 minutos"),
)

@Composable
private fun TypeSpecimen() {    Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
        labTypeEntries.forEach { entry ->
            Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Xs)) {
                Text(
                    "XauxaType.${entry.name}",
                    fontSize = XauxaType.Caption,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    entry.sample,
                    fontSize = entry.value,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
        Text(
            "Xauxa documenta Archivo (display) y Roboto (UI); la implementación usa la pila del sistema como XauxaXcan (FamilyUi) y monoespaciada para valores (FamilyMono). Tracking amplio en XauxaType.LetterSpacingWide.",
            fontSize = XauxaType.Caption,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun MotionSpecimen() {
    Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
        LabLabelValue("DurationShortMs", "${com.agendaqr.core.ui.theme.XauxaMotion.DurationShortMs}ms")
        LabLabelValue("DurationMediumMs", "${com.agendaqr.core.ui.theme.XauxaMotion.DurationMediumMs}ms")
        LabLabelValue("DurationLongMs", "${com.agendaqr.core.ui.theme.XauxaMotion.DurationLongMs}ms")
        LabLabelValue("EasingStandard", com.agendaqr.core.ui.theme.XauxaMotion.EasingStandard)
        LabLabelValue("EasingEmphasized", com.agendaqr.core.ui.theme.XauxaMotion.EasingEmphasized)
        LabLabelValue("EasingDecelerate", com.agendaqr.core.ui.theme.XauxaMotion.EasingDecelerate)
        Text(
            "Movimiento con propósito y sin loops; respetar prefers-reduced-motion del sistema.",
            fontSize = XauxaType.Caption,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
private fun FocusSpecimen() {
    Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
        LabLabelValue("XauxaColor.FocusRing", "Color del anillo (brand-accent)")
        LabLabelValue("XauxaMetrics.Focus", "Grosor del anillo (2dp)")
        LabLabelValue("XauxaMetrics.ControlMinSize", "Objetivo táctil mínimo (48dp)")
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .border(XauxaMetrics.Focus, XauxaColor.FocusRing, RectangleShape)
                .background(XauxaColor.Surface)
                .padding(XauxaSpacing.Md),
        ) {
            Text(
                "Anillo de foco de ejemplo aplicado a un contenedor.",
                fontSize = XauxaType.Label,
                color = MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}
