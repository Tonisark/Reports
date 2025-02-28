package com.mykid.reports.ui.screens.dashboard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mykid.reports.data.repository.BookRepository
import com.mykid.reports.data.repository.LessonRepository
import com.mykid.reports.domain.model.Lesson
import com.mykid.reports.ui.base.BaseViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.mykid.reports.utils.buildReport
import com.mykid.reports.utils.LocalizationManager

data class DashboardUiState(
    val sleepTime: String = "",
    val wakeUpTime: String = "",
    val studyTime: String = "",
    val screenTime: String = "",
    val totalTests: String = "",
    val report: String = "",
    val recentLessons: List<Lesson> = emptyList(),
    val availableBooks: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class DashboardViewModel(
    application: Application
) : BaseViewModel<DashboardState>(DashboardState()) {

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as Application
                DashboardViewModel(application)
            }
        }
    }

    private val bookRepository = BookRepository(application)
    private val lessonRepository = LessonRepository(application)
    private var sharedSleepTime: String = ""
    private var sharedWakeUpTime: String = ""

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        viewModelScope.launch {
            try {
                setLoading(true)
                val books = bookRepository.loadBooks()
                val lessons = lessonRepository.loadLessons()
                updateState { 
                    it.copy(
                        availableBooks = books,
                        lessons = lessons
                    ) 
                }
                if (lessons.isNotEmpty()) {
                    setSharedTimes(lessons.first().sleepTime, lessons.first().wakeUpTime)
                }
            } catch (e: Exception) {
                setError(e.message)
            } finally {
                setLoading(false)
            }
        }
    }

    fun setSharedTimes(sleepTime: String, wakeUpTime: String) {
        sharedSleepTime = sleepTime
        sharedWakeUpTime = wakeUpTime
        updateState { 
            it.copy(
                sleepTime = sleepTime,
                wakeUpTime = wakeUpTime
            )
        }
    }

    fun getSharedTimes(): Pair<String, String> = Pair(sharedSleepTime, sharedWakeUpTime)

    fun updateSleepTime(time: String) {
        updateState { it.copy(sleepTime = time) }
    }

    fun updateWakeUpTime(time: String) {
        updateState { it.copy(wakeUpTime = time) }
    }

    fun updateStudyTime(time: String) {
        updateState { it.copy(studyTime = time) }
    }

    fun updateScreenTime(time: String) {
        updateState { it.copy(screenTime = time) }
    }

    fun updateTotalTests(tests: String) {
        updateState { it.copy(totalTests = tests) }
    }

    fun updateAvailableBooks(books: List<String>) {
        updateState { it.copy(availableBooks = books) }
    }

    fun addLesson(lesson: Lesson) {
        val updatedLesson = lesson.copy(
            sleepTime = sharedSleepTime,
            wakeUpTime = sharedWakeUpTime
        )
        viewModelScope.launch {
            try {
                val currentLessons = uiState.value.lessons + updatedLesson
                lessonRepository.saveLessons(currentLessons)
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
                lessonRepository.saveLessons(currentLessons)
                updateState { it.copy(lessons = currentLessons) }
            } catch (e: Exception) {
                setError("Failed to remove lesson: ${e.message}")
            }
        }
    }

    fun showSnackbar(message: String) {
        updateState { it.copy(error = message) }
    }

    private fun generateReport() {
        val state = uiState.value
        if (state.lessons.isNotEmpty()) {
            val currentLocale = LocalizationManager.getCurrentLocale()
            val isEnglish = currentLocale.language == "en"
            val report = buildReport(state.lessons, isEnglish)
            updateState { it.copy(report = report) }
        }
    }

    fun clearReport() {
        updateState { DashboardState() }
    }
} 