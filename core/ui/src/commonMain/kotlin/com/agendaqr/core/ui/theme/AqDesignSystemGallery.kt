package com.agendaqr.core.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.state.ToggleableState
import com.agendaqr.core.ui.components.AqAccordion
import com.agendaqr.core.ui.components.AqAlert
import com.agendaqr.core.ui.components.AqAmountInput
import com.agendaqr.core.ui.components.AqAppBar
import com.agendaqr.core.ui.components.AqAuthCode
import com.agendaqr.core.ui.components.AqAppBarAction
import com.agendaqr.core.ui.components.AqAvatar
import com.agendaqr.core.ui.components.AqAvatarSize
import com.agendaqr.core.ui.components.AqBannerAction
import com.agendaqr.core.ui.components.AqBottomSheet
import com.agendaqr.core.ui.components.AqButton
import com.agendaqr.core.ui.components.AqButtonSize
import com.agendaqr.core.ui.components.AqButtonVariant
import com.agendaqr.core.ui.components.AqCarousel
import com.agendaqr.core.ui.components.AqCarouselItem
import com.agendaqr.core.ui.components.AqCheckbox
import com.agendaqr.core.ui.components.AqChip
import com.agendaqr.core.ui.components.AqChipSize
import com.agendaqr.core.ui.components.AqChoiceOption
import com.agendaqr.core.ui.components.AqCircularLoader
import com.agendaqr.core.ui.components.AqCircularProgress
import com.agendaqr.core.ui.components.AqDotIndicator
import com.agendaqr.core.ui.components.AqDrawer
import com.agendaqr.core.ui.components.AqDropdown
import com.agendaqr.core.ui.components.AqEmptyPlaceholder
import com.agendaqr.core.ui.components.AqErrorMessage
import com.agendaqr.core.ui.components.AqFieldState
import com.agendaqr.core.ui.components.AqFavoriteToggle
import com.agendaqr.core.ui.components.AqGroupedAction
import com.agendaqr.core.ui.components.AqHairline
import com.agendaqr.core.ui.components.AqHelperMessage
import com.agendaqr.core.ui.components.AqIcon
import com.agendaqr.core.ui.components.AqIcons
import com.agendaqr.core.ui.components.AqLinearLoader
import com.agendaqr.core.ui.components.AqLinearProgress
import com.agendaqr.core.ui.components.AqListSection
import com.agendaqr.core.ui.components.AqLoadMore
import com.agendaqr.core.ui.components.AqMenuItem
import com.agendaqr.core.ui.components.AqModal
import com.agendaqr.core.ui.components.AqPillTab
import com.agendaqr.core.ui.components.AqPopover
import com.agendaqr.core.ui.components.AqProgress
import com.agendaqr.core.ui.components.AqProgressBar
import com.agendaqr.core.ui.components.AqProgressPin
import com.agendaqr.core.ui.components.AqProgressBlock
import com.agendaqr.core.ui.components.AqPullToRefresh
import com.agendaqr.core.ui.components.AqQrPreview
import com.agendaqr.core.ui.components.AqQuickAction
import com.agendaqr.core.ui.components.AqRadio
import com.agendaqr.core.ui.components.AqSectionTitle
import com.agendaqr.core.ui.components.AqSearchInput
import com.agendaqr.core.ui.components.AqSegmentedControl
import com.agendaqr.core.ui.components.AqSegmentedOption
import com.agendaqr.core.ui.components.AqSkeletonBlock
import com.agendaqr.core.ui.components.AqSwipeAction
import com.agendaqr.core.ui.components.AqSwipeActions
import com.agendaqr.core.ui.components.AqSwitch
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
import com.agendaqr.core.ui.components.AqToast
import com.agendaqr.core.ui.components.AqToastHost
import com.agendaqr.core.ui.components.AqTooltip
import com.agendaqr.core.ui.components.AqTone
import com.agendaqr.core.ui.components.AqBreadcrumb as AqBreadcrumbComp
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqSquircles
import com.agendaqr.core.ui.theme.AqTextStyles
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.core.ui.theme.AqWeight
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Galería del sistema de diseño Agenda QR (Moon Design).
 *
 * Solo para inspección interna y previews. No se importa en pantallas de
 * producción: no introduce dependencia arquitectónica en la app.
 *
 * Organizada por familias:
 *  Foundations, Actions, Forms, Navigation, Surfaces, Data Display,
 *  Feedback, Selection, Media, Mobile Interactions.
 */
@Composable
fun AqDesignSystemGallery(darkTheme: Boolean) {
    AqTheme(darkTheme = darkTheme) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(AqSpacing.Lg),
            verticalArrangement = Arrangement.spacedBy(AqSpacing.Lg),
        ) {
            AqText(
                text = "Design System — Galería de componentes",
                size = AqType.HeadingLg,
                fontWeight = AqWeight.SemiBold,
                color = AqColor.TextPrimary,
            )
            AqText(
                text = "Tema ${if (darkTheme) "oscuro" else "claro"} · Moon Design",
                color = AqColor.TextSecondary,
                size = AqType.BodySm,
            )

            // ------------------------------------------------------------------
            // Foundations
            // ------------------------------------------------------------------
            AqSectionTitle(text = "Foundations")
            AqTokenFoundationsPreview()

            // ------------------------------------------------------------------
            // Actions
            // ------------------------------------------------------------------
            AqSectionTitle(text = "Actions")
            AqActionsPreview()

            // ------------------------------------------------------------------
            // Forms
            // ------------------------------------------------------------------
            AqSectionTitle(text = "Forms")
            AqFormsPreview()
            // ------------------------------------------------------------------
            // Navigation
            // ------------------------------------------------------------------
            AqSectionTitle(text = "Navigation")
            AqNavigationPreview()

            // ------------------------------------------------------------------
            // Surfaces
            // ------------------------------------------------------------------
            AqSectionTitle(text = "Surfaces")
            AqSurfacesPreview()

            // ------------------------------------------------------------------
            // Data Display
            // ------------------------------------------------------------------
            AqSectionTitle(text = "Data Display")
            AqDataDisplayPreview()

            // ------------------------------------------------------------------
            // Feedback
            // ------------------------------------------------------------------
            AqSectionTitle(text = "Feedback")
            AqFeedbackPreview()

            // ------------------------------------------------------------------
            // Selection
            // ------------------------------------------------------------------
            AqSectionTitle(text = "Selection")
            AqSelectionPreview()

            // ------------------------------------------------------------------
            // Media
            // ------------------------------------------------------------------
            AqSectionTitle(text = "Media")
            AqMediaPreview()

            // ------------------------------------------------------------------
            // Mobile Interactions
            // ------------------------------------------------------------------
            AqSectionTitle(text = "Mobile Interactions")
            AqMobileInteractionsPreview()
        }
    }
}

/** Previews de tokens y valores base. */
@Composable
private fun AqTokenFoundationsPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
        AqText(
            text = "Colores semánticos",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
            colorChip("Surface", AqColor.Surface)
            colorChip("SurfaceSecondary", AqColor.SurfaceSecondary)
            colorChip("Border", AqColor.Border)
            colorChip("BrandFill", AqColor.BrandFill)
            colorChip("Danger", AqColor.Danger)
            colorChip("Success", AqColor.Success)
            colorChip("Warning", AqColor.Warning)
            colorChip("Info", AqColor.Info)
        }
        AqText(
            text = "Tipografía",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Xs)) {
            AqText(
                text = "Heading 3Xl (36 sp)",
                size = AqType.Heading3Xl,
                fontWeight = AqWeight.SemiBold,
                color = AqColor.TextPrimary,
            )
            AqText(
                text = "Heading 2Xl (32 sp)",
                size = AqType.Heading2Xl,
                fontWeight = AqWeight.SemiBold,
                color = AqColor.TextPrimary,
            )
            AqText(
                text = "Heading Lg (18 sp)",
                size = AqType.HeadingLg,
                fontWeight = AqWeight.SemiBold,
                color = AqColor.TextPrimary,
            )
            AqText(text = "Body Md (16 sp)", size = AqType.BodyMd)
            AqText(text = "Body Sm (14 sp)", size = AqType.BodySm, color = AqColor.TextPrimary)
            AqText(text = "Caption (12 sp)", size = AqType.BodyXs, color = AqColor.TextSecondary)
        }
    }
}

@Composable
private fun colorChip(label: String, color: Color) {
    Row(horizontalArrangement = Arrangement.spacedBy(AqSpacing.Xs)) {
        Box(
            modifier = Modifier.size(AqMetrics.IconSize).background(color),
        )
        AqText(text = label, size = AqType.BodyXs, color = AqColor.TextSecondary)
    }
}

/** Previews de acciones y botones: variantes, estados y temas. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AqActionsPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
        AqText(
            text = "Botones primarios — normal, deshabilitado, cargando",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
            verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
        ) {
            AqButton(
                label = "Primaria",
                onClick = {},
                variant = AqButtonVariant.Primary,
                size = AqButtonSize.Large,
            )
            AqButton(
                label = "Deshabilitada",
                onClick = {},
                enabled = false,
                variant = AqButtonVariant.Primary,
                size = AqButtonSize.Large,
            )
            AqButton(
                label = "Cargando",
                onClick = {},
                isLoading = true,
                busyDescription = "Guardando…",
                variant = AqButtonVariant.Primary,
                size = AqButtonSize.Large,
            )
        }
        AqText(
            text = "Botones secundarios, contorneados y de texto",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
            verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
        ) {
            AqButton(
                label = "Secundaria",
                onClick = {},
                variant = AqButtonVariant.Default,
                size = AqButtonSize.Medium,
            )
            AqButton(
                label = "Contorneada",
                onClick = {},
                variant = AqButtonVariant.Outline,
                size = AqButtonSize.Medium,
            )
            AqButton(
                label = "Texto",
                onClick = {},
                variant = AqButtonVariant.Text,
                size = AqButtonSize.Medium,
            )
        }
        AqText(
            text = "Botón destructivo, solo-icono y FAB extendida",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
            verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
        ) {
            AqButton(
                label = "Destructiva",
                onClick = {},
                variant = AqButtonVariant.Danger,
                size = AqButtonSize.Large,
            )
            AqButton(
                label = "Más",
                onClick = {},
                variant = AqButtonVariant.Text,
                size = AqButtonSize.Small,
                icon = AqIcons.More,
                iconOnly = true,
            )
            AqButton(
                label = "Guardar",
                icon = AqIcons.Add,
                onClick = {},
                variant = AqButtonVariant.Primary,
                size = AqButtonSize.Large,
            )
        }
        AqText(
            text = "Grupo de acciones (una enfatizada)",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
            verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
        ) {
            AqButton(
                label = "Cancelar",
                onClick = {},
                variant = AqButtonVariant.Default,
                size = AqButtonSize.Small,
            )
            AqButton(
                label = "Guardar",
                onClick = {},
                variant = AqButtonVariant.Primary,
                size = AqButtonSize.Small,
            )
        }
        AqText(
            text = "Acciones rápidas (glifo + etiqueta, celda a ancho del padre)",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
            verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
        ) {
            AqQuickAction(
                label = "Traer un QR",
                icon = AqIcons.QrCode,
                onClick = {},
                modifier = Modifier,
            )
            AqQuickAction(
                label = "Registrar actividad",
                icon = AqIcons.Register,
                onClick = {},
                modifier = Modifier,
            )
            AqQuickAction(
                label = "Crear contexto",
                icon = AqIcons.Context,
                onClick = {},
                modifier = Modifier,
            )
        }
    }
}

/** Previews de formularios: normal, deshabilitado, error, carga, contenido corto y largo. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AqFormsPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
        AqText(
            text = "Text Input — normal, deshabilitado, error",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
            AqTextInput(label = "Campo de texto", value = "Valor corto", onValueChange = {})
            AqTextInput(label = "Deshabilitado", value = "No editable", onValueChange = {}, enabled = false)
            AqTextInput(label = "Error", value = "abc", onValueChange = {}, isError = true, errorMessage = "Valor inválido")
        }
        AqText(
            text = "Text Input — contenido largo (se ajusta al ancho)",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        AqTextInput(
            label = "Texto largo",
            value = "Este es un valor de texto bastante largo que se extiende más allá de lo habitual para verificar el desbordamiento y el ajuste al ancho del campo en pantallas compactas.",
            onValueChange = {},
        )
        AqText(
            text = "Text Area — normal, error, contenido largo",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
            AqTextArea(
                label = "Nota multilínea",
                value = "Nota corta.",
                onValueChange = {},
            )
            AqTextArea(
                label = "Nota con error",
                value = "Nota inválida",
                onValueChange = {},
                isError = true,
                errorMessage = "La nota supera el límite permitido.",
            )
        }
        AqTextArea(
            label = "Nota larga",
            value = "Esta es una nota de varias líneas que simula contenido largo del usuario. "
                + "Se compone con minLines y crece en altura según el contenido y la escala de fuente, "
                + "sin altura fija que recorte el texto ni el teclado virtual.",
            onValueChange = {},
            minLines = 3,
        )
        AqText(
            text = "Auth Code — celdas de código de verificación",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
            AqAuthCode(value = "123", onValueChange = {})
            AqAuthCode(value = "", onValueChange = {}, isError = true, errorMessage = "Código inválido")
        }
        AqText(
            text = "Text Input Group — campos agrupados con divisores y helper",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        AqTextInputGroup(
            children = listOf(
                { AqTextInput(label = "Usuario", value = "ana", onValueChange = {}, borderless = true) },
                { AqTextInput(label = "Contraseña", value = "••••", onValueChange = {}, borderless = true) },
            ),
            helper = "Los campos se agrupan con divisores de 1 dp.",
        )
        AqText(
            text = "Search Input — normal, con limpieza, deshabilitado",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
            AqSearchInput(
                value = "",
                onValueChange = {},
                label = "Buscar",
                placeholder = "Buscar en Agenda QR",
                clearLabel = "Limpiar búsqueda",
            )
            AqSearchInput(
                value = "término de búsqueda",
                onValueChange = {},
                label = "Buscar",
                placeholder = "Buscar en Agenda QR",
                clearLabel = "Limpiar búsqueda",
            )
            AqSearchInput(
                value = "",
                onValueChange = {},
                label = "Buscar",
                placeholder = "Sin entrada",
            )
        }
        AqText(
            text = "Controles booleanos — Checkbox, Switch, Radio (seleccionados y no)",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
                verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
            ) {
                AqCheckbox(label = "Casilla", checked = true, onCheckedChange = {})
                AqCheckbox(
                    label = "Deshabilitada",
                    checked = true,
                    onCheckedChange = {},
                    state = AqFieldState(enabled = false),
                )
                AqSwitch(label = "Interruptor", checked = false, onCheckedChange = {})
                AqSwitch(
                    label = "Deshabilitado",
                    checked = true,
                    onCheckedChange = {},
                    state = AqFieldState(enabled = false),
                )
            }
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
                verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
            ) {
                AqRadio(selected = true, contentDescription = "Pago")
                AqText(text = "Pago")
                AqRadio(selected = false, contentDescription = "Cobro")
                AqText(text = "Cobro")
            }
        }
        AqText(
            text = "Amount Input — importe con moneda",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        AqAmountInput(
            label = "Monto",
            value = "125.50",
            onValueChange = {},
            currencySymbol = "$",
        )
        AqText(
            text = "Error y mensajes de ayuda",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        AqErrorMessage(message = "Valor inválido")
        AqHelperMessage(message = "La ayuda llega del llamador.")
    }
}

/** Previews de navegación: barra superior, pestañas inferiores, riel, segmentado. */
@Composable
private fun AqNavigationPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
        AqText(
            text = "App Bar — con flecha atrás, acciones y overflow",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        AqAppBar(
            title = "Título de pantalla",
            onBack = {},
            backLabel = "Atrás",
            actions = listOf(
                AqAppBarAction(
                    label = "Compartir",
                    icon = AqIcons.Share,
                    onClick = {},
                ),
                AqAppBarAction(
                    label = "Buscar",
                    icon = AqIcons.Search,
                    onClick = {},
                ),
            ),
            overflowActions = listOf(
                AqAppBarAction(
                    label = "Más acciones",
                    onClick = {},
                ),
            ),
            overflowLabel = "Más",
        )
        AqText(
            text = "Tab Bar — pestañas inferiores con badge de conteo",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        AqTabBar(
            tabs = listOf(
                AqTab("QR", count = 0),
                AqTab("Actividades", count = 12),
                AqTab("Comprobantes", count = 4),
                AqTab("Ajustes"),
            ),
            selectedIndex = 1,
            onSelect = {},
        )
        AqText(
            text = "Pill Tab — pestañas sobre píldora",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        AqPillTab(
            tabs = listOf(
                AqTab("Hoy"),
                AqTab("Ayer", count = 3),
                AqTab("Semana"),
            ),
            selectedIndex = 0,
            onSelect = {},
        )
        AqText(
            text = "Navigation Rail — variante expanded / tablet",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        Row(modifier = Modifier.fillMaxWidth().height(AqMetrics.NavBarHeight * 4)) {
            AqTabBar(
                tabs = listOf(
                    AqTab("Inicio", icon = AqIcons.Home),
                    AqTab("Buscar", icon = AqIcons.Search),
                    AqTab("Operaciones", icon = AqIcons.Register),
                    AqTab("Contextos", icon = AqIcons.Context),
                ),
                selectedIndex = 1,
                onSelect = {},
            )
        }
        AqText(
            text = "Segmented Control (filtros) — seleccionado, no seleccionado, conteo",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        AqSegmentedControl(
            options = listOf(
                AqSegmentedOption(label = "Todos", count = 12),
                AqSegmentedOption(
                    label = "Favoritos",
                    icon = AqIcons.Favorite,
                    count = 3,
                ),
            ),
            selectedIndex = 0,
            onSelect = {},
        )
    }
}

/** Previews de superficies: tarjeta, acciones inferiores, hoja inferior, modal. */
@Composable
private fun AqSurfacesPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
        AqText(
            text = "Card — normal, con contenido largo",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        AqCardPreview {
            AqText(text = "Contenido de tarjeta", color = AqColor.TextPrimary)
        }
        AqCardPreview {
            Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
                AqText(
                    text = "Contenido largo que ocupa el ancho completo de la tarjeta para verificar el relleno interno y que el texto no toque el borde, incluso con fuente al 150 % o 200 %.",
                    color = AqColor.TextPrimary,
                )
            }
        }
        AqText(
            text = "Modal — diálogo centrado con foco atrapado y scrim clicable",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        val showModal = remember { mutableStateOf(true) }
        if (showModal.value) {
            AqModal(
                show = true,
                title = "Confirmar acción",
                dismissLabel = "Cancelar",
                onDismissRequest = { showModal.value = false },
                confirmLabel = "Aceptar",
                onConfirm = { showModal.value = false },
            ) {
                AqText(text = "¿Desea continuar con esta operación?")
            }
        }
        AqText(
            text = "Bottom Sheet — hoja inferior para inspeccionar sin abandonar el flujo",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        val showSheet = remember { mutableStateOf(true) }
        if (showSheet.value) {
            AqBottomSheet(
                title = "Detalle",
                onDismissRequest = { showSheet.value = false },
                dismissDescription = "Cerrar",
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
                    AqText(text = "Contenido de la hoja inferior.")
                    AqText(
                        text = "Se inspecciona sin abandonar el flujo de la pantalla de origen.",
                        color = AqColor.TextSecondary,
                    )
                }
            }
        }
        AqText(
            text = "Acción fija inferior (FAB extendida) en zona de acción",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        AqButton(
            label = "Guardar",
            icon = AqIcons.Add,
            onClick = {},
            variant = AqButtonVariant.Primary,
            size = AqButtonSize.Large,
        )
        AqText(
            text = "Popover — menú flotante anclado",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        AqPopover(
            open = false,
            trigger = {
                AqButton(label = "Más", onClick = {}, variant = AqButtonVariant.Text, size = AqButtonSize.Small)
            },
            content = {},
        )
        AqText(
            text = "Dropdown — menú desplegable anclado con opciones",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        AqDropdown(
            open = true,
            options = listOf("Todas las categorías", "Pago", "Cobro"),
            selected = 0,
            onOptionClick = {},
            anchor = {
                AqButton(label = "Filtrar", onClick = {}, variant = AqButtonVariant.Default)
            },
            content = {},
        )
        AqText(
            text = "Drawer — cajón lateral sobre scrim",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        AqDrawer(
            show = true,
            title = "Menú",
            dismissLabel = "Cerrar",
            onDismissRequest = {},
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
                AqText(text = "Opción 1", color = AqColor.TextPrimary)
                AqText(text = "Opción 2", color = AqColor.TextPrimary)
            }
        }
        AqText(
            text = "Tooltip — etiqueta flotante sobre el contenido",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        AqTooltip(
            label = "Consejo",
            isVisible = true,
            content = {
                AqText(text = "Contenido anclado", color = AqColor.TextPrimary)
            },
        )
    }
}

/** Previews de visualización de datos: filas, badges, tags, meta, tabla, breadcrumb. */
@Composable
private fun AqDataDisplayPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
        AqText(
            text = "List Row — normal, seleccionado, deshabilitado",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        AqListSection(title = "Destinos") {
            AqMenuItem(
                title = "Destino QR",
                subtitle = "Comercio ejemplo",
                trailing = {
                    AqTag(
                        text = "Activo",
                        tone = AqTone.Success,
                        size = AqTagSize.Xs,
                    )
                },
                onClick = {},
            )
            AqMenuItem(
                title = "Destino seleccionado",
                subtitle = "Estado activo",
                selected = true,
                trailing = {
                    AqTag(
                        text = "Favorito",
                        tone = AqTone.Info,
                        size = AqTagSize.Xs,
                    )
                },
            )
            AqMenuItem(
                title = "Destino sin acción",
                subtitle = "Sin interacción",
                onClick = null,
            )
        }
        AqText(
            text = "Tag / Avatar / Dot Indicator — todos los tonos y tamaños",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
            AqTag(text = "Activo", tone = AqTone.Success, size = AqTagSize.Xs)
            AqTag(text = "Pendiente", tone = AqTone.Warning, size = AqTagSize.Xs)
            AqTag(text = "Error", tone = AqTone.Danger, size = AqTagSize.Xs)
            AqTag(text = "Neutral", tone = AqTone.Neutral, size = AqTagSize.Xs)
            AqAvatar(
                size = AqAvatarSize.Sm,
                imageVector = null,
                initials = "JD",
                contentDescription = "Avatar",
            )
            AqDotIndicator(total = 3, selected = 1)
        }
        AqText(
            text = "Meta Row — label + valor + acción de copia",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        AqMenuItem(
            title = "Sincronizado",
            subtitle = "14:30",
            trailing = {
                AqIcon(
                    imageVector = AqIcons.Copy,
                    contentDescription = null,
                )
            },
        )
        AqText(
            text = "Breadcrumb — ruta de navegación",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        AqBreadcrumbComp(items = listOf("Inicio", "Destinos", "Detalle"))
        AqText(
            text = "Tabla — cabecera y filas con tamaños de fila",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
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
        AqText(
            text = "Accordion — sección expandible con divisor",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        val accordionExpanded = remember { mutableStateOf(true) }
        AqAccordion(
            title = "Detalles del destino",
            expanded = accordionExpanded.value,
            onToggle = { accordionExpanded.value = !accordionExpanded.value },
            content = {
                AqText(
                    text = "Contenido expandible de la sección.",
                    color = AqColor.TextSecondary,
                )
            },
        )
    }
}

/** Previews de feedback y estados: alerta, toast, vacío, carga, progreso, esqueleto. */
@Composable
private fun AqFeedbackPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
        AqText(
            text = "Alerta por tono — Success, Danger, Warning, Info, Neutral",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
            AqAlert(tone = AqTone.Success, message = "Operación guardada")
            AqAlert(
                tone = AqTone.Danger,
                message = "No se pudo sincronizar",
                dismissLabel = "Descartar",
                onDismiss = {},
            )
            AqAlert(tone = AqTone.Warning, message = "Revisa tu conexión a internet")
            AqAlert(tone = AqTone.Info, message = "Modo local activado")
            AqAlert(tone = AqTone.Neutral, message = "Aviso general")
        }
        AqText(
            text = "Toast — notificación efímera en la parte inferior",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        AqToast(
            show = true,
            message = "Operación guardada",
            actionLabel = "Deshacer",
            onAction = {},
            onDismissRequest = {},
        )
        AqText(
            text = "Empty Placeholder — con acción, con título largo, sin acción",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        AqEmptyPlaceholder(
            title = "Sin destinos",
            subtitle = "Trae tu primer QR para comenzar.",
            icon = AqIcons.QrCode,
            actionLabel = "Traer un QR",
            onAction = {},
        )
        AqEmptyPlaceholder(
            title = "Este es un título de estado vacío bastante largo para verificar que el contenido no se desborde ni se solape con el subtítulo ni con la acción primaria en pantallas estrechas",
            subtitle = "Subtítulo que acompaña al título largo y describe la acción recomendada para salir de este estado vacío.",
            icon = AqIcons.QrCode,
            actionLabel = "Acción primaria",
            onAction = {},
        )
        AqEmptyPlaceholder(
            title = "Sin resultados",
            subtitle = "Ajusta los filtros o prueba con otra búsqueda.",
            icon = AqIcons.Search,
        )
        AqText(
            text = "Loading / Spinner / Skeleton",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
            AqCircularLoader(loaderSize = AqMetrics.IconSize, contentDescription = "Cargando")
            AqSkeletonBlock(lines = 3)
            AqCircularProgress(
                progress = 0.5f,
                size = AqMetrics.IconSize,
            )
            AqLinearLoader(contentDescription = "Cargando")
        }
        AqText(
            text = "Linear Progress — 60 %, con pin de progreso",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
            AqLinearProgress(
                progress = 0.6f,
                contentDescription = "Progreso de importación",
            )
            AqLinearProgress(
                progress = 0.4f,
                contentDescription = "Progreso con pin",
                showPin = true,
            )
        }
        AqText(
            text = "Progress Pin — clavo de progreso sobre la barra",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        AqProgressPin(
            progress = 0.65f,
            pinText = "65%",
            content = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(AqSize.X4s)
                        .background(AqColor.Border, AqSquircles.SurfaceSm),
                )
            },
        )
        AqText(
            text = "Progress Bar — 60 %, determinista",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        AqProgressBar(
            progress = AqProgress(0.6f),
            contentDescription = "Progreso de importación",
        )
        AqText(
            text = "Progress Block — guardado de lote, 60 %",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        AqProgressBlock(
            progress = AqProgress(0.6f),
            label = "Guardando comprobantes",
            contentDescription = "Guardando comprobantes, 60 % completados",
            message = "6 de 10 comprobantes guardados",
        )
        AqText(
            text = "Chip (píldora de estado) — normal e inversa (Modo local)",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
            AqChip(
                text = "Al día",
                selected = true,
                onClick = {},
                size = AqChipSize.Sm,
            )
            AqChip(
                text = "Modo local",
                selected = false,
                onClick = {},
                size = AqChipSize.Sm,
            )
        }
    }
}

/** Previews de selección: chips, toggle, radio, switch, selección múltiple. */
@Composable
private fun AqSelectionPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
        AqText(
            text = "ChipGroup — selección inequívoca (check + relleno + borde), conteo y deshabilitado",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
            AqChip(
                text = "Todos",
                selected = true,
                onClick = {},
                size = AqChipSize.Md,
            )
            AqChip(
                text = "Favoritos",
                selected = false,
                onClick = {},
                size = AqChipSize.Md,
            )
            AqChip(
                text = "Pago",
                selected = false,
                onClick = {},
                enabled = false,
                size = AqChipSize.Md,
            )
        }
        AqText(
            text = "Favorito toggle — con glifo y estado",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        AqFavoriteToggle(
            favorite = true,
            onCheckedChange = {},
            contentDescription = "Marcar como favorito",
        )
        AqText(
            text = "Radio — opciones mutuamente excluyentes",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Xs)) {
            Row(horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
                AqRadio(selected = true, contentDescription = "Pago")
                AqText(text = "Pago")
            }
            Row(horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
                AqRadio(selected = false, contentDescription = "Cobro")
                AqText(text = "Cobro")
            }
        }
        AqText(
            text = "Switch — activar/desactivar sincronización",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        AqSwitch(label = "Sincronización activa", checked = true, onCheckedChange = {})
    }
}

/** Previews de medios: QR Preview y Carousel. */
@Composable
private fun AqMediaPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
        AqText(
            text = "QR Preview — sin vista previa y no decodificable",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
            AqQrPreview(
                encodedQr = "",
                unavailableMessage = "Sin vista previa de QR",
            )
            AqQrPreview(
                encodedQr = "esto-no-es-base64-valido!!",
                unavailableMessage = "No se pudo decodificar el QR",
            )
        }
        AqText(
            text = "Carousel — siguiente tarjeta asoma como indicio",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        AqCarousel(
            items = listOf(
                AqCarouselItem("card-1", "Tarjeta 1"),
                AqCarouselItem("card-2", "Tarjeta 2"),
                AqCarouselItem("card-3", "Tarjeta 3"),
                AqCarouselItem("card-4", "Tarjeta 4"),
            ),
        ) { item ->
            AqMenuItem(
                title = item.label ?: "",
                onClick = {},
            )
        }
    }
}

/** Previews de interacciones móviles: pull-to-refresh, swipe actions, bottom action dock. */
@Composable
private fun AqMobileInteractionsPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
        AqText(
            text = "Pull to Refresh — contenedor de recarga por arrastre con loader circular",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        AqPullToRefresh(
            isRefreshing = false,
            onRefresh = {},
            refreshingLabel = "Actualizando",
        ) {
            AqText(text = "Contenido de la lista que se desplaza al tirar hacia abajo.")
        }
        AqText(
            text = "Swipe Actions — acciones reveladas al deslizar la fila",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        val swipeActions = listOf(
            AqSwipeAction(
                label = "Marcar favorito",
                icon = AqIcons.Favorite,
                backgroundColor = AqColor.EmphasisFill,
                contentColor = AqColor.InverseOnSurface,
                onAction = {},
            ),
            AqSwipeAction(
                label = "Eliminar",
                icon = AqIcons.Trash,
                backgroundColor = AqColor.DangerFill,
                contentColor = AqColor.InverseOnSurface,
                onAction = {},
            ),
        )
        AqSwipeActions(actions = swipeActions) {
            AqMenuItem(
                title = "Fila con acciones al deslizar",
                subtitle = "Desliza para revelar acciones",
            )
        }
        AqText(
            text = "Bottom Action Dock — acción primaria alcanzable (una mano) con IME",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        AqButton(
            label = "Guardar",
            onClick = {},
            variant = AqButtonVariant.Primary,
            size = AqButtonSize.Large,
        )
        AqText(
            text = "Text Input — campo con acciones adjuntas",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        AqTextInput(
            label = "Etiqueta",
            value = "",
            onValueChange = {},
        )
    }
}

/** Tarjeta de preview Moon-only: superficie + radio squircle + relleno canónico. */
@Composable
private fun AqCardPreview(content: @Composable () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(AqColor.Surface, AqSquircles.InteractiveSm)
            .padding(AqSpacing.Lg),
    ) {
        content()
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
