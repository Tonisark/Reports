package com.mykid.reports.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mykid.reports.ui.screens.settings.components.*
import com.mykid.reports.data.localization.LocalizationManager

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(LocalizationManager.getString("settings")) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.Default.ArrowBack, 
                            contentDescription = LocalizationManager.getString("back")
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            SettingsSection(title = LocalizationManager.getString("language")) {
                LanguageSelector(
                    selectedLanguage = uiState.selectedLanguage,
                    onLanguageSelected = { viewModel.updateLanguage(it) }
                )
            }

            SettingsSection(title = LocalizationManager.getString("theme")) {
                Switch(
                    checked = uiState.isDarkTheme,
                    onCheckedChange = { viewModel.updateTheme(it) }
                )
            }

            SettingsSection(title = LocalizationManager.getString("notifications")) {
                NotificationPreferences(
                    enabled = uiState.notificationsEnabled,
                    onEnabledChange = { viewModel.updateNotifications(it) }
                )
            }
        }
    }
} 