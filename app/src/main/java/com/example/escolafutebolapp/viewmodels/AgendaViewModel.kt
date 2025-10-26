package com.example.escolafutebolapp.viewmodel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.escolafutebolapp.models.Evento
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.UUID

data class AgendaState(
    val eventos: List<Evento> = emptyList(),
    val carregando: Boolean = false,
    val erro: String? = null
)

class AgendaViewModel : ViewModel() {

    private val database = Firebase.database("https://escola-de-futebol-jaca-default-rtdb.firebaseio.com/")
    private val TAG = "AgendaViewModel"

    private val _state = MutableStateFlow(AgendaState())
    val state: StateFlow<AgendaState> = _state.asStateFlow()

    init {
        carregarEventos()
    }

    fun carregarEventos() {
        viewModelScope.launch {
            Log.d(TAG, "🔄 Carregando eventos...")

            _state.value = _state.value.copy(
                carregando = true,
                erro = null
            )

            try {
                val ref = database.getReference("agenda")

                ref.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        Log.d(TAG, "✅ Eventos encontrados: ${snapshot.childrenCount}")

                        val eventos = mutableListOf<Evento>()

                        for (childSnapshot in snapshot.children) {
                            try {
                                val evento = Evento(
                                    id = childSnapshot.key ?: "",
                                    tipoEvento = childSnapshot.child("tipo_evento").getValue(String::class.java) ?: "Treino",
                                    dataEvento = childSnapshot.child("data_evento").getValue(String::class.java) ?: "",
                                    horario = childSnapshot.child("horario").getValue(String::class.java) ?: "Não informado",
                                    local = childSnapshot.child("local").getValue(String::class.java) ?: "",
                                    descricao = childSnapshot.child("descricao").getValue(String::class.java) ?: ""
                                )
                                eventos.add(evento)
                            } catch (e: Exception) {
                                Log.e(TAG, "❌ Erro ao processar evento: ${e.message}")
                            }
                        }

                        // Ordena por data
                        eventos.sortByDescending { it.dataEvento }

                        _state.value = _state.value.copy(
                            eventos = eventos,
                            carregando = false,
                            erro = null
                        )
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e(TAG, "❌ Erro: ${error.message}")
                        _state.value = _state.value.copy(
                            carregando = false,
                            erro = error.message
                        )
                    }
                })
            } catch (e: Exception) {
                Log.e(TAG, "❌ Exceção: ${e.message}")
                _state.value = _state.value.copy(
                    carregando = false,
                    erro = e.message ?: "Erro desconhecido"
                )
            }
        }
    }

    fun salvarEvento(evento: Evento) {
        viewModelScope.launch {
            try {
                val novoId = UUID.randomUUID().toString().substring(0, 8)
                val dados = mapOf(
                    "tipo_evento" to evento.tipoEvento,
                    "data_evento" to evento.dataEvento,
                    "horario" to evento.horario,
                    "local" to evento.local,
                    "descricao" to evento.descricao,
                    "ultima_atualizacao" to LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
                )

                database.getReference("agenda/$novoId").setValue(dados)
                    .addOnSuccessListener {
                        Log.d(TAG, "✅ Evento salvo com sucesso")
                        carregarEventos()
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "❌ Erro ao salvar: ${e.message}")
                    }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Exceção ao salvar: ${e.message}")
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun atualizarEvento(evento: Evento) {
        viewModelScope.launch {
            try {
                val dados = mapOf(
                    "tipo_evento" to evento.tipoEvento,
                    "data_evento" to evento.dataEvento,
                    "horario" to evento.horario,
                    "local" to evento.local,
                    "descricao" to evento.descricao,
                    "ultima_atualizacao" to LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME)
                )

                database.getReference("agenda/${evento.id}").updateChildren(dados)
                    .addOnSuccessListener {
                        Log.d(TAG, "✅ Evento atualizado")
                        carregarEventos()
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "❌ Erro ao atualizar: ${e.message}")
                    }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Exceção ao atualizar: ${e.message}")
            }
        }
    }

    fun excluirEvento(id: String) {
        viewModelScope.launch {
            try {
                database.getReference("agenda/$id").removeValue()
                    .addOnSuccessListener {
                        Log.d(TAG, "✅ Evento excluído")
                        carregarEventos()
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "❌ Erro ao excluir: ${e.message}")
                    }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Exceção ao excluir: ${e.message}")
            }
        }
    }
}