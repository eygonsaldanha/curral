package ey.buriti.curral.model

data class StockItem(
    val id: String,
    val name: String,
    val category: StockCategory,
    val quantity: Int,
    val unit: String,
    val expiryDate: String? = null,     // ISO-8601 "YYYY-MM-DD"
    val lowStockThreshold: Int? = null, // quantity <= threshold → Baixo badge
)

enum class StockCategory(val label: String, val emoji: String) {
    RACAO("Ração", "📦"),
    FERRAMENTAS("Ferramentas", "🔧"),
    REMEDIOS("Remédios", "💊"),
    OUTROS("Outros", "📋"),
}
