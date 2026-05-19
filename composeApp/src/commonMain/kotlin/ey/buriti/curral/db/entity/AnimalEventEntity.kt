package ey.buriti.curral.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ey.buriti.curral.sync.SyncStatus

@Entity(tableName = "animal_events")
data class AnimalEventEntity(
    @PrimaryKey val id: String,
    val farmId: String,
    val animalId: String,
    val type: String,
    val date: String,
    val time: String = "",
    val notes: String = "",
    val weightKg: Double? = null,
    val groupId: String? = null,
    val syncStatus: String = SyncStatus.PENDING.name,
    val version: Long = 0,
    val updatedAt: String = "",
    val deletedAt: String? = null,
)
