package ey.buriti.curral.data

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import ey.buriti.curral.model.StockCategory
import ey.buriti.curral.model.StockItem

object StockRepository {

    private var nextId = 100

    val items: SnapshotStateList<StockItem> = mutableStateListOf<StockItem>().also { list ->
        list.addAll(listOf(
        // ── Ração ───────────────────────────────────────────────────────────
        StockItem(
            id = "s1", name = "Ração para Gado",
            category = StockCategory.RACAO,
            quantity = 150, unit = "kg",
            lowStockThreshold = 200,
        ),
        StockItem(
            id = "s2", name = "Ração para Galinhas",
            category = StockCategory.RACAO,
            quantity = 80, unit = "kg",
            expiryDate = "2026-06-15",
        ),
        StockItem(
            id = "s3", name = "Milho",
            category = StockCategory.RACAO,
            quantity = 500, unit = "kg",
        ),
        // ── Ferramentas ────────────────────────────────────────────────────
        StockItem(
            id = "s4", name = "Pá",
            category = StockCategory.FERRAMENTAS,
            quantity = 5, unit = "un",
        ),
        StockItem(
            id = "s5", name = "Enxada",
            category = StockCategory.FERRAMENTAS,
            quantity = 3, unit = "un",
        ),
        StockItem(
            id = "s6", name = "Motosserra",
            category = StockCategory.FERRAMENTAS,
            quantity = 1, unit = "un",
        ),
        // ── Remédios ───────────────────────────────────────────────────────
        StockItem(
            id = "s7", name = "Antibiótico Bovino",
            category = StockCategory.REMEDIOS,
            quantity = 10, unit = "doses",
            expiryDate = "2025-08-09",
        ),
        StockItem(
            id = "s8", name = "Vermífugo",
            category = StockCategory.REMEDIOS,
            quantity = 25, unit = "doses",
            expiryDate = "2027-03-14",
        ),
    ))
    }

    fun addItem(item: StockItem) { items.add(item) }
    fun generateId(): String = "s${nextId++}"

    fun increment(id: String) {
        val idx = items.indexOfFirst { it.id == id }
        if (idx >= 0) items[idx] = items[idx].copy(quantity = items[idx].quantity + 1)
    }

    fun decrement(id: String) {
        val idx = items.indexOfFirst { it.id == id }
        if (idx >= 0 && items[idx].quantity > 0)
            items[idx] = items[idx].copy(quantity = items[idx].quantity - 1)
    }

    fun remove(id: String) {
        items.removeAll { it.id == id }
    }
}
