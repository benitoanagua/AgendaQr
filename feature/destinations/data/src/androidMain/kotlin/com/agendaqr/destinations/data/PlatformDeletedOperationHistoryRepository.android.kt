package com.agendaqr.destinations.data

import com.agendaqr.destinations.domain.DeletedOperationHistoryRepository

actual fun createDeletedOperationHistoryRepository(): DeletedOperationHistoryRepository =
    LocalDeletedOperationHistoryRepository()
