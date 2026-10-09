package com.agendaqr.android

import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.UiDevice
import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

/**
 * QA ronda 2 — ajuste y RESTAURACIÓN de estado del sistema.
 *
 * Toca lo que las pruebas necesitan del SO (escala de fuente, rotación,
 * escalas de animación, modo avión, permisos) vía `UiDevice.executeShellCommand`
 * y devuelve TODO a su valor original en un `finally`, aunque la prueba
 * falle (regla 5 de la pasada).
 *
 * Inmutable por ajuste: cada método lee el valor actual, lo guarda y
 * aplica el nuevo; `restore()` se invoca siempre.
 */
class SystemSettingsRule : TestRule {

    private val device: UiDevice
        get() = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    private val backups = linkedMapOf<String, String>()
    private var restoreAsRoot: String? = null

    /** Aplica cambios de sistema; [block] corre con ellos activos. */
    fun apply(
        fontScale: Float? = null,
        nightMode: Boolean? = null,
        userRotation: Int? = null,
        animations: Float? = null,
        airplane: Boolean? = null,
        block: () -> Unit,
    ) {
        if (fontScale != null) putSetting("system", "font_scale", fontScale.toString())
        if (userRotation != null) {
            // Rotación fija: desactiva el sensor y fuerza el ángulo.
            putSetting("system", "accelerometer_rotation", "0")
            putSetting("system", "user_rotation", userRotation.toString())
        }
        if (animations != null) {
            putSetting("global", "animator_duration_scale", animations.toString())
            putSetting("global", "animation_duration_scale", animations.toString())
            putSetting("global", "transition_animation_scale", animations.toString())
        }
        if (airplane != null) {
            putSetting("global", "airplane_mode_on", if (airplane) "1" else "0")
            // El cambio efectivo requiere el broadcast en algunos OEM.
            shell("am broadcast -a android.intent.action.AIRPLANE_MODE >/dev/null 2>&1")
        }
        try {
            block()
        } finally {
            restore()
        }
    }

    /** Modo oscuro: `cmd uimode night yes/no` (se restaura igual). */
    fun setNightMode(dark: Boolean) {
        val current = shell("cmd uimode night").trim()
        backups["cmd:uimode"] = current
        shell("cmd uimode night ${if (dark) "yes" else "no"}")
    }

    private fun putSetting(namespace: String, key: String, value: String) {
        val current = shell("settings get $namespace $key").trim()
        backups["$namespace:$key"] = current
        shell("settings put $namespace $key $value")
    }

    fun restore() {
        for ((key, value) in backups.entries.reversed()) {
            if (key == "cmd:uimode") {
                shell("cmd uimode night $value")
            } else {
                val (ns, k) = key.split(":")
                shell("settings put $ns $k $value")
            }
        }
        restoreAsRoot?.let { shell(it) }
        backups.clear()
    }

    private fun shell(cmd: String): String = device.executeShellCommand(cmd)

    override fun apply(base: Statement, description: Description): Statement {
        return object : Statement() {
            override fun evaluate() {
                try {
                    base.evaluate()
                } finally {
                    restore()
                    // Vuelve a la app por si una prueba la dejó en ajustes.
                    shell("am start -n com.agendaqr.app/com.agendaqr.android.MainActivity")
                }
            }
        }
    }
}
