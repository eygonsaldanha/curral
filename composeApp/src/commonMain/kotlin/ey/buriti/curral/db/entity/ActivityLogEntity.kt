package ey.buriti.curral.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ey.buriti.curral.sync.SyncStatus

@Entity(tableName = "activity_logs")
data class ActivityLogEntity(
    @PrimaryKey val id: String,
    val farmId: String,
    val type: String,
    val action: String,
    val entityId: String?,
    val occurredAt: String,
    val title: String,
    val description: String = "",
    val subjectName: String = "",
    val syncStatus: String = SyncStatus.PENDING.name,
    val version: Long = 0,
    val updatedAt: String = "",
)
