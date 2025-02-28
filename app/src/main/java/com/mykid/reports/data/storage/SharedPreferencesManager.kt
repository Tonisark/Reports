package com.mykid.reports.data.storage

import android.content.Context
import android.content.SharedPreferences

class SharedPreferencesManager(context: Context) {
    private val sharedPreferences: SharedPreferences = context.getSharedPreferences(
        "MyKidReports",
        Context.MODE_PRIVATE
    )

    fun saveString(key: String, value: String) {
        sharedPreferences.edit().putString(key, value).apply()
    }

    fun getString(key: String, defaultValue: String = ""): String {
        return sharedPreferences.getString(key, defaultValue) ?: defaultValue
    }

    fun saveBoolean(key: String, value: Boolean) {
        sharedPreferences.edit().putBoolean(key, value).apply()
    }

    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return sharedPreferences.getBoolean(key, defaultValue)
    }

    fun saveStringList(key: String, list: List<String>) {
        val jsonString = list.joinToString(",")
        saveString(key, jsonString)
    }

    fun getStringList(key: String): List<String> {
        val jsonString = getString(key)
        return if (jsonString.isEmpty()) {
            emptyList()
        } else {
            jsonString.split(",")
        }
    }

    fun clear() {
        sharedPreferences.edit().clear().apply()
    }
} 