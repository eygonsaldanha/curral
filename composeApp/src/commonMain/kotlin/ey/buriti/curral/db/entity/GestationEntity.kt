package ey.buriti.curral.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ey.buriti.curral.sync.SyncStatus

@Entity(tableName = "gestations")
data class GestationEntity(
    @PrimaryKey val id: String,
    val farmId: String,
    val animalId: String,
    val startDate: String,
    val expectedBirthDate: String,
    val notes: String = "",
    val fatherId: String? = null,
    val syncStatus: String = SyncStatus.PENDING.name,
    val version: Long = 0,
    val updatedAt: String = "",
    val deletedAt: String? = null,
)
