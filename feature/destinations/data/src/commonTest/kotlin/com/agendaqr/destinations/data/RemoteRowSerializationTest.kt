package com.agendaqr.destinations.data

import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse

/**
 * Regresión del mapeo fila-remota ↔ esquema SQL (PGRST204).
 *
 * PostgREST resuelve columnas por el nombre serializado; el esquema de
 * `supabase/migrations/001_v1.sql` es snake_case (`user_id`, `created_at`,
 * `person_or_entity`, ...) y las filas Kotlin usan camelCase, por lo que TODO
 * push remoto fallaba con "Could not find the 'createdAt' column". Este test
 * fija el contrato de nombres de cada fila contra el esquema real.
 */
class RemoteRowSerializationTest {
    private val json = Json { encodeDefaults = true; explicitNulls = true }

    @Test
    fun operation_row_serializes_with_schema_snake_case_columns() {
        val row = OperationRow(
            id = "op-1",
            userId = "user-1",
            type = "PAGO",
            occurredAt = Instant.fromEpochMilliseconds(1),
            createdAt = Instant.fromEpochMilliseconds(2),
            updatedAt = Instant.fromEpochMilliseconds(3),
            amount = "10",
            currency = "BOB",
            personOrEntity = "Alguien",
            destinationId = "dest-1",
            concept = "concepto",
            note = "nota",
            contextId = "ctx-1",
        )
        val encoded = json.encodeToString(OperationRow.serializer(), row)
        listOf(
            "\"id\"", "\"user_id\"", "\"type\"", "\"occurred_at\"", "\"created_at\"",
            "\"updated_at\"", "\"amount\"", "\"currency\"", "\"person_or_entity\"",
            "\"destination_id\"", "\"concept\"", "\"note\"", "\"context_id\"",
        ).forEach { column -> assertEquals(true, column in encoded, "missing column $column in $encoded") }
        listOf("\"userId\"", "\"occurredAt\"", "\"createdAt\"", "\"updatedAt\"", "\"personOrEntity\"", "\"destinationId\"", "\"contextId\"")
            .forEach { camel -> assertEquals(false, camel in encoded, "camelCase key $camel leaked into row JSON") }
    }

    @Test
    fun destination_row_serializes_with_schema_snake_case_columns() {
        val row = DestinationRow(
            id = "dest-1",
            userId = "user-1",
            name = "nombre",
            qrRawContent = "payload",
            qrKind = "UNKNOWN",
            category = null,
            note = null,
            favorite = true,
            createdAt = Instant.fromEpochMilliseconds(1),
            updatedAt = Instant.fromEpochMilliseconds(2),
            contextId = "ctx-1",
        )
        val encoded = json.encodeToString(DestinationRow.serializer(), row)
        listOf("\"user_id\"", "\"qr_raw_content\"", "\"qr_kind\"", "\"created_at\"", "\"updated_at\"", "\"context_id\"")
            .forEach { column -> assertEquals(true, column in encoded, "missing column $column in $encoded") }
        listOf("\"userId\"", "\"qrRawContent\"", "\"qrKind\"", "\"createdAt\"", "\"updatedAt\"", "\"contextId\"")
            .forEach { camel -> assertEquals(false, camel in encoded, "camelCase key $camel leaked into row JSON") }
    }

    @Test
    fun context_row_serializes_with_schema_snake_case_columns() {
        val row = ContextRow(
            id = "ctx-1",
            userId = "user-1",
            name = "Contexto",
            note = null,
            createdAt = Instant.fromEpochMilliseconds(1),
            updatedAt = Instant.fromEpochMilliseconds(2),
        )
        val encoded = json.encodeToString(ContextRow.serializer(), row)
        listOf("\"user_id\"", "\"created_at\"", "\"updated_at\"")
            .forEach { column -> assertEquals(true, column in encoded, "missing column $column in $encoded") }
        listOf("\"userId\"", "\"createdAt\"", "\"updatedAt\"")
            .forEach { camel -> assertEquals(false, camel in encoded, "camelCase key $camel leaked into row JSON") }
    }

    @Test
    fun comprobante_row_serializes_with_schema_snake_case_columns() {
        val row = ComprobanteRow(
            id = "rec-1",
            userId = "user-1",
            filePath = "user-1/rec-1.png",
            mimeType = "image/png",
            extension = "png",
            createdAt = Instant.fromEpochMilliseconds(1),
            updatedAt = Instant.fromEpochMilliseconds(2),
            provenance = "RECIBIDO",
            operationId = "op-1",
            contextId = "ctx-1",
        )
        val encoded = json.encodeToString(ComprobanteRow.serializer(), row)
        listOf(
            "\"id\"", "\"user_id\"", "\"file_path\"", "\"mime_type\"", "\"extension\"",
            "\"created_at\"", "\"updated_at\"", "\"provenance\"", "\"operation_id\"", "\"context_id\"",
        ).forEach { column -> assertEquals(true, column in encoded, "missing column $column in $encoded") }
        listOf("\"userId\"", "\"filePath\"", "\"mimeType\"", "\"createdAt\"", "\"updatedAt\"", "\"operationId\"", "\"contextId\"")
            .forEach { camel -> assertEquals(false, camel in encoded, "camelCase key $camel leaked into row JSON") }
        // Los decodificadores de selección de PostgREST devuelven exactamente las
        // columnas del esquema: la fila también debe poder decodificar eso.
        val decoded = json.decodeFromString(
            ComprobanteRow.serializer(),
            "{\"id\":\"rec-1\",\"user_id\":\"user-1\",\"file_path\":\"p\",\"mime_type\":null,\"extension\":\"png\"," +
                "\"created_at\":\"1970-01-01T00:00:00.001Z\",\"updated_at\":\"1970-01-01T00:00:00.002Z\"," +
                "\"provenance\":null,\"operation_id\":null,\"context_id\":null}",
        )
        assertEquals("user-1", decoded.userId)
        assertEquals("png", decoded.extension)
    }

    @Test
    fun no_row_json_contains_legacy_camel_case_only_keys() {
        // Barrido final: ninguna fila debe serializarse sin underscore en claves compuestas.
        val samples = listOf(
            json.encodeToString(
                OperationRow.serializer(),
                OperationRow("op", "u", "PAGO", Instant.fromEpochMilliseconds(1), Instant.fromEpochMilliseconds(1)),
            ),
            json.encodeToString(
                ComprobanteRow.serializer(),
                ComprobanteRow("rec", "u", "p", createdAt = Instant.fromEpochMilliseconds(1), updatedAt = Instant.fromEpochMilliseconds(1)),
            ),
        )
        samples.forEach { encoded ->
            assertFalse("\"userId\"" in encoded, "camelCase userId in $encoded")
        }
    }
}
