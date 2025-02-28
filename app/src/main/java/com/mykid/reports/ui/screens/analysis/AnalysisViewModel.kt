package com.mykid.reports.ui.screens.analysis

import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import androidx.lifecycle.ViewModel
import com.mykid.reports.domain.model.Lesson



class AnalysisViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(AnalysisState())
    val uiState: StateFlow<AnalysisState> = _uiState.asStateFlow()

    init {
        loadAnalysis()
    }

    private fun loadAnalysis() {
        viewModelScope.launch {
            try {
                _uiState.value = AnalysisState(isLoading = true)

                val progressData = listOf(
                    0f to 70f,
                    1f to 75f,
                    2f to 72f,
                    3f to 80f,
                    4f to 85f
                )
                
                _uiState.value = AnalysisState(
                    studyHours = 6.5f,
                    totalTests = 100,
                    averageScore = 85.5f,
                    correctTests = 75,
                    wrongTests = 15,
                    unsolvedTests = 10,
                    screenTime = 2f,
                    sleepTime = 8f,
                    weeklyProgress = progressData
                )
            } catch (e: Exception) {
                _uiState.value = AnalysisState(error = e.message)
            }
        }
    }

    fun calculateProgress(lessons: List<Lesson>): List<Float> {
        return lessons.map { lesson ->
            lesson.percentage
        }
    }
} 