import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import java.util.*

class UserRepository(private val firestore: FirebaseFirestore) {

    suspend fun checkUserExists(email: String): Boolean {
        return try {
            val snapshot = firestore.collection("users")
                .whereEqualTo("email", email.toLowerCase())
                .limit(1)
                .get()
                .await()

            !snapshot.isEmpty
        } catch (e: Exception) {
            throw Exception("Erro ao verificar usuário: ${e.message}")
        }
    }

    suspend fun saveResetToken(email: String, token: String) {
        try {
            val resetData = hashMapOf(
                "resetToken" to token,
                "tokenExpiresAt" to Date(System.currentTimeMillis() + 15 * 60 * 1000), // 15 minutos
                "email" to email.toLowerCase()
            )

            firestore.collection("passwordResets")
                .document(email.toLowerCase())
                .set(resetData)
                .await()
        } catch (e: Exception) {
            throw Exception("Erro ao salvar token: ${e.message}")
        }
    }

    suspend fun validateResetToken(email: String, token: String): Boolean {
        return try {
            val document = firestore.collection("passwordResets")
                .document(email.toLowerCase())
                .get()
                .await()

            if (document.exists()) {
                val storedToken = document.getString("resetToken")
                val expiresAt = document.getDate("tokenExpiresAt")

                val isValid = storedToken == token &&
                        expiresAt != null &&
                        expiresAt.after(Date())

                isValid
            } else {
                false
            }
        } catch (e: Exception) {
            throw Exception("Erro ao validar token: ${e.message}")
        }
    }

    suspend fun updatePassword(email: String, newPassword: String) {
        try {
            // Aqui você implementaria a atualização da senha no Firebase Auth
            // Por enquanto, vamos apenas atualizar no Firestore
            val userUpdate = hashMapOf<String, Any>(
                "password" to newPassword, // Em produção, hash a senha!
                "updatedAt" to Date()
            )

            firestore.collection("users")
                .document(email.toLowerCase())
                .update(userUpdate)
                .await()
        } catch (e: Exception) {
            throw Exception("Erro ao atualizar senha: ${e.message}")
        }
    }

    suspend fun clearResetToken(email: String) {
        try {
            firestore.collection("passwordResets")
                .document(email.toLowerCase())
                .delete()
                .await()
        } catch (e: Exception) {
            throw Exception("Erro ao limpar token: ${e.message}")
        }
    }
}