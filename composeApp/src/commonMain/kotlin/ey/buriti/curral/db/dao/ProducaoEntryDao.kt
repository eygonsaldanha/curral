package ey.buriti.curral.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import ey.buriti.curral.db.entity.ProducaoEntryEntity
import ey.buriti.curral.sync.SyncStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface ProducaoEntryDao {

    @Query("SELECT * FROM producao_entries WHERE farmId = :farmId AND deletedAt IS NULL ORDER BY date DESC")
    fun getAll(farmId: String): Flow<List<ProducaoEntryEntity>>

    @Upsert
    suspend fun upsert(entity: ProducaoEntryEntity)

    @Upsert
    suspend fun upsertAll(entities: List<ProducaoEntryEntity>)

    @Query("SELECT * FROM producao_entries WHERE syncStatus = :status AND farmId = :farmId")
    suspend fun getPending(
        farmId: String,
        status: String = SyncStatus.PENDING.name,
    ): List<ProducaoEntryEntity>

    @Query("SELECT * FROM producao_entries WHERE deletedAt IS NOT NULL AND farmId = :farmId")
    suspend fun getDeleted(farmId: String): List<ProducaoEntryEntity>

    @Query("UPDATE producao_entries SET syncStatus = :synced, version = :version, updatedAt = :updatedAt WHERE id = :id")
    suspend fun markSynced(
        id: String,
        version: Long,
        updatedAt: String,
        synced: String = SyncStatus.SYNCED.name,
    )

    @Query("UPDATE producao_entries SET deletedAt = :deletedAt, syncStatus = :pending WHERE id = :id")
    suspend fun softDelete(
        id: String,
        deletedAt: String,
        pending: String = SyncStatus.PENDING.name,
    )
}
