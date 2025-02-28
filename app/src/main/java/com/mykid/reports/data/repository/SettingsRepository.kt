package com.mykid.reports.data.repository

import android.content.Context
import com.mykid.reports.data.storage.SharedPreferencesManager

class SettingsRepository(context: Context) {
    private val sharedPreferences = SharedPreferencesManager(context)

    fun saveLanguage(language: String) {
        sharedPreferences.saveString("language", language)
    }

    fun getLanguage(): String {
        return sharedPreferences.getString("language", "English")
    }

    fun saveTheme(isDark: Boolean) {
        sharedPreferences.saveString("theme", isDark.toString())
    }

    fun getTheme(): Boolean {
        return sharedPreferences.getString("theme", "false").toBoolean()
    }

    fun saveNotifications(enabled: Boolean) {
        sharedPreferences.saveString("notifications", enabled.toString())
    }

    fun getNotifications(): Boolean {
        return sharedPreferences.getString("notifications", "true").toBoolean()
    }
} 