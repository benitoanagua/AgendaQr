package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.AmSnackbarHost
import com.agendaqr.core.ui.components.AmNavChromeState
import com.agendaqr.core.ui.components.AmEmptyState
import com.agendaqr.core.ui.components.AmIcons
import com.agendaqr.core.ui.components.AmPivot
import com.agendaqr.core.ui.components.AmListBody
import com.agendaqr.core.ui.components.AmListRow
import com.agendaqr.core.ui.components.AmLoading
import com.agendaqr.core.ui.components.AmScreenScaffold
import com.agendaqr.core.ui.components.AmTopBar
import com.agendaqr.core.ui.components.AmSecondaryButton
import com.agendaqr.core.ui.components.AmStatusBanner
import com.agendaqr.core.ui.components.AmText
import com.agendaqr.core.ui.components.AmTextAction
import com.agendaqr.core.ui.components.AmTone
import com.agendaqr.core.ui.components.AmContextBadge
import com.agendaqr.core.ui.theme.AmColor
import com.agendaqr.core.ui.theme.accentFor
import com.agendaqr.core.ui.theme.AmSpacing
import com.agendaqr.core.ui.theme.AmType
import androidx.compose.foundation.layout.Column

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
    navChrome: AmNavChromeState? = null,
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
    navChrome: AmNavChromeState?,
    appHosts: AppHosts? = null,
    onCreateContext: () -> Unit = {},
) {
    // V1.4 (§12): Contextos es destino raíz — barra superior con título y
    // navegación inferior; sin flecha atrás (la navegación raíz y el Back
    // del sistema cubren el regreso a Inicio).
    AmScreenScaffold(
        topBar = {
            AmTopBar(title = AppStrings.Contextos)
        },
        navDestinations = navChrome?.destinations.orEmpty(),
        navSelectedId = navChrome?.selectedId,
        onNavSelect = { navChrome?.onSelect?.invoke(it) },
        snackbarHost = appHosts?.feedback?.let { host -> { AmSnackbarHost(host) } },
        statusContent = appHosts?.status,
    ) {
        state.error?.let { err ->
            val (bannerAction, bannerDismiss) = err.bannerActions(
                onRetry = { onAction(ContextAction.RetryFailed) },
                onClear = { onAction(ContextAction.ClearError) },
            )
            AmStatusBanner(
                message = err.display(),
                tone = AmTone.Danger,
                action = bannerAction,
                dismiss = bannerDismiss,
            )
        }
        when {
            state.isLoading -> AmLoading(message = AppStrings.CargandoContextos)
            state.contexts.isEmpty() -> AmEmptyState(
                icon = AmIcons.Context,
                title = AppStrings.SinContextos,
                subtitle = AppStrings.LosContextosAgrupanTusQr,
                // U3/ADR-0003 (opción B/C): la acción primaria del vacío es
                // crear; "Volver" sigue disponible en la barra superior.
                actionLabel = AppStrings.CrearContexto,
                onAction = onCreateContext,
            )
            else -> {
                // Lista agrupada (referencia Ant Design Mobile):
                // AmListBody con AmListRow y hairlines — superficie de tarjeta
                // que agrupa las filas y se separa del fondo de página.
                AmListBody(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                ) {
                    state.contexts.forEachIndexed { index, context ->
                        // Identidad visual del contexto: bloque de acento derivado
                        // del id + nombre + descripción opcional.
                        AmListRow(
                            title = context.name,
                            subtitle = context.note?.takeIf { it.isNotBlank() },
                            onClick = { onAction(ContextAction.Open(context.id)) },
                            leading = {
                                // Bloque de identidad del contexto (M4): círculo
                                // de 40 dp con el color del contexto.
                                AmContextBadge(context.id)
                            },
                            showDivider = index < state.contexts.lastIndex,
                        )
                    }
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
    AmScreenScaffold(
        scrollable = true,
        topBar = {
            AmTopBar(
                title = contents?.context?.name ?: AppStrings.Contextos,
                onBack = { onAction(ContextAction.Back) },
                backLabel = AppStrings.Volver,
            )
        },
        snackbarHost = appHosts?.feedback?.let { host -> { AmSnackbarHost(host) } },
        statusContent = appHosts?.status,
    ) {
        state.error?.let { err ->
            val (bannerAction, bannerDismiss) = err.bannerActions(
                onRetry = { onAction(ContextAction.RetryFailed) },
                onClear = { onAction(ContextAction.ClearError) },
            )
            AmStatusBanner(
                message = err.display(),
                tone = AmTone.Danger,
                action = bannerAction,
                dismiss = bannerDismiss,
            )
        }
        contents?.let { data ->
            // El acento del contexto (M4, derivado del id) tiñe los
            // encabezados de SU superficie cuando es legible como texto.
            val contextAccent = accentFor(data.context.id).background
            data.context.note?.takeIf { it.isNotBlank() }?.let {
                AmText(it, size = AmType.Label, color = AmColor.TextSecondary)
            }
            // Pestañas estándar QR · Actividades · Comprobantes (modelo
            // mental §2; sin crear navegación nueva — la conmutación es local).
            var pivotSection by remember { mutableStateOf(0) }
            AmPivot(
                sections = listOf(AppStrings.Qr, AppStrings.Operaciones, AppStrings.Comprobantes),
                selectedIndex = pivotSection,
                onSelect = { pivotSection = it },
                accent = contextAccent,
            ) { section ->
                Column(verticalArrangement = Arrangement.spacedBy(AmSpacing.Sm)) {
                    when (section) {
                        0 -> {
                            // U1: QR es invariable en español; conteo vía helper.
                            AmText(contextSectionLabel("QR", "QR", data.destinations.size), size = AmType.Label, color = AmColor.TextSecondary)
                            AmListBody {
                                data.destinations.forEachIndexed { index, destination ->
                                    AmListRow(
                                        title = destination.name.ifBlank { AppStrings.QrSinNombre },
                                        onClick = { onOpenDestination(destination.id) },
                                        showDivider = index < data.destinations.lastIndex,
                                    )
                                }
                            }
                        }
                        1 -> {
                            // U1: singular honesto para N=1 ("Actividad reciente · 1").
                            AmText(contextSectionLabel(AppStrings.ActividadReciente, AppStrings.ActividadesRecientes, data.operations.size), size = AmType.Label, color = AmColor.TextSecondary)
                            val shownOperations = data.operations.take(RECENT_ACTIVITIES_SHOWN)
                            AmListBody {
                                shownOperations.forEachIndexed { index, operation ->
                                    AmListRow(
                                        title = listOfNotNull(
                                            when (operation.type) {
                                                com.agendaqr.destinations.domain.OperationType.PAGO -> AppStrings.Pago
                                                com.agendaqr.destinations.domain.OperationType.COBRO -> AppStrings.Cobro
                                            },
                                            operation.amount,
                                            operation.currency,
                                            operation.personOrEntity,
                                        ).joinToString(" · "),
                                        onClick = { onOpenOperation(operation.id) },
                                        showDivider = index < shownOperations.lastIndex,
                                    )
                                }
                            }
                            if (data.operations.size > RECENT_ACTIVITIES_SHOWN) {
                                AmTextAction(
                                    label = AppStrings.VerMas,
                                    onClick = onOpenOperations,
                                )
                            }
                        }
                        else -> {
                            AmText(contextSectionLabel(AppStrings.Comprobante, AppStrings.Comprobantes, data.comprobantes.size), size = AmType.Label, color = AmColor.TextSecondary)
                            AmListBody {
                                data.comprobantes.forEachIndexed { index, receipt ->
                                    AmListRow(
                                        title = AppStrings.Comprobante2 + receiptProvenanceLabel(receipt.provenance),
                                        subtitle = receipt.file.takeIf { it.isNotBlank() },
                                        onClick = { onOpenComprobante(receipt.id) },
                                        showDivider = index < data.comprobantes.lastIndex,
                                    )
                                }
                            }
                        }
                    }
                }
            }
        } ?: AmLoading(message = AppStrings.CargandoContexto)
    }
}
