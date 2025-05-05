package com.example.remindmeapp.service

import android.content.Context
import androidx.activity.result.ActivityResult
import com.example.remindmeapp.model.User
import com.google.android.gms.auth.api.signin.GoogleSignInClient

interface FirebaseAuthServiceInterface {
    fun getCurrentUserId(): String
    fun initializeGoogleSignIn(context: Context, clientId: String)
    fun getGoogleSignInClient(): GoogleSignInClient
    suspend fun handleGoogleSignInResult(result: ActivityResult): User?
    suspend fun sendPasswordResetEmail(email: String)
    suspend fun loginWithEmail(
        email: String,
        password: String,
        onSuccess: (User) -> Unit,
        onFailure: (Exception) -> Unit
    )
    suspend fun signUpWithEmail(
        email: String,
        password: String,
        name: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    )
}
