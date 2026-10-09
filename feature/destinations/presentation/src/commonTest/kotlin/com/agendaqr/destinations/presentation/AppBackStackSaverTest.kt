package com.agendaqr.destinations.presentation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

/**
 * Ronda 2 (Área C) — restauración del back stack. Con el código anterior
 * (`remember { AppBackStack() }`) rotación o muerte de proceso devolvían
 * SIEMPRE a Inicio: estos tests fijan guardar + restaurar por nombre y el
 * comportamiento conservador ante rutas desconocidas.
 */
class AppBackStackSaverTest {

    private fun roundTrip(stack: AppBackStack): AppBackStack = restoreBackStack(saveBackStack(stack))

    @Test
    fun stack_survives_save_and_restore() {
        val stack = AppBackStack()
        stack.push(AppRoute.Search)
        stack.push(AppRoute.Contexts)
        val restored = roundTrip(stack)
        assertEquals(listOf<AppRoute>(AppRoute.Home, AppRoute.Search, AppRoute.Contexts), restored.stack.value)
        assertTrue(restored.canPop(), "restaurar conserva el Back (pop disponible)")
    }

    @Test
    fun root_only_stack_restores_identically() {
        val restored = roundTrip(AppBackStack())
        assertEquals(listOf<AppRoute>(AppRoute.Home), restored.stack.value)
    }

    @Test
    fun unknown_route_names_are_dropped_conservatively() {
        // Rutas de una versión futura hacia atrás: se descartan sin romper.
        val restored = restoreBackStack(listOf("Home", "Search", "FutureSurface"))
        assertEquals(listOf<AppRoute>(AppRoute.Home, AppRoute.Search), restored.stack.value)
    }

    @Test
    fun restored_stack_without_home_root_rebuilds_from_home() {
        // La raíz Home es un invariante del constructor: si lo guardado no
        // la trae (datos corruptos), se reconstruye desde la raíz.
        val restored = restoreBackStack(listOf("Search", "Contexts"))
        assertEquals(listOf<AppRoute>(AppRoute.Home), restored.stack.value)
    }

    @Test
    fun push_after_restore_works_normally() {
        val restored = roundTrip(AppBackStack())
        restored.push(AppRoute.Operations)
        assertEquals(listOf<AppRoute>(AppRoute.Home, AppRoute.Operations), restored.stack.value)
    }
}
