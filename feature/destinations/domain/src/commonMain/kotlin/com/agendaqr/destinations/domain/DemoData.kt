package com.agendaqr.destinations.domain

/**
 * Modo de demostración: datos ficticios, deterministas y abundantes para
 * explorar la app sin cuenta ni conexión.
 *
 * Aislamiento:
 * - vive bajo el perfil fijo [DEMO_PROFILE_ID] (`*.local.demo-profile-v1`),
 *   nunca bajo el perfil real del usuario;
 * - no se carga automáticamente: solo al pulsar «Explorar demostración»;
 * - no se sincroniza: el grafo desactiva recovery/sync UI para este perfil;
 * - la carga es idempotente (reentrar no duplica).
 */
const val DEMO_PROFILE_ID: String = "demo-profile-v1"

fun isDemoProfile(profileId: String): Boolean = profileId == DEMO_PROFILE_ID

/** Día base determinista: 2024-01-15T10:30:00Z (igual que capturas). */
const val DEMO_EPOCH_MILLIS: Long = 1705314600000L

private const val DAY_MILLIS: Long = 86_400_000L

data class DemoDataset(
    val contexts: List<Context>,
    val destinations: List<Destination>,
    val operations: List<Operation>,
    val comprobantes: List<Comprobante>,
    val importBatch: ImportBatch,
)

/**
 * Construye el dataset de demostración. [qrImage] produce un PNG QR legible
 * en Base64 para cada contenido (en producción lo aporta la plataforma;
 * en tests un marcador). Nunca imágenes arbitrarias como si fueran QR.
 */
fun buildDemoDataset(qrImage: (String) -> String): DemoDataset {
    fun day(offset: Int): Long = DEMO_EPOCH_MILLIS - offset * DAY_MILLIS
    val qr = { content: String -> QrAsset(encoded = qrImage(content), content = content) }

    val contexts = listOf(
        Context(id = "demo-ctx-mercado", name = "Mercado", note = "Compras semanales", createdAt = day(60), updatedAt = day(60)),
        Context(id = "demo-ctx-ferreteria", name = "Ferretería", note = "Obras de casa", createdAt = day(55), updatedAt = day(55)),
        Context(id = "demo-ctx-servicios", name = "Servicios", note = "Luz, agua, internet", createdAt = day(50), updatedAt = day(50)),
        Context(id = "demo-ctx-familia", name = "Familia", note = null, createdAt = day(45), updatedAt = day(45)),
    )

    val destinations = listOf(
        Destination("demo-d-mercado", "Mercado Central", qr("https://agendaqr.app/demo/mercado"), "Almacén", "Frutas y verduras", true, day(60), day(10), "demo-ctx-mercado"),
        Destination("demo-d-ferreteria", "Ferretería El Tornillo", qr("https://agendaqr.app/demo/ferreteria"), "Hogar", "Herramientas", true, day(55), day(9), "demo-ctx-ferreteria"),
        Destination("demo-d-luz", "Edesur · Luz", qr("https://agendaqr.app/demo/luz"), "Servicios", "Factura de luz", false, day(50), day(8), "demo-ctx-servicios"),
        Destination("demo-d-agua", "Aysa · Agua", qr("https://agendaqr.app/demo/agua"), "Servicios", null, false, day(49), day(8), "demo-ctx-servicios"),
        Destination("demo-d-internet", "Fibertel · Internet", qr("https://agendaqr.app/demo/internet"), "Servicios", "Plan 100 MB", false, day(48), day(7), "demo-ctx-servicios"),
        Destination("demo-d-carniceria", "Carnicería Don Bife", qr("https://agendaqr.app/demo/carniceria"), "Almacén", "Compra semanal", true, day(40), day(6), "demo-ctx-mercado"),
        Destination("demo-d-verduleria", "Verdulería Mercedes", qr("https://agendaqr.app/demo/verduleria"), "Almacén", null, false, day(38), day(6), "demo-ctx-mercado"),
        Destination("demo-d-libreria", "Librería Central", qr("https://agendaqr.app/demo/libreria"), "Educación", "Útiles", false, day(30), day(5), null),
        Destination("demo-d-farmacia", "Farmacia del Pueblo", qr("https://agendaqr.app/demo/farmacia"), "Salud", null, false, day(28), day(5), null),
        Destination("demo-d-cafeteria", "Cafetería Central", qr("https://agendaqr.app/demo/cafeteria"), "Gastronomía", "Desayunos de trabajo", false, day(20), day(4), null),
        Destination("demo-d-taller", "Taller Hermanos", qr("https://agendaqr.app/demo/taller"), "Vehículo", "Service auto", false, day(15), day(3), "demo-ctx-familia"),
        Destination("demo-d-colegio", "Colegio San Martín", qr("https://agendaqr.app/demo/colegio"), "Educación", "Cuota mensual", false, day(12), day(2), "demo-ctx-familia"),
    )

    val operations = listOf(
        Operation("demo-op-01", OperationType.PAGO, day(2), day(2), day(2), "4850", "ARS", "Cafetería Central", "demo-d-cafeteria", "Desayuno de trabajo", null, null),
        Operation("demo-op-02", OperationType.COBRO, day(3), day(3), day(3), "25000", "ARS", "Verdulería Mercedes", null, "Venta semanal", null, "demo-ctx-mercado"),
        Operation("demo-op-03", OperationType.PAGO, day(5), day(5), day(5), "12340", "ARS", "Edesur", "demo-d-luz", "Factura enero", "Vence el 20", "demo-ctx-servicios"),
        Operation("demo-op-04", OperationType.PAGO, day(6), day(6), day(6), "8200", "ARS", "Aysa", "demo-d-agua", null, null, "demo-ctx-servicios"),
        Operation("demo-op-05", OperationType.PAGO, day(8), day(8), day(8), "6900", "ARS", "Fibertel", "demo-d-internet", "Internet 100 MB", null, "demo-ctx-servicios"),
        Operation("demo-op-06", OperationType.PAGO, day(9), day(9), day(9), "15200", "ARS", "Carnicería Don Bife", "demo-d-carniceria", "Asado domingo", null, "demo-ctx-mercado"),
        Operation("demo-op-07", OperationType.COBRO, day(10), day(10), day(10), "30000", "ARS", "Familia Gómez", null, "Préstamo devuelto", null, "demo-ctx-familia"),
        Operation("demo-op-08", OperationType.PAGO, day(11), day(11), day(11), "5400", "ARS", "Librería Central", "demo-d-libreria", "Útiles", null, null),
        Operation("demo-op-09", OperationType.PAGO, day(12), day(12), day(12), null, null, "Farmacia del Pueblo", null, null, "Sin importe: registrado al paso", null),
        Operation("demo-op-10", OperationType.COBRO, day(13), day(13), day(13), "18000", "ARS", "Taller Hermanos", null, "Reparación", null, "demo-ctx-familia"),
        Operation("demo-op-11", OperationType.PAGO, day(14), day(14), day(14), "22000", "ARS", "Colegio San Martín", "demo-d-colegio", "Cuota enero", null, "demo-ctx-familia"),
        Operation("demo-op-12", OperationType.PAGO, day(15), day(15), day(15), "9600", "ARS", "Ferretería El Tornillo", "demo-d-ferreteria", "Pintura", null, "demo-ctx-ferreteria"),
        // Duplicado intencional: mismo importe/persona/fecha que op-01 para
        // ejercitar la advertencia de duplicado sin bloqueo.
        Operation("demo-op-13", OperationType.PAGO, day(2), day(1), day(1), "4850", "ARS", "Cafetería Central", "demo-d-cafeteria", "Desayuno de trabajo", "Posible duplicado de demo-op-01", null),
        // Operación sin contexto ni destino (RF-08 mínimo ampliado).
        Operation("demo-op-14", OperationType.PAGO, day(16), day(16), day(16), null, null, null, null, null, null, null),
        // Operación sin comprobante (flujo «adjuntar después»).
        Operation("demo-op-15", OperationType.COBRO, day(17), day(17), day(17), "12000", "ARS", "Vecino Luis", null, "Arreglo", null, null),
    )

    val comprobantes = listOf(
        Comprobante("demo-rc-01", "demo-rc-01.png", "image/png", "png", day(2), day(2), ReceiptProvenance.RECIBIDO, "demo-op-01", null),
        Comprobante("demo-rc-02", "demo-rc-02.png", "image/png", "png", day(3), day(3), ReceiptProvenance.ENVIADO, "demo-op-02", "demo-ctx-mercado"),
        Comprobante("demo-rc-03", "demo-rc-03.png", "image/png", "png", day(5), day(5), ReceiptProvenance.RECIBIDO, "demo-op-03", "demo-ctx-servicios"),
        // Duplicado intencional del rc-01 (misma operación) para RF-14.
        Comprobante("demo-rc-04", "demo-rc-01.png", "image/png", "png", day(2), day(2), ReceiptProvenance.RECIBIDO, "demo-op-01", null),
        // Sin asociar: bandeja de respaldos + proveniencias variadas.
        Comprobante("demo-rc-05", "demo-rc-05.png", "image/png", "png", day(7), day(7), ReceiptProvenance.DESCONOCIDO, null, null),
        Comprobante("demo-rc-06", "demo-rc-06.pdf", "application/pdf", "pdf", day(9), day(9), ReceiptProvenance.RECIBIDO, null, "demo-ctx-mercado"),
        Comprobante("demo-rc-07", "demo-rc-07.png", "image/png", "png", day(11), day(11), ReceiptProvenance.ENVIADO, null, null),
    )

    val importBatch = ImportBatch(
        candidates = listOf(
            ImportCandidate("demo-imp-01", ImportKind.QR, "demo-fp-01", "image/png", "png", null, qr("https://agendaqr.app/demo/nuevo-1")),
            ImportCandidate("demo-imp-02", ImportKind.QR, "demo-fp-02", "image/png", "png", null, qr("https://agendaqr.app/demo/nuevo-2")),
            ImportCandidate("demo-imp-03", ImportKind.COMPROBANTE, "demo-fp-03", "image/png", "png", null, null, "demo-payload-03"),
            // Duplicado dentro del lote (misma huella que imp-01).
            ImportCandidate("demo-imp-04", ImportKind.QR, "demo-fp-01", "image/png", "png", null, qr("https://agendaqr.app/demo/nuevo-1")),
            // Pendiente: ilegible, con error de clasificación.
            ImportCandidate("demo-imp-05", ImportKind.DESCONOCIDO, "demo-fp-05", "image/png", "png", null, null, "demo-payload-05", "No se pudo clasificar"),
            // Fallido: payload corrupto.
            ImportCandidate("demo-imp-06", ImportKind.DESCONOCIDO, "demo-fp-06", "image/png", "png", null, null, null, "Archivo ilegible"),
        ),
    )

    return DemoDataset(contexts, destinations, operations, comprobantes, importBatch)
}
