package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.agendaqr.core.ui.components.XauxaEmptyState
import com.agendaqr.core.ui.components.XauxaSecondaryButton
import com.agendaqr.core.ui.components.XauxaStatusBanner
import com.agendaqr.core.ui.components.XauxaListRow
import com.agendaqr.core.ui.components.XauxaLoading
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.core.ui.theme.XauxaSpacing
import com.agendaqr.core.ui.theme.XauxaType

@Composable
fun ContextsScreen(
    state: ContextsUiState,
    onAction: (ContextAction) -> Unit,
) {
    when (state.route) {
        ContextRoute.List -> ContextList(state, onAction)
        is ContextRoute.Detail -> ContextDetail(state, onAction)
    }
}

@Composable
private fun ContextList(state: ContextsUiState, onAction: (ContextAction) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(XauxaSpacing.Xxl),
        verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Lg),
    ) {
        Text("Contextos", fontSize = XauxaType.Display, fontWeight = FontWeight.Bold, color = XauxaColor.TextPrimary)
        state.error?.let { XauxaStatusBanner(it, danger = true) }
        when {
            state.isLoading -> XauxaLoading(message = "Cargando contextos…")
            state.contexts.isEmpty() -> XauxaEmptyState(
                title = "Sin contextos",
                actionLabel = "Volver",
                onAction = { onAction(ContextAction.Back) },
            )
            else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
                items(state.contexts, key = { it.id }) { context ->
                    XauxaListRow(
                        title = context.name,
                        subtitle = context.note?.takeIf { it.isNotBlank() },
                        onClick = { onAction(ContextAction.Open(context.id)) },
                    )
                }
            }
        }
    }
}

@Composable
private fun ContextDetail(state: ContextsUiState, onAction: (ContextAction) -> Unit) {
    val contents = state.contents
    Column(
        modifier = Modifier.fillMaxSize().padding(XauxaSpacing.Xxl),
        verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Lg),
    ) {
        XauxaSecondaryButton(label = "Volver", onClick = { onAction(ContextAction.Back) })
        contents?.let { data ->
            Text(data.context.name, fontSize = XauxaType.Display, fontWeight = FontWeight.Bold, color = XauxaColor.TextPrimary)
            data.context.note?.takeIf { it.isNotBlank() }?.let {
                Text(it, fontSize = XauxaType.Label, color = XauxaColor.TextSecondary)
            }
            Text("QR · " + data.destinations.size, fontSize = XauxaType.Title, color = XauxaColor.TextPrimary)
            data.destinations.forEach { destination ->
                Text(destination.name.ifBlank { "QR sin nombre" }, color = XauxaColor.TextSecondary)
            }
            Text("Actividad reciente · " + data.operations.size, fontSize = XauxaType.Title, color = XauxaColor.TextPrimary)
            data.operations.take(5).forEach { operation ->
                Text(
                    listOfNotNull(
                        when (operation.type) {
                            com.agendaqr.destinations.domain.OperationType.PAGO -> "Pago"
                            com.agendaqr.destinations.domain.OperationType.COBRO -> "Cobro"
                        },
                        operation.amount,
                        operation.currency,
                        operation.personOrEntity,
                    )
                        .joinToString(" · "),
                    color = XauxaColor.TextSecondary,
                )
            }
            Text("Comprobantes · " + data.comprobantes.size, fontSize = XauxaType.Title, color = XauxaColor.TextPrimary)
        } ?: XauxaLoading(message = "Cargando contexto…")
    }
}
