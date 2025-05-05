package com.example.remindmeapp.screens.home

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.MaterialTheme
import com.example.remindmeapp.ui.components.SearchBar
import androidx.compose.material3.*
import com.example.remindmeapp.ui.components.SwipeToDelete
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.Modifier
import androidx.compose.foundation.background
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.remindmeapp.model.Reminder
import com.example.remindmeapp.navigation.Screen
import com.example.remindmeapp.ui.components.PrimaryButton
import java.text.SimpleDateFormat
import java.util.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.remindmeapp.ui.theme.AppTypography

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavController) {
    val viewModel: HomeViewModel = hiltViewModel()
    val reminders by viewModel.reminders.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    var showMenu by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Reminders",
                style = AppTypography.displayLarge,
                fontWeight = FontWeight.Bold
            )
            
            IconButton(onClick = { showMenu = !showMenu }) {
                Icon(Icons.Default.MoreVert, contentDescription = "Menu")
            }
            
            DropdownMenu(
                expanded = showMenu,
                onDismissRequest = { showMenu = false }
            ) {
                DropdownMenuItem(
                    text = { Text("Settings", style = AppTypography.bodyLarge) },
                    onClick = { /* TODO */ }
                )
                DropdownMenuItem(
                    text = { Text("About", style = AppTypography.bodyLarge) },
                    onClick = { /* TODO */ }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search Bar
        SearchBar(
            query = searchQuery,
            onQueryChange = { viewModel.searchReminders(it) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = "Search reminders..."
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Reminders List
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(reminders) { reminder ->
                ReminderItem(
                    reminder = reminder,
                    onReminderClick = { 
                        navController.navigate(Screen.Edit.createRoute(reminder.id)) 
                    },
                    onDelete = {
                        viewModel.deleteReminder(reminder.id)
                    }
                )
            }
        }

        // Add Reminder Button
        PrimaryButton(
            text = "Add Reminder",
            onClick = { navController.navigate(Screen.Add.createRoute()) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp)
        )
    }
}

@Composable
fun ReminderItem(
    reminder: Reminder,
    onReminderClick: () -> Unit,
    onDelete: () -> Unit
) {
    val formattedDate = remember {
        SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault())
            .format(Date(reminder.time))
    }

    SwipeToDelete(
        onDelete = onDelete
    ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { onReminderClick() },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Reminder",
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = reminder.title,
                            style = AppTypography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = reminder.description,
                        style = AppTypography.bodyLarge,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = formattedDate,
                        style = AppTypography.labelLarge,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
    }
}
