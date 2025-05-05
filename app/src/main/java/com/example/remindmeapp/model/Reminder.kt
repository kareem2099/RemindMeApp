package com.example.remindmeapp.model

import androidx.annotation.Keep
import com.google.firebase.firestore.Exclude

@Keep
data class Reminder(
    val id: String = "",         // Firestore doc ID
    val customId: String = "",   // User-defined readable ID
    val title: String = "",
    val description: String = "",
    val time: Long = System.currentTimeMillis(),
    val reminderTime: Long = System.currentTimeMillis(),
    val userId: String = ""
) {
    constructor() : this("", "", "", "", 0L, 0L, "")

    @Exclude
    fun toMap(): Map<String, Any?> = mapOf(
        "customId" to customId,
        "title" to title,
        "description" to description,
        "time" to time,
        "reminderTime" to reminderTime,
        "userId" to userId
    )

    companion object {
        fun fromMap(map: Map<String, Any>): Reminder = Reminder(
            customId = map["customId"] as? String ?: "",
            title = map["title"] as? String ?: "",
            description = map["description"] as? String ?: "",
            time = map["time"] as? Long ?: 0L,
            reminderTime = map["reminderTime"] as? Long ?: 0L,
            userId = map["userId"] as? String ?: ""
        )
    }
}

