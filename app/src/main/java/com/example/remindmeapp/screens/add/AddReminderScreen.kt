package com.example.remindmeapp.screens.add

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.ui.platform.LocalContext
import java.text.SimpleDateFormat
import java.util.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.clickable
import androidx.compose.ui.platform.LocalContext
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import androidx.navigation.NavController
import com.example.remindmeapp.ui.components.PrimaryButton
import com.example.remindmeapp.ui.components.TextFieldInput

@Composable
fun AddReminderScreen(
    navController: NavController,
    viewModel: AddReminderViewModel = hiltViewModel()
) {
    var customId by remember { mutableStateOf("") }
    val id = remember { UUID.randomUUID().toString() } // Auto-generated Firestore ID
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedDate by remember { mutableStateOf(System.currentTimeMillis()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    
    if (showDatePicker) {
        val calendar = Calendar.getInstance().apply { timeInMillis = selectedDate }
        DatePickerDialog(
            context,
            { _, year, month, day ->
                TimePickerDialog(
                    context,
                    { _, hour, minute ->
                        calendar.set(year, month, day, hour, minute)
                        selectedDate = calendar.timeInMillis
                        showDatePicker = false
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    false
                ).show()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Add New Reminder",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

        TextFieldInput(
            value = customId,
            onValueChange = { customId = it },
            label = "Reminder ID",
            placeholder = "Enter unique ID"
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Date/Time Selection
        val dateFormat = remember { SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault()) }
        Text(
            text = "Selected: ${dateFormat.format(Date(selectedDate))}",
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(8.dp))
        PrimaryButton(
            text = "Select Date/Time",
            onClick = { showDatePicker = true }
        )

        Spacer(modifier = Modifier.height(16.dp))

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

        Spacer(modifier = Modifier.height(32.dp))

        if (isLoading) {
            CircularProgressIndicator()
        } else {
            PrimaryButton(
                text = "Add",
                onClick = {
                    if (title.isNotBlank()) {
                        isLoading = true
                        viewModel.addReminder(
                            id = id,
                            customId = customId,
                            title = title,
                            description = description,
                            reminderTime = selectedDate,
                            onComplete = { success ->
                                isLoading = false
                                if (success) {
                                    navController.navigateUp()
                                }
                            }
                        )
                    }
                }
            )
        }
    }
}
