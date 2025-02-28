package com.mykid.reports.ui.components

import android.app.TimePickerDialog
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.mykid.reports.data.localization.LocalizationManager
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerField(
    label: String,
    time: String,
    onTimeChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showDialog by remember { mutableStateOf(false) }
    val formatter = DateTimeFormatter.ofPattern("HH:mm")

    OutlinedTextField(
        value = time,
        onValueChange = { },
        label = { Text(LocalizationManager.getString(label)) },
        readOnly = true,
        modifier = modifier,
        trailingIcon = {
            IconButton(onClick = { showDialog = true }) {
                Icon(
                    imageVector = Icons.Default.Schedule,
                    contentDescription = LocalizationManager.getString("select_time")
                )
            }
        }
    )

    TimePickerDialog(
        showDialog = showDialog,
        onDismiss = { showDialog = false },
        onConfirm = { selectedTime ->
            onTimeChange(selectedTime.format(formatter))
        },
        initialTime = if (time.isNotEmpty()) {
            LocalTime.parse(time, formatter)
        } else {
            LocalTime.now()
        }
    )
} 