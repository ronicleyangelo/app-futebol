package com.example.escolafutebolapp.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.escolafutebolapp.ui.screens.WelcomeScreen
import com.example.escolafutebolapp.ui.screens.AgendaScreen
import com.example.escolafutebolapp.ui.screens.ForgotPasswordScreen
import com.example.escolafutebolapp.ui.screens.LoginScreen
import com.example.escolafutebolapp.ui.screens.RegisterScreen
import com.example.escolafutebolapp.ui.screens.TreinosScreen


@RequiresApi(Build.VERSION_CODES.O)
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

        // ✅ CERTIFIQUE-SE DE QUE ESTA ROTA EXISTE COM DOIS PARÂMETROS
        composable("agenda/{userId}/{userTipo}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            val userTipo = backStackEntry.arguments?.getString("userTipo") ?: "aluno"
            AgendaScreen(navController, userId, userTipo)
        }

        composable("treino") { TreinosScreen(navController) }
    }
}