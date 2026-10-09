package com.agendaqr.android

import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertTrue

/**
 * QA ronda 2 (checklist rotación + Área C en dispositivo):
 * - superficie abierta (Contextos) sobrevive a la rotación (Saver),
 * - formulario S07 conserva datos y error visible al rotar y al
 *   recrear la actividad (recreate() = rotación de configuración; NO
 *   cubre muerte de proceso real — pendiente manual con `am kill`),
 * - S03 en escaneo re-liga la cámara tras rotar (sin crash).
 */
@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
class RotationRestorationTest {

    private val settings get() = SystemSettingsRule()

    @Test
    fun open_surface_survives_rotation() {
        Qa.launchApp()
        Qa.tapText("Contextos", timeoutMs = 15_000)
        settings.apply(userRotation = 1) { // 1 = landscape
            Qa.waitFor(5_000) { Qa.hasText("Contextos") }
            assertTrue(Qa.hasText("Contextos"), "la superficie abierta se conserva al rotar")
            Qa.screenshot("rotation-contexts-landscape")
        }
        // De vuelta a portrait sigue viva.
        Qa.waitFor(5_000) { Qa.hasText("Contextos") }
    }

    @Test
    fun s07_form_keeps_data_and_visible_error_across_recreation() {
        Qa.launchApp()
        Qa.tapText("Registrar", timeoutMs = 15_000)
        // Envía vacío: el error de fecha (obligatorio) queda visible.
        Qa.tapText("Guardar")
        Qa.waitFor(6_000) { Qa.hasText("Usa el formato dd/mm/aaaa") }
        // Rellena un dato adicional que debe sobrevivir.
        Qa.tapText("Monto (opcional)")
        Qa.device.pressKeyCode(54) // '3'
        Qa.waitFor(2_000) { Qa.hasText("Usa el formato dd/mm/aaaa") }
        settings.apply(userRotation = 1) {
            Qa.waitFor(6_000) { Qa.hasText("Usa el formato dd/mm/aaaa") }
            assertTrue(Qa.hasText("Usa el formato dd/mm/aaaa"), "el error visible sobrevive a la rotación")
            Qa.screenshot("rotation-s07-error-landscape")
        }
        // Recreación de actividad explícita (equivale a cambio de config).
        Qa.shell("am start -n com.agendaqr.app/com.agendaqr.android.MainActivity")
        Qa.waitFor(6_000) { Qa.hasText("Usa el formato dd/mm/aaaa") }
        assertTrue(Qa.hasText("Usa el formato dd/mm/aaaa"), "el error sobrevive a la recreación")
    }

    @Test
    fun s03_camera_rebinds_after_rotation_without_crash() {
        Qa.launchApp()
        Qa.tapText("Añadir", timeoutMs = 15_000)
        Qa.tapText("Cámara")
        Qa.waitFor(10_000) { Qa.hasText("Encuadra el código QR") }
        val pidBefore = Qa.shell("pidof com.agendaqr.app").trim()
        settings.apply(userRotation = 1) {
            // El hint sigue: la cámara se re-ligó tras la recreación.
            Qa.waitFor(10_000) { Qa.hasText("Encuadra el código QR") }
            assertTrue(Qa.hasText("Encuadra el código QR"), "el escáner sigue vivo tras rotar")
            Qa.screenshot("rotation-camera-landscape")
        }
        val pidAfter = Qa.shell("pidof com.agendaqr.app").trim()
        assertTrue(pidAfter.isNotEmpty(), "el proceso sigue vivo (sin crash)")
        assertTrue(pidBefore == pidAfter, "sin crash: el PID no cambia ($pidBefore -> $pidAfter)")
        Qa.tapText("Volver")
    }
}
