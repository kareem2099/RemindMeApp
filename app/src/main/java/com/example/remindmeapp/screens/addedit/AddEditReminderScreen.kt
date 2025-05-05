package com.example.remindmeapp.screens.addedit

import com.example.remindmeapp.model.Reminder
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.remindmeapp.ui.components.PrimaryButton
import com.example.remindmeapp.ui.components.TextFieldInput

@Composable
fun AddEditReminderScreen(
    navController: NavController,
    reminderId: String?
) {
    val viewModel: AddEditReminderViewModel = hiltViewModel()
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (reminderId == null) "Add Reminder" else "Edit Reminder",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(32.dp))

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
                text = "Save",
                onClick = {
                if (title.isNotBlank()) {
                    isLoading = true
                    val reminder = Reminder(
                        id = reminderId ?: "",
                        title = title,
                        description = description,
                        time = System.currentTimeMillis(),
                        userId = viewModel.getCurrentUserId()
                    )
                    viewModel.saveReminder(reminder) { success ->
                        isLoading = false
                        if (success) {
                            navController.navigateUp()
                        }
                    }
                }
            }
            )
        }
    }
}
