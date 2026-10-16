package com.agendaqr.destinations.presentation

import org.junit.Test
import kotlin.test.assertTrue

/**
 * Contrato de copy (ADR-0018 D6): toda cadena visible de [AppStrings]
 * cabe en 60 caracteres para no romper la lectura de una pantalla con un
 * solo mensaje. La única excepción documentada es el prefijo de la
 * explicación del comprobante entrante, cuya segunda línea lleva el
 * nombre del archivo.
 *
 * La prueba refleja las propiedades del objeto y fija el presupuesto: una
 * frase nueva que se pase de largo aparece aquí antes de llegar a la UI.
 */
class AppStringsCopyContractTest {

    @Test
    fun every_string_stays_within_the_60_character_budget() {
        val documentedExceptions = setOf("incomingReceiptExplanationPrefix")
        val budget = 60
        val offenders = AppStrings::class.java.declaredFields
            .filter { it.type == String::class.java && it.name !in documentedExceptions }
            .mapNotNull { field ->
                field.isAccessible = true
                val value = field.get(AppStrings) as? String ?: return@mapNotNull null
                if (value.length > budget) "${field.name} (${value.length})" else null
            }
            .sorted()
        assertTrue(
            offenders.isEmpty(),
            "Cadenas de AppStrings sobre $budget caracteres: $offenders",
        )
    }
}
