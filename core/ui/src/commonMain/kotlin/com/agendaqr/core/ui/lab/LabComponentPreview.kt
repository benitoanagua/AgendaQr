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
import com.agendaqr.core.ui.components.XauxaBadge
import com.agendaqr.core.ui.components.XauxaCategoryChip
import com.agendaqr.core.ui.components.XauxaDangerButton
import com.agendaqr.core.ui.components.XauxaDialog
import com.agendaqr.core.ui.components.XauxaEmptyState
import com.agendaqr.core.ui.components.XauxaErrorPage
import com.agendaqr.core.ui.components.XauxaFavoriteIndicator
import com.agendaqr.core.ui.components.XauxaFavoriteToggle
import com.agendaqr.core.ui.components.XauxaFileUpload
import com.agendaqr.core.ui.components.XauxaFilterChip
import com.agendaqr.core.ui.components.XauxaHeroCard
import com.agendaqr.core.ui.components.XauxaIconButton
import com.agendaqr.core.ui.components.XauxaInlineResult
import com.agendaqr.core.ui.components.XauxaListRow
import com.agendaqr.core.ui.components.XauxaLoadMoreFooter
import com.agendaqr.core.ui.components.XauxaLoading
import com.agendaqr.core.ui.components.XauxaPrimaryButton
import com.agendaqr.core.ui.components.XauxaQrPreview
import com.agendaqr.core.ui.components.XauxaScannerViewport
import com.agendaqr.core.ui.components.XauxaScreen
import com.agendaqr.core.ui.components.XauxaSearchBar
import com.agendaqr.core.ui.components.XauxaSecondaryButton
import com.agendaqr.core.ui.components.XauxaSection
import com.agendaqr.core.ui.components.XauxaSettingRow
import com.agendaqr.core.ui.components.XauxaSkeleton
import com.agendaqr.core.ui.components.XauxaStatBlock
import com.agendaqr.core.ui.components.XauxaStatusBanner
import com.agendaqr.core.ui.components.XauxaTextAction
import com.agendaqr.core.ui.components.XauxaTextInput
import com.agendaqr.core.ui.components.XauxaTile
import com.agendaqr.core.ui.components.XauxaToast
import com.agendaqr.core.ui.components.XauxaTone
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
                "xauxa-hero-card" -> HeroCardPreview(onEvent)
                "xauxa-dialog" -> DialogPreview(onEvent)
                "xauxa-primary-button" -> PrimaryButtonPreview(onEvent)
                "xauxa-secondary-button" -> SecondaryButtonPreview(onEvent)
                "xauxa-danger-button" -> DangerButtonPreview(onEvent)
                "xauxa-text-action" -> TextActionPreview(onEvent)
                "xauxa-icon-button" -> IconButtonPreview(onEvent)
                "xauxa-filter-chip" -> FilterChipPreview(onEvent)
                "xauxa-text-input" -> TextInputPreview()
                "xauxa-search-bar" -> SearchBarPreview(onEvent)
                "xauxa-setting-row" -> SettingRowPreview(onEvent)
                "xauxa-favorite-toggle" -> FavoriteTogglePreview(onEvent)
                "xauxa-toast" -> ToastPreview(onEvent)
                "xauxa-status-banner" -> StatusBannerPreview()
                "xauxa-inline-result" -> InlineResultPreview(onEvent)
                "xauxa-loading" -> LoadingPreview()
                "xauxa-skeleton" -> SkeletonPreview()
                "xauxa-empty-state" -> EmptyStatePreview(onEvent)
                "xauxa-error-page" -> ErrorPagePreview(onEvent)
                "xauxa-load-more" -> LoadMorePreview(onEvent)
                "xauxa-list-row" -> ListRowPreview(onEvent)
                "xauxa-stat-block" -> StatBlockPreview()
                "xauxa-badge" -> BadgePreview()
                "xauxa-category-chip" -> CategoryChipPreview()
                "xauxa-favorite-indicator" -> FavoriteIndicatorPreview()
                "xauxa-qr-preview" -> QrPreviewControls()
                "xauxa-scanner-viewport" -> ScannerViewportPreview()
                "xauxa-file-upload" -> FileUploadPreview(onEvent)
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
    var loading by rememberSaveable { mutableStateOf(false) }
    var clicks by rememberSaveable { mutableStateOf(0) }
    Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Md)) {
        LabTextInput("Texto de etiqueta", label) { label = it }
        LabControlRow("enabled") { LabToggle("enabled", enabled) { enabled = it } }
        LabControlRow("isLoading") { LabToggle("cargando", loading) { loading = it } }
        XauxaPrimaryButton(
            label = label,
            onClick = {
                clicks++
                onEvent("XauxaPrimaryButton.onClick #$clicks")
            },
            enabled = enabled,
            isLoading = loading,
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
    var loading by rememberSaveable { mutableStateOf(false) }
    var clicks by rememberSaveable { mutableStateOf(0) }
    Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Md)) {
        LabTextInput("Texto de etiqueta", label) { label = it }
        LabControlRow("enabled") { LabToggle("enabled", enabled) { enabled = it } }
        LabControlRow("isLoading") { LabToggle("cargando", loading) { loading = it } }
        XauxaSecondaryButton(
            label = label,
            onClick = {
                clicks++
                onEvent("XauxaSecondaryButton.onClick #$clicks")
            },
            enabled = enabled,
            isLoading = loading,
        )
        Text(
            "Activaciones observadas: $clicks",
            fontSize = XauxaType.Caption,
            color = XauxaColor.TextSecondary,
        )
    }
}

@Composable
private fun DangerButtonPreview(onEvent: (String) -> Unit) {
    var enabled by rememberSaveable { mutableStateOf(true) }
    var loading by rememberSaveable { mutableStateOf(false) }
    var clicks by rememberSaveable { mutableStateOf(0) }
    Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Md)) {
        LabControlRow("enabled") { LabToggle("enabled", enabled) { enabled = it } }
        LabControlRow("isLoading") { LabToggle("cargando", loading) { loading = it } }
        XauxaDangerButton(
            label = "Eliminar destino",
            onClick = {
                clicks++
                onEvent("XauxaDangerButton.onClick #$clicks")
            },
            enabled = enabled,
            isLoading = loading,
        )
        Text(
            "Reservado a confirmaciones destructivas dentro de XauxaDialog. Activaciones: $clicks",
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
            "Indicador de solo presentación. Para interacción usar XauxaFavoriteToggle.",
            fontSize = XauxaType.Caption,
            color = XauxaColor.TextSecondary,
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun QrPreviewControls() {    var encodedQr by rememberSaveable { mutableStateOf(LAB_SAMPLE_QR_BASE64) }
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

@Composable
private fun HeroCardPreview(onEvent: (String) -> Unit) {
    var withFooter by rememberSaveable { mutableStateOf(true) }
    var clicks by rememberSaveable { mutableStateOf(0) }
    Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Md)) {
        LabControlRow("Footer") { LabToggle("footer", withFooter) { withFooter = it } }
        XauxaHeroCard(
            value = "Bs 1.250",
            label = "Total de obligaciones",
            footer = if (withFooter) "3 pendientes · 2 al día" else null,
            onClick = {
                clicks++
                onEvent("XauxaHeroCard.onClick #$clicks")
            },
        )
    }
}

@Composable
private fun DialogPreview(onEvent: (String) -> Unit) {
    var visible by rememberSaveable { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Md)) {
        XauxaPrimaryButton(label = "Eliminar destino…", onClick = { visible = true })
        if (visible) {
            XauxaDialog(
                title = "Eliminar destino",
                message = "Se eliminará el destino y sus comprobantes. Esta acción no se puede deshacer.",
                confirmLabel = "Eliminar",
                onConfirm = {
                    visible = false
                    onEvent("XauxaDialog.onConfirm")
                },
                dismissLabel = "Cancelar",
                onDismiss = {
                    visible = false
                    onEvent("XauxaDialog.onDismiss")
                },
            )
        }
        Text(
            "Diálogo con exactamente dos acciones; la destructiva es XauxaDangerButton.",
            fontSize = XauxaType.Caption,
            color = XauxaColor.TextSecondary,
        )
    }
}

@Composable
private fun IconButtonPreview(onEvent: (String) -> Unit) {
    var clicks by rememberSaveable { mutableStateOf(0) }
    Row(
        horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        XauxaIconButton(
            contentDescription = "Copiar resultado",
            onClick = {
                clicks++
                onEvent("XauxaIconButton.onClick #$clicks")
            },
        ) {
            Text("⧉", fontSize = XauxaType.Title, color = XauxaColor.TextPrimary)
        }
        Text(
            "Área táctil de 48dp con etiqueta accesible. Activaciones: $clicks",
            fontSize = XauxaType.Caption,
            color = XauxaColor.TextSecondary,
        )
    }
}

@Composable
private fun FilterChipPreview(onEvent: (String) -> Unit) {
    var selected by rememberSaveable { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Md)) {
        Row(horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
            XauxaFilterChip(
                label = "URL",
                selected = selected,
                onClick = {
                    selected = !selected
                    onEvent("XauxaFilterChip.onClick selected=$selected")
                },
            )
            XauxaFilterChip(label = "Texto", selected = false, onClick = { onEvent("XauxaFilterChip.onClick Texto") })
        }
        Text(
            "Filtra contenido; para clasificar en lectura usar XauxaCategoryChip.",
            fontSize = XauxaType.Caption,
            color = XauxaColor.TextSecondary,
        )
    }
}

@Composable
private fun TextInputPreview() {
    var value by rememberSaveable { mutableStateOf("") }
    var error by rememberSaveable { mutableStateOf(false) }
    var multiline by rememberSaveable { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Md)) {
        LabControlRow("Error") { LabToggle("error", error) { error = it } }
        LabControlRow("Textarea") { LabToggle("multilínea", multiline) { multiline = it } }
        XauxaTextInput(
            label = "Nombre del destino",
            value = value,
            onValueChange = { value = it },
            isError = error,
            errorMessage = if (error) "El nombre es obligatorio" else null,
            singleLine = !multiline,
            minLines = if (multiline) 3 else 1,
        )
    }
}

@Composable
private fun SearchBarPreview(onEvent: (String) -> Unit) {
    var value by rememberSaveable { mutableStateOf("") }
    Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Md)) {
        XauxaSearchBar(
            value = value,
            onValueChange = { value = it },
            onClear = {
                value = ""
                onEvent("XauxaSearchBar.onClear")
            },
        )
        Text(
            "Texto actual: “$value”",
            fontSize = XauxaType.Caption,
            color = XauxaColor.TextSecondary,
        )
    }
}

@Composable
private fun SettingRowPreview(onEvent: (String) -> Unit) {
    var checked by rememberSaveable { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Md)) {
        XauxaSettingRow(
            title = "Sincronización automática",
            description = "Sube los comprobantes al conectarse",
            checked = checked,
            onCheckedChange = {
                checked = it
                onEvent("XauxaSettingRow.onCheckedChange checked=$it")
            },
        )
        XauxaSettingRow(
            title = "Acerca de",
            description = "Versión y licencias",
            onClick = { onEvent("XauxaSettingRow.onClick Acerca de") },
        )
    }
}

@Composable
private fun FavoriteTogglePreview(onEvent: (String) -> Unit) {
    var favorite by rememberSaveable { mutableStateOf(false) }
    Row(
        horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        XauxaFavoriteToggle(
            favorite = favorite,
            onCheckedChange = {
                favorite = it
                onEvent("XauxaFavoriteToggle.onCheckedChange favorite=$it")
            },
        )
        Text(
            if (favorite) "Marcado como favorito" else "Sin marcar",
            fontSize = XauxaType.Label,
            color = XauxaColor.TextPrimary,
        )
    }
    Text(
        "Control real sin integración de producto: demostración.",
        fontSize = XauxaType.Caption,
        color = XauxaColor.TextSecondary,
    )
}

@Composable
private fun ToastPreview(onEvent: (String) -> Unit) {
    var tone by rememberSaveable { mutableStateOf(XauxaTone.Success) }
    var dismissed by rememberSaveable { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Md)) {
        Row(horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Xs)) {
            XauxaFilterChip(label = "Éxito", selected = tone == XauxaTone.Success, onClick = { tone = XauxaTone.Success })
            XauxaFilterChip(label = "Error", selected = tone == XauxaTone.Danger, onClick = { tone = XauxaTone.Danger })
        }
        if (!dismissed) {
            XauxaToast(
                message = "Destino eliminado",
                tone = tone,
                actionLabel = "Deshacer",
                onAction = { onEvent("XauxaToast.onAction Deshacer") },
                onDismiss = {
                    dismissed = true
                    onEvent("XauxaToast.onDismiss")
                },
            )
        } else {
            XauxaTextAction(label = "Mostrar de nuevo", onClick = { dismissed = false })
        }
    }
}

@Composable
private fun InlineResultPreview(onEvent: (String) -> Unit) {
    XauxaInlineResult(
        title = "https://ejemplo.bo/pago/123",
        meta = "URL · hace 2 min",
        tone = XauxaTone.Info,
        actions = {
            XauxaTextAction(label = "Copiar", onClick = { onEvent("XauxaInlineResult Copiar") })
        },
    )
}

@Composable
private fun SkeletonPreview() {
    XauxaSkeleton(lines = 3)
}

@Composable
private fun ErrorPagePreview(onEvent: (String) -> Unit) {
    XauxaErrorPage(
        title = "No se pudo cargar",
        message = "La sincronización falló. Revisa tu conexión e inténtalo de nuevo.",
        actionLabel = "Reintentar",
        onAction = { onEvent("XauxaErrorPage.onAction Reintentar") },
        secondaryLabel = "Volver",
        onSecondary = { onEvent("XauxaErrorPage.onSecondary Volver") },
    )
}

@Composable
private fun LoadMorePreview(onEvent: (String) -> Unit) {
    var loading by rememberSaveable { mutableStateOf(false) }
    var end by rememberSaveable { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Md)) {
        LabControlRow("Cargando") { LabToggle("cargando", loading) { loading = it } }
        LabControlRow("Fin") { LabToggle("fin", end) { end = it } }
        XauxaLoadMoreFooter(
            isLoading = loading,
            endReached = end,
            onLoadMore = { onEvent("XauxaLoadMoreFooter.onLoadMore") },
        )
    }
}

@Composable
private fun ListRowPreview(onEvent: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Xs)) {
        XauxaListRow(
            title = "Mensualidad escolar",
            subtitle = "Vence el día 5 · Bs 450",
            tone = XauxaTone.Warning,
            onClick = { onEvent("XauxaListRow.onClick Mensualidad") },
        )
        XauxaListRow(
            title = "Pago de luz",
            subtitle = "Al día",
            tone = XauxaTone.Success,
        )
    }
}

@Composable
private fun StatBlockPreview() {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
        XauxaStatBlock(value = "24", label = "Escaneos")
        XauxaStatBlock(value = "98%", label = "Éxito", tone = XauxaTone.Success)
        XauxaStatBlock(value = "3", label = "Fallos", tone = XauxaTone.Danger)
    }
}

@Composable
private fun BadgePreview() {
    Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
        Row(horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
            XauxaBadge(text = "Pendiente", tone = XauxaTone.Warning)
            XauxaBadge(text = "Al día", tone = XauxaTone.Success, solid = true)
            XauxaBadge(text = "URL", tone = XauxaTone.Info)
        }
        Text(
            "Rectangulares por invariante 02 (XauxaXcan usa rounded-sm).",
            fontSize = XauxaType.Caption,
            color = XauxaColor.TextSecondary,
        )
    }
}

@Composable
private fun CategoryChipPreview() {
    Row(horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
        XauxaCategoryChip(label = "Servicios")
        XauxaCategoryChip(label = "Educación")
    }
}

@Composable
private fun ScannerViewportPreview() {
    var scanning by rememberSaveable { mutableStateOf(false) }
    Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Md)) {
        LabControlRow("Escaneando") { LabToggle("scanning", scanning) { scanning = it } }
        XauxaScannerViewport(scanning = scanning)
        Text(
            "Encuadre preview real; el escaneo real requiere cámara por plataforma.",
            fontSize = XauxaType.Caption,
            color = XauxaColor.TextSecondary,
        )
    }
}

@Composable
private fun FileUploadPreview(onEvent: (String) -> Unit) {
    var file by rememberSaveable { mutableStateOf<String?>(null) }
    var loading by rememberSaveable { mutableStateOf(false) }
    XauxaFileUpload(
        fileName = file,
        isLoading = loading,
        onSelect = {
            loading = true
            file = "comprobante-042.png"
            loading = false
            onEvent("XauxaFileUpload.onSelect (simulado: picker pendiente)")
        },
        onClear = {
            file = null
            onEvent("XauxaFileUpload.onClear")
        },
    )
    Text(
        "Selección simulada: el picker nativo queda pendiente por plataforma.",
        fontSize = XauxaType.Caption,
        color = XauxaColor.TextSecondary,
    )
}
