package ey.buriti.curral.routes

import ey.buriti.curral.db.Gestations
import ey.buriti.curral.dto.GestationDto
import ey.buriti.curral.util.nowIso
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.gestationRoutes() {
    route("/api/gestations") {
        get {
            val farmId = call.farmId()
            val since = call.request.queryParameters["since"] ?: "1970-01-01T00:00:00Z"
            call.respond(transaction {
                Gestations.selectAll()
                    .where { (Gestations.farmId eq farmId) and (Gestations.updatedAt greaterEq since) }
                    .map { it.toDto() }
            })
        }

        post {
            val farmId = call.farmId()
            val dto = call.receive<GestationDto>()
            val now = nowIso()
            transaction {
                Gestations.upsert {
                    it[Gestations.id] = dto.id
                    it[Gestations.farmId] = farmId
                    it[Gestations.animalId] = dto.animalId
                    it[Gestations.startDate] = dto.startDate
                    it[Gestations.expectedBirthDate] = dto.expectedBirthDate
                    it[Gestations.notes] = dto.notes
                    it[Gestations.fatherId] = dto.fatherId
                    it[Gestations.version] = dto.version + 1
                    it[Gestations.updatedAt] = now
                }
            }
            call.respond(HttpStatusCode.Created, dto.copy(version = dto.version + 1, updatedAt = now))
        }

        delete("/{id}") {
            val farmId = call.farmId()
            val id = call.parameters["id"]!!
            val now = nowIso()
            transaction {
                Gestations.update({ (Gestations.id eq id) and (Gestations.farmId eq farmId) }) {
                    it[Gestations.deletedAt] = now
                    it[Gestations.updatedAt] = now
                }
            }
            call.respond(HttpStatusCode.NoContent)
        }
    }
}

private fun ResultRow.toDto() = GestationDto(
    id = this[Gestations.id],
    farmId = this[Gestations.farmId],
    animalId = this[Gestations.animalId],
    startDate = this[Gestations.startDate],
    expectedBirthDate = this[Gestations.expectedBirthDate],
    notes = this[Gestations.notes],
    fatherId = this[Gestations.fatherId],
    version = this[Gestations.version],
    updatedAt = this[Gestations.updatedAt],
    deletedAt = this[Gestations.deletedAt],
)
