package ey.buriti.curral.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ey.buriti.curral.data.IStockRepository
import ey.buriti.curral.model.StockCategory
import ey.buriti.curral.model.StockItem
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class EstoqueViewModel(
    private val stockRepo: IStockRepository,
) : ViewModel() {

    val items: StateFlow<List<StockItem>> = stockRepo.getItems()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun addItem(
        name: String, category: StockCategory, quantity: Double,
        unit: String, expiryDate: String?, lowStockThreshold: Double,
    ) = viewModelScope.launch {
        val id = stockRepo.generateId()
        stockRepo.addItem(
            StockItem(
                id = id, name = name, category = category,
                quantity = quantity, unit = unit,
                expiryDate = expiryDate, lowStockThreshold = lowStockThreshold,
            )
        )
    }

    fun updateItem(item: StockItem) = viewModelScope.launch {
        stockRepo.updateItem(item)
    }

    fun deleteItem(id: String) = viewModelScope.launch {
        stockRepo.deleteItem(id)
    }

    fun increment(id: String) = viewModelScope.launch {
        stockRepo.incrementQuantity(id)
    }

    fun decrement(id: String) = viewModelScope.launch {
        stockRepo.decrementQuantity(id)
    }
}
