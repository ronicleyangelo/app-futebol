// app/src/main/java/com/example/escolafutebolapp/navigation/AppNavigation.kt
package com.example.escolafutebolapp.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.escolafutebolapp.ui.screens.WelcomeScreen
import com.example.escolafutebolapp.ui.screens.AgendaScreen
import com.example.escolafutebolapp.ui.screens.ForgotPasswordScreen
import com.example.escolafutebolapp.ui.screens.LoginScreen
import com.example.escolafutebolapp.ui.screens.MenuScreen
import com.example.escolafutebolapp.ui.screens.RegisterScreen
import com.example.escolafutebolapp.ui.screens.TreinosScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "login"
    ) {
        composable("welcome/{userName}") { backStackEntry ->
            val userName = backStackEntry.arguments?.getString("userName") ?: "Jogador"
            WelcomeScreen(navController, userName)
        }
        composable("login") { LoginScreen(navController) }
        composable("forgot_password") { ForgotPasswordScreen(navController) }
        composable("register") { RegisterScreen(navController) }
        composable("menu") { MenuScreen(navController) }
        composable("agenda") { AgendaScreen(navController) }
        composable("treino") { TreinosScreen(navController) }
    }
}