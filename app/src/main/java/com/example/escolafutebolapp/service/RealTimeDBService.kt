package com.example.escolafutebolapp.service

import com.example.escolafutebolapp.models.User
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.database
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object RealtimeDBService {

    private val database = FirebaseDatabase.getInstance()
    private val usersRef = database.getReference("users")

    /**
     * Salva ou atualiza um usuário no Realtime Database
     */
    suspend fun saveUser(user: User) {
        try {
            usersRef.child(user.uid).setValue(user).await()
            println("✅ Usuário salvo com sucesso: ${user.nome}")
        } catch (e: Exception) {
            println("❌ Erro ao salvar usuário: ${e.message}")
            throw e
        }
    }

    /**
     * Busca um usuário pelo UID (ID do Firebase)
     */
    suspend fun getUser(uid: String): User? {
        return try {
            val snapshot = usersRef.child(uid).get().await()
            snapshot.getValue(User::class.java)
        } catch (e: Exception) {
            println("❌ Erro ao buscar usuário: ${e.message}")
            null
        }
    }

    /**
     * Busca um usuário pelo nome
     * Percorre todos os usuários para encontrar o nome correspondente
     */
    suspend fun getUserByName(nome: String): User? = suspendCoroutine { continuation ->
        usersRef.orderByChild("nome")
            .equalTo(nome)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        if (snapshot.exists()) {
                            // Pega o primeiro usuário encontrado
                            val userSnapshot = snapshot.children.firstOrNull()
                            val user = userSnapshot?.getValue(User::class.java)
                            continuation.resume(user)
                        } else {
                            continuation.resume(null)
                        }
                    } catch (e: Exception) {
                        println("❌ Erro ao processar dados do usuário: ${e.message}")
                        continuation.resume(null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    println("❌ Erro ao buscar usuário por nome: ${error.message}")
                    continuation.resumeWithException(error.toException())
                }
            })
    }

    /**
     * Busca um usuário pelo email
     */
    suspend fun getUserByEmail(email: String): User? = suspendCoroutine { continuation ->
        usersRef.orderByChild("email")
            .equalTo(email)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        if (snapshot.exists()) {
                            val userSnapshot = snapshot.children.firstOrNull()
                            val user = userSnapshot?.getValue(User::class.java)
                            continuation.resume(user)
                        } else {
                            continuation.resume(null)
                        }
                    } catch (e: Exception) {
                        println("❌ Erro ao processar dados do usuário: ${e.message}")
                        continuation.resume(null)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    println("❌ Erro ao buscar usuário por email: ${error.message}")
                    continuation.resumeWithException(error.toException())
                }
            })
    }

    /**
     * Verifica se um email está disponível
     */
    suspend fun isEmailAvailable(email: String): Boolean {
        return getUserByEmail(email) == null
    }

    /**
     * Atualiza campos específicos de um usuário
     */
    suspend fun updateUser(uid: String, updates: Map<String, Any>) {
        try {
            usersRef.child(uid).updateChildren(updates).await()
            println("✅ Usuário atualizado com sucesso")
        } catch (e: Exception) {
            println("❌ Erro ao atualizar usuário: ${e.message}")
            throw e
        }
    }

    /**
     * Deleta um usuário
     */
    suspend fun deleteUser(uid: String) {
        try {
            usersRef.child(uid).removeValue().await()
            println("✅ Usuário deletado com sucesso")
        } catch (e: Exception) {
            println("❌ Erro ao deletar usuário: ${e.message}")
            throw e
        }
    }

    /**
     * Busca todos os usuários (use com cuidado em produção)
     */
    suspend fun getAllUsers(): List<User> {
        return try {
            val snapshot = usersRef.get().await()
            snapshot.children.mapNotNull { it.getValue(User::class.java) }
        } catch (e: Exception) {
            println("❌ Erro ao buscar todos os usuários: ${e.message}")
            emptyList()
        }
    }

    /**
     * Busca usuários por tipo (aluno, professor, admin)
     */
    suspend fun getUsersByType(tipo: String): List<User> = suspendCoroutine { continuation ->
        usersRef.orderByChild("tipo_usuario")
            .equalTo(tipo)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val users = snapshot.children.mapNotNull {
                            it.getValue(User::class.java)
                        }
                        continuation.resume(users)
                    } catch (e: Exception) {
                        println("❌ Erro ao processar usuários: ${e.message}")
                        continuation.resume(emptyList())
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    println("❌ Erro ao buscar usuários por tipo: ${error.message}")
                    continuation.resume(emptyList())
                }
            })
    }

    /**
     * Busca apenas usuários ativos
     */
    suspend fun getActiveUsers(): List<User> = suspendCoroutine { continuation ->
        usersRef.orderByChild("ativo")
            .equalTo(true)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        val users = snapshot.children.mapNotNull {
                            it.getValue(User::class.java)
                        }
                        continuation.resume(users)
                    } catch (e: Exception) {
                        println("❌ Erro ao processar usuários: ${e.message}")
                        continuation.resume(emptyList())
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    println("❌ Erro ao buscar usuários ativos: ${error.message}")
                    continuation.resume(emptyList())
                }
            })
    }

    // No seu RealtimeDBService, adicione este método:
    suspend fun getUserByUsername(username: String): User? {
        return withContext(Dispatchers.IO) {
            try {
                val database = Firebase.database.reference
                val snapshot = database.child("users")
                    .orderByChild("nome")
                    .equalTo(username)
                    .get()
                    .await()

                if (snapshot.exists()) {
                    for (child in snapshot.children) {
                        return@withContext child.getValue(User::class.java)
                    }
                }
                null
            } catch (e: Exception) {
                println("Erro ao buscar usuário por username: ${e.message}")
                null
            }
        }
    }


    // No RealtimeDBService, adicione este método:
    /**
     * Cria um usuário admin padrão se não existir
     */
    suspend fun createDefaultAdminIfNotExists() {
        try {
            val adminEmail = "admin@escolafutebol.com"
            val adminPassword = "Admin123@" // Senha padrão forte
            val existingAdmin = getUserByEmail(adminEmail)

            if (existingAdmin == null) {
                // Cria o usuário admin padrão
                val adminUser = User(
                    uid = "default_admin_uid", // UID fixo para o admin padrão
                    nome = "Administrador",
                    email = adminEmail,
                    username = "admin",
                    tipo_usuario = "admin",
                    data_criacao = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(java.util.Date()),
                    senha_provisoria = false, // ✅ AGORA É FALSE - SENHA DEFINIDA
                    ativo = true
                )

                // Salva o admin no banco
                usersRef.child(adminUser.uid).setValue(adminUser).await()
                println("✅ Usuário admin padrão criado com sucesso!")
                println("📧 Email: $adminEmail")
                println("👤 Username: admin")
                println("🔑 Senha: $adminPassword")
                println("🔓 Senha provisória: false")

                // ✅ AGORA TAMBÉM CRIA NO FIREBASE AUTHENTICATION
                createAdminInFirebaseAuth(adminEmail, adminPassword)

            } else {
                println("✅ Usuário admin já existe no banco")
            }
        } catch (e: Exception) {
            println("❌ Erro ao criar usuário admin padrão: ${e.message}")
        }
    }

    private suspend fun createAdminInFirebaseAuth(email: String, password: String) {
        try {
            // Importe o Firebase Auth no topo do arquivo
            // import com.google.firebase.auth.FirebaseAuth
            // import com.google.firebase.auth.ktx.auth
            // import com.google.firebase.ktx.Firebase

            val auth = Firebase.auth

            // Verifica se já existe no Auth
            val result = auth.createUserWithEmailAndPassword(email, password).await()

            println("✅ Admin criado no Firebase Authentication: ${result.user?.uid}")
        } catch (e: Exception) {
            when {
                e.message?.contains("already exists") == true -> {
                    println("ℹ️ Admin já existe no Firebase Authentication")
                }
                e.message?.contains("already in use") == true -> {
                    println("ℹ️ Email do admin já está em uso no Firebase Authentication")
                }
                else -> {
                    println("❌ Erro ao criar admin no Firebase Auth: ${e.message}")
                }
            }
        }
    }
    /**
     * Verifica se existe pelo menos um usuário admin no sistema
     */
    suspend fun hasAnyAdmin(): Boolean {
        return try {
            val admins = getUsersByType("admin")
            admins.isNotEmpty()
        } catch (e: Exception) {
            println("❌ Erro ao verificar admins: ${e.message}")
            false
        }
    }


}