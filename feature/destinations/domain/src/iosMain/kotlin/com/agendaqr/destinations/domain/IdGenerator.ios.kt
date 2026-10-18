package com.agendaqr.destinations.domain

import platform.Foundation.NSUUID

actual fun newEntityId(prefix: String): String =
    "${prefix}-${NSUUID().UUIDString}"
