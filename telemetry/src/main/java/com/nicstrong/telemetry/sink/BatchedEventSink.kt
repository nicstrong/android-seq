package com.nicstrong.telemetry.sink

import com.nicstrong.telemetry.event.Event

interface BatchedEventSink {
    suspend fun emitBatch(batch: Iterable<Event>)
    suspend fun onEmptyBatch()
}