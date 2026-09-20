package com.agendaqr.destinations.data

import com.agendaqr.destinations.domain.ComprobanteRepository
import com.agendaqr.destinations.domain.OperationRepository

actual fun createOperationRepository(): OperationRepository = LocalOperationRepository()
actual fun createComprobanteRepository(): ComprobanteRepository = LocalComprobanteRepository()
