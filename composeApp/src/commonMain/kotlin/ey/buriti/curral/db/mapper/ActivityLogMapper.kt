package ey.buriti.curral.db.mapper

import ey.buriti.curral.db.entity.ActivityLogEntity
import ey.buriti.curral.dto.ActivityLogDto
import ey.buriti.curral.model.ActivityAction
import ey.buriti.curral.model.ActivityLog
import ey.buriti.curral.model.ActivityType
import ey.buriti.curral.sync.SyncStatus

fun ActivityLogEntity.toDomain(): ActivityLog = ActivityLog(
    id = id,
    type = ActivityType.valueOf(type),
    action = ActivityAction.valueOf(action),
    entityId = entityId,
    occurredAt = occurredAt,
    title = title,
    description = description,
    subjectName = subjectName,
)

fun ActivityLog.toEntity(
    farmId: String,
    syncStatus: SyncStatus = SyncStatus.PENDING,
    version: Long = 0,
    updatedAt: String = "",
): ActivityLogEntity = ActivityLogEntity(
    id = id,
    farmId = farmId,
    type = type.name,
    action = action.name,
    entityId = entityId,
    occurredAt = occurredAt,
    title = title,
    description = description,
    subjectName = subjectName,
    syncStatus = syncStatus.name,
    version = version,
    updatedAt = updatedAt,
)

fun ActivityLogEntity.toDto(): ActivityLogDto = ActivityLogDto(
    id = id,
    farmId = farmId,
    type = type,
    action = action,
    entityId = entityId,
    occurredAt = occurredAt,
    title = title,
    description = description,
    subjectName = subjectName,
    version = version,
    updatedAt = updatedAt,
)

fun ActivityLogDto.toEntity(syncStatus: SyncStatus = SyncStatus.SYNCED): ActivityLogEntity = ActivityLogEntity(
    id = id,
    farmId = farmId,
    type = type,
    action = action,
    entityId = entityId,
    occurredAt = occurredAt,
    title = title,
    description = description,
    subjectName = subjectName,
    syncStatus = syncStatus.name,
    version = version,
    updatedAt = updatedAt,
)
