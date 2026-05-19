package ey.buriti.curral.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ey.buriti.curral.sync.SyncStatus

@Entity(tableName = "animal_groups")
data class AnimalGroupEntity(
    @PrimaryKey val id: String,
    val farmId: String,
    val name: String,
    val description: String,
    /** JSON array de IDs */
    val animalIdsJson: String = "[]",
    val syncStatus: String = SyncStatus.PENDING.name,
    val version: Long = 0,
    val updatedAt: String = "",
    val deletedAt: String? = null,
)
