package com.mykid.reports.data.localization

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

object LocalizationManager {
    private val _currentLocale = MutableStateFlow(Locale.getDefault())
    val currentLocale = _currentLocale.asStateFlow()

    private val translations = mutableMapOf<String, Map<String, String>>()

    init {
        translations["en"] = mapOf(
            "study_analysis" to "Study Analysis",
            "back" to "Back",
            "performance_overview" to "Performance Overview",
            "study_hours" to "Study Hours",
            "total_tests" to "Total Tests",
            "avg_score" to "Avg Score",
            "progress_chart" to "Progress Chart",
            "test_distribution" to "Test Distribution",
            "study_time_distribution" to "Study Time Distribution",
            "study_report" to "Study Report",
            "sleep_time" to "Sleep Time",
            "wake_up_time" to "Wake Up Time",
            "lessons" to "Lessons",
            "total_tests" to "Total Tests",
            "total_percentage" to "Total Percentage",
            "study_time" to "Study Time",
            "screen_time" to "Screen Time",
            "settings" to "Settings",
            //DashBoard Title
            "dashboard" to "Dash Board",

            //MenuItem
            "analysis" to "Analysis",
            //MenuItem
            "Dash Board" to "Dash Board"
        )

        translations["fa"] = mapOf(
            "study_analysis" to "تحلیل مطالعه",
            "back" to "بازگشت",
            "performance_overview" to "نمای کلی عملکرد",
            "study_hours" to "ساعت مطالعه",
            "total_tests" to "کل تست‌ها",
            "avg_score" to "میانگین نمره",
            "progress_chart" to "نمودار پیشرفت",
            "test_distribution" to "توزیع تست‌ها",
            "study_time_distribution" to "توزیع زمان مطالعه",
            "study_report" to "گزارش مطالعه",
            "sleep_time" to "زمان خواب",
            "wake_up_time" to "زمان بیداری",
            "lessons" to "درس‌ها",
            "total_tests" to "تعداد کل تست‌ها",
            "total_percentage" to "درصد کل",
            "study_time" to "تایم مطالعه",
            "screen_time" to "تایم گوشی",
            "settings" to "تنظیمات",
            //DashBoard Title
            "dashboard" to "داشبورد",
            //MenuItem
            "analysis" to "انالیز",
            //MenuItem
            "Dash Board" to "داشبورد"

        )
    }

    fun setLocale(locale: Locale) {
        _currentLocale.value = locale // Trigger recomposition
    }

    fun getString(key: String): String {
        val languageCode = _currentLocale.value.language
        return translations[languageCode]?.get(key) ?: translations["en"]?.get(key) ?: key
    }
}
