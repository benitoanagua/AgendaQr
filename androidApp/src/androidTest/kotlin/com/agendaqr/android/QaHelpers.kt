package com.agendaqr.android

import android.content.Context
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.StaleObjectException
import androidx.test.uiautomator.UiDevice
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
        if (!hasText("Explorar demostración") && hasText("Configura tu PIN")) {
            scrollToText("Explorar demostración")
        }
        if (hasText("Explorar demostración")) {
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
        repeat(3) { attempt ->
            waitText(text, timeoutMs)
            val node = device.findObject(By.text(text)) ?: device.findObject(By.desc(text))
            if (node == null) return@repeat
            try {
                node.click()
                return
            } catch (stale: StaleObjectException) {
                if (attempt == 2) throw stale
            }
        }
        fail("nodo no clickable: $text")
    }

    fun hasText(text: String): Boolean = textVisible(text) || descVisible(text)

    /** Captura en almacenamiento compartido del emulador, fuera del repositorio. */
    fun screenshot(name: String) {
        require(name.matches(Regex("[A-Za-z0-9_.-]+"))) { "nombre de captura inválido: $name" }
        val path = "/sdcard/agendaqr-ronda2/$name.png"
        val result = shell(
            "mkdir -p /sdcard/agendaqr-ronda2 && screencap -p '$path' && test -s '$path' && echo captured",
        ).trim()
        check(result == "captured") { "screenshot fallido: $name" }
    }

    fun shell(cmd: String): String = device.executeShellCommand(cmd)

    fun scrollToText(text: String, maxSwipes: Int = 8): Boolean {
        repeat(maxSwipes) {
            if (hasText(text)) return true
            device.swipe(
                device.displayWidth / 2,
                device.displayHeight * 4 / 5,
                device.displayWidth / 2,
                device.displayHeight / 3,
                20,
            )
            device.waitForIdle(500)
        }
        return hasText(text)
    }

    /** dp = px / (densityDpi / 160). */
    fun pxToDp(px: Int): Float {
        val densityDpi = context.resources.displayMetrics.densityDpi
        return px / (densityDpi / 160f)
    }

}
