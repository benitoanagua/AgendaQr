package com.agendaqr.destinations.presentation

/**
 * Rastreador de flujos externos iniciados por la app (U-02, D1).
 *
 * Vive en presentación (no en el dominio): la galería, la cámara del
 * sistema, los selectores de archivos, las hojas de compartir y los
 * diálogos de permisos sacan la app de primer plano, y la política de
 * bloqueo (`ON_STOP` → `lock()`) los interrumpía. Cada lanzamiento marca
 * el instante; la política concede una gracia acotada
 * ([ExternalFlowGraceMillis]) y pasado ese tiempo vuelve a bloquear al
 * volver (fail-closed: sin marca previa, el comportamiento es
 * exactamente el actual).
 *
 * El PIN nunca pasa por aquí: solo instantes de lanzamiento.
 */
class ExternalFlowTracker(private val clock: () -> Long = { com.agendaqr.destinations.domain.nowMillis() }) {
    private var lastLaunchMillis: Long? = null

    /** Marca un lanzamiento externo "justo antes de lanzar". */
    fun markLaunch(now: Long = clock()) {
        lastLaunchMillis = now
    }

    /** ¿Hay un lanzamiento pendiente de retorno? */
    fun hasPendingLaunch(): Boolean = lastLaunchMillis != null

    /** ¿Vigente la gracia en [now]? No limpia: el retorno (ON_START)
     * decide y limpia. */
    fun shouldSkipLock(now: Long = clock()): Boolean {
        val launch = lastLaunchMillis ?: return false
        return now - launch < ExternalFlowGraceMillis
    }

    /** Consume la marca al volver a primer plano. */
    fun clear() {
        lastLaunchMillis = null
    }
}

/**
 * Puerta de acceso al rastreador vigente desde código de plataforma
 * (lanzadores de galería/cámara/selectores/compartir): la instancia la
 * crea y publica `AgendaQrApp`; los lanzadores solo marcan. En pruebas se
 * sustituye por una instancia con reloj falso.
 */
object ExternalFlowGates {
    var tracker: ExternalFlowTracker = ExternalFlowTracker()
}

/** Gracia de bloqueo para flujos externos (D1): 120 s. Constante única
 * de política; si el propietario prefiere 0 s, la gracia se desactiva
 * aquí y el borrador se conserva por otra vía. */
const val ExternalFlowGraceMillis = 120_000L

/**
 * Decisión pura de la política de bloqueo al pasar a segundo plano:
 * en demo no hay nada que proteger; con gracia vigente no se bloquea;
 * en cualquier otro caso se bloquea (fail-closed).
 */
fun shouldLockOnStop(demoShowing: Boolean, tracker: ExternalFlowTracker, nowMillis: Long): Boolean =
    !demoShowing && !tracker.shouldSkipLock(nowMillis)

/**
 * Decisión del ciclo de vida para los flujos externos iniciados por la app.
 * Pura y probable: recibe el reloj por constructor y la acción de bloqueo por
 * parámetro; no depende de Android.
 *
 * Orden real al volver de un flujo: STOP → START → RESUME.
 * - `ON_STOP` decide si bloquear con la gracia vigente.
 * - `ON_START` bloquea si la gracia venció y consume la marca.
 * - `ON_RESUME` limpia SIEMPRE la marca pendiente: un flujo translúcido (hoja
 *   de compartir, diálogo de permiso) puede no producir `ON_STOP`, y una marca
 *   viva impediría bloquear un paso a segundo plano posterior dentro de la
 *   ventana de gracia.
 */
class ExternalFlowLockPolicy(
    private val tracker: ExternalFlowTracker,
    private val now: () -> Long = { com.agendaqr.destinations.domain.nowMillis() },
) {
    fun onStop(demoShowing: Boolean, lock: () -> Unit) {
        if (shouldLockOnStop(demoShowing, tracker, now())) lock()
    }

    fun onStart(demoShowing: Boolean, lock: () -> Unit) {
        if (!demoShowing && tracker.hasPendingLaunch()) {
            if (!tracker.shouldSkipLock(now())) lock()
            tracker.clear()
        }
    }

    /** Al volver a primer plano se consume siempre la marca pendiente. */
    fun onResume() {
        tracker.clear()
    }
}
