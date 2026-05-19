package ey.buriti.curral.db.mapper

import ey.buriti.curral.db.entity.AnimalGroupEntity
import ey.buriti.curral.model.AnimalGroup
import ey.buriti.curral.sync.SyncStatus
import kotlinx.serialization.json.Json

private val json = Json { ignoreUnknownKeys = true }

fun AnimalGroupEntity.toDomain(): AnimalGroup = AnimalGroup(
    id = id,
    name = name,
    description = description,
    animalIds = json.decodeFromString(animalIdsJson),
)

fun AnimalGroup.toEntity(
    farmId: String,
    syncStatus: SyncStatus = SyncStatus.PENDING,
    version: Long = 0,
    updatedAt: String = "",
): AnimalGroupEntity = AnimalGroupEntity(
    id = id,
    farmId = farmId,
    name = name,
    description = description,
    animalIdsJson = json.encodeToString(animalIds),
    syncStatus = syncStatus.name,
    version = version,
    updatedAt = updatedAt,
)
