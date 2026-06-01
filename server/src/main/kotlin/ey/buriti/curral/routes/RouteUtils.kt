package ey.buriti.curral.routes

import ey.buriti.curral.db.UserFarms
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.*
import io.ktor.server.auth.jwt.*
import io.ktor.server.application.*
import io.ktor.server.response.respond
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

private const val FARM_HEADER = "X-Farm-Id"

fun ApplicationCall.userId(): String =
    principal<JWTPrincipal>()!!.payload.subject

fun ApplicationCall.resolveFarmIdOrNull(): String? {
    val userId = userId()
    val requestedFarmId = request.headers[FARM_HEADER]?.trim()?.takeIf { it.isNotBlank() }

    return transaction {
        if (requestedFarmId != null) {
            val membership = UserFarms.selectAll()
                .where { (UserFarms.userId eq userId) and (UserFarms.farmId eq requestedFarmId) }
                .limit(1)
                .firstOrNull()
            return@transaction membership?.get(UserFarms.farmId)
        }

        UserFarms.selectAll()
            .where { UserFarms.userId eq userId }
            .orderBy(UserFarms.createdAt to SortOrder.ASC)
            .limit(1)
            .firstOrNull()
            ?.get(UserFarms.farmId)
    }
}

suspend fun ApplicationCall.requireFarmIdOrRespond(): String? {
    val farmId = resolveFarmIdOrNull()
    if (farmId == null) {
        respond(
            HttpStatusCode(428, "Precondition Required"),
            mapOf("error" to "Usuário sem fazenda cadastrada"),
        )
    }
    return farmId
}
