package com.agendaqr.android

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import java.io.File
import kotlin.test.fail

/** Utilidades compartidas de las pruebas instrumentadas. */
object Qa {

    val instrumentation get() = InstrumentationRegistry.getInstrumentation()
    val device: UiDevice get() = UiDevice.getInstance(instrumentation)
    val context: Context get() = instrumentation.targetContext

    fun launchApp() {
        device.executeShellCommand(
            "am start -n com.agendaqr.app/com.agendaqr.android.MainActivity",
        )
        device.waitForIdle(2_000)

        waitFor(15_000) {
            hasText("Explorar demostración") ||
                hasText("Traer un QR") ||
                hasText("Inicio") ||
                hasText("Volver") ||
                hasText("Configura tu PIN")
        }
        if (textVisible("Explorar demostración")) {
            tapText("Explorar demostración", timeoutMs = 15_000)
            waitFor(15_000) { hasText("Traer un QR") }
        }

        repeat(5) {
            if (hasText("Traer un QR")) return
            if (hasText("Inicio")) {
                tapText("Inicio")
            } else {
                device.pressBack()
            }
            device.waitForIdle(1_000)
        }
        waitFor(15_000) { hasText("Traer un QR") }
    }

    /** Espera por condición con tope (nada de sleeps fijos; regla 7). */
    fun waitFor(timeoutMs: Long, intervalMs: Long = 250, condition: () -> Boolean) {
        val start = System.currentTimeMillis()
        while (System.currentTimeMillis() - start < timeoutMs) {
            if (condition()) return
            Thread.sleep(intervalMs)
        }
        fail("condición no cumplida en ${timeoutMs}ms")
    }

    fun textVisible(text: String): Boolean =
        device.findObjects(By.text(text)).isNotEmpty()

    fun descVisible(text: String): Boolean =
        device.findObjects(By.desc(text)).isNotEmpty()

    fun waitText(text: String, timeoutMs: Long = 8_000) {
        waitFor(timeoutMs) { textVisible(text) || descVisible(text) }
    }

    /** Toca el primer elemento con el texto dado (fail si no existe). */
    fun tapText(text: String, timeoutMs: Long = 8_000) {
        waitText(text, timeoutMs)
        val node = device.findObject(By.text(text)) ?: device.findObject(By.desc(text))
        node?.click() ?: fail("nodo no clickable: $text")
    }

    fun hasText(text: String): Boolean = textVisible(text) || descVisible(text)

    /** Captura en el caché del runner de pruebas, fuera del repositorio. */
    fun screenshot(name: String) {
        val dir = File(instrumentation.context.cacheDir, "agendaqr-ronda2")
        check(dir.exists() || dir.mkdirs()) { "no se pudo crear el directorio de capturas" }
        val output = File(dir, "$name.png")
        val captured = device.takeScreenshot(output)
        check(captured) { "screenshot fallido: $name" }
    }

    fun shell(cmd: String): String = device.executeShellCommand(cmd)

    /** dp = px / (densityDpi / 160). */
    fun pxToDp(px: Int): Float {
        val densityDpi = context.resources.displayMetrics.densityDpi
        return px / (densityDpi / 160f)
    }

}
