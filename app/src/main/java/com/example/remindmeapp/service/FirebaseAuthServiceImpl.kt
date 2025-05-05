package com.example.remindmeapp.service

import android.content.Context
import androidx.activity.result.ActivityResult
import com.example.remindmeapp.model.User
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class FirebaseAuthServiceImpl @Inject constructor() : FirebaseAuthServiceInterface {
    private val auth: FirebaseAuth = Firebase.auth
    private lateinit var googleSignInClient: GoogleSignInClient

    override fun getCurrentUserId(): String {
        return auth.currentUser?.uid ?: ""
    }

    override fun initializeGoogleSignIn(context: Context, clientId: String) {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(clientId)
            .requestEmail()
            .build()
        googleSignInClient = GoogleSignIn.getClient(context, gso)
    }

    override fun getGoogleSignInClient(): GoogleSignInClient = googleSignInClient

    override suspend fun handleGoogleSignInResult(result: ActivityResult): User? {
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        val account = task.await()
        val credential = GoogleAuthProvider.getCredential(account.idToken, null)
        val authResult = auth.signInWithCredential(credential).await()
        return authResult.user?.let { user ->
            User(
                id = user.uid,
                name = user.displayName ?: "",
                email = user.email ?: "",
                photoUrl = user.photoUrl?.toString()
            )
        }
    }

    override suspend fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    override suspend fun loginWithEmail(
        email: String,
        password: String,
        onSuccess: (User) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            val authResult = auth.signInWithEmailAndPassword(email, password).await()
            authResult.user?.let { firebaseUser ->
                val user = User(
                    id = firebaseUser.uid,
                    email = firebaseUser.email ?: "",
                    name = firebaseUser.displayName ?: "",
                    photoUrl = firebaseUser.photoUrl?.toString()
                )
                onSuccess(user)
            } ?: onFailure(Exception("User not found"))
        } catch (e: Exception) {
            onFailure(e)
        }
    }

    override suspend fun signUpWithEmail(
        email: String,
        password: String,
        name: String,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        try {
            auth.createUserWithEmailAndPassword(email, password).await()
            onSuccess()
        } catch (e: Exception) {
            onFailure(e)
        }
    }
}
