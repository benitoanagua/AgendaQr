package com.agendaqr.core.ui.lab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.agendaqr.core.ui.lab.model.LabCategory
import com.agendaqr.core.ui.lab.model.LabComponentContract
import com.agendaqr.core.ui.lab.model.LabDarkThemeSupport
import com.agendaqr.core.ui.lab.model.LabInteractionLens
import com.agendaqr.core.ui.lab.model.LabInteractionMatrix
import com.agendaqr.core.ui.lab.model.LabLensVerdict
import com.agendaqr.core.ui.theme.XauxaSpacing
import com.agendaqr.core.ui.theme.XauxaType

/**
 * Component inspector. Everything shown here is derived from the catalog
 * contract: there is no duplicated metadata per screen. The preview is the
 * real component with fake data; the interaction matrix and the declared
 * states expose the honest review classification. The event log closes the
 * pane so interactive callbacks always have a visible response.
 */
@Composable
internal fun LabInspectorPane(
    contract: LabComponentContract,
    darkPreviewActive: Boolean,
    events: List<String>,
    onEvent: (String) -> Unit,
    modifier: Modifier = Modifier,
    scrollable: Boolean = true,
) {
    val sections: List<@Composable () -> Unit> = listOf(
        { LabIdentitySection(contract, darkPreviewActive) },
        {
            if (contract.category == LabCategory.FOUNDATIONS) {
                LabFoundationPreview(contract.id)
            } else {
                LabComponentPreview(contract, onEvent)
            }
        },
        { LabInteractionSection(contract) },
        { LabDeclaredStatesSection(contract) },
        { LabApiSection(contract) },
        { LabTokensSection(contract) },
        { LabUsageSection(contract) },
        { LabNotesSection(contract) },
        { LabEventLog(events) },
    )

    if (scrollable) {
        LazyColumn(
            modifier = modifier.fillMaxWidth().fillMaxSize(),
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

@Composable
private fun LabIdentitySection(contract: LabComponentContract, darkPreviewActive: Boolean) {
    LabPanel(
        title = contract.name,
        subtitle = "${contract.id} · ${contract.category.label}",
        trailing = { LabBadge(if (contract.isInteractive) "Interactivo" else "Presentacional") },
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
            Text(contract.purpose, fontSize = XauxaType.Body, color = MaterialTheme.colorScheme.onSurface)
            Text(
                contract.description,
                fontSize = XauxaType.Label,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            LabLabelValue("Tema oscuro", contract.darkThemeSupport.label)
            Text(
                contract.darkThemeNote,
                fontSize = XauxaType.Caption,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            if (darkPreviewActive && contract.darkThemeSupport == LabDarkThemeSupport.NOT_SUPPORTED) {
                Text(
                    "Preview oscuro activo y el componente no cambia: esta es la brecha real de tokens dark " +
                        "de Xauxa, no un fallo del laboratorio.",
                    fontSize = XauxaType.Caption,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.error,
                )
            }
        }
    }
}

@Composable
private fun LabInteractionSection(contract: LabComponentContract) {
    LabPanel(
        title = "Matriz de interacción",
        subtitle = "Lentes canónicas con veredicto honesto: estado real, lente simulada o no aplica",
        trailing = {
            val applicable = LabInteractionMatrix.applicableLenses(contract).size
            LabBadge("$applicable / ${LabInteractionMatrix.lenses.size} aplican")
        },
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Xs)) {
            LabInteractionMatrix.lenses.forEach { lens ->
                val verdict = LabInteractionMatrix.verdict(contract, lens)
                val exactState = LabInteractionMatrix.exactStateFor(contract, lens)
                LabInteractionRow(lens, verdict, exactState?.name)
            }
        }
    }
}

@Composable
private fun LabInteractionRow(lens: LabInteractionLens, verdict: LabLensVerdict, exactState: String?) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Column {
            Text(lens.label, fontSize = XauxaType.Label, color = MaterialTheme.colorScheme.onSurface)
            Text(
                if (verdict == LabLensVerdict.REAL && exactState != null) {
                    "Estado declarado: $exactState"
                } else {
                    lens.description
                },
                fontSize = XauxaType.Caption,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        LabLensVerdictBadge(verdict)
    }
}

@Composable
private fun LabLensVerdictBadge(verdict: LabLensVerdict) {
    val scheme = MaterialTheme.colorScheme
    val color = when (verdict) {
        LabLensVerdict.REAL -> scheme.primary
        LabLensVerdict.SIMULATED -> scheme.tertiary
        LabLensVerdict.NOT_APPLICABLE -> scheme.outline
    }
    Text(
        verdict.label,
        fontSize = XauxaType.Caption,
        color = color,
    )
}

@Composable
private fun LabDeclaredStatesSection(contract: LabComponentContract) {
    LabPanel(
        title = "Estados declarados",
        subtitle = "Clasificación verificada contra la implementación, no contra la documentación",
        trailing = { LabBadge("${contract.states.size} estados") },
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
            contract.states.forEach { state ->
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(
                        state.name,
                        fontSize = XauxaType.Label,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    LabStatusBadge(state.status)
                }
                Text(
                    state.description,
                    fontSize = XauxaType.Caption,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun LabApiSection(contract: LabComponentContract) {
    LabPanel(title = "Contrato de API", subtitle = "Parámetros y eventos de la implementación real") {
        Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Xs)) {
            contract.props.forEach { prop ->
                LabLabelValue(prop.name, prop.type + (prop.default?.let { " = $it" } ?: ""))
            }
            if (contract.events.isEmpty()) {
                Text(
                    "Sin eventos: componente de presentación.",
                    fontSize = XauxaType.Caption,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                contract.events.forEach { event ->
                    LabLabelValue(event.name, event.description)
                }
            }
        }
    }
}

@Composable
private fun LabTokensSection(contract: LabComponentContract) {
    LabPanel(
        title = "Tokens Xauxa",
        subtitle = "Referencias reales consumidas por el componente",
        trailing = { LabBadge("${contract.tokens.size} tokens") },
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Xs)) {
            contract.tokens.forEach { token ->
                LabLabelValue(token.token, token.role)
            }
        }
    }
}

@Composable
private fun LabUsageSection(contract: LabComponentContract) {
    LabPanel(title = "Uso en AgendaQr", subtitle = "Ejemplos concretos dentro del producto") {
        Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Xs)) {
            contract.usage.forEach { usage ->
                Text(
                    "· $usage",
                    fontSize = XauxaType.Label,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

@Composable
private fun LabNotesSection(contract: LabComponentContract) {
    if (contract.notes.isEmpty()) return
    LabPanel(title = "Recomendaciones y restricciones", subtitle = "Reglas de uso y brechas conocidas") {
        Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Xs)) {
            contract.notes.forEach { note ->
                Text(
                    "· $note",
                    fontSize = XauxaType.Label,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}
