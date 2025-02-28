package com.mykid.reports.data.localization

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

object LocalizationManager {
    var currentLanguage by mutableStateOf("en")
        private set

    fun setLanguage(language: String) {
        currentLanguage = language
    }

    fun getString(key: String): String {
        return LocalizedStrings.strings[currentLanguage]?.get(key) ?: key
    }
} 