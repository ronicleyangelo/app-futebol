package com.example.escolafutebolapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.escolafutebolapp.R
import com.example.escolafutebolapp.ui.theme.EscolaFutebolAppTheme
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@Composable
fun WelcomeScreen(navController: NavController, userName: String = "Jogador") {
    // ✅ Decodifica o nome do usuário
    val decodedUserName = try {
        java.net.URLDecoder.decode(userName, "UTF-8")
    } catch (e: Exception) {
        userName
    }

    // ✅ Obtém o userId do usuário logado
    val currentUser = FirebaseAuth.getInstance().currentUser
    val userId = currentUser?.uid ?: ""

    // ✅ STATE PARA ARMAZENAR O TIPO DO USUÁRIO
    var userTipo by remember { mutableStateOf("aluno") }

    // ✅ BUSCA O TIPO DO USUÁRIO DO FIREBASE
    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            try {
                val database = Firebase.database("https://escola-de-futebol-jaca-default-rtdb.firebaseio.com/")
                val userRef = database.getReference("users/$userId/tipo_usuario")

                userRef.get().addOnSuccessListener { snapshot ->
                    userTipo = snapshot.getValue(String::class.java) ?: "aluno"
                    println("✅ Tipo de usuário obtido: $userTipo")
                }.addOnFailureListener {
                    println("❌ Erro ao obter tipo de usuário: ${it.message}")
                    userTipo = "aluno" // Fallback
                }
            } catch (e: Exception) {
                println("❌ Exceção ao obter tipo de usuário: ${e.message}")
                userTipo = "aluno" // Fallback
            }
        }
    }

    // ✅ DETECTA O TAMANHO DA TELA
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp

    // ✅ CALCULA VALORES RESPONSIVOS
    val isSmallScreen = screenWidth < 360.dp
    val isLargeScreen = screenWidth > 480.dp
    val isTablet = screenWidth > 600.dp
    val isLargeTablet = screenWidth > 800.dp

    // ✅ TAMANHOS RESPONSIVOS
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

    val spacingBetweenSections = when {
        isLargeTablet -> 32.dp
        isTablet -> 28.dp
        isSmallScreen -> 20.dp
        else -> 24.dp
    }

    // ✅ Coroutine scope para logout
    val scope = rememberCoroutineScope()

    WelcomeContent(
        userName = decodedUserName,
        onTreinosClick = { navController.navigate("treino") },
        // ✅ CORREÇÃO: Passe o userId E userTipo na navegação
        onAgendaClick = {
            if (userId.isNotEmpty()) {
                navController.navigate("agenda/$userId/$userTipo")
                println("📍 Navegando para: agenda/$userId/$userTipo")
            } else {
                println("❌ Erro: userId não encontrado")
                // Fallback: navega para login se não tiver userId
                navController.navigate("login") {
                    popUpTo(0) { inclusive = true }
                }
            }
        },
        onSairClick = {
            scope.launch {
                try {
                    // ✅ DESLOGA DO FIREBASE AUTH
                    println("🔓 Fazendo logout do Firebase Auth...")
                    FirebaseAuth.getInstance().signOut()
                    println("✅ Logout realizado com sucesso!")

                    // ✅ NAVEGA PARA LOGIN E LIMPA O BACK STACK
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                } catch (e: Exception) {
                    println("❌ Erro ao fazer logout: ${e.message}")
                    e.printStackTrace()
                }
            }
        },
        horizontalPadding = horizontalPadding,
        verticalPadding = verticalPadding,
        spacingBetweenSections = spacingBetweenSections,
        isTablet = isTablet,
        isSmallScreen = isSmallScreen,
        isLargeTablet = isLargeTablet
    )
}

@Composable
fun WelcomeContent(
    userName: String,
    onTreinosClick: () -> Unit,
    onAgendaClick: () -> Unit,
    onSairClick: () -> Unit,
    horizontalPadding: Dp,
    verticalPadding: Dp,
    spacingBetweenSections: Dp,
    isTablet: Boolean,
    isSmallScreen: Boolean,
    isLargeTablet: Boolean
) {
    // Cores escuras personalizadas
    val darkBackground = Color(0xFF0D0D0D)
    val darkSurface = Color(0xFF1A1A1A)
    val white = Color(0xFFFFFFFF)
    val grayText = Color(0xFFB3B3B3)
    val mediumGray = Color(0xFF404040)

    // ✅ TAMANHOS DE FONTE RESPONSIVOS
    val fontSizeTitle = when {
        isLargeTablet -> 32.sp
        isTablet -> 28.sp
        isSmallScreen -> 22.sp
        else -> 26.sp
    }

    val fontSizeSubtitle = when {
        isLargeTablet -> 20.sp
        isTablet -> 18.sp
        isSmallScreen -> 14.sp
        else -> 16.sp
    }

    val fontSizeBody = when {
        isLargeTablet -> 18.sp
        isTablet -> 16.sp
        isSmallScreen -> 14.sp
        else -> 15.sp
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
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = horizontalPadding, vertical = verticalPadding),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {

                // ✅ LOGOUT NO TOPO DA TELA
                TopLogoutSection(
                    onSairClick = onSairClick,
                    mediumGray = mediumGray,
                    white = white,
                    isTablet = isTablet,
                    isSmallScreen = isSmallScreen,
                    isLargeTablet = isLargeTablet
                )

                Spacer(Modifier.height(spacingBetweenSections))

                // ✅ LOGO CENTRALIZADA
                LogoSection(
                    isTablet = isTablet,
                    isSmallScreen = isSmallScreen,
                    isLargeTablet = isLargeTablet
                )

                Spacer(Modifier.height(spacingBetweenSections))

                // ✅ SAUDAÇÃO RESPONSIVA
                GreetingSection(
                    userName = userName,
                    white = white,
                    grayText = grayText,
                    fontSizeTitle = fontSizeTitle,
                    fontSizeSubtitle = fontSizeSubtitle
                )

                Spacer(Modifier.height(spacingBetweenSections))

                // ✅ CARDS DE FUNCIONALIDADES RESPONSIVOS
                FeaturesGrid(
                    onTreinosClick = onTreinosClick,
                    onAgendaClick = onAgendaClick,
                    mediumGray = mediumGray,
                    white = white,
                    grayText = grayText,
                    darkSurface = darkSurface,
                    isTablet = isTablet,
                    isSmallScreen = isSmallScreen,
                    isLargeTablet = isLargeTablet,
                    fontSizeBody = fontSizeBody
                )
            }
        }
    }
}

@Composable
private fun TopLogoutSection(
    onSairClick: () -> Unit,
    mediumGray: Color,
    white: Color,
    isTablet: Boolean,
    isSmallScreen: Boolean,
    isLargeTablet: Boolean
) {
    val iconButtonSize = when {
        isLargeTablet -> 52.dp
        isTablet -> 48.dp
        isSmallScreen -> 40.dp
        else -> 44.dp
    }

    val iconSize = when {
        isLargeTablet -> 30.dp
        isTablet -> 26.dp
        isSmallScreen -> 22.dp
        else -> 24.dp
    }

    Box(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(iconButtonSize)
                .clip(RoundedCornerShape(if (isLargeTablet) 14.dp else 12.dp))
                .background(mediumGray.copy(alpha = 0.3f))
        ) {
            IconButton(
                onClick = onSairClick,
                modifier = Modifier.size(iconButtonSize)
            ) {
                Icon(
                    imageVector = Icons.Default.ExitToApp,
                    contentDescription = "Sair",
                    tint = white,
                    modifier = Modifier.size(iconSize)
                )
            }
        }
    }
}

@Composable
private fun LogoSection(
    isTablet: Boolean,
    isSmallScreen: Boolean,
    isLargeTablet: Boolean
) {
    val logoSize = when {
        isLargeTablet -> 160.dp
        isTablet -> 140.dp
        isSmallScreen -> 100.dp
        else -> 120.dp
    }

    val logoImageSize = when {
        isLargeTablet -> 140.dp
        isTablet -> 120.dp
        isSmallScreen -> 80.dp
        else -> 100.dp
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Box(
            modifier = Modifier
                .size(logoSize)
                .shadow(
                    elevation = if (isLargeTablet) 12.dp else 8.dp,
                    shape = RoundedCornerShape(if (isLargeTablet) 24.dp else 20.dp)
                )
                .background(Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.logo_aa),
                contentDescription = "Logo Escola de Futebol",
                modifier = Modifier
                    .size(logoImageSize)
                    .padding(if (isLargeTablet) 12.dp else 8.dp),
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
private fun GreetingSection(
    userName: String,
    white: Color,
    grayText: Color,
    fontSizeTitle: androidx.compose.ui.unit.TextUnit,
    fontSizeSubtitle: androidx.compose.ui.unit.TextUnit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Olá, $userName!",
            fontSize = fontSizeTitle,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = fontSizeTitle * 1.1f,
            color = white,
            modifier = Modifier.fillMaxWidth()
        )

        Text(
            "Bem-vindo à Escola de Futebol!",
            color = grayText,
            fontSize = fontSizeSubtitle,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            lineHeight = fontSizeSubtitle * 1.2f,
            modifier = Modifier.fillMaxWidth()
        )

        Box(
            modifier = Modifier
                .fillMaxWidth(0.6f)
                .height(2.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Transparent,
                            grayText.copy(alpha = 0.3f),
                            Color.Transparent
                        )
                    )
                )
        )
    }
}


@Composable
private fun FeaturesGrid(
    onTreinosClick: () -> Unit,
    onAgendaClick: () -> Unit,
    mediumGray: Color,
    white: Color,
    grayText: Color,
    darkSurface: Color,
    isTablet: Boolean,
    isSmallScreen: Boolean,
    isLargeTablet: Boolean,
    fontSizeBody: androidx.compose.ui.unit.TextUnit
) {
    val cardHeight = when {
        isLargeTablet -> 220.dp
        isTablet -> 200.dp
        isSmallScreen -> 150.dp
        else -> 180.dp
    }

    val cardSpacing = when {
        isLargeTablet -> 32.dp
        isTablet -> 24.dp
        isSmallScreen -> 16.dp
        else -> 20.dp
    }

    val cardPadding = when {
        isLargeTablet -> 24.dp
        isTablet -> 20.dp
        isSmallScreen -> 16.dp
        else -> 18.dp
    }

    val iconBoxSize = when {
        isLargeTablet -> 100.dp
        isTablet -> 90.dp
        isSmallScreen -> 70.dp
        else -> 80.dp
    }

    val iconSize = when {
        isLargeTablet -> 42.dp
        isTablet -> 38.dp
        isSmallScreen -> 30.dp
        else -> 36.dp
    }

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            "O que você gostaria de fazer?",
            color = grayText,
            fontSize = fontSizeBody,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )

        if (isSmallScreen) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(cardSpacing),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // ✅ REMOVIDO: Logo duplicado aqui
                // O logo já está sendo exibido na LogoSection acima

                FeatureCard(
                    title = "Treinos",
                    iconRes = R.drawable.registrar,
                    onClick = onTreinosClick,
                    mediumGray = mediumGray,
                    white = white,
                    grayText = grayText,
                    darkSurface = darkSurface,
                    cardHeight = cardHeight,
                    cardPadding = cardPadding,
                    iconBoxSize = iconBoxSize,
                    iconSize = iconSize,
                    fontSizeBody = fontSizeBody,
                    modifier = Modifier.fillMaxWidth()
                )

                FeatureCard(
                    title = "Agenda",
                    iconRes = R.drawable.agenda,
                    onClick = onAgendaClick,
                    mediumGray = mediumGray,
                    white = white,
                    grayText = grayText,
                    darkSurface = darkSurface,
                    cardHeight = cardHeight,
                    cardPadding = cardPadding,
                    iconBoxSize = iconBoxSize,
                    iconSize = iconSize,
                    fontSizeBody = fontSizeBody,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        } else {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(cardHeight),
                horizontalArrangement = Arrangement.spacedBy(cardSpacing),
                verticalAlignment = Alignment.CenterVertically
            ) {
                FeatureCard(
                    title = "Treinos",
                    iconRes = R.drawable.registrar,
                    onClick = onTreinosClick,
                    mediumGray = mediumGray,
                    white = white,
                    grayText = grayText,
                    darkSurface = darkSurface,
                    cardHeight = cardHeight,
                    cardPadding = cardPadding,
                    iconBoxSize = iconBoxSize,
                    iconSize = iconSize,
                    fontSizeBody = fontSizeBody,
                    modifier = Modifier.weight(1f)
                )

                FeatureCard(
                    title = "Agenda",
                    iconRes = R.drawable.agenda,
                    onClick = onAgendaClick,
                    mediumGray = mediumGray,
                    white = white,
                    grayText = grayText,
                    darkSurface = darkSurface,
                    cardHeight = cardHeight,
                    cardPadding = cardPadding,
                    iconBoxSize = iconBoxSize,
                    iconSize = iconSize,
                    fontSizeBody = fontSizeBody,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun FeatureCard(
    title: String,
    iconRes: Int,
    onClick: () -> Unit,
    mediumGray: Color,
    white: Color,
    grayText: Color,
    darkSurface: Color,
    cardHeight: Dp,
    cardPadding: Dp,
    iconBoxSize: Dp,
    iconSize: Dp,
    fontSizeBody: androidx.compose.ui.unit.TextUnit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(cardHeight)
            .shadow(
                elevation = if (cardHeight > 180.dp) 16.dp else 12.dp,
                shape = RoundedCornerShape(if (cardHeight > 180.dp) 24.dp else 20.dp)
            )
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF282828)),
        shape = RoundedCornerShape(if (cardHeight > 180.dp) 24.dp else 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(cardPadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(iconBoxSize)
                    .background(
                        color = Color(0xFF282828),
                        shape = RoundedCornerShape(if (iconBoxSize > 80.dp) 20.dp else 16.dp)
                    ),
                contentAlignment = Alignment.Center // ✅ Já está centralizando
            ) {
                Image(
                    painter = painterResource(iconRes),
                    contentDescription = "Ícone $title",
                    modifier = Modifier.fillMaxSize(0.85f), // ✅ Reduz para 85% com padding interno
                    contentScale = ContentScale.Fit // ✅ Mantém proporção sem cortar
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                title,
                color = white,
                fontSize = fontSizeBody,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, device = "spec:width=411dp,height=891dp")
@Composable
fun WelcomeScreenMobilePreview() {
    EscolaFutebolAppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFF0D0D0D)
        ) {
            WelcomeContent(
                userName = "Carlos Silva",
                onTreinosClick = {},
                onAgendaClick = {},
                onSairClick = {},
                horizontalPadding = 24.dp,
                verticalPadding = 28.dp,
                spacingBetweenSections = 24.dp,
                isTablet = false,
                isSmallScreen = false,
                isLargeTablet = false
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, device = "spec:width=360dp,height=800dp")
@Composable
fun WelcomeScreenSmallPreview() {
    EscolaFutebolAppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFF0D0D0D)
        ) {
            WelcomeContent(
                userName = "Maria Santos",
                onTreinosClick = {},
                onAgendaClick = {},
                onSairClick = {},
                horizontalPadding = 16.dp,
                verticalPadding = 20.dp,
                spacingBetweenSections = 20.dp,
                isTablet = false,
                isSmallScreen = true,
                isLargeTablet = false
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true, device = "spec:width=1280dp,height=800dp")
@Composable
fun WelcomeScreenTabletPreview() {
    EscolaFutebolAppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFF0D0D0D)
        ) {
            WelcomeContent(
                userName = "João Silva",
                onTreinosClick = {},
                onAgendaClick = {},
                onSairClick = {},
                horizontalPadding = 60.dp,
                verticalPadding = 40.dp,
                spacingBetweenSections = 28.dp,
                isTablet = true,
                isSmallScreen = false,
                isLargeTablet = false
            )
        }
    }
}