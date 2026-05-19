package ey.buriti.curral.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import ey.buriti.curral.db.entity.StockItemEntity
import ey.buriti.curral.sync.SyncStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface StockItemDao {

    @Query("SELECT * FROM stock_items WHERE farmId = :farmId AND deletedAt IS NULL ORDER BY name ASC")
    fun getAll(farmId: String): Flow<List<StockItemEntity>>

    @Query("SELECT * FROM stock_items WHERE id = :id AND deletedAt IS NULL")
    fun getById(id: String): Flow<StockItemEntity?>

    @Upsert
    suspend fun upsert(entity: StockItemEntity)

    @Upsert
    suspend fun upsertAll(entities: List<StockItemEntity>)

    @Query("SELECT * FROM stock_items WHERE syncStatus = :status AND farmId = :farmId")
    suspend fun getPending(
        farmId: String,
        status: String = SyncStatus.PENDING.name,
    ): List<StockItemEntity>

    @Query("SELECT * FROM stock_items WHERE deletedAt IS NOT NULL AND farmId = :farmId")
    suspend fun getDeleted(farmId: String): List<StockItemEntity>

    @Query("UPDATE stock_items SET syncStatus = :synced, version = :version, updatedAt = :updatedAt WHERE id = :id")
    suspend fun markSynced(
        id: String,
        version: Long,
        updatedAt: String,
        synced: String = SyncStatus.SYNCED.name,
    )

    @Query("UPDATE stock_items SET deletedAt = :deletedAt, syncStatus = :pending WHERE id = :id")
    suspend fun softDelete(
        id: String,
        deletedAt: String,
        pending: String = SyncStatus.PENDING.name,
    )
}
