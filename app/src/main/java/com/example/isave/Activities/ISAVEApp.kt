package com.example.isave.Activities

import android.app.Application
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatDelegate

class ISAVEApp : Application() {
    override fun onCreate() {
        super.onCreate()

        // Loading dark mode setting
        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val dark = prefs.getBoolean("dark_mode", false)

        // Applying the setting before any activity opens
        AppCompatDelegate.setDefaultNightMode(
            if (dark) AppCompatDelegate.MODE_NIGHT_YES
            else AppCompatDelegate.MODE_NIGHT_NO
        )
    }
}