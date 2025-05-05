package com.example.remindmeapp.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.remindmeapp.model.Reminder
import com.example.remindmeapp.service.ReminderService
import com.example.remindmeapp.service.FirebaseAuthServiceInterface
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val reminderService: ReminderService,
    private val authService: FirebaseAuthServiceInterface
) : ViewModel() {

    private val _reminders = MutableStateFlow<List<Reminder>>(emptyList())
    val reminders: StateFlow<List<Reminder>> = _reminders

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery

    init {
        loadReminders()
    }

    private fun loadReminders() {
        viewModelScope.launch {
            // Get current user ID from auth service
            val userId = authService.getCurrentUserId()
            reminderService.getReminders(userId).collectLatest {
                _reminders.value = it
            }
        }
    }

    fun searchReminders(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            if (query.isBlank()) {
                loadReminders()
            } else {
                val userId = authService.getCurrentUserId()
                reminderService.searchReminders(userId, query).collectLatest {
                    _reminders.value = it
                }
            }
        }
    }

    fun deleteReminder(reminderId: String) {
        viewModelScope.launch {
            // Update local state immediately
            _reminders.value = _reminders.value.filter { it.id != reminderId }
            // Then delete from Firestore
            reminderService.deleteReminder(reminderId)
        }
    }
}
