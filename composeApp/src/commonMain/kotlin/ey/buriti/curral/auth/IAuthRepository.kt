package ey.buriti.curral.auth

import kotlinx.coroutines.flow.StateFlow

interface IAuthRepository {
    val authState: StateFlow<AuthState>

    /** Token JWT atual para uso nas chamadas de API. */
    suspend fun currentToken(): String

    /** ID da fazenda do usuário autenticado. */
    fun currentFarmId(): String?

    suspend fun signInWithEmail(email: String, password: String): Result<Unit>
    suspend fun signUp(email: String, password: String): Result<Unit>
    suspend fun resendEmailConfirmation(email: String): Result<Unit>
    suspend fun createFarmAndActivate(name: String): Result<Unit>
    suspend fun signOut()
    suspend fun refreshSession()
}
