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
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale
import java.util.UUID

data class AgendaState(
    val eventos: List<Evento> = emptyList(),
    val carregando: Boolean = false,
    val erro: String? = null,
    val mensagemSucesso: String? = null
)

@RequiresApi(Build.VERSION_CODES.O)
class AgendaViewModel : ViewModel() {

    private val database = Firebase.database("https://escola-de-futebol-jaca-default-rtdb.firebaseio.com/")
    private val TAG = "AgendaViewModel"

    private val _state = MutableStateFlow(AgendaState())
    val state: StateFlow<AgendaState> = _state.asStateFlow()

    private var eventListener: ValueEventListener? = null
    private var currentUserId: String? = null
    private var userTipo: String? = null // ✅ Adiciona tipo de usuário

    /**
     * Define o usuário atual e seu tipo
     */
    fun setCurrentUser(uid: String, tipoUsuario: String? = null) {
        currentUserId = uid
        userTipo = tipoUsuario
        Log.d(TAG, "👤 Usuário definido: $uid, Tipo: $tipoUsuario")
        carregarEventos()
    }

    /**
     * Carrega eventos da agenda geral
     */
    fun carregarEventos() {
        viewModelScope.launch {
            Log.d(TAG, "🔄 Carregando eventos da agenda geral...")

            _state.value = _state.value.copy(
                carregando = true,
                erro = null,
                mensagemSucesso = null
            )

            try {
                // ✅ MUDANÇA: Carrega da agenda geral
                val ref = database.getReference("agenda")

                ref.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        Log.d(TAG, "✅ Eventos encontrados na agenda geral: ${snapshot.childrenCount}")

                        val eventos = processarEventos(snapshot)

                        _state.value = _state.value.copy(
                            eventos = eventos,
                            carregando = false,
                            erro = null
                        )
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e(TAG, "❌ Erro ao carregar: ${error.message}")
                        _state.value = _state.value.copy(
                            carregando = false,
                            erro = "Erro ao carregar eventos: ${error.message}"
                        )
                    }
                })
            } catch (e: Exception) {
                Log.e(TAG, "❌ Exceção ao carregar: ${e.message}")
                _state.value = _state.value.copy(
                    carregando = false,
                    erro = "Erro inesperado: ${e.message ?: "Erro desconhecido"}"
                )
            }
        }
    }

    /**
     * Verifica se o usuário pode editar/excluir eventos
     */
    fun usuarioPodeEditar(): Boolean {
        return userTipo == "admin" || userTipo == "tecnico"
    }

    /**
     * Salva novo evento na agenda geral
     */
    fun salvarEvento(evento: Evento, onSuccess: (() -> Unit)? = null, onError: ((String) -> Unit)? = null) {
        viewModelScope.launch {
            // ✅ VERIFICA PERMISSÃO
            if (!usuarioPodeEditar()) {
                val mensagem = "Você não tem permissão para criar eventos"
                Log.e(TAG, "❌ $mensagem")
                _state.value = _state.value.copy(erro = mensagem)
                onError?.invoke(mensagem)
                return@launch
            }

            try {
                val novoId = UUID.randomUUID().toString().substring(0, 8)
                val dados = mapOf(
                    "tipo_evento" to evento.tipoEvento,
                    "data_evento" to evento.dataEvento,
                    "horario" to evento.horario,
                    "local" to evento.local,
                    "descricao" to evento.descricao,
                    "ultima_atualizacao" to LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                    "criado_por" to currentUserId,
                    "criador_tipo" to userTipo // ✅ Para auditoria
                )

                // ✅ MUDANÇA: Salva na agenda geral
                database.getReference("agenda/$novoId").setValue(dados)
                    .addOnSuccessListener {
                        Log.d(TAG, "✅ Evento salvo na agenda geral: $novoId")
                        _state.value = _state.value.copy(
                            mensagemSucesso = "Evento criado com sucesso!"
                        )
                        carregarEventos()
                        onSuccess?.invoke()
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "❌ Erro ao salvar: ${e.message}")
                        val mensagemErro = "Erro ao salvar evento: ${e.message}"
                        _state.value = _state.value.copy(erro = mensagemErro)
                        onError?.invoke(mensagemErro)
                    }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Exceção ao salvar: ${e.message}")
                val mensagemErro = "Erro inesperado ao salvar: ${e.message}"
                _state.value = _state.value.copy(erro = mensagemErro)
                onError?.invoke(mensagemErro)
            }
        }
    }

    /**
     * Atualiza evento existente na agenda geral
     */
    fun atualizarEvento(evento: Evento, onSuccess: (() -> Unit)? = null, onError: ((String) -> Unit)? = null) {
        viewModelScope.launch {
            // ✅ VERIFICA PERMISSÃO
            if (!usuarioPodeEditar()) {
                val mensagem = "Você não tem permissão para editar eventos"
                Log.e(TAG, "❌ $mensagem")
                _state.value = _state.value.copy(erro = mensagem)
                onError?.invoke(mensagem)
                return@launch
            }

            try {
                if (evento.id.isEmpty()) {
                    val mensagem = "ID do evento inválido"
                    Log.e(TAG, "❌ $mensagem")
                    _state.value = _state.value.copy(erro = mensagem)
                    onError?.invoke(mensagem)
                    return@launch
                }

                val dados = mapOf(
                    "tipo_evento" to evento.tipoEvento,
                    "data_evento" to evento.dataEvento,
                    "horario" to evento.horario,
                    "local" to evento.local,
                    "descricao" to evento.descricao,
                    "ultima_atualizacao" to LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME),
                    "atualizado_por" to currentUserId,
                    "atualizador_tipo" to userTipo // ✅ Para auditoria
                )

                // ✅ MUDANÇA: Atualiza na agenda geral
                database.getReference("agenda/${evento.id}").updateChildren(dados)
                    .addOnSuccessListener {
                        Log.d(TAG, "✅ Evento atualizado: ${evento.id}")
                        _state.value = _state.value.copy(
                            mensagemSucesso = "Evento atualizado com sucesso!"
                        )
                        carregarEventos()
                        onSuccess?.invoke()
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "❌ Erro ao atualizar: ${e.message}")
                        val mensagemErro = "Erro ao atualizar evento: ${e.message}"
                        _state.value = _state.value.copy(erro = mensagemErro)
                        onError?.invoke(mensagemErro)
                    }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Exceção ao atualizar: ${e.message}")
                val mensagemErro = "Erro inesperado ao atualizar: ${e.message}"
                _state.value = _state.value.copy(erro = mensagemErro)
                onError?.invoke(mensagemErro)
            }
        }
    }

    /**
     * Exclui evento da agenda geral
     */
    fun excluirEvento(id: String, onSuccess: (() -> Unit)? = null, onError: ((String) -> Unit)? = null) {
        viewModelScope.launch {
            // ✅ VERIFICA PERMISSÃO
            if (!usuarioPodeEditar()) {
                val mensagem = "Você não tem permissão para excluir eventos"
                Log.e(TAG, "❌ $mensagem")
                _state.value = _state.value.copy(erro = mensagem)
                onError?.invoke(mensagem)
                return@launch
            }

            try {
                if (id.isEmpty()) {
                    val mensagem = "ID do evento inválido"
                    Log.e(TAG, "❌ $mensagem")
                    _state.value = _state.value.copy(erro = mensagem)
                    onError?.invoke(mensagem)
                    return@launch
                }

                // ✅ MUDANÇA: Exclui da agenda geral
                database.getReference("agenda/$id").removeValue()
                    .addOnSuccessListener {
                        Log.d(TAG, "✅ Evento excluído: $id")
                        _state.value = _state.value.copy(
                            mensagemSucesso = "Evento excluído com sucesso!"
                        )
                        carregarEventos()
                        onSuccess?.invoke()
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "❌ Erro ao excluir: ${e.message}")
                        val mensagemErro = "Erro ao excluir evento: ${e.message}"
                        _state.value = _state.value.copy(erro = mensagemErro)
                        onError?.invoke(mensagemErro)
                    }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Exceção ao excluir: ${e.message}")
                val mensagemErro = "Erro inesperado ao excluir: ${e.message}"
                _state.value = _state.value.copy(erro = mensagemErro)
                onError?.invoke(mensagemErro)
            }
        }
    }

    /**
     * Observa eventos em tempo real da agenda geral
     */
    fun observarEventosTempoReal() {
        viewModelScope.launch {
            Log.d(TAG, "👀 Observando eventos em tempo real da agenda geral...")

            _state.value = _state.value.copy(carregando = true)

            try {
                val ref = database.getReference("agenda")

                // Remove listener anterior se existir
                eventListener?.let { ref.removeEventListener(it) }

                eventListener = object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        Log.d(TAG, "🔄 Eventos atualizados: ${snapshot.childrenCount}")

                        val eventos = processarEventos(snapshot)

                        _state.value = _state.value.copy(
                            eventos = eventos,
                            carregando = false,
                            erro = null
                        )
                    }

                    override fun onCancelled(error: DatabaseError) {
                        Log.e(TAG, "❌ Erro na observação: ${error.message}")
                        _state.value = _state.value.copy(
                            carregando = false,
                            erro = "Erro ao observar eventos: ${error.message}"
                        )
                    }
                }

                ref.addValueEventListener(eventListener!!)

            } catch (e: Exception) {
                Log.e(TAG, "❌ Exceção ao observar: ${e.message}")
                _state.value = _state.value.copy(
                    carregando = false,
                    erro = "Erro ao iniciar observação: ${e.message}"
                )
            }
        }
    }

    /**
     * Para de observar eventos em tempo real
     */
    fun pararObservacao() {
        eventListener?.let { listener ->
            database.getReference("agenda").removeEventListener(listener)
            eventListener = null
            Log.d(TAG, "🛑 Observação de eventos encerrada")
        }
    }

    /**
     * Processa e ordena eventos do snapshot
     */
    private fun processarEventos(snapshot: DataSnapshot): List<Evento> {
        return snapshot.children.mapNotNull { childSnapshot ->
            try {
                Evento(
                    id = childSnapshot.key ?: "",
                    tipoEvento = childSnapshot.child("tipo_evento").getValue(String::class.java) ?: "Treino",
                    dataEvento = childSnapshot.child("data_evento").getValue(String::class.java) ?: "",
                    horario = childSnapshot.child("horario").getValue(String::class.java) ?: "Não informado",
                    local = childSnapshot.child("local").getValue(String::class.java) ?: "",
                    descricao = childSnapshot.child("descricao").getValue(String::class.java) ?: ""
                )
            } catch (e: Exception) {
                Log.e(TAG, "❌ Erro ao processar evento ${childSnapshot.key}: ${e.message}")
                null
            }
        }.sortedByDescending { evento ->
            // Ordena por data corretamente
            converterDataParaOrdenacao(evento.dataEvento)
        }
    }

    /**
     * Converte data em formato dd/MM/yyyy para formato ordenável
     */
    private fun converterDataParaOrdenacao(data: String): String {
        return try {
            val parts = data.split("/")
            if (parts.size == 3) {
                // Converte dd/MM/yyyy para yyyyMMdd
                "${parts[2]}${parts[1].padStart(2, '0')}${parts[0].padStart(2, '0')}"
            } else {
                data
            }
        } catch (e: Exception) {
            Log.w(TAG, "⚠️ Erro ao converter data: $data")
            data
        }
    }

    /**
     * Funções auxiliares para Evento
     */
    fun Evento.isEventoFuturo(): Boolean {
        return try {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val eventoDate = dateFormat.parse(this.dataEvento)
            val hoje = Date()

            eventoDate != null && !eventoDate.before(hoje)
        } catch (e: Exception) {
            false
        }
    }

    fun Evento.getIconePorTipo(): String {
        return when (this.tipoEvento) {
            "Treino" -> "⚽"
            "Jogo" -> "🥅"
            "Reunião" -> "📋"
            "Evento Social" -> "🎉"
            "Amistoso" -> "🤝"
            else -> "📅"
        }
    }

    /**
     * Limpa mensagens de erro e sucesso
     */
    fun limparMensagens() {
        _state.value = _state.value.copy(
            erro = null,
            mensagemSucesso = null
        )
    }

    /**
     * Limpa recursos quando o ViewModel é destruído
     */
    override fun onCleared() {
        super.onCleared()
        pararObservacao()
        Log.d(TAG, "🧹 ViewModel limpo")
    }
}