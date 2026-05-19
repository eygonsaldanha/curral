package ey.buriti.curral.dto

import kotlinx.serialization.Serializable

@Serializable
data class AnimalEventDto(
    val id: String,
    val farmId: String,
    val animalId: String,
    val type: String,
    val date: String,
    val time: String = "",
    val notes: String = "",
    val weightKg: Double? = null,
    val groupId: String? = null,
    val version: Long = 0,
    val updatedAt: String,
    val deletedAt: String? = null,
)
