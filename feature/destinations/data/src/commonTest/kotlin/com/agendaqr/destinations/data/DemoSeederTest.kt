package com.agendaqr.destinations.data

import com.agendaqr.destinations.domain.DEMO_PROFILE_ID
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest

private class MapDestinationStore : DestinationStore {
    private val backing = mutableMapOf<String, String>()
    override fun read(key: String): String? = backing[key]
    override fun write(key: String, value: String) { backing[key] = value }
    override fun remove(key: String) { backing.remove(key) }
    override fun keys(): Set<String> = backing.keys.toSet()
    fun snapshot(): Map<String, String> = backing.toMap()
}

private class MapOperationsStore : OperationsStore {
    private val backing = mutableMapOf<String, String>()
    override fun read(key: String): String? = backing[key]
    override fun write(key: String, value: String) { backing[key] = value }
}

class DemoSeederTest {

    private fun seeder(
        destinations: MapDestinationStore = MapDestinationStore(),
        operations: MapOperationsStore = MapOperationsStore(),
    ): Triple<DemoSeeder, MapDestinationStore, MapOperationsStore> {
        val seeder = DemoSeeder(
            destinations = LocalDestinationRepository(store = destinations, storageKey = demoStorageKey("agendaqr.destinations.v1")),
            contexts = LocalContextRepository(store = destinations, storageKey = demoStorageKey("agendaqr.contexts.v1")),
            operations = LocalOperationRepository(store = operations, storageKey = demoStorageKey("agendaqr.operations.v1")),
            comprobantes = LocalComprobanteRepository(store = operations, storageKey = demoStorageKey("agendaqr.comprobantes.v1")),
        )
        return Triple(seeder, destinations, operations)
    }

    @Test
    fun seeds_demo_namespace_with_fixtures() = runTest {
        val (seeder, _, _) = seeder()
        assertFalse(seeder.isSeeded())
        val report = seeder.seedIfEmpty { content -> "qr:$content" }
        assertFalse(report.alreadySeeded)
        assertTrue(seeder.isSeeded())
        assertTrue(report.dataset.destinations.size >= 10)
        assertTrue(report.dataset.operations.size >= 10)
    }

    @Test
    fun seeding_is_idempotent() = runTest {
        val (seeder, _, _) = seeder()
        seeder.seedIfEmpty { "qr" }
        val second = seeder.seedIfEmpty { "qr" }
        assertTrue(second.alreadySeeded)
        // Sin duplicados: los ids demo siguen siendo únicos.
        val ids = second.dataset.destinations.map { it.id }
        assertEquals(ids.size, ids.toSet().size)
    }

    @Test
    fun demo_keys_never_touch_the_real_profile() = runTest {
        val destinations = MapDestinationStore()
        val operations = MapOperationsStore()
        // Datos reales preexistentes bajo otro perfil.
        destinations.write("agendaqr.destinations.v1.local.real-1", "{\"items\":[]}")
        val (seeder, destSnapshot, _) = seeder(destinations, operations)
        seeder.seedIfEmpty { "qr" }
        // Lo real sigue intacto; lo demo vive en su namespace.
        assertEquals("{\"items\":[]}", destSnapshot.read("agendaqr.destinations.v1.local.real-1"))
        val keys = destSnapshot.snapshot().keys
        assertTrue("agendaqr.destinations.v1.local.$DEMO_PROFILE_ID" in keys)
        assertTrue("agendaqr.contexts.v1.local.$DEMO_PROFILE_ID" in keys)
        assertTrue(keys.none { it.contains("real-1") && it.contains(DEMO_PROFILE_ID) })
    }

    @Test
    fun demo_profile_does_not_enable_remote_sync() {
        // El grafo desactiva recovery/sync UI para el perfil demo; aquí se
        // verifica la condición de aislamiento a nivel de identidad.
        assertTrue(demoStorageKey("agendaqr.sync.queue.v1").contains(DEMO_PROFILE_ID))
        assertTrue(demoStorageKey("agendaqr.destinations.v1").contains(".local."))
    }

    @Test
    fun leaves_real_collections_visible() = runTest {
        val destinations = MapDestinationStore()
        val (seeder, snapshot, _) = seeder(destinations, MapOperationsStore())
        seeder.seedIfEmpty { "qr" }
        // El namespace real sigue vacío/legible: la demo no lo pisa.
        assertEquals(null, snapshot.read("agendaqr.destinations.v1.local.real-1"))
        assertTrue(snapshot.read("agendaqr.destinations.v1.local.$DEMO_PROFILE_ID")!!.isNotBlank())
        // Las claves demo usan el esquema profileScopedKey del perfil demo.
        LocalProfileScope.activate(DEMO_PROFILE_ID)
        try {
            assertEquals(
                demoStorageKey("agendaqr.destinations.v1"),
                profileScopedKey("agendaqr.destinations.v1"),
            )
        } finally {
            LocalProfileScope.activate("reset")
        }
    }
}
