package com.agendaqr.core.ui.fidelity

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

/**
 * Fidelidad Moon v1 generada desde el JSON del Figma (ADR-0050): cada fila
 * canónica de [MoonSpecFixtures] (altura, radio, padding, gap y borde en
 * reposo, tolerancia 0 dp) se compara con las métricas que el componente
 * `Aq*` produce a través de sus tokens ([MoonSpecActuals]). Una fila con -1
 * indica métrica no especificada por el extractor y no se compara; un
 * `Actual` con campo `null` marca la métrica como no invariante del
 * componente.
 */
@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class MoonSpecFidelityTest {

    /**
     * Desviaciones deliberadas y documentadas de la spec Moon v1: el
     * componente `Aq*` no reproduce la métrica indicada porque una ADR
     * posterior fija otra geometría. La prueba sigue verificando todas
     * las demás métricas de la fila; la desviación no se "corrige" hacia
     * el spec ni se oculta: queda registrada aquí y en la ADR citada.
     */
    private val documentedDeviations: Map<String, Set<String>> = mapOf(
        // ADR-0054 / D-13: el switch es un rectángulo redondeado (radio =
        // un cuarto del alto), no la cápsula `radius=999` del spec Moon.
        "AqSwitch" to setOf("radius"),
    )

    @Test
    fun every_fixture_row_matches_component_geometry() {
        val violations = mutableListOf<String>()
        MoonSpecFixtures.rows.forEach { row ->
            val actual = MoonSpecActuals.probe(row.aq, row.size)
            if (actual == null) {
                violations += "${row.aq} (${row.family}, size='${row.size}'): sin probe de fidelidad"
                return@forEach
            }
            val deviations = documentedDeviations[row.aq].orEmpty()
            fun check(metric: String, expected: Int, actualValue: Int?) {
                if (expected == -1 || actualValue == null || metric in deviations) return
                val ok = if (metric == "radius" && (expected >= 900 || actualValue >= 900)) {
                    expected >= 900 && actualValue >= 900
                } else {
                    expected == actualValue
                }
                if (!ok) {
                    violations += "${row.aq} ${row.size.ifEmpty { "-" }} $metric: " +
                        "spec=$expected (${row.variant}) != aq=$actualValue"
                }
            }
            check("width", row.width, actual.width)
            check("height", row.height, actual.height)
            check("radius", row.radius, actual.radius)
            check("padTop", row.padTop, actual.padTop)
            check("padRight", row.padRight, actual.padRight)
            check("padBottom", row.padBottom, actual.padBottom)
            check("padLeft", row.padLeft, actual.padLeft)
            check("gap", row.gap, actual.gap)
            check("border", row.border, actual.border)
        }
        org.junit.Assert.assertTrue(
            "Desviaciones de fidelidad Moon v1 (${violations.size}):\n" +
                violations.joinToString("\n") { "  $it" },
            violations.isEmpty(),
        )
    }
}
