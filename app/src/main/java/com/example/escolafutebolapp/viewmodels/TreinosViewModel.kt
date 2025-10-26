package com.example.escolafutebolapp.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.escolafutebolapp.models.PlanoTreino
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TreinosState(
    val planosTreino: List<PlanoTreino> = emptyList(),
    val carregando: Boolean = false,
    val erro: String? = null
)

class TreinosViewModel : ViewModel() {

    // ✅ Configure a URL do Realtime Database
    private val database = Firebase.database("https://escola-de-futebol-jaca-default-rtdb.firebaseio.com/")
    private val TAG = "TreinosViewModel"

    private val _state = MutableStateFlow(TreinosState())
    val state: StateFlow<TreinosState> = _state.asStateFlow()

    init {
        carregarPlanosTreino()
    }

    fun carregarPlanosTreino() {
        viewModelScope.launch {
            Log.d(TAG, "🔄 Iniciando carregamento dos treinos do Realtime Database...")
            Log.d(TAG, "🔗 URL: https://escola-de-futebol-jaca-default-rtdb.firebaseio.com/")

            _state.value = _state.value.copy(
                carregando = true,
                erro = null
            )

            try {
                val ref = database.getReference("planos_treino")
                Log.d(TAG, "📍 Referência criada: planos_treino")

                ref.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        Log.d(TAG, "✅ onDataChange chamado!")
                        Log.d(TAG, "✅ Snapshot existe: ${snapshot.exists()}")
                        Log.d(TAG, "✅ Documentos encontrados: ${snapshot.childrenCount}")

                        val planos = mutableListOf<PlanoTreino>()

                        for (childSnapshot in snapshot.children) {
                            try {
                                Log.d(TAG, "📄 Processando: ${childSnapshot.key}")
                                Log.d(TAG, "   Dados: ${childSnapshot.value}")

                                val plano = PlanoTreino(
                                    id = childSnapshot.key ?: "",
                                    titulo = childSnapshot.child("titulo").getValue(String::class.java) ?: "Treino Sem Nome",
                                    descricao = childSnapshot.child("descricao").getValue(String::class.java) ?: "Descrição não disponível",
                                    tempo = childSnapshot.child("tempo").getValue(String::class.java) ?: "Tempo não informado",
                                    cor = childSnapshot.child("cor").getValue(String::class.java) ?: "#3366CC",
                                    dataCriacao = childSnapshot.child("data_migracao").getValue(String::class.java) ?: "",
                                )
                                planos.add(plano)
                                Log.d(TAG, "✅ Plano adicionado: ${plano.titulo}")
                            } catch (e: Exception) {
                                Log.e(TAG, "❌ Erro ao processar documento: ${e.message}")
                                e.printStackTrace()
                            }
                        }

                        Log.d(TAG, "✅ Total de planos carregados: ${planos.size}")

                        _state.value = _state.value.copy(
                            planosTreino = planos,
                            carregando = false,
                            erro = null
                        )
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e(TAG, "❌ onCancelled chamado!")
                        Log.e(TAG, "❌ Código do erro: ${error.code}")
                        Log.e(TAG, "❌ Mensagem: ${error.message}")
                        Log.e(TAG, "❌ Detalhes: ${error.details}")

                        _state.value = _state.value.copy(
                            carregando = false,
                            erro = "Erro: ${error.message}"
                        )
                    }
                })
            } catch (e: Exception) {
                Log.e(TAG, "❌ Exceção geral: ${e.message}")
                e.printStackTrace()

                _state.value = _state.value.copy(
                    carregando = false,
                    erro = e.message ?: "Erro ao conectar com o Firebase"
                )
            }
        }
    }
}