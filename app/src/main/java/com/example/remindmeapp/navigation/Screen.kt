package com.example.remindmeapp.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object AddEdit : Screen("add_edit/{reminderId}") {
        fun createRoute(reminderId: String) = "add_edit/$reminderId"
    }
}
