package com.agendaqr.destinations.data

import com.agendaqr.destinations.domain.ImportPayloadStore

expect fun platformImportPayloadStore(): ImportPayloadStore

fun createImportPayloadStore(): ImportPayloadStore = platformImportPayloadStore()
