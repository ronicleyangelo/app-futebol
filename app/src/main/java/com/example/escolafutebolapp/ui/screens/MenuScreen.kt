package com.example.escolafutebolapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.escolafutebolapp.models.User
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuScreen(
    navController: NavController,
    currentUser: User?
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Menu Principal",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A1A1A)
                ),
                actions = {
                    // ✅ Badge do tipo de usuário se existir
                    currentUser?.let { user ->
                        Badge(
                            containerColor = when (user.tipo_usuario) {
                                "admin" -> Color(0xFFDC2626)
                                "tecnico" -> Color(0xFF2563EB)
                                else -> Color(0xFF059669)
                            }
                        ) {
                            Text(
                                text = user.tipo_usuario.uppercase(),
                                color = Color.White,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // ✅ Informações do usuário (só mostra se existir)
            currentUser?.let { user ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF262626)
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "👤 ${user.nome}",
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "📧 ${user.email}",
                            color = Color(0xFFB3B3B3),
                            fontSize = 14.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "🎯 Tipo: ${user.tipo_usuario.uppercase()}",
                            color = when (user.tipo_usuario) {
                                "admin" -> Color(0xFFEF4444)
                                "tecnico" -> Color(0xFF3B82F6)
                                else -> Color(0xFF10B981)
                            },
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Opções do menu
            MenuOption(
                icon = Icons.Default.Event,
                title = "Agenda",
                description = "Ver e gerenciar eventos",
                onClick = { navController.navigate("agenda") }
            )

            MenuOption(
                icon = Icons.Default.SportsSoccer,
                title = "Treinos",
                description = "Acompanhar treinos",
                onClick = { navController.navigate("treino") }
            )

            // ✅ Mostra opções administrativas apenas para admin/tecnico
            if (currentUser?.tipo_usuario == "admin" || currentUser?.tipo_usuario == "tecnico") {
                MenuOption(
                    icon = Icons.Default.Settings,
                    title = "Administração",
                    description = "Gerenciar sistema",
                    onClick = { /* navController.navigate("admin") */ }
                )
            }

            MenuOption(
                icon = Icons.Default.ExitToApp,
                title = "Sair",
                description = "Fazer logout",
                onClick = {
                    // Implementar logout
                    FirebaseAuth.getInstance().signOut()
                    navController.navigate("login") {
                        popUpTo("menu") { inclusive = true }
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MenuOption(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    description: String,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF262626)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = Color(0xFFE84545),
                modifier = Modifier.size(32.dp)
            )
            Column(
                modifier = Modifier.weight(weight = 1f)
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = description,
                    color = Color(0xFFB3B3B3),
                    fontSize = 14.sp
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Ir",
                tint = Color(0xFFB3B3B3)
            )
        }
    }
}