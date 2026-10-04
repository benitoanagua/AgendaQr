package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.agendaqr.core.ui.components.XauxaEmptyState
import com.agendaqr.core.ui.components.XauxaHeading
import com.agendaqr.core.ui.components.XauxaListRow
import com.agendaqr.core.ui.components.XauxaLoading
import com.agendaqr.core.ui.components.XauxaSecondaryButton
import com.agendaqr.core.ui.components.XauxaStatusBanner
import com.agendaqr.core.ui.components.XauxaTextAction
import com.agendaqr.core.ui.components.XauxaTone
import com.agendaqr.core.ui.components.XauxaHeading
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.core.ui.theme.XauxaSpacing
import com.agendaqr.core.ui.theme.XauxaType

/**
 * S06 — Contexto. `onBack` cierra la superficie según el punto de entrada:
 * desde la Lista termina el flujo (vuelve a Inicio o a la Búsqueda que la
 * abrió); desde el Detalle el "Volver" existente regresa a la Lista.
 *
 * T11 — filas navegables: cada QR, actividad y comprobante del contexto es
 * una fila que reutiliza las rutas existentes (detalle de QR en Inicio;
 * detalle/visor en Operaciones). Criterio de acción primaria desde S06
 * (spec: "La acción primaria depende del flujo que llevó al usuario
 * allí"): continuar el trabajo dentro del contexto — ABRIR el elemento.
 * "Ver más" en actividad lleva a la superficie completa de Operaciones
 * (la única ruta existente de lista de actividades; sin crear
 * navegación nueva). La creación (Registrar) no se fuerza desde S06.
 */
@Composable
fun ContextsScreen(
    state: ContextsUiState,
    onAction: (ContextAction) -> Unit,
    onBack: () -> Unit,
    onOpenDestination: (String) -> Unit = {},
    onOpenOperation: (String) -> Unit = {},
    onOpenComprobante: (String) -> Unit = {},
    onOpenOperations: () -> Unit = {},
) {
    when (state.route) {
        ContextRoute.List -> ContextList(state, onAction, onBack)
        is ContextRoute.Detail -> ContextDetail(
            state = state,
            onAction = onAction,
            onOpenDestination = onOpenDestination,
            onOpenOperation = onOpenOperation,
            onOpenComprobante = onOpenComprobante,
            onOpenOperations = onOpenOperations,
        )
    }
}

@Composable
private fun ContextList(state: ContextsUiState, onAction: (ContextAction) -> Unit, onBack: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(XauxaSpacing.Xxl).imePadding(),
        verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Lg),
    ) {
        XauxaSecondaryButton(label = "Volver", onClick = onBack)
        XauxaHeading(text = "Contextos", size = XauxaType.Display, fontWeight = FontWeight.Bold)
        state.error?.let { err ->
            XauxaStatusBanner(
                err.display(),
                tone = XauxaTone.Danger,
                actionLabel = err.action.label,
                onAction = { onAction(ContextAction.ClearError) },
                onDismiss = { onAction(ContextAction.ClearError) },
            )
        }
        when {
            state.isLoading -> XauxaLoading(message = "Cargando contextos…")
            state.contexts.isEmpty() -> XauxaEmptyState(
                title = "Sin contextos",
                subtitle = "Los contextos agrupan tus QR y operaciones.",
                actionLabel = "Volver",
                onAction = onBack,
            )
            else -> LazyColumn(modifier = Modifier.fillMaxSize().weight(1f), verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
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

private const val RECENT_ACTIVITIES_SHOWN = 5

@Composable
private fun ContextDetail(
    state: ContextsUiState,
    onAction: (ContextAction) -> Unit,
    onOpenDestination: (String) -> Unit,
    onOpenOperation: (String) -> Unit,
    onOpenComprobante: (String) -> Unit,
    onOpenOperations: () -> Unit,
) {
    val contents = state.contents
    Column(
        modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(XauxaSpacing.Xxl).imePadding(),
        verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Lg),
    ) {
        XauxaSecondaryButton(label = "Volver", onClick = { onAction(ContextAction.Back) })
        state.error?.let { err ->
            XauxaStatusBanner(
                err.display(),
                tone = XauxaTone.Danger,
                actionLabel = err.action.label,
                onAction = { onAction(ContextAction.ClearError) },
                onDismiss = { onAction(ContextAction.ClearError) },
            )
        }
        contents?.let { data ->
            XauxaHeading(text = data.context.name, size = XauxaType.Display, fontWeight = FontWeight.Bold)
            data.context.note?.takeIf { it.isNotBlank() }?.let {
                Text(it, fontSize = XauxaType.Label, color = XauxaColor.TextSecondary)
            }
            Text("QR · " + data.destinations.size, fontSize = XauxaType.Title, color = XauxaColor.TextPrimary)
            data.destinations.forEach { destination ->
                XauxaListRow(
                    title = destination.name.ifBlank { "QR sin nombre" },
                    onClick = { onOpenDestination(destination.id) },
                )
            }
            Text("Actividad reciente · " + data.operations.size, fontSize = XauxaType.Title, color = XauxaColor.TextPrimary)
            data.operations.take(RECENT_ACTIVITIES_SHOWN).forEach { operation ->
                XauxaListRow(
                    title = listOfNotNull(
                        when (operation.type) {
                            com.agendaqr.destinations.domain.OperationType.PAGO -> "Pago"
                            com.agendaqr.destinations.domain.OperationType.COBRO -> "Cobro"
                        },
                        operation.amount,
                        operation.currency,
                        operation.personOrEntity,
                    ).joinToString(" · "),
                    onClick = { onOpenOperation(operation.id) },
                )
            }
            if (data.operations.size > RECENT_ACTIVITIES_SHOWN) {
                XauxaTextAction(
                    label = "Ver más",
                    onClick = onOpenOperations,
                )
            }
            Text("Comprobantes · " + data.comprobantes.size, fontSize = XauxaType.Title, color = XauxaColor.TextPrimary)
            data.comprobantes.forEach { receipt ->
                XauxaListRow(
                    title = "Comprobante · " + receiptProvenanceLabel(receipt.provenance),
                    subtitle = receipt.file.takeIf { it.isNotBlank() },
                    onClick = { onOpenComprobante(receipt.id) },
                )
            }
        } ?: XauxaLoading(message = "Cargando contexto…")
    }
}
