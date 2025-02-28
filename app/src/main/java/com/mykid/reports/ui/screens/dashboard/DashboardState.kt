package com.mykid.reports.ui.screens.dashboard

import com.mykid.reports.domain.model.Lesson
import com.mykid.reports.ui.base.UiState

data class DashboardState(
    val sleepTime: String = "",
    val wakeUpTime: String = "",
    val studyTime: String = "",
    val screenTime: String = "",
    val totalTests: String = "",
    val lessons: List<Lesson> = emptyList(),
    val availableBooks: List<String> = emptyList(),
    val report: String = "",
    override val isLoading: Boolean = false,
    override val error: String? = null
) : UiState {
    override fun updateLoading(loading: Boolean): UiState = 
        copy(isLoading = loading)

    override fun updateError(error: String?): UiState = 
        copy(error = error)
} 