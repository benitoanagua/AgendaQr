package com.agendaqr.destinations.presentation

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

/**
 * T4 — Semántica del selector de contexto (S08).
 *
 * El selector anterior usaba el copy de S11 ("¿A cuál corresponde?"),
 * confirmaba con "Cerrar" y su acción de descarte ("Sin contexto") borraba
 * la selección previa sin pedírselo al usuario. El modelo nuevo es
 * explícito: Cancelar no toca la selección; "Quitar contexto" es la única
 * vía de limpiarla y solo existe si hay una.
 */
class ContextSelectionTest {

    @Test
    fun cancel_does_not_modify_the_selection() {
        val selection = ContextSelection(contextId = "ctx-mercado")
        val cancelled = selection.openPicker().cancelPicker()
        // El draft de contexto sobrevive abrir/cerrar el selector.
        assertEquals("ctx-mercado", cancelled.contextId)
        assertFalse(cancelled.pickerOpen)
    }

    @Test
    fun remove_clears_the_selection() {
        val selection = ContextSelection(contextId = "ctx-mercado").openPicker()
        val removed = selection.removeContext()
        assertNull(removed.contextId)
        assertFalse(removed.pickerOpen)
    }

    @Test
    fun choose_confirms_and_closes() {
        val selection = ContextSelection(contextId = "ctx-viejo").openPicker()
        val chosen = selection.select("ctx-nuevo")
        assertEquals("ctx-nuevo", chosen.contextId)
        assertFalse(chosen.pickerOpen)
    }

    @Test
    fun quitar_contexto_only_exists_when_a_context_is_chosen() {
        // Sin selección previa no se ofrece "Quitar contexto" (la acción
        // destructiva no aparece cuando no hay nada que quitar).
        assertFalse(ContextSelection(contextId = null).canRemove)
        assertTrue(ContextSelection(contextId = "ctx-mercado").canRemove)
    }

    @Test
    fun the_draft_survives_open_close_cycles_without_side_effects() {
        var selection = ContextSelection(contextId = "ctx-mercado")
        // Abrir y cancelar varias veces no debe cambiar nada.
        repeat(3) {
            selection = selection.openPicker().cancelPicker()
        }
        assertEquals("ctx-mercado", selection.contextId)
        // Elegir el MISMO contexto tampoco destruye nada.
        selection = selection.openPicker().select("ctx-mercado")
        assertEquals("ctx-mercado", selection.contextId)
    }

    @Test
    fun saver_round_trips_the_selection() {
        val original = ContextSelection(contextId = "ctx-mercado", pickerOpen = false)
        val restored = restoreContextSelection(saveContextSelection(original))
        assertEquals(original, restored)
        // La ausencia de contexto también viaja intacta (no confundir ""
        // con null).
        val empty = ContextSelection(contextId = null, pickerOpen = true)
        val restoredEmpty = restoreContextSelection(saveContextSelection(empty))
        assertEquals(empty, restoredEmpty)
        assertNull(restoredEmpty.contextId)
    }
}
