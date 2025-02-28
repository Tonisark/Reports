package com.mykid.reports.utils

import com.mykid.reports.domain.model.Lesson

fun buildReport(lessons: List<Lesson>, isEnglish: Boolean = true): String {
    if (lessons.isEmpty()) return "No lessons to report"

    val lessonDetails = lessons.joinToString("\n") { lesson ->
        if (isEnglish) {
            """
            
            ← ${lesson.name}
            - **Start Time**: ${lesson.start}
            - **End Time**: ${lesson.end}    
            - **Correct**: ${lesson.correctTests}  - **Wrong**: ${lesson.failedTests}  - **Unsolved**: ${lesson.unsolvedTests}
            - **Total Tests**: ${lesson.totalTests}
            - **Percentage**: ${String.format("%.1f", lesson.percentage)}%
            """.trimIndent()
        } else {
            """
            
            ← ${lesson.name}
            - **زمان شروع**: ${lesson.start}
            - **زمان پایان**: ${lesson.end}    
            - **درست**: ${lesson.correctTests}  - **غلط**: ${lesson.failedTests}  - **نزده**: ${lesson.unsolvedTests}
            - **کل تست‌ها**: ${lesson.totalTests}
            - **درصد**: ${String.format("%.1f", lesson.percentage)}%
            """.trimIndent()
        }
    }

    val totalTests = lessons.sumOf { it.totalTests }
    val totalCorrect = lessons.sumOf { it.correctTests }
    val totalFailed = lessons.sumOf { it.failedTests }
    
    // Calculate total percentage using the same formula
    val totalPercentage = if (totalTests > 0) {
        ((((totalCorrect * 3) - totalFailed).toFloat() / (totalTests * 3)) * 100)
    } else {
        0f
    }

    return if (isEnglish) {
        """
        ← Study Report
        
        ** 💤 Sleep Time**: ${lessons.firstOrNull()?.sleepTime ?: ""}
        ** Wake Up Time**: ${lessons.firstOrNull()?.wakeUpTime ?: ""}
        
        ## Lessons
        $lessonDetails
        
        ** ← Total Tests**: $totalTests
        ** ← Total Percentage**: ${String.format("%.1f", totalPercentage)}%
        ** ← Study Time**: ${lessons.firstOrNull()?.studyTime ?: ""}
        ** ← Screen Time**: ${lessons.firstOrNull()?.screenTime ?: ""}
        """.trimIndent()
    } else {
        """
        ← گزارش مطالعه
        
        ** 💤 زمان خواب **: ${lessons.firstOrNull()?.sleepTime ?: ""}
        **  زمان بیداری **: ${lessons.firstOrNull()?.wakeUpTime ?: ""}
        
        ## درس‌ها
        $lessonDetails
        
        ** ← تعداد کل تست‌ها** : $totalTests
        ** ← درصد کل** : ${String.format("%.1f", totalPercentage)}%
        ** ← تایم مطالعه** : ${lessons.firstOrNull()?.studyTime ?: ""}
        ** ← تایم گوشی** : ${lessons.firstOrNull()?.screenTime ?: ""}
        """.trimIndent()
    }
} 