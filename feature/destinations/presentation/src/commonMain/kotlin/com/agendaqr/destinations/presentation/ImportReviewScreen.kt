package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import com.agendaqr.destinations.presentation.AppStrings
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.semantics.semantics
import com.agendaqr.core.ui.components.XauxaEmptyState
import com.agendaqr.core.ui.components.XauxaText
import com.agendaqr.core.ui.components.XauxaHeading
import com.agendaqr.core.ui.components.XauxaPrimaryButton
import com.agendaqr.core.ui.components.XauxaQrPreview
import com.agendaqr.core.ui.components.XauxaSecondaryButton
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
    Column(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(XauxaSpacing.Xxl)
            .imePadding(),
        verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Lg),
    ) {
        XauxaHeading(text = AppStrings.RevisarQr, size = XauxaType.Headline, fontWeight = FontWeight.Normal)
        error?.let { err ->
            XauxaStatusBanner(
                err.display(),
                tone = XauxaTone.Danger,
                actionLabel = err.action.label,
                onAction = onClearError,
                onDismiss = onClearError,
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
                if (assets.size == 1) "1 QR listo para guardar" else "${assets.size} QR listos para guardar",
                size = XauxaType.Label,
                color = XauxaColor.TextSecondary,
            )
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm),
            ) {
                itemsIndexed(assets) { _, asset ->
                    XauxaQrPreview(asset.encoded)
                }
            }
            XauxaPrimaryButton(if (assets.size == 1) "Guardar" else "Guardar todo", onSaveAll, enabled = !isSaving, isLoading = isSaving)
            XauxaSecondaryButton(AppStrings.Volver, onBack, enabled = !isSaving)
        }
    }
}
