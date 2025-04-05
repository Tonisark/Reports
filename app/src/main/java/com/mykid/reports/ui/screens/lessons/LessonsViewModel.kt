package com.mykid.reports.ui.screens.lessons

import android.app.Application
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.mykid.reports.data.repository.LessonRepository
import com.mykid.reports.domain.model.Lesson
import com.mykid.reports.ui.base.BaseViewModel
import kotlinx.coroutines.launch
import java.util.UUID

class LessonsViewModel(
    application: Application
) : BaseViewModel<LessonsState>(LessonsState()) {
    private val repository = LessonRepository(application)
    private val appContext = application.applicationContext

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

    override fun setError(message: String?) {
        updateState { it.copy(error = message, isLoading = false) }
    }

    fun addLesson(lesson: Lesson) {
        viewModelScope.launch {
            try {
                val lessonWithId = if (lesson.id.isBlank()) {
                    lesson.copy(id = UUID.randomUUID().toString())
                } else {
                    lesson
                }
                val currentLessons = uiState.value.lessons + lessonWithId
                repository.saveLessons(currentLessons)
                updateState { it.copy(lessons = currentLessons) }
            } catch (e: Exception) {
                setError("Failed to save lesson: ${e.message}")
            }
        }
    }

    fun updateLesson(oldLesson: Lesson, newLesson: Lesson) {
        viewModelScope.launch {
            try {
                val currentLessons = uiState.value.lessons.map {
                    if (it.id == oldLesson.id) newLesson.copy(id = oldLesson.id) else it
                }
                repository.saveLessons(currentLessons)
                updateState { it.copy(lessons = currentLessons, successMessage = "Lesson updated") }
            } catch (e: Exception) {
                setError("Failed to update lesson: ${e.message}")
            }
        }
    }

    fun removeLesson(lesson: Lesson) {
        viewModelScope.launch {
            try {
                val currentLessons = uiState.value.lessons.filterNot { it.id == lesson.id }
                repository.saveLessons(currentLessons)
                updateState { it.copy(lessons = currentLessons, successMessage = "Lesson removed") }
            } catch (e: Exception) {
                setError("Failed to remove lesson: ${e.message}")
            }
        }
    }

    fun copyLessonToClipboard(lesson: Lesson) {
        try {
            val lessonText = """
                📚 Lesson: ${lesson.name}
                ⏱️ Duration: ${lesson.start} - ${lesson.end}
                📊 Performance:
                   • Total Tests: ${lesson.totalTests}
                   ✅ Correct: ${lesson.correctTests}
                   ❌ Wrong: ${lesson.failedTests}
                   ⏳ Unsolved: ${lesson.unsolvedTests}
                   📈 Percentage: ${"%.1f".format(lesson.percentage)}%
            """.trimIndent()

            val clipboard = appContext.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Lesson Details", lessonText)
            clipboard.setPrimaryClip(clip)

            updateState { it.copy(successMessage = "Lesson copied to clipboard") }
        } catch (e: Exception) {
            setError("Failed to copy lesson: ${e.message}")
        }
    }

    fun loadLessons() {
        viewModelScope.launch {
            try {
                updateState { it.copy(isLoading = true) }
                val lessons = repository.loadLessons()
                updateState { it.copy(lessons = lessons, isLoading = false) }
            } catch (e: Exception) {
                setError("Failed to load lessons: ${e.message}")
                updateState { it.copy(isLoading = false) }
            }
        }
    }

    fun clearMessage() {
        updateState { it.copy(successMessage = null, error = null) }
    }


}