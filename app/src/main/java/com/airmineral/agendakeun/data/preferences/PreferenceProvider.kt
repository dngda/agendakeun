package com.airmineral.agendakeun.data.preferences

import android.content.Context
import android.content.SharedPreferences
import androidx.preference.PreferenceManager

class PreferenceProvider(
    private val context: Context
) {
    private val preference: SharedPreferences
        get() = PreferenceManager.getDefaultSharedPreferences(context)

    fun saveSwitchState(eventId: String, state: Boolean) {
        preference.edit().putBoolean(eventId, state).apply()
    }

    fun getSwitchState(eventId: String): Boolean {
        return preference.getBoolean(eventId, false)
    }

    fun saveUserGroupList(userId: String, groupList: List<String>) {
        preference.edit().putStringSet(userId, groupList.toSet()).apply()
    }

    fun getUserGroupList(userId: String): List<String> {
        return preference.getStringSet(userId, null)!!.toList()
    }

    fun saveStrings(key: String, data: String?) {
        preference.edit().putString(key, data).apply()
    }

    fun getStrings(key: String): String? {
        return preference.getString(key, "")
    }
}