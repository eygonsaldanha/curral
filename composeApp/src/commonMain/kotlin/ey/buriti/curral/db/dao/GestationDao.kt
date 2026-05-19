package ey.buriti.curral.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import ey.buriti.curral.db.entity.GestationEntity
import ey.buriti.curral.sync.SyncStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface GestationDao {

    @Query("SELECT * FROM gestations WHERE farmId = :farmId AND deletedAt IS NULL")
    fun getAll(farmId: String): Flow<List<GestationEntity>>

    @Query("SELECT * FROM gestations WHERE id = :id AND deletedAt IS NULL")
    fun getById(id: String): Flow<GestationEntity?>

    @Query("SELECT * FROM gestations WHERE animalId = :animalId AND deletedAt IS NULL")
    fun getByAnimal(animalId: String): Flow<GestationEntity?>

    @Upsert
    suspend fun upsert(entity: GestationEntity)

    @Upsert
    suspend fun upsertAll(entities: List<GestationEntity>)

    @Query("SELECT * FROM gestations WHERE syncStatus = :status AND farmId = :farmId")
    suspend fun getPending(
        farmId: String,
        status: String = SyncStatus.PENDING.name,
    ): List<GestationEntity>

    @Query("SELECT * FROM gestations WHERE deletedAt IS NOT NULL AND farmId = :farmId")
    suspend fun getDeleted(farmId: String): List<GestationEntity>

    @Query("UPDATE gestations SET syncStatus = :synced, version = :version, updatedAt = :updatedAt WHERE id = :id")
    suspend fun markSynced(
        id: String,
        version: Long,
        updatedAt: String,
        synced: String = SyncStatus.SYNCED.name,
    )

    @Query("UPDATE gestations SET deletedAt = :deletedAt, syncStatus = :pending WHERE id = :id")
    suspend fun softDelete(
        id: String,
        deletedAt: String,
        pending: String = SyncStatus.PENDING.name,
    )
}
