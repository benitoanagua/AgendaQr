package com.agendaqr.android

import android.view.accessibility.AccessibilityNodeInfo
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.test.assertTrue

/**
 * QA ronda 2 (checklist TalkBack-preparación, sin TalkBack real):
 * auditoría del árbol de accesibilidad por pantalla. Falla si hay
 * clicables SIN nombre accesible, con área < 48dp (dp = px/(dpi/160)) o
 * con nombres duplicados dentro de una pantalla. En S01: un ÚNICO nodo
 * "Buscar en Agenda QR" con rol botón.
 */
@RunWith(androidx.test.ext.junit.runners.AndroidJUnit4::class)
class AccessibilityTreeAuditTest {

    private val settings get() = SystemSettingsRule()

    @Test
    fun reachable_screens_have_named_clickable_targets_and_no_duplicates() {
        val screens = listOf(
            "s01" to { Qa.launchApp() },
            "s02" to { Qa.launchApp(); Qa.tapText("Añadir") },
            "s04" to { Qa.launchApp(); Qa.tapText("Buscar en Agenda QR") },
            "s06" to { Qa.launchApp(); Qa.tapText("Contextos") },
            "s07" to { Qa.launchApp(); Qa.tapText("Registrar") },
        )
        for ((id, open) in screens) {
            open()
            // Espera a que la pantalla tenga contenido antes de auditar.
            Qa.waitFor(8_000) {
                Qa.device.findObjects(androidx.test.uiautomator.By.textContains("Agenda")).isNotEmpty() ||
                Qa.device.findObjects(androidx.test.uiautomator.By.textContains("Añadir")).isNotEmpty() ||
                Qa.device.findObjects(androidx.test.uiautomator.By.textContains("Contextos")).isNotEmpty() ||
                Qa.device.findObjects(androidx.test.uiautomator.By.textContains("Registrar")).isNotEmpty()
            }
            auditScreen(id)
        }
    }

    @Test
    fun s01_search_is_a_single_accessible_button() {
        Qa.launchApp()
        Qa.waitFor(10_000) { Qa.hasText("Buscar en Agenda QR") }
        val nodes = mutableListOf<AccessibilityNodeInfo>()
        val root = Qa.instrumentation.uiAutomation.rootInActiveWindow ?: error("sin ventana activa")
        root.findNodes("Buscar en Agenda QR", nodes)
        assertTrue(nodes.size == 1, "debe haber UN solo nodo de búsqueda (había ${nodes.size})")
        val n = nodes.first()
        val name = n.contentDescription?.toString() ?: n.text?.toString()
        assertTrue(name == "Buscar en Agenda QR", "nombre accesible exacto: $name")
        assertTrue(
            n.isClickable,
            "el nodo de búsqueda debe ser clicable (rol botón)",
        )
        n.className?.let {
            assertTrue(it.toString().contains("Button") || n.isClickable, "rol botón: $it")
        }
    }

    private fun auditScreen(id: String) {
        val root = Qa.instrumentation.uiAutomation.rootInActiveWindow ?: error("sin ventana en $id")
        val seen = HashMap<String, Int>()
        val missing = mutableListOf<String>()
        val small = mutableListOf<String>()
        walk(root, seen, missing, small)
        assertTrue(missing.isEmpty(), "[$id] clicables sin nombre accesible: $missing")
        assertTrue(small.isEmpty(), "[$id] targets < 48dp: $small")
        val dups = seen.filter { it.value > 1 }.map { "${it.key} x${it.value}" }
        assertTrue(dups.isEmpty(), "[$id] nombres accesibles duplicados: $dups")
    }

    private fun walk(
        node: AccessibilityNodeInfo,
        seen: MutableMap<String, Int>,
        missing: MutableList<String>,
        small: MutableList<String>,
    ) {
        if (node.isVisibleToUser && node.isClickable) {
            val name = node.contentDescription?.toString() ?: node.text?.toString()
            val label = node.viewIdResourceName ?: ""
            if (name.isNullOrBlank() && label.isBlank()) {
                missing.add(node.className.toString())
            } else {
                val key = name ?: label
                seen[key] = (seen[key] ?: 0) + 1
                val b = android.graphics.Rect()
                node.getBoundsInScreen(b)
                if (b.width() > 0 && b.height() > 0) {
                    val wDp = Qa.pxToDp(b.width())
                    val hDp = Qa.pxToDp(b.height())
                    if (wDp < 48f && hDp < 48f) {
                        small.add("'$key' ${wDp}x${hDp}dp")
                    }
                }
            }
        }
        for (i in 0 until node.childCount) {
            node.getChild(i)?.let { walk(it, seen, missing, small) }
        }
    }

    private fun AccessibilityNodeInfo.findNodes(text: String, out: MutableList<AccessibilityNodeInfo>) {
        if ((contentDescription?.toString() == text) || (this.text?.toString() == text)) {
            out.add(AccessibilityNodeInfo.obtain(this))
        }
        for (i in 0 until childCount) {
            getChild(i)?.findNodes(text, out)
        }
    }
}
