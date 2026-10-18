package com.agendaqr.destinations.data

import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

private class MapStore(private val backing: MutableMap<String, String> = mutableMapOf()) : DestinationStore {
    override fun read(key: String): String? = backing[key]
    override fun write(key: String, value: String) { backing[key] = value }
    override fun remove(key: String) { backing.remove(key) }
    override fun keys(): Set<String> = backing.keys.toSet()
    fun snapshot(): Map<String, String> = backing.toMap()
}

class LocalProfileScopeTest {

    @AfterTest
    fun reset() {
        LocalProfileScope.activate("reset")
    }

    @Test
    fun profileScopedKey_is_namespaced_and_distinct_from_legacy() {
        LocalProfileScope.activate("profile-1")
        assertEquals("agendaqr.destinations.v1.local.profile-1", profileScopedKey("agendaqr.destinations.v1"))
        // el namespace local nunca colisiona con un id remoto (falta el segmento .local)
        assertTrue(profileScopedKey("agendaqr.destinations.v1") != "agendaqr.destinations.v1.<remote-user>")
    }

    @Test
    fun different_profiles_are_isolated() {
        LocalProfileScope.activate("a")
        val keyA = profileScopedKey("agendaqr.destinations.v1")
        LocalProfileScope.activate("b")
        val keyB = profileScopedKey("agendaqr.destinations.v1")
        assertTrue(keyA != keyB)
    }
}

class LegacyLocalDataMigrationTest {

    @Test
    fun migrates_legacy_collections_into_profile_namespace() {
        val store = MapStore(
            mutableMapOf(
                "agendaqr.destinations.v1.remote-user" to "[\"d1\"]",
                "agendaqr.operations.v1.remote-user" to "[\"o1\"]",
                "agendaqr.sync.queue.v1.remote-user" to "[]",
            ),
        )
        val report = LegacyLocalDataMigration(store).migrate(legacyUserId = "remote-user", profileId = "profile-1")

        assertTrue(report.hadLegacyData)
        assertEquals(3, report.migratedCollections)
        assertEquals("[\"d1\"]", store.read("agendaqr.destinations.v1.local.profile-1"))
        assertEquals("[\"o1\"]", store.read("agendaqr.operations.v1.local.profile-1"))
        // el origen se conserva (no se borra hasta validar)
        assertEquals("[\"d1\"]", store.read("agendaqr.destinations.v1.remote-user"))
    }

    @Test
    fun migration_is_idempotent_and_does_not_overwrite_target() {
        val store = MapStore(
            mutableMapOf(
                "agendaqr.destinations.v1.remote-user" to "legacy",
                "agendaqr.destinations.v1.local.profile-1" to "current",
            ),
        )
        val migration = LegacyLocalDataMigration(store)
        val first = migration.migrate("remote-user", "profile-1")
        val second = migration.migrate("remote-user", "profile-1")

        assertTrue(first.hadLegacyData)
        assertEquals(0, first.migratedCollections, "no debe pisar datos ya migrados")
        assertEquals("current", store.read("agendaqr.destinations.v1.local.profile-1"))
        assertEquals(0, second.migratedCollections)
    }

    @Test
    fun no_legacy_data_reports_empty() {
        val store = MapStore()
        val report = LegacyLocalDataMigration(store).migrate("remote-user", "profile-1")
        assertEquals(0, report.migratedCollections)
        assertEquals(false, report.hadLegacyData)
        assertNull(store.read("agendaqr.destinations.v1.local.profile-1"))
    }

    @Test
    fun does_not_mix_other_accounts() {
        val store = MapStore(
            mutableMapOf(
                "agendaqr.destinations.v1.other-user" to "other",
                "agendaqr.destinations.v1.remote-user" to "mine",
            ),
        )
        LegacyLocalDataMigration(store).migrate("remote-user", "profile-1")
        assertEquals("mine", store.read("agendaqr.destinations.v1.local.profile-1"))
        assertNull(store.read("agendaqr.destinations.v1.local.other-user"))
    }
}

class LegacyLocalProfileIdTest {

    @Test
    fun detects_legacy_id_from_storage_keys_without_any_remote_client() {
        val store = MapStore(
            mutableMapOf(
                "agendaqr.destinations.v1.2f3a6c10-9999-4a11-8c22-aa11bb22cc33" to "[]",
                "agendaqr.contexts.v1.local.9f8e7d6c" to "[]", // namespace nuevo: NO es legado
            ),
        )
        assertEquals(
            "2f3a6c10-9999-4a11-8c22-aa11bb22cc33",
            legacyLocalProfileIdOrNull(store),
        )
    }

    @Test
    fun ignores_local_namespace_corrupt_suffixes_and_reserved_names() {
        val store = MapStore(
            mutableMapOf(
                "agendaqr.destinations.v1.local.profile-1" to "[]",
                "agendaqr.sync.queue.v1.local.profile-1.corrupt.123" to "{}",
                "agendaqr.destinations.v1.legacy" to "[]",
                "agendaqr.local.profile.v1" to "{}",
                "agendaqr.local.protection.v1" to "{}",
            ),
        )
        assertNull(legacyLocalProfileIdOrNull(store))
    }

    @Test
    fun returns_null_when_there_is_no_legacy_data() {
        assertNull(legacyLocalProfileIdOrNull(MapStore()))
    }
}

class RemoteRepositoryConstructionTest {

    /**
     * Construir los repositorios remotos NO debe resolver el cliente Supabase:
     * es lo que permite montar el grafo local sin configuración remota. Un
     * proveedor que explota al invocarse demuestra que la construcción es
     * perezosa.
     */
    @Test
    fun constructing_remote_repositories_does_not_touch_the_client() {
        val exploding: () -> Nothing = { error("client must not be resolved at construction") }
        SupabaseContextRepository(clientProvider = exploding)
        SupabaseDestinationRepository(clientProvider = exploding)
        SupabaseOperationRepository(clientProvider = exploding)
        SupabaseComprobanteRepository(clientProvider = exploding)
    }
}
