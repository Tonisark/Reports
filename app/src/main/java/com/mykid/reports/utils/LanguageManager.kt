package com.mykid.reports.utils

import android.content.Context
import android.content.res.Configuration
import android.os.Build
import java.util.*

object LanguageManager {
    fun setLocale(context: Context, languageCode: String) {
        val locale = Locale(languageCode)
        Locale.setDefault(locale)

        val config = Configuration()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            config.setLocale(locale)
        } else {
            config.locale = locale
        }

        context.resources.updateConfiguration(config, context.resources.displayMetrics)

        // Save the selected language in SharedPreferences
        val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        prefs.edit().putString("language", languageCode).apply()
    }

    fun getSavedLocale(context: Context): String {
        val prefs = context.getSharedPreferences("settings", Context.MODE_PRIVATE)
        return prefs.getString("language", Locale.getDefault().language) ?: "en"
    }
}
