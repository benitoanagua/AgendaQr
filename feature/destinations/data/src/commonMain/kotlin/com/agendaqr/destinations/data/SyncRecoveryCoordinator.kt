package com.agendaqr.destinations.data

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class SyncRecoveryCoordinator(
    private val processor: SyncMutationProcessor,
    private val scope: CoroutineScope,
    private val retryIntervalMillis: Long = 60_000L,
) {
    private var job: Job? = null

    fun start(): Job {
        job?.cancel()
        return scope.launch {
            processor.drain()
            while (isActive) {
                delay(retryIntervalMillis)
                processor.drain()
            }
        }.also { job = it }
    }

    fun stop() {
        job?.cancel()
        job = null
    }

    fun triggerNow() {
        scope.launch { processor.drain() }
    }
}
