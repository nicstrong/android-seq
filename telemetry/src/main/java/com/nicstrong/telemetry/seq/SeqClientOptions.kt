package com.nicstrong.telemetry.seq

data class SeqClientOptions(val seqUrl: String,
                            val apiKey: String?,
                            val traceLogging: Boolean = false)
