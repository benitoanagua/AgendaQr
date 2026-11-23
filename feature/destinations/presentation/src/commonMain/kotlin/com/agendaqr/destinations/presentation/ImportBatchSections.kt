package com.agendaqr.destinations.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.LiveRegionMode
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.semantics.liveRegion
import androidx.compose.ui.semantics.semantics
import com.agendaqr.core.ui.components.AqAlert
import com.agendaqr.core.ui.components.AqAppBarAction
import com.agendaqr.core.ui.components.AqBottomSheet
import com.agendaqr.core.ui.components.AqButton
import com.agendaqr.core.ui.components.AqButtonSize
import com.agendaqr.core.ui.components.AqButtonVariant
import com.agendaqr.core.ui.components.AqChip
import com.agendaqr.core.ui.components.AqEmptyPlaceholder
import com.agendaqr.core.ui.components.AqHairline
import com.agendaqr.core.ui.components.AqIcon
import com.agendaqr.core.ui.components.AqIcons
import com.agendaqr.core.ui.components.AqMenuItem
import com.agendaqr.core.ui.components.AqModal
import com.agendaqr.core.ui.components.AqQrPreview
import com.agendaqr.core.ui.components.AqScreen
import com.agendaqr.core.ui.components.AqSectionTitle
import com.agendaqr.core.ui.components.AqSkeletonBlock
import com.agendaqr.core.ui.components.AqSuccessCheck
import com.agendaqr.core.ui.components.AqTag
import com.agendaqr.core.ui.components.AqText
import com.agendaqr.core.ui.components.AqToastHost
import com.agendaqr.core.ui.components.AqTone
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqShadow
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqSquircles
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.core.ui.theme.AqWeight
import com.agendaqr.core.ui.theme.aqShadow
import com.agendaqr.destinations.domain.ImportBatch
import com.agendaqr.destinations.domain.ImportCandidate
import com.agendaqr.destinations.domain.ImportKind
import com.agendaqr.core.ui.components.AqTag as AqTagComponent
import com.agendaqr.core.ui.components.AqProgress as AqProgressComp
import com.agendaqr.core.ui.components.AqProgressBlock as AqProgressBlockComp

/** Secciones del resultado/revisión de importación masiva (extraídas de ImportBatchScreen, A-11). */
@Composable
internal fun BatchResultContent(
    batch: ImportBatch,
    onAction: (ImportBatchAction) -> Unit,
    isSaving: Boolean,
) {
    if (isSaving) {
        val total = (batch.uniqueRecognized.size + batch.duplicates.size + batch.unknown.size).coerceAtLeast(1)
        val recognized = batch.uniqueRecognized.size
        val fraction = (recognized.toFloat() / total.toFloat()).coerceIn(0f, 1f)
        AqProgressBlockComp(
            progress = AqProgressComp(fraction),
            label = AppStrings.Guardando,
            contentDescription = AppStrings.Guardando,
        )
    }
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AqColor.Surface, AqSquircles.SurfaceLg)
            .aqShadow(AqShadow.Sm, AqSquircles.SurfaceLg)
            .padding(AqSpacing.Lg),
        verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
    ) {
        if (batch.uniqueRecognized.isNotEmpty()) {
            AqMetaLine(label = AppStrings.Reconocidos, value = batch.uniqueRecognized.size.toString(), leadingIcon = AqIcons.CircleCheck)
        }
        if (batch.duplicates.isNotEmpty()) {
            AqMetaLine(label = AppStrings.PosiblesDuplicados, value = batch.duplicates.size.toString(), leadingIcon = AqIcons.Warning)
        }
        if (batch.unknown.isNotEmpty()) {
            AqMetaLine(label = AppStrings.PendientesDeRevision, value = batch.unknown.size.toString(), leadingIcon = AqIcons.Info)
        }
    }
    if (batch.pendingItems().isNotEmpty()) {
        AqButton(
            label = reviewPendingLabel(batch),
            onClick = { onAction(ImportBatchAction.ReviewPending) },
            variant = com.agendaqr.core.ui.components.AqButtonVariant.Text,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isSaving,
        )
    }
}

@Composable
internal fun BatchReviewContent(
    batch: ImportBatch,
    onAction: (ImportBatchAction) -> Unit,
    isSaving: Boolean,
) {
    val pending = batch.pendingItems()
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
        // Fila única con scroll horizontal: tres chips apilados consumen
        // ~160 dp y dejan las acciones de la primera tarjeta bajo el
        // pliegue en viewports cortos (D-02 exige targets visibles).
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
        ) {
            AqChip(
                text = AppStrings.Todos,
                selected = filterIndex == 0,
                onClick = { filterIndex = 0 },
            )
            AqChip(
                text = AppStrings.PosiblesDuplicados,
                selected = filterIndex == 1,
                onClick = { filterIndex = 1 },
            )
            AqChip(
                text = AppStrings.PendientesDeRevision,
                selected = filterIndex == 2,
                onClick = { filterIndex = 2 },
            )
        }
    }
    AqSectionTitle(text = AppStrings.ElementosQueNecesitanRevision + " (" + filtered.size + ")")
    Column(modifier = Modifier.fillMaxWidth().testTag("aq-list-body")) {
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

@Composable
internal fun PendingItemCell(
    candidate: ImportCandidate,
    batch: ImportBatch,
    onAction: (ImportBatchAction) -> Unit,
    isSaving: Boolean,
    index: Int = 0,
    showDivider: Boolean = false,
) {
    val isDuplicate = batch.duplicates.any { it.id == candidate.id }
    var showDiscardConfirm by remember(candidate.id) { mutableStateOf(false) }
    Column(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = AqSpacing.Lg, vertical = AqSpacing.Md),
            verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
        ) {
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
                AqTagComponent(
                    text = if (isDuplicate) AppStrings.PosibleDuplicado else AppStrings.PendienteDeRevision,
                    tone = if (isDuplicate) AqTone.Warning else AqTone.Info,
                )
            }
            when {
                isDuplicate -> AqButton(
                    label = AppStrings.VerExistente,
                    onClick = { onAction(ImportBatchAction.ViewExisting(candidate.id)) },
                    variant = com.agendaqr.core.ui.components.AqButtonVariant.Text,
                    modifier = Modifier.heightIn(min = AqMetrics.ControlMinSize),
                )
                else -> AqButton(
                    label = AppStrings.ReintentarClasificacion,
                    onClick = { onAction(ImportBatchAction.RetryCandidate(candidate.id)) },
                    variant = com.agendaqr.core.ui.components.AqButtonVariant.Text,
                    modifier = Modifier.heightIn(min = AqMetrics.ControlMinSize),
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm, alignment = Alignment.End),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Forza que la fila mida al menos un objetivo táctil (48 dp) para
                // que los botones de texto de las acciones puedan medir 48 dp
                // (D-02) aunque el Row envuelva su contenido.
                Spacer(
                    modifier = Modifier
                        .height(AqMetrics.ControlMinSize)
                        .width(AqSpacing.None),
                )
                if (isDuplicate) {
                    AqButton(
                        label = AppStrings.GuardarDeTodosModos,
                        onClick = { onAction(ImportBatchAction.SaveDuplicateAnyway(candidate.id)) },
                        variant = com.agendaqr.core.ui.components.AqButtonVariant.Text,
                        modifier = Modifier.heightIn(min = AqMetrics.ControlMinSize),
                    )
                }
                AqButton(
                    label = AppStrings.Descartar,
                    onClick = {
                        if (isDuplicate) {
                            onAction(ImportBatchAction.DiscardCandidate(candidate.id))
                        } else {
                            showDiscardConfirm = true
                        }
                    },
                    variant = com.agendaqr.core.ui.components.AqButtonVariant.Danger,
                    modifier = Modifier.heightIn(min = AqMetrics.ControlMinSize),
                )
            }
        }
        if (showDivider) AqHairline()
    }
    if (showDiscardConfirm) {
        AqModal(
            title = AppStrings.Descartar,
            dismissLabel = AppStrings.Cancelar,
            confirmLabel = AppStrings.descartarElementoConfirmar,
            destructive = true,
            onConfirm = {
                showDiscardConfirm = false
                onAction(ImportBatchAction.DiscardCandidate(candidate.id))
            },
            onDismissRequest = { showDiscardConfirm = false },
            content = {
                AqText(AppStrings.descartarElementoWarning, color = AqColor.TextSecondary)
            },
        )
    }
}

/** Fila de metadato (etiqueta + valor) construida con piezas Moon. */
@Composable
private fun AqMetaLine(
    label: String,
    value: String,
    leadingIcon: (() -> Unit)? = null,
) {
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
