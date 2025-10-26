package com.example.escolafutebolapp.initializer

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.example.escolafutebolapp.service.RealtimeDBService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AppLifecycleObserver(private val application: Application) : DefaultLifecycleObserver {

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        createDefaultAdmin()
    }

    private fun createDefaultAdmin() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                println("🎯 AppLifecycleObserver - Criando admin padrão...")
                RealtimeDBService.createDefaultAdminIfNotExists()
                println("✅ AppLifecycleObserver - Admin verificado/criado com sucesso")
            } catch (e: Exception) {
                println("❌ AppLifecycleObserver - Erro ao criar admin: ${e.message}")
            }
        }
    }
}