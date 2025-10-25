// app/src/main/java/com/example/escolafutebolapp/ui/screens/LoginScreen.kt
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
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.escolafutebolapp.R
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
@OptIn(ExperimentalMaterial3Api::class, DelicateCoroutinesApi::class)
@Composable
fun LoginScreen(navController: NavController) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var passwordVisible by remember { mutableStateOf(false) }
    var showForgotPasswordOption by remember { mutableStateOf(false) }

    // Cores modernas com gradiente
    val darkBackground = Color(0xFF0D0D0D)
    val darkSurface = Color(0xFF1A1A1A)
    val accentRed = Color(0xFFE65C5C)
    val accentRedLight = Color(0xFFFF7B7B)
    val white = Color(0xFFFFFFFF)
    val grayText = Color(0xFFB3B3B3)
    val grayDark = Color(0xFF404040)

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = darkBackground
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
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
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // HEADER COM LOGO
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Logo container com efeito
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .shadow(
                                elevation = 8.dp,
                                shape = RoundedCornerShape(16.dp),
                                clip = false
                            )
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(darkSurface, grayDark)
                                ),
                                shape = RoundedCornerShape(16.dp)
                            )
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.logo_aa),
                            contentDescription = "Logo Escola de Futebol",
                            modifier = Modifier.size(35.dp),
                            contentScale = ContentScale.Fit
                        )
                    }

                    // Textos de boas-vindas
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Bem-vindo de Volta!",
                            color = white,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = "Entre na sua conta para continuar",
                            color = grayText,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // FORMULÁRIO DE LOGIN
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 6.dp,
                            shape = RoundedCornerShape(16.dp)
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = darkSurface
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {

                        // Campo Email
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
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
                                    if (errorMessage != null) errorMessage = null
                                },
                                placeholder = {
                                    Text(
                                        "seu.email@exemplo.com",
                                        color = grayText.copy(alpha = 0.6f),
                                        fontSize = 14.sp
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Email,
                                        contentDescription = "Email",
                                        tint = grayText,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
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
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp)
                            )
                        }

                        // Campo Senha
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Senha",
                                color = white,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                            OutlinedTextField(
                                value = password,
                                onValueChange = {
                                    password = it
                                    if (errorMessage != null) errorMessage = null
                                },
                                placeholder = {
                                    Text(
                                        "Sua senha",
                                        color = grayText.copy(alpha = 0.6f),
                                        fontSize = 14.sp
                                    )
                                },
                                leadingIcon = {
                                    Icon(
                                        Icons.Default.Lock,
                                        contentDescription = "Senha",
                                        tint = grayText,
                                        modifier = Modifier.size(18.dp)
                                    )
                                },
                                trailingIcon = {
                                    IconButton(
                                        onClick = { passwordVisible = !passwordVisible },
                                        modifier = Modifier.size(20.dp)
                                    ) {
                                        Icon(
                                            imageVector = if (passwordVisible) Icons.Default.Visibility
                                            else Icons.Default.VisibilityOff,
                                            contentDescription = if (passwordVisible) "Ocultar senha" else "Mostrar senha",
                                            tint = grayText,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(48.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedTextColor = white,
                                    unfocusedTextColor = white,
                                    focusedBorderColor = accentRed,
                                    unfocusedBorderColor = grayDark,
                                    cursorColor = accentRed,
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedLeadingIconColor = accentRed,
                                    unfocusedLeadingIconColor = grayText,
                                    focusedTrailingIconColor = accentRed,
                                    unfocusedTrailingIconColor = grayText
                                ),
                                shape = RoundedCornerShape(12.dp),
                                singleLine = true,
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 15.sp)
                            )
                        }

                        // MENSAGEM DE ERRO DINÂMICA
                        AnimatedVisibility(
                            visible = errorMessage != null,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        color = accentRed.copy(alpha = 0.1f),
                                        shape = RoundedCornerShape(10.dp)
                                    )
                                    .padding(12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = errorMessage ?: "",
                                    color = accentRedLight,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium
                                )

                                if (showForgotPasswordOption && errorMessage?.contains("incorretos") == true) {
                                    TextButton(
                                        onClick = {
                                            navController.navigate("forgot_password")
                                        },
                                        modifier = Modifier.padding(0.dp)
                                    ) {
                                        Text(
                                            text = "Esqueceu sua senha? Clique aqui para redefinir",
                                            color = accentRedLight,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.Medium
                                        )
                                    }
                                }
                            }
                        }

                        // Botão Entrar
                        Button(
                            onClick = {
                                if (email.isNotEmpty() && password.isNotEmpty()) {
                                    isLoading = true
                                    errorMessage = null
                                    showForgotPasswordOption = false

                                    GlobalScope.launch {
                                        kotlinx.coroutines.delay(1500)

                                        isLoading = false
                                        if (email == "teste@email.com" && password == "123456") {
                                            navController.navigate("welcome") {
                                                popUpTo("login") { inclusive = true }
                                            }
                                        } else {
                                            errorMessage = "Email ou senha incorretos. Tente novamente."
                                            showForgotPasswordOption = true
                                        }
                                    }
                                } else {
                                    errorMessage = "Por favor, preencha todos os campos."
                                    showForgotPasswordOption = false
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .shadow(
                                    elevation = 4.dp,
                                    shape = RoundedCornerShape(12.dp)
                                ),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = accentRed,
                                contentColor = white
                            ),
                            shape = RoundedCornerShape(12.dp),
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
                                    text = "Entrar na Conta",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        // Link Esqueci Senha
                        TextButton(
                            onClick = { navController.navigate("forgot_password") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                        ) {
                            Text(
                                text = "Esqueceu sua senha?",
                                color = accentRedLight,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // SEÇÃO DE CADASTRO
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(
                            elevation = 4.dp,
                            shape = RoundedCornerShape(12.dp)
                        ),
                    colors = CardDefaults.cardColors(
                        containerColor = darkSurface.copy(alpha = 0.8f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(
                        1.dp,
                        accentRed.copy(alpha = 0.3f)
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Ainda não tem uma conta?",
                            color = white,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center
                        )

                        Button(
                            onClick = { navController.navigate("register") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(44.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color.Transparent,
                                contentColor = accentRedLight
                            ),
                            shape = RoundedCornerShape(10.dp),
                            border = BorderStroke(1.5.dp, accentRed)
                        ) {
                            Text(
                                text = "Criar Conta Agora",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Text(
                            text = "Dúvidas? (27) 99999-9999",
                            color = grayText,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}