package com.mykid.reports

import android.app.TimePickerDialog
import android.content.Context
import android.os.Bundle
import android.widget.TimePicker
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import com.mykid.reports.ui.theme.ReportsTheme
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            ReportsTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    DashboardScreen()
                }
            }
        }
    }
}

@Composable
fun DashboardScreen() {
    var sleepTime by remember { mutableStateOf("") }
    var wakeUpTime by remember { mutableStateOf("") }
    var lessons = remember { mutableStateListOf<Lesson>() }
    var studyTime by remember { mutableStateOf("") }
    var screenOnTime by remember { mutableStateOf("") }
    var totalTestsDay by remember { mutableStateOf("") }
    var report by remember { mutableStateOf("") }
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(text = "داشبورد پیگیری مطالعه", style = MaterialTheme.typography.headlineMedium)

        TimePickerField(label = "زمان خواب", time = sleepTime) { sleepTime = it }
        TimePickerField(label = "زمان بیداری", time = wakeUpTime) { wakeUpTime = it }

        Text(text = "درس‌ها")
        lessons.forEach { lesson ->
            LessonRow(lesson = lesson, onRemove = { lessons.remove(lesson) })
        }

        var lessonName by remember { mutableStateOf("") }
        var lessonStart by remember { mutableStateOf("") }
        var lessonEnd by remember { mutableStateOf("") }
        var totalTests by remember { mutableStateOf("") }
        var correctTests by remember { mutableStateOf("") }
        var failedTests by remember { mutableStateOf("") }
        var unsolvedTests by remember { mutableStateOf("") }

        OutlinedTextField(value = lessonName, onValueChange = { lessonName = it }, label = { Text("نام درس") }, modifier = Modifier.fillMaxWidth())
        TimePickerField(label = "زمان شروع درس", time = lessonStart) { lessonStart = it }
        TimePickerField(label = "زمان پایان درس", time = lessonEnd) { lessonEnd = it }
        NumberField(value = totalTests, onValueChange = { totalTests = it }, label = "تعداد کل تست‌ها")
        NumberField(value = correctTests, onValueChange = { correctTests = it }, label = "تعداد تست‌های صحیح")
        NumberField(value = failedTests, onValueChange = { failedTests = it }, label = "تعداد تست‌های نادرست")
        NumberField(value = unsolvedTests, onValueChange = { unsolvedTests = it }, label = "تعداد تست‌های حل نشده")

        Button(onClick = {
            if (lessonName.isNotEmpty() && lessonStart.isNotEmpty() && lessonEnd.isNotEmpty() &&
                totalTests.isNotEmpty() && correctTests.isNotEmpty() && failedTests.isNotEmpty() && unsolvedTests.isNotEmpty()) {
                val total = totalTests.toIntOrNull() ?: 0
                val correct = correctTests.toIntOrNull() ?: 0
                val percentage = if (total > 0) (correct * 100 / total).toString() + "%" else "N/A"

                lessons.add(Lesson(lessonName, lessonStart, lessonEnd, totalTests, correctTests, failedTests, unsolvedTests, percentage))
                lessonName = ""
                lessonStart = ""
                lessonEnd = ""
                correctTests = ""
                failedTests = ""
                unsolvedTests = ""
                totalTests = ""

            }
        }) { Text("افزودن درس") }

        NumberField(value = totalTestsDay, onValueChange = { totalTestsDay = it }, label = "تعداد کل تست‌ها")
        OutlinedTextField(value = studyTime, onValueChange = { studyTime = it }, label = { Text("زمان مطالعه") }, modifier = Modifier.fillMaxWidth())
        OutlinedTextField(value = screenOnTime, onValueChange = { screenOnTime = it }, label = { Text("تایم گوشی") }, modifier = Modifier.fillMaxWidth())

        Button(onClick = {
            report = buildReport(sleepTime, wakeUpTime, lessons, studyTime, screenOnTime, totalTestsDay)

            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
            val clip = android.content.ClipData.newPlainText("گزارش مطالعه", report)
            clipboard.setPrimaryClip(clip)

            Toast.makeText(context, "گزارش در کلیپ بورد کپی شد", Toast.LENGTH_SHORT).show()
        }) {
            Text("تولید گزارش")
        }

        if (report.isNotEmpty()) {
            Text(text = report, modifier = Modifier.padding(top = 16.dp))
        }
    }
}

@Composable
fun LessonRow(lesson: Lesson, onRemove: () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Column(modifier = Modifier.weight(1f)) {
            Text(text = lesson.name, style = MaterialTheme.typography.bodyLarge)
            Text(text = "شروع: ${lesson.start}, پایان: ${lesson.end}", style = MaterialTheme.typography.bodyMedium)
            Text(text = "کل تست‌ها: ${lesson.totalTests}, صحیح: ${lesson.correctTests}, نادرست: ${lesson.failedTests}, حل نشده: ${lesson.unsolvedTests}, درصد: ${lesson.percentage}", style = MaterialTheme.typography.bodyMedium)
        }
        Button(onClick = onRemove) { Text("حذف") }
    }
}

@Composable
fun TimePickerField(label: String, time: String, onTimeChange: (String) -> Unit) {
    val context = LocalContext.current
    OutlinedTextField(
        value = time,
        onValueChange = { /* No-op */ },
        label = { Text(label) },
        modifier = Modifier.fillMaxWidth(),
        trailingIcon = {
            IconButton(onClick = {
                val calendar = Calendar.getInstance()
                val timePickerDialog = TimePickerDialog(
                    context,
                    { _: TimePicker, hour: Int, minute: Int ->
                        onTimeChange(String.format("%02d:%02d", hour, minute))
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    true
                )
                timePickerDialog.show()
            }) {
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
        modifier = Modifier.fillMaxWidth()
    )
}

data class Lesson(
    val name: String,
    val start: String,
    val end: String,
    val totalTests: String,
    val correctTests: String,
    val failedTests: String,
    val unsolvedTests: String,
    val percentage: String
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
        - **درست**: ${lesson.correctTests}
        - **غلط**: ${lesson.failedTests}
        - **نزده**: ${lesson.unsolvedTests}
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
