// app/src/main/java/com/example/escolafutebolapp/models/AuthState.kt
package com.example.escolafutebolapp.models

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: Any? = null) : AuthState()
    data class Error(val message: String) : AuthState()
}