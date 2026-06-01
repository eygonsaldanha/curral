package ey.buriti.curral.routes

import ey.buriti.curral.db.AnimalGroups
import ey.buriti.curral.dto.AnimalGroupDto
import ey.buriti.curral.util.nowIso
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.groupRoutes() {
    route("/api/groups") {
        get {
            val farmId = call.requireFarmIdOrRespond() ?: return@get
            val since = call.request.queryParameters["since"] ?: "1970-01-01T00:00:00Z"
            val rows = transaction {
                AnimalGroups.selectAll()
                    .where { (AnimalGroups.farmId eq farmId) and (AnimalGroups.updatedAt greaterEq since) }
                    .map { it.toDto() }
            }
            call.respond(rows)
        }

        post {
            val farmId = call.requireFarmIdOrRespond() ?: return@post
            val dto = call.receive<AnimalGroupDto>()
            val now = nowIso()
            transaction {
                AnimalGroups.upsert {
                    it[AnimalGroups.id] = dto.id
                    it[AnimalGroups.farmId] = farmId
                    it[AnimalGroups.name] = dto.name
                    it[AnimalGroups.description] = dto.description
                    it[AnimalGroups.animalIds] = Json.encodeToString(dto.animalIds)
                    it[AnimalGroups.version] = dto.version + 1
                    it[AnimalGroups.updatedAt] = now
                }
            }
            call.respond(HttpStatusCode.Created, dto.copy(version = dto.version + 1, updatedAt = now))
        }

        put("/{id}") {
            val farmId = call.requireFarmIdOrRespond() ?: return@put
            val id = call.parameters["id"]!!
            val dto = call.receive<AnimalGroupDto>()
            val now = nowIso()
            transaction {
                AnimalGroups.update({ (AnimalGroups.id eq id) and (AnimalGroups.farmId eq farmId) }) {
                    it[AnimalGroups.name] = dto.name
                    it[AnimalGroups.description] = dto.description
                    it[AnimalGroups.animalIds] = Json.encodeToString(dto.animalIds)
                    it[AnimalGroups.version] = dto.version + 1
                    it[AnimalGroups.updatedAt] = now
                }
            }
            call.respond(dto.copy(version = dto.version + 1, updatedAt = now))
        }

        delete("/{id}") {
            val farmId = call.requireFarmIdOrRespond() ?: return@delete
            val id = call.parameters["id"]!!
            val now = nowIso()
            transaction {
                AnimalGroups.update({ (AnimalGroups.id eq id) and (AnimalGroups.farmId eq farmId) }) {
                    it[AnimalGroups.deletedAt] = now
                    it[AnimalGroups.updatedAt] = now
                }
            }
            call.respond(HttpStatusCode.NoContent)
        }
    }
}

private fun ResultRow.toDto() = AnimalGroupDto(
    id = this[AnimalGroups.id],
    farmId = this[AnimalGroups.farmId],
    name = this[AnimalGroups.name],
    description = this[AnimalGroups.description],
    animalIds = Json.decodeFromString(this[AnimalGroups.animalIds]),
    version = this[AnimalGroups.version],
    updatedAt = this[AnimalGroups.updatedAt],
    deletedAt = this[AnimalGroups.deletedAt],
)
