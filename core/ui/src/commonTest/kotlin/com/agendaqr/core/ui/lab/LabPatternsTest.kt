package com.agendaqr.core.ui.lab

import com.agendaqr.core.ui.lab.model.LabPatterns
import kotlin.test.Test
import kotlin.test.assertTrue

class LabPatternsTest {

    @Test
    fun patterns_reference_only_real_contracts() {
        assertTrue(LabPatterns.validate().isEmpty(), LabPatterns.validate().joinToString("\n"))
    }

    @Test
    fun patterns_cover_the_mandated_compositions() {
        val ids = LabPatterns.all.map { it.id }.toSet()
        assertTrue(
            ids.containsAll(
                setOf(
                    "primera-vez",
                    "fallo-puntual",
                    "fallo-contenido",
                    "fallo-aplicacion",
                    "pantalla-completa",
                    "incorporacion",
                    "bloqueo-opcional",
                    "confirmacion-destructiva",
                ),
            ),
        )
    }

    @Test
    fun demos_declare_their_limitation_explicitly() {
        LabPatterns.all.filter { it.demo }.forEach { pattern ->
            assertTrue(pattern.demoNote.isNotBlank(), "${pattern.id} es demo sin nota explícita")
        }
    }
}
