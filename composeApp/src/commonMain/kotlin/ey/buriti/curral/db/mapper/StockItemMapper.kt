package ey.buriti.curral.db.mapper

import ey.buriti.curral.db.entity.StockItemEntity
import ey.buriti.curral.model.StockCategory
import ey.buriti.curral.model.StockItem
import ey.buriti.curral.sync.SyncStatus

fun StockItemEntity.toDomain(): StockItem = StockItem(
    id = id,
    name = name,
    category = StockCategory.valueOf(category),
    quantity = quantity,
    unit = unit,
    expiryDate = expiryDate,
    lowStockThreshold = lowStockThreshold,
)

fun StockItem.toEntity(
    farmId: String,
    syncStatus: SyncStatus = SyncStatus.PENDING,
    version: Long = 0,
    updatedAt: String = "",
): StockItemEntity = StockItemEntity(
    id = id,
    farmId = farmId,
    name = name,
    category = category.name,
    quantity = quantity,
    unit = unit,
    expiryDate = expiryDate,
    lowStockThreshold = lowStockThreshold,
    syncStatus = syncStatus.name,
    version = version,
    updatedAt = updatedAt,
)
