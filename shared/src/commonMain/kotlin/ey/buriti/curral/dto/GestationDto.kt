package ey.buriti.curral.dto

import kotlinx.serialization.Serializable

@Serializable
data class GestationDto(
    val id: String,
    val farmId: String,
    val animalId: String,
    val startDate: String,
    val expectedBirthDate: String,
    val notes: String = "",
    val fatherId: String? = null,
    val version: Long = 0,
    val updatedAt: String,
    val deletedAt: String? = null,
)
