package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.XauxaPrimaryButton
import com.agendaqr.core.ui.components.XauxaQrPreview
import com.agendaqr.core.ui.components.XauxaSecondaryButton
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.core.ui.theme.XauxaSpacing
import com.agendaqr.core.ui.theme.XauxaType
import com.agendaqr.destinations.domain.QrAsset

@Composable
fun ImportReviewScreen(
    assets: List<QrAsset>,
    onSaveAll: () -> Unit,
    onBack: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().padding(XauxaSpacing.Xxl),
        verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Lg),
    ) {
        Text("Imported QR", fontSize = XauxaType.Headline, color = XauxaColor.TextPrimary)
        Text("${assets.size} destination(s)", fontSize = XauxaType.Label, color = XauxaColor.TextSecondary)
        LazyColumn(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm), modifier = Modifier.fillMaxWidth()) {
            itemsIndexed(assets) { _, asset ->
                XauxaQrPreview(asset.encoded)
            }
        }
        XauxaPrimaryButton("Save all", onSaveAll)
        XauxaSecondaryButton("Back", onBack)
    }
}
