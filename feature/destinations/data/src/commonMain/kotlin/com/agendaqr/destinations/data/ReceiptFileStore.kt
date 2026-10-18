package com.agendaqr.destinations.data

import com.agendaqr.destinations.domain.ComprobanteFileStore

expect fun platformComprobanteFileStore(): ComprobanteFileStore

fun createComprobanteFileStore(): ComprobanteFileStore = platformComprobanteFileStore()
