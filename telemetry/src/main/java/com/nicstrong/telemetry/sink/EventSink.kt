package com.nicstrong.telemetry.sink

import com.nicstrong.telemetry.event.Event

interface EventSink {
    fun emit(event: Event)
    fun close()
}