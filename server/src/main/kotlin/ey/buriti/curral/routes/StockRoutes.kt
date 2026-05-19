package ey.buriti.curral.routes

import ey.buriti.curral.db.StockItems
import ey.buriti.curral.dto.StockItemDto
import ey.buriti.curral.util.nowIso
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.stockRoutes() {
    route("/api/stock") {
        get {
            val farmId = call.farmId()
            val since = call.request.queryParameters["since"] ?: "1970-01-01T00:00:00Z"
            call.respond(transaction {
                StockItems.selectAll()
                    .where { (StockItems.farmId eq farmId) and (StockItems.updatedAt greaterEq since) }
                    .map { it.toDto() }
            })
        }

        post {
            val farmId = call.farmId()
            val dto = call.receive<StockItemDto>()
            val now = nowIso()
            transaction {
                StockItems.upsert {
                    it[StockItems.id] = dto.id
                    it[StockItems.farmId] = farmId
                    it[StockItems.name] = dto.name
                    it[StockItems.category] = dto.category
                    it[StockItems.quantity] = dto.quantity
                    it[StockItems.unit] = dto.unit
                    it[StockItems.expiryDate] = dto.expiryDate
                    it[StockItems.lowStockThreshold] = dto.lowStockThreshold
                    it[StockItems.version] = dto.version + 1
                    it[StockItems.updatedAt] = now
                }
            }
            call.respond(HttpStatusCode.Created, dto.copy(version = dto.version + 1, updatedAt = now))
        }

        put("/{id}") {
            val farmId = call.farmId()
            val id = call.parameters["id"]!!
            val dto = call.receive<StockItemDto>()
            val now = nowIso()
            transaction {
                StockItems.update({ (StockItems.id eq id) and (StockItems.farmId eq farmId) }) {
                    it[StockItems.name] = dto.name
                    it[StockItems.category] = dto.category
                    it[StockItems.quantity] = dto.quantity
                    it[StockItems.unit] = dto.unit
                    it[StockItems.expiryDate] = dto.expiryDate
                    it[StockItems.lowStockThreshold] = dto.lowStockThreshold
                    it[StockItems.version] = dto.version + 1
                    it[StockItems.updatedAt] = now
                }
            }
            call.respond(dto.copy(version = dto.version + 1, updatedAt = now))
        }

        delete("/{id}") {
            val farmId = call.farmId()
            val id = call.parameters["id"]!!
            val now = nowIso()
            transaction {
                StockItems.update({ (StockItems.id eq id) and (StockItems.farmId eq farmId) }) {
                    it[StockItems.deletedAt] = now
                    it[StockItems.updatedAt] = now
                }
            }
            call.respond(HttpStatusCode.NoContent)
        }
    }
}

private fun ResultRow.toDto() = StockItemDto(
    id = this[StockItems.id],
    farmId = this[StockItems.farmId],
    name = this[StockItems.name],
    category = this[StockItems.category],
    quantity = this[StockItems.quantity],
    unit = this[StockItems.unit],
    expiryDate = this[StockItems.expiryDate],
    lowStockThreshold = this[StockItems.lowStockThreshold],
    version = this[StockItems.version],
    updatedAt = this[StockItems.updatedAt],
    deletedAt = this[StockItems.deletedAt],
)
