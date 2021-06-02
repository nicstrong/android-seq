package com.nicstrong.telemetry.sink

import com.nicstrong.telemetry.event.Event

class NulLSink(): EventSink {
    override fun emit(event: Event) {
    }

    override fun close() {

    }
}