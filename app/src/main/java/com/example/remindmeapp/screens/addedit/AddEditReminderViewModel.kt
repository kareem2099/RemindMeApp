package com.example.remindmeapp.screens.addedit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.remindmeapp.model.Reminder
import com.example.remindmeapp.service.FirebaseAuthServiceInterface
import com.example.remindmeapp.service.ReminderService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddEditReminderViewModel @Inject constructor(
    private val reminderService: ReminderService,
    private val authService: FirebaseAuthServiceInterface
) : ViewModel() {

    fun getCurrentUserId(): String {
        return authService.getCurrentUserId()
    }

    fun saveReminder(reminder: Reminder, onComplete: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                if (reminder.id.isEmpty()) {
                    reminderService.addReminder(reminder)
                } else {
                    reminderService.updateReminder(reminder)
                }
                onComplete(true)
            } catch (e: Exception) {
                onComplete(false)
            }
        }
    }
}
