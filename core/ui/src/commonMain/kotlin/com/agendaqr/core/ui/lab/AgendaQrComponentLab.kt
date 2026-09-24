package com.agendaqr.core.ui.lab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Development-only gallery for reviewing core Material 3 component states.
 *
 * Keep this composable out of production navigation until the visual contract
 * and the Xauxa token-backed theme are wired into the host application.
 */
@Composable
fun AgendaQrComponentLab(
    modifier: Modifier = Modifier,
) {
    var sampleText by remember { mutableStateOf("Pago de mensualidad") }
    var selected by remember { mutableStateOf(false) }
    var lastAction by remember { mutableStateOf("Ninguna acción todavía") }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(
                text = "Laboratorio UI",
                style = MaterialTheme.typography.headlineMedium,
            )
            Text(
                text = "Muestras de desarrollo · sin datos ni servicios de producto",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            LabSection(title = "Acciones") {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(onClick = { lastAction = "Acción primaria activada" }) {
                        Text("Acción primaria")
                    }
                    OutlinedButton(onClick = { lastAction = "Acción secundaria activada" }) {
                        Text("Secundaria")
                    }
                }
                Text(text = lastAction, style = MaterialTheme.typography.bodySmall)
            }

            LabSection(title = "Entrada de texto") {
                OutlinedTextField(
                    value = sampleText,
                    onValueChange = { sampleText = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Descripción de muestra") },
                    supportingText = { Text("Campo local; no se guarda.") },
                    singleLine = true,
                )
            }

            LabSection(title = "Selección y estado") {
                OutlinedButton(onClick = { selected = !selected }) {
                    Text(if (selected) "Seleccionado ✓" else "Seleccionar muestra")
                }
                Text(
                    text = if (selected) "Estado: seleccionado" else "Estado: sin seleccionar",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            LabSection(title = "Superficie / tarjeta") {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text("Comprobante de ejemplo", style = MaterialTheme.typography.titleMedium)
                        Text("Vence el 30 de septiembre", style = MaterialTheme.typography.bodyMedium)
                        Text(
                            "Contenido ficticio para revisar jerarquía y espaciado.",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Nota: el tema y los tokens Xauxa deben conectarse desde el tema compartido antes de considerar esta galería una validación visual final.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun LabSection(
    title: String,
    content: @Composable () -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(text = title, style = MaterialTheme.typography.titleSmall)
        content()
    }
}
