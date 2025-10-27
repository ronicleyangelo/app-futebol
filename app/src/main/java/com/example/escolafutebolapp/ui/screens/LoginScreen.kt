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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.escolafutebolapp.R
import com.example.escolafutebolapp.models.AuthState
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.draw.clip
import com.example.escolafutebolapp.viewmodels.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(navController: NavController) {
    // ✅ DETECTA O TAMANHO DA TELA
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    // ✅ CALCULA VALORES RESPONSIVOS - TAMANHOS AUMENTADOS
    val isSmallScreen = screenWidth < 360.dp
    val isLargeScreen = screenWidth > 480.dp
    val isTablet = screenWidth > 600.dp
    val isLargeTablet = screenWidth > 800.dp

    // ✅ TAMANHOS RESPONSIVOS AUMENTADOS
    val horizontalPadding = when {
        isLargeTablet -> 120.dp
        isTablet -> 60.dp
        isLargeScreen -> 32.dp
        isSmallScreen -> 16.dp
        else -> 24.dp
    }

    val verticalPadding = when {
        isLargeTablet -> 60.dp
        isTablet -> 40.dp
        isSmallScreen -> 20.dp
        else -> 28.dp
    }

    val cardPadding = when {
        isLargeTablet -> 40.dp
        isTablet -> 28.dp
        isSmallScreen -> 20.dp
        else -> 24.dp
    }

    val logoSize = when {
        isLargeTablet -> 140.dp
        isTablet -> 110.dp
        isLargeScreen -> 90.dp
        isSmallScreen -> 70.dp
        else -> 80.dp
    }

    val iconSize = when {
        isLargeTablet -> 24.dp
        isTablet -> 22.dp
        isSmallScreen -> 18.dp
        else -> 20.dp
    }

    val fontSizeTitle = when {
        isLargeTablet -> 28.sp
        isTablet -> 24.sp
        isSmallScreen -> 18.sp
        else -> 20.sp
    }

    val fontSizeBody = when {
        isLargeTablet -> 18.sp
        isTablet -> 16.sp
        isSmallScreen -> 14.sp
        else -> 15.sp
    }

    val fontSizeSmall = when {
        isLargeTablet -> 14.sp
        isTablet -> 12.sp
        isSmallScreen -> 11.sp
        else -> 12.sp
    }

    val buttonHeight = when {
        isLargeTablet -> 60.dp
        isTablet -> 54.dp
        isSmallScreen -> 46.dp
        else -> 52.dp
    }

    val textFieldHeight = when {
        isLargeTablet -> 60.dp
        isTablet -> 54.dp
        isSmallScreen -> 46.dp
        else -> 52.dp
    }

    val spacingBetweenSections = when {
        isLargeTablet -> 32.dp
        isTablet -> 28.dp
        isSmallScreen -> 20.dp
        else -> 24.dp
    }

    // ✅ VIEWMODEL E ESTADOS
    val authViewModel: AuthViewModel = viewModel()
    val authState by authViewModel.authState.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    // Cores modernas com gradiente
    val darkBackground = Color(0xFF0D0D0D)
    val darkSurface = Color(0xFF1A1A1A)
    val accentRed = Color(0xFFE65C5C)
    val accentRedLight = Color(0xFFFF7B7B)
    val white = Color(0xFFFFFFFF)
    val grayText = Color(0xFFB3B3B3)
    val grayDark = Color(0xFF404040)

    // ✅ EFEiTO PARA NAVEGAR QUANDO LOGIN FOR BEM-SUCEDIDO
    LaunchedEffect(authState) {
        when (authState) {
            is AuthState.Success -> {
                try {
                    val userName = authViewModel.getCurrentUserName()
                    println("✅ Navegando para Welcome com nome: $userName")
                    navController.navigate("welcome/$userName") {
                        popUpTo("login") { inclusive = true }
                    }
                } catch (e: Exception) {
                    println("⚠️ Erro ao obter nome do usuário: ${e.message}")
                    // Em caso de erro, navega com nome padrão
                    navController.navigate("welcome/Jogador") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            }
            else -> {}
        }
    }
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
            // ✅ LAYOUT RESPONSIVO - Column para mobile, Row para tablet
            if (isTablet) {
                // ✅ LAYOUT TABLET - Lado a lado
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontalPadding),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Lado esquerdo - Logo e informações
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(28.dp)
                        ) {
                            LogoSection(
                                logoSize = logoSize,
                                white = white,
                                grayDark = grayDark,
                                darkSurface = darkSurface,
                                fontSizeTitle = fontSizeTitle,
                                grayText = grayText
                            )

                            // Informações adicionais para tablet
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(
                                    text = "Escola de Futebol Premium",
                                    color = white,
                                    fontSize = fontSizeTitle,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center
                                )
                                Text(
                                    text = "Treinos profissionais • Tecnologia avançada • Resultados comprovados",
                                    color = grayText,
                                    fontSize = fontSizeBody,
                                    textAlign = TextAlign.Center,
                                    lineHeight = fontSizeBody * 1.4f
                                )
                            }
                        }
                    }

                    // Lado direito - Formulário
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        LoginForm(
                            email = email,
                            onEmailChange = { email = it },
                            password = password,
                            onPasswordChange = { password = it },
                            passwordVisible = passwordVisible,
                            onPasswordVisibleChange = { passwordVisible = it },
                            authState = authState,
                            onLoginClick = {
                                if (email.isNotEmpty() && password.isNotEmpty()) {
                                    authViewModel.loginUser(email, password)
                                }
                            },
                            onForgotPasswordClick = { navController.navigate("forgot_password") },
                            onRegisterClick = { navController.navigate("register") },
                            horizontalPadding = 0.dp,
                            cardPadding = cardPadding,
                            buttonHeight = buttonHeight,
                            textFieldHeight = textFieldHeight,
                            fontSizeTitle = fontSizeTitle,
                            fontSizeBody = fontSizeBody,
                            fontSizeSmall = fontSizeSmall,
                            iconSize = iconSize,
                            darkSurface = darkSurface,
                            white = white,
                            grayText = grayText,
                            grayDark = grayDark,
                            accentRed = accentRed,
                            accentRedLight = accentRedLight,
                            spacingBetweenSections = spacingBetweenSections
                        )
                    }
                }
            } else {
                // ✅ LAYOUT MOBILE - Coluna única
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = horizontalPadding, vertical = verticalPadding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(spacingBetweenSections)
                ) {
                    // ✅ HEADER RESPONSIVO
                    LogoSection(
                        logoSize = logoSize,
                        white = white,
                        grayDark = grayDark,
                        darkSurface = darkSurface,
                        fontSizeTitle = fontSizeTitle,
                        grayText = grayText
                    )

                    // ✅ FORMULÁRIO RESPONSIVO
                    LoginForm(
                        email = email,
                        onEmailChange = { email = it },
                        password = password,
                        onPasswordChange = { password = it },
                        passwordVisible = passwordVisible,
                        onPasswordVisibleChange = { passwordVisible = it },
                        authState = authState,
                        onLoginClick = {
                            if (email.isNotEmpty() && password.isNotEmpty()) {
                                authViewModel.loginUser(email, password)
                            }
                        },
                        onForgotPasswordClick = { navController.navigate("forgot_password") },
                        onRegisterClick = { navController.navigate("register") },
                        horizontalPadding = 0.dp,
                        cardPadding = cardPadding,
                        buttonHeight = buttonHeight,
                        textFieldHeight = textFieldHeight,
                        fontSizeTitle = fontSizeTitle,
                        fontSizeBody = fontSizeBody,
                        fontSizeSmall = fontSizeSmall,
                        iconSize = iconSize,
                        darkSurface = darkSurface,
                        white = white,
                        grayText = grayText,
                        grayDark = grayDark,
                        accentRed = accentRed,
                        accentRedLight = accentRedLight,
                        spacingBetweenSections = spacingBetweenSections
                    )
                }
            }
        }
    }
}

@Composable
private fun LogoSection(
    logoSize: Dp,
    white: Color,
    grayDark: Color,
    darkSurface: Color,
    fontSizeTitle: androidx.compose.ui.unit.TextUnit,
    grayText: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Box(
            modifier = Modifier
                .size(logoSize)
                .shadow(elevation = 12.dp, shape = RoundedCornerShape(20.dp))
                .background(
                    brush = Brush.verticalGradient(colors = listOf(darkSurface, grayDark)),
                    shape = RoundedCornerShape(20.dp)
                )
                .clip(RoundedCornerShape(16.dp)), // Clip interno
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_aa),
                contentDescription = "Logo Escola de Futebol",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop // Crop pode ser melhor para logos
            )
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
//            Text(
//                text = "Bem-vindo de Volta!",
//                color = white,
//                fontSize = fontSizeTitle,
//                fontWeight = FontWeight.Bold,
//                textAlign = TextAlign.Center
//            )
            Text(
                text = "Entre no seu perfil de atleta",
                color = grayText,
                fontSize = fontSizeTitle * 0.75f,
                textAlign = TextAlign.Center
            )
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun LoginForm(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    passwordVisible: Boolean,
    onPasswordVisibleChange: (Boolean) -> Unit,
    authState: AuthState,
    onLoginClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onRegisterClick: () -> Unit,
    horizontalPadding: Dp,
    cardPadding: Dp,
    buttonHeight: Dp,
    textFieldHeight: Dp,
    fontSizeTitle: androidx.compose.ui.unit.TextUnit,
    fontSizeBody: androidx.compose.ui.unit.TextUnit,
    fontSizeSmall: androidx.compose.ui.unit.TextUnit,
    iconSize: Dp,
    darkSurface: Color,
    white: Color,
    grayText: Color,
    grayDark: Color,
    accentRed: Color,
    accentRedLight: Color,
    spacingBetweenSections: Dp
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 8.dp, // Aumentado
                shape = RoundedCornerShape(20.dp) // Aumentado
            ),
        colors = CardDefaults.cardColors(
            containerColor = darkSurface
        ),
        shape = RoundedCornerShape(20.dp) // Aumentado
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(cardPadding),
            verticalArrangement = Arrangement.spacedBy(24.dp) // Aumentado
        ) {
            // Campo Email
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) { // Aumentado
                Text(
                    text = "Usuário ou E-mail",
                    color = white,
                    fontSize = fontSizeBody,
                    fontWeight = FontWeight.Medium
                )
                OutlinedTextField(
                    value = email,
                    onValueChange = onEmailChange,
                    placeholder = {
                        Text(
                            "digite seu usuário ou e-mail",
                            color = grayText.copy(alpha = 0.6f),
                            fontSize = fontSizeBody
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Email,
                            contentDescription = "Email",
                            tint = grayText,
                            modifier = Modifier.size(iconSize)
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(textFieldHeight),
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
                    shape = RoundedCornerShape(14.dp), // Aumentado
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = fontSizeBody)
                )
            }

            // Campo Senha
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) { // Aumentado
                Text(
                    text = "Senha",
                    color = white,
                    fontSize = fontSizeBody,
                    fontWeight = FontWeight.Medium
                )
                OutlinedTextField(
                    value = password,
                    onValueChange = onPasswordChange,
                    placeholder = {
                        Text(
                            "Digite sua senha",
                            color = grayText.copy(alpha = 0.6f),
                            fontSize = fontSizeBody
                        )
                    },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = "Senha",
                            tint = grayText,
                            modifier = Modifier.size(iconSize)
                        )
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = { onPasswordVisibleChange(!passwordVisible) },
                            modifier = Modifier.size(iconSize + 6.dp) // Aumentado
                        ) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility
                                else Icons.Default.VisibilityOff,
                                contentDescription = if (passwordVisible) "Ocultar senha" else "Mostrar senha",
                                tint = grayText,
                                modifier = Modifier.size(iconSize)
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(textFieldHeight),
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
                    shape = RoundedCornerShape(14.dp), // Aumentado
                    singleLine = true,
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    textStyle = androidx.compose.ui.text.TextStyle(fontSize = fontSizeBody)
                )
            }

            // Mensagem de Erro
            AnimatedVisibility(
                visible = authState is AuthState.Error,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = accentRed.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(12.dp) // Aumentado
                        )
                        .padding(16.dp), // Aumentado
                    verticalArrangement = Arrangement.spacedBy(8.dp) // Aumentado
                ) {
                    Text(
                        text = (authState as? AuthState.Error)?.message ?: "Erro desconhecido",
                        color = accentRedLight,
                        fontSize = fontSizeBody * 0.9f,
                        fontWeight = FontWeight.Medium
                    )

                    if ((authState as? AuthState.Error)?.message?.contains("password") == true ||
                        (authState as? AuthState.Error)?.message?.contains("credential") == true) {
                        TextButton(
                            onClick = onForgotPasswordClick,
                            modifier = Modifier.padding(0.dp)
                        ) {
                            Text(
                                text = "Esqueceu sua senha? Clique aqui para redefinir",
                                color = accentRedLight,
                                fontSize = fontSizeBody * 0.85f,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }
            }

            // Botão Entrar
            Button(
                onClick = onLoginClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(buttonHeight)
                    .shadow(
                        elevation = 6.dp, // Aumentado
                        shape = RoundedCornerShape(14.dp) // Aumentado
                    ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = accentRed,
                    contentColor = white
                ),
                shape = RoundedCornerShape(14.dp), // Aumentado
                enabled = authState !is AuthState.Loading
            ) {
                if (authState is AuthState.Loading) {
                    CircularProgressIndicator(
                        color = white,
                        strokeWidth = 2.5.dp, // Aumentado
                        modifier = Modifier.size(buttonHeight * 0.4f)
                    )
                } else {
                    Text(
                        text = "Acessar Conta",
                        fontSize = fontSizeBody,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // Link Esqueci Senha
            TextButton(
                onClick = onForgotPasswordClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(buttonHeight * 0.85f) // Ajustado
            ) {
                Text(
                    text = "Esqueceu sua senha?",
                    color = accentRedLight,
                    fontSize = fontSizeBody,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }

    Spacer(modifier = Modifier.height(8.dp)) // Espaço entre cards

    // Seção de Cadastro
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = 6.dp, // Aumentado
                shape = RoundedCornerShape(16.dp) // Aumentado
            ),
        colors = CardDefaults.cardColors(
            containerColor = darkSurface.copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(16.dp), // Aumentado
        border = BorderStroke(
            1.5.dp, // Aumentado
            accentRed.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(cardPadding * 0.9f), // Ajustado
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp) // Aumentado
        ) {
            Text(
                text = "Ainda não tem uma conta?",
                color = white,
                fontSize = fontSizeBody,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )

            Button(
                onClick = onRegisterClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(buttonHeight * 0.95f), // Ajustado
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color.Transparent,
                    contentColor = accentRedLight
                ),
                shape = RoundedCornerShape(12.dp), // Aumentado
                border = BorderStroke(2.dp, accentRed) // Aumentado
            ) {
                Text(
                    text = "Entrar para o Time",
                    fontSize = fontSizeBody,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "Dúvidas? (27) 99999-9999",
                color = grayText,
                fontSize = fontSizeSmall,
                textAlign = TextAlign.Center
            )
        }
    }
}