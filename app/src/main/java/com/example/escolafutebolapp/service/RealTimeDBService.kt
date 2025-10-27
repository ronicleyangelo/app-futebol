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
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object RealtimeDBService {

    private val database = FirebaseDatabase.getInstance()
    private val usersRef = database.getReference("users")

    /**
     * ✅ FUNÇÃO NOVA: Busca o usuário atual logado no Firebase Auth
     */
    suspend fun getCurrentUser(): User? {
        return try {
            val firebaseUser = Firebase.auth.currentUser
            if (firebaseUser != null) {
                println("🔍 Buscando usuário atual no Realtime Database: ${firebaseUser.uid}")
                val user = getUser(firebaseUser.uid)
                if (user != null) {
                    println("✅ Usuário atual encontrado: ${user.nome} (${user.tipo_usuario})")
                } else {
                    println("⚠️ Usuário do Auth não encontrado no Realtime Database")
                }
                user
            } else {
                println("ℹ️ Nenhum usuário logado no Firebase Auth")
                null
            }
        } catch (e: Exception) {
            println("❌ Erro ao buscar usuário atual: ${e.message}")
            e.printStackTrace()
            null
        }
    }

    /**
     * Método para verificar admin específico por email
     */
    suspend fun checkAdminExists(email: String): Boolean {
        return try {
            val admin = getUserByEmail(email)
            val exists = admin != null
            println("🔍 Verificando admin $email: ${if (exists) "EXISTE" else "NÃO EXISTE"}")
            exists
        } catch (e: Exception) {
            println("❌ Erro ao verificar admin $email: ${e.message}")
            false
        }
    }

    /**
     * ✅ FUNÇÃO NOVA: Busca o usuário atual com listener em tempo real
     */
    fun getCurrentUserRealtime(onUserChanged: (User?) -> Unit) {
        val firebaseUser = Firebase.auth.currentUser
        if (firebaseUser != null) {
            usersRef.child(firebaseUser.uid)
                .addValueEventListener(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        try {
                            val user = snapshot.getValue(User::class.java)
                            if (user != null) {
                                println("🔄 Usuário atualizado em tempo real: ${user.nome}")
                            }
                            onUserChanged(user)
                        } catch (e: Exception) {
                            println("❌ Erro ao processar usuário em tempo real: ${e.message}")
                            onUserChanged(null)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        println("❌ Listener de usuário cancelado: ${error.message}")
                        onUserChanged(null)
                    }
                })
        } else {
            onUserChanged(null)
        }
    }

    /**
     * ✅ FUNÇÃO NOVA: Verifica se o usuário atual tem permissão de admin/tecnico
     */
    suspend fun currentUserCanEdit(): Boolean {
        return try {
            val currentUser = getCurrentUser()
            val canEdit = currentUser?.tipo_usuario == "admin" || currentUser?.tipo_usuario == "tecnico"
            println("🔐 Permissão de edição: ${if (canEdit) "PERMITIDO" else "NEGADO"} para ${currentUser?.nome}")
            canEdit
        } catch (e: Exception) {
            println("❌ Erro ao verificar permissões: ${e.message}")
            false
        }
    }

    /**
     * ✅ FUNÇÃO NOVA: Atualiza dados do usuário atual
     */
    suspend fun updateCurrentUser(updates: Map<String, Any>): Boolean {
        return try {
            val firebaseUser = Firebase.auth.currentUser
            if (firebaseUser != null) {
                updateUser(firebaseUser.uid, updates)
                true
            } else {
                println("⚠️ Nenhum usuário logado para atualizar")
                false
            }
        } catch (e: Exception) {
            println("❌ Erro ao atualizar usuário atual: ${e.message}")
            false
        }
    }

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

    /**
     * Busca usuário pelo username (versão corrigida)
     */
    suspend fun getUserByUsername(username: String): User? {
        return try {
            println("🔍 Buscando usuário por username: '$username'")

            val database = Firebase.database.reference
            val usersRef = database.child("users")

            val dataSnapshot = withContext(Dispatchers.IO) {
                usersRef.get().await()
            }

            println("📊 Total de usuários no banco: ${dataSnapshot.children.count()}")

            var userFound: User? = null
            dataSnapshot.children.forEach { snapshot ->
                try {
                    val user = snapshot.getValue(User::class.java)
                    if (user != null) {
                        println("👤 Verificando usuário: username='${user.username}' == '$username'")
                        if (user.username?.equals(username, ignoreCase = true) == true) {
                            userFound = user
                            println("✅ Usuário encontrado: ${user.nome} (${user.email})")
                            return@forEach
                        }
                    }
                } catch (e: Exception) {
                    println("⚠️ Erro ao processar usuário: ${e.message}")
                }
            }

            if (userFound == null) {
                println("❌ Nenhum usuário encontrado com username: '$username'")
            }

            userFound
        } catch (e: Exception) {
            println("❌ Erro ao buscar usuário por username '$username': ${e.message}")
            e.printStackTrace()
            null
        }
    }

    // No RealtimeDBService, atualize o método createDefaultAdminIfNotExists:

    /**
     * Cria um usuário admin padrão se não existir
     */
    suspend fun createDefaultAdminIfNotExists() {
        try {
            val adminEmail = "admin@escolafutebol.com"

            // ✅ PRIMEIRO VERIFICA SE JÁ EXISTE NO BANCO
            val existingAdmin = getUserByEmail(adminEmail)

            if (existingAdmin == null) {
                println("🔍 Admin não encontrado no banco. Criando novo admin...")

                // Cria o usuário admin padrão
                val adminUser = User(
                    uid = "default_admin_uid", // UID fixo para o admin padrão
                    nome = "Administrador",
                    email = adminEmail,
                    username = "admin",
                    tipo_usuario = "admin",
                    data_criacao = java.text.SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault()).format(java.util.Date()),
                    senha_provisoria = false,
                    ativo = true
                )

                // Salva o admin no banco
                usersRef.child(adminUser.uid).setValue(adminUser).await()
                println("✅ Usuário admin padrão criado com sucesso!")
                println("📧 Email: $adminEmail")
                println("👤 Username: admin")
                println("🔓 Senha provisória: false")

                // ✅ TENTA CRIAR NO FIREBASE AUTH (se não existir)
                createAdminInFirebaseAuth(adminEmail, "Admin123@")

            } else {
                println("✅ Usuário admin já existe no banco. Nada a fazer.")
                println("📧 Email: ${existingAdmin.email}")
                println("👤 Nome: ${existingAdmin.nome}")
                println("🔑 Tipo: ${existingAdmin.tipo_usuario}")
            }
        } catch (e: Exception) {
            println("❌ Erro ao verificar/criar usuário admin padrão: ${e.message}")
            e.printStackTrace()
        }
    }

    private suspend fun createAdminInFirebaseAuth(email: String, password: String) {
        try {
            val auth = Firebase.auth

            // ✅ PRIMEIRO TENTA LOGIN PARA VER SE JÁ EXISTE
            try {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                println("ℹ️ Admin já existe no Firebase Authentication: ${result.user?.uid}")
                return // Já existe, não precisa criar
            } catch (signInException: Exception) {
                // Se falhou no login, tenta criar
                when {
                    signInException.message?.contains("invalid credential") == true -> {
                        println("🔍 Admin não existe no Auth. Criando...")
                        val createResult = auth.createUserWithEmailAndPassword(email, password).await()
                        println("✅ Admin criado no Firebase Authentication: ${createResult.user?.uid}")
                    }
                    signInException.message?.contains("user not found") == true -> {
                        println("🔍 Admin não existe no Auth. Criando...")
                        val createResult = auth.createUserWithEmailAndPassword(email, password).await()
                        println("✅ Admin criado no Firebase Authentication: ${createResult.user?.uid}")
                    }
                    else -> {
                        println("⚠️ Erro ao verificar admin no Auth: ${signInException.message}")
                    }
                }
            }
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
     * Método adicional para verificar se existe algum admin no sistema
     */
    suspend fun hasAnyAdmin(): Boolean {
        return try {
            val admins = getUsersByType("admin")
            println("🔍 Verificando admins no sistema: ${admins.size} encontrados")
            admins.isNotEmpty()
        } catch (e: Exception) {
            println("❌ Erro ao verificar admins: ${e.message}")
            false
        }
    }

}