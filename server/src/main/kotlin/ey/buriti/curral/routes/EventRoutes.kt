package ey.buriti.curral.routes

import ey.buriti.curral.db.AnimalEvents
import ey.buriti.curral.dto.AnimalEventDto
import ey.buriti.curral.util.nowIso
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.eventRoutes() {
    route("/api/events") {
        get {
            val farmId = call.requireFarmIdOrRespond() ?: return@get
            val since = call.request.queryParameters["since"] ?: "1970-01-01T00:00:00Z"
            val rows = transaction {
                AnimalEvents.selectAll()
                    .where { (AnimalEvents.farmId eq farmId) and (AnimalEvents.updatedAt greaterEq since) }
                    .map { it.toDto() }
            }
            call.respond(rows)
        }

        post {
            val farmId = call.requireFarmIdOrRespond() ?: return@post
            val dto = call.receive<AnimalEventDto>()
            val now = nowIso()
            transaction {
                AnimalEvents.upsert {
                    it[AnimalEvents.id] = dto.id
                    it[AnimalEvents.farmId] = farmId
                    it[AnimalEvents.animalId] = dto.animalId
                    it[AnimalEvents.type] = dto.type
                    it[AnimalEvents.date] = dto.date
                    it[AnimalEvents.time] = dto.time
                    it[AnimalEvents.notes] = dto.notes
                    it[AnimalEvents.weightKg] = dto.weightKg
                    it[AnimalEvents.groupId] = dto.groupId
                    it[AnimalEvents.version] = dto.version + 1
                    it[AnimalEvents.updatedAt] = now
                }
            }
            call.respond(HttpStatusCode.Created, dto.copy(version = dto.version + 1, updatedAt = now))
        }

        delete("/{id}") {
            val farmId = call.requireFarmIdOrRespond() ?: return@delete
            val id = call.parameters["id"]!!
            val now = nowIso()
            transaction {
                AnimalEvents.update({ (AnimalEvents.id eq id) and (AnimalEvents.farmId eq farmId) }) {
                    it[AnimalEvents.deletedAt] = now
                    it[AnimalEvents.updatedAt] = now
                }
            }
            call.respond(HttpStatusCode.NoContent)
        }

        put("/{id}") {
            val farmId = call.requireFarmIdOrRespond() ?: return@put
            val id = call.parameters["id"]!!
            val dto = call.receive<AnimalEventDto>()
            val now = nowIso()
            val updated = transaction {
                AnimalEvents.update({ (AnimalEvents.id eq id) and (AnimalEvents.farmId eq farmId) }) {
                    it[AnimalEvents.type] = dto.type
                    it[AnimalEvents.date] = dto.date
                    it[AnimalEvents.time] = dto.time
                    it[AnimalEvents.notes] = dto.notes
                    it[AnimalEvents.weightKg] = dto.weightKg
                    it[AnimalEvents.groupId] = dto.groupId
                    it[AnimalEvents.version] = dto.version + 1
                    it[AnimalEvents.updatedAt] = now
                }
            }
            if (updated == 0) call.respond(HttpStatusCode.NotFound)
            else call.respond(dto.copy(version = dto.version + 1, updatedAt = now))
        }
    }
}

private fun ResultRow.toDto() = AnimalEventDto(
    id = this[AnimalEvents.id],
    farmId = this[AnimalEvents.farmId],
    animalId = this[AnimalEvents.animalId],
    type = this[AnimalEvents.type],
    date = this[AnimalEvents.date],
    time = this[AnimalEvents.time],
    notes = this[AnimalEvents.notes],
    weightKg = this[AnimalEvents.weightKg],
    groupId = this[AnimalEvents.groupId],
    version = this[AnimalEvents.version],
    updatedAt = this[AnimalEvents.updatedAt],
    deletedAt = this[AnimalEvents.deletedAt],
)
