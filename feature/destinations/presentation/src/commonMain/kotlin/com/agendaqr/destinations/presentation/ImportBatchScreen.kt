package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.AqSnackbarHost
import com.agendaqr.core.ui.components.AqDialog
import com.agendaqr.core.ui.components.AqFeedbackEvent
import com.agendaqr.core.ui.components.AqHairline
import com.agendaqr.core.ui.components.AqListBody
import com.agendaqr.core.ui.components.AqListRow
import com.agendaqr.core.ui.components.AqMetaRow
import com.agendaqr.core.ui.components.AqSectionHeader
import com.agendaqr.core.ui.components.AqTextActionVariant
import com.agendaqr.core.ui.components.AqBottomAction
import com.agendaqr.core.ui.components.AqQrPreview
import com.agendaqr.core.ui.components.AqProgress
import com.agendaqr.core.ui.components.AqProgressBlock
import com.agendaqr.core.ui.components.AqScreenScaffold
import com.agendaqr.core.ui.components.AqTopBar
import com.agendaqr.core.ui.components.AqSecondaryButton
import com.agendaqr.core.ui.components.AqStatusBanner
import com.agendaqr.core.ui.components.AqStatusPill
import com.agendaqr.core.ui.components.AqSuccessCheck
import com.agendaqr.core.ui.components.AqCard
import com.agendaqr.core.ui.components.AqChipGroup
import com.agendaqr.core.ui.components.AqChipOption
import com.agendaqr.core.ui.components.AqIcons
import com.agendaqr.core.ui.components.AqText
import com.agendaqr.core.ui.components.AqTextAction
import com.agendaqr.core.ui.components.AqLoading
import com.agendaqr.core.ui.components.AqTone
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.core.ui.theme.AqWeight
import com.agendaqr.destinations.domain.ImportBatch
import com.agendaqr.destinations.domain.ImportCandidate
import com.agendaqr.destinations.domain.ImportKind

/**
 * Vista previa del elemento existente con el que coincide un duplicado
 * ("Ver existente"). No navega: el usuario decide sin perder la
 * revisión del lote.
 */
data class ExistingImportPreview(
    val qrAsset: com.agendaqr.destinations.domain.QrAsset? = null,
    val comprobanteBytes: ByteArray? = null,
    val comprobanteMimeType: String? = null,
)

@Composable
fun ImportBatchScreen(
    state: ImportBatchUiState,
    onAction: (ImportBatchAction) -> Unit,
    existing: ExistingImportPreview? = null,
    appHosts: AppHosts? = null,
) {
    // guardado del lote anunciado en la TRANSICIÓN al
    // estado Saved (por evento, no por estado).
    var savedEvent by remember { mutableStateOf<Any?>(null) }
    var wasSaving by remember { mutableStateOf(false) }
    androidx.compose.runtime.LaunchedEffect(state) {
        val savingNow = state is ImportBatchUiState.Saving
        if (wasSaving && state is ImportBatchUiState.Saved) {
            savedEvent = Any()
        }
        wasSaving = state is ImportBatchUiState.Saving || state is ImportBatchUiState.Saved
    }
    AqFeedbackEvent(
        event = savedEvent,
        message = AppStrings.GuardadoOk,
        haptic = false,
    )
    // V1.4 (§4 S12): barra superior con título y atrás; "Guardar
    // reconocidos" como botón principal FIJO abajo, solo cuando hay
    // elementos reconocibles; "Revisar N pendientes" en el cuerpo.
    // Mientras se guarda NO hay vuelta (el lote se perdería); el Back del
    // estado de revisión regresa al RESULTADO (flujo padre), no al origen.
    val currentBatch = when (state) {
        is ImportBatchUiState.Result -> state.batch
        is ImportBatchUiState.Review -> state.batch
        is ImportBatchUiState.Saving -> state.batch
        is ImportBatchUiState.Saved -> state.batch
        is ImportBatchUiState.Error -> state.batch
        else -> null
    }
    AqScreenScaffold(
        scrollable = true,
        topBar = {
            AqTopBar(
                title = AppStrings.ResultadoDeImportacion,
                onBack = if (state is ImportBatchUiState.Saving) null else ({ onAction(ImportBatchAction.Back) }),
                backLabel = if (state is ImportBatchUiState.Review) AppStrings.VolverAResultado else AppStrings.Volver,
            )
        },
        snackbarHost = appHosts?.feedback?.let { host -> { AqSnackbarHost(host) } },
        statusContent = appHosts?.status,
        bottomAction = if (currentBatch?.canSaveRecognized() == true) {
            {
                AqBottomAction(
                    label = if (state is ImportBatchUiState.Error) {
                        AppStrings.reintentarGuardado
                    } else {
                        val recognized = currentBatch?.uniqueRecognized?.size ?: 0
                        if (recognized > 0) guardarReconocidosLabel(recognized) else AppStrings.GuardarReconocidos
                    },
                    onClick = { onAction(ImportBatchAction.SaveRecognized) },
                    enabled = state !is ImportBatchUiState.Saving,
                    isLoading = state is ImportBatchUiState.Saving,
                )
            }
        } else {
            null
        },
    ) {
        when (state) {
            // Idle es alcanzable por restauración tras muerte de proceso
            // (stack restaurado con S12 arriba y holder nuevo): el estado
            // lleva una acción real ("Elegir imágenes"), no una pantalla
            // vacía.
            ImportBatchUiState.Idle -> {
                AqText(AppStrings.TraeVariosElementosAAgenda, color = AqColor.TextSecondary)
                ImportBatchPickerButton(label = AppStrings.ElegirImagenes)
            }
            is ImportBatchUiState.Result -> BatchResultContent(state.batch, onAction, isSaving = false)
            is ImportBatchUiState.Review -> BatchReviewContent(state.batch, onAction, isSaving = false)
            is ImportBatchUiState.Saving -> {
                BatchResultContent(state.batch, onAction, isSaving = true)
            }
            is ImportBatchUiState.Saved -> {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center,
                ) {
                    AqSuccessCheck(size = AqMetrics.ResultGlyphSize)
                }
                AqStatusBanner(
                    title = AppStrings.GuardadoOk,
                    message = AppStrings.batchSavedKeepReviewNotice,
                    tone = AqTone.Success,
                )
                state.batch.let { batch ->
                    if (batch.pendingItems().isNotEmpty()) {
                        AqSecondaryButton(
                            label = reviewPendingLabel(batch),
                            onClick = { onAction(ImportBatchAction.ReviewPending) },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
            is ImportBatchUiState.Error -> {
                // El banner no lleva acción: el botón fijo inferior
                // "Reintentar guardado" es la única fuente de la acción.
                // El margen de pantalla lo aplica una sola vez el contenido
                // del scaffold; el banner no suma el suyo por defecto.
                AqStatusBanner(
                    title = state.error.what,
                    message = state.error.dataStatus,
                    tone = AqTone.Danger,
                )
                state.batch?.let { BatchResultContent(it, onAction, isSaving = false) }
            }
        }
    }

    existing?.let { preview ->
        AqDialog(
            title = AppStrings.ElementoExistente,
            confirmLabel = AppStrings.Cerrar,
            onConfirm = { onAction(ImportBatchAction.CloseExisting) },
            onDismiss = { onAction(ImportBatchAction.CloseExisting) },
            content = {
                Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
                    when {
                        preview.qrAsset != null -> AqQrPreview(preview.qrAsset.encoded)
                        preview.comprobanteBytes != null ->
                            ComprobantePreview(preview.comprobanteBytes, preview.comprobanteMimeType)
                        else -> AqText(AppStrings.NoPudimosMostrarElElemento, color = AqColor.TextSecondary)
                    }
                }
            },
        )
    }
}

/**
 * Resumen de importación con jerarquía clara:
 * 1. Reconocidos (listos para guardar) — tono de éxito
 * 2. Posibles duplicados — tono de advertencia
 * 3. Pendientes de revisión — tono neutro
 */
@Composable
private fun BatchResultContent(
    batch: ImportBatch,
    onAction: (ImportBatchAction) -> Unit,
    isSaving: Boolean,
) {
    // Tarjeta resumen como filas etiqueta-valor: cada estado del lote con
    // su conteo real alineado a la derecha (singular honesto por conteo).
    if (isSaving) {
        val total = (batch.uniqueRecognized.size + batch.duplicates.size + batch.unknown.size).coerceAtLeast(1)
        val recognized = batch.uniqueRecognized.size
        val fraction = (recognized.toFloat() / total.toFloat()).coerceIn(0f, 1f)
        AqProgressBlock(
            progress = AqProgress(fraction),
            label = AppStrings.Guardando,
            contentDescription = AppStrings.Guardando,
        )
    }
    AqCard {
        Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm)) {
            if (batch.uniqueRecognized.isNotEmpty()) {
                AqMetaRow(
                    label = AppStrings.Reconocidos,
                    value = batch.uniqueRecognized.size.toString(),
                    icon = AqIcons.CircleCheck,
                )
            }
            if (batch.duplicates.isNotEmpty()) {
                AqMetaRow(
                    label = AppStrings.PosiblesDuplicados,
                    value = batch.duplicates.size.toString(),
                    icon = AqIcons.Warning,
                )
            }
            if (batch.unknown.isNotEmpty()) {
                AqMetaRow(
                    label = AppStrings.PendientesDeRevision,
                    value = batch.unknown.size.toString(),
                    icon = AqIcons.Info,
                )
            }
        }
    }

    // Contrato S12: "Revisar N pendientes" como acción secundaria a ancho
    // completo; "Guardar reconocidos" vive fijo abajo (botón principal del
    // scaffold).
    if (batch.pendingItems().isNotEmpty()) {
        AqSecondaryButton(
            reviewPendingLabel(batch),
            onClick = { onAction(ImportBatchAction.ReviewPending) },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isSaving,
        )
    }
}

/**
 * Revisión de pendientes: cada elemento como unidad visual reconocible
 * con acciones diferenciadas (principal vs secundarias).
 */
@Composable
private fun BatchReviewContent(
    batch: ImportBatch,
    onAction: (ImportBatchAction) -> Unit,
    isSaving: Boolean,
) {
    val pending = batch.pendingItems()
    // ADR-0028: con duplicados Y sin clasificar a la vez, chips de filtro
    // (AqChipGroup); con un solo tipo de pendiente no hay filtro que ofrecer.
    val hasDuplicates = batch.duplicates.isNotEmpty()
    val hasUnknown = batch.unknown.isNotEmpty()
    val showFilter = hasDuplicates || hasUnknown
    var filterIndex by remember(batch) { mutableStateOf(0) }
    val filtered = when {
        !showFilter || filterIndex == 0 -> pending
        filterIndex == 1 -> batch.duplicates
        else -> batch.unknown
    }
    if (showFilter) {
        AqChipGroup(
            options = listOf(
                AqChipOption(AppStrings.Todos, count = pending.size),
                AqChipOption(AppStrings.PosiblesDuplicados, count = batch.duplicates.size),
                AqChipOption(AppStrings.PendientesDeRevision, count = batch.unknown.size),
            ),
            selectedIndex = filterIndex,
            onSelect = { filterIndex = it.coerceIn(0, 2) },
            horizontalScroll = true,
        )
    }
    AqSectionHeader(text = AppStrings.ElementosQueNecesitanRevision + " (" + filtered.size + ")")
    // Spec S12: Cell-group de elementos con Tag textual de estado. Todos los
    // pendientes forman un único grupo de celdas con divisores (no una
    // tarjeta por elemento); cada celda lleva su contenido y sus acciones.
    AqListBody {
        filtered.forEachIndexed { index, candidate ->
            PendingItemCell(
                candidate = candidate,
                batch = batch,
                onAction = onAction,
                isSaving = isSaving,
                index = index,
                showDivider = index < filtered.lastIndex,
            )
        }
    }
}

/**
 * Celda de pendiente con jerarquía de acciones (acciones de texto dentro de
 * la celda, sin botón por fila):
 * - Principal: "Reintentar clasificación" (para desconocidos) o "Ver existente" (para duplicados)
 * - Secundaria: "Guardar de todos modos" (solo duplicados)
 * - Terciaria: "Descartar"
 */
@Composable
private fun PendingItemCell(
    candidate: ImportCandidate,
    batch: ImportBatch,
    onAction: (ImportBatchAction) -> Unit,
    isSaving: Boolean,
    index: Int = 0,
    showDivider: Boolean = false,
) {
    val isDuplicate = batch.duplicates.any { it.id == candidate.id }
    // El descarte de un elemento SIN clasificar pierde su contenido
    // retenido (no recuperable dentro del flujo: habría que re-importar),
    // así que pide confirmación; el de un duplicado no pierde nada (el
    // original existe) y se ejecuta directo.
    var showDiscardConfirm by remember(candidate.id) { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AqSpacing.Lg, vertical = AqSpacing.Md),
            verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
        ) {
            // Nombre útil, dato del archivo o motivo, y estado. El estado se
            // expresa con una insignia CON texto (el glifo y el tono solo
            // acompañan): la insignia va en su propia línea para no recortar
            // el ancho del título, tampoco con fuente al 200 %.
            Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Xs)) {
                AqText(text = pendingItemName(candidate, index), fontWeight = AqWeight.Medium)
                AqText(
                    text = if (candidate.displayName?.isNotBlank() == true) {
                        (candidate.extension?.let { AppStrings.ArchivoPrefijo + it + " · " } ?: "") + when (candidate.kind) {
                            ImportKind.QR -> "QR"
                            ImportKind.COMPROBANTE -> "comprobante"
                            ImportKind.DESCONOCIDO -> "sin clasificar"
                        }
                    } else {
                        when {
                            isDuplicate -> AppStrings.elementoYaGuardado
                            candidate.kind == ImportKind.DESCONOCIDO -> AppStrings.elementoSinClasificar
                            else -> AppStrings.elementoPendienteRevision
                        }
                    },
                    size = AqType.BodySmall,
                    color = AqColor.TextSecondary,
                )
                AqStatusPill(
                    text = if (isDuplicate) AppStrings.PosibleDuplicado else AppStrings.PendienteDeRevision,
                    icon = if (isDuplicate) AqIcons.Warning else AqIcons.Info,
                    tone = if (isDuplicate) AqTone.Warning else AqTone.Info,
                )
            }
            // Acción principal de la celda (texto de acento, objetivo ≥ 48 dp).
            when {
                isDuplicate -> AqTextAction(
                    label = AppStrings.VerExistente,
                    onClick = { onAction(ImportBatchAction.ViewExisting(candidate.id)) },
                    contentPadding = PaddingValues(horizontal = AqSpacing.None, vertical = AqSpacing.None),
                )
                else -> AqTextAction(
                    label = AppStrings.ReintentarClasificacion,
                    onClick = { onAction(ImportBatchAction.RetryCandidate(candidate.id)) },
                    contentPadding = PaddingValues(horizontal = AqSpacing.None, vertical = AqSpacing.None),
                )
            }
            // Acciones secundarias alineadas al final, mismo orden siempre:
            // [Guardar de todos modos] [Descartar], Descartar a la derecha.
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(
                    AqSpacing.Sm,
                    alignment = Alignment.End,
                ),
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,
            ) {
                if (isDuplicate) {
                    AqTextAction(
                        label = AppStrings.GuardarDeTodosModos,
                        onClick = { onAction(ImportBatchAction.SaveDuplicateAnyway(candidate.id)) },
                        variant = AqTextActionVariant.Neutral,
                        contentPadding = PaddingValues(horizontal = AqSpacing.None, vertical = AqSpacing.None),
                    )
                }
                AqTextAction(
                    label = AppStrings.Descartar,
                    onClick = {
                        if (isDuplicate) {
                            onAction(ImportBatchAction.DiscardCandidate(candidate.id))
                        } else {
                            showDiscardConfirm = true
                        }
                    },
                    variant = AqTextActionVariant.Danger,
                    contentPadding = PaddingValues(horizontal = AqSpacing.None, vertical = AqSpacing.None),
                )
            }
        }
        if (showDivider) AqHairline()
    }
    if (showDiscardConfirm) {
        AqDialog(
            title = AppStrings.Descartar,
            message = AppStrings.descartarElementoWarning,
            // Etiqueta específica (no "Descartar" genérico): nombra la
            // consecuencia exacta y desambigua frente a la acción de fila.
            confirmLabel = AppStrings.descartarElementoConfirmar,
            destructive = true,
            onConfirm = {
                showDiscardConfirm = false
                onAction(ImportBatchAction.DiscardCandidate(candidate.id))
            },
            dismissLabel = AppStrings.Cancelar,
            onDismiss = { showDiscardConfirm = false },
        )
    }
}
