package com.agendaqr.buildlogic.ux

/**
 * Contratos de transición UX de AgendaQr.
 *
 * Cada transición declara: evento UX → pantalla origen → pantalla destino →
 * efecto observable → condiciones de validez. La fuente normativa es
 * `docs/04-ux/02-especificacion-ux-ui-v1.md` (§3 reglas congeladas, §5
 * estados transversales, §6 importación) y
 * `docs/04-ux/03-contrato-interaccion-pantallas.md` (destinos válidos por
 * superficie).
 *
 * Este archivo es la fuente estructurada que `verifyUxTransitionContracts`
 * cruza contra el código real y los tests de comportamiento.
 */

/** Un evento UX disparado por el usuario o por el ciclo de vida. */
enum class UxEvent {
    /** Guardar / confirmar la intención de la pantalla. */
    Guardar,
    /** Abandonar la intención actual sin persistir (§3). */
    Cancelar,
    /** Confirmar una acción destructiva o de consecuencia. */
    Confirmar,
    /** Volver a la pantalla/flujo padre conservando el trabajo. */
    Volver,
    /** Reintentar la última operación fallida (§10). */
    Reintentar,
    /** Navegar a un destino declarado del contrato. */
    Navegar,
    /** Importar uno o varios recursos existentes (galería/cámara/archivo). */
    Importar,
    /** Asociar un comprobante a una actividad. */
    Asociar,
    /** Desasociar un comprobante de una actividad (reversible). */
    Desasociar,
    /** Eliminar un elemento (requiere confirmación destructiva). */
    Eliminar,
    /** Recuperar tras denegación de permiso. */
    RecuperarPermisos,
    /** Recuperar tras error recuperable. */
    RecuperarError,
    /** Restaurar estado tras muerte de proceso / rotación. */
    RestaurarEstado,
}

/** Resultado observable de una transición. */
enum class UxEffect {
    /** Cambio de superficie (ruta de navegación). */
    NavegacionSuperficie,
    /** Cambio de ruta interna de la superficie (ViewModel). */
    NavegacionInterna,
    /** Persistencia local ejecutada. */
    PersistenciaLocal,
    /** Sincronización encolada / iniciada. */
    Sincronizacion,
    /** Diálogo de confirmación mostrado. */
    ConfirmacionMostrada,
    /** Error contextual mostrado con recuperación. */
    ErrorMostrado,
    /** Estado de guardado (saving/saved) mostrado. */
    GuardandoMostrado,
    /** No hay navegación: la intención se abandona en su sitio. */
    SinNavegacion,
    /** La app se cierra (desde raíz S01). */
    SalidaApp,
}

/** Condición bajo la cual la transición está habilitada. */
data class Condicion(
    /** Descripción legible de la condición. */
    val descripcion: String,
    /** Si la transición solo aplica en cierto estado de la pantalla. */
    val estadoRequerido: String? = null,
)

/** Una transición UX declarada. */
data class UxTransition(
    /** ID del contrato de pantalla origen (ScreenContracts.id). */
    val pantallaOrigen: String,
    /** Evento que dispara la transición. */
    val evento: UxEvent,
    /** Pantalla destino (null si no hay navegación de superficie). */
    val pantallaDestino: String?,
    /** Efecto observable que debe producirse. */
    val efecto: UxEffect,
    /** Condiciones de validez (vacía = siempre válida). */
    val condiciones: List<Condicion> = emptyList(),
    /** Si la transición inhabilita la navegación mientras persiste (p. ej. guardando). */
    val bloqueaMientrasGuarda: Boolean = false,
    /** Si el Back del sistema consume esta transición antes del pop de superficie. */
    val esBackInterno: Boolean = false,
)

/** Vocabulario cerrado de IDs de pantalla (debe coincidir con ScreenContracts.all). */
val pantallasValidas: Set<String> = setOf(
    "S01", "S02", "S09", "destination-detail", "destination-edit", "qr-fullscreen",
    "S04", "S06", "context-detail", "S07-list", "operation-detail", "S07",
    "inbox", "S08", "S12", "account", "auth", "S00", "S03",
)

/**
 * Todas las transiciones UX declaradas por los contratos vigentes.
 * Cada una debe tener correspondencia en el código real y en tests de
 * comportamiento.
 */
val UxTransitions: List<UxTransition> = listOf(

    // ------------------------------------------------------------------
    // S01 — Inicio (hub raíz). Una pantalla = una intención: encontrar o
    // iniciar algo. No tiene acción primaria propia; las acciones del hub
    // navegan a otros flujos.
    // ------------------------------------------------------------------
    UxTransition("S01", UxEvent.Navegar, "S02", UxEffect.NavegacionSuperficie),
    UxTransition("S01", UxEvent.Navegar, "S04", UxEffect.NavegacionSuperficie),
    UxTransition("S01", UxEvent.Navegar, "S07-list", UxEffect.NavegacionSuperficie),
    UxTransition("S01", UxEvent.Navegar, "S06", UxEffect.NavegacionSuperficie),
    UxTransition("S01", UxEvent.Navegar, "account", UxEffect.NavegacionSuperficie),
    UxTransition("S01", UxEvent.Cancelar, null, UxEffect.SalidaApp, esBackInterno = false),
    UxTransition("S01", UxEvent.RecuperarError, "S01", UxEffect.ErrorMostrado),

    // ------------------------------------------------------------------
    // S02 — Añadir. Intención: traer algo existente. Acción primaria: Galería
    // (una sola acción, selección múltiple). 1 elemento → S09; 2+ → S12.
    // ------------------------------------------------------------------
    UxTransition("S02", UxEvent.Importar, "S09", UxEffect.NavegacionInterna),
    UxTransition("S02", UxEvent.Importar, "S12", UxEffect.NavegacionSuperficie),
    UxTransition("S02", UxEvent.Volver, "S01", UxEffect.NavegacionSuperficie),
    UxTransition("S02", UxEvent.Cancelar, null, UxEffect.SinNavegacion),
    UxTransition("S02", UxEvent.RecuperarPermisos, "S02", UxEffect.SinNavegacion),

    // ------------------------------------------------------------------
    // S09 — Revisar QR. Acción primaria: Guardar. Mientras se guarda no hay
    // vuelta (§3).
    // ------------------------------------------------------------------
    UxTransition("S09", UxEvent.Guardar, "S01", UxEffect.PersistenciaLocal, bloqueaMientrasGuarda = true),
    UxTransition("S09", UxEvent.Guardar, "S07-list", UxEffect.PersistenciaLocal, bloqueaMientrasGuarda = true),
    UxTransition("S09", UxEvent.Cancelar, "S02", UxEffect.NavegacionInterna),
    UxTransition("S09", UxEvent.Volver, "S02", UxEffect.NavegacionInterna),
    UxTransition("S09", UxEvent.RecuperarError, "S09", UxEffect.ErrorMostrado),
    UxTransition("S09", UxEvent.Reintentar, "S09", UxEffect.PersistenciaLocal),

    // ------------------------------------------------------------------
    // destination-detail — Detalle de destino. Acción primaria: Mostrar QR.
    // ------------------------------------------------------------------
    UxTransition("destination-detail", UxEvent.Navegar, "qr-fullscreen", UxEffect.NavegacionInterna),
    UxTransition("destination-detail", UxEvent.Navegar, "S07", UxEffect.NavegacionSuperficie),
    UxTransition("destination-detail", UxEvent.Navegar, "destination-edit", UxEffect.NavegacionInterna),
    UxTransition("destination-detail", UxEvent.Navegar, "S07-list", UxEffect.NavegacionSuperficie),
    UxTransition("destination-detail", UxEvent.Eliminar, "S07-list", UxEffect.PersistenciaLocal),
    UxTransition("destination-detail", UxEvent.Confirmar, null, UxEffect.ConfirmacionMostrada),
    UxTransition("destination-detail", UxEvent.Cancelar, "S07-list", UxEffect.NavegacionSuperficie),
    UxTransition("destination-detail", UxEvent.Volver, "S07-list", UxEffect.NavegacionSuperficie),

    // ------------------------------------------------------------------
    // destination-edit — Editor de destino. Acción primaria: Guardar.
    // ------------------------------------------------------------------
    UxTransition("destination-edit", UxEvent.Guardar, "destination-detail", UxEffect.PersistenciaLocal, bloqueaMientrasGuarda = true),
    UxTransition("destination-edit", UxEvent.Guardar, "S07-list", UxEffect.PersistenciaLocal, bloqueaMientrasGuarda = true),
    UxTransition("destination-edit", UxEvent.Volver, "destination-detail", UxEffect.NavegacionInterna),
    UxTransition("destination-edit", UxEvent.Cancelar, "destination-detail", UxEffect.NavegacionInterna),
    UxTransition("destination-edit", UxEvent.RecuperarError, "destination-edit", UxEffect.ErrorMostrado),
    UxTransition("destination-edit", UxEvent.Reintentar, "destination-edit", UxEffect.PersistenciaLocal),

    // ------------------------------------------------------------------
    // qr-fullscreen — QR a pantalla completa. No tiene acción primaria.
    // ------------------------------------------------------------------
    UxTransition("qr-fullscreen", UxEvent.Volver, "destination-detail", UxEffect.NavegacionInterna),
    UxTransition("qr-fullscreen", UxEvent.Cancelar, "destination-detail", UxEffect.NavegacionInterna),

    // ------------------------------------------------------------------
    // S04 — Buscar y resultados. Búsqueda global, sin selección previa de tipo.
    // ------------------------------------------------------------------
    UxTransition("S04", UxEvent.Navegar, "destination-detail", UxEffect.NavegacionSuperficie),
    UxTransition("S04", UxEvent.Navegar, "operation-detail", UxEffect.NavegacionSuperficie),
    UxTransition("S04", UxEvent.Navegar, "context-detail", UxEffect.NavegacionSuperficie),
    UxTransition("S04", UxEvent.Volver, null, UxEffect.SalidaApp),
    UxTransition("S04", UxEvent.Cancelar, null, UxEffect.SinNavegacion),
    UxTransition("S04", UxEvent.RecuperarError, "S04", UxEffect.ErrorMostrado),

    // ------------------------------------------------------------------
    // S06 — Contextos. Las pestañas son secciones, no destinos raíz.
    // ------------------------------------------------------------------
    UxTransition("S06", UxEvent.Navegar, "context-detail", UxEffect.NavegacionInterna),
    UxTransition("S06", UxEvent.Navegar, "destination-detail", UxEffect.NavegacionSuperficie),
    UxTransition("S06", UxEvent.Volver, null, UxEffect.SalidaApp),
    UxTransition("S06", UxEvent.Cancelar, null, UxEffect.SinNavegacion),
    UxTransition("S06", UxEvent.RecuperarError, "S06", UxEffect.ErrorMostrado),

    // ------------------------------------------------------------------
    // context-detail — Detalle de contexto.
    // ------------------------------------------------------------------
    UxTransition("context-detail", UxEvent.Navegar, "destination-detail", UxEffect.NavegacionSuperficie),
    UxTransition("context-detail", UxEvent.Navegar, "operation-detail", UxEffect.NavegacionSuperficie),
    UxTransition("context-detail", UxEvent.Volver, "S06", UxEffect.NavegacionInterna),
    UxTransition("context-detail", UxEvent.Cancelar, "S06", UxEffect.NavegacionInterna),

    // ------------------------------------------------------------------
    // S07-list — Actividades. Acción primaria: Registrar.
    // ------------------------------------------------------------------
    UxTransition("S07-list", UxEvent.Navegar, "operation-detail", UxEffect.NavegacionInterna),
    UxTransition("S07-list", UxEvent.Navegar, "S07", UxEffect.NavegacionSuperficie),
    UxTransition("S07-list", UxEvent.Navegar, "inbox", UxEffect.NavegacionInterna),
    UxTransition("S07-list", UxEvent.Eliminar, "S07-list", UxEffect.PersistenciaLocal),
    UxTransition("S07-list", UxEvent.Confirmar, null, UxEffect.ConfirmacionMostrada),
    UxTransition("S07-list", UxEvent.Volver, null, UxEffect.SalidaApp),
    UxTransition("S07-list", UxEvent.RecuperarError, "S07-list", UxEffect.ErrorMostrado),

    // ------------------------------------------------------------------
    // operation-detail — Detalle de actividad.
    // ------------------------------------------------------------------
    UxTransition("operation-detail", UxEvent.Navegar, "S07", UxEffect.NavegacionInterna),
    UxTransition("operation-detail", UxEvent.Navegar, "S07-list", UxEffect.NavegacionInterna),
    UxTransition("operation-detail", UxEvent.Asociar, null, UxEffect.PersistenciaLocal),
    UxTransition("operation-detail", UxEvent.Desasociar, null, UxEffect.PersistenciaLocal),
    UxTransition("operation-detail", UxEvent.Eliminar, "S07-list", UxEffect.PersistenciaLocal),
    UxTransition("operation-detail", UxEvent.Confirmar, null, UxEffect.ConfirmacionMostrada),
    UxTransition("operation-detail", UxEvent.Volver, "S07-list", UxEffect.NavegacionInterna),
    UxTransition("operation-detail", UxEvent.RecuperarError, "operation-detail", UxEffect.ErrorMostrado),

    // ------------------------------------------------------------------
    // S07 — Registrar actividad. Acción primaria: Guardar. Back con cambios
    // pide confirmación de descarte.
    // ------------------------------------------------------------------
    UxTransition("S07", UxEvent.Guardar, "operation-detail", UxEffect.PersistenciaLocal, bloqueaMientrasGuarda = true),
    UxTransition("S07", UxEvent.Guardar, "S07-list", UxEffect.PersistenciaLocal, bloqueaMientrasGuarda = true),
    UxTransition("S07", UxEvent.Volver, "S07-list", UxEffect.NavegacionInterna),
    UxTransition("S07", UxEvent.Cancelar, "S07-list", UxEffect.NavegacionInterna),
    UxTransition("S07", UxEvent.Confirmar, null, UxEffect.ConfirmacionMostrada),
    UxTransition("S07", UxEvent.Eliminar, "S07-list", UxEffect.PersistenciaLocal),
    UxTransition("S07", UxEvent.RecuperarError, "S07", UxEffect.ErrorMostrado),
    UxTransition("S07", UxEvent.Reintentar, "S07", UxEffect.PersistenciaLocal),

    // ------------------------------------------------------------------
    // inbox — Comprobantes sin asociar.
    // ------------------------------------------------------------------
    UxTransition("inbox", UxEvent.Navegar, "operation-detail", UxEffect.NavegacionInterna),
    UxTransition("inbox", UxEvent.Asociar, null, UxEffect.PersistenciaLocal),
    UxTransition("inbox", UxEvent.Volver, "S07-list", UxEffect.NavegacionInterna),
    UxTransition("inbox", UxEvent.Cancelar, "S07-list", UxEffect.NavegacionInterna),
    UxTransition("inbox", UxEvent.Eliminar, "inbox", UxEffect.PersistenciaLocal),
    UxTransition("inbox", UxEvent.Confirmar, null, UxEffect.ConfirmacionMostrada),

    // ------------------------------------------------------------------
    // S08 — Seleccionar contexto (overlay). Cancelar es la única salida;
    // el draft del flujo padre se conserva.
    // ------------------------------------------------------------------
    UxTransition("S08", UxEvent.Navegar, "S06", UxEffect.NavegacionSuperficie),
    UxTransition("S08", UxEvent.Volver, "S07", UxEffect.NavegacionInterna),
    UxTransition("S08", UxEvent.Volver, "destination-edit", UxEffect.NavegacionInterna),
    UxTransition("S08", UxEvent.Cancelar, null, UxEffect.SinNavegacion),

    // ------------------------------------------------------------------
    // S12 — Resultado de importación masiva. Acción primaria: Guardar
    // reconocidos. Mientras se guarda no hay vuelta.
    // ------------------------------------------------------------------
    UxTransition("S12", UxEvent.Guardar, "S01", UxEffect.PersistenciaLocal, bloqueaMientrasGuarda = true),
    UxTransition("S12", UxEvent.Guardar, "S07-list", UxEffect.PersistenciaLocal, bloqueaMientrasGuarda = true),
    UxTransition("S12", UxEvent.Volver, "S01", UxEffect.NavegacionSuperficie),
    UxTransition("S12", UxEvent.Volver, "S07-list", UxEffect.NavegacionSuperficie),
    UxTransition("S12", UxEvent.Cancelar, null, UxEffect.SinNavegacion),
    UxTransition("S12", UxEvent.Reintentar, "S12", UxEffect.PersistenciaLocal),
    UxTransition("S12", UxEvent.RecuperarError, "S12", UxEffect.ErrorMostrado),

    // ------------------------------------------------------------------
    // account — Cuenta y respaldo.
    // ------------------------------------------------------------------
    UxTransition("account", UxEvent.Volver, "S01", UxEffect.NavegacionSuperficie),
    UxTransition("account", UxEvent.RecuperarError, "account", UxEffect.ErrorMostrado),

    // ------------------------------------------------------------------
    // auth — Cuenta: autenticación (overlay).
    // ------------------------------------------------------------------
    UxTransition("auth", UxEvent.Guardar, "account", UxEffect.PersistenciaLocal),
    UxTransition("auth", UxEvent.RecuperarError, "auth", UxEffect.ErrorMostrado),

    // ------------------------------------------------------------------
    // S00 — Acceso local (plataforma). No es login: PIN local.
    // ------------------------------------------------------------------
    UxTransition("S00", UxEvent.Guardar, null, UxEffect.PersistenciaLocal),
    UxTransition("S00", UxEvent.Cancelar, null, UxEffect.SalidaApp),
    UxTransition("S00", UxEvent.RecuperarPermisos, "S00", UxEffect.SinNavegacion),
    UxTransition("S00", UxEvent.RecuperarError, "S00", UxEffect.ErrorMostrado),

    // ------------------------------------------------------------------
    // S03 — Cámara (plataforma).
    // ------------------------------------------------------------------
    UxTransition("S03", UxEvent.Volver, "S02", UxEffect.NavegacionSuperficie),
    UxTransition("S03", UxEvent.RecuperarPermisos, "S03", UxEffect.SinNavegacion),
    UxTransition("S03", UxEvent.RecuperarError, "S03", UxEffect.ErrorMostrado),
)

/**
 * Valida que todas las pantallas origen/destino declaradas existan en el
 * vocabulario válido y que no haya transiciones huérfanas.
 */
fun validarTransiciones(transiciones: List<UxTransition>): List<String> = buildList {
    val idsInvalidos = transiciones.flatMap { t ->
        buildList {
            if (t.pantallaOrigen !in pantallasValidas) add("origen-inválido: '${t.pantallaOrigen}' no es una pantalla declarada")
            t.pantallaDestino?.let { d ->
                if (d !in pantallasValidas && d != "system-exit") {
                    add("destino-inválido: '${t.pantallaOrigen}' --${t.evento}--> '$d' no es una pantalla declarada")
                }
            }
        }
    }
    idsInvalidos.forEach { add(it) }

    // No debe haber dos transiciones idénticas (mismo origen, mismo evento, mismo destino).
    val duplicados = transiciones.groupBy { t -> Triple(t.pantallaOrigen, t.evento, t.pantallaDestino) }
        .filterValues { it.size > 1 }
    duplicados.forEach { (triple, ocurrencias) ->
        add("transición-duplicada: ${triple.first} --${triple.second}--> ${triple.third} (${ocurrencias.size} veces)")
    }
}

/**
 * Efectos esperados por pantalla y evento, derivados del contrato. Se usa
 * para cruzar contra los tests de comportamiento: cada transición crítica
 * debe tener al menos un test que verifique el efecto.
 */
val transicionesCriticas: List<UxTransition> = UxTransitions.filter { t ->
    t.evento == UxEvent.Guardar ||
        t.evento == UxEvent.Cancelar ||
        t.evento == UxEvent.Eliminar ||
        t.evento == UxEvent.Reintentar ||
        t.evento == UxEvent.RecuperarError ||
        t.bloqueaMientrasGuarda
}
