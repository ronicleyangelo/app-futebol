// app/src/main/java/com/example/escolafutebolapp/ui/screens/WelcomeScreen.kt
package com.example.escolafutebolapp.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.escolafutebolapp.R
import com.example.escolafutebolapp.ui.theme.EscolaFutebolAppTheme

@Composable
fun WelcomeScreen(navController: NavController) {
    WelcomeContent(
        onTreinosClick = { navController.navigate("treino") },
        onAgendaClick = { navController.navigate("agenda") },
        onSairClick = {
            navController.navigate("login") {
                popUpTo("welcome") { inclusive = true }
            }
        }
    )
}

@Composable
fun WelcomeContent(
    onTreinosClick: () -> Unit,
    onAgendaClick: () -> Unit,
    onSairClick: () -> Unit
) {
    // Cores escuras personalizadas
    val darkBackground = Color(0xFF0D0D0D)
    val darkSurface = Color(0xFF1A1A1A)
    val white = Color(0xFFFFFFFF)
    val grayText = Color(0xFFB3B3B3)
    val mediumGray = Color(0xFF404040)

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
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {

                // CABEÇALHO COM LOGOUT
                HeaderSectionWithLogout(onSairClick, mediumGray, darkSurface, white)

                Spacer(Modifier.height(40.dp))

                // SAUDAÇÃO
                GreetingSection(white, grayText)

                Spacer(Modifier.height(48.dp))

                // CARDS DE FUNCIONALIDADES
                FeaturesGrid(
                    onTreinosClick = onTreinosClick,
                    onAgendaClick = onAgendaClick,
                    mediumGray = mediumGray,
                    white = white,
                    grayText = grayText,
                    darkSurface = darkSurface
                )
            }
        }
    }
}

@Composable
private fun HeaderSectionWithLogout(
    onSairClick: () -> Unit,
    mediumGray: Color,
    darkSurface: Color,
    white: Color
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Container da logo
                Box(
                    modifier = Modifier
                        .size(140.dp)
                        .shadow(
                            elevation = 8.dp,
                            shape = RoundedCornerShape(20.dp)
                        )
                        .background(Color.Transparent),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.logo_aa),
                        contentDescription = "Logo Escola de Futebol",
                        modifier = Modifier
                            .size(120.dp)
                            .padding(8.dp),
                        contentScale = ContentScale.Fit
                    )
                }
            }

            // Ícone de logout
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .size(44.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(mediumGray.copy(alpha = 0.3f))
            ) {
                IconButton(
                    onClick = onSairClick,
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = "Sair",
                        tint = white,
                        modifier = Modifier.size(26.dp)
                    )
                }
            }
        }

        Text(
            "Olá, Amigo Bom de Bola!",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            lineHeight = 32.sp,
            color = white
        )
    }
}

@Composable
private fun GreetingSection(white: Color, grayText: Color) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            "Bem-vindo à Escola de Futebol!",
            color = grayText,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center,
            lineHeight = 22.sp,
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
    darkSurface: Color
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            "O que você gostaria de fazer?",
            color = grayText,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium,
            textAlign = TextAlign.Center
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FeatureCard(
                "Treinos",
                R.drawable.registrar,
                onTreinosClick,
                mediumGray,
                white,
                grayText,
                darkSurface,
                Modifier.weight(1f)
            )

            FeatureCard(
                "Agenda",
                R.drawable.agenda,
                onAgendaClick,
                mediumGray,
                white,
                grayText,
                darkSurface,
                Modifier.weight(1f)
            )
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
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(180.dp)
            .shadow(
                elevation = 12.dp,
                shape = RoundedCornerShape(20.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = darkSurface),
        shape = RoundedCornerShape(20.dp),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .background(
                        color = mediumGray.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(16.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(iconRes),
                    contentDescription = "Ícone $title",
                    modifier = Modifier.size(36.dp),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                title,
                color = white,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun WelcomeScreenPreview() {
    EscolaFutebolAppTheme {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = Color(0xFF0D0D0D)
        ) {
            WelcomeContent(
                onTreinosClick = {},
                onAgendaClick = {},
                onSairClick = {}
            )
        }
    }
}