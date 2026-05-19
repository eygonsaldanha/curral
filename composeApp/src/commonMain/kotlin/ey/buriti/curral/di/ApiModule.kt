package ey.buriti.curral.di

import ey.buriti.curral.api.IApiService
import ey.buriti.curral.api.KtorApiService
import ey.buriti.curral.auth.SessionManager
import ey.buriti.curral.API_BASE_URL
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.koin.dsl.module

val apiModule = module {
    single {
        HttpClient {
            install(ContentNegotiation) {
                json(Json { ignoreUnknownKeys = true })
            }
        }
    }
    single<IApiService> {
        KtorApiService(
            client = get(),
            baseUrl = API_BASE_URL,
            authRepository = get(),
        )
    }
}
