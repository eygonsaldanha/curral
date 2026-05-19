package ey.buriti.curral.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import ey.buriti.curral.db.entity.AnimalGroupEntity
import ey.buriti.curral.sync.SyncStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimalGroupDao {

    @Query("SELECT * FROM animal_groups WHERE farmId = :farmId AND deletedAt IS NULL ORDER BY name ASC")
    fun getAll(farmId: String): Flow<List<AnimalGroupEntity>>

    @Query("SELECT * FROM animal_groups WHERE id = :id AND deletedAt IS NULL")
    fun getById(id: String): Flow<AnimalGroupEntity?>

    @Upsert
    suspend fun upsert(entity: AnimalGroupEntity)

    @Upsert
    suspend fun upsertAll(entities: List<AnimalGroupEntity>)

    @Query("SELECT * FROM animal_groups WHERE syncStatus = :status AND farmId = :farmId")
    suspend fun getPending(
        farmId: String,
        status: String = SyncStatus.PENDING.name,
    ): List<AnimalGroupEntity>

    @Query("SELECT * FROM animal_groups WHERE deletedAt IS NOT NULL AND farmId = :farmId")
    suspend fun getDeleted(farmId: String): List<AnimalGroupEntity>

    @Query("UPDATE animal_groups SET syncStatus = :synced, version = :version, updatedAt = :updatedAt WHERE id = :id")
    suspend fun markSynced(
        id: String,
        version: Long,
        updatedAt: String,
        synced: String = SyncStatus.SYNCED.name,
    )

    @Query("UPDATE animal_groups SET deletedAt = :deletedAt, syncStatus = :pending WHERE id = :id")
    suspend fun softDelete(
        id: String,
        deletedAt: String,
        pending: String = SyncStatus.PENDING.name,
    )
}
