package ey.buriti.curral.data

import ey.buriti.curral.model.StockItem
import kotlinx.coroutines.flow.Flow

interface IStockRepository {
    fun getItems(): Flow<List<StockItem>>
    fun getItemById(id: String): Flow<StockItem?>
    suspend fun addItem(item: StockItem)
    suspend fun updateItem(item: StockItem)
    suspend fun deleteItem(id: String)
    suspend fun incrementQuantity(id: String)
    suspend fun decrementQuantity(id: String)
    suspend fun generateId(): String
}
