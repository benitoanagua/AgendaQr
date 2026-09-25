package com.agendaqr.destinations.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.heading
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import com.agendaqr.core.ui.components.XauxaPrimaryButton
import com.agendaqr.core.ui.components.XauxaSecondaryButton
import com.agendaqr.core.ui.components.XauxaTextAction
import com.agendaqr.core.ui.components.XauxaTextInput
import com.agendaqr.core.ui.components.XauxaDialog
import com.agendaqr.core.ui.components.XauxaTile
import com.agendaqr.core.ui.components.XauxaQrPreview
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.core.ui.theme.XauxaSpacing
import com.agendaqr.core.ui.theme.XauxaType
import com.agendaqr.destinations.domain.Context
import com.agendaqr.destinations.domain.Destination
import com.agendaqr.destinations.domain.QrAsset

@Composable
fun DestinationEditorScreen(
    existing: Destination?,
    onSave: (Destination) -> Unit,
    onImportMany: (List<QrAsset>) -> Unit,
    contexts: List<Context> = emptyList(),
    onBack: () -> Unit,
) {
    val initial = remember(existing) {
        existing ?: Destination(
            id = com.agendaqr.destinations.domain.newEntityId("destination"),
            name = "",
            qr = QrAsset(encoded = ""),
            createdAt = com.agendaqr.destinations.domain.nowMillis(),
            updatedAt = com.agendaqr.destinations.domain.nowMillis(),
        )
    }
    varName(initial, existing, onSave, onImportMany, contexts, onBack)
}

@Composable
private fun varName(
    initial: Destination,
    existing: Destination?,
    onSave: (Destination) -> Unit,
    onImportMany: (List<QrAsset>) -> Unit,
    contexts: List<Context>,
    onBack: () -> Unit,
) {
    var name by remember(initial.name) { mutableStateOf(initial.name) }
    var category by remember(existing?.category) { mutableStateOf(existing?.category.orEmpty()) }
    var note by remember(existing?.note) { mutableStateOf(existing?.note.orEmpty()) }
    var qr by remember(existing?.qr) { mutableStateOf(existing?.qr ?: QrAsset(encoded = "")) }
    var contextId by remember(existing?.contextId) { mutableStateOf(existing?.contextId) }
    var showContextPicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().padding(XauxaSpacing.Xxl),
        verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Lg),
    ) {
        Text(if (existing == null) "Agregar destino" else "Editar destino", modifier = Modifier.semantics { heading() }, fontSize = XauxaType.Headline, fontWeight = FontWeight.Bold, color = XauxaColor.TextPrimary)
        XauxaTextInput(label = "Nombre", value = name, onValueChange = { name = it })
        XauxaTextInput(label = "Categoría", value = category, onValueChange = { category = it })
        XauxaTextInput(label = "Nota", value = note, onValueChange = { note = it }, singleLine = false, minLines = 3)
        XauxaSecondaryButton(label = contextId?.let { id -> "Para: " + (contexts.firstOrNull { it.id == id }?.name ?: "Contexto") } ?: "Para: elegir contexto (opcional)", onClick = { showContextPicker = true })
        QrImportControls { result ->
            when {
                result.assets.size > 1 -> onImportMany(result.assets)
                result.assets.size == 1 -> qr = result.assets.first()
            }
        }
        XauxaQrPreview(qr.encoded)
        Row(horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
            XauxaSecondaryButton(label = "Volver", onClick = onBack)
            XauxaPrimaryButton(label = "Guardar", onClick = {
                val now = com.agendaqr.destinations.domain.nowMillis()
                onSave(initial.copy(
                    name = name.trim(),
                    category = category.trim().ifBlank { null },
                    note = note.trim().ifBlank { null },
                    qr = qr,
                    contextId = contextId,
                    updatedAt = now,
                ))
            })
        }
        if (showContextPicker) {
            XauxaDialog(
                title = "¿A cuál corresponde?",
                message = contexts.joinToString("\n") { it.name }.ifBlank { "Sin contextos disponibles." },
                confirmLabel = "Cerrar",
                onConfirm = { showContextPicker = false },
                dismissLabel = "Sin contexto",
                onDismiss = { contextId = null; showContextPicker = false },
            )
        }
    }
}
