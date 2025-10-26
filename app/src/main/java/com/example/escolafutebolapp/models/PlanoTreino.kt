package com.example.escolafutebolapp.models

import androidx.compose.ui.graphics.Color

data class PlanoTreino(
    val id: String = "",
    val titulo: String = "Treino Sem Nome",
    val descricao: String = "Descrição não disponível",
    val tempo: String = "Tempo não informado",
    val cor: String = "#3366CC",  // ✅ String com valor padrão
    val dataCriacao: String = "",
) {
    // Converte cor String hexadecimal para Color do Compose
    fun getCorCompose(): Color {
        return try {
            // Remove o # se existir e converte para Long
            val colorHex = cor.removePrefix("#")
            Color(android.graphics.Color.parseColor("#$colorHex"))
        } catch (e: Exception) {
            Color(0xFF3366CC) // Cor padrão azul se houver erro
        }
    }

    // Retorna a cor em hexadecimal
    fun getCorHex(): String {
        return if (cor.startsWith("#")) {
            cor
        } else {
            "#$cor"
        }
    }
}