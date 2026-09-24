package com.agendaqr.core.ui.lab

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.agendaqr.core.ui.components.XauxaEmptyState
import com.agendaqr.core.ui.components.XauxaFavoriteIndicator
import com.agendaqr.core.ui.components.XauxaLoading
import com.agendaqr.core.ui.components.XauxaPrimaryButton
import com.agendaqr.core.ui.components.XauxaScreen
import com.agendaqr.core.ui.components.XauxaSecondaryButton
import com.agendaqr.core.ui.components.XauxaSection
import com.agendaqr.core.ui.components.XauxaStatusBanner
import com.agendaqr.core.ui.components.XauxaTextAction
import com.agendaqr.core.ui.components.XauxaTile
import com.agendaqr.core.ui.theme.AgendaQrTheme
import com.agendaqr.core.ui.theme.XauxaColor
import com.agendaqr.core.ui.theme.XauxaMetrics
import com.agendaqr.core.ui.theme.XauxaSpacing
import com.agendaqr.core.ui.theme.XauxaType

/**
 * Development-only AgendaQr design-system catalog.
 *
 * This follows the catalog/inspector approach used by the WaraWerse
 * component laboratory, but contains AgendaQr-only product semantics.
 *
 * It is intentionally isolated from production navigation, repositories,
 * notifications, contacts, camera and storage.
 */
@Composable
fun AgendaQrComponentLab(
    modifier: Modifier = Modifier,
) {
    var query by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(LabCategory.All) }
    var selectedComponent by remember { mutableStateOf(AgendaQrComponentId.Screen) }
    var darkPreview by remember { mutableStateOf(false) }

    AgendaQrTheme(darkTheme = darkPreview) {
        XauxaScreen(modifier = modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxSize().padding(XauxaSpacing.Lg),
                horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Lg),
            ) {
                ComponentCatalogPane(
                    query = query,
                    onQueryChange = { query = it },
                    selectedCategory = selectedCategory,
                    onCategoryChange = { selectedCategory = it },
                    selectedComponent = selectedComponent,
                    onComponentSelected = { selectedComponent = it },
                    darkPreview = darkPreview,
                    onDarkPreviewChange = { darkPreview = it },
                    modifier = Modifier.weight(0.34f),
                )
                ComponentInspectorPane(
                    component = selectedComponent,
                    modifier = Modifier.weight(0.66f),
                )
            }
        }
    }
}

private enum class LabCategory(val label: String) {
    All("Todos"),
    Foundations("Fundamentos"),
    Actions("Acciones"),
    Feedback("Feedback"),
    Data("Datos"),
    QR("QR"),
}

private enum class AgendaQrComponentId(
    val label: String,
    val category: LabCategory,
    val description: String,
) {
    Screen("XauxaScreen", LabCategory.Foundations, "Contenedor base de pantalla"),
    Section("XauxaSection", LabCategory.Foundations, "Título, contenido y acción contextual"),
    Tile("XauxaTile", LabCategory.Data, "Superficie rectangular para una entidad o bloque"),
    PrimaryButton("XauxaPrimaryButton", LabCategory.Actions, "Acción primaria de AgendaQr"),
    SecondaryButton("XauxaSecondaryButton", LabCategory.Actions, "Acción secundaria o alternativa"),
    TextAction("XauxaTextAction", LabCategory.Actions, "Acción textual de bajo énfasis"),
    StatusBanner("XauxaStatusBanner", LabCategory.Feedback, "Mensaje persistente de estado"),
    Loading("XauxaLoading", LabCategory.Feedback, "Estado de carga"),
    EmptyState("XauxaEmptyState", LabCategory.Feedback, "Estado sin elementos"),
    Favorite("XauxaFavoriteIndicator", LabCategory.Data, "Indicador de favorito"),
    QrPreview("XauxaQrPreview", LabCategory.QR, "Contrato de preview de QR multiplataforma"),
}

@Composable
private fun ComponentCatalogPane(
    query: String,
    onQueryChange: (String) -> Unit,
    selectedCategory: LabCategory,
    onCategoryChange: (LabCategory) -> Unit,
    selectedComponent: AgendaQrComponentId,
    onComponentSelected: (AgendaQrComponentId) -> Unit,
    darkPreview: Boolean,
    onDarkPreviewChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val filtered = AgendaQrComponentId.entries.filter {
        (selectedCategory == LabCategory.All || it.category == selectedCategory) &&
            (query.isBlank() || it.label.contains(query, ignoreCase = true) || it.description.contains(query, ignoreCase = true))
    }

    XauxaTile(modifier = modifier) {
        Column(
            modifier = Modifier.fillMaxSize().padding(XauxaSpacing.Lg),
            verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Md),
        ) {
            Text("AgendaQr · Component Catalog", style = MaterialTheme.typography.headlineSmall)
            Text(
                "Inventario navegable de componentes. Selecciona uno para inspeccionar su contrato visual y estados.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            OutlinedTextField(
                value = query,
                onValueChange = onQueryChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Buscar componente") },
                singleLine = true,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Xs)) {
                LabCategory.entries.forEach { category ->
                    FilterChip(
                        selected = selectedCategory == category,
                        onClick = { onCategoryChange(category) },
                        label = { Text(category.label) },
                    )
                }
            }
            HorizontalDivider()
            LazyColumn(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Xs), modifier = Modifier.weight(1f)) {
                items(filtered) { component ->
                    CatalogItem(
                        component = component,
                        selected = component == selectedComponent,
                        onClick = { onComponentSelected(component) },
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
                XauxaSecondaryButton(
                    label = if (darkPreview) "Modo claro" else "Modo oscuro",
                    onClick = { onDarkPreviewChange(!darkPreview) },
                )
                Text(
                    if (darkPreview) "Preview: dark" else "Preview: light",
                    modifier = Modifier.padding(top = XauxaSpacing.Md),
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
    }
}

@Composable
private fun CatalogItem(
    component: AgendaQrComponentId,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(XauxaSpacing.Xs),
        color = if (selected) XauxaColor.Surface2 else XauxaColor.Surface,
        onClick = onClick,
    ) {
        Column(modifier = Modifier.padding(XauxaSpacing.Md)) {
            Text(component.label, style = MaterialTheme.typography.titleSmall)
            Text(component.description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun ComponentInspectorPane(
    component: AgendaQrComponentId,
    modifier: Modifier = Modifier,
) {
    XauxaTile(modifier = modifier) {
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(XauxaSpacing.Xl),
            verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Lg),
        ) {
            item {
                Text(component.label, style = MaterialTheme.typography.headlineSmall)
                Text(component.description, style = MaterialTheme.typography.bodyMedium)
            }
            item { ComponentStatusMatrix(component) }
            item { ComponentPreview(component) }
            item { ComponentTokenReference(component) }
            item { ComponentUsageExample(component) }
        }
    }
}

@Composable
private fun ComponentStatusMatrix(component: AgendaQrComponentId) {
    XauxaSection(title = "Estados de revisión") {
        Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
            ReviewRow("Default", "Disponible")
            ReviewRow("Pressed / Action", if (component.isInteractive()) "Interactivo" else "No aplica")
            ReviewRow("Disabled", if (component.isInteractive()) "Revisar" else "No aplica")
            ReviewRow("Empty / Loading", if (component.supportsFeedback()) "Disponible" else "No aplica")
            ReviewRow("Dark preview", "Disponible en catálogo")
        }
    }
}

@Composable
private fun ReviewRow(name: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(name, style = MaterialTheme.typography.bodyMedium)
        Text(value, style = MaterialTheme.typography.labelMedium)
    }
}

@Composable
private fun ComponentPreview(component: AgendaQrComponentId) {
    XauxaSection(title = "Live preview") {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.surfaceVariant,
            tonalElevation = 0.dp,
        ) {
            Column(
                modifier = Modifier.padding(XauxaSpacing.Lg),
                verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Md),
            ) {
                when (component) {
                    AgendaQrComponentId.Screen -> Text("Pantalla base de AgendaQr", style = MaterialTheme.typography.titleMedium)
                    AgendaQrComponentId.Section -> XauxaSection(title = "Obligaciones") { Text("Contenido de sección") }
                    AgendaQrComponentId.Tile -> XauxaTile { Text("Mensualidad escolar · Bs 450", modifier = Modifier.padding(XauxaSpacing.Lg)) }
                    AgendaQrComponentId.PrimaryButton -> XauxaPrimaryButton("Añadir QR", onClick = {})
                    AgendaQrComponentId.SecondaryButton -> XauxaSecondaryButton("Escanear QR", onClick = {})
                    AgendaQrComponentId.TextAction -> XauxaTextAction("Editar", onClick = {})
                    AgendaQrComponentId.StatusBanner -> XauxaStatusBanner("El comprobante está pendiente de revisar")
                    AgendaQrComponentId.Loading -> XauxaLoading()
                    AgendaQrComponentId.EmptyState -> XauxaEmptyState("No hay obligaciones registradas", "Añadir obligación", onClick = {})
                    AgendaQrComponentId.Favorite -> Row(horizontalArrangement = Arrangement.spacedBy(XauxaSpacing.Sm)) {
                        XauxaFavoriteIndicator(favorite = true)
                        Text("Favorito")
                    }
                    AgendaQrComponentId.QrPreview -> Column {
                        Text("QR Preview Contract", style = MaterialTheme.typography.titleMedium)
                        Text("La implementación visual se provee por plataforma.")
                    }
                }
            }
        }
    }
}

@Composable
private fun ComponentTokenReference(component: AgendaQrComponentId) {
    XauxaSection(title = "Tokens y contrato") {
        Column(verticalArrangement = Arrangement.spacedBy(XauxaSpacing.Xs)) {
            Text("Spacing: Xs / Sm / Md / Lg / Xl / Xxl", style = MaterialTheme.typography.bodySmall)
            Text("Metrics: Border / Focus / ControlMinSize / ContentMaxWidth", style = MaterialTheme.typography.bodySmall)
            Text("Type: Display / Headline / Title / Body / Label / Caption", style = MaterialTheme.typography.bodySmall)
            Text(
                when (component) {
                    AgendaQrComponentId.Tile -> "Semántica: superficie de datos y acción."
                    AgendaQrComponentId.PrimaryButton -> "Semántica: acción primaria."
                    AgendaQrComponentId.SecondaryButton -> "Semántica: acción secundaria."
                    AgendaQrComponentId.QrPreview -> "Contrato expect/actual: el laboratorio prueba la interfaz, no la implementación de plataforma."
                    else -> "Consumo mediante tokens Xauxa y MaterialTheme."
                },
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@Composable
private fun ComponentUsageExample(component: AgendaQrComponentId) {
    XauxaSection(title = "Uso en AgendaQr") {
        Text(
            when (component) {
                AgendaQrComponentId.Screen -> "Base para pantallas de lista, detalle, dashboard y configuración."
                AgendaQrComponentId.Section -> "Agrupa filtros, obligaciones, comprobantes o metadatos relacionados."
                AgendaQrComponentId.Tile -> "Representa una obligación, QR, comprobante, contacto o resumen."
                AgendaQrComponentId.PrimaryButton -> "Añadir QR, guardar obligación, registrar comprobante."
                AgendaQrComponentId.SecondaryButton -> "Escanear, ver detalle, abrir comprobante."
                AgendaQrComponentId.TextAction -> "Editar, cancelar o realizar una acción secundaria."
                AgendaQrComponentId.StatusBanner -> "Estado de sincronización, comprobante o validación."
                AgendaQrComponentId.Loading -> "Carga de QR, comprobantes o datos remotos."
                AgendaQrComponentId.EmptyState -> "Ninguna obligación, QR o comprobante disponible."
                AgendaQrComponentId.Favorite -> "Marcar QR u obligación como favorito."
                AgendaQrComponentId.QrPreview -> "Visualizar el QR almacenado antes de compartirlo o reutilizarlo."
            },
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

private fun AgendaQrComponentId.isInteractive(): Boolean = when (this) {
    AgendaQrComponentId.PrimaryButton,
    AgendaQrComponentId.SecondaryButton,
    AgendaQrComponentId.TextAction,
    AgendaQrComponentId.Tile,
    AgendaQrComponentId.EmptyState -> true
    else -> false
}

private fun AgendaQrComponentId.supportsFeedback(): Boolean = when (this) {
    AgendaQrComponentId.StatusBanner,
    AgendaQrComponentId.Loading,
    AgendaQrComponentId.EmptyState -> true
    else -> false
}
