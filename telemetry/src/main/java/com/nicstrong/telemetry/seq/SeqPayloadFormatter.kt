package com.nicstrong.telemetry.seq

import android.util.JsonWriter
import android.util.Log
import com.nicstrong.telemetry.event.Event
import com.nicstrong.telemetry.event.EventLevel
import java.io.StringWriter
import java.time.format.DateTimeFormatter


class SeqPayloadFormatter {
    companion object {
        private val instantFormatter = DateTimeFormatter.ISO_INSTANT

        fun formatPayload(events: Iterable<Event>): String {
            val payload = StringWriter()

            for (event in events) {
                val buffer = StringWriter()
                try {
                    formatEvent(event, buffer)
                } catch (ex: Throwable) {
                    Log.e(SeqClient.LOG_TAG, "Failed to format event", ex)
                    continue
                }
                val json = buffer.toString()
                payload.write(json)
                payload.write("\r\n")
            }
            return payload.toString()
        }

        private fun formatEvent(event: Event, output: StringWriter) {
            val writer = JsonWriter(output)
            writer.setIndent("")
            writer.beginObject()
                .name("@t")
                .value(instantFormatter.format(event.timestamp))

            writer.name("@mt")
                .value(event.messageTemplate)

            if (event.level != EventLevel.Information) {
                writer.name("@l")
                    .value(event.level.toString())
            }

            if (event.throwable != null) {
                writer.name("@x")
                    .value(Log.getStackTraceString(event.throwable))
            }

            for (property in event.properties) {
                var name = property.key
                if (name.isNotEmpty() && name[0] == '@') {
                    name = "@$name"
                }
                writer.name(name).value(property.value.toString())
            }

            writer.endObject()
            writer.close()
        }


    }
}