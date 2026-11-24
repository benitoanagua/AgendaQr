package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.AqAlert
import com.agendaqr.core.ui.components.AqAppBarAction
import com.agendaqr.core.ui.components.AqButton
import com.agendaqr.core.ui.components.AqButtonVariant
import com.agendaqr.core.ui.components.AqEmptyPlaceholder
import com.agendaqr.core.ui.components.AqMenuItem
import com.agendaqr.core.ui.components.AqNavChromeState
import com.agendaqr.core.ui.components.AqNavDestination
import com.agendaqr.core.ui.components.AqScreen
import com.agendaqr.core.ui.components.AqSectionTitle
import com.agendaqr.core.ui.components.AqSkeletonBlock
import com.agendaqr.core.ui.components.AqTag
import com.agendaqr.core.ui.components.AqText
import com.agendaqr.core.ui.components.AqToastHost
import com.agendaqr.core.ui.components.AqTone
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.core.ui.theme.accentFor
import com.agendaqr.destinations.domain.OperationType
import com.agendaqr.core.ui.components.AqTab

/**
 * S06 — Contexto. `onBack` cierra la superficie según el punto de entrada.
 *
 * Construido exclusivamente con componentes Moon: [AqScreen] (scaffold con
 * navegación raíz dedicada), [AqMenuItem] (filas), [AqEmptyPlaceholder] (vacío),
 * [AqToastHost] (toasts), [AqAlert] (banners), [AqTag] (etiquetas).
 * Sin símbolos legacy ni wrappers `Aq*`.
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

private const val RECENT_ACTIVITIES_SHOWN = 5

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
    // navegación inferior; sin flecha atrás.
    val toastHost = appHosts?.toastHost
    val navDestinations = navChrome?.destinations.orEmpty()
    val navSelectedId = navChrome?.selectedId
    AqScreen(
        title = AppStrings.Contextos,
        onBack = null,
        navDestinations = navDestinations,
        navSelectedId = navSelectedId,
        onNavSelect = { name -> navChrome?.onSelect?.invoke(name); Unit },
        statusContent = appHosts?.status,
        toastHost = toastHost?.let { host -> { AqToastHost(host) } },
    ) {
        state.error?.let { err ->
            val (bannerAction, bannerDismiss) = err.bannerActions(
                onRetry = { onAction(ContextAction.RetryFailed) },
                onClear = { onAction(ContextAction.ClearError) },
            )
            AqAlert(
                tone = AqTone.Danger,
                message = err.dataStatus,
                dismissLabel = bannerDismiss?.label ?: AppStrings.Cerrar,
                onDismiss = bannerDismiss?.onClick ?: { },
            )
        }
        when {
            state.isLoading -> AqSkeletonBlock(lines = 3)
            state.contexts.isEmpty() -> AqEmptyPlaceholder(
                icon = com.agendaqr.core.ui.components.AqIcons.Context,
                title = AppStrings.SinContextos,
                subtitle = AppStrings.LosContextosAgrupanTusQr,
                actionLabel = AppStrings.CrearContexto,
                onAction = onCreateContext,
            )
            else -> {
                Column(modifier = Modifier.fillMaxWidth()) {
                    state.contexts.forEachIndexed { index, context ->
                        AqMenuItem(
                            title = context.name,
                            subtitle = context.note?.takeIf { it.isNotBlank() },
                            onClick = { onAction(ContextAction.Open(context.id)) },
                            leading = {
                                AqTag(text = context.id)
                            },
                            showDivider = index < state.contexts.lastIndex,
                        )
                        if (index < state.contexts.lastIndex) {
                            com.agendaqr.core.ui.components.AqHairline()
                        }
                    }
                }
            }
        }
    }
}

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
    val toastHost = appHosts?.toastHost
    AqScreen(
        title = contents?.context?.name ?: AppStrings.Contextos,
        onBack = { onAction(ContextAction.Back) },
        backLabel = AppStrings.Volver,
        statusContent = appHosts?.status,
        toastHost = toastHost?.let { host -> { AqToastHost(host) } },
    ) {
        state.error?.let { err ->
            val (bannerAction, bannerDismiss) = err.bannerActions(
                onRetry = { onAction(ContextAction.RetryFailed) },
                onClear = { onAction(ContextAction.ClearError) },
            )
            AqAlert(
                tone = AqTone.Danger,
                message = err.dataStatus,
                dismissLabel = bannerDismiss?.label ?: AppStrings.Cerrar,
                onDismiss = bannerDismiss?.onClick ?: { },
            )
        }
        contents?.let { data ->
            val contextAccent = accentFor(data.context.id).background
            data.context.note?.takeIf { it.isNotBlank() }?.let {
                AqText(it, size = AqType.Label, color = AqColor.TextSecondary)
            }
            var pivotSection by remember { mutableStateOf(0) }
            AqPillTab(
                tabs = listOf(
                    com.agendaqr.core.ui.components.AqTab(AppStrings.Qr, count = data.destinations.size),
                    com.agendaqr.core.ui.components.AqTab(AppStrings.Operaciones, count = data.operations.size),
                    com.agendaqr.core.ui.components.AqTab(AppStrings.Comprobantes, count = data.comprobantes.size),
                ),
                selectedIndex = pivotSection,
                onSelect = { section: Int -> pivotSection = section },
            )
            Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
                when (pivotSection) {
                    0 -> {
                        if (data.destinations.isEmpty()) {
                            AqEmptyPlaceholder(
                                icon = com.agendaqr.core.ui.components.AqIcons.QrCode,
                                title = AppStrings.AunNoHayDestinos,
                            )
                        } else {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                data.destinations.forEachIndexed { index, destination ->
                                    AqMenuItem(
                                        title = destination.name.ifBlank { AppStrings.QrSinNombre },
                                        onClick = { onOpenDestination(destination.id) },
                                        showDivider = index < data.destinations.lastIndex,
                                    )
                                }
                            }
                        }
                    }
                    1 -> {
                        if (data.operations.isEmpty()) {
                            AqEmptyPlaceholder(
                                icon = com.agendaqr.core.ui.components.AqIcons.Register,
                                title = AppStrings.AunNoHayOperaciones,
                            )
                        } else {
                            val shownOperations = data.operations.take(RECENT_ACTIVITIES_SHOWN)
                            Column(modifier = Modifier.fillMaxWidth()) {
                                shownOperations.forEachIndexed { index, operation ->
                                    AqMenuItem(
                                        title = listOfNotNull(
                                            when (operation.type) {
                                                OperationType.PAGO -> AppStrings.Pago
                                                OperationType.COBRO -> AppStrings.Cobro
                                            },
                                            operation.amount,
                                            operation.currency,
                                            operation.personOrEntity,
                                        ).joinToString(" · "),
                                        trailing = {
                                            AqText(
                                                text = formatDate(operation.occurredAt),
                                                size = AqType.Label,
                                                color = AqColor.TextSecondary,
                                            )
                                        },
                                        onClick = { onOpenOperation(operation.id) },
                                        showDivider = index < shownOperations.lastIndex,
                                    )
                                }
                            }
                            if (data.operations.size > RECENT_ACTIVITIES_SHOWN) {
                                AqButton(
                                    label = AppStrings.VerMas,
                                    onClick = onOpenOperations,
                                    variant = com.agendaqr.core.ui.components.AqButtonVariant.Text,
                                )
                            }
                        }
                    }
                    else -> {
                        if (data.comprobantes.isEmpty()) {
                            AqEmptyPlaceholder(
                                icon = com.agendaqr.core.ui.components.AqIcons.Attachment,
                                title = AppStrings.AunNoHayComprobantes,
                            )
                        } else {
                            Column(modifier = Modifier.fillMaxWidth()) {
                                data.comprobantes.forEachIndexed { index, receipt ->
                                    AqMenuItem(
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
        } ?: AqSkeletonBlock(lines = 4)
    }
}

/** Fila de metadato (etiqueta + valor) construida con piezas Moon. */
@Composable
private fun AqMetaLine(label: String, value: String, leadingIcon: (() -> Unit)? = null) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
    ) {
        leadingIcon?.invoke()
        AqText(
            text = label,
            size = AqType.Body,
            color = com.agendaqr.core.ui.theme.AqColor.TextSecondary,
            modifier = Modifier,
        )
        AqText(
            text = value,
            size = AqType.Body,
            color = com.agendaqr.core.ui.theme.AqColor.TextPrimary,
            modifier = Modifier.weight(1f, fill = false),
        )
    }
}
