package com.mykid.reports.ui.screens.lessons

import android.app.Application
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.mykid.reports.data.repository.LessonRepository
import com.mykid.reports.domain.model.Lesson
import com.mykid.reports.ui.base.BaseViewModel
import kotlinx.coroutines.launch

class LessonsViewModel(
    application: Application
) : BaseViewModel<LessonsState>(LessonsState()) {
    private val repository = LessonRepository(application)

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                LessonsViewModel(application)
            }
        }
    }

    init {
        loadLessons()
    }

    fun addLesson(lesson: Lesson) {
        viewModelScope.launch {
            try {
                val currentLessons = uiState.value.lessons + lesson
                repository.saveLessons(currentLessons)
                updateState { it.copy(lessons = currentLessons) }
            } catch (e: Exception) {
                setError("Failed to save lesson: ${e.message}")
            }
        }
    }

    fun removeLesson(lesson: Lesson) {
        viewModelScope.launch {
            try {
                val currentLessons = uiState.value.lessons - lesson
                repository.saveLessons(currentLessons)
                updateState { it.copy(lessons = currentLessons) }
            } catch (e: Exception) {
                setError("Failed to remove lesson: ${e.message}")
            }
        }
    }

    fun loadLessons() {
        viewModelScope.launch {
            try {
                setLoading(true)
                val lessons = repository.loadLessons()
                updateState { it.copy(lessons = lessons) }
            } catch (e: Exception) {
                setError("Failed to load lessons: ${e.message}")
            } finally {
                setLoading(false)
            }
        }
    }
} 