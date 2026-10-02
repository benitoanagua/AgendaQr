package com.agendaqr.destinations.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

/**
 * Reintenta el drenado con backoff exponencial y respeto a la conectividad.
 *
 * - Tras un drain sin fallos pendientes, espera [idleIntervalMillis].
 * - Si quedan mutaciones en fallo, espera el mínimo entre [idleIntervalMillis]
 *   y el `nextRetryAt` reportado, con un piso de [minRetryIntervalMillis] para
 *   no girar en caliente.
 * - Con [NetworkMonitor], pausa el ciclo mientras no haya red y reacciona de
 *   inmediato al volver la conectividad.
 * - [onResult] expone cada [DrainResult] (telemetría / badge de "pendiente").
 */
class SyncRecoveryCoordinator @OptIn(ExperimentalTime::class) constructor(
    private val processor: SyncMutationProcessor,
    private val scope: CoroutineScope,
    private val retryIntervalMillis: Long = 60_000L,
    private val idleIntervalMillis: Long = retryIntervalMillis,
    private val minRetryIntervalMillis: Long = 2_000L,
    private val networkMonitor: NetworkMonitor? = null,
    private val clock: () -> Long = { Clock.System.now().toEpochMilliseconds() },
    private val onResult: (DrainResult) -> Unit = {},
) {
    private var job: Job? = null

    fun start(): Job {
        job?.cancel()
        return scope.launch {
            val periodic = launch {
                // Drenado inicial inmediato + reintentos con backoff.
                while (isActive) {
                    val result = drainAndReport()
                    delay(nextDelay(result))
                }
            }
            if (networkMonitor != null) {
                // Drenado oportunista al recuperar conectividad (concurre de
                // forma segura: el procesador serializa con Mutex).
                networkMonitor.observe().collect { online ->
                    if (online && isActive) drainAndReport()
                }
            } else {
                periodic.join()
            }
        }.also { job = it }
    }

    fun stop() {
        job?.cancel()
        job = null
    }

    fun triggerNow(): Job = scope.launch { drainAndReport() }

    private suspend fun drainAndReport(): DrainResult {
        val result = processor.drainWithReport()
        runCatching { onResult(result) }
        return result
    }

    private fun nextDelay(last: DrainResult): Long {
        val pendingAt = last.nextRetryAt
        if (pendingAt == null) return idleIntervalMillis
        val now = clock()
        return (pendingAt - now).coerceIn(minRetryIntervalMillis, idleIntervalMillis)
    }
}
