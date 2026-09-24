package com.agendaqr.core.ui.lab.model

/**
 * Integrity rules for the lab catalog. The same rules run in the unit tests
 * and back the integrity indicator in the lab header, so the UI can never
 * present a catalog that the tests would reject.
 */
object LabCatalogIntegrity {

    fun validate(catalog: List<LabComponentContract> = LabComponentCatalog.all): List<String> = buildList {
        val ids = catalog.map { it.id }
        val duplicated = ids.groupBy { it }.filterValues { it.size > 1 }.keys
        if (duplicated.isNotEmpty()) add("identificadores duplicados: ${duplicated.sorted().joinToString()}")

        catalog.forEach { contract ->
            val where = contract.id
            if (contract.name.isBlank()) add("$where: nombre vacío")
            if (contract.purpose.isBlank()) add("$where: propósito vacío")
            if (contract.description.isBlank()) add("$where: descripción vacía")
            if (contract.states.isEmpty()) add("$where: sin estados declarados")
            contract.states.forEach { state ->
                if (state.name.isBlank()) add("$where: estado sin nombre")
                if (state.description.isBlank()) add("$where ${state.name}: estado sin descripción")
            }
            contract.states.map { it.name }.groupBy { it }.filterValues { it.size > 1 }.keys.forEach {
                add("$where: estado duplicado '$it'")
            }
            if (contract.usage.isEmpty()) add("$where: sin ejemplos de uso en AgendaQr")
            if (contract.category !in LabCategory.entries) add("$where: categoría inválida")
            contract.tokens.forEach { token ->
                if (!XauxaTokenIndex.isValid(token.token)) add("$where: token inexistente '${token.token}'")
                if (token.role.isBlank()) add("$where '${token.token}': rol vacío")
            }
            if (contract.category == LabCategory.ACTIONS && contract.events.isEmpty()) {
                add("$where: una acción debe declarar al menos un evento")
            }
            val declaresInteractivity = contract.states.any {
                it.name.equals("Interactivo", ignoreCase = true) && it.status != LabReviewStatus.NOT_SUPPORTED
            }
            if (declaresInteractivity && contract.events.isEmpty()) {
                add("$where: declara estado interactivo pero no expone eventos")
            }
            if (contract.events.isNotEmpty() && contract.props.none { it.name.startsWith("on") }) {
                add("$where: eventos sin parámetro de callback en props")
            }
        }
    }
}
