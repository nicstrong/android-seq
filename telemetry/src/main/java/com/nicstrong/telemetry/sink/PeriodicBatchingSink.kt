package com.nicstrong.telemetry.sink

import android.annotation.SuppressLint
import android.os.CountDownTimer
import android.util.Log
import com.nicstrong.telemetry.event.Event
import kotlinx.coroutines.*
import java.util.*
import java.util.concurrent.ArrayBlockingQueue
import kotlin.math.max
import kotlin.math.pow
import kotlin.math.roundToLong

class PeriodicBatchingSink(
    private val batchedSink: BatchedEventSink,
    private val batchSizeLimit: Int,
    private val periodMs: Long,
    queueLimit: Int,
    private val traceLogging: Boolean
) : EventSink {

    companion object {
        const val LOG_TAG = "PeriodicBatchingSink"
    }

    private val stateLock = Object()

    private val waitingQueue = ArrayBlockingQueue<Event>(queueLimit)
    private val batch = ArrayList<Event>(batchSizeLimit)
    private var started = false
    private var timer: CountDownTimer? = null
    private var failuresSinceSuccessfulBatch = 0
    private var unloading = false

    override fun emit(event: Event) {
        if (unloading) return

        if (!started) {
            synchronized(stateLock)
            {
                if (unloading) return

                if (!started) {
                    waitingQueue.offer(event)
                    started = true
                    setTimer()
                    return
                }
            }
        }
        waitingQueue.offer(event)
    }

    private fun onTimer() {
        timer?.cancel()

        CoroutineScope(Dispatchers.IO).launch {
            onTimerAsync()
        }
    }

    private suspend fun onTimerAsync() {
        try {
            if (traceLogging) {
                Log.d("Logger", "Batch timer fired")
            }

            var batchWasFull = false
            do {
                var next = waitingQueue.poll()
                while (batch.size < batchSizeLimit && next != null) {
                    if (canInclude())
                        batch.add(next)
                    next = waitingQueue.poll()
                }

                if (traceLogging) {
                    Log.d(LOG_TAG, "Emitting batch of size ${batch.size}")
                }
                batchedSink.emitBatch(batch)

                batchWasFull = batch.size >= batchSizeLimit
                batch.clear()
                failuresSinceSuccessfulBatch = 0
            } while (batchWasFull) // Otherwise, allow the period to elapse
        } catch (ex: Throwable) {
            Log.e("PeriodicBatchingSink",  "Exception while emitting periodic batch:", ex)
            failuresSinceSuccessfulBatch++
        } finally {
            if (failuresSinceSuccessfulBatch > 8)
                batch.clear()

            if (failuresSinceSuccessfulBatch > 10) {
                waitingQueue.clear()
            }

            withContext(Dispatchers.Main) {
                synchronized(stateLock) {
                    if (!unloading) {
                        setTimer()
                    }
                }
            }
        }
    }

    private fun setTimer() {
        val next = nextInterval()

        timer = object : CountDownTimer(next, next) {
            override fun onFinish() {
                onTimer()
            }

            override fun onTick(millisUntilFinished: Long) {
            }
        }
        timer?.start()

        if (traceLogging) {
            Log.d("Logger","Next batch sent in ${next}ms")
        }
    }

    override fun close() {
        timer?.cancel()
        synchronized(stateLock) {
            if (!started) return
            unloading = true
        }

        runBlocking { batchedSink.emitBatch(waitingQueue.toList()) }
    }

    fun canInclude(): Boolean = true

    private fun nextInterval(): Long {
        if (failuresSinceSuccessfulBatch <= 1) return periodMs
        val backoffFactor = 2.0.pow(failuresSinceSuccessfulBatch - 1)
        val backoffPeriod = max(periodMs, 5000)
        val backedOff: Long = (backoffPeriod * backoffFactor).roundToLong()
        val cappedBackoff = 300000L.coerceAtMost(backedOff)
        return max(periodMs, cappedBackoff)
    }
}