package com.example.remindmeapp.screens.add

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.remindmeapp.model.Reminder
import com.example.remindmeapp.service.FirebaseAuthServiceInterface
import com.example.remindmeapp.service.ReminderService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddReminderViewModel @Inject constructor(
    private val reminderService: ReminderService,
    private val authService: FirebaseAuthServiceInterface
) : ViewModel() {

    fun addReminder(
        customId: String,
        title: String,
        description: String,
        reminderTime: Long,
        onComplete: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // Let Firestore generate the ID
                val reminder = Reminder(
                    id = "", // Will be set by Firestore
                    customId = customId,
                    title = title,
                    description = description,
                    time = System.currentTimeMillis(),
                    reminderTime = reminderTime,
                    userId = authService.getCurrentUserId()
                )
                val savedReminder = reminderService.addReminder(reminder)
                onComplete(savedReminder.id.isNotBlank())
            } catch (e: Exception) {
                onComplete(false)
            }
        }
    }
}
