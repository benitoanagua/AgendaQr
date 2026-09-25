package com.agendaqr.core.ui.lab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.XauxaDialog
import com.agendaqr.core.ui.components.XauxaEmptyState
import com.agendaqr.core.ui.components.XauxaErrorPage
import com.agendaqr.core.ui.components.XauxaFileUpload
import com.agendaqr.core.ui.components.XauxaHeroCard
import com.agendaqr.core.ui.components.XauxaInlineResult
import com.agendaqr.core.ui.components.XauxaScannerViewport
import com.agendaqr.core.ui.components.XauxaSettingRow
import com.agendaqr.core.ui.components.XauxaTextInput
import com.agendaqr.core.ui.components.XauxaToast
import com.agendaqr.core.ui.components.XauxaTone
import com.agendaqr.core.ui.lab.model.LabComponentCatalog
import com.agendaqr.core.ui.lab.model.LabPattern
import com.agendaqr.core.ui.theme.XauxaSpacing
import com.agendaqr.core.ui.theme.XauxaType

/**
 * Pattern detail pane. Everything is derived from [LabPattern] (pure model):
 * steps in order, the contracts it composes (resolved against the central
 * inventory) and the explicit demo note when the composition is not wired.
 */
@Composable
internal fun LabPatternPane(
    pattern: LabPattern,
    modifier: Modifier = Modifier,
    scrollable: Boolean = true,
    onComponentSelected: (String) -> Unit = {},
    onSectionChange: (LabSection) -> Unit = {},
    onEvent: (String) -> Unit = {},
) {
    val sections: List<@Composable () -> Unit> = listOf(
        {
            LabPanel(
                title = pattern.title,
                subtitle = "Patrón de composición · ${pattern.id}",
                trailing = { LabBadge(if (pattern.demo) "Demo" else "Composición") },
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
                    Text(
                        pattern.description,
                        fontSize = XauxaType.Body,
                        color = XauxaColor.TextPrimary,
                    )
                    if (pattern.demo) {
                        Text(
                            pattern.demoNote,
                            fontSize = XauxaType.Caption,
                            color = XauxaColor.Danger,
                        )
                    }
                }
            }
        },
        {
            LabPanel(
                title = "Escenario",
                subtitle = "Composición real con datos ficticios" + if (pattern.demo) " · demostración" else "",
            ) {
                LabPatternScenario(pattern, onEvent)
            }
        },
        {
            LabPanel(title = "Pasos", subtitle = "Orden de composición") {
                Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Xs)) {
                    pattern.steps.forEachIndexed { index, step ->
                        Text(
                            "${index + 1}. $step",
                            fontSize = XauxaType.Label,
                            color = XauxaColor.TextPrimary,
                        )
                    }
                }
            }
        },
        {
            LabPanel(
                title = "Contratos que compone",
                subtitle = "Resueltos contra el inventario central",
                trailing = { LabBadge("${pattern.components.size} contratos") },
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Xs)) {
                    pattern.components.forEach { id ->
                        val contract = LabComponentCatalog.find(id)
                        if (contract == null) {
                            Text(
                                "Contrato inexistente: $id",
                                fontSize = XauxaType.Caption,
                                color = XauxaColor.Danger,
                            )
                        } else {
                            LabCatalogRow(contract, selected = false) {
                                onSectionChange(if (contract.category == com.agendaqr.core.ui.lab.model.LabCategory.FOUNDATIONS) LabSection.FOUNDATIONS else LabSection.COMPONENTS)
                                onComponentSelected(id)
                            }
                        }
                    }
                }
            }
        },
    )

    if (scrollable) {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Md),
        ) {
            items(sections.size) { index -> sections[index]() }
        }
    } else {
        Column(
            modifier = modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Md),
        ) {
            sections.forEach { it() }
        }
    }
}

/**
 * Live scenario per pattern: the real contracts composed with fake local
 * data. Interactive callbacks report to the lab event log; platform-bound
 * steps (camera, picker) render their honest presentational state.
 */
@Composable
private fun LabPatternScenario(pattern: LabPattern, onEvent: (String) -> Unit) {
    when (pattern.id) {
        "primera-vez" -> XauxaEmptyState(
            title = "Sin destinos todavía",
            actionLabel = "Añadir destino",
            onAction = { onEvent("Patrón primera-vez: XauxaEmptyState.onAction") },
        )
        "fallo-puntual" -> XauxaToast(
            message = "No se pudo sincronizar",
            tone = XauxaTone.Danger,
            actionLabel = "Reintentar",
            onAction = { onEvent("Patrón fallo-puntual: XauxaToast.onAction") },
            onDismiss = { onEvent("Patrón fallo-puntual: XauxaToast.onDismiss") },
        )
        "fallo-contenido" -> XauxaInlineResult(
            title = "Comprobante ilegible",
            meta = "IMG_042.png · reintenta la captura",
            tone = XauxaTone.Danger,
        )
        "fallo-aplicacion" -> XauxaErrorPage(
            title = "No se pudo cargar",
            message = "La sincronización falló. Revisa tu conexión.",
            actionLabel = "Reintentar",
            onAction = { onEvent("Patrón fallo-aplicacion: XauxaErrorPage.onAction") },
            secondaryLabel = "Volver",
            onSecondary = { onEvent("Patrón fallo-aplicacion: XauxaErrorPage.onSecondary") },
        )
        "pantalla-completa" -> XauxaHeroCard(
            value = "Bs 1.250",
            label = "Total de obligaciones",
            footer = "3 pendientes · 2 al día",
        )
        "incorporacion" -> Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Md)) {
            XauxaScannerViewport()
            XauxaFileUpload(
                onSelect = { onEvent("Patrón incorporacion: XauxaFileUpload.onSelect (simulado)") },
                onClear = { onEvent("Patrón incorporacion: XauxaFileUpload.onClear") },
            )
            var manual by rememberSaveable { mutableStateOf("") }
            XauxaTextInput(
                label = "Pegar contenido",
                value = manual,
                onValueChange = { manual = it },
            )
        }
        "bloqueo-opcional" -> {
            var locked by rememberSaveable { mutableStateOf(false) }
            XauxaSettingRow(
                title = "Bloquear edición",
                description = "Pide confirmación antes de modificar",
                checked = locked,
                onCheckedChange = {
                    locked = it
                    onEvent("Patrón bloqueo-opcional: checked=$it")
                },
            )
        }
        "confirmacion-destructiva" -> XauxaDialog(
            title = "Eliminar destino",
            message = "Se eliminará el destino y sus comprobantes. Esta acción no se puede deshacer.",
            confirmLabel = "Eliminar",
            onConfirm = { onEvent("Patrón confirmacion-destructiva: XauxaDialog.onConfirm (demo, sin reversión real)") },
            dismissLabel = "Cancelar",
            onDismiss = { onEvent("Patrón confirmacion-destructiva: XauxaDialog.onDismiss") },
        )
        else -> Text(
            "Patrón sin escenario.",
            fontSize = XauxaType.Label,
            color = XauxaColor.TextSecondary,
        )
    }
}
