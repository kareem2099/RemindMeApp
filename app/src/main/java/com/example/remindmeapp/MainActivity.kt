package com.example.remindmeapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import dagger.hilt.android.AndroidEntryPoint
import com.example.remindmeapp.navigation.AppNavGraph
import com.example.remindmeapp.navigation.Screen
import com.example.remindmeapp.navigation.AppNavGraph
import com.example.remindmeapp.ui.theme.RemindMeAppTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        val startDestination = when (intent?.getStringExtra("destination")) {
            "login" -> Screen.Login.route
            "home" -> Screen.Home.route
            else -> Screen.Login.route // Default to login
        }

        setContent {
            RemindMeAppTheme {
                AppNavGraph(startDestination = startDestination)
            }
        }
    }
}
