package com.agendaqr.core.ui.theme

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.agendaqr.core.ui.components.AqBadge
import com.agendaqr.core.ui.components.AqBottomAction
import com.agendaqr.core.ui.components.AqBottomActionDock
import com.agendaqr.core.ui.components.AqButton
import com.agendaqr.core.ui.components.AqButtonGroup
import com.agendaqr.core.ui.components.AqButtonVariant
import com.agendaqr.core.ui.components.AqCard
import com.agendaqr.core.ui.components.AqCarousel
import com.agendaqr.core.ui.components.AqCarouselItem
import com.agendaqr.core.ui.components.AqCheckbox
import com.agendaqr.core.ui.components.AqCheckboxGroup
import com.agendaqr.core.ui.components.AqChoiceOption
import com.agendaqr.core.ui.components.AqChoiceGroup
import com.agendaqr.core.ui.components.AqContextBadge
import com.agendaqr.core.ui.components.AqDatePickerDialog
import com.agendaqr.core.ui.components.AqDangerButton
import com.agendaqr.core.ui.components.AqDialog
import com.agendaqr.core.ui.components.AqDivider
import com.agendaqr.core.ui.components.AqEmptyState
import com.agendaqr.core.ui.components.AqEvidenceBadge
import com.agendaqr.core.ui.components.AqExpansionPanel
import com.agendaqr.core.ui.components.AqFeedbackEvent
import com.agendaqr.core.ui.components.AqFieldState
import com.agendaqr.core.ui.components.AqFilterChip
import com.agendaqr.core.ui.components.AqGroupedAction
import com.agendaqr.core.ui.components.AqHeading
import com.agendaqr.core.ui.components.AqHairline
import com.agendaqr.core.ui.components.AqIcon
import com.agendaqr.core.ui.components.AqIconBadge
import com.agendaqr.core.ui.components.AqIconButton
import com.agendaqr.core.ui.components.AqIndexTable
import com.agendaqr.core.ui.components.AqLink
import com.agendaqr.core.ui.components.AqListBody
import com.agendaqr.core.ui.components.AqListRow
import com.agendaqr.core.ui.components.AqLoadMoreFooter
import com.agendaqr.core.ui.components.AqLoading
import com.agendaqr.core.ui.components.AqMetaRow
import com.agendaqr.core.ui.components.AqMultilineTextField
import com.agendaqr.core.ui.components.AqOutlineButton
import com.agendaqr.core.ui.components.AqProgress
import com.agendaqr.core.ui.components.AqProgressBar
import com.agendaqr.core.ui.components.AqPrimaryButton
import com.agendaqr.core.ui.components.AqProgressBlock
import com.agendaqr.core.ui.components.AqQrPreview
import com.agendaqr.core.ui.components.AqRadioButton
import com.agendaqr.core.ui.components.AqSearchField
import com.agendaqr.core.ui.components.AqSegmentedControl
import com.agendaqr.core.ui.components.AqSegmentedOption
import com.agendaqr.core.ui.components.AqSelect
import com.agendaqr.core.ui.components.AqSecondaryButton
import com.agendaqr.core.ui.components.AqSection
import com.agendaqr.core.ui.components.AqSectionHeader
import com.agendaqr.core.ui.components.AqSpinner
import com.agendaqr.core.ui.components.AqStatusBanner
import com.agendaqr.core.ui.components.AqStatusPill
import com.agendaqr.core.ui.components.AqSwitch
import com.agendaqr.core.ui.components.AqText
import com.agendaqr.core.ui.components.AqTextAction
import com.agendaqr.core.ui.components.AqTextField
import com.agendaqr.core.ui.components.AqTextActionVariant
import com.agendaqr.core.ui.components.AqTimePickerDialog
import com.agendaqr.core.ui.components.AqTonalTag
import com.agendaqr.core.ui.components.AqTopBar
import com.agendaqr.core.ui.components.AqTone
import com.agendaqr.core.ui.components.AqVerticalDivider
import com.agendaqr.core.ui.components.AqBannerAction
import org.jetbrains.compose.ui.tooling.preview.Preview

/**
 * Galería del sistema de diseño Agenda QR (Polaris reimplementado en Kotlin/Compose).
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
            AqHeading(text = "Design System — Galería de componentes", size = AqType.HeadingLg)
            AqText(
                text = "Tema ${if (darkTheme) "oscuro" else "claro"} · Polaris 9.4.2",
                color = AqColor.TextSecondary,
                size = AqType.BodySm,
            )

            // ------------------------------------------------------------------
            // Foundations
            // ------------------------------------------------------------------
            AqSectionHeader(text = "Foundations")
            AqTokenFoundationsPreview()

            // ------------------------------------------------------------------
            // Actions
            // ------------------------------------------------------------------
            AqSectionHeader(text = "Actions")
            AqActionsPreview()

            // ------------------------------------------------------------------
            // Forms
            // ------------------------------------------------------------------
            AqSectionHeader(text = "Forms")
            AqFormsPreview()

            // ------------------------------------------------------------------
            // Navigation
            // ------------------------------------------------------------------
            AqSectionHeader(text = "Navigation")
            AqNavigationPreview()

            // ------------------------------------------------------------------
            // Surfaces
            // ------------------------------------------------------------------
            AqSectionHeader(text = "Surfaces")
            AqSurfacesPreview()

            // ------------------------------------------------------------------
            // Data Display
            // ------------------------------------------------------------------
            AqSectionHeader(text = "Data Display")
            AqDataDisplayPreview()

            // ------------------------------------------------------------------
            // Feedback
            // ------------------------------------------------------------------
            AqSectionHeader(text = "Feedback")
            AqFeedbackPreview()

            // ------------------------------------------------------------------
            // Selection
            // ------------------------------------------------------------------
            AqSectionHeader(text = "Selection")
            AqSelectionPreview()

            // ------------------------------------------------------------------
            // Media
            // ------------------------------------------------------------------
            AqSectionHeader(text = "Media")
            AqMediaPreview()

            // ------------------------------------------------------------------
            // Mobile Interactions
            // ------------------------------------------------------------------
            AqSectionHeader(text = "Mobile Interactions")
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
            AqHeading(text = "Heading 3Xl (36 sp)", size = AqType.Heading3Xl)
            AqHeading(text = "Heading 2Xl (32 sp)", size = AqType.Heading2Xl)
            AqHeading(text = "Heading Lg (18 sp)", size = AqType.HeadingLg)
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

/** Previews de acciones y botones. */
@Composable
private fun AqActionsPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
        AqText(
            text = "Botones — variantes y estados",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
            AqPrimaryButton(label = "Primaria", onClick = {})
            AqSecondaryButton(label = "Secundaria", onClick = {})
            AqDangerButton(label = "Destructiva", onClick = {})
            com.agendaqr.core.ui.components.AqOutlineButton(label = "Contorneada", onClick = {})
        }
        Row(horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
            AqButton(
                label = "Deshabilitada",
                onClick = {},
                enabled = false,
                variant = AqButtonVariant.Primary,
            )
            AqButton(
                label = "Cargando",
                onClick = {},
                isLoading = true,
                busyDescription = "Guardando…",
                variant = AqButtonVariant.Primary,
            )
            AqTextAction(label = "Texto", onClick = {}, variant = AqTextActionVariant.Default)
            AqTextAction(label = "Peligro", onClick = {}, variant = AqTextActionVariant.Danger)
        }
        AqText(
            text = "Botón solo-icono y FAB extendida",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
            AqIconButton(
                contentDescription = "Más",
                onClick = {},
            ) {
                AqIcon(imageVector = com.agendaqr.core.ui.components.AqIcons.More, contentDescription = null)
            }
            com.agendaqr.core.ui.components.AqExtendedFab(
                label = "Guardar",
                icon = com.agendaqr.core.ui.components.AqIcons.Add,
                onClick = {},
            )
        }
        AqText(
            text = "Grupo de acciones",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        AqButtonGroup(
            actions = listOf(
                com.agendaqr.core.ui.components.AqGroupedAction("cancel", "Cancelar", onClick = {}),
                com.agendaqr.core.ui.components.AqGroupedAction("save", "Guardar", onClick = {}, emphasized = true),
            ),
        )
    }
}

/** Previews de formularios. */
@Composable
private fun AqFormsPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
        AqTextField(
            label = "Campo de texto",
            value = "",
            onValueChange = {},
        )
        AqMultilineTextField(
            label = "Nota multilínea",
            value = "",
            onValueChange = {},
        )
        AqSearchField(
            value = "",
            onValueChange = {},
            label = "Buscar",
            placeholder = "Buscar en Agenda QR",
        )
        AqText(
            text = "Estado de error",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        AqTextField(
            label = "Importe",
            value = "abc",
            onValueChange = {},
            isError = true,
            errorMessage = "Importe inválido",
        )
        AqText(
            text = "Controles booleanos",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
            AqCheckbox(label = "Casilla", checked = true, onCheckedChange = {})
            AqSwitch(label = "Interruptor", checked = false, onCheckedChange = {})
        }
        AqTextField(
            label = "Importe",
            value = "10,00",
            onValueChange = {},
            leadingIcon = com.agendaqr.core.ui.components.AqIcons.Wallet,
        )
        AqSelect(
            options = listOf(
                AqChoiceOption("bob", "BOB"),
                AqChoiceOption("usd", "USD"),
                AqChoiceOption("otra", "Otra"),
            ),
            selectedId = "bob",
            onSelected = {},
            label = "Moneda",
            dialogTitle = "Selecciona moneda",
            confirmLabel = "Aceptar",
            dismissLabel = "Cancelar",
        )
        AqChoiceGroup(
            label = "Tipo",
            options = listOf(
                AqChoiceOption("pago", "Pago"),
                AqChoiceOption("cobro", "Cobro"),
            ),
            selectedId = "pago",
            onSelected = {},
        )
    }
}

/** Previews de navegación. */
@Composable
private fun AqNavigationPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
        AqText(
            text = "Top Bar",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        AqTopBar(
            title = "Título de pantalla",
            onBack = {},
            backLabel = "Atrás",
            actions = listOf(
                com.agendaqr.core.ui.components.AqTopBarAction(
                    label = "Compartir",
                    icon = com.agendaqr.core.ui.components.AqIcons.Share,
                    onClick = {},
                ),
            ),
        )
        AqText(
            text = "Segmented Control (filtros)",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        AqSegmentedControl(
            options = listOf(
                AqSegmentedOption(label = "Todos", count = 12),
                AqSegmentedOption(
                    label = "Favoritos",
                    icon = com.agendaqr.core.ui.components.AqIcons.Favorite,
                    count = 3,
                ),
            ),
            selectedIndex = 0,
            onSelect = {},
        )
        AqText(
            text = "Tabs",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        com.agendaqr.core.ui.components.AqTabs(
            sections = listOf("QR", "Actividades", "Comprobantes"),
            selectedIndex = 0,
            onSelect = {},
            content = { index -> AqText(text = "Contenido pestaña $index") },
        )
    }
}

/** Previews de superficies. */
@Composable
private fun AqSurfacesPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
        AqText(
            text = "Card",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        AqCard {
            AqText(text = "Contenido de tarjeta", color = AqColor.TextPrimary)
        }
        AqText(
            text = "Dialog / Bottom Sheet / Modal Sheet",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        AqText(
            text = "(requieren estado de visibilidad; se muestran en pantalla real)",
            color = AqColor.TextSecondary,
            size = AqType.BodyXs,
        )
        AqText(
            text = "Expansion Panel",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        val expanded = remember { mutableStateOf(false) }
        AqExpansionPanel(
            title = "Más detalles",
            expanded = expanded.value,
            onToggle = { expanded.value = !expanded.value },
        ) {
            AqText(text = "Contenido expandido del panel.")
        }
        AqText(
            text = "List Body + List Row",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        AqListBody {
            AqListRow(
                title = "Destino ejemplo",
                subtitle = "Comprobante recibido",
                showChevron = true,
                onClick = {},
            )
        }
    }
}

/** Previews de visualización de datos. */
@Composable
private fun AqDataDisplayPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
        AqText(
            text = "List Row",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        AqListRow(
            title = "Destino QR",
            subtitle = "Comercio ejemplo",
            leading = {
                AqIconBadge(
                    icon = com.agendaqr.core.ui.components.AqIcons.QrCode,
                    tone = com.agendaqr.core.ui.components.AqTone.Info,
                )
            },
            trailing = {
                com.agendaqr.core.ui.components.AqFavoriteToggle(
                    favorite = false,
                    contentDescription = "Marcar favorito",
                    onCheckedChange = {},
                )
            },
            showDivider = true,
        )
        AqText(
            text = "Badge / Tonal Tag",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
            AqBadge(text = "Activo", tone = com.agendaqr.core.ui.components.AqTone.Success)
            AqBadge(text = "Pendiente", tone = com.agendaqr.core.ui.components.AqTone.Warning)
            AqBadge(text = "Error", tone = com.agendaqr.core.ui.components.AqTone.Danger)
            AqTonalTag(text = "PAGO", tone = com.agendaqr.core.ui.components.AqTone.Success)
            AqTonalTag(text = "COBRO", tone = com.agendaqr.core.ui.components.AqTone.Info)
        }
        AqText(
            text = "Meta Row",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        AqMetaRow(
            label = "Sincronizado",
            value = "14:30",
            trailing = {
                AqIcon(imageVector = com.agendaqr.core.ui.components.AqIcons.Copy, contentDescription = null)
            },
        )
        AqText(
            text = "Index Table",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        com.agendaqr.core.ui.components.AqIndexTable(
            columns = listOf(
                com.agendaqr.core.ui.components.AqTableColumn("concepto", "Concepto"),
                com.agendaqr.core.ui.components.AqTableColumn("importe", "Importe"),
            ),
            rows = listOf(
                com.agendaqr.core.ui.components.AqTableRow("1", mapOf("concepto" to "Cobro", "importe" to "10,00")),
            ),
        )
        AqText(
            text = "Section Header",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        AqSectionHeader(text = "Grupo de operaciones")
    }
}

/** Previews de feedback y estados. */
@Composable
private fun AqFeedbackPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
        AqText(
            text = "Status Banner por tono",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        AqStatusBanner(message = "Operación guardada", tone = com.agendaqr.core.ui.components.AqTone.Success)
        AqStatusBanner(
            message = "No se pudo sincronizar",
            tone = com.agendaqr.core.ui.components.AqTone.Danger,
            action = AqBannerAction("Reintentar", {}),
        )
        AqStatusBanner(message = "Modo local activado", tone = com.agendaqr.core.ui.components.AqTone.Info)
        AqText(
            text = "Empty State",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        AqEmptyState(
            title = "Sin destinos",
            subtitle = "Trae tu primer QR para comenzar.",
            icon = com.agendaqr.core.ui.components.AqIcons.QrCode,
            actionLabel = "Traer un QR",
            onAction = {},
        )
        AqText(
            text = "Loading / Spinner / Skeleton",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        AqLoading(message = "Cargando…")
        AqSpinner(contentDescription = "Cargando")
        com.agendaqr.core.ui.components.AqSkeleton(lines = 3)
        AqText(
            text = "Progress Bar",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        AqProgressBar(
            progress = com.agendaqr.core.ui.components.AqProgress(0.6f),
            contentDescription = "Progreso de importación",
        )
        AqText(
            text = "Status Pill",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        AqStatusPill(text = "Al día", tone = com.agendaqr.core.ui.components.AqTone.Success)
    }
}

/** Previews de selección. */
@Composable
private fun AqSelectionPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
        AqText(
            text = "Filter Chips",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
            AqFilterChip(label = "Todos", selected = true, onClick = {}, count = 12)
            AqFilterChip(label = "Favoritos", selected = false, onClick = {}, count = 3)
            AqFilterChip(label = "Pago", selected = false, onClick = {}, count = 5)
        }
        AqText(
            text = "Context Badge",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        AqContextBadge(contextId = "ctx-1")
        AqText(
            text = "Evidence Badge",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        com.agendaqr.core.ui.components.AqEvidenceBadge(
            text = "Comprobante recibido",
            icon = com.agendaqr.core.ui.components.AqIcons.Attachment,
        )
    }
}

/** Previews de medios. */
@Composable
private fun AqMediaPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
        AqText(
            text = "QR Preview",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        com.agendaqr.core.ui.components.AqQrPreview(
            encodedQr = "",
            unavailableMessage = "Sin vista previa de QR",
        )
        AqText(
            text = "Carousel",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        AqCarousel(
            items = listOf(
                AqCarouselItem("card-1", "Tarjeta 1"),
                AqCarouselItem("card-2", "Tarjeta 2"),
                AqCarouselItem("card-3", "Tarjeta 3"),
            ),
        ) { item ->
            com.agendaqr.core.ui.components.AqTappableCard(
                onClick = {},
                modifier = Modifier.size(AqMetrics.FavCardWidth),
            ) {
                AqText(text = item.label ?: "", color = AqColor.TextPrimary)
            }
        }
    }
}

/** Previews de interacciones móviles. */
@Composable
private fun AqMobileInteractionsPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
        AqText(
            text = "Expansion Panel",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        val expanded = remember { mutableStateOf(false) }
        AqExpansionPanel(
            title = "Más detalles",
            expanded = expanded.value,
            onToggle = { expanded.value = !expanded.value },
        ) {
            AqText(text = "Contenido revelado al expandir.")
        }
        AqText(
            text = "Bottom Action Dock",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        AqBottomActionDock {
            AqBottomAction(label = "Guardar", onClick = {})
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
