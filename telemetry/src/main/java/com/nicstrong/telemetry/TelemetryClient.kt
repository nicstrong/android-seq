package com.nicstrong.telemetry

import com.nicstrong.telemetry.enricher.EventEnricher
import com.nicstrong.telemetry.event.EventLevel
import com.nicstrong.telemetry.sink.EventSink
import okhttp3.internal.toImmutableList

open class TelemetryClient internal constructor(
    builder: Builder
) {
    val sinks: List<EventSink> = builder.sinks.toImmutableList()
    val enrichers: List<EventEnricher> = builder.enrichers.toImmutableList()
    val minimumLevel: EventLevel = builder.minimumLevel

    class Builder constructor() {
        internal val sinks: MutableList<EventSink> = mutableListOf()
        internal val enrichers: MutableList<EventEnricher> = mutableListOf()
        internal var minimumLevel = EventLevel.Information

        fun addSink(sink: EventSink): Builder {
            sinks.add(sink)
            return this
        }

        fun addEnrichers(enricher: EventEnricher): Builder {
            enrichers.add(enricher)
            return this
        }

        fun minimumLevel(level: EventLevel): Builder {
            minimumLevel = level
            return this
        }

        fun build() = TelemetryClient(this)
    }
}