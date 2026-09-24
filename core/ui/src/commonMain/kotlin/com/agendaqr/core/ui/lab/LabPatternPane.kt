package com.agendaqr.core.ui.lab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
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
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    if (pattern.demo) {
                        Text(
                            pattern.demoNote,
                            fontSize = XauxaType.Caption,
                            color = MaterialTheme.colorScheme.error,
                        )
                    }
                }
            }
        },
        {
            LabPanel(title = "Pasos", subtitle = "Orden de composición") {
                Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Xs)) {
                    pattern.steps.forEachIndexed { index, step ->
                        Text(
                            "${index + 1}. $step",
                            fontSize = XauxaType.Label,
                            color = MaterialTheme.colorScheme.onSurface,
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
                                color = MaterialTheme.colorScheme.error,
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
