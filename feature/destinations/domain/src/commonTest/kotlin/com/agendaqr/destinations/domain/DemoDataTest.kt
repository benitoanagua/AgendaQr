package com.agendaqr.destinations.domain

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals
import kotlin.test.assertTrue

class DemoDataTest {

    private val qrImage: (String) -> String = { content -> "qr:$content" }

    @Test
    fun dataset_is_abundant_and_deterministic() {
        val first = buildDemoDataset(qrImage)
        val second = buildDemoDataset(qrImage)
        assertEquals(first, second)
        assertTrue(first.destinations.size >= 10, "destinos abundantes para scroll/filtros")
        assertTrue(first.contexts.size >= 3, "varios contextos")
        assertTrue(first.operations.size >= 10, "operaciones de pago y cobro")
        assertTrue(first.comprobantes.size >= 5, "comprobantes asociados y sueltos")
    }

    @Test
    fun covers_required_flows() {
        val dataset = buildDemoDataset(qrImage)
        val types = dataset.operations.map { it.type }.toSet()
        assertTrue(OperationType.PAGO in types)
        assertTrue(OperationType.COBRO in types)
        // Con y sin contexto.
        assertTrue(dataset.operations.any { it.contextId != null })
        assertTrue(dataset.operations.any { it.contextId == null })
        // Comprobantes asociados y no asociados.
        assertTrue(dataset.comprobantes.any { it.operationId != null })
        assertTrue(dataset.comprobantes.any { it.operationId == null })
        // Operación mínima sin comprobante (adjuntar después).
        assertTrue(dataset.operations.any { op ->
            op.amount == null && dataset.comprobantes.none { it.operationId == op.id }
        })
    }

    @Test
    fun exposes_duplicates_without_blocking() {
        val dataset = buildDemoDataset(qrImage)
        // Lote: misma huella dos veces → duplicado tratable.
        assertTrue(dataset.importBatch.duplicates.isNotEmpty())
        assertTrue(dataset.importBatch.uniqueRecognized.isNotEmpty())
        // Pendientes y fallidos para revisión parcial.
        assertTrue(dataset.importBatch.pendingReview.isNotEmpty())
        // Comprobantes duplicados (mismo archivo en una operación).
        val files = dataset.comprobantes.map { it.file }
        assertNotEquals(files.size, files.toSet().size)
    }

    @Test
    fun qr_images_come_from_the_provider_never_arbitrary() {
        val seen = mutableListOf<String>()
        buildDemoDataset { content ->
            seen.add(content)
            "qr:$content"
        }
        // Cada destino aporta su contenido al generador.
        assertTrue(seen.size >= 10)
        val dataset = buildDemoDataset(qrImage)
        assertTrue(dataset.destinations.all { it.qr.encoded.startsWith("qr:") })
        assertTrue(dataset.destinations.all { it.qr.content != null })
    }

    @Test
    fun every_id_is_demo_namespaced() {
        val dataset = buildDemoDataset(qrImage)
        val ids = dataset.contexts.map { it.id } +
            dataset.destinations.map { it.id } +
            dataset.operations.map { it.id } +
            dataset.comprobantes.map { it.id }
        assertTrue(ids.isNotEmpty())
        assertTrue(ids.all { it.startsWith("demo-") }, "ids demo-*: $ids")
        assertEquals(ids.size, ids.toSet().size, "ids únicos")
    }

    @Test
    fun demo_profile_id_is_stable_and_detectable() {
        assertEquals("demo-profile-v1", DEMO_PROFILE_ID)
        assertTrue(isDemoProfile(DEMO_PROFILE_ID))
        assertTrue(!isDemoProfile("profile-real"))
    }
}
