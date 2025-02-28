package com.mykid.reports.data.repository

import android.content.Context
import com.mykid.reports.data.storage.SharedPreferencesManager
import com.mykid.reports.domain.model.Lesson
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class LessonRepository(private val context: Context) {
    private val sharedPreferences = SharedPreferencesManager(context)
    private val json = Json { ignoreUnknownKeys = true }

    fun saveLessons(lessons: List<Lesson>) {
        val lessonsJson = json.encodeToString(lessons)
        sharedPreferences.saveString("lessons", lessonsJson)
    }

    fun loadLessons(): List<Lesson> {
        val lessonsJson = sharedPreferences.getString("lessons", "[]")
        return try {
            json.decodeFromString(lessonsJson)
        } catch (e: Exception) {
            emptyList()
        }
    }
} 