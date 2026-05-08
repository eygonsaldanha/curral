package ey.buriti.curral.model

data class Gestation(
    val id: String,
    val animalId: String,
    val startDate: String,          // ISO-8601 "YYYY-MM-DD"
    val expectedBirthDate: String,  // ISO-8601 "YYYY-MM-DD"
    val notes: String = "",
    val fatherId: String? = null,
)
