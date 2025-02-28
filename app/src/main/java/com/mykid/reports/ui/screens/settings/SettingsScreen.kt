package com.mykid.reports.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.mykid.reports.data.localization.LocalizationManager
import com.mykid.reports.ui.screens.settings.components.LanguageSelector
import com.mykid.reports.ui.screens.settings.components.SettingsSection

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: SettingsViewModel = viewModel(factory = SettingsViewModel.Factory),
    onNavigateBack: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    val locale by LocalizationManager.currentLocale.collectAsState()
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
            // Language Selector
            SettingsSection(title = LocalizationManager.getString("language")) {
                LanguageSelector(
                    selectedLanguage = uiState.selectedLanguage,
                    onLanguageSelected = { viewModel.updateLanguage(it) }
                )
            }

            // Dark Mode Toggle
            SettingsSection(title = LocalizationManager.getString("theme")) {
                Switch(
                    checked = uiState.isDarkTheme,
                    onCheckedChange = { viewModel.updateTheme(it) }
                )
            }
        }
    }
}
