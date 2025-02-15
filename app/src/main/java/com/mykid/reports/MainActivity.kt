package com.mykid.reports

import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.TimePicker
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mykid.reports.ui.theme.ReportsTheme
import kotlinx.coroutines.launch
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            var isDarkTheme by remember { mutableStateOf(false) }
            ReportsTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        ThemeToggleButton(isDarkTheme = isDarkTheme, onToggle = { isDarkTheme = !isDarkTheme })
                        Spacer(modifier = Modifier.height(16.dp))
                        DashboardScreen()
                    }
                }
            }
        }
    }
}


@Composable
fun DashboardScreen() {
    var sleepTime by remember { mutableStateOf("") }
    var wakeUpTime by remember { mutableStateOf("") }
    var lessons by remember { mutableStateOf(mutableListOf<Lesson>()) }
    var studyTime by remember { mutableStateOf("") }
    var screenOnTime by remember { mutableStateOf("") }
    var totalTestsDay by remember { mutableStateOf("") }
    var report by remember { mutableStateOf("") }
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    if (lessons.isNotEmpty()) {
                        report = buildReport(sleepTime, wakeUpTime, lessons, studyTime, screenOnTime, totalTestsDay)
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
                        val clip = android.content.ClipData.newPlainText("Study Report", report)
                        clipboard.setPrimaryClip(clip)
                        scope.launch {
                            snackbarHostState.showSnackbar("Report copied to clipboard")
                        }
                    }
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.ContentCopy, contentDescription = "Generate Report")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(text = "Study Tracking Dashboard", style = MaterialTheme.typography.headlineMedium)

            TimePickerField(label = "Sleep Time", time = sleepTime) { sleepTime = it }
            TimePickerField(label = "Wake Up Time", time = wakeUpTime) { wakeUpTime = it }

            Text(text = "Lessons")
            lessons.forEach { lesson ->
                LessonRow(lesson = lesson, onRemove = { lessons.remove(lesson) })
            }

            var lessonName by remember { mutableStateOf("") }
            var lessonStart by remember { mutableStateOf("") }
            var lessonEnd by remember { mutableStateOf("") }
            var correctTests by remember { mutableStateOf("") }
            var failedTests by remember { mutableStateOf("") }
            var unsolvedTests by remember { mutableStateOf("") }

            OutlinedTextField(value = lessonName, onValueChange = { lessonName = it }, label = { Text("Lesson Name") }, modifier = Modifier.fillMaxWidth())
            TimePickerField(label = "Lesson Start Time", time = lessonStart) { lessonStart = it }
            TimePickerField(label = "Lesson End Time", time = lessonEnd) { lessonEnd = it }
            NumberField(value = correctTests, onValueChange = { correctTests = it }, label = "Correct Tests")
            NumberField(value = failedTests, onValueChange = { failedTests = it }, label = "Wrong Tests")
            NumberField(value = unsolvedTests, onValueChange = { unsolvedTests = it }, label = "Unsolved Tests")

            Button(onClick = {
                if (lessonName.isNotEmpty() && lessonStart.isNotEmpty() && lessonEnd.isNotEmpty() &&
                    correctTests.isNotEmpty() && failedTests.isNotEmpty()
                ) {
                    val unsolved = unsolvedTests.toIntOrNull() ?: 0
                    val correct = correctTests.toIntOrNull() ?: 0
                    val failed = failedTests.toIntOrNull() ?: 0
                    val total = correct + failed + unsolved
                    val totalPercent = if (total > 0) {
                        ((((correct * 3) - failed).toFloat() / (total * 3)) * 100).coerceIn(0f, 100f)
                    } else {
                        0f
                    }
                    val formattedTotalPercent = "%.1f".format(totalPercent) + "%"

                    lessons.add(
                        Lesson(
                            name = lessonName,
                            start = lessonStart,
                            end = lessonEnd,
                            totalTests = total.toString(),
                            correctTests = correctTests,
                            failedTests = failedTests,
                            unsolvedTests = unsolved.toString(),
                            percentage = formattedTotalPercent
                        )
                    )

                    lessonName = ""
                    lessonStart = ""
                    lessonEnd = ""
                    correctTests = ""
                    failedTests = ""
                    unsolvedTests = ""
                }
            }) {
                Text("Add Lesson")
            }

            NumberField(value = totalTestsDay, onValueChange = { totalTestsDay = it }, label = "Total Tests")
            OutlinedTextField(value = studyTime, onValueChange = { studyTime = it }, label = { Text("Study Time") }, modifier = Modifier.fillMaxWidth())
            OutlinedTextField(value = screenOnTime, onValueChange = { screenOnTime = it }, label = { Text("Screen Time") }, modifier = Modifier.fillMaxWidth())
        }
    }
}

@Composable
fun LessonRow(lesson: Lesson, onRemove: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(text = lesson.name, style = MaterialTheme.typography.bodyLarge)
                Text(text = "Start: ${lesson.start}, End: ${lesson.end}", style = MaterialTheme.typography.bodyMedium)
                Text(
                    text = "Total: ${lesson.totalTests} | ✅ Correct: ${lesson.correctTests} | ❌ Wrong: ${lesson.failedTests} | ⏳ Unsolved: ${lesson.unsolvedTests} | 📊 Percentage: ${lesson.percentage}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, contentDescription = "Remove")
            }
        }
    }
}

@Composable
fun TimePickerField(label: String, time: String, onTimeChange: (String) -> Unit) {
    val context = LocalContext.current
    var showTimePicker by remember { mutableStateOf(false) }

    if (showTimePicker) {
        val calendar = Calendar.getInstance()
        val timePickerDialog = TimePickerDialog(
            context,
            { _: TimePicker, hour: Int, minute: Int ->
                onTimeChange(String.format("%02d:%02d", hour, minute))
                showTimePicker = false
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
        timePickerDialog.show()
    }

    OutlinedTextField(
        value = time,
        onValueChange = { /* No-op */ },
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        trailingIcon = {
            IconButton(onClick = { showTimePicker = true }) {
                Icon(Icons.Default.Edit, contentDescription = null)
            }
        },
        readOnly = true
    )
}


@Composable
fun NumberField(value: String, onValueChange: (String) -> Unit, label: String) {
    OutlinedTextField(
        value = value,
        onValueChange = { newValue ->
            if (newValue.all { it.isDigit() }) {
                onValueChange(newValue)
            }
        },
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number)
    )
}

@Composable
fun ThemeToggleButton(isDarkTheme: Boolean, onToggle: () -> Unit) {
    val icon = if (isDarkTheme) Icons.Default.FavoriteBorder else Icons.Default.Favorite
    val iconColor = if (isDarkTheme) Color(0xFF03DAC5) else Color(0xFF6200EA)

    Button(
        onClick = onToggle,
        colors = ButtonDefaults.buttonColors(
            containerColor = iconColor,
            contentColor = Color.White
        ),
        modifier = Modifier.fillMaxWidth().height(60.dp)
    ) {
        AnimatedVisibility(visible = isDarkTheme) {
            Icon(
                imageVector = icon,
                contentDescription = "Toggle Theme",
                modifier = Modifier.padding(end = 8.dp)
            )
        }
        Text(if (isDarkTheme) "Switch to Light Mode" else "Switch to Dark Mode")
    }
}



data class Lesson(
    val name: String,
    val start: String,
    val end: String,
    val totalTests: String,
    val correctTests: String,
    val failedTests: String,
    val unsolvedTests: String, // Add this parameter
    val percentage: String // Add this parameter
)

fun buildReport(
    sleepTime: String,
    wakeUpTime: String,
    lessons: List<Lesson>,
    studyTime: String,
    screenOnTime: String,
    totalTestsDay: String
): String {
    val lessonDetails = lessons.joinToString("\n") { lesson ->
        """
            
        ← ${lesson.name}
        - **زمان شروع**: ${lesson.start}
        - **زمان پایان**: ${lesson.end}    
        - **درست**: ${lesson.correctTests}  - **غلط**: ${lesson.failedTests}  - **نزده**: ${lesson.unsolvedTests}
        - **کل تست‌ها**: ${lesson.totalTests}
        - **درصد**: ${lesson.percentage}
        """.trimIndent()
    }

    return """
    ← گزارش مطالعه
    
    ** 💤 زمان خواب **: $sleepTime
    **  زمان بیداری **: $wakeUpTime
    
    ## درس‌ها
    $lessonDetails
    
    ** ← تعداد کل تست‌ها** : $totalTestsDay
    ** ← تایم مطالعه** : $studyTime
    ** ← تایم گوشی** : $screenOnTime
    """.trimIndent()
}

@Preview(showBackground = true)
@Composable
fun PreviewDashboardScreen() {
    ReportsTheme {
        DashboardScreen()

    }
}
