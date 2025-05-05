package com.example.remindmeapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.remindmeapp.screens.auth.LoginScreen
import com.example.remindmeapp.screens.auth.SignUpScreen
import com.example.remindmeapp.screens.home.HomeScreen
import com.example.remindmeapp.screens.add.AddReminderScreen
import com.example.remindmeapp.screens.edit.EditReminderScreen

@Composable
fun AppNavGraph(
    startDestination: String = Screen.Login.route
) {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(Screen.Login.route) {
            LoginScreen(navController)
        }
        composable(Screen.SignUp.route) {
            SignUpScreen(navController)
        }
        composable(Screen.Home.route) {
            HomeScreen(navController) // Fixed: Added navController parameter
        }
        composable(Screen.Add.route) {
            AddReminderScreen(navController)
        }
        composable(Screen.Edit.route) { backStackEntry ->
            val reminderId = backStackEntry.arguments?.getString("reminderId") ?: ""
            EditReminderScreen(navController, reminderId)
        }
    }
}
