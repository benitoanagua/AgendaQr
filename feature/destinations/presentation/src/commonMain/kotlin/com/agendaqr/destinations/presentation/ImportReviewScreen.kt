package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import com.agendaqr.destinations.presentation.AppStrings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.XauxaEmptyState
import com.agendaqr.core.ui.components.XauxaText
import com.agendaqr.core.ui.components.XauxaPrimaryButton
import com.agendaqr.core.ui.components.XauxaQrPreview
import com.agendaqr.core.ui.components.XauxaScreenScaffold
import com.agendaqr.core.ui.components.XauxaTopBar
import com.agendaqr.core.ui.components.XauxaStatusBanner
import com.agendaqr.core.ui.components.XauxaTone
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.core.ui.theme.XauxaSpacing
import com.agendaqr.core.ui.theme.XauxaType
import com.agendaqr.destinations.domain.QrAsset

@Composable
fun ImportReviewScreen(
    assets: List<QrAsset>,
    onSaveAll: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    isSaving: Boolean = false,
    error: UserFacingError? = null,
    onClearError: () -> Unit = {},
) {
    // V1.2 (§12): "Revisar QR" en la barra superior; Guardar/Guardar todo es
    // el botón principal FIJO abajo (acción real de guardado). Mientras se
    // guarda no hay vuelta (§3).
    XauxaScreenScaffold(
        modifier = modifier,
        topBar = {
            XauxaTopBar(
                title = AppStrings.RevisarQr,
                onBack = if (isSaving) null else onBack,
                backLabel = AppStrings.Volver,
            )
        },
        bottomAction = if (assets.isNotEmpty()) {
            {
                XauxaPrimaryButton(
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
                        .padding(horizontal = XauxaSpacing.ScreenMargin, vertical = XauxaSpacing.Sm),
                )
            }
        } else {
            null
        },
    ) {
        error?.let { err ->
            XauxaStatusBanner(
                err.display(),
                tone = XauxaTone.Danger,
                actionLabel = err.action.label,
                // REINTENTAR guarda los importados de nuevo (el lote sigue
                // retenido en el VM); el resto de acciones solo descarta.
                onAction = { if (err.action == ErrorAction.Retry) onSaveAll() else onClearError() },
                onDismiss = onClearError,
                dismissLabel = AppStrings.Descartar,
            )
        }
        if (assets.isEmpty()) {
            XauxaEmptyState(
                title = AppStrings.NoHayQrParaRevisar,
                subtitle = AppStrings.VuelveEImportaDesdeCamara,
                actionLabel = AppStrings.Volver,
                onAction = onBack,
            )
        } else {
            XauxaText(
                if (assets.size == 1) AppStrings.UnQrListoParaGuardar
                else assets.size.toString() + AppStrings.QrListosParaGuardarSufijo,
                size = XauxaType.Label,
                color = XauxaColor.TextSecondary,
            )
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm),
            ) {
                itemsIndexed(assets) { _, asset ->
                    // S09/el código va centrado (antes quedaba pegado a
                    // la izquierda) y sin depender de métricas técnicas.
                    Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                        XauxaQrPreview(asset.encoded)
                    }
                    // S09: evidencia de lo que Agenda QR entendió — el texto
                    // decodificado, sin exponer payload técnico.
                    asset.content?.takeIf { it.isNotBlank() }?.let { content ->
                        XauxaText(
                            AppStrings.Contenido + ": " + content,
                            size = XauxaType.Label,
                            color = XauxaColor.TextSecondary,
                        )
                    }
                }
            }
        }
    }
}
