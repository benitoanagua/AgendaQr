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
 * Development-only AgendaQr component gallery.
 *
 * Product-shaped sample content is local and fictional. This is a component
 * review surface, not a production screen and not connected to repositories,
 * navigation, notifications, contacts, camera, or storage.
 */
@Composable
fun AgendaQrComponentLab(
    modifier: Modifier = Modifier,
) {
    var search by remember { mutableStateOf("") }
    var paymentName by remember { mutableStateOf("Mensualidad escolar") }
    var reminderEnabled by remember { mutableStateOf(true) }
    var activeFilter by remember { mutableStateOf("Todos") }
    var lastAction by remember { mutableStateOf("Selecciona una acción para probar su estado.") }

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
            Text("AgendaQr · Laboratorio de componentes", style = MaterialTheme.typography.headlineSmall)
            Text(
                "Muestras de interfaz con datos ficticios. El estilo debe venir del tema Xauxa.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            LabSection(title = "Acciones principales") {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Button(onClick = { lastAction = "Añadir QR: acción activada" }) {
                        Text("Añadir QR")
                    }
                    OutlinedButton(onClick = { lastAction = "Escanear QR: acción activada" }) {
                        Text("Escanear")
                    }
                }
                Text(lastAction, style = MaterialTheme.typography.bodySmall)
            }

            LabSection(title = "Búsqueda y filtros") {
                OutlinedTextField(
                    value = search,
                    onValueChange = { search = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Buscar QR, persona u obligación") },
                    singleLine = true,
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    listOf("Todos", "Por vencer", "Vencidos").forEach { filter ->
                        if (activeFilter == filter) {
                            Button(onClick = { activeFilter = filter }) { Text(filter) }
                        } else {
                            OutlinedButton(onClick = { activeFilter = filter }) { Text(filter) }
                        }
                    }
                }
            }

            LabSection(title = "Elemento de lista · obligación") {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text("Mensualidad escolar", style = MaterialTheme.typography.titleMedium)
                        Text("Contacto: María López", style = MaterialTheme.typography.bodyMedium)
                        Text("Vence: 30 sep 2026 · Bs 450", style = MaterialTheme.typography.bodyMedium)
                        Text("Por vencer", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.tertiary)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedButton(onClick = { lastAction = "Abrir detalle de obligación" }) { Text("Ver detalle") }
                            Button(onClick = { lastAction = "Registrar comprobante" }) { Text("Comprobante") }
                        }
                    }
                }
            }

            LabSection(title = "Formulario · crear o editar obligación") {
                OutlinedTextField(
                    value = paymentName,
                    onValueChange = { paymentName = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Nombre de la obligación") },
                    singleLine = true,
                )
                OutlinedButton(onClick = { reminderEnabled = !reminderEnabled }) {
                    Text(if (reminderEnabled) "Recordatorio: activado" else "Recordatorio: desactivado")
                }
                Text(
                    "El recordatorio es una muestra de estado local; no programa notificaciones.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Button(onClick = { lastAction = "Guardar muestra: $paymentName" }) { Text("Guardar") }
            }

            LabSection(title = "QR y comprobante") {
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text("QR asociado", style = MaterialTheme.typography.titleMedium)
                        Text("Titular: Colegio Los Pinos", style = MaterialTheme.typography.bodyMedium)
                        Text("Tipo: Pago · Estado: Guardado", style = MaterialTheme.typography.bodyMedium)
                        OutlinedButton(onClick = { lastAction = "Vista previa del QR" }) { Text("Ver QR") }
                        Text("Comprobante reciente: pago-septiembre.pdf", style = MaterialTheme.typography.bodySmall)
                        OutlinedButton(onClick = { lastAction = "Abrir comprobante de ejemplo" }) { Text("Ver comprobante") }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Sin lógica de juego. La galería prueba patrones de AgendaQr con contenido de demostración; no conecta servicios ni guarda datos.",
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
