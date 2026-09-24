package com.agendaqr.core.ui.lab.model

/**
 * Puerta de accesibilidad a nivel de contrato, adaptada del proyecto de
 * referencia (`WwAccessibilityGate`). No sustituye pruebas con lector de
 * pantalla: verifica que el contrato declare lo necesario (callback,
 * objetivo táctil y nombre accesible) antes de darlo por listo.
 */
enum class LabAccessibilityGateStatus { Pass, Fail }

data class LabAccessibilityGateResult(
    val status: LabAccessibilityGateStatus,
    val checks: List<String>,
    val failures: List<String>,
) {
    val passed: Boolean get() = status == LabAccessibilityGateStatus.Pass
}

object LabAccessibilityGate {

    fun evaluate(contract: LabComponentContract): LabAccessibilityGateResult {
        val checks = mutableListOf<String>()
        val failures = mutableListOf<String>()

        checks += "contract-completeness"
        if (contract.states.isEmpty()) failures += "el contrato no declara estados"

        if (contract.events.isNotEmpty()) {
            checks += "interactive-callback"
            if (contract.props.none { it.name.startsWith("on") }) {
                failures += "componente interactivo sin parámetro de callback on*"
            }
        }

        if (contract.category == LabCategory.ACTIONS && contract.events.isNotEmpty()) {
            checks += "touch-target"
            if (contract.tokens.none { it.token == "XauxaMetrics.ControlMinSize" }) {
                failures += "acción interactiva sin token XauxaMetrics.ControlMinSize (mínimo 48dp)"
            }
        }

        if (contract.name.contains("Icon", ignoreCase = true)) {
            checks += "content-description"
            val declared = contract.props.any {
                it.name.equals("contentDescription", ignoreCase = true) ||
                    it.name.equals("label", ignoreCase = true)
            }
            if (!declared) failures += "contentDescription o label requerido pero no expuesto"
        }

        if (contract.name.contains("Input", ignoreCase = true) ||
            contract.name.contains("Search", ignoreCase = true)
        ) {
            checks += "accessible-name"
            val declared = contract.props.any {
                it.name.equals("label", ignoreCase = true) ||
                    it.name.equals("contentDescription", ignoreCase = true)
            }
            if (!declared) failures += "origen de nombre accesible (label/contentDescription) requerido"
        }

        return LabAccessibilityGateResult(
            status = if (failures.isEmpty()) LabAccessibilityGateStatus.Pass else LabAccessibilityGateStatus.Fail,
            checks = checks,
            failures = failures,
        )
    }
}
