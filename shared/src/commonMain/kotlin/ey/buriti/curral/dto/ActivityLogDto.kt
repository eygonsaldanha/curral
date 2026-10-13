package ey.buriti.curral.dto

import kotlinx.serialization.Serializable

@Serializable
data class ActivityLogDto(
    val id: String,
    val farmId: String,
    val type: String,
    val action: String,
    val entityId: String? = null,
    val occurredAt: String,
    val title: String,
    val description: String = "",
    val subjectName: String = "",
    val version: Long = 0,
    val updatedAt: String,
)
