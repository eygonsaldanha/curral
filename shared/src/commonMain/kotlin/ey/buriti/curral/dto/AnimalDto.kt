package ey.buriti.curral.dto

import kotlinx.serialization.Serializable

@Serializable
data class AnimalDto(
    val id: String,
    val farmId: String,
    val name: String,
    val type: String,
    val breed: String,
    val status: String,
    val sex: String,
    val tagNumber: String,
    val birthDate: String,
    val weightKg: Double,
    val groupIds: List<String> = emptyList(),
    val motherId: String? = null,
    val fatherId: String? = null,
    val offspringIds: List<String> = emptyList(),
    val gestationId: String? = null,
    val version: Long = 0,
    val updatedAt: String,
    val deletedAt: String? = null,
)
