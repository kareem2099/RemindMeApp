package com.example.remindmeapp.model

import androidx.annotation.Keep
import com.google.firebase.firestore.Exclude

@Keep
data class Reminder(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val time: Long = System.currentTimeMillis(),
    val userId: String = ""
) {
    // Required empty constructor for Firestore
    constructor() : this("", "", "", 0L, "")

    @Exclude
    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "title" to title,
        "description" to description,
        "time" to time,
        "userId" to userId
    )

    companion object {
        fun fromMap(map: Map<String, Any>): Reminder = Reminder(
            id = map["id"] as? String ?: "",
            title = map["title"] as? String ?: "",
            description = map["description"] as? String ?: "",
            time = map["time"] as? Long ?: 0L,
            userId = map["userId"] as? String ?: ""
        )
    }
}
