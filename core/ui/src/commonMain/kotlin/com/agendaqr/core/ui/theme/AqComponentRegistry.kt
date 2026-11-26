package com.agendaqr.core.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.state.ToggleableState
import com.agendaqr.core.ui.components.AqAccordion
import com.agendaqr.core.ui.components.AqActionGroup
import com.agendaqr.core.ui.components.AqAlert
import com.agendaqr.core.ui.components.AqAmountInput
import com.agendaqr.core.ui.components.AqAppBar
import com.agendaqr.core.ui.components.AqAppBarAction
import com.agendaqr.core.ui.components.AqAuthCode
import com.agendaqr.core.ui.components.AqAvatar
import com.agendaqr.core.ui.components.AqAvatarSize
import com.agendaqr.core.ui.components.AqBottomNavigation
import com.agendaqr.core.ui.components.AqBottomSheet
import com.agendaqr.core.ui.components.AqBreadcrumb
import com.agendaqr.core.ui.components.AqButton
import com.agendaqr.core.ui.components.AqButtonSize
import com.agendaqr.core.ui.components.AqButtonVariant
import com.agendaqr.core.ui.components.AqCarousel
import com.agendaqr.core.ui.components.AqCarouselItem
import com.agendaqr.core.ui.components.AqCheckbox
import com.agendaqr.core.ui.components.AqCheckboxSet
import com.agendaqr.core.ui.components.AqCombobox
import com.agendaqr.core.ui.components.AqChip
import com.agendaqr.core.ui.components.AqChipSize
import com.agendaqr.core.ui.components.AqChipOption
import com.agendaqr.core.ui.components.AqChipRow
import com.agendaqr.core.ui.components.AqChoiceOption
import com.agendaqr.core.ui.components.AqChoiceSet
import com.agendaqr.core.ui.components.AqCircularLoader
import com.agendaqr.core.ui.components.AqCircularProgress
import com.agendaqr.core.ui.components.AqDockAction
import com.agendaqr.core.ui.components.AqDotIndicator
import com.agendaqr.core.ui.components.AqDrawer
import com.agendaqr.core.ui.components.AqDropdown
import com.agendaqr.core.ui.components.AqEmptyPlaceholder
import com.agendaqr.core.ui.components.AqErrorMessage
import com.agendaqr.core.ui.components.AqEvidenceTag
import com.agendaqr.core.ui.components.AqFavoriteToggle
import com.agendaqr.core.ui.components.AqFieldState
import com.agendaqr.core.ui.components.AqFormEntry
import com.agendaqr.core.ui.components.AqFormSection
import com.agendaqr.core.ui.components.AqGroupedAction
import com.agendaqr.core.ui.components.AqHairline
import com.agendaqr.core.ui.components.AqHelperMessage
import com.agendaqr.core.ui.components.AqIcon
import com.agendaqr.core.ui.components.AqIcons
import com.agendaqr.core.ui.components.AqKeypad
import com.agendaqr.core.ui.components.AqLinearLoader
import com.agendaqr.core.ui.components.AqLinearProgress
import com.agendaqr.core.ui.components.AqLeaderboard
import com.agendaqr.core.ui.components.AqLeaderboardEntry
import com.agendaqr.core.ui.components.AqListSection
import com.agendaqr.core.ui.components.AqLoadMore
import com.agendaqr.core.ui.components.AqMediaFrame
import com.agendaqr.core.ui.components.AqMenuItem
import com.agendaqr.core.ui.components.AqModal
import com.agendaqr.core.ui.components.AqNavDestination
import com.agendaqr.core.ui.components.AqNavLink
import com.agendaqr.core.ui.components.AqPagination
import com.agendaqr.core.ui.components.AqPillTab
import com.agendaqr.core.ui.components.AqPinDots
import com.agendaqr.core.ui.components.AqPopover
import com.agendaqr.core.ui.components.AqProgress
import com.agendaqr.core.ui.components.AqProgressBar
import com.agendaqr.core.ui.components.AqProgressBlock
import com.agendaqr.core.ui.components.AqProgressPin
import com.agendaqr.core.ui.components.AqSelect
import com.agendaqr.core.ui.components.AqPullToRefresh
import com.agendaqr.core.ui.components.AqQrPreview
import com.agendaqr.core.ui.components.AqQuickAction
import com.agendaqr.core.ui.components.AqRadio
import com.agendaqr.core.ui.components.AqScannerFrame
import com.agendaqr.core.ui.components.AqScreen
import com.agendaqr.core.ui.components.AqSearchInput
import com.agendaqr.core.ui.components.AqSectionTitle
import com.agendaqr.core.ui.components.AqSegmentedControl
import com.agendaqr.core.ui.components.AqSegmentedControlSize
import com.agendaqr.core.ui.components.AqSegmentedOption
import com.agendaqr.core.ui.components.AqSelectAll
import com.agendaqr.core.ui.components.AqSelectionToolbar
import com.agendaqr.core.ui.components.AqSkeletonBlock
import com.agendaqr.core.ui.components.AqSkeletonLine
import com.agendaqr.core.ui.components.AqSuccessCheck
import com.agendaqr.core.ui.components.AqSidebar
import com.agendaqr.core.ui.components.AqSidebarItem
import com.agendaqr.core.ui.components.AqSwipeAction
import com.agendaqr.core.ui.components.AqSwipeActions
import com.agendaqr.core.ui.components.AqSwitch
import com.agendaqr.core.ui.components.AqSyncLabels
import com.agendaqr.core.ui.components.AqSyncIndicator
import com.agendaqr.core.ui.components.AqSyncState
import com.agendaqr.core.ui.components.AqTab
import com.agendaqr.core.ui.components.AqTabBar
import com.agendaqr.core.ui.components.AqTable
import com.agendaqr.core.ui.components.AqTableColumn
import com.agendaqr.core.ui.components.AqTableRow
import com.agendaqr.core.ui.components.AqTableRowSize
import com.agendaqr.core.ui.components.AqTag
import com.agendaqr.core.ui.components.AqTagSize
import com.agendaqr.core.ui.components.AqText
import com.agendaqr.core.ui.components.AqTextArea
import com.agendaqr.core.ui.components.AqTextInput
import com.agendaqr.core.ui.components.AqTextInputGroup
import com.agendaqr.core.ui.components.AqTextInputGroupOrientation
import com.agendaqr.core.ui.components.AqTextInputSize
import com.agendaqr.core.ui.components.AqThumbnail
import com.agendaqr.core.ui.components.AqThumbnailFallback
import com.agendaqr.core.ui.components.AqThumbnailFrame
import com.agendaqr.core.ui.components.AqThumbnailSkeleton
import com.agendaqr.core.ui.components.AqToast
import com.agendaqr.core.ui.components.AqToastHost
import com.agendaqr.core.ui.components.AqToastHostState
import com.agendaqr.core.ui.components.AqTooltip
import com.agendaqr.core.ui.components.AqTone
import com.agendaqr.core.ui.components.ThumbnailResult
import com.agendaqr.core.ui.components.ThumbnailSize
import com.agendaqr.core.ui.components.ThumbnailState

/**
 * Categorías del Component Lab; coinciden con las familias de
 * [AqDesignSystemGallery].
 */
enum class AqComponentCategory(val label: String) {
    Foundations("Foundations"),
    Actions("Actions"),
    Forms("Forms"),
    Navigation("Navigation"),
    Surfaces("Surfaces"),
    DataDisplay("Data Display"),
    Feedback("Feedback"),
    Selection("Selection"),
    Media("Media"),
    MobileInteractions("Mobile Interactions"),
}

/** Estado o variante exhibible de un componente en el lab. */
class AqComponentState(
    val id: String,
    val label: String,
    /** Recibe el registrador de eventos del lab para registrar interacciones. */
    val content: @Composable (log: (String) -> Unit) -> Unit,
)

/** Entrada del registro: un componente público de `core/ui/components`. */
class AqComponentEntry(
    val id: String,
    val name: String,
    val category: AqComponentCategory,
    val states: List<AqComponentState>,
) {
    val stateLabels: List<String> get() = states.map { it.label }
}

private fun state(
    id: String,
    label: String,
    content: @Composable (log: (String) -> Unit) -> Unit,
) = AqComponentState(id = id, label = label, content = content)

private fun entry(
    name: String,
    category: AqComponentCategory,
    vararg states: AqComponentState,
) = AqComponentEntry(
    id = name,
    name = name,
    category = category,
    states = states.toList(),
)

/**
 * Registro único de estados/variantes para Component Lab y galería. Cada
 * componente público de `core/ui/components` debe tener una entrada; lo
 * verifica `verifyComponentRegistry`.
 *
 * Lab y galería renderizan estos mismos estados mediante los componentes
 * `Aq*` reales. La galería es una vista read-only del registro; no mantiene
 * composables alternativos. Producción consume los mismos componentes y
 * `AqTheme` (ADR-0053).
 */
@OptIn(ExperimentalLayoutApi::class)
val AqComponentRegistry: List<AqComponentEntry> = listOf(
    // ------------------------------------------------------------------
    // Foundations
    // ------------------------------------------------------------------
    entry(
        "AqText", AqComponentCategory.Foundations,
        state("body", "Cuerpo") { _ ->
            Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Xs)) {
                AqText(text = "Heading 3Xl (36 sp)", size = AqType.Heading3Xl, fontWeight = AqWeight.SemiBold)
                AqText(text = "Heading Lg (18 sp)", size = AqType.HeadingLg, fontWeight = AqWeight.SemiBold)
                AqText(text = "Body Md (16 sp)", size = AqType.BodyMd)
                AqText(text = "Body Sm (14 sp)", size = AqType.BodySm)
                AqText(text = "Caption (12 sp)", size = AqType.BodyXs, color = AqColor.TextSecondary)
            }
        },
        state("tonos", "Tonos") { _ ->
            Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Xs)) {
                AqText(text = "Primario", color = AqColor.TextPrimary)
                AqText(text = "Secundario", color = AqColor.TextSecondary)
                AqText(text = "Deshabilitado", color = AqColor.TextTertiary)
                AqText(text = "Énfasis", color = AqColor.EmphasisText)
            }
        },
    ),
    entry(
        "AqIcon", AqComponentCategory.Foundations,
        state("glifo", "Glifo") { _ ->
            Row(horizontalArrangement = Arrangement.spacedBy(AqSpacing.Md)) {
                AqIcon(imageVector = AqIcons.QrCode, contentDescription = "QR", size = AqMetrics.IconSize)
                AqIcon(imageVector = AqIcons.Search, contentDescription = "Buscar", size = AqMetrics.IconSize)
                AqIcon(imageVector = AqIcons.Favorite, contentDescription = "Favorito", size = AqMetrics.IconSizeCompact)
            }
        },
    ),
    entry(
        "AqHairline", AqComponentCategory.Foundations,
        state("divisor", "Divisor") { _ ->
            Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
                AqText(text = "Contenido superior", color = AqColor.TextPrimary)
                AqHairline()
                AqText(text = "Contenido inferior", color = AqColor.TextPrimary)
            }
        },
    ),
    entry(
        "AqSectionTitle", AqComponentCategory.Foundations,
        state("titulo", "Título") { _ ->
            AqSectionTitle(text = "Sección", trailing = {
                AqText(text = "Ver todo", size = AqType.BodySm, color = AqColor.EmphasisText)
            })
        },
    ),

    // ------------------------------------------------------------------
    // Actions
    // ------------------------------------------------------------------
    entry(
        "AqButton", AqComponentCategory.Actions,
        state("variantes", "Variantes") { log ->
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
                verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
            ) {
                AqButtonVariant.entries.forEach { variant ->
                    AqButton(
                        label = variant.name,
                        onClick = { log("click: ${variant.name}") },
                        variant = variant,
                        size = AqButtonSize.Medium,
                    )
                }
            }
        },
        state("tamanos", "Tamaños") { log ->
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
                verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
            ) {
                AqButtonSize.entries.forEach { size ->
                    AqButton(
                        label = size.name,
                        onClick = { log("click: ${size.name}") },
                        size = size,
                    )
                }
            }
        },
        state("estados", "Estados") { log ->
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
                verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
            ) {
                AqButton(label = "Deshabilitada", onClick = { log("click") }, enabled = false)
                AqButton(label = "Cargando", onClick = {}, isLoading = true, busyDescription = "Guardando…")
                AqButton(label = "Con icono", icon = AqIcons.Add, onClick = { log("click") })
            }
        },
        state("icono", "Solo icono") { log ->
            Row(horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
                AqButton(label = "Más", icon = AqIcons.More, onClick = { log("click") }, iconOnly = true, variant = AqButtonVariant.Text)
                AqButton(label = "Borrar", icon = AqIcons.Trash, onClick = { log("click") }, iconOnly = true)
            }
        },
        state("icono-final", "Icono a la derecha") { log ->
            // Moon v1 Button `Icon=Right`.
            Row(horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
                AqButtonVariant.entries.forEach { variant ->
                    AqButton(
                        label = variant.name,
                        onClick = { log("click: ${variant.name}") },
                        variant = variant,
                        icon = AqIcons.ChevronRight,
                        iconAtEnd = true,
                    )
                }
            }
        },
        state("ancho", "Ancho completo") { log ->
            AqButton(label = "Guardar cambios", onClick = { log("click") }, fullWidth = true)
        },
    ),
    entry(
        "AqActionGroup", AqComponentCategory.Actions,
        state("grupo", "Grupo") { log ->
            AqActionGroup(
                actions = listOf(
                    AqGroupedAction(id = "cancel", label = "Cancelar", onClick = { log("cancel") }),
                    AqGroupedAction(id = "save", label = "Guardar", onClick = { log("save") }, emphasized = true, icon = AqIcons.Add),
                ),
                fillMaxWidth = true,
            )
        },
    ),
    entry(
        "AqQuickAction", AqComponentCategory.Actions,
        state("acciones", "Acciones") { log ->
            Row(horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
                AqQuickAction(label = "Traer QR", icon = AqIcons.QrCode, onClick = { log("qr") })
                AqQuickAction(label = "Registrar", icon = AqIcons.Register, onClick = { log("register") })
                AqQuickAction(label = "Contexto", icon = AqIcons.Context, onClick = { log("context") })
            }
        },
        state("deshabilitada", "Deshabilitada") { log ->
            AqQuickAction(label = "No disponible", icon = AqIcons.More, onClick = { log("click") }, enabled = false)
        },
    ),
    entry(
        "AqNavLink", AqComponentCategory.Actions,
        state("enlace", "Enlace") { log ->
            Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Xs)) {
                AqNavLink(label = "Ver detalle", icon = AqIcons.ChevronRight, onClick = { log("click") })
                AqNavLink(label = "Deshabilitado", onClick = { log("click") }, enabled = false)
            }
        },
    ),
    entry(
        "AqDockAction", AqComponentCategory.Actions,
        state("dock", "Dock") { log ->
            Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
                AqDockAction(label = "Guardar", onClick = { log("click") })
                AqDockAction(label = "Procesando…", onClick = {}, isLoading = true, busyDescription = "Procesando")
            }
        },
    ),

    // ------------------------------------------------------------------
    // Forms
    // ------------------------------------------------------------------
    entry(
        "AqTextInput", AqComponentCategory.Forms,
        state("normal", "Normal") { log ->
            AqTextInput(label = "Correo", value = "ana@agendaqr.app", onValueChange = { log("input") })
        },
        state("estados", "Estados") { log ->
            Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
                AqTextInput(label = "Deshabilitado", value = "No editable", onValueChange = {}, enabled = false)
                AqTextInput(label = "Solo lectura", value = "Fijo", onValueChange = {}, readOnly = true)
                AqTextInput(label = "Error", value = "abc", onValueChange = {}, isError = true, errorMessage = "Valor inválido")
            }
        },
        state("vacio", "Vacío (placeholder)") { log ->
            AqTextInput(label = "Nombre", value = "", onValueChange = { log("input") }, placeholder = "Escribe tu nombre")
        },
    ),
    entry(
        "AqTextArea", AqComponentCategory.Forms,
        state("normal", "Normal") { log ->
            AqTextArea(label = "Nota", value = "Nota corta.", onValueChange = { log("input") })
        },
        state("error", "Error") { log ->
            AqTextArea(label = "Nota", value = "Inválida", onValueChange = {}, isError = true, errorMessage = "Supera el límite")
        },
        state("largo", "Contenido largo") { log ->
            AqTextArea(
                label = "Descripción",
                value = "Texto multilínea de ejemplo que crece en altura según el contenido, " +
                    "respetando la escala de fuente del sistema sin recortar el valor.",
                onValueChange = { log("input") },
                minLines = 3,
            )
        },
    ),
    entry(
        "AqSearchInput", AqComponentCategory.Forms,
        state("vacio", "Vacío") { log ->
            AqSearchInput(value = "", onValueChange = { log("input") }, label = "Buscar", placeholder = "Buscar en Agenda QR", clearLabel = "Limpiar")
        },
        state("con-valor", "Con valor") { log ->
            AqSearchInput(value = "término", onValueChange = { log("input") }, label = "Buscar", placeholder = "Buscar en Agenda QR", clearLabel = "Limpiar", onClear = { log("clear") })
        },
        state("deshabilitado", "Deshabilitado") { _ ->
            AqSearchInput(value = "", onValueChange = {}, label = "Buscar", enabled = false)
        },
    ),
    entry(
        "AqAuthCode", AqComponentCategory.Forms,
        state("parcial", "Parcial") { log ->
            AqAuthCode(value = "123", onValueChange = { log("input") })
        },
        state("error", "Error") { log ->
            AqAuthCode(value = "", onValueChange = { log("input") }, isError = true, errorMessage = "Código inválido")
        },
        state("completo", "Completo") { log ->
            AqAuthCode(value = "482913", onValueChange = { log("input") })
        },
    ),
    entry(
        "AqAmountInput", AqComponentCategory.Forms,
        state("normal", "Normal") { log ->
            AqAmountInput(label = "Monto", value = "125.50", onValueChange = { log("input") }, currencySymbol = "$")
        },
        state("error", "Error") { log ->
            AqAmountInput(label = "Monto", value = "-5", onValueChange = {}, currencySymbol = "€", isError = true, errorMessage = "Importe inválido")
        },
    ),
    entry(
        "AqTextInputGroup", AqComponentCategory.Forms,
        state("vertical", "Vertical") { log ->
            AqTextInputGroup(
                children = listOf(
                    { AqTextInput(label = "Usuario", value = "ana", onValueChange = { log("user") }, borderless = true) },
                    { AqTextInput(label = "Contraseña", value = "••••", onValueChange = { log("pass") }, borderless = true) },
                ),
                helper = "Campos agrupados con divisores.",
            )
        },
        state("horizontal", "Horizontal") { log ->
            AqTextInputGroup(
                children = listOf(
                    { AqTextInput(label = "Día", value = "12", onValueChange = { log("day") }, borderless = true) },
                    { AqTextInput(label = "Mes", value = "06", onValueChange = { log("month") }, borderless = true) },
                ),
                orientation = AqTextInputGroupOrientation.Horizontal,
            )
        },
    ),
    entry(
        "AqFormEntry", AqComponentCategory.Forms,
        state("entrada", "Entrada") { log ->
            AqFormEntry(label = "Datos", optionalLabel = "opcional") {
                AqText(text = "Contenido del campo.", color = AqColor.TextSecondary)
            }
        },
        state("estado", "Con estado") { _ ->
            AqFormEntry(label = "Requerido", state = AqFieldState(enabled = false)) {
                AqText(text = "No editable.", color = AqColor.TextTertiary)
            }
        },
    ),
    entry(
        "AqFormSection", AqComponentCategory.Forms,
        state("seccion", "Sección") { _ ->
            AqFormSection(title = "Cuenta") {
                AqText(text = "Campo uno", color = AqColor.TextPrimary)
                AqText(text = "Campo dos", color = AqColor.TextPrimary)
            }
        },
    ),
    entry(
        "AqCheckbox", AqComponentCategory.Forms,
        state("estados", "Estados") { log ->
            Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
                AqCheckbox(label = "Acepto los términos", checked = true, onCheckedChange = { log("check") })
                AqCheckbox(label = "Sin marcar", checked = false, onCheckedChange = { log("check") })
                AqCheckbox(label = "Deshabilitada", checked = true, onCheckedChange = {}, state = AqFieldState(enabled = false))
            }
        },
        state("descripcion", "Con descripción") { log ->
            AqCheckbox(label = "Recordarme", checked = false, onCheckedChange = { log("check") }, description = "Mantiene la sesión activa")
        },
    ),
    entry(
        "AqCheckboxSet", AqComponentCategory.Forms,
        state("opciones", "Opciones") { log ->
            val checked = remember { mutableStateOf(setOf("qr")) }
            AqCheckboxSet(
                label = "Tipos",
                options = listOf(
                    AqChoiceOption(id = "qr", label = "QR"),
                    AqChoiceOption(id = "act", label = "Actividad"),
                    AqChoiceOption(id = "comp", label = "Comprobante", description = "PDF o imagen"),
                ),
                checkedIds = checked.value,
                onToggle = { id, on ->
                    checked.value = if (on) checked.value + id else checked.value - id
                    log("toggle: $id=$on")
                },
            )
        },
    ),
    entry(
        "AqSwitch", AqComponentCategory.Forms,
        state("estados", "Estados") { log ->
            Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
                AqSwitch(label = "Activado", checked = true, onCheckedChange = { log("switch") })
                AqSwitch(label = "Desactivado", checked = false, onCheckedChange = { log("switch") })
                AqSwitch(label = "Deshabilitado", checked = true, onCheckedChange = {}, state = AqFieldState(enabled = false))
            }
        },
    ),
    entry(
        "AqRadio", AqComponentCategory.Forms,
        state("opciones", "Opciones") { log ->
            val selected = remember { mutableStateOf("pago") }
            Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
                listOf("pago" to "Pago", "cobro" to "Cobro", "transfer" to "Transferencia").forEach { (id, label) ->
                    Row(horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm), verticalAlignment = Alignment.CenterVertically) {
                        AqRadio(selected = selected.value == id, onClick = { selected.value = id; log("radio: $id") }, contentDescription = label)
                        AqText(text = label, color = AqColor.TextPrimary)
                    }
                }
            }
        },
    ),
    entry(
        "AqChoiceSet", AqComponentCategory.Forms,
        state("opciones", "Opciones") { log ->
            val selected = remember { mutableStateOf<String?>("qr") }
            AqChoiceSet(
                label = "Origen",
                options = listOf(
                    AqChoiceOption(id = "qr", label = "Código QR"),
                    AqChoiceOption(id = "manual", label = "Manual", description = "Escritura libre"),
                ),
                selectedId = selected.value,
                onSelected = { selected.value = it; log("choice: $it") },
            )
        },
    ),
    entry(
        "AqSelectAll", AqComponentCategory.Forms,
        state("seleccionar", "Seleccionar todo") { log ->
            val state = remember { mutableStateOf(ToggleableState.Off) }
            AqSelectAll(
                label = "Seleccionar todo",
                checkedState = state.value,
                onClick = {
                    state.value = when (state.value) {
                        ToggleableState.Off -> ToggleableState.On
                        ToggleableState.On -> ToggleableState.Indeterminate
                        ToggleableState.Indeterminate -> ToggleableState.Off
                    }
                    log("selectAll: ${state.value}")
                },
                description = "3 elementos",
            )
        },
    ),
    entry(
        "AqKeypad", AqComponentCategory.Forms,
        state("teclado", "Teclado") { log ->
            AqKeypad(onDigit = { log("digit: $it") }, onBackspace = { log("backspace") }, backspaceDescription = "Borrar")
        },
    ),
    entry(
        "AqPinDots", AqComponentCategory.Forms,
        state("parcial", "Parcial") { _ ->
            AqPinDots(filled = 2, total = 4)
        },
        state("error", "Error") { _ ->
            AqPinDots(filled = 4, total = 4, error = true)
        },
    ),

    // ------------------------------------------------------------------
    // Navigation
    // ------------------------------------------------------------------
    entry(
        "AqAppBar", AqComponentCategory.Navigation,
        state("completa", "Con acciones") { log ->
            AqAppBar(
                title = "Detalle",
                onBack = { log("back") },
                backLabel = "Atrás",
                actions = listOf(
                    AqAppBarAction(label = "Compartir", icon = AqIcons.Share, onClick = { log("share") }),
                    AqAppBarAction(label = "Buscar", icon = AqIcons.Search, onClick = { log("search") }),
                ),
                overflowActions = listOf(AqAppBarAction(label = "Más", onClick = { log("more") })),
                overflowLabel = "Más",
            )
        },
        state("simple", "Sin acciones") { log ->
            AqAppBar(title = "Inicio", onBack = { log("back") }, backLabel = "Atrás")
        },
    ),
    entry(
        "AqTabBar", AqComponentCategory.Navigation,
        state("pestanas", "Pestañas") { log ->
            val selected = remember { mutableStateOf(1) }
            AqTabBar(
                tabs = listOf(
                    AqTab("QR"),
                    AqTab("Actividades", count = 12),
                    AqTab("Comprobantes", count = 4),
                ),
                selectedIndex = selected.value,
                onSelect = { selected.value = it; log("tab: $it") },
            )
        },
        state("iconos", "Con iconos") { log ->
            val selected = remember { mutableStateOf(0) }
            AqTabBar(
                tabs = listOf(
                    AqTab("Inicio", icon = AqIcons.Home),
                    AqTab("Buscar", icon = AqIcons.Search),
                    AqTab("Operaciones", icon = AqIcons.Register),
                    AqTab("Contextos", icon = AqIcons.Context),
                ),
                selectedIndex = selected.value,
                onSelect = { selected.value = it; log("tab: $it") },
            )
        },
    ),
    entry(
        "AqPillTab", AqComponentCategory.Navigation,
        state("pestanas", "Pestañas") { log ->
            val selected = remember { mutableStateOf(0) }
            AqPillTab(
                tabs = listOf(AqTab("Hoy"), AqTab("Ayer", count = 3), AqTab("Semana")),
                selectedIndex = selected.value,
                onSelect = { selected.value = it; log("pill: $it") },
            )
        },
    ),
    entry(
        "AqBottomNavigation", AqComponentCategory.Navigation,
        state("destinos", "Destinos") { log ->
            val selected = remember { mutableStateOf("home") }
            AqBottomNavigation(
                destinations = listOf(
                    AqNavDestination(id = "home", label = "Inicio", icon = AqIcons.Home),
                    AqNavDestination(id = "search", label = "Buscar", icon = AqIcons.Search),
                    AqNavDestination(id = "ops", label = "Operaciones", icon = AqIcons.Register),
                    AqNavDestination(id = "ctx", label = "Contextos", icon = AqIcons.Context),
                ),
                selectedId = selected.value,
                onSelect = { selected.value = it; log("nav: $it") },
            )
        },
    ),
    entry(
        "AqSegmentedControl", AqComponentCategory.Navigation,
        state("segmentos", "Segmentos") { log ->
            val selected = remember { mutableStateOf(0) }
            AqSegmentedControl(
                options = listOf(
                    AqSegmentedOption(label = "Todos", count = 12),
                    AqSegmentedOption(label = "Favoritos", icon = AqIcons.Favorite, count = 3),
                    AqSegmentedOption(label = "Recientes"),
                ),
                selectedIndex = selected.value,
                onSelect = { selected.value = it; log("seg: $it") },
            )
        },
        state("mediano", "Tamaño mediano") { log ->
            val selected = remember { mutableStateOf(0) }
            AqSegmentedControl(
                options = listOf(AqSegmentedOption("Uno"), AqSegmentedOption("Dos")),
                selectedIndex = selected.value,
                onSelect = { selected.value = it; log("seg: $it") },
                size = AqSegmentedControlSize.Md,
            )
        },
    ),
    entry(
        "AqBreadcrumb", AqComponentCategory.Navigation,
        state("ruta", "Ruta") { log ->
            AqBreadcrumb(items = listOf("Inicio", "Destinos", "Detalle"), onItemClick = { log("crumb: $it") })
        },
        state("larga", "Ruta larga") { _ ->
            AqBreadcrumb(items = listOf("Inicio", "Contextos", "Comercios", "Detalle", "Historial"), visibleItemCount = 3)
        },
    ),
    entry(
        "AqScreen", AqComponentCategory.Navigation,
        state("pantalla", "Pantalla") { log ->
            AqScreen(
                title = "Pantalla",
                onBack = { log("back") },
                backLabel = "Atrás",
                scrollable = false,
            ) {
                AqText(text = "Contenido de la pantalla.", color = AqColor.TextPrimary)
            }
        },
    ),

    // ------------------------------------------------------------------
    // Surfaces
    // ------------------------------------------------------------------
    entry(
        "AqModal", AqComponentCategory.Surfaces,
        state("dialogo", "Diálogo") { log ->
            val show = remember { mutableStateOf(true) }
            if (show.value) {
                AqModal(
                    show = true,
                    title = "Confirmar",
                    dismissLabel = "Cancelar",
                    onDismissRequest = { show.value = false; log("dismiss") },
                    confirmLabel = "Aceptar",
                    onConfirm = { show.value = false; log("confirm") },
                ) {
                    AqText(text = "¿Desea continuar?")
                }
            }
        },
    ),
    entry(
        "AqBottomSheet", AqComponentCategory.Surfaces,
        state("hoja", "Hoja inferior") { log ->
            val show = remember { mutableStateOf(true) }
            if (show.value) {
                AqBottomSheet(
                    title = "Detalle",
                    onDismissRequest = { show.value = false; log("dismiss") },
                    dismissDescription = "Cerrar",
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
                        AqText(text = "Contenido de la hoja.")
                        AqText(text = "Se inspecciona sin abandonar el flujo.", color = AqColor.TextSecondary)
                    }
                }
            }
        },
    ),
    entry(
        "AqDrawer", AqComponentCategory.Surfaces,
        state("cajon", "Cajón") { log ->
            AqDrawer(show = true, title = "Menú", dismissLabel = "Cerrar", onDismissRequest = { log("dismiss") }) {
                Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
                    AqText(text = "Opción 1", color = AqColor.TextPrimary)
                    AqText(text = "Opción 2", color = AqColor.TextPrimary)
                }
            }
        },
    ),
    entry(
        "AqDropdown", AqComponentCategory.Surfaces,
        state("menu", "Menú") { log ->
            AqDropdown(
                open = true,
                options = listOf("Todas", "Pago", "Cobro"),
                selected = 0,
                onOptionClick = { log("option: $it") },
                anchor = { AqButton(label = "Filtrar", onClick = {}, variant = AqButtonVariant.Default, size = AqButtonSize.Small) },
                content = {},
            )
        },
    ),
    entry(
        "AqPopover", AqComponentCategory.Surfaces,
        state("anclado", "Anclado") { log ->
            AqPopover(
                open = true,
                trigger = { AqButton(label = "Más", onClick = { log("trigger") }, variant = AqButtonVariant.Text, size = AqButtonSize.Small) },
                content = {
                    Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Xs)) {
                        AqText(text = "Opción A", color = AqColor.TextPrimary)
                        AqText(text = "Opción B", color = AqColor.TextPrimary)
                    }
                },
            )
        },
    ),
    entry(
        "AqTooltip", AqComponentCategory.Surfaces,
        state("visible", "Visible") { log ->
            AqTooltip(label = "Consejo", isVisible = true, onLongClick = { log("longpress") }) {
                AqText(text = "Contenido anclado", color = AqColor.TextPrimary)
            }
        },
    ),
    entry(
        "AqAccordion", AqComponentCategory.Surfaces,
        state("expandido", "Expandido") { log ->
            val expanded = remember { mutableStateOf(true) }
            AqAccordion(
                title = "Detalles",
                expanded = expanded.value,
                onToggle = { expanded.value = !expanded.value; log("toggle") },
                content = { AqText(text = "Contenido expandible.", color = AqColor.TextSecondary) },
            )
        },
        state("colapsado", "Colapsado") { log ->
            AqAccordion(
                title = "Colapsado",
                expanded = false,
                onToggle = { log("toggle") },
                content = { AqText(text = "Oculto.", color = AqColor.TextSecondary) },
            )
        },
    ),

    // ------------------------------------------------------------------
    // Data Display
    // ------------------------------------------------------------------
    entry(
        "AqMenuItem", AqComponentCategory.DataDisplay,
        state("fila", "Fila") { log ->
            AqListSection(title = "Destinos") {
                AqMenuItem(title = "Destino QR", subtitle = "Comercio ejemplo", onClick = { log("item") }, trailing = {
                    AqTag(text = "Activo", tone = AqTone.Success, size = AqTagSize.Xs)
                })
                AqMenuItem(title = "Seleccionado", subtitle = "Estado activo", selected = true, onClick = { log("item") })
                AqMenuItem(title = "Sin acción", subtitle = "No interactivo", onClick = null)
            }
        },
    ),
    entry(
        "AqListSection", AqComponentCategory.DataDisplay,
        state("seccion", "Sección") { log ->
            AqListSection(title = "Sección") {
                AqMenuItem(title = "Elemento 1", onClick = { log("e1") })
                AqMenuItem(title = "Elemento 2", onClick = { log("e2") })
            }
        },
    ),
    entry(
        "AqTag", AqComponentCategory.DataDisplay,
        state("tonos", "Tonos") { _ ->
            FlowRow(horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm), verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
                AqTone.entries.forEach { tone ->
                    AqTag(text = tone.name, tone = tone, size = AqTagSize.Xs)
                }
            }
        },
        state("tamanos", "Tamaños") { _ ->
            Row(horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
                AqTag(text = "X2s", size = AqTagSize.X2s)
                AqTag(text = "Xs", size = AqTagSize.Xs)
                AqTag(text = "Sm", size = AqTagSize.Sm)
            }
        },
        state("icono", "Con icono") { _ ->
            // Moon v1 Tag `Icon`: Left / Right / Both.
            Row(horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
                AqTag(text = "Nuevo", tone = AqTone.Info, size = AqTagSize.Xs, icon = AqIcons.Add)
                AqTag(text = "Nuevo", tone = AqTone.Info, size = AqTagSize.Xs, trailingIcon = AqIcons.Check)
                AqTag(
                    text = "Nuevo",
                    tone = AqTone.Info,
                    size = AqTagSize.Xs,
                    icon = AqIcons.Add,
                    trailingIcon = AqIcons.Check,
                )
            }
        },
    ),
    entry(
        "AqAvatar", AqComponentCategory.DataDisplay,
        state("variantes", "Variantes") { _ ->
            Row(horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
                AqAvatarSize.entries.forEach { size ->
                    AqAvatar(size = size, imageVector = null, initials = "AQ", contentDescription = "Avatar ${size.name}")
                }
            }
        },
        state("badge", "Con insignia") { _ ->
            AqAvatar(
                size = AqAvatarSize.Lg,
                imageVector = null,
                initials = "JD",
                showBadge = true,
                contentDescription = "Avatar con insignia",
            )
        },
    ),
    entry(
        "AqDotIndicator", AqComponentCategory.DataDisplay,
        state("indicador", "Indicador") { _ ->
            Row(horizontalArrangement = Arrangement.spacedBy(AqSpacing.Lg)) {
                AqDotIndicator(total = 3, selected = 0)
                AqDotIndicator(total = 3, selected = 1)
                AqDotIndicator(total = 3, selected = 2)
            }
        },
    ),
    entry(
        "AqTable", AqComponentCategory.DataDisplay,
        state("tabla", "Tabla") { _ ->
            Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
                AqTable(
                    columns = listOf(
                        AqTableColumn(id = "destino", label = "Destino", weight = 2f),
                        AqTableColumn(id = "estado", label = "Estado", weight = 1f),
                    ),
                    rows = listOf(
                        AqTableRow(id = "1", cells = mapOf("destino" to "Café Central", "estado" to "Activo")),
                        AqTableRow(id = "2", cells = mapOf("destino" to "Tienda Norte", "estado" to "Pendiente")),
                    ),
                    rowSize = AqTableRowSize.Sm,
                )
            }
        },
    ),
    entry(
        "AqEvidenceTag", AqComponentCategory.DataDisplay,
        state("etiqueta", "Etiqueta") { _ ->
            Row(horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
                AqEvidenceTag(text = "Verificado", icon = AqIcons.Check, tone = AqTone.Success)
                AqEvidenceTag(text = "Revisar", icon = AqIcons.Info, tone = AqTone.Warning)
            }
        },
    ),
    entry(
        "AqSyncIndicator", AqComponentCategory.DataDisplay,
        state("estados", "Estados") { log ->
            val labels = AqSyncLabels(synced = "Sincronizado", pending = "Pendiente", syncing = "Sincronizando", error = "Error", dead = "Sin conexión")
            Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Xs)) {
                AqSyncIndicator(state = AqSyncState.Synced, labels = labels, showWhenSynced = true)
                AqSyncIndicator(state = AqSyncState.Pending, labels = labels)
                AqSyncIndicator(state = AqSyncState.Syncing, labels = labels)
                AqSyncIndicator(state = AqSyncState.Error, labels = labels)
                AqSyncIndicator(state = AqSyncState.ErrorRecoverable(retryLabel = "Reintentar", onRetry = { log("retry") }), labels = labels)
                AqSyncIndicator(state = AqSyncState.Dead, labels = labels)
            }
        },
    ),
    entry(
        "AqHelperMessage", AqComponentCategory.DataDisplay,
        state("ayuda", "Ayuda") { _ ->
            AqHelperMessage(message = "La ayuda llega del llamador.")
        },
    ),
    entry(
        "AqErrorMessage", AqComponentCategory.DataDisplay,
        state("error", "Error") { _ ->
            Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Xs)) {
                AqErrorMessage(message = "Valor inválido")
                AqHelperMessage(message = null)
            }
        },
    ),
    entry(
        "AqThumbnail", AqComponentCategory.DataDisplay,
        state("estados", "Estados") { _ ->
            Row(horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
                AqThumbnail(result = ThumbnailResult(ThumbnailState.Loading), mimeType = null)
                AqThumbnail(result = ThumbnailResult(ThumbnailState.Failure), mimeType = null)
            }
        },
    ),
    entry(
        "AqThumbnailFrame", AqComponentCategory.DataDisplay,
        state("marco", "Marco") { _ ->
            AqThumbnailFrame {
                AqThumbnailFallback(isPdf = false, contentDescription = "Miniatura")
            }
        },
    ),
    entry(
        "AqThumbnailFallback", AqComponentCategory.DataDisplay,
        state("fallback", "Fallback") { _ ->
            Row(horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
                AqThumbnailFallback(isPdf = false, contentDescription = "Imagen")
                AqThumbnailFallback(isPdf = true, contentDescription = "PDF")
            }
        },
    ),
    entry(
        "AqThumbnailSkeleton", AqComponentCategory.DataDisplay,
        state("esqueleto", "Esqueleto") { _ ->
            Box(modifier = Modifier.size(ThumbnailSize)) {
                AqThumbnailSkeleton()
            }
        },
    ),
    entry(
        "AqMediaFrame", AqComponentCategory.DataDisplay,
        state("marco", "Marco") { _ ->
            AqMediaFrame {
                AqIcon(imageVector = AqIcons.Gallery, contentDescription = "Imagen", size = AqMetrics.IconSize)
            }
        },
    ),
    entry(
        "AqSkeletonLine", AqComponentCategory.DataDisplay,
        state("linea", "Línea") { _ ->
            Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Xs)) {
                AqSkeletonLine(progress = 0.8f)
                AqSkeletonLine(progress = 0.5f)
            }
        },
    ),

    // ------------------------------------------------------------------
    // Feedback
    // ------------------------------------------------------------------
    entry(
        "AqAlert", AqComponentCategory.Feedback,
        state("tonos", "Tonos") { log ->
            Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
                AqAlert(tone = AqTone.Success, message = "Operación guardada")
                AqAlert(tone = AqTone.Danger, message = "No se pudo sincronizar", dismissLabel = "Descartar", onDismiss = { log("dismiss") })
                AqAlert(tone = AqTone.Warning, message = "Revisa tu conexión")
                AqAlert(tone = AqTone.Info, message = "Modo local activado")
                AqAlert(tone = AqTone.Neutral, message = "Aviso general")
            }
        },
        state("titulo", "Con título") { _ ->
            AqAlert(tone = AqTone.Info, title = "Sincronización", message = "Los cambios se subirán al reconectar.")
        },
    ),
    entry(
        "AqToast", AqComponentCategory.Feedback,
        state("toast", "Toast") { log ->
            AqToast(show = true, message = "Operación guardada", actionLabel = "Deshacer", onAction = { log("action") }, onDismissRequest = { log("dismiss") })
        },
        state("sin-accion", "Sin acción") { _ ->
            AqToast(show = true, message = "Sin acción disponible")
        },
    ),
    entry(
        "AqToastHost", AqComponentCategory.Feedback,
        state("host", "Host") { log ->
            val host = remember { AqToastHostState() }
            LaunchedEffect(Unit) {
                host.show(message = "Guardado", actionLabel = "Deshacer", onAction = { log("action") })
            }
            Box(modifier = Modifier.fillMaxWidth().height(AqSize.X2l)) {
                AqText(text = "El toast aparece sobre el contenido.", color = AqColor.TextSecondary)
                AqToastHost(host = host)
            }
        },
    ),
    entry(
        "AqCircularLoader", AqComponentCategory.Feedback,
        state("loader", "Loader") { _ ->
            Row(horizontalArrangement = Arrangement.spacedBy(AqSpacing.Md)) {
                AqCircularLoader(loaderSize = AqSize.Sm, contentDescription = "Cargando")
                AqCircularLoader(loaderSize = AqSize.Md, contentDescription = "Cargando")
                AqCircularLoader(loaderSize = AqMetrics.IconSize, contentDescription = "Cargando")
            }
        },
    ),
    entry(
        "AqCircularProgress", AqComponentCategory.Feedback,
        state("progreso", "Progreso") { _ ->
            Row(horizontalArrangement = Arrangement.spacedBy(AqSpacing.Md)) {
                AqCircularProgress(progress = 0.25f, size = AqSize.Md)
                AqCircularProgress(progress = 0.6f, size = AqSize.Md)
                AqCircularProgress(progress = 0.9f, size = AqSize.Md)
            }
        },
    ),
    entry(
        "AqLinearLoader", AqComponentCategory.Feedback,
        state("loader", "Loader") { _ ->
            Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
                AqLinearLoader(contentDescription = "Cargando")
                AqLinearLoader(progress = 0.5f, contentDescription = "Cargando")
            }
        },
    ),
    entry(
        "AqLinearProgress", AqComponentCategory.Feedback,
        state("progreso", "Progreso") { _ ->
            Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
                AqLinearProgress(progress = 0.6f, contentDescription = "Progreso")
                AqLinearProgress(progress = 0.4f, contentDescription = "Progreso con pin", showPin = true)
            }
        },
    ),
    entry(
        "AqProgressBar", AqComponentCategory.Feedback,
        state("barra", "Barra") { _ ->
            AqProgressBar(progress = AqProgress(0.6f), contentDescription = "Progreso")
        },
        state("tamanos", "Tamaños") { _ ->
            // Moon v1 «Progress bar» lineal: alturas 2/4/8/12/16 dp (6xs…2xs).
            Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
                listOf(AqSize.X6s, AqSize.X5s, AqSize.X4s, AqSize.X3s, AqSize.X2s).forEach { h ->
                    AqProgressBar(
                        progress = AqProgress(0.6f),
                        contentDescription = "Progreso ${h.value} dp",
                        height = h,
                    )
                }
            }
        },
    ),
    entry(
        "AqProgressBlock", AqComponentCategory.Feedback,
        state("bloque", "Bloque") { _ ->
            AqProgressBlock(
                progress = AqProgress(0.6f),
                label = "Guardando comprobantes",
                contentDescription = "Guardando comprobantes",
                message = "6 de 10 guardados",
            )
        },
    ),
    entry(
        "AqProgressPin", AqComponentCategory.Feedback,
        state("pin", "Pin") { _ ->
            AqProgressPin(progress = 0.65f, pinText = "65%") {
                Box(modifier = Modifier.fillMaxWidth().height(AqSize.X4s).background(AqColor.Border, AqSquircles.SurfaceSm))
            }
        },
    ),
    entry(
        "AqSkeletonBlock", AqComponentCategory.Feedback,
        state("esqueleto", "Esqueleto") { _ ->
            AqSkeletonBlock(lines = 3)
        },
    ),
    entry(
        "AqLoadMore", AqComponentCategory.Feedback,
        state("estados", "Estados") { log ->
            val loading = remember { mutableStateOf(false) }
            val end = remember { mutableStateOf(false) }
            Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Xs)) {
                Row(horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
                    AqButton(label = "Cargar", onClick = { loading.value = true; log("load") }, variant = AqButtonVariant.Outline, size = AqButtonSize.Small)
                    AqButton(label = "Fin", onClick = { end.value = true; log("end") }, variant = AqButtonVariant.Text, size = AqButtonSize.Small)
                }
                AqLoadMore(
                    onLoadMore = { log("loadMore") },
                    loadMoreLabel = "Cargar más",
                    loadingLabel = "Cargando…",
                    endLabel = "Fin de la lista",
                    loading = loading.value,
                    end = end.value,
                )
            }
        },
    ),
    entry(
        "AqEmptyPlaceholder", AqComponentCategory.Feedback,
        state("con-accion", "Con acción") { log ->
            AqEmptyPlaceholder(
                title = "Sin destinos",
                subtitle = "Trae tu primer QR para comenzar.",
                icon = AqIcons.QrCode,
                actionLabel = "Traer QR",
                onAction = { log("action") },
            )
        },
        state("sin-accion", "Sin acción") { _ ->
            AqEmptyPlaceholder(title = "Sin resultados", subtitle = "Ajusta los filtros.", icon = AqIcons.Search)
        },
        state("multi-boton", "Multi botón") { log ->
            // Moon v1 Empty state `Type=Multi button`.
            AqEmptyPlaceholder(
                title = "Sin destinos",
                subtitle = "Trae tu primer QR o crea uno nuevo.",
                icon = AqIcons.QrCode,
                actionLabel = "Traer QR",
                onAction = { log("action") },
                secondaryActionLabel = "Crear manual",
                onSecondaryAction = { log("secondary") },
            )
        },
        state("ilustracion", "Con ilustración") { _ ->
            // Moon v1 Empty state `Type=Simple+ Illu`.
            AqEmptyPlaceholder(
                title = "Todo al día",
                subtitle = "No hay comprobantes pendientes.",
                illustration = {
                    AqIcon(imageVector = AqIcons.Check, contentDescription = null, size = AqSize.Xl)
                },
            )
        },
    ),
    entry(
        "AqSuccessCheck", AqComponentCategory.Feedback,
        state("check", "Check") { _ ->
            Row(horizontalArrangement = Arrangement.spacedBy(AqSpacing.Md)) {
                AqSuccessCheck()
                AqSuccessCheck(size = AqSize.Lg)
            }
        },
    ),
    entry(
        "AqScannerFrame", AqComponentCategory.Feedback,
        state("encuadre", "Encuadre") { _ ->
            AqScannerFrame {
                Box(modifier = Modifier.size(AqMetrics.QrPreviewSize).background(AqColor.SurfaceContainerHighest, AqSquircles.SurfaceMd))
            }
        },
    ),

    // ------------------------------------------------------------------
    // Selection
    // ------------------------------------------------------------------
    entry(
        "AqChip", AqComponentCategory.Selection,
        state("seleccion", "Selección") { log ->
            val selected = remember { mutableStateOf(0) }
            Row(horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
                listOf("Todos" to 12, "QR" to null, "Actividades" to 3).forEachIndexed { i, (label, count) ->
                    AqChip(
                        text = label,
                        count = count,
                        selected = selected.value == i,
                        onClick = { selected.value = i; log("chip: $label") },
                        size = AqChipSize.Md,
                    )
                }
            }
        },
        state("estados", "Estados") { log ->
            Row(horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
                AqChip(text = "Seleccionado", selected = true, onClick = { log("chip") }, size = AqChipSize.Md)
                AqChip(text = "Normal", selected = false, onClick = { log("chip") }, size = AqChipSize.Md)
                AqChip(text = "Deshabilitado", selected = false, enabled = false, onClick = {}, size = AqChipSize.Md)
                AqChip(text = "Con icono", icon = AqIcons.Favorite, selected = false, onClick = { log("chip") }, size = AqChipSize.Md)
            }
        },
        state("tamanos", "Tamaños") { log ->
            Row(horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
                AqChip(text = "Sm", selected = false, onClick = { log("chip") }, size = AqChipSize.Sm)
                AqChip(text = "Md", selected = true, onClick = { log("chip") }, size = AqChipSize.Md)
            }
        },
    ),
    entry(
        "AqChipRow", AqComponentCategory.Selection,
        state("fila", "Fila") { log ->
            val selected = remember { mutableStateOf(0) }
            AqChipRow(
                options = listOf(
                    AqChipOption(label = "Todos"),
                    AqChipOption(label = "QR", count = 5),
                    AqChipOption(label = "Actividad", count = 12),
                    AqChipOption(label = "Comprobante", enabled = false),
                ),
                selectedIndex = selected.value,
                onSelect = { selected.value = it; log("chipRow: $it") },
                allowDeselect = true,
            )
        },
        state("scroll", "Con scroll") { log ->
            val selected = remember { mutableStateOf(-1) }
            AqChipRow(
                options = (1..8).map { AqChipOption(label = "Opción $it") },
                selectedIndex = selected.value,
                onSelect = { selected.value = it; log("chipRow: $it") },
                horizontalScroll = true,
                allowDeselect = true,
            )
        },
    ),
    entry(
        "AqFavoriteToggle", AqComponentCategory.Selection,
        state("toggle", "Toggle") { log ->
            val fav = remember { mutableStateOf(false) }
            Row(horizontalArrangement = Arrangement.spacedBy(AqSpacing.Md)) {
                AqFavoriteToggle(favorite = fav.value, onCheckedChange = { fav.value = it; log("fav: $it") }, contentDescription = "Favorito")
                AqFavoriteToggle(favorite = true, onCheckedChange = { log("fav") }, contentDescription = "Favorito activo")
            }
        },
    ),
    entry(
        "AqSelectionToolbar", AqComponentCategory.Selection,
        state("barra", "Barra") { log ->
            AqSelectionToolbar(
                closeLabel = "Cerrar",
                selectAllLabel = "Seleccionar todo",
                selectedCountLabel = "seleccionados",
                selectedCount = 2,
                totalCount = 5,
                selectAllChecked = false,
                onSelectAll = { log("selectAll") },
                onClose = { log("close") },
            )
        },
    ),
    entry(
        "AqSwipeActions", AqComponentCategory.Selection,
        state("deslizar", "Deslizar") { log ->
            val actions = listOf(
                AqSwipeAction(label = "Favorito", icon = AqIcons.Favorite, backgroundColor = AqColor.EmphasisFill, contentColor = AqColor.InverseOnSurface, onAction = { log("fav") }),
                AqSwipeAction(label = "Eliminar", icon = AqIcons.Trash, backgroundColor = AqColor.DangerFill, contentColor = AqColor.InverseOnSurface, onAction = { log("delete") }),
            )
            AqSwipeActions(actions = actions) {
                AqMenuItem(title = "Fila deslizable", subtitle = "Desliza para revelar acciones")
            }
        },
    ),

    // ------------------------------------------------------------------
    // Media
    // ------------------------------------------------------------------
    entry(
        "AqQrPreview", AqComponentCategory.Media,
        state("sin-qr", "Sin QR") { _ ->
            AqQrPreview(encodedQr = "", unavailableMessage = "Sin vista previa de QR")
        },
        state("error", "No decodificable") { _ ->
            AqQrPreview(encodedQr = "no-es-base64!!", unavailableMessage = "No se pudo decodificar el QR")
        },
    ),
    entry(
        "AqCarousel", AqComponentCategory.Media,
        state("carrusel", "Carrusel") { log ->
            AqCarousel(
                items = listOf(
                    AqCarouselItem("1", "Tarjeta 1"),
                    AqCarouselItem("2", "Tarjeta 2"),
                    AqCarouselItem("3", "Tarjeta 3"),
                    AqCarouselItem("4", "Tarjeta 4"),
                ),
            ) { item ->
                AqMenuItem(title = item.label ?: "", onClick = { log("card: ${item.id}") })
            }
        },
    ),

    // ------------------------------------------------------------------
    // Mobile Interactions
    // ------------------------------------------------------------------
    entry(
        "AqPullToRefresh", AqComponentCategory.MobileInteractions,
        state("pull", "Pull to refresh") { log ->
            AqPullToRefresh(isRefreshing = false, onRefresh = { log("refresh") }, refreshingLabel = "Actualizando") {
                AqText(text = "Desliza hacia abajo para actualizar.", color = AqColor.TextSecondary)
            }
        },
        state("refrescando", "Refrescando") { _ ->
            AqPullToRefresh(isRefreshing = true, onRefresh = {}, refreshingLabel = "Actualizando") {
                AqText(text = "Actualizando…", color = AqColor.TextSecondary)
            }
        },
    ),

    // ------------------------------------------------------------------
    // Moon Design v1 (Figma): componentes del catálogo sin equivalente en
    // `moon_design` 1.1.0. Geometría según
    // `docs/05-design-system/evidencia/figma/moon-v1-tokens-y-componentes.json`.
    // ------------------------------------------------------------------
    entry(
        "AqSelect", AqComponentCategory.Forms,
        state("select", "Selector") { log ->
            val selected = remember { mutableStateOf(1) }
            AqSelect(
                options = listOf("Todos", "QR", "Actividades", "Contextos"),
                selectedIndex = selected.value,
                onSelected = { selected.value = it; log("select: $it") },
                label = "Tipo",
                placeholder = "Elegir tipo",
            )
        },
        state("error", "Con error") { _ ->
            AqSelect(
                options = listOf("Opción A", "Opción B"),
                selectedIndex = -1,
                onSelected = {},
                isError = true,
                errorMessage = "Selecciona una opción",
            )
        },
    ),
    entry(
        "AqCombobox", AqComponentCategory.Forms,
        state("combobox", "Multiselección") { log ->
            val query = remember { mutableStateOf("") }
            val selected = remember { mutableStateOf(setOf(0, 2)) }
            AqCombobox(
                query = query.value,
                onQueryChange = { query.value = it },
                options = listOf("Comercio A", "Comercio B", "Comercio C", "Otro"),
                selectedIndices = selected.value,
                onToggle = { index ->
                    selected.value = if (index in selected.value) {
                        selected.value - index
                    } else {
                        selected.value + index
                    }
                    log("combo: $index")
                },
                label = "Comercios",
                multiple = true,
                counterText = { n -> "$n seleccionados" },
                expanded = true,
            )
        },
    ),
    entry(
        "AqPagination", AqComponentCategory.Navigation,
        state("paginas", "Páginas") { log ->
            val page = remember { mutableStateOf(1) }
            AqPagination(
                pageCount = 5,
                currentPage = page.value,
                onPageSelected = { page.value = it; log("page: ${it + 1}") },
                previousContentDescription = "Página anterior",
                nextContentDescription = "Página siguiente",
                pageContentDescription = { "Página $it" },
            )
        },
    ),
    entry(
        "AqSidebar", AqComponentCategory.Navigation,
        state("expandida", "Expandida") { log ->
            val selected = remember { mutableStateOf(0) }
            AqSidebar(
                items = listOf(
                    AqSidebarItem("Inicio", icon = AqIcons.Home),
                    AqSidebarItem("Contextos", icon = AqIcons.Context),
                    AqSidebarItem("Actividad", icon = AqIcons.Clock),
                    AqSidebarItem("Ajustes", icon = AqIcons.Lock),
                ),
                selectedIndex = selected.value,
                onItemSelected = { selected.value = it; log("nav: $it") },
                expanded = true,
                header = {
                    AqText(text = "AgendaQr", size = AqType.HeadingSm, modifier = Modifier.padding(AqSize.X3s))
                },
            )
        },
        state("rail", "Rail") { log ->
            val selected = remember { mutableStateOf(1) }
            AqSidebar(
                items = listOf(
                    AqSidebarItem("Inicio", icon = AqIcons.Home),
                    AqSidebarItem("Contextos", icon = AqIcons.Context),
                    AqSidebarItem("Actividad", icon = AqIcons.Clock),
                ),
                selectedIndex = selected.value,
                onItemSelected = { selected.value = it; log("nav: $it") },
                expanded = false,
            )
        },
    ),
    entry(
        "AqLeaderboard", AqComponentCategory.DataDisplay,
        state("ranking", "Ranking") { _ ->
            AqLeaderboard(
                entries = listOf(
                    AqLeaderboardEntry("María", rank = 1, subtitle = "12 contextos", value = "9.840"),
                    AqLeaderboardEntry("Luis", rank = 2, subtitle = "9 contextos", value = "8.120"),
                    AqLeaderboardEntry("Ana", rank = 3, subtitle = "7 contextos", value = "6.540"),
                    AqLeaderboardEntry("Pedro", rank = 4, subtitle = "5 contextos", value = "4.010"),
                ),
            )
        },
    ),
)
