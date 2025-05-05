package com.example.remindmeapp.screens.edit

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.State
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.remindmeapp.model.Reminder
import com.example.remindmeapp.service.ReminderService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditReminderViewModel @Inject constructor(
    private val reminderService: ReminderService
) : ViewModel() {
    private val _reminder = mutableStateOf<Reminder?>(null)
    val reminder: State<Reminder?> get() = _reminder

    fun updateReminderTime(newTime: Long) = viewModelScope.launch {
        _reminder.value = _reminder.value?.copy(reminderTime = newTime)
    }

    fun loadReminder(id: String) {
        Log.d("EditReminderVM", "Loading reminder with id: $id")
        if (id.isBlank()) {
            Log.e("EditReminderVM", "Empty reminder ID provided!")
            return
        }
        
        viewModelScope.launch {
            try {
                Log.d("EditReminderVM", "Calling reminderService.getReminder")
                val loadedReminder = reminderService.getReminder(id)
                Log.d("EditReminderVM", "Service returned: ${loadedReminder?.toString()}")
                
                if (loadedReminder == null) {
                    Log.e("EditReminderVM", "No reminder found with ID: $id")
                }
                
                _reminder.value = loadedReminder
                Log.d("EditReminderVM", "ViewModel reminder updated to: ${_reminder.value?.toString()}")
            } catch (e: Exception) {
                Log.e("EditReminderVM", "Error loading reminder", e)
            }
        }
    }

    fun updateReminder(
        title: String,
        description: String,
        reminderTime: Long,
        onComplete: (Boolean) -> Unit
    ) {
        Log.d("EditReminderVM", "Starting updateReminder")
        viewModelScope.launch {
            try {
                val current = _reminder.value ?: run {
                    Log.e("EditReminderVM", "No current reminder to update")
                    onComplete(false)
                    return@launch
                }
                Log.d("EditReminderVM", "Updating reminder ${current.id} with new data")
                val updated = current.copy(
                    title = title,
                    description = description,
                    reminderTime = reminderTime,
                    time = System.currentTimeMillis()
                )
                Log.d("EditReminderVM", "Calling reminderService.updateReminder")
                val success = reminderService.updateReminder(updated)
                Log.d("EditReminderVM", "Service call result: $success")
                if (success) {
                    _reminder.value = updated
                }
                onComplete(success)
            } catch (e: Exception) {
                Log.e("EditReminderVM", "Update failed", e)
                onComplete(false)
            }
        }
    }
}
