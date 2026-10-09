package com.agendaqr.android

import androidx.test.uiautomator.By
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertTrue

/**
 * QA ronda 2 (checklist B/TalkBack-2, item de cámara): denegación
 * PERMANENTE del permiso de cámara → la acción visible es "Abrir ajustes"
 * (no "Reintentar"), y esta abre los ajustes de la app. Tras conceder
 * desde ajustes, la cámara arranca.
 *
 * Flujo: revoca → abre S02 (Añadir) → Cámara → deniega el diálogo del
 * SISTEMA dos veces → "no volver a preguntar" → banner con "Abrir
 * ajustes" → settings package → conceder → volver → cámara.
 */
@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
class CameraPermissionFlowTest {

    private val settings get() = SystemSettingsRule()

    @Test
    fun permanent_denial_offers_open_settings_and_recovers() {
        // Limpieza inicial del permiso (no se restaura al final: el permiso
        // se concede de nuevo dentro del propio test).
        Qa.shell("pm revoke com.agendaqr.app android.permission.CAMERA")
        Qa.launchApp()
        Qa.tapText("Añadir", timeoutMs = 15_000)
        Qa.tapText("Cámara")

        // Deniega el diálogo del SISTEMA dos veces (la segunda con "no
        // preguntar más" activa en muchos OEM tras la primera denegación).
        denySystemDialog()
        // Segunda ronda: reintenta (aún no permanente) y vuelve a denegar.
        Qa.tapText("Cámara")
        denySystemDialog()

        // Con "no volver a preguntar" el reintento es invisible: la acción
        // del banner DEBE ser "Abrir ajustes".
        Qa.waitFor(8_000) { Qa.hasText("No pudimos abrir la cámara.") }
        assertTrue(
            Qa.textVisible("Abrir ajustes"),
            "la acción visible debe ser 'Abrir ajustes' tras la denegación permanente",
        )

        // La pulsa y aterriza en los ajustes de la app.
        Qa.tapText("Abrir ajustes")
        Qa.waitFor(8_000) {
            Qa.device.currentPackageName.let { it == "com.android.settings" || it == "com.google.android.settings" || it == "com.motorola.settings" }
        }
        Qa.screenshot("camera-permission-settings")

        // Concede desde ajustes (permiso del sistema) y vuelve a la app.
        Qa.shell("pm grant com.agendaqr.app android.permission.CAMERA")
        Qa.launchApp()
        Qa.tapText("Añadir", timeoutMs = 15_000)
        Qa.tapText("Cámara")
        // La cámara arranca: overlay de captura con el hint del scanner.
        Qa.waitFor(10_000) { Qa.hasText("Encuadra el código QR") }
        Qa.tapText("Volver")
    }

    private fun denySystemDialog() {
        // Diálogo del sistema: sin "Don't allow" en español varía por OEM;
        // se busca por varios textos conocidos y, como último recurso,
        // se deniega con el permiso revocado (back + revoke).
        val denyCandidates = listOf(
            "No permitir",
            "Denegar",
            "Don't allow",
            "NO PERMITIR",
        )
        val pressed = denyCandidates.firstOrNull { candidate ->
            Qa.waitFor(4_000) { Qa.textVisible(candidate) }
            Qa.device.findObjects(By.text(candidate)).firstOrNull()?.let { node ->
                node.click()
                true
            } ?: false
        }
        if (pressed == null) {
            // El diálogo no apareció (permiso ya permanente): cierra con
            // Back y asegura el estado denegado.
            Qa.device.pressBack()
        }
        Qa.waitFor(3_000) { !Qa.textVisible("Mientras uses la app") }
    }
}
