package com.nicstrong.telemetry.sink

import android.util.Log
import com.nicstrong.telemetry.event.Event

import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.atomic.AtomicInteger

class BackgroundThreadSink (
    private val wrappedSink: EventSink,
    private val blockWhenFull: Boolean,
    bufferCapacity: Int = Int.MAX_VALUE
) : EventSink {
    private var backgroundThread: Thread
    private val droppedMessages = AtomicInteger(0)
    private val queue = LinkedBlockingQueue<Event>(bufferCapacity)

    init {
        backgroundThread = Thread(Runnable {
            try {
                while (true) {
                    val next = queue.take()
                    wrappedSink.emit(next)
                }

            } catch (t: Throwable) {
                Log.e("AsyncSync", "Failed processing async background queue", t)
            }
        })
    }

    override fun emit(event: Event) {
        if (blockWhenFull) {
            queue.offer(event)
        } else {
            try {
                queue.add(event)
            } catch (ex: IllegalStateException) {
                droppedMessages.incrementAndGet()
            }
        }
    }

    override fun close() {
    }
}