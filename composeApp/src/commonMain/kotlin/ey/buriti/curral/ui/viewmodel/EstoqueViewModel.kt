package ey.buriti.curral.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ey.buriti.curral.data.IStockRepository
import ey.buriti.curral.sync.SyncEngine
import ey.buriti.curral.model.StockCategory
import ey.buriti.curral.model.StockItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EstoqueViewModel(
    private val stockRepo: IStockRepository,
    private val syncEngine: SyncEngine,
) : ViewModel() {

    val items: StateFlow<List<StockItem>> = stockRepo.getItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addItem(
        name: String, category: StockCategory, quantity: Int,
        unit: String, expiryDate: String?, lowStockThreshold: Int,
    ) = viewModelScope.launch {
        val id = stockRepo.generateId()
        stockRepo.addItem(
            StockItem(
                id = id, name = name, category = category,
                quantity = quantity, unit = unit,
                expiryDate = expiryDate, lowStockThreshold = lowStockThreshold,
            )
        )
        runCatching { syncEngine.sync() }
    }

    fun updateItem(item: StockItem) = viewModelScope.launch {
        stockRepo.updateItem(item)
        runCatching { syncEngine.sync() }
    }

    fun deleteItem(id: String) = viewModelScope.launch {
        stockRepo.deleteItem(id)
        runCatching { syncEngine.sync() }
    }

    fun increment(id: String) = viewModelScope.launch {
        stockRepo.incrementQuantity(id)
        runCatching { syncEngine.sync() }
    }

    fun decrement(id: String) = viewModelScope.launch {
        stockRepo.decrementQuantity(id)
        runCatching { syncEngine.sync() }
    }
}
