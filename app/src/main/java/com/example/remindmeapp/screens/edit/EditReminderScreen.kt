package com.example.remindmeapp.screens.edit

import android.app.TimePickerDialog
import android.util.Log
import androidx.compose.foundation.layout.*
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.remindmeapp.ui.components.PrimaryButton
import com.example.remindmeapp.ui.components.TextFieldInput

@Composable
fun EditReminderScreen(
    navController: NavController,
    reminderId: String,
    viewModel: EditReminderViewModel = hiltViewModel()
) {
    var title by remember { mutableStateOf("") }

@Composable
fun DateTimePickerSection(
    initialTime: Long,
    onTimeSelected: (Long) -> Unit
) {
    var showDateTimePicker by remember { mutableStateOf(false) }
    val dateTimeFormatter = remember { SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault()) }
    val dateTimeText = dateTimeFormatter.format(Date(initialTime))
    val context = LocalContext.current

    Column {
        Button(
            onClick = { showDateTimePicker = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Date & Time: $dateTimeText")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LaunchedEffect(showDateTimePicker) {
            if (showDateTimePicker) {
                val calendar = Calendar.getInstance().apply { timeInMillis = initialTime }
                val datePickerDialog = android.app.DatePickerDialog(
                    context,
                    { _, year, month, day ->
                        calendar.set(year, month, day)
                        val timePickerDialog = android.app.TimePickerDialog(
                            context,
                            { _, hour, minute ->
                                calendar.set(Calendar.HOUR_OF_DAY, hour)
                                calendar.set(Calendar.MINUTE, minute)
                                onTimeSelected(calendar.timeInMillis)
                            },
                            calendar.get(Calendar.HOUR_OF_DAY),
                            calendar.get(Calendar.MINUTE),
                            false
                        )
                        timePickerDialog.show()
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
                )
                datePickerDialog.show()
                showDateTimePicker = false
            }
        }
    }
}
    var description by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(reminderId) {
        Log.d("EditReminderScreen", "Loading reminder with ID: $reminderId")
        if (reminderId.isBlank()) {
            Log.e("EditReminderScreen", "Received blank reminder ID!")
        }
        viewModel.loadReminder(reminderId)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Edit Reminder",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        viewModel.reminder.value?.let { reminder ->
            Text(
                text = "Reminder ID: ${reminder.customId}",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )
        }

        TextFieldInput(
            value = title,
            onValueChange = { title = it },
            label = "Title",
            placeholder = "Enter reminder title"
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextFieldInput(
            value = description,
            onValueChange = { description = it },
            label = "Description",
            placeholder = "Enter reminder description",
            singleLine = false
        )

        DateTimePickerSection(
            initialTime = viewModel.reminder.value?.reminderTime ?: System.currentTimeMillis(),
            onTimeSelected = { newTime ->
                viewModel.updateReminderTime(newTime)
            }
        )

        Spacer(modifier = Modifier.height(32.dp))

        LaunchedEffect(viewModel.reminder.value) {
            viewModel.reminder.value?.let { reminder ->
                title = reminder.title
                description = reminder.description
            }
        }

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            PrimaryButton(
                text = "Save",
                onClick = {
                    if (title.isNotBlank()) {
                        isLoading = true
                        Log.d("EditReminderScreen", "Save button clicked")
                        val current = viewModel.reminder.value
                        if (current == null) {
                            Log.e("EditReminderScreen", "Current reminder is null!")
                            isLoading = false
                            return@PrimaryButton
                        }
                        
                        Log.d("EditReminderScreen", "Calling updateReminder with title: $title, desc: $description, time: ${current.reminderTime}")
                        viewModel.updateReminder(
                            title = title,
                            description = description,
                            reminderTime = current.reminderTime,
                            onComplete = { success ->
                                isLoading = false
                                Log.d("EditReminderScreen", "Update completed with success: $success")
                                if (success) {
                                    navController.popBackStack()
                                }
                            }
                        )
                    }
                }
            )
        }
    }
}
