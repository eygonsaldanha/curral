package ey.buriti.curral.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import ey.buriti.curral.sync.SyncStatus

@Entity(tableName = "animals")
data class AnimalEntity(
    @PrimaryKey val id: String,
    val farmId: String,
    val name: String,
    val type: String,
    val breed: String,
    val status: String,
    val sex: String,
    val tagNumber: String,
    val birthDate: String,
    val weightKg: Double,
    /** JSON array de IDs — ex: ["g1","g2"] */
    val groupIdsJson: String = "[]",
    val motherId: String? = null,
    val fatherId: String? = null,
    /** JSON array de IDs */
    val offspringIdsJson: String = "[]",
    val gestationId: String? = null,
    val syncStatus: String = SyncStatus.PENDING.name,
    val version: Long = 0,
    val updatedAt: String = "",
    val deletedAt: String? = null,
)
