package com.example.escolafutebolapp.service

import com.example.escolafutebolapp.models.User
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface FirebaseService {

    // 🔐 AUTHENTICATION ENDPOINTS
    @POST("v1/accounts:signInWithPassword")
    suspend fun signInWithPassword(
        @Query("key") apiKey: String,
        @Body request: SignInRequest
    ): Response<SignInResponse>

    @POST("v1/accounts:signUp")
    suspend fun signUp(
        @Query("key") apiKey: String,
        @Body request: SignUpRequest
    ): Response<SignUpResponse>

    @POST("v1/accounts:sendOobCode")
    suspend fun sendPasswordResetEmail(
        @Query("key") apiKey: String,
        @Body request: PasswordResetRequest
    ): Response<PasswordResetResponse>

    // 👥 USER ENDPOINTS
    @GET("users/{userId}.json")
    suspend fun getUser(@Path("userId") userId: String): Response<User>

    @PUT("users/{userId}.json")
    suspend fun createUser(@Path("userId") userId: String, @Body user: User): Response<User>

    @PUT("users/{userId}.json")
    suspend fun updateUser(@Path("userId") userId: String, @Body user: User): Response<User>

    @DELETE("users/{userId}.json")
    suspend fun deleteUser(@Path("userId") userId: String): Response<Void>

    // 📊 DATA ENDPOINTS (para treinos e agenda)
    @GET("treinos.json")
    suspend fun getTreinos(): Response<Map<String, Any>>

    @PUT("treinos/{treinoId}.json")
    suspend fun createTreino(@Path("treinoId") treinoId: String, @Body treino: Map<String, Any>): Response<Any>

    @GET("agenda/{userId}.json")
    suspend fun getAgenda(@Path("userId") userId: String): Response<Map<String, Any>>
}

// 🔐 REQUEST/RESPONSE MODELS PARA AUTH
data class SignInRequest(
    val email: String,
    val password: String,
    val returnSecureToken: Boolean = true
)

data class SignInResponse(
    val kind: String,
    val localId: String,
    val email: String,
    val displayName: String,
    val idToken: String,
    val registered: Boolean,
    val refreshToken: String,
    val expiresIn: String
)

data class SignUpRequest(
    val email: String,
    val password: String,
    val returnSecureToken: Boolean = true
)

data class SignUpResponse(
    val kind: String,
    val localId: String,
    val email: String,
    val idToken: String,
    val refreshToken: String,
    val expiresIn: String
)

data class PasswordResetRequest(
    val requestType: String = "PASSWORD_RESET",
    val email: String
)

data class PasswordResetResponse(
    val kind: String,
    val email: String
)