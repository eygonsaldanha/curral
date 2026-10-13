package ey.buriti.curral.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import ey.buriti.curral.db.entity.ActivityLogEntity
import ey.buriti.curral.sync.SyncStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityLogDao {
    @Query("SELECT * FROM activity_logs WHERE farmId = :farmId ORDER BY occurredAt DESC")
    fun getAll(farmId: String): Flow<List<ActivityLogEntity>>

    @Query("SELECT * FROM activity_logs WHERE syncStatus = :status AND farmId = :farmId")
    suspend fun getPending(farmId: String, status: String = SyncStatus.PENDING.name): List<ActivityLogEntity>

    @Upsert
    suspend fun upsert(entity: ActivityLogEntity)

    @Upsert
    suspend fun upsertAll(entities: List<ActivityLogEntity>)

    @Query("UPDATE activity_logs SET syncStatus = :synced, version = :version, updatedAt = :updatedAt WHERE id = :id")
    suspend fun markSynced(
        id: String,
        version: Long,
        updatedAt: String,
        synced: String = SyncStatus.SYNCED.name,
    )
}
