package ey.buriti.curral.model

data class ProducaoEntry(
    val id: String,
    val productType: ProductType,
    val quantity: Double,
    val unit: String,       // "L", "kg", "und"
    val date: String,       // ISO-8601 "YYYY-MM-DD"
    val notes: String = "",
)

enum class ProductType(val label: String, val emoji: String) {
    LEITE("Leite", "🥛"),
    OVOS("Ovos", "🥚"),
    MEL("Mel", "🍯"),
    CAPIM_FENO("Capim/Feno", "🌿"),
}
