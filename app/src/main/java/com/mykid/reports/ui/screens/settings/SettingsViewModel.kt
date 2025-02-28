package com.mykid.reports.ui.screens.settings

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.mykid.reports.data.repository.SettingsRepository
import com.mykid.reports.ui.base.BaseViewModel
import com.mykid.reports.data.localization.LocalizationManager
import kotlinx.coroutines.launch
import java.util.Locale

data class SettingsUiState(
    val selectedLanguage: String = "en",
    val isDarkTheme: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val studyRemindersEnabled: Boolean = true,
    val screenTimeLimit: Int = 120,
    val isLoading: Boolean = false,
    val error: String? = null
)

class SettingsViewModel(application: Application) : BaseViewModel<SettingsState>(SettingsState()) {
    private val repository = SettingsRepository(application)

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                SettingsViewModel(application)
            }
        }
    }

    init {
        loadSettings()
        // Initialize with saved language
        LocalizationManager.setLocale(Locale(repository.getLanguage()))
    }

    private fun loadSettings() {
        updateState {
            it.copy(
                selectedLanguage = repository.getLanguage(),
                isDarkTheme = repository.getTheme(),
                notificationsEnabled = repository.getNotifications()
            )
        }
    }

    fun updateLanguage(language: String) {
        repository.saveLanguage(language)
        LocalizationManager.setLocale(Locale(language))
        updateState { it.copy(selectedLanguage = language) }
    }

    fun updateTheme(isDark: Boolean) {
        repository.saveTheme(isDark)
        updateState { it.copy(isDarkTheme = isDark) }
    }

    fun updateNotifications(enabled: Boolean) {
        repository.saveNotifications(enabled)
        updateState { it.copy(notificationsEnabled = enabled) }
    }

    fun toggleStudyReminders() {
        updateState { it.copy(studyRemindersEnabled = !it.studyRemindersEnabled) }
    }

    fun updateScreenTimeLimit(minutes: Int) {
        updateState { it.copy(screenTimeLimit = minutes) }
    }

    private fun setLanguage(language: String) {
        viewModelScope.launch {
            repository.saveLanguage(language)
        }
    }
}
