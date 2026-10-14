package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.agendaqr.destinations.data.SyncResource
import com.agendaqr.core.ui.components.AmSnackbarHost
import com.agendaqr.core.ui.components.AmFavoriteToggle
import com.agendaqr.core.ui.components.AmFilterChip
import com.agendaqr.core.ui.components.AmIcon
import com.agendaqr.core.ui.components.AmIconBadge
import com.agendaqr.core.ui.components.AmIcons
import com.agendaqr.core.ui.components.AmExtendedFab
import com.agendaqr.core.ui.components.AmDialog
import com.agendaqr.core.ui.components.AmDialogList
import com.agendaqr.core.ui.components.AmListRow
import com.agendaqr.core.ui.components.AmNavChromeState
import com.agendaqr.core.ui.components.AmOverflowAction
import com.agendaqr.core.ui.components.AmQuickAction
import com.agendaqr.core.ui.components.AmScreenScaffold
import com.agendaqr.core.ui.components.AmSearchTrigger
import com.agendaqr.core.ui.components.AmSectionHeader
import com.agendaqr.core.ui.components.AmSkeleton
import com.agendaqr.core.ui.components.AmStatusPill
import com.agendaqr.core.ui.components.AmText
import com.agendaqr.core.ui.components.AmTextAction
import com.agendaqr.core.ui.components.AmTonalTag
import com.agendaqr.core.ui.components.AmTone
import com.agendaqr.core.ui.components.AmStatusBanner
import com.agendaqr.core.ui.components.AmEmptyState
import com.agendaqr.core.ui.components.AmLoadMoreFooter
import com.agendaqr.core.ui.components.AmListBody
import com.agendaqr.core.ui.components.AmTopBar
import com.agendaqr.core.ui.theme.AmColor
import com.agendaqr.core.ui.theme.AmSpacing
import com.agendaqr.destinations.domain.Destination

/**
 * S01 — Inicio (V1.4, ADR-0014; composición de la referencia Stitch
 * 2026-10, ADR-0015): píldora de reaseguro local-first, búsqueda
 * dominante con filtros en píldora, acciones rápidas reales, sección
 * Recientes con conteo y tarjeta de disponibilidad; el FAB abre la hoja
 * "¿Qué quieres añadir?" con las tres creaciones reales del producto.
 */
@Composable
fun DestinationsScreen(
    state: DestinationsUiState,
    onAction: (DestinationAction) -> Unit,
    navChrome: AmNavChromeState? = null,
    appHosts: AppHosts? = null,
    onOpenSearch: () -> Unit = {},
    onOpenContexts: () -> Unit = {},
    onSignOut: () -> Unit = {},
    syncLookup: ElementSyncLookup = ElementSyncLookup.Empty,
    /** Acción rápida / hoja del FAB: traer un QR (S02, cámara incluida). */
    onScanQr: () -> Unit = { onAction(DestinationAction.Edit(null)) },
    /** Acción rápida / hoja del FAB: registrar actividad (S07). */
    onRegisterOperation: () -> Unit = {},
    /** Hoja del FAB: crear contexto (persiste vía ContextAction.Create). */
    onCreateContext: (name: String, note: String?) -> Unit = { _, _ -> },
) {
    var visibleCount by remember(state.visibleDestinations.size) { mutableStateOf(50) }
    val paged = state.visibleDestinations.take(visibleCount)
    // Hoja del FAB ("¿Qué quieres añadir?") y creación de contexto en la
    // misma superficie (el draft del editor nunca sale de composición).
    var showAddSheet by rememberSaveable { mutableStateOf(false) }
    var showCreateContext by rememberSaveable { mutableStateOf(false) }

    AmScreenScaffold(
        scrollable = true,
        topBar = {
            AmTopBar(
                title = AppStrings.AgendaQr,
                overflowActions = listOf(
                    AmOverflowAction(AppStrings.CerrarSesion, onClick = onSignOut),
                ),
                overflowLabel = AppStrings.Mas,
            )
        },
        navDestinations = navChrome?.destinations.orEmpty(),
        snackbarHost = appHosts?.feedback?.let { host -> { AmSnackbarHost(host) } },
        statusContent = appHosts?.status,

        navSelectedId = navChrome?.selectedId,
        onNavSelect = { navChrome?.onSelect?.invoke(it) },
        floatingActionButton = {
            AmExtendedFab(
                label = AppStrings.Anadir,
                icon = AmIcons.Add,
                onClick = { showAddSheet = true },
            )
        },
        verticalArrangement = Arrangement.spacedBy(AmSpacing.Xl),
    ) {
        // Reaseguro local-first (referencia S01): SIEMPRE cierto en este
        // producto (la agenda vive en el dispositivo). El estado de
        // sincronización por elemento sigue en su fila.
        AmStatusPill(
            text = AppStrings.GuardadoEnTuTelefono,
            icon = AmIcons.CircleCheck,
            tone = AmTone.Success,
        )

        // §10: errores recuperables y contextuales, visibles sin scroll.
        state.error?.let { error ->
            AmStatusBanner(
                error.display(),
                tone = AmTone.Danger,
                actionLabel = error.action.label,
                // REINTENTAR re-ejecuta la operación fallida en el VM.
                onAction = {
                    onAction(
                        if (error.action == ErrorAction.Retry) DestinationAction.RetryFailed
                        else DestinationAction.ClearError,
                    )
                },
                onDismiss = { onAction(DestinationAction.ClearError) },
                dismissLabel = AppStrings.Descartar,
            )
        }

        // Búsqueda dominante + filtros reales de la lista (Todo ·
        // Favoritos) como píldoras sólidas de la referencia.
        Column(verticalArrangement = Arrangement.spacedBy(AmSpacing.Sm)) {
            AmSearchTrigger(
                label = AppStrings.BuscarEnAgendaQr,
                placeholder = AppStrings.searchPlaceholder,
                onClick = onOpenSearch,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(AmSpacing.Sm)) {
                AmFilterChip(
                    label = AppStrings.Todos,
                    selected = !state.favoriteOnly,
                    onClick = { onAction(DestinationAction.ToggleFavorites) },
                )
                AmFilterChip(
                    label = if (state.favoriteOnly) AppStrings.FavoritosActivado else AppStrings.Favoritos,
                    selected = state.favoriteOnly,
                    onClick = { onAction(DestinationAction.ToggleFavorites) },
                )
            }
        }

        // Acciones rápidas (referencia S01): entradas REALES — traer un
        // QR (S02 con cámara) y registrar una actividad (S07).
        Row(horizontalArrangement = Arrangement.spacedBy(AmSpacing.Sm)) {
            AmQuickAction(
                title = AppStrings.EscanearQr,
                caption = AppStrings.AbrirLaCamara,
                icon = AmIcons.ScanLine,
                tone = AmTone.Info,
                onClick = onScanQr,
                modifier = Modifier.weight(1f),
            )
            AmQuickAction(
                title = AppStrings.RegistrarPago,
                caption = AppStrings.CargarActividad,
                icon = AmIcons.Register,
                tone = AmTone.Neutral,
                onClick = onRegisterOperation,
                modifier = Modifier.weight(1f),
            )
        }

        // Sección Recientes: encabezado con conteo real y "Ver todo"
        // (expande la paginación local).
        Column(verticalArrangement = Arrangement.spacedBy(AmSpacing.Sm)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AmSpacing.Sm),
            ) {
                AmSectionHeader(text = AppStrings.Recientes)
                AmTonalTag(
                    text = state.visibleDestinations.size.toString(),
                    tone = AmTone.Neutral,
                )
                if (paged.size < state.visibleDestinations.size) {
                    AmTextAction(
                        label = AppStrings.VerTodo,
                        onClick = {
                            visibleCount = state.visibleDestinations.size
                        },
                    )
                }
            }
            when {
                // la carga inicial se anuncia con el skeleton del DS.
                state.isLoading -> AmSkeleton(lines = 4)
                state.visibleDestinations.isEmpty() -> AmEmptyState(
                    icon = AmIcons.QrCode,
                    title = if (state.destinations.isEmpty()) AppStrings.AunNoHayDestinos else AppStrings.NoSeEncontraronDestinos,
                    subtitle = if (state.destinations.isEmpty()) AppStrings.emptyDestinationsHint else AppStrings.PruebaConOtraBusquedaOLimpia,
                    actionLabel = if (state.destinations.isEmpty()) AppStrings.Anadir else AppStrings.LimpiarBusqueda,
                    onAction = { if (state.destinations.isEmpty()) onAction(DestinationAction.Edit(null)) else onAction(DestinationAction.Search("")) },
                )
                else -> {
                    // Tarjeta de lista de la referencia: superficie con
                    // hairlines entre celdas; la paginación vive fuera.
                    AmListBody {
                        paged.forEachIndexed { index, destination ->
                            DestinationRow(
                                destination,
                                onAction,
                                syncLookup,
                                showDivider = index < paged.lastIndex,
                            )
                        }
                    }
                    if (paged.size < state.visibleDestinations.size) {
                        AmLoadMoreFooter(
                            onLoadMore = { visibleCount = (visibleCount + 50).coerceAtMost(state.visibleDestinations.size) },
                            loadMoreLabel = AppStrings.CargarMas,
                            loadingLabel = AppStrings.Cargando,
                            endLabel = AppStrings.NoHayMasElementos,
                        )
                    }
                }
            }
        }

        // Reaseguro de cierre (referencia S01): disponible sin conexión.
        AmListBody {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = AmSpacing.Lg, vertical = AmSpacing.Md),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(AmSpacing.Md),
            ) {
                AmIcon(
                    imageVector = AmIcons.ShieldCheck,
                    contentDescription = null,
                    tint = AmColor.AccentText,
                )
                Column {
                    AmText(
                        AppStrings.DisponibilidadTotal,
                        size = com.agendaqr.core.ui.theme.AmType.RowTitle,
                        fontWeight = androidx.compose.ui.text.font.FontWeight.Medium,
                        color = AmColor.TextPrimary,
                    )
                    AmText(
                        AppStrings.TuInformacionEstaGuardadaEnElDispositivo,
                        color = AmColor.TextSecondary,
                    )
                }
            }
        }
    }

    if (showAddSheet) {
        AmDialog(
            title = AppStrings.QueQuieresAnadir,
            confirmLabel = AppStrings.Cancelar,
            confirmAsText = true,
            onConfirm = { showAddSheet = false },
            onDismiss = { showAddSheet = false },
            onDismissRequest = { showAddSheet = false },
            content = {
                AmDialogList {
                    AmListRow(
                        title = AppStrings.Anadir,
                        subtitle = AppStrings.TraeAAgendaQrAlgo,
                        onClick = { showAddSheet = false; onScanQr() },
                        leading = { AmIconBadge(icon = AmIcons.QrCode, tone = AmTone.Info) },
                        trailing = { SheetChevron() },
                    )
                    AmListRow(
                        title = AppStrings.CrearContexto,
                        subtitle = AppStrings.LosContextosAgrupanTusQr,
                        onClick = { showAddSheet = false; showCreateContext = true },
                        leading = { AmIconBadge(icon = AmIcons.Context, tone = AmTone.Neutral) },
                        trailing = { SheetChevron() },
                    )
                    AmListRow(
                        title = AppStrings.RegistrarOperacion,
                        subtitle = AppStrings.GuardaUnComprobanteOMovimiento,
                        onClick = { showAddSheet = false; onRegisterOperation() },
                        leading = { AmIconBadge(icon = AmIcons.Register, tone = AmTone.Success) },
                        trailing = { SheetChevron() },
                    )
                }
            },
        )
    }
    if (showCreateContext) {
        ContextCreationDialog(
            onConfirmCreate = { name, note ->
                onCreateContext(name, note)
                showCreateContext = false
            },
            onDismiss = { showCreateContext = false },
        )
    }
}

@Composable
private fun SheetChevron() {
    AmIcon(
        imageVector = AmIcons.ChevronRight,
        contentDescription = null,
        tint = AmColor.TextTertiary,
    )
}

@Composable
fun DestinationRow(
    destination: Destination,
    onAction: (DestinationAction) -> Unit,
    syncLookup: ElementSyncLookup = ElementSyncLookup.Empty,
    showDivider: Boolean = false,
) {
    // §12 V1.4 + Stitch: fila con tile tipado (QR sobre tinte de acento),
    // título 15/500, metadato caption y favorito como acción de fila.
    AmListRow(
        title = destination.name.ifBlank { AppStrings.SinNombre },
        subtitle = destinationSubtitle(destination),
        showDivider = showDivider,
        onClick = { onAction(DestinationAction.Open(destination.id)) },
        leading = {
            AmIconBadge(icon = AmIcons.QrCode, tone = AmTone.Info)
        },
        // Badge bajo el texto (línea propia vía `meta`): no compite por el
        // ancho con la acción en pantallas estrechas.
        meta = {
            // Estado de sincronización por elemento (con texto).
            ElementSyncBadge(
                syncLookup.status(SyncResource.DESTINATION, destination.id),
            )
        },
        trailing = {
            AmFavoriteToggle(
                favorite = destination.favorite,
                contentDescription = if (destination.favorite) {
                    AppStrings.QuitarFavorito
                } else {
                    AppStrings.MarcarFavorito
                },
                onCheckedChange = { onAction(DestinationAction.ToggleFavorite(destination)) },
            )
        },
    )
}

/**
 * Subtítulo de la fila del destino: nota/categoría si existen; si el
 * nombre es genérico ("Sin nombre" o "QR importado"), ORIGEN + FECHA —
 * dos destinos con nombre genérico quedaban indistinguibles en la lista
 * (defecto abierto registrado; decisión de esta pasada, no de spec).
 */
internal fun destinationSubtitle(destination: Destination): String? {
    val generic = destination.name.isBlank() ||
        destination.name.startsWith(AppStrings.QrImportado)
    return when {
        generic && destination.name.isBlank() ->
            AppStrings.AnadidoEl + formatDate(destination.createdAt)
        generic -> AppStrings.ImportadoEl + formatDate(destination.createdAt)
        else -> destination.note ?: destination.category
    }
}
