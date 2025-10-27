package com.example.escolafutebolapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.escolafutebolapp.models.AuthState
import com.example.escolafutebolapp.models.User
import com.example.escolafutebolapp.service.RealtimeDBService
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.util.Patterns

class AuthViewModel : ViewModel() {

    // Declaração do Firebase Auth
    private val auth = FirebaseAuth.getInstance()

    // Declaração correta do StateFlow
    private val _authState = MutableStateFlow<AuthState>(AuthState.Idle)
    val authState: StateFlow<AuthState> = _authState

    /**
     * Faz login com email ou nome de usuário
     * @param emailOrUsername pode ser email ou username (nome)
     * @param password senha do usuário
     */
    fun loginUser(emailOrUsername: String, password: String) {
        if (emailOrUsername.isBlank()) {
            _authState.value = AuthState.Error("Email ou nome de usuário não pode estar vazio")
            return
        }

        if (password.length < 6) {
            _authState.value = AuthState.Error("Senha deve ter pelo menos 6 caracteres")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                val email = if (isValidEmail(emailOrUsername)) {
                    // Se for um email válido, usa diretamente
                    emailOrUsername
                } else {
                    // Se não for email, busca o email pelo username
                    val emailFromUsername = getEmailFromUsername(emailOrUsername)
                    if (emailFromUsername == null) {
                        _authState.value = AuthState.Error("Usuário não encontrado")
                        return@launch
                    }
                    emailFromUsername
                }

                // Faz login com o email encontrado
                val result = withContext(Dispatchers.IO) {
                    auth.signInWithEmailAndPassword(email, password).await()
                }

                val firebaseUser = result.user ?: throw Exception("Usuário não retornado pelo Firebase")

                // Atualiza o usuário no Realtime DB se necessário
                handleUserRegistration(firebaseUser)

                _authState.value = AuthState.Success(firebaseUser)

            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is FirebaseAuthInvalidCredentialsException -> "Email/senha incorretos"
                    is FirebaseAuthInvalidUserException -> "Usuário não encontrado"
                    is FirebaseAuthWeakPasswordException -> "Senha muito fraca"
                    else -> "Erro no login: ${e.message}"
                }
                _authState.value = AuthState.Error(errorMessage)
            }
        }
    }
    /**
     * Busca o email de um usuário pelo username no Realtime Database
     */
    private suspend fun getEmailFromUsername(username: String): String? {
        return withContext(Dispatchers.IO) {
            try {
                println("🔍 Buscando email pelo username: '$username'")

                val user = RealtimeDBService.getUserByUsername(username)

                if (user != null) {
                    println("✅ Email encontrado para username '$username': ${user.email}")
                    user.email
                } else {
                    println("❌ Nenhum usuário encontrado com username: '$username'")
                    null
                }
            } catch (e: Exception) {
                println("⚠️ Erro ao buscar email pelo username '$username': ${e.message}")
                null
            }
        }
    }

    /**
     * Registra um novo usuário com email, senha e username
     * @param nome nome completo
     * @param email email
     * @param username nome de usuário único
     * @param password senha
     */
    fun registerUser(nome: String, email: String, username: String, password: String) {
        // Validações
        if (nome.isBlank()) {
            _authState.value = AuthState.Error("Nome não pode estar vazio")
            return
        }

        if (!isValidEmail(email)) {
            _authState.value = AuthState.Error("Email inválido")
            return
        }

        if (username.isBlank() || username.length < 3) {
            _authState.value = AuthState.Error("Nome de usuário deve ter pelo menos 3 caracteres")
            return
        }

        if (!isValidUsername(username)) {
            _authState.value = AuthState.Error("Nome de usuário só pode conter letras, números e underscore")
            return
        }

        if (password.length < 6) {
            _authState.value = AuthState.Error("Senha deve ter pelo menos 6 caracteres")
            return
        }

        viewModelScope.launch {
            _authState.value = AuthState.Loading
            try {
                // Verifica se o username já existe
                val existingUser = withContext(Dispatchers.IO) {
                    RealtimeDBService.getUserByUsername(username)
                }

                if (existingUser != null) {
                    _authState.value = AuthState.Error("Nome de usuário já está em uso")
                    return@launch
                }

                // Cria conta no Firebase Auth
                val result = withContext(Dispatchers.IO) {
                    auth.createUserWithEmailAndPassword(email, password).await()
                }

                val firebaseUser = result.user ?: throw Exception("Usuário não retornado pelo Firebase")

                val newUser = User(
                    uid = firebaseUser.uid,
                    nome = nome,
                    email = email,
                    username = username,
                    tipo_usuario = "aluno",
                    data_criacao = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
                    senha_provisoria = false,
                    ativo = true
                )

                withContext(Dispatchers.IO) {
                    RealtimeDBService.saveUser(newUser)
                }

                _authState.value = AuthState.Success(firebaseUser)

            } catch (e: Exception) {
                val errorMessage = when (e) {
                    is FirebaseAuthWeakPasswordException -> "Senha muito fraca"
                    else -> e.message ?: "Erro ao criar conta"
                }
                _authState.value = AuthState.Error(errorMessage)
            }
        }
    }

    private suspend fun handleUserRegistration(firebaseUser: FirebaseUser) {
        withContext(Dispatchers.IO) {
            try {
                val existingUser = RealtimeDBService.getUser(firebaseUser.uid)

                if (existingUser == null) {
                    val newUser = createUserFromFirebaseUser(firebaseUser)
                    RealtimeDBService.saveUser(newUser)
                    println("✅ Novo usuário criado no Realtime DB")
                } else {
                    println("✅ Usuário encontrado no Realtime DB: ${existingUser.nome}")
                }
            } catch (e: Exception) {
                println("⚠️ Aviso: Problema no Realtime DB, mas login continuará: ${e.message}")
                // Não lança exceção para não interromper o fluxo de login
            }
        }
    }

    private fun createUserFromFirebaseUser(firebaseUser: FirebaseUser): User {
        return User(
            uid = firebaseUser.uid,
            nome = generateUserName(firebaseUser), // ✅ MÉTODO MELHORADO
            email = firebaseUser.email ?: "",
            username = null,
            tipo_usuario = "aluno",
            data_criacao = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
            senha_provisoria = false,
            ativo = true
        )
    }

    /**
     * Gera um nome amigável a partir dos dados do FirebaseUser
     */
    private fun generateUserName(firebaseUser: FirebaseUser): String {
        // 1. Tenta usar o displayName do Firebase
        firebaseUser.displayName?.takeIf { it.isNotBlank() }?.let { return it }

        // 2. Extrai nome do email
        firebaseUser.email?.let { email ->
            val nameFromEmail = extractNameFromEmail(email)
            if (nameFromEmail != null && nameFromEmail != "user" && nameFromEmail != "admin") {
                return nameFromEmail
            }
        }

        // 3. Fallback genérico mas personalizado
        return "Jogador ${firebaseUser.uid.takeLast(4)}" // Ex: "Jogador A1B2"
    }

    /**
     * Extrai e formata nome do email
     */
    private fun extractNameFromEmail(email: String): String? {
        return try {
            val namePart = email.substringBefore('@')

            // Remove números e caracteres especiais no final
            val cleanName = namePart.replace(Regex("[._-]"), " ")
                .replace(Regex("\\d+$"), "") // Remove números no final
                .trim()

            if (cleanName.isBlank() || cleanName.length < 2) {
                return null
            }

            // Capitaliza cada palavra
            cleanName.split(' ')
                .joinToString(" ") { word ->
                    word.replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase() else it.toString()
                    }
                }
        } catch (e: Exception) {
            null
        }
    }
    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValidUsername(username: String): Boolean {
        // Permite apenas letras, números e underscore
        return username.matches(Regex("^[a-zA-Z0-9_]+$"))
    }

    // Método para resetar o estado
    fun resetAuthState() {
        _authState.value = AuthState.Idle
    }

    // Método para logout
    fun logout() {
        auth.signOut()
        _authState.value = AuthState.Idle
    }

    // Método para verificar disponibilidade de username
    fun checkUsernameAvailability(username: String, onResult: (Boolean) -> Unit) {
        if (username.length < 3) {
            onResult(false)
            return
        }

        viewModelScope.launch {
            try {
                val user = withContext(Dispatchers.IO) {
                    RealtimeDBService.getUserByUsername(username)
                }
                onResult(user == null) // Disponível se não encontrou usuário
            } catch (e: Exception) {
                onResult(false)
            }
        }
    }

    suspend fun getCurrentUserName(): String {
        val currentUser = auth.currentUser ?: return "Jogador"
        return withContext(Dispatchers.IO) {
            try {
                val user = RealtimeDBService.getUser(currentUser.uid)
                if (user != null) {
                    println("✅ Usuário encontrado: nome='${user.nome}', email='${user.email}'")
                    return@withContext user.nome
                }

                println("❌ Usuário NÃO encontrado no banco para UID: ${currentUser.uid}")
                currentUser.displayName ?: "Jogador"
            } catch (e: Exception) {
                println("❌ Erro ao buscar nome do usuário: ${e.message}")
                currentUser.displayName ?: "Jogador"
            }
        }
    }

    // Método para buscar detalhes completos do usuário
    suspend fun getCurrentUserDetails(): User? {
        val currentUser = auth.currentUser ?: return null
        return withContext(Dispatchers.IO) {
            RealtimeDBService.getUser(currentUser.uid)
        }
    }

}