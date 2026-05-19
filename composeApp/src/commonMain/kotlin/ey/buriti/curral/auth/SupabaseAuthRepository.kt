package ey.buriti.curral.auth

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.auth.status.SessionStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SupabaseAuthRepository(
    private val supabase: SupabaseClient,
) : IAuthRepository {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Loading)
    override val authState: StateFlow<AuthState> = _authState.asStateFlow()

    init {
        CoroutineScope(Dispatchers.Default).launch {
            supabase.auth.sessionStatus.collect { status ->
                _authState.value = when (status) {
                    is SessionStatus.Authenticated -> {
                        val user = supabase.auth.currentUserOrNull()
                        AuthState.Authenticated(
                            userId = user?.id ?: "",
                            farmId = user?.id ?: "", // farmId = userId (uma fazenda por usuário)
                            email = user?.email ?: "",
                            token = supabase.auth.currentAccessTokenOrNull() ?: "",
                        )
                    }
                    is SessionStatus.NotAuthenticated -> AuthState.Unauthenticated
                    is SessionStatus.LoadingFromStorage -> AuthState.Loading
                    is SessionStatus.NetworkError -> _authState.value // mantém estado anterior
                    else -> AuthState.Unauthenticated
                }
            }
        }
    }

    override suspend fun currentToken(): String =
        supabase.auth.currentAccessTokenOrNull() ?: error("Usuário não autenticado")

    override fun currentFarmId(): String? =
        (authState.value as? AuthState.Authenticated)?.farmId

    override suspend fun signInWithEmail(email: String, password: String): Result<AuthState.Authenticated> =
        runCatching {
            supabase.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            val user = supabase.auth.currentUserOrNull() ?: error("Falha ao autenticar")
            AuthState.Authenticated(
                userId = user.id,
                farmId = user.id,
                email = user.email ?: "",
                token = supabase.auth.currentAccessTokenOrNull() ?: "",
            )
        }

    override suspend fun signUp(email: String, password: String): Result<AuthState.Authenticated> =
        runCatching {
            supabase.auth.signUpWith(Email) {
                this.email = email
                this.password = password
            }
            val user = supabase.auth.currentUserOrNull() ?: error("Falha no cadastro")
            AuthState.Authenticated(
                userId = user.id,
                farmId = user.id,
                email = user.email ?: "",
                token = supabase.auth.currentAccessTokenOrNull() ?: "",
            )
        }

    override suspend fun signOut() {
        supabase.auth.signOut()
    }

    override suspend fun refreshSession() {
        supabase.auth.refreshCurrentSession()
    }
}
