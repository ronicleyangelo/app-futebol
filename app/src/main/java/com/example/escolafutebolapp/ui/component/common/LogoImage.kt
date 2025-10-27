package com.example.escolafutebolapp.ui.component.common

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.escolafutebolapp.R

/**
 * Componente de Logo reutilizável
 *
 * @param modifier Modificador para customização
 * @param width Largura do logo
 * @param withBackground Se deve ter fundo decorativo
 * @param backgroundColor Cor do fundo (se withBackground = true)
 */
@Composable
fun LogoImage(
    modifier: Modifier = Modifier,
    width: Dp = 200.dp,
    withBackground: Boolean = false,
    backgroundColor: Color = Color(0xFF1A1A1A)
) {
    val aspectRatio = 668f / 374f // 1.786

    if (withBackground) {
        Surface(
            modifier = modifier
                .width(width)
                .aspectRatio(aspectRatio)
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(16.dp)
                ),
            color = backgroundColor,
            shape = RoundedCornerShape(16.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(width * 0.15f)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.logo_aa),
                    contentDescription = "Logo Escola de Futebol",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
                )
            }
        }
    } else {
        Image(
            painter = painterResource(id = R.drawable.logo_aa),
            contentDescription = "Logo Escola de Futebol",
            modifier = modifier
                .width(width)
                .aspectRatio(aspectRatio),
            contentScale = ContentScale.Fit
        )
    }
}

/**
 * Variação do logo com tamanho pequeno
 */
@Composable
fun SmallLogo(
    modifier: Modifier = Modifier
) {
    LogoImage(
        modifier = modifier,
        width = 100.dp
    )
}

/**
 * Variação do logo com tamanho médio
 */
@Composable
fun MediumLogo(
    modifier: Modifier = Modifier,
    withBackground: Boolean = false
) {
    LogoImage(
        modifier = modifier,
        width = 150.dp,
        withBackground = withBackground
    )
}

/**
 * Variação do logo com tamanho grande
 */
@Composable
fun LargeLogo(
    modifier: Modifier = Modifier,
    withBackground: Boolean = true
) {
    LogoImage(
        modifier = modifier,
        width = 200.dp,
        withBackground = withBackground
    )
}