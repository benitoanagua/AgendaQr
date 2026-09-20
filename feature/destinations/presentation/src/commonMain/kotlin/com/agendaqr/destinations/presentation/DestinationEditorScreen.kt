package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.agendaqr.core.ui.components.XauxaPrimaryButton
import com.agendaqr.core.ui.components.XauxaSecondaryButton
import com.agendaqr.core.ui.components.XauxaQrPreview
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.core.ui.theme.XauxaSpacing
import com.agendaqr.core.ui.theme.XauxaType
import com.agendaqr.destinations.domain.Destination
import com.agendaqr.destinations.domain.QrAsset

@Composable
fun DestinationEditorScreen(
    existing: Destination?,
    onSave: (Destination) -> Unit,
    onImportMany: (List<QrAsset>) -> Unit,
    onBack: () -> Unit,
) {
    val initial = remember(existing) {
        existing ?: Destination(
            id = "destination-${com.agendaqr.destinations.domain.nowMillis()}",
            name = "",
            qr = QrAsset(encoded = ""),
            createdAt = com.agendaqr.destinations.domain.nowMillis(),
            updatedAt = com.agendaqr.destinations.domain.nowMillis(),
        )
    }
    varName(initial.name, existing, onSave, onBack)
}

@Composable
private fun varName(
    initialName: String,
    existing: Destination?,
    onSave: (Destination) -> Unit,
    onImportMany: (List<QrAsset>) -> Unit,
    onBack: () -> Unit,
) {
    var name by remember(initialName) { mutableStateOf(initialName) }
    var category by remember(existing?.category) { mutableStateOf(existing?.category.orEmpty()) }
    var note by remember(existing?.note) { mutableStateOf(existing?.note.orEmpty()) }
    var qr by remember(existing?.qr) { mutableStateOf(existing?.qr ?: QrAsset(encoded = "")) }

    Column(
        modifier = Modifier.fillMaxSize().padding(XauxaSpacing.Xxl),
        verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Lg),
    ) {
        Text(if (existing == null) "Add destination" else "Edit destination", fontSize = XauxaType.Headline, fontWeight = FontWeight.Bold, color = XauxaColor.TextPrimary)
        OutlinedTextField(name, { name = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Name") }, shape = androidx.compose.foundation.shape.RectangleShape)
        OutlinedTextField(category, { category = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Category") }, shape = androidx.compose.foundation.shape.RectangleShape)
        OutlinedTextField(note, { note = it }, modifier = Modifier.fillMaxWidth(), label = { Text("Note") }, shape = androidx.compose.foundation.shape.RectangleShape, minLines = 3)
        QrImportControls { result ->
            when {
                result.assets.size > 1 -> onImportMany(result.assets)
                result.assets.size == 1 -> qr = result.assets.first()
            }
        }
        XauxaQrPreview(qr.encoded)
        Row(horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
            XauxaSecondaryButton("Back", onBack)
            XauxaPrimaryButton("Save", {
                val now = com.agendaqr.destinations.domain.nowMillis()
                onSave(initial.copy(
                    name = name.trim(),
                    category = category.trim().ifBlank { null },
                    note = note.trim().ifBlank { null },
                    qr = qr,
                    updatedAt = now,
                ))
            })
        }
    }
}
