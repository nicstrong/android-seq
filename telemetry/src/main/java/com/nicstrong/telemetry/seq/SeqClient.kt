package com.nicstrong.telemetry.seq

import android.net.Uri
import android.util.Log
import com.nicstrong.telemetry.event.Event
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

class SeqClient(private val options: SeqClientOptions) {
    companion object {
        val CLEF_MIME = "application/vnd.serilog.clef".toMediaType()
        const val LOG_TAG = "SeqClient"
    }

    private val seqUrl =
        Uri.parse(options.seqUrl).buildUpon().appendEncodedPath("api/events/raw").build()

    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .build()

    fun emitEvent(event: Event) {
        emitBatch(listOf(event))
    }

    fun emitEvents(events: Iterable<Event>) {
        emitBatch(events.toList())
    }

    private fun emitBatch(events: Iterable<Event>) {
        val body = SeqPayloadFormatter.formatPayload(events).toRequestBody(CLEF_MIME)
        val requestBuilder = Request.Builder()
            .url(seqUrl.toString())
            .post(body);
        if (!options.apiKey.isNullOrEmpty()) {
            requestBuilder.header("X-Seq-ApiKey", options.apiKey)
        }
        val request = requestBuilder.build()
        val response = httpClient.newCall(request).execute()
        if (!response.isSuccessful) {
            Log.e(
                LOG_TAG,
                "Failed to send batch to Seq: status=${response.code}, message=${response.message}"
            )
        }
        response.close()
        if (options.traceLogging) {
            Log.d(LOG_TAG, "Successfully sent batch to Seq Server")
        }
    }

    class Builder {
        private var seqUrl: String? = null
        private var apiKey: String? = null
        private var traceLogging: Boolean = false

        fun url(url: String): Builder {
            seqUrl = url
            return this
        }
        fun apiKey(key: String): Builder {
            apiKey = key
            return this
        }
        fun enableTraceLogging(): Builder {
            traceLogging = true
            return this
        }

        fun build(): SeqClient {
            if (seqUrl.isNullOrEmpty()) {
                throw IllegalArgumentException("Must define a url")
            }
            return SeqClient(SeqClientOptions(seqUrl!!, apiKey, traceLogging))
        }
    }
}
