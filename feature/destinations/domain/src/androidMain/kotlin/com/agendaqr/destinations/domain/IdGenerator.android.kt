package com.agendaqr.destinations.domain

actual fun newEntityId(prefix: String): String =
    $prefix-${java.util.UUID.randomUUID()}
