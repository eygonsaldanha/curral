package ey.buriti.curral

import com.auth0.jwk.JwkProviderBuilder
import ey.buriti.curral.db.DatabaseFactory
import ey.buriti.curral.routes.animalRoutes
import ey.buriti.curral.routes.eventRoutes
import ey.buriti.curral.routes.gestationRoutes
import ey.buriti.curral.routes.groupRoutes
import ey.buriti.curral.routes.producaoRoutes
import ey.buriti.curral.routes.stockRoutes
import ey.buriti.curral.routes.syncRoutes
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.plugins.cors.routing.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json

fun main() {
    embeddedServer(Netty, port = SERVER_PORT, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    DatabaseFactory.init()
    configurePlugins()
    configureAuth()
    configureRouting()
}

fun Application.configurePlugins() {
    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
        })
    }
    install(CORS) {
        allowMethod(HttpMethod.Options)
        allowMethod(HttpMethod.Get)
        allowMethod(HttpMethod.Post)
        allowMethod(HttpMethod.Put)
        allowMethod(HttpMethod.Delete)
        allowHeader(HttpHeaders.Authorization)
        allowHeader(HttpHeaders.ContentType)
        anyHost() // Restringir em produção
    }
}

fun Application.configureAuth() {
    val jwtIssuer = "$SUPABASE_URL/auth/v1"
    val jwkProvider = JwkProviderBuilder(jwtIssuer)
        .cached(10, 24, java.util.concurrent.TimeUnit.HOURS)
        .rateLimited(10, 1, java.util.concurrent.TimeUnit.MINUTES)
        .build()

    install(Authentication) {
        jwt("supabase") {
            realm = "Curral API"
            verifier(jwkProvider, jwtIssuer)
            validate { credential ->
                val sub = credential.payload.subject
                if (sub != null) JWTPrincipal(credential.payload) else null
            }
            challenge { _, _ ->
                call.respond(
                    io.ktor.http.HttpStatusCode.Unauthorized,
                    mapOf("error" to "Token inválido ou ausente")
                )
            }
        }
    }
}

fun Application.configureRouting() {
    routing {
        get("/") {
            call.respondText("Curral API v1.0")
        }
        authenticate("supabase") {
            animalRoutes()
            groupRoutes()
            eventRoutes()
            gestationRoutes()
            producaoRoutes()
            stockRoutes()
            syncRoutes()
        }
    }
}