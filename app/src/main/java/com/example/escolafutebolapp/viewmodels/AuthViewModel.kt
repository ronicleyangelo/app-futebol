// app/src/main/java/com/example/escolafutebolapp/viewmodels/AuthViewModel.kt
package com.example.escolafutebolapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.escolafutebolapp.models.AuthState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState.asStateFlow()

    fun loginUser(email: String, password: String) {
        viewModelScope.launch {
            _authState.value = AuthState.Loading

            // Simulação de login - substitua pela sua lógica real
            kotlinx.coroutines.delay(1000) // Simula chamada de API

            if (email.isNotEmpty() && password.isNotEmpty()) {
                // Login bem-sucedido
                _authState.value = AuthState.Success()
            } else {
                _authState.value = AuthState.Error("Email ou senha inválidos")
            }
        }
    }
}