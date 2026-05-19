package ey.buriti.curral.routes

import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.application.*

/** Extrai o farmId (= sub claim) do JWT autenticado. */
fun ApplicationCall.farmId(): String =
    principal<JWTPrincipal>()!!.payload.subject
