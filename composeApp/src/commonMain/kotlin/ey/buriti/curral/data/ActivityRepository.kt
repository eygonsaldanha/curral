package ey.buriti.curral.data

import ey.buriti.curral.model.ActivityAction
import ey.buriti.curral.model.ActivityLog
import ey.buriti.curral.model.ActivityType
import ey.buriti.curral.db.mapper.toDomain
import ey.buriti.curral.db.mapper.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface IActivityRepository {
    fun getActivities(): Flow<List<ActivityLog>>
}

class ActivityRepositoryImpl(private val recorder: ActivityRecorder) : IActivityRepository {
    override fun getActivities(): Flow<List<ActivityLog>> = recorder.activities()
}

class ActivityRecorder(
    private val db: ey.buriti.curral.db.CurralDatabase,
    private val farmIdProvider: () -> String,
) {
    fun activities(): Flow<List<ActivityLog>> =
        db.activityLogDao().getAll(farmIdProvider()).map { rows -> rows.map { it.toDomain() } }

    suspend fun record(
        type: ActivityType,
        action: ActivityAction,
        entityId: String?,
        title: String,
        description: String = "",
        subjectName: String = "",
    ) {
        val now = ey.buriti.curral.util.nowIso()
        val safeEntity = entityId.orEmpty().ifBlank { "none" }
        val activity = ActivityLog(
            id = "act-${now.replace(":", "-")}-${type.name.lowercase()}-$safeEntity",
            type = type,
            action = action,
            entityId = entityId,
            occurredAt = now,
            title = title,
            description = description,
            subjectName = subjectName,
        )
        db.activityLogDao().upsert(activity.toEntity(farmIdProvider()))
    }
}
