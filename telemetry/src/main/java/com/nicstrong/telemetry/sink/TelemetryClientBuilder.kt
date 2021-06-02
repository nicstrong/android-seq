package com.nicstrong.telemetry.sink

import com.nicstrong.telemetry.TelemetryClient
import com.nicstrong.telemetry.seq.SeqClient

fun TelemetryClient.Builder.addSeqSink(seqClient: SeqClient, config: (SeqSinkBuilder) -> Unit): TelemetryClient.Builder {
    val sinkBuilder = SeqSinkBuilder(seqClient)
    config(sinkBuilder)
    val sink = sinkBuilder.build()
    this.addSink(sink)
    return this
}

class SeqSinkBuilder(seqClient: SeqClient) {
    private var wrappingSink: EventSink? = null
    private val seqSink = SeqSink(seqClient)

    fun useBatching(batchSizeLimit: Int, periodMs: Long, queueLimit: Int, traceLogging: Boolean = false) {
        wrappingSink = PeriodicBatchingSink(seqSink, batchSizeLimit, periodMs, queueLimit, traceLogging)
    }

    fun build(): EventSink {
        return wrappingSink ?: seqSink
    }
}
