package com.example.escolafutebolapp.viewmodel

import UserRepository
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.escolafutebolapp.service.EmailService

class ForgotPasswordViewModelFactory(
    private val application: Application,
    private val userRepository: UserRepository,
    private val emailService: EmailService
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ForgotPasswordViewModel::class.java)) {
            return ForgotPasswordViewModel(application, userRepository, emailService) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}