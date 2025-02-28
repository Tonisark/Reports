package com.mykid.reports.ui.screens.analysis

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.patrykandpatrick.vico.compose.axis.horizontal.bottomAxis
import com.patrykandpatrick.vico.compose.axis.vertical.startAxis
import com.patrykandpatrick.vico.compose.chart.Chart
import com.patrykandpatrick.vico.compose.chart.line.lineChart
import com.patrykandpatrick.vico.core.entry.entryModelOf
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.background
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import com.mykid.reports.data.localization.LocalizationManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalysisScreen(
    viewModel: AnalysisViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(LocalizationManager.getString("study_analysis")) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = LocalizationManager.getString("back"))
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = LocalizationManager.getString("performance_overview"),
                            style = MaterialTheme.typography.titleLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            StatisticItem(
                                title = LocalizationManager.getString("study_hours"),
                                value = "${uiState.studyHours}h",
                                color = MaterialTheme.colorScheme.primary
                            )
                            StatisticItem(
                                title = LocalizationManager.getString("total_tests"),
                                value = "${uiState.totalTests}",
                                color = MaterialTheme.colorScheme.secondary
                            )
                            StatisticItem(
                                title = LocalizationManager.getString("avg_score"),
                                value = "${String.format("%.1f", uiState.averageScore)}%",
                                color = MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                }
            }
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = LocalizationManager.getString("progress_chart"),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Chart(
                            chart = lineChart(),
                            model = entryModelOf(*uiState.weeklyProgress.map { (x, y) -> 
                                Pair(x, y)
                            }.toTypedArray()),
                            startAxis = startAxis(),
                            bottomAxis = bottomAxis(),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = LocalizationManager.getString("test_distribution"),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        PieChart(
                            correct = uiState.correctTests,
                            wrong = uiState.wrongTests,
                            unsolved = uiState.unsolvedTests
                        )
                    }
                }
            }

            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = LocalizationManager.getString("study_time_distribution"),
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        StudyTimeBar(
                            studyHours = uiState.studyHours,
                            screenTime = uiState.screenTime,
                            sleepTime = uiState.sleepTime
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun StatisticItem(
    title: String,
    value: String,
    color: Color
) {
    Column(
        modifier = Modifier.padding(8.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            color = color
        )
    }
}

@Composable
fun PieChart(
    correct: Int,
    wrong: Int,
    unsolved: Int
) {
    val total = correct + wrong + unsolved
    val correctAngle = 360f * correct / total
    val wrongAngle = 360f * wrong / total

    Canvas(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
    ) {
        val radius = size.minDimension / 2
        drawArc(
            color = Color.Green,
            startAngle = 0f,
            sweepAngle = correctAngle,
            useCenter = true,
            size = Size(radius * 2, radius * 2),
            topLeft = Offset(center.x - radius, center.y - radius)
        )
        drawArc(
            color = Color.Red,
            startAngle = correctAngle,
            sweepAngle = wrongAngle,
            useCenter = true,
            size = Size(radius * 2, radius * 2),
            topLeft = Offset(center.x - radius, center.y - radius)
        )
        drawArc(
            color = Color.Gray,
            startAngle = correctAngle + wrongAngle,
            sweepAngle = 360f - correctAngle - wrongAngle,
            useCenter = true,
            size = Size(radius * 2, radius * 2),
            topLeft = Offset(center.x - radius, center.y - radius)
        )
    }
}

@Composable
fun StudyTimeBar(
    studyHours: Float,
    screenTime: Float,
    sleepTime: Float
) {
    val total = 24f
    val studyWidth = studyHours / total
    val screenWidth = screenTime / total
    val sleepWidth = sleepTime / total

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(24.dp)
    ) {
        Box(
            modifier = Modifier
                .weight(studyWidth)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.primary)
        )
        Box(
            modifier = Modifier
                .weight(screenWidth)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.secondary)
        )
        Box(
            modifier = Modifier
                .weight(sleepWidth)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.tertiary)
        )
    }
} 