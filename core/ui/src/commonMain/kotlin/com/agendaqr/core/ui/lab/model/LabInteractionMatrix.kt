package com.agendaqr.core.ui.lab.model

/**
 * Canonical interaction lenses reviewed by the lab for every component.
 *
 * The lens set is adapted to AgendaQr touch targets (Android/iOS): there is no
 * hover lens because the product has no pointer-hover surface, and transient
 * states that the component API cannot force deterministically (focus, press)
 * are reviewed through the live preview and marked as simulated.
 */
enum class LabInteractionLens(val label: String, val description: String) {
    NORMAL("Normal", "Presentación base habilitada."),
    FOCUS("Foco", "Tratamiento de foco de teclado o accesibilidad."),
    PRESSED("Presionado", "Tratamiento visual de pulsación."),
    DISABLED("Deshabilitado", "Tratamiento no interactivo."),
    LOADING("Carga", "Tratamiento de espera o progreso."),
}

/** Honest verdict for a lens applied to a component. */
enum class LabLensVerdict(val label: String) {
    REAL("Estado real"),
    SIMULATED("Lente simulada"),
    NOT_APPLICABLE("No aplica"),
}

object LabInteractionMatrix {

    val lenses: List<LabInteractionLens> = LabInteractionLens.entries

    private val pointerEvents = setOf("onClick", "onAction")
    private val textEvents = pointerEvents + setOf("onValueChange")

    private fun LabComponentContract.hasEvent(names: Set<String>): Boolean =
        events.any { it.name in names }

    /** Declared states that the current API actually offers. */
    private fun LabComponentContract.realStates(): List<LabState> =
        states.filter { it.status != LabReviewStatus.NOT_SUPPORTED }

    private fun LabComponentContract.hasRealState(vararg names: String): Boolean =
        realStates().any { state -> names.any { it.equals(state.name, ignoreCase = true) } }

    /**
     * A lens is only applicable when it is meaningful for the component: a
     * passive container must not manufacture pressed or focus states, and
     * Disabled / Loading require a state the API really offers.
     */
    fun isApplicable(contract: LabComponentContract, lens: LabInteractionLens): Boolean = when (lens) {
        LabInteractionLens.NORMAL -> contract.realStates().isNotEmpty()
        LabInteractionLens.FOCUS -> contract.hasEvent(textEvents) || contract.hasRealState("Foco", "Focused")
        LabInteractionLens.PRESSED -> contract.hasEvent(pointerEvents) || contract.hasRealState("Presionado")
        LabInteractionLens.DISABLED -> contract.hasRealState("Deshabilitado", "Disabled")
        LabInteractionLens.LOADING -> contract.hasRealState("Carga indeterminada", "Carga", "Loading")
    }

    /**
     * Resolves the declared state that matches a lens, if any. Only states the
     * API actually offers count: a "Deshabilitado" documented as
     * NOT_SUPPORTED must not upgrade the Disabled lens to a real state.
     */
    fun exactStateFor(contract: LabComponentContract, lens: LabInteractionLens): LabState? {
        val candidates = when (lens) {
            LabInteractionLens.NORMAL -> listOf(
                "Default", "Estático", "Interactivo", "Neutro", "Peligro", "Marcado", "Sin marcar",
                "Con acción contextual", "QR renderizado", "Placeholder", "Carga indeterminada",
                "Paleta clara", "Escala completa", "Métricas de control y foco", "Escala tipográfica",
            )
            LabInteractionLens.FOCUS -> listOf("Foco", "Focused")
            LabInteractionLens.PRESSED -> listOf("Presionado")
            LabInteractionLens.DISABLED -> listOf("Deshabilitado", "Disabled")
            LabInteractionLens.LOADING -> listOf("Carga indeterminada", "Carga", "Loading")
        }
        return contract.realStates().firstOrNull { state ->
            candidates.any { it.equals(state.name, ignoreCase = true) }
        }
    }

    /**
     * Honest verdict per lens:
     * - NOT_APPLICABLE when the lens is not meaningful for the component;
     * - REAL when the API offers a declared state for the lens;
     * - SIMULATED for transient focus/press, which are observable in the live
     *   preview but cannot be forced through the public API.
     */
    fun verdict(contract: LabComponentContract, lens: LabInteractionLens): LabLensVerdict {
        if (!isApplicable(contract, lens)) return LabLensVerdict.NOT_APPLICABLE
        if (exactStateFor(contract, lens) != null) return LabLensVerdict.REAL
        return when (lens) {
            LabInteractionLens.FOCUS,
            LabInteractionLens.PRESSED,
            LabInteractionLens.NORMAL,
            -> LabLensVerdict.SIMULATED
            LabInteractionLens.DISABLED,
            LabInteractionLens.LOADING,
            -> LabLensVerdict.NOT_APPLICABLE
        }
    }

    /** Applicable lenses for a component, used by the inspector matrix. */
    fun applicableLenses(contract: LabComponentContract): List<LabInteractionLens> =
        lenses.filter { isApplicable(contract, it) }
}
