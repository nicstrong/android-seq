package com.nicstrong.telemetry.event

import java.time.Instant
import java.util.regex.Pattern

class Event constructor(
    val timestamp: Instant,
    val level: EventLevel,
    val throwable: Throwable?,
    val messageTemplate: String,
    val properties: MutableMap<String, Any>) {

    constructor (timestamp: Instant, level: EventLevel, throwable: Throwable?, messageTemplate: String) : this(timestamp, level, throwable, messageTemplate, mutableMapOf<String, Any>())
}

private val ANONYMOUS_CLASS = Pattern.compile("(\\$\\d+)+$")

fun Event.getSourceContext(): String? {
    var tag: String = ""
    if (properties.containsKey("SourceContext")) {
        tag = properties["SourceContext"].toString()
        val m = ANONYMOUS_CLASS.matcher(tag)
        if (m.find()) {
            tag = m.replaceAll("")
        }
        if (tag.contains('.')) {
            tag = tag.substring(tag.lastIndexOf('.') + 1)
        }
        return tag
    }
    return null
}

