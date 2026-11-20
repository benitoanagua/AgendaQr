package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.AqSnackbarHost
import com.agendaqr.core.ui.components.AqCard
import com.agendaqr.core.ui.components.AqEmptyState
import com.agendaqr.core.ui.components.AqIcons
import com.agendaqr.core.ui.components.AqText
import com.agendaqr.core.ui.components.AqBottomAction
import com.agendaqr.core.ui.components.AqProgress
import com.agendaqr.core.ui.components.AqProgressBar
import com.agendaqr.core.ui.components.AqQrPreview
import com.agendaqr.core.ui.components.AqScreenScaffold
import com.agendaqr.core.ui.components.AqTopBar
import com.agendaqr.core.ui.components.AqStatusBanner
import com.agendaqr.core.ui.components.AqTone
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqMetrics
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.destinations.domain.QrAsset

@Composable
fun ImportReviewScreen(
    appHosts: AppHosts? = null,
    assets: List<QrAsset>,
    onSaveAll: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    isSaving: Boolean = false,
    error: UserFacingError? = null,
    onClearError: () -> Unit = {},
) {
    // V1.4 (§12): "Revisar QR" en la barra superior; Guardar/Guardar todo es
    // el botón principal FIJO abajo (acción real de guardado). Mientras se
    // guarda no hay vuelta (§3).
    AqScreenScaffold(
        modifier = modifier,
        topBar = {
            AqTopBar(
                title = AppStrings.RevisarQr,
                onBack = if (isSaving) null else onBack,
                backLabel = AppStrings.Volver,
            )
        },
        snackbarHost = appHosts?.feedback?.let { host -> { AqSnackbarHost(host) } },
        statusContent = appHosts?.status,
        bottomAction = if (assets.isNotEmpty()) {
            {
                AqBottomAction(
                    label = when {
                        isSaving -> AppStrings.Guardando
                        assets.size == 1 -> AppStrings.Guardar
                        else -> AppStrings.GuardarTodo
                    },
                    onClick = onSaveAll,
                    enabled = !isSaving,
                    isLoading = isSaving,
                )
            }
        } else {
            null
        },
    ) {
        error?.let { err ->
            val (bannerAction, bannerDismiss) = err.bannerActions(
                onRetry = onSaveAll,
                onClear = onClearError,
            )
            AqStatusBanner(
                title = err.what,
                message = err.dataStatus,
                tone = AqTone.Danger,
                action = bannerAction,
                dismiss = bannerDismiss,
            )
        }
        if (isSaving) {
            AqProgressBar(
                progress = AqProgress(1f),
                contentDescription = AppStrings.Guardando,
            )
        }
        if (assets.isEmpty()) {
            AqEmptyState(
                icon = AqIcons.ScanLine,
                title = AppStrings.NoHayQrParaRevisar,
                subtitle = AppStrings.VuelveEImportaDesdeCamara,
                actionLabel = AppStrings.Volver,
                onAction = onBack,
            )
        } else {
            AqText(
                if (assets.size == 1) AppStrings.UnQrListoParaGuardar
                else assets.size.toString() + AppStrings.QrListosParaGuardarSufijo,
                size = AqType.Label,
                color = AqColor.TextSecondary,
            )
            if (assets.size == 1) {
                // Un solo código: vista previa grande (aprovecha el ancho
                // sin deformar, siempre 1:1) con su evidencia centrada.
                val asset = assets.single()
                Column(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    // Vista previa + evidencia decodificada como grupo (8 dp).
                    verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm, Alignment.CenterVertically),
                ) {
                    AqQrPreview(asset.encoded, size = AqMetrics.QrReviewSize)
                    asset.content?.takeIf { it.isNotBlank() }?.let { content ->
                        DecodedEvidenceCard(content)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
                ) {
                    itemsIndexed(assets) { _, asset ->
                        // S09/el código va centrado (antes quedaba pegado a
                        // la izquierda) y sin depender de métricas técnicas.
                        // Vista previa + evidencia como grupo (8 dp).
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
                        ) {
                            AqQrPreview(asset.encoded)
                            // S09: evidencia de lo que Agenda QR entendió — el texto
                            // decodificado, sin exponer payload técnico.
                            asset.content?.takeIf { it.isNotBlank() }?.let { content ->
                                DecodedEvidenceCard(content)
                            }
                        }
                    }
                }
            }
        }
    }
}

/**
 * Evidencia decodificada de un QR (S09): el texto que Agenda QR entendió,
 * presentado como bloque etiqueta-valor dentro de una tarjeta del sistema,
 * sin exponer payload técnico innecesario.
 */
@Composable
private fun DecodedEvidenceCard(content: String) {
    AqCard {
        Column(verticalArrangement = Arrangement.spacedBy(AqSpacing.Xs)) {
            AqText(
                AppStrings.Contenido,
                size = AqType.Caption,
                color = AqColor.TextSecondary,
            )
            AqText(
                content,
                size = AqType.Label,
                color = AqColor.TextPrimary,
                maxLines = 2,
            )
        }
    }
}
