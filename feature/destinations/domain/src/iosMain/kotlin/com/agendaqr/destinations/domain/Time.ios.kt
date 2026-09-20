package com.agendaqr.destinations.domain

import platform.Foundation.NSDate

actual fun nowMillis(): Long = (NSDate().timeIntervalSince1970 * 1000.0).toLong()
