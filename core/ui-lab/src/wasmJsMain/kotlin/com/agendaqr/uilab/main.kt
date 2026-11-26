package com.agendaqr.uilab

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeViewport
import com.agendaqr.core.ui.components.AqButton
import com.agendaqr.core.ui.components.AqButtonSize
import com.agendaqr.core.ui.components.AqButtonVariant
import com.agendaqr.core.ui.components.AqChip
import com.agendaqr.core.ui.components.AqChipSize
import com.agendaqr.core.ui.components.AqSearchInput
import com.agendaqr.core.ui.components.AqText
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqComponentCategory
import com.agendaqr.core.ui.theme.AqComponentEntry
import com.agendaqr.core.ui.theme.AqComponentRegistry
import com.agendaqr.core.ui.theme.AqDesignSystemGallery
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqTheme
import com.agendaqr.core.ui.theme.AqType
import kotlinx.browser.document
import kotlinx.browser.window

private val ViewportWidths = listOf<Dp?>(null, 360.dp, 412.dp, 768.dp)
private val FontScales = listOf(1f, 1.5f, 2f)

@OptIn(ExperimentalComposeUiApi::class, kotlin.js.ExperimentalWasmJsInterop::class)
fun main() {
    ComposeViewport(document.getElementById("app")!!) {
        AqComponentLab()
    }
}

/**
 * Component Lab web (wasmJs): registro navegable de los componentes `Aq*`
 * con tema, escala de fuente, ancho de viewport, estados y log de
 * interacciones. Enlace profundo por hash: `#AqButton` o
 * `#AqButton/estados` (id de estado).
 */
@OptIn(kotlin.js.ExperimentalWasmJsInterop::class)
@Composable
private fun AqComponentLab() {
    var darkTheme by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    var category by remember { mutableStateOf<AqComponentCategory?>(null) }
    var fontScale by remember { mutableStateOf(1f) }
    var viewportWidth by remember { mutableStateOf<Dp?>(null) }
    var showGallery by remember { mutableStateOf(false) }
    val events = remember { mutableStateListOf<String>() }

    val log: (String) -> Unit = { message ->
        events.add(0, message)
        while (events.size > 60) events.removeAt(events.lastIndex)
    }

    var selectedEntry by remember { mutableStateOf<AqComponentEntry?>(null) }
    var selectedState by remember { mutableStateOf(0) }

    fun select(entry: AqComponentEntry?, stateIndex: Int = 0) {
        selectedEntry = entry
        selectedState = stateIndex.coerceIn(0, (entry?.states?.lastIndex ?: 0).coerceAtLeast(0))
        val hash = if (entry == null) "" else "#${entry.id}" +
            (entry.states.getOrNull(stateIndex)?.let { "/${it.id}" } ?: "")
        window.history.replaceState(null, "", hash)
        if (entry != null) log("nav: ${entry.id}/${entry.states.getOrNull(stateIndex)?.id}")
    }

    // Enlace profundo: #Componente[/estado], también en caliente (hashchange).
    fun applyHash() {
        val hash = window.location.hash.removePrefix("#")
        if (hash.isEmpty()) return
        val parts = hash.split("/")
        val entry = AqComponentRegistry.firstOrNull { it.id == parts[0] } ?: return
        val stateIndex = entry.states.indexOfFirst { it.id == parts.getOrNull(1) }
        select(entry, if (stateIndex >= 0) stateIndex else 0)
    }
    LaunchedEffect(Unit) {
        applyHash()
        window.onhashchange = { applyHash() }
    }

    val filtered = remember(query, category) {
        AqComponentRegistry.filter { entry ->
            (category == null || entry.category == category) &&
                (query.isBlank() || entry.name.contains(query.trim(), ignoreCase = true) ||
                    entry.category.label.contains(query.trim(), ignoreCase = true))
        }
    }

    val density = LocalDensity.current
    CompositionLocalProvider(
        LocalDensity provides Density(density.density, fontScale),
    ) {
        AqTheme(darkTheme = darkTheme) {
        Column(modifier = Modifier.fillMaxSize().background(AqColor.Background)) {
            LabToolbar(
                darkTheme = darkTheme,
                onTheme = { darkTheme = it; log("theme: ${if (it) "dark" else "light"}") },
                query = query,
                onQuery = { query = it },
                fontScale = fontScale,
                onFontScale = { fontScale = it; log("fontScale: ${it}x") },
                viewportWidth = viewportWidth,
                onViewport = { viewportWidth = it; log("viewport: ${it?.value?.toInt() ?: "auto"}dp") },
                showGallery = showGallery,
                onGallery = { showGallery = it },
            )
            Row(modifier = Modifier.fillMaxSize()) {
                ComponentList(
                    entries = filtered,
                    selected = selectedEntry,
                    category = category,
                    query = query,
                    onCategory = { category = it; log("category: ${it?.label ?: "all"}") },
                    onSelect = { select(it, 0) },
                    modifier = Modifier.width(264.dp).fillMaxHeight(),
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .background(AqColor.Surface),
                    contentAlignment = Alignment.TopCenter,
                ) {
                    if (showGallery) {
                        Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                            AqDesignSystemGallery(darkTheme = darkTheme)
                        }
                    } else {
                        val entry = selectedEntry
                        val fixedWidth = viewportWidth
                        if (entry == null) {
                            Column(
                                modifier = Modifier.fillMaxSize().padding(AqSpacing.Xl),
                                verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                AqText(text = "Component Lab", size = AqType.HeadingLg)
                                AqText(
                                    text = "Selecciona un componente de la lista o usa #Componente en la URL.",
                                    color = AqColor.TextSecondary,
                                )
                            }
                        } else {
                            val stateIndex = selectedState.coerceIn(0, entry.states.lastIndex)
                            Column(
                                modifier = Modifier
                                    .then(if (fixedWidth != null) Modifier.width(fixedWidth) else Modifier.fillMaxWidth())
                                    .fillMaxHeight()
                                    .verticalScroll(rememberScrollState())
                                    .padding(AqSpacing.Lg),
                                verticalArrangement = Arrangement.spacedBy(AqSpacing.Md),
                            ) {
                                StateSelector(
                                    entry = entry,
                                    selectedIndex = stateIndex,
                                    onSelect = { idx ->
                                        selectedState = idx
                                        window.history.replaceState(null, "", "#${entry.id}/${entry.states[idx].id}")
                                        log("state: ${entry.id}/${entry.states[idx].id}")
                                    },
                                )
                                entry.states[stateIndex].content(log)
                            }
                        }
                    }
                }
            }
            EventLog(events = events, modifier = Modifier.fillMaxWidth().height(96.dp))
        }
        }
    }
}

@Composable
private fun LabToolbar(
    darkTheme: Boolean,
    onTheme: (Boolean) -> Unit,
    query: String,
    onQuery: (String) -> Unit,
    fontScale: Float,
    onFontScale: (Float) -> Unit,
    viewportWidth: Dp?,
    onViewport: (Dp?) -> Unit,
    showGallery: Boolean,
    onGallery: (Boolean) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(AqColor.SurfaceSecondary)
            .padding(horizontal = AqSpacing.Md, vertical = AqSpacing.Sm),
        verticalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(AqSpacing.Sm),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AqText(text = "AgendaQR Lab", size = AqType.BodyMd)
            ToolbarToggle("Claro", !darkTheme) { onTheme(false) }
            ToolbarToggle("Oscuro", darkTheme) { onTheme(true) }
            ToolbarToggle("Galería", showGallery) { onGallery(!showGallery) }
            ToolbarDivider()
            FontScaleSelector(fontScale, onFontScale)
            ToolbarDivider()
            ViewportSelector(viewportWidth, onViewport)
        }
        AqSearchInput(
            value = query,
            onValueChange = onQuery,
            label = "Buscar componente",
            placeholder = "Nombre o categoría…",
            clearLabel = "Limpiar",
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun ToolbarToggle(label: String, active: Boolean, onClick: () -> Unit) {
    AqButton(
        label = label,
        onClick = onClick,
        variant = if (active) AqButtonVariant.Primary else AqButtonVariant.Outline,
        size = AqButtonSize.Small,
    )
}

@Composable
private fun ToolbarDivider() {
    Box(modifier = Modifier.width(1.dp).height(AqSpacing.Lg).background(AqColor.Border))
}

@Composable
private fun FontScaleSelector(value: Float, onSelect: (Float) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(AqSpacing.Xs), verticalAlignment = Alignment.CenterVertically) {
        AqText(text = "Fuente", color = AqColor.TextSecondary, size = AqType.BodySm)
        FontScales.forEach { scale ->
            ToolbarToggle("${(scale * 100).toInt()}%", value == scale) { onSelect(scale) }
        }
    }
}

@Composable
private fun ViewportSelector(value: Dp?, onSelect: (Dp?) -> Unit) {
    Row(horizontalArrangement = Arrangement.spacedBy(AqSpacing.Xs), verticalAlignment = Alignment.CenterVertically) {
        AqText(text = "Viewport", color = AqColor.TextSecondary, size = AqType.BodySm)
        ViewportWidths.forEach { width ->
            val label = width?.value?.toInt()?.toString() ?: "Auto"
            ToolbarToggle("${label}dp", value == width) { onSelect(width) }
        }
    }
}

@Composable
private fun ComponentList(
    entries: List<AqComponentEntry>,
    selected: AqComponentEntry?,
    category: AqComponentCategory?,
    query: String,
    onCategory: (AqComponentCategory?) -> Unit,
    onSelect: (AqComponentEntry) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(AqColor.SurfaceSecondary)
            .verticalScroll(rememberScrollState()),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = AqSpacing.Sm, vertical = AqSpacing.Xs),
            horizontalArrangement = Arrangement.spacedBy(AqSpacing.Xs),
        ) {
            AqChip(
                text = "Todas",
                selected = category == null,
                onClick = { onCategory(null) },
                size = AqChipSize.Sm,
            )
            AqComponentCategory.entries.forEach { cat ->
                AqChip(
                    text = cat.label,
                    selected = category == cat,
                    onClick = { onCategory(cat) },
                    size = AqChipSize.Sm,
                )
            }
        }
        entries.forEach { entry ->
            val isSelected = entry == selected
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(if (isSelected) AqColor.SurfaceContainerHigh else AqColor.Transparent)
                    .clickable { onSelect(entry) }
                    .padding(horizontal = AqSpacing.Md, vertical = AqSpacing.Sm),
            ) {
                AqText(
                    text = entry.name,
                    size = AqType.BodySm,
                )
                AqText(
                    text = "${entry.category.label} · ${entry.states.size} estado(s)",
                    size = AqType.BodyXs,
                    color = AqColor.TextSecondary,
                )
            }
        }
        if (entries.isEmpty()) {
            AqText(
                text = "Sin componentes para «${category?.label ?: ""}» / «$query»",
                color = AqColor.TextSecondary,
                size = AqType.BodySm,
                modifier = Modifier.padding(AqSpacing.Md),
            )
        }
    }
}

@Composable
private fun StateSelector(
    entry: AqComponentEntry,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
) {
    if (entry.states.size <= 1) return
    Row(
        modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(AqSpacing.Xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        AqText(text = "Estado:", color = AqColor.TextSecondary, size = AqType.BodySm)
        entry.states.forEachIndexed { index, state ->
            AqChip(
                text = state.label,
                selected = index == selectedIndex,
                onClick = { onSelect(index) },
                size = AqChipSize.Sm,
            )
        }
    }
}

@Composable
private fun EventLog(events: List<String>, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(AqColor.SurfaceSecondary)
            .padding(horizontal = AqSpacing.Md, vertical = AqSpacing.Xs),
    ) {
        AqText(text = "Eventos", color = AqColor.TextSecondary, size = AqType.BodyXs)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f, fill = false)
                .verticalScroll(rememberScrollState()),
        ) {
            events.take(12).forEach { event ->
                AqText(text = event, size = AqType.BodyXs, color = AqColor.TextSecondary)
            }
        }
    }
}
