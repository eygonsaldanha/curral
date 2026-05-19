package ey.buriti.curral.auth

sealed class AuthState {
    data object Loading : AuthState()
    data class Authenticated(
        val userId: String,
        val farmId: String,
        val email: String,
        val token: String,
    ) : AuthState()
    data object Unauthenticated : AuthState()
}
