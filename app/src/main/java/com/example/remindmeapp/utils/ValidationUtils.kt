package com.example.remindmeapp.utils

object ValidationUtils {

    fun isValidEmail(email: String): Boolean {
        val emailRegex = Regex("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}\$")
        return emailRegex.matches(email)
    }

    fun isValidName(name: String): Boolean {
        return name.trim().isNotEmpty()
    }

    fun isValidPassword(password: String): Boolean {
        return password.length >= 8
    }

    fun doPasswordsMatch(password: String, confirmPassword: String): Boolean {
        return password == confirmPassword
    }
}
