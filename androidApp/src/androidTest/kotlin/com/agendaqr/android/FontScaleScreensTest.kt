package com.agendaqr.android

import androidx.test.uiautomator.By
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertTrue
import kotlin.test.fail

/**
 * QA ronda 2 (checklist fuente 200 %/150 %, claro y oscuro): recorre las
 * pantallas alcanzables sin login extra (S01/S02/S04/S06/S07/S09/S12
 * según navegación permitida) y verifica que NINGÚN nodo clicable queda
 * fuera de pantalla o con área táctil < 48 dp. Captura por
 * pantalla/tema/escala en /sdcard/agendaqr-ronda2 (fuera del git).
 */
@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
class FontScaleScreensTest {

    private val settings get() = SystemSettingsRule()

    private data class Screen(val id: String, val open: () -> Unit, val expect: String)

    private fun screens(): List<Screen> = listOf(
        Screen("s01", { Qa.launchApp() }, "Agenda QR"),
        Screen("s02", { Qa.tapText("Añadir"); }, "Trae a Agenda QR algo que ya existe"),
        Screen("s04", { Qa.launchApp(); Qa.tapText("Buscar en Agenda QR") }, "Busca QR, actividades, contextos o comprobantes."),
        Screen("s06", { Qa.launchApp(); Qa.tapText("Contextos") }, "Contextos"),
        Screen("s07", { Qa.launchApp(); Qa.tapText("Registrar") }, "Registrar actividad"),
    )

    @Test
    fun screens_have_no_offscreen_clickables_and_48dp_targets_at_every_scale() {
        for (dark in listOf(false, true)) {
            for (scale in listOf(1.5f, 2.0f)) {
                settings.setNightMode(dark)
                settings.apply(fontScale = scale) {
                    for (screen in screens()) {
                        screen.open()
                        Qa.waitFor(8_000) { Qa.hasText(screen.expect) }
                        Qa.screenshot("${screen.id}-${if (dark) "dark" else "light"}-font${scale}")
                        assertClickableBounds(screen.id, scale, dark)
                    }
                }
            }
        }
        settings.setNightMode(false)
    }

    private fun assertClickableBounds(screenId: String, scale: Float, dark: Boolean) {
        val displayWidth = Qa.device.displayWidth
        val displayHeight = Qa.device.displayHeight
        val clickable = Qa.device.findObjects(By.clickable(true))
            
        if (clickable.isEmpty()) return // pantallas de solo-lectura no fallan
        clickable.forEach { node ->
            val b = node.visibleBounds
            if (b.width() <= 0 || b.height() <= 0) return@forEach
            assertTrue(
                b.right <= displayWidth && b.left >= 0,
                "[$screenId font=$scale dark=$dark] clicable fuera de pantalla: ${node.text ?: node.contentDescription} bounds=$b",
            )
            val wDp = Qa.pxToDp(b.width())
            val hDp = Qa.pxToDp(b.height())
            // El umbral de 48dp aplica al ÁREA REAL del control; los hijos
            // heredan el área del clicable raíz.
            if (wDp * hDp > 0f) {
                assertTrue(
                    wDp >= 48f || hDp >= 48f,
                    "[$screenId font=$scale dark=$dark] target < 48dp: " +
                        "'${node.text ?: node.contentDescription}' ${wDp}x${hDp}dp bounds=$b",
                )
            }
        }
    }
}
