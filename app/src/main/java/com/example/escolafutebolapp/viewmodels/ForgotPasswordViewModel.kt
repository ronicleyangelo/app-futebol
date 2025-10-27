package com.example.escolafutebolapp.viewmodel

import UserRepository
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.escolafutebolapp.service.EmailService
import kotlinx.coroutines.launch

class ForgotPasswordViewModel(
    application: Application,
    private val userRepository: UserRepository,
    private val emailService: EmailService
) : AndroidViewModel(application) {  // Mude para AndroidViewModel

    private val _uiState = MutableLiveData<ForgotPasswordUiState>(ForgotPasswordUiState.Idle)
    val uiState: LiveData<ForgotPasswordUiState> = _uiState

    private val _navigateToLogin = MutableLiveData<Boolean>()
    val navigateToLogin: LiveData<Boolean> = _navigateToLogin

    fun requestPasswordReset(email: String) {
        if (!isValidEmail(email)) {
            _uiState.value = ForgotPasswordUiState.Error("Por favor, insira um email válido")
            return
        }

        viewModelScope.launch {
            _uiState.value = ForgotPasswordUiState.Loading

            try {
                // Verifica se o usuário existe
                val userExists = userRepository.checkUserExists(email)
                if (!userExists) {
                    _uiState.value = ForgotPasswordUiState.Error("Nenhuma conta encontrada com este email")
                    return@launch
                }

                // Gera token de reset
                val resetToken = generateResetToken()

                // Salva token no Firestore
                userRepository.saveResetToken(email, resetToken)

                // Envia email
                emailService.sendPasswordResetEmail(email, resetToken)

                _uiState.value = ForgotPasswordUiState.Success("Email de recuperação enviado com sucesso!")

            } catch (e: Exception) {
                _uiState.value = ForgotPasswordUiState.Error("Erro ao processar solicitação: ${e.message}")
            }
        }
    }

    fun resetPassword(email: String, token: String, newPassword: String) {
        if (newPassword.length < 6) {
            _uiState.value = ForgotPasswordUiState.Error("A senha deve ter pelo menos 6 caracteres")
            return
        }

        viewModelScope.launch {
            _uiState.value = ForgotPasswordUiState.Loading

            try {
                // Verifica token válido
                val isValidToken = userRepository.validateResetToken(email, token)
                if (!isValidToken) {
                    _uiState.value = ForgotPasswordUiState.Error("Token inválido ou expirado")
                    return@launch
                }

                // Atualiza senha
                userRepository.updatePassword(email, newPassword)

                // Limpa token usado
                userRepository.clearResetToken(email)

                _uiState.value = ForgotPasswordUiState.PasswordResetSuccess("Senha redefinida com sucesso!")

            } catch (e: Exception) {
                _uiState.value = ForgotPasswordUiState.Error("Erro ao redefinir senha: ${e.message}")
            }
        }
    }

    fun navigateToLoginScreen() {
        _navigateToLogin.value = true
    }

    fun resetNavigation() {
        _navigateToLogin.value = false
    }

    fun resetUiState() {
        _uiState.value = ForgotPasswordUiState.Idle
    }

    private fun isValidEmail(email: String): Boolean {
        val emailPattern = "[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}"
        return email.matches(emailPattern.toRegex())
    }

    private fun generateResetToken(): String {
        return (1..6).map { (0..9).random() }.joinToString("")
    }
}

sealed class ForgotPasswordUiState {
    object Idle : ForgotPasswordUiState()
    object Loading : ForgotPasswordUiState()
    data class Success(val message: String) : ForgotPasswordUiState()
    data class PasswordResetSuccess(val message: String) : ForgotPasswordUiState()
    data class Error(val message: String) : ForgotPasswordUiState()
}