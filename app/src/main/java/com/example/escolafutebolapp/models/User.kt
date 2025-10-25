package com.example.escolafutebolapp.models

data class User(
    val id: String = "",
    val nome: String = "",
    val email: String = "",
    val tipo: String = "aluno",
    val telefone: String = "",
    val dataNascimento: String = "",
    val dataCadastro: String = "",
    val ativo: Boolean = true
) {
    constructor() : this("", "", "", "aluno", "", "", "", true)
}