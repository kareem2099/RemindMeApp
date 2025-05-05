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
import android.util.Log

class ReminderService @Inject constructor() {
    private val db: FirebaseFirestore = Firebase.firestore
    private val remindersCollection = db.collection("reminders")

    suspend fun addReminder(reminder: Reminder): Reminder {
        val docRef = remindersCollection.add(reminder.toMap()).await()
        return reminder.copy(id = docRef.id)
    }

    suspend fun getReminder(id: String): Reminder? {
        return try {
            val doc = remindersCollection.document(id).get().await()
            doc.toObject(Reminder::class.java)?.copy(id = doc.id)
        } catch (e: Exception) {
            null
        }
    }
    
    

    suspend fun updateReminder(reminder: Reminder): Boolean {
        return try {
            remindersCollection.document(reminder.id)
                .update(reminder.toMap()) // updates customId, title, etc.
                .await()
            true
        } catch (e: Exception) {
            false
        }
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
