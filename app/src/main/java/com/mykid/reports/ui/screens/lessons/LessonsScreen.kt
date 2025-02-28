package com.mykid.reports.ui.screens.lessons

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mykid.reports.ui.components.AddLessonDialog
import com.mykid.reports.ui.components.LessonRow
import com.mykid.reports.data.localization.LocalizationManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LessonsScreen(
    viewModel: LessonsViewModel = viewModel(factory = LessonsViewModel.Factory),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.loadLessons()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(LocalizationManager.getString("lessons")) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showAddDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = "Add Lesson")
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (uiState.lessons.isEmpty()) {
                Text(
                    text = "No lessons added yet",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    items(uiState.lessons) { lesson ->
                        key(lesson.name + lesson.start) {
                            LessonRow(
                                lesson = lesson,
                                onRemove = { viewModel.removeLesson(lesson) }
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }

            if (showAddDialog) {
                AddLessonDialog(
                    showDialog = showAddDialog,
                    onDismiss = { showAddDialog = false },
                    onAddLesson = { 
                        viewModel.addLesson(it)
                        showAddDialog = false
                    },
                    sharedTimes = Pair("", ""),
                    isFirstLesson = uiState.lessons.isEmpty()
                )
            }

            // Show error if any
            uiState.error?.let { error ->
                Snackbar(
                    modifier = Modifier
                        .padding(16.dp)
                        .align(Alignment.BottomCenter)
                ) {
                    Text(error)
                }
            }
        }
    }
} 