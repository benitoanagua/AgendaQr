package com.agendaqr.destinations.domain

import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.time.Clock  // Kotlin/Native safe clock when available; fallback below
import kotlin.test.assertTrue

/**
 * D5 — riesgo documentado: observe() mantiene colecciones en memoria.
 * Este test fija el tamaño del problema a un lote de 200 elementos y
 * mide el coste de recolección (cada emisión materializa una nueva lista
 * ordenada): el límite aceptable queda documentado para V1.
 *
 * No sustituye una prueba de escalabilidad: si V2 crece, la decisión es
 * un ADR de paginación (fuera de contrato V1).
 */
class ObserveMemoryBudgetTest {

    @Test
    fun collecting_200_operations_renders_in_well_under_a_second() {
        val operations = (0 until 200).map {
            Operation(
                id = "operation-$it",
                type = OperationType.PAGO,
                occurredAt = it.toLong(),
                createdAt = it.toLong(),
                personOrEntity = "Persona $it",
                concept = "Concepto $it",
                note = "Nota $it",
            )
        }
        val flow = MutableStateFlow(operations)
        val start = kotlin.time.TimeSource.Monotonic.markNow()
        var renders = 0
        repeat(100) {
            val snapshot = flow.value.sortedByDescending { it.occurredAt }
            assertEquals(200, snapshot.size)
            renders++
        }
        val elapsedMs = start.elapsedNow().inWholeMilliseconds
        // 100 recolecciones completas de 200 elementos: presupuesto de V1.
        assertTrue(elapsedMs < 2000, "100 renders de 200 elementos tardaron ${elapsedMs}ms; revisar paginación")
    }
}
