package com.mykid.reports.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.mykid.reports.data.localization.LocalizationManager
import com.mykid.reports.domain.model.Lesson

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddLessonDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onAddLesson: (Lesson) -> Unit,
    sharedTimes: Pair<String, String>,
    isFirstLesson: Boolean
) {
    if (showDialog) {
        var name by remember { mutableStateOf("") }
        var startTime by remember { mutableStateOf("") }
        var endTime by remember { mutableStateOf("") }
        var correctTests by remember { mutableStateOf("") }
        var failedTests by remember { mutableStateOf("") }
        var unsolvedTests by remember { mutableStateOf("") }
        var sleepTime by remember { mutableStateOf(sharedTimes.first) }
        var wakeUpTime by remember { mutableStateOf(sharedTimes.second) }

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(LocalizationManager.getString("add_lesson")) },
            text = {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text(LocalizationManager.getString("lesson_name")) }
                    )

                    if (isFirstLesson) {
                        TimePickerField(
                            label = "sleep_time",
                            time = sleepTime,
                            onTimeChange = { sleepTime = it }
                        )

                        TimePickerField(
                            label = "wake_up_time",
                            time = wakeUpTime,
                            onTimeChange = { wakeUpTime = it }
                        )
                    }

                    TimePickerField(
                        label = "start_time",
                        time = startTime,
                        onTimeChange = { startTime = it }
                    )

                    TimePickerField(
                        label = "end_time",
                        time = endTime,
                        onTimeChange = { endTime = it }
                    )

                    OutlinedTextField(
                        value = correctTests,
                        onValueChange = { newValue ->
                            if (newValue.all { it.isDigit() }) correctTests = newValue
                        },
                        label = { Text(LocalizationManager.getString("correct_tests")) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = failedTests,
                        onValueChange = { newValue ->
                            if (newValue.all { it.isDigit() }) failedTests = newValue
                        },
                        label = { Text(LocalizationManager.getString("failed_tests")) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = unsolvedTests,
                        onValueChange = { newValue ->
                            if (newValue.all { it.isDigit() }) unsolvedTests = newValue
                        },
                        label = { Text(LocalizationManager.getString("unsolved_tests")) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        if (name.isNotEmpty() && startTime.isNotEmpty() && endTime.isNotEmpty()) {
                            val correct = correctTests.toIntOrNull() ?: 0
                            val failed = failedTests.toIntOrNull() ?: 0
                            val unsolved = unsolvedTests.toIntOrNull() ?: 0
                            val total = correct + failed + unsolved
                            
                            val percentage = if (total > 0) {
                                ((((correct * 3) - failed).toFloat() / (total * 3)) * 100).coerceIn(0f, 100f)
                            } else {
                                0f
                            }
                            
                            onAddLesson(
                                Lesson(
                                    name = name,
                                    start = startTime,
                                    end = endTime,
                                    totalTests = total,
                                    correctTests = correct,
                                    failedTests = failed,
                                    unsolvedTests = unsolved,
                                    percentage = percentage,
                                    sleepTime = sleepTime,
                                    wakeUpTime = wakeUpTime,
                                    studyTime = "$startTime-$endTime",
                                    screenTime = ""
                                )
                            )
                            onDismiss()
                        }
                    }
                ) {
                    Text(LocalizationManager.getString("add"))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(LocalizationManager.getString("cancel"))
                }
            }
        )
    }
} 