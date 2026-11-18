package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.agendaqr.core.ui.components.AqSnackbarHost
import com.agendaqr.core.ui.components.*
import com.agendaqr.core.ui.components.AqNavChromeState
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.destinations.domain.AgendaSearchResult

@Composable
fun GlobalSearchScreen(
    state: GlobalSearchUiState,
    navChrome: AqNavChromeState? = null,
    appHosts: AppHosts? = null,
    onAction: (GlobalSearchAction) -> Unit,
    onSelect: (AgendaSearchResult) -> Unit,
    /** Foco automático solo al abrir desde el disparador de Inicio
     * (U-21); por pestaña se entra sin foco. */
    autoFocus: Boolean = true,
) {
    // V1.4 (§12): Buscar es destino raíz — barra superior con título y
    // navegación inferior; el campo de búsqueda es el cuerpo de la
    // superficie. Los resultados siguen agrupados por tipo con
    // encabezados de sección estándar.
    AqScreenScaffold(
        topBar = {
            AqTopBar(title = AppStrings.Buscar)
        },
        navDestinations = navChrome?.destinations.orEmpty(),
        navSelectedId = navChrome?.selectedId,
        onNavSelect = { navChrome?.onSelect?.invoke(it) },
        snackbarHost = appHosts?.feedback?.let { host -> { AqSnackbarHost(host) } },
        statusContent = appHosts?.status,
    ) {
        AqSearchField(
            value = state.query,
            onValueChange = { onAction(GlobalSearchAction.QueryChanged(it)) },
            label = AppStrings.BuscarEnAgendaQr,
            placeholder = AppStrings.Buscar,
            onClear = { onAction(GlobalSearchAction.Clear) },
            clearLabel = AppStrings.Limpiar,
            // Entrar a Buscar es intención explícita de búsqueda: foco
            // inmediato con teclado de texto (sin autocompletado agresivo).
            keyboardOptions = androidx.compose.foundation.text.KeyboardOptions(
                keyboardType = androidx.compose.ui.text.input.KeyboardType.Text,
                imeAction = androidx.compose.ui.text.input.ImeAction.Search,
            ),
            autoFocus = autoFocus,
        )
        state.error?.let { err ->
            val (bannerAction, bannerDismiss) = err.bannerActions(
                onRetry = { onAction(GlobalSearchAction.QueryChanged(state.query)) },
                onClear = { onAction(GlobalSearchAction.ClearError) },
            )
            AqStatusBanner(
                title = err.what,
                message = err.dataStatus,
                tone = AqTone.Danger,
                action = bannerAction,
                dismiss = bannerDismiss,
            )
        }
        when {
            state.query.isBlank() -> AqText(
                AppStrings.searchIntroHint,
                color = AqColor.TextSecondary,
            )
            state.isSearching -> AqLoading(message = AppStrings.BuscandoEnAgendaQr)
            state.results.isEmpty() -> AqEmptyState(
                icon = AqIcons.SearchX,
                title = AppStrings.SinResultados,
                subtitle = AppStrings.PruebaConOtraPalabraO,
                actionLabel = AppStrings.Limpiar,
                onAction = { onAction(GlobalSearchAction.Clear) },
            )
            else -> LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                // Grupos funcionales (cada tipo con su encabezado + cuerpo)
                // separados a 20 dp; dentro del grupo el encabezado vive
                // pegado a su lista (8 dp).
                verticalArrangement = Arrangement.spacedBy(AqSpacing.Xl),
            ) {
                // S04/S05 (M7): resultados heterogéneos agrupados por tipo,
                // cada grupo con su encabezado de sección (§4: el tipo se
                // expresa con texto/semántica) y su cuerpo de lista.
                val groups = state.results.groupBy { it.type }
                com.agendaqr.destinations.domain.AgendaSearchResultType.entries.forEach { type ->
                    val group = groups[type].orEmpty()
                    if (group.isNotEmpty()) {
                        item(key = "group:" + type.name) {
                            Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
                                AqSectionHeader(
                                    // S04 (V1.19): el encabezado lleva el
                                    // conteo real del grupo.
                                    text = searchResultTypeLabel(type) + " (" + group.size + ")",
                                )
                                AqListBody {
                                    group.forEachIndexed { index, result ->
                                        AqListRow(
                                            title = searchResultTitle(result),
                                            subtitle = searchResultSubtitle(result),
                                            // Fecha como metadato final (solo los
                                            // tipos que tienen una: actividad y
                                            // comprobante). El formato es de la
                                            // capa de presentación.
                                            endMeta = result.dateMillis?.let { formatDate(it) },
                                            // Marca de tipo a la izquierda:
                                            // glifo decorativo del tipo del
                                            // grupo. La señal textual sigue
                                            // siendo el encabezado de sección
                                            // (nunca color/glifo solos).
                                            leading = {
                                                AqIconBadge(icon = searchResultTypeIcon(type))
                                            },
                                            onClick = { onSelect(result) },
                                            showDivider = index < group.lastIndex,
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun searchResultTypeLabel(type: com.agendaqr.destinations.domain.AgendaSearchResultType): String =
    when (type) {
        com.agendaqr.destinations.domain.AgendaSearchResultType.CONTEXT -> AppStrings.TipoContexto
        com.agendaqr.destinations.domain.AgendaSearchResultType.QR -> AppStrings.TipoQr
        com.agendaqr.destinations.domain.AgendaSearchResultType.ACTIVITY -> AppStrings.TipoActividad
        com.agendaqr.destinations.domain.AgendaSearchResultType.COMPROBANTE -> AppStrings.TipoComprobante
    }

/**
 * Glifo decorativo del tipo de resultado (marca de fila). El tipo se
 * comunica con TEXTO en el encabezado de sección; el glifo acompaña, nunca
 * es la única señal (contrato de accesibilidad).
 */
private fun searchResultTypeIcon(type: com.agendaqr.destinations.domain.AgendaSearchResultType): ImageVector =
    when (type) {
        com.agendaqr.destinations.domain.AgendaSearchResultType.CONTEXT -> AqIcons.Context
        com.agendaqr.destinations.domain.AgendaSearchResultType.QR -> AqIcons.QrCode
        com.agendaqr.destinations.domain.AgendaSearchResultType.ACTIVITY -> AqIcons.Wallet
        com.agendaqr.destinations.domain.AgendaSearchResultType.COMPROBANTE -> AqIcons.Attachment
    }

/**
 * El dominio emite `type.name`/`provenance.name` como fallback de texto; aquí
 * se traducen a la semántica visible (nunca enums técnicos en UI).
 */
internal fun searchResultTitle(result: AgendaSearchResult): String = when (result.title) {
    "PAGO" -> AppStrings.Pago
    "COBRO" -> AppStrings.Cobro
    else -> result.title
}

internal fun searchResultSubtitle(result: AgendaSearchResult): String? {
    val subtitle = result.subtitle ?: return null
    val provenance = runCatching {
        com.agendaqr.destinations.domain.ReceiptProvenance.valueOf(subtitle)
    }.getOrNull()
    return provenance?.let { receiptProvenanceLabel(it) } ?: subtitle
}
