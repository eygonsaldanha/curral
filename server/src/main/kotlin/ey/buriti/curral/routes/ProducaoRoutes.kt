package ey.buriti.curral.routes

import ey.buriti.curral.db.ProducaoEntries
import ey.buriti.curral.dto.ProducaoEntryDto
import ey.buriti.curral.util.nowIso
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.producaoRoutes() {
    route("/api/producao") {
        get {
            val farmId = call.farmId()
            val since = call.request.queryParameters["since"] ?: "1970-01-01T00:00:00Z"
            call.respond(transaction {
                ProducaoEntries.selectAll()
                    .where { (ProducaoEntries.farmId eq farmId) and (ProducaoEntries.updatedAt greaterEq since) }
                    .map { it.toDto() }
            })
        }

        post {
            val farmId = call.farmId()
            val dto = call.receive<ProducaoEntryDto>()
            val now = nowIso()
            transaction {
                ProducaoEntries.upsert {
                    it[ProducaoEntries.id] = dto.id
                    it[ProducaoEntries.farmId] = farmId
                    it[ProducaoEntries.productType] = dto.productType
                    it[ProducaoEntries.quantity] = dto.quantity
                    it[ProducaoEntries.unit] = dto.unit
                    it[ProducaoEntries.date] = dto.date
                    it[ProducaoEntries.notes] = dto.notes
                    it[ProducaoEntries.version] = dto.version + 1
                    it[ProducaoEntries.updatedAt] = now
                }
            }
            call.respond(HttpStatusCode.Created, dto.copy(version = dto.version + 1, updatedAt = now))
        }

        delete("/{id}") {
            val farmId = call.farmId()
            val id = call.parameters["id"]!!
            val now = nowIso()
            transaction {
                ProducaoEntries.update({ (ProducaoEntries.id eq id) and (ProducaoEntries.farmId eq farmId) }) {
                    it[ProducaoEntries.deletedAt] = now
                    it[ProducaoEntries.updatedAt] = now
                }
            }
            call.respond(HttpStatusCode.NoContent)
        }

        put("/{id}") {
            val farmId = call.farmId()
            val id = call.parameters["id"]!!
            val dto = call.receive<ProducaoEntryDto>()
            val now = nowIso()
            val updated = transaction {
                ProducaoEntries.update({ (ProducaoEntries.id eq id) and (ProducaoEntries.farmId eq farmId) }) {
                    it[ProducaoEntries.productType] = dto.productType
                    it[ProducaoEntries.quantity] = dto.quantity
                    it[ProducaoEntries.unit] = dto.unit
                    it[ProducaoEntries.date] = dto.date
                    it[ProducaoEntries.notes] = dto.notes
                    it[ProducaoEntries.version] = dto.version + 1
                    it[ProducaoEntries.updatedAt] = now
                }
            }
            if (updated == 0) call.respond(HttpStatusCode.NotFound)
            else call.respond(dto.copy(version = dto.version + 1, updatedAt = now))
        }
    }
}

private fun ResultRow.toDto() = ProducaoEntryDto(
    id = this[ProducaoEntries.id],
    farmId = this[ProducaoEntries.farmId],
    productType = this[ProducaoEntries.productType],
    quantity = this[ProducaoEntries.quantity],
    unit = this[ProducaoEntries.unit],
    date = this[ProducaoEntries.date],
    notes = this[ProducaoEntries.notes],
    version = this[ProducaoEntries.version],
    updatedAt = this[ProducaoEntries.updatedAt],
    deletedAt = this[ProducaoEntries.deletedAt],
)
