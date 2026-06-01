package ey.buriti.curral.routes

import ey.buriti.curral.db.*
import ey.buriti.curral.dto.*
import ey.buriti.curral.util.nowIso
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.serialization.json.Json
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction

fun Route.syncRoutes() {
    /**
     * POST /api/sync/push — recebe pendências do mobile, persiste e retorna versões atualizadas
     */
    post("/api/sync/push") {
        val farmId = call.requireFarmIdOrRespond() ?: return@post
        val payload = call.receive<SyncPushPayload>()
        val now = nowIso()

        val savedAnimals = mutableListOf<AnimalDto>()
        val savedGroups = mutableListOf<AnimalGroupDto>()
        val savedEvents = mutableListOf<AnimalEventDto>()
        val savedGestations = mutableListOf<GestationDto>()
        val savedProducao = mutableListOf<ProducaoEntryDto>()
        val savedStock = mutableListOf<StockItemDto>()

        transaction {
            payload.animals.forEach { dto ->
                val newVersion = dto.version + 1
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
                    it[Animals.groupIds] = Json.encodeToString(dto.groupIds)
                    it[Animals.motherId] = dto.motherId
                    it[Animals.fatherId] = dto.fatherId
                    it[Animals.offspringIds] = Json.encodeToString(dto.offspringIds)
                    it[Animals.gestationId] = dto.gestationId
                    it[Animals.version] = newVersion
                    it[Animals.updatedAt] = now
                    it[Animals.deletedAt] = dto.deletedAt
                }
                savedAnimals.add(dto.copy(version = newVersion, updatedAt = now))
            }

            payload.groups.forEach { dto ->
                val newVersion = dto.version + 1
                AnimalGroups.upsert {
                    it[AnimalGroups.id] = dto.id
                    it[AnimalGroups.farmId] = farmId
                    it[AnimalGroups.name] = dto.name
                    it[AnimalGroups.description] = dto.description
                    it[AnimalGroups.animalIds] = Json.encodeToString(dto.animalIds)
                    it[AnimalGroups.version] = newVersion
                    it[AnimalGroups.updatedAt] = now
                    it[AnimalGroups.deletedAt] = dto.deletedAt
                }
                savedGroups.add(dto.copy(version = newVersion, updatedAt = now))
            }

            payload.events.forEach { dto ->
                val newVersion = dto.version + 1
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
                    it[AnimalEvents.version] = newVersion
                    it[AnimalEvents.updatedAt] = now
                    it[AnimalEvents.deletedAt] = dto.deletedAt
                }
                savedEvents.add(dto.copy(version = newVersion, updatedAt = now))
            }

            payload.gestations.forEach { dto ->
                val newVersion = dto.version + 1
                Gestations.upsert {
                    it[Gestations.id] = dto.id
                    it[Gestations.farmId] = farmId
                    it[Gestations.animalId] = dto.animalId
                    it[Gestations.startDate] = dto.startDate
                    it[Gestations.expectedBirthDate] = dto.expectedBirthDate
                    it[Gestations.notes] = dto.notes
                    it[Gestations.fatherId] = dto.fatherId
                    it[Gestations.version] = newVersion
                    it[Gestations.updatedAt] = now
                    it[Gestations.deletedAt] = dto.deletedAt
                }
                savedGestations.add(dto.copy(version = newVersion, updatedAt = now))
            }

            payload.producao.forEach { dto ->
                val newVersion = dto.version + 1
                ProducaoEntries.upsert {
                    it[ProducaoEntries.id] = dto.id
                    it[ProducaoEntries.farmId] = farmId
                    it[ProducaoEntries.productType] = dto.productType
                    it[ProducaoEntries.quantity] = dto.quantity
                    it[ProducaoEntries.unit] = dto.unit
                    it[ProducaoEntries.date] = dto.date
                    it[ProducaoEntries.notes] = dto.notes
                    it[ProducaoEntries.version] = newVersion
                    it[ProducaoEntries.updatedAt] = now
                    it[ProducaoEntries.deletedAt] = dto.deletedAt
                }
                savedProducao.add(dto.copy(version = newVersion, updatedAt = now))
            }

            payload.stock.forEach { dto ->
                val newVersion = dto.version + 1
                StockItems.upsert {
                    it[StockItems.id] = dto.id
                    it[StockItems.farmId] = farmId
                    it[StockItems.name] = dto.name
                    it[StockItems.category] = dto.category
                    it[StockItems.quantity] = dto.quantity
                    it[StockItems.unit] = dto.unit
                    it[StockItems.expiryDate] = dto.expiryDate
                    it[StockItems.lowStockThreshold] = dto.lowStockThreshold
                    it[StockItems.version] = newVersion
                    it[StockItems.updatedAt] = now
                    it[StockItems.deletedAt] = dto.deletedAt
                }
                savedStock.add(dto.copy(version = newVersion, updatedAt = now))
            }
        }

        call.respond(
            SyncResponse(
                animals = savedAnimals,
                groups = savedGroups,
                events = savedEvents,
                gestations = savedGestations,
                producao = savedProducao,
                stock = savedStock,
            )
        )
    }

    /**
     * GET /api/sync/pull?since=<ISO-8601> — retorna todas as entidades atualizadas desde `since`
     */
    get("/api/sync/pull") {
        val farmId = call.requireFarmIdOrRespond() ?: return@get
        val since = call.request.queryParameters["since"] ?: "1970-01-01T00:00:00Z"

        val response = transaction {
            SyncResponse(
                animals = Animals.selectAll()
                    .where { (Animals.farmId eq farmId) and (Animals.updatedAt greaterEq since) }
                    .map { row ->
                        AnimalDto(
                            id = row[Animals.id], farmId = row[Animals.farmId],
                            name = row[Animals.name], type = row[Animals.type],
                            breed = row[Animals.breed], status = row[Animals.status],
                            sex = row[Animals.sex], tagNumber = row[Animals.tagNumber],
                            birthDate = row[Animals.birthDate], weightKg = row[Animals.weightKg],
                            groupIds = Json.decodeFromString(row[Animals.groupIds]),
                            motherId = row[Animals.motherId], fatherId = row[Animals.fatherId],
                            offspringIds = Json.decodeFromString(row[Animals.offspringIds]),
                            gestationId = row[Animals.gestationId],
                            version = row[Animals.version], updatedAt = row[Animals.updatedAt],
                            deletedAt = row[Animals.deletedAt],
                        )
                    },
                groups = AnimalGroups.selectAll()
                    .where { (AnimalGroups.farmId eq farmId) and (AnimalGroups.updatedAt greaterEq since) }
                    .map { row ->
                        AnimalGroupDto(
                            id = row[AnimalGroups.id], farmId = row[AnimalGroups.farmId],
                            name = row[AnimalGroups.name], description = row[AnimalGroups.description],
                            animalIds = Json.decodeFromString(row[AnimalGroups.animalIds]),
                            version = row[AnimalGroups.version], updatedAt = row[AnimalGroups.updatedAt],
                            deletedAt = row[AnimalGroups.deletedAt],
                        )
                    },
                events = AnimalEvents.selectAll()
                    .where { (AnimalEvents.farmId eq farmId) and (AnimalEvents.updatedAt greaterEq since) }
                    .map { row ->
                        AnimalEventDto(
                            id = row[AnimalEvents.id], farmId = row[AnimalEvents.farmId],
                            animalId = row[AnimalEvents.animalId], type = row[AnimalEvents.type],
                            date = row[AnimalEvents.date], time = row[AnimalEvents.time],
                            notes = row[AnimalEvents.notes], weightKg = row[AnimalEvents.weightKg],
                            groupId = row[AnimalEvents.groupId],
                            version = row[AnimalEvents.version], updatedAt = row[AnimalEvents.updatedAt],
                            deletedAt = row[AnimalEvents.deletedAt],
                        )
                    },
                gestations = Gestations.selectAll()
                    .where { (Gestations.farmId eq farmId) and (Gestations.updatedAt greaterEq since) }
                    .map { row ->
                        GestationDto(
                            id = row[Gestations.id], farmId = row[Gestations.farmId],
                            animalId = row[Gestations.animalId],
                            startDate = row[Gestations.startDate],
                            expectedBirthDate = row[Gestations.expectedBirthDate],
                            notes = row[Gestations.notes], fatherId = row[Gestations.fatherId],
                            version = row[Gestations.version], updatedAt = row[Gestations.updatedAt],
                            deletedAt = row[Gestations.deletedAt],
                        )
                    },
                producao = ProducaoEntries.selectAll()
                    .where { (ProducaoEntries.farmId eq farmId) and (ProducaoEntries.updatedAt greaterEq since) }
                    .map { row ->
                        ProducaoEntryDto(
                            id = row[ProducaoEntries.id], farmId = row[ProducaoEntries.farmId],
                            productType = row[ProducaoEntries.productType],
                            quantity = row[ProducaoEntries.quantity], unit = row[ProducaoEntries.unit],
                            date = row[ProducaoEntries.date], notes = row[ProducaoEntries.notes],
                            version = row[ProducaoEntries.version], updatedAt = row[ProducaoEntries.updatedAt],
                            deletedAt = row[ProducaoEntries.deletedAt],
                        )
                    },
                stock = StockItems.selectAll()
                    .where { (StockItems.farmId eq farmId) and (StockItems.updatedAt greaterEq since) }
                    .map { row ->
                        StockItemDto(
                            id = row[StockItems.id], farmId = row[StockItems.farmId],
                            name = row[StockItems.name], category = row[StockItems.category],
                            quantity = row[StockItems.quantity], unit = row[StockItems.unit],
                            expiryDate = row[StockItems.expiryDate],
                            lowStockThreshold = row[StockItems.lowStockThreshold],
                            version = row[StockItems.version], updatedAt = row[StockItems.updatedAt],
                            deletedAt = row[StockItems.deletedAt],
                        )
                    },
            )
        }
        call.respond(response)
    }
}
