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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.agendaqr.core.ui.components.XauxaPrimaryButton
import com.agendaqr.core.ui.components.XauxaSecondaryButton
import com.agendaqr.core.ui.components.XauxaTile
import com.agendaqr.core.ui.theme.AgendaQrTheme
import com.agendaqr.core.ui.theme.XauxaSpacing

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

    AgendaQrTheme {
    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(XauxaSpacing.Xxl),
            verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Xl),
        ) {
            Text("AgendaQr · Laboratorio de componentes", style = MaterialTheme.typography.headlineSmall)
            Text(
                "Muestras de interfaz con datos ficticios. El estilo debe venir del tema Xauxa.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            LabSection(title = "Acciones principales") {
                Row(horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
                    XauxaPrimaryButton(label = "Añadir QR", onClick = { lastAction = "Añadir QR: acción activada" })
                    XauxaSecondaryButton(label = "Escanear", onClick = { lastAction = "Escanear QR: acción activada" })
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
                Row(horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
                    listOf("Todos", "Por vencer", "Vencidos").forEach { filter ->
                        if (activeFilter == filter) {
                            XauxaPrimaryButton(label = filter, onClick = { activeFilter = filter })
                        } else {
                            XauxaSecondaryButton(label = filter, onClick = { activeFilter = filter })
                        }
                    }
                }
            }

            LabSection(title = "Elemento de lista · obligación") {
                XauxaTile {
                    Column(
                        modifier = Modifier.padding(XauxaSpacing.Lg),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text("Mensualidad escolar", style = MaterialTheme.typography.titleMedium)
                        Text("Contacto: María López", style = MaterialTheme.typography.bodyMedium)
                        Text("Vence: 30 sep 2026 · Bs 450", style = MaterialTheme.typography.bodyMedium)
                        Text("Por vencer", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.tertiary)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            XauxaSecondaryButton(label = "Ver detalle", onClick = { lastAction = "Abrir detalle de obligación" })
                            XauxaPrimaryButton(label = "Comprobante", onClick = { lastAction = "Registrar comprobante" })
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
                XauxaSecondaryButton(label = if (reminderEnabled) "Recordatorio: activado" else "Recordatorio: desactivado", onClick = { reminderEnabled = !reminderEnabled })
                Text(
                    "El recordatorio es una muestra de estado local; no programa notificaciones.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                XauxaPrimaryButton(label = "Guardar", onClick = { lastAction = "Guardar muestra: $paymentName" })
            }

            LabSection(title = "QR y comprobante") {
                XauxaTile {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Xs),
                    ) {
                        Text("QR asociado", style = MaterialTheme.typography.titleMedium)
                        Text("Titular: Colegio Los Pinos", style = MaterialTheme.typography.bodyMedium)
                        Text("Tipo: Pago · Estado: Guardado", style = MaterialTheme.typography.bodyMedium)
                        XauxaSecondaryButton(label = "Ver QR", onClick = { lastAction = "Vista previa del QR" })
                        Text("Comprobante reciente: pago-septiembre.pdf", style = MaterialTheme.typography.bodySmall)
                        XauxaSecondaryButton(label = "Ver comprobante", onClick = { lastAction = "Abrir comprobante de ejemplo" })
                    }
                }
            }

            Spacer(modifier = Modifier.height(XauxaSpacing.Sm))
            Text(
                "Sin lógica de juego. La galería prueba patrones de AgendaQr con contenido de demostración; no conecta servicios ni guarda datos.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
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
