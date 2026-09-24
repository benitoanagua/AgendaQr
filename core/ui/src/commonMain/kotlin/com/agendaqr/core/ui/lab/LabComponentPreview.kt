package com.agendaqr.core.ui.lab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.XauxaEmptyState
import com.agendaqr.core.ui.components.XauxaFavoriteIndicator
import com.agendaqr.core.ui.components.XauxaLoading
import com.agendaqr.core.ui.components.XauxaPrimaryButton
import com.agendaqr.core.ui.components.XauxaQrPreview
import com.agendaqr.core.ui.components.XauxaScreen
import com.agendaqr.core.ui.components.XauxaSecondaryButton
import com.agendaqr.core.ui.components.XauxaSection
import com.agendaqr.core.ui.components.XauxaStatusBanner
import com.agendaqr.core.ui.components.XauxaTextAction
import com.agendaqr.core.ui.components.XauxaTile
import com.agendaqr.core.ui.lab.model.LabComponentContract
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.core.ui.theme.XauxaMetrics
import com.agendaqr.core.ui.theme.XauxaSpacing
import com.agendaqr.core.ui.theme.XauxaType

/**
 * Real QR image (PNG encoded as base64) used as fake data for the QrPreview
 * interactive preview. Generated offline; the lab never touches production
 * storage or the camera.
 */
internal const val LAB_SAMPLE_QR_BASE64: String =
    "iVBORw0KGgoAAAANSUhEUgAAAPAAAADwAQAAAAAWLtQ/AAABSklEQVR4XuWWQW7DMAwE9QP+" +
        "/5f8gavZpdwkQHsJctkosWTt0AcPaMPr+nes1+B5vIl7aVTvf11d2nveazAubl8zbFdQd9Jk3GhQgAyW0VPfgYFoEfwmXBBtKIjH8" +
        "xjQ/kvd4IqXpyQO0wQKnn6T5mJu/wxkbCAlHrkYAWr70rtes7sDVblYp5Pgg3K3Alk0xsVE2lC47SAoF7NiwFQy9tRHSzAmVTJr+" +
        "clXaS6eVWnjZrkftON1H4rV9pQcLSr3kYz3FvK41DRD83bIxRP13QrMOMnGkiEpCxVScwqCcXPmcxm68b6Ct2IqVmQGGjnsw/FYOR" +
        "UPIxvPcAtYhg4V5OIRcRrhrnR5MFbUan1H9IdqLj5jc/H2Man1UMYZRfmYDTKmRKG/9tKx1FBCbk/RWDqIpEXxmmt+H/88TANYj" +
        "CbrUHE0Rsrf46P4BzFAshNbQhZZAAAAAElFTkSuQmCC"

/**
 * Live interactive preview of a catalog entry.
 *
 * The real production composable is rendered with fake local data. Whenever
 * the component is interactive, its callbacks produce a visible response in
 * the lab (a state change plus an entry in the event log) instead of an empty
 * callback. Components render over XauxaColor.Background, exactly as in a
 * production screen, so light/dark gaps stay visible.
 */
@Composable
internal fun LabComponentPreview(
    contract: LabComponentContract,
    onEvent: (String) -> Unit,
    modifier: Modifier = Modifier,
) {
    LabPanel(
        title = "Preview interactivo",
        subtitle = "Componente real con datos ficticios y estado local",
        trailing = { LabBadge(if (contract.isInteractive) "Interactivo" else "Presentacional") },
        modifier = modifier,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(XauxaColor.Background)
                .padding(XauxaSpacing.Lg),
        ) {
            when (contract.id) {
                "xauxa-screen" -> ScreenPreview()
                "xauxa-section" -> SectionPreview(onEvent)
                "xauxa-tile" -> TilePreview(onEvent)
                "xauxa-primary-button" -> PrimaryButtonPreview(onEvent)
                "xauxa-secondary-button" -> SecondaryButtonPreview(onEvent)
                "xauxa-text-action" -> TextActionPreview(onEvent)
                "xauxa-status-banner" -> StatusBannerPreview()
                "xauxa-loading" -> LoadingPreview()
                "xauxa-empty-state" -> EmptyStatePreview(onEvent)
                "xauxa-favorite-indicator" -> FavoriteIndicatorPreview()
                "xauxa-qr-preview" -> QrPreviewControls()
                else -> Text(
                    "Este contrato es un grupo de tokens: revisa la especimen de la sección Fundamentos.",
                    fontSize = XauxaType.Label,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

/** Label + control row used by the preview configuration controls. */
@Composable
private fun LabControlRow(label: String, control: @Composable () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(label, fontSize = XauxaType.Label, color = MaterialTheme.colorScheme.onSurfaceVariant)
        control()
    }
}

/** Toggle control: a selected-state chip with the minimum touch target. */
@Composable
private fun LabToggle(label: String, value: Boolean, onChange: (Boolean) -> Unit) {
    FilterChip(
        selected = value,
        onClick = { onChange(!value) },
        label = { Text(if (value) "$label: sí" else "$label: no") },
        modifier = Modifier.defaultMinSize(minHeight = XauxaMetrics.ControlMinSize),
    )
}

@Composable
private fun LabTextInput(
    label: String,
    value: String,
    singleLine: Boolean = true,
    onChange: (String) -> Unit,
) {
    OutlinedTextField(
        value = value,
        onValueChange = onChange,
        modifier = Modifier.fillMaxWidth(),
        label = { Text(label) },
        singleLine = singleLine,
    )
}

@Composable
private fun ScreenPreview() {
    XauxaScreen {
        Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Lg)) {
            XauxaSection(title = "Obligaciones") {
                Text(
                    "Contenido de la pantalla sobre XauxaColor.Background.",
                    fontSize = XauxaType.Body,
                    color = XauxaColor.TextPrimary,
                )
            }
            XauxaTile {
                Text(
                    "Mensualidad escolar · Bs 450",
                    modifier = Modifier.padding(XauxaSpacing.Lg),
                    fontSize = XauxaType.Body,
                    color = XauxaColor.TextPrimary,
                )
            }
        }
    }
}

@Composable
private fun SectionPreview(onEvent: (String) -> Unit) {
    var withTrailing by rememberSaveable { mutableStateOf(true) }
    var clicks by rememberSaveable { mutableStateOf(0) }
    Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Md)) {
        LabControlRow("Acción contextual (trailing)") {
            LabToggle("trailing", withTrailing) { withTrailing = it }
        }
        XauxaSection(
            title = "Obligaciones",
            trailing = if (withTrailing) {
                {
                    XauxaTextAction(
                        label = "Ver todo (${clicks})",
                        onClick = {
                            clicks++
                            onEvent("XauxaSection trailing XauxaTextAction.onClick #$clicks")
                        },
                    )
                }
            } else {
                null
            },
        ) {
            Text(
                "Contenido agrupado por la sección.",
                fontSize = XauxaType.Body,
                color = XauxaColor.TextPrimary,
            )
        }
    }
}

@Composable
private fun TilePreview(onEvent: (String) -> Unit) {
    var interactive by rememberSaveable { mutableStateOf(true) }
    var clicks by rememberSaveable { mutableStateOf(0) }
    Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Md)) {
        LabControlRow("Tile con onClick") {
            LabToggle("interactivo", interactive) { interactive = it }
        }
        XauxaTile(
            onClick = if (interactive) {
                {
                    clicks++
                    onEvent("XauxaTile.onClick #$clicks")
                }
            } else {
                null
            },
        ) {
            Text(
                "Mensualidad escolar · Bs 450" + if (interactive) " · activaciones: $clicks" else "",
                modifier = Modifier.padding(XauxaSpacing.Lg),
                fontSize = XauxaType.Body,
                color = XauxaColor.TextPrimary,
            )
        }
    }
}

@Composable
private fun PrimaryButtonPreview(onEvent: (String) -> Unit) {
    var label by rememberSaveable { mutableStateOf("Añadir QR") }
    var enabled by rememberSaveable { mutableStateOf(true) }
    var clicks by rememberSaveable { mutableStateOf(0) }
    Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Md)) {
        LabTextInput("Texto de etiqueta", label) { label = it }
        LabControlRow("enabled") { LabToggle("enabled", enabled) { enabled = it } }
        XauxaPrimaryButton(
            label = label,
            onClick = {
                clicks++
                onEvent("XauxaPrimaryButton.onClick #$clicks")
            },
            enabled = enabled,
        )
        Text(
            "Activaciones observadas: $clicks",
            fontSize = XauxaType.Caption,
            color = XauxaColor.TextSecondary,
        )
    }
}

@Composable
private fun SecondaryButtonPreview(onEvent: (String) -> Unit) {
    var label by rememberSaveable { mutableStateOf("Escanear QR") }
    var enabled by rememberSaveable { mutableStateOf(true) }
    var clicks by rememberSaveable { mutableStateOf(0) }
    Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Md)) {
        LabTextInput("Texto de etiqueta", label) { label = it }
        LabControlRow("enabled") { LabToggle("enabled", enabled) { enabled = it } }
        XauxaSecondaryButton(
            label = label,
            onClick = {
                clicks++
                onEvent("XauxaSecondaryButton.onClick #$clicks")
            },
            enabled = enabled,
        )
        Text(
            "Activaciones observadas: $clicks",
            fontSize = XauxaType.Caption,
            color = XauxaColor.TextSecondary,
        )
    }
}

@Composable
private fun TextActionPreview(onEvent: (String) -> Unit) {
    var label by rememberSaveable { mutableStateOf("Editar") }
    var clicks by rememberSaveable { mutableStateOf(0) }
    Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Md)) {
        LabTextInput("Texto de etiqueta", label) { label = it }
        XauxaTextAction(
            label = label,
            onClick = {
                clicks++
                onEvent("XauxaTextAction.onClick #$clicks")
            },
        )
        Text(
            "Activaciones observadas: $clicks. La API no expone enabled: no existe estado deshabilitado.",
            fontSize = XauxaType.Caption,
            color = XauxaColor.TextSecondary,
        )
    }
}

@Composable
private fun StatusBannerPreview() {
    var message by rememberSaveable { mutableStateOf("El comprobante está pendiente de revisar") }
    var danger by rememberSaveable { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Md)) {
        LabTextInput("Mensaje", message) { message = it }
        LabControlRow("danger") { LabToggle("danger", danger) { danger = it } }
        XauxaStatusBanner(message = message, danger = danger)
    }
}

@Composable
private fun LoadingPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Md)) {
        XauxaLoading()
        Text(
            "Carga indeterminada: la API no expone progreso ni mensaje.",
            fontSize = XauxaType.Caption,
            color = XauxaColor.TextSecondary,
        )
    }
}

@Composable
private fun EmptyStatePreview(onEvent: (String) -> Unit) {
    var title by rememberSaveable { mutableStateOf("No hay obligaciones registradas") }
    var actionLabel by rememberSaveable { mutableStateOf("Añadir obligación") }
    var clicks by rememberSaveable { mutableStateOf(0) }
    Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Md)) {
        LabTextInput("Título", title) { title = it }
        LabTextInput("Etiqueta de la acción", actionLabel) { actionLabel = it }
        XauxaEmptyState(
            title = title,
            actionLabel = actionLabel,
            onAction = {
                clicks++
                onEvent("XauxaEmptyState.onAction #$clicks")
            },
        )
    }
}

@Composable
private fun FavoriteIndicatorPreview() {
    var favorite by rememberSaveable { mutableStateOf(true) }
    Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Md)) {
        LabControlRow("favorite") { LabToggle("favorite", favorite) { favorite = it } }
        Row(
            horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            XauxaFavoriteIndicator(favorite = favorite)
            Text(
                if (favorite) "Marcado como favorito" else "Sin marcar",
                fontSize = XauxaType.Label,
                color = XauxaColor.TextPrimary,
            )
        }
        Text(
            "Indicador de solo presentación: Xauxa documenta un toggle interactivo, aún no implementado.",
            fontSize = XauxaType.Caption,
            color = XauxaColor.TextSecondary,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun QrPreviewControls() {
    var encodedQr by rememberSaveable { mutableStateOf(LAB_SAMPLE_QR_BASE64) }
    Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Md)) {
        LabTextInput("encodedQr (base64 del PNG)", encodedQr, singleLine = false) { encodedQr = it }
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Xs),
            verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Xs),
        ) {
            FilterChip(
                selected = encodedQr == LAB_SAMPLE_QR_BASE64,
                onClick = { encodedQr = LAB_SAMPLE_QR_BASE64 },
                label = { Text("QR de ejemplo") },
                modifier = Modifier.defaultMinSize(minHeight = XauxaMetrics.ControlMinSize),
            )
            FilterChip(
                selected = encodedQr == "dato-no-png",
                onClick = { encodedQr = "dato-no-png" },
                label = { Text("Dato inválido") },
                modifier = Modifier.defaultMinSize(minHeight = XauxaMetrics.ControlMinSize),
            )
            FilterChip(
                selected = encodedQr.isEmpty(),
                onClick = { encodedQr = "" },
                label = { Text("Vacío") },
                modifier = Modifier.defaultMinSize(minHeight = XauxaMetrics.ControlMinSize),
            )
        }
        XauxaQrPreview(encodedQr = encodedQr)
        Text(
            "En Android el QR de ejemplo se decodifica a bitmap; dato inválido o vacío muestra placeholder. " +
                "En iOS y en el host web (Wasm) la implementación aún muestra placeholder.",
            fontSize = XauxaType.Caption,
            color = XauxaColor.TextSecondary,
        )
    }
}
