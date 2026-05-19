package ey.buriti.curral.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ey.buriti.curral.sync.SyncStatus

@Entity(tableName = "producao_entries")
data class ProducaoEntryEntity(
    @PrimaryKey val id: String,
    val farmId: String,
    val productType: String,
    val quantity: Double,
    val unit: String,
    val date: String,
    val notes: String = "",
    val syncStatus: String = SyncStatus.PENDING.name,
    val version: Long = 0,
    val updatedAt: String = "",
    val deletedAt: String? = null,
)
