package ey.buriti.curral.db.mapper

import ey.buriti.curral.db.entity.GestationEntity
import ey.buriti.curral.model.Gestation
import ey.buriti.curral.sync.SyncStatus

fun GestationEntity.toDomain(): Gestation = Gestation(
    id = id,
    animalId = animalId,
    startDate = startDate,
    expectedBirthDate = expectedBirthDate,
    notes = notes,
    fatherId = fatherId,
)

fun Gestation.toEntity(
    farmId: String,
    syncStatus: SyncStatus = SyncStatus.PENDING,
    version: Long = 0,
    updatedAt: String = "",
): GestationEntity = GestationEntity(
    id = id,
    farmId = farmId,
    animalId = animalId,
    startDate = startDate,
    expectedBirthDate = expectedBirthDate,
    notes = notes,
    fatherId = fatherId,
    syncStatus = syncStatus.name,
    version = version,
    updatedAt = updatedAt,
)
