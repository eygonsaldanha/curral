package ey.buriti.curral.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import ey.buriti.curral.db.entity.AnimalEntity
import ey.buriti.curral.sync.SyncStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimalDao {

    @Query("SELECT * FROM animals WHERE farmId = :farmId AND deletedAt IS NULL ORDER BY name ASC")
    fun getAll(farmId: String): Flow<List<AnimalEntity>>

    @Query("SELECT * FROM animals WHERE id = :id AND deletedAt IS NULL")
    fun getById(id: String): Flow<AnimalEntity?>

    @Upsert
    suspend fun upsert(entity: AnimalEntity)

    @Upsert
    suspend fun upsertAll(entities: List<AnimalEntity>)

    @Query("SELECT * FROM animals WHERE syncStatus = :status AND farmId = :farmId")
    suspend fun getPending(
        farmId: String,
        status: String = SyncStatus.PENDING.name,
    ): List<AnimalEntity>

    @Query("SELECT * FROM animals WHERE deletedAt IS NOT NULL AND farmId = :farmId")
    suspend fun getDeleted(farmId: String): List<AnimalEntity>

    @Query("UPDATE animals SET syncStatus = :synced, version = :version, updatedAt = :updatedAt WHERE id = :id")
    suspend fun markSynced(
        id: String,
        version: Long,
        updatedAt: String,
        synced: String = SyncStatus.SYNCED.name,
    )

    @Query("UPDATE animals SET deletedAt = :deletedAt, syncStatus = :pending WHERE id = :id")
    suspend fun softDelete(
        id: String,
        deletedAt: String,
        pending: String = SyncStatus.PENDING.name,
    )
}
