// app/src/main/java/com/example/escolafutebolapp/models/AuthState.kt
package com.example.escolafutebolapp.models

import com.google.firebase.auth.FirebaseUser

sealed class AuthState {
    object Idle : AuthState()
    object Loading : AuthState()
    data class Success(val user: FirebaseUser) : AuthState()
    data class SuccessWithName(val user: FirebaseUser, val userName: String) : AuthState() // Novo
    data class Error(val message: String) : AuthState()
}