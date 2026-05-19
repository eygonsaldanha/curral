package ey.buriti.curral.db.mapper

import ey.buriti.curral.db.entity.ProducaoEntryEntity
import ey.buriti.curral.model.ProducaoEntry
import ey.buriti.curral.model.ProductType
import ey.buriti.curral.sync.SyncStatus

fun ProducaoEntryEntity.toDomain(): ProducaoEntry = ProducaoEntry(
    id = id,
    productType = ProductType.valueOf(productType),
    quantity = quantity,
    unit = unit,
    date = date,
    notes = notes,
)

fun ProducaoEntry.toEntity(
    farmId: String,
    syncStatus: SyncStatus = SyncStatus.PENDING,
    version: Long = 0,
    updatedAt: String = "",
): ProducaoEntryEntity = ProducaoEntryEntity(
    id = id,
    farmId = farmId,
    productType = productType.name,
    quantity = quantity,
    unit = unit,
    date = date,
    notes = notes,
    syncStatus = syncStatus.name,
    version = version,
    updatedAt = updatedAt,
)
