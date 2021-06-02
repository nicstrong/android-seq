package com.nicstrong.telemetry.enricher

import android.content.Context
import android.util.Log
import com.jaredrummler.android.device.DeviceName
import com.nicstrong.telemetry.event.Event
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class DeviceInfoEnricher(context: Context) : EventEnricher {
    private var  deviceInfo: DeviceName.DeviceInfo? = null

    init {
        DeviceName.with(context).request { info, error ->
            if (error != null) {
                Log.e("DeviceInfoEnricher", "Failed to get device info: ${error.message}")
            } else {
                deviceInfo = info
            }
        }
    }

    override fun enrich(event: Event) {
        deviceInfo?.apply {
            event.properties["deviceName"] = name
            event.properties["deviceManufacturer"] = manufacturer
            event.properties["deviceModel"] = model
        }
    }

    private suspend fun getDeviceInfo(context: Context): DeviceName.DeviceInfo =
        suspendCoroutine { cont ->
            DeviceName.with(context).request { info, err ->
                if (err != null) {
                    cont.resumeWithException(err)
                }
                cont.resume(info)
            }
        }
}



