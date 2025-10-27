package com.example.escolafutebolapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.escolafutebolapp.uitls.AppLifecycleObserver
import com.example.escolafutebolapp.navigation.AppNavigation
import com.example.escolafutebolapp.ui.theme.EscolaFutebolAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // ✅ REGISTRA O OBSERVER DO CICLO DE VIDA
        lifecycle.addObserver(AppLifecycleObserver(application))

        setContent {
            EscolaFutebolAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}