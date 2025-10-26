package com.example.escolafutebolapp.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.escolafutebolapp.models.PlanoTreino
import com.example.escolafutebolapp.viewmodel.TreinosViewModel
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TreinosScreen(
    navController: NavController,
    viewModel: TreinosViewModel = viewModel()
) {
    // ✅ DETECTA O TAMANHO DA TELA
    val configuration = LocalConfiguration.current
    val screenWidth = configuration.screenWidthDp.dp
    val screenHeight = configuration.screenHeightDp.dp

    // ✅ CALCULA VALORES RESPONSIVOS
    val isSmallScreen = screenWidth < 360.dp
    val isLargeScreen = screenWidth > 480.dp
    val isTablet = screenWidth > 600.dp
    val isLargeTablet = screenWidth > 800.dp

    // ✅ TAMANHOS RESPONSIVOS
    val horizontalPadding = when {
        isLargeTablet -> 32.dp
        isTablet -> 24.dp
        isSmallScreen -> 12.dp
        else -> 16.dp
    }

    val verticalPadding = when {
        isLargeTablet -> 24.dp
        isTablet -> 20.dp
        isSmallScreen -> 12.dp
        else -> 16.dp
    }

    val cardSpacing = when {
        isLargeTablet -> 20.dp
        isTablet -> 16.dp
        isSmallScreen -> 12.dp
        else -> 14.dp
    }

    // ✅ CORES DO TEMA (PRETO E VERMELHO)
    val darkBackground = Color(0xFF0D0D0D)
    val darkSurface = Color(0xFF1A1A1A)
    val accentRed = Color(0xFFE65C5C)
    val accentRedLight = Color(0xFFFF7B7B)
    val white = Color(0xFFFFFFFF)
    val grayText = Color(0xFFB3B3B3)
    val grayDark = Color(0xFF404040)

    // 🎨 NOVAS CORES PARA OS CARDS
    val cardColors = mapOf(
        "intensivo" to Pair(Color(0xFFE65C5C), Color(0xFFFF5252)), // Vermelho intenso
        "força" to Pair(Color(0xFFFF9800), Color(0xFFFFB74D)),    // Laranja força
        "cardio" to Pair(Color(0xFF2196F3), Color(0xFF64B5F6)),   // Azul cardio
        "técnica" to Pair(Color(0xFF9C27B0), Color(0xFFBA68C8)),  // Roxo técnica
        "alongamento" to Pair(Color(0xFF009688), Color(0xFF4DB6AC)), // Verde alongamento
        "recuperação" to Pair(Color(0xFF607D8B), Color(0xFF90A4AE)), // Cinza recuperação
        "padrão" to Pair(Color(0xFF1A1A1A), Color(0xFF252525))    // Padrão escuro
    )

    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Carrega os dados quando a tela é aberta
    LaunchedEffect(Unit) {
        viewModel.carregarPlanosTreino()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Meus Treinos",
                        color = white,
                        fontSize = when {
                            isLargeTablet -> 22.sp
                            isTablet -> 20.sp
                            isSmallScreen -> 18.sp
                            else -> 19.sp
                        }
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Voltar",
                            tint = white,
                            modifier = Modifier.size(
                                when {
                                    isLargeTablet -> 28.dp
                                    isTablet -> 26.dp
                                    isSmallScreen -> 22.dp
                                    else -> 24.dp
                                }
                            )
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = darkSurface,
                    titleContentColor = white,
                    actionIconContentColor = white
                ),
                actions = {
                    IconButton(
                        onClick = { viewModel.carregarPlanosTreino() },
                        enabled = !state.carregando
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Recarregar",
                            tint = if (state.carregando) grayText else accentRed,
                            modifier = Modifier.size(
                                when {
                                    isLargeTablet -> 26.dp
                                    isTablet -> 24.dp
                                    isSmallScreen -> 20.dp
                                    else -> 22.dp
                                }
                            )
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = darkBackground
    ) { paddingValues ->
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
                .padding(paddingValues)
        ) {
            when {
                state.carregando -> {
                    LoadingSection(
                        isTablet = isTablet,
                        isSmallScreen = isSmallScreen,
                        isLargeTablet = isLargeTablet,
                        white = white,
                        grayText = grayText
                    )
                }

                state.erro != null -> {
                    ErrorSection(
                        error = state.erro!!,
                        onRetry = { viewModel.carregarPlanosTreino() },
                        isTablet = isTablet,
                        isSmallScreen = isSmallScreen,
                        isLargeTablet = isLargeTablet,
                        white = white,
                        grayText = grayText,
                        accentRed = accentRed
                    )
                }

                state.planosTreino.isEmpty() -> {
                    EmptySection(
                        isTablet = isTablet,
                        isSmallScreen = isSmallScreen,
                        isLargeTablet = isLargeTablet,
                        white = white,
                        grayText = grayText
                    )
                }

                else -> {
                    TreinosListSection(
                        planosTreino = state.planosTreino,
                        snackbarHostState = snackbarHostState,
                        scope = scope,
                        horizontalPadding = horizontalPadding,
                        verticalPadding = verticalPadding,
                        cardSpacing = cardSpacing,
                        isTablet = isTablet,
                        isSmallScreen = isSmallScreen,
                        isLargeTablet = isLargeTablet,
                        white = white,
                        grayText = grayText,
                        darkSurface = darkSurface,
                        accentRed = accentRed,
                        cardColors = cardColors
                    )
                }
            }
        }
    }
}

@Composable
private fun LoadingSection(
    isTablet: Boolean,
    isSmallScreen: Boolean,
    isLargeTablet: Boolean,
    white: Color,
    grayText: Color
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        CircularProgressIndicator(
            color = Color(0xFFE65C5C),
            strokeWidth = when {
                isLargeTablet -> 4.dp
                isTablet -> 3.5.dp
                isSmallScreen -> 2.5.dp
                else -> 3.dp
            },
            modifier = Modifier.size(
                when {
                    isLargeTablet -> 48.dp
                    isTablet -> 44.dp
                    isSmallScreen -> 36.dp
                    else -> 40.dp
                }
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Carregando planos de treino...",
            color = grayText,
            fontSize = when {
                isLargeTablet -> 18.sp
                isTablet -> 16.sp
                isSmallScreen -> 14.sp
                else -> 15.sp
            }
        )
    }
}

@Composable
private fun ErrorSection(
    error: String,
    onRetry: () -> Unit,
    isTablet: Boolean,
    isSmallScreen: Boolean,
    isLargeTablet: Boolean,
    white: Color,
    grayText: Color,
    accentRed: Color
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "❌ Erro ao carregar",
            color = accentRed,
            fontSize = when {
                isLargeTablet -> 22.sp
                isTablet -> 20.sp
                isSmallScreen -> 18.sp
                else -> 19.sp
            },
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = error,
            color = grayText,
            fontSize = when {
                isLargeTablet -> 16.sp
                isTablet -> 15.sp
                isSmallScreen -> 13.sp
                else -> 14.sp
            },
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = accentRed,
                contentColor = white
            ),
            modifier = Modifier
                .height(
                    when {
                        isLargeTablet -> 52.dp
                        isTablet -> 48.dp
                        isSmallScreen -> 42.dp
                        else -> 46.dp
                    }
                )
                .padding(horizontal = 32.dp)
        ) {
            Text(
                text = "Tentar Novamente",
                fontSize = when {
                    isLargeTablet -> 16.sp
                    isTablet -> 15.sp
                    isSmallScreen -> 13.sp
                    else -> 14.sp
                },
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun EmptySection(
    isTablet: Boolean,
    isSmallScreen: Boolean,
    isLargeTablet: Boolean,
    white: Color,
    grayText: Color
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            Icons.Default.FitnessCenter,
            contentDescription = "Sem treinos",
            tint = grayText.copy(alpha = 0.5f),
            modifier = Modifier.size(
                when {
                    isLargeTablet -> 80.dp
                    isTablet -> 70.dp
                    isSmallScreen -> 50.dp
                    else -> 60.dp
                }
            )
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Nenhum plano de treino disponível",
            color = grayText,
            fontSize = when {
                isLargeTablet -> 18.sp
                isTablet -> 16.sp
                isSmallScreen -> 14.sp
                else -> 15.sp
            },
            fontWeight = FontWeight.Medium
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Os treinos aparecerão aqui quando disponíveis",
            color = grayText.copy(alpha = 0.7f),
            fontSize = when {
                isLargeTablet -> 14.sp
                isTablet -> 13.sp
                isSmallScreen -> 11.sp
                else -> 12.sp
            },
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}

@Composable
private fun TreinosListSection(
    planosTreino: List<PlanoTreino>,
    snackbarHostState: SnackbarHostState,
    scope: kotlinx.coroutines.CoroutineScope,
    horizontalPadding: Dp,
    verticalPadding: Dp,
    cardSpacing: Dp,
    isTablet: Boolean,
    isSmallScreen: Boolean,
    isLargeTablet: Boolean,
    white: Color,
    grayText: Color,
    darkSurface: Color,
    accentRed: Color,
    cardColors: Map<String, Pair<Color, Color>>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(
            horizontal = horizontalPadding,
            vertical = verticalPadding
        ),
        verticalArrangement = Arrangement.spacedBy(cardSpacing)
    ) {
        item {
            Column {
                Text(
                    text = "${planosTreino.size} planos encontrados",
                    color = grayText,
                    fontSize = when {
                        isLargeTablet -> 16.sp
                        isTablet -> 15.sp
                        isSmallScreen -> 13.sp
                        else -> 14.sp
                    },
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(12.dp))
            }
        }

        items(planosTreino) { plano ->
            CardPlanoTreino(
                plano = plano,
                onClick = {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            "Treino '${plano.titulo}' iniciado!"
                        )
                    }
                },
                isTablet = isTablet,
                isSmallScreen = isSmallScreen,
                isLargeTablet = isLargeTablet,
                white = white,
                grayText = grayText,
                darkSurface = darkSurface,
                accentRed = accentRed,
                cardColors = cardColors
            )
        }
    }
}
@Composable
private fun CardPlanoTreino(
    plano: PlanoTreino,
    onClick: () -> Unit,
    isTablet: Boolean,
    isSmallScreen: Boolean,
    isLargeTablet: Boolean,
    white: Color,
    grayText: Color,
    darkSurface: Color,
    accentRed: Color,
    cardColors: Map<String, Pair<Color, Color>>
) {
    // 🎯 CORES POR CATEGORIA INTELIGENTE
    val (primaryColor, secondaryColor) = remember(plano.titulo) {
        getSmartCardColors(plano)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(
                elevation = when {
                    isLargeTablet -> 8.dp
                    isTablet -> 6.dp
                    isSmallScreen -> 4.dp
                    else -> 5.dp
                },
                shape = RoundedCornerShape(
                    when {
                        isLargeTablet -> 20.dp
                        isTablet -> 18.dp
                        isSmallScreen -> 14.dp
                        else -> 16.dp
                    }
                )
            )
            .clickable(onClick = onClick),
        colors = CardDefaults.cardColors(containerColor = primaryColor),
        shape = RoundedCornerShape(
            when {
                isLargeTablet -> 20.dp
                isTablet -> 18.dp
                isSmallScreen -> 14.dp
                else -> 16.dp
            }
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(primaryColor, secondaryColor)
                    )
                )
                .padding(
                    when {
                        isLargeTablet -> 24.dp
                        isTablet -> 20.dp
                        isSmallScreen -> 16.dp
                        else -> 18.dp
                    }
                )
        ) {
            Column {
                Text(
                    text = plano.titulo.uppercase(),
                    color = white,
                    fontSize = when {
                        isLargeTablet -> 20.sp
                        isTablet -> 18.sp
                        isSmallScreen -> 16.sp
                        else -> 17.sp
                    },
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = plano.descricao,
                    color = white.copy(alpha = 0.9f),
                    fontSize = when {
                        isLargeTablet -> 16.sp
                        isTablet -> 15.sp
                        isSmallScreen -> 13.sp
                        else -> 14.sp
                    },
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(white.copy(alpha = 0.2f))
                            .padding(
                                horizontal = when {
                                    isLargeTablet -> 12.dp
                                    isTablet -> 10.dp
                                    isSmallScreen -> 8.dp
                                    else -> 9.dp
                                },
                                vertical = when {
                                    isLargeTablet -> 6.dp
                                    isTablet -> 5.dp
                                    isSmallScreen -> 4.dp
                                    else -> 4.5.dp
                                }
                            )
                    ) {
                        Text(
                            text = "⏱️ ${plano.tempo}",
                            color = white,
                            fontSize = when {
                                isLargeTablet -> 14.sp
                                isTablet -> 13.sp
                                isSmallScreen -> 11.sp
                                else -> 12.sp
                            },
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
    }
}

// 🎯 FUNÇÃO INTELIGENTE PARA CORES
private fun getSmartCardColors(plano: PlanoTreino): Pair<Color, Color> {
    val titulo = plano.titulo.lowercase()

    return when {
        // TREINOS DE FORÇA E POTÊNCIA
        titulo.contains("força") || titulo.contains("muscul") || titulo.contains("potência") ||
                titulo.contains("peso") || titulo.contains("halter") ->
            Pair(Color(0xFFFF5722), Color(0xFFFF8A65)) // Laranja forte

        // TREINOS CARDIOVASCULARES
        titulo.contains("cardio") || titulo.contains("aerób") || titulo.contains("corrida") ||
                titulo.contains("esteira") || titulo.contains("bike") ->
            Pair(Color(0xFF2196F3), Color(0xFF64B5F6)) // Azul cardio

        // TREINOS TÉCNICOS
        titulo.contains("técnic") || titulo.contains("habilid") || titulo.contains("coordena") ||
                titulo.contains("domínio") || titulo.contains("passe") ->
            Pair(Color(0xFF9C27B0), Color(0xFFBA68C8)) // Roxo técnico

        // TREINOS DE VELOCIDADE
        titulo.contains("velocidade") || titulo.contains("rápid") || titulo.contains("sprint") ||
                titulo.contains("aceleração") ->
            Pair(Color(0xFFE65C5C), Color(0xFFFF5252)) // Vermelho velocidade

        // TREINOS FLEXIBILIDADE
        titulo.contains("along") || titulo.contains("flexib") || titulo.contains("mobilid") ||
                titulo.contains("yoga") || titulo.contains("stretch") ->
            Pair(Color(0xFF009688), Color(0xFF4DB6AC)) // Verde alongamento

        // TREINOS EQUIPE/JOGO
        titulo.contains("jogo") || titulo.contains("equipe") || titulo.contains("coletivo") ||
                titulo.contains("partida") ->
            Pair(Color(0xFFFF9800), Color(0xFFFFB74D)) // Laranja time

        // TREINOS RECUPERAÇÃO
        titulo.contains("recuper") || titulo.contains("regener") || titulo.contains("descans") ||
                titulo.contains("leve") ->
            Pair(Color(0xFF607D8B), Color(0xFF90A4AE)) // Cinza recuperação

        // PADRÃO - CORES VARIADAS
        else -> {
            val colors = listOf(
                Pair(Color(0xFFE65C5C), Color(0xFFFF5252)),
                Pair(Color(0xFF2196F3), Color(0xFF64B5F6)),
                Pair(Color(0xFF9C27B0), Color(0xFFBA68C8)),
                Pair(Color(0xFF009688), Color(0xFF4DB6AC)),
                Pair(Color(0xFFFF9800), Color(0xFFFFB74D))
            )
            colors[plano.titulo.hashCode().absoluteValue % colors.size]
        }
    }
}