package com.example.escolafutebolapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun MenuScreen(navController: NavController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Menu Principal",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(50.dp))

        Button(
            onClick = { navController.navigate("agenda") },
            modifier = Modifier.height(50.dp)
        ) {
            Text(text = "Minha Agenda")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = { navController.navigate("treino") },
            modifier = Modifier.height(50.dp)
        ) {
            Text(text = "Meus Treinos")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {
                // TODO: Implementar logout
                navController.navigate("welcome") {
                    popUpTo("welcome") { inclusive = true }
                }
            },
            modifier = Modifier.height(50.dp)
        ) {
            Text(text = "Sair")
        }
    }
}