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

/**
 * Vista previa del elemento existente con el que coincide un duplicado.
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
    var savedEvent by remember { mutableStateOf<Any?>(null) }
    var wasSaving by remember { mutableStateOf(false) }
    LaunchedEffect(state) {
        val savingNow = state is ImportBatchUiState.Saving
        if (wasSaving && state is ImportBatchUiState.Saved) {
            savedEvent = Any()
        }
        wasSaving = state is ImportBatchUiState.Saving || state is ImportBatchUiState.Saved
    }
    val toastHost = appHosts?.toastHost
    LaunchedEffect(savedEvent) {
        if (savedEvent != null) {
            toastHost?.show(AppStrings.GuardadoOk)
        }
    }
    val currentBatch = when (state) {
        is ImportBatchUiState.Result -> state.batch
        is ImportBatchUiState.Review -> state.batch
        is ImportBatchUiState.Saving -> state.batch
        is ImportBatchUiState.Saved -> state.batch
        is ImportBatchUiState.Error -> state.batch
        else -> null
    }
    val onBack: (() -> Unit)? = if (state is ImportBatchUiState.Saving) {
        null
    } else {
        { onAction(ImportBatchAction.Back) }
    }
    AqScreen(
        title = AppStrings.ResultadoDeImportacion,
        onBack = onBack,
        backLabel = if (state is ImportBatchUiState.Review) AppStrings.VolverAResultado else AppStrings.Volver,
        statusContent = appHosts?.status,
        bottomAction = if (currentBatch?.canSaveRecognized() == true) {
            {
                AqButton(
                    label = if (state is ImportBatchUiState.Error) {
                        AppStrings.reintentarGuardado
                    } else {
                        val recognized = currentBatch?.uniqueRecognized?.size ?: 0
                        if (recognized > 0) guardarReconocidosLabel(recognized) else AppStrings.GuardarReconocidos
                    },
                    onClick = { onAction(ImportBatchAction.SaveRecognized) },
                    enabled = state !is ImportBatchUiState.Saving,
                    variant = com.agendaqr.core.ui.components.AqButtonVariant.Primary,
                )
            }
        } else {
            null
        },
        toastHost = toastHost?.let { host -> { AqToastHost(host) } },
        scrollable = true,
    ) {
        when (state) {
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
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(AqSpacing.Md),
                ) {
                    com.agendaqr.core.ui.components.AqSuccessCheck(size = AqMetrics.ResultGlyphSize)
                    AqAlert(
                        tone = AqTone.Success,
                        message = AppStrings.GuardadoOk,
                        dismissLabel = AppStrings.Cerrar,
                        onDismiss = {},
                        contentDescription = AppStrings.GuardadoOk,
                    )
                    AqText(
                        text = AppStrings.batchSavedKeepReviewNotice,
                        size = AqType.BodySm,
                        color = AqColor.TextSecondary,
                    )
                    state.batch.let { batch ->
                        if (batch.pendingItems().isNotEmpty()) {
                            AqButton(
                                label = reviewPendingLabel(batch),
                                onClick = { onAction(ImportBatchAction.ReviewPending) },
                                variant = com.agendaqr.core.ui.components.AqButtonVariant.Text,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                }
            }
            is ImportBatchUiState.Error -> {
                // §10: el bloque de error muestra título (what) y descripción
                // (dataStatus) como texto, precedidos por un icono de alerta,
                // con semántica de encabezado en el título y liveRegion en el
                // contenedor para anuncios de accesibilidad. Un solo margen
                // de pantalla (el de AqScreen), sin padding duplicado.
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .semantics { liveRegion = LiveRegionMode.Polite },
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Spacer(modifier = Modifier.width(AqSpacing.Lg))
                    AqIcon(
                        imageVector = com.agendaqr.core.ui.components.AqIcons.Warning,
                        contentDescription = null,
                        size = AqMetrics.IconSizeCompact,
                        tint = AqColor.Danger,
                    )
                    Spacer(modifier = Modifier.width(AqSpacing.Sm))
                    Column(verticalArrangement = Arrangement.spacedBy(AqSize.Sm)) {
                        AqText(
                            text = state.error.what,
                            size = AqType.HeadingMd,
                            fontWeight = AqWeight.SemiBold,
                            color = AqColor.Danger,
                            semanticHeading = true,
                        )
                        if (state.error.dataStatus.isNotBlank()) {
                            AqText(
                                text = state.error.dataStatus,
                                size = AqType.BodySm,
                                color = AqColor.TextSecondary,
                            )
                        }
                    }
                }
                state.batch?.let { BatchResultContent(it, onAction, isSaving = false) }
            }
        }
    }

    existing?.let { preview ->
        AqModal(
            title = AppStrings.ElementoExistente,
            dismissLabel = AppStrings.Cerrar,
            confirmLabel = AppStrings.Cerrar,
            onConfirm = { onAction(ImportBatchAction.CloseExisting) },
            onDismissRequest = { onAction(ImportBatchAction.CloseExisting) },
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
