package com.agendaqr.core.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onAllNodesWithTag
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onRoot
import androidx.compose.ui.test.printToLog
import androidx.compose.ui.unit.dp
import com.agendaqr.core.ui.theme.AqColor
import com.agendaqr.core.ui.theme.AqType
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqSpacing
import com.agendaqr.core.ui.theme.AqTheme
import com.github.takahirom.roborazzi.captureRoboImage
import java.io.File
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

/**
 * Harness de evidencia visual Aq: renderiza cada componente `Aq*` en los
 * mismos escenarios que el harness Flutter de `moon_design` 1.1.0
 * (`tools/moon-reference`) y exporta PNG + métricas para el comparador
 * (`tools/moon-compare`). Solo escribe archivos con `-Pagendaqr.capture=true`.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], qualifiers = "w360dp-h740dp")
class AqComponentReferenceTest {

    @get:Rule
    val compose = createComposeRule()

    private val capture = System.getProperty("agendaqr.capture") == "true"

    private data class Scenario(
        val id: String,
        val fillWidth: Boolean = false,
        val captureScreen: Boolean = false,
        val captureTag: String? = null,
        val content: @Composable () -> Unit,
    )

    @Test
    fun captureAll() {
        if (!capture) return
        val outDir = File("build/captures/components").apply { mkdirs() }
        val metrics = StringBuilder("[")
        val only = System.getProperty("aq.only")?.split(",")?.toSet()
        val scenarios = scenarios().filter { only == null || it.id in only }
        // setContent solo puede invocarse una vez por test: una única
        // composición cuyo contenido se intercambia por estado.
        var currentDark by mutableStateOf(false)
        var current by mutableStateOf(scenarios.first())
        var currentId by mutableStateOf(scenarios.first().id)
        compose.setContent {
            AqTheme(darkTheme = currentDark) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        // Fondo de página Moon = `gohan` (= AqColor.Surface),
                        // igual que el storybook de Moon; `Background` es
                        // `goku`, reservado a superficies de widgets.
                        .background(AqColor.Surface),
                    contentAlignment = Alignment.TopStart,
                ) {
                    Box(
                        modifier = Modifier
                            .padding(AqSpacing.Lg)
                            .then(
                                if (current.fillWidth) {
                                    Modifier.width(328.dp)
                                } else {
                                    Modifier
                                },
                            ),
                    ) {
                        Box(modifier = Modifier.testTag("aq-scenario-$currentId")) {
                            // key() por escenario: sin ella el LayoutNode se
                            // reutiliza y Robolectric puede servir el bitmap
                            // cacheado del escenario anterior para contenido
                            // estático (colores/fills sin animación).
                            androidx.compose.runtime.key(currentId) {
                                current.content()
                            }
                        }
                    }
                }
            }
        }
        for (dark in listOf(false, true)) {
            val theme = if (dark) "dark" else "light"
            currentDark = dark
            for (scenario in scenarios) {
                current = scenario
                currentId = scenario.id
                compose.waitForIdle()
                // Sin vsync el frame de dibujo puede no ejecutarse: se
                // avanza el reloj para forzar measure/layout/draw antes de
                // capturar (si no, el PNG repite el último frame dibujado).
                compose.mainClock.advanceTimeByFrame()
                compose.waitForIdle()
                // captureRoboImage sobre un nodo interno reutiliza el bitmap
                // cacheado del LayoutNode entre escenarios (PNG idénticos
                // para variantes distintas). Se capturan TODAS las raíces
                // semánticas (Popup/Dialog viven en raíces propias) y se
                // componen sobre un lienzo 360×740 en sus boundsInWindow;
                // el comparador recorta a las bounds absolutas de pantalla
                // registradas en aq-metricas.json.
                val tag = scenario.captureTag ?: "aq-scenario-${scenario.id}"
                val semNodes = runCatching {
                    compose.onAllNodesWithTag(tag, useUnmergedTree = true)
                        .fetchSemanticsNodes()
                }.getOrNull().orEmpty()
                val semNode = semNodes.firstOrNull()
                if (scenario.captureScreen) {
                    val rootNodes = compose.onAllNodes(
                        SemanticsMatcher("isRoot") { it.isRoot },
                        useUnmergedTree = true,
                    ).fetchSemanticsNodes()
                    if (metrics.length > 1) metrics.append(',')
                    val outFile = File(outDir, "${scenario.id}--$theme.png")
                    // Pantalla completa fusionada (360×740): barrier +
                    // hoja/vista en una sola imagen.
                    metrics.append(
                        "{\"id\":\"${scenario.id}--$theme\",\"x\":0.0," +
                            "\"y\":0.0,\"width\":360.0,\"height\":740.0}",
                    )
                    compositeRoots(compose, rootNodes, outFile)
                } else if (semNode != null) {
                    // Captura del widget: `captureToImage` deja alfa 0 donde
                    // el widget no pinta, igual que los goldens Moon.
                    // `captureRoboImage` sobre un nodo dentro de un Popup
                    // captura la ventana principal en sus coords (el popup
                    // puede quedar fuera de pantalla en Robolectric), así
                    // que se captura la RAÍZ semántica que contiene al nodo
                    // y se recorta por `boundsInRoot` (coords de su raíz).
                    val rootSem = generateSequence(semNode) { it.parent }
                        .last { it.isRoot || it.parent == null }
                    val tmp = File(outDir, ".tmp-node.png")
                    compose.onNode(
                        SemanticsMatcher("capture root ${rootSem.id}") {
                            it.isRoot && it.id == rootSem.id
                        },
                    ).captureRoboImage(tmp.absolutePath)
                    val full = android.graphics.BitmapFactory.decodeFile(tmp.absolutePath)
                    tmp.delete()
                    val r = semNode.boundsInRoot
                    val left = r.left.toInt().coerceIn(0, full.width)
                    val top = r.top.toInt().coerceIn(0, full.height)
                    val right = r.right.toInt().coerceIn(left, full.width)
                    val bottom = r.bottom.toInt().coerceIn(top, full.height)
                    val bmp = if (right > left && bottom > top) {
                        android.graphics.Bitmap.createBitmap(
                            full, left, top, right - left, bottom - top,
                        )
                    } else {
                        full
                    }
                    if (metrics.length > 1) metrics.append(',')
                    metrics.append(
                        "{\"id\":\"${scenario.id}--$theme\",\"x\":0.0," +
                            "\"y\":0.0,\"width\":" +
                            "${semNode.boundsInWindow.width},\"height\":" +
                            "${semNode.boundsInWindow.height}}",
                    )
                    java.io.FileOutputStream(
                        File(outDir, "${scenario.id}--$theme.png"),
                    ).use {
                        bmp.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it)
                    }
                }
            }
        }
        metrics.append("]")
        File("build/captures/aq-metricas.json").writeText(metrics.toString())
    }

    /**
     * Compone todas las raíces semánticas (pantalla + Popup/Dialog) sobre un
     * lienzo 360×740. Las raíces se crean en orden de ventana: la principal
     * primero y los overlays después, así el scrim no queda oculto tras el
     * fondo opaco.
     */
    private fun compositeRoots(
        compose: androidx.compose.ui.test.junit4.ComposeContentTestRule,
        rootNodes: List<androidx.compose.ui.semantics.SemanticsNode>,
        outFile: File,
    ) {
        val screen = android.graphics.Bitmap.createBitmap(
            360, 740, android.graphics.Bitmap.Config.ARGB_8888,
        )
        val screenCanvas = android.graphics.Canvas(screen)
        val ordered = rootNodes.sortedBy { it.id }
        ordered.forEachIndexed { index, rootSem ->
            val tmp = File(outFile.parentFile, ".tmp-$index.png")
            compose.onNode(
                SemanticsMatcher("root ${rootSem.id}") {
                    it.isRoot && it.id == rootSem.id
                },
            ).captureRoboImage(tmp.absolutePath)
            val bmp = android.graphics.BitmapFactory.decodeFile(tmp.absolutePath)
            screenCanvas.drawBitmap(
                bmp,
                rootSem.boundsInWindow.left.toFloat(),
                rootSem.boundsInWindow.top.toFloat(),
                null,
            )
            tmp.delete()
        }
        java.io.FileOutputStream(outFile).use {
            screen.compress(android.graphics.Bitmap.CompressFormat.PNG, 100, it)
        }
    }

    private fun scenarios(): List<Scenario> {
        val list = mutableListOf<Scenario>()
        fun add(
            id: String,
            fillWidth: Boolean = false,
            captureScreen: Boolean = false,
            captureTag: String? = null,
            content: @Composable () -> Unit,
        ) = list.add(Scenario(id, fillWidth, captureScreen, captureTag, content))

        // Botones (variantes × tamaños × estados)
        val buttonSizes = listOf(
            "xs" to AqButtonSize.ExtraSmall,
            "sm" to AqButtonSize.Small,
            "md" to AqButtonSize.Medium,
            "lg" to AqButtonSize.Large,
            "xl" to AqButtonSize.ExtraLarge,
        )
        for ((name, size) in buttonSizes) {
            add("text-button--$name") {
                AqButton(label = "Button", onClick = {}, variant = AqButtonVariant.Text, size = size)
            }
            add("outlined-button--$name") {
                AqButton(label = "Button", onClick = {}, variant = AqButtonVariant.Outline, size = size)
            }
            add("filled-button--$name") {
                AqButton(label = "Button", onClick = {}, variant = AqButtonVariant.Primary, size = size)
            }
        }
        add("filled-button--disabled") {
            AqButton(label = "Button", onClick = {}, enabled = false, size = AqButtonSize.Medium)
        }
        add("filled-button--full") {
            Box(modifier = Modifier.fillMaxWidth()) {
                AqButton(label = "Button", onClick = {}, fullWidth = true, size = AqButtonSize.Medium)
            }
        }

        // Selección
        add("checkbox--checked") {
            AqCheckbox(label = "", checked = true, onCheckedChange = {})
        }
        add("checkbox--unchecked") {
            AqCheckbox(label = "", checked = false, onCheckedChange = {})
        }
        add("radio--selected") { AqRadio(selected = true) }
        add("radio--unselected") { AqRadio(selected = false) }
        for (size in AqSwitchSize.values()) {
            add("switch--${size.name.lowercase()}--on") {
                AqSwitch(label = "", checked = true, onCheckedChange = {}, size = size)
            }
            add("switch--${size.name.lowercase()}--off") {
                AqSwitch(label = "", checked = false, onCheckedChange = {}, size = size)
            }
        }

        // Chip / Tag / DotIndicator
        for (size in AqChipSize.values()) {
            add("chip--${size.name.lowercase()}--active") {
                AqChip(text = "Chip", selected = true, onClick = {}, size = size)
            }
            add("chip--${size.name.lowercase()}--inactive") {
                AqChip(text = "Chip", selected = false, onClick = {}, size = size)
            }
        }
        for (size in AqTagSize.values()) {
            add("tag--${size.name.lowercase()}") { AqTag(text = "Tag", size = size) }
        }
        add("dot-indicator", fillWidth = true) { AqDotIndicator(total = 4, selected = 1) }

        // Segmentado / Tabs / Breadcrumb / Accordion
        val segOptions = listOf(
            AqSegmentedOption("Todos"), AqSegmentedOption("Favs"), AqSegmentedOption("Rec"),
        )
        for (size in AqSegmentedControlSize.values()) {
            add("segmented-control--${size.name.lowercase()}", fillWidth = true) {
                AqSegmentedControl(
                    options = segOptions, selectedIndex = 0, onSelect = {}, size = size,
                )
            }
        }
        val tabs = listOf(AqTab("QR"), AqTab("Ops"), AqTab("Cmp"))
        for (size in AqTabSize.values()) {
            add("tab-bar--${size.name.lowercase()}", fillWidth = true) {
                // Moon `MoonTabBar(isExpanded: true)`.
                AqTabBar(tabs = tabs, selectedIndex = 0, onSelect = {}, size = size, isExpanded = true)
            }
            add("pill-tab--${size.name.lowercase()}", fillWidth = true) {
                // `AqPillTab` ya reparte el ancho con `weight` (= expanded).
                AqPillTab(tabs = tabs, selectedIndex = 0, onSelect = {}, size = size)
            }
        }
        add("breadcrumb") {
            AqBreadcrumb(items = listOf("Inicio", "Contextos", "Detalle"))
        }
        for (size in AqAccordionSize.values()) {
            // Moon `children: [Text('Contenido')]` hereda `contentTextStyle`
            // (body.text12 en sm, body.textDefault en el resto) vía
            // `DefaultTextStyle`; el `Text` de material3 lo reproduce.
            add("accordion--${size.name.lowercase()}--collapsed") {
                AqAccordion(title = "Sección", expanded = false, onToggle = {}, size = size, content = {
                    androidx.compose.material3.Text("Contenido")
                })
            }
            add("accordion--${size.name.lowercase()}--expanded") {
                AqAccordion(title = "Sección", expanded = true, onToggle = {}, size = size, content = {
                    androidx.compose.material3.Text("Contenido")
                })
            }
        }

        // Avatar / MenuItem / Alert
        for (size in AqAvatarSize.values()) {
            add("avatar--${size.name.lowercase()}") {
                // Moon `avatar--*`: `showBadge: true` (punto roshi).
                AqAvatar(size = size, initials = "A", showBadge = true, contentDescription = "Avatar")
            }
        }
        add("menu-item", fillWidth = true) {
            AqMenuItem(
                title = "Título",
                subtitle = "Subtítulo",
                leading = { AqIcon(AqIcons.ChevronRight, null, size = AqSize.Xs) },
                trailing = { AqIcon(AqIcons.Info, null, size = AqSize.X2s) },
                onClick = {},
            )
        }
        add("alert", fillWidth = true) {
            AqAlert(tone = com.agendaqr.core.ui.components.AqTone.Neutral, message = "Se produjo un error. Inténtalo de nuevo.")
        }
        add("alert--filled", fillWidth = true) {
            // Moon `MoonAlert.filled`: fondo tonal y texto `textPrimary`
            // (la etiqueta usa `body.textDefault` cuando no hay content).
            AqAlert(
                tone = com.agendaqr.core.ui.components.AqTone.Neutral,
                message = "Se produjo un error.",
                containerColor = AqColor.DangerContainer,
            )
        }

        // Formularios (espejo de `capture_test.dart` de Moon)
        for (size in AqTextInputSize.values()) {
            val name = size.name.lowercase()
            add("text-input--$name") {
                AqTextInput(value = "Valor", onValueChange = {}, size = size, placeholder = "Escribe aquí")
            }
            add("text-input--$name--error") {
                AqTextInput(value = "", onValueChange = {}, size = size, placeholder = "Escribe aquí", isError = true, errorMessage = "Campo obligatorio")
            }
            add("text-input--$name--hint") {
                AqTextInput(value = "", onValueChange = {}, size = size, placeholder = "Escribe aquí")
            }
        }
        add("text-area") {
            AqTextArea(value = "Nota", onValueChange = {}, placeholder = "Escribe una nota", minLines = 3)
        }
        add("text-input-group") {
            AqTextInputGroup(children = listOf(
                { AqTextInput(value = "", onValueChange = {}, placeholder = "Usuario", borderless = true) },
                {
                    AqTextInput(
                        value = "",
                        onValueChange = {},
                        placeholder = "Contraseña",
                        borderless = true,
                        visualTransformation = androidx.compose.ui.text.input.PasswordVisualTransformation(),
                    )
                },
            ))
        }
        add("auth-code") {
            AqAuthCode(value = "", onValueChange = {})
        }

        // Toast: se captura solo la tarjeta (Moon `MoonToast` es una entrada
        // de Overlay; el comparador recorta por las bounds registradas).
        add("toast--original", captureTag = "aq-toast-card") {
            Column {
                AqText(text = "fondo", size = AqType.BodySm)
                AqToast(show = true, message = "Operación guardada", durationMs = 60000)
            }
        }
        add("toast--inverted", captureTag = "aq-toast-card") {
            Column {
                AqText(text = "fondo", size = AqType.BodySm)
                AqToast(show = true, message = "Operación guardada", durationMs = 60000, variant = AqToastVariant.Inverted)
            }
        }

        // Loaders / progreso (tamaños Moon: x2s=16, xs=24, sm=32, md=40, lg=48)
        for (size in listOf("x2s" to AqSize.X2s, "xs" to AqSize.Xs, "sm" to AqSize.Sm, "md" to AqSize.Md, "lg" to AqSize.Lg)) {
            add("circular-loader--${size.first}") {
                AqCircularLoader(loaderSize = size.second)
            }
        }
        for (height in listOf(AqSize.X6s, AqSize.X5s, AqSize.X4s, AqSize.X3s, AqSize.X2s)) {
            val name = when (height) {
                AqSize.X6s -> "x6s"; AqSize.X5s -> "x5s"; AqSize.X4s -> "x4s"
                AqSize.X3s -> "x3s"; else -> "x2s"
            }
            add("linear-loader--$name") {
                AqLinearLoader(loaderHeight = height)
            }
            add("linear-progress--$name") {
                AqLinearProgress(progress = 0.6f, contentDescription = "Progreso", height = height)
            }
            add("linear-progress--$name--pin") {
                // Moon `MoonProgressPin(child: MoonLinearProgress(showPin))`:
                // el pin externo no pasa `thumbSize` → Moon pinta el pulgar
                // con radio `child.height/2` (pulgar gigante que cubre el pin
                // interno). `affectsHeight = false`: MoonProgressPin no
                // reserva altura propia.
                AqProgressPin(
                    progress = 0.6f,
                    pinText = "60%",
                    barHeight = height,
                    thumbSize = null,
                    affectsHeight = false,
                ) {
                    AqLinearProgress(
                        progress = 0.6f,
                        contentDescription = "Progreso",
                        height = height,
                        showPin = true,
                    )
                }
            }
        }
        for (size in listOf("x2s" to AqSize.X2s, "xs" to AqSize.Xs, "sm" to AqSize.Sm, "md" to AqSize.Md, "lg" to AqSize.Lg)) {
            add("circular-progress--${size.first}") {
                AqCircularProgress(progress = 0.6f, contentDescription = "Progreso", size = size.second)
            }
        }

        // Carousel / Table
        add("carousel") {
            // Moon `itemBuilder`: Container(color E8E8E8, radius 12) +
            // Text('Item i') centrado.
            Box(modifier = Modifier.height(120.dp).fillMaxWidth()) {
                AqCarousel(
                    items = (0 until 5).map { AqCarouselItem("i$it", "Item $it") },
                    itemExtent = 140.dp,
                    gap = 8.dp,
                    isCentered = true,
                ) { item ->
                    Box(
                        modifier = Modifier
                            .size(width = 140.dp, height = 120.dp)
                            .clip(androidx.compose.foundation.shape.RoundedCornerShape(12.dp))
                            // Mismo color de ítem que el harness Moon
                            // (`Color(0xFFE8E8E8)` en capture_test.dart).
                            .background(androidx.compose.ui.graphics.Color(0xFFE8E8E8)),
                        contentAlignment = Alignment.Center,
                    ) { AqText(text = item.label ?: "", size = AqType.BodySm) }
                }
            }
        }
        add("table", captureTag = "aq-table") {
            // Moon envuelve la tabla en `SizedBox(height: 200, width: 328)`.
            Box(modifier = Modifier.fillMaxWidth().height(200.dp)) {
            AqTable(
                columns = listOf(
                    AqTableColumn("c0", "Col 0"), AqTableColumn("c1", "Col 1"), AqTableColumn("c2", "Col 2"),
                ),
                rows = (0 until 3).map { r ->
                    AqTableRow("r$r", mapOf("c0" to "R$r C0", "c1" to "R$r C1", "c2" to "R$r C2"))
                },
                rowSize = AqTableRowSize.Sm,
                modifier = Modifier.testTag("aq-table"),
            )
            }
        }

        // Overlays / superficies
        add("drawer", captureTag = "aq-drawer-pane") {
            // Moon `SizedBox(280×400, MoonDrawer(child: Column[4 items]))`:
            // el drawer es solo superficie; sin título ni acciones.
            Box(modifier = Modifier.size(width = 280.dp, height = 400.dp)) {
                AqDrawer(onDismissRequest = {}) {
                    Column {
                        repeat(4) { i -> AqMenuItem(title = "Opción $i", onClick = {}) }
                    }
                }
            }
        }
        add("dropdown", captureTag = "aq-dropdown-popup") {
            // Las ventanas `Popup` no rasterizan bajo Robolectric: se
            // renderiza inline la misma tarjeta que el Popup muestra.
            AqDropdownSurface(
                maxWidth = aqAnchorMaxWidthDp(55.5f),
                options = listOf("Opción 1", "Opción 2"),
                selected = -1,
                onOptionClick = {},
                content = {},
            )
        }
        add("popover", captureTag = "aq-popover-popup") {
            // Igual que dropdown: la tarjeta se renderiza inline (el `Popup`
            // no rasteriza bajo Robolectric). Moon `content`:
            // `Container(padding: all(12), Text(...))` — el padding interno
            // se suma al `contentPadding` (12) de la tarjeta.
            // Ancla = botón outlined md "Abrir" en x=16 (padding del
            // harness), ancho 79 → centro ≈ 55.5 px, como en el escenario
            // Moon (mismo botón). De ahí sale el `maxWidth` que fuerza el
            // wrap del texto.
            AqPopoverSurface(maxWidth = aqAnchorMaxWidthDp(55.5f)) {
                Box(modifier = Modifier.padding(AqSize.X3s)) {
                    AqText(text = "Contenido del popover", size = AqType.BodySm)
                }
            }
        }
        add("tooltip", captureTag = "aq-tooltip") {
            AqTooltip(label = "Tooltip", isVisible = true) {
                AqButton(label = "Hover", onClick = {}, variant = AqButtonVariant.Outline, size = AqButtonSize.Medium)
            }
        }
        add("modal", captureTag = "aq-modal-pane") {
            // Moon `MoonModal(child: Container(width 300, padding 20,
            // Column[Título 18, gap 12, Contenido]))`: tarjeta sin
            // título/acciones propias.
            AqModal(onDismissRequest = {}) {
                Column {
                    AqText(text = "Título del modal", size = AqType.BodyLg)
                    androidx.compose.foundation.layout.Spacer(Modifier.height(12.dp))
                    AqText(text = "Contenido del diálogo.", size = AqType.BodySm)
                }
            }
        }
        add("bottom-sheet", captureScreen = true) {
            // Moon `showMoonModalBottomSheet` con `Container(height 300)`
            // blanco y dos `MoonMenuItem`: hoja sin handle/título/acciones,
            // precedida del botón «Abrir sheet» que la dispara.
            var open by remember { mutableStateOf(true) }
            Column {
                AqButton(
                    label = "Abrir sheet",
                    onClick = { open = false },
                    variant = AqButtonVariant.Outline,
                    size = AqButtonSize.Medium,
                )
                if (open) {
                    AqBottomSheet(
                        onDismissRequest = {},
                        showHandle = false,
                        height = 300.dp,
                    ) {
                        Column {
                            AqMenuItem(title = "Opción 1", onClick = {})
                            AqMenuItem(title = "Opción 2", onClick = {})
                        }
                    }
                }
            }
        }
        return list
    }
}
