package com.mykid.reports.ui.screens.settings.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun NotificationPreferences(
    enabled: Boolean,
    onEnabledChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Switch(
        checked = enabled,
        onCheckedChange = onEnabledChange,
        modifier = modifier
    )
} 