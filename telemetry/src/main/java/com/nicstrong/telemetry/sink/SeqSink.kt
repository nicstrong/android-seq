package com.nicstrong.telemetry.sink

import com.nicstrong.telemetry.event.Event
import com.nicstrong.telemetry.seq.SeqClient
import java.net.UnknownHostException

class SeqSink(private val seqClient: SeqClient) : EventSink, BatchedEventSink {

    override fun emit(event: Event) {
        try {
            seqClient.emitEvent(event)
        } catch (e: UnknownHostException) {
            // ignore can't connect to seq at the moment
        }
    }

    override fun close() {
        // seqClient.
    }

    override suspend fun emitBatch(batch: Iterable<Event>) {
        try {
            seqClient.emitEvents(batch)
        } catch (e: UnknownHostException) {
            // ignore can't connect to seq at the moment
        }
    }

    override suspend fun onEmptyBatch() {
    }
}