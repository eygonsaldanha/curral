package ey.buriti.curral.auth

import kotlinx.coroutines.flow.StateFlow

/**
 * Centraliza o acesso ao farmId e token da sessão atual.
 * Usado pelo DI para injetar `farmId` nos repositórios e o token no API client.
 */
class SessionManager(private val authRepo: IAuthRepository) {
    val farmId: String
        get() = authRepo.currentFarmId() ?: ""

    suspend fun token(): String = authRepo.currentToken()

    val authState: StateFlow<AuthState> get() = authRepo.authState
}
