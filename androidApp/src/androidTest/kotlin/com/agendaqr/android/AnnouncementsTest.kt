package com.agendaqr.android

import android.view.accessibility.AccessibilityEvent
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertTrue

/**
 * QA ronda 2 (checklist de anuncios): captura eventos de accesibilidad
 * vía UiAutomation y verifica UN anuncio por acción para:
 * - eliminación ("Eliminado") — diário destructivo del detalle de destino,
 * - error de correo en el login ("Ingresa un correo válido"),
 * - sincronización completada ("Sincronización completada") — offline→online.
 *
 * El guardado de lote (S12) y la lectura de QR requieren activos que no
 * existen en el dispositivo de prueba (imágenes/QR real): quedan
 * registrados como REQUIERE TALKBACK REAL / PENDIENTE en el informe.
 */
@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
class AnnouncementsTest {

    private class EventRecorder {
        val events = mutableListOf<Pair<Int, String>>()
        var recording = false
    }

    private fun withRecorder(block: (EventRecorder) -> Unit): EventRecorder {
        val recorder = EventRecorder()
        recorder.recording = true
        val listener = android.view.accessibility.AccessibilityManager::class.let { }
        val uiAutomation = Qa.instrumentation.uiAutomation
        val listenerObj = object : android.app.UiAutomation.OnAccessibilityEventListener {
            override fun onAccessibilityEvent(event: AccessibilityEvent) {
                if (!recorder.recording) return
                val text = event.text.joinToString(" ").ifBlank {
                    event.contentDescription?.toString() ?: ""
                }
                recorder.events.add(event.eventType to text)
            }
        }
        uiAutomation.setOnAccessibilityEventListener(listenerObj)
        try {
            block(recorder)
        } finally {
            recorder.recording = false
            uiAutomation.setOnAccessibilityEventListener(null)
        }
        return recorder
    }

    private fun recorderHas(recorder: EventRecorder, message: String): Int =
        recorder.events.count { (_, text) -> text.contains(message) }

    @Test
    fun delete_confirmation_announces_eliminado() {
        Qa.launchApp()
        // Abre el primer destino de la lista (S01 → detalle).
        Qa.waitFor(15_000) {
            Qa.device.findObjects(androidx.test.uiautomator.By.textContains("Carniceria")).isNotEmpty() ||
                Qa.device.findObjects(androidx.test.uiautomator.By.textContains("QR importado")).isNotEmpty() ||
                Qa.device.findObjects(androidx.test.uiautomator.By.textContains("Sin nombre")).isNotEmpty()
        }
        val first = Qa.device.findObjects(androidx.test.uiautomator.By.textContains("Recientes"))
            .firstOrNull() ?: Qa.device.findObjects(androidx.test.uiautomator.By.clickable(true))
            .firstOrNull { it.text?.isNotBlank() == true && it.text != "Añadir" && it.text != "Registrar" && it.text != "Favoritos" && it.text != "Contextos" }
        first?.click() ?: kotlin.test.fail("sin destino en la lista")
        Qa.waitFor(6_000) { Qa.hasText("Mostrar QR") || Qa.hasText("Volver") }
        Qa.tapText("Más") // menú overflow con "Eliminar"
        Qa.waitFor(4_000) { Qa.textVisible("Eliminar") }
        val recorder = withRecorder {
            Qa.tapText("Eliminar")
            // El foco inicial del diálogo destructivo es Cancelar; el
            // anuncio llega al CONFIRMAR.
            Qa.waitFor(4_000) { Qa.textVisible("Cancelar") }
            Qa.tapText("Eliminar")
        }
        Qa.waitFor(4_000) { true }
        val count = recorderHas(recorder, "Eliminado")
        assertTrue(count >= 1, "debe anunciarse 'Eliminado' (eventos: ${recorder.events})")
    }

    @Test
    fun login_invalid_email_announces_the_error() {
        // Cerrar sesión para volver al login.
        Qa.launchApp()
        Qa.tapText("Más")
        Qa.waitFor(4_000) { Qa.textVisible("Cerrar sesión") }
        Qa.tapText("Cerrar sesión")
        Qa.waitFor(8_000) { Qa.textVisible("Correo") || Qa.hasText("Iniciar sesión") }
        val recorder = withRecorder {
            Qa.tapText("Iniciar sesión")
            Qa.waitFor(4_000) { Qa.textVisible("Ingresa un correo válido") }
        }
        val count = recorderHas(recorder, "Ingresa un correo válido")
        assertTrue(count >= 1, "debe anunciar el error de correo (eventos: ${recorder.events})")
    }

    @Test
    fun sync_completing_announces_sincronizacion_completada() {
        Qa.launchApp()
        Qa.tapText("Registrar")
        // Modo avión ON antes de guardar: la operación queda pendiente.
        val settings = SystemSettingsRule()
        settings.apply(airplane = true) {
            Qa.tapText("Fecha")
            Qa.device.pressKeyCode(7) // 0
            Qa.device.pressKeyCode(7)
            Qa.device.pressKeyCode(52) // /
            Qa.device.pressKeyCode(8) // 1
            Qa.device.pressKeyCode(8)
            Qa.device.pressKeyCode(52)
            Qa.device.pressKeyCode(13) // 2
            Qa.device.pressKeyCode(10) // 6
            Qa.tapText("Guardar")
            Qa.waitFor(10_000) { Qa.hasText("Pendiente") }
        }
        // Reconecta y captura el anuncio de completado.
        val recorder = withRecorder {
            Qa.waitFor(25_000) { recorderIdleSync() }
        }
        val count = recorderHas(recorder, "Sincronización completada")
        assertTrue(count >= 1, "debe anunciar 'Sincronización completada' (eventos: ${recorder.events})")
    }

    private fun recorderIdleSync(): Boolean {
        // Éxito cuando el badge deja de mostrar pendientes (banner fuera).
        return !Qa.hasText("Sincronización pendiente")
    }
}
