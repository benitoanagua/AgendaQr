/**
 * UX flow contracts: explicit, machine-checkable state graphs for the highest-risk
 * interaction patterns. This validates the contract graph itself; runtime behavior
 * and device-level journeys still require local integration tests.
 */
data class UxTransition(
    val from: String,
    val event: String,
    val to: String,
)

data class UxFlowContract(
    val id: String,
    val title: String,
    val initial: String,
    val states: Set<String>,
    val terminal: Set<String>,
    val transitions: List<UxTransition>,
    val invariantIds: Set<String>,
)

object UxTransitionContracts {
    val all: List<UxFlowContract> = listOf(
        UxFlowContract(
            id = "UX-SAVE",
            title = "Guardar de forma idempotente",
            initial = "editing",
            states = setOf("editing", "saving", "saved", "error"),
            terminal = setOf("saved"),
            transitions = listOf(
                UxTransition("editing", "save", "saving"),
                UxTransition("saving", "save-success", "saved"),
                UxTransition("saving", "save-failure", "error"),
                UxTransition("error", "retry", "saving"),
                UxTransition("error", "edit", "editing"),
            ),
            invariantIds = setOf("UXI-001", "UXI-002", "UXI-003"),
        ),
        UxFlowContract(
            id = "UX-CANCEL",
            title = "Cancelar edición sin perder el estado previo",
            initial = "editing",
            states = setOf("editing", "confirm-discard", "discarded", "saved"),
            terminal = setOf("discarded", "saved"),
            transitions = listOf(
                UxTransition("editing", "cancel-with-changes", "confirm-discard"),
                UxTransition("editing", "cancel-without-changes", "discarded"),
                UxTransition("confirm-discard", "confirm", "discarded"),
                UxTransition("confirm-discard", "keep-editing", "editing"),
                UxTransition("editing", "save", "saved"),
            ),
            invariantIds = setOf("UXI-004", "UXI-005"),
        ),
        UxFlowContract(
            id = "UX-PERMISSION",
            title = "Permiso denegado con recuperación",
            initial = "ready",
            states = setOf("ready", "requesting", "granted", "denied", "settings", "unavailable"),
            terminal = setOf("granted", "unavailable"),
            transitions = listOf(
                UxTransition("ready", "request", "requesting"),
                UxTransition("requesting", "allow", "granted"),
                UxTransition("requesting", "deny", "denied"),
                UxTransition("denied", "retry", "requesting"),
                UxTransition("denied", "open-settings", "settings"),
                UxTransition("settings", "permission-restored", "granted"),
                UxTransition("settings", "return-without-permission", "denied"),
                UxTransition("ready", "feature-unavailable", "unavailable"),
            ),
            invariantIds = setOf("UXI-006", "UXI-007"),
        ),
        UxFlowContract(
            id = "UX-IMPORT",
            title = "Importación parcial recuperable",
            initial = "selecting",
            states = setOf("selecting", "processing", "review", "partial-error", "saved", "cancelled"),
            terminal = setOf("saved", "cancelled"),
            transitions = listOf(
                UxTransition("selecting", "import", "processing"),
                UxTransition("processing", "all-valid", "review"),
                UxTransition("processing", "some-invalid", "partial-error"),
                UxTransition("partial-error", "continue-valid", "review"),
                UxTransition("partial-error", "retry-invalid", "processing"),
                UxTransition("review", "save", "saved"),
                UxTransition("selecting", "cancel", "cancelled"),
                UxTransition("review", "cancel", "cancelled"),
                UxTransition("partial-error", "cancel", "cancelled"),
            ),
            invariantIds = setOf("UXI-008", "UXI-009", "UXI-010"),
        ),
        UxFlowContract(
            id = "UX-DELETE",
            title = "Confirmación explícita de acción destructiva",
            initial = "idle",
            states = setOf("idle", "confirming", "deleting", "deleted", "error"),
            terminal = setOf("deleted"),
            transitions = listOf(
                UxTransition("idle", "request-delete", "confirming"),
                UxTransition("confirming", "confirm", "deleting"),
                UxTransition("confirming", "cancel", "idle"),
                UxTransition("deleting", "success", "deleted"),
                UxTransition("deleting", "failure", "error"),
                UxTransition("error", "retry", "deleting"),
                UxTransition("error", "cancel", "idle"),
            ),
            invariantIds = setOf("UXI-011", "UXI-012"),
        ),
    )

    val invariantDescriptions: Map<String, String> = mapOf(
        "UXI-001" to "Guardar repetidamente no crea duplicados.",
        "UXI-002" to "Mientras se guarda, la acción no inicia una segunda operación.",
        "UXI-003" to "El error conserva los datos y permite reintentar.",
        "UXI-004" to "Cancelar con cambios pide confirmación explícita.",
        "UXI-005" to "Volver a editar desde la confirmación conserva el borrador.",
        "UXI-006" to "La denegación no bloquea el resto de la aplicación.",
        "UXI-007" to "El usuario puede reintentar o abrir ajustes cuando corresponda.",
        "UXI-008" to "Los elementos válidos se pueden guardar aunque otros fallen.",
        "UXI-009" to "Los elementos inválidos identifican el problema y ofrecen recuperación.",
        "UXI-010" to "Cancelar no persiste los elementos del lote.",
        "UXI-011" to "La acción destructiva requiere confirmación antes de ejecutarse.",
        "UXI-012" to "El fallo de borrado es recuperable y no se comunica éxito falso.",
    )
}

/** Structural validation: state references, unique edges, reachability and invariants. */
fun uxTransitionContractViolations(
    contracts: List<UxFlowContract>,
): List<String> = buildList {
    val ids = contracts.map { it.id }
    if (ids.size != ids.toSet().size) add("ux-flow-id-duplicado: hay IDs repetidos")
    contracts.forEach { flow ->
        if (flow.initial !in flow.states) add("${flow.id}: estado inicial no declarado")
        if (flow.terminal.isEmpty() || !flow.terminal.all { it in flow.states }) {
            add("${flow.id}: estados terminales ausentes o no declarados")
        }
        val edgeKeys = flow.transitions.map { "${it.from}|${it.event}|${it.to}" }
        if (edgeKeys.size != edgeKeys.toSet().size) add("${flow.id}: transición duplicada")
        flow.transitions.forEach { edge ->
            if (edge.from !in flow.states) add("${flow.id}: origen desconocido '${edge.from}'")
            if (edge.to !in flow.states) add("${flow.id}: destino desconocido '${edge.to}'")
            if (edge.event.isBlank()) add("${flow.id}: evento vacío")
            if (edge.from in flow.terminal) add("${flow.id}: estado terminal '${edge.from}' tiene salida")
        }
        val reachable = mutableSetOf(flow.initial)
        var changed = true
        while (changed) {
            changed = false
            flow.transitions.filter { it.from in reachable }.forEach {
                if (reachable.add(it.to)) changed = true
            }
        }
        (flow.states - reachable).forEach { add("${flow.id}: estado inalcanzable '$it'") }
        (flow.states - flow.terminal).filter { state ->
            flow.transitions.none { it.from == state }
        }.forEach { add("${flow.id}: estado no terminal sin salida '$it'") }
        flow.invariantIds.forEach { invariant ->
            if (invariant !in UxTransitionContracts.invariantDescriptions) {
                add("${flow.id}: invariante desconocida '$invariant'")
            }
        }
        if (flow.invariantIds.isEmpty()) add("${flow.id}: no declara invariantes")
    }
}
