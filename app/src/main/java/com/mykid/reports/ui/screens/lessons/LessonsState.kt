package com.mykid.reports.ui.screens.lessons

import com.mykid.reports.domain.model.Lesson
import com.mykid.reports.ui.base.UiState

data class LessonsState(
    val lessons: List<Lesson> = emptyList(),
    override val isLoading: Boolean = false,
    override val error: String? = null
) : UiState {
    override fun updateLoading(loading: Boolean): UiState = 
        copy(isLoading = loading)

    override fun updateError(error: String?): UiState = 
        copy(error = error)
} 