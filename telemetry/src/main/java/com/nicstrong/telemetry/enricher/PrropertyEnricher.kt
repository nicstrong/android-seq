package com.nicstrong.telemetry.enricher

import com.nicstrong.telemetry.event.Event

class PropertyEnricher() : EventEnricher {
    private val properties = mutableMapOf<String, Any>()

    override fun enrich(event: Event) {
        if (properties.any()) {
            for (prop in properties) {
                event.properties[prop.key] = prop.value
            }
        }
    }

}