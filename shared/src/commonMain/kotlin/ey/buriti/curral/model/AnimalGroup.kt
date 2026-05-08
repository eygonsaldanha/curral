package ey.buriti.curral.model

data class AnimalGroup(
    val id: String,
    val name: String,
    val description: String,
    val animalIds: List<String> = emptyList(),
)
