package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import com.agendaqr.core.ui.components.XauxaCommandBar
import com.agendaqr.core.ui.components.XauxaOverflowAction
import com.agendaqr.core.ui.components.XauxaPrimaryButton
import com.agendaqr.core.ui.components.XauxaQrPreview
import com.agendaqr.core.ui.components.XauxaSecondaryButton
import com.agendaqr.core.ui.components.XauxaTextAction
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.core.ui.theme.XauxaSpacing
import com.agendaqr.core.ui.theme.XauxaType
import com.agendaqr.destinations.domain.Destination

@Composable
fun DestinationDetailScreen(
    destination: Destination,
    onShowQr: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onShare: () -> Unit,
    onBack: () -> Unit,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.weight(1f).padding(XauxaSpacing.Xxl),
            verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Lg),
        ) {
            Text(destination.name, modifier = Modifier.semantics { heading() }, fontSize = XauxaType.Headline, fontWeight = FontWeight.Bold, color = XauxaColor.TextPrimary)
            destination.category?.let { Text(it, color = XauxaColor.TextSecondary) }
            destination.note?.takeIf { it.isNotBlank() }?.let { Text(it, color = XauxaColor.TextSecondary) }
            XauxaQrPreview(destination.qr.encoded)
            Row(horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
                XauxaPrimaryButton("Mostrar QR", onShowQr)
                XauxaSecondaryButton("Volver", onBack)
            }
        }
        // cb1: command bar contextual solo en pantallas de detalle/foco como
        // esta (nunca en la lista/Home) — Xauxa checklist KMP §07. Delete va
        // a overflow a propósito, no a las hasta 3 acciones visibles (cb2):
        // es destructiva y no debería quedar a un toque de Edit/Share.
        XauxaCommandBar(
            visibleActions = listOf(
                { XauxaTextAction("Editar", onEdit) },
                { XauxaTextAction("Compartir", onShare) },
            ),
            overflowActions = listOf(
                XauxaOverflowAction("Eliminar", onDelete),
            ),
        )
    }
}
