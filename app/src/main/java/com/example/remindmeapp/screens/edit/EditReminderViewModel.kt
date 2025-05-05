package com.example.remindmeapp.screens.edit

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
        viewModelScope.launch {
            _reminder.value = reminderService.getReminder(id)
        }
    }

    fun updateReminder(
        title: String,
        description: String,
        reminderTime: Long,
        onComplete: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val current = _reminder.value ?: run {
                    onComplete(false)
                    return@launch
                }
                val updated = current.copy(
                    title = title,
                    description = description,
                    reminderTime = reminderTime,
                    time = System.currentTimeMillis()
                )
                val success = reminderService.updateReminder(updated)
                if (success) {
                    _reminder.value = updated
                }
                onComplete(success)
            } catch (e: Exception) {
                onComplete(false)
            }
        }
    }
}
