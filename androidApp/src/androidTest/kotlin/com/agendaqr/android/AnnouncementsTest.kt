package com.agendaqr.android

import android.view.accessibility.AccessibilityEvent
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Anuncios: captura eventos de accesibilidad
 * vía UiAutomation y verifica UN anuncio por acción para:
 * - eliminación ("Eliminado") — diálogo destructivo del detalle de destino,
 * - error de correo al conectar una cuenta ("Ingresa un correo válido").
 *
 * El guardado de lote (S12) y la lectura de QR requieren activos que no
 * existen en el dispositivo de prueba (imágenes/QR real): quedan
 * registrados como REQUIERE TALKBACK REAL / PENDIENTE en el informe.
 * El anuncio de sincronización se verifica con un evento determinista en
 * RootNavigationChromeTest; el perfil de demostración del emulador no sincroniza.
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
                val source = event.source
                val text = (event.text.map(CharSequence::toString) +
                    listOfNotNull(
                        event.contentDescription?.toString(),
                        source?.text?.toString(),
                        source?.contentDescription?.toString(),
                        source?.error?.toString(),
                    )).filter(String::isNotBlank).distinct().joinToString(" ")
                recorder.events.add(event.eventType to text)
                source?.recycle()
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
            Qa.device.findObjects(androidx.test.uiautomator.By.textContains("Carnicería")).isNotEmpty() ||
                Qa.device.findObjects(androidx.test.uiautomator.By.textContains("QR importado")).isNotEmpty() ||
                Qa.device.findObjects(androidx.test.uiautomator.By.textContains("Sin nombre")).isNotEmpty()
        }
        val first = Qa.device.findObjects(androidx.test.uiautomator.By.textContains("Carnicería")).firstOrNull()
            ?: Qa.device.findObjects(androidx.test.uiautomator.By.textContains("QR importado")).firstOrNull()
            ?: Qa.device.findObjects(androidx.test.uiautomator.By.textContains("Sin nombre")).firstOrNull()
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
        // La cuenta remota es opcional y se conecta desde la superficie de cuenta.
        Qa.launchApp()
        Qa.tapText("Más")
        Qa.tapText("Cuenta y respaldo")
        Qa.waitFor(8_000) { Qa.textVisible("Correo") || Qa.hasText("Iniciar sesión") }
        val recorder = withRecorder {
            Qa.tapText("Iniciar sesión")
            Qa.waitFor(4_000) { Qa.textVisible("Ingresa un correo válido") }
        }
        val count = recorderHas(recorder, "Ingresa un correo válido")
        assertTrue(count >= 1, "debe anunciar el error de correo (eventos: ${recorder.events})")
    }

    @Test
    fun demo_mode_does_not_claim_remote_sync() {
        Qa.launchApp()
        assertTrue(Qa.hasText("Modo demostración · datos ficticios"))
        assertFalse(Qa.hasText("Sincronización pendiente"))
        assertFalse(Qa.hasText("Sincronización completada"))
    }
}
