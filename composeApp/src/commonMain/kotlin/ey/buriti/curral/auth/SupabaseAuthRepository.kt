package ey.buriti.curral.auth

import ey.buriti.curral.API_BASE_URL
import ey.buriti.curral.SUPABASE_ANON_KEY
import ey.buriti.curral.SUPABASE_URL
import ey.buriti.curral.dto.CreateFarmRequest
import ey.buriti.curral.dto.FarmDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.get
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class SupabaseAuthRepository : IAuthRepository {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Unauthenticated)
    override val authState: StateFlow<AuthState> = _authState.asStateFlow()

    private val json = Json { ignoreUnknownKeys = true }
    private val client = HttpClient {
        install(ContentNegotiation) {
            json(json)
        }
        expectSuccess = false
    }

    override suspend fun currentToken(): String =
        when (val state = authState.value) {
            is AuthState.Authenticated -> state.token
            is AuthState.NeedsFarmSetup -> state.token
            else -> error("Usuário não autenticado")
        }

    override fun currentFarmId(): String? =
        (authState.value as? AuthState.Authenticated)?.farmId

    override suspend fun signInWithEmail(email: String, password: String): Result<Unit> =
        runCatching {
            val normalizedEmail = email.trim()
            require(normalizedEmail.isNotBlank()) { "Informe o e-mail" }
            require(password.isNotBlank()) { "Informe a senha" }

            val response = client.post("$SUPABASE_URL/auth/v1/token?grant_type=password") {
                contentType(ContentType.Application.Json)
                header("apikey", SUPABASE_ANON_KEY)
                header(HttpHeaders.Authorization, "Bearer $SUPABASE_ANON_KEY")
                setBody(EmailPasswordRequest(email = normalizedEmail, password = password))
            }

            if (!response.status.isSuccess()) {
                val errorMessage = response.toAuthErrorMessage()
                if (errorMessage.contains("email not confirmed", ignoreCase = true)) {
                    _authState.value = AuthState.AwaitingEmailConfirmation(normalizedEmail)
                    return@runCatching
                }
                throw IllegalStateException(errorMessage.ifBlank { "Erro ao entrar" })
            }

            val payload = response.body<SupabaseTokenResponse>()
            val userId = payload.user?.id?.takeIf { it.isNotBlank() }
                ?: throw IllegalStateException("Supabase não retornou o identificador do usuário")

            val farms = fetchUserFarms(payload.accessToken)
            if (farms.isEmpty()) {
                _authState.value = AuthState.NeedsFarmSetup(
                    userId = userId,
                    email = normalizedEmail,
                    token = payload.accessToken,
                )
                return@runCatching
            }

            _authState.value = AuthState.Authenticated(
                userId = userId,
                farmId = farms.first().id,
                email = normalizedEmail,
                token = payload.accessToken,
            )
        }

    override suspend fun signUp(email: String, password: String): Result<Unit> =
        runCatching {
            val normalizedEmail = email.trim()
            require(normalizedEmail.isNotBlank()) { "Informe o e-mail" }
            require(password.isNotBlank()) { "Informe a senha" }

            val response = client.post("$SUPABASE_URL/auth/v1/signup") {
                contentType(ContentType.Application.Json)
                header("apikey", SUPABASE_ANON_KEY)
                header(HttpHeaders.Authorization, "Bearer $SUPABASE_ANON_KEY")
                setBody(EmailPasswordRequest(email = normalizedEmail, password = password))
            }

            if (!response.status.isSuccess()) {
                throw IllegalStateException(response.toAuthErrorMessage().ifBlank { "Erro ao criar conta" })
            }

            _authState.value = AuthState.AwaitingEmailConfirmation(normalizedEmail)
        }

    override suspend fun resendEmailConfirmation(email: String): Result<Unit> =
        runCatching {
            val normalizedEmail = email.trim()
            require(normalizedEmail.isNotBlank()) { "Informe o e-mail" }

            val response = client.post("$SUPABASE_URL/auth/v1/resend") {
                contentType(ContentType.Application.Json)
                header("apikey", SUPABASE_ANON_KEY)
                header(HttpHeaders.Authorization, "Bearer $SUPABASE_ANON_KEY")
                setBody(ResendConfirmationRequest(email = normalizedEmail, type = "signup"))
            }

            if (!response.status.isSuccess()) {
                throw IllegalStateException(response.toAuthErrorMessage().ifBlank { "Erro ao reenviar confirmação" })
            }
        }

    override suspend fun createFarmAndActivate(name: String): Result<Unit> =
        runCatching {
            val currentState = authState.value as? AuthState.NeedsFarmSetup
                ?: throw IllegalStateException("Fluxo de criação de fazenda indisponível")

            val farmName = name.trim()
            require(farmName.isNotBlank()) { "Informe o nome da fazenda" }

            val response = client.post("$API_BASE_URL/api/farms") {
                bearerAuth(currentState.token)
                contentType(ContentType.Application.Json)
                setBody(CreateFarmRequest(name = farmName))
            }

            if (!response.status.isSuccess()) {
                throw IllegalStateException(response.toAuthErrorMessage().ifBlank { "Erro ao criar fazenda" })
            }

            val farm = response.body<FarmDto>()
            _authState.value = AuthState.Authenticated(
                userId = currentState.userId,
                farmId = farm.id,
                email = currentState.email,
                token = currentState.token,
            )
        }

    override suspend fun signOut() {
        _authState.value = AuthState.Unauthenticated
    }

    override suspend fun refreshSession() = Unit

    private suspend fun fetchUserFarms(token: String): List<FarmDto> {
        val response = client.get("$API_BASE_URL/api/farms") {
            bearerAuth(token)
        }
        if (!response.status.isSuccess()) {
            throw IllegalStateException(response.toAuthErrorMessage().ifBlank { "Erro ao consultar fazendas" })
        }
        return response.body()
    }

    private suspend fun HttpResponse.toAuthErrorMessage(): String {
        val parsed = runCatching { body<SupabaseErrorResponse>() }.getOrNull()
        if (parsed != null) {
            return parsed.errorDescription ?: parsed.message ?: ""
        }
        return runCatching { bodyAsText() }.getOrDefault("")
    }
}

@Serializable
private data class EmailPasswordRequest(
    val email: String,
    val password: String,
)

@Serializable
private data class ResendConfirmationRequest(
    val email: String,
    val type: String,
)

@Serializable
private data class SupabaseTokenResponse(
    @SerialName("access_token")
    val accessToken: String,
    val user: SupabaseUser? = null,
)

@Serializable
private data class SupabaseUser(
    val id: String,
)

@Serializable
private data class SupabaseErrorResponse(
    val message: String? = null,
    @SerialName("error_description")
    val errorDescription: String? = null,
)
