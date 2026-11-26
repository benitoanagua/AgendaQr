package com.agendaqr.core.ui.fidelity

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.agendaqr.core.ui.components.AqAccordion
import com.agendaqr.core.ui.components.AqAccordionSize
import com.agendaqr.core.ui.components.AqAlert
import com.agendaqr.core.ui.components.AqAuthCode
import com.agendaqr.core.ui.components.AqAvatar
import com.agendaqr.core.ui.components.AqAvatarSize
import com.agendaqr.core.ui.components.AqButton
import com.agendaqr.core.ui.components.AqButtonSize
import com.agendaqr.core.ui.components.AqCheckbox
import com.agendaqr.core.ui.components.AqChip
import com.agendaqr.core.ui.components.AqChipSize
import com.agendaqr.core.ui.components.AqCircularLoader
import com.agendaqr.core.ui.components.AqCombobox
import com.agendaqr.core.ui.components.AqDrawer
import com.agendaqr.core.ui.components.AqLinearProgress
import com.agendaqr.core.ui.components.AqMenuItem
import com.agendaqr.core.ui.components.AqPagination
import com.agendaqr.core.ui.components.AqPillTab
import com.agendaqr.core.ui.components.AqProgress
import com.agendaqr.core.ui.components.AqProgressBar
import com.agendaqr.core.ui.components.AqRadio
import com.agendaqr.core.ui.components.AqSearchInput
import com.agendaqr.core.ui.components.AqSegmentedControl
import com.agendaqr.core.ui.components.AqSegmentedControlSize
import com.agendaqr.core.ui.components.AqSegmentedOption
import com.agendaqr.core.ui.components.AqSelect
import com.agendaqr.core.ui.components.AqSidebar
import com.agendaqr.core.ui.components.AqSidebarItem
import com.agendaqr.core.ui.components.AqSwitch
import com.agendaqr.core.ui.components.AqSwitchSize
import com.agendaqr.core.ui.components.AqTab
import com.agendaqr.core.ui.components.AqTabBar
import com.agendaqr.core.ui.components.AqTabSize
import com.agendaqr.core.ui.components.AqTag
import com.agendaqr.core.ui.components.AqTagSize
import com.agendaqr.core.ui.components.AqTextArea
import com.agendaqr.core.ui.components.AqTextInput
import com.agendaqr.core.ui.components.AqTextInputSize
import com.agendaqr.core.ui.components.AqTone
import com.agendaqr.core.ui.theme.AqLayout
import com.agendaqr.core.ui.theme.AqSize
import com.agendaqr.core.ui.theme.AqTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Capa renderizada de la fidelidad Moon v1 (ADR-0050): compone cada
 * componente `Aq*` en su tamaño del fixture y mide los bounds reales del
 * nodo (tag semántico o caja envolvente). Complementa a
 * [MoonSpecFidelityTest], que compara tokens; aquí se detecta cualquier
 * desviación entre el token y lo que realmente se dibuja (padding,
 * expansión táctil, alturas fijas).
 *
 * Solo se miden las métricas invariantes: el ancho de los componentes con
 * etiqueta depende del texto y no se compara; la altura de los componentes
 * cuyo alto depende del contenido (placeholders, tablas, overlays) queda
 * cubierta por la prueba de tokens o por las pruebas de geometría existentes.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class MoonSpecRenderedTest {

    @get:Rule
    val compose = createComposeRule()

    /** Probe renderizable de una fila del fixture. */
    private class Probe(
        val content: @Composable () -> Unit,
        /** Tag del nodo a medir; `null` = caja envolvente. */
        val tag: String? = null,
        val checkWidth: Boolean = false,
        val checkHeight: Boolean = true,
        /** `true` = el spec es un mínimo (`>=`), no un valor exacto. */
        val minHeight: Boolean = false,
    )

    private fun dpOf(label: String): Dp = when (label) {
        "6xs" -> AqSize.X6s
        "5xs" -> AqSize.X5s
        "4xs" -> AqSize.X4s
        "3xs" -> AqSize.X3s
        "2xs" -> AqSize.X2s
        "xs" -> AqSize.Xs
        "sm" -> AqSize.Sm
        "md" -> AqSize.Md
        "lg" -> AqSize.Lg
        "xl" -> AqSize.Xl
        "2xl" -> AqSize.X2l
        else -> throw IllegalArgumentException("tamaño Moon desconocido: '$label'")
    }

    private fun buttonSize(size: String) = when (size) {
        "xs" -> AqButtonSize.ExtraSmall
        "sm" -> AqButtonSize.Small
        "md" -> AqButtonSize.Medium
        "lg" -> AqButtonSize.Large
        else -> AqButtonSize.ExtraLarge
    }

    private fun inputSize(size: String) = when (size) {
        "sm" -> AqTextInputSize.Sm
        "lg" -> AqTextInputSize.Lg
        "xl" -> AqTextInputSize.Xl
        else -> AqTextInputSize.Md
    }

    private fun probe(aq: String, size: String): Probe? = when (aq) {
        "AqAccordion" -> Probe(
            content = {
                AqAccordion(
                    title = "Sección",
                    expanded = false,
                    onToggle = {},
                    content = {},
                    size = when (size) {
                        "sm" -> AqAccordionSize.Sm
                        "lg" -> AqAccordionSize.Lg
                        "xl" -> AqAccordionSize.Xl
                        else -> AqAccordionSize.Md
                    },
                )
            },
            checkWidth = false,
        )

        "AqAlert" -> Probe(
            content = { AqAlert(tone = AqTone.Info, message = "Mensaje de alerta") },
            minHeight = true,
        )

        "AqAuthCode" -> Probe(
            content = { AqAuthCode(value = "", onValueChange = {}) },
            checkWidth = false,
        )

        "AqAvatar" -> Probe(
            content = {
                AqAvatar(
                    contentDescription = "Avatar",
                    size = when (size) {
                        "xs" -> AqAvatarSize.Xs
                        "sm" -> AqAvatarSize.Sm
                        "lg" -> AqAvatarSize.Lg
                        "xl" -> AqAvatarSize.Xl
                        "2xl" -> AqAvatarSize.X2l
                        else -> AqAvatarSize.Md
                    },
                )
            },
            tag = "aq-avatar",
            checkWidth = true,
        )

        "AqButton" -> Probe(
            content = {
                AqButton(label = "Guardar", onClick = {}, size = buttonSize(size))
            },
            tag = "aq-button-visual",
            checkWidth = false,
        )

        "AqCheckbox" -> Probe(
            content = { AqCheckbox(label = "", checked = false, onCheckedChange = {}) },
            tag = "aq-checkbox-glyph",
            checkWidth = true,
        )

        "AqChip" -> Probe(
            content = {
                AqChip(
                    text = "Filtro",
                    size = if (size == "sm") AqChipSize.Sm else AqChipSize.Md,
                )
            },
            checkWidth = false,
        )

        "AqCircularLoader" -> Probe(
            content = { AqCircularLoader(loaderSize = dpOf(size)) },
            checkWidth = true,
        )

        "AqCombobox" -> Probe(
            content = {
                AqCombobox(
                    query = "",
                    onQueryChange = {},
                    options = listOf("Opción A", "Opción B"),
                    selectedIndices = emptySet(),
                    onToggle = {},
                    size = inputSize(size),
                    expanded = false,
                )
            },
            tag = "aq-text-input-visual",
            checkWidth = false,
        )

        "AqDrawer" -> Probe(
            content = {
                // El cajón ocupa `min(448, ancho)`: el marco debe ser ≥448
                // para medir el ancho nominal de la especificación.
                Box(Modifier.width(500.dp).height(600.dp)) {
                    AqDrawer(show = true, onDismissRequest = {}, content = {})
                }
            },
            tag = "aq-drawer-pane",
            // `min(448, pantalla)`: el entorno de prueba (320 dp) no puede
            // medir el ancho nominal; el token `DrawerWidth` lo cubre.
            checkWidth = false,
            checkHeight = false,
        )

        "AqLinearProgress" -> Probe(
            content = {
                AqLinearProgress(
                    progress = 0.4f,
                    contentDescription = "Progreso",
                    height = dpOf(size),
                )
            },
            checkWidth = false,
        )

        "AqMenuItem" -> Probe(
            content = { AqMenuItem(title = "Elemento") },
            // El spec (40) es el mínimo de una línea; Robolectric infla las
            // métricas de fuente, así que se afirma como suelo, no exacto.
            minHeight = true,
            checkWidth = false,
        )

        "AqPagination" -> Probe(
            content = {
                AqPagination(
                    pageCount = 5,
                    currentPage = 0,
                    onPageSelected = {},
                    previousContentDescription = "Anterior",
                    nextContentDescription = "Siguiente",
                    pageContentDescription = { "Página $it" },
                )
            },
            checkWidth = false,
        )

        "AqPillTab" -> Probe(
            content = {
                AqPillTab(
                    tabs = listOf(AqTab("Pestaña")),
                    selectedIndex = 0,
                    onSelect = {},
                    size = if (size == "sm") AqTabSize.Sm else AqTabSize.Md,
                )
            },
            // El fixture mide la pestaña; la pista del contenedor añade su
            // propio padding (`SegmentedControlPadding`) fuera de spec.
            tag = "aq-tab-item",
            checkWidth = false,
        )

        "AqProgressBar" -> Probe(
            content = { AqProgressBar(AqProgress(0.4f), contentDescription = "Progreso") },
            checkWidth = false,
        )

        "AqRadio" -> Probe(
            content = { AqRadio(selected = false) },
            tag = "aq-radio-glyph",
            checkWidth = true,
        )

        "AqSearchInput" -> Probe(
            content = {
                AqSearchInput(
                    label = "Buscar",
                    value = "",
                    onValueChange = {},
                    size = inputSize(size),
                )
            },
            tag = "aq-text-input-visual",
            checkWidth = false,
        )

        "AqSegmentedControl" -> Probe(
            content = {
                AqSegmentedControl(
                    options = listOf(
                        AqSegmentedOption("Uno"),
                        AqSegmentedOption("Dos"),
                    ),
                    selectedIndex = 0,
                    onSelect = {},
                    size = if (size == "sm") {
                        AqSegmentedControlSize.Sm
                    } else {
                        AqSegmentedControlSize.Md
                    },
                )
            },
            checkWidth = false,
        )

        "AqSelect" -> Probe(
            content = {
                AqSelect(
                    options = listOf("Opción A", "Opción B"),
                    selectedIndex = 0,
                    onSelected = {},
                    size = inputSize(size),
                )
            },
            tag = "aq-select-visual",
            checkWidth = false,
        )

        "AqSidebar" -> Probe(
            content = {
                Box(Modifier.width(400.dp)) {
                    AqSidebar(
                        items = listOf(AqSidebarItem("Inicio"), AqSidebarItem("Ajustes")),
                        selectedIndex = 0,
                        onItemSelected = {},
                    )
                }
            },
            // Igual que el drawer: el ancho fijo (280) ya lo afirma el
            // token `SidebarWidthExpanded` en la prueba de tokens.
            checkWidth = false,
            checkHeight = false,
        )

        "AqSwitch" -> Probe(
            content = {
                AqSwitch(
                    label = "",
                    checked = false,
                    onCheckedChange = {},
                    size = when (size) {
                        "2xs" -> AqSwitchSize.X2s
                        "xs" -> AqSwitchSize.Xs
                        else -> AqSwitchSize.Sm
                    },
                )
            },
            tag = "aq-switch-track",
            checkWidth = true,
        )

        "AqTabBar" -> Probe(
            content = {
                AqTabBar(
                    tabs = listOf(AqTab("Pestaña")),
                    selectedIndex = 0,
                    onSelect = {},
                    size = if (size == "sm") AqTabSize.Sm else AqTabSize.Md,
                )
            },
            tag = "aq-tab-item",
            checkWidth = false,
        )

        "AqTag" -> Probe(
            content = {
                AqTag(
                    text = "Estado",
                    size = if (size == "2xs") AqTagSize.X2s else AqTagSize.Xs,
                )
            },
            checkWidth = false,
        )

        "AqTextArea" -> Probe(
            content = { AqTextArea(value = "", onValueChange = {}) },
            tag = "aq-field-touch-target",
            checkWidth = false,
        )

        "AqTextInput" -> Probe(
            content = {
                AqTextInput(
                    value = "",
                    onValueChange = {},
                    size = inputSize(size),
                )
            },
            tag = "aq-text-input-visual",
            checkWidth = false,
        )

        else -> null
    }

    @Test
    fun rendered_bounds_match_spec() {
        val violations = mutableListOf<String>()
        var density by mutableFloatStateOf(1f)
        var current by mutableStateOf<Probe?>(null)
        // Una sola composición: el probe activo se intercambia por estado.
        compose.setContent {
            AqTheme {
                density = LocalDensity.current.density
                current?.let { probe ->
                    Box(Modifier.width(360.dp)) {
                        Box(Modifier.testTag("moon-spec-wrap")) {
                            probe.content()
                        }
                    }
                }
            }
        }
        MoonSpecFixtures.rows.forEach { row ->
            val probe = probe(row.aq, row.size) ?: return@forEach
            current = probe
            compose.waitForIdle()
            val node = if (probe.tag != null) {
                compose.onNodeWithTag(probe.tag, useUnmergedTree = true)
                    .fetchSemanticsNode()
            } else {
                compose.onNodeWithTag("moon-spec-wrap", useUnmergedTree = true)
                    .fetchSemanticsNode()
            }
            val bounds = node.boundsInRoot
            val measuredW = bounds.width.toInt()
            val measuredH = bounds.height.toInt()

            fun check(metric: String, specDp: Int, px: Int, min: Boolean = false) {
                if (specDp == -1) return
                val actualDp = (px / density).toInt()
                val ok = if (min) actualDp >= specDp else actualDp == specDp
                if (!ok) {
                    violations += "${row.aq} '${row.size}' $metric: spec=$specDp " +
                        "render=${actualDp}dp (${row.variant})"
                }
            }
            if (probe.checkWidth) check("width", row.width, measuredW)
            if (probe.checkHeight) check("height", row.height, measuredH, probe.minHeight)
        }
        assertTrue(
            "Desviaciones renderizadas Moon v1 (${violations.size}):\n" +
                violations.joinToString("\n") { "  $it" },
            violations.isEmpty(),
        )
    }
}
