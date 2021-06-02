package com.nicstrong.telemetry.enricher
import com.nicstrong.telemetry.event.Event

interface EventEnricher {
    fun enrich(event: Event)
}