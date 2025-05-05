package com.example.remindmeapp.service

import com.example.remindmeapp.model.Reminder
import com.example.remindmeapp.model.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.toObject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseService @Inject constructor() {
    private val db = FirebaseFirestore.getInstance()
    private val usersCollection = db.collection("users")
    private val remindersCollection = db.collection("reminders")

    // User functions
    fun saveUser(user: User, callback: (Boolean) -> Unit) {
        usersCollection.document(user.id)
            .set(user)
            .addOnSuccessListener { callback(true) }
            .addOnFailureListener { callback(false) }
    }

    // Reminder functions
    suspend fun saveReminder(reminder: Reminder): Boolean {
        return try {
            remindersCollection.document(reminder.id).set(reminder).await()
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getReminders(): Flow<List<Reminder>> = flow {
        val snapshot = remindersCollection.get().await()
        val reminders = snapshot.documents.mapNotNull { it.toObject<Reminder>() }
        emit(reminders)
    }

    suspend fun deleteReminder(id: String): Boolean {
        return try {
            remindersCollection.document(id).delete().await()
            true
        } catch (e: Exception) {
            false
        }
    }
}
