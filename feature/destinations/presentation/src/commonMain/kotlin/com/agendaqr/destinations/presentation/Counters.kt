package com.agendaqr.destinations.presentation

/**
 * U1 — contadores en español: ninguna superficie concatena a mano
 * `"etiqueta: $n"`; todas pasan por aquí para respetar el singular de N=1.
 *
 * La concordancia es la del copy congelado para N≠1; para N=1 se usa el
 * singular honesto ("1 pendiente", "1 comprobante"). Sustantivos
 * invariables (QR) y encabezados invariables no cambian.
 */
internal fun spanishPlural(noun: String, count: Int): String = when {
    count == 1 -> noun
    noun.endsWith("z") -> noun.dropLast(1) + "ces"
    noun.endsWith("a") || noun.endsWith("e") || noun.endsWith("i") ||
        noun.endsWith("o") || noun.endsWith("u") -> noun + "s"
    else -> noun + "es"
}

/**
 * Etiqueta de contador: singular honesto para N=1.
 * `counterLabel("Comprobantes sin asociar", "Comprobante sin asociar", n)`.
 */
internal fun counterLabel(singular: String, plural: String, count: Int): String =
    if (count == 1) singular else plural

/** "Revisar N pendiente(s)" — botón S12 (contrato). */
internal fun reviewPendingLabel(pendingCount: Int): String =
    "Revisar " + pendingCount + " " + spanishPlural("pendiente", pendingCount)

/** "Comprobante(s) sin asociar: N" — bandeja de respaldos. */
internal fun unassociatedReceiptsLabel(count: Int): String =
    counterLabel("Comprobante sin asociar", "Comprobantes sin asociar", count) + ": " + count

/** "Sincronización pendiente: N" (+ reintentando cuando hay fallos). */
internal fun syncPendingLabel(count: Int, retrying: Boolean): String =
    "Sincronización pendiente: " + count + (if (retrying) " · reintentando…" else "")

/** Encabezado de sección del contexto: "QR · N", "Comprobante(s) · N". */
internal fun contextSectionLabel(nounSingular: String, nounPlural: String, count: Int): String =
    counterLabel(nounSingular, nounPlural, count) + " · " + count
