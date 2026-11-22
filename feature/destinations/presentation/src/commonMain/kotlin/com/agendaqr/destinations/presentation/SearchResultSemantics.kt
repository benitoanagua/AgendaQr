package com.agendaqr.destinations.presentation

import com.agendaqr.destinations.domain.AgendaSearchResult
import com.agendaqr.destinations.domain.AgendaSearchResultType

/** Título visible de un resultado de búsqueda: nunca el nombre técnico del
 * enum (PAGO/COBRO...), siempre la palabra visible. Los títulos normales
 * (p. ej. "Mercado Central") pasan sin cambios. */
fun searchResultTitle(result: AgendaSearchResult): String =
    when (result.type) {
        AgendaSearchResultType.ACTIVITY -> when (val t = result.title.uppercase()) {
            "PAGO" -> "Pago"
            "COBRO" -> "Cobro"
            else -> result.title
        }
        else -> result.title
    }

/** Subtítulo visible de un resultado de búsqueda. */
fun searchResultSubtitle(result: AgendaSearchResult): String? =
    when (result.type) {
        AgendaSearchResultType.COMPROBANTE -> result.subtitle?.let { s ->
            when (s) {
                "RECIBIDO" -> "Recibido"
                "ENVIADO" -> "Enviado"
                "DESCONOCIDO" -> "Origen desconocido"
                else -> s
            }
        }
        else -> result.subtitle
    }
