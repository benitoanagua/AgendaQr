package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import com.agendaqr.core.ui.components.XauxaEmptyState
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
    error: String? = null,
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
        Text("QR importados", modifier = Modifier.semantics { heading() }, fontSize = XauxaType.Headline, color = XauxaColor.TextPrimary)
        error?.let { XauxaStatusBanner(it, tone = XauxaTone.Danger, onDismiss = onClearError) }
        if (assets.isEmpty()) {
            XauxaEmptyState(
                title = "No hay QR para revisar",
                subtitle = "Vuelve e importa desde Cámara o Galería.",
                actionLabel = "Volver",
                onAction = onBack,
            )
        } else {
            Text(
                if (assets.size == 1) "1 destino" else "${assets.size} destinos",
                fontSize = XauxaType.Label,
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
            XauxaPrimaryButton("Guardar todo", onSaveAll, enabled = !isSaving, isLoading = isSaving)
            XauxaSecondaryButton("Volver", onBack, enabled = !isSaving)
        }
    }
}
