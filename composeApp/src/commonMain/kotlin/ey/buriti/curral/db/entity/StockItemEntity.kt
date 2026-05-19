package ey.buriti.curral.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ey.buriti.curral.sync.SyncStatus

@Entity(tableName = "stock_items")
data class StockItemEntity(
    @PrimaryKey val id: String,
    val farmId: String,
    val name: String,
    val category: String,
    val quantity: Int,
    val unit: String,
    val expiryDate: String? = null,
    val lowStockThreshold: Int? = null,
    val syncStatus: String = SyncStatus.PENDING.name,
    val version: Long = 0,
    val updatedAt: String = "",
    val deletedAt: String? = null,
)
