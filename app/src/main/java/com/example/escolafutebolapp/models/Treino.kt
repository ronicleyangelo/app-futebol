package com.example.escolafutebolapp.models

data class Treino(
    val id: String = "",
    val titulo: String = "",
    val descricao: String = "",
    val duracao: Int = 60, // minutos
    val intensidade: String = "", // leve, moderado, intenso
    val exercicios: List<Exercicio> = emptyList(),
    val criadoPor: String = "", // ID do professor
    val dataCriacao: String = "",
    val ativo: Boolean = true
) {
    constructor() : this("", "", "", 60, "", emptyList(), "", "", true)
}

data class Exercicio(
    val nome: String = "",
    val repeticoes: String = "",
    val descricao: String = "",
    val duracao: Int = 0 // segundos
) {
    constructor() : this("", "", "", 0)
}