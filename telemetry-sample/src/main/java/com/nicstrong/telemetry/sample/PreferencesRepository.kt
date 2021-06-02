package com.nicstrong.telemetry.sample

import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

data class SeqPreferences(
    val url: String?,
    val apiKey: String?
)

class PreferencesRepository(private val dataStore: DataStore<Preferences>) {
    private val LOG_TAG: String = "PreferencesRepository"

    private object PreferencesKeys {
        val SEQ_URL_KEY = stringPreferencesKey("seq_url")
        val SEQ_APIKEY_KEY = stringPreferencesKey("seq_api_key")
    }

    val seqPreferencesFlow: Flow<SeqPreferences> = dataStore.data
        .catch { exception ->
            if (exception is IOException) {
                Log.e(LOG_TAG, "Error reading preferences.", exception)
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }.map { preferences ->
            val seqUrl = preferences[PreferencesKeys.SEQ_URL_KEY]
            val apiKey = preferences[PreferencesKeys.SEQ_APIKEY_KEY]
            SeqPreferences(seqUrl, apiKey)
        }


    suspend fun setSeqUrl(url: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SEQ_URL_KEY] = url
        }
    }

    suspend fun setApiKey(apiKey: String) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.SEQ_APIKEY_KEY] = apiKey
        }
    }

}