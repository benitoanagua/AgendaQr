package com.agendaqr.android

import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertTrue

/**
 * Rotación en dispositivo:
 * - superficie abierta (Contextos) sobrevive a la rotación (Saver),
 * - formulario S07 conserva datos durante la recreación por rotación
 *   (NO cubre muerte de proceso real — pendiente manual con `am kill`),
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
    fun s07_form_keeps_data_after_rotation() {
        Qa.launchApp()
        Qa.tapText("Registrar actividad", timeoutMs = 15_000)
        // El importe opcional permanece en el borrador durante la rotación.
        Qa.tapText("Monto (opcional)")
        Qa.device.pressKeyCode(54) // '3'
        Qa.waitFor(2_000) { Qa.hasText("3") }
        settings.apply(userRotation = 1) {
            Qa.waitFor(6_000) { Qa.hasText("3") }
            assertTrue(Qa.hasText("3"), "el importe conserva su valor al rotar")
            Qa.screenshot("rotation-s07-data-landscape")
        }
    }

    @Test
    fun s03_camera_rebinds_after_rotation_without_crash() {
        Qa.launchApp()
        Qa.tapText("Traer un QR", timeoutMs = 15_000)
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
