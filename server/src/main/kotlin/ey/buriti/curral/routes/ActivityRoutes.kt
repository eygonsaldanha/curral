package ey.buriti.curral.routes

import ey.buriti.curral.db.ActivityLogs
import ey.buriti.curral.dto.ActivityLogDto
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.activityRoutes() {
    route("/api/activities") {
        get {
            val farmId = call.requireFarmIdOrRespond() ?: return@get
            val since = call.request.queryParameters["since"] ?: "1970-01-01T00:00:00Z"
            call.respond(transaction {
                ActivityLogs.selectAll()
                    .where { (ActivityLogs.farmId eq farmId) and (ActivityLogs.updatedAt greaterEq since) }
                    .orderBy(ActivityLogs.occurredAt to org.jetbrains.exposed.sql.SortOrder.DESC)
                    .map(ResultRow::toActivityDto)
            })
        }
    }
}

private fun ResultRow.toActivityDto() = ActivityLogDto(
    id = this[ActivityLogs.id], farmId = this[ActivityLogs.farmId],
    type = this[ActivityLogs.type], action = this[ActivityLogs.action],
    entityId = this[ActivityLogs.entityId], occurredAt = this[ActivityLogs.occurredAt],
    title = this[ActivityLogs.title], description = this[ActivityLogs.description],
    subjectName = this[ActivityLogs.subjectName], version = this[ActivityLogs.version],
    updatedAt = this[ActivityLogs.updatedAt],
)
