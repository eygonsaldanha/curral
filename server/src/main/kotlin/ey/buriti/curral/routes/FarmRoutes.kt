package ey.buriti.curral.routes

import ey.buriti.curral.db.Farms
import ey.buriti.curral.db.UserFarms
import ey.buriti.curral.dto.CreateFarmRequest
import ey.buriti.curral.dto.FarmDto
import ey.buriti.curral.util.nowIso
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import org.jetbrains.exposed.sql.SortOrder
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.JoinType
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.insert
import java.util.UUID

fun Route.farmRoutes() {
    route("/api/farms") {
        get {
            val userId = call.userId()
            val farms = transaction {
                Farms.join(
                    UserFarms,
                    JoinType.INNER,
                    onColumn = Farms.id,
                    otherColumn = UserFarms.farmId,
                )
                    .selectAll()
                    .where { UserFarms.userId eq userId }
                    .orderBy(UserFarms.createdAt to SortOrder.ASC)
                    .map { row ->
                        FarmDto(
                            id = row[Farms.id],
                            name = row[Farms.name],
                            ownerUserId = row[Farms.ownerUserId],
                            createdAt = row[Farms.createdAt],
                            updatedAt = row[Farms.updatedAt],
                        )
                    }
            }
            call.respond(farms)
        }

        post {
            val userId = call.userId()
            val payload = call.receive<CreateFarmRequest>()
            val farmName = payload.name.trim()
            if (farmName.isBlank()) {
                call.respond(HttpStatusCode.BadRequest, mapOf("error" to "Informe o nome da fazenda"))
                return@post
            }

            val now = nowIso()
            val farm = transaction {
                val farmId = "farm-${UUID.randomUUID()}"
                Farms.insert {
                    it[Farms.id] = farmId
                    it[Farms.name] = farmName
                    it[Farms.ownerUserId] = userId
                    it[Farms.createdAt] = now
                    it[Farms.updatedAt] = now
                }
                UserFarms.insert {
                    it[UserFarms.userId] = userId
                    it[UserFarms.farmId] = farmId
                    it[UserFarms.role] = "owner"
                    it[UserFarms.createdAt] = now
                }

                FarmDto(
                    id = farmId,
                    name = farmName,
                    ownerUserId = userId,
                    createdAt = now,
                    updatedAt = now,
                )
            }

            call.respond(HttpStatusCode.Created, farm)
        }
    }
}
