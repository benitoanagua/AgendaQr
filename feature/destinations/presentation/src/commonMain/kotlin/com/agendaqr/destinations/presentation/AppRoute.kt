package com.agendaqr.destinations.presentation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

/**
 * Modelo de navegación único de la app autenticada (T2).
 *
 * Un `AppRoute` es una SUPERFICIE completa. Las pantallas internas de cada
 * flujo (p. ej. Lista/Detalle de contextos, Lista/Registrar/Detalle de
 * operaciones, o las rutas de destinos) viven en el ViewModel de su flujo:
 * son hoja del mismo árbol jerárquico y no navegación paralela.
 *
 * Esto reemplaza los 4 booleanos `showOperations/showSearch/showContexts/
 * showImportBatch`: el orden del stack es la única autoridad de qué
 * superficie está visible y a dónde vuelve el Back. El flujo congelado no
 * cambia: las mismas pantallas con las mismas transiciones; solo deja de
 * haber superposiciones invisibles de booleanos.
 *
 * Reglas congeladas que este modelo hace cumplir de forma testeable:
 * - §3 "Back conserva el trabajo del flujo padre": el Back a una superficie
 *   la restaura tal como estaba (p. ej. la consulta de S05).
 * - §3 "Cancelar abandona la intención actual": el Back interno del flujo
 *   (decidido por su ViewModel) se ejecuta antes que el pop de superficie.
 */
sealed interface AppRoute {
    /**
     * S01 — Inicio. Raíz de la app: lista de destinos y entrada a los
     * flujos. Las rutas internas de destinos (Add, Detail, Edit,
     * FullscreenQr, ImportReview) viven en [DestinationsUiState.route].
     */
    data object Home : AppRoute

    /** S04/S05 — Búsqueda global; su consulta pertenece a la superficie. */
    data object Search : AppRoute

    /** S06 — Contextos; Lista/Detalle viven en [ContextsUiState.route]. */
    data object Contexts : AppRoute

    /**
     * S07 — Operaciones; Lista/Registrar/Detalle/Bandeja viven en
     * [OperationsUiState.route].
     */
    data object Operations : AppRoute

    /** S12 — Resultado de importación masiva (derivada de Añadir). */
    data object ImportBatch : AppRoute
}

/**
 * Back stack de superficies. Puro y testeable: sin Compose, sin VMs.
 *
 * - `push` no duplica la superficie ya visible (los eventos repetidos de
 *   imports compartidos no apilan la misma superficie dos veces).
 * - `pop` en la raíz devuelve null: la app NO consume el Back y el sistema
 *   cierra la app (comportamiento nativo esperado desde S01).
 */
class AppBackStack(
    initial: List<AppRoute> = listOf(AppRoute.Home),
) {
    init {
        require(initial.isNotEmpty()) { "El back stack necesita al menos la raíz" }
        require(initial.first() == AppRoute.Home) { "La raíz del back stack debe ser Home" }
    }

    private val _stack = MutableStateFlow(initial)
    val stack: StateFlow<List<AppRoute>> = _stack.asStateFlow()

    val top: AppRoute get() = _stack.value.last()

    val size: Int get() = _stack.value.size

    fun canPop(): Boolean = _stack.value.size > 1

    /** Superficie que queda debajo (null en la raíz). */
    fun belowTop(): AppRoute? = _stack.value.dropLast(1).lastOrNull()

    fun push(route: AppRoute) {
        _stack.update { current ->
            if (current.last() == route) current else current + route
        }
    }

    /** Quita la superficie visible. Null: solo quedaba la raíz. */
    fun pop(): AppRoute? {
        var popped: AppRoute? = null
        _stack.update { current ->
            if (current.size <= 1) {
                current
            } else {
                popped = current.last()
                current.dropLast(1)
            }
        }
        return popped
    }

    fun popToRoot() {
        _stack.update { current -> listOf(current.first()) }
    }
}

/**
 * Qué debe hacer el Back del sistema para la superficie visible.
 *
 * El orden es el contrato: primero decide el flujo interno de la
 * superficie (su ViewModel, que conserva/cancela según su contrato);
 * si el flujo no tiene adónde volver, la superficie hace pop; si no hay
 * superficie que hacer pop, el Back pertenece al sistema (salir de la
 * app desde la raíz, S01).
 */
sealed interface SystemBackAction {
    /** El flujo interno de la superficie consume el Back. */
    data class InnerFlow(val route: AppRoute) : SystemBackAction

    /** La superficie hace pop y vuelve a la que está debajo. */
    data object PopSurface : SystemBackAction

    /** Raíz sin flujo interno: el sistema decide (salir de la app). */
    data object SystemExit : SystemBackAction
}

/**
 * Decisión pura del Back del sistema para [route].
 *
 * @param needsInnerBack si el flujo interno de la superficie puede
 * consumir el Back (p. ej. Detalle → Lista). Sin efectos secundarios.
 */
fun systemBackAction(
    route: AppRoute,
    needsInnerBack: (AppRoute) -> Boolean,
    canPop: Boolean,
): SystemBackAction = when {
    needsInnerBack(route) -> SystemBackAction.InnerFlow(route)
    canPop -> SystemBackAction.PopSurface
    else -> SystemBackAction.SystemExit
}
