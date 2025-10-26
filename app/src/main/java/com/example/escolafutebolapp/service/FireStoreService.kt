package com.example.escolafutebolapp.service

import com.example.escolafutebolapp.models.PlanoTreino
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreService @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    suspend fun buscarPlanosTreino(): List<PlanoTreino> {
        return try {
            val snapshot = firestore.collection("planos_treino")
                .get()
                .await()

            snapshot.documents.map { document ->
                PlanoTreino(
                    id = document.id,
                    titulo = document.getString("titulo") ?: "Treino Sem Nome",
                    descricao = document.getString("descricao") ?: "Descrição não disponível",
                    tempo = document.getString("tempo") ?: "Tempo não informado",
                    cor = document.getString("cor") ?: "#000000",
                    dataCriacao = document.getString("dataCriacao") ?: "",
                )
            }
        } catch (e: Exception) {
            println("❌ Erro no Firestore: ${e.message}")
            emptyList()
        }
    }
}