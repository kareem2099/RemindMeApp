package com.example.remindmeapp.screens.auth

import android.content.Context
import androidx.activity.result.ActivityResult
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.remindmeapp.model.User
import com.example.remindmeapp.service.FirebaseAuthServiceInterface
import com.example.remindmeapp.utils.Constants
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authService: FirebaseAuthServiceInterface
) : ViewModel() {

    private val _loginState = MutableStateFlow<LoginState>(LoginState.Idle)
    val loginState: StateFlow<LoginState> = _loginState

    fun loginWithEmail(email: String, password: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            authService.loginWithEmail(
                email = email,
                password = password,
                onSuccess = { user: User ->
                    _loginState.value = LoginState.Success(user)
                },
                onFailure = { e: Exception ->
                    _loginState.value = LoginState.Error(e.message ?: "Login failed")
                }
            )
        }
    }

    fun initializeGoogleSignIn(context: Context) {
        authService.initializeGoogleSignIn(context, Constants.GOOGLE_CLIENT_ID)
    }

    suspend fun handleGoogleSignInResult(result: ActivityResult): User? {
        return try {
            authService.handleGoogleSignInResult(result)?.also { user: User ->
                _loginState.value = LoginState.Success(user)
            }
        } catch (e: Exception) {
            _loginState.value = LoginState.Error(e.message ?: "Google login failed")
            null
        }
    }

    fun getGoogleSignInClient(): GoogleSignInClient {
        return authService.getGoogleSignInClient()
    }

    suspend fun sendPasswordResetEmail(email: String) {
        try {
            authService.sendPasswordResetEmail(email)
        } catch (e: Exception) {
            _loginState.value = LoginState.Error(e.message ?: "Failed to send reset email")
        }
    }

    sealed class LoginState {
        object Idle : LoginState()
        object Loading : LoginState()
        data class Success(val user: User) : LoginState()
        data class Error(val message: String) : LoginState()
    }
}
