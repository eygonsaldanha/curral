package ey.buriti.curral.dto

import kotlinx.serialization.Serializable

@Serializable
data class ProducaoEntryDto(
    val id: String,
    val farmId: String,
    val productType: String,
    val quantity: Double,
    val unit: String,
    val date: String,
    val notes: String = "",
    val version: Long = 0,
    val updatedAt: String,
    val deletedAt: String? = null,
)
