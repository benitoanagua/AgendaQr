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
import com.agendaqr.core.ui.components.AqTooltip
import com.agendaqr.core.ui.components.AqTopBar
import com.agendaqr.core.ui.components.AqTone
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

/** Previews de acciones y botones: variantes, estados y temas. */
@Composable
private fun AqActionsPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
        AqText(
            text = "Botones primarios — normal, deshabilitado, cargando",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
            AqPrimaryButton(label = "Primaria", onClick = {})
            AqPrimaryButton(
                label = "Deshabilitada",
                onClick = {},
                enabled = false,
            )
            AqPrimaryButton(
                label = "Cargando",
                onClick = {},
                isLoading = true,
                busyDescription = "Guardando…",
            )
        }
        AqText(
            text = "Botones secundarios — normal, deshabilitado, contorneada",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
            AqSecondaryButton(label = "Secundaria", onClick = {})
            AqSecondaryButton(label = "Deshabilitada", onClick = {}, enabled = false)
            com.agendaqr.core.ui.components.AqOutlineButton(
                label = "Contorneada",
                onClick = {},
            )
        }
        AqText(
            text = "Botón destructivo y acciones de texto",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
            AqDangerButton(label = "Destructiva", onClick = {})
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
                AqIcon(
                    imageVector = com.agendaqr.core.ui.components.AqIcons.More,
                    contentDescription = null,
                )
            }
            com.agendaqr.core.ui.components.AqExtendedFab(
                label = "Guardar",
                icon = com.agendaqr.core.ui.components.AqIcons.Add,
                onClick = {},
            )
        }
        AqText(
            text = "Grupo de acciones (una enfatizada)",
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

/** Previews de formularios: normal, deshabilitado, error, carga, contenido corto y largo. */
@Composable
private fun AqFormsPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
        AqText(
            text = "Text Field — normal, deshabilitado, error",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
            Column {
                AqTextField(
                    label = "Campo de texto",
                    value = "Valor corto",
                    onValueChange = {},
                )
            }
            Column {
                AqTextField(
                    label = "Deshabilitado",
                    value = "No editable",
                    onValueChange = {},
                    enabled = false,
                )
            }
            Column {
                AqTextField(
                    label = "Error",
                    value = "abc",
                    onValueChange = {},
                    isError = true,
                    errorMessage = "Valor inválido",
                )
            }
        }
        AqText(
            text = "Text Field — contenido largo (se ajusta al ancho)",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        AqTextField(
            label = "Texto largo",
            value = "Este es un valor de texto bastante largo que se extiende más allá de lo habitual para verificar el desbordamiento y el ajuste al ancho del campo en pantallas compactas.",
            onValueChange = {},
        )
        AqText(
            text = "Multiline Text Field — normal, error, contenido largo",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
            AqMultilineTextField(
                label = "Nota multilínea",
                value = "Nota corta.",
                onValueChange = {},
            )
            AqMultilineTextField(
                label = "Nota con error",
                value = "Nota inválida",
                onValueChange = {},
                isError = true,
                errorMessage = "La nota supera el límite permitido.",
            )
        }
        AqMultilineTextField(
            label = "Nota larga",
            value = "Esta es una nota de varias líneas que simula contenido largo del usuario. "
                + "Se compone con minLines y crece en altura según el contenido y la escala de fuente, "
                + "sin altura fija que recorte el texto ni el teclado virtual.",
            onValueChange = {},
            minLines = 3,
        )
        AqText(
            text = "Search Field — normal, con limpieza, deshabilitado",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
            AqSearchField(
                value = "",
                onValueChange = {},
                label = "Buscar",
                placeholder = "Buscar en Agenda QR",
                onClear = {},
                clearLabel = "Limpiar búsqueda",
            )
            AqSearchField(
                value = "término de búsqueda",
                onValueChange = {},
                label = "Buscar",
                placeholder = "Buscar en Agenda QR",
                onClear = {},
                clearLabel = "Limpiar búsqueda",
            )
            AqSearchField(
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
            Row(horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
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
            AqChoiceGroup(
                label = "Tipo (ChoiceGroup)",
                options = listOf(
                    AqChoiceOption("pago", "Pago"),
                    AqChoiceOption("cobro", "Cobro"),
                ),
                selectedId = "pago",
                onSelected = {},
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
        }
    }
}

/** Previews de navegación: estados de selección, activo y deshabilitado. */
@Composable
private fun AqNavigationPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
        AqText(
            text = "Top Bar — con flecha atrás, acciones y overflow",
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
                com.agendaqr.core.ui.components.AqTopBarAction(
                    label = "Buscar",
                    icon = com.agendaqr.core.ui.components.AqIcons.Search,
                    onClick = {},
                ),
            ),
            overflowActions = listOf(
                com.agendaqr.core.ui.components.AqOverflowAction(
                    label = "Más acciones",
                    onClick = {},
                ),
            ),
            overflowLabel = "Más",
        )
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
                    icon = com.agendaqr.core.ui.components.AqIcons.Favorite,
                    count = 3,
                ),
            ),
            selectedIndex = 0,
            onSelect = {},
        )
        AqText(
            text = "Tabs — desplazable con icono+etiqueta",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        com.agendaqr.core.ui.components.AqTabs(
            sections = listOf("QR", "Actividades", "Comprobantes", "Ajustes", "Cuenta"),
            selectedIndex = 1,
            onSelect = {},
            content = { index -> AqText(text = "Contenido pestaña $index") },
        )
    }
}

/** Previews de superficies: normal, seleccionado, deshabilitado, error, expandido. */
@Composable
private fun AqSurfacesPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
        AqText(
            text = "Card — normal, con contenido largo",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        AqCard {
            AqText(text = "Contenido de tarjeta", color = AqColor.TextPrimary)
        }
        AqCard {
            Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
                AqText(
                    text = "Contenido largo que ocupa el ancho completo de la tarjeta para verificar el relleno interno de 16 dp y que el texto no toque el borde, incluso con fuente al 150 % o 200 %.",
                    color = AqColor.TextPrimary,
                )
            }
        }
        AqText(
            text = "Dialog / Bottom Sheet (requieren estado de visibilidad; se muestran en pantalla real)",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        AqText(
            text = "Expansion Panel — colapsado y expandido",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        val expanded = remember { mutableStateOf(false) }
        AqExpansionPanel(
            title = "Más detalles",
            expanded = expanded.value,
            onToggle = { expanded.value = !expanded.value },
        ) {
            AqText(text = "Contenido expandido del panel revelado al pulsar el título.")
        }
        AqText(
            text = "List Body + List Row — normal, seleccionado, deshabilitado, con chevron y divider",
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
            AqListRow(
                title = "Fila seleccionada",
                subtitle = "Estado activo",
                showChevron = true,
                onClick = {},
                selected = true,
            )
            AqListRow(
                title = "Fila sin acción",
                subtitle = "Sin interacción (onClick nulo)",
                showChevron = true,
                onClick = null,
            )
        }
    }
}

/** Previews de visualización de datos: filas seleccionadas, badges, tags, meta, tabla. */
@Composable
private fun AqDataDisplayPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
        AqText(
            text = "List Row — normal, seleccionado, deshabilitado",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        AqListBody {
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
            AqListRow(
                title = "Destino seleccionado",
                subtitle = "Estado activo",
                selected = true,
                showDivider = true,
                trailing = {
                    com.agendaqr.core.ui.components.AqFavoriteToggle(
                        favorite = true,
                        contentDescription = "Quitar favorito",
                        onCheckedChange = {},
                    )
                },
            )
            AqListRow(
                title = "Destino sin acción",
                subtitle = "Sin interacción (onClick nulo)",
                showDivider = true,
                onClick = null,
            )
        }
        AqText(
            text = "Badge / Tonal Tag — todos los tonos, outline y relleno",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
            AqBadge(text = "Activo", tone = com.agendaqr.core.ui.components.AqTone.Success)
            AqBadge(text = "Pendiente", tone = com.agendaqr.core.ui.components.AqTone.Warning)
            AqBadge(text = "Error", tone = com.agendaqr.core.ui.components.AqTone.Danger)
            AqBadge(text = "Neutral", tone = com.agendaqr.core.ui.components.AqTone.Neutral)
            AqTonalTag(text = "PAGO", tone = com.agendaqr.core.ui.components.AqTone.Success)
            AqTonalTag(text = "COBRO", tone = com.agendaqr.core.ui.components.AqTone.Info)
        }
        AqText(
            text = "Meta Row — label + valor + acción de copia",
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
            text = "Index Table — cabecera y filas",
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
                com.agendaqr.core.ui.components.AqTableRow("2", mapOf("concepto" to "Pago", "importe" to "25,50")),
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

/** Previews de feedback y estados: todos los tonos, vacío, carga, progreso. */
@Composable
private fun AqFeedbackPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
        AqText(
            text = "Status Banner por tono — Success, Danger, Warning, Info, Neutral",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
            AqStatusBanner(message = "Operación guardada", tone = com.agendaqr.core.ui.components.AqTone.Success)
            AqStatusBanner(
                message = "No se pudo sincronizar",
                tone = com.agendaqr.core.ui.components.AqTone.Danger,
                action = AqBannerAction("Reintentar", {}),
            )
            AqStatusBanner(
                message = "Revisa tu conexión a internet",
                tone = com.agendaqr.core.ui.components.AqTone.Warning,
            )
            AqStatusBanner(message = "Modo local activado", tone = com.agendaqr.core.ui.components.AqTone.Info)
            AqStatusBanner(message = "Aviso general", tone = com.agendaqr.core.ui.components.AqTone.Neutral)
        }
        AqText(
            text = "Empty State — con acción, con título largo, sin acción",
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
        AqEmptyState(
            title = "Este es un título de estado vacío bastante largo para verificar que el contenido no se desborde ni se solape con el subtítulo ni con la acción primaria en pantallas estrechas",
            subtitle = "Subtítulo que acompaña al título largo y describe la acción recomendada para salir de este estado vacío.",
            icon = com.agendaqr.core.ui.components.AqIcons.QrCode,
            actionLabel = "Acción primaria",
            onAction = {},
        )
        AqEmptyState(
            title = "Sin resultados",
            subtitle = "Ajusta los filtros o prueba con otra búsqueda.",
            icon = com.agendaqr.core.ui.components.AqIcons.SearchX,
        )
        AqText(
            text = "Loading / Spinner / Skeleton",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
            AqLoading(message = "Cargando…")
            AqSpinner(contentDescription = "Cargando")
            com.agendaqr.core.ui.components.AqSkeleton(lines = 3)
        }
        AqText(
            text = "Progress Bar — 60 %, determinista",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        AqProgressBar(
            progress = com.agendaqr.core.ui.components.AqProgress(0.6f),
            contentDescription = "Progreso de importación",
        )
        AqText(
            text = "Status Pill — normal e inversa (Modo local)",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
            AqStatusPill(text = "Al día", tone = com.agendaqr.core.ui.components.AqTone.Success)
            AqStatusPill(text = "Modo local", inverse = true, tone = com.agendaqr.core.ui.components.AqTone.Success)
        }
    }
}

/** Previews de selección: chips seleccionados/no, deshabilitados, conteo, badges. */
@Composable
private fun AqSelectionPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
        AqText(
            text = "Filter Chips — seleccionado, no seleccionado, deshabilitado, con conteo",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
            AqFilterChip(label = "Todos", selected = true, onClick = {}, count = 12)
            AqFilterChip(label = "Favoritos", selected = false, onClick = {}, count = 3)
            AqFilterChip(label = "Pago", selected = false, onClick = {}, count = 5)
            AqFilterChip(label = "Deshabilitado", selected = false, onClick = {}, enabled = false)
        }
        AqText(
            text = "Context Badge — identidad de contexto por color",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
            AqContextBadge(contextId = "ctx-1")
            AqContextBadge(contextId = "ctx-2")
            AqContextBadge(contextId = "ctx-3")
        }
        AqText(
            text = "Evidence Badge — X-01: icono + texto (nunca «Verificado» ni «Pagado»)",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        com.agendaqr.core.ui.components.AqEvidenceBadge(
            text = "Comprobante recibido",
            icon = com.agendaqr.core.ui.components.AqIcons.Attachment,
        )
    }
}

/** Previews de medios: QR Preview (sin imagen / no decodificable) y Carousel. */
@Composable
private fun AqMediaPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
        AqText(
            text = "QR Preview — sin vista previa y no decodificable",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
            com.agendaqr.core.ui.components.AqQrPreview(
                encodedQr = "",
                unavailableMessage = "Sin vista previa de QR",
            )
            com.agendaqr.core.ui.components.AqQrPreview(
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
            com.agendaqr.core.ui.components.AqTappableCard(
                onClick = {},
                modifier = Modifier.size(AqMetrics.FavCardWidth),
            ) {
                AqText(text = item.label ?: "", color = AqColor.TextPrimary)
            }
        }
    }
}

/** Previews de interacciones móviles: Bottom Action Dock (acción alcanzable) y Expansion Panel. */
@Composable
private fun AqMobileInteractionsPreview() {
    Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
        AqText(
            text = "Bottom Action Dock — acción primaria alcanzable (una mano) con IME",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        AqBottomActionDock {
            AqBottomAction(label = "Guardar", onClick = {})
        }
        AqText(
            text = "Expansion Panel — colapsado y expandido con contenido largo",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        val expanded = remember { mutableStateOf(false) }
        AqExpansionPanel(
            title = "Más detalles",
            expanded = expanded.value,
            onToggle = { expanded.value = !expanded.value },
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
                AqText(
                    text = "Contenido revelado al expandir, con varias líneas para verificar que la expansión no recorta el texto y respeta la escala de fuente del sistema.",
                )
                AqText(
                    text = "Línea adicional de contenido para comprobar la altura dinámica del panel expandido.",
                    color = AqColor.TextSecondary,
                )
            }
        }
        AqText(
            text = "Tooltip — etiqueta flotante tonal en estado visible",
            color = AqColor.TextPrimary,
            size = AqType.BodyMd,
        )
        AqTooltip(
            label = "Actualizar lista",
            isVisible = true,
            content = {
                AqIconButton(
                    contentDescription = "Actualizar lista",
                    onClick = {},
                ) {
                    AqIcon(
                    imageVector = com.agendaqr.core.ui.components.AqIcons.Search,
                        contentDescription = null,
                    )
                }
            },
        )
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
