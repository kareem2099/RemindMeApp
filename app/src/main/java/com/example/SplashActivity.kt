package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.example.remindmeapp.MainActivity
import kotlinx.coroutines.delay

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val compositionResult = rememberLottieComposition(
                        spec = LottieCompositionSpec.Asset("lottie/splash_animation.json")
                    )
                    val composition = compositionResult.value
        
                    val animationState = animateLottieCompositionAsState(composition)
                    val progress = animationState.value
        
                    LottieAnimation(
                        composition = composition,
                        progress = { progress },
                        modifier = Modifier.fillMaxSize()
                    )
        
                    LaunchedEffect(Unit) {
                        delay(3000) // Splash duration
                        // Navigate to LoginScreen via MainActivity
                        startActivity(Intent(this@SplashActivity, MainActivity::class.java).apply {
                            putExtra("destination", "login")
                        })
                        finish()
                    }
                }
            }
        }
    }
}
