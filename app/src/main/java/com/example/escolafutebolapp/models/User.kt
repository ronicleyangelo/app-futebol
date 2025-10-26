package com.example.escolafutebolapp.models

data class User(
    val uid: String = "",
    val nome: String = "",
    val email: String = "",
    val username: String? = null, // Adicione este campo
    val tipo_usuario: String = "aluno", // "aluno", "professor", "admin"
    val data_criacao: String = "",
    val senha_provisoria: Boolean = false,
    val ativo: Boolean = true
) {
    // Construtor vazio necessário para Firebase
    constructor() : this("", "", "", null, "aluno", "", false, true)
}