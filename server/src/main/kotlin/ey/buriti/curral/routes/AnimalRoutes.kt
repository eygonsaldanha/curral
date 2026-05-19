package ey.buriti.curral.routes

import ey.buriti.curral.db.Animals
import ey.buriti.curral.dto.AnimalDto
import ey.buriti.curral.util.nowIso
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.animalRoutes() {
    route("/api/animals") {
        get {
            val farmId = call.farmId()
            val since = call.request.queryParameters["since"] ?: "1970-01-01T00:00:00Z"
            val rows = transaction {
                Animals.selectAll()
                    .where { (Animals.farmId eq farmId) and (Animals.updatedAt greaterEq since) }
                    .map { it.toAnimalDto() }
            }
            call.respond(rows)
        }

        get("/{id}") {
            val farmId = call.farmId()
            val id = call.parameters["id"]!!
            val dto = transaction {
                Animals.selectAll()
                    .where { (Animals.id eq id) and (Animals.farmId eq farmId) }
                    .map { it.toAnimalDto() }.firstOrNull()
            }
            if (dto == null) call.respond(HttpStatusCode.NotFound)
            else call.respond(dto)
        }

        post {
            val farmId = call.farmId()
            val dto = call.receive<AnimalDto>()
            val now = nowIso()
            transaction {
                Animals.upsert {
                    it[Animals.id] = dto.id
                    it[Animals.farmId] = farmId
                    it[Animals.name] = dto.name
                    it[Animals.type] = dto.type
                    it[Animals.breed] = dto.breed
                    it[Animals.status] = dto.status
                    it[Animals.sex] = dto.sex
                    it[Animals.tagNumber] = dto.tagNumber
                    it[Animals.birthDate] = dto.birthDate
                    it[Animals.weightKg] = dto.weightKg
                    it[Animals.groupIds] = kotlinx.serialization.json.Json.encodeToString(dto.groupIds)
                    it[Animals.motherId] = dto.motherId
                    it[Animals.fatherId] = dto.fatherId
                    it[Animals.offspringIds] = kotlinx.serialization.json.Json.encodeToString(dto.offspringIds)
                    it[Animals.gestationId] = dto.gestationId
                    it[Animals.version] = dto.version + 1
                    it[Animals.updatedAt] = now
                }
            }
            call.respond(HttpStatusCode.Created, dto.copy(version = dto.version + 1, updatedAt = now))
        }

        put("/{id}") {
            val farmId = call.farmId()
            val id = call.parameters["id"]!!
            val dto = call.receive<AnimalDto>()
            val now = nowIso()
            transaction {
                Animals.update({ (Animals.id eq id) and (Animals.farmId eq farmId) }) {
                    it[Animals.name] = dto.name
                    it[Animals.type] = dto.type
                    it[Animals.breed] = dto.breed
                    it[Animals.status] = dto.status
                    it[Animals.sex] = dto.sex
                    it[Animals.tagNumber] = dto.tagNumber
                    it[Animals.birthDate] = dto.birthDate
                    it[Animals.weightKg] = dto.weightKg
                    it[Animals.groupIds] = kotlinx.serialization.json.Json.encodeToString(dto.groupIds)
                    it[Animals.motherId] = dto.motherId
                    it[Animals.fatherId] = dto.fatherId
                    it[Animals.offspringIds] = kotlinx.serialization.json.Json.encodeToString(dto.offspringIds)
                    it[Animals.gestationId] = dto.gestationId
                    it[Animals.version] = dto.version + 1
                    it[Animals.updatedAt] = now
                }
            }
            call.respond(dto.copy(version = dto.version + 1, updatedAt = now))
        }

        delete("/{id}") {
            val farmId = call.farmId()
            val id = call.parameters["id"]!!
            val now = nowIso()
            transaction {
                Animals.update({ (Animals.id eq id) and (Animals.farmId eq farmId) }) {
                    it[Animals.deletedAt] = now
                    it[Animals.updatedAt] = now
                }
            }
            call.respond(HttpStatusCode.NoContent)
        }
    }
}

private fun ResultRow.toAnimalDto(): AnimalDto {
    val json = kotlinx.serialization.json.Json
    return AnimalDto(
        id = this[Animals.id],
        farmId = this[Animals.farmId],
        name = this[Animals.name],
        type = this[Animals.type],
        breed = this[Animals.breed],
        status = this[Animals.status],
        sex = this[Animals.sex],
        tagNumber = this[Animals.tagNumber],
        birthDate = this[Animals.birthDate],
        weightKg = this[Animals.weightKg],
        groupIds = json.decodeFromString(this[Animals.groupIds]),
        motherId = this[Animals.motherId],
        fatherId = this[Animals.fatherId],
        offspringIds = json.decodeFromString(this[Animals.offspringIds]),
        gestationId = this[Animals.gestationId],
        version = this[Animals.version],
        updatedAt = this[Animals.updatedAt],
        deletedAt = this[Animals.deletedAt],
    )
}
