// app/src/main/java/com/example/escolafutebolapp/models/Treino.kt
package com.example.escolafutebolapp.models

data class Treino(
    val treinoId: String = "",
    val userId: String = "",
    val nome: String = "",
    val descricao: String = "",
    val data: String = "",
    val hora: String = "",
    val duracao: Int = 60,
    val local: String = "",
    val exercicios: List<Exercicio> = emptyList(),
    val dataCriacao: Long = System.currentTimeMillis(),
    val id: String
)

data class Exercicio(
    val nome: String = "",
    val series: Int = 0,
    val repeticoes: Int = 0,
    val peso: String = "",
    val descanso: Int = 0
)