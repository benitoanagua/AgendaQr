package com.agendaqr.core.ui.lab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.agendaqr.core.ui.lab.model.LabCatalogIntegrity
import com.agendaqr.core.ui.lab.model.LabCatalogQuery
import com.agendaqr.core.ui.lab.model.LabComponentCatalog
import com.agendaqr.core.ui.lab.model.LabPatterns
import com.agendaqr.core.ui.theme.XauxaTheme
import com.agendaqr.core.ui.theme.XauxaMetrics
import com.agendaqr.core.ui.theme.XauxaSpacing
import com.agendaqr.core.ui.theme.XauxaType

/**
 * Development-only component laboratory for AgendaQr.
 *
 * A navigable catalog of the Xauxa components and token groups that really
 * exist in core:ui, with an inspector that documents each contract
 * (responsibility, props, states, events, tokens and usage) and a live
 * interactive preview fed with fake local data.
 *
 * Design notes:
 * - The catalog model is pure Kotlin (no Compose) and lives in `lab.model`,
 *   so inventory integrity, search and interaction rules are unit-testable
 *   without a UI runtime.
 * - The layout is adaptive: below XauxaMetrics.BreakpointMedium (640dp, a
 *   breakpoint documented by Xauxa) the page collapses to a single
 *   scroll owner to avoid nested-scroll conflicts on narrow devices.
 * - The dark preview wraps everything in AgendaQrTheme with the dark
 *   Material scheme. Xauxa components consume fixed light tokens, so the
 *   preview makes the real dark-mode gap visible instead of hiding it.
 * - The lab is intentionally isolated from production navigation,
 *   repositories, services, authentication, camera, storage and
 *   notifications. Its only host is the WebAssembly target of core:ui,
 *   which renders it in a browser; no production app entry point
 *   references it.
 */
@Composable
fun AgendaQrComponentLab(
    modifier: Modifier = Modifier,
) {
    var query by remember { mutableStateOf(LabCatalogQuery()) }
    var section by rememberSaveable { mutableStateOf(LabSection.COMPONENTS) }
    var selectedId by rememberSaveable { mutableStateOf(LabComponentCatalog.components.first().id) }
    var selectedPatternId by rememberSaveable { mutableStateOf(LabPatterns.all.first().id) }
    var darkPreview by rememberSaveable { mutableStateOf(false) }
    val events = remember { mutableStateListOf<String>() }
    val integrityProblems = remember { LabCatalogIntegrity.validate() }

    val contract = LabComponentCatalog.find(selectedId) ?: LabComponentCatalog.all.first()
    val pattern = LabPatterns.find(selectedPatternId) ?: LabPatterns.all.first()

    fun onEvent(event: String) {
        events.add(event)
        if (events.size > MAX_LOGGED_EVENTS) events.removeAt(0)
    }

    XauxaTheme(darkTheme = darkPreview) {
        Surface(
            modifier = modifier.fillMaxSize(),
            color = XauxaColor.Background,
        ) {
            BoxWithConstraints(modifier = Modifier.fillMaxSize().padding(XauxaSpacing.Lg)) {
                val compact = maxWidth < XauxaMetrics.BreakpointMedium
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Lg),
                ) {
                    LabHeader(
                        totalComponents = LabComponentCatalog.all.size,
                        integrityProblems = integrityProblems,
                        darkPreview = darkPreview,
                        onDarkPreviewChange = { darkPreview = it },
                    )

                    @Composable
                    fun catalogPane(modifier: Modifier, scrollable: Boolean) {
                        LabCatalogPane(
                            catalog = LabComponentCatalog.all,
                            query = query,
                            onQueryChange = { query = it },
                            selectedId = contract.id,
                            onComponentSelected = { selectedId = it },
                            modifier = modifier,
                            scrollable = scrollable,
                            section = section,
                            onSectionChange = { section = it },
                            selectedPatternId = pattern.id,
                            onPatternSelected = { selectedPatternId = it },
                        )
                    }

                    @Composable
                    fun inspectorPane(modifier: Modifier, scrollable: Boolean) {
                        if (section == LabSection.PATTERNS) {
                            LabPatternPane(
                                pattern = pattern,
                                modifier = modifier,
                                scrollable = scrollable,
                                onComponentSelected = { selectedId = it },
                                onSectionChange = { section = it },
                                onEvent = ::onEvent,
                            )
                        } else {
                            LabInspectorPane(
                                contract = contract,
                                darkPreviewActive = darkPreview,
                                events = events,
                                onEvent = ::onEvent,
                                modifier = modifier,
                                scrollable = scrollable,
                            )
                        }
                    }

                    if (compact) {
                        Column(
                            modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
                            verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Lg),
                        ) {
                            catalogPane(Modifier.fillMaxWidth(), false)
                            inspectorPane(Modifier.fillMaxWidth(), false)
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxSize(),
                            horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Lg),
                        ) {
                            catalogPane(Modifier.weight(CATALOG_PANE_WEIGHT).fillMaxHeight(), true)
                            inspectorPane(Modifier.weight(INSPECTOR_PANE_WEIGHT).fillMaxHeight(), true)
                        }
                    }
                }
            }
        }
    }
}

private const val MAX_LOGGED_EVENTS = 12
private const val CATALOG_PANE_WEIGHT = 0.34f
private const val INSPECTOR_PANE_WEIGHT = 0.66f

@Composable
private fun LabHeader(
    totalComponents: Int,
    integrityProblems: List<String>,
    darkPreview: Boolean,
    onDarkPreviewChange: (Boolean) -> Unit,
) {
    LabPanel(
        title = "AgendaQr · Laboratorio de componentes",
        subtitle = "Herramienta de desarrollo; aislada de la navegación y la lógica de producción",
        trailing = {
            LabChoiceChip(
                label = if (darkPreview) "Preview: oscuro" else "Preview: claro",
                selected = darkPreview,
                onClick = { onDarkPreviewChange(!darkPreview) },
            )
        },
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Xs)) {
            Row(horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
                LabBadge("$totalComponents entradas")
                if (integrityProblems.isEmpty()) {
                    LabBadge("Inventario íntegro")
                } else {
                    Text(
                        "Inventario con ${integrityProblems.size} problema(s); ver pruebas",
                        fontSize = XauxaType.Caption,
                        fontWeight = FontWeight.SemiBold,
                        color = XauxaColor.Danger,
                    )
                }
            }
            Text(
                "Los componentes resuelven el esquema vía XauxaColor: el preview oscuro aplica valores de referencia pendientes de validación.",
                fontSize = XauxaType.Caption,
                color = XauxaColor.TextSecondary,
            )
        }
    }
}
