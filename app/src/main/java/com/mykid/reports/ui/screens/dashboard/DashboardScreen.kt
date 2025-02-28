package com.mykid.reports.ui.screens.dashboard

import android.content.Context
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mykid.reports.data.localization.LocalizationManager
import com.mykid.reports.ui.components.LessonRow
import com.mykid.reports.ui.components.AddLessonDialog
import com.mykid.reports.utils.buildReport

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel = viewModel(factory = DashboardViewModel.Factory),
    onNavigateToSettings: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val snackbarHostState = remember { SnackbarHostState() }
    val sharedTimes = viewModel.getSharedTimes()

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
        floatingActionButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FloatingActionButton(
                    onClick = {
                        if (uiState.lessons.isNotEmpty()) {
                            val report = buildReport(uiState.lessons)
                            copyToClipboard(context, report)
                            viewModel.showSnackbar("Report copied to clipboard")
                        }
                    }
                ) {
                    Icon(Icons.Default.ContentCopy, contentDescription = "Copy Report")
                }
                
                FloatingActionButton(
                    onClick = { showAddDialog = true }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add Lesson")
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            if (uiState.lessons.isEmpty()) {
                Text(
                    text = "No lessons added yet",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(vertical = 16.dp)
                )
            } else {
                Text(
                    text = LocalizationManager.getString("today_lessons"),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                LazyColumn {
                    items(uiState.lessons) { lesson ->
                        LessonRow(
                            lesson = lesson,
                            onRemove = { viewModel.removeLesson(lesson) }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }

            if (showAddDialog) {
                AddLessonDialog(
                    showDialog = showAddDialog,
                    onDismiss = { showAddDialog = false },
                    onAddLesson = { 
                        if (uiState.lessons.isEmpty()) {
                            viewModel.setSharedTimes(it.sleepTime, it.wakeUpTime)
                        }
                        viewModel.addLesson(it)
                    },
                    sharedTimes = sharedTimes,
                    isFirstLesson = uiState.lessons.isEmpty()
                )
            }
        }
    }
}

private fun copyToClipboard(context: Context, text: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
    val clip = android.content.ClipData.newPlainText("Study Report", text)
    clipboard.setPrimaryClip(clip)
} 