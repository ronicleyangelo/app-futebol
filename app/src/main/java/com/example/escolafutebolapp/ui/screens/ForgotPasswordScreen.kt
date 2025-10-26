// app/src/main/java/com/example/escolafutebolapp/ui/screens/ForgotPasswordScreen.kt
package com.example.escolafutebolapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.escolafutebolapp.R
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class, DelicateCoroutinesApi::class)
@Composable
fun ForgotPasswordScreen(navController: NavController) {
    // ✅ DETECTA O TAMANHO DA TELA
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    // ✅ CATEGORIAS DE DISPOSITIVOS
    val isSmallPhone = screenWidth < 360.dp
    val isNormalPhone = screenWidth >= 360.dp && screenWidth < 480.dp
    val isLargePhone = screenWidth >= 480.dp && screenWidth < 600.dp
    val isSmallTablet = screenWidth >= 600.dp && screenWidth < 800.dp
    val isLargeTablet = screenWidth >= 800.dp && screenWidth < 1024.dp
    val isXLargeTablet = screenWidth >= 1024.dp

    // ✅ PADDINGS HORIZONTAIS - AUMENTADOS
    val horizontalPadding = when {
        isXLargeTablet -> 160.dp
        isLargeTablet -> 120.dp
        isSmallTablet -> 80.dp
        isLargePhone -> 40.dp
        isNormalPhone -> 28.dp
        else -> 20.dp
    }

    // ✅ PADDINGS VERTICAIS - AUMENTADOS
    val verticalPadding = when {
        isXLargeTablet -> 48.dp
        isLargeTablet -> 40.dp
        isSmallTablet -> 36.dp
        isLargePhone -> 32.dp
        isNormalPhone -> 28.dp
        else -> 24.dp
    }

    // ✅ PADDING INTERNO DOS CARDS - AUMENTADOS
    val cardPadding = when {
        isXLargeTablet -> 48.dp
        isLargeTablet -> 40.dp
        isSmallTablet -> 32.dp
        isLargePhone -> 28.dp
        isNormalPhone -> 24.dp
        else -> 20.dp
    }

    // ✅ TAMANHO DO LOGO - AUMENTADO
    val logoSize = when {
        isXLargeTablet -> 180.dp
        isLargeTablet -> 150.dp
        isSmallTablet -> 120.dp
        isLargePhone -> 100.dp
        isNormalPhone -> 85.dp
        else -> 75.dp
    }

    // ✅ TAMANHO DOS ÍCONES - AUMENTADO
    val iconSize = when {
        isXLargeTablet -> 28.dp
        isLargeTablet -> 26.dp
        isSmallTablet -> 24.dp
        isLargePhone -> 22.dp
        isNormalPhone -> 20.dp
        else -> 18.dp
    }

    // ✅ FONTE DO TÍTULO - AUMENTADA
    val fontSizeTitle = when {
        isXLargeTablet -> 36.sp
        isLargeTablet -> 32.sp
        isSmallTablet -> 28.sp
        isLargePhone -> 24.sp
        isNormalPhone -> 22.sp
        else -> 20.sp
    }

    // ✅ FONTE DO CORPO - AUMENTADA
    val fontSizeBody = when {
        isXLargeTablet -> 20.sp
        isLargeTablet -> 18.sp
        isSmallTablet -> 17.sp
        isLargePhone -> 16.sp
        isNormalPhone -> 15.sp
        else -> 14.sp
    }

    // ✅ FONTE PEQUENA - AUMENTADA
    val fontSizeSmall = when {
        isXLargeTablet -> 16.sp
        isLargeTablet -> 14.sp
        isSmallTablet -> 13.sp
        isLargePhone -> 13.sp
        isNormalPhone -> 12.sp
        else -> 11.sp
    }

    // ✅ ALTURA DOS BOTÕES - AUMENTADA
    val buttonHeight = when {
        isXLargeTablet -> 68.dp
        isLargeTablet -> 64.dp
        isSmallTablet -> 60.dp
        isLargePhone -> 56.dp
        isNormalPhone -> 52.dp
        else -> 48.dp
    }

    // ✅ ALTURA DOS CAMPOS DE TEXTO - AUMENTADA
    val textFieldHeight = when {
        isXLargeTablet -> 68.dp
        isLargeTablet -> 64.dp
        isSmallTablet -> 60.dp
        isLargePhone -> 56.dp
        isNormalPhone -> 52.dp
        else -> 48.dp
    }

    // ✅ ESPAÇAMENTO ENTRE SEÇÕES - AUMENTADO
    val spacingBetweenSections = when {
        isXLargeTablet -> 40.dp
        isLargeTablet -> 36.dp
        isSmallTablet -> 32.dp
        isLargePhone -> 28.dp
        isNormalPhone -> 24.dp
        else -> 20.dp
    }

    // ✅ RAIO DOS CANTOS - AUMENTADO
    val cornerRadius = when {
        isXLargeTablet -> 28.dp
        isLargeTablet -> 26.dp
        isSmallTablet -> 24.dp
        isLargePhone -> 20.dp
        else -> 18.dp
    }

    // ✅ ELEVAÇÃO DAS SOMBRAS - AUMENTADA
    val shadowElevation = when {
        isXLargeTablet -> 12.dp
        isLargeTablet -> 10.dp
        isSmallTablet -> 10.dp
        else -> 8.dp
    }

    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var message by remember { mutableStateOf<String?>(null) }
    var isSuccess by remember { mutableStateOf(false) }

    // Cores consistentes
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
                            fontWeight = FontWeight.Medium,
                            fontSize = fontSizeBody
                        )
                    },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                Icons.Default.ArrowBack,
                                contentDescription = "Voltar",
                                tint = white,
                                modifier = Modifier.size(iconSize)
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
                // ✅ SCROLL SUAVE PARA TELAS PEQUENAS
                val scrollState = rememberScrollState()

                // ✅ LAYOUT ADAPTATIVO
                if (screenWidth >= 600.dp) {
                    // ✅ TABLETS - CENTRALIZADO
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = horizontalPadding, vertical = verticalPadding),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(scrollState),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(spacingBetweenSections)
                        ) {
                            ForgotPasswordContent(
                                email = email,
                                onEmailChange = { email = it },
                                isLoading = isLoading,
                                message = message,
                                isSuccess = isSuccess,
                                onSendClick = {
                                    if (email.isNotEmpty() && email.contains("@")) {
                                        isLoading = true
                                        message = null
                                        GlobalScope.launch {
                                            kotlinx.coroutines.delay(2000)
                                            isLoading = false
                                            isSuccess = true
                                        }
                                    } else {
                                        message = "Por favor, digite um email válido"
                                    }
                                },
                                onBackClick = { navController.popBackStack() },
                                cardPadding = cardPadding,
                                buttonHeight = buttonHeight,
                                textFieldHeight = textFieldHeight,
                                fontSizeTitle = fontSizeTitle,
                                fontSizeBody = fontSizeBody,
                                fontSizeSmall = fontSizeSmall,
                                iconSize = iconSize,
                                logoSize = logoSize,
                                darkSurface = darkSurface,
                                white = white,
                                grayText = grayText,
                                grayDark = grayDark,
                                accentRed = accentRed,
                                accentRedLight = accentRedLight,
                                successGreen = successGreen,
                                spacingBetweenSections = spacingBetweenSections,
                                cornerRadius = cornerRadius,
                                shadowElevation = shadowElevation
                            )
                        }
                    }
                } else {
                    // ✅ SMARTPHONES - COLUNA COMPLETA
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .verticalScroll(scrollState)
                            .padding(horizontal = horizontalPadding, vertical = verticalPadding),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(spacingBetweenSections)
                    ) {
                        ForgotPasswordContent(
                            email = email,
                            onEmailChange = { email = it },
                            isLoading = isLoading,
                            message = message,
                            isSuccess = isSuccess,
                            onSendClick = {
                                if (email.isNotEmpty() && email.contains("@")) {
                                    isLoading = true
                                    message = null
                                    GlobalScope.launch {
                                        kotlinx.coroutines.delay(2000)
                                        isLoading = false
                                        isSuccess = true
                                    }
                                } else {
                                    message = "Por favor, digite um email válido"
                                }
                            },
                            onBackClick = { navController.popBackStack() },
                            cardPadding = cardPadding,
                            buttonHeight = buttonHeight,
                            textFieldHeight = textFieldHeight,
                            fontSizeTitle = fontSizeTitle,
                            fontSizeBody = fontSizeBody,
                            fontSizeSmall = fontSizeSmall,
                            iconSize = iconSize,
                            logoSize = logoSize,
                            darkSurface = darkSurface,
                            white = white,
                            grayText = grayText,
                            grayDark = grayDark,
                            accentRed = accentRed,
                            accentRedLight = accentRedLight,
                            successGreen = successGreen,
                            spacingBetweenSections = spacingBetweenSections,
                            cornerRadius = cornerRadius,
                            shadowElevation = shadowElevation
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ForgotPasswordContent(
    email: String,
    onEmailChange: (String) -> Unit,
    isLoading: Boolean,
    message: String?,
    isSuccess: Boolean,
    onSendClick: () -> Unit,
    onBackClick: () -> Unit,
    cardPadding: Dp,
    buttonHeight: Dp,
    textFieldHeight: Dp,
    fontSizeTitle: androidx.compose.ui.unit.TextUnit,
    fontSizeBody: androidx.compose.ui.unit.TextUnit,
    fontSizeSmall: androidx.compose.ui.unit.TextUnit,
    iconSize: Dp,
    logoSize: Dp,
    darkSurface: Color,
    white: Color,
    grayText: Color,
    grayDark: Color,
    accentRed: Color,
    accentRedLight: Color,
    successGreen: Color,
    spacingBetweenSections: Dp,
    cornerRadius: Dp,
    shadowElevation: Dp
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(spacingBetweenSections)
    ) {
        // ✅ LOGO E HEADER
        LogoSection(
            logoSize = logoSize,
            white = white,
            grayDark = grayDark,
            darkSurface = darkSurface,
            fontSizeTitle = fontSizeTitle,
            grayText = grayText,
            cornerRadius = cornerRadius,
            shadowElevation = shadowElevation
        )

        // ✅ FORMULÁRIO
        ForgotPasswordForm(
            email = email,
            onEmailChange = onEmailChange,
            isLoading = isLoading,
            message = message,
            isSuccess = isSuccess,
            onSendClick = onSendClick,
            onBackClick = onBackClick,
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
            successGreen = successGreen,
            cornerRadius = cornerRadius,
            shadowElevation = shadowElevation
        )

        // ✅ INFORMAÇÕES DE CONTATO
        ContactCard(
            fontSizeBody = fontSizeBody,
            fontSizeSmall = fontSizeSmall,
            darkSurface = darkSurface,
            white = white,
            grayText = grayText,
            accentRed = accentRed,
            cardPadding = cardPadding,
            cornerRadius = cornerRadius,
            shadowElevation = shadowElevation
        )
    }
}

@Composable
private fun LogoSection(
    logoSize: Dp,
    white: Color,
    grayDark: Color,
    darkSurface: Color,
    fontSizeTitle: androidx.compose.ui.unit.TextUnit,
    grayText: Color,
    cornerRadius: Dp,
    shadowElevation: Dp
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Box(
            modifier = Modifier
                .size(logoSize)
                .shadow(
                    elevation = shadowElevation,
                    shape = RoundedCornerShape(cornerRadius),
                    clip = false
                )
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(darkSurface, grayDark)
                    ),
                    shape = RoundedCornerShape(cornerRadius)
                )
                .padding(logoSize * 0.18f),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_aa),
                contentDescription = "Logo Escola de Futebol",
                modifier = Modifier.size(logoSize * 0.55f),
                contentScale = ContentScale.Fit
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Recuperação de Senha",
                color = white,
                fontSize = fontSizeTitle,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Digite seu email para receber um link de recuperação",
                color = grayText,
                fontSize = fontSizeTitle * 0.65f,
                textAlign = TextAlign.Center,
                lineHeight = fontSizeTitle * 0.85f,
                modifier = Modifier.padding(horizontal = 16.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ForgotPasswordForm(
    email: String,
    onEmailChange: (String) -> Unit,
    isLoading: Boolean,
    message: String?,
    isSuccess: Boolean,
    onSendClick: () -> Unit,
    onBackClick: () -> Unit,
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
    successGreen: Color,
    cornerRadius: Dp,
    shadowElevation: Dp
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = shadowElevation,
                shape = RoundedCornerShape(cornerRadius)
            ),
        colors = CardDefaults.cardColors(
            containerColor = darkSurface
        ),
        shape = RoundedCornerShape(cornerRadius)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(cardPadding),
            verticalArrangement = Arrangement.spacedBy(cardPadding * 0.75f)
        ) {
            if (isSuccess) {
                SuccessMessage(
                    email = email,
                    onBackClick = onBackClick,
                    buttonHeight = buttonHeight,
                    fontSizeBody = fontSizeBody,
                    fontSizeSmall = fontSizeSmall,
                    successGreen = successGreen,
                    white = white,
                    grayText = grayText,
                    accentRed = accentRed,
                    cornerRadius = cornerRadius
                )
            } else {
                RecoveryForm(
                    email = email,
                    onEmailChange = onEmailChange,
                    isLoading = isLoading,
                    message = message,
                    onSendClick = onSendClick,
                    onBackClick = onBackClick,
                    buttonHeight = buttonHeight,
                    textFieldHeight = textFieldHeight,
                    fontSizeBody = fontSizeBody,
                    fontSizeSmall = fontSizeSmall,
                    iconSize = iconSize,
                    white = white,
                    grayText = grayText,
                    grayDark = grayDark,
                    accentRed = accentRed,
                    accentRedLight = accentRedLight,
                    cornerRadius = cornerRadius,
                    shadowElevation = shadowElevation
                )
            }
        }
    }
}

@Composable
private fun SuccessMessage(
    email: String,
    onBackClick: () -> Unit,
    buttonHeight: Dp,
    fontSizeBody: androidx.compose.ui.unit.TextUnit,
    fontSizeSmall: androidx.compose.ui.unit.TextUnit,
    successGreen: Color,
    white: Color,
    grayText: Color,
    accentRed: Color,
    cornerRadius: Dp
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = successGreen.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(cornerRadius * 0.7f)
                )
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "✓ Email Enviado!",
                    color = successGreen,
                    fontSize = fontSizeBody * 1.1f,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Enviamos um link de recuperação para:",
                    color = grayText,
                    fontSize = fontSizeBody,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = email,
                    color = white,
                    fontSize = fontSizeBody,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Verifique sua caixa de entrada e siga as instruções.",
                    color = grayText,
                    fontSize = fontSizeSmall,
                    textAlign = TextAlign.Center,
                    lineHeight = fontSizeSmall * 1.4f
                )
            }
        }

        Button(
            onClick = onBackClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(buttonHeight),
            colors = ButtonDefaults.buttonColors(
                containerColor = accentRed,
                contentColor = white
            ),
            shape = RoundedCornerShape(cornerRadius * 0.6f)
        ) {
            Text(
                text = "Voltar para o Login",
                fontSize = fontSizeBody,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecoveryForm(
    email: String,
    onEmailChange: (String) -> Unit,
    isLoading: Boolean,
    message: String?,
    onSendClick: () -> Unit,
    onBackClick: () -> Unit,
    buttonHeight: Dp,
    textFieldHeight: Dp,
    fontSizeBody: androidx.compose.ui.unit.TextUnit,
    fontSizeSmall: androidx.compose.ui.unit.TextUnit,
    iconSize: Dp,
    white: Color,
    grayText: Color,
    grayDark: Color,
    accentRed: Color,
    accentRedLight: Color,
    cornerRadius: Dp,
    shadowElevation: Dp
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Campo Email
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                text = "Email",
                color = white,
                fontSize = fontSizeBody,
                fontWeight = FontWeight.Medium
            )
            OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                placeholder = {
                    Text(
                        "seu.email@exemplo.com",
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
                shape = RoundedCornerShape(cornerRadius * 0.6f),
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                textStyle = androidx.compose.ui.text.TextStyle(fontSize = fontSizeBody)
            )
        }

        // Mensagem de Status
        if (message != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = accentRed.copy(alpha = 0.1f),
                        shape = RoundedCornerShape(cornerRadius * 0.5f)
                    )
                    .padding(18.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                Text(
                    text = message,
                    color = accentRedLight,
                    fontSize = fontSizeBody * 0.9f,
                    fontWeight = FontWeight.Medium,
                    lineHeight = fontSizeBody * 1.3f
                )
            }
        }

        // Botão Enviar Link
        Button(
            onClick = onSendClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(buttonHeight)
                .shadow(
                    elevation = shadowElevation * 0.75f,
                    shape = RoundedCornerShape(cornerRadius * 0.6f)
                ),
            colors = ButtonDefaults.buttonColors(
                containerColor = accentRed,
                contentColor = white
            ),
            shape = RoundedCornerShape(cornerRadius * 0.6f),
            enabled = !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    color = white,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(buttonHeight * 0.4f)
                )
            } else {
                Text(
                    text = "Enviar Link de Recuperação",
                    fontSize = fontSizeBody,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Link Voltar
        TextButton(
            onClick = onBackClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(buttonHeight * 0.85f)
        ) {
            Text(
                text = "Voltar para o Login",
                color = accentRedLight,
                fontSize = fontSizeBody,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun ContactCard(
    fontSizeBody: androidx.compose.ui.unit.TextUnit,
    fontSizeSmall: androidx.compose.ui.unit.TextUnit,
    darkSurface: Color,
    white: Color,
    grayText: Color,
    accentRed: Color,
    cardPadding: Dp,
    cornerRadius: Dp,
    shadowElevation: Dp
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = shadowElevation * 0.75f,
                shape = RoundedCornerShape(cornerRadius)
            ),
        colors = CardDefaults.cardColors(
            containerColor = darkSurface.copy(alpha = 0.8f)
        ),
        shape = RoundedCornerShape(cornerRadius),
        border = BorderStroke(
            1.5.dp,
            accentRed.copy(alpha = 0.3f)
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(cardPadding * 0.9f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Text(
                text = "Problemas para recuperar?",
                color = white,
                fontSize = fontSizeBody,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Entre em contato: (27) 99999-9999",
                color = grayText,
                fontSize = fontSizeSmall,
                textAlign = TextAlign.Center,
                lineHeight = fontSizeSmall * 1.4f
            )
        }
    }
}