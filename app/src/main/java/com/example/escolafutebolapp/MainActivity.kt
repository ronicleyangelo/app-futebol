// DEVE estar assim:
package com.example.escolafutebolapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.escolafutebolapp.navigation.AppNavigation
import com.example.escolafutebolapp.ui.theme.EscolaFutebolAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EscolaFutebolAppTheme {
                AppNavigation() //
            }
        }
    }
}