package com.example.escolafutebolapp.models

data class Agenda(
    val id: String = "",
    val userId: String = "",
    val treinoId: String = "",
    val data: String = "",
    val hora: String = "",
    val status: String = "agendado", // agendado, realizado, cancelado
    val observacoes: String = "",
    val dataCriacao: String = ""
) {
    constructor() : this("", "", "", "", "", "agendado", "", "")
}