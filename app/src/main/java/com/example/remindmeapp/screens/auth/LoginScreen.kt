package com.example.remindmeapp.screens.auth

import android.app.Activity
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import coil.compose.AsyncImage
import coil.decode.SvgDecoder
import coil.request.ImageRequest
import com.example.remindmeapp.navigation.Screen
import com.example.remindmeapp.ui.components.PrimaryButton
import com.example.remindmeapp.utils.ValidationUtils
import kotlinx.coroutines.launch

@Composable
fun LoginScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel: AuthViewModel = hiltViewModel()
    val loginState by viewModel.loginState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var emailError by remember { mutableStateOf(false) }
    var passwordError by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    var triggerShake by remember { mutableStateOf(false) }
    var resetRequested by remember { mutableStateOf(false) }

    val shakeOffset = remember { Animatable(0f) }

    val googleLauncher = rememberLauncherForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            coroutineScope.launch {
                try {
                    val user = viewModel.handleGoogleSignInResult(result)
                    if (user != null) {
                        navController.navigate(Screen.Home.route)
                    } else {
                        snackbarHostState.showSnackbar("Google login failed: User not found")
                    }
                } catch (e: Exception) {
                    snackbarHostState.showSnackbar("Google login failed: ${e.localizedMessage}")
                }
            }
        } else {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("Google login cancelled")
            }
        }
    }

    LaunchedEffect(loginState) {
        when (val state = loginState) {
            is AuthViewModel.LoginState.Success -> {
                navController.navigate(Screen.Home.route) {
                    popUpTo(Screen.Login.route) { inclusive = true }
                }
            }
            is AuthViewModel.LoginState.Error -> {
                triggerShake = true
                snackbarHostState.showSnackbar(state.message)
            }
            else -> {}
        }
    }

    LaunchedEffect(triggerShake) {
        if (triggerShake) {
            shakeOffset.snapTo(0f)
            shakeOffset.animateTo(0f, keyframes {
                durationMillis = 400
                -10f at 50; 10f at 100; -8f at 150; 8f at 200; -5f at 250; 5f at 300; 0f at 350
            })
            triggerShake = false
        }
    }

    val formAnim = remember { MutableTransitionState(false) }
    LaunchedEffect(Unit) {
        formAnim.targetState = true
        viewModel.initializeGoogleSignIn(context)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        SnackbarHost(snackbarHostState, Modifier.align(Alignment.TopCenter))

        AnimatedVisibility(
            visibleState = formAnim,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it / 3 }, animationSpec = tween(700)),
            exit = fadeOut()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary),
                    modifier = Modifier
                        .offset(x = shakeOffset.value.dp)
                        .shadow(8.dp, RoundedCornerShape(16.dp))
                        .clip(RoundedCornerShape(16.dp))
                ) {
                    Column(Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Welcome Back", style = MaterialTheme.typography.headlineMedium, 
                            color = MaterialTheme.colorScheme.primary)
                        Spacer(Modifier.height(8.dp))
                        Text("Login to your account", style = MaterialTheme.typography.bodyMedium, 
                            color = MaterialTheme.colorScheme.primary)
                    }
                }

                Spacer(Modifier.height(32.dp))

                TextField(
                    value = email,
                    onValueChange = {
                        email = it
                        emailError = !ValidationUtils.isValidEmail(it)
                    },
                    isError = emailError,
                    label = { Text("Email") },
                    supportingText = { if (emailError) Text("Please enter a valid email") },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(16.dp))

                TextField(
                    value = password,
                    onValueChange = {
                        password = it
                        passwordError = it.length < 8
                    },
                    isError = passwordError,
                    label = { Text("Password") },
                    visualTransformation = if (passwordVisible) VisualTransformation.None 
                        else PasswordVisualTransformation(),
                    supportingText = { if (passwordError) Text("Password must be at least 8 characters") },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            AsyncImage(
                                model = ImageRequest.Builder(context)
                                    .data("file:///android_asset/svg/${
                                        if (passwordVisible) "visibility_off.svg" else "visibility.svg"
                                    }")
                                    .decoderFactory(SvgDecoder.Factory())
                                    .build(),
                                contentDescription = null
                            )
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Forgot Password?",
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .align(Alignment.End)
                        .clickable {
                            if (ValidationUtils.isValidEmail(email)) {
                                resetRequested = true
                                coroutineScope.launch {
                                    viewModel.sendPasswordResetEmail(email)
                                    snackbarHostState.showSnackbar("Password reset email sent")
                                    resetRequested = false
                                }
                            } else {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar("Enter a valid email to reset password")
                                }
                            }
                        }
                )

                Spacer(Modifier.height(24.dp))

                if (loginState is AuthViewModel.LoginState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), strokeWidth = 2.dp)
                }

                PrimaryButton(
                    text = if (loginState is AuthViewModel.LoginState.Loading) "Logging in..." else "Login",
                    modifier = Modifier.scale(
                        if (loginState is AuthViewModel.LoginState.Loading) 0.98f else 1f
                    ),
                    enabled = loginState !is AuthViewModel.LoginState.Loading,
                    onClick = {
                        emailError = !ValidationUtils.isValidEmail(email)
                        passwordError = password.length < 8

                        if (!emailError && !passwordError) {
                            viewModel.loginWithEmail(email, password)
                        } else {
                            triggerShake = true
                        }
                    }
                )

                Spacer(Modifier.height(12.dp))

                PrimaryButton(
                    text = "Continue with Google",
                    onClick = {
                        val intent = viewModel.getGoogleSignInClient().signInIntent
                        googleLauncher.launch(intent)
                    }
                )

                Spacer(Modifier.height(12.dp))

                PrimaryButton(
                    text = "Continue with Facebook",
                    onClick = {
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Facebook login not implemented in preview")
                        }
                    }
                )

                Spacer(Modifier.height(12.dp))

                TextButton(onClick = { navController.navigate(Screen.SignUp.route) }) {
                    Text("Don't have an account? Sign up")
                }
            }
        }
    }
}
