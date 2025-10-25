package com.example.escolafutebolapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
@OptIn(ExperimentalMaterial3Api::class, DelicateCoroutinesApi::class)
@Composable
fun RegisterScreen(navController: NavController) {
    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // Cores
    val darkBackground = Color(0xFF0D0D0D)
    val darkSurface = Color(0xFF1A1A1A)
    val accentRed = Color(0xFFE65C5C)
    val accentRedLight = Color(0xFFFF7B7B)
    val white = Color(0xFFFFFFFF)
    val grayText = Color(0xFFB3B3B3)
    val grayDark = Color(0xFF404040)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Criar Conta", color = white, fontWeight = FontWeight.SemiBold)
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Voltar", tint = white)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = white
                )
            )
        },
        containerColor = Color.Transparent
    ) { innerPadding ->
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = darkBackground
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(Color(0xFF1A1A1A), darkBackground)
                        )
                    )
                    .padding(innerPadding)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {

                    // HEADER
//                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
////                        Box(
////                            modifier = Modifier
////                                .size(70.dp)
////                                .clip(RoundedCornerShape(16.dp))
////                                .background(darkSurface),
////                            contentAlignment = Alignment.Center
////                        ) {
////                            Image(
////                                painter = painterResource(id = R.drawable.logo_aa),
////                                contentDescription = "Logo",
////                                modifier = Modifier.size(35.dp)
////                            )
////                        }
//
//                        Text(
//                            text = "Crie sua Conta",
//                            color = white,
//                            fontSize = 18.sp,
//                            fontWeight = FontWeight.Bold,
//                            textAlign = TextAlign.Center,
//                            modifier = Modifier.padding(top = 8.dp)
//                        )
//                    }

                    // FORMULÁRIO
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .shadow(elevation = 6.dp, shape = RoundedCornerShape(16.dp)),
                        colors = CardDefaults.cardColors(containerColor = darkSurface),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {

                            // Função de campo reutilizável
                            @Composable
                            fun campoPadrao(
                                label: String,
                                valor: String,
                                onValueChange: (String) -> Unit,
                                icon: ImageVector,
                                placeholder: String,
                                keyboardType: KeyboardType = KeyboardType.Text
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                    Text(
                                        text = label,
                                        color = white,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                    OutlinedTextField(
                                        value = valor,
                                        onValueChange = onValueChange,
                                        placeholder = {
                                            Text(
                                                placeholder,
                                                color = grayText.copy(alpha = 0.6f),
                                                fontSize = 14.sp
                                            )
                                        },
                                        leadingIcon = {
                                            Icon(
                                                icon,
                                                contentDescription = label,
                                                tint = grayText,
                                                modifier = Modifier.size(18.dp) // Ícones menores
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
                                            unfocusedContainerColor = Color.Transparent
                                        ),
                                        shape = RoundedCornerShape(12.dp),
                                        singleLine = true,
                                        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
                                        textStyle = TextStyle(fontSize = 15.sp) // Texto maior para ver melhor
                                    )
                                }
                            }

                            // Campos principais
                            campoPadrao("Nome Completo", nome, { nome = it }, Icons.Default.Person, "Seu nome completo")
                            campoPadrao("Email", email, { email = it }, Icons.Default.Email, "seu.email@exemplo.com", KeyboardType.Email)

                            // Campo Senha
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("Senha", color = white, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                OutlinedTextField(
                                    value = password,
                                    onValueChange = { password = it },
                                    placeholder = {
                                        Text("Mínimo 6 caracteres", color = grayText.copy(alpha = 0.6f), fontSize = 14.sp)
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Lock,
                                            null,
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
                                                if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                                null,
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
                                        unfocusedContainerColor = Color.Transparent
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true,
                                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                    textStyle = TextStyle(fontSize = 15.sp)
                                )
                            }

                            // Campo Confirmar Senha
                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                Text("Confirmar Senha", color = white, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                                OutlinedTextField(
                                    value = confirmPassword,
                                    onValueChange = { confirmPassword = it },
                                    placeholder = {
                                        Text("Digite novamente sua senha", color = grayText.copy(alpha = 0.6f), fontSize = 14.sp)
                                    },
                                    leadingIcon = {
                                        Icon(
                                            Icons.Default.Lock,
                                            null,
                                            tint = grayText,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    },
                                    trailingIcon = {
                                        IconButton(
                                            onClick = { confirmPasswordVisible = !confirmPasswordVisible },
                                            modifier = Modifier.size(20.dp)
                                        ) {
                                            Icon(
                                                if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                                null,
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
                                        unfocusedContainerColor = Color.Transparent
                                    ),
                                    shape = RoundedCornerShape(12.dp),
                                    singleLine = true,
                                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                    textStyle = TextStyle(fontSize = 15.sp)
                                )
                            }

                            // MENSAGEM DE ERRO
                            if (errorMessage != null) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(
                                            color = accentRed.copy(alpha = 0.1f),
                                            shape = RoundedCornerShape(10.dp)
                                        )
                                        .padding(12.dp),
                                    contentAlignment = Alignment.CenterStart
                                ) {
                                    Text(
                                        text = errorMessage!!,
                                        color = accentRedLight,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }

                            // BOTÃO CADASTRAR
                            Button(
                                onClick = {
                                    if (nome.isNotEmpty() && email.isNotEmpty() &&
                                        password.isNotEmpty() && confirmPassword.isNotEmpty()) {

                                        if (password != confirmPassword) {
                                            errorMessage = "As senhas não coincidem"
                                            return@Button
                                        }

                                        if (password.length < 6) {
                                            errorMessage = "A senha deve ter pelo menos 6 caracteres"
                                            return@Button
                                        }

                                        isLoading = true
                                        errorMessage = null

                                        GlobalScope.launch {
                                            delay(2000)
                                            isLoading = false
                                            navController.navigate("welcome") {
                                                popUpTo("register") { inclusive = true }
                                            }
                                        }
                                    } else {
                                        errorMessage = "Preencha todos os campos"
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .shadow(elevation = 4.dp, shape = RoundedCornerShape(12.dp)),
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
                                        text = "Criar Conta",
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // VOLTAR PARA LOGIN
                            TextButton(
                                onClick = { navController.popBackStack() },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Text(
                                    text = "Já tem uma conta? Fazer login",
                                    color = accentRedLight,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}