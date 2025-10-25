// app/src/main/java/com/example/escolafutebolapp/ui/screens/ForgotPasswordScreen.kt
package com.example.escolafutebolapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.escolafutebolapp.R
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class, DelicateCoroutinesApi::class)
@Composable
fun ForgotPasswordScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    var isSuccess by remember { mutableStateOf(false) }

    // Cores consistentes com o LoginScreen
    val darkBackground = Color(0xFF0D0D0D)
    val darkSurface = Color(0xFF1A1A1A)
    val accentRed = Color(0xFFE65C5C)
    val accentRedLight = Color(0xFFFF7B7B)
    val white = Color(0xFFFFFFFF)
    val grayText = Color(0xFFB3B3B3)
    val grayDark = Color(0xFF404040)
    val successGreen = Color(0xFF4CAF50)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = darkBackground
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            "Recuperar Senha",
                            color = white,
                            fontWeight = FontWeight.Medium
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Voltar",
                                tint = white
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent,
                        scrolledContainerColor = Color.Transparent
                    )
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                Color(0xFF1A1A1A),
                                darkBackground
                            )
                        )
                    )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(28.dp)
                ) {

                    // HEADER COM ÍCONE
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(120.dp)
                                .shadow(
                                    elevation = 16.dp,
                                    shape = RoundedCornerShape(24.dp),
                                    clip = false
                                )
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(darkSurface, grayDark)
                                    ),
                                    shape = RoundedCornerShape(24.dp)
                                )
                                .padding(16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.logo_aa),
                                contentDescription = "Logo Escola de Futebol",
                                modifier = Modifier.size(60.dp),
                                contentScale = ContentScale.Fit
                            )
                        }

                        // Textos explicativos
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                text = "Recuperação de Senha",
                                color = white,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Digite seu email para receber um link de recuperação",
                                color = grayText,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center,
                                lineHeight = 18.sp
                            )
                        }
                    }

                    // CARD DO FORMULÁRIO
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(24.dp)
                            ),
                        colors = CardDefaults.cardColors(
                            containerColor = darkSurface
                        ),
                        shape = RoundedCornerShape(24.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {

                            if (isSuccess) {
                                // MENSAGEM DE SUCESSO
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            color = successGreen.copy(alpha = 0.1f),
                                            shape = RoundedCornerShape(16.dp)
                                        )
                                        .padding(16.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Text(
                                            text = "✓ Email Enviado!",
                                            color = successGreen,
                                            fontSize = 18.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                        Text(
                                            text = "Enviamos um link de recuperação para:",
                                            color = grayText,
                                            fontSize = 14.sp,
                                            textAlign = TextAlign.Center
                                        )
                                        Text(
                                            text = email,
                                            color = white,
                                            fontSize = 14.sp,
                                            fontWeight = FontWeight.Medium,
                                            textAlign = TextAlign.Center
                                        )
                                        Text(
                                            text = "Verifique sua caixa de entrada e siga as instruções.",
                                            color = grayText,
                                            fontSize = 12.sp,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }

                                // BOTÃO VOLTAR APÓS SUCESSO
                                Button(
                                    onClick = { navController.popBackStack() },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = accentRed,
                                        contentColor = white
                                    ),
                                    shape = RoundedCornerShape(16.dp)
                                ) {
                                    Text(
                                        text = "Voltar para o Login",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            } else {
                                // FORMULÁRIO DE RECUPERAÇÃO
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(
                                        text = "Email",
                                        color = white,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    OutlinedTextField(
                                        value = email,
                                        onValueChange = {
                                            email = it
                                            message = null // Limpa mensagem ao digitar
                                        },
                                        placeholder = {
                                            Text(
                                                "seu.email@exemplo.com",
                                                color = grayText.copy(alpha = 0.6f)
                                            )
                                        },
                                        leadingIcon = {
                                            Icon(
                                                Icons.Default.Email,
                                                contentDescription = "Email",
                                                tint = grayText
                                            )
                                        },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(56.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedTextColor = white,
                                            unfocusedTextColor = white,
                                            focusedBorderColor = accentRed,
                                            unfocusedBorderColor = grayDark,
                                            cursorColor = accentRed,
                                            focusedContainerColor = Color.Transparent,
                                            unfocusedContainerColor = Color.Transparent,
                                            focusedLeadingIconColor = accentRed,
                                            unfocusedLeadingIconColor = grayText
                                        ),
                                        shape = RoundedCornerShape(16.dp),
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                                    )
                                }

                                // MENSAGEM DE STATUS
                                if (message != null) {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(
                                                color = accentRed.copy(alpha = 0.1f),
                                                shape = RoundedCornerShape(12.dp)
                                            )
                                            .padding(12.dp),
                                        contentAlignment = Alignment.CenterStart
                                    ) {
                                        Text(
                                            text = message!!,
                                            color = accentRedLight,
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }

                                // BOTÃO ENVIAR LINK
                                Button(
                                    onClick = {
                                        if (email.isNotEmpty() && email.contains("@")) {
                                            isLoading = true
                                            message = null

                                            // Simula envio de email
                                            GlobalScope.launch {
                                                kotlinx.coroutines.delay(2000) // Simula chamada API

                                                isLoading = false
                                                isSuccess = true // Mostra tela de sucesso
                                            }
                                        } else {
                                            message = "Por favor, digite um email válido"
                                        }
                                    },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(56.dp)
                                        .shadow(
                                            elevation = 4.dp,
                                            shape = RoundedCornerShape(16.dp)
                                        ),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = accentRed,
                                        contentColor = white
                                    ),
                                    shape = RoundedCornerShape(16.dp),
                                    enabled = !isLoading
                                ) {
                                    if (isLoading) {
                                        CircularProgressIndicator(
                                            color = white,
                                            strokeWidth = 2.dp,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    } else {
                                        Text(
                                            text = "Enviar Link de Recuperação",
                                            fontSize = 16.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                // LINK VOLTAR
                                TextButton(
                                    onClick = { navController.popBackStack() },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(40.dp)
                                ) {
                                    Text(
                                        text = "Voltar para o Login",
                                        color = accentRedLight,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                    }

                    // INFORMAÇÕES DE CONTATO
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(
                                elevation = 4.dp,
                                shape = RoundedCornerShape(20.dp)
                            ),
                        colors = CardDefaults.cardColors(
                            containerColor = darkSurface.copy(alpha = 0.8f)
                        ),
                        shape = RoundedCornerShape(20.dp),
                        border = BorderStroke(
                            1.dp,
                            accentRed.copy(alpha = 0.3f)
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "Problemas para recuperar?",
                                color = white,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Entre em contato: (27) 99999-9999",
                                color = grayText,
                                fontSize = 12.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            }
        }
    }
}