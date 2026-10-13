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
import com.agendaqr.core.ui.components.AmSnackbarHost
import com.agendaqr.core.ui.components.AmEmptyState
import com.agendaqr.core.ui.components.AmIcons
import com.agendaqr.core.ui.components.AmText
import com.agendaqr.core.ui.components.AmPrimaryButton
import com.agendaqr.core.ui.components.AmQrPreview
import com.agendaqr.core.ui.components.AmScreenScaffold
import com.agendaqr.core.ui.components.AmTopBar
import com.agendaqr.core.ui.components.AmStatusBanner
import com.agendaqr.core.ui.components.AmTone
import com.agendaqr.core.ui.theme.AmColor
import com.agendaqr.core.ui.theme.AmMetrics
import com.agendaqr.core.ui.theme.AmSpacing
import com.agendaqr.core.ui.theme.AmType
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
    AmScreenScaffold(
        modifier = modifier,
        topBar = {
            AmTopBar(
                title = AppStrings.RevisarQr,
                onBack = if (isSaving) null else onBack,
                backLabel = AppStrings.Volver,
            )
        },
        snackbarHost = appHosts?.feedback?.let { host -> { AmSnackbarHost(host) } },
        statusContent = appHosts?.status,
        bottomAction = if (assets.isNotEmpty()) {
            {
                AmPrimaryButton(
                    label = when {
                        isSaving -> AppStrings.Guardando
                        assets.size == 1 -> AppStrings.Guardar
                        else -> AppStrings.GuardarTodo
                    },
                    onClick = onSaveAll,
                    enabled = !isSaving,
                    isLoading = isSaving,
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding()
                    .imePadding()
                        .padding(horizontal = AmSpacing.ScreenMargin, vertical = AmSpacing.Sm),
                )
            }
        } else {
            null
        },
    ) {
        error?.let { err ->
            AmStatusBanner(
                err.display(),
                tone = AmTone.Danger,
                actionLabel = err.action.label,
                // REINTENTAR guarda los importados de nuevo (el lote sigue
                // retenido en el VM); el resto de acciones solo descarta.
                onAction = { if (err.action == ErrorAction.Retry) onSaveAll() else onClearError() },
                onDismiss = onClearError,
                dismissLabel = AppStrings.Descartar,
            )
        }
        if (assets.isEmpty()) {
            AmEmptyState(
                icon = AmIcons.ScanLine,
                title = AppStrings.NoHayQrParaRevisar,
                subtitle = AppStrings.VuelveEImportaDesdeCamara,
                actionLabel = AppStrings.Volver,
                onAction = onBack,
            )
        } else {
            AmText(
                if (assets.size == 1) AppStrings.UnQrListoParaGuardar
                else assets.size.toString() + AppStrings.QrListosParaGuardarSufijo,
                size = AmType.Label,
                color = AmColor.TextSecondary,
            )
            if (assets.size == 1) {
                // Un solo código: vista previa grande (aprovecha el ancho
                // sin deformar, siempre 1:1) con su evidencia centrada.
                val asset = assets.single()
                Column(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    AmQrPreview(asset.encoded, size = AmMetrics.QrReviewSize)
                    asset.content?.takeIf { it.isNotBlank() }?.let { content ->
                        AmText(
                            AppStrings.Contenido + ": " + content,
                            size = AmType.Label,
                            color = AmColor.TextSecondary,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    verticalArrangement = Arrangement.spacedBy(AmSpacing.Sm),
                ) {
                    itemsIndexed(assets) { _, asset ->
                        // S09/el código va centrado (antes quedaba pegado a
                        // la izquierda) y sin depender de métricas técnicas.
                        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                            AmQrPreview(asset.encoded)
                        }
                        // S09: evidencia de lo que Agenda QR entendió — el texto
                        // decodificado, sin exponer payload técnico.
                        asset.content?.takeIf { it.isNotBlank() }?.let { content ->
                            AmText(
                                AppStrings.Contenido + ": " + content,
                                size = AmType.Label,
                                color = AmColor.TextSecondary,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                            )
                        }
                    }
                }
            }
        }
    }
}
