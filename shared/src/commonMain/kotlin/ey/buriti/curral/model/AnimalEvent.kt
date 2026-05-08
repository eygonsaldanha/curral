package ey.buriti.curral.model

data class AnimalEvent(
    val id: String,
    val animalId: String,
    val type: EventType,
    val date: String,           // ISO-8601 date string "YYYY-MM-DD"
    val time: String = "",      // "HH:MM"
    val notes: String = "",
    val weightKg: Double? = null,
    val groupId: String? = null,
)

enum class EventType(val label: String, val emoji: String) {
    NASCIMENTO("Nascimento", "🐣"),
    NASCIMENTO_FILHOTE("Nascimento de Filhote", "🍼"),
    VACINACAO("Vacinação", "💉"),
    VISITA_VETERINARIA("Visita Veterinária", "👨‍⚕️"),
    TRATAMENTO("Tratamento", "🩺"),
    ALIMENTACAO_ESPECIAL("Alimentação Especial", "🌿"),
    CONTROLE_PESO("Controle de Peso", "⚖️"),
    VENDA("Venda", "💰"),
    OUTRO("Outro", "📋"),
}
