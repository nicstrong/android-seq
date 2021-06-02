package com.nicstrong.telemetry.sample

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity


class PreferenceActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, PreferenceFragment())
            .commit()
    }
}
