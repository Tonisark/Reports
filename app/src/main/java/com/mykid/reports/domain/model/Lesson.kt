package com.mykid.reports.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Lesson(
    val name: String,
    val start: String,
    val end: String,
    val totalTests: Int,
    val correctTests: Int,
    val failedTests: Int,
    val unsolvedTests: Int,
    val percentage: Float,
    val sleepTime: String,
    val wakeUpTime: String,
    val studyTime: String,
    val screenTime: String
) 