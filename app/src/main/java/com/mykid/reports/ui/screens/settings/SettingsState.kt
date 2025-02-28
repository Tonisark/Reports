package com.mykid.reports.ui.screens.settings

import com.mykid.reports.ui.base.UiState

data class SettingsState(
    val selectedLanguage: String = "English",
    val isDarkTheme: Boolean = false,
    val notificationsEnabled: Boolean = true,
    val studyRemindersEnabled: Boolean = true,
    val screenTimeLimit: Int = 120,
    override val isLoading: Boolean = false,
    override val error: String? = null
) : UiState {
    override fun updateLoading(loading: Boolean): UiState = 
        copy(isLoading = loading)

    override fun updateError(error: String?): UiState = 
        copy(error = error)
} 