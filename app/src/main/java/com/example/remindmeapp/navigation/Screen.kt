package com.example.remindmeapp.navigation

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Home : Screen("home")
    object Login : Screen("login")
    object SignUp : Screen("signup")
    object Add : Screen("add") {
        fun createRoute() = "add"
    }
    
    object Edit : Screen("edit/{reminderId}") {
        fun createRoute(reminderId: String) = "edit/$reminderId"
    }
}
