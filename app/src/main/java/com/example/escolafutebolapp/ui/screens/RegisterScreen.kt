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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.escolafutebolapp.R
import com.example.escolafutebolapp.models.User
import com.example.escolafutebolapp.service.RealtimeDBService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, DelicateCoroutinesApi::class)
@Composable
fun RegisterScreen(navController: NavController) {
    // ✅ DETECTA O TAMANHO DA TELA
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

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
        isXLargeTablet -> 32.dp
        isLargeTablet -> 28.dp
        isSmallTablet -> 24.dp
        isLargePhone -> 20.dp
        isNormalPhone -> 16.dp
        else -> 12.dp
    }

    // ✅ PADDING INTERNO DOS CARDS - AUMENTADOS
    val cardPadding = when {
        isXLargeTablet -> 64.dp
        isLargeTablet -> 56.dp
        isSmallTablet -> 48.dp
        isLargePhone -> 40.dp
        isNormalPhone -> 32.dp
        else -> 28.dp
    }

    // ✅ TAMANHO DO LOGO - AUMENTADO
    val logoSize = when {
        isXLargeTablet -> 120.dp
        isLargeTablet -> 100.dp
        isSmallTablet -> 85.dp
        isLargePhone -> 75.dp
        isNormalPhone -> 70.dp
        else -> 65.dp
    }

    // ✅ TAMANHO DOS ÍCONES - AUMENTADO
    val iconSize = when {
        isXLargeTablet -> 26.dp
        isLargeTablet -> 24.dp
        isSmallTablet -> 22.dp
        isLargePhone -> 20.dp
        isNormalPhone -> 19.dp
        else -> 18.dp
    }

    // ✅ FONTE DO TÍTULO - AUMENTADA
    val fontSizeTitle = when {
        isXLargeTablet -> 32.sp
        isLargeTablet -> 28.sp
        isSmallTablet -> 24.sp
        isLargePhone -> 22.sp
        isNormalPhone -> 20.sp
        else -> 18.sp
    }

    // ✅ FONTE DO CORPO - AUMENTADA
    val fontSizeBody = when {
        isXLargeTablet -> 18.sp
        isLargeTablet -> 17.sp
        isSmallTablet -> 16.sp
        isLargePhone -> 15.sp
        isNormalPhone -> 15.sp
        else -> 14.sp
    }

    // ✅ FONTE PEQUENA - AUMENTADA
    val fontSizeSmall = when {
        isXLargeTablet -> 15.sp
        isLargeTablet -> 14.sp
        isSmallTablet -> 14.sp
        isLargePhone -> 13.sp
        isNormalPhone -> 13.sp
        else -> 12.sp
    }

    // ✅ ALTURA DOS BOTÕES - AUMENTADA
    val buttonHeight = when {
        isXLargeTablet -> 72.dp
        isLargeTablet -> 68.dp
        isSmallTablet -> 64.dp
        isLargePhone -> 60.dp
        isNormalPhone -> 56.dp
        else -> 54.dp
    }

    // ✅ ALTURA DOS CAMPOS DE TEXTO - AUMENTADA
    val textFieldHeight = when {
        isXLargeTablet -> 72.dp
        isLargeTablet -> 68.dp
        isSmallTablet -> 64.dp
        isLargePhone -> 60.dp
        isNormalPhone -> 56.dp
        else -> 54.dp
    }

    // ✅ ESPAÇAMENTO ENTRE CAMPOS - AUMENTADO
    val spacingBetweenFields = when {
        isXLargeTablet -> 28.dp
        isLargeTablet -> 26.dp
        isSmallTablet -> 24.dp
        isLargePhone -> 22.dp
        isNormalPhone -> 20.dp
        else -> 18.dp
    }

    // ✅ ESPAÇAMENTO ENTRE SEÇÕES - AUMENTADO
    val spacingBetweenSections = when {
        isXLargeTablet -> 36.dp
        isLargeTablet -> 32.dp
        isSmallTablet -> 28.dp
        isLargePhone -> 24.dp
        isNormalPhone -> 20.dp
        else -> 16.dp
    }

    // ✅ RAIO DOS CANTOS - AUMENTADO
    val cornerRadius = when {
        isXLargeTablet -> 28.dp
        isLargeTablet -> 26.dp
        isSmallTablet -> 24.dp
        isLargePhone -> 20.dp
        isNormalPhone -> 18.dp
        else -> 16.dp
    }

    // ✅ ELEVAÇÃO DAS SOMBRAS - AUMENTADA
    val shadowElevation = when {
        isXLargeTablet -> 12.dp
        isLargeTablet -> 11.dp
        isSmallTablet -> 10.dp
        else -> 8.dp
    }

    var nome by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

    // Firebase Auth
    val auth: FirebaseAuth = Firebase.auth

    // Cores
    val darkBackground = Color(0xFF0D0D0D)
    val darkSurface = Color(0xFF1A1A1A)
    val accentRed = Color(0xFFE65C5C)
    val accentRedLight = Color(0xFFFF7B7B)
    val successGreen = Color(0xFF4CAF50)
    val white = Color(0xFFFFFFFF)
    val grayText = Color(0xFFB3B3B3)
    val grayDark = Color(0xFF404040)

    // Efeito para navegar após sucesso
    LaunchedEffect(successMessage) {
        if (successMessage != null) {
            // Aguarda 2 segundos e depois volta para o login
            kotlinx.coroutines.delay(2000)
            navController.popBackStack()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Criar Conta",
                        color = white,
                        fontWeight = FontWeight.SemiBold,
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
                            RegisterContent(
                                nome = nome,
                                email = email,
                                password = password,
                                confirmPassword = confirmPassword,
                                passwordVisible = passwordVisible,
                                confirmPasswordVisible = confirmPasswordVisible,
                                isLoading = isLoading,
                                errorMessage = errorMessage,
                                successMessage = successMessage,
                                onNomeChange = { nome = it },
                                onEmailChange = { email = it },
                                onPasswordChange = { password = it },
                                onConfirmPasswordChange = { confirmPassword = it },
                                onPasswordVisibleChange = { passwordVisible = !passwordVisible },
                                onConfirmPasswordVisibleChange = { confirmPasswordVisible = !confirmPasswordVisible },
                                onRegisterClick = {
                                    registerUser(
                                        nome = nome,
                                        email = email,
                                        password = password,
                                        confirmPassword = confirmPassword,
                                        auth = auth,
                                        onLoadingChange = { isLoading = it },
                                        onErrorMessageChange = { errorMessage = it },
                                        onSuccessMessageChange = { successMessage = it }
                                    )
                                },
                                onBackToLoginClick = { navController.popBackStack() },
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
                                spacingBetweenFields = spacingBetweenFields,
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
                        RegisterContent(
                            nome = nome,
                            email = email,
                            password = password,
                            confirmPassword = confirmPassword,
                            passwordVisible = passwordVisible,
                            confirmPasswordVisible = confirmPasswordVisible,
                            isLoading = isLoading,
                            errorMessage = errorMessage,
                            successMessage = successMessage,
                            onNomeChange = { nome = it },
                            onEmailChange = { email = it },
                            onPasswordChange = { password = it },
                            onConfirmPasswordChange = { confirmPassword = it },
                            onPasswordVisibleChange = { passwordVisible = !passwordVisible },
                            onConfirmPasswordVisibleChange = { confirmPasswordVisible = !confirmPasswordVisible },
                            onRegisterClick = {
                                registerUser(
                                    nome = nome,
                                    email = email,
                                    password = password,
                                    confirmPassword = confirmPassword,
                                    auth = auth,
                                    onLoadingChange = { isLoading = it },
                                    onErrorMessageChange = { errorMessage = it },
                                    onSuccessMessageChange = { successMessage = it }
                                )
                            },
                            onBackToLoginClick = { navController.popBackStack() },
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
                            spacingBetweenFields = spacingBetweenFields,
                            cornerRadius = cornerRadius,
                            shadowElevation = shadowElevation
                        )
                    }
                }
            }
        }
    }
}

@OptIn(DelicateCoroutinesApi::class)
private fun registerUser(
    nome: String,
    email: String,
    password: String,
    confirmPassword: String,
    auth: FirebaseAuth,
    onLoadingChange: (Boolean) -> Unit,
    onErrorMessageChange: (String?) -> Unit,
    onSuccessMessageChange: (String?) -> Unit
) {
    // Validações
    if (nome.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
        onErrorMessageChange("Preencha todos os campos")
        return
    }

    if (password != confirmPassword) {
        onErrorMessageChange("As senhas não coincidem")
        return
    }

    if (password.length < 6) {
        onErrorMessageChange("A senha deve ter pelo menos 6 caracteres")
        return
    }

    if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
        onErrorMessageChange("Digite um email válido")
        return
    }

    onLoadingChange(true)
    onErrorMessageChange(null)
    onSuccessMessageChange(null)

    GlobalScope.launch {
        try {
            // 1. Criar usuário no Firebase Authentication
            val authResult = auth.createUserWithEmailAndPassword(email, password).await()
            val user = authResult.user

            if (user != null) {
                // 2. Criar objeto User para salvar no Realtime Database
                val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                val currentDate = dateFormat.format(Date())

                val newUser = User(
                    uid = user.uid,
                    nome = nome,
                    email = email,
                    username = generateUsername(nome),
                    tipo_usuario = "aluno", // Tipo padrão para novos usuários
                    data_criacao = currentDate,
                    senha_provisoria = false,
                    ativo = true
                )

                // 3. Salvar usuário no Realtime Database
                RealtimeDBService.saveUser(newUser)

                // 4. Sucesso - mostra mensagem e navega após delay
                onLoadingChange(false)
                onSuccessMessageChange("✅ Conta criada com sucesso! Redirecionando para login...")
            } else {
                onLoadingChange(false)
                onErrorMessageChange("Erro ao criar usuário")
            }
        } catch (e: Exception) {
            onLoadingChange(false)
            val errorMessage = when {
                e.message?.contains("email address is already in use") == true ->
                    "Este email já está em uso"
                e.message?.contains("network error") == true ->
                    "Erro de conexão. Verifique sua internet"
                e.message?.contains("invalid email") == true ->
                    "Formato de email inválido"
                e.message?.contains("WEAK_PASSWORD") == true ->
                    "Senha muito fraca. Use uma senha mais forte"
                else -> "Erro ao criar conta: ${e.localizedMessage ?: e.message}"
            }
            onErrorMessageChange(errorMessage)
        }
    }
}

private fun generateUsername(nome: String): String {
    // Gera um username baseado no nome (primeiro.nome)
    val parts = nome.trim().split(" ").filter { it.isNotBlank() }
    return if (parts.size >= 2) {
        "${parts.first().lowercase()}.${parts.last().lowercase()}"
    } else {
        nome.trim().lowercase().replace(" ", ".")
    }
}

@Composable
private fun RegisterContent(
    nome: String,
    email: String,
    password: String,
    confirmPassword: String,
    passwordVisible: Boolean,
    confirmPasswordVisible: Boolean,
    isLoading: Boolean,
    errorMessage: String?,
    successMessage: String?,
    onNomeChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onPasswordVisibleChange: () -> Unit,
    onConfirmPasswordVisibleChange: () -> Unit,
    onRegisterClick: () -> Unit,
    onBackToLoginClick: () -> Unit,
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
    spacingBetweenFields: Dp,
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
        colors = CardDefaults.cardColors(containerColor = darkSurface),
        shape = RoundedCornerShape(cornerRadius)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(cardPadding),
            verticalArrangement = Arrangement.spacedBy(spacingBetweenFields)
        ) {
            // Campo Nome Completo
            StandardTextField(
                label = "Nome Completo",
                value = nome,
                onValueChange = onNomeChange,
                icon = Icons.Default.Person,
                placeholder = "Seu nome completo",
                keyboardType = KeyboardType.Text,
                textFieldHeight = textFieldHeight,
                fontSizeBody = fontSizeBody,
                iconSize = iconSize,
                white = white,
                grayText = grayText,
                grayDark = grayDark,
                accentRed = accentRed,
                cornerRadius = cornerRadius
            )

            // Campo Email
            StandardTextField(
                label = "Email",
                value = email,
                onValueChange = onEmailChange,
                icon = Icons.Default.Email,
                placeholder = "seu.email@exemplo.com",
                keyboardType = KeyboardType.Email,
                textFieldHeight = textFieldHeight,
                fontSizeBody = fontSizeBody,
                iconSize = iconSize,
                white = white,
                grayText = grayText,
                grayDark = grayDark,
                accentRed = accentRed,
                cornerRadius = cornerRadius
            )

            // Campo Senha
            PasswordTextField(
                label = "Senha",
                value = password,
                onValueChange = onPasswordChange,
                placeholder = "Mínimo 6 caracteres",
                passwordVisible = passwordVisible,
                onPasswordVisibleChange = onPasswordVisibleChange,
                textFieldHeight = textFieldHeight,
                fontSizeBody = fontSizeBody,
                iconSize = iconSize,
                white = white,
                grayText = grayText,
                grayDark = grayDark,
                accentRed = accentRed,
                cornerRadius = cornerRadius
            )

            // Campo Confirmar Senha
            PasswordTextField(
                label = "Confirmar Senha",
                value = confirmPassword,
                onValueChange = onConfirmPasswordChange,
                placeholder = "Digite novamente sua senha",
                passwordVisible = confirmPasswordVisible,
                onPasswordVisibleChange = onConfirmPasswordVisibleChange,
                textFieldHeight = textFieldHeight,
                fontSizeBody = fontSizeBody,
                iconSize = iconSize,
                white = white,
                grayText = grayText,
                grayDark = grayDark,
                accentRed = accentRed,
                cornerRadius = cornerRadius
            )

            // MENSAGEM DE ERRO
            if (errorMessage != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = accentRed.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(cornerRadius * 0.7f)
                        )
                        .padding(20.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = errorMessage,
                        color = accentRedLight,
                        fontSize = fontSizeSmall,
                        fontWeight = FontWeight.Medium,
                        lineHeight = fontSizeSmall * 1.5f
                    )
                }
            }

            // MENSAGEM DE SUCESSO
            if (successMessage != null) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            color = successGreen.copy(alpha = 0.1f),
                            shape = RoundedCornerShape(cornerRadius * 0.7f)
                        )
                        .padding(20.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Text(
                        text = successMessage,
                        color = successGreen,
                        fontSize = fontSizeSmall,
                        fontWeight = FontWeight.Medium,
                        lineHeight = fontSizeSmall * 1.5f
                    )
                }
            }

            // BOTÃO CADASTRAR
            Button(
                onClick = onRegisterClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(buttonHeight)
                    .shadow(
                        elevation = shadowElevation * 0.75f,
                        shape = RoundedCornerShape(cornerRadius * 0.75f)
                    ),
                colors = ButtonDefaults.buttonColors(
                    containerColor = accentRed,
                    contentColor = white
                ),
                shape = RoundedCornerShape(cornerRadius * 0.75f),
                enabled = !isLoading && successMessage == null
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = white,
                        strokeWidth = 3.dp,
                        modifier = Modifier.size(buttonHeight * 0.4f)
                    )
                } else {
                    Text(
                        text = if (successMessage != null) "Conta Criada!" else "Criar Conta",
                        fontSize = fontSizeBody,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // VOLTAR PARA LOGIN
            TextButton(
                onClick = onBackToLoginClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(buttonHeight * 0.85f)
            ) {
                Text(
                    text = "Já tem uma conta? Fazer login",
                    color = accentRedLight,
                    fontSize = fontSizeSmall,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StandardTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    placeholder: String,
    keyboardType: KeyboardType,
    textFieldHeight: Dp,
    fontSizeBody: androidx.compose.ui.unit.TextUnit,
    iconSize: Dp,
    white: Color,
    grayText: Color,
    grayDark: Color,
    accentRed: Color,
    cornerRadius: Dp
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = label,
            color = white,
            fontSize = fontSizeBody,
            fontWeight = FontWeight.Medium
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(textFieldHeight),
            placeholder = {
                Text(
                    placeholder,
                    color = grayText.copy(alpha = 0.6f),
                    fontSize = fontSizeBody
                )
            },
            leadingIcon = {
                Icon(
                    icon,
                    contentDescription = label,
                    tint = grayText,
                    modifier = Modifier.size(iconSize)
                )
            },
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
            shape = RoundedCornerShape(cornerRadius * 0.65f),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = fontSizeBody)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PasswordTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    placeholder: String,
    passwordVisible: Boolean,
    onPasswordVisibleChange: () -> Unit,
    textFieldHeight: Dp,
    fontSizeBody: androidx.compose.ui.unit.TextUnit,
    iconSize: Dp,
    white: Color,
    grayText: Color,
    grayDark: Color,
    accentRed: Color,
    cornerRadius: Dp
) {
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(
            text = label,
            color = white,
            fontSize = fontSizeBody,
            fontWeight = FontWeight.Medium
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier
                .fillMaxWidth()
                .height(textFieldHeight),
            placeholder = {
                Text(
                    placeholder,
                    color = grayText.copy(alpha = 0.6f),
                    fontSize = fontSizeBody
                )
            },
            leadingIcon = {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = null,
                    tint = grayText,
                    modifier = Modifier.size(iconSize)
                )
            },
            trailingIcon = {
                IconButton(
                    onClick = onPasswordVisibleChange,
                    modifier = Modifier.size(iconSize * 1.5f)
                ) {
                    Icon(
                        if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                        contentDescription = null,
                        tint = grayText,
                        modifier = Modifier.size(iconSize)
                    )
                }
            },
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
            shape = RoundedCornerShape(cornerRadius * 0.65f),
            singleLine = true,
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            textStyle = androidx.compose.ui.text.TextStyle(fontSize = fontSizeBody)
        )
    }
}