package ey.buriti.curral.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import ey.buriti.curral.db.entity.AnimalEventEntity
import ey.buriti.curral.sync.SyncStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface AnimalEventDao {

    @Query("SELECT * FROM animal_events WHERE farmId = :farmId AND deletedAt IS NULL ORDER BY date DESC, time DESC")
    fun getAll(farmId: String): Flow<List<AnimalEventEntity>>

    @Query("SELECT * FROM animal_events WHERE animalId = :animalId AND farmId = :farmId AND deletedAt IS NULL ORDER BY date DESC, time DESC")
    fun getByAnimal(animalId: String, farmId: String): Flow<List<AnimalEventEntity>>

    @Query("SELECT * FROM animal_events WHERE date = :date AND farmId = :farmId AND deletedAt IS NULL ORDER BY time ASC")
    fun getByDate(date: String, farmId: String): Flow<List<AnimalEventEntity>>

    @Upsert
    suspend fun upsert(entity: AnimalEventEntity)

    @Upsert
    suspend fun upsertAll(entities: List<AnimalEventEntity>)

    @Query("SELECT * FROM animal_events WHERE syncStatus = :status AND farmId = :farmId")
    suspend fun getPending(
        farmId: String,
        status: String = SyncStatus.PENDING.name,
    ): List<AnimalEventEntity>

    @Query("SELECT * FROM animal_events WHERE deletedAt IS NOT NULL AND farmId = :farmId")
    suspend fun getDeleted(farmId: String): List<AnimalEventEntity>

    @Query("UPDATE animal_events SET syncStatus = :synced, version = :version, updatedAt = :updatedAt WHERE id = :id")
    suspend fun markSynced(
        id: String,
        version: Long,
        updatedAt: String,
        synced: String = SyncStatus.SYNCED.name,
    )

    @Query("UPDATE animal_events SET deletedAt = :deletedAt, syncStatus = :pending WHERE id = :id")
    suspend fun softDelete(
        id: String,
        deletedAt: String,
        pending: String = SyncStatus.PENDING.name,
    )
}
