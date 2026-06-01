package ey.buriti.curral.sync

import ey.buriti.curral.api.IApiService
import ey.buriti.curral.db.CurralDatabase
import ey.buriti.curral.db.mapper.toDomain
import ey.buriti.curral.db.mapper.toEntity
import ey.buriti.curral.dto.SyncPushPayload
import ey.buriti.curral.dto.AnimalDto
import ey.buriti.curral.dto.AnimalGroupDto
import ey.buriti.curral.dto.AnimalEventDto
import ey.buriti.curral.dto.GestationDto
import ey.buriti.curral.dto.ProducaoEntryDto
import ey.buriti.curral.dto.StockItemDto
import ey.buriti.curral.util.nowIso
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

class SyncEngine(
    private val db: CurralDatabase,
    private val api: IApiService,
    private val farmIdProvider: () -> String,
) {
    private val animalDao get() = db.animalDao()
    private val groupDao get() = db.animalGroupDao()
    private val eventDao get() = db.animalEventDao()
    private val gestationDao get() = db.gestationDao()
    private val producaoDao get() = db.producaoEntryDao()
    private val stockDao get() = db.stockItemDao()

    /**
     * Executa um ciclo completo de sincronização:
     * 1. Coleta todas as entidades PENDING e envia ao servidor (push).
     * 2. Atualiza version/updatedAt localmente com base na resposta.
     * 3. Baixa alterações do servidor desde o último sync (pull).
     */
    suspend fun sync(lastSyncAt: String = "1970-01-01T00:00:00Z") = withContext(Dispatchers.IO) {
        try {
            push()
            pull(since = lastSyncAt)
        } catch (e: Exception) {
            // Sync falhou — tentará novamente na próxima execução
            throw e
        }
    }

    private suspend fun push() {
        val farmId = farmIdProvider()
        val pendingAnimals = animalDao.getPending(farmId).map { entity ->
            val d = entity.toDomain()
            AnimalDto(
                id = entity.id, farmId = entity.farmId, name = d.name, type = d.type.name,
                breed = d.breed, status = d.status.name, sex = d.sex.name,
                tagNumber = d.tagNumber, birthDate = d.birthDate, weightKg = d.weightKg,
                groupIds = d.groupIds, motherId = d.motherId, fatherId = d.fatherId,
                offspringIds = d.offspringIds, gestationId = d.gestationId,
                version = entity.version, updatedAt = entity.updatedAt,
                deletedAt = entity.deletedAt,
            )
        }
        val pendingGroups = groupDao.getPending(farmId).map { entity ->
            val d = entity.toDomain()
            AnimalGroupDto(
                id = entity.id, farmId = entity.farmId, name = d.name,
                description = d.description, animalIds = d.animalIds,
                version = entity.version, updatedAt = entity.updatedAt,
                deletedAt = entity.deletedAt,
            )
        }
        val pendingEvents = eventDao.getPending(farmId).map { entity ->
            val d = entity.toDomain()
            AnimalEventDto(
                id = entity.id, farmId = entity.farmId, animalId = d.animalId,
                type = d.type.name, date = d.date, time = d.time, notes = d.notes,
                weightKg = d.weightKg, groupId = d.groupId,
                version = entity.version, updatedAt = entity.updatedAt,
                deletedAt = entity.deletedAt,
            )
        }
        val pendingGestations = gestationDao.getPending(farmId).map { entity ->
            val d = entity.toDomain()
            GestationDto(
                id = entity.id, farmId = entity.farmId, animalId = d.animalId,
                startDate = d.startDate, expectedBirthDate = d.expectedBirthDate,
                notes = d.notes, fatherId = d.fatherId,
                version = entity.version, updatedAt = entity.updatedAt,
                deletedAt = entity.deletedAt,
            )
        }
        val pendingProducao = producaoDao.getPending(farmId).map { entity ->
            val d = entity.toDomain()
            ProducaoEntryDto(
                id = entity.id, farmId = entity.farmId, productType = d.productType.name,
                quantity = d.quantity, unit = d.unit, date = d.date, notes = d.notes,
                version = entity.version, updatedAt = entity.updatedAt,
                deletedAt = entity.deletedAt,
            )
        }
        val pendingStock = stockDao.getPending(farmId).map { entity ->
            val d = entity.toDomain()
            StockItemDto(
                id = entity.id, farmId = entity.farmId, name = d.name,
                category = d.category.name, quantity = d.quantity, unit = d.unit,
                expiryDate = d.expiryDate, lowStockThreshold = d.lowStockThreshold,
                version = entity.version, updatedAt = entity.updatedAt,
                deletedAt = entity.deletedAt,
            )
        }

        if (listOf(pendingAnimals, pendingGroups, pendingEvents, pendingGestations, pendingProducao, pendingStock)
                .all { it.isEmpty() }) return

        val response = api.pushSync(
            SyncPushPayload(
                animals = pendingAnimals,
                groups = pendingGroups,
                events = pendingEvents,
                gestations = pendingGestations,
                producao = pendingProducao,
                stock = pendingStock,
            )
        )

        // Marca como SYNCED com a versão retornada pelo servidor
        response.animals.forEach { animalDao.markSynced(it.id, it.version, it.updatedAt) }
        response.groups.forEach { groupDao.markSynced(it.id, it.version, it.updatedAt) }
        response.events.forEach { eventDao.markSynced(it.id, it.version, it.updatedAt) }
        response.gestations.forEach { gestationDao.markSynced(it.id, it.version, it.updatedAt) }
        response.producao.forEach { producaoDao.markSynced(it.id, it.version, it.updatedAt) }
        response.stock.forEach { stockDao.markSynced(it.id, it.version, it.updatedAt) }
    }

    private suspend fun pull(since: String) {
        val response = api.pullSync(since)
        val now = nowIso()

        response.animals.forEach { dto ->
            animalDao.upsert(
                dto.toDomain().toEntity(
                    farmId = dto.farmId,
                    syncStatus = SyncStatus.SYNCED,
                    version = dto.version,
                    updatedAt = dto.updatedAt,
                ).copy(deletedAt = dto.deletedAt)
            )
        }
        response.groups.forEach { dto ->
            groupDao.upsert(
                dto.toDomain().toEntity(
                    farmId = dto.farmId,
                    syncStatus = SyncStatus.SYNCED,
                    version = dto.version,
                    updatedAt = dto.updatedAt,
                ).copy(deletedAt = dto.deletedAt)
            )
        }
        response.events.forEach { dto ->
            eventDao.upsert(
                dto.toDomain().toEntity(
                    farmId = dto.farmId,
                    syncStatus = SyncStatus.SYNCED,
                    version = dto.version,
                    updatedAt = dto.updatedAt,
                ).copy(deletedAt = dto.deletedAt)
            )
        }
        response.gestations.forEach { dto ->
            gestationDao.upsert(
                dto.toDomain().toEntity(
                    farmId = dto.farmId,
                    syncStatus = SyncStatus.SYNCED,
                    version = dto.version,
                    updatedAt = dto.updatedAt,
                ).copy(deletedAt = dto.deletedAt)
            )
        }
        response.producao.forEach { dto ->
            producaoDao.upsert(
                dto.toDomain().toEntity(
                    farmId = dto.farmId,
                    syncStatus = SyncStatus.SYNCED,
                    version = dto.version,
                    updatedAt = dto.updatedAt,
                ).copy(deletedAt = dto.deletedAt)
            )
        }
        response.stock.forEach { dto ->
            stockDao.upsert(
                dto.toDomain().toEntity(
                    farmId = dto.farmId,
                    syncStatus = SyncStatus.SYNCED,
                    version = dto.version,
                    updatedAt = dto.updatedAt,
                ).copy(deletedAt = dto.deletedAt)
            )
        }
    }
}
