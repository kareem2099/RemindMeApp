package com.example.remindmeapp.service

import com.example.remindmeapp.model.Reminder
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.snapshots
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ReminderService @Inject constructor() {
    private val db: FirebaseFirestore = Firebase.firestore
    private val remindersCollection = db.collection("reminders")

    suspend fun addReminder(reminder: Reminder): Reminder {
        val docRef = remindersCollection.add(reminder.toMap()).await()
        return reminder.copy(id = docRef.id)
    }

    suspend fun updateReminder(reminder: Reminder) {
        remindersCollection.document(reminder.id).set(reminder.toMap()).await()
    }

    suspend fun deleteReminder(reminderId: String) {
        remindersCollection.document(reminderId).delete().await()
    }

    fun getReminders(userId: String): Flow<List<Reminder>> {
        return remindersCollection
            .whereEqualTo("userId", userId)
            .orderBy("time", Query.Direction.ASCENDING)
            .snapshots()
            .map { snapshot ->
                snapshot.documents.map { doc ->
                    Reminder.fromMap(doc.data ?: emptyMap())
                }
            }
    }

    fun searchReminders(userId: String, query: String): Flow<List<Reminder>> {
        return remindersCollection
            .whereEqualTo("userId", userId)
            .orderBy("title")
            .startAt(query)
            .endAt("$query~")
            .snapshots()
            .map { snapshot ->
                snapshot.documents.map { doc ->
                    Reminder.fromMap(doc.data ?: emptyMap())
                }
            }
    }
}
