package com.example.remindmeapp.model

import androidx.annotation.Keep
import com.google.firebase.firestore.Exclude

@Keep
data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val photoUrl: String? = null,
    val provider: AuthProvider = AuthProvider.EMAIL
) {
    // Required empty constructor for Firebase
    constructor() : this("", "", "", null, AuthProvider.EMAIL)

    @Exclude
    fun toMap(): Map<String, Any?> = mapOf(
        "id" to id,
        "name" to name,
        "email" to email,
        "photoUrl" to photoUrl,
        "provider" to provider.name
    )

    companion object {
        fun fromMap(map: Map<String, Any>): User = User(
            id = map["id"] as? String ?: "",
            name = map["name"] as? String ?: "",
            email = map["email"] as? String ?: "",
            photoUrl = map["photoUrl"] as? String,
            provider = AuthProvider.valueOf(map["provider"] as? String ?: "EMAIL")
        )
    }
}

enum class AuthProvider {
    EMAIL,
    GOOGLE,
    FACEBOOK
}
