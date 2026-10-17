package com.agendaqr.android

import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

/**
 * Estados accesibles en recorridos reales: confirmación de eliminación y
 * error de validación al conectar una cuenta.
 *
 * El guardado de lote (S12) y la lectura de QR requieren activos que no
 * existen en el dispositivo de prueba (imágenes/QR real): quedan
 * registrados como REQUIERE TALKBACK REAL / PENDIENTE en el informe.
 * Los contratos liveRegion se verifican en pruebas de semántica Compose;
 * el perfil de demostración del emulador no sincroniza.
 */
@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
class AnnouncementsTest {

    @Test
    fun delete_confirmation_shows_eliminado() {
        Qa.launchApp()
        check(Qa.scrollToText("Edesur · Luz")) { "no se encontró el destino demo" }
        Qa.tapText("Edesur · Luz")
        Qa.waitFor(6_000) { Qa.hasText("Mostrar QR") || Qa.hasText("Volver") }
        Qa.tapText("Más") // menú overflow con "Eliminar"
        Qa.waitFor(4_000) { Qa.textVisible("Eliminar") }
        Qa.tapText("Eliminar")
        Qa.waitFor(4_000) { Qa.textVisible("Cancelar") }
        Qa.tapText("Eliminar")
        Qa.waitFor(6_000) { Qa.hasText("Eliminado") }
        assertTrue(Qa.hasText("Eliminado"), "la confirmación debe quedar expuesta en pantalla")
    }

    @Test
    fun login_invalid_email_shows_the_field_error() {
        // La cuenta remota es opcional y se conecta desde la superficie de cuenta.
        Qa.launchApp()
        Qa.tapText("Más")
        Qa.tapText("Cuenta y respaldo")
        Qa.waitFor(8_000) { Qa.textVisible("Correo") || Qa.hasText("Iniciar sesión") }
        Qa.tapText("Iniciar sesión")
        Qa.waitFor(4_000) { Qa.hasText("Ingresa un correo válido") }
        assertTrue(Qa.hasText("Ingresa un correo válido"), "el error de validación debe ser accesible")
    }

    @Test
    fun demo_mode_does_not_claim_remote_sync() {
        Qa.launchApp()
        assertTrue(Qa.hasText("Modo demostración · datos ficticios"))
        assertFalse(Qa.hasText("Sincronización pendiente"))
        assertFalse(Qa.hasText("Sincronización completada"))
    }
}
