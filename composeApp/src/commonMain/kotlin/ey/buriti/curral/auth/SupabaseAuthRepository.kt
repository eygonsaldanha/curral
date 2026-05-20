package ey.buriti.curral.auth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SupabaseAuthRepository : IAuthRepository {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    override val authState: StateFlow<AuthState> = _authState.asStateFlow()

    override suspend fun currentToken(): String =
        (authState.value as? AuthState.Authenticated)?.token ?: error("Usuário não autenticado")

    override fun currentFarmId(): String? =
        (authState.value as? AuthState.Authenticated)?.farmId

    override suspend fun signInWithEmail(email: String, password: String): Result<AuthState.Authenticated> =
        runCatching {
            require(email.isNotBlank()) { "Informe o e-mail" }
            require(password.isNotBlank()) { "Informe a senha" }
            val authenticated = AuthState.Authenticated(
                userId = email.trim().lowercase(),
                farmId = email.trim().lowercase(),
                email = email.trim(),
                token = "local-token-${email.trim().lowercase()}",
            )
            _authState.value = authenticated
            authenticated
        }

    override suspend fun signUp(email: String, password: String): Result<AuthState.Authenticated> =
        signInWithEmail(email, password)

    override suspend fun signOut() {
        _authState.value = AuthState.Unauthenticated
    }

    override suspend fun refreshSession() = Unit
}
