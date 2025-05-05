package com.example.remindmeapp.screens.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import com.example.remindmeapp.service.FirebaseAuthService
import com.example.remindmeapp.service.FirebaseService
import com.example.remindmeapp.model.User
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import coil.decode.SvgDecoder
import com.example.remindmeapp.navigation.Screen
import com.example.remindmeapp.ui.components.PrimaryButton
import com.example.remindmeapp.ui.components.TextFieldInput
import com.example.remindmeapp.utils.ValidationUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SignUpScreen(navController: NavController) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var nameError by remember { mutableStateOf(false) }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var confirmPasswordError by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        SnackbarHost(
            hostState = snackbarHostState,
            modifier = Modifier.align(Alignment.TopCenter)
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.onPrimary
                ), 
                modifier = Modifier
                    .shadow(elevation = 8.dp, shape = RoundedCornerShape(16.dp))
                    .clip(RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Create Account",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.primary
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Join us today",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            TextFieldInput(
                value = name,
                onValueChange = {
                    name = it
                    nameError = it.isEmpty()
                },
                label = "Full Name",
                placeholder = "Enter your full name",
                isError = nameError,
                supportingText = {
                    if (nameError) {
                        Text("Name cannot be empty")
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextFieldInput(
                value = email,
                onValueChange = {
                    email = it
                    emailError = !ValidationUtils.isValidEmail(it)
                },
                label = "Email",
                placeholder = "Enter your email",
                isError = emailError,
                supportingText = {
                    if (emailError) {
                        Text("Please enter a valid email")
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextFieldInput(
                value = password,
                onValueChange = {
                    password = it
                    passwordError = it.length < 8
                },
                label = "Password",
                placeholder = "Enter your password (min 8 chars)",
                isError = passwordError,
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data("file:///android_asset/svg/${if (passwordVisible) "visibility_off.svg" else "visibility.svg"}")
                                .decoderFactory(SvgDecoder.Factory())
                                .build(),
                            contentDescription = if (passwordVisible) "Hide password" else "Show password"
                        )
                    }
                },
                supportingText = {
                    if (passwordError) {
                        Text("Password must be at least 8 characters")
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextFieldInput(
                value = confirmPassword,
                onValueChange = {
                    confirmPassword = it
                    confirmPasswordError = it != password
                },
                label = "Confirm Password",
                placeholder = "Re-enter your password",
                isError = confirmPasswordError,
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        AsyncImage(
                            model = ImageRequest.Builder(context)
                                .data("file:///android_asset/svg/${if (confirmPasswordVisible) "visibility_off.svg" else "visibility.svg"}")
                                .decoderFactory(SvgDecoder.Factory())
                                .build(),
                            contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password"
                        )
                    }
                },
                supportingText = {
                    if (confirmPasswordError) {
                        Text("Passwords do not match")
                    }
                }
            )

            Spacer(modifier = Modifier.height(32.dp))

            AnimatedVisibility(
                visible = isLoading,
                enter = fadeIn(animationSpec = tween(200)),
                exit = fadeOut(animationSpec = tween(200))
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    strokeWidth = 2.dp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            PrimaryButton(
                text = if (isLoading) "Creating account..." else "Sign Up",
                onClick = {
                    nameError = name.isEmpty()
                    emailError = !ValidationUtils.isValidEmail(email)
                    passwordError = !ValidationUtils.isValidPassword(password)
                    confirmPasswordError = !ValidationUtils.doPasswordsMatch(password, confirmPassword)

                    if (!nameError && !emailError && !passwordError && !confirmPasswordError) {
                        isLoading = true
                        coroutineScope.launch {
                            val authService = FirebaseAuthService()
                                    authService.signUpWithEmail(
                                        email = email,
                                        password = password,
                                        name = name,
                                        onSuccess = {
                                            // Create user profile after successful signup
                                            val user = User(
                                                id = authService.getCurrentUserId(),
                                                name = name,
                                                email = email
                                            )
                                            // Save user to Firestore
                                            FirebaseService().saveUser(user) { success ->
                                                isLoading = false
                                                if (success) {
                                                    navController.navigate(Screen.Home.route) {
                                                        popUpTo(Screen.SignUp.route) {
                                                            inclusive = true
                                                        }
                                                    }
                                                } else {
                                                    coroutineScope.launch {
                                                        snackbarHostState.showSnackbar("Failed to save user profile")
                                                    }
                                                }
                                            }
                                },
                                onFailure = { e ->
                                    isLoading = false
                                    coroutineScope.launch {
                                        val errorMessage = when (e) {
                                            is FirebaseAuthWeakPasswordException -> "Password must be at least 6 characters"
                                            is FirebaseAuthInvalidCredentialsException -> "Invalid email format"
                                            is FirebaseAuthUserCollisionException -> "Email already in use"
                                            else -> "Sign up failed: ${e.localizedMessage}"
                                        }
                                        snackbarHostState.showSnackbar(errorMessage)
                                    }
                                }
                            )
                        }
                    }
                },
                enabled = !isLoading
            )

            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = { navController.navigate(Screen.Login.route) },
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    buildString {
                        append("Already have an account? ")
                        append("Login")
                    },
                )
            }
        }
    }
}
