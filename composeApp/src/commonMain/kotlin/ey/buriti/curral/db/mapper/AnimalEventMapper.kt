package ey.buriti.curral.db.mapper

import ey.buriti.curral.db.entity.AnimalEventEntity
import ey.buriti.curral.model.AnimalEvent
import ey.buriti.curral.model.EventType
import ey.buriti.curral.sync.SyncStatus

fun AnimalEventEntity.toDomain(): AnimalEvent = AnimalEvent(
    id = id,
    animalId = animalId,
    type = EventType.valueOf(type),
    date = date,
    time = time,
    notes = notes,
    weightKg = weightKg,
    groupId = groupId,
)

fun AnimalEvent.toEntity(
    farmId: String,
    syncStatus: SyncStatus = SyncStatus.PENDING,
    version: Long = 0,
    updatedAt: String = "",
): AnimalEventEntity = AnimalEventEntity(
    id = id,
    farmId = farmId,
    animalId = animalId,
    type = type.name,
    date = date,
    time = time,
    notes = notes,
    weightKg = weightKg,
    groupId = groupId,
    syncStatus = syncStatus.name,
    version = version,
    updatedAt = updatedAt,
)
