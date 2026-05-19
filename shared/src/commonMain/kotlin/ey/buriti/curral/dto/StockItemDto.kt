package ey.buriti.curral.dto

import kotlinx.serialization.Serializable

@Serializable
data class StockItemDto(
    val id: String,
    val farmId: String,
    val name: String,
    val category: String,
    val quantity: Int,
    val unit: String,
    val expiryDate: String? = null,
    val lowStockThreshold: Int? = null,
    val version: Long = 0,
    val updatedAt: String,
    val deletedAt: String? = null,
)
