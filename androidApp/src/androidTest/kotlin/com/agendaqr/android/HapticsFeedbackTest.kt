package com.agendaqr.android

import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertTrue

/**
 * Háptica: el sistema registra las vibraciones emitidas por
 * la app en `dumpsys vibrator_manager`. Tras una acción con feedback
 * (eliminación confirmada), el registro debe contener al menos un efecto
 * de `com.agendaqr.app`. La lectura de QR no es automatizable sin un QR
 * físico real: queda como PENDIENTE/manual en el informe (se valida el
 * MISMO mecanismo AmFeedbackEvent con la acción de eliminar).
 */
@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
class HapticsFeedbackTest {

    @Test
    fun delete_confirmation_produces_a_haptic_effect() {
        Qa.launchApp()
        // Limpia el registro previo (solo lectura de la sección de la app).
        val before = appVibratorEntries()
        // Abre el primer destino → overflow → eliminar → confirmar.
        check(Qa.scrollToText("Aysa · Agua")) { "no se encontró el destino demo" }
        Qa.tapText("Aysa · Agua")
        Qa.waitFor(6_000) { Qa.hasText("Mostrar QR") || Qa.hasText("Volver") }
        Qa.tapText("Más")
        Qa.tapText("Eliminar")
        Qa.waitFor(4_000) { Qa.textVisible("Cancelar") }
        Qa.tapText("Eliminar")
        Qa.waitFor(4_000) { true }
        val after = appVibratorEntries()
        assertTrue(
            after.size > before.size,
            "la eliminación confirmada debe vibrar (antes=${before.size}, después=${after.size})",
        )
    }

    private fun appVibratorEntries(): List<String> {
        val dump = Qa.shell("dumpsys vibrator_manager")
        return dump.lines().filter { it.contains("com.agendaqr.app") }
    }
}
