package com.agendaqr.destinations.domain

import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
actual fun nowMillis(): Long = Clock.System.now().toEpochMilliseconds()
