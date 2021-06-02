package com.nicstrong.telemetry.sample

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.nicstrong.telemetry.TelemetryClient
import com.nicstrong.telemetry.event.EventLevel
import com.nicstrong.telemetry.sample.databinding.ActivityMainBinding
import com.nicstrong.telemetry.seq.SeqClient
import com.nicstrong.telemetry.sink.addSeqSink
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private var telemetryClient: TelemetryClient? = null
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        viewModel = ViewModelProvider(
            this,
            MainViewModelFactory(
                PreferencesRepository(dataStore)
            )
        ).get(MainViewModel::class.java)

        binding.connectButton.setOnClickListener {
            createClients()
        }
    }

//    private fun createClients() {
//        val builder = SeqClient.Builder()
//            .url(pref.url!!)
//
//        if (pref.apiKey != null) {
//            builder.apiKey(pref.apiKey)
//        }
//
//        val seqClient = builder.build()
//        telemetryClient = TelemetryClient.Builder()
//            .addSeqSink(seqClient) { config ->
//                config.useBatching(20, 5000, 1000)
//            }
//            .minimumLevel(EventLevel.Debug)
//            .build()
//    }
}