package ey.buriti.curral.db.mapper

import ey.buriti.curral.db.entity.AnimalEntity
import ey.buriti.curral.model.Animal
import ey.buriti.curral.model.AnimalSex
import ey.buriti.curral.model.AnimalStatus
import ey.buriti.curral.model.AnimalType
import ey.buriti.curral.sync.SyncStatus
import kotlinx.serialization.json.Json

private val json = Json { ignoreUnknownKeys = true }

fun AnimalEntity.toDomain(): Animal = Animal(
    id = id,
    name = name,
    type = AnimalType.valueOf(type),
    breed = breed,
    status = AnimalStatus.valueOf(status),
    sex = AnimalSex.valueOf(sex),
    tagNumber = tagNumber,
    birthDate = birthDate,
    weightKg = weightKg,
    groupIds = json.decodeFromString(groupIdsJson),
    motherId = motherId,
    fatherId = fatherId,
    offspringIds = json.decodeFromString(offspringIdsJson),
    gestationId = gestationId,
)

fun Animal.toEntity(
    farmId: String,
    syncStatus: SyncStatus = SyncStatus.PENDING,
    version: Long = 0,
    updatedAt: String = "",
): AnimalEntity = AnimalEntity(
    id = id,
    farmId = farmId,
    name = name,
    type = type.name,
    breed = breed,
    status = status.name,
    sex = sex.name,
    tagNumber = tagNumber,
    birthDate = birthDate,
    weightKg = weightKg,
    groupIdsJson = json.encodeToString(groupIds),
    motherId = motherId,
    fatherId = fatherId,
    offspringIdsJson = json.encodeToString(offspringIds),
    gestationId = gestationId,
    syncStatus = syncStatus.name,
    version = version,
    updatedAt = updatedAt,
)
