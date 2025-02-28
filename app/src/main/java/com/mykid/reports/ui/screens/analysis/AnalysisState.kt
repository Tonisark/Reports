package com.mykid.reports.ui.screens.analysis

import com.mykid.reports.ui.base.UiState

data class AnalysisState(
    val studyHours: Float = 0f,
    val totalTests: Int = 0,
    val averageScore: Float = 0f,
    val weeklyProgress: List<Float> = emptyList(),
    override val isLoading: Boolean = false,
    override val error: String? = null
) : UiState {
    override fun updateLoading(loading: Boolean): UiState = 
        copy(isLoading = loading)

    override fun updateError(error: String?): UiState = 
        copy(error = error)
} 