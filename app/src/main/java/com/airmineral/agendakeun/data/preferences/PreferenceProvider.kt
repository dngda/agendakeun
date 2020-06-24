package com.airmineral.agendakeun.data.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class PreferenceProvider(
    context: Context
) {

    private val appContext = context.applicationContext
    private val preference: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(appContext)

    fun saveSwitchState(eventId: String, state: Boolean) {
        preference.edit().putBoolean(eventId, state).apply()
    }

    fun getSwitchState(eventId: String): Boolean? {
        return preference.getBoolean(eventId, false)
    }
}