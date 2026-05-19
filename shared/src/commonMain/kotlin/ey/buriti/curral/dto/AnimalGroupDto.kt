package ey.buriti.curral.dto

import kotlinx.serialization.Serializable

@Serializable
data class AnimalGroupDto(
    val id: String,
    val farmId: String,
    val name: String,
    val description: String,
    val animalIds: List<String> = emptyList(),
    val version: Long = 0,
    val updatedAt: String,
    val deletedAt: String? = null,
)
