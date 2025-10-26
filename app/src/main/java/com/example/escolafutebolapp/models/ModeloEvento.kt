package com.example.escolafutebolapp.models

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import com.google.firebase.database.PropertyName
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class Evento(
    @get:PropertyName("id")
    @set:PropertyName("id")
    var id: String = "",

    @get:PropertyName("tipo_evento")
    @set:PropertyName("tipo_evento")
    var tipoEvento: String = "Treino",

    @get:PropertyName("data_evento")
    @set:PropertyName("data_evento")
    var dataEvento: String = "",

    @get:PropertyName("horario")
    @set:PropertyName("horario")
    var horario: String = "Não informado",

    @get:PropertyName("local")
    @set:PropertyName("local")
    var local: String = "",

    @get:PropertyName("descricao")
    @set:PropertyName("descricao")
    var descricao: String = ""
) {
    // ✅ Propriedade computada para o filtro de data
    @get:PropertyName("data")
    val data: String
        get() = dataEvento

    // ✅ Construtor vazio necessário para Firebase
    constructor() : this("", "Treino", "", "Não informado", "", "")

    fun getCorPorTipo(): Color {
        return when (tipoEvento) {
            "Treino" -> Color(0xFF1AB366)
            "Jogo" -> Color(0xFFCC6619)
            "Reunião" -> Color(0xFF804DCC)
            "Evento Social" -> Color(0xFFE69919)
            "Amistoso" -> Color(0xFFCC3333)
            else -> Color(0xFF4D4D4D)
        }
    }

    fun getIconePorTipo(): String {
        return when (tipoEvento) {
            "Treino" -> "🏃‍♂️"
            "Jogo" -> "⚽"
            "Reunião" -> "📊"
            "Evento Social" -> "🎉"
            "Amistoso" -> "🤝"
            else -> "📅"
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun isEventoFuturo(): Boolean {
        return try {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val data = LocalDate.parse(dataEvento, formatter)
            data >= LocalDate.now()
        } catch (e: Exception) {
            true
        }
    }
}