package com.agendaqr.android

import android.content.Context
import android.graphics.Bitmap
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiObject2
import java.io.File
import kotlin.test.fail

/** QA ronda 2 — utilidades compartidas de las pruebas instrumentadas. */
object Qa {

    val instrumentation get() = InstrumentationRegistry.getInstrumentation()
    val device: UiDevice get() = UiDevice.getInstance(instrumentation)
    val context: Context get() = instrumentation.targetContext

    fun launchApp() {
        device.executeShellCommand(
            "am start -n com.agendaqr.app/com.agendaqr.android.MainActivity",
        )
        device.waitForIdle(2_000)
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

    /** Captura a /sdcard (fuera del repo; se recupera con adb pull). */
    fun screenshot(name: String) {
        val cap = device.takeScreenshot()
        check(cap is Bitmap) { "screenshot nulo" }
        val dir = File("/sdcard/agendaqr-ronda2").apply { mkdirs() }
        File(dir, "$name.png").outputStream().use { cap.compress(Bitmap.CompressFormat.PNG, 100, it) }
        cap.recycle()
    }

    fun shell(cmd: String): String = device.executeShellCommand(cmd)

    /** dp = px / (densityDpi / 160). */
    fun pxToDp(px: Int): Float {
        val densityDpi = context.resources.displayMetrics.densityDpi
        return px / (densityDpi / 160f)
    }

    fun dumpNodes(): List<UiObject2> = device.findObjects(By.clazz("android.view.View"))
}
