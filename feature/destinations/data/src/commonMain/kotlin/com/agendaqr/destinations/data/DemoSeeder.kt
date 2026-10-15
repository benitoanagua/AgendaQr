package com.agendaqr.destinations.data

import com.agendaqr.destinations.domain.DEMO_PROFILE_ID
import com.agendaqr.destinations.domain.DemoDataset
import com.agendaqr.destinations.domain.buildDemoDataset
import kotlinx.coroutines.flow.first

/**
 * Clave de almacenamiento del perfil de demostración. Usa el mismo esquema
 * que [profileScopedKey] pero con el id fijo de demo, sin tocar el
 * [LocalProfileScope] activo (el perfil real del usuario no se altera).
 */
fun demoStorageKey(prefix: String): String = "$prefix.local.$DEMO_PROFILE_ID"

/**
 * Carga idempotente de datos de demostración en el namespace aislado de demo.
 *
 * - No borra ni toca el namespace del perfil real.
 * - Si ya hay destinos demo, no duplica (reentrar es seguro).
 * - No encola sincronización: escribe en los repositorios locales crudos,
 *   nunca en los wrappers Sync ni en remoto.
 */
class DemoSeeder(
    private val destinations: LocalDestinationRepository =
        LocalDestinationRepository(storageKey = demoStorageKey("agendaqr.destinations.v1")),
    private val contexts: LocalContextRepository =
        LocalContextRepository(storageKey = demoStorageKey("agendaqr.contexts.v1")),
    private val operations: LocalOperationRepository =
        LocalOperationRepository(storageKey = demoStorageKey("agendaqr.operations.v1")),
    private val comprobantes: LocalComprobanteRepository =
        LocalComprobanteRepository(storageKey = demoStorageKey("agendaqr.comprobantes.v1")),
) {
    /** `true` si ya había datos demo (no se escribió nada). */
    suspend fun seedIfEmpty(qrImage: (String) -> String = ::demoQrPngBase64): SeedReport {
        val existing = destinations.observe().first()
        if (existing.isNotEmpty()) {
            val dataset = buildDemoDataset(qrImage)
            return SeedReport(alreadySeeded = true, dataset = dataset)
        }
        val dataset = buildDemoDataset(qrImage)
        dataset.contexts.forEach { contexts.save(it) }
        dataset.destinations.forEach { destinations.save(it) }
        dataset.operations.forEach { operations.save(it) }
        dataset.comprobantes.forEach { comprobantes.save(it) }
        return SeedReport(alreadySeeded = false, dataset = dataset)
    }

    suspend fun isSeeded(): Boolean = destinations.observe().first().isNotEmpty()
}

data class SeedReport(
    val alreadySeeded: Boolean,
    val dataset: DemoDataset,
)
