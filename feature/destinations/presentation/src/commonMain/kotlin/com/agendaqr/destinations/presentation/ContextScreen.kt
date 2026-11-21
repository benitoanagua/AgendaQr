package com.agendaqr.destinations.presentation

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.AqSnackbarHost
import com.agendaqr.core.ui.components.AqNavChromeState
import com.agendaqr.core.ui.components.AqEmptyState
import com.agendaqr.core.ui.components.AqIcons
import com.agendaqr.core.ui.components.AqTab
import com.agendaqr.core.ui.components.AqTabs
import com.agendaqr.core.ui.components.AqListBody
import com.agendaqr.core.ui.components.AqListRow
import com.agendaqr.core.ui.components.AqLoading
import com.agendaqr.core.ui.components.AqScreenScaffold
import com.agendaqr.core.ui.components.AqTopBar
import com.agendaqr.core.ui.components.AqSecondaryButton
import com.agendaqr.core.ui.components.AqSkeleton
import com.agendaqr.core.ui.components.AqStatusBanner
import com.agendaqr.core.ui.components.AqText
import com.agendaqr.core.ui.components.AqTextAction
import com.agendaqr.core.ui.components.AqTone
import com.agendaqr.core.ui.components.AqContextBadge
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.accentFor
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.destinations.domain.ContextContents
import androidx.compose.foundation.layout.*

/**
 * S06 — Contexto. `onBack` cierra la superficie según el punto de entrada:
 * desde la Lista termina el flujo (vuelve a Inicio o a la Búsqueda que la
 * abrió); desde el Detalle el "Volver" existente regresa a la Lista.
 *
 * filas navegables: cada QR, actividad y comprobante del contexto es
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
    navChrome: AqNavChromeState? = null,
    appHosts: AppHosts? = null,
    onOpenDestination: (String) -> Unit = {},
    onOpenOperation: (String) -> Unit = {},
    onOpenComprobante: (String) -> Unit = {},
    onOpenOperations: () -> Unit = {},
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    when (state.route) {
        ContextRoute.List -> ContextList(
            state = state,
            onAction = onAction,
            onBack = onBack,
            navChrome = navChrome,
            appHosts = appHosts,
            onCreateContext = { showCreateDialog = true },
        )
        is ContextRoute.Detail -> ContextDetail(
            state = state,
            appHosts = appHosts,
            onAction = onAction,
            onOpenDestination = onOpenDestination,
            onOpenOperation = onOpenOperation,
            onOpenComprobante = onOpenComprobante,
            onOpenOperations = onOpenOperations,
        )
    }
    // U3/ADR-0003 (opción C/B): S06 vacío ofrece Crear contexto como
    // acción primaria (con ≥1 contexto no aparece: sin jerarquía nueva).
    if (showCreateDialog) {
        ContextCreationDialog(
            onConfirmCreate = { name, note ->
                onAction(ContextAction.Create(name, note))
                showCreateDialog = false
            },
            onDismiss = { showCreateDialog = false },
        )
    }
}

@Composable
private fun ContextList(
    state: ContextsUiState,
    onAction: (ContextAction) -> Unit,
    onBack: () -> Unit,
    navChrome: AqNavChromeState?,
    appHosts: AppHosts? = null,
    onCreateContext: () -> Unit = {},
) {
    // V1.4 (§12): Contextos es destino raíz — barra superior con título y
    // navegación inferior; sin flecha atrás (la navegación raíz y el Back
    // del sistema cubren el regreso a Inicio).
    AqScreenScaffold(
        topBar = {
            AqTopBar(title = AppStrings.Contextos)
        },
        navDestinations = navChrome?.destinations.orEmpty(),
        navSelectedId = navChrome?.selectedId,
        onNavSelect = { navChrome?.onSelect?.invoke(it) },
        snackbarHost = appHosts?.feedback?.let { host -> { AqSnackbarHost(host) } },
        statusContent = appHosts?.status,
    ) {
        ContextErrorBanner(state, onAction)
        ContextListContent(state, onAction, onCreateContext)
    }
}

/** §10: error recuperable, visible sin scroll. */
@Composable
private fun ContextErrorBanner(
    state: ContextsUiState,
    onAction: (ContextAction) -> Unit,
) {
    state.error?.let { err ->
        val (bannerAction, bannerDismiss) = err.bannerActions(
            onRetry = { onAction(ContextAction.RetryFailed) },
            onClear = { onAction(ContextAction.ClearError) },
        )
        AqStatusBanner(
            title = err.what,
            message = err.dataStatus,
            tone = AqTone.Danger,
            action = bannerAction,
            dismiss = bannerDismiss,
        )
    }
}

/** Lista de contextos: carga, vacío o filas agrupadas. */
@Composable
private fun ContextListContent(
    state: ContextsUiState,
    onAction: (ContextAction) -> Unit,
    onCreateContext: () -> Unit,
) {
    when {
        state.isLoading -> AqSkeleton(lines = 3)
        state.contexts.isEmpty() -> AqEmptyState(
            icon = AqIcons.Context,
            title = AppStrings.SinContextos,
            subtitle = AppStrings.LosContextosAgrupanTusQr,
            // U3/ADR-0003 (opción B/C): la acción primaria del vacío es
            // crear; "Volver" sigue disponible en la barra superior.
            actionLabel = AppStrings.CrearContexto,
            onAction = onCreateContext,
        )
        else -> {
            // Lista agrupada (patrón de lista de chats y contactos):
            // AqListBody con AqListRow y hairlines — superficie de tarjeta
            // que agrupa las filas y se separa del fondo de página.
            AqListBody {
                state.contexts.forEachIndexed { index, context ->
                    // Identidad visual del contexto: bloque de acento derivado
                    // del id + nombre + descripción opcional.
                    AqListRow(
                        title = context.name,
                        subtitle = context.note?.takeIf { it.isNotBlank() },
                        onClick = { onAction(ContextAction.Open(context.id)) },
                        leading = {
                            // Bloque de identidad del contexto (M4): círculo
                            // de 40 dp con el color del contexto.
                            AqContextBadge(context.id)
                        },
                        showDivider = index < state.contexts.lastIndex,
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
    appHosts: AppHosts? = null,
    onAction: (ContextAction) -> Unit,
    onOpenDestination: (String) -> Unit,
    onOpenOperation: (String) -> Unit,
    onOpenComprobante: (String) -> Unit,
    onOpenOperations: () -> Unit,
) {
    val contents = state.contents
    // V1.4 (§12): detalle de contexto con título en la barra superior y
    // flecha atrás; las secciones siguen siendo QR · Actividades ·
    // Comprobantes (pestañas estándar).
    AqScreenScaffold(
        scrollable = true,
        topBar = {
            AqTopBar(
                title = contents?.context?.name ?: AppStrings.Contextos,
                onBack = { onAction(ContextAction.Back) },
                backLabel = AppStrings.Volver,
            )
        },
        snackbarHost = appHosts?.feedback?.let { host -> { AqSnackbarHost(host) } },
        statusContent = appHosts?.status,
    ) {
        ContextDetailErrorBanner(state, onAction)
        ContextDetailContent(
            state = state,
            onOpenDestination = onOpenDestination,
            onOpenOperation = onOpenOperation,
            onOpenComprobante = onOpenComprobante,
            onOpenOperations = onOpenOperations,
        )
    }
}

/** §10: error recuperable del detalle, visible sin scroll. */
@Composable
private fun ContextDetailErrorBanner(
    state: ContextsUiState,
    onAction: (ContextAction) -> Unit,
) {
    state.error?.let { err ->
        val (bannerAction, bannerDismiss) = err.bannerActions(
            onRetry = { onAction(ContextAction.RetryFailed) },
            onClear = { onAction(ContextAction.ClearError) },
        )
        AqStatusBanner(
            title = err.what,
            message = err.dataStatus,
            tone = AqTone.Danger,
            action = bannerAction,
            dismiss = bannerDismiss,
        )
    }
}

/** Contenido del detalle de contexto: nota, pestañas QR · Actividades · Comprobantes. */
@Composable
private fun ContextDetailContent(
    state: ContextsUiState,
    onOpenDestination: (String) -> Unit,
    onOpenOperation: (String) -> Unit,
    onOpenComprobante: (String) -> Unit,
    onOpenOperations: () -> Unit,
) {
    val contents = state.contents
    contents?.let { data ->
        // El acento del contexto (M4, derivado del id) tiñe los
        // encabezados de SU superficie cuando es legible como texto.
        val contextAccent = accentFor(data.context.id).background
        data.context.note?.takeIf { it.isNotBlank() }?.let {
            AqText(it, size = AqType.Label, color = AqColor.TextSecondary)
        }
        // Pestañas estándar QR · Actividades · Comprobantes (modelo
        // mental §2; sin crear navegación nueva — la conmutación es local).
        var pivotSection by remember { mutableStateOf(0) }
        // El conteo vive en el badge de la pestaña (AqTab.count);
        // las secciones vacías usan el empty state estándar.
        AqTabs(
            tabs = listOf(
                AqTab(AppStrings.Qr, count = data.destinations.size),
                AqTab(AppStrings.Operaciones, count = data.operations.size),
                AqTab(AppStrings.Comprobantes, count = data.comprobantes.size),
            ),
            selectedIndex = pivotSection,
            onSelect = { pivotSection = it },
            accent = contextAccent,
        ) { section ->
            Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
                when (section) {
                    0 -> ContextDestinationsSection(data, onOpenDestination)
                    1 -> ContextOperationsSection(data, onOpenOperation, onOpenOperations)
                    else -> ContextComprobantesSection(data, onOpenComprobante)
                }
            }
        }
    } ?: AqSkeleton(lines = 4)
}

/** Sección de destinos (QR) del detalle de contexto. */
@Composable
private fun ContextDestinationsSection(
    data: ContextContents,
    onOpenDestination: (String) -> Unit,
) {
    if (data.destinations.isEmpty()) {
        AqEmptyState(
            icon = AqIcons.QrCode,
            title = AppStrings.AunNoHayDestinos,
        )
    } else {
        AqListBody {
            data.destinations.forEachIndexed { index, destination ->
                AqListRow(
                    title = destination.name.ifBlank { AppStrings.QrSinNombre },
                    onClick = { onOpenDestination(destination.id) },
                    showDivider = index < data.destinations.lastIndex,
                )
            }
        }
    }
}

/** Sección de operaciones recientes del detalle de contexto. */
@Composable
private fun ContextOperationsSection(
    data: ContextContents,
    onOpenOperation: (String) -> Unit,
    onOpenOperations: () -> Unit,
) {
    if (data.operations.isEmpty()) {
        AqEmptyState(
            icon = AqIcons.Register,
            title = AppStrings.AunNoHayOperaciones,
        )
    } else {
        val shownOperations = data.operations.take(RECENT_ACTIVITIES_SHOWN)
        AqListBody {
            shownOperations.forEachIndexed { index, operation ->
                AqListRow(
                    title = listOfNotNull(
                        when (operation.type) {
                            com.agendaqr.destinations.domain.OperationType.PAGO -> AppStrings.Pago
                            com.agendaqr.destinations.domain.OperationType.COBRO -> AppStrings.Cobro
                        },
                        operation.amount,
                        operation.currency,
                        operation.personOrEntity,
                    ).joinToString(" · "),
                    // Fecha real de la actividad: metadato
                    // corto en la línea del título.
                    endMeta = formatDate(operation.occurredAt),
                    onClick = { onOpenOperation(operation.id) },
                    showDivider = index < shownOperations.lastIndex,
                )
            }
        }
        if (data.operations.size > RECENT_ACTIVITIES_SHOWN) {
            AqTextAction(
                label = AppStrings.VerMas,
                onClick = onOpenOperations,
            )
        }
    }
}

/** Sección de comprobantes del detalle de contexto. */
@Composable
private fun ContextComprobantesSection(
    data: ContextContents,
    onOpenComprobante: (String) -> Unit,
) {
    if (data.comprobantes.isEmpty()) {
        AqEmptyState(
            icon = AqIcons.Attachment,
            title = AppStrings.AunNoHayComprobantes,
        )
    } else {
        AqListBody {
            data.comprobantes.forEachIndexed { index, receipt ->
                AqListRow(
                    title = AppStrings.Comprobante2 + receiptProvenanceLabel(receipt.provenance),
                    subtitle = receipt.file.takeIf { it.isNotBlank() },
                    onClick = { onOpenComprobante(receipt.id) },
                    showDivider = index < data.comprobantes.lastIndex,
                )
            }
        }
    }
}
