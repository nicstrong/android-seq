package com.nicstrong.telemetry.sample

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.nicstrong.telemetry.TelemetryClient
import com.nicstrong.telemetry.event.EventLevel
import com.nicstrong.telemetry.seq.SeqClient
import com.nicstrong.telemetry.sink.addSeqSink
import kotlinx.coroutines.flow.map

class MainActivity : AppCompatActivity() {
    private var telemetryClient: TelemetryClient? = null
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    val prefsRepo = PreferencesRepository(dataStore)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        prefsRepo.samplePreferencesFlow
            .map { pref ->
                pref.url?.let {
                    createClients(pref)
                }
            }
    }

    private fun createClients(pref: SamplePreferences) {
        val builder = SeqClient.Builder()
            .url(pref.url!!)

        if (pref.apiKey != null) {
            builder.apiKey(pref.apiKey)
        }

        val seqClient = builder.build()
        telemetryClient = TelemetryClient.Builder()
            .addSeqSink(seqClient) { config ->
                config.useBatching(20, 5000, 1000)
            }
            .minimumLevel(EventLevel.Debug)
            .build()
    }
}