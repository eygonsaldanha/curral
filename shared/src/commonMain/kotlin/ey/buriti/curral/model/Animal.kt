package ey.buriti.curral.model

data class Animal(
    val id: String,
    val name: String,
    val type: AnimalType,
    val breed: String,
    val status: AnimalStatus,
    val sex: AnimalSex,
    val tagNumber: String,
    val birthDate: String,          // ISO-8601 date string "YYYY-MM-DD"
    val weightKg: Double,
    val groupIds: List<String> = emptyList(),
    val motherId: String? = null,
    val fatherId: String? = null,
    val offspringIds: List<String> = emptyList(),
    val gestationId: String? = null,
)

enum class AnimalType(val label: String, val emoji: String) {
    VACA("Vaca", "🐄"),
    BOI("Boi", "🐂"),
    CAVALO("Cavalo", "🐴"),
    BODE("Bode", "🐐"),
    GALINHA("Galinha", "🐔"),
    OUTRO("Outro", "🐾"),
}

enum class AnimalStatus(val label: String) {
    SAUDAVEL("Saudável"),
    PRENHA("Prenha"),
    DOENTE("Doente"),
    VENDIDO("Vendido"),
    MORTO("Morto"),
}

enum class AnimalSex(val label: String) {
    MACHO("Macho"),
    FEMEA("Fêmea"),
}
