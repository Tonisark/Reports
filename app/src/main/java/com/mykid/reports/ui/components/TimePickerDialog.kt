package com.mykid.reports.ui.components

import androidx.compose.material3.*
import androidx.compose.runtime.*
import com.mykid.reports.data.localization.LocalizationManager
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (LocalTime) -> Unit,
    initialTime: LocalTime = LocalTime.now()
) {
    if (showDialog) {
        var state by remember { mutableStateOf(TimePickerState(
            initialHour = initialTime.hour,
            initialMinute = initialTime.minute,
            is24Hour = true
        )) }

        AlertDialog(
            onDismissRequest = onDismiss,
            confirmButton = {
                TextButton(onClick = {
                    onConfirm(LocalTime.of(state.hour, state.minute))
                    onDismiss()
                }) {
                    Text(LocalizationManager.getString("confirm"))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text(LocalizationManager.getString("cancel"))
                }
            },
            text = {
                TimePicker(state = state)
            }
        )
    }
} 